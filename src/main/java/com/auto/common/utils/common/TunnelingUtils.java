package com.auto.common.utils.common;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.davidmoten.rx.util.Pair;
import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

public class TunnelingUtils {

	private static final String STRICT_HOST_KEY_CHECKING = "StrictHostKeyChecking";
	public static Session jumpSession;
	static Logger logger = LoggerFactory.getLogger(TunnelingUtils.class);

	private TunnelingUtils() {
		//do nothing
	}

	/**
	 * This method is used to create the ssh tunnel
	 *
	 * @param localPort
	 * @param remotePort
	 * @param remoteHost
	 * @param remoteForwardingIp
	 * @return
	 */
	public static Session createSSHTunnel(final int localPort,
	                                      final int remotePort, final String remoteHost,
	                                      final String remoteForwardingIp, final String sshKeyPath,
	                                      final String sshKeyPass, final String sshUserName) {
		try {

			if (jumpSession != null) {
				TunnelingUtils.jumpSession.disconnect();
				jumpSession = null;
			}

			Session session;

			logger.debug("The values used for the tunneling localPort : " + localPort + " Remote Port : " + remotePort + " Remote Host : " + remoteHost
					+ " SSH Key path : " + sshKeyPath + " SSH Key Pass : " + sshKeyPass + " SSH Username : " + sshUserName + " Remote Forwarding IP: " + remoteForwardingIp);

			JSch jsch = new JSch();

			//Adding the identity
			jsch.addIdentity(sshKeyPath, sshKeyPass);

			// Create SSH session.  Port 22 is your SSH port which
			// is open in your firewall setup.
			session = jsch.getSession(sshUserName, remoteHost);

			// Additional SSH options.  See your ssh_config manual for
			// more options.  Set options according to your requirements.
			Properties config = new Properties();
			config.put(STRICT_HOST_KEY_CHECKING, "no");
			config.put("Compression", "yes");
			config.put("ConnectionAttempts", "2");
			session.setConfig(config);

			// Connect
			session.connect();

			//Local port forwarding
			session.setPortForwardingL(localPort, remoteForwardingIp, remotePort);

			return session;
		} catch (JSchException error) {
			logger.error("The error occurred during tunneling : " + error);
			return null;
		} catch (Exception error) {
			logger.error("The error occurred during tunneling : " + error);
			return null;
		}
	}

	/**
	 * This method is used to create the channel for tunneling
	 *
	 * @param sessions
	 * @param hosts
	 * @param jsch
	 * @return
	 */
	private static Pair<Integer, Channel> createChannel(final Session[] sessions, final String[] hosts, final JSch jsch) {
		try {
			Session session;
			String host = hosts[0];
			String user = host.substring(0, host.indexOf('@'));
			host = host.substring(host.indexOf('@') + 1);

			sessions[0] = session = jsch.getSession(user, host, 22);

			Properties config = new Properties();
			config.put(STRICT_HOST_KEY_CHECKING, "no");
			session.setConfig(config);

			session.connect();
			logger.debug("The session established to " + user + "@" + host);
			int assignedPort;
			int dbPort = 0;
			for (int i = 1; i < hosts.length; i++) {
				host = hosts[i];
				user = host.substring(0, host.indexOf('@'));
				host = host.substring(host.indexOf('@') + 1);

				assignedPort = session.setPortForwardingL(0, host, 22);
				dbPort = session.setPortForwardingL(0, host, 27017);
				logger.debug("Port Forwarding: " +
						"localhost:" + assignedPort + " -> " + host + ":" + 22);
				sessions[i] = session =
						jsch.getSession(user, "127.0.0.1", assignedPort);

				config = new Properties();
				config.put(STRICT_HOST_KEY_CHECKING, "no");
				session.setConfig(config);

				session.setHostKeyAlias(host);
				session.connect();
				logger.debug("The session has established to " +
						user + "@" + host);
			}

			return new Pair<>(dbPort, session.openChannel("exec"));
		} catch (JSchException error) {
			logger.error("The exception occurred in this method create channel is  : " + error);
			error.printStackTrace();
			return null;
		}
	}

	/**
	 * This method is used to read the logs
	 *
	 * @param grep
	 * @param expected
	 * @param getCurrentLogs
	 * @param ms
	 * @return
	 */
	public static boolean readLogs(final String grep, final String expected,
	                               final boolean getCurrentLogs, final long ms, final String jump, final String remote,
	                               final String logPath, final String sshKeyPath,
	                               final String sshKeyPass, final String sshUserName) {
		try {

			final String jumpHostName = sshUserName + "@" + jump;
			final String remoteHostName = sshUserName + "@" + remote;

			JSch jsch = new JSch();
			jsch.addIdentity(sshKeyPath, sshKeyPass);

			String[] hosts = {jumpHostName, remoteHostName};
			Session[] sessions = new Session[hosts.length];
			Pair<Integer, Channel> pair = createChannel(sessions, hosts, jsch);
			assert pair != null;
			Channel channel = pair.b();

			assert channel != null;
			channel.setInputStream(null);
			((ChannelExec) channel).setErrStream(System.err);
			InputStream in = channel.getInputStream();
			BufferedReader br = new BufferedReader(new InputStreamReader(in));

			String command;
			String line;
			StringBuilder sBuffer = new StringBuilder();

			if (getCurrentLogs) {
				command = "tail -f " + logPath + " | grep \"" + grep + "\"";
				((ChannelExec) channel).setCommand(command);
				channel.connect();
				long now = System.currentTimeMillis() + ms;
				while ((line = br.readLine()) != null) {
					sBuffer.append(line);
					if (now <= System.currentTimeMillis()) {
						channel.disconnect();
					}
				}
			} else {
				command = "tail -n40000 " + logPath + " | grep \"" + grep + "\"";
				((ChannelExec) channel).setCommand(command);
				channel.connect();

				while ((line = br.readLine()) != null) {
					sBuffer.append(line);
					logger.debug(line);
				}
				channel.disconnect();
			}
			br.close();
			logger.debug(sBuffer.toString());

			for (int i = sessions.length - 1; i >= 0; i--) {
				sessions[i].disconnect();
			}

			return sBuffer.toString().contains(expected);
		} catch (Exception error) {
			logger.error("The exception occurred in this method read logs is  : " + error);
			error.printStackTrace();
			return false;
		}
	}

	/**
	 * This method is used to return the string from the logs
	 *
	 * @param grep
	 * @return
	 */
	public static StringBuilder returnString(final String grep, final String jump,
	                                         final String remote, final String logPath,
	                                         final String sshKeyPath,
	                                         final String sshKeyPass, final String sshUserName) {
		try {

			JSch jsch = new JSch();
			jsch.addIdentity(sshKeyPath, sshKeyPass);

			final String jumpHostName = sshUserName + "@" + jump;
			final String remoteHostName = sshUserName + "@" + remote;
			String[] hosts = {jumpHostName, remoteHostName};

			Session[] sessions = new Session[hosts.length];
			Pair<Integer, Channel> pair = createChannel(sessions, hosts, jsch);
			assert pair != null;
			Channel channel = pair.b();

			assert channel != null;
			channel.setInputStream(null);
			((ChannelExec) channel).setErrStream(System.err);
			InputStream in = channel.getInputStream();
			BufferedReader br = new BufferedReader(new InputStreamReader(in));

			String command;
			String line;
			StringBuilder sBuffer = new StringBuilder();

			command = "tail -n10000 " + logPath + " | grep \"" + grep + "\"";
			((ChannelExec) channel).setCommand(command);
			channel.connect();

			while ((line = br.readLine()) != null) {
				sBuffer.append(line).append("//n");
				logger.debug(line);
			}
			channel.disconnect();
			br.close();
			logger.debug(sBuffer.toString());

			for (int i = sessions.length - 1; i >= 0; i--) {
				sessions[i].disconnect();
			}
			return sBuffer;
		} catch (Exception error) {
			logger.error("The exception occurred in this method returnString is  : " + error);
			error.printStackTrace();
			return null;
		}
	}

	public static StringBuilder readSystemLogs(final String grep, final String logFilePath,
	                                           final String remoteHost, final String jumpHost,
	                                           final String sshKeyPath,
	                                           final String sshKeyPass, final String sshUserName) {
		try {
			JSch jsch = new JSch();
			jsch.addIdentity(sshKeyPath, sshKeyPass);

			final String jumpHostName = sshUserName + "@" + jumpHost;
			final String remoteHostName = sshUserName + "@" + remoteHost;
			String[] hosts = {jumpHostName, remoteHostName};

			Session[] sessions = new Session[hosts.length];
			Pair<Integer, Channel> pair = createChannel(sessions, hosts, jsch);
			assert pair != null;
			Channel channel = pair.b();
			assert channel != null;
			channel.setInputStream(null);
			((ChannelExec) channel).setErrStream(System.err);
			InputStream in = channel.getInputStream();
			BufferedReader br = new BufferedReader(new InputStreamReader(in));

			String command;
			String line;
			StringBuilder sBuffer = new StringBuilder();

			command = "tail -n5000 " + logFilePath + " | grep \"" + grep + "\"";
			((ChannelExec) channel).setCommand(command);
			channel.connect();

			while ((line = br.readLine()) != null) {
				sBuffer.append(line).append("//n");
				logger.debug(line);
			}
			channel.disconnect();
			br.close();
			logger.debug(sBuffer.toString());

			for (int i = sessions.length - 1; i >= 0; i--) {
				sessions[i].disconnect();
			}
			return sBuffer;
		} catch (Exception error) {
			logger.error("The exception occurred in this method returnString is  : " + error);
			error.printStackTrace();
			return null;
		}
	}

	public static void closeAllSessions(Session[] sessions) {
		for (int i = sessions.length - 1; i >= 0; i--) {
			sessions[i].disconnect();
		}
	}

}
