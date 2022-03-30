package com.klye.ime.latin;

import android.os.Bundle;

public class IEBG extends IE {
    public void onCreate(Bundle savedInstanceState) {
        if (M.dicB == null) {
            M.msg(this, "Bigram isn't enabled");
            finish();
        } else {
            this.s = M.dicB.exp();
        }
        super.onCreate(savedInstanceState);
    }

    public void run() {
        M.dicB.imp(this.s);
    }
}
