package com.auto.common.testng.listener;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.google.gson.annotations.SerializedName;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Testcase {

	private String date;

	@SerializedName("build_name")
	private String buildName;

	@SerializedName("build_version")
	private String buildVersion;

	@SerializedName("job_name")
	private String jobName;

	@SerializedName("testcase_id")
	private String testcaseId;

	@SerializedName("testcase_name")
	private String testcaseName;

	private String description;

	@SerializedName("owner_name")
	private String ownerName;

	private String priority;

	private String status;

	@SerializedName("executed_on")
	private String executedOn;

	private String group;

	private String reason;

	private String comments;

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getBuildName() {
		return buildName;
	}

	public void setBuildName(String buildName) {
		this.buildName = buildName;
	}

	public String getBuildVersion() {
		return buildVersion;
	}

	public void setBuildVersion(String buildVersion) {
		this.buildVersion = buildVersion;
	}

	public String getJobName() {
		return jobName;
	}

	public void setJobName(String jobName) {
		this.jobName = jobName;
	}

	public String getTestcaseId() {
		return testcaseId;
	}

	public void setTestcaseId(String testcaseId) {
		this.testcaseId = testcaseId;
	}

	public String getTestcaseName() {
		return testcaseName;
	}

	public void setTestcaseName(String testcaseName) {
		this.testcaseName = testcaseName;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getOwnerName() {
		return ownerName;
	}

	public void setOwnerName(String ownerName) {
		this.ownerName = ownerName;
	}

	public String getPriority() {
		return priority;
	}

	public void setPriority(String priority) {
		this.priority = priority;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getExecutedOn() {
		return executedOn;
	}

	public void setExecutedOn(String executedOn) {
		this.executedOn = executedOn;
	}

	public String getGroup() {
		return group;
	}

	public void setGroup(String group) {
		this.group = group;
	}

	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}

	public String getComments() {
		return comments;
	}

	public void setComments(String comments) {
		this.comments = comments;
	}

	@Override
	public String toString() {
		return "Testcase{" +
				"date='" + date + '\'' +
				", buildName='" + buildName + '\'' +
				", buildVersion='" + buildVersion + '\'' +
				", jobName='" + jobName + '\'' +
				", testcaseId='" + testcaseId + '\'' +
				", testcaseName='" + testcaseName + '\'' +
				", description='" + description + '\'' +
				", ownerName='" + ownerName + '\'' +
				", priority='" + priority + '\'' +
				", status='" + status + '\'' +
				", executedOn='" + executedOn + '\'' +
				", group='" + group + '\'' +
				", reason='" + reason + '\'' +
				", comments='" + comments + '\'' +
				'}';
	}
}

