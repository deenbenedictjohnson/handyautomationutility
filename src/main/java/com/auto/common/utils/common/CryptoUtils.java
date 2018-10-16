package com.auto.common.utils.common;

import java.security.Key;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

public class CryptoUtils {

	private static final String ALGORITHM = "AES";
	private static final String KEY = "1Hbfh667adfDEJ78";
	static Logger logger = LoggerFactory.getLogger(CryptoUtils.class);

	/**
	 * This method generate key for encryption & decryption
	 *
	 * @return
	 * @
	 */
	private static Key generateKey() {
		logger.debug("Generating the key for the encryption and the decryption");
		final Key key = new SecretKeySpec(KEY.getBytes(), ALGORITHM);
		logger.debug("The Generated Key is : " + key);
		return key;
	}


	/**
	 * This method decrypts the value
	 *
	 * @param value
	 * @return
	 */
	public static String decrypt(final String value) {
		if (value == null) {
			return null;
		}
		try {
			logger.debug("Decrypting the text : " + value);
			Key key = new SecretKeySpec(KEY.getBytes(), ALGORITHM) {
			};
			Cipher cipher = Cipher.getInstance(ALGORITHM);
			cipher.init(Cipher.DECRYPT_MODE, key);
			byte[] decryptedValue64 = new BASE64Decoder().decodeBuffer(value);
			byte[] decryptedByteValue = cipher.doFinal(decryptedValue64);
			return new String(decryptedByteValue, "utf-8");
		} catch (Exception error) {
			logger.error("The error occurred during decryption is : " + error);

		}
		return null;
	}

	/**
	 * This method encrypts the value
	 *
	 * @param value
	 * @return
	 */
	public static String encrypt(final String value) {
		try {
			Key key = generateKey();
			Cipher cipher = Cipher.getInstance(ALGORITHM);
			cipher.init(Cipher.ENCRYPT_MODE, key);
			byte[] encryptedByteValue = cipher.doFinal(value.getBytes("utf-8"));
			return new BASE64Encoder().encode(encryptedByteValue);
		} catch (Exception error) {
			logger.error("The error occurred during encryption is : " + error);
		}
		return null;
	}

	public static String getMd5(String input) {
		if (input == null) {
			return "";
		}
		try {
			java.security.MessageDigest md = java.security.MessageDigest.getInstance("MD5");
			byte[] array = md.digest(input.getBytes());
			StringBuffer sb = new StringBuffer();
			for (int i = 0; i < array.length; ++i) {
				sb.append(Integer.toHexString((array[i] & 0xFF) | 0x100).substring(1, 3));
			}
			return sb.toString();
		} catch (java.security.NoSuchAlgorithmException e) {
		}
		return null;
	}

}
