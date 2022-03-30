package com.androi.development;

import android.app.Activity;
import android.app.ActivityManagerNative;
import android.app.IActivityController.Stub;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.os.IPowerManager;
import android.os.Process;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class BadBehaviorActivity extends Activity {

    private static class BadBehaviorException extends RuntimeException {
        BadBehaviorException() {
            super("Whatcha gonna do, whatcha gonna do", new IllegalStateException("When they come for you"));
        }
    }

    public static class BadController extends Stub {
        private int mDelay;

        public BadController(int delay) {
            this.mDelay = delay;
        }

        public boolean activityStarting(Intent intent, String pkg) {
            try {
                ActivityManagerNative.getDefault().setActivityController(null);
            } catch (RemoteException e) {
                Log.e("BadBehaviorActivity", "Can't call IActivityManager.setActivityController", e);
            }
            if (this.mDelay > 0) {
                Log.i("BadBehaviorActivity", "in activity controller -- about to hang");
                try {
                    Thread.sleep((long) this.mDelay);
                } catch (InterruptedException e2) {
                    Log.wtf("BadBehaviorActivity", e2);
                }
                Log.i("BadBehaviorActivity", "activity controller hang finished -- disabling and returning");
                this.mDelay = 0;
            }
            return true;
        }

        public boolean activityResuming(String pkg) {
            return true;
        }

        public boolean appCrashed(String proc, int pid, String m, String m2, long time, String st) {
            return true;
        }

        public int appEarlyNotResponding(String processName, int pid, String annotation) {
            return 0;
        }

        public int appNotResponding(String proc, int pid, String st) {
            return 0;
        }

        public int systemNotResponding(String message) {
            return 0;
        }
    }

    public static class BadReceiver extends BroadcastReceiver {
        public void onReceive(Context context, Intent intent) {
            Log.i("BadBehaviorActivity", "in broadcast receiver -- about to hang");
            try {
                Thread.sleep(20000);
            } catch (InterruptedException e) {
                Log.wtf("BadBehaviorActivity", e);
            }
            Log.i("BadBehaviorActivity", "broadcast receiver hang finished -- returning");
        }
    }

    public static class BadService extends Service {
        public IBinder onBind(Intent intent) {
            return null;
        }

        public int onStartCommand(Intent intent, int flags, int id) {
            Log.i("BadBehaviorActivity", "in service start -- about to hang");
            try {
                Thread.sleep(30000);
            } catch (InterruptedException e) {
                Log.wtf("BadBehaviorActivity", e);
            }
            Log.i("BadBehaviorActivity", "service hang finished -- stopping and returning");
            stopSelf();
            return 2;
        }
    }

    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        if (getIntent().getBooleanExtra("anr", false)) {
            Log.i("BadBehaviorActivity", "in ANR activity -- about to hang");
            try {
                Thread.sleep(20000);
            } catch (InterruptedException e) {
                Log.wtf("BadBehaviorActivity", e);
            }
            Log.i("BadBehaviorActivity", "activity hang finished -- finishing");
            finish();
        } else if (getIntent().getBooleanExtra("dummy", false)) {
            Log.i("BadBehaviorActivity", "in dummy activity -- finishing");
            finish();
        } else {
            setContentView(R.layout.bad_behavior);
            ((Button) findViewById(R.id.bad_behavior_crash_system)).setOnClickListener(new OnClickListener() {
                public void onClick(View v) {
                    try {
                        IPowerManager.Stub.asInterface(ServiceManager.getService("power")).crash("Crashed by BadBehaviorActivity");
                    } catch (RemoteException e) {
                        Log.e("BadBehaviorActivity", "Can't call IPowerManager.crash()", e);
                    }
                }
            });
            ((Button) findViewById(R.id.bad_behavior_crash_main)).setOnClickListener(new OnClickListener() {
                public void onClick(View v) {
                    throw new BadBehaviorException();
                }
            });
            ((Button) findViewById(R.id.bad_behavior_crash_thread)).setOnClickListener(new OnClickListener() {
                public void onClick(View v) {
                    new Thread() {
                        public void run() {
                            throw new BadBehaviorException();
                        }
                    }.start();
                }
            });
            ((Button) findViewById(R.id.bad_behavior_crash_native)).setOnClickListener(new OnClickListener() {
                public void onClick(View v) {
                    Log.i("BadBehaviorActivity", "Native crash pressed -- about to kill -11 self");
                    Process.sendSignal(Process.myPid(), 11);
                    Process.sendSignal(Process.myPid(), 11);
                    Log.i("BadBehaviorActivity", "Finished kill -11, should be dead or dying");
                }
            });
            ((Button) findViewById(R.id.bad_behavior_wtf)).setOnClickListener(new OnClickListener() {
                public void onClick(View v) {
                    Log.wtf("BadBehaviorActivity", "Apps Behaving Badly");
                }
            });
            ((Button) findViewById(R.id.bad_behavior_anr)).setOnClickListener(new OnClickListener() {
                public void onClick(View v) {
                    Log.i("BadBehaviorActivity", "ANR pressed -- about to hang");
                    try {
                        Thread.sleep(20000);
                    } catch (InterruptedException e) {
                        Log.wtf("BadBehaviorActivity", e);
                    }
                    Log.i("BadBehaviorActivity", "hang finished -- returning");
                }
            });
            ((Button) findViewById(R.id.bad_behavior_anr_activity)).setOnClickListener(new OnClickListener() {
                public void onClick(View v) {
                    Intent intent = new Intent(BadBehaviorActivity.this, BadBehaviorActivity.class);
                    Log.i("BadBehaviorActivity", "ANR activity pressed -- about to launch");
                    BadBehaviorActivity.this.startActivity(intent.putExtra("anr", true));
                }
            });
            ((Button) findViewById(R.id.bad_behavior_anr_broadcast)).setOnClickListener(new OnClickListener() {
                public void onClick(View v) {
                    Log.i("BadBehaviorActivity", "ANR broadcast pressed -- about to send");
                    BadBehaviorActivity.this.sendOrderedBroadcast(new Intent("com.android.development.BAD_BEHAVIOR"), null);
                }
            });
            ((Button) findViewById(R.id.bad_behavior_anr_service)).setOnClickListener(new OnClickListener() {
                public void onClick(View v) {
                    Log.i("BadBehaviorActivity", "ANR service pressed -- about to start");
                    BadBehaviorActivity.this.startService(new Intent(BadBehaviorActivity.this, BadService.class));
                }
            });
            ((Button) findViewById(R.id.bad_behavior_anr_system)).setOnClickListener(new OnClickListener() {
                public void onClick(View v) {
                    Intent intent = new Intent(BadBehaviorActivity.this, BadBehaviorActivity.class);
                    Log.i("BadBehaviorActivity", "ANR system pressed -- about to engage");
                    try {
                        ActivityManagerNative.getDefault().setActivityController(new BadController(20000));
                    } catch (RemoteException e) {
                        Log.e("BadBehaviorActivity", "Can't call IActivityManager.setActivityController", e);
                    }
                    BadBehaviorActivity.this.startActivity(intent.putExtra("dummy", true));
                }
            });
            ((Button) findViewById(R.id.bad_behavior_wedge_system)).setOnClickListener(new OnClickListener() {
                public void onClick(View v) {
                    Intent intent = new Intent(BadBehaviorActivity.this, BadBehaviorActivity.class);
                    Log.i("BadBehaviorActivity", "Wedge system pressed -- about to engage");
                    try {
                        ActivityManagerNative.getDefault().setActivityController(new BadController(300000));
                    } catch (RemoteException e) {
                        Log.e("BadBehaviorActivity", "Can't call IActivityManager.setActivityController", e);
                    }
                    BadBehaviorActivity.this.startActivity(intent.putExtra("dummy", true));
                }
            });
        }
    }
}
