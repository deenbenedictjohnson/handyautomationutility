package com.auto.common.utils.api;

import static com.jayway.restassured.RestAssured.given;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jayway.restassured.response.ExtractableResponse;
import com.jayway.restassured.response.Response;

public class RestAssuredHelper {

	static Logger logger = LoggerFactory.getLogger(RestAssuredHelper.class);

	/**
	 * Rest Assured Generic Function
	 */
	protected static String getResponse(final HTTPRequestType requestType, final Map<String, String> headerMap,
	                                    final String requestURL, final String requestBody) {

		List<String> headerList = new ArrayList<>();
		if (headerMap != null) {
			Set<String> keySet = headerMap.keySet();
			headerList.addAll(keySet);
		}

		logger.debug("Request URL : " + requestType + " " + requestURL);

		if (requestBody != null)
			logger.debug("Request Body : " + requestBody);

		ExtractableResponse<Response> response = null;

		try {
			switch (requestType) {

				case GET:

					if (headerMap != null && headerMap.size() == 1) {
						response = given().log().all().headers(headerList.get(0), headerMap.get(headerList.get(0)))
								.when().get(requestURL).then().log().all().extract();
					} else if (headerMap != null && headerMap.size() == 2) {
						response = given().log().all()
								.headers(headerList.get(0), headerMap.get(headerList.get(0)), headerList.get(1),
										headerMap.get(headerList.get(1)))
								.when().get(requestURL).then().log().all().extract();
					} else {
						response = given().log().all().when().get(requestURL).then().log().all().extract();
					}
					break;

				case PUT:

					if (headerMap != null && headerMap.size() == 1 && requestBody == null) {
						response = given().log().all().headers(headerList.get(0), headerMap.get(headerList.get(0)))
								.when().put(requestURL).then().log().all().extract();
					} else if (headerMap != null && headerMap.size() == 2 && requestBody == null) {
						response = given().log().all()
								.headers(headerList.get(0), headerMap.get(headerList.get(0)), headerList.get(1),
										headerMap.get(headerList.get(1)))
								.when().put(requestURL).then().log().all().extract();
					} else if (headerMap != null && headerMap.size() == 1 && requestBody != null) {
						response = given().log().all().headers(headerList.get(0), headerMap.get(headerList.get(0)))
								.body(requestBody).when().put(requestURL).then().log().all().extract();
					} else if (headerMap != null && headerMap.size() == 2 && requestBody != null) {
						response = given().log().all()
								.headers(headerList.get(0), headerMap.get(headerList.get(0)), headerList.get(1),
										headerMap.get(headerList.get(1)))
								.body(requestBody).when().put(requestURL).then().log().all().extract();
					} else {
						response = given().log().all().when().put(requestURL).then().log().all().extract();
					}
					break;

				case POST:

					if (headerMap != null && headerMap.size() == 2) {
						response = given().log().all()
								.headers(headerList.get(0), headerMap.get(headerList.get(0)), headerList.get(1),
										headerMap.get(headerList.get(1)))
								.body(requestBody).when().post(requestURL).then().log().all().extract();
					} else if (headerMap != null && headerMap.size() == 1) {
						response = given().log().all().headers(headerList.get(0), headerMap.get(headerList.get(0)))
								.body(requestBody).when().post(requestURL).then().log().all().extract();
					} else {
						response = given().log().all().when().post(requestURL).then().log().all().extract();
					}
					break;

				case DELETE:
					if (headerMap != null && headerMap.size() == 1 && requestBody == null) {
						response = given().log().all().headers(headerList.get(0), headerMap.get(headerList.get(0)))
								.when().delete(requestURL).then().log().all().extract();
					} else if (headerMap != null && headerMap.size() == 2 && requestBody == null) {
						response = given().log().all()
								.headers(headerList.get(0), headerMap.get(headerList.get(0)), headerList.get(1),
										headerMap.get(headerList.get(1)))
								.when().delete(requestURL).then().log().all().extract();
					} else if (headerMap != null && headerMap.size() == 1 && requestBody != null) {
						response = given().log().all().headers(headerList.get(0), headerMap.get(headerList.get(0)))
								.body(requestBody).when().delete(requestURL).then().log().all().extract();
					} else if (headerMap != null && headerMap.size() == 2 && requestBody != null) {
						response = given().log().all()
								.headers(headerList.get(0), headerMap.get(headerList.get(0)), headerList.get(1),
										headerMap.get(headerList.get(1)))
								.body(requestBody).when().delete(requestURL).then().log().all().extract();
					} else {
						response = given().log().all().when().delete(requestURL).then().log().all().extract();
					}
					break;
			}
		} catch (Exception e) {
			logger.error("Exception Occurred : " + e);
			return null;
		}

		if (response != null) {
			logger.debug("Response : " + response.asString());
			return response.asString();
		} else {
			return null;
		}
	}

	protected enum HTTPRequestType {
		GET, POST, DELETE, PUT;
	}

}
