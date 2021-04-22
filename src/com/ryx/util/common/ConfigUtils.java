package com.ryx.util.common;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ConfigUtils {

	public static String FILE_NAME = "config.properties";
    private static Properties prop     = null;
    private static ConfigUtils config;

    static {
    	config = new ConfigUtils();
    }
    
	public void loadPropertiesFromSrc() {
		InputStream in = null;
		try {
			in = ConfigUtils.class.getClassLoader().getResourceAsStream(FILE_NAME);
			if (in != null) {
				prop = new Properties();
				try {
					prop.load(in);
				} catch (IOException e) {
					throw e;
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (in != null)
				try {
					in.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
		}
	}

    public static String getProperty(String keyName) {
    	config.loadPropertiesFromSrc();
        return prop.getProperty(keyName);
    }

    public static String getProperty(String keyName, String defaultValue) {
    	config.loadPropertiesFromSrc();
        return prop.getProperty(keyName, defaultValue);
    }

}
