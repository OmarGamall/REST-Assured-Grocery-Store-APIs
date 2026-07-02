package com.grocerystore.listeners;

import com.grocerystore.utils.LogsManager;
import org.slf4j.MDC;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

public class TestLogListener implements ITestListener {

    @Override
    public void onStart(ITestContext context) {
        LogsManager.info("=========================================");
        LogsManager.info("SUITE EXECUTION STARTED: {}", context.getName());
        LogsManager.info("=========================================");
    }

    @Override
    public void onFinish(ITestContext context) {
        LogsManager.info("=========================================");
        LogsManager.info("SUITE EXECUTION FINISHED: {}", context.getName());
        LogsManager.info("=========================================");
    }

    @Override
    public void onTestStart(ITestResult result) {
        MDC.put("testName", result.getName());
        LogsManager.info(">>> Test Started: {}.{}", result.getInstanceName(), result.getName());
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        LogsManager.info("<<< Test Passed: {}.{}", result.getInstanceName(), result.getName());
        MDC.remove("testName");
    }

    @Override
    public void onTestFailure(ITestResult result) {
        LogsManager.error("<<< Test Failed: {}.{}", result.getInstanceName(), result.getName());
        if (result.getThrowable() != null) {
            LogsManager.error("Failure Exception Details: ", result.getThrowable());
        }
        MDC.remove("testName");
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        LogsManager.warn("<<< Test Skipped: {}.{}", result.getInstanceName(), result.getName());
        MDC.remove("testName");
    }
}

