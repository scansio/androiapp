package com.androi.development;

import android.app.ActivityManagerNative;
import android.app.IInstrumentationWatcher;
import android.app.IInstrumentationWatcher.Stub;
import android.app.ListActivity;
import android.content.ComponentName;
import android.os.Bundle;
import android.os.RemoteException;
import android.os.UserHandle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MenuItem.OnMenuItemClickListener;
import android.view.View;
import android.widget.ListView;

public class InstrumentationList extends ListActivity {
    private OnMenuItemClickListener mProfilingCallback = new OnMenuItemClickListener() {
        public boolean onMenuItemClick(MenuItem item) {
            InstrumentationList.this.mProfilingMode = !InstrumentationList.this.mProfilingMode;
            return true;
        }
    };
    private MenuItem mProfilingItem;
    private boolean mProfilingMode;
    private IInstrumentationWatcher mWatcher = new Stub() {
        public void instrumentationStatus(ComponentName name, int resultCode, Bundle results) {
            if (results != null) {
                for (String key : results.keySet()) {
                    Log.i("instrumentation", "INSTRUMENTATION_STATUS_RESULT: " + key + "=" + results.get(key));
                }
            }
            Log.i("instrumentation", "INSTRUMENTATION_STATUS_CODE: " + resultCode);
        }

        public void instrumentationFinished(ComponentName name, int resultCode, Bundle results) {
            if (results != null) {
                for (String key : results.keySet()) {
                    Log.i("instrumentation", "INSTRUMENTATION_RESULT: " + key + "=" + results.get(key));
                }
            }
            Log.i("instrumentation", "INSTRUMENTATION_CODE: " + resultCode);
        }
    };

    protected void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        boolean z = icicle != null && icicle.containsKey("profiling");
        this.mProfilingMode = z;
        setListAdapter(new InstrumentationAdapter(this, null));
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        this.mProfilingItem = menu.add(0, 0, 0, "Profiling Mode").setOnMenuItemClickListener(this.mProfilingCallback);
        this.mProfilingItem.setCheckable(true);
        return true;
    }

    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        this.mProfilingItem.setChecked(this.mProfilingMode);
        return true;
    }

    protected void onSaveInstanceState(Bundle outState) {
        if (this.mProfilingMode) {
            outState.putBoolean("profiling", true);
        }
    }

    protected void onListItemClick(ListView l, View v, int position, long id) {
        ComponentName className = ((InstrumentationAdapter) getListAdapter()).instrumentationForPosition(position);
        if (className != null) {
            String profilingFile = null;
            if (this.mProfilingMode) {
                profilingFile = "/tmp/trace/" + className + ".dmtrace";
            }
            try {
                ActivityManagerNative.getDefault().startInstrumentation(className, profilingFile, 0, null, this.mWatcher, null, UserHandle.myUserId());
            } catch (RemoteException e) {
            }
        }
    }
}
