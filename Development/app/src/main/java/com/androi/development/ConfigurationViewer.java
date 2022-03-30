package com.androi.development;

import android.app.Activity;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.widget.TextView;

public class ConfigurationViewer extends Activity {
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setContentView(R.layout.configuration_viewer);
        Configuration c = getResources().getConfiguration();
        DisplayMetrics m = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(m);
        TextView tv = (TextView) findViewById(R.id.text);
        String s = "Configuration\n\nfontScale=" + c.fontScale + "\n" + "hardKeyboardHidden=" + c.hardKeyboardHidden + "\n" + "keyboard=" + c.keyboard + "\n" + "locale=" + c.locale + "\n" + "mcc=" + c.mcc + "\n" + "mnc=" + c.mnc + "\n" + "navigation=" + c.navigation + "\n" + "navigationHidden=" + c.navigationHidden + "\n" + "orientation=" + c.orientation + "\n" + "screenLayout=0x" + Integer.toHexString(c.screenLayout) + "\n" + "touchscreen=" + c.touchscreen + "\n" + "uiMode=0x" + Integer.toHexString(c.uiMode) + "\n" + "\n" + "DisplayMetrics\n" + "\n" + "density=" + m.density + "\n" + "densityDpi=" + m.densityDpi + "\n" + "heightPixels=" + m.heightPixels + "\n" + "scaledDensity=" + m.scaledDensity + "\n" + "widthPixels=" + m.widthPixels + "\n" + "xdpi=" + m.xdpi + "\n" + "ydpi=" + m.ydpi + "\n";
        tv.setText(s);
        Log.d("ConfigurationViewer", s);
    }
}
