package com.auto.data.constants;

import static com.auto.common.constants.CommonConstants.PATH;

import com.auto.common.utils.common.PropertyUtils;

public class URLConstants {

	public static final String HTTP = "http://";
	public static final String HTTPS = "https://";
	public static final String BASE_URL;
	// Initializing properties
	public static final PropertyUtils serverConfigProperties;

	static {

		if (System.getenv("env").equals("prod")) {
			serverConfigProperties = new PropertyUtils(PATH + "/config/prod/prod_server_config.properties");
		} else {
			serverConfigProperties = new PropertyUtils(PATH + "/config/stage/stage_server_config.properties");
		}
		BASE_URL = HTTP + serverConfigProperties.getProperty("baseURL");
	}

	private URLConstants() {
		//do nothing
	}


}
