package com.auto.common.utils.db.mysql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MySQLDBUtils {

	private static Logger logger = LoggerFactory.getLogger(MySQLDBUtils.class);

	private MySQLDBUtils() {
		//do nothing
	}

	/**
	 * This method is used to create connection
	 *
	 * @return
	 */
	public static Connection connection(final String dbHost, final String userName, final String password,
	                                    final String dbDriver) {
		Connection conn = null;
		try {
			Class.forName(dbDriver);
			conn = DriverManager.getConnection(dbHost, userName, password);
		} catch (Exception error) {
			logger.error("The exception for the mysql db connection is {0} :: ", error);
		}
		return conn;
	}

	/**
	 * This method is used to execute db query for select command
	 *
	 * @param query
	 * @param conn
	 * @return
	 * @throws SQLException
	 */
	public static List<Map<String, String>> executeSelectQuery(String query, Connection conn) throws SQLException {
		Map<String, String> response;
		List<Map<String, String>> responseList = new ArrayList<>();
		Statement statement = null;
		ResultSet resultSet = null;
		try {
			try {
				statement = conn.createStatement();
				resultSet = statement.executeQuery(query);
			} finally {
				if (statement != null) {
					statement.close();
				}
			}

			ResultSetMetaData metaData = resultSet.getMetaData();
			int columnsNumber = metaData.getColumnCount();
			while (resultSet.next()) {
				response = new HashMap<>();
				for (int count = 1; count <= columnsNumber; count++) {
					String columnValue = resultSet.getString(count);
					response.put(metaData.getColumnName(count), columnValue);
				}
				responseList.add(response);
			}
			logger.debug(String.format("The table value is %s ", responseList.toString()));
		} catch (Exception error) {
			logger.error("The exception in executeQuery is {0} ", error);
		} finally {
			if (resultSet != null) {
				resultSet.close();
			}
		}
		return responseList;
	}

	/**
	 * This method is used to execute db query for update command
	 *
	 * @param query
	 * @param conn
	 * @return
	 * @throws SQLException
	 */
	public static boolean executeUpdateQuery(String query, Connection conn) throws SQLException {
		Statement statement = null;

		try {
			statement = conn.createStatement();
			return (statement.executeUpdate(query) >= 1);
		} catch (Exception error) {
			logger.error("The Exception in executeUpdateQuery method is {0} ", error);
		} finally {
			if (statement != null) {
				statement.close();
			}
		}
		return false;
	}

	/**
	 * This method is used to update the table
	 *
	 * @param query
	 * @param values
	 * @throws SQLException
	 */
	public static void updateExecuteQuery(String query, Map<Integer, String> values) throws SQLException {
		//to do
	}

}
