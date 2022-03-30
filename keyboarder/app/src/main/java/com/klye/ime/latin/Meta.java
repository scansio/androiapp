package com.klye.ime.latin;

import android.text.method.MetaKeyKeyListener;
import android.view.KeyEvent;

class Meta extends MetaKeyKeyListener {
    private static final int LOCKED_SHIFT = 8;
    private static final long META_ALT_MASK = 2207646745090L;
    private static final long META_ALT_PRESSED = 8589934592L;
    private static final long META_ALT_RELEASED = 2199023255552L;
    private static final long META_ALT_USED = 33554432;
    private static final long META_CAP_PRESSED = 4294967296L;
    private static final long META_CAP_RELEASED = 1099511627776L;
    private static final long META_CAP_USED = 16777216;
    private static final long META_SHIFT_MASK = 1103823372545L;
    private static final long META_SYM_MASK = 4415293490180L;
    private static final long META_SYM_PRESSED = 17179869184L;
    private static final long META_SYM_RELEASED = 4398046511104L;
    private static final long META_SYM_USED = 67108864;
    private static final int PRESSED_SHIFT = 32;
    private static final int RELEASED_SHIFT = 40;
    private static final int USED_SHIFT = 24;
    long st = 0;

    Meta() {
    }

    public static long handleKeyDown(long state, int keyCode, KeyEvent event) {
        if (keyCode == 59 || keyCode == 60) {
            return press(state, 1, META_SHIFT_MASK);
        }
        if (keyCode == 57 || keyCode == 58 || keyCode == 78) {
            return press(state, 2, META_ALT_MASK);
        }
        if (keyCode == 63) {
            return press(state, 4, META_SYM_MASK);
        }
        return state;
    }

    private static long press(long state, int what, long mask) {
        if (((((long) what) << 32) & state) != 0) {
            return state;
        }
        if (((((long) what) << 40) & state) != 0) {
            return (((mask ^ -1) & state) | ((long) what)) | (((long) what) << 8);
        }
        if (((((long) what) << USED_SHIFT) & state) != 0) {
            return state;
        }
        if (((((long) what) << 8) & state) != 0) {
            return state & (mask ^ -1);
        }
        return ((((long) what) | state) | (((long) what) << 32)) & ((((long) what) << 40) ^ -1);
    }

    public static long handleKeyUp(long state, int keyCode, KeyEvent event) {
        if (keyCode == 59 || keyCode == 60) {
            return release(state, 1, META_SHIFT_MASK);
        }
        if (keyCode == 57 || keyCode == 58 || keyCode == 78) {
            return release(state, 2, META_ALT_MASK);
        }
        if (keyCode == 63) {
            return release(state, 4, META_SYM_MASK);
        }
        return state;
    }

    private static long release(long state, int what, long mask) {
        if (((((long) what) << USED_SHIFT) & state) != 0) {
            return state & (mask ^ -1);
        }
        if (((((long) what) << 32) & state) != 0) {
            return ((((long) what) | state) | (((long) what) << RELEASED_SHIFT)) & ((((long) what) << 32) ^ -1);
        }
        return state;
    }

    public long kd(int kc, KeyEvent e) {
        this.st = M.stky ? handleKeyDown(this.st, kc, e) : (long) e.getMetaState();
        return this.st;
    }

    public void ku(int kc, KeyEvent e) {
        this.st = M.stky ? handleKeyUp(this.st, kc, e) : 0;
    }

    public void aakp() {
        this.st = M.stky ? adjustMetaAfterKeypress(this.st) : 0;
        if (M.ic != null) {
            if (getMetaState(this.st, 1) == 0) {
                M.ic.clearMetaKeyStates(1);
            }
            if (getMetaState(this.st, 2) == 0) {
                M.ic.clearMetaKeyStates(2);
            }
        }
    }

    public char guc(KeyEvent e) {
        return (char) e.getUnicodeChar(MetaKeyKeyListener.getMetaState(this.st));
    }

    public boolean g(int i) {
        return MetaKeyKeyListener.getMetaState(this.st, i) != 0;
    }

    public boolean b(int i) {
        return 0 != (this.st & ((long) i));
    }

    public boolean uc() {
        return b(1048833);
    }

    public boolean alt() {
        return b(514);
    }
}
