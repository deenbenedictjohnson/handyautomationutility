package com.auto.common.testng.listener.extent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

import com.relevantcodes.extentreports.LogStatus;

public class ExtentTestListener implements ITestListener {

	private static Logger logger = LoggerFactory.getLogger(ExtentTestListener.class);

	private static String getTestMethodName(ITestResult iTestResult) {
		return iTestResult.getMethod().getConstructorOrMethod().getName();
	}

	//Before starting all tests, below method runs.
	@Override
	public void onStart(ITestContext iTestContext) {
		logger.info("I am in onStart method " + iTestContext.getName());
	}

	//After ending all tests, below method runs.
	@Override
	public void onFinish(ITestContext iTestContext) {
		logger.info("I am in onFinish method " + iTestContext.getName());
		//Do tier down operations for extentreports reporting!
		ExtentTestManager.endTest();
		ExtentManager.getReporter().flush();
	}

	@Override
	public void onTestStart(ITestResult iTestResult) {
		logger.info("I am in onTestStart method " + getTestMethodName(iTestResult) + " start");
		//Start operation for extentreports.
		ExtentTestManager.startTest(iTestResult.getMethod().getMethodName(), "");
	}

	@Override
	public void onTestSuccess(ITestResult iTestResult) {
		logger.info("I am in onTestSuccess method " + getTestMethodName(iTestResult) + " succeed");
		//Extentreports log operation for passed tests.
		ExtentTestManager.getTest().log(LogStatus.PASS, "Test passed");
	}

	@Override
	public void onTestFailure(ITestResult iTestResult) {
		logger.info("I am in onTestFailure method " + getTestMethodName(iTestResult) + " failed");


		//Extentreports log and screenshot operations for failed tests.
		ExtentTestManager.getTest().log(LogStatus.FAIL, "Test Failed");
	}

	@Override
	public void onTestSkipped(ITestResult iTestResult) {
		logger.info("I am in onTestSkipped method " + getTestMethodName(iTestResult) + " skipped");
		//Extentreports log operation for skipped tests.
		ExtentTestManager.getTest().log(LogStatus.SKIP, "Test Skipped");
	}

	@Override
	public void onTestFailedButWithinSuccessPercentage(ITestResult iTestResult) {
		logger.info("Test failed but it is in defined success ratio " + getTestMethodName(iTestResult));
	}

}