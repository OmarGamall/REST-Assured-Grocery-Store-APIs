package com.grocerystore.listeners;

import com.grocerystore.utils.ConfigLoader;
import com.grocerystore.utils.TokenManager;
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
                
                // Clear cached token only if the failure is likely related to authentication/unauthorized (401)
                Throwable throwable = result.getThrowable();
                if (throwable != null) {
                    String msg = throwable.toString().toLowerCase();
                    if (msg.contains("401") || msg.contains("unauthorized") || msg.contains("bearer token")) {
                        System.out.println("[Retry] Test failed with authentication issue. Evicting stale token from cache...");
                        TokenManager.clearToken();
                    }
                }
                
                return true;
            }
        }
        return false;
    }
}
