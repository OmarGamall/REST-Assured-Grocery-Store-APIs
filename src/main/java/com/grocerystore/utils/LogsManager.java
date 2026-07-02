package com.grocerystore.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LogsManager {

    // Dynamically retrieve the Logger for the calling class
    private static Logger getLogger() {
        String className = Thread.currentThread().getStackTrace()[3].getClassName();
        return LoggerFactory.getLogger(className);
    }

    public static void info(String message) {
        getLogger().info(message);
    }

    public static void info(String message, Object... args) {
        getLogger().info(message, args);
    }

    public static void warn(String message) {
        getLogger().warn(message);
    }

    public static void warn(String message, Object... args) {
        getLogger().warn(message, args);
    }

    public static void error(String message) {
        getLogger().error(message);
    }

    public static void error(String message, Object... args) {
        getLogger().error(message, args);
    }

    public static void error(String message, Throwable t) {
        getLogger().error(message, t);
    }

    public static void debug(String message) {
        getLogger().debug(message);
    }

    public static void debug(String message, Object... args) {
        getLogger().debug(message, args);
    }

    public static void trace(String message) {
        getLogger().trace(message);
    }

    public static void trace(String message, Object... args) {
        getLogger().trace(message, args);
    }
}
