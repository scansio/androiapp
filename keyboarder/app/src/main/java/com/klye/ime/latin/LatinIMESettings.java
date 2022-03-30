package com.klye.ime.latin;

import android.annotation.TargetApi;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.DialogInterface.OnDismissListener;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
import android.preference.PreferenceGroup;
import android.speech.SpeechRecognizer;
import android.text.AutoText;
import android.util.Log;
import com.klye.ime.voice.SettingsUtil;
import java.util.Locale;

@TargetApi(8)
public class LatinIMESettings extends PreferenceActivity implements OnSharedPreferenceChangeListener, OnPreferenceClickListener, OnDismissListener {
    private static final String DEBUG_MODE_KEY = "debug_mode";
    private static final String PREDICTION_SETTINGS_KEY = "prediction_settings";
    static final String PREF_SETTINGS_KEY = "settings_key";
    private static final String QUICK_FIXES_KEY = "quick_fixes";
    private static final String TAG = "LatinIMESettings";
    private static final int VOICE_INPUT_CONFIRM_DIALOG = 0;
    private static final String VOICE_SETTINGS_KEY = "voice_mode";
    private CheckBoxPreference mDebugMode;
    private boolean mOkClicked = false;
    private CheckBoxPreference mQuickFixes;
    private ListPreference mSettingsKeyPreference;
    private String mVoiceModeOff;
    private boolean mVoiceOn;
    private ListPreference mVoicePreference;

    protected void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        addPreferencesFromResource(R.xml.prefs);
        this.mQuickFixes = (CheckBoxPreference) findPreference(QUICK_FIXES_KEY);
        this.mVoicePreference = (ListPreference) findPreference(VOICE_SETTINGS_KEY);
        this.mSettingsKeyPreference = (ListPreference) findPreference(PREF_SETTINGS_KEY);
        this.mDebugMode = (CheckBoxPreference) findPreference(DEBUG_MODE_KEY);
        updateDebugMode();
        SharedPreferences prefs = getPreferenceManager().getSharedPreferences();
        prefs.registerOnSharedPreferenceChangeListener(this);
        this.mVoiceModeOff = getString(R.string.voice_mode_off);
        this.mVoiceOn = !prefs.getString(VOICE_SETTINGS_KEY, this.mVoiceModeOff).equals(this.mVoiceModeOff);
        findPreference("utext").setOnPreferenceClickListener(this);
    }

    private void updateDebugMode() {
        if (this.mDebugMode != null) {
            boolean isDebugMode = this.mDebugMode.isChecked();
            String version = "";
            try {
                version = "Version " + getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
            } catch (NameNotFoundException e) {
                Log.e(TAG, "Could not find version info.");
            }
            this.mDebugMode.setTitle(getResources().getString(R.string.prefs_debug_mode));
            this.mDebugMode.setSummary(version);
        }
    }

    protected void onResume() {
        super.onResume();
        if (AutoText.getSize(getListView()) < 1) {
            ((PreferenceGroup) findPreference(PREDICTION_SETTINGS_KEY)).removePreference(this.mQuickFixes);
        }
        boolean b = false;
        try {
            b = SpeechRecognizer.isRecognitionAvailable(this);
        } catch (Throwable th) {
        }
        if (LatinIME.VOICE_INSTALLED && b) {
            updateVoiceModeSummary();
        } else {
            getPreferenceScreen().removePreference(this.mVoicePreference);
        }
        updateSettingsKeySummary();
    }

    protected void onDestroy() {
        getPreferenceManager().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
        super.onDestroy();
    }

    public void onSharedPreferenceChanged(SharedPreferences prefs, String key) {
        if (!(!key.equals(VOICE_SETTINGS_KEY) || this.mVoiceOn || prefs.getString(VOICE_SETTINGS_KEY, this.mVoiceModeOff).equals(this.mVoiceModeOff))) {
            showVoiceConfirmation();
        }
        this.mVoiceOn = !prefs.getString(VOICE_SETTINGS_KEY, this.mVoiceModeOff).equals(this.mVoiceModeOff);
        updateVoiceModeSummary();
        updateSettingsKeySummary();
    }

    private void updateSettingsKeySummary() {
        this.mSettingsKeyPreference.setSummary(getResources().getStringArray(R.array.settings_key_modes)[this.mSettingsKeyPreference.findIndexOfValue(this.mSettingsKeyPreference.getValue())]);
        us1("lpe", R.array.lpe);
        us("t");
        us("b");
        us("l");
        us("r");
        us("tr");
        us("br");
        us("tl");
        us("bl");
    }

    private void us(String s) {
        us1(s, R.array.gest);
    }

    private void us1(String s, int id) {
        try {
            ListPreference p = (ListPreference) findPreference(s);
            p.setSummary(getResources().getStringArray(id)[p.findIndexOfValue(p.getValue())]);
        } catch (Throwable th) {
        }
    }

    private void showVoiceConfirmation() {
        this.mOkClicked = false;
        showDialog(0);
    }

    private void updateVoiceModeSummary() {
        this.mVoicePreference.setSummary(getResources().getStringArray(R.array.voice_input_modes_summary)[this.mVoicePreference.findIndexOfValue(this.mVoicePreference.getValue())]);
    }

    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case 0:
                OnClickListener listener = new OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        if (whichButton == -2) {
                            LatinIMESettings.this.mVoicePreference.setValue(LatinIMESettings.this.mVoiceModeOff);
                        } else if (whichButton == -1) {
                            LatinIMESettings.this.mOkClicked = true;
                        }
                        LatinIMESettings.this.updateVoicePreference();
                    }
                };
                Builder builder = new Builder(this).setTitle(R.string.voice_warning_title).setPositiveButton(17039370, listener).setNegativeButton(17039360, listener);
                if (LatinIME.newArrayList(SettingsUtil.getSettingsString(getContentResolver(), SettingsUtil.LATIN_IME_VOICE_INPUT_SUPPORTED_LOCALES, LatinIME.DEFAULT_VOICE_INPUT_SUPPORTED_LOCALES).split("\\s+")).contains(Locale.getDefault().toString())) {
                    builder.setMessage(getString(R.string.voice_warning_may_not_understand) + "\n\n" + getString(R.string.voice_hint_dialog_message));
                } else {
                    builder.setMessage(getString(R.string.voice_warning_locale_not_supported) + "\n\n" + getString(R.string.voice_warning_may_not_understand) + "\n\n" + getString(R.string.voice_hint_dialog_message));
                }
                Dialog dialog = builder.create();
                dialog.setOnDismissListener(this);
                return dialog;
            default:
                Log.e(TAG, "unknown dialog " + id);
                return null;
        }
    }

    public void onDismiss(DialogInterface dialog) {
        if (!this.mOkClicked) {
            this.mVoicePreference.setValue(this.mVoiceModeOff);
        }
    }

    private void updateVoicePreference() {
        if (!this.mVoicePreference.getValue().equals(this.mVoiceModeOff)) {
        }
    }

    public boolean onPreferenceClick(Preference preference) {
        M.ut(this);
        return true;
    }
}
