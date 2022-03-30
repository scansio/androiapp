package com.klye.ime.latin;

import android.inputmethodservice.Keyboard.Key;

class MiniKeyboardKeyDetector extends KeyDetector {
    private static final int MAX_NEARBY_KEYS = 4;
    private final int mSlideAllowanceSquare;
    private final int mSlideAllowanceSquareTop = (this.mSlideAllowanceSquare * 2);

    public MiniKeyboardKeyDetector(float slideAllowance) {
        this.mSlideAllowanceSquare = (int) (slideAllowance * slideAllowance);
    }

    protected int getMaxNearbyKeys() {
        return 4;
    }

    public int getKeyIndexAndNearbyCodes(int x, int y, int[] allKeys) {
        Key[] keys = getKeys();
        int touchX = getTouchX(x);
        int touchY = getTouchY(y);
        int closestKeyIndex = -1;
        int closestKeyDist = y < 0 ? this.mSlideAllowanceSquareTop : this.mSlideAllowanceSquare;
        int keyCount = keys.length;
        for (int i = 0; i < keyCount; i++) {
            int dist = keys[i].squaredDistanceFrom(touchX, touchY);
            if (dist < closestKeyDist) {
                closestKeyIndex = i;
                closestKeyDist = dist;
            }
        }
        if (!(allKeys == null || closestKeyIndex == -1)) {
            allKeys[0] = keys[closestKeyIndex].codes[0];
        }
        return closestKeyIndex;
    }
}
