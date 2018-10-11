package com.auto.common.utils.common.test;

import static com.auto.common.utils.common.CSVUtils.readCSVFile;

import org.junit.Assert;
import org.junit.Test;

import com.auto.common.constants.CommonConstants;

public class CSVUtilsTest {

	@Test
	public void readCSVFileTest() {
		Object[][] objects = readCSVFile(CommonConstants.PATH + "/config/testdata/employee_csv.csv");

		for (Object[] obj : objects) {
			for (Object object : obj) {
				Assert.assertNotNull(object);
			}
		}

	}

	@Test
	public void readCSVFileToBeanTest() {
		readCSVFile(CommonConstants.PATH + "/config/testdata/employee_csv.csv", "com.auto.data.model.Employee");
	}

}
