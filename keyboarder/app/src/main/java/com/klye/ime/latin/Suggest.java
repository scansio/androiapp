package com.klye.ime.latin;

import android.content.Context;
import android.text.AutoText;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import com.klye.ime.latin.Dictionary.DataType;
import com.klye.ime.latin.Dictionary.WordCallback;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Suggest implements WordCallback {
    public static final int APPROX_MAX_WORD_LENGTH = 32;
    public static final double BIGRAM_MULTIPLIER_MAX = 1.5d;
    public static final double BIGRAM_MULTIPLIER_MIN = 1.2d;
    public static final int CORRECTION_BASIC = 1;
    public static final int CORRECTION_FULL = 2;
    public static final int CORRECTION_FULL_BIGRAM = 3;
    public static final int CORRECTION_NONE = 0;
    public static final int DIC_AUTO = 3;
    public static final int DIC_CONTACTS = 4;
    public static final int DIC_MAIN = 1;
    public static final int DIC_TYPE_LAST_ID = 5;
    public static final int DIC_USER = 2;
    public static final int DIC_USER_BIGRAM = 5;
    public static final int DIC_USER_TYPED = 0;
    static final int LARGE_DICTIONARY_THRESHOLD = 100000;
    public static final int MAXIMUM_BIGRAM_FREQUENCY = 127;
    private static final int PREF_MAX_BIGRAMS = 60;
    boolean ivw;
    private boolean mAutoTextEnabled;
    private int[] mBigramPriorities;
    ArrayList<CharSequence> mBigramSuggestions;
    private int mCorrectionMode;
    private boolean mHaveCorrection;
    private boolean mIsAllUpperCase;
    private boolean mIsFirstCharCapitalized;
    private String mLowerOriginalWord;
    private int[] mNextLettersFrequencies;
    private CharSequence mOriginalWord;
    private int mPrefMaxSuggestions;
    private int[] mPriorities;
    private ArrayList<CharSequence> mStringPool;
    ArrayList<CharSequence> mSuggestions;
    private WordComposer mWC;

    public Suggest(Context context, int[] dictionaryResId, String s) {
        int i = (M.zt != -1 || M.mLC == M.ja) ? 250 : 50;
        this.mPrefMaxSuggestions = i;
        this.mPriorities = new int[this.mPrefMaxSuggestions];
        this.mBigramPriorities = new int[PREF_MAX_BIGRAMS];
        this.mNextLettersFrequencies = new int[1280];
        this.mSuggestions = new ArrayList();
        this.mBigramSuggestions = new ArrayList();
        this.mStringPool = new ArrayList();
        this.mCorrectionMode = 1;
        this.ivw = false;
        M.dicParam();
        M.dicM = new BinaryDictionary(context, dictionaryResId, 1, M.TYPED_LETTER_MULTIPLIER, M.FULL_WORD_FREQ_MULTIPLIER);
        initPool();
    }

    public void changeParam() {
        M.dicParam();
        M.dicM.changeParam(M.TYPED_LETTER_MULTIPLIER, M.FULL_WORD_FREQ_MULTIPLIER);
    }

    private void initPool() {
        for (int i = 0; i < this.mPrefMaxSuggestions; i++) {
            this.mStringPool.add(new StringBuilder(getApproxMaxWordLength()));
        }
    }

    public void setAutoTextEnabled(boolean enabled) {
        this.mAutoTextEnabled = enabled;
    }

    public int getCorrectionMode() {
        return this.mCorrectionMode;
    }

    public void setCorrectionMode(int mode) {
        this.mCorrectionMode = mode;
    }

    public boolean hasMainDictionary() {
        return M.dicM.getSize() > LARGE_DICTIONARY_THRESHOLD;
    }

    public boolean hasDictionary() {
        return M.dicM.getSize() > 1000;
    }

    public int getApproxMaxWordLength() {
        return 32;
    }

    public void setMaxSuggestions(int maxSuggestions) {
        if (maxSuggestions < 1 || maxSuggestions > 100) {
            throw new IllegalArgumentException("maxSuggestions must be between 1 and 100");
        }
        this.mPrefMaxSuggestions = maxSuggestions;
        this.mPriorities = new int[this.mPrefMaxSuggestions];
        this.mBigramPriorities = new int[PREF_MAX_BIGRAMS];
        collectGarbage(this.mSuggestions, this.mPrefMaxSuggestions);
    }

    private boolean haveSufficientCommonality(String original, CharSequence suggestion) {
        int originalLength = original.length();
        int suggestionLength = suggestion.length();
        int minLength = Math.min(originalLength, suggestionLength);
        if (minLength <= 2) {
            return true;
        }
        int matching = 0;
        int lessMatching = 0;
        int i = 0;
        while (i < minLength) {
            char origChar = ExpandableDictionary.toLowerCase(original.charAt(i));
            if (origChar == ExpandableDictionary.toLowerCase(suggestion.charAt(i))) {
                matching++;
                lessMatching++;
            } else if (i + 1 < suggestionLength && origChar == ExpandableDictionary.toLowerCase(suggestion.charAt(i + 1))) {
                lessMatching++;
            }
            i++;
        }
        matching = Math.max(matching, lessMatching);
        if (minLength <= 4) {
            if (matching < 2) {
                return false;
            }
            return true;
        } else if (matching <= minLength / 2) {
            return false;
        } else {
            return true;
        }
    }

    public List<CharSequence> getSuggestions(String[] s) {
        if (s == null) {
            return null;
        }
        if (M.mLC == M.ja) {
            M.dicJa.getWords(s, (WordCallback) this, this.mNextLettersFrequencies);
        } else {
            M.dicM.getWords(s, this, this.mNextLettersFrequencies);
        }
        if ((this.mCorrectionMode == 2 || this.mCorrectionMode == 3) && this.mSuggestions.size() > 0) {
            this.mHaveCorrection = true;
        }
        addIfValidWord(s);
        removeDupes();
        return this.mSuggestions;
    }

    public List<CharSequence> clearSuggestions() {
        this.mHaveCorrection = false;
        this.mIsAllUpperCase = false;
        this.mIsFirstCharCapitalized = false;
        collectGarbage(this.mSuggestions, this.mPrefMaxSuggestions);
        Arrays.fill(this.mPriorities, 0);
        Arrays.fill(this.mNextLettersFrequencies, 0);
        return this.mSuggestions;
    }

    public List<CharSequence> imprt(String[] s, int zt, int i) {
        if (zt == 89) {
            this.mHaveCorrection = false;
        }
        M.add(this.mSuggestions, s, i);
        return this.mSuggestions;
    }

    private void addIfValidWord(String[] s1) {
        boolean j;
        if (M.mLC == M.ja) {
            j = true;
        } else {
            j = false;
        }
        for (String s : s1) {
            if ((j || isValidWord(s)) && M.mLC != M.ko) {
                this.mSuggestions.add(0, s);
            }
        }
    }

    public List<CharSequence> getSuggestions(View view, WordComposer wc, boolean includeTypedWordIfValid, CharSequence prevWordForBigram) {
        this.mHaveCorrection = false;
        this.mIsFirstCharCapitalized = wc.isFirstCharCapitalized();
        this.mIsAllUpperCase = wc.isAllUpperCase();
        collectGarbage(this.mSuggestions, this.mPrefMaxSuggestions);
        Arrays.fill(this.mPriorities, 0);
        Arrays.fill(this.mNextLettersFrequencies, 0);
        M.hat = false;
        if (wc.mTW.length() > 1 && M.dicUt != null) {
            M.dicUt.getWords(wc, this, this.mNextLettersFrequencies);
            M.hat = this.mSuggestions.size() > 0;
        }
        this.mOriginalWord = wc.getTypedWord();
        if (this.mOriginalWord != null) {
            String s = this.mOriginalWord.toString();
            int ws = s.length();
            if (ws > 2) {
                try {
                    int i;
                    int j;
                    if (s.charAt(0) == '&' && s.charAt(1) == '#') {
                        if (this.mOriginalWord.charAt(2) != 'x' || ws <= 3) {
                            i = Integer.parseInt(s.substring(2));
                        } else {
                            i = Integer.parseInt(s.substring(3), 16);
                        }
                        for (j = 0; j < 10; j++) {
                            this.mSuggestions.add(Character.toString((char) (i + j)));
                        }
                    }
                    if (Character.toLowerCase(s.charAt(0)) == 'u' && s.charAt(1) == '+') {
                        i = Integer.parseInt(s.substring(2), 16);
                        for (j = 0; j < 10; j++) {
                            this.mSuggestions.add(Character.toString((char) (i + j)));
                        }
                    }
                } catch (Throwable e) {
                    M.l(e);
                }
            }
        }
        if (M.mLC != M.ja && M.mLC == M.ko) {
            return getSuggestions(wc.kogs());
        }
        this.mWC = wc;
        if (this.mOriginalWord != null) {
            String mOriginalWordString = this.mOriginalWord.toString();
            this.mOriginalWord = mOriginalWordString;
            this.mLowerOriginalWord = mOriginalWordString.toLowerCase();
        } else {
            this.mLowerOriginalWord = "";
        }
        if (this.mLowerOriginalWord.indexOf("htt") == 0) {
            this.mSuggestions.add("http://www.");
            this.mSuggestions.add("https://www.");
        }
        if (M.dsw || M.hw()) {
            return getSuggestionsCJ(view, wc, includeTypedWordIfValid, prevWordForBigram);
        }
        return getSuggestionsNormal(view, wc, includeTypedWordIfValid, prevWordForBigram);
    }

    public List<CharSequence> getSuggestionsCJ(View view, WordComposer wc, boolean includeTypedWordIfValid, CharSequence prevWordForBigram) {
        int ws = wc.size();
        if (ws > 0) {
            if (M.mLC == M.ja) {
                M.dicJa.getWords(wc, (WordCallback) this, this.mNextLettersFrequencies);
                StringBuilder sb = new StringBuilder();
                wc.jaK(sb, 12449);
                this.mSuggestions.add(sb);
                if ("t".contentEquals(sb)) {
                    this.mSuggestions.add("ティー");
                }
                int ii = "テイ".indexOf(sb.toString());
                if (ii != -1) {
                    sb.replace(ii + 1, ii + 1, "ィ");
                    this.mSuggestions.add(sb);
                }
            } else if (M.zt == 80 && wc.lastChar() == '\'') {
                M.a5t(this.mSuggestions, this.mOriginalWord.subSequence(0, ws - 1).toString());
            } else {
                M.dicM.getWords(wc, this, this.mNextLettersFrequencies, -1);
                if (this.mSuggestions.size() > 0) {
                    this.mHaveCorrection = true;
                }
            }
        }
        if (ws == 1 && this.mSuggestions.size() <= 1 && "WC".indexOf(M.zt) != -1) {
            M.add(this.mSuggestions, wc.getCodesAt(0), true);
        }
        removeDupes();
        return this.mSuggestions;
    }

    public List<CharSequence> getSuggestionsNormal(View v, WordComposer wc, boolean includeTypedWordIfValid, CharSequence prevWordForBigram) {
        int i;
        boolean z;
        boolean z2;
        int i2;
        this.ivw = false;
        M.bga = 0;
        boolean hcm = this.mCorrectionMode == 2 || this.mCorrectionMode == 3;
        int ws = wc.size();
        if (ws > 1) {
            if (!(M.dicU == null && M.dicC == null)) {
                if (M.dicU != null) {
                    M.dicU.getWords(wc, this, this.mNextLettersFrequencies);
                }
                if (M.dicC != null) {
                    M.dicC.getWords(wc, this, this.mNextLettersFrequencies);
                }
                if (this.mSuggestions.size() > 0 && hcm) {
                    this.mHaveCorrection = true;
                }
            }
            if (M.aad) {
                M.dicM.getWords(wc, this, this.mNextLettersFrequencies, -2);
            }
            M.dicM.getWords(wc, this, this.mNextLettersFrequencies, -1);
            if (hcm && this.mSuggestions.size() > 0) {
                this.mHaveCorrection = true;
            }
        } else if (M.dicB != null && M.bgd && !TextUtils.isEmpty(prevWordForBigram)) {
            CharSequence lowerPrevWord = prevWordForBigram.toString().toLowerCase();
            if (M.dicM != null && M.dicM.isValidWord(lowerPrevWord)) {
                prevWordForBigram = lowerPrevWord;
            }
            if (ws != 0) {
                char currentCharUpper = Character.toUpperCase(this.mOriginalWord.charAt(0));
                int count = 0;
                int bigramSuggestionSize = this.mBigramSuggestions.size();
                for (i = 0; i < bigramSuggestionSize; i++) {
                    CharSequence bigramSuggestion = (CharSequence) this.mBigramSuggestions.get(i);
                    if (wc.fc(bigramSuggestion.charAt(0))) {
                        M.bga++;
                        this.mHaveCorrection = true;
                        addBigramToSuggestions(bigramSuggestion);
                        count++;
                        if (count > this.mPrefMaxSuggestions) {
                            break;
                        }
                    }
                }
            }
        }
        if (!(this.mOriginalWord == null || this.ivw)) {
            if (!isValidWord(this.mOriginalWord)) {
                if (!isValidWord(this.mLowerOriginalWord)) {
                    z = false;
                    this.ivw = z;
                    this.mSuggestions.add(0, M.ime(this.mOriginalWord));
                }
            }
            z = true;
            this.ivw = z;
            this.mSuggestions.add(0, M.ime(this.mOriginalWord));
        }
        if (ws == 1) {
            char c = this.mLowerOriginalWord.charAt(0);
            if (M.ivw(c) != c) {
                if (!isValidWord(this.mOriginalWord)) {
                    if (!isValidWord(this.mLowerOriginalWord)) {
                        z = false;
                        this.ivw = z;
                        z2 = this.mHaveCorrection;
                        i2 = (M.sslw(this.mSuggestions, wc.getCodesAt(0), c) || !hcm) ? 0 : 1;
                        this.mHaveCorrection = i2 | z2;
                    }
                }
            }
            z = true;
            this.ivw = z;
            z2 = this.mHaveCorrection;
            if (M.sslw(this.mSuggestions, wc.getCodesAt(0), c)) {
            }
            this.mHaveCorrection = i2 | z2;
        }
        if (ws > 1 && this.mAutoTextEnabled && M.fk() && v != null) {
            i = 0;
            int max = 6;
            if (this.mCorrectionMode == 1) {
                max = 1;
            }
            while (i < this.mSuggestions.size() && i < max) {
                String suggestedWord = ((CharSequence) this.mSuggestions.get(i)).toString().toLowerCase();
                CharSequence autoText = AutoText.get(suggestedWord, 0, suggestedWord.length(), v);
                boolean canAdd = (autoText != null) & (!TextUtils.equals(autoText, (CharSequence) this.mSuggestions.get(i)) ? 1 : 0);
                if (canAdd && i + 1 < this.mSuggestions.size() && this.mCorrectionMode != 1) {
                    canAdd &= !TextUtils.equals(autoText, (CharSequence) this.mSuggestions.get(i + 1)) ? 1 : 0;
                }
                if (canAdd) {
                    this.mHaveCorrection = true;
                    this.mSuggestions.add(i + 1, autoText);
                    i++;
                }
                i++;
            }
        }
        z2 = this.mHaveCorrection;
        i2 = (ws == 1 && this.mOriginalWord.charAt(0) == 'I') ? 0 : 1;
        this.mHaveCorrection = i2 & z2;
        removeDupes();
        return this.mSuggestions;
    }

    public List<CharSequence> bggs(CharSequence prevWordForBigram) {
        int i;
        int i2;
        clearSuggestions();
        Arrays.fill(this.mBigramPriorities, 0);
        collectGarbage(this.mBigramSuggestions, PREF_MAX_BIGRAMS);
        M.dicB.getBigrams(null, prevWordForBigram, this, null);
        int insertCount = Math.min(this.mBigramSuggestions.size(), 3);
        boolean z = this.mHaveCorrection;
        if (insertCount > 0) {
            i = 1;
        } else {
            i = 0;
        }
        this.mHaveCorrection = i | z;
        M.bgl.clear();
        int n = M.psbl.size();
        int n1 = Math.min(n, 0);
        for (i2 = 0; i2 < insertCount; i2++) {
            M.bgl.add(this.mBigramSuggestions.get(i2));
        }
        for (i2 = 0; i2 < n; i2++) {
            M.bgl.add(M.psbl.get(i2));
        }
        return M.bgl;
    }

    protected void addBigramToSuggestions(CharSequence sb) {
        this.mSuggestions.add(sb);
    }

    public int[] getNextLettersFrequencies() {
        return this.mNextLettersFrequencies;
    }

    private void removeDupes() {
        ArrayList<CharSequence> suggestions = this.mSuggestions;
        if (suggestions.size() >= 2) {
            int i = 1;
            while (i < suggestions.size()) {
                CharSequence cur = (CharSequence) suggestions.get(i);
                for (int j = 0; j < i; j++) {
                    CharSequence previous = (CharSequence) suggestions.get(j);
                    if (j == 0) {
                        previous = M.to(previous, M.zhType());
                    }
                    if (TextUtils.equals(cur, previous)) {
                        removeFromSuggestions(i);
                        i--;
                        break;
                    }
                }
                i++;
            }
        }
    }

    private void removeFromSuggestions(int index) {
        CharSequence garbage = (CharSequence) this.mSuggestions.remove(index);
        if (garbage != null && (garbage instanceof StringBuilder)) {
            this.mStringPool.add(garbage);
        }
    }

    public boolean hasMinimalCorrection() {
        boolean z = false;
        if (!M.isT9Semi()) {
            if ((this.mHaveCorrection && M.zt != 67) || M.hat) {
                z = true;
            }
            return z;
        } else if (M.mt9()) {
            return false;
        } else {
            return true;
        }
    }

    private boolean compareCaseInsensitive(String mLowerOriginalWord, char[] word, int offset, int length) {
        if (mLowerOriginalWord == null) {
            return false;
        }
        int originalLength = mLowerOriginalWord.length();
        if (originalLength != length) {
            return false;
        }
        for (int i = 0; i < originalLength; i++) {
            if (mLowerOriginalWord.charAt(i) != Character.toLowerCase(word[offset + i])) {
                return false;
            }
        }
        return true;
    }

    public boolean addWord(char[] word, int offset, int length, int freq, int id, DataType dataType) {
        return M.dsw ? addWordCJ(word, offset, length, freq, id, dataType) : addWordNormal(word, offset, length, freq, id, dataType);
    }

    public boolean addWordCJ(char[] word, int offset, int len, int freq, int dicTypeId, DataType dataType) {
        DataType dataTypeForLog = dataType;
        ArrayList<CharSequence> suggestions = this.mSuggestions;
        int[] priorities = this.mPriorities;
        int prefMaxSuggestions = this.mPrefMaxSuggestions;
        int pos = 0;
        StringBuilder sb = new StringBuilder(getApproxMaxWordLength());
        sb.append(word, offset, len);
        boolean ik = M.mLC == 7012463;
        if (ik) {
            Ko.koA(sb, len);
            Ko.koIme(sb, len);
        }
        int si = sb.indexOf(".");
        if (si == -1) {
            si = len;
        }
        if (si != len) {
            String s = sb.substring(si + 1);
            if (!ik) {
                sb.setLength(0);
                sb.append(s);
            } else if (M.mHanja == 1) {
                int ir = s.indexOf("-");
                if (ir > 0) {
                    s = s.substring(0, ir);
                    String s2 = sb.substring(ir, si);
                    sb.setLength(ir);
                    sb.append('(');
                    sb.append(s);
                    sb.append(')');
                    sb.append(s2);
                } else if (ir == 0) {
                    s = s.substring(1);
                    sb.setLength(si);
                    sb.append('(');
                    sb.append(s);
                    sb.append(')');
                } else {
                    sb.setLength(si);
                    sb.append('(');
                    sb.append(s);
                    sb.append(')');
                }
                si = sb.length();
            } else {
                sb.setLength(si);
            }
        }
        if (!(M.zhType() == -1 || this.mWC == null)) {
            int ld = si - this.mWC.size();
            if (ld < 0) {
                ld = 0;
            }
            freq += 10000 / (ld + 1);
        }
        if (M.jfs() && M.JF == 1) {
            M.toSimp(sb);
        }
        if (priorities[prefMaxSuggestions - 1] >= freq) {
            return true;
        }
        while (pos < prefMaxSuggestions && priorities[pos] >= freq && (priorities[pos] != freq || si >= ((CharSequence) suggestions.get(pos)).length())) {
            pos++;
        }
        if (pos >= prefMaxSuggestions) {
            return true;
        }
        System.arraycopy(priorities, pos, priorities, pos + 1, (prefMaxSuggestions - pos) - 1);
        priorities[pos] = freq;
        suggestions.add(pos, sb);
        if (suggestions.size() > prefMaxSuggestions) {
            CharSequence garbage = (CharSequence) suggestions.remove(prefMaxSuggestions);
            if (garbage instanceof StringBuilder) {
                this.mStringPool.add(garbage);
            }
        }
        return true;
    }

    public boolean addWord(String word, int freq) {
        System.arraycopy(this.mPriorities, 0, this.mPriorities, 1, this.mPrefMaxSuggestions - 1);
        this.mPriorities[0] = freq;
        this.mSuggestions.add(0, word);
        return true;
    }

    public boolean addWordNormal(char[] word, int offset, int length, int freq, int id, DataType dataType) {
        ArrayList<CharSequence> suggestions;
        int[] priorities;
        int prefMaxSuggestions;
        DataType dataTypeForLog = dataType;
        if (dataType == DataType.BIGRAM) {
            suggestions = this.mBigramSuggestions;
            priorities = this.mBigramPriorities;
            prefMaxSuggestions = PREF_MAX_BIGRAMS;
        } else {
            suggestions = this.mSuggestions;
            priorities = this.mPriorities;
            prefMaxSuggestions = this.mPrefMaxSuggestions;
        }
        int pos = 0;
        CharSequence s = new String(word, offset, length);
        boolean ivw1 = this.mOriginalWord != null && this.mOriginalWord.equals(s);
        this.ivw |= ivw1;
        if (dataType == DataType.UNIGRAM) {
            if (this.mWC != null) {
                int ld = (M.en == M.mLC && word[offset] == 'I' && word[offset + 1] == '\'') ? 0 : length - this.mWC.size();
                if (ld < 0) {
                    ld = 0;
                }
                if (ivw1) {
                    freq += freq + 8888;
                }
                freq += ((M.en == M.mLC ? 15 : 100) * M.wlp) / (ld + 1);
            }
            int bigramSuggestion = searchBigramSuggestion(word, offset, length);
            if (bigramSuggestion >= 0) {
                freq = ((this.mBigramPriorities[bigramSuggestion] + 1) * freq) / 1;
            }
        }
        if (priorities[prefMaxSuggestions - 1] >= freq) {
            return true;
        }
        while (pos < prefMaxSuggestions && priorities[pos] >= freq && (priorities[pos] != freq || length >= ((CharSequence) suggestions.get(pos)).length())) {
            pos++;
        }
        if (pos >= prefMaxSuggestions) {
            return true;
        }
        String s2;
        System.arraycopy(priorities, pos, priorities, pos + 1, (prefMaxSuggestions - pos) - 1);
        priorities[pos] = freq;
        StringBuilder sb = new StringBuilder(getApproxMaxWordLength());
        if (id == 4) {
            s2 = M.zwsp + s2;
        } else if (id == 1) {
            s2 = M.mLC == M.vi ? s2.replace('\'', ' ') : M.ime(s2).toString();
        }
        if (this.mIsAllUpperCase) {
            sb.append(s2.toUpperCase());
        } else {
            sb.append(s2);
            if (this.mIsFirstCharCapitalized) {
                sb.deleteCharAt(0);
                sb.insert(0, Character.toUpperCase(s2.charAt(0)));
            }
        }
        suggestions.add(pos, sb);
        if (suggestions.size() > prefMaxSuggestions) {
            CharSequence garbage = (CharSequence) suggestions.remove(prefMaxSuggestions);
            if (garbage instanceof StringBuilder) {
                this.mStringPool.add(garbage);
            }
        }
        return true;
    }

    private int searchBigramSuggestion(char[] word, int offset, int length) {
        int bigramSuggestSize = this.mBigramSuggestions.size();
        for (int i = 0; i < bigramSuggestSize; i++) {
            CharSequence s = (CharSequence) this.mBigramSuggestions.get(i);
            int d = s.length() - length;
            if (d < 3 && d >= 0) {
                boolean chk = true;
                for (int j = 0; j < length; j++) {
                    if (Character.toLowerCase(s.charAt(j)) != Character.toLowerCase(word[offset + j])) {
                        chk = false;
                        break;
                    }
                }
                if (chk) {
                    return i;
                }
            }
        }
        return -1;
    }

    public boolean isValidWord(CharSequence word) {
        if (word == null || word.length() == 0) {
            return false;
        }
        if (M.dicM.isValidWord(word) || ((M.dicU != null && M.dicU.isValidWord(word)) || (M.dicC != null && M.dicC.isValidWord(word)))) {
            return true;
        }
        return false;
    }

    private void collectGarbage(ArrayList<CharSequence> suggestions, int prefMaxSuggestions) {
        int poolSize = this.mStringPool.size();
        int garbageSize = suggestions.size();
        while (poolSize < prefMaxSuggestions && garbageSize > 0) {
            CharSequence garbage = (CharSequence) suggestions.get(garbageSize - 1);
            if (garbage != null && (garbage instanceof StringBuilder)) {
                this.mStringPool.add(garbage);
                poolSize++;
            }
            garbageSize--;
        }
        if (poolSize == prefMaxSuggestions + 1) {
            Log.w("Suggest", "String pool got too big: " + poolSize);
        }
        suggestions.clear();
    }

    public void close() {
        if (M.dicM != null) {
            M.dicM.close();
        }
    }
}
