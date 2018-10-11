package com.auto.common.utils.common;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.json.JSONArray;
import org.testng.Assert;

public class AssertionUtils {

	/**
	 * This method asserts 2 JsonArray irrespective of order
	 * @param firstElement
	 * @param secondElement
	 * @return
	 */
	public static boolean assertEqualsNoOrder(final JSONArray firstElement, final JSONArray secondElement) {
		Assert.assertEquals(firstElement.length(), secondElement.length());

		List<String> firstList = new ArrayList<>();
		List<String> secondList = new ArrayList<>();

		for (int count = 0; count < firstElement.length(); count++) {
			firstList.add(firstElement.get(count).toString());
			secondList.add(secondElement.get(count).toString());
		}

		return secondList.containsAll(firstList);
	}


	/**
	 * This method asserts 2 StringArray irrespective of order
	 * @param firstArray
	 * @param secondArray
	 * @return
	 */
	public static boolean assertEqualsNoOrder(final String[] firstArray, final String[] secondArray) {
		Assert.assertEquals(firstArray.length, secondArray.length);

		List<String> firstList = Arrays.asList(firstArray);
		List<String> secondList = Arrays.asList(secondArray);
		return secondList.containsAll(firstList);
	}

	public static boolean isNullOrEmpty(String s) {
		return s == null ? true : s.isEmpty();
	}

}
