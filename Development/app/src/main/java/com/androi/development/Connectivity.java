package com.androi.development;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkUtils;
import android.net.RouteInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.INetworkManagementService;
import android.os.INetworkManagementService.Stub;
import android.os.Message;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.os.ServiceManager;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.Socket;
import java.util.Enumeration;
import java.util.List;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.params.ConnRouteParams;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;

public class Connectivity extends Activity {
    private OnClickListener mClickListener = new OnClickListener() {
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.enableWifi:
                    Connectivity.this.mWm.setWifiEnabled(true);
                    return;
                case R.id.disableWifi:
                    Connectivity.this.mWm.setWifiEnabled(false);
                    return;
                case R.id.startDelayedCycle:
                    Connectivity.this.onStartDelayedCycle();
                    return;
                case R.id.stopDelayedCycle:
                    Connectivity.this.onStopDelayedCycle();
                    return;
                case R.id.startScreenCycle:
                    Connectivity.this.onStartScreenCycle();
                    return;
                case R.id.stopScreenCycle:
                    Connectivity.this.onStopScreenCycle();
                    return;
                case R.id.startScan:
                    Connectivity.this.onStartScanCycle();
                    return;
                case R.id.startTdls:
                    Connectivity.this.onStartTdls();
                    return;
                case R.id.stopTdls:
                    Connectivity.this.onStopTdls();
                    return;
                case R.id.start_mms:
                    Connectivity.this.mCm.startUsingNetworkFeature(0, "enableMMS");
                    return;
                case R.id.stop_mms:
                    Connectivity.this.mCm.stopUsingNetworkFeature(0, "enableMMS");
                    return;
                case R.id.start_hipri:
                    Connectivity.this.mCm.startUsingNetworkFeature(0, "enableHIPRI");
                    return;
                case R.id.stop_hipri:
                    Connectivity.this.mCm.stopUsingNetworkFeature(0, "enableHIPRI");
                    return;
                case R.id.crash:
                    Connectivity.this.onCrash();
                    return;
                case R.id.add_default_route:
                    Connectivity.this.onAddDefaultRoute();
                    return;
                case R.id.remove_default_route:
                    Connectivity.this.onRemoveDefaultRoute();
                    return;
                case R.id.default_request:
                    Connectivity.this.onDefaultRequest();
                    return;
                case R.id.default_socket:
                    Connectivity.this.onDefaultSocket();
                    return;
                case R.id.bound_http_request:
                    Connectivity.this.onBoundHttpRequest();
                    return;
                case R.id.bound_socket_request:
                    Connectivity.this.onBoundSocketRequest();
                    return;
                case R.id.routed_http_request:
                    Connectivity.this.onRoutedHttpRequest();
                    return;
                case R.id.routed_socket_request:
                    Connectivity.this.onRoutedSocketRequest();
                    return;
                default:
                    return;
            }
        }
    };
    private ConnectivityManager mCm;
    private int mDCCycleCount = 0;
    private TextView mDCCycleCountView;
    private long mDCOffDuration = 120000;
    private EditText mDCOffDurationEdit;
    private long mDCOnDuration = 120000;
    private EditText mDCOnDurationEdit;
    private boolean mDelayedCycleStarted = false;
    public Handler mHandler2 = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    Log.e("DevTools - Connectivity", "EVENT_TOGGLE_WIFI");
                    if (Connectivity.this.mDelayedCycleStarted && Connectivity.this.mWm != null) {
                        long delay;
                        switch (Connectivity.this.mWm.getWifiState()) {
                            case 2:
                            case 3:
                                Connectivity.this.mWm.setWifiEnabled(false);
                                delay = Connectivity.this.mDCOffDuration;
                                break;
                            default:
                                Connectivity.this.mWm.setWifiEnabled(true);
                                delay = Connectivity.this.mDCOnDuration;
                                Connectivity.this.mDCCycleCount = Connectivity.this.mDCCycleCount + 1;
                                Connectivity.this.mDCCycleCountView.setText(Integer.toString(Connectivity.this.mDCCycleCount));
                                break;
                        }
                        sendMessageDelayed(obtainMessage(1), delay);
                        return;
                    }
                    return;
                default:
                    return;
            }
        }
    };
    IntentFilter mIntentFilter;
    private INetworkManagementService mNetd;
    private PowerManager mPm;
    public BroadcastReceiver mReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals("com.android.development.CONNECTIVITY_TEST_ALARM")) {
                String extra = (String) intent.getExtra("CONNECTIVITY_TEST_EXTRA");
                PowerManager pm = (PowerManager) context.getSystemService("power");
                Long on = new Long(120000);
                Long off = new Long(120000);
                int cycle = 0;
                try {
                    on = Long.valueOf(Long.parseLong((String) intent.getExtra("CONNECTIVITY_TEST_ON_EXTRA")));
                    off = Long.valueOf(Long.parseLong((String) intent.getExtra("CONNECTIVITY_TEST_OFF_EXTRA")));
                    cycle = Integer.parseInt((String) intent.getExtra("CONNECTIVITY_TEST_CYCLE_EXTRA"));
                } catch (Exception e) {
                }
                if (extra.equals("SCREEN_ON")) {
                    Connectivity.this.mScreenonWakeLock = Connectivity.this.mPm.newWakeLock(268435482, "ConnectivityTest");
                    Connectivity.this.mScreenonWakeLock.acquire();
                    Connectivity.this.mSCCycleCount = cycle + 1;
                    Connectivity.this.mSCOnDuration = on.longValue();
                    Connectivity.this.mSCOffDuration = off.longValue();
                    Connectivity.this.mSCCycleCountView.setText(Integer.toString(Connectivity.this.mSCCycleCount));
                    Connectivity.this.scheduleAlarm(Connectivity.this.mSCOnDuration, "SCREEN_OFF");
                } else if (extra.equals("SCREEN_OFF")) {
                    Connectivity.this.mSCCycleCount = cycle;
                    Connectivity.this.mSCOnDuration = on.longValue();
                    Connectivity.this.mSCOffDuration = off.longValue();
                    Connectivity.this.mScreenonWakeLock.release();
                    Connectivity.this.mScreenonWakeLock = null;
                    Connectivity.this.scheduleAlarm(Connectivity.this.mSCOffDuration, "SCREEN_ON");
                    pm.goToSleep(SystemClock.uptimeMillis());
                }
            }
        }
    };
    private int mSCCycleCount = 0;
    private TextView mSCCycleCountView;
    private long mSCOffDuration = 12000;
    private EditText mSCOffDurationEdit;
    private long mSCOnDuration = 120000;
    private EditText mSCOnDurationEdit;
    private Button mScanButton;
    private long mScanCur = -1;
    private long mScanCycles = 15;
    private EditText mScanCyclesEdit;
    private CheckBox mScanDisconnect;
    private WifiScanReceiver mScanRecv;
    private TextView mScanResults;
    private boolean mScreenOff = false;
    private boolean mScreenOffToggleRunning = false;
    private WakeLock mScreenonWakeLock = null;
    private long mStartTime = -1;
    private long mStopTime;
    private String mTdlsAddr = null;
    private long mTotalScanCount = 0;
    private long mTotalScanTime = 0;
    private WakeLock mWakeLock = null;
    private WifiManager mWm;

    private class WifiScanReceiver extends BroadcastReceiver {
        private WifiScanReceiver() {
        }

        /* synthetic */ WifiScanReceiver(Connectivity x0, AnonymousClass1 x1) {
            this();
        }

        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals("android.net.wifi.SCAN_RESULTS")) {
                Connectivity.this.mStopTime = SystemClock.elapsedRealtime();
                if (Connectivity.this.mStartTime != -1) {
                    Connectivity.access$1514(Connectivity.this, Connectivity.this.mStopTime - Connectivity.this.mStartTime);
                    Connectivity.this.mStartTime = -1;
                }
                Log.d("DevTools - Connectivity", "Scan: READY " + Connectivity.this.mScanCur);
                List<ScanResult> wifiScanResults = Connectivity.this.mWm.getScanResults();
                if (wifiScanResults != null) {
                    Connectivity.access$1714(Connectivity.this, (long) wifiScanResults.size());
                    Log.d("DevTools - Connectivity", "Scan: Results = " + wifiScanResults.size());
                }
                Connectivity.this.mScanCur = Connectivity.this.mScanCur - 1;
                Connectivity.this.mScanCyclesEdit.setText(Long.toString(Connectivity.this.mScanCur));
                if (Connectivity.this.mScanCur == 0) {
                    Connectivity.this.unregisterReceiver(Connectivity.this.mScanRecv);
                    Connectivity.this.mScanButton.setText("Get Results");
                    return;
                }
                Log.d("DevTools - Connectivity", "Scan: START " + Connectivity.this.mScanCur);
                Connectivity.this.mStartTime = SystemClock.elapsedRealtime();
                Connectivity.this.mWm.startScan();
            }
        }
    }

    static /* synthetic */ long access$1514(Connectivity x0, long x1) {
        long j = x0.mTotalScanTime + x1;
        x0.mTotalScanTime = j;
        return j;
    }

    static /* synthetic */ long access$1714(Connectivity x0, long x1) {
        long j = x0.mTotalScanCount + x1;
        x0.mTotalScanCount = j;
        return j;
    }

    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setContentView(R.layout.connectivity);
        this.mWm = (WifiManager) getSystemService("wifi");
        this.mPm = (PowerManager) getSystemService("power");
        this.mCm = (ConnectivityManager) getSystemService("connectivity");
        this.mNetd = Stub.asInterface(ServiceManager.getService("network_management"));
        findViewById(R.id.enableWifi).setOnClickListener(this.mClickListener);
        findViewById(R.id.disableWifi).setOnClickListener(this.mClickListener);
        findViewById(R.id.startDelayedCycle).setOnClickListener(this.mClickListener);
        findViewById(R.id.stopDelayedCycle).setOnClickListener(this.mClickListener);
        this.mDCOnDurationEdit = (EditText) findViewById(R.id.dc_wifi_on_duration);
        this.mDCOnDurationEdit.setText(Long.toString(this.mDCOnDuration));
        this.mDCOffDurationEdit = (EditText) findViewById(R.id.dc_wifi_off_duration);
        this.mDCOffDurationEdit.setText(Long.toString(this.mDCOffDuration));
        this.mDCCycleCountView = (TextView) findViewById(R.id.dc_wifi_cycles_done);
        this.mDCCycleCountView.setText(Integer.toString(this.mDCCycleCount));
        findViewById(R.id.startScreenCycle).setOnClickListener(this.mClickListener);
        findViewById(R.id.stopScreenCycle).setOnClickListener(this.mClickListener);
        this.mSCOnDurationEdit = (EditText) findViewById(R.id.sc_wifi_on_duration);
        this.mSCOnDurationEdit.setText(Long.toString(this.mSCOnDuration));
        this.mSCOffDurationEdit = (EditText) findViewById(R.id.sc_wifi_off_duration);
        this.mSCOffDurationEdit.setText(Long.toString(this.mSCOffDuration));
        this.mSCCycleCountView = (TextView) findViewById(R.id.sc_wifi_cycles_done);
        this.mSCCycleCountView.setText(Integer.toString(this.mSCCycleCount));
        this.mScanButton = (Button) findViewById(R.id.startScan);
        this.mScanButton.setOnClickListener(this.mClickListener);
        this.mScanCyclesEdit = (EditText) findViewById(R.id.sc_scan_cycles);
        this.mScanCyclesEdit.setText(Long.toString(this.mScanCycles));
        this.mScanDisconnect = (CheckBox) findViewById(R.id.scanDisconnect);
        this.mScanDisconnect.setChecked(true);
        this.mScanResults = (TextView) findViewById(R.id.sc_scan_results);
        this.mScanResults.setVisibility(4);
        this.mScanRecv = new WifiScanReceiver(this, null);
        this.mIntentFilter = new IntentFilter();
        this.mIntentFilter.addAction("android.net.wifi.SCAN_RESULTS");
        findViewById(R.id.startTdls).setOnClickListener(this.mClickListener);
        findViewById(R.id.stopTdls).setOnClickListener(this.mClickListener);
        findViewById(R.id.start_mms).setOnClickListener(this.mClickListener);
        findViewById(R.id.stop_mms).setOnClickListener(this.mClickListener);
        findViewById(R.id.start_hipri).setOnClickListener(this.mClickListener);
        findViewById(R.id.stop_hipri).setOnClickListener(this.mClickListener);
        findViewById(R.id.crash).setOnClickListener(this.mClickListener);
        findViewById(R.id.add_default_route).setOnClickListener(this.mClickListener);
        findViewById(R.id.remove_default_route).setOnClickListener(this.mClickListener);
        findViewById(R.id.bound_http_request).setOnClickListener(this.mClickListener);
        findViewById(R.id.bound_socket_request).setOnClickListener(this.mClickListener);
        findViewById(R.id.routed_http_request).setOnClickListener(this.mClickListener);
        findViewById(R.id.routed_socket_request).setOnClickListener(this.mClickListener);
        findViewById(R.id.default_request).setOnClickListener(this.mClickListener);
        findViewById(R.id.default_socket).setOnClickListener(this.mClickListener);
        registerReceiver(this.mReceiver, new IntentFilter("com.android.development.CONNECTIVITY_TEST_ALARM"));
    }

    public void onResume() {
        super.onResume();
        findViewById(R.id.connectivity_layout).requestFocus();
    }

    private void onStartDelayedCycle() {
        if (!this.mDelayedCycleStarted) {
            this.mDelayedCycleStarted = true;
            try {
                this.mDCOnDuration = Long.parseLong(this.mDCOnDurationEdit.getText().toString());
                this.mDCOffDuration = Long.parseLong(this.mDCOffDurationEdit.getText().toString());
            } catch (Exception e) {
            }
            this.mDCCycleCount = 0;
            this.mWakeLock = this.mPm.newWakeLock(26, "ConnectivityTest");
            this.mWakeLock.acquire();
            this.mHandler2.sendMessage(this.mHandler2.obtainMessage(1));
        }
    }

    private void onStopDelayedCycle() {
        if (this.mDelayedCycleStarted) {
            this.mDelayedCycleStarted = false;
            this.mWakeLock.release();
            this.mWakeLock = null;
            if (this.mHandler2.hasMessages(1)) {
                this.mHandler2.removeMessages(1);
            }
        }
    }

    private void onStartScreenCycle() {
        try {
            this.mSCOnDuration = Long.parseLong(this.mSCOnDurationEdit.getText().toString());
            this.mSCOffDuration = Long.parseLong(this.mSCOffDurationEdit.getText().toString());
        } catch (Exception e) {
        }
        this.mSCCycleCount = 0;
        this.mScreenonWakeLock = this.mPm.newWakeLock(26, "ConnectivityTest");
        this.mScreenonWakeLock.acquire();
        scheduleAlarm(10, "SCREEN_OFF");
    }

    private void scheduleAlarm(long delayMs, String eventType) {
        AlarmManager am = (AlarmManager) getSystemService("alarm");
        Intent i = new Intent("com.android.development.CONNECTIVITY_TEST_ALARM");
        i.putExtra("CONNECTIVITY_TEST_EXTRA", eventType);
        i.putExtra("CONNECTIVITY_TEST_ON_EXTRA", Long.toString(this.mSCOnDuration));
        i.putExtra("CONNECTIVITY_TEST_OFF_EXTRA", Long.toString(this.mSCOffDuration));
        i.putExtra("CONNECTIVITY_TEST_CYCLE_EXTRA", Integer.toString(this.mSCCycleCount));
        am.set(2, SystemClock.elapsedRealtime() + delayMs, PendingIntent.getBroadcast(this, 0, i, 134217728));
    }

    private void onStopScreenCycle() {
    }

    private void onCrash() {
        ConnectivityManager foo = null;
        foo.startUsingNetworkFeature(0, "enableMMS");
    }

    private void onStartScanCycle() {
        if (this.mScanCur == -1) {
            try {
                this.mScanCur = Long.parseLong(this.mScanCyclesEdit.getText().toString());
                this.mScanCycles = this.mScanCur;
            } catch (Exception e) {
            }
            if (this.mScanCur <= 0) {
                this.mScanCur = -1;
                this.mScanCycles = 15;
                return;
            }
        }
        if (this.mScanCur > 0) {
            registerReceiver(this.mScanRecv, this.mIntentFilter);
            this.mScanButton.setText("In Progress");
            this.mScanResults.setVisibility(4);
            if (this.mScanDisconnect.isChecked()) {
                this.mWm.disconnect();
            }
            this.mTotalScanTime = 0;
            this.mTotalScanCount = 0;
            Log.d("DevTools - Connectivity", "Scan: START " + this.mScanCur);
            this.mStartTime = SystemClock.elapsedRealtime();
            this.mWm.startScan();
            return;
        }
        this.mScanResults.setText("Average Scan Time = " + Long.toString(this.mTotalScanTime / this.mScanCycles) + " ms ; Average Scan Amount = " + Long.toString(this.mTotalScanCount / this.mScanCycles));
        this.mScanResults.setVisibility(0);
        this.mScanButton.setText("Start Scan");
        this.mScanCur = -1;
        this.mScanCyclesEdit.setText(Long.toString(this.mScanCycles));
        if (this.mScanDisconnect.isChecked()) {
            this.mWm.reassociate();
        }
    }

    private void onStartTdls() {
        this.mTdlsAddr = ((EditText) findViewById(R.id.sc_ip_mac)).getText().toString();
        Log.d("DevTools - Connectivity", "TDLS: START " + this.mTdlsAddr);
        try {
            this.mWm.setTdlsEnabled(InetAddress.getByName(this.mTdlsAddr), true);
        } catch (Exception e) {
            this.mWm.setTdlsEnabledWithMacAddress(this.mTdlsAddr, true);
        }
    }

    private void onStopTdls() {
        if (this.mTdlsAddr != null) {
            Log.d("DevTools - Connectivity", "TDLS: STOP " + this.mTdlsAddr);
            try {
                this.mWm.setTdlsEnabled(InetAddress.getByName(this.mTdlsAddr), false);
            } catch (Exception e) {
                this.mWm.setTdlsEnabledWithMacAddress(this.mTdlsAddr, false);
            }
        }
    }

    private void onAddDefaultRoute() {
        try {
            this.mNetd.addRoute("eth0", new RouteInfo(null, NetworkUtils.numericToInetAddress("8.8.8.8")));
        } catch (Exception e) {
            Log.e("DevTools - Connectivity", "onAddDefaultRoute got exception: " + e.toString());
        }
    }

    private void onRemoveDefaultRoute() {
        try {
            this.mNetd.removeRoute("eth0", new RouteInfo(null, NetworkUtils.numericToInetAddress("8.8.8.8")));
        } catch (Exception e) {
            Log.e("DevTools - Connectivity", "onRemoveDefaultRoute got exception: " + e.toString());
        }
    }

    private void onRoutedHttpRequest() {
        onRoutedRequest(2);
    }

    private void onRoutedSocketRequest() {
        onRoutedRequest(1);
    }

    private void onRoutedRequest(int type) {
        String url = "www.google.com";
        try {
            this.mCm.requestRouteToHostAddress(5, InetAddress.getByName(url));
            switch (type) {
                case 1:
                    onBoundSocketRequest();
                    return;
                case 2:
                    try {
                        Log.d("DevTools - Connectivity", "routed http request gives " + new DefaultHttpClient().execute(new HttpGet("http://" + url)).getStatusLine());
                        return;
                    } catch (Exception e) {
                        Log.e("DevTools - Connectivity", "routed http request exception = " + e);
                        return;
                    }
                default:
                    return;
            }
        } catch (Exception e2) {
            Log.e("DevTools - Connectivity", "error fetching address for " + url);
        }
    }

    private void onBoundHttpRequest() {
        try {
            NetworkInterface networkInterface = NetworkInterface.getByName("rmnet0");
            Log.d("DevTools - Connectivity", "networkInterface is " + networkInterface);
            if (networkInterface != null) {
                Enumeration inetAddressess = networkInterface.getInetAddresses();
                while (inetAddressess.hasMoreElements()) {
                    Log.d("DevTools - Connectivity", " inetAddress:" + ((InetAddress) inetAddressess.nextElement()));
                }
            }
            HttpParams httpParams = new BasicHttpParams();
            if (networkInterface != null) {
                ConnRouteParams.setLocalAddress(httpParams, (InetAddress) networkInterface.getInetAddresses().nextElement());
            }
            try {
                Log.d("DevTools - Connectivity", "response code = " + new DefaultHttpClient(httpParams).execute(new HttpGet("http://www.bbc.com")).getStatusLine());
            } catch (Exception e) {
                Log.e("DevTools - Connectivity", "Exception = " + e);
            }
        } catch (Exception e2) {
            Log.e("DevTools - Connectivity", " exception getByName: " + e2);
        }
    }

    private void onBoundSocketRequest() {
        try {
            NetworkInterface networkInterface = NetworkInterface.getByName("rmnet0");
            if (networkInterface == null) {
                try {
                    Log.d("DevTools - Connectivity", "getting any networkInterface");
                    networkInterface = (NetworkInterface) NetworkInterface.getNetworkInterfaces().nextElement();
                } catch (Exception e) {
                    Log.e("DevTools - Connectivity", "exception getting any networkInterface: " + e);
                    return;
                }
            }
            if (networkInterface == null) {
                Log.e("DevTools - Connectivity", "couldn't find a local interface");
                return;
            }
            Enumeration inetAddressess = networkInterface.getInetAddresses();
            while (inetAddressess.hasMoreElements()) {
                Log.d("DevTools - Connectivity", " addr:" + ((InetAddress) inetAddressess.nextElement()));
            }
            try {
                InetAddress local = (InetAddress) networkInterface.getInetAddresses().nextElement();
                try {
                    InetAddress remote = InetAddress.getByName("www.flickr.com");
                    Log.d("DevTools - Connectivity", "remote addr =" + remote);
                    Log.d("DevTools - Connectivity", "local addr =" + local);
                    try {
                        try {
                            new PrintWriter(new Socket(remote, 80, local, 6000).getOutputStream(), true).println("Hi flickr");
                        } catch (Exception e2) {
                            Log.e("DevTools - Connectivity", "Exception writing to socket: " + e2);
                        }
                    } catch (Exception e22) {
                        Log.e("DevTools - Connectivity", "Exception creating socket: " + e22);
                    }
                } catch (Exception e222) {
                    Log.e("DevTools - Connectivity", "exception getting remote InetAddress: " + e222);
                }
            } catch (Exception e2222) {
                Log.e("DevTools - Connectivity", "exception getting local InetAddress: " + e2222);
            }
        } catch (Exception e22222) {
            Log.e("DevTools - Connectivity", "exception getByName: " + e22222);
        }
    }

    private void onDefaultRequest() {
        HttpParams params = new BasicHttpParams();
        try {
            Log.e("DevTools - Connectivity", "response code = " + new DefaultHttpClient(params).execute(new HttpGet("http://www.cnn.com")).getStatusLine());
        } catch (Exception e) {
            Log.e("DevTools - Connectivity", "Exception = " + e);
        }
    }

    private void onDefaultSocket() {
        try {
            InetAddress remote = InetAddress.getByName("www.flickr.com");
            Log.e("DevTools - Connectivity", "remote addr =" + remote);
            try {
                try {
                    new PrintWriter(new Socket(remote, 80).getOutputStream(), true).println("Hi flickr");
                    Log.e("DevTools - Connectivity", "written");
                } catch (Exception e) {
                    Log.e("DevTools - Connectivity", "Exception writing to socket: " + e);
                }
            } catch (Exception e2) {
                Log.e("DevTools - Connectivity", "Exception creating socket: " + e2);
            }
        } catch (Exception e22) {
            Log.e("DevTools - Connectivity", "exception getting remote InetAddress: " + e22);
        }
    }
}
