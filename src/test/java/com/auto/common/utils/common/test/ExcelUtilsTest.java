package com.auto.common.utils.common.test;

import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

import com.auto.common.constants.CommonConstants;
import com.auto.common.utils.common.ExcelUtils;

public class ExcelUtilsTest {

	@Test
	public void getTestDataFromExcelTest() {
		List<Map<String, Object>> list = ExcelUtils.readFromExcel(CommonConstants.PATH + "/config/testdata/employee.xlsx");

		for (Map<String, Object> map : list) {
			for (Map.Entry<String, Object> entry : map.entrySet()) {
				Assert.assertNotNull(entry.getKey());
				Assert.assertNotNull(entry.getValue());
				System.out.print(entry.getKey() + "=" + entry.getValue()+", ");
			}
			System.out.println();
		}

	}

	@Test
	public void readFromExcelTest() {
		Object[][] objects = ExcelUtils.getTestDataFromExcel("com.auto.data.model.Employee", "/config/testdata/", "employee.xlsx");
		AssertionUtils.verifyNotNull(objects);
	}

}
