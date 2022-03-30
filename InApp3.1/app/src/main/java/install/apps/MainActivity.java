package install.apps;

import android.app.Activity;
// import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
// import android.content.pm.ResolveInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.io.Serializable;
// import java.util.List;

public class MainActivity extends Activity implements Serializable {
    private static final long serialVersionUID = 98987876L;
    public appDetail apps;
    public EditText sbar;
    public LinearLayout parentLayout;
    public HorizontalScrollView searchLayout;
    //public ChangeReceiver changeReceiver;
    public PackageManager packageManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(1);
        setContentView(R.layout.activity_main);
        packageManager = getPackageManager();
        packageManager.setComponentEnabledSetting(
                getComponentName(),
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                PackageManager.DONT_KILL_APP);
        init();
        /*IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.intent.action.PACKAGE_INSTALL");
        intentFilter.addAction("android.intent.action.PACKAGE_REMOVED");
        intentFilter.addAction("android.intent.action.PACKAGE_CHANGED");
        changeReceiver = new ChangeReceiver();
        registerReceiver(changeReceiver, intentFilter);

                Intent intent = new Intent(Intent.ACTION_MAIN, null);
                intent.addCategory("android.intent.category.HOME");
                //intent.addCategory("android.intent.category.LAUNCHER");
                intent.addCategory("android.intent.category.DEFAULT");
                List<ResolveInfo> homeApps = packageManager.queryIntentActivities(intent, 0);
                apps.toast(homeApps);
        */

    }

    public void match(final String t) {
        try {
            new Thread(
                            new Runnable() {
                                public void run() {
                                    int i = 0;
                                    for (String t1 : apps.getLabels()) {
                                        if (t1.toLowerCase().contains(t.toLowerCase())) {
                                            apps.addResult(t1, i == 0);
                                            i++;
                                        }
                                    }
                                }
                            })
                    .start();
        } catch (Exception e) {
            Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    public void onBackPressed() {}

    private void init() {
        try {
            parentLayout = findViewById(R.id.ly);

            searchLayout = findViewById(R.id.searchLayout);
            sbar = findViewById(R.id.sbar);
            sbar.setVisibility(View.INVISIBLE);
            final View[] v = {parentLayout, searchLayout, sbar};

            new Thread(
                            new Runnable() {
                                public void run() {
                                    apps = new appDetail(getApplicationContext(), v);
                                    appDetail.setInstance(apps);
                                }
                            })
                    .start();
            sbar.addTextChangedListener(
                    new TextWatcher() {
                        public void onTextChanged(
                                CharSequence s, int start, int before, int count) {}

                        public void beforeTextChanged(
                                CharSequence s, int start, int count, int after) {}

                        public void afterTextChanged(Editable s) {
                            String element = s.toString();
                            if (!element.equals("")) match(element);
                        }
                    });

        } catch (Exception e) {
            Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle outState) {
        super.onRestoreInstanceState(outState);
    }

    @Override
    protected void onPause() {
        super.onPause();
        
    }

    @Override
    protected void onStart() {
        super.onStart();
        
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onConfigurationChanged(Configuration config) {
        super.onConfigurationChanged(config);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        Toast.makeText(this, "onConfigurationChange", Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
      //  unregisterReceiver(changeReceiver);
    }
}
