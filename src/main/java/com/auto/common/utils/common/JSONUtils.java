package com.auto.common.utils.common;

import static com.auto.common.constants.CommonConstants.PATH;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class JSONUtils {

	static Logger logger = LoggerFactory.getLogger(JSONUtils.class);

	/**
	 * This method is used to convert the object to json object
	 *
	 * @param object
	 * @return
	 */
	public static JSONObject convertToJsonObject(final Object object) {
		JSONObject jsonObject = new JSONObject(object);
		logger.debug("The json object is : " + jsonObject);
		return jsonObject;
	}

	/**
	 * This method is used to convert the object to String
	 *
	 * @param object
	 * @return
	 */
	public static String convertSerializableJsonToString(final Object object) {
		Gson gsonObj = new GsonBuilder().setPrettyPrinting().create();
		// converts object to json string
		return gsonObj.toJson(object);
	}

	/**
	 * This method is used to convert the json to list
	 *
	 * @param json
	 * @param fieldName
	 * @return
	 */
	public static List<JSONObject> convertToJsonArrayList(final JSONObject json, final String fieldName) {

		List<JSONObject> jsonObjectList = new ArrayList<>();
		for (int count = 0; count < json.getJSONArray(fieldName).length(); count++) {
			jsonObjectList.add((JSONObject) (json.getJSONArray(fieldName).get(count)));

		}
		return jsonObjectList;
	}

	/**
	 * This method is used to convert the json to list
	 *
	 * @param json
	 * @param fieldName
	 * @return
	 */
	public static List<String> convertToStringArrayList(final JSONObject json, final String fieldName) {

		List<String> stringList = new ArrayList<>();
		for (int count = 0; count < json.getJSONArray(fieldName).length(); count++) {
			stringList.add((String) (json.getJSONArray(fieldName).get(count)));

		}
		return stringList;
	}

	public static Object readJSONFile(final String filePath, final String type) {
		try {
			JSONParser parser = new JSONParser();

			if (type.equalsIgnoreCase("object")) {
				return (JSONObject) parser.parse(new FileReader(PATH + filePath));
			} else if (type.equalsIgnoreCase("array")) {
				return (JSONArray) parser.parse(new FileReader(PATH + filePath));
			}
		} catch (ParseException | IOException error) {
			error.printStackTrace();
		}
		return null;
	}


}
