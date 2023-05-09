package com.skyun.app.util.config;

import java.io.IOException;
import java.util.Locale;

//System.out.println("OS: " + OSInfo.getOs());
//System.out.println("OS version: " + OSInfo.getOs().getVersion());
//System.out.println("Is mac? " + OSInfo.OS.MAC.equals(OSInfo.getOs()));

public class OSInfo {

	public enum OS {
		WINDOWS, LINUX, SUN, MAC, HPUX, AIX, OTHER;

		private String version;

		public String getVersion() {
			return version;
		}

		public void setVersion(String version) {
			this.version = version;
		}
	}

	private static OS os = OS.OTHER;

	static {
		try {
			String osName = System.getProperty("os.name");
			if (osName == null) {
				throw new IOException("os.name not found");
			}
			osName = osName.toLowerCase(Locale.ENGLISH);
			if (osName.contains("windows")) {
				os = OS.WINDOWS;
			} else if (osName.contains("linux") || osName.contains("mpe/ix") || osName.contains("freebsd") || osName.contains("irix") || osName.contains("digital unix") || osName.contains("unix")) {
				os = OS.LINUX;
			} else if (osName.contains("mac os")) {
				os = OS.MAC;
			} else if (osName.contains("sun os") || osName.contains("sunos") || osName.contains("solaris")) {
				os = OS.SUN;
			} else if (osName.contains("hp-ux") ) {
				os = OS.HPUX;
			} else if (osName.contains("aix")) {
				os = OS.AIX;
			} else {
				os = OS.OTHER;
			}
		} catch (Exception ex) {
			os = OS.OTHER;
		} finally {
			os.setVersion(System.getProperty("os.version"));
		}
	}

	public static OS getOs() {
		return os;
	}
}