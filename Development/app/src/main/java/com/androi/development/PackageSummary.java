package com.androi.development;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.InstrumentationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ProviderInfo;
import android.content.pm.ServiceInfo;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

public class PackageSummary extends Activity {
    private TextView mClass;
    private TextView mData;
    private View mDebuggable;
    private View mDisabled;
    private ImageView mIconImage;
    private TextView mLabel;
    private View mNoCode;
    private TextView mPackage;
    String mPackageName;
    private View mPersistent;
    private TextView mProcess;
    private Button mRestart;
    private TextView mSource;
    private View mSystem;
    private TextView mTask;
    private TextView mUid;
    private TextView mVersion;

    private final class ActivityOnClick implements OnClickListener {
        private final ComponentName mClassName;

        ActivityOnClick(ComponentName className) {
            this.mClassName = className;
        }

        public void onClick(View v) {
            Intent intent = new Intent(null, Uri.fromParts("component", this.mClassName.flattenToString(), null));
            intent.setClass(PackageSummary.this, ShowActivity.class);
            PackageSummary.this.startActivity(intent);
        }
    }

    protected void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setContentView(R.layout.package_summary);
        PackageManager pm = getPackageManager();
        this.mPackage = (TextView) findViewById(R.id.packageView);
        this.mIconImage = (ImageView) findViewById(R.id.icon);
        this.mClass = (TextView) findViewById(R.id.classView);
        this.mLabel = (TextView) findViewById(R.id.label);
        this.mDisabled = findViewById(R.id.disabled);
        this.mSystem = findViewById(R.id.system);
        this.mDebuggable = findViewById(R.id.debuggable);
        this.mNoCode = findViewById(R.id.nocode);
        this.mPersistent = findViewById(R.id.persistent);
        this.mRestart = (Button) findViewById(R.id.restart);
        this.mTask = (TextView) findViewById(R.id.task);
        this.mVersion = (TextView) findViewById(R.id.version);
        this.mUid = (TextView) findViewById(R.id.uid);
        this.mProcess = (TextView) findViewById(R.id.process);
        this.mSource = (TextView) findViewById(R.id.source);
        this.mData = (TextView) findViewById(R.id.data);
        this.mPackageName = getIntent().getData().getSchemeSpecificPart();
        PackageInfo info = null;
        try {
            info = pm.getPackageInfo(this.mPackageName, 543);
        } catch (NameNotFoundException e) {
        }
        if (info != null) {
            int i;
            int N;
            ActivityInfo ai;
            View view;
            this.mPackage.setText(info.packageName);
            CharSequence label = null;
            String appClass = null;
            if (info.applicationInfo != null) {
                this.mIconImage.setImageDrawable(pm.getApplicationIcon(info.applicationInfo));
                label = info.applicationInfo.nonLocalizedLabel;
                appClass = info.applicationInfo.className;
                if (info.applicationInfo.enabled) {
                    this.mDisabled.setVisibility(8);
                }
                if ((info.applicationInfo.flags & 1) == 0) {
                    this.mSystem.setVisibility(8);
                }
                if ((info.applicationInfo.flags & 2) == 0) {
                    this.mDebuggable.setVisibility(8);
                }
                if ((info.applicationInfo.flags & 4) != 0) {
                    this.mNoCode.setVisibility(8);
                }
                if ((info.applicationInfo.flags & 8) == 0) {
                    this.mPersistent.setVisibility(8);
                }
                this.mUid.setText(Integer.toString(info.applicationInfo.uid));
                this.mProcess.setText(info.applicationInfo.processName);
                if (info.versionName != null) {
                    this.mVersion.setText(info.versionName + " (#" + info.versionCode + ")");
                } else {
                    this.mVersion.setText("(#" + info.versionCode + ")");
                }
                this.mSource.setText(info.applicationInfo.sourceDir);
                this.mData.setText(info.applicationInfo.dataDir);
                if (info.applicationInfo.taskAffinity != null) {
                    this.mTask.setText("\"" + info.applicationInfo.taskAffinity + "\"");
                } else {
                    this.mTask.setText("(No Task Affinity)");
                }
            }
            if (appClass == null) {
                this.mClass.setText("(No Application Class)");
            } else if (appClass.startsWith(info.packageName + ".")) {
                this.mClass.setText(appClass.substring(info.packageName.length()));
            } else {
                this.mClass.setText(appClass);
            }
            if (label != null) {
                this.mLabel.setText("\"" + label + "\"");
            } else {
                this.mLabel.setText("(No Label)");
            }
            this.mRestart.setOnClickListener(new OnClickListener() {
                public void onClick(View v) {
                    ((ActivityManager) PackageSummary.this.getSystemService("activity")).killBackgroundProcesses(PackageSummary.this.mPackageName);
                }
            });
            LayoutInflater inflate = (LayoutInflater) getSystemService("layout_inflater");
            LayoutParams lp = new LayoutParams(-2, -2);
            LinearLayout activities = (LinearLayout) findViewById(R.id.activities);
            LinearLayout receivers = (LinearLayout) findViewById(R.id.receivers);
            LinearLayout services = (LinearLayout) findViewById(R.id.services);
            LinearLayout providers = (LinearLayout) findViewById(R.id.providers);
            LinearLayout instrumentation = (LinearLayout) findViewById(R.id.instrumentation);
            if (info.activities != null) {
                for (ActivityInfo ai2 : info.activities) {
                    if (ai2 != null) {
                        view = (Button) inflate.inflate(R.layout.package_item, null, false);
                        view.setOnClickListener(new ActivityOnClick(new ComponentName(ai2.applicationInfo.packageName, ai2.name)));
                        setItemText(view, info, ai2.name);
                        activities.addView(view, lp);
                    }
                }
            } else {
                activities.setVisibility(8);
            }
            if (info.receivers != null) {
                N = info.receivers.length;
                for (i = 0; i < N; i++) {
                    ai2 = info.receivers[i];
                    view = (Button) inflate.inflate(R.layout.package_item, null, false);
                    Log.i("foo", "Receiver #" + i + " of " + N + ": " + ai2);
                    setItemText(view, info, ai2.name);
                    receivers.addView(view, lp);
                }
            } else {
                receivers.setVisibility(8);
            }
            if (info.services != null) {
                for (ServiceInfo si : info.services) {
                    view = (Button) inflate.inflate(R.layout.package_item, null, false);
                    setItemText(view, info, si.name);
                    services.addView(view, lp);
                }
            } else {
                services.setVisibility(8);
            }
            if (info.providers != null) {
                for (ProviderInfo pi : info.providers) {
                    view = (Button) inflate.inflate(R.layout.package_item, null, false);
                    setItemText(view, info, pi.name);
                    providers.addView(view, lp);
                }
            } else {
                providers.setVisibility(8);
            }
            if (info.instrumentation != null) {
                for (InstrumentationInfo ii : info.instrumentation) {
                    view = (Button) inflate.inflate(R.layout.package_item, null, false);
                    setItemText(view, info, ii.name);
                    instrumentation.addView(view, lp);
                }
            } else {
                instrumentation.setVisibility(8);
            }
        }
        this.mPackage.requestFocus();
    }

    private static final void setItemText(Button item, PackageInfo pi, String className) {
        item.setText(className.substring(className.lastIndexOf(46) + 1));
    }
}
