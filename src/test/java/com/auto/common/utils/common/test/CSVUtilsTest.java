package com.auto.common.utils.common.test;

import static com.auto.common.utils.common.CSVUtils.readCSVFile;

import java.io.IOException;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.auto.common.constants.CommonConstants;

public class CSVUtilsTest {

	private static Logger logger = LoggerFactory.getLogger(CSVUtilsTest.class);

	private static String filePath;

	@BeforeClass
	public static void setUp() {
		filePath = CommonConstants.PATH + "/config/testdata/employee_csv.csv";
	}

	@Test
	public void readCSVFileTest() throws IOException {
		Object[][] objects = readCSVFile(CommonConstants.PATH + "/config/testdata/employee_csv.csv");

		for (Object[] obj : objects) {
			for (Object object : obj) {
				Assert.assertNotNull(object);
				System.out.print(object + "  ");
			}
			System.out.println();
		}
	}

	@Test
	public void readCSVFileToBeanTest() throws IOException {
		Object[][] objects = readCSVFile(filePath, "com.auto.data.model.Employee");
		AssertionUtils.verifyNotNull(objects);
	}

}
