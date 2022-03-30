package com.androi.development;

import android.app.Activity;
import android.content.ComponentName;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

public class ShowActivity extends Activity {
    private ActivityInfo mActivityInfo;
    private TextView mClass;
    private TextView mClearOnBackground;
    private ImageView mIconImage;
    private TextView mLabel;
    private TextView mLaunch;
    private TextView mMultiprocess;
    private TextView mPackage;
    private TextView mPermission;
    private TextView mProcess;
    private TextView mStateNotNeeded;
    private TextView mTaskAffinity;

    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setContentView(R.layout.show_activity);
        this.mPackage = (TextView) findViewById(R.id.packageView);
        this.mIconImage = (ImageView) findViewById(R.id.icon);
        this.mClass = (TextView) findViewById(R.id.classView);
        this.mLabel = (TextView) findViewById(R.id.label);
        this.mLaunch = (TextView) findViewById(R.id.launch);
        this.mProcess = (TextView) findViewById(R.id.process);
        this.mTaskAffinity = (TextView) findViewById(R.id.taskAffinity);
        this.mPermission = (TextView) findViewById(R.id.permission);
        this.mMultiprocess = (TextView) findViewById(R.id.multiprocess);
        this.mClearOnBackground = (TextView) findViewById(R.id.clearOnBackground);
        this.mStateNotNeeded = (TextView) findViewById(R.id.stateNotNeeded);
        PackageManager pm = getPackageManager();
        try {
            this.mActivityInfo = pm.getActivityInfo(ComponentName.unflattenFromString(getIntent().getData().getSchemeSpecificPart()), 0);
        } catch (NameNotFoundException e) {
        }
        if (this.mActivityInfo != null) {
            this.mPackage.setText(this.mActivityInfo.applicationInfo.packageName);
            this.mIconImage.setImageDrawable(this.mActivityInfo.loadIcon(pm));
            if (this.mActivityInfo.name.startsWith(this.mActivityInfo.applicationInfo.packageName + ".")) {
                this.mClass.setText(this.mActivityInfo.name.substring(this.mActivityInfo.applicationInfo.packageName.length()));
            } else {
                this.mClass.setText(this.mActivityInfo.name);
            }
            CharSequence label = this.mActivityInfo.loadLabel(pm);
            TextView textView = this.mLabel;
            StringBuilder append = new StringBuilder().append("\"");
            if (label == null) {
                label = "";
            }
            textView.setText(append.append(label).append("\"").toString());
            switch (this.mActivityInfo.launchMode) {
                case 0:
                    this.mLaunch.setText(getText(R.string.launch_multiple));
                    break;
                case 1:
                    this.mLaunch.setText(getText(R.string.launch_singleTop));
                    break;
                case 2:
                    this.mLaunch.setText(getText(R.string.launch_singleTask));
                    break;
                case 3:
                    this.mLaunch.setText(getText(R.string.launch_singleInstance));
                    break;
                default:
                    this.mLaunch.setText(getText(R.string.launch_unknown));
                    break;
            }
            this.mProcess.setText(this.mActivityInfo.processName);
            this.mTaskAffinity.setText(this.mActivityInfo.taskAffinity != null ? this.mActivityInfo.taskAffinity : getText(R.string.none));
            this.mPermission.setText(this.mActivityInfo.permission != null ? this.mActivityInfo.permission : getText(R.string.none));
            this.mMultiprocess.setText((this.mActivityInfo.flags & 1) != 0 ? getText(R.string.yes) : getText(R.string.no));
            this.mClearOnBackground.setText((this.mActivityInfo.flags & 4) != 0 ? getText(R.string.yes) : getText(R.string.no));
            this.mStateNotNeeded.setText((this.mActivityInfo.flags & 16) != 0 ? getText(R.string.yes) : getText(R.string.no));
        }
    }

    public void onResume() {
        super.onResume();
    }
}
