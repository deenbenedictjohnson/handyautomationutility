package com.auto.common.testng.listener.retry;

import org.testng.IRetryAnalyzer;
import org.testng.ITestResult;

import com.auto.common.testng.listener.extent.ExtentTestManager;
import com.relevantcodes.extentreports.LogStatus;

public class Retry implements IRetryAnalyzer {

	private static int maxTry = 1; //Run the failed test 2 times
	private int count = 0;

	@Override
	public boolean retry(ITestResult iTestResult) {
		if (!iTestResult.isSuccess()) {                      //Check if test not succeed
			if (count < maxTry) {                            //Check if maxtry count is reached
				count++;                                     //Increase the maxTry count by 1
				iTestResult.setStatus(ITestResult.FAILURE);  //Mark test as failed and take base64Screenshot
				extendReportsFailOperations(iTestResult);    //ExtentReports fail operations
				return true;                                 //Tells TestNG to re-run the test
			}
		} else {
			iTestResult.setStatus(ITestResult.SUCCESS);      //If test passes, TestNG marks it as passed
		}
		return false;
	}

	public void extendReportsFailOperations(ITestResult iTestResult) {
		ExtentTestManager.getTest().log(LogStatus.FAIL, "Test Failed");
	}

}
