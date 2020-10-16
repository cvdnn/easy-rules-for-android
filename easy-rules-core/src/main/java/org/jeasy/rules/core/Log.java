package org.jeasy.rules.core;

import android.text.TextUtils;

public final class Log {
    private static String TAG = "_LOG";

    public static void v(String msg, Object... args) {
        v(TAG, msg, args);
    }

    public static void v(Throwable t) {
        v(TAG, t);
    }

    public static void v(String tag, Throwable t) {
        v(tag, getMessage(t));
    }

    public static void v(String tag, String msg, Object... args) {
        println(android.util.Log.VERBOSE, tag, msg, args);
    }

    public static void d(String msg, Object... args) {
        d(TAG, msg, args);
    }

    public static void d(Throwable t) {
        d(TAG, t);
    }

    public static void d(String tag, Throwable t) {
        d(tag, getMessage(t));
    }

    public static void d(String tag, String msg, Object... args) {
        println(android.util.Log.DEBUG, tag, msg, args);
    }

    public static void i(String msg, Object... args) {
        i(TAG, msg, args);
    }

    public static void i(Throwable t) {
        i(TAG, t);
    }

    public static void i(String tag, Throwable t) {
        i(tag, getMessage(t));
    }

    public static void i(String tag, String msg, Object... args) {
        println(android.util.Log.INFO, tag, msg, args);
    }

    public static void w(String msg, Object... args) {
        w(TAG, msg, args);
    }

    public static void w(Throwable t) {
        w(TAG, t);
    }

    public static void w(String tag, Throwable t) {
        w(tag, getMessage(t));
    }

    public static void w(String tag, String msg, Object... args) {

        println(android.util.Log.WARN, tag, msg, args);
    }

    public static void e(String msg, Object... args) {
        e(TAG, msg, args);
    }

    public static void e(Throwable t) {
        e(TAG, t);
    }

    public static void e(String tag, Throwable t) {
        e(tag, getMessage(t));
    }

    public static void e(String tag, String msg, Object... args) {
        println(android.util.Log.ERROR, tag, msg, args);
    }

    public static void println(int priority, String tag, String msg, Object... args) {
        String logMsg = msg;

        long timeMillis = System.currentTimeMillis();

        if (!TextUtils.isEmpty(msg) && msg.indexOf('%') >= 0 && args != null) {
            try {
                logMsg = String.format(msg, args);
            } catch (Exception e) {
                android.util.Log.d(TAG, msg);
                android.util.Log.v(TAG, "String format error", e);
            }
        }

        String text = logMsg == null ? "" : logMsg;

        android.util.Log.println(priority, tag, text);
    }

    public static String getMessage(Throwable t) {

        return t != null ? t.getMessage() + "\n" + android.util.Log.getStackTraceString(t) : "";
    }
}
