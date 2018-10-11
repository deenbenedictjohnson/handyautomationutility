package com.auto.common.utils.common;

import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.List;

import com.auto.common.constants.CommonConstants;
import com.auto.data.model.Employee;
import com.opencsv.CSVReader;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;

public class CSVUtils {

	private CSVUtils() {
		//do nothing
	}

	public static Object[][] readCSVFile(final String filePath) {
		try {

			Reader reader = Files.newBufferedReader(Paths.get(filePath));
			CSVReader csvReader = new CSVReader(reader);

			// Reading All Records at once into a List<String[]>
			List<String[]> csvData = csvReader.readAll();

			Object[][] csvDataSet = new Object[csvData.size()][];

			int count = 0;
			for (String[] record : csvData) {
				csvDataSet[count++] = record;
			}

			return csvDataSet;

		} catch (IOException error) {
			error.printStackTrace();
		}
		return null;
	}

	public static void readCSVFile(final String filePath, final String className) {
		try {
			Reader reader = Files.newBufferedReader(Paths.get(filePath));
			Class classTemp = Class.forName(className);

			CsvToBean csvToBean = new CsvToBeanBuilder(reader)
					.withType(classTemp)
					.withIgnoreLeadingWhiteSpace(true)
					.build();

			Iterator csvUserIterator = csvToBean.iterator();

			System.out.println("==========================");
			while (csvUserIterator.hasNext()) {
				Employee employee = (Employee) csvUserIterator.next();
				System.out.print("Employee : " + employee);
			}
			System.out.println("==========================");

		} catch (Exception error) {
			error.printStackTrace();
		}
	}

	public static void main(String[] args) {

		System.out.println(ExcelUtils.readFromExcel(CommonConstants.PATH + "/config/testdata/employee.xlsx"));

		Object[][] objects = ExcelUtils.getTestDataFromExcel("com.auto.data.model.Employee", "/config/testdata/", "employee.xlsx");

		for (Object[] obj : objects) {
			for (Object object : obj) {
				Employee employee = (Employee) object;
				System.out.println(employee);
			}
		}

		objects = readCSVFile(CommonConstants.PATH + "/config/testdata/employee_csv.csv");

		System.out.println("***** CSV ******");
		for (Object[] obj : objects) {
			for (Object object : obj) {
				System.out.print(object + "  ");
			}
			System.out.println();
		}

		readCSVFile(CommonConstants.PATH + "/config/testdata/employee_csv.csv", "com.auto.data.model.Employee");


	}

}
