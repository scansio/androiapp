package com.androi.development;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ProcessInfo extends Activity {
    PackageManager mPm;

    protected void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        Intent intent = getIntent();
        String processName = intent.getStringExtra("processName");
        String[] pkgList = intent.getStringArrayExtra("packageList");
        this.mPm = getPackageManager();
        setContentView(R.layout.process_info);
        TextView processNameView = (TextView) findViewById(R.id.process_name);
        LinearLayout pkgListView = (LinearLayout) findViewById(R.id.package_list);
        if (processName != null) {
            processNameView.setText(processName);
        }
        if (pkgList != null) {
            for (String pkg : pkgList) {
                TextView pkgView = new TextView(this);
                pkgView.setText(pkg);
                pkgListView.addView(pkgView);
            }
        }
    }

    protected void onResume() {
        super.onResume();
    }

    protected void onStop() {
        super.onStop();
    }
}
