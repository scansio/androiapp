package com.klye.ime.voiceime;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.inputmethodservice.InputMethodService;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class VoiceRecognitionTrigger {
    private ImeTrigger mImeTrigger;
    private final InputMethodService mInputMethodService;
    private BroadcastReceiver mReceiver;
    private Trigger mTrigger = getTrigger();

    public interface Listener {
        void onVoiceImeEnabledStatusChange();
    }

    public VoiceRecognitionTrigger(InputMethodService inputMethodService) {
        this.mInputMethodService = inputMethodService;
    }

    private Trigger getTrigger() {
        if (ImeTrigger.isInstalled(this.mInputMethodService)) {
            return getImeTrigger();
        }
        return null;
    }

    private Trigger getImeTrigger() {
        if (this.mImeTrigger == null) {
            this.mImeTrigger = new ImeTrigger(this.mInputMethodService);
        }
        return this.mImeTrigger;
    }

    public boolean isInstalled() {
        return this.mTrigger != null;
    }

    public boolean isEnabled() {
        return isNetworkAvailable();
    }

    public void startVoiceRecognition() {
        startVoiceRecognition(null);
    }

    public void startVoiceRecognition(String language) {
        if (this.mTrigger != null) {
            this.mTrigger.startVoiceRecognition(language);
        }
    }

    public void onStartInputView() {
        if (this.mTrigger != null) {
            this.mTrigger.onStartInputView();
        }
        this.mTrigger = getTrigger();
    }

    private boolean isNetworkAvailable() {
        try {
            NetworkInfo info = ((ConnectivityManager) this.mInputMethodService.getSystemService("connectivity")).getActiveNetworkInfo();
            if (info == null || !info.isConnected()) {
                return false;
            }
            return true;
        } catch (SecurityException e) {
            return true;
        }
    }

    public void register(final Listener listener) {
        this.mReceiver = new BroadcastReceiver() {
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals("android.net.conn.CONNECTIVITY_CHANGE")) {
                    listener.onVoiceImeEnabledStatusChange();
                }
            }
        };
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        this.mInputMethodService.registerReceiver(this.mReceiver, filter);
    }

    public void unregister(Context context) {
        if (this.mReceiver != null) {
            this.mInputMethodService.unregisterReceiver(this.mReceiver);
            this.mReceiver = null;
        }
    }
}
