package com.auto.common.utils.db.mysql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
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
			error.printStackTrace();
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
			statement = conn.createStatement();
			resultSet = statement.executeQuery(query);

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
			logger.debug("The table value :: " + responseList);
		} catch (Exception error) {
			logger.error("The exception in executeQuery is : " + error);
			error.printStackTrace();
		} finally {
			if (resultSet != null) {
				resultSet.close();
			}
			if (statement != null) {
				statement.close();
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
	 * @throws Exception
	 */
	public static boolean executeUpdateQuery(String query, Connection conn) throws SQLException {
		Statement statement = null;

		try {
			statement = conn.createStatement();
			return (statement.executeUpdate(query) >= 1) ? true : false;
		} catch (Exception error) {
			error.printStackTrace();
		} finally {
			if (statement != null) {
				statement.close();
			}
		}
		return false;
	}

	//To do
	private static void updateExecuteQuery(String query, Map<Integer, String> values, String imei) throws SQLException {
		Connection conn = null;
		PreparedStatement preparedStatement = null;

		try {
			preparedStatement = conn.prepareStatement(query);
			for (int count = 1; count <= values.size(); count++) {
				preparedStatement.setString(count, values.get(count));
			}
			preparedStatement.executeUpdate();
		} catch (SQLException error) {
			logger.error("The exception in updateCityTaxiTable is : " + error);
		} finally {
			if (preparedStatement != null) {
				preparedStatement.close();
			}
			if (conn != null) {
				conn.close();
			}
		}
	}

}
