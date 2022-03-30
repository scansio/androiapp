package com.klye.ime.latin;

import android.content.Context;
import android.util.Log;
import com.klye.ime.latin.Dictionary.DataType;
import com.klye.ime.latin.Dictionary.WordCallback;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.Channels;
import java.util.Arrays;

public class BinaryDictionary extends Dictionary {
    private static final boolean ENABLE_MISSED_CHARACTERS = true;
    private static final int MAX_ALTERNATIVES = 16;
    private static final int MAX_BIGRAMS = 60;
    private static final int MAX_WORDS = 180;
    protected static final int MAX_WORD_LENGTH = 48;
    private static final String TAG = "BinaryDictionary";
    private static ByteBuffer sbuf;
    private int mDicTypeId;
    private int mDictLength;
    private int[] mFrequencies = new int[MAX_WORDS];
    private int[] mFrequencies_bigrams = new int[MAX_BIGRAMS];
    private int mNativeDict;
    private ByteBuffer mNativeDictDirectBuffer;
    private char[] mOutputChars = new char[8640];
    private char[] mOutputChars_bigrams = new char[2880];

    private native void closeNative(int i);

    private native int getBigramsNative(int i, char[] cArr, int i2, int[] iArr, int i3, char[] cArr2, int[] iArr2, int i4, int i5, int i6);

    private native int getSuggestionsNative(int i, int[] iArr, int i2, char[] cArr, int[] iArr2, int i3, int i4, int i5, int i6, int[] iArr3, int i7);

    private native boolean isValidWordNative(int i, char[] cArr, int i2);

    private native int openNative(ByteBuffer byteBuffer, int i, int i2);

    public static native int toAccentLess(int i);

    static {
        try {
            System.loadLibrary("jni_ime");
        } catch (UnsatisfiedLinkError e) {
            Log.e(TAG, "Could not load native library jni_latinime");
        }
    }

    public BinaryDictionary(Context context, int[] resId, int dicTypeId, int TYPED_LETTER_MULTIPLIER, int FULL_WORD_FREQ_MULTIPLIER) {
        if (!((M.phw() && M.h != null && M.h.isOK()) || resId == null || resId.length <= 0 || resId[0] == 0)) {
            loadDictionary(context, resId, TYPED_LETTER_MULTIPLIER, FULL_WORD_FREQ_MULTIPLIER);
        }
        this.mDicTypeId = dicTypeId;
    }

    private final void loadDictionary(Context context, int[] resId, int TYPED_LETTER_MULTIPLIER, int FULL_WORD_FREQ_MULTIPLIER) {
        InputStream[] is = null;
        int total = 0;
        int i;
        try {
            is = new InputStream[resId.length];
            for (i = 0; i < resId.length; i++) {
                is[i] = context.getResources().openRawResource(resId[i]);
                total += is[i].available();
            }
            this.mNativeDictDirectBuffer = alc(total);
            int got = 0;
            for (i = 0; i < resId.length; i++) {
                got += Channels.newChannel(is[i]).read(this.mNativeDictDirectBuffer);
            }
            if (got != total) {
                Log.e(TAG, "Read " + got + " bytes, expected " + total);
            } else {
                this.mNativeDict = openNative(this.mNativeDictDirectBuffer, TYPED_LETTER_MULTIPLIER, FULL_WORD_FREQ_MULTIPLIER);
                this.mDictLength = total;
            }
            if (is != null) {
                i = 0;
                while (i < is.length) {
                    try {
                        is[i].close();
                        i++;
                    } catch (Throwable th) {
                        Log.w(TAG, "Failed to close input stream");
                        return;
                    }
                }
            }
        } catch (Throwable th2) {
            Log.w(TAG, "Failed to close input stream");
        }
    }

    private ByteBuffer alc(int total) {
        if (sbuf == null || sbuf.capacity() < total) {
            sbuf = ByteBuffer.allocateDirect(total).order(ByteOrder.nativeOrder());
        } else {
            sbuf.clear();
        }
        return sbuf;
    }

    final void changeParam(int TYPED_LETTER_MULTIPLIER, int FULL_WORD_FREQ_MULTIPLIER) {
        if (this.mNativeDict != 0) {
            close();
            this.mNativeDict = openNative(this.mNativeDictDirectBuffer, TYPED_LETTER_MULTIPLIER, FULL_WORD_FREQ_MULTIPLIER);
        }
    }

    public void getBigrams(WordComposer codes, CharSequence previousWord, WordCallback callback, int[] nextLettersFrequencies) {
        char[] chars = previousWord.toString().toCharArray();
        Arrays.fill(this.mOutputChars_bigrams, 0);
        Arrays.fill(this.mFrequencies_bigrams, 0);
        int codesSize = codes.size();
        Arrays.fill(M.icb, -1);
        int[] alternatives = codes.getCodesAt(0);
        System.arraycopy(alternatives, 0, M.icb, 0, Math.min(alternatives.length, MAX_ALTERNATIVES));
        int count = getBigramsNative(this.mNativeDict, chars, chars.length, M.icb, codesSize, this.mOutputChars_bigrams, this.mFrequencies_bigrams, MAX_WORD_LENGTH, MAX_BIGRAMS, MAX_ALTERNATIVES);
        int j = 0;
        while (j < count && this.mFrequencies_bigrams[j] >= 1) {
            int start = j * MAX_WORD_LENGTH;
            int len = 0;
            while (this.mOutputChars_bigrams[start + len] != 0) {
                len++;
            }
            if (len > 0) {
                callback.addWord(this.mOutputChars_bigrams, start, len, this.mFrequencies_bigrams[j], this.mDicTypeId, DataType.BIGRAM);
            }
            j++;
        }
    }

    public void getWords(WordComposer codes, WordCallback callback, int[] nextLettersFrequencies, int m) {
        int c = getWords(codes, callback, nextLettersFrequencies, M.ned(), m);
    }

    public int getWords(WordComposer codes, WordCallback callback, int[] nextLettersFrequencies, boolean enc, int m) {
        int codesSize = codes.size();
        if (codesSize > 47) {
            return 0;
        }
        Arrays.fill(M.icb, -1);
        for (int i = 0; i < codesSize; i++) {
            int[] alternatives = codes.getCodesAt(i);
            int l = alternatives.length;
            int s = i * MAX_ALTERNATIVES;
            System.arraycopy(alternatives, 0, M.icb, s, Math.min(l, MAX_ALTERNATIVES));
            if (enc) {
                int n = s + l;
                int j = s;
                while (j < n && M.icb[j] != -1) {
                    M.icb[j] = M.enc(M.icb[j]);
                    j++;
                }
            }
        }
        if (codesSize == 1 && M.zt != 89) {
            M.icb[codesSize * MAX_ALTERNATIVES] = 46;
            codesSize++;
        }
        return getWords(codesSize, nextLettersFrequencies, enc, callback, m);
    }

    private int getWords(int codesSize, int[] nextLettersFrequencies, boolean enc, WordCallback callback, int m) {
        int length;
        Arrays.fill(this.mOutputChars, 0);
        if (M.ncmd()) {
            this.mOutputChars[0] = 'i';
        } else if (M.cyr) {
            this.mOutputChars[0] = 'c';
        }
        Arrays.fill(this.mFrequencies, -10);
        int i = this.mNativeDict;
        int[] iArr = M.icb;
        char[] cArr = this.mOutputChars;
        int[] iArr2 = this.mFrequencies;
        if (nextLettersFrequencies != null) {
            length = nextLettersFrequencies.length;
        } else {
            length = 0;
        }
        int count = getSuggestionsNative(i, iArr, codesSize, cArr, iArr2, MAX_WORD_LENGTH, MAX_WORDS, MAX_ALTERNATIVES, m, nextLettersFrequencies, length);
        if (!M.el() && M.zt != 67 && count < 1) {
            int skip = 1;
            while (skip < codesSize && skip < 6) {
                int tempCount = getSuggestionsNative(this.mNativeDict, M.icb, codesSize, this.mOutputChars, this.mFrequencies, MAX_WORD_LENGTH, MAX_WORDS, MAX_ALTERNATIVES, skip, null, 0);
                count = Math.max(count, tempCount);
                if (tempCount > 0) {
                    break;
                }
                skip++;
            }
        }
        int j = 0;
        while (j < count && this.mFrequencies[j] >= 1) {
            int start = j * MAX_WORD_LENGTH;
            int len = 0;
            while (this.mOutputChars[start + len] != 0) {
                if (enc) {
                    this.mOutputChars[start + len] = (char) M.enc1(this.mOutputChars[start + len]);
                }
                len++;
            }
            if (len > 0) {
                callback.addWord(this.mOutputChars, start, len, this.mFrequencies[j], this.mDicTypeId, DataType.UNIGRAM);
            }
            j++;
        }
        return count;
    }

    public void getWords(String[] ss, WordCallback callback, int[] nextLettersFrequencies) {
        for (String s : ss) {
            if (s != null) {
                int length;
                Arrays.fill(M.icb, -1);
                int codesSize = s.length();
                for (int i = 0; i < codesSize; i++) {
                    M.icb[i] = s.charAt(i);
                }
                Arrays.fill(this.mOutputChars, 0);
                Arrays.fill(this.mFrequencies, 0);
                int i2 = this.mNativeDict;
                int[] iArr = M.icb;
                char[] cArr = this.mOutputChars;
                int[] iArr2 = this.mFrequencies;
                if (nextLettersFrequencies != null) {
                    length = nextLettersFrequencies.length;
                } else {
                    length = 0;
                }
                int count = getSuggestionsNative(i2, iArr, codesSize, cArr, iArr2, MAX_WORD_LENGTH, MAX_WORDS, 1, -1, nextLettersFrequencies, length);
                if (count < 1) {
                    int skip = 1;
                    while (skip < codesSize && skip < 5) {
                        int tempCount = getSuggestionsNative(this.mNativeDict, M.icb, codesSize, this.mOutputChars, this.mFrequencies, MAX_WORD_LENGTH, MAX_WORDS, 1, skip, null, 0);
                        count = Math.max(count, tempCount);
                        if (tempCount > 0) {
                            break;
                        }
                        skip++;
                    }
                }
                int j = 0;
                while (j < count && this.mFrequencies[j] >= 1) {
                    int start = j * MAX_WORD_LENGTH;
                    int len = 0;
                    while (this.mOutputChars[start + len] != 0) {
                        len++;
                    }
                    if (len > 0) {
                        callback.addWord(this.mOutputChars, start, len, this.mFrequencies[j], this.mDicTypeId, DataType.UNIGRAM);
                    }
                    j++;
                }
            }
        }
    }

    public boolean isValidWord(CharSequence word) {
        if (word == null) {
            return false;
        }
        try {
            char[] chars = word.toString().toCharArray();
            if (M.ned()) {
                for (int i = 0; i < chars.length; i++) {
                    chars[i] = (char) M.enc(chars[i]);
                }
            }
            return isValidWordNative(this.mNativeDict, chars, chars.length);
        } catch (Throwable th) {
            return ENABLE_MISSED_CHARACTERS;
        }
    }

    public int getSize() {
        return this.mDictLength;
    }

    public synchronized void close() {
        if (this.mNativeDict != 0) {
            closeNative(this.mNativeDict);
            this.mNativeDict = 0;
        }
    }

    protected void finalize() throws Throwable {
        close();
        super.finalize();
    }
}
