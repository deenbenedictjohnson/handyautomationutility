package com.auto.common.utils.common;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ShellScriptUtils {

	private static Logger logger = LoggerFactory.getLogger(ShellScriptUtils.class);

	public static String executeCommand(String command) {

		StringBuffer output = new StringBuffer();

		Process p;
		try {
			p = Runtime.getRuntime().exec(command);
			p.waitFor();
			BufferedReader reader =
					new BufferedReader(new InputStreamReader(p.getInputStream()));

			String line = "";
			while ((line = reader.readLine()) != null) {
				output.append(line + "\n");
			}

		} catch (Exception error) {
			logger.error("The exception in convertToStringArrayList is :: " + error);
		}

		return output.toString();

	}

}
