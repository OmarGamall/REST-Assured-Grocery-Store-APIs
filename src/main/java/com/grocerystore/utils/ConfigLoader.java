package com.grocerystore.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ConfigLoader {
    private static final Properties properties = new Properties();

    static {
        try (InputStream is = ConfigLoader.class.getClassLoader().getResourceAsStream("config.properties")) {
            if (is == null) {
                throw new RuntimeException("config.properties file not found in resources folder");
            }
            properties.load(is);
        } catch (IOException e) {
            throw new RuntimeException("Could not load config.properties file", e);
        }
    }

    /**
     * Retrieves value for the given key from System properties (takes precedence)
     * or defaults to the properties file.
     *
     * @param key the property key to look up
     * @return the property value, or null if not found
     */
    public static String getProperty(String key) {
        String systemProperty = System.getProperty(key);
        if (systemProperty != null) {
            return systemProperty;
        }
        return properties.getProperty(key);
    }

    /**
     * Retrieves value for the given key. Returns defaultValue if the key is not found.
     *
     * @param key the property key to look up
     * @param defaultValue fallback value
     * @return the property value, or defaultValue if not found
     */
    public static String getProperty(String key, String defaultValue) {
        String value = getProperty(key);
        return value != null ? value : defaultValue;
    }
}
