package com.skyun.app.util.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Properties;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AppConfig {

	private static String CONF_PATH = null;
	private static String LOGJ_PATH = null;
	private static String PID = null;

	private static Logger logger = LoggerFactory.getLogger(AppConfig.class);
	private static Properties props = new Properties();

	public static String currentDir = null;

	public String key = "IoptsKeyKey12345"; // 128 bit key
	public String initVector = "1234qwerasdfzxcv"; // 16 bytes IV
	
	public static HashMap<String, String> account = new HashMap();
	
	static {
		currentDir = System.getProperty("user.dir");
		File f = new File(currentDir);
		currentDir = f.getParent().toString();

		LOGJ_PATH = currentDir + "/conf/logback.properties";
		CONF_PATH = currentDir + "/conf/server.properties";
		System.setProperty("logback.configurationFile", "file:/" + LOGJ_PATH);

		//PropertyConfigurator.configure(AppConfig.getLOGJ_PATH());
		//System.setProperty("log4j.configurationFile", "file:/" + LOGJ_PATH);

		LocaleMessageFromResource();

	}

	public AppConfig() {

	}

	public static String getProperty(String key) {

		String value = props.getProperty(key);

		if (value == null) {
			logger.error(String.format("getProperty value not exit [%s] file Value [%s]", currentDir, key));
			return "";
		} else {
			value = value.trim();
			return value;
		}
	}

	public static String getPropertyDecode(String key) {
		String value = props.getProperty(key);
		if (value == null) {
			logger.error(String.format("getPropertyDecode value not exit [%s] file Value [%s]", currentDir, key));
			return "";
		} else {
			value = value.trim();
			return value;
		}
	}

	public static String getLocaleMesage(String key) {
		return props.getProperty(key).trim();
	}

	public static String getLocaleMesage(String key, String decode) {

		String str = "None";
		try {
			str = new String(props.getProperty(key).trim().toString().getBytes("iso-8859-1"), decode);
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			logger.error(String.format("getLocaleMesage value not exit [%s] file Value [%s]", currentDir, key));
		}

		return str;
	}

	public static String getLOGJ_PATH() {
		return LOGJ_PATH;
	}

	public static void setPID(String s) {
		PID = s;
	}

	public static String getPID() {
		return PID;
	}

	public static int getPropertyInt(String key) {
		String value = props.getProperty(key);

		if (value == null) {
			logger.error(String.format("getPropertyInt value not exit [%s] file Value [%s]", currentDir, key));
			return 0;
		} else {
			value = value.trim();
			int a = Integer.parseInt(value);
			return a;
		}
	}

	public static Long getPropertyLong(String key) {
		String value = props.getProperty(key);

		if (value == null) {
			logger.error(String.format("getPropertyLong value not exit [%s] file Value [%s]", currentDir, key));
			return 0L;
		} else {
			value = value.trim();
			return Long.parseLong(value);
		}
	}

	public static boolean getPropertyBoolean(String key) {
		String value = props.getProperty(key).toLowerCase();

		if (value == null) {
			logger.error(String.format("getPropertyBoolean value not exit [%s] file Value [%s]", currentDir, key));
			return false;
		} else if (value.toLowerCase().equals("false")) {
			return false;
		} else {
			return true;
		}
	}

	public static void setProperty(String key, String value) {
		props.setProperty(key, value);
	}

	//
	private static void LocaleMessageFromResource() {

		FileInputStream lis;
		FileInputStream fis;

		try {
			//lis = new FileInputStream(LOGJ_PATH);
			fis = new FileInputStream(CONF_PATH);

			//props.load(lis);
			props.load(fis);

			//lis.close();
			fis.close();

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			logger.error(e.getMessage());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			logger.error(e.getMessage());
		}

	}

	// ��ȣȭ ���
	public static String encrypt(String key, String initVector, String value) {
		try {
			IvParameterSpec iv = new IvParameterSpec(initVector.getBytes("UTF-8"));
			SecretKeySpec skeySpec = new SecretKeySpec(key.getBytes("UTF-8"), "AES");

			Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
			cipher.init(Cipher.ENCRYPT_MODE, skeySpec, iv);

			byte[] encrypted = cipher.doFinal(value.getBytes());

			String s = new String(Base64.getEncoder().encode(encrypted));
			return s;

		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return null;
	}

	// ��ȣȭ ���
	public static String decrypt(String key, String initVector, String encrypted) {
		try {
			IvParameterSpec iv = new IvParameterSpec(initVector.getBytes("UTF-8"));
			SecretKeySpec skeySpec = new SecretKeySpec(key.getBytes("UTF-8"), "AES");

			Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
			cipher.init(Cipher.DECRYPT_MODE, skeySpec, iv);

			byte[] original = cipher.doFinal(Base64.getDecoder().decode(encrypted));
			return new String(original);

		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return null;
	}

	public static String getDate(String s) {

		Timestamp timestamp = new Timestamp(Long.parseLong(s) * 1000);
		Date date = new Date(timestamp.getTime());

		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd' 'HH:mm:ss");
		return simpleDateFormat.format(timestamp);

	}



}
