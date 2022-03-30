package com.mediatek.filemanager.utils;

import android.widget.TextView;

public class LongStringUtils {
    private static final String TAG = LongStringUtils.class.getSimpleName();

    public static void fadeOutLongString(TextView textView) {
        if (textView == null) {
            LogUtils.w(TAG, "#adjustWithLongString(),the view is to be set is null");
        } else if (textView instanceof TextView) {
            textView.setHorizontalFadingEdgeEnabled(true);
            textView.setSingleLine(true);
            textView.setGravity(3);
            textView.setGravity(16);
        } else {
            LogUtils.w(TAG, "#adjustWithLongString(),the view instance is not right,execute failed!");
        }
    }
}
