package com.mot.upd.pcba.utils;


/*
 * MOTOROLA INTERNAL USE ONLY
 * Copyright MOTOROLA (c)2000
 * All Rights Reserved
 */

import java.io.InputStream;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

import org.apache.log4j.Logger;

//import org.apache.log4j.Logger;

/*
 * @author Jyoti Swain(TCSL)
 * 
 * @version 0.1 Date:03/10/2006
 */

public class SwaggerApplicationProperties {

	private static Logger log = Logger.getLogger(SwaggerApplicationProperties.class);

	private static SwaggerApplicationProperties _instance = null;

	private static PropertyResourceBundle _propertyBundle = null;

	// Constants
	private final String CONFIG_BUNDLE_BASENAME = "pcbaSwagger";

	private final String MOT_ENV_STRING = "com.mot.corp.GISEnvironment";

	@SuppressWarnings("rawtypes")
	private static HashMap fileProperty = new HashMap();

	static {
		_instance = new SwaggerApplicationProperties();
	}

	/*
	 * Protected so nobody instantiates this class directly Client must always
	 * use the getInstance() method instead
	 */

	private SwaggerApplicationProperties() {

		this.initialize();
	}

	/*
	 * Creates once and returns the singleton reference
	 */
	public static SwaggerApplicationProperties getInstance() {

		return (_instance);
	}

	public String getAppProperty(String key) {		
		return _propertyBundle.getString(key);
	}

	/*
	 * Retrieves application properties from file and puts them in a cache
	 */
	private void initialize() {		
		log.info("SwaggerApplicationProperties.initialize()");
		/*
		 * Get property defined on weblogic.Server command line (DEV, TEST or
		 * PROD) Default to DEV if no value provided (Better to mess up dev
		 * database than any of the other ones...)
		 */

		String strEnvironment = System.getProperty(MOT_ENV_STRING, null);

		if (strEnvironment != null && !strEnvironment.equals("")) {
			strEnvironment = strEnvironment.toLowerCase();
		} else {			
			strEnvironment = "dev";
			log
					.debug("SwaggerApplicationProperties.initialize(): Failed to get system property "
							+ MOT_ENV_STRING + ". Using \"DEV\"");
		}		
		log
				.debug("SwaggerApplicationProperties.initialize(): Getting PropertyResourceBundle \""
						+ CONFIG_BUNDLE_BASENAME + "_" + strEnvironment + "\".");

		/*
		 * Retrieve properties from file into resource bundle object and store
		 * it in singleton
		 */

		_propertyBundle = (PropertyResourceBundle) ResourceBundle
				.getBundle(CONFIG_BUNDLE_BASENAME + "_" + strEnvironment);

	}

	/**
	 * @param file
	 * @param key
	 * @return
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static String getProperty(String file, String key) {
		HashMap properties = (HashMap) fileProperty.get(file);
		if (properties == null) {
			properties = new HashMap();

			try {

				ClassLoader loader = ApplicationProperties.class
						.getClassLoader();
				InputStream input = loader.getResourceAsStream(_instance
						.getAppProperty(file));
				PropertyResourceBundle bundle = new PropertyResourceBundle(
						input);

				Enumeration enum_keys = bundle.getKeys();
				while (enum_keys.hasMoreElements()) {
					String fileKey = (String) enum_keys.nextElement();
					String property = bundle.getString(fileKey);
					properties.put(fileKey, property);
				}

				fileProperty.put(file, properties);

			} catch (Exception e) {				
				log.debug("Error reading " + key
						+ " from properties file for : " + file);

			}
		}
		return (String) properties.get(key);
	}
}