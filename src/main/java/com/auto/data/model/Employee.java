package com.auto.data.model;

import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class Employee {

	@SerializedName("emp_name")
	private String empName;

	@SerializedName("emp_id")
	private int empId;

	private int age;

	private String company;

	@SerializedName("pan_no")
	private String panNo;

	@SerializedName("aadhar_no")
	private String aadharNo;

	private String address;

	@SerializedName("father_name")
	private String fatherName;

	@SerializedName("mother_name")
	private String motherName;

	private int pincode;

	private Employee(EmployeeBuilder employee) {
		this.empName = employee.empName;
		this.empId = employee.empId;
		this.age = employee.age;
		this.company = employee.company;
		this.panNo = employee.panNo;
		this.aadharNo = employee.aadharNo;
		this.address = employee.address;
		this.fatherName = employee.fatherName;
		this.motherName = employee.motherName;
		this.pincode = employee.pincode;

	}

	public static class EmployeeBuilder {

		private String empName;
		private int empId;
		private int age;
		private String company;
		private String panNo;
		private String aadharNo;
		private String address;
		private String fatherName;
		private String motherName;
		private int pincode;

		public EmployeeBuilder(final int empId, final String empName, final int age) {
			this.empId = empId;
			this.empName = empName;
			this.age = age;
			this.company = "default";
			this.panNo = "default";
			this.aadharNo = "default";
			this.address = "default";
			this.fatherName = "default";
			this.motherName = "default";
			this.pincode = 5603;

		}

		public EmployeeBuilder setEmpName(String empName) {
			this.empName = empName;
			return this;
		}

		public EmployeeBuilder setEmpId(int empId) {
			this.empId = empId;
			return this;
		}

		public EmployeeBuilder setAge(int age) {
			this.age = age;
			return this;
		}

		public EmployeeBuilder setCompany(String company) {
			this.company = company;
			return this;
		}

		public EmployeeBuilder setPanNo(String panNo) {
			this.panNo = panNo;
			return this;
		}

		public EmployeeBuilder setAadharNo(String aadharNo) {
			this.aadharNo = aadharNo;
			return this;
		}

		public EmployeeBuilder setAddress(String address) {
			this.address = address;
			return this;
		}

		public EmployeeBuilder setFatherName(String fatherName) {
			this.fatherName = fatherName;
			return this;
		}

		public EmployeeBuilder setMotherName(String motherName) {
			this.motherName = motherName;
			return this;
		}

		public EmployeeBuilder setPincode(int pincode) {
			this.pincode = pincode;
			return this;
		}

		public Employee build() {
			return new Employee(this);
		}

	}


}
