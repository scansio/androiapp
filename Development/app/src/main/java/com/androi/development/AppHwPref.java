package com.androi.development;

import android.app.Activity;
import android.content.pm.ConfigurationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import java.util.HashSet;
import java.util.Iterator;

public class AppHwPref extends Activity {
    PackageManager mPm;

    protected void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        String pkgName = getIntent().getStringExtra("packageName");
        if (pkgName == null) {
            handleError("Null package name", true);
            return;
        }
        PackageInfo pInfo;
        this.mPm = getPackageManager();
        try {
            pInfo = this.mPm.getPackageInfo(pkgName, 16384);
        } catch (NameNotFoundException e) {
            pInfo = null;
        }
        if (pInfo == null) {
            handleError("Failed retrieving packageInfo for pkg:" + pkgName, true);
            return;
        }
        ConfigurationInfo[] appHwPref = pInfo.configPreferences;
        setContentView(R.layout.application_hw_pref);
        if (appHwPref != null) {
            displayTextView(R.id.attr_package, pInfo.applicationInfo.loadLabel(this.mPm));
            displayTextView(R.id.attr_touchscreen, appHwPref, 1);
            displayTextView(R.id.attr_input_method, appHwPref, 2);
            displayTextView(R.id.attr_navigation, appHwPref, 3);
            displayFlag(R.id.attr_hard_keyboard, 1, appHwPref);
            displayFlag(R.id.attr_five_way_nav, 2, appHwPref);
            displayTextView(R.id.attr_gles_version, appHwPref, 4);
        }
    }

    void displayFlag(int viewId, int flagMask, ConfigurationInfo[] appHwPref) {
        if (appHwPref != null) {
            boolean flag = false;
            for (ConfigurationInfo pref : appHwPref) {
                if ((pref.reqInputFeatures & flagMask) != 0) {
                    flag = true;
                    break;
                }
            }
            if (flag) {
                displayTextView(viewId, "true");
            } else {
                displayTextView(viewId, "false");
            }
        }
    }

    void handleError(String errMsg, boolean finish) {
        Log.i("AppHwPref", errMsg);
        if (finish) {
            finish();
        }
    }

    void displayTextView(int textViewId, CharSequence displayStr) {
        TextView tView = (TextView) findViewById(textViewId);
        if (displayStr != null) {
            tView.setText(displayStr);
        }
    }

    void displayTextView(int viewId, ConfigurationInfo[] config, int type) {
        if (config != null && config.length >= 1) {
            HashSet<String> list = new HashSet();
            for (int i = 0; i < config.length; i++) {
                String str = null;
                switch (type) {
                    case 1:
                        str = getTouchScreenStr(config[i]);
                        break;
                    case 2:
                        str = getKeyboardTypeStr(config[i]);
                        break;
                    case 3:
                        str = getNavigationStr(config[i]);
                        break;
                    case 4:
                        str = config[i].getGlEsVersion();
                        break;
                }
                if (str != null) {
                    list.add(str);
                }
            }
            String listStr = "";
            boolean set = false;
            Iterator i$ = list.iterator();
            while (i$.hasNext()) {
                set = true;
                listStr = listStr + ((String) i$.next()) + ",";
            }
            if (set) {
                ((TextView) findViewById(viewId)).setText(listStr.subSequence(0, listStr.length() - 1));
            }
        }
    }

    String getTouchScreenStr(ConfigurationInfo appHwPref) {
        if (appHwPref == null) {
            handleError("Invalid HardwareConfigurationObject", true);
            return null;
        }
        switch (appHwPref.reqTouchScreen) {
            case 1:
                return "notouch";
            case 2:
                return "stylus";
            case 3:
                return "finger";
            default:
                return null;
        }
    }

    String getKeyboardTypeStr(ConfigurationInfo appHwPref) {
        if (appHwPref == null) {
            handleError("Invalid HardwareConfigurationObject", true);
            return null;
        }
        switch (appHwPref.reqKeyboardType) {
            case 1:
                return "nokeys";
            case 2:
                return "querty";
            case 3:
                return "12key";
            default:
                return null;
        }
    }

    String getNavigationStr(ConfigurationInfo appHwPref) {
        if (appHwPref == null) {
            handleError("Invalid HardwareConfigurationObject", true);
            return null;
        }
        switch (appHwPref.reqNavigation) {
            case 2:
                return "dpad";
            case 3:
                return "trackball";
            case 4:
                return "wheel";
            default:
                return null;
        }
    }

    protected void onResume() {
        super.onResume();
    }

    protected void onStop() {
        super.onStop();
    }
}
