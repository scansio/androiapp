package com.androi.development;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.ISyncAdapter;
import android.content.ISyncContext.Stub;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SyncAdapterType;
import android.content.SyncResult;
import android.content.pm.RegisteredServicesCache;
import android.content.pm.RegisteredServicesCache.ServiceInfo;
import android.content.pm.RegisteredServicesCacheListener;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.os.UserHandle;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import com.android.internal.R;
import java.util.Collection;

public class SyncAdapterDriver extends Activity implements RegisteredServicesCacheListener<SyncAdapterType>, OnItemClickListener {
    private ListView mAccountPickerView = null;
    MyServiceConnection mActiveServiceConnection;
    private Button mBindButton;
    private TextView mBoundAdapterTextView;
    private Button mCancelSyncButton;
    final Object mServiceConnectionLock = new Object();
    private Button mStartSyncButton;
    private TextView mStatusTextView;
    private Spinner mSyncAdapterSpinner;
    private Object[] mSyncAdapters;
    private SyncAdaptersCache mSyncAdaptersCache;
    private final Object mSyncAdaptersLock = new Object();
    private Button mUnbindButton;

    private class MyServiceConnection extends Stub implements ServiceConnection {
        private volatile ISyncAdapter mBoundSyncAdapter;
        final ServiceInfo<SyncAdapterType> mSyncAdapter;

        public MyServiceConnection(ServiceInfo<SyncAdapterType> syncAdapter) {
            this.mSyncAdapter = syncAdapter;
        }

        public void onServiceConnected(ComponentName name, IBinder service) {
            this.mBoundSyncAdapter = ISyncAdapter.Stub.asInterface(service);
            SyncAdapterType type = SyncAdapterDriver.this.mActiveServiceConnection.mSyncAdapter.type;
            SyncAdapterDriver.this.mBoundAdapterTextView.setText(SyncAdapterDriver.this.getString(R.string.binding_connected_format, new Object[]{type.authority, type.accountType}));
            SyncAdapterDriver.this.updateUi();
        }

        public void onServiceDisconnected(ComponentName name) {
            SyncAdapterDriver.this.mBoundAdapterTextView.setText(SyncAdapterDriver.this.getString(R.string.binding_not_connected));
            this.mBoundSyncAdapter = null;
            SyncAdapterDriver.this.updateUi();
        }

        public void sendHeartbeat() {
            SyncAdapterDriver.this.runOnUiThread(new Runnable() {
                public void run() {
                    MyServiceConnection.this.uiThreadSendHeartbeat();
                }
            });
        }

        public void uiThreadSendHeartbeat() {
            SyncAdapterDriver.this.mStatusTextView.setText(SyncAdapterDriver.this.getString(R.string.status_received_heartbeat));
        }

        public void uiThreadOnFinished(SyncResult result) {
            if (result.hasError()) {
                SyncAdapterDriver.this.mStatusTextView.setText(SyncAdapterDriver.this.getString(R.string.status_sync_failed_format, new Object[]{result.toString()}));
                return;
            }
            SyncAdapterDriver.this.mStatusTextView.setText(SyncAdapterDriver.this.getString(R.string.status_sync_succeeded_format, new Object[]{result.toString()}));
        }

        public void onFinished(final SyncResult result) throws RemoteException {
            SyncAdapterDriver.this.runOnUiThread(new Runnable() {
                public void run() {
                    MyServiceConnection.this.uiThreadOnFinished(result);
                }
            });
        }
    }

    static class SyncAdaptersCache extends RegisteredServicesCache<SyncAdapterType> {
        SyncAdaptersCache(Context context) {
            super(context, "android.content.SyncAdapter", "android.content.SyncAdapter", "sync-adapter", null);
        }

        public SyncAdapterType parseServiceAttributes(Resources res, String packageName, AttributeSet attrs) {
            TypedArray sa = res.obtainAttributes(attrs, R.styleable.SyncAdapter);
            try {
                String authority = sa.getString(2);
                String accountType = sa.getString(1);
                if (authority == null || accountType == null) {
                    sa.recycle();
                    return null;
                }
                SyncAdapterType syncAdapterType = new SyncAdapterType(authority, accountType, sa.getBoolean(3, true), sa.getBoolean(4, true), sa.getBoolean(6, false), sa.getBoolean(5, false), sa.getString(0));
                sa.recycle();
                return syncAdapterType;
            } catch (Throwable th) {
                sa.recycle();
            }
        }
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.mSyncAdaptersCache = new SyncAdaptersCache(this);
        setContentView(R.layout.sync_adapter_driver);
        this.mSyncAdapterSpinner = (Spinner) findViewById(R.id.sync_adapters_spinner);
        this.mBindButton = (Button) findViewById(R.id.bind_button);
        this.mUnbindButton = (Button) findViewById(R.id.unbind_button);
        this.mBoundAdapterTextView = (TextView) findViewById(R.id.bound_adapter_text_view);
        this.mStartSyncButton = (Button) findViewById(R.id.start_sync_button);
        this.mCancelSyncButton = (Button) findViewById(R.id.cancel_sync_button);
        this.mStatusTextView = (TextView) findViewById(R.id.status_text_view);
        getSyncAdapters();
        this.mSyncAdaptersCache.setListener(this, null);
    }

    private void getSyncAdapters() {
        Collection<ServiceInfo<SyncAdapterType>> all = this.mSyncAdaptersCache.getAllServices(UserHandle.myUserId());
        synchronized (this.mSyncAdaptersLock) {
            this.mSyncAdapters = new Object[all.size()];
            String[] names = new String[this.mSyncAdapters.length];
            int i = 0;
            for (ServiceInfo<SyncAdapterType> item : all) {
                this.mSyncAdapters[i] = item;
                names[i] = ((SyncAdapterType) item.type).authority + " - " + ((SyncAdapterType) item.type).accountType;
                i++;
            }
            this.mSyncAdapterSpinner.setAdapter(new ArrayAdapter(this, R.layout.sync_adapter_item, names));
        }
    }

    void updateUi() {
        boolean hasServiceConnection;
        boolean isBound;
        boolean z = true;
        synchronized (this.mServiceConnectionLock) {
            if (this.mActiveServiceConnection != null) {
                hasServiceConnection = true;
            } else {
                hasServiceConnection = false;
            }
            if (!hasServiceConnection || this.mActiveServiceConnection.mBoundSyncAdapter == null) {
                isBound = false;
            } else {
                isBound = true;
            }
        }
        this.mStartSyncButton.setEnabled(isBound);
        this.mCancelSyncButton.setEnabled(isBound);
        Button button = this.mBindButton;
        if (hasServiceConnection) {
            z = false;
        }
        button.setEnabled(z);
        this.mUnbindButton.setEnabled(hasServiceConnection);
    }

    public void startSyncSelected(View view) {
        synchronized (this.mServiceConnectionLock) {
            ISyncAdapter syncAdapter = null;
            if (this.mActiveServiceConnection != null) {
                syncAdapter = this.mActiveServiceConnection.mBoundSyncAdapter;
            }
            if (syncAdapter != null) {
                removeDialog(1);
                this.mAccountPickerView = (ListView) LayoutInflater.from(this).inflate(R.layout.account_list_view, null);
                this.mAccountPickerView.setOnItemClickListener(this);
                Account[] accounts = AccountManager.get(this).getAccountsByType(((SyncAdapterType) this.mActiveServiceConnection.mSyncAdapter.type).accountType);
                String[] accountNames = new String[accounts.length];
                for (int i = 0; i < accounts.length; i++) {
                    accountNames[i] = accounts[i].name;
                }
                this.mAccountPickerView.setAdapter(new ArrayAdapter(this, 17367043, accountNames));
                showDialog(1);
            }
        }
        updateUi();
    }

    private void startSync(String accountName) {
        synchronized (this.mServiceConnectionLock) {
            ISyncAdapter syncAdapter = null;
            if (this.mActiveServiceConnection != null) {
                syncAdapter = this.mActiveServiceConnection.mBoundSyncAdapter;
            }
            if (syncAdapter != null) {
                try {
                    this.mStatusTextView.setText(getString(R.string.status_starting_sync_format, new Object[]{accountName}));
                    syncAdapter.startSync(this.mActiveServiceConnection, ((SyncAdapterType) this.mActiveServiceConnection.mSyncAdapter.type).authority, new Account(accountName, ((SyncAdapterType) this.mActiveServiceConnection.mSyncAdapter.type).accountType), new Bundle());
                } catch (RemoteException e) {
                    this.mStatusTextView.setText(getString(R.string.status_remote_exception_while_starting_sync));
                }
            }
        }
        updateUi();
    }

    public void cancelSync(View view) {
        synchronized (this.mServiceConnectionLock) {
            ISyncAdapter syncAdapter = null;
            if (this.mActiveServiceConnection != null) {
                syncAdapter = this.mActiveServiceConnection.mBoundSyncAdapter;
            }
            if (syncAdapter != null) {
                try {
                    this.mStatusTextView.setText(getString(R.string.status_canceled_sync));
                    syncAdapter.cancelSync(this.mActiveServiceConnection);
                } catch (RemoteException e) {
                    this.mStatusTextView.setText(getString(R.string.status_remote_exception_while_canceling_sync));
                }
            }
        }
        updateUi();
    }

    public void onServiceChanged(SyncAdapterType type, int userId, boolean removed) {
        getSyncAdapters();
    }

    protected Dialog onCreateDialog(int id) {
        if (id != 1) {
            return super.onCreateDialog(id);
        }
        Builder builder = new Builder(this);
        builder.setMessage(R.string.select_account_to_sync);
        builder.setInverseBackgroundForced(true);
        builder.setView(this.mAccountPickerView);
        return builder.create();
    }

    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
        String accountName = ((TextView) view).getText().toString();
        dismissDialog(1);
        startSync(accountName);
    }

    public void initiateBind(View view) {
        synchronized (this.mServiceConnectionLock) {
            if (this.mActiveServiceConnection != null) {
                this.mStatusTextView.setText(getString(R.string.status_already_bound));
                return;
            }
            ServiceInfo<SyncAdapterType> syncAdapter = getSelectedSyncAdapter();
            if (syncAdapter == null) {
                this.mStatusTextView.setText(getString(R.string.status_sync_adapter_not_selected));
                return;
            }
            this.mActiveServiceConnection = new MyServiceConnection(syncAdapter);
            Intent intent = new Intent();
            intent.setAction("android.content.SyncAdapter");
            intent.setComponent(syncAdapter.componentName);
            intent.putExtra("android.intent.extra.client_label", 17040814);
            intent.putExtra("android.intent.extra.client_intent", PendingIntent.getActivity(this, 0, new Intent("android.settings.SYNC_SETTINGS"), 0));
            if (bindService(intent, this.mActiveServiceConnection, 1)) {
                this.mBoundAdapterTextView.setText(getString(R.string.binding_waiting_for_connection));
                updateUi();
                return;
            }
            this.mBoundAdapterTextView.setText(getString(R.string.binding_bind_failed));
            this.mActiveServiceConnection = null;
        }
    }

    public void initiateUnbind(View view) {
        synchronized (this.mServiceConnectionLock) {
            if (this.mActiveServiceConnection == null) {
                return;
            }
            this.mBoundAdapterTextView.setText("");
            unbindService(this.mActiveServiceConnection);
            this.mActiveServiceConnection = null;
            updateUi();
        }
    }

    private ServiceInfo<SyncAdapterType> getSelectedSyncAdapter() {
        synchronized (this.mSyncAdaptersLock) {
            int position = this.mSyncAdapterSpinner.getSelectedItemPosition();
            if (position == -1) {
                return null;
            }
            try {
                ServiceInfo<SyncAdapterType> serviceInfo = (ServiceInfo) this.mSyncAdapters[position];
                return serviceInfo;
            } catch (Exception e) {
                return null;
            }
        }
    }
}
