package com.nayidisha.utils;

import java.io.InputStream;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class PropertyReader {
	public static Log log = LogFactory.getLog(PropertyReader.class.getName());
	private static Properties properties;
	public static String SERVICE_PROPERTIES = "service.properties";
	
    static {
        properties = new Properties();
        try {
            InputStream is = PropertyReader.class.getClassLoader().getResourceAsStream(SERVICE_PROPERTIES);
            if (is != null) {
                properties.load(is); 
                is.close();
            }


        }
        catch (Exception e) {
        	log.warn("Error reading property file " + SERVICE_PROPERTIES, e);
        }
    }
    
    public static String getProperty(String key) {
        String property = null;

        //First read System prop
        if (NDStringUtils.hasSomeMeaningfulValue(key)){
	        property = System.getProperty(key);
	        if (!NDStringUtils.hasSomeMeaningfulValue(property)){
		        if (properties.containsKey(key)) {
		            property = properties.getProperty(key);
		        }
	        }
        }
        return property;
    }
}
