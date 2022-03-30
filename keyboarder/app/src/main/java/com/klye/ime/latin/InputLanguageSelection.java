package com.klye.ime.latin;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Typeface;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceGroup;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.TextView;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Arrays;

public class InputLanguageSelection extends PreferenceActivity {
    private ArrayList<Loc> mAvailableLanguages = new ArrayList();
    private String mSelectedLanguages;

    class CB extends CheckBoxPreference {
        public CB(Context context) {
            super(context);
        }

        protected void onBindView(View v) {
            super.onBindView(v);
            if (M.xtf) {
                Typeface tf = M.xtf(getTitle().charAt(0));
                if (tf != null) {
                    ((TextView) v.findViewById(16908310)).setTypeface(tf);
                }
            }
        }
    }

    private static class Loc implements Comparable<Object> {
        static Collator sCollator = Collator.getInstance();
        String label;
        String locale;

        public Loc(String label, String locale) {
            this.label = label;
            this.locale = locale;
        }

        public String toString() {
            return this.label;
        }

        public int compareTo(Object o) {
            return sCollator.compare(this.label, ((Loc) o).label);
        }
    }

    protected void onCreate(Bundle icicle) {
        super.onCreate(icicle);
    }

    private void init() {
        addPreferencesFromResource(R.xml.language_prefs);
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        M.xtf = sp.getBoolean("ms", M.xtf);
        M.xtf((Context) this);
        this.mSelectedLanguages = sp.getString(LatinIME.PREF_SELECTED_LANGUAGES, LatinIME.SELECTED_LANGUAGES);
        String[] languageList = this.mSelectedLanguages.split(",");
        this.mAvailableLanguages = getUniqueLocales();
        PreferenceGroup parent = getPreferenceScreen();
        for (int i = 0; i < this.mAvailableLanguages.size(); i++) {
            CheckBoxPreference pref = new CB(this);
            String locale = ((Loc) this.mAvailableLanguages.get(i)).locale;
            String s = locale.toString();
            pref.setTitle(M.dn(s));
            s = M.dne(s);
            pref.setChecked(isLocaleIn(locale, languageList));
            pref.setSummary(s + getString(M.hasDict(this, locale) ? R.string.has_dictionary : R.string.no_dictionary));
            parent.addPreference(pref);
        }
    }

    private boolean isLocaleIn(String lang, String[] list) {
        for (String equalsIgnoreCase : list) {
            if (lang.equalsIgnoreCase(equalsIgnoreCase)) {
                return true;
            }
        }
        return false;
    }

    protected void onResume() {
        super.onResume();
        init();
    }

    protected void onPause() {
        super.onPause();
        try {
            String checkedLanguages = "";
            PreferenceGroup parent = getPreferenceScreen();
            int count = parent.getPreferenceCount();
            for (int i = 3; i < count; i++) {
                if (((CheckBoxPreference) parent.getPreference(i)).isChecked()) {
                    checkedLanguages = checkedLanguages + ((Loc) this.mAvailableLanguages.get(i - 3)).locale + ",";
                }
            }
            if (checkedLanguages.length() < 1) {
                checkedLanguages = null;
            }
            Editor editor = PreferenceManager.getDefaultSharedPreferences(this).edit();
            editor.putString(LatinIME.PREF_SELECTED_LANGUAGES, checkedLanguages);
            SharedPreferencesCompat.apply(editor);
        } catch (Throwable e) {
            e.printStackTrace();
        }
        finish();
    }

    ArrayList<Loc> getUniqueLocales() {
        ArrayList<Loc> uniqueLocales = new ArrayList();
        int origSize = M.wl.length;
        Loc[] preprocess = new Loc[origSize];
        int i = 0;
        int finalSize = 0;
        while (i < origSize) {
            String s = M.wl[i];
            int finalSize2 = finalSize + 1;
            preprocess[finalSize] = new Loc(M.dn(s), s);
            i++;
            finalSize = finalSize2;
        }
        Arrays.sort(preprocess);
        for (i = 0; i < finalSize; i++) {
            uniqueLocales.add(preprocess[i]);
        }
        return uniqueLocales;
    }
}
