package com.klye.ime.latin;

class ModifierKeyState {
    private static final int MOMENTARY = 2;
    private static final int PRESSING = 1;
    private static final int RELEASING = 0;
    private int mState = 0;

    ModifierKeyState() {
    }

    public void onPress() {
        this.mState = 1;
    }

    public void onRelease() {
        this.mState = 0;
    }

    public void onOtherKeyPressed() {
        if (this.mState == 1) {
            this.mState = 2;
        }
    }

    public boolean isMomentary() {
        return this.mState == 2;
    }
}
