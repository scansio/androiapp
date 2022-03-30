package com.klye.ime.latin;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public class T extends Activity {
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent in = getIntent();
        if ("android.intent.action.SEND".equals(in.getAction())) {
            String sk = "android.intent.extra.TEXT";
            String s = in.getStringExtra("android.intent.extra.TEXT");
            if (s != null && s.length() > 1) {
                M.trans(this, s);
            }
        }
        finish();
    }
}
