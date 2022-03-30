package com.klye.ime.latin;

import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.preference.PreferenceManager;
import android.view.InflateException;
import com.klye.ime.latin.LatinIMEUtil.GCUtils;
import java.lang.ref.SoftReference;
import java.util.HashMap;
import java.util.Locale;

public class KeyboardSwitcher implements OnSharedPreferenceChangeListener {
    private static final int[] ALPHABET_MODES = new int[]{R.id.mode_normal, R.id.mode_webentry, R.id.mode_normal_with_settings_key, R.id.mode_webentry_with_settings_key};
    private static final int CHAR_THEME_COLOR_BLACK = 1;
    private static final int CHAR_THEME_COLOR_WHITE = 0;
    private static final int[] KBD_PHONE = new int[]{R.xml.kbd_phone, R.xml.kbd_phone_black};
    private static final int[] KBD_PHONE_SYMBOLS = new int[]{R.xml.kbd_phone_symbols, R.xml.kbd_phone_symbols_black};
    public static final int KEYBOARDMODE_NORMAL = 2131689500;
    public static final int KEYBOARDMODE_NORMAL_WITH_SETTINGS_KEY = 2131689501;
    public static final int KEYBOARDMODE_SYMBOLS = 2131689502;
    public static final int KEYBOARDMODE_SYMBOLS_WITH_SETTINGS_KEY = 2131689503;
    public static final int KEYBOARDMODE_WEB = 2131689504;
    public static final int KEYBOARDMODE_WEB_WITH_SETTINGS_KEY = 2131689505;
    public static final int MODE_EMAIL = 5;
    public static final int MODE_IM = 6;
    public static final int MODE_NONE = 0;
    public static final int MODE_PHONE = 3;
    public static final int MODE_SYMBOLS = 2;
    public static final int MODE_TEXT = 1;
    public static final int MODE_URL = 4;
    public static final int MODE_WEB = 7;
    private static final int SETTINGS_KEY_MODE_ALWAYS_SHOW = 2131296599;
    private static final int SETTINGS_KEY_MODE_AUTO = 2131296600;
    private static final int SYMBOLS_MODE_STATE_BEGIN = 1;
    private static final int SYMBOLS_MODE_STATE_NONE = 0;
    private static final int SYMBOLS_MODE_STATE_SYMBOL = 2;
    private static final int[] THEMES = new int[]{R.layout.input_gingerbread, R.layout.input_basic_highcontrast, R.layout.input_stone_normal, R.layout.input_stone_bold, R.layout.input_basic, R.layout.thm_black, R.layout.thm_trans};
    private boolean mHasSettingsKey;
    private boolean mHasVoice;
    private int mImeOptions;
    private String mInputLocale;
    private boolean mIsAutoCompletionActive;
    private int mIsSymbols;
    private final HashMap<KID, SoftReference<LatinKeyboard>> mKeyboards;
    private LanguageSwitcher mLanguageSwitcher;
    private int mLastDisplayWidth;
    private int mMode = 0;
    private int mPreferSymbols;
    private KID mSymbolsId;
    private int mSymbolsModeState = 0;
    private KID mSymbolsShiftedId;
    private boolean mVoiceOnPrimary;

    public KeyboardSwitcher(LatinIME ims) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(ims);
        updateSettingsKeyState(prefs);
        prefs.registerOnSharedPreferenceChangeListener(this);
        this.mKeyboards = new HashMap();
        this.mSymbolsId = makeId(false, R.xml.kbd_symbols);
        this.mSymbolsShiftedId = makeId(false, R.xml.kbd_symbols_shift);
    }

    public void setLanguageSwitcher(LanguageSwitcher languageSwitcher) {
        this.mLanguageSwitcher = languageSwitcher;
        this.mInputLocale = this.mLanguageSwitcher.getInputLanguage();
    }

    private KID makeId(boolean hasVoice, int xml) {
        return new KID(xml, this.mHasSettingsKey ? R.id.mode_symbols_with_settings_key : R.id.mode_symbols, false, hasVoice);
    }

    public void makeKeyboards(boolean forceCreate) {
        boolean z;
        boolean z2 = true;
        if (!this.mHasVoice || this.mVoiceOnPrimary) {
            z = false;
        } else {
            z = true;
        }
        this.mSymbolsId = makeId(z, R.xml.kbd_symbols);
        if (!this.mHasVoice || this.mVoiceOnPrimary) {
            z2 = false;
        }
        this.mSymbolsShiftedId = makeId(z2, R.xml.kbd_symbols_shift);
        if (forceCreate) {
            this.mKeyboards.clear();
        }
        int displayWidth = M.mIme.getMaxWidth();
        if (displayWidth != this.mLastDisplayWidth) {
            this.mLastDisplayWidth = displayWidth;
            if (!forceCreate) {
                this.mKeyboards.clear();
            }
        }
    }

    public void setVoiceMode(boolean enableVoice, boolean voiceOnPrimary) {
        if (!(enableVoice == this.mHasVoice && voiceOnPrimary == this.mVoiceOnPrimary)) {
            this.mKeyboards.clear();
        }
        this.mHasVoice = enableVoice;
        this.mVoiceOnPrimary = voiceOnPrimary;
        setKeyboardMode(this.mMode, this.mImeOptions, this.mHasVoice, this.mIsSymbols);
    }

    private boolean hasVoiceButton(boolean isSymbols) {
        return this.mHasVoice && this.mVoiceOnPrimary && !isSymbols;
    }

    public void setKeyboardMode(int mode, int imeOptions, boolean enableVoice) {
        int i = 0;
        this.mSymbolsModeState = 0;
        if (M.kid != null && M.kid.is(R.xml.kbd_edit)) {
            i = 2;
        } else if (mode == 2) {
            i = 1;
        }
        this.mPreferSymbols = i;
        if (mode == 2 || !M.dk) {
            mode = 1;
        }
        try {
            setKeyboardMode(mode, imeOptions, enableVoice, this.mPreferSymbols);
        } catch (RuntimeException e) {
        }
    }

    private void setKeyboardMode(int mode, int imeOptions, boolean enableVoice, int isSymbols) {
        if (M.iv != null) {
            this.mMode = mode;
            this.mImeOptions = imeOptions;
            if (enableVoice != this.mHasVoice) {
                setVoiceMode(enableVoice, this.mVoiceOnPrimary);
            }
            this.mIsSymbols = isSymbols;
            M.iv.setPreviewEnabled(M.mIme.getPopupOn());
            KID id = getKeyboardId(mode, imeOptions);
            M.kid(id);
            LatinKeyboard keyboard = getKeyboard(id);
            if (mode == 3) {
                M.iv.setPhoneKeyboard(keyboard);
            }
            M.iv.setKeyboard(keyboard, 0);
            if (M.kid.is(R.xml.kbd_hw)) {
                M.hw(M.iv, M.dm.widthPixels, M.iv.mKeys[0].height);
            }
            keyboard.setShifted(false);
            keyboard.setShiftLocked(keyboard.isShiftLocked());
            keyboard.setImeOptions(M.mIme.getResources(), this.mMode, imeOptions);
            keyboard.setColorOfSymbolIcons(this.mIsAutoCompletionActive, isBlackSym());
            updateSettingsKeyState(PreferenceManager.getDefaultSharedPreferences(M.mIme));
        }
    }

    private LatinKeyboard getKeyboard(KID id) {
        LatinKeyboard keyboard;
        SoftReference<LatinKeyboard> ref = (SoftReference) this.mKeyboards.get(id);
        if (ref == null) {
            keyboard = null;
        } else {
            keyboard = (LatinKeyboard) ref.get();
        }
        if (keyboard == null) {
            Resources orig = M.mIme.getResources();
            Configuration conf = orig.getConfiguration();
            Locale saveLocale = conf.locale;
            conf.locale = M.lcl(M.mergeKB(this.mInputLocale));
            orig.updateConfiguration(conf, null);
            M.ial(M.mIme);
            keyboard = new LatinKeyboard(M.mIme, id.mXml, id.mKeyboardMode);
            keyboard.setVoiceMode(hasVoiceButton(id.mXml == R.xml.kbd_symbols), this.mHasVoice);
            keyboard.setLanguageSwitcher(this.mLanguageSwitcher, this.mIsAutoCompletionActive, isBlackSym());
            if (id.mEnableShiftLock) {
                keyboard.enableShiftLock();
            }
            this.mKeyboards.put(id, new SoftReference(keyboard));
            conf.locale = saveLocale;
            orig.updateConfiguration(conf, null);
        }
        return keyboard;
    }

    private KID getKeyboardId(int mode, int imeOptions) {
        boolean z;
        boolean sm1;
        if (this.mIsSymbols > 0) {
            z = true;
        } else {
            z = false;
        }
        boolean hasVoice = hasVoiceButton(z);
        int charColorId = getCharColorId();
        if (M.sm3 && (M.isLatinC || M.cy() != -1 || M.mLC == M.ko || M.el() || M.mLC == M.ag || "WC".indexOf(M.zt) != -1)) {
            sm1 = true;
        } else {
            sm1 = false;
        }
        int keyboardRowsResId = M.hw() ? R.xml.kbd_hw : (M.mLC == M.ko && M.km() == 1) ? R.xml.kbd_ko : (M.t9() || M.zt == 66 || (M.mLC == M.ko && M.km() == 3)) ? R.xml.kbd_t9 : M.qw_er() ? (!sm1 || M.cy() == -1) ? R.xml.kbd_qw_er_ty : R.xml.kbd_cyph : M.dvlh() ? R.xml.kbd_lh : M.dvrh() ? R.xml.kbd_rh : M.colemak() ? sm1 ? R.xml.kbd_clmks : R.xml.kbd_colemak : M.dvorak() ? sm1 ? R.xml.kbd_dvrks : R.xml.kbd_dvorak : M.neo() ? R.xml.kbd_neo : M.bepo() ? R.xml.kbd_bepo : M.dk(sm1);
        M.acp();
        switch (this.mIsSymbols) {
            case 1:
                if (mode == 3) {
                    return new KID(KBD_PHONE_SYMBOLS[charColorId], hasVoice);
                }
                return new KID(R.xml.kbd_symbols, this.mHasSettingsKey ? R.id.mode_symbols_with_settings_key : R.id.mode_symbols, false, hasVoice);
            case 2:
                return new KID(R.xml.kbd_edit, R.id.mode_normal, true, hasVoice);
            case 3:
                return new KID(KBD_PHONE[charColorId], R.id.mode_normal_with_settings_key, true, hasVoice);
            case 5:
                return new KID(R.xml.kbd_ding, R.id.mode_normal, false, false);
            case 6:
                return new KID(M.emj, R.id.mode_normal, false, false);
            case 7:
                return new KID(R.xml.kbd_smiley, R.id.mode_normal, false, false);
            case R.styleable.LatinKeyboardBaseView_keyHysteresisDistance /*8*/:
                return new KID(R.xml.kbd_hw, R.id.mode_normal, true, true);
            default:
                switch (mode) {
                    case 0:
                    case 1:
                    case 4:
                    case 5:
                    case 6:
                    case 7:
                        return new KID(keyboardRowsResId, this.mHasSettingsKey ? R.id.mode_normal_with_settings_key : R.id.mode_normal, true, hasVoice);
                    case 2:
                        return new KID(R.xml.kbd_symbols, this.mHasSettingsKey ? R.id.mode_symbols_with_settings_key : R.id.mode_symbols, false, hasVoice);
                    case 3:
                        return new KID(KBD_PHONE[charColorId], R.id.mode_normal, true, hasVoice);
                    default:
                        return null;
                }
        }
    }

    public int getKeyboardMode() {
        return this.mMode;
    }

    public boolean isAlphabetMode() {
        if (isEditPad() || this.mIsSymbols == 4) {
            return true;
        }
        if (M.kid == null) {
            return false;
        }
        int currentMode = M.kid.mKeyboardMode;
        for (int valueOf : ALPHABET_MODES) {
            if (currentMode == Integer.valueOf(valueOf).intValue()) {
                return true;
            }
        }
        return false;
    }

    public void setShifted(boolean s) {
        if (M.iv != null) {
            M.iv.setShifted(s);
        }
    }

    public void setShiftLocked(boolean shiftLocked) {
        if (M.iv != null) {
            M.iv.setShiftLocked(shiftLocked);
            if (!shiftLocked) {
                M.iv.setShifted(false);
            }
        }
    }

    public void toggleShift() {
        if (!isAlphabetMode()) {
            if (M.kid.equals(this.mSymbolsId) || !M.kid.equals(this.mSymbolsShiftedId)) {
                shift(this.mSymbolsShiftedId, true);
            } else {
                shift(this.mSymbolsId, false);
            }
        }
    }

    private void shift(KID i, boolean set) {
        LatinKeyboard lk = getKeyboard(i);
        M.kid = i;
        M.iv.setKeyboard(lk, 0);
        lk.enableShiftLock();
        if (set) {
            lk.setShiftLocked(true);
        } else {
            lk.setShifted(false);
        }
        lk.setImeOptions(M.mIme.getResources(), this.mMode, this.mImeOptions);
    }

    public void toggleSymbols(int m) {
        int i = this.mMode;
        int i2 = this.mImeOptions;
        boolean z = this.mHasVoice;
        if (m == -1) {
            if (this.mIsSymbols > 0) {
                m = 0;
            } else {
                m = 1;
            }
        }
        setKeyboardMode(i, i2, z, m);
        if (this.mIsSymbols <= 0 || this.mPreferSymbols > 0) {
            this.mSymbolsModeState = 0;
        } else {
            this.mSymbolsModeState = 1;
        }
    }

    public boolean hasDistinctMultitouch() {
        return (M.mAltCaps || M.iv == null || !M.iv.hasDistinctMultitouch()) ? false : true;
    }

    public boolean onKey(int key) {
        if (M.asb) {
            switch (this.mSymbolsModeState) {
                case 1:
                    if (!(key == 32 || key == 10 || key <= 0)) {
                        this.mSymbolsModeState = 2;
                        break;
                    }
                case 2:
                    if (key == 10 || key == 32) {
                        return true;
                    }
            }
        }
        return false;
    }

    public void recreateInputView() {
        changeLatinKeyboardView(M.tid, true);
    }

    private void changeLatinKeyboardView(int newLayout, boolean forceReset) {
        if (M.tid != newLayout || M.iv == null || forceReset) {
            if (M.iv != null) {
                M.iv.closing();
            }
            if (THEMES.length <= newLayout) {
                newLayout = 4;
            }
            GCUtils.getInstance().reset();
            boolean tryGC = true;
            for (int i = 0; i < 5 && tryGC; i++) {
                try {
                    M.iv = (LatinKeyboardView) M.mIme.getLayoutInflater().inflate(THEMES[newLayout], null);
                    M.iv.setOnKeyboardActionListener(M.mIme);
                    M.cbc = M.iv.getSymbolColorScheme() == 1 ? M.dkr(M.ltr(M.ltr(M.hlc)), 18, 20) : 0;
                    tryGC = false;
                } catch (OutOfMemoryError e) {
                    tryGC = GCUtils.getInstance().tryGCOrWait(M.tid + "," + newLayout, e);
                } catch (InflateException e2) {
                    tryGC = GCUtils.getInstance().tryGCOrWait(M.tid + "," + newLayout, e2);
                }
            }
            M.tid = newLayout;
        }
        M.mIme.mHandler.post(new Runnable() {
            public void run() {
                try {
                    if (M.iv != null) {
                        M.mIme.setInputView(M.iv);
                    }
                    M.mIme.updateInputViewShown();
                } catch (Throwable th) {
                }
            }
        });
    }

    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        this.mKeyboards.clear();
        if ("pref_keyboard_layout_20100902".equals(key)) {
            changeLatinKeyboardView(Integer.valueOf(sharedPreferences.getString(key, M.DEFAULT_LAYOUT_ID)).intValue(), true);
        } else if ("settings_key".equals(key)) {
            updateSettingsKeyState(sharedPreferences);
            recreateInputView();
        }
    }

    public boolean isBlackSym() {
        if (M.iv == null || M.iv.getSymbolColorScheme() != 1) {
            return false;
        }
        return true;
    }

    private int getCharColorId() {
        if (isBlackSym()) {
            return 1;
        }
        return 0;
    }

    public void onAutoCompletionStateChanged(boolean isAutoCompletion) {
        if (isAutoCompletion != this.mIsAutoCompletionActive && M.iv != null) {
            this.mIsAutoCompletionActive = isAutoCompletion;
            LatinKeyboard kb = (LatinKeyboard) M.iv.getKeyboard();
            if (kb != null) {
                M.iv.invalidateKey(kb.onAutoCompletionStateChanged(isAutoCompletion));
            }
        }
    }

    private void updateSettingsKeyState(SharedPreferences prefs) {
        Resources resources = M.mIme.getResources();
        String settingsKeyMode = prefs.getString("settings_key", "2");
        if (settingsKeyMode.equals(resources.getString(R.string.settings_key_mode_always_show)) || (settingsKeyMode.equals(resources.getString(R.string.settings_key_mode_auto)) && LatinIMEUtil.hasMultipleEnabledIMEs(M.mIme))) {
            this.mHasSettingsKey = true;
        } else {
            this.mHasSettingsKey = false;
        }
    }

    public boolean isEditPad() {
        return this.mIsSymbols == 2;
    }

    public void invalidate() {
        if (M.iv != null) {
            M.iv.invalidateAllKeys();
        }
    }
}
