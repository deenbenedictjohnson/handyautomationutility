package com.auto.common.testng.listener;

import java.util.Date;

import com.auto.common.constants.CommonConstants;
import com.auto.common.utils.common.PropertyUtils;

public class ZephyrConfig {

	public static final PropertyUtils zephyrConfigProperties = new PropertyUtils(CommonConstants.PATH + "/config/common/config.properties");

	public static final boolean ZEPHYR_UPDATE = Boolean.parseBoolean(zephyrConfigProperties.getProperty("zephyrUpdate"));
	public static final String ZEPHYR_CYCLE_NAME = zephyrConfigProperties.getProperty("zephyrTestCycle") + "-" + (new Date()).getTime();
	public static final String JIRA_CREDENTIALS = zephyrConfigProperties.getProperty("zephyrCredentials");
	public static final String JIRA_URL = zephyrConfigProperties.getProperty("zephyrURL");
	public static final int JIRA_PROJECT_ID = Integer.parseInt(zephyrConfigProperties.getProperty("zephyrProjectId"));
	public static final int JIRA_VERSION_ID = Integer.parseInt(zephyrConfigProperties.getProperty("zephyrVersionId"));
	public static final String ZEPHYR_CYCLE_ID = zephyrConfigProperties.getProperty("zephyrCycleIdToClone");
	//public static final boolean isCyclePresent = Boolean.parseBoolean(zephyrConfigProperties.getProperty("driverNumber"));
	//public static final String cycleToClone = zephyrConfigProperties.getProperty("driverNumber");

}
