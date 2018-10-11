package com.auto.common.utils.common;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DateUtils {

	private static Logger logger = LoggerFactory.getLogger(DateUtils.class);

	public static String getUTCDate(String timestamp) {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'+05:30'");
		formatter.setTimeZone(TimeZone.getTimeZone("UTC"));

		String formattedDate = null;

		try {
			Long timestampLong = Long.parseLong(timestamp);
			formattedDate = formatter.format(timestampLong);
		} catch (Exception e) {
			logger.error("Error in getting the datetime from timestamp");
			try {
				Date now = new Date();
				formattedDate = formatter.format(now);
			} catch (Exception e1) {
				logger.error("Error in getting the datetime from current time");
			}
		}

		logger.debug("UTC datetime => " + formattedDate);
		return formattedDate;
	}

	public static String getDateSpaceFormat(String timestamp) {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

		String formattedDate = null;

		try {
			Long timestampLong = Long.parseLong(timestamp);
			formattedDate = formatter.format(timestampLong);
		} catch (Exception e) {
			logger.error("Error in getting the datetime from timestamp");
		}

		logger.debug("datetime => " + formattedDate);
		return formattedDate;
	}

	public static Date convertDate(String expiryDate) {
		expiryDate = expiryDate.replaceAll("\\s", "");
		if (expiryDate.contains("T")) {
			expiryDate = StringUtils.join(expiryDate.split("T"), " ");
		}
		String[] dateArr = expiryDate.split(" ");
		if (dateArr.length == 1) {
			expiryDate = expiryDate + " 00:00:00";
		}
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		formatter.setLenient(false);
		try {
			return formatter.parse(expiryDate);
		} catch (Exception e) {
			logger.error("Parse error {}. Not a valid date", expiryDate);
			return null;
		}
	}

	public static String getCurrentTime() {
		// Get current time
		Date date = new Date();

		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String currentTime = dateFormat.format(date);

		return currentTime;
	}

	public static String getFormattedDateTime(Date date, String format) {
		// Get current time
		SimpleDateFormat dateFormat = new SimpleDateFormat(format);
		String currentTime = dateFormat.format(date);
		return currentTime;
	}

	public static String getCurrentGMTTime() {
		// Get current time
		Date date = new Date();

		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
		String currentTime = dateFormat.format(date);

		return currentTime;
	}

	public static Date oneHourBack() {
		Calendar cal = Calendar.getInstance();
		cal.setTime(new Date());
		cal.add(Calendar.HOUR, -1);
		Date oneHourBack = cal.getTime();
		return oneHourBack;
	}

	public static Long getTimeInMilliseconds() {
		Date date = new Date();

		return date.getTime();
	}

	public static Long getEpochTimeSpaceFormat(String timestamp) {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

		Long epoch = 0l;

		try {
			Date date = formatter.parse(timestamp);
			epoch = date.getTime();
		} catch (Exception e) {
			logger.error("Error in getting epoch time");
		}

		return epoch;
	}

	public static Integer getDayOfWeekToday() {
		int dayOfWeek = 2;
		try {
			Calendar c = Calendar.getInstance();
			Date date = new Date();
			c.setTime(date);
			dayOfWeek = c.get(Calendar.DAY_OF_WEEK);
		} catch (Exception e) {
			logger.error("Error in getting day of week: " + e);
		}
		return dayOfWeek;
	}

	public static Date addSecondsToDate(Date date, Integer deltaTime) {
		try {
			Calendar cal = Calendar.getInstance();
			cal.setTime(date);
			cal.add(Calendar.SECOND, deltaTime);
			date = cal.getTime();
		} catch (Exception e) {
			logger.error("Error in getting date with added seconds: {}", e);
		}
		return date;
	}

	public static int getHourFromEpoch(Long timestamp) {
		try {
			Date date = new Date(timestamp);
			Calendar cal = Calendar.getInstance();
			cal.setTime(date);
			return cal.get(Calendar.HOUR_OF_DAY);
		} catch (Exception e) {
			logger.error("Error in getting date from epoch: {}", e);
		}
		return 0;
	}

	public static String getCurrentHour() {
		Calendar cal = Calendar.getInstance();
		cal.setTime(new Date());
		return String.valueOf(cal.get(Calendar.HOUR_OF_DAY));
	}

	public static Integer getDayOfWeek(Long longTime) {
		Date date = new Date(longTime);
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		int dayOfWeek = c.get(Calendar.DAY_OF_WEEK);
		return dayOfWeek;
	}

	public static long getCurrentMinute() {
		Calendar cal = Calendar.getInstance();
		cal.setTime(new Date());
		return cal.get(Calendar.MINUTE);
	}

	public static Date getTimeByDay(String dayOfWeek, String hour) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(new Date());
		calendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(hour));
		calendar.set(Calendar.DAY_OF_WEEK, Integer.parseInt(dayOfWeek));
		return calendar.getTime();
	}

	public static Integer getRemainingTimeWithinHour() {
		Date currentTime = new Date();
		Calendar calender = Calendar.getInstance();
		calender.setTime(currentTime);
		return 60 - calender.MINUTE;
	}

}
