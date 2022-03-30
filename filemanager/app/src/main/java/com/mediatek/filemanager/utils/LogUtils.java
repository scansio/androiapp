package com.mediatek.filemanager.utils;

import android.util.Log;

public final class LogUtils {
    private static final String MODULE_NAME = "FileManager";
    private static final String MSG_HEAD = "[Performance Test][FileManager]";
    private static final boolean XLOG_ENABLED = false;

    public static void e(String tag, String msg) {
        Log.e(MODULE_NAME, tag + ", " + msg);
    }

    public static void e(String tag, String msg, Throwable t) {
        Log.e(MODULE_NAME, tag + ", " + msg, t);
    }

    public static void w(String tag, String msg) {
        Log.w(MODULE_NAME, tag + ", " + msg);
    }

    public static void w(String tag, String msg, Throwable t) {
        Log.w(MODULE_NAME, tag + ", " + msg, t);
    }

    public static void i(String tag, String msg) {
        Log.i(MODULE_NAME, tag + ", " + msg);
    }

    public static void i(String tag, String msg, Throwable t) {
        Log.i(MODULE_NAME, tag + ", " + msg, t);
    }

    public static void d(String tag, String msg) {
        Log.d(MODULE_NAME, tag + ", " + msg);
    }

    public static void performance(String msg) {
        Log.i(MODULE_NAME, MSG_HEAD + msg);
    }

    public static void d(String tag, String msg, Throwable t) {
        Log.d(MODULE_NAME, tag + ", " + msg, t);
    }

    public static void v(String tag, String msg) {
        Log.v(MODULE_NAME, tag + ", " + msg);
    }

    public static void v(String tag, String msg, Throwable t) {
        Log.v(MODULE_NAME, tag + ", " + msg, t);
    }
}
