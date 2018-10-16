package com.auto.common.utils.db.mongo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mongodb.MongoClient;
import com.mongodb.MongoCredential;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;

public class MongoDBUtils {

	private static MongoClient mongoClient = null;

	private static Logger logger = LoggerFactory.getLogger(MongoDBUtils.class);

	/**
	 * This method is used to connect with mongo booking system db
	 *
	 * @return
	 */
	public static synchronized MongoDatabase dbConnect(final String url, final int port, final String userName, final String password, final String dbName) {

		logger.debug("The mongo db host : " + url + " port :" + port);
		mongoClient = new MongoClient(url, port);

		// Creating Credentials
		MongoCredential credential = MongoCredential.createCredential(userName, dbName, password.toCharArray());
		logger.info("Connected to the database successfully");

		// Accessing the database
		MongoDatabase database = mongoClient.getDatabase(dbName);
		logger.info("Credentials ::" + credential);

		return database;
	}

	/**
	 * To get the collection list from mongo based on mongo database
	 *
	 * @param database
	 * @return
	 */
	public static List<String> getCollectionList(final MongoDatabase database) {
		List<String> collectionList = new ArrayList<>();

		for (String name : database.listCollectionNames()) {
			collectionList.add(name);
		}
		return collectionList;
	}

	/**
	 * To get the collection list from mongo based on mongo client and db name
	 *
	 * @param mongoClient
	 * @param dbName
	 * @return
	 */
	public static List<String> getCollectionList(final MongoClient mongoClient, final String dbName) {
		MongoDatabase database = mongoClient.getDatabase(dbName);
		return getCollectionList(database);
	}

	/**
	 * To get the collection list based on host, port and db name
	 *
	 * @param host
	 * @param port
	 * @param dbName
	 * @return
	 */
	public static List<String> getCollectionList(final String host, final int port, final String dbName) {
		MongoClient mongoClient = null;
		try {
			mongoClient = new MongoClient(host, port);
			return getCollectionList(mongoClient, dbName);
		} catch (Exception error) {
			logger.error("The exception in getCollectionList is {0} ", error);
		} finally {
			mongoClient.close();
		}
		return null;
	}

	/**
	 * To get the database statistics based on mongo client and db name
	 *
	 * @param mongoClient
	 * @param dbName
	 */
	public static void getDatabaseStatistics(final MongoClient mongoClient, final String dbName) {
		MongoDatabase database = mongoClient.getDatabase(dbName);
		Document stats = database.runCommand(new Document("dbstats", 1));

		for (Map.Entry<String, Object> set : stats.entrySet()) {

			System.out.format("%s: %s%n", set.getKey(), set.getValue());
		}
	}

	/**
	 * To get the database statistics based on host, port and db name
	 *
	 * @param host
	 * @param port
	 * @param dbName
	 */
	public static void getDatabaseStatistics(final String host, final int port, final String dbName) {
		MongoClient mongoClient = null;
		try {
			mongoClient = new MongoClient(host, port);
			getDatabaseStatistics(mongoClient, dbName);
		} catch (Exception error) {
			logger.error("The exception in getDatabaseStatistics is {0} ", error);
		} finally {
			mongoClient.close();
		}
	}

	/**
	 * To read the collection based on host, port, db name and collection name
	 *
	 * @param host
	 * @param port
	 * @param dbName
	 * @param collectionName
	 * @return
	 */
	public static HashMap<String, String> readCollection(final String host, final int port, final String dbName, final String collectionName) {

		MongoClient mongoClient = null;
		try {
			mongoClient = new MongoClient(host, port);
			return readCollection(mongoClient, dbName, collectionName);
		} catch (Exception error) {
			logger.error("The exception in readCollection is {0} ", error);
		} finally {
			mongoClient.close();
		}
		return null;
	}

	/**
	 * To read the collection based on mongo client, db name and collection name
	 *
	 * @param mongoClient
	 * @param dbName
	 * @param collectionName
	 * @return
	 */
	public static HashMap<String, String> readCollection(final MongoClient mongoClient, final String dbName, final String collectionName) {
		try {
			MongoDatabase database = mongoClient.getDatabase(dbName);
			return readCollection(database, collectionName);
		} catch (Exception error) {
			logger.error("The exception in readCollection is {0} ", error);
		}
		return null;
	}

	/**
	 * To read the collection based on database and collection name
	 *
	 * @param database
	 * @param collectionName
	 * @return
	 */
	public static HashMap<String, String> readCollection(final MongoDatabase database, final String collectionName) {
		try {
			MongoCollection<Document> col = database.getCollection(collectionName);

			HashMap<String, String> keyValueMap = new HashMap<>();
			try (MongoCursor<Document> cur = col.find().iterator()) {
				while (cur.hasNext()) {
					Document doc = cur.next();
					List list = new ArrayList(doc.values());
					keyValueMap.put(list.get(1).toString(), list.get(2).toString());
				}
			}
			return keyValueMap;
		} catch (Exception error) {
			logger.error("The exception in readCollection is {0} ", error);
		}
		return null;
	}


}
