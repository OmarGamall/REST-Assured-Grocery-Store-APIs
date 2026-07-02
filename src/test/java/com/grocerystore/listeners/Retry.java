package com.grocerystore.listeners;

import com.grocerystore.utils.PropertyReader;
import com.grocerystore.utils.TokenManager;
import com.grocerystore.utils.LogsManager;
import org.testng.IRetryAnalyzer;
import org.testng.ITestResult;

public class Retry implements IRetryAnalyzer {
    private int count = 0;
    private static final int MAX_LIMIT;

    static {
        int limit = 0; // Default to 0 retries
        try {
            String limitProp = PropertyReader.getProperty("retry.limit");
            if (limitProp != null) {
                limit = Integer.parseInt(limitProp.trim());
            }
        } catch (NumberFormatException e) {
            LogsManager.error("Invalid retry.limit configuration. Defaulting to 0.");
        }
        MAX_LIMIT = limit;
    }

    @Override
    public boolean retry(ITestResult result) {
        if (!result.isSuccess()) {
            if (count < MAX_LIMIT) {
                count++;
                LogsManager.warn("Retrying test {} for the {} time(s) out of {}", result.getName(), count, MAX_LIMIT);
                
                // Clear cached token only if the failure is likely related to authentication/unauthorized (401)
                Throwable throwable = result.getThrowable();
                if (throwable != null) {
                    String msg = throwable.toString().toLowerCase();
                    if (msg.contains("401") || msg.contains("unauthorized") || msg.contains("bearer token")) {
                        LogsManager.warn("Test failed with authentication issue. Evicting stale token from cache...");
                        TokenManager.clearToken();
                    }
                }
                
                return true;
            }
        }
        return false;
    }
}
