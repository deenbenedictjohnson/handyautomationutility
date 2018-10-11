package com.auto.common.utils.common;

import static com.auto.common.constants.CommonConstants.PATH;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;

public class ExcelUtils {

	static Logger logger = LoggerFactory.getLogger(ExcelUtils.class);

	private ExcelUtils() {
		//do nothing
	}

	/**
	 * This method is used to get the test data from the excel
	 *
	 * @param className
	 * @param fileName
	 * @return
	 */
	public static Object[][] getTestDataFromExcel(final String className, final String url, final String fileName) {
		return getDataFromExcel(className, url, fileName, null);
	}

	private static Object[][] getDataFromExcel(final String className, final String url, final String fileName,
	                                           final String type) {
		try {

			//To read the xlsx file
			FileInputStream file = new FileInputStream(new File(PATH + url + fileName));

			//Create Workbook instance holding reference to .xlsx file
			XSSFWorkbook workbook = new XSSFWorkbook(file);

			//Get first/desired sheet from the workbook
			XSSFSheet sheet = workbook.getSheetAt(0);

			//Iterate through each rows one by one
			Iterator<Row> rowIterator = sheet.iterator();

			Row row = rowIterator.next();
			Iterator<Cell> cellIterator = row.cellIterator();

			int rowCount = 0;

			//To Get the row count in the file
			while (rowIterator.hasNext()) {
				rowIterator.next();
				rowCount++;
			}

			//Staring the row iterator from the beginning
			rowIterator = sheet.iterator();

			List<String> headers = new ArrayList<>();

			//To get the header list from the file
			while (cellIterator.hasNext()) {
				Cell cell = cellIterator.next();
				headers.add(cell.getStringCellValue());
			}
			//Skipping the header row
			rowIterator.next();

			int rowCounter = 0;
			Map<String, Object> valueMap;
			Object[][] objects = new Object[rowCount][1];

			//Iterating row by row
			while (rowIterator.hasNext()) {
				row = rowIterator.next();

				//For each row, iterate through all the columns
				cellIterator = row.cellIterator();

				int colnCounter = 0;
				valueMap = readFromCells(cellIterator, headers, colnCounter);

				//Initializing the object type based on the class name
				Object[] object = new Object[1];
				Class classTemp = Class.forName(className);
				object[0] = classTemp.newInstance();

				//Mapping the map to the object
				ObjectMapper mapper = new ObjectMapper();
				object[0] = mapper.convertValue(valueMap, classTemp);

				//Assigning the object row by row
				objects[rowCounter++] = object;
			}

			//Closing the file connection
			file.close();

			return objects;
		} catch (Exception error) {
			logger.error("The exception occurred in this method : " + error);
			return new Object[0][0];
		}
	}

	public static List<Map<String, Object>> readFromExcel(final String filePath) {
		try {

			//To read the xlsx file
			FileInputStream file = new FileInputStream(new File(filePath));

			//Create Workbook instance holding reference to .xlsx file
			XSSFWorkbook workbook = new XSSFWorkbook(file);

			//Get first/desired sheet from the workbook
			XSSFSheet sheet = workbook.getSheetAt(0);

			//Iterate through each rows one by one
			Iterator<Row> rowIterator = sheet.iterator();

			Row row = rowIterator.next();
			Iterator<Cell> cellIterator = row.cellIterator();

			//To Get the row count in the file
			while (rowIterator.hasNext()) {
				rowIterator.next();
			}

			//Staring the row iterator from the beginning
			rowIterator = sheet.iterator();

			List<String> headers = new ArrayList<>();

			//To get the header list from the file
			while (cellIterator.hasNext()) {
				Cell cell = cellIterator.next();
				headers.add(cell.getStringCellValue());
			}
			//Skipping the header row
			rowIterator.next();

			List<Map<String, Object>> excelListValues = new ArrayList<>();
			Map<String, Object> valueMap;

			//Iterating row by row
			while (rowIterator.hasNext()) {
				row = rowIterator.next();

				//For each row, iterate through all the columns
				cellIterator = row.cellIterator();

				int colnCounter = 0;
				valueMap = readFromCells(cellIterator, headers, colnCounter);

				excelListValues.add(valueMap);
			}

			//Closing the file connection
			file.close();

			return excelListValues;
		} catch (Exception error) {
			logger.error("The exception occurred in this method : " + error);
			return null;
		}
	}

	private static Map<String, Object> readFromCells(final Iterator<Cell> cellIterator, final List<String> headers, int colnCounter) {

		Map<String, Object> valueMap = new HashMap<>();

		//Iterating the cell by cell in a row
		while (cellIterator.hasNext()) {

			Cell cell = cellIterator.next();
			String headerValue = headers.get(colnCounter++);

			//Check the cell type and format accordingly
			switch (cell.getCellType()) {
				case Cell.CELL_TYPE_NUMERIC:
					valueMap.put(headerValue, cell.getNumericCellValue());
					break;
				case Cell.CELL_TYPE_STRING:
					valueMap.put(headerValue, cell.getStringCellValue());
					break;
				case Cell.CELL_TYPE_BOOLEAN:
					valueMap.put(headerValue, cell.getBooleanCellValue());
					break;
				default:
					break;
			}
		}

		return valueMap;

	}

}
