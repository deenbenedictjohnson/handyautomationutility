package com.auto.common.utils.db.mongo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mongodb.MongoClient;
import com.mongodb.MongoCredential;
import com.mongodb.client.MongoDatabase;

public class MongoDBUtils {

	private static MongoClient mongoClient = null;

	private static Logger logger = LoggerFactory.getLogger(MongoDBUtils.class);

	/**
	 * This method is used to connect with mongo booking system db
	 *
	 * @return
	 */
	public static synchronized MongoClient dbConnect(final String url, final int port) {

		logger.debug("The mongo db host : " + url + " port :" + port);
		mongoClient = new MongoClient(url, port);

		// Creating Credentials
		MongoCredential credential = MongoCredential.createCredential("sampleUser", "myDb", "password".toCharArray());
		System.out.println("Connected to the database successfully");

		// Accessing the database
		MongoDatabase database = mongoClient.getDatabase("myDb");
		System.out.println("Credentials ::" + credential);
		return null;
	}

}
