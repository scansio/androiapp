package com.klye.ime.latin;

import android.text.TextUtils;
import android.view.inputmethod.ExtractedText;
import android.view.inputmethod.ExtractedTextRequest;
import android.view.inputmethod.InputConnection;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.regex.Pattern;

public class EditingUtil {
    private static final int LOOKBACK_CHARACTER_NUM = 15;
    private static Method sMethodGetSelectedText;
    private static Method sMethodSetComposingRegion;
    private static boolean sMethodsInitialized;
    private static final Pattern spaceRegex = Pattern.compile("\\s+");

    public static class Range {
        public int charsAfter;
        public int charsBefore;
        public String word;

        public Range(int charsBefore, int charsAfter, String word) {
            if (charsBefore < 0 || charsAfter < 0) {
                throw new IndexOutOfBoundsException();
            }
            this.charsBefore = charsBefore;
            this.charsAfter = charsAfter;
            this.word = word;
        }
    }

    public static class SelectedWord {
        public int end;
        public int start;
        public CharSequence word;

        public int len() {
            return this.end - this.start;
        }
    }

    private EditingUtil() {
    }

    public static void appendText(InputConnection connection, String newText) {
        if (connection != null) {
            connection.finishComposingText();
            CharSequence charBeforeCursor = connection.getTextBeforeCursor(1, 0);
            if (!(charBeforeCursor == null || charBeforeCursor.equals(" ") || charBeforeCursor.length() <= 0)) {
                newText = " " + newText;
            }
            connection.setComposingText(newText, 1);
        }
    }

    private static int getCursorPosition(InputConnection connection) {
        ExtractedText extracted = connection.getExtractedText(new ExtractedTextRequest(), 0);
        if (extracted == null) {
            return -1;
        }
        return extracted.startOffset + extracted.selectionStart;
    }

    public static String getWordAtCursor(InputConnection connection, Range range) {
        Range r = getWordRangeAtCursor(connection, range);
        return r == null ? null : r.word;
    }

    public static Range deleteWordAtCursor(InputConnection connection) {
        Range range = getWordRangeAtCursor(connection, null);
        if (range == null) {
            return null;
        }
        connection.finishComposingText();
        int newCursor = getCursorPosition(connection) - range.charsBefore;
        connection.setSelection(newCursor, newCursor);
        connection.deleteSurroundingText(0, range.charsBefore + range.charsAfter);
        return range;
    }

    private static Range getWordRangeAtCursor(InputConnection connection, Range range) {
        Range returnRange = null;
        if (connection != null) {
            CharSequence before = connection.getTextBeforeCursor(1000, 0);
            CharSequence after = connection.getTextAfterCursor(1000, 0);
            if (!(before == null || after == null)) {
                int start = before.length();
                while (start > 0 && !isWhitespace(before.charAt(start - 1))) {
                    start--;
                }
                int end = -1;
                do {
                    end++;
                    if (end >= after.length()) {
                        break;
                    }
                } while (!isWhitespace(after.charAt(end)));
                int cursor = getCursorPosition(connection);
                if (start >= 0 && cursor + end <= after.length() + before.length()) {
                    String word = before.toString().substring(start, before.length()) + after.toString().substring(0, end);
                    returnRange = range != null ? range : new Range();
                    returnRange.charsBefore = before.length() - start;
                    returnRange.charsAfter = end;
                    returnRange.word = word;
                }
            }
        }
        return returnRange;
    }

    private static boolean isWhitespace(char c) {
        return M.isWordSep(c);
    }

    private static boolean isWordBoundary(CharSequence singleChar) {
        return (TextUtils.isEmpty(singleChar) || (singleChar != null && singleChar.length() > 0)) ? M.isWordSep(singleChar.charAt(0)) : false;
    }

    public static SelectedWord getWordAtCursorOrSelection(InputConnection ic, int selStart, int selEnd) {
        CharSequence touching;
        SelectedWord selWord;
        if (selStart == selEnd) {
            try {
                Range range = new Range();
                touching = getWordAtCursor(ic, range);
                if (!TextUtils.isEmpty(touching)) {
                    selWord = new SelectedWord();
                    selWord.word = touching;
                    selWord.start = selStart - range.charsBefore;
                    selWord.end = range.charsAfter + selEnd;
                    return selWord;
                }
            } catch (Throwable th) {
            }
            return null;
        } else if (!isWordBoundary(ic.getTextBeforeCursor(1, 0))) {
            return null;
        } else {
            if (!isWordBoundary(ic.getTextAfterCursor(1, 0))) {
                return null;
            }
            touching = getSelectedText(ic, selStart, selEnd);
            if (TextUtils.isEmpty(touching)) {
                return null;
            }
            int length = touching.length();
            for (int i = 0; i < length; i++) {
                if (M.isWordSep(touching.charAt(i))) {
                    return null;
                }
            }
            selWord = new SelectedWord();
            selWord.start = selStart;
            selWord.end = selEnd;
            selWord.word = touching;
            return selWord;
        }
    }

    private static void initializeMethodsForReflection() {
        try {
            sMethodGetSelectedText = InputConnection.class.getMethod("getSelectedText", new Class[]{Integer.TYPE});
            sMethodSetComposingRegion = InputConnection.class.getMethod("setComposingRegion", new Class[]{Integer.TYPE, Integer.TYPE});
        } catch (NoSuchMethodException e) {
        }
        sMethodsInitialized = true;
    }

    private static CharSequence getSelectedText(InputConnection ic, int selStart, int selEnd) {
        if (!sMethodsInitialized) {
            initializeMethodsForReflection();
        }
        if (sMethodGetSelectedText != null) {
            try {
                return (CharSequence) sMethodGetSelectedText.invoke(ic, new Object[]{Integer.valueOf(0)});
            } catch (InvocationTargetException e) {
            } catch (IllegalArgumentException e2) {
            } catch (IllegalAccessException e3) {
            }
        }
        ic.setSelection(selStart, selEnd);
        CharSequence result = ic.getTextAfterCursor(selEnd - selStart, 0);
        ic.setSelection(selStart, selEnd);
        return result;
    }

    public static void underlineWord(InputConnection ic, SelectedWord word) {
        if (!sMethodsInitialized) {
            initializeMethodsForReflection();
        }
        if (sMethodSetComposingRegion != null) {
            try {
                sMethodSetComposingRegion.invoke(ic, new Object[]{Integer.valueOf(word.start), Integer.valueOf(word.end)});
            } catch (InvocationTargetException e) {
            } catch (IllegalArgumentException e2) {
            } catch (IllegalAccessException e3) {
            }
        }
    }
}
