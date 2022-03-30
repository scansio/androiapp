package com.klye.ime.latin;

import android.os.Bundle;

public class IEAT extends IE {
    private static final String d1 = "__";
    private static final String d2 = "::";

    public void onCreate(Bundle savedInstanceState) {
        if (M.emjCT("klye.usertext") == null) {
            String s = getString(R.string.utextdl);
            M.noti(this, s, M.hp("dlat.html"));
            M.msg(this, s);
            finish();
        } else {
            this.s = M.dicUt.exp();
        }
        super.onCreate(savedInstanceState);
    }

    public void run() {
        M.dicUt.imp(this.s, getBaseContext());
    }
}
