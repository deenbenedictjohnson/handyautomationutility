package com.auto.common.constants;

import java.util.ArrayList;
import java.util.List;

import com.auto.common.testng.listener.ReportFormatter;

public class CommonConstants {

	public static final String ANDROID_PLATFORM_TOOLS = "/home/deviceplatform/Documents/android-sdk-linux/platform-tools/";
	public static final String APPIUM_URL = "http://127.0.0.1:5701";
	// path of current project
	public static final String PATH = System.getProperty("user.dir");
	public static final List<ReportFormatter> reportFormatList = new ArrayList<>();
	public static final String PASSED = "passed";
	public static final String FAILED = "failed";
	public static final String SKIPPED = "skipped";
	public static final String API_INSERT_RECORD = "/api/report/insert/record";
	public static final String API_JOB_COUNT = "/api/report/get/all/job/count";
	public static final String TRUE = "true";
	public static final String BUILD_VERSION = "build_version";
	public static final String PORT_ENABLED = "portEnabled";
	public static final String PORT = "port";
	public static final String URL = "url";
	public static final String SUCCESS = "success";
	public static final String HTTP = "http";
	public static final String GET_METHOD = "GET";
	public static final String POST_METHOD = "POST";
	public static final String RESPONSE_TYPE = "application/json";
	public static final String DEFAULT = "default";
	public static int JOB_ID = 0;
	public static boolean NODE_SERVER_STATUS = false;

}
