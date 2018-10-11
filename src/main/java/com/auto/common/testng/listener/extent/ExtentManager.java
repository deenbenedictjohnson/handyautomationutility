package com.auto.common.testng.listener.extent;

import com.relevantcodes.extentreports.ExtentReports;

public class ExtentManager {

	private static ExtentReports extent;

	public synchronized static ExtentReports getReporter() {
		if (extent == null) {
			extent = new ExtentReports("target/custom-test-reports/ExtentReports/ExtentReportResults.html", true);
		}
		return extent;
	}

}
