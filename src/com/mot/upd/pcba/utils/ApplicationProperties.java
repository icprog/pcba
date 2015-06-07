/**
 * 
 */
package com.mot.upd.pcba.utils;

import java.util.HashMap;

import org.apache.log4j.Logger;

/**
 * @author HRDJ36 Thammaiah M B
 *
 */

public class ApplicationProperties {
	
	private static Logger logger = Logger.getLogger(ApplicationProperties.class);

	// Instance objects
	private static ApplicationProperties _instance;
	private final String MOT_ENV_STRING = "com.mot.corp.GISEnvironment";
	
	private String _strEnvironment;

	private static HashMap fileProperty = new HashMap();

	static {
		_instance = new ApplicationProperties();
	}

	// Protected so nobody instantiates this class directly
	// Client must always use the getInstance() method instead
	private ApplicationProperties() {
		System.out.println("ApplicationProperties Constructor");
		this.initialize();
	}

	// Returns the singleton reference
	public static ApplicationProperties getInstance() {
		System.out.println("ApplicationProperties.getInstance()");
		return (_instance);
	}

	// Retrieves application properties from file and puts them in a cache
	private void initialize() {
		System.out.println("ApplicationProperties.initialize()");
		// Get property defined on weblogic.Server command line (DEV, TEST or
		// PROD)
		// Default to DEV if no value provided (Better to mess up dev database
		// than any of the other ones...)
		//_strEnvironment = System.getProperty(MOT_ENV_STRING, "TEST");
		_strEnvironment = System.getProperty(MOT_ENV_STRING, null);
		logger.info("_strEnvironment -------------------" + _strEnvironment);
		if (_strEnvironment != null && !_strEnvironment.equals("")) {
			logger.info("_strEnvironment ------------------inside if-" + _strEnvironment);
			_strEnvironment = _strEnvironment.toLowerCase();
		} else {
			System.out
					.println("ApplicationProperties.initialize(): Failed to get system property "
							+ MOT_ENV_STRING + ". Using \"TEST\"");
			_strEnvironment = "test";
		}
		System.out
				.println("ApplicationProperties.initialize(): Getting PropertyResourceBundle \""
						+ "*_" + _strEnvironment + ".properties\".");
	}

	// Getter method for application properties
	public String getAppProperty(String propertyFile) {
		StringBuffer property_file = new StringBuffer();
		property_file.append(propertyFile.substring(0,
				propertyFile.indexOf(".")));
		property_file.append("_");
		property_file.append(_strEnvironment);
		property_file.append(".properties");
		System.out.println("ApplicationProperties.getAppProperty(\""
				+ property_file.toString() + "\")");
		return property_file.toString();
	}

}
