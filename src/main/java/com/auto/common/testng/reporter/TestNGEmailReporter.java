package com.auto.common.testng.reporter;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.IReporter;
import org.testng.ISuite;
import org.testng.ISuiteResult;
import org.testng.ITestContext;
import org.testng.ITestNGMethod;
import org.testng.TestListenerAdapter;
import org.testng.xml.XmlSuite;

public class TestNGEmailReporter extends TestListenerAdapter implements IReporter {

	private static Logger logger = LoggerFactory.getLogger(TestNGEmailReporter.class);
	private static PrintWriter f_out;
	private static String outputDir;
	NumberFormat nf = NumberFormat.getInstance();

	private String background_color = "background-color: #004040; border: 0px solid grey; height: 25px; color: white; font-family: Georgia;";

	public void generateReport(List<XmlSuite> arg0, List<ISuite> suites, String outdir) {
		try {
			outputDir = "target/custom-test-reports";
		} catch (Exception error) {
			logger.error("The exception in generateReport is :: " + error);
		}
		try {
			f_out = createWriter(outputDir);
		} catch (IOException error) {
			logger.error("The exception in generateReport is :: " + error);
		}
		startHtmlPage(f_out);
		generateTestExecutionStatus(suites);
		endHtmlPage(f_out);
		f_out.flush();
		f_out.close();
	}

	private void generateTestExecutionStatus(List<ISuite> suites) {
		int totalPassedMethods = 0;
		int totalFailedMethods = 0;
		int totalSkippedMethods = 0;
		int totalSkippedConfigurationMethods = 0;
		int totalFailedConfigurationMethods = 0;
		int totalMethods = 0;

		int suite_totalPassedMethods = 0;
		int suite_totalFailedMethods = 0;
		int suite_totalSkippedMethods = 0;

		String suite_passPercentage = "";
		String suiteName = "";

		ITestContext overview = null;
		HashMap<String, String> dashboardReportMap = new HashMap<String, String>();

		for (ISuite suite : suites) {
			suiteName = suite.getName();

			Map<String, ISuiteResult> tests = suite.getResults();

			for (ISuiteResult r : tests.values()) {
				overview = r.getTestContext();

				totalPassedMethods = overview.getPassedTests().getAllMethods().size();
				totalFailedMethods = overview.getFailedTests().getAllMethods().size();
				totalSkippedMethods = overview.getSkippedTests().getAllMethods().size();

				totalMethods = overview.getAllTestMethods().length;


				nf.setMaximumFractionDigits(2);
				nf.setGroupingUsed(true);

				ITestNGMethod[] allTestMethods = overview.getAllTestMethods();

				String browser = "NA";
				String browser_version = "NA";
				String platform = "NA";

				if (platform == null || platform.trim().length() == 0) {
					platform = "Windows XP";
				}

				if (browser_version == null || browser_version.trim().length() == 0) {
					browser_version = "N/A";
				}

				if (browser == null || browser.trim().length() == 0) {
					browser = "N/A";
				}

				if (!(dashboardReportMap.containsKey(""))) {
					if (browser_version.equalsIgnoreCase("N/A")) {
						browser_version = "";
					}
					dashboardReportMap.put("", "os1~" + platform + "|browser1~" + browser + browser_version
							+ "|testcase_count_1~" + totalMethods + "|pass_count_1~" + totalPassedMethods
							+ "|fail_count_1~" + totalFailedMethods + "|skip_count_1~" + totalSkippedMethods
							+ "|skip_conf_count_1~" + totalSkippedConfigurationMethods + "|fail_conf_count_1~"
							+ totalFailedConfigurationMethods);

				} else {
					for (String key : dashboardReportMap.keySet()) {

						if (key.equalsIgnoreCase("")) {
							if (browser_version.equalsIgnoreCase("N/A")) {
								browser_version = "";
							}
							String value = dashboardReportMap.get(key);
							int index = StringUtils.countMatches(value, "#") + 1;

							index += 1;

							value = value + "#" + "os" + index + "~" + platform + "|browser" + index + "~" + browser
									+ browser_version + "|testcase_count_" + index + "~" + totalMethods
									+ "|pass_count_" + index + "~" + totalPassedMethods + "|fail_count_" + index + "~"
									+ totalFailedMethods + "|skip_count_" + index + "~" + totalSkippedMethods
									+ "|skip_conf_count_" + index + "~" + totalSkippedConfigurationMethods
									+ "|fail_conf_count_" + index + "~" + totalFailedConfigurationMethods;
							dashboardReportMap.put(key, value);
						}
					}
				}

				suite_totalPassedMethods += totalPassedMethods;
				suite_totalFailedMethods += totalFailedMethods;
				suite_totalSkippedMethods += totalSkippedMethods;

				try {
					suite_passPercentage = nf
							.format(((float) suite_totalPassedMethods / (float) (suite_totalPassedMethods
									+ suite_totalFailedMethods + suite_totalSkippedMethods)) * 100);
				} catch (NumberFormatException error) {
					logger.error("The exception in generateTestExecutionStatus :: " + error);
				}
			}
		}

		StringBuilder dashboardResults = new StringBuilder();

		dashboardResults.append("<table style=\"border-collapse: collapse; width: 100%;\">");

		int total_browser_combinations = 0;
		int total_unique_testcases = 0;

		for (String key : dashboardReportMap.keySet()) {
			String module = key;

			String value = dashboardReportMap.get(key);
			String[] values = value.split("#");

			int testcase_count = 0;
			int pass_count = 0;
			int fail_count = 0;
			int skip_count = 0;
			int skip_conf_count = 0;
			int fail_conf_count = 0;

			for (String val : values) {
				String[] tokens = val.split("\\|");
				for (String token : tokens) {
					if (token.contains("testcase_count")) {
						testcase_count = testcase_count + Integer.parseInt(token.split("~")[1]);
					}
					if (token.contains("pass_count")) {
						pass_count = pass_count + Integer.parseInt(token.split("~")[1]);
					}
					if (token.contains("fail_count")) {
						fail_count = fail_count + Integer.parseInt(token.split("~")[1]);
					}
					if (token.contains("skip_count")) {
						skip_count = skip_count + Integer.parseInt(token.split("~")[1]);
					}
					if (token.contains("skip_conf_count")) {
						skip_conf_count = skip_conf_count + Integer.parseInt(token.split("~")[1]);
					}
					if (token.contains("fail_conf_count")) {
						fail_conf_count = fail_conf_count + Integer.parseInt(token.split("~")[1]);
					}
				}
			}

			logger.debug("Value: " + value);

			String[] sub = value.split("#");
			String temp = "";
			for (String s : sub) {
				s = s.substring(0, s.indexOf("fail_count"));
				temp = temp + s;
			}

			temp = temp.substring(0, temp.lastIndexOf("|"));
			temp = temp.replace(" ", "%20");

			NumberFormat nformat = NumberFormat.getInstance();
			nformat.setMaximumFractionDigits(2);
			nformat.setGroupingUsed(true);
			String passPercent = nformat
					.format(((float) pass_count / (float) (pass_count + fail_count + skip_count)) * 100);

			String finalStr = "[";
			String[] val = dashboardReportMap.get(key).split("#");

			int unique_testcase = 0;

			int limit = val.length - 1;
			for (int i = 0; i < val.length; i++) {
				String testCaseCount = (val[i].split("\\|")[2]).split("~")[1];
				int next = Integer.parseInt(testCaseCount);
				if (next > unique_testcase) {
					unique_testcase = next;
				}
				finalStr = finalStr + testCaseCount + " T * 1 B]";
				if (i != limit) {
					finalStr += " + [";
				}
			}

			String finalString = "";
			if ((unique_testcase * values.length) != (pass_count + fail_count + skip_count)) {
				finalString = "<a href=\"#\" title=\"" + finalStr + "\">" + (pass_count + fail_count + skip_count)
						+ "</a>";
			} else {
				finalString = String.valueOf((pass_count + fail_count + skip_count));
			}

			String passCount = "";
			String failCount = "";
			String skipCount = "";

			if (pass_count > 0) {
				passCount = "<td style=\"text-align: center; border: 0px solid grey; height: 25px; color: #00CC00; font-family: Georgia;\"><b>"
						+ pass_count + "</b></td>";
			} else {
				passCount = "<td style=\"text-align: center; border: 0px solid grey; height: 25px; color: #1C1C1C; font-family: Georgia;\">"
						+ pass_count + "</td>";
			}

			if (fail_count > 0) {
				failCount = "<td style=\"text-align: center; border: 0px solid grey; height: 25px; color: red; font-family: Georgia;\"><b>"
						+ fail_count + "</b></td>";
			} else {
				failCount = "<td style=\"text-align: center; border: 0px solid grey; height: 25px; color: #1C1C1C; font-family: Georgia;\">"
						+ fail_count + "</td>";
			}

			if (skip_count > 0) {
				skipCount = "<td style=\"text-align: center; border: 0px solid grey; height: 25px; color: #A35200; font-family: Georgia;\"><b>"
						+ skip_count + "</b></td>";
			} else {
				skipCount = "<td style=\"text-align: center; border: 0px solid grey; height: 25px; color: #1C1C1C; font-family: Georgia;\">"
						+ skip_count + "</td>";
			}

			dashboardResults
					.append("<tr><td style=\"text-align: left; border: 0px solid grey; height: 25px; color: #333300; font-family: Georgia;\"><b>"
							+ module
							+ "</b><td style=\"text-align: center; border: 0px solid grey; height: 25px; color: #1C1C1C; font-family: Georgia;\">"
							+ unique_testcase
							+ "</td><td style=\"text-align: center; border: 0px solid grey; height: 25px; color: #1C1C1C; font-family: Georgia;\">"
							+ values.length
							+ "</td>"
							+ passCount
							+ failCount
							+ skipCount
							+ "<td style=\"text-align: center; border: 0px solid grey; height: 25px; color: #1C1C1C; font-family: Georgia;\">"
							+ finalString
							+ "</td><td style=\"text-align: center; border: 0px solid grey; height: 25px; color: #A35200; font-family: Georgia;\"><b>"
							+ passPercent + " %" + "</b></td></tr>");

			if (total_browser_combinations < values.length) {
				total_browser_combinations = values.length;
			}

			total_unique_testcases += unique_testcase;
		}

		dashboardResults.append("</table>");

		String suite_pass = "";
		String suite_fail = "";
		String suite_skip = "";

		if (suite_totalPassedMethods > 0) {
			suite_pass = "<td style=\"text-align: center; border: 0px solid grey; height: 25px; color: #00CC00; font-family: Georgia;\"><b>"
					+ suite_totalPassedMethods + "</b></td>";
		} else {
			suite_pass = "<td style=\"text-align: left; border: 0px solid grey; height: 25px; color: #1C1C1C; font-family: Georgia;\">"
					+ suite_totalPassedMethods + "</td>";
		}

		if (suite_totalFailedMethods > 0) {
			suite_fail = "<td style=\"text-align: center; border: 0px solid grey; height: 25px; color: red; font-family: Georgia;\"><b>"
					+ suite_totalFailedMethods + "</b></td>";
		} else {
			suite_fail = "<td style=\"text-align: center; border: 0px solid grey; height: 25px; color: #1C1C1C; font-family: Georgia;\">"
					+ suite_totalFailedMethods + "</td>";
		}

		if (suite_totalSkippedMethods > 0) {
			suite_skip = "<td style=\"text-align: center; border: 0px solid grey; height: 25px; color: #A35200; font-family: Georgia;\"><b>"
					+ suite_totalSkippedMethods + "</b></td>";
		} else {
			suite_skip = "<td style=\"text-align: center; border: 0px solid grey; height: 25px; color: #1C1C1C; font-family: Georgia;\">"
					+ suite_totalSkippedMethods + "</td>";
		}

		f_out.println("<table style=\"border-collapse: collapse; width: 100%;\"><tr>"
				+ "<th style=\"text-align: center;  height: 25px; color: #4c4c4c; font-family: Georgia;\" colspan=\"8\"><b>Execution Summary</b></th></tr>"
				+ "<tr><th style=\"" + background_color + "\" >Test Suite Name</th>"
				+ "<th style=\"" + background_color + "\" ># Unique TestCases</th>"
				+ "<th style=\"" + background_color + "\" ># Passed</th>"
				+ "<th style=\"" + background_color + "\" ># Failed</th>"
				+ "<th style=\"" + background_color + "\" ># Skipped</th>"
				+ "<th style=\"" + background_color + "\" ># Failed + Skipped </th>"
				+ "<th style=\"" + background_color + "\" >Success Rate</th>"
				+ "<th style=\"" + background_color + "\" ># Failure Rate</th> </tr>"
				+ "<tr><td style=\"text-align: left; border: 0px solid grey; height: 25px; color: #333300; font-family: Georgia;\"><b>"
				+ suiteName
				+ "</b></td><td style=\"text-align: center; border: 0px solid grey; height: 25px; color: #1C1C1C; font-family: Georgia;\">"
				+ total_unique_testcases
				+ "</td>"
				+ suite_pass
				+ suite_fail
				+ "</td><td style=\"text-align: center; border: 0px solid grey; height: 25px; color: #1C1C1C; font-family: Georgia;\">"
				+ (total_unique_testcases - (suite_totalPassedMethods + suite_totalFailedMethods))
				+ "</td>"
				+ "<td style=\"text-align: center; border: 0px solid grey; height: 25px; color: #1C1C1C; font-family: Georgia;\">"
				+ (total_unique_testcases - suite_totalPassedMethods)
				+ "</td><td style=\"text-align: center; border: 0px solid grey; height: 25px; color: #00CC00; font-family: Georgia;\"><b>"
				+ nf.format(((float) suite_totalPassedMethods / (float) (total_unique_testcases)) * 100) + " %" + "</b></td>"
				+ "</td><td style=\"text-align: center; border: 0px solid grey; height: 25px; color: #A35200; font-family: Georgia;\"><b>"
				+ nf.format(100 - ((float) suite_totalPassedMethods / (float) (total_unique_testcases)) * 100) + " %" + "</b></td></tr></table>");

		f_out.flush();
		f_out.println("<br/><br/>");

		String failedTest = "true";

		if (failedTest.equalsIgnoreCase("true")) {
			String jiraURL = "";
			String testlinkURL = "";
			String testlinkPrefix = "";
			String BUILD_URL = "";

			String SLNUM = "<SLNUM>";
			try {
				jiraURL = "";
				testlinkURL = "";
				testlinkPrefix = "OSP";
			} catch (Exception error) {
				logger.error("The exception occurred is :: " + error);
			}

			int count = 1;
			String testIncludePassTest = "true";
			String tableSummary = "Failed and Skipped Tests Summary";

			if (testIncludePassTest.contains("true")) {
				tableSummary = "Tests Summary";
			}

			StringBuilder[] sBuilder = new StringBuilder[3];
			sBuilder[0] = new StringBuilder();
			sBuilder[1] = new StringBuilder();
			sBuilder[2] = new StringBuilder();

			for (ISuite suite : suites) {
				suiteName = suite.getName();
				boolean isHeaderPrinted = false;

				Map<String, ISuiteResult> tests = suite.getResults();

				for (ISuiteResult r : tests.values()) {
					overview = r.getTestContext();

					if (overview.getAllTestMethods().length > 0 && !isHeaderPrinted) {
						f_out.println("<table style=\"border-collapse: collapse; width: 100%;\">"
								+ "<tr><th style=\"text-align: center;  height: 25px; color: #4c4c4c; font-family: Georgia;\" colspan=\"8\"><b>"
								+ tableSummary
								+ "</b></th></tr>"
								+ "<tr><th style=\"" + background_color + "\"> S.No </th>"
								+ "<th style=\"" + background_color + "\" >Test ID</th>"
								+ "<th style=\"" + background_color + "\" >Component</th>"
								+ "<th style=\"" + background_color + "\" >Feature</th>"
								+ "<th style=\"" + background_color + "\" >Owner Name</th>"
								+ "<th style=\"" + background_color + "\" >Test Description</th>"
								+ "<th style=\"" + background_color + "\" >Bug ID</th>"
								+ "<th style=\"" + background_color + "\" >Status</th>"
								+ "</tr>");

						isHeaderPrinted = true;
					}


					Collection<ITestNGMethod> passedTestCases = overview.getPassedTests().getAllMethods();
					Collection<ITestNGMethod> failedTestCases = overview.getFailedTests().getAllMethods();
					Collection<ITestNGMethod> skippedTestCases = overview.getSkippedTests().getAllMethods();

					StringBuilder current = null;
					for (ITestNGMethod method : overview.getAllTestMethods()) {
						if (passedTestCases.contains(method)) {
							if (!testIncludePassTest.contains("true")) {
								continue;
							}
						}

						if (failedTestCases.contains(method)) {
							current = sBuilder[0];
						} else if (skippedTestCases.contains(method)) {
							current = sBuilder[1];
						} else if (passedTestCases.contains(method)) {
							current = sBuilder[2];
						} else {
							current = sBuilder[1];
						}

						current.append("<tr>");
						current.append("<td style=\"text-align: center; height: 25px; color: #1C1C1C; border: 1px solid grey; font-family: Georgia;\">");
						current.append(SLNUM);
						current.append("</td>");

						String[] groups = method.getGroups();

						String component = "", testId = "", bugids = "", feature = "", owner = "";
						for (String group : groups) {
							if (group.contains("Component:")) {
								component = group.split("Component:")[1];
							}
							if (group.contains("Feature:")) {
								feature = group.split("Feature:")[1];
							}
							if (group.contains("Owner:")) {
								owner = group.split("Owner:")[1];
							}
							if (group.contains(testlinkPrefix + "-") || group.contains("cm-")) {
								testId = group;
							}
							if (group.contains("Bug:")) {
								bugids = group;
							}
						}

						/*Testcase ID*/
						current.append("<td style=\"text-align: center; height: 25px; color: #1C1C1C; border: 1px solid grey; font-family: Georgia;\">");
						if (!testId.isEmpty()) {
							String[] split = testId.split(",");
							int counter = 0;
							for (String tc : split) {
								if (counter != 0) {
									current.append("<br>");
								}
								current.append("<a href=\"" + "" + testlinkURL + "/browse/" + tc + "  \"  style=\"text-decoration: none \" >" + tc + " </a>");
								counter++;
							}
							counter = 0;
						} else {
							current.append("");
						}
						current.append("</td>");

						/*Component Name*/
						current.append("<td style=\"text-align: center; height: 25px; color: #1C1C1C; border: 1px solid grey; font-family: Georgia;\">");
						if (!component.isEmpty()) {
							String[] reqs = component.replace("Component:", "").split(",");
							for (int i = 0; i < reqs.length; i++) {
								current.append(reqs[i]);
							}
						} else {
							current.append("");
						}
						current.append("</td>");

						/*Feature Name*/
						current.append("<td style=\"text-align: center; height: 25px; color: #1C1C1C; border: 1px solid grey; font-family: Georgia;\">");
						if (!feature.isEmpty()) {
							String[] reqs = feature.replace("Feature:", "").split(",");
							for (int i = 0; i < reqs.length; i++) {
								current.append(reqs[i]);
							}
						} else {
							current.append("");
						}
						current.append("</td>");

						/*Owner Name*/
						current.append("<td style=\"text-align: center; height: 25px; color: #1C1C1C; border: 1px solid grey; font-family: Georgia;\">");
						if (!owner.isEmpty()) {
							String[] reqs = owner.replace("Owner:", "").split(",");
							for (int i = 0; i < reqs.length; i++) {
								current.append(reqs[i]);
							}
						} else {
							current.append("");
						}
						current.append("</td>");

						String testClassName = method.getMethodName();
						String toolTilps = "";

						/*Test case name*/
						current.append("<td style=\"text-align: center; height: 25px; color: #1C1C1C; border: 1px solid grey; font-family: Georgia;\" title=\"" + toolTilps + "\"><b>");
						current.append(testClassName);
						current.append("</b></td>");

						current.append("<td style=\"text-align: center; height: 25px; color: #1C1C1C; border: 1px solid grey; font-family: Georgia;\">");
						if (!bugids.isEmpty()) {
							String[] bugs = bugids.replace("Bug:", "").split(",");
							for (int i = 0; i < bugs.length; i++) {
								current.append("<a href=\"" + jiraURL + "/browse/" + bugs[i] + "\">" + bugs[i] + "</a>");
							}
						} else {
							current.append("");
						}
						current.append("</td>");

						current.append("<td style=\"text-align: center; height: 25px; color: #1C1C1C; border: 1px solid grey; font-family: Georgia;\">");

						if (skippedTestCases.contains(method)) {
							current.append("<b><font color=\"Red\">");
							current.append("Skipped</font></a>");
						} else if (!passedTestCases.contains(method)) {
							current.append("<b><font color=\"Red\">");
							current.append("Failed</font></a>");
						} else {
							current.append("<b><font color=\"Green\">");
							current.append("Passed</font></a>");
						}

						current.append("</td>");
						current.append("</tr>");
					}
				}

				int indexSubStr = 0;
				int subStringLeng = SLNUM.length();

				while (true) {
					indexSubStr = sBuilder[0].indexOf(SLNUM);
					if (indexSubStr == -1)
						break;
					sBuilder[0].replace(indexSubStr, indexSubStr + subStringLeng, "" + (count++));
				}

				indexSubStr = 0;

				while (true) {
					indexSubStr = sBuilder[1].indexOf(SLNUM);
					if (indexSubStr == -1)
						break;
					sBuilder[1].replace(indexSubStr, indexSubStr + subStringLeng, "" + (count++));
				}

				indexSubStr = 0;

				while (true) {
					indexSubStr = sBuilder[2].indexOf(SLNUM);
					if (indexSubStr == -1)
						break;
					sBuilder[2].replace(indexSubStr, indexSubStr + subStringLeng, "" + (count++));
				}

				f_out.print(sBuilder[0].toString());
				f_out.print(sBuilder[1].toString());
				f_out.print(sBuilder[2].toString());
				f_out.print("</table>");
			}

			f_out.println("<br/><br/><br/><br/>");
		}

		f_out.flush();
	}

	private PrintWriter createWriter(String outdir) throws IOException {
		new File(outdir).mkdirs();
		return new PrintWriter(
				new BufferedWriter(new FileWriter(new File(outputDir, "emailable-test-run-report.html"))));
	}

	/**
	 * Starts HTML Stream
	 */
	private void startHtmlPage(PrintWriter out) {
		out.println("<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.1//EN http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd\">");
		out.println("<html xmlns=\"http://www.w3.org/1999/xhtml\">");
		out.println("<head>");
		out.println("<title>Automation Test Results Summary</title>");
		out.println("</head>");
		out.println("<body><div style=\"margin:0 auto; padding:15px; min-height:450px; min-width: 450px; height:auto;\">"
				+ "<div style=\"height:auto; background: #d0ffff;padding:20px;box-shadow: 0 10px 6px -6px #777 \">"
				+ "<h1 style=\"color: #4c4c4c; text-align: center; font-family: Georgia;\">Automation Report</h1>");
		DateFormat formatter = new SimpleDateFormat("dd-MM-yyyy hh:mm a z");
		TimeZone obj = TimeZone.getTimeZone("IST");
		formatter.setTimeZone(obj);
		out.flush();
	}

	/**
	 * Finishes HTML Stream
	 */
	private void endHtmlPage(PrintWriter out) {
		out.println("</body></html>");
	}
}

