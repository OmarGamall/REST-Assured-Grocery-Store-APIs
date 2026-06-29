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
}
