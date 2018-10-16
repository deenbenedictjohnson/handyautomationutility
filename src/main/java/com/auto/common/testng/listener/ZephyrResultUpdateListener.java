package com.auto.common.testng.listener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.core.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.IInvokedMethod;
import org.testng.ISuite;
import org.testng.ISuiteListener;

import com.auto.common.utils.api.JerseyRestClientHelper;

/**
 * It updates the test results in the Zephyr JIRA
 */
public class ZephyrResultUpdateListener implements ISuiteListener {

	private static JSONArray jsonArrayExecutions;
	private static Logger logger = LoggerFactory.getLogger(ZephyrResultUpdateListener.class);
	private int cycleId;
	private boolean isCyclePresent;
	private Map<String, Object> headers;

	@Override
	public void onStart(ISuite suite) {
	}

	@Override
	public void onFinish(ISuite suite) {
		headers = new HashMap<>();
		if (!ZephyrConfig.ZEPHYR_CYCLE_ID.isEmpty()) {
			cycleId = Integer.parseInt(ZephyrConfig.ZEPHYR_CYCLE_ID);
		}
		try {
			String encoding = java.util.Base64.getEncoder().encodeToString(ZephyrConfig.JIRA_CREDENTIALS.getBytes());
			headers.put("Content-type", "application/json");
			headers.put("Authorization", "Basic " + encoding);
		} catch (Exception e) {
			logger.error("Did nt find some property in properties file.Can't upload the Result on Zephyr !!");
		}

		if (ZephyrConfig.ZEPHYR_UPDATE) {
			try {
				createCycle(); // creating cycle
			} catch (IOException | JSONException e) {
				logger.error("Could not create cycle. Can't upload the result");
			}
		}

		if (ZephyrConfig.ZEPHYR_UPDATE) {
			List<IInvokedMethod> methods = suite.getAllInvokedMethods();
			for (IInvokedMethod method : methods) {
				Zephyr zephyrAnnotation = method.getTestMethod().getMethod().getAnnotation(Zephyr.class);
				if (null != zephyrAnnotation) {
					String[] issueIds = zephyrAnnotation.id();
					int result = method.getTestResult().getStatus();
					int zapiResultInt = getResultStateToPublish(result);
					String id;
					addTestsToCycle(issueIds);

					for (int count = 0; count < issueIds.length; count++) {
						try {
							String issueId = issueIds[count];
							id = getExecutionIdFromIssueID(issueId);
							sendStatus(id, zapiResultInt);
						} catch (JSONException error) {
							logger.error("The json exception is :: " + error);
						}
						// send the status to Zephyr
					}
				}
			}
		}
	}

	/**
	 * Adding testcase to the test cycle of the Zephyr
	 *
	 * @param issueIds The String array of issue ids
	 */
	public void addTestsToCycle(String[] issueIds) {
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("issues", issueIds);
		jsonObject.put("versionId", ZephyrConfig.JIRA_VERSION_ID);
		jsonObject.put("cycleId", cycleId);
		jsonObject.put("projectId", ZephyrConfig.JIRA_PROJECT_ID);
		jsonObject.put("method", "1");
		Response clientResponse = JerseyRestClientHelper.callPostAPI(ZephyrConfig.JIRA_URL, "rest/zapi/latest/execution/addTestsToCycle", headers, jsonObject.toString());

		if (clientResponse.getStatus() != 200) {
			logger.error("Adding tcs to cycle failed!! check it");
		} else {
			clientResponse = JerseyRestClientHelper.callGetAPI(ZephyrConfig.JIRA_URL,
					"rest/zapi/latest/execution?projectId=" + ZephyrConfig.JIRA_PROJECT_ID + "&versionId=" + ZephyrConfig.JIRA_VERSION_ID
							+ "&cycleId=" + cycleId + "&action=expand", headers, null);

			JSONObject jsonOb1 = new JSONObject(JerseyRestClientHelper.getPayloadFromResponse(clientResponse));
			jsonArrayExecutions = jsonOb1.getJSONArray("executions");
		}


	}

	/**
	 * This method maps the TestNG result integer with Zephyr status integer
	 *
	 * @param result The result code
	 * @return The result code to publish
	 */
	private int getResultStateToPublish(int result) {
		switch (result) {
			case 1:
				return 1; // 1 is for success in both Zephyr and TestNG
			case 2:
				return 2; // 2 is for failure for both Zephyr and TestNG
			default:
				return 3; // 3 in Zephyr is for WorkInProgress, while in TestNG it
			// is for skip since we cannot have a skip in Zephyr, we
			// will keep it as WorkInProgress. Same is for any other
			// value from TestNG.
		}
	}

	/**
	 * The testcases have an issue id in Zephyr ,e.g : "DAPP-1123", which will
	 * have an execution id in a cycle and its version.
	 *
	 * @param testId
	 * @return
	 * @throws JSONException
	 */
	private String getExecutionIdFromIssueID(String testId) throws JSONException {
		String returnValue = "0";
		for (int iter = 0; iter < jsonArrayExecutions.length(); iter++) {
			JSONObject jsObTemp = jsonArrayExecutions.getJSONObject(iter);
			String tempString = jsObTemp.getString("issueKey");
			if (testId.equals(tempString.trim())) {
				returnValue = "" + (jsObTemp.getInt("id")) + "";
				break;
			}
		}
		return returnValue;
	}

	/**
	 * This method updates the result for the test in Zephyr.
	 *
	 * @param id
	 * @param status
	 */
	private void sendStatus(String id, int status) {
		if (id.equalsIgnoreCase("0")) {
			logger.error("The id was not found, so not uploading this case!");
			return;
		}
		JSONObject obj = new JSONObject();
		try {
			obj.put("status", status);
			JerseyRestClientHelper.callPutAPI(ZephyrConfig.JIRA_URL, "rest/zapi/latest/execution/" + id + "/execute", headers, obj.toString());
		} catch (Exception e) {
			logger.error("Result could not be sent, some Error occured!!");
		}
	}

	private JSONObject getCycles() throws IOException, JSONException {
		Response clientResponse = JerseyRestClientHelper.callGetAPI(ZephyrConfig.JIRA_URL, "rest/zapi/latest/cycle?projectId=" + ZephyrConfig.JIRA_PROJECT_ID + "&versionId=" + ZephyrConfig.JIRA_VERSION_ID, headers, null);
		JSONObject jsonObject = new JSONObject(JerseyRestClientHelper.getPayloadFromResponse(clientResponse));
		return jsonObject;
	}

	private void createCycle() throws IOException, JSONException {
		JSONObject obj1 = getCycles();

		// find if cycle is already present
		String[] keys = obj1.getNames(obj1);
		List<String> newKeys = new ArrayList();
		for (String key : keys) {
			boolean validNumberKey = false;
			try {
				Integer.parseInt(key);
				validNumberKey = true;
			} catch (Exception e) {
				// do nothing
			}
			if (validNumberKey) {
				newKeys.add(key);
			}
		}
		for (int start = 0; start < newKeys.size(); start++) {
			JSONObject tempOb = (JSONObject) obj1.get(newKeys.get(start));
			if (tempOb.getString("name").equals(ZephyrConfig.ZEPHYR_CYCLE_NAME)) {
				isCyclePresent = true;
				cycleId = Integer.parseInt(newKeys.get(start));
				logger.error("Cycle is already present !!");
				break;
			}
		}

		// if cycle not present, create it.
		if (!isCyclePresent) {
			JSONObject newObj = new JSONObject();
			if (cycleId != 0) {
				newObj.put("clonedCycleId", cycleId);
			}
			newObj.put("name", ZephyrConfig.ZEPHYR_CYCLE_NAME);
			newObj.put("build", "");
			newObj.put("environment", "");
			newObj.put("description", "");
			newObj.put("startDate", "");
			newObj.put("endDate", "");
			newObj.put("projectId", ZephyrConfig.JIRA_PROJECT_ID);
			newObj.put("versionId", ZephyrConfig.JIRA_VERSION_ID);

			// create cycle
			Response clientResponse = JerseyRestClientHelper.callPostAPI(ZephyrConfig.JIRA_URL, "rest/zapi/latest/cycle", headers, newObj.toString());
			JSONObject obj2 = new JSONObject(JerseyRestClientHelper.getPayloadFromResponse(clientResponse));
			cycleId = Integer.parseInt(obj2.getString("id"));
			logger.error("cycleId is : " + cycleId + "\n");
		}
	}

}