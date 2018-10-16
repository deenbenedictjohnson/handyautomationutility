package com.auto.common.testng.listener;

import static com.auto.common.constants.CommonConstants.PATH;
import static com.auto.common.constants.CommonConstants.reportFormatList;

import java.util.Properties;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import javax.mail.Authenticator;
import javax.mail.Message.RecipientType;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.auto.common.utils.common.PropertyUtils;
import sun.misc.BASE64Decoder;

public class SendEmail {

	private static final String ALGORITHM = "AES";
	private static final String KEY = "1Hbfh667adfDEJ78";
	private static final String UTF8 = "utf-8";
	private static final String AUTH_ENABLED = "true";
	private static final String STARTTLS_ENABLED = "true";
	private static final String HOST_NAME = "smtp.gmail.com";
	private static final String PORT_NO = "587";
	private static final String MAIL_SMTP = "mail.smtp";
	private static final String AUTH = MAIL_SMTP + ".auth";
	private static final String HOST = MAIL_SMTP + ".host";
	private static final String PORT = MAIL_SMTP + ".port";
	private static final String STARTTLS = MAIL_SMTP + ".starttls.enable";
	private static final String PROJECT_NAME = "projectName";
	private static final String ENV = "env";

	static Logger logger = LoggerFactory.getLogger(SendEmail.class);

	private SendEmail() {
	}

	public static String decrypt(String value) {
		try {
			SecretKeySpec error = new SecretKeySpec(KEY.getBytes(), ALGORITHM) {
			};
			Cipher cipher = Cipher.getInstance(ALGORITHM);
			cipher.init(2, error);
			byte[] decryptedValue64 = (new BASE64Decoder()).decodeBuffer(value);
			byte[] decryptedByteValue = cipher.doFinal(decryptedValue64);
			return new String(decryptedByteValue, UTF8);
		} catch (Exception error) {
			logger.error("The error occurred during decryption is : " + error);
			return null;
		}
	}

	public static void triggerMail() {

		PropertyUtils reportProperty = new PropertyUtils(PATH + "/config/report.properties");

		if (reportProperty.getProperty("reportEnabled").trim().equalsIgnoreCase("true")) {
			logger.debug("Triggering mail for the report");
			String to = reportProperty.getProperty("to");
			logger.debug("Sending reports to the email : " + to);
			String[] recipientList = to.split(",");
			InternetAddress[] recipientAddress = new InternetAddress[recipientList.length];
			String cc = reportProperty.getProperty("cc");
			logger.debug("Sending reports cc the email : " + cc);
			String[] ccRecipientList = cc.split(",");
			InternetAddress[] ccRecipientAddress = new InternetAddress[ccRecipientList.length];
			String from = reportProperty.getProperty("from");
			logger.debug("Sending reports from the email : " + from);
			final String username = reportProperty.getProperty("username");
			logger.debug("Sending reports for the username : " + username);
			final String password = decrypt(reportProperty.getProperty("password"));
			Properties props = new Properties();
			props.put(AUTH, AUTH_ENABLED);
			props.put(STARTTLS, STARTTLS_ENABLED);
			props.put(HOST, HOST_NAME);
			props.put(PORT, PORT_NO);
			Session session = Session.getInstance(props, new Authenticator() {
				protected PasswordAuthentication getPasswordAuthentication() {
					return new PasswordAuthentication(username, password);
				}
			});

			try {
				MimeMessage message = new MimeMessage(session);
				message.setFrom(new InternetAddress(from));
				int counter = 0;
				for (String recipient : recipientList) {
					recipientAddress[counter] = new InternetAddress(recipient.trim());
					counter++;
				}
				counter = 0;
				for (String recipient : ccRecipientList) {
					ccRecipientAddress[counter] = new InternetAddress(recipient.trim());
					counter++;
				}
				message.setRecipients(RecipientType.TO, recipientAddress);
				message.setRecipients(RecipientType.CC, ccRecipientAddress);
				String content = "";
				int count = 1;
				int totalCount = 0;
				int passedCount = 0;
				int failedCount = 0;
				int skippedCount = 0;

				for (ReportFormatter reportFormatter : reportFormatList) {
					logger.debug("Coming inside to generate the report  ");
					logger.debug("Total : " + reportFormatter.getTotal());
					logger.debug("Passed : " + reportFormatter.getPassed());
					logger.debug("Failed : " + reportFormatter.getFailed());
					logger.debug("Skipped : " + reportFormatter.getSkipped());
					content = content + "<tr><td>" + count++ + "</td>";
					content = content + "<td>" + reportFormatter.getComponent() + "</td>";
					content = content + "<td>" + reportFormatter.getOwner() + "</td>";
					content = content + "<td>" + reportFormatter.getTotal() + "</td>";
					content = content + "<td>" + reportFormatter.getPassed() + "</td>";
					content = content + "<td>" + reportFormatter.getFailed() + "</td>";
					content = content + "<td>" + reportFormatter.getSkipped() + "</td></tr>";
					logger.debug("content : " + content);
					totalCount += reportFormatter.getTotal();
					passedCount += reportFormatter.getPassed();
					failedCount += reportFormatter.getFailed();
					skippedCount += reportFormatter.getSkipped();
					logger.debug("totalCount : " + totalCount);
					logger.debug("passedCount : " + passedCount);
					logger.debug("failedCount : " + failedCount);
					logger.debug("skippedCount : " + skippedCount);
				}

				content = content + "<tr><td></td>";
				content = content + "<td></td>";
				content = content + "<td bgcolor=\"#fff0b3\"><b>Total</b></td>";
				content = content + "<td bgcolor=\"#ccccb3\"><b>" + totalCount + "</b></td>";
				content = content + "<td bgcolor=\"#b3ffb3\"><b>" + passedCount + "</b></td>";
				content = content + "<td bgcolor=\"#ff9999\"><b>" + failedCount + "</b></td>";
				content = content + "<td bgcolor=\"#80ccff\"><b>" + skippedCount + "</b></td></tr>";
				message.setSubject(reportProperty.getProperty(PROJECT_NAME) + " Report in " + reportProperty.getProperty(ENV) + " environment");

				String note = "Refer the detailed report <u> http://10.20.96.28:8080/view/Automation/job/" + reportProperty.getProperty(PROJECT_NAME) + "/Automation_Results</u><br>";
				note = note + "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Further details refer this link " + reportProperty.getProperty("report_url") + "</u>";

				message.setContent("<html>" +
						"<head>" +
						"<style>" +
						"<link rel=\'stylesheet prefetch\' href=\'http://maxcdn.bootstrapcdn.com/bootswatch/3.2.0/sandstone/bootstrap.min.css\'>\n    " +
						"<link rel=\'stylesheet prefetch\' href=\'http://maxcdn.bootstrapcdn.com/font-awesome/4.2.0/css/font-awesome.min.css\'>" +
						"</style>" +
						"</head>" +
						"Hi All,<br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<br>" +
						"&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<b>Please find the automation report for the project " + reportProperty.getProperty(PROJECT_NAME) + " </b>" +
						"<br><br>" +
						"<table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" height=\"100%\" width=\"100%\" id=\"bodyTable\">\n" +
						"<tr>\n" +
						"<td align=\"center\" valign=\"top\">\n" +
						"<table class=\"table table-bordered table-striped\" border=\"1\" cellpadding=\"5\" cellspacing=\"0\" width=\"600\" id=\"emailContainer\">\n" +
						"<tr>\n" +
						"<th>S.No</th>\n" +
						"<th>Component</th>\n" +
						"<th>Owner</th>\n" +
						"<th>Total Automated</th>\n" +
						"<th>Success</th>\n" +
						"<th>Failure</th>\n" +
						"<th>Skipped</th>\n" +
						"</tr>\n" + content +
						"</table>\n" +
						"</td>\n" +
						"</tr>\n" +
						"</table>" +
						"<br>" +
						"<b>Note :-</b><br> &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Triggered automatically.<br>" +
						"&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;" + note +
						"</html>", "text/html");

				if (reportFormatList.size() != 0) {
					Transport.send(message);
				}

				logger.debug("Sent report to email successfully");
			} catch (MessagingException error) {
				logger.error("The exception is : " + error);
			}
		}


	}

	public static void triggerMail(final String subject, final String content, final String msg, final String to, final String cc) {

		PropertyUtils reportProperty = new PropertyUtils(PATH + "/config/report.properties");

		logger.debug("Triggering mail for the report");
		String[] recipientList = to.split(",");
		InternetAddress[] recipientAddress = new InternetAddress[recipientList.length];
		String[] ccRecipientList = cc.split(",");
		InternetAddress[] ccRecipientAddress = new InternetAddress[ccRecipientList.length];
		String from = reportProperty.getProperty("from");
		logger.debug("Sending reports from the email : " + from);
		final String username = reportProperty.getProperty("username");
		logger.debug("Sending reports for the username : " + username);
		final String password = decrypt(reportProperty.getProperty("password"));
		Properties props = new Properties();
		props.put(AUTH, AUTH_ENABLED);
		props.put(STARTTLS, STARTTLS_ENABLED);
		props.put(HOST, HOST_NAME);
		props.put(PORT, PORT_NO);
		Session session = Session.getInstance(props, new Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(username, password);
			}
		});

		try {
			MimeMessage message = new MimeMessage(session);
			message.setFrom(new InternetAddress(from));
			int counter = 0;
			for (String recipient : recipientList) {
				recipientAddress[counter] = new InternetAddress(recipient.trim());
				counter++;
			}
			counter = 0;
			for (String recipient : ccRecipientList) {
				ccRecipientAddress[counter] = new InternetAddress(recipient.trim());
				counter++;
			}
			message.setRecipients(RecipientType.TO, recipientAddress);
			message.setRecipients(RecipientType.CC, ccRecipientAddress);

			message.setSubject(subject);

			message.setContent("<html>" +
					"<head>" +
					"<style>" +
					"<link rel=\'stylesheet prefetch\' href=\'http://maxcdn.bootstrapcdn.com/bootswatch/3.2.0/sandstone/bootstrap.min.css\'>\n    " +
					"<link rel=\'stylesheet prefetch\' href=\'http://maxcdn.bootstrapcdn.com/font-awesome/4.2.0/css/font-awesome.min.css\'>" +
					"</style>" +
					"</head>" +
					"Hi All,<br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;" +
					"<b> " + content + " </b>" +
					"<br>" +
					"<br>" +
					"<b>Note :-</b><br> &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Triggered automatically.<br><br>" +
					"&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;" + msg +
					"</html>", "text/html");

			Transport.send(message);
			logger.debug("Sent report to email successfully");
		} catch (MessagingException error) {
			logger.error("The exception is : " + error);
		}


	}


}


