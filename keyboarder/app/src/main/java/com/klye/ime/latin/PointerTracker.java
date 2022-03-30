package com.klye.ime.latin;

import android.content.res.Resources;
import android.inputmethodservice.Keyboard.Key;
import com.klye.ime.latin.LatinKeyboardBaseView.OnKeyboardActionListener;

public class PointerTracker {
    private static final boolean DEBUG = false;
    private static final boolean DEBUG_MOVE = false;
    private static final int[] KEY_DELETE = new int[]{-5};
    private static final int NOT_A_KEY = -1;
    private static final String TAG = "PointerTracker";
    private final int mDelayBeforeKeyRepeatStart;
    private final UIHandler mHandler;
    private final boolean mHasDistinctMultitouch;
    private boolean mInMultiTap;
    private boolean mIsRepeatableKey;
    private boolean mKeyAlreadyProcessed;
    private final KeyDetector mKeyDetector;
    private int mKeyHysteresisDistanceSquared = -1;
    private final KeyState mKeyState;
    private Key[] mKeys;
    private int mLastSentIndex;
    private long mLastTapTime;
    private OnKeyboardActionListener mListener;
    public final int mPointerId;
    private final StringBuilder mPreviewLabel = new StringBuilder(1);
    private int mPreviousKey = -1;
    private final UIProxy mProxy;
    private int mTapCount;
    int xp;
    int yp;

    public interface UIProxy {
        boolean hasDistinctMultitouch();

        void invalidateKey(Key key);

        void showPreview(int i, PointerTracker pointerTracker);
    }

    private static class KeyState {
        private long mDownTime;
        private final KeyDetector mKeyDetector;
        private int mKeyIndex = -1;
        private int mKeyX;
        private int mKeyY;
        private int mLastX;
        private int mLastY;
        private int mStartX;
        private int mStartY;

        public KeyState(KeyDetector keyDetecor) {
            this.mKeyDetector = keyDetecor;
        }

        public int getKeyIndex() {
            return this.mKeyIndex;
        }

        public int getKeyX() {
            return this.mKeyX;
        }

        public int getKeyY() {
            return this.mKeyY;
        }

        public int getStartX() {
            return this.mStartX;
        }

        public int getStartY() {
            return this.mStartY;
        }

        public long getDownTime() {
            return this.mDownTime;
        }

        public int getLastX() {
            return this.mLastX;
        }

        public int getLastY() {
            return this.mLastY;
        }

        public int onDownKey(int x, int y, long eventTime) {
            this.mStartX = x;
            this.mStartY = y;
            this.mDownTime = eventTime;
            return onMoveToNewKey(onMoveKeyInternal(x, y), x, y);
        }

        private int onMoveKeyInternal(int x, int y) {
            this.mLastX = x;
            this.mLastY = y;
            return this.mKeyDetector.getKeyIndexAndNearbyCodes(x, y, null);
        }

        public int onMoveKey(int x, int y) {
            return onMoveKeyInternal(x, y);
        }

        public int onMoveToNewKey(int keyIndex, int x, int y) {
            this.mKeyIndex = keyIndex;
            this.mKeyX = x;
            this.mKeyY = y;
            return keyIndex;
        }

        public int onUpKey(int x, int y) {
            return onMoveKeyInternal(x, y);
        }

        public void onSetKeyboard() {
            this.mKeyIndex = this.mKeyDetector.getKeyIndexAndNearbyCodes(this.mKeyX, this.mKeyY, null);
        }
    }

    public PointerTracker(int id, UIHandler handler, KeyDetector keyDetector, UIProxy proxy, Resources res) {
        if (proxy == null || handler == null || keyDetector == null) {
            throw new NullPointerException();
        }
        this.mPointerId = id;
        this.mProxy = proxy;
        this.mHandler = handler;
        this.mKeyDetector = keyDetector;
        this.mKeyState = new KeyState(keyDetector);
        this.mHasDistinctMultitouch = proxy.hasDistinctMultitouch();
        this.mDelayBeforeKeyRepeatStart = res.getInteger(R.integer.config_delay_before_key_repeat_start);
        resetMultiTap();
    }

    public void setOnKeyboardActionListener(OnKeyboardActionListener listener) {
        this.mListener = listener;
    }

    public void setKeyboard(Key[] keys, float keyHysteresisDistance) {
        if (keys == null || keyHysteresisDistance < 0.0f) {
            throw new IllegalArgumentException();
        }
        this.mKeys = keys;
        this.mKeyHysteresisDistanceSquared = (int) (keyHysteresisDistance * keyHysteresisDistance);
        this.mKeyState.onSetKeyboard();
    }

    private boolean isValidKeyIndex(int keyIndex) {
        return keyIndex >= 0 && keyIndex < this.mKeys.length && this.mKeys[keyIndex].codes[0] != 0;
    }

    public Key getKey(int keyIndex) {
        return isValidKeyIndex(keyIndex) ? this.mKeys[keyIndex] : null;
    }

    private boolean isModifierInternal(int keyIndex) {
        Key key = getKey(keyIndex);
        if (key == null) {
            return false;
        }
        int primaryCode = key.codes[0];
        if (primaryCode == -1 || primaryCode == -2) {
            return true;
        }
        return false;
    }

    public boolean isModifier() {
        return isModifierInternal(this.mKeyState.getKeyIndex());
    }

    public boolean isOnModifierKey(int x, int y) {
        return isModifierInternal(this.mKeyDetector.getKeyIndexAndNearbyCodes(x, y, null));
    }

    public boolean isSpaceKey(int keyIndex) {
        Key key = getKey(keyIndex);
        if (key == null || key.codes[0] != 32) {
            return false;
        }
        return true;
    }

    public void updateKey(int keyIndex) {
        if (!this.mKeyAlreadyProcessed) {
            int oldKeyIndex = this.mPreviousKey;
            this.mPreviousKey = keyIndex;
            if (keyIndex != oldKeyIndex) {
                if (isValidKeyIndex(oldKeyIndex)) {
                    this.mKeys[oldKeyIndex].onReleased(keyIndex == -1);
                    this.mProxy.invalidateKey(this.mKeys[oldKeyIndex]);
                }
                if (isValidKeyIndex(keyIndex)) {
                    this.mKeys[keyIndex].onPressed();
                    this.mProxy.invalidateKey(this.mKeys[keyIndex]);
                }
            }
        }
    }

    public void setAlreadyProcessed() {
        this.mKeyAlreadyProcessed = true;
    }

    public void onTouchEvent(int action, int x, int y, long eventTime) {
        switch (action) {
            case 0:
            case 5:
                onDownEvent(x, y, eventTime);
                return;
            case 1:
            case 6:
                onUpEvent(x, y, eventTime);
                return;
            case 2:
                onMoveEvent(x, y, eventTime);
                return;
            case 3:
                onCancelEvent(x, y, eventTime);
                return;
            default:
                return;
        }
    }

    public void onDownEvent(int x, int y, long eventTime) {
        this.xp = x;
        this.yp = y;
        int keyIndex = this.mKeyState.onDownKey(x, y, eventTime);
        this.mKeyAlreadyProcessed = false;
        this.mIsRepeatableKey = false;
        checkMultiTap(eventTime, keyIndex);
        if (this.mListener != null && isValidKeyIndex(keyIndex)) {
            this.mListener.onPress(this.mKeys[keyIndex].codes[0]);
            keyIndex = this.mKeyState.getKeyIndex();
        }
        if (isValidKeyIndex(keyIndex)) {
            if (this.mKeys[keyIndex].repeatable) {
                repeatKey(keyIndex);
                this.mHandler.startKeyRepeatTimer((long) this.mDelayBeforeKeyRepeatStart, keyIndex, this);
                this.mIsRepeatableKey = true;
            }
            this.mHandler.startLongPressTimer(M.lpto(this.mKeys[keyIndex]), keyIndex, this);
        }
        if (M.mIme.mPopupOn) {
            showKeyPreviewAndUpdateKey(keyIndex);
        } else {
            updateKey(keyIndex);
        }
    }

    public void onMoveEvent(int x, int y, long eventTime) {
        if (!this.mKeyAlreadyProcessed) {
            KeyState keyState = this.mKeyState;
            int keyIndex = keyState.onMoveKey(x, y);
            if (isValidKeyIndex(keyIndex)) {
                if (keyState.getKeyIndex() == -1) {
                    keyState.onMoveToNewKey(keyIndex, x, y);
                    this.mHandler.startLongPressTimer(M.lpto(this.mKeys[keyIndex]), keyIndex, this);
                } else if (!isMinorMoveBounce(x, y, keyIndex)) {
                    resetMultiTap();
                    keyState.onMoveToNewKey(keyIndex, x, y);
                    this.mHandler.startLongPressTimer(M.lpto(this.mKeys[keyIndex]), keyIndex, this);
                }
            } else if (!M.jaT9(R.xml.popup_ja)) {
                if (keyState.getKeyIndex() != -1) {
                    keyState.onMoveToNewKey(keyIndex, x, y);
                    this.mHandler.cancelLongPressTimer();
                } else if (!isMinorMoveBounce(x, y, keyIndex)) {
                    resetMultiTap();
                    keyState.onMoveToNewKey(keyIndex, x, y);
                    this.mHandler.cancelLongPressTimer();
                }
            }
            M.sld = emv(x, y);
            showKeyPreviewAndUpdateKey(this.mKeyState.getKeyIndex());
        }
    }

    private boolean emv(int x, int y) {
        if (Math.abs(this.xp - x) > M.mm1 || Math.abs(this.yp - y) > M.mm1) {
            return true;
        }
        return false;
    }

    public void onUpEvent(int x, int y, long eventTime) {
        this.mHandler.cancelKeyTimers();
        this.mHandler.cancelPopupPreview();
        if (!this.mKeyAlreadyProcessed) {
            int keyIndex = this.mKeyState.onUpKey(x, y);
            keyIndex = this.mKeyState.getKeyIndex();
            x = this.mKeyState.getKeyX();
            y = this.mKeyState.getKeyY();
            showKeyPreviewAndUpdateKey(-1);
            if (!this.mIsRepeatableKey) {
                detectAndSendKey(keyIndex, x, y, eventTime);
            }
            if (isValidKeyIndex(keyIndex)) {
                this.mProxy.invalidateKey(this.mKeys[keyIndex]);
            }
        }
    }

    public void onCancelEvent(int x, int y, long eventTime) {
        this.mHandler.cancelKeyTimers();
        this.mHandler.cancelPopupPreview();
        showKeyPreviewAndUpdateKey(-1);
        int keyIndex = this.mKeyState.getKeyIndex();
        if (isValidKeyIndex(keyIndex)) {
            this.mProxy.invalidateKey(this.mKeys[keyIndex]);
        }
    }

    public void repeatKey(int keyIndex) {
        Key key = getKey(keyIndex);
        if (key != null) {
            detectAndSendKey(keyIndex, key.x, key.y, -1);
        }
    }

    public int getLastX() {
        return this.mKeyState.getLastX();
    }

    public int getLastY() {
        return this.mKeyState.getLastY();
    }

    public long getDownTime() {
        return this.mKeyState.getDownTime();
    }

    int getStartX() {
        return this.mKeyState.getStartX();
    }

    int getStartY() {
        return this.mKeyState.getStartY();
    }

    private boolean isMinorMoveBounce(int x, int y, int newKey) {
        if (this.mKeys == null || this.mKeyHysteresisDistanceSquared < 0) {
            throw new IllegalStateException("keyboard and/or hysteresis not set");
        }
        int curKey = this.mKeyState.getKeyIndex();
        if (newKey == curKey) {
            return true;
        }
        if (!isValidKeyIndex(curKey)) {
            return false;
        }
        if (getSquareDistanceToKeyEdge(x, y, this.mKeys[curKey]) >= this.mKeyHysteresisDistanceSquared) {
            return false;
        }
        return true;
    }

    private static int getSquareDistanceToKeyEdge(int x, int y, Key key) {
        int left = key.x;
        int right = key.x + key.width;
        int top = key.y;
        int bottom = key.y + key.height;
        int edgeX = x < left ? left : x > right ? right : x;
        int edgeY = y < top ? top : y > bottom ? bottom : y;
        int dx = x - edgeX;
        int dy = y - edgeY;
        return (dx * dx) + (dy * dy);
    }

    private void showKeyPreviewAndUpdateKey(int keyIndex) {
        updateKey(keyIndex);
        if (this.mHasDistinctMultitouch && isModifier()) {
            this.mProxy.showPreview(-1, this);
        } else {
            this.mProxy.showPreview(keyIndex, this);
        }
    }

    private void detectAndSendKey(int index, int x, int y, long eventTime) {
        OnKeyboardActionListener listener = this.mListener;
        Key key = getKey(index);
        if (key != null) {
            if (key.text == null) {
                int code = key.codes[0];
                int[] codes = this.mKeyDetector.newCodeArray();
                if (M.iv.isShifted() && ((M.tlpc || M.zw) && M.isAK())) {
                    if (M.bo((char) code)) {
                        code = S.boshf(code);
                    } else if (!(!M.nm || key.modifier || key.popupCharacters == null)) {
                        code = key.popupCharacters.charAt(0);
                    }
                    switch (code) {
                        case 44:
                        case 46:
                            code = S.toUpper((char) code);
                            break;
                    }
                }
                this.mKeyDetector.getKeyIndexAndNearbyCodes(x, y, codes);
                if (this.mInMultiTap) {
                    if (this.mTapCount == -1) {
                        this.mTapCount = 0;
                    } else if (key.codes[0] != -1) {
                        this.mListener.onKey(-5, KEY_DELETE, x, y);
                    }
                    code = key.codes[this.mTapCount % key.codes.length];
                }
                if (!M.tlpc && codes.length >= 2 && codes[0] != code && codes[1] == code) {
                    codes[1] = codes[0];
                    codes[0] = code;
                }
                if (listener != null) {
                    listener.onKey(code, codes, x, y);
                    listener.onRelease(code);
                }
            } else if (listener != null) {
                listener.onText(key.text);
                listener.onRelease(-1);
            }
            this.mLastSentIndex = index;
            this.mLastTapTime = eventTime;
        } else if (listener != null) {
            listener.onCancel();
        }
    }

    public CharSequence getPreviewText(Key key) {
        int i = 0;
        if (!this.mInMultiTap) {
            return key.label;
        }
        this.mPreviewLabel.setLength(0);
        StringBuilder stringBuilder = this.mPreviewLabel;
        int[] iArr = key.codes;
        if (this.mTapCount >= 0) {
            i = this.mTapCount;
        }
        stringBuilder.append((char) iArr[i]);
        return this.mPreviewLabel;
    }

    private void resetMultiTap() {
        this.mLastSentIndex = -1;
        this.mTapCount = 0;
        this.mLastTapTime = -1;
        this.mInMultiTap = false;
    }

    private void checkMultiTap(long eventTime, int keyIndex) {
        Key key = getKey(keyIndex);
        if (key != null) {
            boolean isMultiTap = eventTime < this.mLastTapTime + M.mtt && keyIndex == this.mLastSentIndex;
            int l = key.codes.length;
            if (l > 1) {
                this.mInMultiTap = true;
                if (isMultiTap) {
                    this.mTapCount = (this.mTapCount + 1) % l;
                } else {
                    this.mTapCount = -1;
                }
            } else if (!isMultiTap) {
                resetMultiTap();
            }
        }
    }
}
