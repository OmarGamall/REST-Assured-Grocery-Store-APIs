package com.grocerystore.utils;

import org.apache.commons.io.FileUtils;
import java.io.File;

public class AllureUtilities {

    /**
     * Deletes the Allure results directory quietly to ensure a clean slate before each test run.
     */
    public static void cleanAllureResults() {
        String resultsDir = PropertyReader.getProperty("allure.results.directory", "target/allure-results");
        File directory = new File(resultsDir);
        if (directory.exists()) {
            System.out.println("[AllureUtilities] Cleaning allure results directory: " + directory.getAbsolutePath());
            FileUtils.deleteQuietly(directory);
        } else {
            System.out.println("[AllureUtilities] Allure results directory does not exist or already clean: " + directory.getAbsolutePath());
        }
    }

    /**
     * Dynamically writes environment.properties file containing OS, JDK, and environment URL details
     * into the Allure results directory on execution finish.
     */
    public static void writeEnvironmentProperties() {
        String resultsDir = PropertyReader.getProperty("allure.results.directory", "target/allure-results");
        File directory = new File(resultsDir);
        if (!directory.exists()) {
            directory.mkdirs();
        }
        File propertiesFile = new File(directory, "environment.properties");
        java.util.Properties properties = new java.util.Properties();
        properties.setProperty("OS", System.getProperty("os.name"));
        properties.setProperty("JDK Version", System.getProperty("java.version"));
        properties.setProperty("Test Env URL", com.grocerystore.apis.Routes.BASE_URI);
        properties.setProperty("Active Environment Profile", PropertyReader.getProperty("env", "production"));

        try (java.io.FileOutputStream fos = new java.io.FileOutputStream(propertiesFile)) {
            properties.store(fos, "Allure Environment Properties");
            System.out.println("[AllureUtilities] environment.properties successfully written to: " + propertiesFile.getAbsolutePath());
        } catch (java.io.IOException e) {
            System.err.println("[AllureUtilities] Failed to write environment.properties: " + e.getMessage());
        }
    }
}
