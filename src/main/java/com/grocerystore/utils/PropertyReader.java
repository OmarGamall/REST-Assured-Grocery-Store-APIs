package com.grocerystore.utils;

import org.apache.commons.io.FileUtils;
import java.io.File;
import java.io.InputStream;
import java.util.Collection;
import java.util.Properties;

public class PropertyReader {
    private static final Properties properties = new Properties();
    private static final String MAIN_RESOURCES_PATH = "src/main/resources";
    private static final String TEST_RESOURCES_PATH = "src/test/resources";

    static {
        loadAllProperties();
    }

    /**
     * Scans and loads all .properties files from the resources directories.
     * System-defined configurations (like CLI arguments) take precedence.
     */
    public static void loadAllProperties() {
        try {
            Properties localProps = new Properties();

            // 1. Scan and load main resources first
            loadFromDirectory(MAIN_RESOURCES_PATH, localProps);

            // 2. Scan and load test resources second (overwriting duplicate keys)
            loadFromDirectory(TEST_RESOURCES_PATH, localProps);

            // 3. Merge local properties into our local properties object
            properties.putAll(localProps);

            // 4. Safely merge into System properties WITHOUT overwriting CLI flags (-D arguments)
            for (String key : localProps.stringPropertyNames()) {
                if (!System.getProperties().containsKey(key)) {
                    System.setProperty(key, localProps.getProperty(key));
                }
            }
        } catch (Exception e) {
            LogsManager.error("Critical failure scanning resources directory: ", e);
        }
    }

    private static void loadFromDirectory(String dirPath, Properties localProps) {
        File dir = new File(dirPath);
        if (!dir.exists()) {
            return;
        }

        try {
            Collection<File> propertiesFiles = FileUtils.listFiles(dir, new String[]{"properties"}, true);
            for (File file : propertiesFiles) {
                try (InputStream is = FileUtils.openInputStream(file)) {
                    localProps.load(is);
                } catch (Exception e) {
                    LogsManager.error("Error loading individual file: {} - {}", file.getName(), e.getMessage());
                }
            }
        } catch (Exception e) {
            LogsManager.error("Error reading directory: {} - {}", dirPath, e.getMessage());
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
