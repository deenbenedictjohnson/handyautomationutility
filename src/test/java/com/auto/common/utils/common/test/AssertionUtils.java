package com.auto.common.utils.common.test;

import org.junit.Assert;

import com.auto.data.model.Employee;

public class AssertionUtils {

	public static void verifyNotNull(final Object[][] objects) {
		for (Object[] obj : objects) {
			for (Object object : obj) {
				Employee employee = (Employee) object;
				System.out.println(employee);
				Assert.assertNotNull(employee.getAge());
				Assert.assertNotNull(employee.getEmpId());
				Assert.assertNotNull(employee.getCompany());
				Assert.assertNotNull(employee.getPanNo());
				Assert.assertNotNull(employee.getAadharNo());
				Assert.assertNotNull(employee.getAddress());
				Assert.assertNotNull(employee.getFatherName());
				Assert.assertNotNull(employee.getMotherName());
				Assert.assertNotNull(employee.getPincode());
			}
		}
	}

}
