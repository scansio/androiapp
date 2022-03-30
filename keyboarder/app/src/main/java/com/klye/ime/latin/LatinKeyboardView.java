package com.klye.ime.latin;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.Keyboard.Key;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.MotionEvent;
import com.klye.ime.latin.LatinIMEUtil.GCUtils;
import java.util.List;

public class LatinKeyboardView extends LatinKeyboardBaseView {
    static final boolean DEBUG_AUTO_PLAY = false;
    static final boolean DEBUG_LINE = false;
    private static final int MSG_TOUCH_DOWN = 1;
    private static final int MSG_TOUCH_UP = 2;
    private Key[] mAsciiKeys;
    private boolean mDownDelivered;
    Handler mHandler2;
    private int mJumpThresholdSquare;
    private int mLastX;
    private int mLastY;
    private Paint mPaint;
    private Keyboard mPhoneKeyboard;
    private boolean mPlaying;
    private int mStringIndex;
    private String mStringToPlay;

    public LatinKeyboardView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LatinKeyboardView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.mJumpThresholdSquare = Integer.MAX_VALUE;
        this.mAsciiKeys = new Key[256];
        this.g = new G();
    }

    public void setPhoneKeyboard(Keyboard phoneKeyboard) {
        this.mPhoneKeyboard = phoneKeyboard;
    }

    public void setPreviewEnabled(boolean previewEnabled) {
        if (getKeyboard() == this.mPhoneKeyboard) {
            super.setPreviewEnabled(false);
        } else {
            super.setPreviewEnabled(previewEnabled);
        }
    }

    public void setKeyboard(Keyboard k, int vc) {
        super.setKeyboard(k, vc);
        this.mJumpThresholdSquare = k.getMinWidth() / 7;
        this.mJumpThresholdSquare *= this.mJumpThresholdSquare;
        setKeyboardLocal(k);
    }

    protected boolean onLongPress(Key key) {
        int pc = key.codes[0];
        switch (pc) {
            case -100:
                return invokeOnKey(-101);
            case Suggest.APPROX_MAX_WORD_LENGTH /*32*/:
                if (M.lpsl && getLKB().getLanguageChangeDirection() == 0) {
                    key.popupResId = R.xml.kbd_popup_template;
                    key.popupCharacters = "".substring(0, M.nl());
                    break;
                }
                return false;
            default:
                if (pc == 48 && getKeyboard() == this.mPhoneKeyboard) {
                    return invokeOnKey(43);
                }
        }
        return super.onLongPress(key);
    }

    private boolean invokeOnKey(int primaryCode) {
        getOnKeyboardActionListener().onKey(primaryCode, null, -1, -1);
        return true;
    }

    public boolean setShiftLocked(boolean shiftLocked) {
        Keyboard keyboard = getKeyboard();
        if (!(keyboard instanceof LatinKeyboard)) {
            return false;
        }
        ((LatinKeyboard) keyboard).setShiftLocked(shiftLocked);
        invalidateAllKeys();
        return true;
    }

    public boolean onTouchEvent(MotionEvent me) {
        try {
            LatinKeyboard keyboard = (LatinKeyboard) getKeyboard();
            if (me.getAction() == 0) {
                keyboard.keyReleased();
            }
            if (me.getAction() == 1 && M.swsl) {
                int languageDirection = keyboard.getLanguageChangeDirection();
                if (languageDirection != 0) {
                    getOnKeyboardActionListener().onKey(languageDirection == 1 ? -104 : -105, null, this.mLastX, this.mLastY);
                    me.setAction(3);
                    keyboard.keyReleased();
                    return super.onTouchEvent(me);
                }
            }
        } catch (OutOfMemoryError e) {
            M.msg(getContext(), "Your device is running out of memory.\n" + e.getLocalizedMessage());
        }
        return super.onTouchEvent(me);
    }

    private void setKeyboardLocal(Keyboard k) {
    }

    private void findKeys() {
        List<Key> keys = getKeyboard().getKeys();
        for (int i = 0; i < keys.size(); i++) {
            int code = ((Key) keys.get(i)).codes[0];
            if (code >= 0 && code <= 255) {
                this.mAsciiKeys[code] = (Key) keys.get(i);
            }
        }
    }

    public void startPlaying(String s) {
    }

    public void draw(Canvas c) {
        GCUtils.getInstance().reset();
        boolean tryGC = true;
        for (int i = 0; i < 5 && tryGC; i++) {
            try {
                super.draw(c);
                tryGC = false;
            } catch (OutOfMemoryError e) {
                tryGC = GCUtils.getInstance().tryGCOrWait("LatinKeyboardView", e);
            }
        }
    }
}
