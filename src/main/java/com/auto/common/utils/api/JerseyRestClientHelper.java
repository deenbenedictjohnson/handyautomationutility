package com.auto.common.utils.api;

import java.util.Map;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;

import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.client.ClientProperties;
import org.glassfish.jersey.client.authentication.HttpAuthenticationFeature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.auto.common.constants.RestMethod;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.internal.LinkedTreeMap;

public class JerseyRestClientHelper {

	static Logger logger = LoggerFactory.getLogger(JerseyRestClientHelper.class);
	private static int retryCounter = 0;

	private JerseyRestClientHelper() {
		//do nothing
	}

	/**
	 * This method is used to call the API
	 *
	 * @param url
	 * @param apiName
	 * @param auth
	 * @param headers
	 * @param methodType
	 * @param queryParam
	 * @param payload
	 * @return
	 */
	public static Response callAPI(final String url, final String apiName,
	                               final Map<String, String> auth,
	                               final Map<String, Object> headers,
	                               final RestMethod.TYPE methodType,
	                               final MultivaluedMap<String, String> queryParam,
	                               final String payload) {
		if (retryCounter < 3) {
			Response response = null;
			try {

				ClientConfig clientConfig = new ClientConfig();
				clientConfig.property(ClientProperties.CONNECT_TIMEOUT, 15000);
				clientConfig.property(ClientProperties.READ_TIMEOUT, 15000);



				/*Checking whether the Auth is there*/
				if (auth != null && !auth.isEmpty()) {
					/*Creating the Basic Auth HTTP Filter*/
					HttpAuthenticationFeature authentication = HttpAuthenticationFeature.basicBuilder()
							.nonPreemptive()
							.credentials(auth.get("username"), auth.get("password"))
							.build();
					/*Adding it to the client*/
					clientConfig.register(authentication);
				}

				/*Creating the Jersey rest client instance*/
				Client client = ClientBuilder.newClient(clientConfig);

				WebTarget webTarget;
				webTarget = client.target(url + apiName);

				/*Checking whether the query param is not null for get methods*/
				if (queryParam != null) {
					/*Assigning the url, api path and query parameter*/
					if (null != queryParam && !queryParam.isEmpty()) {
						for (String key : queryParam.keySet()) {
							webTarget.queryParam(key, queryParam.get(key));
						}
					}
					logger.debug(" The request url is : " + webTarget);
					logger.debug(" The query parameters are : " + queryParam);
				}
				logger.debug("The headers are : " + headers);

				/*Getting the instance builder from the resources*/
				Invocation.Builder builder = webTarget.request(MediaType.APPLICATION_JSON);

				if (headers != null) {
					/*Iterating the header map*/
					for (Map.Entry<String, Object> headersMapEntry : headers.entrySet()) {
						/*Building the headers for the resource*/
						builder = builder.header(headersMapEntry.getKey(), headersMapEntry.getValue());
					}
				}

				switch (methodType) {
					case GET:
						/*Calling the get api*/
						response = builder.get();
						break;
					case POST:
						if (payload != null) {
							/*Printing the request string in json format*/
							ObjectMapper mapper = new ObjectMapper();
							Object json = mapper.readValue(payload, Object.class);
							String payloadJson = payload
									.replaceAll("\n", "").replaceAll(" ", "");
							logger.debug("The payload is : " + payloadJson);
							logger.debug("\n" + mapper.writerWithDefaultPrettyPrinter().writeValueAsString(json));
						}

						/*Calling the post api*/
						response = builder.post(Entity.entity(payload, MediaType.APPLICATION_JSON));
						break;
					case PUT:
						if (payload != null) {
							/*Printing the request string in json format*/
							ObjectMapper mapper = new ObjectMapper();
							Object json = mapper.readValue(payload, Object.class);
							logger.debug("The payload is : " + json);
							logger.debug("\n" + mapper.writerWithDefaultPrettyPrinter().writeValueAsString(json));
						}

						/*Calling the pout api*/
						response = builder.put(Entity.entity(payload, MediaType.APPLICATION_JSON));
						break;
					case DELETE:
						response = builder.delete();
						break;
					default:
						break;
				}

				/*Checking whether the response code is not 200*/
				if (response != null && response.getStatus() != 200) {
					if (response.getStatus() != 201) {
						logger.error("Failed : HTTP error code : " + response.getStatus());
					}
				}
				retryCounter = 0;
			} catch (Exception error) {
				logger.error("The exception occurred is in this method is : " + error);
				return null;
			}
			return response;
		} else {
			logger.error("The exception occurred ");
			retryCounter = 0;
			return null;
		}
	}

	/**
	 * This method is used to call the get method without authentication and returns the response
	 *
	 * @param url
	 * @param apiName
	 * @param headers
	 * @param queryParam
	 * @return
	 */
	public static Response callGetAPI(final String url, final String apiName,
	                                  final Map<String, Object> headers,
	                                  final MultivaluedMap<String, String> queryParam) {
		return callAPI(url, apiName, null, headers, RestMethod.TYPE.GET, queryParam, null);
	}

	/**
	 * This method is used to call the get method with authentication and returns the response
	 *
	 * @param url
	 * @param apiName
	 * @param auth
	 * @param headers
	 * @param queryParam
	 * @return
	 */
	public static Response callGetAPIWithAuth(final String url, final String apiName, final Map<String, String> auth,
	                                          final Map<String, Object> headers,
	                                          final MultivaluedMap<String, String> queryParam) {
		return callAPI(url, apiName, auth, headers, RestMethod.TYPE.GET, queryParam, null);
	}

	/**
	 * This method is used to call the post method without authentication and returns the response
	 *
	 * @param url
	 * @param apiName
	 * @param headers
	 * @param payload
	 * @return
	 */
	public static Response callPostAPI(final String url, final String apiName,
	                                   final Map<String, Object> headers,
	                                   final String payload) {
		return callAPI(url, apiName, null, headers, RestMethod.TYPE.POST, null, payload);
	}

	/**
	 * This method is used to call the put method without authentication and returns the response
	 *
	 * @param url
	 * @param apiName
	 * @param headers
	 * @param payload
	 * @return
	 */
	public static Response callPutAPI(final String url, final String apiName,
	                                  final Map<String, Object> headers,
	                                  final String payload) {
		return callAPI(url, apiName, null, headers, RestMethod.TYPE.PUT, null, payload);
	}

	/**
	 * This method is used to call the delete method without authentication and returns the response
	 *
	 * @param url
	 * @param apiName
	 * @param headers
	 * @return
	 */
	public static Response callDeleteAPI(final String url, final String apiName,
	                                     final Map<String, Object> headers,
	                                     final MultivaluedMap<String, String> queryParam) {
		return callAPI(url, apiName, null, headers, RestMethod.TYPE.DELETE, queryParam, null);
	}

	/**
	 * This method is used to call the get method with authentication and returns the response
	 *
	 * @param url
	 * @param apiName
	 * @param auth
	 * @param headers
	 * @param payload
	 * @return
	 */
	public static Response callPostAPIWithAuth(final String url, final String apiName, final Map<String, String> auth,
	                                           final Map<String, Object> headers,
	                                           final String payload) {
		return callAPI(url, apiName, auth, headers, RestMethod.TYPE.GET, null, payload);
	}

	/**
	 * This method is used to call the post method with parameters
	 *
	 * @param url
	 * @param apiName
	 * @param headers
	 * @param queryParam
	 * @return
	 */
	public static Response callPostAPIWithParams(String url, String apiName, Map<String, Object> headers, MultivaluedMap<String, String> queryParam) {
		return JerseyRestClientHelper.callAPI(url, apiName, null, headers, RestMethod.TYPE.POST, queryParam, null);
	}

	public static String getPayloadFromResponse(final Response response) {

		try {
			/*Reading the response in the string format*/
			String jsonStringResponse = response.readEntity(String.class);
			logger.debug("The payload is : " + jsonStringResponse);
			return jsonStringResponse;
		} catch (Exception error) {
			logger.error("The exception is : " + error);
			return null;
		}
	}

	public static LinkedTreeMap<?, ?> getResponseAsHashMap(String payload) {
		GsonBuilder builder = new GsonBuilder();
		Gson gson = builder.serializeNulls().create();
		return gson.fromJson(payload, LinkedTreeMap.class);
	}

	public static <T> T getResponseAsClass(String payload, Class<T> tClass) {
		GsonBuilder gsonBuilder = new GsonBuilder();
		Gson gson = gsonBuilder.serializeNulls().create();
		return gson.fromJson(payload, tClass);
	}


}
