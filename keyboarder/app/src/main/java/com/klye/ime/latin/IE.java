package com.klye.ime.latin;

import android.app.Activity;
import android.os.Bundle;
import android.text.ClipboardManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

public abstract class IE extends Activity implements OnClickListener, Runnable {
    protected TextView et;
    protected String s;
    protected Thread t;

    protected boolean ct() {
        if (this.t == null || !this.t.isAlive()) {
            return false;
        }
        M.msg(this, "Please wait...");
        return true;
    }

    public void onPause() {
        if (ct()) {
            try {
                this.t.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        super.onPause();
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View v = getLayoutInflater().inflate(R.layout.ie, null);
        setContentView(v);
        this.et = (TextView) findViewById(R.id.ebox);
        this.et.setText(this.s);
        v.findViewById(R.id.copy).setOnClickListener(this);
        v.findViewById(R.id.paste).setOnClickListener(this);
        v.findViewById(R.id.save).setOnClickListener(this);
    }

    public void onClick(View v) {
        if (M.cm(this) && !ct()) {
            ClipboardManager cm = (ClipboardManager) getSystemService("clipboard");
            if (cm == null) {
                M.msg(this, "Error: Can't open clipboard service");
                return;
            }
            switch (v.getId()) {
                case R.id.copy:
                    cm.setText(this.s);
                    M.msg(this, "Copied");
                    return;
                case R.id.paste:
                    CharSequence s1 = cm.getText();
                    if (s1 != null) {
                        this.s = s1.toString();
                        this.et.setText(this.s);
                        M.msg(this, "Pasted");
                        return;
                    }
                    return;
                case R.id.save:
                    if (!(this instanceof IESet)) {
                        M.msg(this, "Depends on data size, it could take a long time...\nPlease wait...");
                    }
                    onSave();
                    return;
                default:
                    return;
            }
        }
    }

    protected void onSave() {
        this.t = new Thread(this);
        this.t.run();
    }

    public void run() {
    }
}
