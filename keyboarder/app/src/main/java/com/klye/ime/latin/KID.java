package com.klye.ime.latin;

import java.util.Arrays;

class KID {
    public final boolean mEnableShiftLock;
    public final boolean mHasVoice;
    private final int mHashCode;
    public final int mKeyboardMode;
    public final int mXml;

    public KID(int xml, int mode, boolean enableShiftLock, boolean hasVoice) {
        this.mXml = xml;
        this.mKeyboardMode = mode;
        this.mEnableShiftLock = enableShiftLock;
        this.mHasVoice = hasVoice;
        this.mHashCode = Arrays.hashCode(new Object[]{Integer.valueOf(xml), Integer.valueOf(mode), Boolean.valueOf(enableShiftLock), Boolean.valueOf(hasVoice)});
    }

    public KID(int xml, boolean hasVoice) {
        this(xml, 0, false, hasVoice);
    }

    public boolean equals(Object other) {
        return (other instanceof KID) && equals((KID) other);
    }

    private boolean equals(KID other) {
        return other.mXml == this.mXml && other.mKeyboardMode == this.mKeyboardMode && other.mEnableShiftLock == this.mEnableShiftLock && other.mHasVoice == this.mHasVoice;
    }

    public int hashCode() {
        return this.mHashCode;
    }

    public boolean is(int i) {
        return this.mXml == i;
    }

    public boolean isSplit() {
        switch (this.mXml) {
            case R.xml.kbd_big_split:
            case R.xml.kbd_clmks:
            case R.xml.kbd_dvrks:
            case R.xml.kbd_split:
                return true;
            default:
                return false;
        }
    }

    public boolean isBig() {
        return this.mXml == R.xml.kbd_big || this.mXml == R.xml.kbd_big_split;
    }

    public boolean isPh() {
        return this.mXml == R.xml.kbd_phone || this.mXml == R.xml.kbd_phone_black;
    }
}
