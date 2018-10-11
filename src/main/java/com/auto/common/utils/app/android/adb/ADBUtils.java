package com.auto.common.utils.app.android.adb;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ADBUtils {

	static Logger logger = LoggerFactory.getLogger(ADBUtils.class);
	private String ID;

	private ADBUtils() {
		//do nothing
	}

	public ADBUtils(final String deviceID) {
		ID = deviceID;
	}

	/**
	 * Android Menu button
	 */
	public static void clickAndroidMenu(final String androidPath) {
		try {
			Runtime.getRuntime().exec(
					androidPath + "/adb shell input keyevent 1");
		} catch (Exception e) {
			logger.error("clickAndroidMenu, Error is : " + e);
		}
	}

	/**
	 * Android Home button
	 */
	public static void clickAndroidHome(final String androidPath) {
		try {
			Runtime.getRuntime().exec(
					androidPath + "/adb shell input keyevent 3");
		} catch (Exception error) {
			logger.error("Error occurred in this clickAndroidHome " + error);
		}

	}

	/**
	 * Android Task Manager button
	 */
	public static void clickAndroidRecentApps(final String androidPath) {
		try {
			Runtime.getRuntime().exec(
					androidPath + "/adb shell input keyevent 187");
		} catch (Exception e) {
			logger.error("clickAndroidRecentApps, Error is : " + e);
		}
	}

	/**
	 * Android back button
	 */
	public static void clickAndroidBack(final String androidPath) {
		try {
			Runtime.getRuntime().exec(
					androidPath + "/adb shell input keyevent 4");
		} catch (Exception e) {
			logger.error("clickAndroidBack, Error is : " + e);
		}
	}

	/**
	 * Android power button
	 */
	public static void clickAndroidPower(final String androidPath) {
		try {
			Runtime.getRuntime().exec(
					androidPath + "/adb shell input keyevent 26");
		} catch (Exception e) {
			logger.error("clickAndroidPower, Error is : " + e);
		}
	}

	/**
	 * Android Status Bar
	 */
	public static void expandStatusBar(final String androidPath) {
		try {
			Runtime.getRuntime().exec(
					androidPath + "/adb shell service call statusbar 1");
		} catch (Exception e) {
			logger.error("clickAndroidPower, Error is : " + e);
		}
	}

	/**
	 * Android Status Bar
	 */
	public static void goSafeMode(final String androidPath) {
		try {
			Runtime.getRuntime().exec(
					androidPath + "/adb shell su -c 'setprop persist.sys.safemode 1'");
		} catch (Exception e) {
			logger.error("goSafeMode, Error is : " + e);
		}
	}

	public static List<String> runAdbCommand(String inputstr) {
		List<String> output = new ArrayList<>();
		Process p = null;
		try {
			logger.debug("command is : " + inputstr);
			p = Runtime.getRuntime().exec(inputstr);

			InputStream cmdStdOut = null;
			InputStream cmdStdErr = null;

			cmdStdOut = p.getInputStream();
			cmdStdErr = p.getErrorStream();

			String line = "";
			String line1 = "";
			BufferedReader stdOut = new BufferedReader(new InputStreamReader(
					cmdStdOut));
			BufferedReader stdErr = new BufferedReader(new InputStreamReader(
					cmdStdErr));
			while ((line = stdOut.readLine()) != null) {
				logger.debug(line);
				output.add(line);
			}
			stdOut.close();
			logger.debug("Error Status: ");
			while ((line1 = stdErr.readLine()) != null) {
				logger.debug(line1);
				output.add(line1);
			}
			stdErr.close();

			try {
				p.waitFor();

			} catch (InterruptedException error) {
				logger.error("The error in runAdbCommand interrupted : " + error);
				error.printStackTrace();
			}
		} catch (IOException error) {
			logger.error("The error in runAdbCommand : " + error);
			error.printStackTrace();
		}

		return output;
	}

	public static String command(String command) {

		try {
			String ANDROID_HOME = System.getenv("ANDROID_HOME");
			if (ANDROID_HOME == null) {
				throw new RuntimeException("Failed to find ANDROID_HOME, make sure the environment variable is set");
			} else {
				logger.debug("Formatting ADB Command: " + command);
				if (command.startsWith("adb")) command = command.replace("adb ", ANDROID_HOME + "/platform-tools/adb ");
				else throw new RuntimeException("This method is designed to run ADB commands only!");
				logger.debug("Formatted ADB Command: " + command);
				String output = null;
				Scanner scanner = new Scanner(Runtime.getRuntime().exec(command).getInputStream()).useDelimiter("\\A");
				if (scanner.hasNext()) output = scanner.next();
				logger.debug("Output of the ADB Command: " + output);
				if (output == null) return "";
				else return output.trim();
			}

		} catch (IOException error) {
			error.printStackTrace();
		}
		return null;
	}

	public static String getIpAddress() {
		try {
			String resp = command("adb shell ip addr show wlan0");
			String[] lines = resp.split(System.getProperty("line.separator"));
			for (int count = 0; count < lines.length; count++) {
				if (lines[count].contains("inet")) {
					String[] values = lines[count].trim().split(" ");
					String[] ipAddress = values[1].split("/");
					logger.info("The device ip address is :: " + ipAddress[0]);
					return ipAddress[0];
				}
			}
			return null;
		} catch (Exception error) {
			error.printStackTrace();
		}
		return null;
	}

	public static void killServer() {
		command("adb kill-server");
	}

	public static void startServer() {
		command("adb start-server");
	}

	public static ArrayList getConnectedDevices() {
		ArrayList devices = new ArrayList();
		String output = command("adb devices");
		for (String line : output.split("\n")) {
			line = line.trim();
			if (line.endsWith("device")) devices.add(line.replace("device", "").trim());
		}
		return devices;
	}

	public String getForegroundActivity() {
		return command("adb -s " + ID + " shell dumpsys window windows | grep mCurrentFocus");
	}

	public String getAndroidVersionAsString() {
		String output = command("adb -s " + ID + " shell getprop ro.build.version.release");
		if (output.length() == 3) output += ".0";
		return output;
	}

	public int getAndroidVersion() {
		return Integer.parseInt(getAndroidVersionAsString().replaceAll("\\.", ""));
	}

	public ArrayList getInstalledPackages() {
		ArrayList packages = new ArrayList();
		String[] output = command("adb -s " + ID + " shell pm list packages").split("\n");
		for (String packageID : output) packages.add(packageID.replace("package:", "").trim());
		return packages;
	}

	public void openAppsActivity(String packageID, String activityID) {
		command("adb -s " + ID + " shell am start -c api.android.intent.category.LAUNCHER -a api.android.intent.action.MAIN -n " + packageID + "/" + activityID);
	}

	public void clearAppsData(String packageID) {
		command("adb -s " + ID + " shell pm clear " + packageID);
	}

	public void forceStopApp(String packageID) {
		command("adb -s " + ID + " shell am force-stop " + packageID);
	}

	public void installApp(String apkPath) {
		command("adb -s " + ID + " install " + apkPath);
	}

	public void uninstallApp(String packageID) {
		command("adb -s " + ID + " uninstall " + packageID);
	}

	public void clearLogBuffer() {
		command("adb -s " + ID + " shell -c");
	}

	public void pushFile(String source, String target) {
		command("adb -s " + ID + " push " + source + " " + target);
	}

	public void pullFile(String source, String target) {
		command("adb -s " + ID + " pull " + source + " " + target);
	}

	public void deleteFile(String target) {
		command("adb -s " + ID + " shell rm " + target);
	}

	public void moveFile(String source, String target) {
		command("adb -s " + ID + " shell mv " + source + " " + target);
	}

	public void takeScreenshot(String target) {
		command("adb -s " + ID + " shell screencap " + target);
	}

	public void rebootDevice() {
		command("adb -s " + ID + " reboot");
	}

	public String getDeviceModel() {
		return command("adb -s " + ID + " shell getprop ro.product.model");
	}

	public String getDeviceSerialNumber() {
		return command("adb -s " + ID + " shell getprop ro.serialno");
	}

	public String getDeviceCarrier() {
		return command("adb -s " + ID + " shell getprop gsm.operator.alpha");
	}

	public ArrayList getLogcatProcesses() {
		String[] output = command("adb -s " + ID + " shell top -n 1 | grep -i 'logcat'").split("\n");
		ArrayList processes = new ArrayList();
		for (String line : output) {
			processes.add(line.split(" ")[0]);
			processes.removeAll(Arrays.asList("", null));
		}
		return processes;
	}

	public Object startLogcat(final String logID, final String grep) {
		ArrayList pidBefore = getLogcatProcesses();

		Thread logcat = new Thread(new Runnable() {
			@Override
			public void run() {
				if (grep == null) command("adb -s " + ID + " shell logcat -v threadtime > /sdcard/" + logID + ".txt");
				else
					command("adb -s " + ID + " shell logcat -v threadtime | grep -i '" + grep + "'> /sdcard/" + logID + ".txt");
			}
		});
		logcat.setName(logID);
		logcat.start();
		logcat.interrupt();

		ArrayList pidAfter = getLogcatProcesses();
		Timer timer = new Timer();
		timer.start();
		while (!timer.expired(5)) {
			if (pidBefore.size() > 0) pidAfter.removeAll(pidBefore);
			if (pidAfter.size() > 0) break;
			pidAfter = getLogcatProcesses();
		}

		if (pidAfter.size() == 1) return pidAfter.get(0);
		else if (pidAfter.size() > 1)
			throw new RuntimeException("Multiple logcat processes were started when only one was expected!");
		else throw new RuntimeException("Failed to start logcat process!");
	}

	public void stopLocat(Object PID) {
		command("adb -s " + ID + " shell kill " + PID);
	}

}

class Timer {

	public long startStamp;

	public static long getTimeStamp() {
		return new Date().getTime();
	}

	public static int getDifference(long start, long end) {
		return (int) ((end - start) / 1000);
	}

	public void start() {
		startStamp = getTimeStamp();
	}

	public boolean expired(int seconds) {
		int difference = (int) ((getTimeStamp() - startStamp) / 1000);
		return difference > seconds;
	}
}

