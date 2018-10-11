package com.auto.common.utils.common;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PropertyUtils {


	static Logger logger = LoggerFactory.getLogger(PropertyUtils.class);

	private Properties props = new Properties();

	public PropertyUtils(String path) {

		loadPropertyFile(path);
	}

	public void loadPropertyFile(String propertyFileName) {
		try {
			props.load(new FileInputStream(propertyFileName));
		} catch (IOException error) {
			logger.debug("The exception in loadPropertyFile is : " + error);
		}
	}

	public String getProperty(String propertyKey) {
		String propertyValue = props.getProperty(propertyKey.trim());

		if (propertyValue == null || propertyValue.trim().length() == 0) {
			// Log error message
		}

		return propertyValue;
	}

	public void setProperty(String propertyKey, String value) {
		props.setProperty(propertyKey, value);
	}

	public void store(OutputStream outputStream, String comments) throws IOException {
		props.store(outputStream, comments);
	}
}
