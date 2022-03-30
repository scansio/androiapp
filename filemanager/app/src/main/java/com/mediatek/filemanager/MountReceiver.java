package com.mediatek.filemanager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import com.mediatek.filemanager.utils.LogUtils;
import com.mediatek.filemanager.utils.OptionsUtils;
import java.util.ArrayList;
import java.util.Iterator;

public class MountReceiver extends BroadcastReceiver {
    private static final String INTENT_SD_SWAP = "com.mediatek.SD_SWAP";
    private static final String TAG = "MountReceiver";
    private final ArrayList<MountListener> mMountListenerList = new ArrayList();
    private final MountPointManager mMountPointManager = MountPointManager.getInstance();

    public interface MountListener {
        void onEjected(String str);

        void onMounted(String str);

        void onSdSwap();

        void onUnMounted(String str);
    }

    public void registerMountListener(MountListener listener) {
        this.mMountListenerList.add(listener);
    }

    public void unregisterMountListener(MountListener listener) {
        this.mMountListenerList.remove(listener);
    }

    public void onReceive(Context context, Intent intent) {
        Iterator i$;
        String action = intent.getAction();
        String mountPoint = null;
        Uri mountPointUri = intent.getData();
        if (mountPointUri != null) {
            mountPoint = mountPointUri.getPath();
        }
        LogUtils.d(TAG, "onReceive: " + action + " mountPoint: " + mountPoint);
        if (INTENT_SD_SWAP.equals(action)) {
            synchronized (this) {
                i$ = this.mMountListenerList.iterator();
                while (i$.hasNext()) {
                    MountListener listener = (MountListener) i$.next();
                    LogUtils.d(TAG, "onReceive,handle SD_SWAP ");
                    listener.onSdSwap();
                }
            }
        }
        if (mountPoint != null && mountPointUri != null) {
            if ("android.intent.action.MEDIA_MOUNTED".equals(action)) {
                i$ = this.mMountListenerList.iterator();
                while (i$.hasNext()) {
                    ((MountListener) i$.next()).onMounted(mountPoint);
                }
            } else if ("android.intent.action.MEDIA_UNMOUNTED".equals(action)) {
                if (this.mMountPointManager.changeMountState(mountPoint, Boolean.valueOf(false))) {
                    i$ = this.mMountListenerList.iterator();
                    while (i$.hasNext()) {
                        ((MountListener) i$.next()).onUnMounted(mountPoint);
                    }
                }
            } else if ("android.intent.action.MEDIA_EJECT".equals(action)) {
                i$ = this.mMountListenerList.iterator();
                while (i$.hasNext()) {
                    ((MountListener) i$.next()).onEjected(mountPoint);
                }
            }
        }
    }

    public static MountReceiver registerMountReceiver(Context context) {
        MountReceiver receiver = new MountReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.intent.action.MEDIA_MOUNTED");
        intentFilter.addAction("android.intent.action.MEDIA_UNMOUNTED");
        intentFilter.addAction("android.intent.action.MEDIA_EJECT");
        intentFilter.addDataScheme("file");
        context.registerReceiver(receiver, intentFilter);
        if (OptionsUtils.isMtkSDSwapSurpported()) {
            IntentFilter intentFilterSDSwap = new IntentFilter();
            intentFilterSDSwap.addAction(INTENT_SD_SWAP);
            context.registerReceiver(receiver, intentFilterSDSwap);
        }
        return receiver;
    }
}
