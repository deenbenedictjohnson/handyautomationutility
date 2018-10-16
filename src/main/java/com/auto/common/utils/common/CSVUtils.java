package com.auto.common.utils.common;

import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.opencsv.CSVReader;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;

public class CSVUtils {

	private static Logger logger = LoggerFactory.getLogger(CSVUtils.class);

	private CSVUtils() {
		//do nothing
	}

	/**
	 * To read the csv file based on the file path
	 *
	 * @param filePath
	 * @return
	 */
	public static Object[][] readCSVFile(final String filePath) throws IOException {
		Reader reader = null;
		CSVReader csvReader = null;
		try {

			reader = Files.newBufferedReader(Paths.get(filePath));

			// Reading All Records at once into a List<String[]>
			List<String[]> csvData;

			try {
				csvReader = new CSVReader(reader);
				csvData = csvReader.readAll();

			} finally {
				if (csvReader != null) {
					csvReader.close();
				}
			}

			Object[][] csvDataSet = new Object[csvData.size()][];

			int count = 0;
			for (String[] record : csvData) {
				csvDataSet[count++] = record;
			}

			return csvDataSet;

		} catch (IOException error) {
			logger.error("The exception in readCSVFile is :: " + error);
		} finally {
			if (reader != null) {
				reader.close();
			}
		}
		return null;
	}

	/**
	 * To read the csv file based on the filepath and the class name
	 *
	 * @param filePath
	 * @param className
	 * @return
	 * @throws IOException
	 */
	public static Object[][] readCSVFile(final String filePath, final String className) throws IOException {
		Reader reader = null;
		try {

			reader = Files.newBufferedReader(Paths.get(filePath));
			Class classTemp = Class.forName(className);

			CsvToBean csvToBean = new CsvToBeanBuilder(reader)
					.withType(classTemp)
					.withIgnoreLeadingWhiteSpace(true)
					.build();

			Iterator csvUserIterator = csvToBean.iterator();


			List<Object> objectList = new ArrayList<>();

			int count = 0;

			while (csvUserIterator.hasNext()) {
				objectList.add(csvUserIterator.next());
				count++;
			}

			Object[][] csvDataSet = new Object[count][];

			count = 0;
			for (Object obj : objectList) {
				Object[] object = new Object[1];
				object[0] = obj;
				csvDataSet[count++] = object;
			}

			return csvDataSet;
		} catch (Exception error) {
			error.printStackTrace();
		} finally {
			if (reader != null) {
				reader.close();
			}
		}
		return null;
	}

}
