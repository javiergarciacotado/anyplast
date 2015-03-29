package com.anyplast.portal.email;

import java.io.IOException;
import java.util.Properties;

public class MandrillConfiguration {

	private final Properties properties;
		
	public MandrillConfiguration() {
		properties = new Properties();
		try {
			properties.load(getClass().getResourceAsStream("/mandrill.properties"));
		} catch (IOException | NullPointerException ex) {
			ex.printStackTrace();
		}
	}
	
	public String get(String key) {
		return properties.getProperty(key);
	}
	
	public String get(String key, String defaultValue) {
		return properties.getProperty(key, defaultValue);
	}
	
}
