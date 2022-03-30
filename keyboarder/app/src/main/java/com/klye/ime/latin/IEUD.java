package com.klye.ime.latin;

import android.os.Bundle;

public class IEUD extends IE {
    public void onCreate(Bundle savedInstanceState) {
        if (M.dicU == null) {
            M.msg(this, "User dictionary isn't enabled");
            finish();
        } else {
            this.s = M.dicU.exp();
        }
        super.onCreate(savedInstanceState);
    }

    public void run() {
        M.dicU.imp(this.s);
    }
}
