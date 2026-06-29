package com.grocerystore.listeners;

import com.grocerystore.utils.TokenManager;
import org.testng.IExecutionListener;

public class TokenCleanupListener implements IExecutionListener {
    @Override
    public void onExecutionStart() {
        System.out.println("[TokenCleanupListener] Initializing token manager. Ensuring clean slate...");
        TokenManager.clearToken();
        com.grocerystore.utils.AllureUtilities.cleanAllureResults();
    }

    @Override
    public void onExecutionFinish() {
        System.out.println("[TokenCleanupListener] Cleaning up token manager to prevent memory leaks...");
        TokenManager.clearToken();
        com.grocerystore.utils.AllureUtilities.writeEnvironmentProperties();
    }
}
