package com.klye.ime.latin;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.inputmethodservice.InputMethodService;
import android.inputmethodservice.InputMethodService.Insets;
import android.media.AudioManager;
import android.os.Build;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.speech.SpeechRecognizer;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.Window;
import android.view.WindowManager.LayoutParams;
import android.view.inputmethod.CompletionInfo;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.ExtractedText;
import android.view.inputmethod.ExtractedTextRequest;
import android.view.inputmethod.InputConnection;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;
import com.klye.ime.latin.EditingUtil.Range;
import com.klye.ime.latin.EditingUtil.SelectedWord;
import com.klye.ime.latin.Hints.Display;
import com.klye.ime.latin.LatinIMEUtil.GCUtils;
import com.klye.ime.latin.LatinKeyboardBaseView.OnKeyboardActionListener;
import com.klye.ime.latin.TextEntryState.State;
import com.klye.ime.voice.FieldContext;
import com.klye.ime.voice.VoiceInput;
import com.klye.ime.voice.VoiceInput.UiListener;
import com.klye.ime.voiceime.VoiceRecognitionTrigger;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class LatinIME extends InputMethodService implements OnKeyboardActionListener, UiListener, OnSharedPreferenceChangeListener {
    private static final int CPS_BUFFER_SIZE = 16;
    public static final String CURR_LANGUAGES = "en";
    static final boolean DEBUG = false;
    public static final String DEFAULT_VOICE_INPUT_SUPPORTED_LOCALES = "fr de zh_CN zh_TW zh_HK jp ko it es tr ru cs pl pt nl af zuen_US en_GB en_AU en_CA en_IE en_IN en_NZ en_SG en_ZA ";
    private static final int DELETE_ACCELERATE_AT = 20;
    static final boolean ENABLE_VOICE_BUTTON = true;
    static final int KEYCODE_ENTER = 10;
    static final int KEYCODE_PERIOD = 46;
    static final int KEYCODE_SPACE = 32;
    static final int KEYCODE_TAB = 9;
    private static final int MSG_EXIT = 5;
    private static final int MSG_UPDATE_OLD_SUGGESTIONS = 4;
    private static final int MSG_UPDATE_SHIFT_STATE = 2;
    private static final int MSG_UPDATE_SUGGESTIONS = 0;
    private static final int MSG_VOICE_RESULTS = 3;
    private static final boolean PERF_DEBUG = false;
    private static final String PREF_AUTO_CAP = "auto_cap";
    private static final String PREF_AUTO_COMPLETE = "auto_complete";
    private static final String PREF_AUTO_SPACE = "auto_space";
    private static final String PREF_HAS_USED_VOICE_INPUT = "has_used_voice_input";
    private static final String PREF_HAS_USED_VOICE_INPUT_UNSUPPORTED_LOCALE = "has_used_voice_input_unsupported_locale";
    public static final String PREF_INPUT_LANGUAGE = "input_language";
    private static final String PREF_POPUP_ON = "popup_on";
    private static final String PREF_QUICK_FIXES = "quick_fixes";
    private static final String PREF_RECORRECTION_ENABLED = "recorrection_enabled";
    public static final String PREF_SELECTED_LANGUAGES = "selected_languages";
    private static final String PREF_SHOW_SUGGESTIONS = "show_suggestions";
    private static final String PREF_SOUND_ON = "sound_on";
    private static final String PREF_VIBRATE_ON = "vibrate_on";
    private static final String PREF_VOICE_MODE = "voice_mode";
    private static final int QUICK_PRESS = 200;
    public static final String SELECTED_LANGUAGES = "en,fr,el,ko";
    static final boolean TRACE = false;
    static final boolean VOICE_INSTALLED = (!M.ecl() ? ENABLE_VOICE_BUTTON : false);
    private boolean mAfterVoiceInput;
    private AudioManager mAudioManager;
    private boolean mAutoCap;
    private boolean mAutoCorrectEnabled;
    private boolean mAutoCorrectOn;
    private boolean mAutoSpace;
    private CharSequence mBestWord;
    private final boolean mBigramSuggestionEnabled = false;
    private CandidateView mCandidateView;
    private LinearLayout mCandidateViewContainer;
    private boolean mCapsLock;
    private int mCommittedLength;
    private boolean mCompletionOn;
    private CompletionInfo[] mCompletions;
    private boolean mConfigurationChanging;
    private int mCorrectionMode;
    private int mCpsIndex;
    private long[] mCpsIntervals = new long[CPS_BUFFER_SIZE];
    private int mDeleteCount;
    private boolean mEnableVoice = ENABLE_VOICE_BUTTON;
    private boolean mEnableVoiceButton = ENABLE_VOICE_BUTTON;
    Handler mHandler = new Hd();
    private boolean mHasDictionary;
    private boolean mHasUsedVoiceInput;
    private boolean mHasUsedVoiceInputUnsupportedLocale;
    private Hints mHints;
    private boolean mImmediatelyAfterVoiceInput;
    private boolean mInputTypeNoAutoCorrect;
    private boolean mIsShowingHint;
    private boolean mJustAccepted;
    private boolean mJustAddedAutoSpace;
    private CharSequence mJustRevertedSeparator;
    KeyboardSwitcher mKeyboardSwitcher;
    public LanguageSwitcher mLanguageSwitcher;
    private long mLastCpsTime;
    private long mLastKeyTime;
    private int mLastSelectionEnd;
    private int mLastSelectionStart;
    private boolean mLocaleSupportedForVoiceInput;
    private AlertDialog mOptionsDialog;
    private int mOrientation;
    boolean mPopupOn;
    boolean mPredicting;
    private boolean mPredictionOn;
    private CharSequence mPrevWord2;
    private boolean mQuickFixes;
    private boolean mReCorrectionEnabled;
    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            LatinIME.this.updateRingerMode();
        }
    };
    private boolean mRecognizing;
    private boolean mRefreshKeyboardRequired;
    private boolean mResetRequired = false;
    private ModifierKeyState mShiftKeyState = new ModifierKeyState();
    private boolean mShowSuggestions;
    private boolean mShowingVoiceSuggestions;
    private boolean mSilentMode;
    private boolean mSoundOn;
    private Suggest mSuggest;
    private ModifierKeyState mSymbolKeyState = new ModifierKeyState();
    private boolean mVibrateOn;
    private VoiceInput mVoiceInput;
    private boolean mVoiceInputHighlighted;
    private boolean mVoiceOnPrimary;
    private VoiceRecognitionTrigger mVoiceRecognitionTrigger;
    private VoiceResults mVoiceResults = new VoiceResults(this, null);
    private AlertDialog mVoiceWarningDialog;
    WordComposer mWord = new WordComposer();
    private ArrayList<WordAlternatives> mWordHistory = new ArrayList();
    private Map<String, List<CharSequence>> mWordToSuggestions = new HashMap();
    private boolean samsungEmail = false;

    private static class Hd extends Handler {
        private Hd() {
        }

        /* synthetic */ Hd(AnonymousClass1 x0) {
            this();
        }

        public void handleMessage(Message msg) {
            try {
                switch (msg.what) {
                    case 0:
                        M.mIme.updateSuggestions();
                        return;
                    case 2:
                        M.mIme.updateShiftKeyState(M.mIme.getCurrentInputEditorInfo());
                        return;
                    case 3:
                        M.mIme.handleVoiceResults();
                        return;
                    case 4:
                        M.mIme.setOldSuggestions();
                        return;
                    case 5:
                        System.exit(0);
                        return;
                    default:
                        return;
                }
            } catch (Throwable e) {
                e.printStackTrace();
            }
            e.printStackTrace();
        }
    }

    public static abstract class WordAlternatives {
        protected CharSequence mChosenWord;

        public abstract List<CharSequence> getAlternatives();

        public abstract CharSequence getOriginalWord();

        public WordAlternatives(CharSequence chosenWord) {
            this.mChosenWord = chosenWord;
        }

        public int hashCode() {
            return this.mChosenWord.hashCode();
        }

        public CharSequence getChosenWord() {
            return this.mChosenWord;
        }
    }

    public class TypedWordAlternatives extends WordAlternatives {
        private WordComposer word;

        public TypedWordAlternatives(CharSequence chosenWord, WordComposer wordComposer) {
            super(chosenWord);
            this.word = wordComposer;
        }

        public CharSequence getOriginalWord() {
            return this.word.getTypedWord();
        }

        public List<CharSequence> getAlternatives() {
            return LatinIME.this.getTypedSuggestions(this.word);
        }
    }

    private class VoiceResults {
        Map<String, List<CharSequence>> alternatives;
        List<String> candidates;

        private VoiceResults() {
        }

        /* synthetic */ VoiceResults(LatinIME x0, AnonymousClass1 x1) {
            this();
        }
    }

    public void onCreate() {
        super.onCreate();
        M.mIme = this;
        M.res = getResources();
        Configuration conf = M.res.getConfiguration();
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        this.mLanguageSwitcher = new LanguageSwitcher(this);
        this.mLanguageSwitcher.loadLocales(sp);
        this.mKeyboardSwitcher = new KeyboardSwitcher(this);
        this.mKeyboardSwitcher.setLanguageSwitcher(this.mLanguageSwitcher);
        M.mSL = conf.locale.toString();
        M.dm = M.res.getDisplayMetrics();
        M.mm1 = (int) (((float) M.mm1) * M.dm.density);
        String inputLanguage = this.mLanguageSwitcher.getInputLanguage();
        if (inputLanguage == null) {
            inputLanguage = conf.locale.toString();
        }
        this.mReCorrectionEnabled = sp.getBoolean(PREF_RECORRECTION_ENABLED, getResources().getBoolean(R.bool.default_recorrection_enabled));
        M.ls(sp);
        if (M.zt != -1) {
            M.hw1();
        }
        GCUtils.getInstance().reset();
        boolean tryGC = ENABLE_VOICE_BUTTON;
        for (int i = 0; i < 5 && tryGC; i++) {
            try {
                initSuggest(inputLanguage);
                tryGC = false;
            } catch (OutOfMemoryError e) {
                tryGC = GCUtils.getInstance().tryGCOrWait(inputLanguage, e);
            }
        }
        this.mOrientation = conf.orientation;
        registerReceiver(this.mReceiver, new IntentFilter("android.media.RINGER_MODE_CHANGED"));
        if (VOICE_INSTALLED) {
            this.mVoiceInput = new VoiceInput(this, this);
            this.mHints = new Hints(this, new Display() {
                public void showHint(int viewResource) {
                    LatinIME.this.setCandidatesView(((LayoutInflater) LatinIME.this.getSystemService("layout_inflater")).inflate(viewResource, null));
                    LatinIME.this.scvs(LatinIME.ENABLE_VOICE_BUTTON);
                    LatinIME.this.mIsShowingHint = LatinIME.ENABLE_VOICE_BUTTON;
                }
            });
        }
        sp.registerOnSharedPreferenceChangeListener(this);
        this.mPredictionOn = M.smo();
    }

    static int[] getDictionary(Context c) {
        if (c == null) {
            return null;
        }
        ArrayList<Integer> dictionaries = new ArrayList();
        dictionaries.add(Integer.valueOf(c.getResources().getIdentifier("main", "raw", c.getPackageName())));
        int count = dictionaries.size();
        int[] dict = new int[count];
        for (int i = 0; i < count; i++) {
            dict[i] = ((Integer) dictionaries.get(i)).intValue();
        }
        return dict;
    }

    private void initSuggest(String locale) {
        M.cl(locale);
        Resources orig = getResources();
        Configuration conf = orig.getConfiguration();
        Locale saveLocale = conf.locale;
        conf.locale = M.lcl(locale);
        orig.updateConfiguration(conf, orig.getDisplayMetrics());
        M.res = getResources();
        if (this.mSuggest != null) {
            this.mSuggest.close();
        }
        this.mQuickFixes = PreferenceManager.getDefaultSharedPreferences(this).getBoolean(PREF_QUICK_FIXES, ENABLE_VOICE_BUTTON);
        Context dictContext = M.getDictContext(this, M.mergeDict(locale));
        this.mSuggest = new Suggest(dictContext, getDictionary(dictContext), locale);
        updateAutoTextEnabled(saveLocale);
        ld();
        updateCorrectionMode();
        M.psbl();
        conf.locale = saveLocale;
        orig.updateConfiguration(conf, orig.getDisplayMetrics());
    }

    private void ld() {
        try {
            if (M.mLC == M.ja) {
                M.dicJa(this);
            } else {
                M.dicJa = null;
            }
            if (M.usd) {
                if (M.dicU != null) {
                    M.dicU.close();
                }
                M.dicU = new UserDictionary(this, M.mIL);
            }
            if (M.dicB != null) {
                M.dicB.close();
                M.dicB = null;
            }
            if (M.bgd && !M.dsw) {
                M.dicB = new UserBigramDictionary(this, this, M.mIL);
                if (M.en == M.mLC) {
                    M.dicB.addBigram("this", "is", 10);
                    M.dicB.addBigram("it", "is", 10);
                    M.dicB.addBigram("he", "is", 10);
                    M.dicB.addBigram("she", "is", 10);
                }
            }
            M.dicUt.init(this);
            if (M.dicC == null && M.rc) {
                M.dicC = new ContactsDictionary(this, 4);
            }
            if (M.lrn && M.usd) {
                if (M.dicA != null) {
                    M.dicA.close();
                }
                M.dicA = new AutoDictionary(this, this, M.mIL, 3);
            }
        } catch (Throwable th) {
        }
    }

    public void onDestroy() {
        if (M.dicU != null) {
            M.dicU.close();
        }
        if (M.dicJa != null) {
            M.dicJa.close();
        }
        if (M.dicC != null) {
            M.dicC.close();
        }
        unregisterReceiver(this.mReceiver);
        try {
            if (VOICE_INSTALLED && this.mVoiceInput != null) {
                this.mVoiceInput.destroy();
            }
        } catch (Throwable th) {
        }
        super.onDestroy();
    }

    public void onConfigurationChanged(Configuration conf) {
        String systemLocale = conf.locale.toString();
        if (!TextUtils.equals(systemLocale, M.mSL)) {
            M.mSL = systemLocale;
            if (this.mLanguageSwitcher != null) {
                this.mLanguageSwitcher.loadLocales(PreferenceManager.getDefaultSharedPreferences(this));
                toggleLanguage(ENABLE_VOICE_BUTTON, ENABLE_VOICE_BUTTON);
            } else {
                reloadKeyboards();
            }
        }
        if (conf.orientation != this.mOrientation) {
            fc();
            this.mOrientation = conf.orientation;
            reloadKeyboards();
        }
        this.mConfigurationChanging = ENABLE_VOICE_BUTTON;
        super.onConfigurationChanged(conf);
        if (this.mRecognizing) {
            switchToRecognitionStatusView();
        }
        this.mConfigurationChanging = false;
    }

    public View onCreateInputView() {
        if (M.hsk) {
            return null;
        }
        this.mKeyboardSwitcher.recreateInputView();
        this.mKeyboardSwitcher.makeKeyboards(ENABLE_VOICE_BUTTON);
        this.mKeyboardSwitcher.setKeyboardMode(1, 0, shouldShowVoiceButton());
        M.iv.setPreviewEnabled(this.mPopupOn);
        M.iv.setProximityCorrectionEnabled(ENABLE_VOICE_BUTTON);
        return M.iv;
    }

    public View onCreateCandidatesView() {
        this.mKeyboardSwitcher.makeKeyboards(ENABLE_VOICE_BUTTON);
        this.mCandidateViewContainer = (LinearLayout) getLayoutInflater().inflate(R.layout.candidates, null);
        this.mCandidateView = (CandidateView) this.mCandidateViewContainer.findViewById(R.id.candidates);
        this.mCandidateView.setService(this);
        return this.mCandidateViewContainer;
    }

    public void onStartInput(EditorInfo ei, boolean restarting) {
        super.onStartInput(ei, restarting);
        if (!restarting) {
            M.meta.st = 0;
        }
        osi(ei);
    }

    private void osi(EditorInfo ei) {
        boolean z = ENABLE_VOICE_BUTTON;
        this.mWord.reset();
        loadSettings();
        if (this.mResetRequired) {
            M.ex();
        }
        if (this.mRefreshKeyboardRequired) {
            this.mRefreshKeyboardRequired = false;
            toggleLanguage(ENABLE_VOICE_BUTTON, ENABLE_VOICE_BUTTON);
        }
        this.mKeyboardSwitcher.makeKeyboards(false);
        TextEntryState.newSession(this);
        int variation = ei.inputType & 4080;
        this.mEnableVoiceButton = shouldShowVoiceButton();
        boolean enableVoiceButton = (this.mEnableVoiceButton && this.mEnableVoice) ? ENABLE_VOICE_BUTTON : false;
        this.mAfterVoiceInput = false;
        this.mImmediatelyAfterVoiceInput = false;
        this.mShowingVoiceSuggestions = false;
        this.mVoiceInputHighlighted = false;
        this.mInputTypeNoAutoCorrect = false;
        String pn = ei.packageName;
        boolean z2 = ((this.mShowSuggestions || M.smo()) && M.na(pn)) ? ENABLE_VOICE_BUTTON : false;
        this.mPredictionOn = z2;
        boolean rctdbug = (M.na(pn, new String[]{"com.whatsapp", "com.jb.gosms"}) || VERSION.SDK_INT >= 9) ? false : ENABLE_VOICE_BUTTON;
        z2 = (rctdbug || !(M.na(pn, new String[]{"com.android.email", "com.android.mms"}) || Build.MANUFACTURER.indexOf("samsung") == -1)) ? ENABLE_VOICE_BUTTON : false;
        this.samsungEmail = z2;
        this.mCompletionOn = false;
        this.mCompletions = null;
        this.mCapsLock = false;
        switch (ei.inputType & 15) {
            case 1:
                this.mKeyboardSwitcher.setKeyboardMode(1, ei.imeOptions, enableVoiceButton);
                if (variation == 128 || variation == 144) {
                    this.mPredictionOn = false;
                }
                if (variation == 32 || variation == 96) {
                    this.mAutoSpace = false;
                } else {
                    this.mAutoSpace &= 1;
                }
                if (variation == 32) {
                    z2 = (this.mPredictionOn && M.insf) ? ENABLE_VOICE_BUTTON : false;
                    this.mPredictionOn = z2;
                    this.mKeyboardSwitcher.setKeyboardMode(5, ei.imeOptions, enableVoiceButton);
                } else if (variation == CPS_BUFFER_SIZE) {
                    z2 = (this.mPredictionOn && M.insf) ? ENABLE_VOICE_BUTTON : false;
                    this.mPredictionOn = z2;
                    this.mKeyboardSwitcher.setKeyboardMode(4, ei.imeOptions, enableVoiceButton);
                } else if (variation == 64) {
                    this.mKeyboardSwitcher.setKeyboardMode(6, ei.imeOptions, enableVoiceButton);
                } else if (variation == 176) {
                    z2 = (this.mPredictionOn && M.insf) ? ENABLE_VOICE_BUTTON : false;
                    this.mPredictionOn = z2;
                } else if (variation == 160) {
                    this.mKeyboardSwitcher.setKeyboardMode(7, ei.imeOptions, enableVoiceButton);
                    if ((ei.inputType & 32768) == 0) {
                        this.mInputTypeNoAutoCorrect = ENABLE_VOICE_BUTTON;
                    }
                }
                if (!((ei.inputType & 524288) == 0 || M.insf)) {
                    z2 = (this.mPredictionOn && M.insf) ? ENABLE_VOICE_BUTTON : false;
                    this.mPredictionOn = z2;
                    this.mInputTypeNoAutoCorrect = ENABLE_VOICE_BUTTON;
                }
                if ((ei.inputType & 32768) != 0 || (ei.inputType & 131072) == 0) {
                }
                if ((ei.inputType & 65536) != 0) {
                    z2 = (this.mPredictionOn && M.insf) ? ENABLE_VOICE_BUTTON : false;
                    this.mPredictionOn = z2;
                    this.mCompletionOn = isFullscreenMode();
                    break;
                }
                break;
            case 2:
            case 3:
            case 4:
                this.mKeyboardSwitcher.setKeyboardMode(3, ei.imeOptions, enableVoiceButton);
                this.mPredictionOn = false;
                break;
            default:
                this.mKeyboardSwitcher.setKeyboardMode(1, ei.imeOptions, enableVoiceButton);
                break;
        }
        this.mPredicting = false;
        this.mDeleteCount = 0;
        this.mJustAddedAutoSpace = false;
        updateSuggestions();
        this.mHasDictionary = this.mSuggest.hasMainDictionary();
        updateCorrectionMode();
        if (!(this.mPredictionOn && (this.mCorrectionMode > 0 || this.mShowSuggestions || M.smo()))) {
            z = false;
        }
        this.mPredictionOn = z;
    }

    public void onStartInputView(EditorInfo ei, boolean restarting) {
        if (!M.cm()) {
            if (M.shc == 0) {
                M.sha = ENABLE_VOICE_BUTTON;
                M.shc = 4;
            } else {
                M.shc--;
            }
        }
        scvs();
        ss(M.psbl);
        osi(ei);
        if (M.nfs(ei.packageName)) {
            updateFullscreenMode();
        }
        M.nfs = false;
        if (ei.inputType == 0 && this.mPredictionOn) {
            if (M.nb(ei.packageName)) {
                this.mPredictionOn = false;
            } else {
                sendDownUpKeyEvents(62);
                sendDownUpKeyEvents(67);
            }
        }
        updateShiftKeyState(ei);
    }

    private void ad() {
        try {
            Editor ed;
            SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
            long now = System.currentTimeMillis();
            String key = "prevad1";
            long ran1 = now + ((30 + (now % 25)) * 86400000);
            long ran2 = now + ((100 + (now % 25)) * 86400000);
            long prev = sp.getLong("prevad1", 0);
            if (prev == 0) {
                prev = ran1;
                ed = sp.edit();
                ed.putLong("prevad1", ran1);
                ed.commit();
            }
            if (now > prev) {
                if (M.emjCT("kl.ime.oh") == null) {
                    M.noti(this, "Update available", M.hp1("a.html"));
                }
                ed = sp.edit();
                ed.putLong("prevad1", ran2);
                ed.commit();
            }
        } catch (Throwable th) {
        }
    }

    public void onFinishInput() {
        super.onFinishInput();
        this.mWord.reset();
        onAutoCompletionStateChanged(false);
        try {
            if (VOICE_INSTALLED && !this.mConfigurationChanging) {
                if (this.mAfterVoiceInput) {
                    this.mVoiceInput.flushAllTextModificationCounters();
                }
                this.mVoiceInput.cancel();
            }
            if (M.iv != null) {
                M.iv.closing();
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
        M.dicClose();
        scvs(false);
    }

    public void onFinishInputView(boolean finishingInput) {
        super.onFinishInputView(finishingInput);
        this.mHandler.removeMessages(0);
        this.mHandler.removeMessages(4);
        ad();
    }

    public void onUpdateExtractedText(int token, ExtractedText text) {
        super.onUpdateExtractedText(token, text);
        InputConnection ic = ic();
        if (this.mImmediatelyAfterVoiceInput || !this.mAfterVoiceInput || ic == null || this.mHints.showPunctuationHintIfNecessary(ic)) {
        }
        this.mImmediatelyAfterVoiceInput = false;
    }

    public void onUpdateSelection(int oldSelStart, int oldSelEnd, int newSelStart, int newSelEnd, int candidatesStart, int candidatesEnd) {
        super.onUpdateSelection(oldSelStart, oldSelEnd, newSelStart, newSelEnd, candidatesStart, candidatesEnd);
        if (candidatesStart == -1 && newSelEnd == 0) {
            ExtractedText et = gxt();
            if (!(et == null || et.text == null || et.text.length() != 0)) {
                this.mWord.reset();
                this.mPredicting = false;
            }
        }
        if (M.zt == -1 && M.ko != M.mLC && this.mShowSuggestions && !M.mt9()) {
            if (this.mPredicting && M.g233() && newSelEnd > candidatesEnd) {
                fc();
                return;
            }
            if (this.mAfterVoiceInput) {
                this.mVoiceInput.setCursorPos(newSelEnd);
                this.mVoiceInput.setSelectionSpan(newSelEnd - newSelStart);
            }
            if (((this.mWord.mTW.length() > 0 && this.mPredicting) || this.mVoiceInputHighlighted) && ((newSelStart != candidatesEnd || newSelEnd != candidatesEnd) && this.mLastSelectionStart != newSelStart)) {
                this.mWord.mTW.setLength(0);
                M.pw1 = null;
                this.mPrevWord2 = null;
                this.mPredicting = false;
                postUpdateSuggestions();
                TextEntryState.reset();
                this.mVoiceInputHighlighted = false;
            } else if (!(this.mPredicting || this.mJustAccepted)) {
                switch (TextEntryState.getState()) {
                    case ACCEPTED_DEFAULT:
                        TextEntryState.reset();
                        break;
                    case SPACE_AFTER_PICKED:
                        break;
                }
                this.mJustAddedAutoSpace = false;
            }
            postUpdateShiftKeyState();
            this.mLastSelectionStart = newSelStart;
            this.mLastSelectionEnd = newSelEnd;
            if (this.mReCorrectionEnabled && !this.mJustAccepted && !this.samsungEmail && this.mKeyboardSwitcher != null && M.iv != null && M.iv.isShown() && isPredictionOn() && this.mJustRevertedSeparator == null && ((candidatesStart == candidatesEnd || newSelStart != oldSelStart || TextEntryState.isCorrecting()) && ((newSelStart < newSelEnd - 1 || !this.mPredicting) && !this.mVoiceInputHighlighted))) {
                if (isCursorTouchingWord() || this.mLastSelectionStart < this.mLastSelectionEnd) {
                    postUpdateOldSuggestions();
                } else {
                    abortCorrection(false);
                }
            }
            this.mJustAccepted = false;
        }
    }

    public void onExtractedTextClicked() {
        if (!this.mReCorrectionEnabled || !isPredictionOn()) {
            super.onExtractedTextClicked();
        }
    }

    public void onExtractedCursorMovement(int dx, int dy) {
        if (!this.mReCorrectionEnabled || !isPredictionOn()) {
            super.onExtractedCursorMovement(dx, dy);
        }
    }

    public void hideWindow() {
        onAutoCompletionStateChanged(false);
        if (this.mOptionsDialog != null && this.mOptionsDialog.isShowing()) {
            this.mOptionsDialog.dismiss();
            this.mOptionsDialog = null;
        }
        if (!this.mConfigurationChanging) {
            if (this.mVoiceWarningDialog != null && this.mVoiceWarningDialog.isShowing()) {
                this.mVoiceWarningDialog.dismiss();
                this.mVoiceWarningDialog = null;
            }
            if ((VOICE_INSTALLED & this.mRecognizing) != 0) {
                this.mVoiceInput.cancel();
            }
        }
        this.mWordToSuggestions.clear();
        this.mWordHistory.clear();
        try {
            super.hideWindow();
        } catch (Throwable th) {
        }
        TextEntryState.endSession();
    }

    public void onDisplayCompletions(CompletionInfo[] completions) {
        if (this.mCompletionOn) {
            this.mCompletions = completions;
            if (completions != null && completions.length != 0) {
                List<CharSequence> stringList = new ArrayList();
                int i = 0;
                while (true) {
                    if (i < (completions != null ? completions.length : 0)) {
                        CompletionInfo ci = completions[i];
                        if (ci != null) {
                            stringList.add(ci.getText());
                        }
                        i++;
                    } else {
                        setSuggestions(stringList, ENABLE_VOICE_BUTTON, ENABLE_VOICE_BUTTON, false);
                        this.mBestWord = this.mWord.getTypedWord();
                        setCandidatesViewShown(ENABLE_VOICE_BUTTON);
                        return;
                    }
                }
            }
        }
    }

    public void setCandidatesViewShown(boolean b) {
        boolean z = (!b || (!M.hard && (M.iv == null || !M.iv.isShown()))) ? false : ENABLE_VOICE_BUTTON;
        scvs(z);
    }

    private void scvs() {
        boolean z = ((isPredictionOn() && this.mShowSuggestions) || M.smo() || this.mCompletionOn) ? ENABLE_VOICE_BUTTON : false;
        scvs(z);
    }

    private void scvs(boolean b) {
        boolean z = (!b || (!M.hard && (M.iv == null || !M.iv.isShown()))) ? false : ENABLE_VOICE_BUTTON;
        super.setCandidatesViewShown(z);
    }

    public void onComputeInsets(Insets outInsets) {
        super.onComputeInsets(outInsets);
        if (!isFullscreenMode()) {
            outInsets.contentTopInsets = outInsets.visibleTopInsets;
        }
    }

    public boolean onEvaluateFullscreenMode() {
        boolean z = ENABLE_VOICE_BUTTON;
        if (this.mPredicting) {
            fc();
        }
        if (nfs()) {
            return false;
        }
        switch (M.fs()) {
            case 0:
                float displayHeight = (float) M.dm.heightPixels;
                float dimen = getResources().getDimension(R.dimen.max_height_for_fullscreen);
                if ((!M.ms() && displayHeight >= dimen) || (268435456 & getCurrentInputEditorInfo().imeOptions) != 0) {
                    z = false;
                }
                return z;
            case 1:
                return false;
            default:
                return ENABLE_VOICE_BUTTON;
        }
    }

    private boolean nfs() {
        EditorInfo iei = getCurrentInputEditorInfo();
        if (iei == null || (iei.imeOptions & 301989888) == 0) {
            return false;
        }
        return ENABLE_VOICE_BUTTON;
    }

    /* JADX WARNING: Missing block: B:61:0x010f, code:
            if (com.klye.ime.latin.M.zhType() != 90) goto L_0x0195;
     */
    /* JADX WARNING: Missing block: B:62:0x0111, code:
            r6 = ENABLE_VOICE_BUTTON;
     */
    /* JADX WARNING: Missing block: B:63:0x0112, code:
            com.klye.ime.latin.M.hk = ENABLE_VOICE_BUTTON;
     */
    /* JADX WARNING: Missing block: B:64:0x011a, code:
            if (com.klye.ime.latin.M.ko != com.klye.ime.latin.M.mLC) goto L_0x0198;
     */
    /* JADX WARNING: Missing block: B:65:0x011c, code:
            r3 = ENABLE_VOICE_BUTTON;
     */
    /* JADX WARNING: Missing block: B:67:0x011f, code:
            if (com.klye.ime.latin.M.hard != false) goto L_0x0125;
     */
    /* JADX WARNING: Missing block: B:68:0x0121, code:
            if (r6 != false) goto L_0x0125;
     */
    /* JADX WARNING: Missing block: B:69:0x0123, code:
            if (r3 == false) goto L_0x01bd;
     */
    /* JADX WARNING: Missing block: B:70:0x0125, code:
            r2 = com.klye.ime.latin.M.meta.b(8);
            r1 = com.klye.ime.latin.M.meta.b(4096);
            r4 = com.klye.ime.latin.M.meta.b(4);
     */
    /* JADX WARNING: Missing block: B:71:0x013f, code:
            if (com.klye.ime.latin.M.hwk != 2) goto L_0x019a;
     */
    /* JADX WARNING: Missing block: B:73:0x0143, code:
            if (r13 != 56) goto L_0x019a;
     */
    /* JADX WARNING: Missing block: B:74:0x0145, code:
            if (r0 == false) goto L_0x019a;
     */
    /* JADX WARNING: Missing block: B:75:0x0147, code:
            onKey(44, new int[]{44}, 0, 0);
     */
    /* JADX WARNING: Missing block: B:93:0x0195, code:
            r6 = false;
     */
    /* JADX WARNING: Missing block: B:94:0x0198, code:
            r3 = false;
     */
    /* JADX WARNING: Missing block: B:95:0x019a, code:
            if (r2 != false) goto L_0x01bd;
     */
    /* JADX WARNING: Missing block: B:96:0x019c, code:
            if (r1 != false) goto L_0x01bd;
     */
    /* JADX WARNING: Missing block: B:97:0x019e, code:
            if (r4 != false) goto L_0x01bd;
     */
    /* JADX WARNING: Missing block: B:98:0x01a0, code:
            if (r6 == false) goto L_0x01a8;
     */
    /* JADX WARNING: Missing block: B:100:0x01a6, code:
            if (tzy(r13, r14) != false) goto L_0x01ba;
     */
    /* JADX WARNING: Missing block: B:101:0x01a8, code:
            if (r3 == false) goto L_0x01b0;
     */
    /* JADX WARNING: Missing block: B:103:0x01ae, code:
            if (tko(r13, r14, r0) != false) goto L_0x01ba;
     */
    /* JADX WARNING: Missing block: B:105:0x01b2, code:
            if (com.klye.ime.latin.M.hard == false) goto L_0x01bd;
     */
    /* JADX WARNING: Missing block: B:107:0x01b8, code:
            if (translateKeyDown(r13, r14) == false) goto L_0x01bd;
     */
    /* JADX WARNING: Missing block: B:122:?, code:
            return ENABLE_VOICE_BUTTON;
     */
    /* JADX WARNING: Missing block: B:125:?, code:
            return false;
     */
    /* JADX WARNING: Missing block: B:129:?, code:
            return ENABLE_VOICE_BUTTON;
     */
    /* JADX WARNING: Missing block: B:130:?, code:
            return super.onKeyDown(r13, r14);
     */
    public boolean onKeyDown(int r13, android.view.KeyEvent r14) {
        /*
        r12 = this;
        r7 = 219; // 0xdb float:3.07E-43 double:1.08E-321;
        if (r13 <= r7) goto L_0x0006;
    L_0x0004:
        r7 = 0;
    L_0x0005:
        return r7;
    L_0x0006:
        r7 = 4;
        if (r13 != r7) goto L_0x0021;
    L_0x0009:
        r7 = com.klye.ime.latin.M.iv;
        if (r7 == 0) goto L_0x001f;
    L_0x000d:
        r7 = com.klye.ime.latin.M.iv;
        r7 = r7.handleBack();
        if (r7 == 0) goto L_0x0017;
    L_0x0015:
        r7 = 1;
        goto L_0x0005;
    L_0x0017:
        r7 = r12.isInputViewShown();
        if (r7 == 0) goto L_0x001f;
    L_0x001d:
        r7 = 1;
        goto L_0x0005;
    L_0x001f:
        r7 = 0;
        goto L_0x0005;
    L_0x0021:
        r7 = com.klye.ime.latin.M.dc;
        r8 = (char) r13;
        r7.c(r8);
        r7 = com.klye.ime.latin.M.mDebugMode;
        if (r7 == 0) goto L_0x0063;
    L_0x002b:
        r7 = com.klye.ime.latin.M.me;
        if (r7 == 0) goto L_0x0063;
    L_0x002f:
        r7 = new java.lang.StringBuilder;
        r7.<init>();
        r8 = "\nKeyDown KeyCode:";
        r7 = r7.append(r8);
        r7 = r7.append(r13);
        r8 = " ScanCode:";
        r7 = r7.append(r8);
        r8 = r14.getScanCode();
        r7 = r7.append(r8);
        r8 = " char:";
        r7 = r7.append(r8);
        r8 = r14.getCharacters();
        r7 = r7.append(r8);
        r7 = r7.toString();
        r12.onText(r7);
        r7 = 1;
        goto L_0x0005;
    L_0x0063:
        switch(r13) {
            case 24: goto L_0x006e;
            case 25: goto L_0x0086;
            default: goto L_0x0066;
        };
    L_0x0066:
        r7 = r12.gxt();
        if (r7 != 0) goto L_0x009f;
    L_0x006c:
        r7 = 0;
        goto L_0x0005;
    L_0x006e:
        r7 = com.klye.ime.latin.M.vlcr;
        if (r7 == 0) goto L_0x0066;
    L_0x0072:
        r5 = com.klye.ime.latin.M.iv;
        if (r5 == 0) goto L_0x0066;
    L_0x0076:
        r7 = r5.isShown();
        if (r7 == 0) goto L_0x0066;
    L_0x007c:
        r12.fc();
        r7 = 21;
        r12.keyDownUp(r7);
        r7 = 1;
        goto L_0x0005;
    L_0x0086:
        r7 = com.klye.ime.latin.M.vlcr;
        if (r7 == 0) goto L_0x0066;
    L_0x008a:
        r5 = com.klye.ime.latin.M.iv;
        if (r5 == 0) goto L_0x0066;
    L_0x008e:
        r7 = r5.isShown();
        if (r7 == 0) goto L_0x0066;
    L_0x0094:
        r12.fc();
        r7 = 22;
        r12.keyDownUp(r7);
        r7 = 1;
        goto L_0x0005;
    L_0x009f:
        r7 = com.klye.ime.latin.M.meta;
        r8 = 4096; // 0x1000 float:5.74E-42 double:2.0237E-320;
        r7 = r7.b(r8);
        if (r7 == 0) goto L_0x00d4;
    L_0x00a9:
        switch(r13) {
            case 29: goto L_0x00b5;
            case 31: goto L_0x00bc;
            case 50: goto L_0x00c3;
            case 52: goto L_0x00ca;
            case 82: goto L_0x00d1;
            case 113: goto L_0x00b2;
            case 114: goto L_0x00b2;
            default: goto L_0x00ac;
        };
    L_0x00ac:
        r7 = com.klye.ime.latin.M.meta;
        r8 = 0;
        r7.st = r8;
    L_0x00b2:
        r7 = 1;
        goto L_0x0005;
    L_0x00b5:
        r7 = 16908319; // 0x102001f float:2.3877316E-38 double:8.3538195E-317;
        r12.pcma(r7);
        goto L_0x00b2;
    L_0x00bc:
        r7 = 16908321; // 0x1020021 float:2.3877321E-38 double:8.3538205E-317;
        r12.pcma(r7);
        goto L_0x00b2;
    L_0x00c3:
        r7 = 16908322; // 0x1020022 float:2.3877324E-38 double:8.353821E-317;
        r12.pcma(r7);
        goto L_0x00b2;
    L_0x00ca:
        r7 = 16908320; // 0x1020020 float:2.387732E-38 double:8.35382E-317;
        r12.pcma(r7);
        goto L_0x00b2;
    L_0x00d1:
        r7 = 0;
        goto L_0x0005;
    L_0x00d4:
        r7 = com.klye.ime.latin.M.meta;
        r7.kd(r13, r14);
        r7 = com.klye.ime.latin.M.tlc;
        if (r13 != r7) goto L_0x00df;
    L_0x00dd:
        if (r13 != 0) goto L_0x00e8;
    L_0x00df:
        r7 = com.klye.ime.latin.M.hwk;
        r8 = 1;
        if (r7 != r8) goto L_0x00ee;
    L_0x00e4:
        r7 = 95;
        if (r13 != r7) goto L_0x00ee;
    L_0x00e8:
        r12.tl();
        r7 = 1;
        goto L_0x0005;
    L_0x00ee:
        r7 = com.klye.ime.latin.M.kcs;
        if (r13 != r7) goto L_0x00fe;
    L_0x00f2:
        r7 = 1;
        r12.setCandidatesViewShown(r7);
        r7 = com.klye.ime.latin.M.psbl;
        r12.ss(r7);
        r7 = 1;
        goto L_0x0005;
    L_0x00fe:
        r7 = com.klye.ime.latin.M.meta;
        r8 = 514; // 0x202 float:7.2E-43 double:2.54E-321;
        r0 = r7.b(r8);
        switch(r13) {
            case 19: goto L_0x0187;
            case 20: goto L_0x0187;
            case 21: goto L_0x0187;
            case 22: goto L_0x0187;
            case 57: goto L_0x017b;
            case 58: goto L_0x017b;
            case 59: goto L_0x017b;
            case 60: goto L_0x017b;
            case 61: goto L_0x017b;
            case 62: goto L_0x0159;
            case 67: goto L_0x018d;
            case 82: goto L_0x0172;
            case 84: goto L_0x0172;
            case 112: goto L_0x0169;
            case 113: goto L_0x0172;
            case 114: goto L_0x0172;
            case 115: goto L_0x017b;
            case 116: goto L_0x017e;
            case 119: goto L_0x017b;
            default: goto L_0x0109;
        };
    L_0x0109:
        r7 = com.klye.ime.latin.M.zhType();
        r8 = 90;
        if (r7 != r8) goto L_0x0195;
    L_0x0111:
        r6 = 1;
    L_0x0112:
        r7 = 1;
        com.klye.ime.latin.M.hk = r7;
        r7 = 7012463; // 0x6b006f float:9.826554E-39 double:3.464617E-317;
        r8 = com.klye.ime.latin.M.mLC;
        if (r7 != r8) goto L_0x0198;
    L_0x011c:
        r3 = 1;
    L_0x011d:
        r7 = com.klye.ime.latin.M.hard;
        if (r7 != 0) goto L_0x0125;
    L_0x0121:
        if (r6 != 0) goto L_0x0125;
    L_0x0123:
        if (r3 == 0) goto L_0x01bd;
    L_0x0125:
        r7 = com.klye.ime.latin.M.meta;
        r8 = 8;
        r2 = r7.b(r8);
        r7 = com.klye.ime.latin.M.meta;
        r8 = 4096; // 0x1000 float:5.74E-42 double:2.0237E-320;
        r1 = r7.b(r8);
        r7 = com.klye.ime.latin.M.meta;
        r8 = 4;
        r4 = r7.b(r8);
        r7 = com.klye.ime.latin.M.hwk;
        r8 = 2;
        if (r7 != r8) goto L_0x019a;
    L_0x0141:
        r7 = 56;
        if (r13 != r7) goto L_0x019a;
    L_0x0145:
        if (r0 == 0) goto L_0x019a;
    L_0x0147:
        r7 = 44;
        r8 = 1;
        r8 = new int[r8];
        r9 = 0;
        r10 = 44;
        r8[r9] = r10;
        r9 = 0;
        r10 = 0;
        r12.onKey(r7, r8, r9, r10);
        r7 = 1;
        goto L_0x0005;
    L_0x0159:
        r7 = r14.isShiftPressed();
        if (r7 == 0) goto L_0x0109;
    L_0x015f:
        r7 = com.klye.ime.latin.M.sstl;
        if (r7 == 0) goto L_0x0109;
    L_0x0163:
        r12.tl();
        r7 = 1;
        goto L_0x0005;
    L_0x0169:
        r7 = com.klye.ime.latin.M.ms();
        if (r7 == 0) goto L_0x0109;
    L_0x016f:
        r7 = 0;
        goto L_0x0005;
    L_0x0172:
        r7 = com.klye.ime.latin.M.meta;
        r8 = r7.st;
        r10 = 4096; // 0x1000 float:5.74E-42 double:2.0237E-320;
        r8 = r8 | r10;
        r7.st = r8;
    L_0x017b:
        r7 = 0;
        goto L_0x0005;
    L_0x017e:
        r7 = com.klye.ime.latin.M.htcv();
        if (r7 != 0) goto L_0x0109;
    L_0x0184:
        r7 = 0;
        goto L_0x0005;
    L_0x0187:
        r12.fc();
        r7 = 0;
        goto L_0x0005;
    L_0x018d:
        if (r0 != 0) goto L_0x0192;
    L_0x018f:
        r12.handleBackspace();
    L_0x0192:
        r7 = 1;
        goto L_0x0005;
    L_0x0195:
        r6 = 0;
        goto L_0x0112;
    L_0x0198:
        r3 = 0;
        goto L_0x011d;
    L_0x019a:
        if (r2 != 0) goto L_0x01bd;
    L_0x019c:
        if (r1 != 0) goto L_0x01bd;
    L_0x019e:
        if (r4 != 0) goto L_0x01bd;
    L_0x01a0:
        if (r6 == 0) goto L_0x01a8;
    L_0x01a2:
        r7 = r12.tzy(r13, r14);
        if (r7 != 0) goto L_0x01ba;
    L_0x01a8:
        if (r3 == 0) goto L_0x01b0;
    L_0x01aa:
        r7 = r12.tko(r13, r14, r0);
        if (r7 != 0) goto L_0x01ba;
    L_0x01b0:
        r7 = com.klye.ime.latin.M.hard;
        if (r7 == 0) goto L_0x01bd;
    L_0x01b4:
        r7 = r12.translateKeyDown(r13, r14);
        if (r7 == 0) goto L_0x01bd;
    L_0x01ba:
        r7 = 1;
        goto L_0x0005;
    L_0x01bd:
        r7 = super.onKeyDown(r13, r14);
        goto L_0x0005;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.klye.ime.latin.LatinIME.onKeyDown(int, android.view.KeyEvent):boolean");
    }

    private void fc() {
        M.ic = ic();
        if (M.ic != null) {
            M.ic.finishComposingText();
        }
        this.mWord.reset();
        this.mPredicting = false;
        M.pw1 = null;
        this.mPrevWord2 = null;
        this.mBestWord = null;
    }

    private void tl() {
        toggleLanguage(false, ENABLE_VOICE_BUTTON);
        M.meta.st = 0;
        if (M.snl) {
            M.noti(this, M.dn(), M.wp(""));
        } else {
            M.msg(this, M.dn());
        }
    }

    public void onAppPrivateCommand(String s, Bundle data) {
        if (s != null && s.length() >= 3) {
            switch (s.charAt(0)) {
                case 'l':
                    this.mLanguageSwitcher.set(s.substring(2));
                    loadLanguage();
                    return;
                case 'm':
                    switch (s.charAt(2)) {
                        case 'e':
                            changeKeyboardMode(6);
                            return;
                        case 'h':
                            toggleHW(1);
                            return;
                        case 'k':
                            toggleHW(0);
                            return;
                        case 'v':
                            vr();
                            return;
                        default:
                            return;
                    }
                default:
                    return;
            }
        }
    }

    private boolean translateKeyDown(int kc, KeyEvent e) {
        char c = M.meta.guc(e);
        InputConnection ic = ic();
        if (M.hwk == 0 && (c == 0 || ic == null)) {
            return false;
        }
        char C = Character.toUpperCase(c);
        char c2 = Character.toLowerCase(c);
        boolean up = M.meta.uc();
        boolean iu = c2 != c ? ENABLE_VOICE_BUTTON : false;
        boolean d = c != C ? ENABLE_VOICE_BUTTON : false;
        boolean dvrk = M.dvorak();
        switch (M.mLC) {
            case M.ag /*6357095*/:
            case M.el /*6619244*/:
            case M.pc /*7340131*/:
                c = HKM.m(3, c2);
                break;
            case M.bo /*6422639*/:
            case M.dz /*6553722*/:
                c = HKM.m(4, c2);
                break;
            case M.hy /*6815865*/:
                c = HKM.m(2, c2);
                break;
            case M.is /*6881395*/:
                c = HKM.mis(c, kc, iu);
                break;
            case M.iw /*6881399*/:
                c = HKM.miw(c, kc, iu);
                break;
            default:
                if (!M.cyr) {
                    if (M.rmp) {
                        if (!M.colemak()) {
                            if (!dvrk) {
                                if (M.qwertzC) {
                                    switch (C) {
                                        case 'Y':
                                            c = 'Z';
                                            break;
                                        case 'Z':
                                            c = 'Y';
                                            break;
                                    }
                                }
                            }
                            switch (kc) {
                                case 55:
                                    c = 'w';
                                    break;
                                case 56:
                                    c = 'v';
                                    break;
                                case 74:
                                    c = 's';
                                    break;
                                case 76:
                                    c = 'z';
                                    break;
                                default:
                                    switch (c) {
                                        case '\"':
                                            c = '_';
                                            break;
                                        case '\'':
                                            c = '-';
                                            break;
                                        case ',':
                                            c = 'w';
                                            break;
                                        case KEYCODE_PERIOD /*46*/:
                                            c = 'v';
                                            break;
                                        case '/':
                                            c = 'z';
                                            break;
                                        case ':':
                                            c = 'S';
                                            break;
                                        case ';':
                                            c = 's';
                                            break;
                                        case '<':
                                            c = 'W';
                                            break;
                                        case '>':
                                            c = 'V';
                                            break;
                                        case '?':
                                            c = 'Z';
                                            break;
                                        case 'E':
                                            c = '>';
                                            break;
                                        case 'Q':
                                            c = '\"';
                                            break;
                                        case 'W':
                                            c = '<';
                                            break;
                                        case 'Z':
                                            c = ':';
                                            break;
                                        case '[':
                                            c = '/';
                                            break;
                                        case ']':
                                            c = '=';
                                            break;
                                        case 'e':
                                            c = '.';
                                            break;
                                        case 'q':
                                            c = '\'';
                                            break;
                                        case 'w':
                                            c = ',';
                                            break;
                                        case 'z':
                                            c = ';';
                                            break;
                                        case '{':
                                            c = '?';
                                            break;
                                        case '}':
                                            c = '+';
                                            break;
                                    }
                                    switch (C) {
                                        case 'B':
                                            c = 'X';
                                            break;
                                        case 'C':
                                            c = 'J';
                                            break;
                                        case 'D':
                                            c = 'E';
                                            break;
                                        case 'F':
                                            c = 'U';
                                            break;
                                        case 'G':
                                            c = 'I';
                                            break;
                                        case 'H':
                                            c = 'D';
                                            break;
                                        case 'I':
                                            c = 'C';
                                            break;
                                        case 'J':
                                            c = 'H';
                                            break;
                                        case 'K':
                                            c = 'T';
                                            break;
                                        case 'L':
                                            c = 'N';
                                            break;
                                        case 'N':
                                            c = 'B';
                                            break;
                                        case 'O':
                                            c = 'R';
                                            break;
                                        case 'P':
                                            c = 'L';
                                            break;
                                        case 'R':
                                            c = 'P';
                                            break;
                                        case 'S':
                                            c = 'O';
                                            break;
                                        case 'T':
                                            c = 'Y';
                                            break;
                                        case 'U':
                                            c = 'G';
                                            break;
                                        case 'V':
                                            c = 'K';
                                            break;
                                        case 'X':
                                            c = 'Q';
                                            break;
                                        case 'Y':
                                            c = 'F';
                                            break;
                                    }
                                    break;
                            }
                        }
                        c = HKM.m(17, c);
                    }
                } else {
                    int i = (M.km() == 2 || !(M.cy2 == -1 || M.mLC == M.mn)) ? 1 : 0;
                    if ((M.dc.dc || e.isLongPress()) && (M.htcv() || M.ms())) {
                        switch (c2) {
                            case 'e':
                            case 't':
                                c = 1241;
                                break;
                            case 'o':
                                c = 1093;
                                break;
                            case 'p':
                                c = 1098;
                                break;
                        }
                    }
                    if (c == c2 || c == C) {
                        c = HKM.m(i, c2);
                    } else {
                        handleBackspace();
                    }
                    switch (M.hwk) {
                        case 1:
                            boolean a = M.meta.alt();
                            switch (kc) {
                                case 39:
                                    if (a) {
                                        c = 1093;
                                        break;
                                    }
                                    break;
                                case 40:
                                    if (a) {
                                        c = 1098;
                                        break;
                                    }
                                    break;
                                case 68:
                                    if (!a) {
                                        c = 1073;
                                        break;
                                    }
                                    c = 1102;
                                    break;
                                case 75:
                                    if (!a) {
                                        c = 1101;
                                        break;
                                    }
                                    c = 1078;
                                    break;
                            }
                            break;
                        case 2:
                            switch (kc) {
                                case 116:
                                    c = 1101;
                                    break;
                            }
                            if (c == c2 || c == C) {
                                c = HKM.mRu(i, c2, kc);
                                break;
                            }
                        default:
                            if (c == c2 || c == C) {
                                c = HKM.mRu(i, c2, kc);
                                break;
                            }
                    }
                    char c1 = 0;
                    switch (M.mLC) {
                        case M.mk /*7143531*/:
                            c1 = M.mk(c);
                            break;
                        case M.uk /*7667819*/:
                            c1 = M.uk(c);
                            break;
                    }
                    if (c1 != 0) {
                        c = c1;
                    }
                }
                if (d) {
                    c = Character.toLowerCase(c);
                }
                if (up) {
                    if (!(dvrk || M.cyr)) {
                        switch (kc) {
                            case 55:
                                c = '<';
                                break;
                            case 56:
                                c = '>';
                                break;
                            case 69:
                                c = '_';
                                break;
                            case 72:
                                c = '}';
                                break;
                            case 73:
                                c = '|';
                                break;
                            case 74:
                                c = ':';
                                break;
                            case 75:
                                c = '\"';
                                break;
                            case 76:
                                c = '?';
                                break;
                            case 81:
                                c = '+';
                                break;
                        }
                    }
                    c = Character.toUpperCase(c);
                    break;
                }
                break;
        }
        if (iu || up) {
            c = Character.toUpperCase(c);
        }
        if (c == 1) {
            c = 1105;
        }
        onKey(c, new int[]{Character.toLowerCase(c)}, 0, 0);
        return ENABLE_VOICE_BUTTON;
    }

    public boolean tzy(int c1, KeyEvent e) {
        int c = 0;
        boolean a = !e.isAltPressed() ? ENABLE_VOICE_BUTTON : false;
        if (e.isShiftPressed()) {
            return false;
        }
        switch (c1) {
            case 7:
                if (a) {
                    c = 12578;
                    break;
                }
                break;
            case R.styleable.LatinKeyboardBaseView_keyHysteresisDistance /*8*/:
                if (a) {
                    c = 12549;
                    break;
                }
                break;
            case 9:
                if (a) {
                    c = 12553;
                    break;
                }
                break;
            case 10:
                if (a) {
                    c = 711;
                    break;
                }
                break;
            case R.styleable.LatinKeyboardBaseView_shadowColor /*11*/:
                if (a) {
                    c = 715;
                    break;
                }
                break;
            case R.styleable.LatinKeyboardBaseView_shadowRadius /*12*/:
                if (a) {
                    c = 12563;
                    break;
                }
                break;
            case R.styleable.LatinKeyboardBaseView_backgroundDimAmount /*13*/:
                if (a) {
                    c = 714;
                    break;
                }
                break;
            case R.styleable.LatinKeyboardBaseView_keyTextStyle /*14*/:
                if (a) {
                    c = 729;
                    break;
                }
                break;
            case R.styleable.LatinKeyboardBaseView_symbolColorScheme /*15*/:
                if (a) {
                    c = 12570;
                    break;
                }
                break;
            case CPS_BUFFER_SIZE /*16*/:
                if (a) {
                    c = 12574;
                    break;
                }
                break;
            case 29:
                if (a) {
                    c = 12551;
                    break;
                }
                break;
            case 30:
                if (a) {
                    c = 12566;
                    break;
                }
                break;
            case 31:
                if (a) {
                    c = 12559;
                    break;
                }
                break;
            case 32:
                if (a) {
                    c = 12558;
                    break;
                }
                break;
            case 33:
                if (!a) {
                    c = 711;
                    break;
                }
                c = 12557;
                break;
            case 34:
                if (a) {
                    c = 12561;
                    break;
                }
                break;
            case 35:
                if (a) {
                    c = 12565;
                    break;
                }
                break;
            case 36:
                if (a) {
                    c = 12568;
                    break;
                }
                break;
            case 37:
                if (!a) {
                    c = 12570;
                    break;
                }
                c = 12571;
                break;
            case 38:
                if (a) {
                    c = 12584;
                    break;
                }
                break;
            case 39:
                if (a) {
                    c = 12572;
                    break;
                }
                break;
            case 40:
                if (a) {
                    c = 12576;
                    break;
                }
                break;
            case 41:
                if (a) {
                    c = 12585;
                    break;
                }
                break;
            case 42:
                if (a) {
                    c = 12569;
                    break;
                }
                break;
            case 43:
                if (!a) {
                    c = 12574;
                    break;
                }
                c = 12575;
                break;
            case 44:
                if (!a) {
                    c = 12578;
                    break;
                }
                c = 12579;
                break;
            case 45:
                if (!a) {
                    c = 12549;
                    break;
                }
                c = 12550;
                break;
            case KEYCODE_PERIOD /*46*/:
                if (!a) {
                    c = 715;
                    break;
                }
                c = 12560;
                break;
            case 47:
                if (a) {
                    c = 12555;
                    break;
                }
                break;
            case 48:
                if (!a) {
                    c = 12563;
                    break;
                }
                c = 12564;
                break;
            case 49:
                if (!a) {
                    c = 729;
                    break;
                }
                c = 12583;
                break;
            case 50:
                if (a) {
                    c = 12562;
                    break;
                }
                break;
            case 51:
                if (!a) {
                    c = 12553;
                    break;
                }
                c = 12554;
                break;
            case 52:
                if (a) {
                    c = 12556;
                    break;
                }
                break;
            case 53:
                if (!a) {
                    c = 714;
                    break;
                }
                c = 12567;
                break;
            case 54:
                if (a) {
                    c = 12552;
                    break;
                }
                break;
            case 55:
                if (!a) {
                    c = 12580;
                    break;
                }
                c = 12573;
                break;
            case 56:
                if (a) {
                    c = 12577;
                    break;
                }
                break;
            case 69:
                if (a) {
                    c = 12582;
                    break;
                }
                break;
            case 74:
                if (a) {
                    c = 12580;
                    break;
                }
                break;
            case 76:
                if (a) {
                    c = 12581;
                    break;
                }
                break;
            case 77:
                if (a) {
                    c = 12582;
                    break;
                }
                break;
        }
        if (c == 0) {
            return false;
        }
        onKey(c, new int[]{c}, 0, 0);
        return ENABLE_VOICE_BUTTON;
    }

    public boolean tko(int c1, KeyEvent e, boolean alt) {
        int c = 0;
        if (!alt) {
            boolean a = M.meta.uc();
            switch (c1) {
                case 29:
                    c = 12609;
                    break;
                case 30:
                    c = 12640;
                    break;
                case 31:
                    c = 12618;
                    break;
                case 32:
                    c = 12615;
                    break;
                case 33:
                    if (!a) {
                        c = 12599;
                        break;
                    }
                    c = 12600;
                    break;
                case 34:
                    c = 12601;
                    break;
                case 35:
                    c = 12622;
                    break;
                case 36:
                    c = 12631;
                    break;
                case 37:
                    c = 12625;
                    break;
                case 38:
                    c = 12627;
                    break;
                case 39:
                    c = 12623;
                    break;
                case 40:
                    c = 12643;
                    break;
                case 41:
                    c = 12641;
                    break;
                case 42:
                    c = 12636;
                    break;
                case 43:
                    if (!a) {
                        c = 12624;
                        break;
                    }
                    c = 12626;
                    break;
                case 44:
                    if (!a) {
                        c = 12628;
                        break;
                    }
                    c = 12630;
                    break;
                case 45:
                    if (!a) {
                        c = 12610;
                        break;
                    }
                    c = 12611;
                    break;
                case KEYCODE_PERIOD /*46*/:
                    if (!a) {
                        c = 12593;
                        break;
                    }
                    c = 12594;
                    break;
                case 47:
                    c = 12596;
                    break;
                case 48:
                    if (!a) {
                        c = 12613;
                        break;
                    }
                    c = 12614;
                    break;
                case 49:
                    c = 12629;
                    break;
                case 50:
                    c = 12621;
                    break;
                case 51:
                    if (!a) {
                        c = 12616;
                        break;
                    }
                    c = 12617;
                    break;
                case 52:
                    c = 12620;
                    break;
                case 53:
                    c = 12635;
                    break;
                case 54:
                    c = 12619;
                    break;
            }
        }
        c = M.meta.guc(e);
        if (c == 0) {
            return false;
        }
        onKey(c, new int[]{c}, 0, 0);
        return ENABLE_VOICE_BUTTON;
    }

    public boolean onKeyUp(int c, KeyEvent e) {
        if (c > 219) {
            return false;
        }
        if (c == 4) {
            if (M.iv == null) {
                return false;
            }
            if (M.iv.handleBack()) {
                return ENABLE_VOICE_BUTTON;
            }
            if (!isInputViewShown()) {
                return false;
            }
            handleClose();
            return ENABLE_VOICE_BUTTON;
        } else if (gxt() == null) {
            return false;
        } else {
            if (M.mDebugMode && M.me) {
                switch (c) {
                    case 4:
                    case 24:
                    case 25:
                    case 82:
                    case 84:
                        break;
                    default:
                        onText("\nKeyUp:" + c);
                        return ENABLE_VOICE_BUTTON;
                }
            }
            M.hk = false;
            if (M.meta.b(4096)) {
                switch (c) {
                    case 82:
                    case 113:
                    case BuildConfig.VERSION_CODE /*114*/:
                        boolean b = M.meta.b(268435456);
                        M.meta.st = 0;
                        return b;
                    default:
                        return ENABLE_VOICE_BUTTON;
                }
            }
            M.meta.ku(c, e);
            if (M.vlcr) {
                LatinKeyboardView v = M.iv;
                if (v != null && v.isShown()) {
                    switch (c) {
                        case 24:
                        case 25:
                            return ENABLE_VOICE_BUTTON;
                    }
                }
            }
            switch (c) {
                case 57:
                case 58:
                case 59:
                case 60:
                case 61:
                case 115:
                    return false;
                case 112:
                    if (M.ms()) {
                        return false;
                    }
                    break;
            }
            M.meta.aakp();
            return super.onKeyUp(c, e);
        }
    }

    private void revertVoiceInput() {
        InputConnection ic = ic();
        if (ic != null) {
            ic.commitText("", 1);
        }
        updateSuggestions();
        this.mVoiceInputHighlighted = false;
    }

    private void commitVoiceInput() {
        InputConnection ic = ic();
        if (ic != null) {
            ic.finishComposingText();
        }
        updateSuggestions();
        this.mVoiceInputHighlighted = false;
    }

    private void reloadKeyboards() {
        this.mKeyboardSwitcher.setLanguageSwitcher(this.mLanguageSwitcher);
        if (!(M.iv == null || this.mKeyboardSwitcher.getKeyboardMode() == 0)) {
            KeyboardSwitcher keyboardSwitcher = this.mKeyboardSwitcher;
            boolean z = (this.mEnableVoice && this.mEnableVoiceButton) ? ENABLE_VOICE_BUTTON : false;
            keyboardSwitcher.setVoiceMode(z, this.mVoiceOnPrimary);
        }
        this.mKeyboardSwitcher.makeKeyboards(ENABLE_VOICE_BUTTON);
    }

    private void commitTyped(InputConnection ic) {
        if (this.mPredicting) {
            this.mPredicting = false;
            if (this.mWord.mTW.length() > 0) {
                if (ic != null) {
                    cmmt(M.ime(this.mWord.mTW, ENABLE_VOICE_BUTTON), ic);
                }
                this.mCommittedLength = this.mWord.mTW.length();
                TextEntryState.acceptedTyped(this.mWord.mTW);
                addToDictionaries(this.mWord.mTW, 2);
                this.mJustAccepted = ENABLE_VOICE_BUTTON;
            }
            updateSuggestions();
        }
    }

    private void postUpdateShiftKeyState() {
        this.mHandler.removeMessages(2);
        this.mHandler.sendMessageDelayed(this.mHandler.obtainMessage(2), 300);
    }

    public void updateShiftKeyState(EditorInfo attr) {
        InputConnection ic = ic();
        if (ic != null && attr != null && this.mKeyboardSwitcher.isAlphabetMode()) {
            KeyboardSwitcher keyboardSwitcher = this.mKeyboardSwitcher;
            boolean z = (this.mCapsLock || this.mShiftKeyState.isMomentary() || getCursorCapsMode(ic, attr) != 0) ? ENABLE_VOICE_BUTTON : false;
            keyboardSwitcher.setShifted(z);
        }
    }

    private int getCursorCapsMode(InputConnection ic, EditorInfo attr) {
        int caps = 0;
        EditorInfo ei = getCurrentInputEditorInfo();
        if (!(M.noAutoCapC || !this.mAutoCap || !M.isAK() || ei == null || ei.inputType == 0)) {
            caps = ic.getCursorCapsMode(attr.inputType);
            switch (M.mLC) {
                case M.hy /*6815865*/:
                    CharSequence l2 = ic.getTextBeforeCursor(2, 0);
                    if (l2 == null || l2.length() == 0) {
                        return 1;
                    }
                    if (l2.length() == 2 && l2.charAt(1) == ' ') {
                        switch (l2.charAt(0)) {
                            case ':':
                            case 1417:
                            case 8230:
                                return 1;
                        }
                    }
                    return 0;
            }
        }
        return caps;
    }

    private void swapPunctuationAndSpace() {
        InputConnection ic = ic();
        if (ic != null) {
            CharSequence lastTwo = ic.getTextBeforeCursor(2, 0);
            if (lastTwo != null && lastTwo.length() == 2 && lastTwo.charAt(0) == ' ' && M.iss(lastTwo.charAt(1))) {
                ic.beginBatchEdit();
                ic.deleteSurroundingText(2, 0);
                ic.commitText(lastTwo.charAt(1) + " ", 1);
                ic.endBatchEdit();
                updateShiftKeyState(getCurrentInputEditorInfo());
                this.mJustAddedAutoSpace = ENABLE_VOICE_BUTTON;
            }
        }
    }

    private void cmmt(CharSequence cs, InputConnection ic) {
        if (M.pyt(cs, cs.length())) {
            cs = M.pyt(cs.toString());
        } else if (M.fdb) {
            cs = M.fd(cs);
        }
        ic.commitText(cs, 1);
        this.mPrevWord2 = M.pw1;
        M.pw1 = cs.toString().toLowerCase();
        if (M.hw() && M.h != null) {
            M.h.clearStrokes();
        }
        if (M.dicB != null) {
            this.mWord.setFirstCharCapitalized(false);
        }
    }

    private void reswapPeriodAndSpace() {
        InputConnection ic = ic();
        if (ic != null) {
            CharSequence lastThree = ic.getTextBeforeCursor(3, 0);
            if (lastThree != null && lastThree.length() == 3 && lastThree.charAt(0) == '.' && lastThree.charAt(1) == ' ' && lastThree.charAt(2) == '.') {
                ic.beginBatchEdit();
                ic.deleteSurroundingText(3, 0);
                ic.commitText(" ..", 1);
                ic.endBatchEdit();
                updateShiftKeyState(getCurrentInputEditorInfo());
            }
        }
    }

    private void doubleSpace() {
        InputConnection ic = ic();
        if (ic != null) {
            CharSequence lastThree = ic.getTextBeforeCursor(3, 0);
            if (lastThree != null && lastThree.length() == 3 && Character.isLetterOrDigit(lastThree.charAt(0)) && lastThree.charAt(1) == ' ' && lastThree.charAt(2) == ' ') {
                ic.beginBatchEdit();
                ic.deleteSurroundingText(2, 0);
                ic.commitText(". ", 1);
                ic.endBatchEdit();
                updateShiftKeyState(getCurrentInputEditorInfo());
                this.mJustAddedAutoSpace = ENABLE_VOICE_BUTTON;
            }
        }
    }

    private void maybeRemovePreviousPeriod(CharSequence text) {
        InputConnection ic = ic();
        if (ic != null && M.mLC != M.lj) {
            CharSequence lastOne = ic.getTextBeforeCursor(1, 0);
            if (lastOne != null && lastOne.length() == 1 && lastOne.charAt(0) == '.' && text.charAt(0) == '.') {
                ic.deleteSurroundingText(1, 0);
            }
        }
    }

    private void removeTrailingSpace() {
        InputConnection ic = ic();
        if (ic != null) {
            CharSequence lastOne = ic.getTextBeforeCursor(1, 0);
            if (lastOne != null && lastOne.length() == 1 && lastOne.charAt(0) == ' ') {
                ic.deleteSurroundingText(1, 0);
            }
        }
    }

    public String addWordToDictionary(CharSequence s) {
        if (s == null || s.charAt(0) == M.zwsp) {
            return null;
        }
        if (M.udx()) {
            return "User dictionary NOT available yet for " + M.dn();
        }
        int i;
        int f = M.dicU.getWordFrequency(s);
        if (f == 0) {
            f = 128;
        } else if (f == 1) {
            f = 129;
        } else if (f > -1) {
            f = 0;
        } else if (this.mSuggest.ivw) {
            return "Unremovable. Non-user-word";
        } else {
            if (s.length() >= M.dicU.getMaxWordLength()) {
                return "Word too long to save";
            }
            f = 128;
            commitTyped(ic());
            ss(M.psbl);
        }
        M.dicU.addWord(s.toString(), f);
        if (f > 10) {
            i = R.string.added_word;
        } else {
            i = R.string.rm_word;
        }
        return getString(i, new Object[]{s});
    }

    private InputConnection ic() {
        M.ic = getCurrentInputConnection();
        return M.ic;
    }

    private boolean isAlphabet(char c) {
        return (Character.isLetter(c) || c > 255) ? ENABLE_VOICE_BUTTON : false;
    }

    private void showInputMethodPicker() {
        ((InputMethodManager) getSystemService("input_method")).showInputMethodPicker();
    }

    private void onOptionKeyPressed() {
        if (!isShowingOptionDialog()) {
            showOptionsMenu();
        }
    }

    private void mdf(int w) {
        InputConnection ic = ic();
        int n = this.mWord.mTW.length();
        if (ic != null && n != 0) {
            this.mWord.replaceLast(M.jaTog(this.mWord.mTW.charAt(n - 1)));
            sct(ic);
            postUpdateSuggestions();
        }
    }

    private void onOptionKeyLongPressed() {
        if (!isShowingOptionDialog()) {
            if (LatinIMEUtil.hasMultipleEnabledIMEs(this)) {
                showInputMethodPicker();
            } else {
                M.launchSettings(this);
            }
        }
    }

    private boolean isShowingOptionDialog() {
        return (this.mOptionsDialog == null || !this.mOptionsDialog.isShowing()) ? false : ENABLE_VOICE_BUTTON;
    }

    private void keyDownUp(int keyEventCode) {
        InputConnection ic = ic();
        if (ic != null) {
            ic.sendKeyEvent(new KeyEvent(0, keyEventCode));
            ic.sendKeyEvent(new KeyEvent(1, keyEventCode));
        }
    }

    private void keyDownUp(int kc, int keyEventCode) {
        InputConnection ic = ic();
        if (ic != null) {
            ic.sendKeyEvent(new KeyEvent(0, keyEventCode));
            keyDownUp(kc);
            ic.sendKeyEvent(new KeyEvent(1, keyEventCode));
        }
    }

    private static void sks(InputConnection ic, int[] c, int i) {
        ic.sendKeyEvent(new KeyEvent(0, c[i]));
        if (i + 1 < c.length) {
            sks(ic, c, i + 1);
        }
        ic.sendKeyEvent(new KeyEvent(1, c[i]));
    }

    public void onKey(int pc, int[] keyCodes, int x, int y) {
        try {
            M.sha = false;
            onKey1(pc, keyCodes, x, y);
        } catch (Throwable e) {
            M.l(e);
        }
    }

    /* JADX WARNING: Missing block: B:142:0x03ac, code:
            if (com.klye.ime.latin.M.emjAM() != false) goto L_0x03ae;
     */
    public void onKey1(int r23, int[] r24, int r25, int r26) {
        /*
        r22 = this;
        r9 = r22.ic();
        if (r9 != 0) goto L_0x0007;
    L_0x0006:
        return;
    L_0x0007:
        r18 = com.klye.ime.latin.M.ac1;
        switch(r18) {
            case -7: goto L_0x0100;
            case -6: goto L_0x00cb;
            default: goto L_0x000c;
        };
    L_0x000c:
        r15 = com.klye.ime.latin.M.zhType();
        r18 = 32;
        r0 = r23;
        r1 = r18;
        if (r0 != r1) goto L_0x0054;
    L_0x0018:
        r18 = com.klye.ime.latin.M.h;
        if (r18 == 0) goto L_0x0054;
    L_0x001c:
        r18 = com.klye.ime.latin.M.h;
        r18 = r18.isOK();
        if (r18 == 0) goto L_0x0054;
    L_0x0024:
        r18 = 90;
        r0 = r18;
        if (r15 != r0) goto L_0x0054;
    L_0x002a:
        r18 = com.klye.ime.latin.M.klZY;
        if (r18 != 0) goto L_0x0054;
    L_0x002e:
        r0 = r22;
        r0 = r0.mWord;
        r18 = r0;
        r18 = r18.size();
        if (r18 <= 0) goto L_0x0054;
    L_0x003a:
        r0 = r22;
        r0 = r0.mWord;
        r18 = r0;
        r18 = r18.lastChar();
        r18 = java.lang.Character.toString(r18);
        r18 = com.klye.ime.latin.M.htm(r18);
        if (r18 != 0) goto L_0x0054;
    L_0x004e:
        r18 = 0;
        r23 = 175; // 0xaf float:2.45E-43 double:8.65E-322;
        r24[r18] = r23;
    L_0x0054:
        r14 = com.klye.ime.latin.M.iv;
        if (r14 == 0) goto L_0x005b;
    L_0x0058:
        r14.dismissPopupKeyboard();
    L_0x005b:
        r16 = android.os.SystemClock.uptimeMillis();
        r18 = -5;
        r0 = r23;
        r1 = r18;
        if (r0 != r1) goto L_0x0075;
    L_0x0067:
        r0 = r22;
        r0 = r0.mLastKeyTime;
        r18 = r0;
        r20 = 200; // 0xc8 float:2.8E-43 double:9.9E-322;
        r18 = r18 + r20;
        r18 = (r16 > r18 ? 1 : (r16 == r18 ? 0 : -1));
        if (r18 <= 0) goto L_0x007d;
    L_0x0075:
        r18 = 0;
        r0 = r18;
        r1 = r22;
        r1.mDeleteCount = r0;
    L_0x007d:
        r0 = r16;
        r2 = r22;
        r2.mLastKeyTime = r0;
        r0 = r22;
        r0 = r0.mKeyboardSwitcher;
        r18 = r0;
        r8 = r18.hasDistinctMultitouch();
        r18 = com.klye.ime.latin.M.isLP(r23);
        if (r18 == 0) goto L_0x0145;
    L_0x0093:
        r0 = r22;
        r0 = r0.mLanguageSwitcher;
        r18 = r0;
        r19 = com.klye.ime.latin.M.lp(r23);
        r18.set(r19);
        r22.loadLanguage();
    L_0x00a3:
        r0 = r22;
        r0 = r0.mKeyboardSwitcher;
        r18 = r0;
        r0 = r18;
        r1 = r23;
        r18 = r0.onKey(r1);
        if (r18 == 0) goto L_0x00bc;
    L_0x00b3:
        r18 = 0;
        r0 = r22;
        r1 = r18;
        r0.changeKeyboardMode(r1);
    L_0x00bc:
        r18 = com.klye.ime.latin.M.eth;
        if (r18 == 0) goto L_0x0006;
    L_0x00c0:
        r18 = com.klye.ime.latin.M.iv;
        if (r18 == 0) goto L_0x0006;
    L_0x00c4:
        r18 = com.klye.ime.latin.M.iv;
        r18.invalidateAllKeys();
        goto L_0x0006;
    L_0x00cb:
        r0 = r23;
        r0 = (char) r0;
        r18 = r0;
        r18 = com.klye.ime.latin.M.irabc(r18);
        if (r18 == 0) goto L_0x00f1;
    L_0x00d6:
        r18 = 2;
        r0 = r18;
        r4 = new int[r0];
        r18 = 0;
        r19 = 57;
        r4[r18] = r19;
        r18 = 1;
        r19 = r23 + -97;
        r19 = r19 + 29;
        r4[r18] = r19;
        r18 = 0;
        r0 = r18;
        sks(r9, r4, r0);
    L_0x00f1:
        r18 = 0;
        com.klye.ime.latin.M.ac1 = r18;
        r0 = r22;
        r0 = r0.mKeyboardSwitcher;
        r18 = r0;
        r18.invalidate();
        goto L_0x0006;
    L_0x0100:
        r18 = java.lang.Character.toLowerCase(r23);
        r18 = r18 + -97;
        r18 = r18 + 1;
        r0 = r18;
        r6 = (char) r0;
        r18 = 1;
        r19 = 31;
        r0 = r18;
        r1 = r19;
        r18 = com.klye.ime.latin.M.ir(r6, r0, r1);
        if (r18 == 0) goto L_0x0136;
    L_0x0119:
        r0 = r22;
        r0.sendKeyChar(r6);
        r18 = 1;
        r0 = r22;
        r1 = r18;
        r0.handleShiftInternal(r1);
        r18 = 0;
        com.klye.ime.latin.M.ac1 = r18;
        r0 = r22;
        r0 = r0.mKeyboardSwitcher;
        r18 = r0;
        r18.invalidate();
        goto L_0x0006;
    L_0x0136:
        r18 = 0;
        com.klye.ime.latin.M.ac1 = r18;
        r0 = r22;
        r0 = r0.mKeyboardSwitcher;
        r18 = r0;
        r18.invalidate();
        goto L_0x000c;
    L_0x0145:
        switch(r23) {
            case -130: goto L_0x01b7;
            case -129: goto L_0x0280;
            case -128: goto L_0x017f;
            case -127: goto L_0x03d5;
            case -126: goto L_0x016b;
            case -125: goto L_0x03c8;
            case -124: goto L_0x039e;
            case -122: goto L_0x03a3;
            case -120: goto L_0x0373;
            case -119: goto L_0x031a;
            case -118: goto L_0x03ae;
            case -117: goto L_0x03ae;
            case -115: goto L_0x03ae;
            case -114: goto L_0x03a8;
            case -113: goto L_0x03ae;
            case -111: goto L_0x0285;
            case -110: goto L_0x029d;
            case -109: goto L_0x02a9;
            case -108: goto L_0x0291;
            case -107: goto L_0x03be;
            case -105: goto L_0x03f8;
            case -104: goto L_0x03e9;
            case -102: goto L_0x0407;
            case -101: goto L_0x03e4;
            case -100: goto L_0x03c3;
            case -99: goto L_0x0264;
            case -8: goto L_0x02e1;
            case -7: goto L_0x02d9;
            case -6: goto L_0x02d9;
            case -5: goto L_0x02b5;
            case -3: goto L_0x03b9;
            case -2: goto L_0x0378;
            case -1: goto L_0x0309;
            case 1: goto L_0x01ee;
            case 2: goto L_0x01f9;
            case 3: goto L_0x0234;
            case 6: goto L_0x0204;
            case 9: goto L_0x040c;
            case 19: goto L_0x01c6;
            case 20: goto L_0x01c6;
            case 21: goto L_0x01c6;
            case 22: goto L_0x01c6;
            case 46: goto L_0x0434;
            case 711: goto L_0x0417;
            case 714: goto L_0x0417;
            case 715: goto L_0x0417;
            case 729: goto L_0x0417;
            default: goto L_0x0148;
        };
    L_0x0148:
        r18 = 10;
        r0 = r23;
        r1 = r18;
        if (r0 != r1) goto L_0x04ac;
    L_0x0150:
        r18 = com.klye.ime.latin.M.h;
        if (r18 == 0) goto L_0x0484;
    L_0x0154:
        r18 = com.klye.ime.latin.M.h;
        r18 = r18.isOK();
        if (r18 == 0) goto L_0x0484;
    L_0x015c:
        r18 = com.klye.ime.latin.M.h;
        r18 = r18.isw();
        if (r18 == 0) goto L_0x0484;
    L_0x0164:
        r18 = com.klye.ime.latin.M.h;
        r18.process();
        goto L_0x0006;
    L_0x016b:
        r18 = 2131296402; // 0x7f090092 float:1.821072E38 double:1.0530003333E-314;
        r0 = r22;
        r1 = r18;
        r18 = r0.getString(r1);
        r0 = r22;
        r1 = r18;
        r0.onText(r1);
        goto L_0x00a3;
    L_0x017f:
        r19 = new java.lang.StringBuilder;
        r19.<init>();
        r18 = com.klye.ime.latin.M.hw();
        if (r18 == 0) goto L_0x01ab;
    L_0x018a:
        r18 = "hw";
    L_0x018c:
        r0 = r19;
        r1 = r18;
        r18 = r0.append(r1);
        r19 = ".html";
        r18 = r18.append(r19);
        r18 = r18.toString();
        r18 = com.klye.ime.latin.M.hp(r18);
        r0 = r22;
        r1 = r18;
        com.klye.ime.latin.M.sa(r0, r1);
        goto L_0x00a3;
    L_0x01ab:
        r18 = 89;
        r0 = r18;
        if (r15 != r0) goto L_0x01b4;
    L_0x01b1:
        r18 = "jy";
        goto L_0x018c;
    L_0x01b4:
        r18 = "ml";
        goto L_0x018c;
    L_0x01b7:
        r18 = "a.html";
        r18 = com.klye.ime.latin.M.hp1(r18);
        r0 = r22;
        r1 = r18;
        com.klye.ime.latin.M.sa(r0, r1);
        goto L_0x00a3;
    L_0x01c6:
        r22.fc();
        r18 = com.klye.ime.latin.M.kid;
        r19 = 2131034125; // 0x7f05000d float:1.7678759E38 double:1.0528707513E-314;
        r18 = r18.is(r19);
        if (r18 == 0) goto L_0x01e9;
    L_0x01d4:
        if (r14 == 0) goto L_0x01e9;
    L_0x01d6:
        r18 = r14.isShifted();
        if (r18 == 0) goto L_0x01e9;
    L_0x01dc:
        r18 = 59;
        r0 = r22;
        r1 = r23;
        r2 = r18;
        r0.keyDownUp(r1, r2);
        goto L_0x00a3;
    L_0x01e9:
        r22.keyDownUp(r23);
        goto L_0x00a3;
    L_0x01ee:
        r18 = "MMM dd,yyyy ";
        r0 = r22;
        r1 = r18;
        r0.ins(r9, r1);
        goto L_0x0006;
    L_0x01f9:
        r18 = "h:mm a ";
        r0 = r22;
        r1 = r18;
        r0.ins(r9, r1);
        goto L_0x0006;
    L_0x0204:
        r18 = com.klye.ime.latin.M.kid;
        r19 = 2131034125; // 0x7f05000d float:1.7678759E38 double:1.0528707513E-314;
        r18 = r18.is(r19);
        if (r18 == 0) goto L_0x0220;
    L_0x020f:
        if (r14 == 0) goto L_0x0220;
    L_0x0211:
        r18 = r14.isShifted();
        if (r18 == 0) goto L_0x0220;
    L_0x0217:
        r18 = 59;
        r0 = r22;
        r1 = r18;
        r0.keyDownUp(r1);
    L_0x0220:
        r18 = 58;
        r0 = r22;
        r1 = r18;
        r0.keyDownUp(r1);
        r18 = 22;
        r0 = r22;
        r1 = r18;
        r0.keyDownUp(r1);
        goto L_0x00a3;
    L_0x0234:
        r18 = com.klye.ime.latin.M.kid;
        r19 = 2131034125; // 0x7f05000d float:1.7678759E38 double:1.0528707513E-314;
        r18 = r18.is(r19);
        if (r18 == 0) goto L_0x0250;
    L_0x023f:
        if (r14 == 0) goto L_0x0250;
    L_0x0241:
        r18 = r14.isShifted();
        if (r18 == 0) goto L_0x0250;
    L_0x0247:
        r18 = 59;
        r0 = r22;
        r1 = r18;
        r0.keyDownUp(r1);
    L_0x0250:
        r18 = 57;
        r0 = r22;
        r1 = r18;
        r0.keyDownUp(r1);
        r18 = 21;
        r0 = r22;
        r1 = r18;
        r0.keyDownUp(r1);
        goto L_0x00a3;
    L_0x0264:
        r18 = 22;
        r0 = r22;
        r1 = r18;
        r0.keyDownUp(r1);
        r22.handleBackspace();
        r0 = r22;
        r0 = r0.mDeleteCount;
        r18 = r0;
        r18 = r18 + 1;
        r0 = r18;
        r1 = r22;
        r1.mDeleteCount = r0;
        goto L_0x00a3;
    L_0x0280:
        r22.trans();
        goto L_0x00a3;
    L_0x0285:
        r18 = 16908319; // 0x102001f float:2.3877316E-38 double:8.3538195E-317;
        r0 = r22;
        r1 = r18;
        r0.pcma(r1);
        goto L_0x00a3;
    L_0x0291:
        r18 = 16908321; // 0x1020021 float:2.3877321E-38 double:8.3538205E-317;
        r0 = r22;
        r1 = r18;
        r0.pcma(r1);
        goto L_0x00a3;
    L_0x029d:
        r18 = 16908322; // 0x1020022 float:2.3877324E-38 double:8.353821E-317;
        r0 = r22;
        r1 = r18;
        r0.pcma(r1);
        goto L_0x00a3;
    L_0x02a9:
        r18 = 16908320; // 0x1020020 float:2.387732E-38 double:8.35382E-317;
        r0 = r22;
        r1 = r18;
        r0.pcma(r1);
        goto L_0x00a3;
    L_0x02b5:
        r18 = com.klye.ime.latin.M.hw();
        if (r18 == 0) goto L_0x02c6;
    L_0x02bb:
        r18 = com.klye.ime.latin.M.h;
        if (r18 == 0) goto L_0x02c6;
    L_0x02bf:
        r18 = com.klye.ime.latin.M.h;
        r18.del();
        goto L_0x00a3;
    L_0x02c6:
        r22.handleBackspace();
        r0 = r22;
        r0 = r0.mDeleteCount;
        r18 = r0;
        r18 = r18 + 1;
        r0 = r18;
        r1 = r22;
        r1.mDeleteCount = r0;
        goto L_0x00a3;
    L_0x02d9:
        r18 = com.klye.ime.latin.M.isLatinC;
        if (r18 == 0) goto L_0x00a3;
    L_0x02dd:
        com.klye.ime.latin.M.ac1 = r23;
        goto L_0x00a3;
    L_0x02e1:
        r18 = 0;
        com.klye.ime.latin.M.ac1 = r18;
        r0 = r22;
        r0 = r0.mCapsLock;
        r18 = r0;
        if (r18 != 0) goto L_0x0306;
    L_0x02ed:
        r18 = 1;
    L_0x02ef:
        r0 = r18;
        r1 = r22;
        r1.mCapsLock = r0;
        r0 = r22;
        r0 = r0.mKeyboardSwitcher;
        r18 = r0;
        r0 = r22;
        r0 = r0.mCapsLock;
        r19 = r0;
        r18.setShiftLocked(r19);
        goto L_0x00a3;
    L_0x0306:
        r18 = 0;
        goto L_0x02ef;
    L_0x0309:
        r18 = com.klye.ime.latin.M.ac1;
        if (r18 == 0) goto L_0x0313;
    L_0x030d:
        r18 = 0;
        com.klye.ime.latin.M.ac1 = r18;
        goto L_0x00a3;
    L_0x0313:
        if (r8 != 0) goto L_0x00a3;
    L_0x0315:
        r22.handleShift();
        goto L_0x00a3;
    L_0x031a:
        r18 = com.klye.ime.latin.M.mLC;
        r19 = 7995496; // 0x7a0068 float:1.1204076E-38 double:3.9503E-317;
        r0 = r18;
        r1 = r19;
        if (r0 == r1) goto L_0x032b;
    L_0x0325:
        r18 = com.klye.ime.latin.M.hw();
        if (r18 == 0) goto L_0x033b;
    L_0x032b:
        r18 = com.klye.ime.latin.M.JF;
        r18 = r18 + 1;
        r18 = r18 % 2;
        com.klye.ime.latin.M.JF = r18;
        com.klye.ime.latin.M.rjf();
        r22.updateSuggestions();
        goto L_0x00a3;
    L_0x033b:
        r18 = com.klye.ime.latin.M.isLatinC;
        if (r18 == 0) goto L_0x00a3;
    L_0x033f:
        r18 = com.klye.ime.latin.M.fdb;
        if (r18 != 0) goto L_0x036d;
    L_0x0343:
        r18 = 1;
    L_0x0345:
        com.klye.ime.latin.M.fdb = r18;
        r18 = new java.lang.StringBuilder;
        r18.<init>();
        r19 = "εχ๑тïς : ";
        r19 = r18.append(r19);
        r18 = com.klye.ime.latin.M.fdb;
        if (r18 == 0) goto L_0x0370;
    L_0x0356:
        r18 = "On";
    L_0x0358:
        r0 = r19;
        r1 = r18;
        r18 = r0.append(r1);
        r18 = r18.toString();
        r0 = r22;
        r1 = r18;
        com.klye.ime.latin.M.msg(r0, r1);
        goto L_0x00a3;
    L_0x036d:
        r18 = 0;
        goto L_0x0345;
    L_0x0370:
        r18 = "Off";
        goto L_0x0358;
    L_0x0373:
        r22.ta();
        goto L_0x00a3;
    L_0x0378:
        r18 = com.klye.ime.latin.M.h;
        if (r18 == 0) goto L_0x0393;
    L_0x037c:
        r18 = com.klye.ime.latin.M.h;
        r18 = r18.isOK();
        if (r18 != 0) goto L_0x0393;
    L_0x0384:
        r18 = com.klye.ime.latin.M.hw();
        if (r18 == 0) goto L_0x0393;
    L_0x038a:
        r18 = com.klye.ime.latin.M.mIme;
        r19 = 0;
        r18.toggleHW(r19);
        goto L_0x00a3;
    L_0x0393:
        r18 = -1;
        r0 = r22;
        r1 = r18;
        r0.changeKeyboardMode(r1);
        goto L_0x00a3;
    L_0x039e:
        r22.tkl();
        goto L_0x00a3;
    L_0x03a3:
        r22.toggleHW();
        goto L_0x00a3;
    L_0x03a8:
        r18 = com.klye.ime.latin.M.emjAM();
        if (r18 == 0) goto L_0x00a3;
    L_0x03ae:
        r18 = r23 + 120;
        r0 = r22;
        r1 = r18;
        r0.changeKeyboardMode(r1);
        goto L_0x00a3;
    L_0x03b9:
        com.klye.ime.latin.M.ex();
        goto L_0x00a3;
    L_0x03be:
        r22.mdf(r23);
        goto L_0x00a3;
    L_0x03c3:
        r22.onOptionKeyPressed();
        goto L_0x00a3;
    L_0x03c8:
        r18 = com.klye.ime.latin.M.sS();
        r0 = r22;
        r1 = r18;
        com.klye.ime.latin.M.sa(r0, r1);
        goto L_0x00a3;
    L_0x03d5:
        r18 = "hw.html";
        r18 = com.klye.ime.latin.M.hp(r18);
        r0 = r22;
        r1 = r18;
        com.klye.ime.latin.M.sa(r0, r1);
        goto L_0x00a3;
    L_0x03e4:
        r22.onOptionKeyLongPressed();
        goto L_0x00a3;
    L_0x03e9:
        r18 = 0;
        r19 = 1;
        r0 = r22;
        r1 = r18;
        r2 = r19;
        r0.toggleLanguage(r1, r2);
        goto L_0x00a3;
    L_0x03f8:
        r18 = 0;
        r19 = 0;
        r0 = r22;
        r1 = r18;
        r2 = r19;
        r0.toggleLanguage(r1, r2);
        goto L_0x00a3;
    L_0x0407:
        r22.vr();
        goto L_0x00a3;
    L_0x040c:
        r18 = 61;
        r0 = r22;
        r1 = r18;
        r0.sendDownUpKeyEvents(r1);
        goto L_0x00a3;
    L_0x0417:
        r18 = 90;
        r0 = r18;
        if (r15 != r0) goto L_0x0434;
    L_0x041d:
        r18 = com.klye.ime.latin.M.h;
        if (r18 == 0) goto L_0x0429;
    L_0x0421:
        r18 = com.klye.ime.latin.M.h;
        r18 = r18.isOK();
        if (r18 != 0) goto L_0x0434;
    L_0x0429:
        r18 = "如要使用聲調，請下載【漢書】插件…";
        r0 = r22;
        r1 = r18;
        com.klye.ime.latin.M.msg(r0, r1);
        goto L_0x00a3;
    L_0x0434:
        r18 = 2;
        r19 = 0;
        r0 = r18;
        r1 = r19;
        r11 = r9.getTextBeforeCursor(r0, r1);
        r18 = com.klye.ime.latin.M.mLC;
        r19 = 6815865; // 0x680079 float:9.551061E-39 double:3.3674847E-317;
        r0 = r18;
        r1 = r19;
        if (r0 != r1) goto L_0x0148;
    L_0x044b:
        if (r11 == 0) goto L_0x0148;
    L_0x044d:
        r18 = r11.length();
        r19 = 2;
        r0 = r18;
        r1 = r19;
        if (r0 != r1) goto L_0x0148;
    L_0x0459:
        r18 = 0;
        r0 = r18;
        r18 = r11.charAt(r0);
        r0 = r18;
        r1 = r23;
        if (r0 != r1) goto L_0x0148;
    L_0x0467:
        r18 = 1;
        r0 = r18;
        r18 = r11.charAt(r0);
        r0 = r18;
        r1 = r23;
        if (r0 != r1) goto L_0x0148;
    L_0x0475:
        r18 = 2;
        r19 = 0;
        r0 = r18;
        r1 = r19;
        r9.deleteSurroundingText(r0, r1);
        r23 = 8230; // 0x2026 float:1.1533E-41 double:4.066E-320;
        goto L_0x0148;
    L_0x0484:
        r18 = com.klye.ime.latin.M.isCJ();
        if (r18 == 0) goto L_0x04ac;
    L_0x048a:
        r0 = r22;
        r0 = r0.mWord;
        r18 = r0;
        r0 = r18;
        r0 = r0.mTW;
        r18 = r0;
        r18 = r18.length();
        if (r18 <= 0) goto L_0x04ac;
    L_0x049c:
        r0 = r22;
        r0.commitTyped(r9);
        r0 = r22;
        r0 = r0.mWord;
        r18 = r0;
        r18.reset();
        goto L_0x0006;
    L_0x04ac:
        r18 = com.klye.ime.latin.M.mLC;
        r19 = 7077994; // 0x6c006a float:9.918382E-39 double:3.4969937E-317;
        r0 = r18;
        r1 = r19;
        if (r0 != r1) goto L_0x0560;
    L_0x04b7:
        r18 = " \n";
        r0 = r18;
        r1 = r23;
        r18 = r0.indexOf(r1);
        r19 = -1;
        r0 = r18;
        r1 = r19;
        if (r0 != r1) goto L_0x0560;
    L_0x04c9:
        r10 = 0;
    L_0x04ca:
        r0 = r23;
        r0 = (char) r0;
        r18 = r0;
        r18 = com.klye.ime.latin.H.IsHWDigit(r18);
        if (r18 != 0) goto L_0x05aa;
    L_0x04d5:
        if (r10 != 0) goto L_0x05aa;
    L_0x04d7:
        if (r14 == 0) goto L_0x05aa;
    L_0x04d9:
        r18 = r14.isShifted();
        if (r18 == 0) goto L_0x05aa;
    L_0x04df:
        r18 = com.klye.ime.latin.M.ncu(r23);
        if (r18 != 0) goto L_0x05aa;
    L_0x04e5:
        r18 = com.klye.ime.latin.M.mLC;
        r19 = 6946913; // 0x6a0061 float:9.734699E-39 double:3.432231E-317;
        r0 = r18;
        r1 = r19;
        if (r0 == r1) goto L_0x05aa;
    L_0x04f0:
        r18 = com.klye.ime.latin.M.bho;
        if (r18 != 0) goto L_0x05aa;
    L_0x04f4:
        r0 = r22;
        r0 = r0.mAutoCap;
        r18 = r0;
        if (r18 != 0) goto L_0x0500;
    L_0x04fc:
        r18 = com.klye.ime.latin.M.hk;
        if (r18 != 0) goto L_0x05aa;
    L_0x0500:
        r18 = com.klye.ime.latin.M.kid;
        r19 = 2131034122; // 0x7f05000a float:1.7678753E38 double:1.05287075E-314;
        r18 = r18.is(r19);
        if (r18 != 0) goto L_0x05aa;
    L_0x050b:
        if (r24 == 0) goto L_0x05aa;
    L_0x050d:
        r18 = 0;
        r18 = r24[r18];
        if (r18 < 0) goto L_0x05aa;
    L_0x0513:
        r18 = 0;
        r18 = r24[r18];
        r19 = 1114111; // 0x10ffff float:1.561202E-39 double:5.50444E-318;
        r0 = r18;
        r1 = r19;
        if (r0 > r1) goto L_0x05aa;
    L_0x0520:
        r0 = r22;
        r0 = r0.mKeyboardSwitcher;
        r18 = r0;
        r18 = r18.isAlphabetMode();
        if (r18 == 0) goto L_0x05aa;
    L_0x052c:
        r0 = r23;
        r0 = (char) r0;
        r18 = r0;
        r5 = com.klye.ime.latin.S.toUpper(r18);
        r18 = 65275; // 0xfefb float:9.147E-41 double:3.225E-319;
        r0 = r18;
        if (r5 != r0) goto L_0x056b;
    L_0x053c:
        r18 = 0;
        r19 = 1604; // 0x644 float:2.248E-42 double:7.925E-321;
        r24[r18] = r19;
        r18 = 1604; // 0x644 float:2.248E-42 double:7.925E-321;
        r0 = r22;
        r1 = r18;
        r2 = r24;
        r0.handleCharacter(r1, r2);
        r18 = 0;
        r19 = 1575; // 0x627 float:2.207E-42 double:7.78E-321;
        r24[r18] = r19;
        r18 = 1575; // 0x627 float:2.207E-42 double:7.78E-321;
        r0 = r22;
        r1 = r18;
        r2 = r24;
        r0.handleCharacter(r1, r2);
        goto L_0x0006;
    L_0x0560:
        r0 = r23;
        r0 = (char) r0;
        r18 = r0;
        r10 = com.klye.ime.latin.M.isWordSep(r18);
        goto L_0x04ca;
    L_0x056b:
        r0 = r23;
        if (r5 == r0) goto L_0x057e;
    L_0x056f:
        r23 = r5;
        r18 = com.klye.ime.latin.M.noAutoCapC;
        if (r18 == 0) goto L_0x05aa;
    L_0x0575:
        r18 = 0;
        r24[r18] = r23;
        r22.handleCharacter(r23, r24);
        goto L_0x0006;
    L_0x057e:
        r18 = new java.lang.String;
        r19 = 1;
        r0 = r19;
        r0 = new int[r0];
        r19 = r0;
        r20 = 0;
        r19[r20] = r23;
        r20 = 0;
        r21 = 1;
        r18.<init>(r19, r20, r21);
        r13 = r18.toUpperCase();
        r18 = r13.length();
        r19 = 1;
        r0 = r18;
        r1 = r19;
        if (r0 <= r1) goto L_0x05aa;
    L_0x05a3:
        r0 = r22;
        r0.onText(r13);
        goto L_0x0006;
    L_0x05aa:
        r18 = com.klye.ime.latin.M.mLC;
        r19 = 6946913; // 0x6a0061 float:9.734699E-39 double:3.432231E-317;
        r0 = r18;
        r1 = r19;
        if (r0 != r1) goto L_0x063a;
    L_0x05b5:
        if (r14 == 0) goto L_0x05c6;
    L_0x05b7:
        r18 = r14.isShifted();
        if (r18 == 0) goto L_0x05c6;
    L_0x05bd:
        r0 = r23;
        r0 = (char) r0;
        r18 = r0;
        r23 = com.klye.ime.latin.S.toUpper(r18);
    L_0x05c6:
        r0 = r22;
        r0 = r0.mKeyboardSwitcher;
        r18 = r0;
        r18.invalidate();
    L_0x05cf:
        r18 = 10;
        r0 = r23;
        r1 = r18;
        if (r0 == r1) goto L_0x05df;
    L_0x05d7:
        r18 = 0;
        r0 = r18;
        r1 = r22;
        r1.mJustAddedAutoSpace = r0;
    L_0x05df:
        r18 = com.klye.ime.latin.LatinIMEUtil.RingCharBuffer.getInstance();
        r0 = r23;
        r0 = (char) r0;
        r19 = r0;
        r0 = r18;
        r1 = r19;
        r2 = r25;
        r3 = r26;
        r0.push(r1, r2, r3);
        if (r10 == 0) goto L_0x0680;
    L_0x05f5:
        r0 = r22;
        r0 = r0.mPredicting;
        r18 = r0;
        if (r18 == 0) goto L_0x066f;
    L_0x05fd:
        r18 = com.klye.ime.latin.M.isCJ();
        if (r18 == 0) goto L_0x066f;
    L_0x0603:
        r18 = 32;
        r0 = r23;
        r1 = r18;
        if (r0 != r1) goto L_0x066f;
    L_0x060b:
        r18 = com.klye.ime.latin.M.cjsp;
        if (r18 == 0) goto L_0x061b;
    L_0x060f:
        r0 = r22;
        r0 = r0.mCandidateView;
        r18 = r0;
        r18 = r18.next();
        if (r18 != 0) goto L_0x0630;
    L_0x061b:
        r0 = r22;
        r0 = r0.mCandidateView;
        r18 = r0;
        r12 = r18.pick1stSuggestion();
        if (r12 == 0) goto L_0x0669;
    L_0x0627:
        r18 = 0;
        r0 = r22;
        r1 = r18;
        r0.pickSuggestionManually(r1, r12);
    L_0x0630:
        r18 = 0;
        r0 = r18;
        r1 = r22;
        r1.mJustRevertedSeparator = r0;
        goto L_0x00a3;
    L_0x063a:
        r0 = r23;
        r0 = (char) r0;
        r18 = r0;
        r18 = com.klye.ime.latin.M.thTone(r18);
        if (r18 == 0) goto L_0x05cf;
    L_0x0645:
        r18 = 1;
        r19 = 0;
        r0 = r18;
        r1 = r19;
        r7 = r9.getTextBeforeCursor(r0, r1);
        if (r7 == 0) goto L_0x05cf;
    L_0x0653:
        r18 = r7.length();
        if (r18 <= 0) goto L_0x05cf;
    L_0x0659:
        r18 = 0;
        r0 = r18;
        r18 = r7.charAt(r0);
        r18 = com.klye.ime.latin.M.thTone(r18);
        if (r18 == 0) goto L_0x05cf;
    L_0x0667:
        goto L_0x0006;
    L_0x0669:
        r0 = r22;
        r0.commitTyped(r9);
        goto L_0x0630;
    L_0x066f:
        r0 = r23;
        r0 = (char) r0;
        r18 = r0;
        r18 = com.klye.ime.latin.M.toPunc(r18);
        r0 = r22;
        r1 = r18;
        r0.handleSeparator(r1);
        goto L_0x0630;
    L_0x0680:
        r22.handleCharacter(r23, r24);
        goto L_0x0630;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.klye.ime.latin.LatinIME.onKey1(int, int[], int, int):void");
    }

    private void ta() {
        boolean z = ENABLE_VOICE_BUTTON;
        if (M.mLC == M.ko) {
            M.msg(this, M.hj());
            M.mHanja = (M.mHanja + 1) % 2;
            M.rmb("HJ", M.mHanja);
            updateSuggestions();
        } else if (M.arConAble()) {
            if (M.mArConn) {
                z = false;
            }
            M.mArConn = z;
            M.rmb(M.car, M.mArConn);
            M.msg(this, "ع ⇄ ـﻌـ : " + (M.mArConn ? "On" : "Off"));
        } else if (M.isCJ()) {
            M.wfw = (M.wfw + 1) % 2;
            M.rmb("wfw", M.wfw);
            M.psbl();
        } else if (M.mSms) {
            if (M.mSms) {
                z = false;
            }
            M.mSms = z;
            M.rmb("sms", M.mSms);
            M.msg(this, "À→A : " + (M.mSms ? "On" : "Off"));
        } else {
            snad();
        }
    }

    private void ins(InputConnection ic, String s) {
        ic.commitText(new SimpleDateFormat(s).format(new Date(System.currentTimeMillis())), 1);
    }

    private void vr() {
        boolean nv = false;
        if (this.mVoiceRecognitionTrigger == null) {
            this.mVoiceRecognitionTrigger = new VoiceRecognitionTrigger(this);
        }
        if (this.mVoiceRecognitionTrigger.isInstalled()) {
            this.mVoiceRecognitionTrigger.startVoiceRecognition(null);
            nv = ENABLE_VOICE_BUTTON;
        }
        if (!nv) {
            if (this.mEnableVoiceButton && VOICE_INSTALLED) {
                try {
                    startListening(false);
                    return;
                } catch (Throwable th) {
                    return;
                }
            }
            M.msg(this, "Google Voice Search is not installed");
        }
    }

    private void trans() {
        ExtractedText et = gxt();
        if (et != null) {
            M.trans(this, et.text.toString());
        }
    }

    private ExtractedText gxt() {
        InputConnection ic = ic();
        if (ic == null) {
            return null;
        }
        ExtractedTextRequest etr = new ExtractedTextRequest();
        etr.token = 0;
        return ic.getExtractedText(etr, 0);
    }

    private void pcma(int a) {
        Meta meta = M.meta;
        meta.st |= 268435456;
        InputConnection ic = ic();
        if (ic != null) {
            ic.performContextMenuAction(a);
        }
    }

    void tkl() {
        if (!M.fko()) {
            M.kmRo();
            M.isHW = 0;
            M.rmb("hw", M.isHW);
            this.mSuggest.changeParam();
            loadKeyboard();
        }
    }

    void ts() {
        boolean z = ENABLE_VOICE_BUTTON;
        if (M.isLand()) {
            if (M.sm) {
                z = false;
            }
            M.sm = z;
            M.sm3 = z;
            M.rmb(M.sm1, M.sm);
            loadKeyboard();
            return;
        }
        if (M.sm2) {
            z = false;
        }
        M.sm2 = z;
        M.sm3 = z;
        M.rmb(M.sm2s, M.sm2);
        loadKeyboard();
    }

    void tklt9() {
        if (!M.fko() && M.isHW != 1) {
            M.kmRo();
            if (M.km() == 2) {
                M.kmRo();
            }
            this.mSuggest.changeParam();
            loadKeyboard();
        }
    }

    void tklComp() {
        if (!M.fko() && M.isHW != 1) {
            M.kmRo();
            if (M.km() == 1) {
                M.kmRo();
            }
            this.mSuggest.changeParam();
            loadKeyboard();
        }
    }

    public void toggleHW() {
        toggleHW((M.isHW + 1) % 2);
    }

    public void toggleHW(int i) {
        M.isHW = i;
        M.rmb("hw", M.isHW);
        loadKeyboard();
    }

    public void onText(CharSequence cs) {
        if (VOICE_INSTALLED && this.mVoiceInputHighlighted) {
            commitVoiceInput();
        }
        InputConnection ic = ic();
        if (ic != null) {
            abortCorrection(false);
            ic.beginBatchEdit();
            if (this.mPredicting) {
                commitTyped(ic);
            }
            maybeRemovePreviousPeriod(cs);
            if (M.nose != '-') {
                cs = M.to(cs, 45);
            }
            if (M.fdb) {
                cs = M.fd(cs);
            }
            ic.commitText(cs, 1);
            ic.endBatchEdit();
            updateShiftKeyState(getCurrentInputEditorInfo());
            this.mJustRevertedSeparator = null;
            this.mJustAddedAutoSpace = false;
        }
    }

    public void onCancel() {
    }

    public void handleBackspace() {
        if (VOICE_INSTALLED && this.mVoiceInputHighlighted) {
            this.mVoiceInput.incrementTextModificationDeleteCount(((String) this.mVoiceResults.candidates.get(0)).toString().length());
            revertVoiceInput();
            return;
        }
        boolean deleteChar = false;
        InputConnection ic = ic();
        if (ic != null) {
            ic.beginBatchEdit();
            if (this.mAfterVoiceInput && this.mVoiceInput.getCursorPos() > 0) {
                this.mVoiceInput.incrementTextModificationDeleteCount(this.mVoiceInput.getSelectionSpan() > 0 ? this.mVoiceInput.getSelectionSpan() : 1);
            }
            if (!this.mPredicting) {
                M.pw1 = null;
                this.mPrevWord2 = null;
                deleteChar = ENABLE_VOICE_BUTTON;
            } else if (this.mWord.mTW.length() > 0) {
                this.mWord.deleteLast();
                sct(ic);
                if (this.mWord.mTW.length() == 0) {
                    this.mPredicting = false;
                }
                postUpdateSuggestions();
            } else {
                ic.deleteSurroundingText(1, 0);
            }
            if (M.fk()) {
                postUpdateShiftKeyState();
            } else {
                updateShiftKeyState(getCurrentInputEditorInfo());
            }
            TextEntryState.backspace();
            if (TextEntryState.getState() == State.UNDO_COMMIT) {
                revertLastWord(deleteChar);
                ic.endBatchEdit();
                return;
            }
            if (deleteChar) {
                if (this.mCandidateView == null || !this.mCandidateView.dismissAddToDictionaryHint()) {
                    sendDownUpKeyEvents(67);
                    if (this.mDeleteCount > DELETE_ACCELERATE_AT) {
                        sendDownUpKeyEvents(67);
                    }
                } else {
                    revertLastWord(deleteChar);
                }
            }
            this.mJustRevertedSeparator = null;
            ic.endBatchEdit();
        }
    }

    private void resetShift() {
        handleShiftInternal(ENABLE_VOICE_BUTTON);
    }

    private void handleShift() {
        handleShiftInternal(false);
    }

    private void handleShiftInternal(boolean forceNormal) {
        this.mHandler.removeMessages(2);
        KeyboardSwitcher switcher = this.mKeyboardSwitcher;
        LatinKeyboardView inputView = M.iv;
        if (switcher.isAlphabetMode()) {
            if (M.jfs1()) {
                M.JF = (M.JF + 1) % 2;
                M.rjf();
                updateSuggestions();
            } else if (this.mCapsLock || forceNormal || (M.jfs() && M.JF == 1)) {
                this.mCapsLock = false;
                psa();
                switcher.setShifted(false);
            } else if (inputView == null) {
            } else {
                if (!inputView.isShifted() && !M.wantlock() && !switcher.isEditPad()) {
                    switcher.setShifted(ENABLE_VOICE_BUTTON);
                } else if ((M.mLC != M.ko && !M.mAltCaps) || switcher.isEditPad() || M.wantlock()) {
                    this.mCapsLock = ENABLE_VOICE_BUTTON;
                    psa();
                    switcher.setShiftLocked(ENABLE_VOICE_BUTTON);
                } else {
                    switcher.setShifted(false);
                }
            }
        } else if (forceNormal) {
            switcher.setShifted(false);
        } else {
            switcher.toggleShift();
        }
    }

    private void psa() {
        if (M.mLC == M.ja) {
            sct();
            updateSuggestions();
        }
    }

    private void abortCorrection(boolean force) {
        if (force || TextEntryState.isCorrecting()) {
            ic().finishComposingText();
            clearSuggestions();
        }
    }

    private void handleCharacter(int pc, int[] keyCodes) {
        hc(pc, keyCodes, ENABLE_VOICE_BUTTON);
    }

    private void hc(int pc, int[] keyCodes, boolean exp) {
        if (exp) {
            String s = M.ls1 ? M.lsmap(pc) : (M.mLC == M.yi && this.mShowSuggestions) ? null : M.cc(pc);
            if (!this.mPredicting) {
                if (M.ls1) {
                    s = M.lsIme(s);
                } else if (M.mLC == M.s4) {
                    s = M.s4Ime(s);
                }
            }
            if (s != null) {
                handleCharacters(s);
                return;
            }
        }
        if (VOICE_INSTALLED && this.mVoiceInputHighlighted) {
            commitVoiceInput();
        }
        if (this.mAfterVoiceInput) {
            this.mVoiceInput.incrementTextModificationInsertCount(1);
        }
        if (this.mLastSelectionStart == this.mLastSelectionEnd && TextEntryState.isCorrecting()) {
            abortCorrection(false);
        }
        if ((((isAlphabet((char) pc) || M.ss(pc) || pc == KEYCODE_PERIOD) && (isPredictionOn() || M.np())) || M.isT9Semi() || (M.zt == 66 && M.irn((char) pc))) && !this.mPredicting && (M.kid == null || !M.kid.isPh())) {
            this.mPredicting = ENABLE_VOICE_BUTTON;
            this.mWord.mTW.setLength(0);
            saveWordInHistory(this.mBestWord);
            this.mWord.reset();
        }
        InputConnection ic = ic();
        if (ic != null) {
            if (this.mPredicting) {
                LatinKeyboardView v = M.iv;
                if (v != null && v.isShifted() && this.mKeyboardSwitcher.isAlphabetMode() && this.mWord.mTW.length() == 0) {
                    this.mWord.setFirstCharCapitalized(ENABLE_VOICE_BUTTON);
                }
                char c1;
                if (M.mLC != M.ja || M.hw()) {
                    if (M.mLC == M.ko) {
                        int l = this.mWord.mOTW.length();
                        l = this.mWord.mTW.length();
                        if (l > 6) {
                            ic.commitText(this.mWord.mTW.subSequence(0, l - 1), 1);
                            c1 = this.mWord.mTW.charAt(l - 1);
                            this.mWord.reset();
                            if (H.IsHangul(c1)) {
                                c1 = (char) (c1 - 44032);
                                this.mWord.add2(Ko.koI(c1 / 588), null);
                                c1 = (char) (c1 % 588);
                                this.mWord.add2((c1 / 28) + 12623, null);
                                int i = c1 % 28;
                                if (i != 0) {
                                    this.mWord.add2(Ko.koF(i), null);
                                }
                            } else {
                                this.mWord.add2(c1, null);
                            }
                        }
                        this.mWord.add2(pc, keyCodes);
                    } else if (M.mLC == M.vi) {
                        this.mWord.add3(pc, keyCodes);
                    } else {
                        this.mWord.add1(pc, keyCodes);
                    }
                } else if (M.t9()) {
                    int i1;
                    if (keyCodes == null || keyCodes.length < 6) {
                        keyCodes = new int[6];
                        keyCodes = new int[]{-1, -1, -1, -1, -1, -1};
                    }
                    char c = (char) pc;
                    int i12 = 0 + 1;
                    keyCodes[0] = c;
                    char C = S.toUpper(c);
                    if (C != c) {
                        i1 = i12 + 1;
                        keyCodes[i12] = C;
                    } else {
                        i1 = i12;
                    }
                    c1 = M.jaTog(c);
                    if (c != c1) {
                        i12 = i1 + 1;
                        keyCodes[i1] = c1;
                        C = S.toUpper(c1);
                        if (C != c1) {
                            i1 = i12 + 1;
                            keyCodes[i12] = C;
                        } else {
                            i1 = i12;
                        }
                        char c2 = M.jaTog(c1);
                        if (!(c2 == c || c2 == c1)) {
                            i12 = i1 + 1;
                            keyCodes[i1] = c2;
                            C = S.toUpper(c2);
                            if (C != c2) {
                                i1 = i12 + 1;
                                keyCodes[i12] = C;
                            } else {
                                i1 = i12;
                            }
                        }
                    }
                    this.mWord.add1(pc, keyCodes);
                } else {
                    this.mWord.add1(pc, keyCodes);
                }
                if (ic != null) {
                    if (this.mWord.size() == 1) {
                        this.mWord.setAutoCapitalized(getCursorCapsMode(ic, getCurrentInputEditorInfo()) != 0 ? ENABLE_VOICE_BUTTON : false);
                    }
                    if (!(M.isT9Semi() && !M.mt9() && this.mShowSuggestions && M.ko != M.mLC && M.zt == -1)) {
                        sct(ic);
                    }
                }
                postUpdateSuggestions();
            } else {
                if (M.fdb) {
                    pc = M.fd((char) pc);
                }
                sendKeyChar((char) pc);
                pdk(ic);
            }
            updateShiftKeyState(getCurrentInputEditorInfo());
            TextEntryState.typedCharacter((char) pc, M.isWordSep((char) pc));
        }
    }

    private void pdk(InputConnection ic) {
        CharSequence c = ic.getTextBeforeCursor(3, 0);
        if (c != null) {
            int l = c.length();
            if (l > 1) {
                char charAt;
                char c2 = c.charAt(l - 2);
                char c3 = c.charAt(l - 1);
                if (l == 3) {
                    charAt = c.charAt(0);
                } else {
                    charAt = 0;
                }
                char r = M.pdk(c2, c3, charAt);
                if (r != 0) {
                    ic.deleteSurroundingText(c2 == '\'' ? 3 : 2, 0);
                    if (M.ir(r, 772, 774)) {
                        sendKeyChar(c3);
                        sendKeyChar(r);
                    } else if (r == 1) {
                        sendKeyChar(c3);
                        sendKeyChar(c2);
                    } else {
                        sendKeyChar(r);
                    }
                    pdk(ic);
                }
            }
        }
    }

    private void handleCharacters(String s) {
        int l = s.length();
        for (int i = 0; i < l; i++) {
            hc(s.charAt(i), null, false);
        }
    }

    private void handleSeparator(int pc) {
        if (VOICE_INSTALLED && this.mVoiceInputHighlighted) {
            commitVoiceInput();
        }
        if (this.mAfterVoiceInput) {
            this.mVoiceInput.incrementTextModificationInsertPunctuationCount(1);
        }
        if (this.mCandidateView != null && this.mCandidateView.dismissAddToDictionaryHint()) {
            postUpdateSuggestions();
        }
        boolean pickedDefault = false;
        InputConnection ic = ic();
        if (ic != null) {
            ic.beginBatchEdit();
            abortCorrection(false);
        }
        boolean spaceOK = ENABLE_VOICE_BUTTON;
        if (this.mPredicting) {
            spaceOK = (M.cjsp && M.noSpace()) ? false : ENABLE_VOICE_BUTTON;
            if (this.mShowSuggestions && this.mAutoCorrectOn && pc != 39 && (this.mJustRevertedSeparator == null || this.mJustRevertedSeparator.length() == 0 || this.mJustRevertedSeparator.charAt(0) != pc)) {
                pickedDefault = pickDefaultSuggestion();
                if (pc == 32) {
                    this.mJustAddedAutoSpace = ENABLE_VOICE_BUTTON;
                } else {
                    M.pw1 = null;
                    this.mPrevWord2 = null;
                }
            } else {
                commitTyped(ic);
            }
        } else {
            M.pw1 = null;
            this.mPrevWord2 = null;
        }
        if (this.mJustAddedAutoSpace && pc == 10) {
            removeTrailingSpace();
            this.mJustAddedAutoSpace = false;
        }
        if (pc != 32 || spaceOK) {
            sendKeyChar((char) pc);
        }
        if (TextEntryState.getState() == State.PUNCTUATION_AFTER_ACCEPTED && pc == KEYCODE_PERIOD) {
            reswapPeriodAndSpace();
        }
        TextEntryState.typedCharacter((char) pc, ENABLE_VOICE_BUTTON);
        if (TextEntryState.getState() != State.PUNCTUATION_AFTER_ACCEPTED || pc == 10) {
            if (pc == 32 && M.wds()) {
                doubleSpace();
            }
        } else if (M.swp && M.mLC != M.lj) {
            swapPunctuationAndSpace();
        }
        if (pickedDefault) {
            TextEntryState.backToAcceptedDefault(this.mWord.getTypedWord());
        }
        updateShiftKeyState(getCurrentInputEditorInfo());
        switch (pc) {
            case 32:
                ss2();
                break;
            case KEYCODE_PERIOD /*46*/:
                ss(M.dmn2);
                break;
        }
        if (ic != null) {
            ic.endBatchEdit();
        }
    }

    private void ss2() {
        if (!M.bgd) {
            ss(M.psbl);
        } else if (M.dicB != null && M.pw1 != null) {
            ss(this.mSuggest.bggs(M.pw1));
        }
    }

    private void handleClose() {
        if ((VOICE_INSTALLED & this.mRecognizing) != 0) {
            this.mVoiceInput.cancel();
        }
        requestHideSelf(0);
        if (this.mKeyboardSwitcher != null) {
            LatinKeyboardView inputView = M.iv;
            if (inputView != null) {
                inputView.closing();
            }
        }
        M.dicClose();
        TextEntryState.endSession();
    }

    private void saveWordInHistory(CharSequence result) {
        if (this.mWord.size() <= 1) {
            this.mWord.reset();
        } else if (!TextUtils.isEmpty(result)) {
            this.mWordHistory.add(new TypedWordAlternatives(result.toString(), new WordComposer(this.mWord)));
        }
    }

    private void pus(int t) {
        if (M.zt != -1) {
            t += t / 4;
        }
        this.mHandler.removeMessages(0);
        this.mHandler.sendMessageDelayed(this.mHandler.obtainMessage(0), (long) t);
    }

    private void postUpdateSuggestions() {
        if (isPredictionOn()) {
            pus(this.mWord.size() > 1 ? 100 : 300);
        }
    }

    private void postUpdateOldSuggestions() {
        this.mHandler.removeMessages(4);
        this.mHandler.sendMessageDelayed(this.mHandler.obtainMessage(4), 300);
    }

    public void pex() {
        handleClose();
        this.mHandler.removeMessages(5);
        this.mHandler.sendMessageDelayed(this.mHandler.obtainMessage(5), 1000);
    }

    private boolean isPredictionOn() {
        return ((this.mPredictionOn && M.oo) || M.mLC == M.zh) ? ENABLE_VOICE_BUTTON : false;
    }

    public void onCancelVoice() {
        if (this.mRecognizing) {
            switchToKeyboardView();
        }
    }

    private void switchToKeyboardView() {
        this.mHandler.post(new Runnable() {
            public void run() {
                try {
                    LatinIME.this.mRecognizing = false;
                    if (M.iv != null) {
                        LatinIME.this.setInputView(M.iv);
                    }
                } catch (Throwable th) {
                }
                LatinIME.this.scvs(LatinIME.ENABLE_VOICE_BUTTON);
                LatinIME.this.updateInputViewShown();
                LatinIME.this.postUpdateSuggestions();
            }
        });
    }

    private void switchToRecognitionStatusView() {
        final boolean configChanged = this.mConfigurationChanging;
        this.mHandler.post(new Runnable() {
            public void run() {
                LatinIME.this.scvs(false);
                LatinIME.this.mRecognizing = LatinIME.ENABLE_VOICE_BUTTON;
                View v = LatinIME.this.mVoiceInput.getView();
                ViewParent p = v.getParent();
                if (p != null && (p instanceof ViewGroup)) {
                    ((ViewGroup) v.getParent()).removeView(v);
                }
                LatinIME.this.setInputView(v);
                LatinIME.this.updateInputViewShown();
                if (configChanged) {
                    LatinIME.this.mVoiceInput.onConfigurationChanged();
                }
            }
        });
    }

    private void startListening(boolean swipe) {
        if (this.mHasUsedVoiceInput && (this.mLocaleSupportedForVoiceInput || this.mHasUsedVoiceInputUnsupportedLocale)) {
            rsl(swipe);
        } else {
            showVoiceWarningDialog(swipe);
        }
    }

    private void rsl(boolean swipe) {
        if (M.zhType() != -1) {
            Builder builder = new Builder(this);
            builder.setCancelable(ENABLE_VOICE_BUTTON);
            builder.setNegativeButton(17039360, null);
            builder.setItems(new CharSequence[]{"香港", "大陸", "臺灣"}, new OnClickListener() {
                public void onClick(DialogInterface di, int position) {
                    di.dismiss();
                    M.zhVo = position;
                    LatinIME.this.reallyStartListening(false);
                }
            });
            builder.setTitle(M.res.getString(R.string.voice_input));
            this.mOptionsDialog = builder.create();
            Window window = this.mOptionsDialog.getWindow();
            LayoutParams lp = window.getAttributes();
            lp.token = M.iv.getWindowToken();
            lp.type = 1003;
            window.setAttributes(lp);
            window.addFlags(131072);
            this.mOptionsDialog.show();
            return;
        }
        reallyStartListening(swipe);
    }

    private void reallyStartListening(boolean swipe) {
        Editor editor;
        if (!this.mHasUsedVoiceInput) {
            editor = PreferenceManager.getDefaultSharedPreferences(this).edit();
            editor.putBoolean(PREF_HAS_USED_VOICE_INPUT, ENABLE_VOICE_BUTTON);
            SharedPreferencesCompat.apply(editor);
            this.mHasUsedVoiceInput = ENABLE_VOICE_BUTTON;
        }
        if (!(this.mLocaleSupportedForVoiceInput || this.mHasUsedVoiceInputUnsupportedLocale)) {
            editor = PreferenceManager.getDefaultSharedPreferences(this).edit();
            editor.putBoolean(PREF_HAS_USED_VOICE_INPUT_UNSUPPORTED_LOCALE, ENABLE_VOICE_BUTTON);
            SharedPreferencesCompat.apply(editor);
            this.mHasUsedVoiceInputUnsupportedLocale = ENABLE_VOICE_BUTTON;
        }
        clearSuggestions();
        this.mVoiceInput.startListening(new FieldContext(ic(), getCurrentInputEditorInfo(), M.voIL(), this.mLanguageSwitcher.getEnabledLanguages()), swipe);
        switchToRecognitionStatusView();
    }

    private void showVoiceWarningDialog(final boolean swipe) {
        Builder builder = new Builder(this);
        builder.setCancelable(ENABLE_VOICE_BUTTON);
        builder.setPositiveButton(17039370, new OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                LatinIME.this.rsl(swipe);
            }
        });
        builder.setNegativeButton(17039360, new OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
            }
        });
        if (this.mLocaleSupportedForVoiceInput) {
            builder.setMessage(getString(R.string.voice_warning_may_not_understand) + "\n\n" + getString(R.string.voice_warning_how_to_turn_off));
        } else {
            builder.setMessage(getString(R.string.voice_warning_locale_not_supported) + "\n\n" + getString(R.string.voice_warning_may_not_understand) + "\n\n" + getString(R.string.voice_warning_how_to_turn_off));
        }
        builder.setTitle(R.string.voice_warning_title);
        this.mVoiceWarningDialog = builder.create();
        Window window = this.mVoiceWarningDialog.getWindow();
        LayoutParams lp = window.getAttributes();
        lp.token = M.iv.getWindowToken();
        lp.type = 1003;
        window.setAttributes(lp);
        window.addFlags(131072);
        this.mVoiceWarningDialog.show();
    }

    private void snad() {
        Builder builder = new Builder(this);
        builder.setCancelable(ENABLE_VOICE_BUTTON);
        builder.setPositiveButton(17039370, new OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                M.mSms = !M.mSms ? LatinIME.ENABLE_VOICE_BUTTON : false;
                M.rmb("sms", M.mSms);
            }
        });
        builder.setNegativeButton(17039360, new OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
            }
        });
        builder.setMessage("Remove diacritics/accents?");
        builder.setTitle(M.sms);
        this.mVoiceWarningDialog = builder.create();
        Window window = this.mVoiceWarningDialog.getWindow();
        LayoutParams lp = window.getAttributes();
        lp.token = M.iv.getWindowToken();
        lp.type = 1003;
        window.setAttributes(lp);
        window.addFlags(131072);
        this.mVoiceWarningDialog.show();
    }

    public void onVoiceResults(List<String> candidates, Map<String, List<CharSequence>> alternatives) {
        if (this.mRecognizing) {
            this.mVoiceResults.candidates = candidates;
            this.mVoiceResults.alternatives = alternatives;
            this.mHandler.sendMessage(this.mHandler.obtainMessage(3));
        }
    }

    private void handleVoiceResults() {
        this.mAfterVoiceInput = ENABLE_VOICE_BUTTON;
        this.mImmediatelyAfterVoiceInput = ENABLE_VOICE_BUTTON;
        InputConnection ic = ic();
        if (!(isFullscreenMode() || ic == null)) {
            ic.getExtractedText(new ExtractedTextRequest(), 1);
        }
        vibrate();
        switchToKeyboardView();
        List<CharSequence> nBest = new ArrayList();
        boolean capitalizeFirstWord = (preferCapitalization() || (this.mKeyboardSwitcher.isAlphabetMode() && M.iv.isShifted())) ? ENABLE_VOICE_BUTTON : false;
        for (String c : this.mVoiceResults.candidates) {
            String c2;
            if (capitalizeFirstWord) {
                c2 = Character.toUpperCase(c2.charAt(0)) + c2.substring(1, c2.length());
            }
            nBest.add(c2);
        }
        if (nBest.size() != 0) {
            String bestResult = ((CharSequence) nBest.get(0)).toString();
            this.mHints.registerVoiceResult(bestResult);
            if (ic != null) {
                ic.beginBatchEdit();
            }
            commitTyped(ic);
            EditingUtil.appendText(ic, bestResult);
            if (ic != null) {
                ic.endBatchEdit();
            }
            this.mVoiceInputHighlighted = ENABLE_VOICE_BUTTON;
            this.mWordToSuggestions.putAll(this.mVoiceResults.alternatives);
        }
    }

    private void clearSuggestions() {
        ss(M.psbl);
        this.mWord.reset();
    }

    private void setSuggestions(List<CharSequence> s, boolean completions, boolean typedWordValid, boolean haveMinimalSuggestion) {
        if (this.mIsShowingHint) {
            setCandidatesView(this.mCandidateViewContainer);
            this.mIsShowingHint = false;
        }
        if (this.mCandidateView != null) {
            this.mCandidateView.setSuggestions(s, completions, typedWordValid, haveMinimalSuggestion);
        }
    }

    private void updateSuggestions() {
        try {
            spl(null);
            if ((this.mSuggest != null && isPredictionOn()) || this.mVoiceInputHighlighted) {
                if (!(M.mLC == M.ko || M.rldc >= 5 || this.mSuggest.hasDictionary())) {
                    long t = System.currentTimeMillis();
                    if (t - M.rld > 100000) {
                        initSuggest(M.mIL);
                        M.rldc++;
                        if (this.mSuggest.hasDictionary()) {
                            M.rldc = 0;
                        }
                    }
                    M.rld = t;
                }
                if (this.mPredicting) {
                    showSuggestions(this.mWord);
                } else {
                    ss(M.psbl);
                }
            }
        } catch (Throwable e) {
            M.l(e);
        }
    }

    private List<CharSequence> getTypedSuggestions(WordComposer word) {
        return this.mSuggest.getSuggestions(M.iv, word, false, null);
    }

    private void showCorrections(WordAlternatives alternatives) {
        List<CharSequence> stringList = alternatives.getAlternatives();
        if (stringList != null) {
            spl(null);
            showSuggestions(stringList, alternatives.getOriginalWord(), false, false);
        }
    }

    private void showSuggestions(WordComposer word) {
        M.h.n = 0;
        if (this.mShowSuggestions || M.smo()) {
            String s1 = word.mTW.toString();
            int l = s1.length();
            if (l >= 1) {
                CharSequence s;
                boolean hz = (word.mTW.length() <= 0 || !H.isH(word.mTW.charAt(0))) ? false : ENABLE_VOICE_BUTTON;
                int zt = M.zhType();
                List<CharSequence> stringList = this.mSuggest.clearSuggestions();
                M.ic = ic();
                stringList = this.mSuggest.getSuggestions(M.iv, word, false, M.pw1);
                if (M.isT9Semi() && !M.mt9() && M.ko != M.mLC && M.zt == -1) {
                    if (stringList.size() > 1) {
                        s = (CharSequence) stringList.get(1);
                    } else {
                        Object s2 = s1;
                    }
                    if (s2.charAt(0) == M.zwsp) {
                        s2 = s2.subSequence(1, l + 1);
                    } else {
                        s2 = s2.subSequence(0, l);
                    }
                    M.ic.setComposingText(s2, 1);
                }
                spl(this.mSuggest.getNextLettersFrequencies());
                boolean correctionAvailable = (this.mInputTypeNoAutoCorrect || !this.mSuggest.hasMinimalCorrection()) ? false : ENABLE_VOICE_BUTTON;
                boolean typedWordValid = this.mSuggest.ivw;
                if (this.mCorrectionMode == 2 || this.mCorrectionMode == 3) {
                    correctionAvailable |= typedWordValid;
                }
                correctionAvailable = (correctionAvailable & (!word.hasCap(2) ? 1 : 0)) & (!TextEntryState.isCorrecting() ? 1 : 0);
                if (M.h != null && M.h.isOK() && word.lastChar() != '\'' && (hz || (zt != -1 && (stringList == null || stringList.size() == 0 || M.phw() || M.htm(s1))))) {
                    boolean nh = !H.isH(s1.charAt(l + -1)) ? ENABLE_VOICE_BUTTON : false;
                    if ("WB".indexOf(zt) == -1 || l <= 1 || !nh) {
                        String[] s3 = M.h.suggest(s1, M.cs(), M.hwF);
                        if (s3 != null) {
                            stringList = this.mSuggest.imprt(s3, zt, H.isH(s1.charAt(0)) ? 0 : 1);
                            if (s3.length == 1 && (l > 1 || M.cjs != 83)) {
                                if (M.jl || !H.isH(s1.charAt(l - 1)) || M.hw()) {
                                    cmmtPart(s1, l);
                                    return;
                                } else {
                                    commitTyped(ic());
                                    return;
                                }
                            }
                        }
                    }
                    cmmtPart(s1, l);
                    return;
                }
                if (stringList != null && stringList.size() == 1 && this.mWord.size() > 1 && !this.mSuggest.hasMinimalCorrection() && !M.isCJ() && M.mLC != M.vi && !M.el() && M.ss1(this.mWord.getTypedWord().charAt(0))) {
                    s2 = (CharSequence) stringList.get(0);
                    cmmtPart(s2, 1, s2.length());
                    return;
                } else if (stringList.size() > 0) {
                    showSuggestions(stringList, (CharSequence) stringList.get(0), typedWordValid, correctionAvailable);
                    return;
                } else {
                    return;
                }
            }
            return;
        }
        this.mBestWord = word.getTypedWord();
    }

    private void cmmtPart(CharSequence s, int l) {
        M.ic = ic();
        M.ic.beginBatchEdit();
        if (l == 1) {
            commitTyped(M.ic);
        } else {
            int k = l;
            do {
                k--;
                if (k <= 0) {
                    break;
                }
            } while (!H.isH(s.charAt(k - 1)));
            if (k > 0) {
                cmmtPart(s, k, l);
            } else {
                ss(null);
            }
        }
        M.ic.endBatchEdit();
    }

    private void cmmtPart(CharSequence s, int k, int l) {
        cmmt(s.subSequence(0, k), M.ic);
        this.mWord.set(k, l);
        sct();
        pus(1);
    }

    public void showSuggestions(String[] s) {
        String s1 = s[0];
        if (s1.length() != 0) {
            if (M.isLatin(s1.charAt(0))) {
                this.mPredicting = ENABLE_VOICE_BUTTON;
                this.mSuggest.clearSuggestions();
                List<CharSequence> stringList = this.mSuggest.getSuggestions(s);
                if (stringList.size() != 0) {
                    spl(this.mSuggest.getNextLettersFrequencies());
                    ss(stringList);
                    this.mBestWord = (CharSequence) stringList.get(0);
                    scvs();
                    return;
                }
                return;
            }
            showSuggestions(M.cvt(s), s[0], ENABLE_VOICE_BUTTON, ENABLE_VOICE_BUTTON);
        }
    }

    private void spl(int[] nextLettersFrequencies) {
        if (M.iv != null) {
            LatinKeyboard kb = M.iv.getLKB();
            if (kb != null) {
                kb.setPreferredLetters(nextLettersFrequencies);
            }
        }
    }

    void showSuggestions(List<CharSequence> stringList, CharSequence typedWord, boolean typedWordValid, boolean correctionAvailable) {
        int i = 1;
        if (stringList != null) {
            setSuggestions(stringList, false, typedWordValid, correctionAvailable);
            this.mBestWord = typedWord;
            if (stringList.size() > 0 && ((correctionAvailable || M.isT9Semi()) && !typedWordValid)) {
                if (stringList.size() <= 1 || M.isCJ() || M.mLC == M.ko) {
                    i = 0;
                }
                this.mBestWord = (CharSequence) stringList.get(i);
            }
            scvs();
        }
    }

    private boolean pickDefaultSuggestion() {
        if (this.mHandler.hasMessages(0)) {
            this.mHandler.removeMessages(0);
            updateSuggestions();
        }
        if (this.mBestWord == null || this.mBestWord.length() <= 0) {
            return false;
        }
        TextEntryState.acceptedDefault(this.mWord.getTypedWord(), this.mBestWord);
        this.mJustAccepted = ENABLE_VOICE_BUTTON;
        pickSuggestion(this.mBestWord, false);
        addToDictionaries(this.mBestWord, 2);
        return ENABLE_VOICE_BUTTON;
    }

    public boolean pfh(int index, CharSequence suggestion, InputConnection ic, int l) {
        if (!M.zhlx) {
            return false;
        }
        boolean eq = this.mWord.eq(suggestion);
        if (M.h == null || !M.h.isOK()) {
            return false;
        }
        if (eq && l == 1) {
            return false;
        }
        ic.beginBatchEdit();
        if (index != 0 || l <= 1 || (!eq && H.isH(suggestion.charAt(l - 1)))) {
            this.mWord.set(suggestion);
            M.hwF &= -2;
            ic.setComposingText(suggestion, 1);
            showSuggestions(this.mWord);
            M.zhFlag();
        } else {
            cmmtPart(suggestion, l);
        }
        ic.endBatchEdit();
        return ENABLE_VOICE_BUTTON;
    }

    public void pickSuggestionManually(int index, CharSequence suggestion) {
        boolean showingAddToDictionaryHint = false;
        CharSequence s = suggestion;
        InputConnection ic = ic();
        int l = suggestion.length();
        if (ic != null && l != 0) {
            char pc = suggestion.charAt(0);
            if ((l == 1 && this.mCandidateView.is(M.psbl)) || (this.mCandidateView.is(M.bgl) && index >= M.bgl.size() - M.psbl.size())) {
                onKey(pc, new int[]{pc}, -1, -1);
            } else if (M.pyt(s, l)) {
                this.mWord.set(s);
                ic.setComposingText(M.pyt(s.toString()), 1);
                postUpdateSuggestions();
            } else {
                boolean zh = H.isH(suggestion.charAt(0));
                if (M.zt != -1 && M.h.n > 0 && index > 0 && !zh) {
                    this.mWord.set(s);
                    ic.setComposingText(s, 1);
                    postUpdateSuggestions();
                } else if (!zh || !pfh(index, suggestion, ic, l)) {
                    if (this.mAfterVoiceInput && !this.mShowingVoiceSuggestions) {
                        this.mVoiceInput.flushAllTextModificationCounters();
                    }
                    boolean correcting = TextEntryState.isCorrecting();
                    if (ic != null) {
                        ic.beginBatchEdit();
                    }
                    if (!this.mCompletionOn || this.mCompletions == null || index < 0 || index >= this.mCompletions.length) {
                        if (!M.udx() && index == 0 && this.mCorrectionMode > 0 && !this.mCandidateView.isl() && !this.mSuggest.ivw && suggestion.charAt(0) >= 'A') {
                            showingAddToDictionaryHint = ENABLE_VOICE_BUTTON;
                        }
                        this.mJustAccepted = ENABLE_VOICE_BUTTON;
                        pickSuggestion(suggestion, correcting);
                        if (index == 0) {
                            addToDictionaries(suggestion, 3);
                        } else {
                            addToBigramDictionary(suggestion, 1);
                        }
                        TextEntryState.acceptedSuggestion(this.mWord.mTW.toString(), suggestion);
                        if (!(!this.mAutoSpace || s.charAt(l - 1) == '\'' || correcting || M.noSpace() || zh || (l == 1 && M.irn(pc)))) {
                            sendSpace();
                            this.mJustAddedAutoSpace = ENABLE_VOICE_BUTTON;
                        }
                        if (!correcting) {
                            TextEntryState.typedCharacter(' ', ENABLE_VOICE_BUTTON);
                        } else if (!showingAddToDictionaryHint) {
                            clearSuggestions();
                            postUpdateOldSuggestions();
                        }
                        if (showingAddToDictionaryHint) {
                            if (M.ots) {
                                addWordToDictionary(suggestion);
                            } else {
                                this.mCandidateView.showAddToDictionaryHint(suggestion);
                            }
                        }
                        if (ic != null) {
                            ic.endBatchEdit();
                        }
                        if (M.eth) {
                            M.iv.invalidateAllKeys();
                            return;
                        }
                        return;
                    }
                    CompletionInfo ci = this.mCompletions[index];
                    if (ic != null) {
                        ic.commitCompletion(ci);
                    }
                    this.mCommittedLength = l;
                    if (this.mCandidateView != null) {
                        this.mCandidateView.clear();
                    }
                    updateShiftKeyState(getCurrentInputEditorInfo());
                    if (ic != null) {
                        ic.endBatchEdit();
                    }
                }
            }
        }
    }

    private void rememberReplacedWord(CharSequence suggestion) {
        try {
            if (this.mShowingVoiceSuggestions) {
                String wordToBeReplaced = EditingUtil.getWordAtCursor(ic(), new Range());
                if (!this.mWordToSuggestions.containsKey(wordToBeReplaced)) {
                    wordToBeReplaced = wordToBeReplaced.toLowerCase();
                }
                if (this.mWordToSuggestions.containsKey(wordToBeReplaced)) {
                    List<CharSequence> suggestions = (List) this.mWordToSuggestions.get(wordToBeReplaced);
                    if (suggestions.contains(suggestion)) {
                        suggestions.remove(suggestion);
                    }
                    suggestions.add(wordToBeReplaced);
                    this.mWordToSuggestions.remove(wordToBeReplaced);
                    this.mWordToSuggestions.put(suggestion.toString(), suggestions);
                }
            }
        } catch (Throwable th) {
        }
    }

    private void pickSuggestion(CharSequence suggestion, boolean correcting) {
        LatinKeyboardView inputView = M.iv;
        InputConnection ic = ic();
        if (ic != null) {
            int l = suggestion.length();
            if (suggestion.charAt(0) == M.zwsp) {
                suggestion = suggestion.subSequence(1, l);
            }
            if (this.mCapsLock) {
                suggestion = M.toUpper(suggestion, M.mLC == M.tr ? ENABLE_VOICE_BUTTON : false);
            } else if (preferCapitalization() || (this.mKeyboardSwitcher.isAlphabetMode() && inputView.isShifted())) {
                if (suggestion.charAt(0) != '\'' || l <= 1) {
                    suggestion = M.toUpper(suggestion, M.mLC == M.tr ? ENABLE_VOICE_BUTTON : false).charAt(0) + suggestion.subSequence(1, suggestion.length()).toString();
                } else {
                    suggestion = "'" + M.toUpper(suggestion, M.mLC == M.tr ? ENABLE_VOICE_BUTTON : false).charAt(1) + suggestion.subSequence(2, suggestion.length()).toString();
                }
            }
            rememberReplacedWord(suggestion);
            if (TextEntryState.isCorrecting()) {
                EditingUtil.deleteWordAtCursor(ic);
            }
            suggestion = M.ime(suggestion, ENABLE_VOICE_BUTTON);
            cmmt(suggestion, ic);
            saveWordInHistory(suggestion);
            this.mPredicting = false;
            this.mCommittedLength = suggestion.length();
            spl(null);
            if (!correcting) {
                if (M.ja == M.mLC) {
                    M.dicJa.learnWord(suggestion);
                    ss(this.mSuggest.getSuggestions(new String[]{""}));
                } else {
                    ss2();
                }
            }
            updateShiftKeyState(getCurrentInputEditorInfo());
        }
    }

    private boolean applyVoiceAlternatives(SelectedWord touching) {
        String selectedWord = touching.word.toString().trim();
        if (!this.mWordToSuggestions.containsKey(selectedWord)) {
            selectedWord = selectedWord.toLowerCase();
        }
        if (!this.mWordToSuggestions.containsKey(selectedWord)) {
            return false;
        }
        this.mShowingVoiceSuggestions = ENABLE_VOICE_BUTTON;
        List<CharSequence> suggestions = (List) this.mWordToSuggestions.get(selectedWord);
        if (Character.isUpperCase(touching.word.charAt(0))) {
            for (int i = 0; i < suggestions.size(); i++) {
                String origSugg = (String) suggestions.get(i);
                suggestions.set(i, origSugg.toUpperCase().charAt(0) + origSugg.subSequence(1, origSugg.length()).toString());
            }
        }
        setSuggestions(suggestions, false, ENABLE_VOICE_BUTTON, ENABLE_VOICE_BUTTON);
        scvs(ENABLE_VOICE_BUTTON);
        return ENABLE_VOICE_BUTTON;
    }

    /* JADX WARNING: Removed duplicated region for block: B:17:0x005a A:{LOOP_END, LOOP:1: B:15:0x0052->B:17:0x005a} */
    /* JADX WARNING: Removed duplicated region for block: B:22:0x0083  */
    /* JADX WARNING: Removed duplicated region for block: B:27:0x0098  */
    /* JADX WARNING: Removed duplicated region for block: B:25:0x008f  */
    private boolean applyTypedAlternatives(com.klye.ime.latin.EditingUtil.SelectedWord r11) {
        /*
        r10 = this;
        r7 = 1;
        r6 = 0;
        r2 = 0;
        r0 = 0;
        r5 = r10.mWordHistory;
        r4 = r5.iterator();
    L_0x000a:
        r5 = r4.hasNext();
        if (r5 == 0) goto L_0x002e;
    L_0x0010:
        r1 = r4.next();
        r1 = (com.klye.ime.latin.LatinIME.WordAlternatives) r1;
        r5 = r1.getChosenWord();
        r8 = r11.word;
        r5 = android.text.TextUtils.equals(r5, r8);
        if (r5 == 0) goto L_0x000a;
    L_0x0022:
        r5 = r1 instanceof com.klye.ime.latin.LatinIME.TypedWordAlternatives;
        if (r5 == 0) goto L_0x002d;
    L_0x0026:
        r5 = r1;
        r5 = (com.klye.ime.latin.LatinIME.TypedWordAlternatives) r5;
        r2 = r5.word;
    L_0x002d:
        r0 = r1;
    L_0x002e:
        if (r2 != 0) goto L_0x007d;
    L_0x0030:
        r5 = r10.mSuggest;
        r8 = r11.word;
        r5 = r5.isValidWord(r8);
        if (r5 != 0) goto L_0x004c;
    L_0x003a:
        r5 = r10.mSuggest;
        r8 = r11.word;
        r8 = r8.toString();
        r8 = r8.toLowerCase();
        r5 = r5.isValidWord(r8);
        if (r5 == 0) goto L_0x007d;
    L_0x004c:
        r2 = new com.klye.ime.latin.WordComposer;
        r2.<init>();
        r3 = 0;
    L_0x0052:
        r5 = r11.word;
        r5 = r5.length();
        if (r3 >= r5) goto L_0x0070;
    L_0x005a:
        r5 = r11.word;
        r5 = r5.charAt(r3);
        r8 = new int[r7];
        r9 = r11.word;
        r9 = r9.charAt(r3);
        r8[r6] = r9;
        r2.add1(r5, r8);
        r3 = r3 + 1;
        goto L_0x0052;
    L_0x0070:
        r5 = r11.word;
        r5 = r5.charAt(r6);
        r5 = java.lang.Character.isUpperCase(r5);
        r2.setFirstCharCapitalized(r5);
    L_0x007d:
        if (r2 != 0) goto L_0x0081;
    L_0x007f:
        if (r0 == 0) goto L_0x009e;
    L_0x0081:
        if (r0 != 0) goto L_0x008a;
    L_0x0083:
        r0 = new com.klye.ime.latin.LatinIME$TypedWordAlternatives;
        r5 = r11.word;
        r0.<init>(r5, r2);
    L_0x008a:
        r10.showCorrections(r0);
        if (r2 == 0) goto L_0x0098;
    L_0x008f:
        r5 = new com.klye.ime.latin.WordComposer;
        r5.<init>(r2);
        r10.mWord = r5;
    L_0x0096:
        r5 = r7;
    L_0x0097:
        return r5;
    L_0x0098:
        r5 = r10.mWord;
        r5.reset();
        goto L_0x0096;
    L_0x009e:
        r5 = r6;
        goto L_0x0097;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.klye.ime.latin.LatinIME.applyTypedAlternatives(com.klye.ime.latin.EditingUtil$SelectedWord):boolean");
    }

    private void setOldSuggestions() {
        this.mShowingVoiceSuggestions = false;
        if (this.mCandidateView == null || !this.mCandidateView.isShowingAddToDictionaryHint()) {
            InputConnection ic = ic();
            if (ic == null) {
                return;
            }
            if (this.mPredicting) {
                abortCorrection(ENABLE_VOICE_BUTTON);
                return;
            }
            SelectedWord touching = EditingUtil.getWordAtCursorOrSelection(ic, this.mLastSelectionStart, this.mLastSelectionEnd);
            if (touching == null || touching.word.length() <= 1) {
                abortCorrection(ENABLE_VOICE_BUTTON);
                return;
            }
            ic.beginBatchEdit();
            if (applyVoiceAlternatives(touching) || applyTypedAlternatives(touching)) {
                TextEntryState.selectedForCorrection();
                EditingUtil.underlineWord(ic, touching);
            } else {
                abortCorrection(ENABLE_VOICE_BUTTON);
            }
            ic.endBatchEdit();
        }
    }

    private void ss(List<CharSequence> s) {
        setSuggestions(s, false, false, false);
    }

    private void addToDictionaries(CharSequence suggestion, int frequencyDelta) {
        checkAddToDictionary(suggestion, frequencyDelta, false);
    }

    private void addToBigramDictionary(CharSequence suggestion, int frequencyDelta) {
        checkAddToDictionary(suggestion, frequencyDelta, ENABLE_VOICE_BUTTON);
    }

    private void checkAddToDictionary(CharSequence s, int frequencyDelta, boolean addToBigramDictionary) {
        int l = s.length();
        if (s != null && l >= 1 && s.charAt(0) != M.zwsp && !M.udx()) {
            if ((this.mCorrectionMode == 2 || this.mCorrectionMode == 3) && s != null) {
                if (M.dicA != null && ((!addToBigramDictionary && M.dicA.isValidWord(s)) || !(this.mSuggest.isValidWord(s.toString()) || this.mSuggest.isValidWord(s.toString().toLowerCase())))) {
                    M.dicA.addWord(s.toString(), frequencyDelta);
                }
                if (M.dicB != null && getCurrentInputConnection() != null && !TextUtils.isEmpty(this.mPrevWord2)) {
                    M.dicB.addBigrams(this.mPrevWord2.toString(), s.toString());
                }
            }
        }
    }

    private boolean isCursorTouchingWord() {
        InputConnection ic = ic();
        if (M.noSpace() || ic == null) {
            return false;
        }
        CharSequence toLeft = ic.getTextBeforeCursor(1, 0);
        CharSequence toRight = ic.getTextAfterCursor(1, 0);
        if (!TextUtils.isEmpty(toLeft) && !M.isWordSep(toLeft.charAt(0))) {
            return ENABLE_VOICE_BUTTON;
        }
        if (TextUtils.isEmpty(toRight) || M.isWordSep(toRight.charAt(0))) {
            return false;
        }
        return ENABLE_VOICE_BUTTON;
    }

    public void revertLastWord(boolean deleteChar) {
        int length = this.mWord.mTW.length();
        if (this.mPredicting || length <= 0) {
            sendDownUpKeyEvents(67);
            this.mJustRevertedSeparator = null;
            return;
        }
        InputConnection ic = ic();
        this.mPredicting = ENABLE_VOICE_BUTTON;
        this.mJustRevertedSeparator = ic.getTextBeforeCursor(1, 0);
        if (deleteChar) {
            ic.deleteSurroundingText(1, 0);
        }
        int toDelete = this.mCommittedLength;
        CharSequence toTheLeft = ic.getTextBeforeCursor(this.mCommittedLength, 0);
        if (toTheLeft != null && toTheLeft.length() > 0 && M.isWordSep(toTheLeft.charAt(0))) {
            toDelete--;
        }
        ic.deleteSurroundingText(toDelete, 0);
        sct(ic);
        TextEntryState.backspace();
        postUpdateSuggestions();
    }

    private void sct() {
        M.ic = ic();
        if (M.ic != null) {
            sct(M.ic);
        }
    }

    private void sct(InputConnection ic) {
        CharSequence cs = M.ime(this.mWord);
        if (M.fdb) {
            cs = M.fd(cs);
        }
        super.getCurrentInputStarted();
        ic.setComposingText(cs, 1);
    }

    private void sendSpace() {
        sendKeyChar(' ');
        updateShiftKeyState(getCurrentInputEditorInfo());
    }

    public boolean preferCapitalization() {
        return this.mWord.isFirstCharCapitalized();
    }

    private void toggleLanguage(boolean reset, boolean next) {
        if (reset) {
            this.mLanguageSwitcher.reset();
        } else if (next) {
            this.mLanguageSwitcher.next();
        } else {
            this.mLanguageSwitcher.prev();
        }
        loadLanguage();
    }

    private void loadLanguage() {
        boolean b = M.ko == M.mLC ? ENABLE_VOICE_BUTTON : false;
        initSuggest(this.mLanguageSwitcher.getInputLanguage());
        this.mLanguageSwitcher.persist();
        loadKeyboard();
        if (b || M.noAutoCapC) {
            commitTyped(ic());
        } else {
            postUpdateSuggestions();
        }
        updateCorrectionMode();
        scvs();
    }

    private void loadKeyboard() {
        boolean z = false;
        M.cHasp = 0;
        int currentKeyboardMode = this.mKeyboardSwitcher.getKeyboardMode();
        reloadKeyboards();
        this.mKeyboardSwitcher.makeKeyboards(ENABLE_VOICE_BUTTON);
        KeyboardSwitcher keyboardSwitcher = this.mKeyboardSwitcher;
        boolean z2 = (this.mEnableVoiceButton && this.mEnableVoice) ? ENABLE_VOICE_BUTTON : false;
        keyboardSwitcher.setKeyboardMode(currentKeyboardMode, 0, z2);
        this.mCapsLock = false;
        updateShiftKeyState(getCurrentInputEditorInfo());
        if (this.mShowSuggestions || M.smo()) {
            z = ENABLE_VOICE_BUTTON;
        }
        this.mPredictionOn = z;
        cd();
        M.zhFlag();
    }

    public boolean hs() {
        return (!(this.mShowSuggestions && this.mSuggest.hasDictionary()) && (M.zt == -1 || M.h == null || !M.h.isOK())) ? false : ENABLE_VOICE_BUTTON;
    }

    private void cd() {
        int zt = M.zhType();
        boolean nd = !this.mSuggest.hasDictionary() ? ENABLE_VOICE_BUTTON : false;
        if (this.mShowSuggestions && nd && M.rmd < 12 && M.rdd()) {
            if (M.rmd % 3 == 0) {
                M.noti(this, getString(R.string.hw_lang_remind), M.hp("plugin.html"));
            }
            M.rmd++;
            M.rmb("rmd", M.rmd);
        }
        if (zt != -1 || M.mLC == M.ii) {
            M.hw1();
            M.lzt = zt;
            if (zt != 89 && nd && !M.h.isOK()) {
                M.noti(this, "請下載插件", M.hp("zh.html"));
            }
        }
    }

    public void onSharedPreferenceChanged(SharedPreferences sp, String key) {
        if (PREF_SELECTED_LANGUAGES.equals(key)) {
            this.mLanguageSwitcher.loadLocales(sp);
            this.mRefreshKeyboardRequired = ENABLE_VOICE_BUTTON;
        } else if ("hsk".equals(key)) {
            this.mResetRequired = ENABLE_VOICE_BUTTON;
        } else if (PREF_RECORRECTION_ENABLED.equals(key)) {
            this.mReCorrectionEnabled = sp.getBoolean(PREF_RECORRECTION_ENABLED, getResources().getBoolean(R.bool.default_recorrection_enabled));
        }
        this.mKeyboardSwitcher.invalidate();
    }

    public void swipeRight() {
    }

    public void swipeLeft() {
    }

    public void swipeDown() {
    }

    public void swipeUp() {
    }

    public void cs() {
        int keySize;
        switch (M.ks()) {
            case 80:
                keySize = 90;
                break;
            case 100:
                keySize = 120;
                break;
            case 120:
                keySize = 80;
                break;
            default:
                keySize = 100;
                break;
        }
        if (M.isLand()) {
            M.ksl = keySize;
            M.rmb("key_sizeL", "" + keySize);
        } else {
            M.ksp = keySize;
            M.rmb("key_size", "" + keySize);
        }
        loadKeyboard();
    }

    public void onPress(int pc) {
        vibrate();
        playKeyClick(pc);
        boolean distinctMultiTouch = this.mKeyboardSwitcher.hasDistinctMultitouch();
        if (distinctMultiTouch && pc == -1) {
            this.mShiftKeyState.onPress();
            handleShift();
        } else if (!distinctMultiTouch || pc != -2) {
            this.mShiftKeyState.onOtherKeyPressed();
            this.mSymbolKeyState.onOtherKeyPressed();
        }
    }

    public void onRelease(int pc) {
        ((LatinKeyboard) M.iv.getKeyboard()).keyReleased();
        boolean distinctMultiTouch = this.mKeyboardSwitcher.hasDistinctMultitouch();
        if (distinctMultiTouch && pc == -1) {
            if (this.mShiftKeyState.isMomentary()) {
                resetShift();
            }
            this.mShiftKeyState.onRelease();
            return;
        }
        if (!(distinctMultiTouch && pc == -2)) {
        }
    }

    @SuppressLint({"NewApi"})
    private boolean shouldShowVoiceButton() {
        return VOICE_INSTALLED ? SpeechRecognizer.isRecognitionAvailable(this) : false;
    }

    private void updateRingerMode() {
        if (this.mAudioManager == null) {
            this.mAudioManager = (AudioManager) getSystemService("audio");
        }
        if (this.mAudioManager != null) {
            this.mSilentMode = this.mAudioManager.getRingerMode() != 2 ? ENABLE_VOICE_BUTTON : false;
        }
    }

    private void playKeyClick(int pc) {
        if (this.mAudioManager == null && M.iv != null) {
            updateRingerMode();
        }
        if (this.mSoundOn && !this.mSilentMode) {
            int sound = 5;
            switch (pc) {
                case -5:
                    sound = 7;
                    break;
                case 10:
                    sound = 8;
                    break;
                case 32:
                    sound = 6;
                    break;
            }
            this.mAudioManager.playSoundEffect(sound, M.volf);
        }
    }

    private void vibrate() {
        if (M.vib.intValue() > 0) {
            try {
                M.v.cancel();
                M.v.vibrate((long) M.vib.intValue());
            } catch (Throwable th) {
            }
        }
    }

    void promoteToUserDictionary(String word, int frequency) {
        if (M.dicU != null && !M.dicU.isValidWord(word)) {
            M.dicU.addWord(word, frequency);
        }
    }

    WordComposer getCurrentWord() {
        return this.mWord;
    }

    boolean getPopupOn() {
        return this.mPopupOn;
    }

    private void updateCorrectionMode() {
        boolean hasMainDictionary;
        int i = 1;
        if (this.mSuggest != null) {
            hasMainDictionary = this.mSuggest.hasMainDictionary();
        } else {
            hasMainDictionary = false;
        }
        this.mHasDictionary = hasMainDictionary;
        boolean ac = ((this.mAutoCorrectEnabled && (this.mShowSuggestions || M.smo())) || M.isT9Semi()) ? ENABLE_VOICE_BUTTON : false;
        hasMainDictionary = (((ac || this.mQuickFixes) && !this.mInputTypeNoAutoCorrect && this.mHasDictionary && M.mLC != M.ko) || M.mLC == M.ja || M.zhType() != -1) ? ENABLE_VOICE_BUTTON : false;
        this.mAutoCorrectOn = hasMainDictionary;
        if (this.mAutoCorrectOn && ac) {
            i = 2;
        } else if (!this.mAutoCorrectOn) {
            i = 0;
        }
        this.mCorrectionMode = i;
        this.mCorrectionMode = this.mCorrectionMode;
        if (this.mSuggest != null) {
            this.mSuggest.setCorrectionMode(this.mCorrectionMode);
        }
    }

    private void updateAutoTextEnabled(Locale systemLocale) {
        boolean z = ENABLE_VOICE_BUTTON;
        if (this.mSuggest != null) {
            boolean different = !systemLocale.getLanguage().equalsIgnoreCase(M.mIL.substring(0, 2)) ? ENABLE_VOICE_BUTTON : false;
            Suggest suggest = this.mSuggest;
            if (different || !this.mQuickFixes) {
                z = false;
            }
            suggest.setAutoTextEnabled(z);
        }
    }

    private void loadSettings() {
        boolean enableVoice = false;
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        this.mVibrateOn = sp.getBoolean(PREF_VIBRATE_ON, false);
        M.vib = Integer.valueOf(sp.getString("vib", this.mVibrateOn ? "30" : "0"));
        if (M.vib.intValue() > 0) {
            M.v = (Vibrator) getSystemService("vibrator");
        }
        this.mSoundOn = sp.getBoolean(PREF_SOUND_ON, false);
        M.mAltCaps = sp.getBoolean("cap_lock", ENABLE_VOICE_BUTTON);
        M.dsp = sp.getBoolean("dsp", false);
        M.swp = sp.getBoolean("swp", ENABLE_VOICE_BUTTON);
        M.asb = sp.getBoolean("auto_norm", ENABLE_VOICE_BUTTON);
        M.mDG = sp.getBoolean("gest_on", ENABLE_VOICE_BUTTON);
        M.hard = sp.getBoolean("hard", ENABLE_VOICE_BUTTON);
        M.ls(sp);
        this.mAutoCorrectEnabled = sp.getBoolean(PREF_AUTO_COMPLETE, ENABLE_VOICE_BUTTON);
        M.bgd &= this.mAutoCorrectEnabled;
        if ((M.rc && M.dicC == null) || ((M.lrn && M.dicA == null) || ((M.bgd && M.dicB == null) || (M.usd && M.dicU == null)))) {
            loadLanguage();
        }
        M.ll = Integer.valueOf(sp.getString("ll", "0")).intValue();
        M.lpe = Integer.valueOf(sp.getString("lpe", "2")).intValue();
        M.gt = Integer.valueOf(sp.getString("t", M.DEFAULT_LAYOUT_ID)).intValue();
        M.gb = Integer.valueOf(sp.getString("b", "1")).intValue();
        M.gl = Integer.valueOf(sp.getString("l", "2")).intValue();
        M.gr = Integer.valueOf(sp.getString("r", "13")).intValue();
        M.gtl = Integer.valueOf(sp.getString("tl", "0")).intValue();
        M.gbl = Integer.valueOf(sp.getString("bl", "3")).intValue();
        M.gtr = Integer.valueOf(sp.getString("tr", "6")).intValue();
        M.gbr = Integer.valueOf(sp.getString("br", "0")).intValue();
        M.qwertz1();
        M.azerty1();
        M.JF = sp.getInt("JF", 0);
        M.mHanja = sp.getInt("HJ", 0);
        M.wfw = sp.getInt("wfw", 1);
        M.mDebugMode = sp.getBoolean("debug_mode", false);
        M.asp = sp.getBoolean("asp", M.asp);
        M.ncf = sp.getBoolean("ncf", M.ncf);
        M.ns = sp.getBoolean("ns", M.ns);
        M.vlcr = sp.getBoolean("vlcr", M.vlcr);
        M.cHasp = 0;
        M.ksp = Integer.valueOf(sp.getString("key_size", "100")).intValue();
        M.ksl = Integer.valueOf(sp.getString("key_sizeL", "100")).intValue();
        M.nose = sp.getString("nose", ":-)").charAt(1);
        M.mDD = sp.getInt("mDD", 2);
        this.mPopupOn = sp.getBoolean(PREF_POPUP_ON, M.res.getBoolean(R.bool.default_popup_preview));
        this.mAutoCap = sp.getBoolean(PREF_AUTO_CAP, ENABLE_VOICE_BUTTON);
        this.mAutoSpace = sp.getBoolean(PREF_AUTO_SPACE, ENABLE_VOICE_BUTTON);
        this.mQuickFixes = sp.getBoolean(PREF_QUICK_FIXES, ENABLE_VOICE_BUTTON);
        this.mHasUsedVoiceInput = sp.getBoolean(PREF_HAS_USED_VOICE_INPUT, false);
        this.mHasUsedVoiceInputUnsupportedLocale = sp.getBoolean(PREF_HAS_USED_VOICE_INPUT_UNSUPPORTED_LOCALE, false);
        this.mLocaleSupportedForVoiceInput = ENABLE_VOICE_BUTTON;
        this.mShowSuggestions = sp.getBoolean(PREF_SHOW_SUGGESTIONS, ENABLE_VOICE_BUTTON);
        if (VOICE_INSTALLED) {
            String voiceMode = sp.getString(PREF_VOICE_MODE, "1");
            if (!voiceMode.equals(getString(R.string.voice_mode_off)) && this.mEnableVoiceButton) {
                enableVoice = ENABLE_VOICE_BUTTON;
            }
            boolean voiceOnPrimary = voiceMode.equals(getString(R.string.voice_mode_main));
            if (!(this.mKeyboardSwitcher == null || (enableVoice == this.mEnableVoice && voiceOnPrimary == this.mVoiceOnPrimary))) {
                this.mKeyboardSwitcher.setVoiceMode(enableVoice, voiceOnPrimary);
            }
            this.mEnableVoice = enableVoice;
            this.mVoiceOnPrimary = voiceOnPrimary;
        }
        this.mLocaleSupportedForVoiceInput = ENABLE_VOICE_BUTTON;
        updateCorrectionMode();
        updateAutoTextEnabled(M.res.getConfiguration().locale);
        this.mLanguageSwitcher.loadLocales(sp);
    }

    private void showOptionsMenu() {
        Builder builder = new Builder(this);
        builder.setCancelable(ENABLE_VOICE_BUTTON);
        builder.setIcon(R.drawable.ic_application);
        builder.setNegativeButton(17039360, null);
        CharSequence itemSettings = getString(R.string.english_ime_settings);
        builder.setItems(new CharSequence[]{getString(R.string.main_page), itemSettings, getString(R.string.language_selection_title), getString(R.string.hw_reload_dict)}, new OnClickListener() {
            public void onClick(DialogInterface di, int position) {
                di.dismiss();
                switch (position) {
                    case 0:
                        LatinIME.this.handleClose();
                        M.lw(LatinIME.this);
                        return;
                    case 1:
                        LatinIME.this.handleClose();
                        M.launchSettings(LatinIME.this);
                        return;
                    case 2:
                        M.lsl(LatinIME.this);
                        return;
                    case 3:
                        M.ex();
                        return;
                    default:
                        return;
                }
            }
        });
        builder.setTitle(M.res.getString(R.string.english_ime_input_options));
        this.mOptionsDialog = builder.create();
        Window window = this.mOptionsDialog.getWindow();
        LayoutParams lp = window.getAttributes();
        lp.token = M.iv.getWindowToken();
        lp.type = 1003;
        window.setAttributes(lp);
        window.addFlags(131072);
        this.mOptionsDialog.show();
    }

    private void changeKeyboardMode(int m) {
        this.mKeyboardSwitcher.toggleSymbols(m);
        if (this.mCapsLock && this.mKeyboardSwitcher.isAlphabetMode()) {
            this.mKeyboardSwitcher.setShiftLocked(this.mCapsLock);
        }
        updateShiftKeyState(getCurrentInputEditorInfo());
    }

    public static <E> ArrayList<E> newArrayList(E... elements) {
        ArrayList<E> list = new ArrayList(((elements.length * 110) / 100) + 5);
        Collections.addAll(list, elements);
        return list;
    }

    private void measureCps() {
        long now = System.currentTimeMillis();
        if (this.mLastCpsTime == 0) {
            this.mLastCpsTime = now - 100;
        }
        this.mCpsIntervals[this.mCpsIndex] = now - this.mLastCpsTime;
        this.mLastCpsTime = now;
        this.mCpsIndex = (this.mCpsIndex + 1) % CPS_BUFFER_SIZE;
        long total = 0;
        for (int i = 0; i < CPS_BUFFER_SIZE; i++) {
            total += this.mCpsIntervals[i];
        }
        System.out.println("CPS = " + (16000.0f / ((float) total)));
    }

    public void onAutoCompletionStateChanged(boolean isAutoCompletion) {
        this.mKeyboardSwitcher.onAutoCompletionStateChanged(isAutoCompletion);
    }

    public void hg(int g) {
        boolean z = ENABLE_VOICE_BUTTON;
        int i = 0;
        switch (g) {
            case 1:
                handleClose();
                return;
            case 2:
                tkl();
                return;
            case 3:
                if (!M.smo()) {
                    if (this.mPredictionOn) {
                        z = false;
                    }
                    this.mPredictionOn = z;
                    this.mShowSuggestions = z;
                    M.oo = z;
                    loadKeyboard();
                    M.rmb(PREF_SHOW_SUGGESTIONS, this.mShowSuggestions);
                    updateCorrectionMode();
                    scvs();
                    return;
                }
                return;
            case 4:
                cs();
                return;
            case 5:
                toggleHW();
                return;
            case 6:
                if (M.hw()) {
                    M.h.del();
                    return;
                }
                return;
            case 7:
                keyDownUp(21);
                return;
            case R.styleable.LatinKeyboardBaseView_keyHysteresisDistance /*8*/:
                keyDownUp(22);
                return;
            case 9:
                tklt9();
                return;
            case 10:
                tklComp();
                return;
            case R.styleable.LatinKeyboardBaseView_shadowColor /*11*/:
                if (!M.kid.is(R.xml.kbd_edit)) {
                    i = 2;
                }
                changeKeyboardMode(i);
                return;
            case R.styleable.LatinKeyboardBaseView_shadowRadius /*12*/:
                if (M.emjAM()) {
                    if (!M.kid.is(M.emj)) {
                        i = 6;
                    }
                    changeKeyboardMode(i);
                    return;
                }
                return;
            case R.styleable.LatinKeyboardBaseView_backgroundDimAmount /*13*/:
                if (!M.kid.is(R.xml.kbd_ding)) {
                    i = 5;
                }
                changeKeyboardMode(i);
                return;
            case R.styleable.LatinKeyboardBaseView_keyTextStyle /*14*/:
                M.dw(ic());
                return;
            case R.styleable.LatinKeyboardBaseView_symbolColorScheme /*15*/:
                if (!M.kid.isPh()) {
                    i = 3;
                }
                changeKeyboardMode(i);
                return;
            case CPS_BUFFER_SIZE /*16*/:
                vr();
                return;
            case 17:
                ts();
                return;
            case 18:
                toggleLanguage(false, ENABLE_VOICE_BUTTON);
                return;
            case 19:
                ta();
                return;
            case DELETE_ACCELERATE_AT /*20*/:
                M.ut(this);
                return;
            default:
                return;
        }
    }

    public void p() {
        if (!this.mPredicting) {
            this.mWord.reset();
        }
    }
}
