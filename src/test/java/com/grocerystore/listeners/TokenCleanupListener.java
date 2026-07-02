package com.grocerystore.listeners;

import com.grocerystore.utils.TokenManager;
import com.grocerystore.utils.LogsManager;
import org.testng.IExecutionListener;

public class TokenCleanupListener implements IExecutionListener {
    @Override
    public void onExecutionStart() {
        LogsManager.info("Initializing token manager. Ensuring clean slate...");
        TokenManager.clearToken();
        com.grocerystore.utils.AllureUtilities.cleanAllureResults();
    }

    @Override
    public void onExecutionFinish() {
        LogsManager.info("Cleaning up token manager to prevent memory leaks...");
        TokenManager.clearToken();
        com.grocerystore.utils.AllureUtilities.writeEnvironmentProperties();
    }
}

