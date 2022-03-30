package com.mediatek.filemanager.utils;

import android.content.Context;
import android.widget.Toast;

public class ToastHelper {
    private final Context mContext;
    private Toast mToast = null;

    public ToastHelper(Context context) {
        if (context == null) {
            throw new IllegalArgumentException();
        }
        this.mContext = context;
    }

    public void showToast(String text) {
        if (this.mToast == null) {
            this.mToast = Toast.makeText(this.mContext, text, 0);
        } else {
            this.mToast.setText(text);
        }
        this.mToast.show();
    }

    public void showToast(int resId) {
        if (this.mToast == null) {
            this.mToast = Toast.makeText(this.mContext, resId, 0);
        } else {
            this.mToast.setText(resId);
        }
        this.mToast.show();
    }
}
