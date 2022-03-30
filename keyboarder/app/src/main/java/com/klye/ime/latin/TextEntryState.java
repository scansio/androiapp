package com.klye.ime.latin;

import android.content.Context;
import android.inputmethodservice.Keyboard.Key;
import android.text.format.DateFormat;
import android.util.Log;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;

public class TextEntryState {
    private static final boolean DBG = false;
    private static boolean LOGGING = false;
    private static final String TAG = "TextEntryState";
    private static int sActualChars;
    private static int sAutoSuggestCount = 0;
    private static int sAutoSuggestUndoneCount = 0;
    private static int sBackspaceCount = 0;
    private static FileOutputStream sKeyLocationFile;
    private static int sManualSuggestCount = 0;
    private static int sSessionCount = 0;
    private static State sState = State.UNKNOWN;
    private static int sTypedChars;
    private static FileOutputStream sUserActionFile;
    private static int sWordNotInDictionaryCount = 0;

    public enum State {
        UNKNOWN,
        START,
        IN_WORD,
        ACCEPTED_DEFAULT,
        PICKED_SUGGESTION,
        PUNCTUATION_AFTER_WORD,
        PUNCTUATION_AFTER_ACCEPTED,
        SPACE_AFTER_ACCEPTED,
        SPACE_AFTER_PICKED,
        UNDO_COMMIT,
        CORRECTING,
        PICKED_CORRECTION
    }

    public static void newSession(Context context) {
        sSessionCount++;
        sAutoSuggestCount = 0;
        sBackspaceCount = 0;
        sAutoSuggestUndoneCount = 0;
        sManualSuggestCount = 0;
        sWordNotInDictionaryCount = 0;
        sTypedChars = 0;
        sActualChars = 0;
        sState = State.START;
        if (LOGGING) {
            try {
                sKeyLocationFile = context.openFileOutput("key.txt", 32768);
                sUserActionFile = context.openFileOutput("action.txt", 32768);
            } catch (IOException ioe) {
                Log.e(TAG, "Couldn't open file for output: " + ioe);
            }
        }
    }

    public static void endSession() {
        if (sKeyLocationFile != null) {
            try {
                sKeyLocationFile.close();
                sUserActionFile.write((DateFormat.format("MM:dd hh:mm:ss", Calendar.getInstance().getTime()).toString() + " BS: " + sBackspaceCount + " auto: " + sAutoSuggestCount + " manual: " + sManualSuggestCount + " typed: " + sWordNotInDictionaryCount + " undone: " + sAutoSuggestUndoneCount + " saved: " + (((float) (sActualChars - sTypedChars)) / ((float) sActualChars)) + "\n").getBytes());
                sUserActionFile.close();
                sKeyLocationFile = null;
                sUserActionFile = null;
            } catch (IOException e) {
            }
        }
    }

    public static void acceptedDefault(CharSequence typedWord, CharSequence actualWord) {
        if (typedWord != null) {
            if (!typedWord.equals(actualWord)) {
                sAutoSuggestCount++;
            }
            sTypedChars += typedWord.length();
            sActualChars += actualWord.length();
            sState = State.ACCEPTED_DEFAULT;
            displayState();
        }
    }

    public static void backToAcceptedDefault(CharSequence typedWord) {
        if (typedWord != null || M.bgd) {
            switch (sState) {
                case SPACE_AFTER_ACCEPTED:
                case PUNCTUATION_AFTER_ACCEPTED:
                case IN_WORD:
                    sState = State.ACCEPTED_DEFAULT;
                    break;
            }
            displayState();
        }
    }

    public static void acceptedTyped(CharSequence typedWord) {
        sWordNotInDictionaryCount++;
        sState = State.PICKED_SUGGESTION;
        displayState();
    }

    public static void acceptedSuggestion(CharSequence typedWord, CharSequence actualWord) {
        sManualSuggestCount++;
        State oldState = sState;
        if (typedWord.equals(actualWord)) {
            acceptedTyped(typedWord);
        }
        if (oldState == State.CORRECTING || oldState == State.PICKED_CORRECTION) {
            sState = State.PICKED_CORRECTION;
        } else {
            sState = State.PICKED_SUGGESTION;
        }
        displayState();
    }

    public static void selectedForCorrection() {
        sState = State.CORRECTING;
        displayState();
    }

    public static void typedCharacter(char c, boolean isSeparator) {
        boolean isSpace = c == ' ';
        switch (AnonymousClass1.$SwitchMap$com$klye$ime$latin$TextEntryState$State[sState.ordinal()]) {
            case 1:
            case 2:
            case R.styleable.LatinKeyboardBaseView_keyHysteresisDistance /*8*/:
            case R.styleable.LatinKeyboardBaseView_verticalCorrection /*9*/:
            case R.styleable.LatinKeyboardBaseView_popupLayout /*10*/:
                if (!isSpace && !isSeparator) {
                    sState = State.IN_WORD;
                    break;
                } else {
                    sState = State.START;
                    break;
                }
                break;
            case 3:
                if (isSpace || isSeparator) {
                    sState = State.START;
                    break;
                }
            case 4:
            case 5:
                if (!isSpace) {
                    if (!isSeparator) {
                        sState = State.IN_WORD;
                        break;
                    } else {
                        sState = State.PUNCTUATION_AFTER_ACCEPTED;
                        break;
                    }
                }
                sState = State.SPACE_AFTER_ACCEPTED;
                break;
            case 6:
            case 7:
                if (!isSpace) {
                    if (!isSeparator) {
                        sState = State.IN_WORD;
                        break;
                    } else {
                        sState = State.PUNCTUATION_AFTER_ACCEPTED;
                        break;
                    }
                }
                sState = State.SPACE_AFTER_PICKED;
                break;
            case R.styleable.LatinKeyboardBaseView_shadowColor /*11*/:
                if (!isSpace && !isSeparator) {
                    sState = State.IN_WORD;
                    break;
                } else {
                    sState = State.ACCEPTED_DEFAULT;
                    break;
                }
                break;
            case R.styleable.LatinKeyboardBaseView_shadowRadius /*12*/:
                sState = State.START;
                break;
        }
        displayState();
    }

    public static void backspace() {
        if (sState == State.ACCEPTED_DEFAULT) {
            sState = State.UNDO_COMMIT;
            sAutoSuggestUndoneCount++;
        } else if (sState == State.UNDO_COMMIT) {
            sState = State.IN_WORD;
        }
        sBackspaceCount++;
        displayState();
    }

    public static void reset() {
        sState = State.START;
        displayState();
    }

    public static State getState() {
        return sState;
    }

    public static boolean isCorrecting() {
        return sState == State.CORRECTING || sState == State.PICKED_CORRECTION;
    }

    public static void keyPressedAt(Key key, int x, int y) {
        if (LOGGING && sKeyLocationFile != null && key.codes[0] >= 32) {
            try {
                sKeyLocationFile.write(("KEY: " + ((char) key.codes[0]) + " X: " + x + " Y: " + y + " MX: " + (key.x + (key.width / 2)) + " MY: " + (key.y + (key.height / 2)) + "\n").getBytes());
            } catch (IOException e) {
            }
        }
    }

    private static void displayState() {
    }
}
