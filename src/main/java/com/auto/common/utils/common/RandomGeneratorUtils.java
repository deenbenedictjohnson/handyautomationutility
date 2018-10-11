package com.auto.common.utils.common;

import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RandomGeneratorUtils {

	private static final String CHAR_LIST =
			"abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
	private static final String NO_LIST =
			"1234567890";
	private static final int RANDOM_STRING_LENGTH = 10;

	static Logger logger = LoggerFactory.getLogger(RandomGeneratorUtils.class);

	/**
	 * This method generates random string
	 *
	 * @return
	 */
	public static String generateRandomString() {
		return generateRandomString(RANDOM_STRING_LENGTH);
	}

	/**
	 * This method generates random string
	 *
	 * @return
	 */
	public static String generateRandomString(final int length) {
		StringBuilder randStr = new StringBuilder();
		for (int i = 0; i < length; i++) {
			int number = getRandomNumber();
			char ch = CHAR_LIST.charAt(number);
			randStr.append(ch);
		}
		return randStr.toString();
	}

	/**
	 * This method generates random numbers
	 *
	 * @return int
	 */
	private static int getRandomNumber() {
		int randomInt;
		Random randomGenerator = new Random();
		randomInt = randomGenerator.nextInt(CHAR_LIST.length());
		if (randomInt - 1 == -1) {
			logger.debug("The Random number is : " + randomInt);
			return randomInt;
		} else {
			return randomInt - 1;
		}
	}


	/**
	 * This method generates random numbers
	 *
	 * @return
	 */
	public static String generateRandomNumbers(final int length) {
		StringBuilder randStr = new StringBuilder();
		for (int i = 0; i < length; i++) {
			int randomInt;
			Random randomGenerator = new Random();
			randomInt = randomGenerator.nextInt(NO_LIST.length());
			char ch = NO_LIST.charAt(randomInt);
			randStr.append(ch);
		}
		return randStr.toString();
	}

}
