package com.mediatek.filemanager.utils;

import android.os.SystemProperties;

public final class OptionsUtils {
    private static final String TAG = "OptionsManager";

    public static boolean isMtkDrmApp() {
        return true;
    }

    public static boolean isMtkBeamSurpported() {
        return false;
    }

    public static boolean isMtkSDSwapSurpported() {
        return true;
    }

    public static boolean isMtkHotKnotSupported() {
        return false;
    }

    public static boolean isOP01Surported() {
        return "OP01".equals(SystemProperties.get("ro.operator.optr"));
    }
}
