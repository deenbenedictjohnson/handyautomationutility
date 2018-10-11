package com.auto.common.utils.common;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TimeUtils {

	static Logger logger = LoggerFactory.getLogger(TimeUtils.class);

	/**
	 * returns current system time in epoch format
	 *
	 * @return
	 */
	public static long getEpochTime() {
		return System.currentTimeMillis() / 1000L;
	}

	/**
	 * returns current system time in defined time format
	 *
	 * @return
	 */
	public static String getCurrentFixTime(final String format) {
		SimpleDateFormat dateFormat = new SimpleDateFormat(format);
		Date dateobj = new Date();
		return dateFormat.format(dateobj);
	}

	/**
	 * This method is used to sleep the thread
	 *
	 * @param milliseconds
	 */
	public static void sleep(final long milliseconds) {
		try {
			Thread.sleep(milliseconds);
		} catch (Exception error) {
			logger.error("The exception occurred is : " + error);
		}
	}


	public static long getEpochTimeBasedOnDateFormat(String time, String format) throws ParseException {
		Date date = new SimpleDateFormat(format, Locale.ENGLISH).parse(time);
		return date.toInstant().toEpochMilli();
	}

}
