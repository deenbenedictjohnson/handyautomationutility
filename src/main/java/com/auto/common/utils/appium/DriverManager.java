package com.auto.common.utils.appium;

import java.net.URL;
import java.util.concurrent.TimeUnit;

import org.json.JSONObject;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.MobileElement;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.ios.IOSDriver;
import io.appium.java_client.remote.AndroidMobileCapabilityType;
import io.appium.java_client.remote.IOSMobileCapabilityType;
import io.appium.java_client.remote.MobileCapabilityType;

public final class DriverManager {

	private static Logger logger = LoggerFactory.getLogger(DriverManager.class);

	private DriverManager() {
		//do nothing
	}

	public static AppiumDriver<MobileElement> initializeDriver(final JSONObject jsonObject,
	                                                           final String appiumIpAddress,
	                                                           final String appiumPort) {
		try {
			logger.info("The appium ip address :: " + appiumIpAddress + " port :: " + appiumPort);
			logger.info("The json value :: " + jsonObject.toString());

			String url = "http://" + appiumIpAddress + ":" + appiumPort + "/wd/hub";

			DesiredCapabilities capabilities = new DesiredCapabilities();
			capabilities.setCapability(MobileCapabilityType.AUTOMATION_NAME, jsonObject.get(MobileCapabilityType.AUTOMATION_NAME).toString());
			capabilities.setCapability(MobileCapabilityType.PLATFORM_NAME, jsonObject.get(MobileCapabilityType.PLATFORM_NAME).toString());
			capabilities.setCapability(MobileCapabilityType.PLATFORM_VERSION, jsonObject.get(MobileCapabilityType.PLATFORM_VERSION).toString());
			capabilities.setCapability(MobileCapabilityType.DEVICE_NAME, jsonObject.get(MobileCapabilityType.DEVICE_NAME).toString());
			capabilities.setCapability(MobileCapabilityType.UDID, jsonObject.get(MobileCapabilityType.UDID).toString());
			capabilities.setCapability(MobileCapabilityType.ORIENTATION, jsonObject.get(MobileCapabilityType.ORIENTATION).toString());
			capabilities.setCapability(MobileCapabilityType.NO_RESET, Boolean.parseBoolean(jsonObject.get(MobileCapabilityType.NO_RESET).toString()));
			capabilities.setCapability(MobileCapabilityType.FULL_RESET, Boolean.parseBoolean(jsonObject.get(MobileCapabilityType.FULL_RESET).toString()));
			AppiumDriver<MobileElement> driver = null;

			if (jsonObject.get(MobileCapabilityType.PLATFORM_NAME).toString().equalsIgnoreCase("android")) {
				capabilities.setCapability(AndroidMobileCapabilityType.AUTO_GRANT_PERMISSIONS, jsonObject.get(AndroidMobileCapabilityType.AUTO_GRANT_PERMISSIONS));
				capabilities.setCapability(AndroidMobileCapabilityType.APP_PACKAGE, jsonObject.get(AndroidMobileCapabilityType.APP_PACKAGE).toString());
				capabilities.setCapability(AndroidMobileCapabilityType.APP_ACTIVITY, jsonObject.get(AndroidMobileCapabilityType.APP_ACTIVITY).toString());
				capabilities.setCapability(AndroidMobileCapabilityType.SYSTEM_PORT, Integer.parseInt(jsonObject.get(AndroidMobileCapabilityType.SYSTEM_PORT).toString()));
				driver = new AndroidDriver(new URL(url), capabilities);
			} else if (jsonObject.get(MobileCapabilityType.PLATFORM_NAME).toString().equalsIgnoreCase("ios")) {
				capabilities.setCapability(IOSMobileCapabilityType.BUNDLE_ID, jsonObject.get(IOSMobileCapabilityType.BUNDLE_ID));
				driver = new IOSDriver<>(new URL(url), capabilities);
			}

			driver.manage().timeouts().implicitlyWait(20, TimeUnit.SECONDS);
			return driver;
		} catch (Exception error) {
			error.printStackTrace();
			return null;
		}
	}

}
