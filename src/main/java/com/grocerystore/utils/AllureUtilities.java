package com.grocerystore.utils;

import com.grocerystore.apis.Routes;
import org.apache.commons.io.FileUtils;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

public class AllureUtilities {

    /**
     * Deletes the Allure results directory quietly to ensure a clean slate before each test run.
     */
    public static void cleanAllureResults() {
        // 1. Clean Allure Results Directory
        String resultsDir = PropertyReader.getProperty("allure.results.directory", "target/allure-results");
        File resultsDirectory = new File(resultsDir);
        if (resultsDirectory.exists()) {
            LogsManager.info("Cleaning allure results directory: {}", resultsDirectory.getAbsolutePath());
            FileUtils.deleteQuietly(resultsDirectory);
        } else {
            LogsManager.info("Allure results directory does not exist or already clean: {}", resultsDirectory.getAbsolutePath());
        }

        // 2. Clean Allure Report Directory
        String reportDir = PropertyReader.getProperty("allure.report.directory", "target/allure-report");
        File reportDirectory = new File(reportDir);
        if (reportDirectory.exists()) {
            LogsManager.info("Cleaning allure report directory: {}", reportDirectory.getAbsolutePath());
            FileUtils.deleteQuietly(reportDirectory);
        } else {
            LogsManager.info("Allure report directory does not exist or already clean: {}", reportDirectory.getAbsolutePath());
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
        Properties properties = new Properties();

        // Define Properties that will be present in the file
        String os = System.getProperty("os.name");
        String jdk = System.getProperty("java.version");
        String env = PropertyReader.getProperty("env");
        if (os != null) {
            properties.setProperty("OS", os);
        }
        if (jdk != null) {
            properties.setProperty("JDK Version", jdk);
        }
        if (env != null) {
            properties.setProperty("Active Environment Profile", env);
            properties.setProperty("Test Env URL", Routes.BASE_URI);
        }

        try (FileOutputStream fos = new FileOutputStream(propertiesFile)) {
            properties.store(fos, "Allure Environment Properties");
            LogsManager.info("environment.properties successfully written to: {}", propertiesFile.getAbsolutePath());
        } catch (IOException e) {
            LogsManager.error("Failed to write environment.properties: {}", e.getMessage());
        }
    }
}

