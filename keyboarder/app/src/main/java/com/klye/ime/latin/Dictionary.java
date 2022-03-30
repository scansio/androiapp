package com.klye.ime.latin;

public abstract class Dictionary {
    protected static final int FULL_WORD_FREQ_MULTIPLIER = 2;
    protected static final boolean INCLUDE_TYPED_WORD_IF_VALID = true;

    public enum DataType {
        UNIGRAM,
        BIGRAM
    }

    public interface WordCallback {
        boolean addWord(String str, int i);

        boolean addWord(char[] cArr, int i, int i2, int i3, int i4, DataType dataType);
    }

    public abstract boolean isValidWord(CharSequence charSequence);

    public void getBigrams(WordComposer composer, CharSequence previousWord, WordCallback callback, int[] nextLettersFrequencies) {
    }

    protected boolean same(char[] word, int length, CharSequence typedWord) {
        if (typedWord == null || typedWord.length() != length) {
            return false;
        }
        for (int i = 0; i < length; i++) {
            if (word[i] != typedWord.charAt(i)) {
                return false;
            }
        }
        return INCLUDE_TYPED_WORD_IF_VALID;
    }

    public void close() {
    }
}
