package com.klye.ime.latin;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import java.util.Locale;

public class LanguageSwitcher {
    private int mCurrentIndex = 0;
    private String mDefaultInputLanguage;
    private LatinIME mIme;
    private String[] mSelectedLanguageArray;
    private String mSelectedLanguages;

    public LanguageSwitcher(LatinIME ime) {
        this.mIme = ime;
    }

    public int getLocaleCount() {
        return this.mSelectedLanguageArray.length;
    }

    public boolean loadLocales(SharedPreferences sp) {
        String selectedLanguages = sp.getString(LatinIME.PREF_SELECTED_LANGUAGES, M.lg);
        String currentLanguage = sp.getString(LatinIME.PREF_INPUT_LANGUAGE, LatinIME.CURR_LANGUAGES);
        if (selectedLanguages == null || selectedLanguages.length() < 1) {
            loadDefaults();
            return true;
        }
        selectedLanguages = selectedLanguages.replace("zh_HW,", "").replace("zh_HW", "");
        if (selectedLanguages.equals(this.mSelectedLanguages)) {
            return false;
        }
        this.mSelectedLanguageArray = selectedLanguages.split(",");
        this.mSelectedLanguages = selectedLanguages;
        set(currentLanguage);
        return true;
    }

    private int find(String currentLanguage) {
        if (currentLanguage != null) {
            int i = this.mSelectedLanguageArray.length;
            do {
                i--;
                if (this.mSelectedLanguageArray[i].equals(currentLanguage)) {
                    return i;
                }
            } while (i > 0);
        }
        return -1;
    }

    private void loadDefaults() {
        Locale mDefaultInputLocale = this.mIme.getResources().getConfiguration().locale;
        String country = mDefaultInputLocale.getCountry();
        this.mDefaultInputLanguage = mDefaultInputLocale.getLanguage() + (TextUtils.isEmpty(country) ? "" : "_" + country);
    }

    public String getInputLanguage() {
        try {
            if (getLocaleCount() == 0) {
                return this.mDefaultInputLanguage;
            }
            return this.mSelectedLanguageArray[this.mCurrentIndex];
        } catch (Throwable th) {
            return this.mDefaultInputLanguage;
        }
    }

    public String[] getEnabledLanguages() {
        return this.mSelectedLanguageArray;
    }

    public String getNextInputLocale() {
        if (getLocaleCount() == 0) {
            return this.mDefaultInputLanguage;
        }
        return this.mSelectedLanguageArray[(this.mCurrentIndex + 1) % this.mSelectedLanguageArray.length];
    }

    public String getPrevInputLocale() {
        if (getLocaleCount() == 0) {
            return this.mDefaultInputLanguage;
        }
        return this.mSelectedLanguageArray[((this.mCurrentIndex - 1) + this.mSelectedLanguageArray.length) % this.mSelectedLanguageArray.length];
    }

    public void reset() {
        this.mCurrentIndex = 0;
    }

    public void set(int i) {
        this.mCurrentIndex = i;
    }

    public void set(String s) {
        int i = find(s);
        if (i != -1) {
            set(i);
        }
    }

    public void next() {
        this.mCurrentIndex++;
        if (this.mCurrentIndex >= this.mSelectedLanguageArray.length) {
            this.mCurrentIndex = 0;
        }
    }

    public void prev() {
        this.mCurrentIndex--;
        if (this.mCurrentIndex < 0) {
            this.mCurrentIndex = this.mSelectedLanguageArray.length - 1;
        }
    }

    public void persist() {
        Editor editor = PreferenceManager.getDefaultSharedPreferences(this.mIme).edit();
        editor.putString(LatinIME.PREF_INPUT_LANGUAGE, getInputLanguage());
        SharedPreferencesCompat.apply(editor);
    }
}
