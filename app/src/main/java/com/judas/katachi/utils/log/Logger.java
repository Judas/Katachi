package com.judas.katachi.utils.log;

import android.util.Log;

import com.judas.katachi.BuildConfig;

public final class Logger {
    public enum Level {
        // Order by importance (needed for logger filter)
        VERBOSE, DEBUG, INFO, WARNING, ERROR
    }

    private static String PREFIX = "";
    private static final Level LEVEL = BuildConfig.DEBUG ? Level.VERBOSE : Level.WARNING;

    private Logger() {
    }

    public static void setAppName(final String appName) {
        PREFIX = "[" + appName + "] ";
    }

    public static void log(final Level level, final String tag, final String msg) {
        final String message = PREFIX + msg;

        if (level.ordinal() >= LEVEL.ordinal()) {
            switch (level) {
                case VERBOSE:
                    Log.v(tag, message);
                    break;
                case DEBUG:
                    Log.d(tag, message);
                    break;
                case INFO:
                    Log.i(tag, message);
                    break;
                case WARNING:
                    Log.w(tag, message);
                    break;
                case ERROR:
                    Log.e(tag, message);
                    break;
                default:
                    break;
            }
        }
    }

    public static void logWithTrace(final Level level, final String tag, final String msg) {
        if (level.ordinal() >= LEVEL.ordinal()) {

            final String log = msg + Log.getStackTraceString(new Exception("logWithTrace"));

            switch (level) {
                case VERBOSE:
                    Log.v(tag, log);
                    break;
                case DEBUG:
                    Log.d(tag, log);
                    break;
                case INFO:
                    Log.i(tag, log);
                    break;
                case WARNING:
                    Log.w(tag, log);
                    break;
                case ERROR:
                    Log.e(tag, log);
                    break;
                default:
                    break;
            }
        }
    }

    public static void logError(final String tag, final String target, final Throwable error) {
        log(Level.ERROR, tag, target + " ERROR: " + error.getMessage());
    }
}
