package com.auto.common.testng.listener;

import static com.auto.common.constants.CommonConstants.API_INSERT_RECORD;
import static com.auto.common.constants.CommonConstants.API_JOB_COUNT;
import static com.auto.common.constants.CommonConstants.BUILD_VERSION;
import static com.auto.common.constants.CommonConstants.FAILED;
import static com.auto.common.constants.CommonConstants.GET_METHOD;
import static com.auto.common.constants.CommonConstants.HTTP;
import static com.auto.common.constants.CommonConstants.JOB_ID;
import static com.auto.common.constants.CommonConstants.NODE_SERVER_STATUS;
import static com.auto.common.constants.CommonConstants.PASSED;
import static com.auto.common.constants.CommonConstants.PORT;
import static com.auto.common.constants.CommonConstants.PORT_ENABLED;
import static com.auto.common.constants.CommonConstants.POST_METHOD;
import static com.auto.common.constants.CommonConstants.RESPONSE_TYPE;
import static com.auto.common.constants.CommonConstants.SKIPPED;
import static com.auto.common.constants.CommonConstants.SUCCESS;
import static com.auto.common.constants.CommonConstants.TRUE;
import static com.auto.common.constants.CommonConstants.reportFormatList;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.sql.Timestamp;
import java.util.Date;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.IExecutionListener;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

import com.auto.common.utils.api.JerseyRestClientHelper;
import com.auto.common.utils.common.PropertyUtils;
import com.google.gson.Gson;

public class CustomReportListener implements ITestListener, IExecutionListener {

	public static final String REPORT_ENABLED = "reportEnabled";
	static Logger logger = LoggerFactory.getLogger(CustomReportListener.class);

	private static void emailReport(ITestResult iTestResult, String status) {

		//Checking whether the method level annotation of the Report Class is present
		if (iTestResult.getMethod().getMethod().isAnnotationPresent(Report.class)) {

			Annotation annotation = iTestResult.getMethod().getMethod().getAnnotation(Report.class);
			Report report = (Report) annotation;

			boolean reportStatus = false;

			for (int count = 0; count < reportFormatList.size(); count++) {

				ReportFormatter reportFormatter = reportFormatList.get(count);

				if (reportFormatter.getComponent().equals(report.component()) &&
						reportFormatter.getOwner().equals(report.ownerName())) {

					if (status.equals(PASSED)) {
						reportFormatter.setPassed(reportFormatter.getPassed() + 1);
					} else if (status.equals(FAILED)) {
						reportFormatter.setFailed(reportFormatter.getFailed() + 1);
					} else {
						reportFormatter.setSkipped(reportFormatter.getSkipped() + 1);
					}

					reportStatus = true;
					reportFormatter.setTotal(reportFormatter.getTotal() + 1);
					reportFormatList.set(count, reportFormatter);
				}

			}

			if (!reportStatus) {

				ReportFormatter reportFormatter = new ReportFormatter();
				reportFormatter.setOwner(report.ownerName());
				reportFormatter.setComponent(report.component());
				reportFormatter.setTotal(1);
				reportFormatter.setPassed(0);
				reportFormatter.setFailed(0);
				reportFormatter.setSkipped(0);

				if (status.equals(PASSED)) {
					reportFormatter.setPassed(1);
				} else if (status.equals(FAILED)) {
					reportFormatter.setFailed(1);
				} else {
					reportFormatter.setSkipped(1);
				}

				reportFormatList.add(reportFormatter);
			}

		}

	}

	/**
	 * @param method
	 * @param status This method is used to insert the test details into the report portal
	 */
	private static synchronized void getReportDetails(Method method, String status, String reason) {
		logger.debug("Coming inside the get report details : " + status);
		PropertyUtils REPORT_PROPERTY = new PropertyUtils(System.getProperty("user.dir") + "/config/report.properties");
		try {
			//Checking whether the method level annotation of the Report Class is present
			if (method.isAnnotationPresent(Report.class)) {

				Annotation annotation = method.getAnnotation(Report.class);
				Report report = (Report) annotation;

				String[] splitTestCaseIds = report.testcaseId().split(",");

				for (String testCaseID : splitTestCaseIds) {

					Date date = new Date();

					//Assigning the report annotation values to the testcase objects
					Testcase testcase = new Testcase();
					testcase.setStatus(status);
					testcase.setPriority(report.priority());
					testcase.setDescription(report.testcaseDescription());
					testcase.setOwnerName(report.ownerName());
					testcase.setTestcaseId(testCaseID);
					testcase.setTestcaseName(report.testcaseName());
					testcase.setExecutedOn(new Timestamp(date.getTime()).toString());
					testcase.setJobName(Integer.toString(JOB_ID));
					testcase.setGroup(report.groups());
					testcase.setBuildName(report.component());
					testcase.setBuildVersion(REPORT_PROPERTY.getProperty(BUILD_VERSION));
					testcase.setDate(Long.toString(date.getTime()));
					testcase.setReason(reason);

					Gson gsonObj = new Gson();
					String payload = gsonObj.toJson(testcase);

					apiCallToNodeServer(API_INSERT_RECORD, POST_METHOD, payload, RESPONSE_TYPE);
				}

			}
		} catch (Exception error) {
			logger.debug(" Node server is down " + error);
		}

	}

	/**
	 * @param apiName
	 * @param methodType
	 * @param value
	 * @param responseType
	 * @return This method is used to called the API to the Node server
	 */
	private static synchronized Response apiCallToNodeServer(String apiName, String methodType,

	                                                         String value, String responseType) {
		//Creating the client object
		Client client = ClientBuilder.newClient();

		PropertyUtils REPORT_PROPERTY = new PropertyUtils(System.getProperty("user.dir") + "/config/report.properties");

		Boolean portEnabled = Boolean.parseBoolean(REPORT_PROPERTY.getProperty(PORT_ENABLED));

		WebTarget webTarget;


		if (portEnabled) {
			//Constructing the Web URL fro the API

			webTarget = client.target(HTTP + "://" + REPORT_PROPERTY.getProperty("url") + ":" + REPORT_PROPERTY.getProperty(PORT) +
					apiName);
		} else {
			//Constructing the Web URL fro the API
			webTarget = client.target(HTTP + "://" + REPORT_PROPERTY.getProperty("url") + ":" + REPORT_PROPERTY.getProperty(PORT) +
					apiName);
		}

		Invocation.Builder builder = webTarget.request(MediaType.APPLICATION_JSON);

		Response response;

		//Checking for the method type
		if (methodType.equals(GET_METHOD)) {
			response = builder.get();
		} else {
			response = builder.post(Entity.entity(value, MediaType.APPLICATION_JSON));
		}

		//Checking for the response status
		if (response.getStatus() != 200) {
			logger.error("Failed : HTTP error code : "
					+ response.getStatus());
		}

		return response;

	}

	private static synchronized void getJobCount() {
		try {

			PropertyUtils REPORT_PROPERTY = new PropertyUtils(System.getProperty("user.dir") + "/config/report.properties");

			//Calling the API to get the job count
			Response response = JerseyRestClientHelper.callGetAPI("http://" + REPORT_PROPERTY.getProperty("url") + ":" + REPORT_PROPERTY.getProperty(PORT), API_JOB_COUNT, null, null);

			//Getting the value from the response
			String output = JerseyRestClientHelper.getPayloadFromResponse(response);

			//Assigning the job count to the Constants
			JOB_ID = Integer.parseInt(output);
			JOB_ID++;
			logger.debug("The job id is : " + JOB_ID);
			NODE_SERVER_STATUS = true;
		} catch (Exception error) {
			NODE_SERVER_STATUS = false;
			logger.error("Node Server is down " + error);
		}
	}

	@Override
	public void onTestStart(ITestResult var1) {
		// Do nothing
	}

	@Override
	public void onTestFailedButWithinSuccessPercentage(ITestResult var1) {
		// Do nothing
	}

	@Override
	public void onStart(ITestContext arg0) {
		// Do nothing
	}

	@Override
	public void onFinish(ITestContext testContext) {
		logger.debug("Test suite finish");
	}

	/**
	 * @param result This method will be called on every Test success
	 */
	@Override
	public void onTestSuccess(ITestResult result) {
		PropertyUtils REPORT_PROPERTY = new PropertyUtils(System.getProperty("user.dir") + "/config/report.properties");
		logger.debug("Test case is passing" + NODE_SERVER_STATUS + " : " + REPORT_PROPERTY.getProperty(REPORT_ENABLED).trim());
		//Checking whether the Node Server is down
		if (NODE_SERVER_STATUS && TRUE.equalsIgnoreCase(REPORT_PROPERTY.getProperty(REPORT_ENABLED).trim())) {
			getReportDetails(result.getMethod().getMethod(), PASSED, SUCCESS);
		}
		emailReport(result, PASSED);
	}

	/**
	 * @param result This method will be called on every Test failure
	 */
	@Override
	public void onTestFailure(ITestResult result) {
		PropertyUtils REPORT_PROPERTY = new PropertyUtils(System.getProperty("user.dir") + "/config/report.properties");
		logger.debug("Test case is failing : " + NODE_SERVER_STATUS + " : " + REPORT_PROPERTY.getProperty(REPORT_ENABLED).trim());
		//Checking whether the Node Server is down
		if (NODE_SERVER_STATUS && TRUE.equalsIgnoreCase(REPORT_PROPERTY.getProperty(REPORT_ENABLED).trim())) {
			StringWriter sw = new StringWriter();
			PrintWriter pw = new PrintWriter(sw, true);
			result.getThrowable().printStackTrace(pw);
			String exceptionMessage = sw.getBuffer().toString();
			getReportDetails(result.getMethod().getMethod(), FAILED, exceptionMessage);
		}
		emailReport(result, FAILED);

	}

	/**
	 * @param result This method will be called on every Test skipped
	 */
	@Override
	public void onTestSkipped(ITestResult result) {
		PropertyUtils REPORT_PROPERTY = new PropertyUtils(System.getProperty("user.dir") + "/config/report.properties");
		logger.debug("Test case is skipping");
		//Checking whether the Node Server is down
		if (NODE_SERVER_STATUS && TRUE.equalsIgnoreCase(REPORT_PROPERTY.getProperty(REPORT_ENABLED).trim())) {
			getReportDetails(result.getMethod().getMethod(), SKIPPED, SKIPPED);
		}
		emailReport(result, SKIPPED);
	}

	/**
	 * This method will be called on start of the testng
	 */
	@Override
	public void onExecutionStart() {
		logger.debug("<<<<<<========onExecutionStart==========>>>>>>>");
		PropertyUtils REPORT_PROPERTY = new PropertyUtils(System.getProperty("user.dir") + "/config/report.properties");
		if (REPORT_PROPERTY.getProperty(REPORT_ENABLED).equals("true")) {
			getJobCount();
		}
	}

	@Override
	public void onExecutionFinish() {
		logger.debug("<<<<<<========onExecutionFinish==========>>>>>>>");

		SendEmail.triggerMail();
	}
}


