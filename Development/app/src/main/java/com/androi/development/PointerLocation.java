package com.androi.development;

import android.app.Activity;
import android.os.Bundle;
import android.view.WindowManager.LayoutParams;
import com.android.internal.widget.PointerLocationView;

public class PointerLocation extends Activity {
    protected void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setContentView(new PointerLocationView(this));
        LayoutParams lp = getWindow().getAttributes();
        lp.screenBrightness = 1.0f;
        getWindow().setAttributes(lp);
    }
}
