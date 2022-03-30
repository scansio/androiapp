package com.androi.development;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import java.io.File;

public class CacheAbuser extends Activity {
    AsyncTask<Void, Void, Void> mExternalAbuseTask;
    AsyncTask<Void, Void, Void> mInternalAbuseTask;
    Button mStartExternalAbuse;
    Button mStartInternalAbuse;
    Button mStartSlowExternalAbuse;
    Button mStartSlowInternalAbuse;
    Button mStopAbuse;

    static class AbuseTask extends AsyncTask<Void, Void, Void> {
        final File mBaseDir;
        final byte[] mBuffer;
        final boolean mQuick;

        AbuseTask(File cacheDir, boolean quick) {
            this.mBaseDir = new File(new File(cacheDir, quick ? "quick" : "slow"), Long.toString(System.currentTimeMillis()));
            this.mQuick = quick;
            this.mBuffer = quick ? new byte[1048576] : new byte[1024];
        }

        /* JADX WARNING: Removed duplicated region for block: B:43:0x0039 A:{SYNTHETIC} */
        /* JADX WARNING: Removed duplicated region for block: B:22:0x006a A:{SYNTHETIC, Splitter: B:22:0x006a} */
        protected java.lang.Void doInBackground(java.lang.Void... r15) {
            /*
            r14 = this;
            r9 = 0;
        L_0x0002:
            r11 = r14.isCancelled();
            if (r11 != 0) goto L_0x0077;
        L_0x0008:
            r11 = 100;
            r1 = r9 / r11;
            r11 = 100;
            r3 = r9 % r11;
            r0 = new java.io.File;
            r11 = r14.mBaseDir;
            r12 = java.lang.Long.toString(r1);
            r0.<init>(r11, r12);
            r6 = new java.io.File;
            r11 = java.lang.Long.toString(r3);
            r6.<init>(r0, r11);
            r7 = 0;
            r0.mkdirs();	 Catch:{ IOException -> 0x0040 }
            r8 = new java.io.FileOutputStream;	 Catch:{ IOException -> 0x0040 }
            r11 = 0;
            r8.<init>(r6, r11);	 Catch:{ IOException -> 0x0040 }
            r11 = r14.mBuffer;	 Catch:{ IOException -> 0x0080, all -> 0x007d }
            r8.write(r11);	 Catch:{ IOException -> 0x0080, all -> 0x007d }
            if (r8 == 0) goto L_0x0038;
        L_0x0035:
            r8.close();	 Catch:{ IOException -> 0x003d }
        L_0x0038:
            r7 = r8;
        L_0x0039:
            r11 = 1;
            r9 = r9 + r11;
            goto L_0x0002;
        L_0x003d:
            r11 = move-exception;
            r7 = r8;
            goto L_0x0039;
        L_0x0040:
            r5 = move-exception;
        L_0x0041:
            r11 = "CacheAbuser";
            r12 = new java.lang.StringBuilder;	 Catch:{ all -> 0x0070 }
            r12.<init>();	 Catch:{ all -> 0x0070 }
            r13 = "Write failed to ";
            r12 = r12.append(r13);	 Catch:{ all -> 0x0070 }
            r12 = r12.append(r6);	 Catch:{ all -> 0x0070 }
            r13 = ": ";
            r12 = r12.append(r13);	 Catch:{ all -> 0x0070 }
            r12 = r12.append(r5);	 Catch:{ all -> 0x0070 }
            r12 = r12.toString();	 Catch:{ all -> 0x0070 }
            android.util.Log.w(r11, r12);	 Catch:{ all -> 0x0070 }
            r11 = 5000; // 0x1388 float:7.006E-42 double:2.4703E-320;
            r14.wait(r11);	 Catch:{ InterruptedException -> 0x0079 }
        L_0x0068:
            if (r7 == 0) goto L_0x0039;
        L_0x006a:
            r7.close();	 Catch:{ IOException -> 0x006e }
            goto L_0x0039;
        L_0x006e:
            r11 = move-exception;
            goto L_0x0039;
        L_0x0070:
            r11 = move-exception;
        L_0x0071:
            if (r7 == 0) goto L_0x0076;
        L_0x0073:
            r7.close();	 Catch:{ IOException -> 0x007b }
        L_0x0076:
            throw r11;
        L_0x0077:
            r11 = 0;
            return r11;
        L_0x0079:
            r11 = move-exception;
            goto L_0x0068;
        L_0x007b:
            r12 = move-exception;
            goto L_0x0076;
        L_0x007d:
            r11 = move-exception;
            r7 = r8;
            goto L_0x0071;
        L_0x0080:
            r5 = move-exception;
            r7 = r8;
            goto L_0x0041;
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.development.CacheAbuser.AbuseTask.doInBackground(java.lang.Void[]):java.lang.Void");
        }
    }

    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setContentView(R.layout.cache_abuser);
        this.mStartInternalAbuse = (Button) findViewById(R.id.start_internal_abuse);
        this.mStartInternalAbuse.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                if (CacheAbuser.this.mInternalAbuseTask == null) {
                    CacheAbuser.this.mInternalAbuseTask = new AbuseTask(CacheAbuser.this.getCacheDir(), true);
                    CacheAbuser.this.mInternalAbuseTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[0]);
                    CacheAbuser.this.updateButtonState();
                }
            }
        });
        this.mStartSlowInternalAbuse = (Button) findViewById(R.id.start_slow_internal_abuse);
        this.mStartSlowInternalAbuse.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                if (CacheAbuser.this.mInternalAbuseTask == null) {
                    CacheAbuser.this.mInternalAbuseTask = new AbuseTask(CacheAbuser.this.getCacheDir(), false);
                    CacheAbuser.this.mInternalAbuseTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[0]);
                    CacheAbuser.this.updateButtonState();
                }
            }
        });
        this.mStartExternalAbuse = (Button) findViewById(R.id.start_external_abuse);
        this.mStartExternalAbuse.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                if (CacheAbuser.this.mExternalAbuseTask == null) {
                    CacheAbuser.this.mExternalAbuseTask = new AbuseTask(CacheAbuser.this.getExternalCacheDir(), true);
                    CacheAbuser.this.mExternalAbuseTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[0]);
                    CacheAbuser.this.updateButtonState();
                }
            }
        });
        this.mStartSlowExternalAbuse = (Button) findViewById(R.id.start_slow_external_abuse);
        this.mStartSlowExternalAbuse.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                if (CacheAbuser.this.mExternalAbuseTask == null) {
                    CacheAbuser.this.mExternalAbuseTask = new AbuseTask(CacheAbuser.this.getExternalCacheDir(), false);
                    CacheAbuser.this.mExternalAbuseTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[0]);
                    CacheAbuser.this.updateButtonState();
                }
            }
        });
        this.mStopAbuse = (Button) findViewById(R.id.stop_abuse);
        this.mStopAbuse.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                CacheAbuser.this.stopAbuse();
            }
        });
        updateButtonState();
    }

    public void onStart() {
        super.onStart();
        updateButtonState();
    }

    public void onStop() {
        super.onStop();
        stopAbuse();
    }

    void stopAbuse() {
        if (this.mInternalAbuseTask != null) {
            this.mInternalAbuseTask.cancel(false);
            this.mInternalAbuseTask = null;
        }
        if (this.mExternalAbuseTask != null) {
            this.mExternalAbuseTask.cancel(false);
            this.mExternalAbuseTask = null;
        }
        updateButtonState();
    }

    void updateButtonState() {
        boolean z;
        boolean z2 = false;
        this.mStartInternalAbuse.setEnabled(this.mInternalAbuseTask == null);
        Button button = this.mStartSlowInternalAbuse;
        if (this.mInternalAbuseTask == null) {
            z = true;
        } else {
            z = false;
        }
        button.setEnabled(z);
        button = this.mStartExternalAbuse;
        if (this.mExternalAbuseTask == null) {
            z = true;
        } else {
            z = false;
        }
        button.setEnabled(z);
        button = this.mStartSlowExternalAbuse;
        if (this.mExternalAbuseTask == null) {
            z = true;
        } else {
            z = false;
        }
        button.setEnabled(z);
        Button button2 = this.mStopAbuse;
        if (!(this.mInternalAbuseTask == null && this.mExternalAbuseTask == null)) {
            z2 = true;
        }
        button2.setEnabled(z2);
    }
}
