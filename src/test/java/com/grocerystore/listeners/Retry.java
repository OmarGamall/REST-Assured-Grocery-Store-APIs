package com.grocerystore.listeners;

import com.grocerystore.utils.ConfigLoader;
import org.testng.IRetryAnalyzer;
import org.testng.ITestResult;

public class Retry implements IRetryAnalyzer {
    private int count = 0;
    private static final int MAX_LIMIT;

    static {
        int limit = 0; // Default to 0 retries
        try {
            String limitProp = ConfigLoader.getProperty("retry.limit");
            if (limitProp != null) {
                limit = Integer.parseInt(limitProp.trim());
            }
        } catch (NumberFormatException e) {
            System.err.println("Invalid retry.limit configuration. Defaulting to 0.");
        }
        MAX_LIMIT = limit;
    }

    @Override
    public boolean retry(ITestResult result) {
        if (!result.isSuccess()) {
            if (count < MAX_LIMIT) {
                count++;
                System.out.println("Retrying test " + result.getName() + " for the " + count + " time(s) out of " + MAX_LIMIT);
                return true;
            }
        }
        return false;
    }
}
