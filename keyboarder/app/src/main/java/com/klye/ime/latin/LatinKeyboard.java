package com.klye.ime.latin;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.content.res.XmlResourceParser;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.PorterDuff.Mode;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.Keyboard.Key;
import android.inputmethodservice.Keyboard.Row;
import android.text.TextPaint;
import android.view.ViewConfiguration;
import java.util.List;

public class LatinKeyboard extends Keyboard {
    private static final boolean DEBUG_PREFERRED_LETTER = false;
    private static final int OPACITY_FULLY_OPAQUE = 255;
    private static final float OVERLAP_PERCENTAGE_HIGH_PROB = 0.85f;
    private static final float OVERLAP_PERCENTAGE_LOW_PROB = 0.7f;
    private static final int SHIFT_LOCKED = 2;
    private static final int SHIFT_OFF = 0;
    private static final int SHIFT_ON = 1;
    private static final float SPACEBAR_DRAG_THRESHOLD = 0.6f;
    private static final float SPACEBAR_LANGUAGE_BASELINE = 0.76f;
    private static final float SPACEBAR_POPUP_MIN_RATIO = 0.4f;
    private static final int SPACE_LED_LENGTH_PERCENT = 80;
    private static final String TAG = "LatinKeyboard";
    private static CharSequence mADn = "▼";
    private static CharSequence mALt = "◀";
    private static CharSequence mARt = "▶";
    private static CharSequence mAUp = "▲";
    private static int sSpacebarVerticalCorrection;
    private final int NUMBER_HINT_COUNT = 10;
    private int[] ia;
    private Key m123Key;
    private CharSequence m123Label;
    private Drawable m123MicIcon;
    private final Context mContext;
    private boolean mCurrentlyInSpace;
    private Key mDelKey;
    private Key mEnterKey;
    private Key mF1Key;
    private boolean mHasVoiceButton;
    private boolean mIsBlackSym;
    private LanguageSwitcher mLanguageSwitcher;
    private String mLocale = null;
    private Drawable mMicIcon;
    private int mMode;
    private Drawable[] mNumberHintIcons = new Drawable[10];
    private Key mOptKey;
    private int mPrefDistance;
    private int mPrefLetter;
    private int[] mPrefLetterFrequencies;
    private int mPrefLetterX;
    private int mPrefLetterY;
    private final Resources mRes;
    private int mScale = 100;
    private Key mShiftKey;
    private int mShiftState = 0;
    private SlidingLocaleDrawable mSlidingLocaleIcon;
    private int mSpaceDragLastDiff;
    private int mSpaceDragStartX;
    private Key mSpaceKey;
    private int mSpaceKeyIndex = -1;
    private Key mTabKey;
    private final int mVerticalGap;
    private boolean mVoiceEnabled;
    private Key[] mxk;
    private Paint paint;

    class LatinKey extends Key {
        private final int[] KEY_STATE_FUNCTIONAL_NORMAL = new int[]{16842915};
        private final int[] KEY_STATE_FUNCTIONAL_PRESSED = new int[]{16842915, 16842919};
        private boolean mShiftLockEnabled;

        public LatinKey(Resources res, Row parent, int x, int y, XmlResourceParser parser) {
            super(res, parent, x, y, parser);
            if (this.popupCharacters != null && this.popupCharacters.length() == 0) {
                this.popupResId = 0;
            }
        }

        private void enableShiftLock() {
            this.mShiftLockEnabled = true;
        }

        private boolean isFunctionalKey() {
            return !this.sticky && this.modifier;
        }

        public void onReleased(boolean inside) {
            if (this.mShiftLockEnabled) {
                this.pressed = !this.pressed;
            } else {
                super.onReleased(inside);
            }
        }

        public boolean isInside(int x, int y) {
            return LatinKeyboard.this.isInside(this, x, y);
        }

        boolean isInsideSuper(int x, int y) {
            return super.isInside(x, y);
        }

        public int[] getCurrentDrawableState() {
            if (!isFunctionalKey()) {
                return super.getCurrentDrawableState();
            }
            if (this.pressed) {
                return this.KEY_STATE_FUNCTIONAL_PRESSED;
            }
            return this.KEY_STATE_FUNCTIONAL_NORMAL;
        }

        public int squaredDistanceFrom(int x, int y) {
            int xDist = (this.x + (this.width / 2)) - x;
            int yDist = (this.y + ((this.height + LatinKeyboard.this.mVerticalGap) / 2)) - y;
            return (xDist * xDist) + (yDist * yDist);
        }
    }

    class SlidingLocaleDrawable extends Drawable {
        private final Drawable mBackground;
        private String mCurrentLanguage;
        private int mDiff;
        private final int mHeight;
        private boolean mHitThreshold;
        private final int mMiddleX;
        private String mNextLanguage;
        private String mPrevLanguage;
        private final TextPaint mTextPaint = new TextPaint();
        private final int mThreshold;
        private final int mWidth;

        @SuppressLint({"ResourceAsColor", "ResourceAsColor"})
        public SlidingLocaleDrawable(Drawable background, int width, int height) {
            this.mBackground = background;
            LatinKeyboard.this.setDefaultBounds(this.mBackground);
            this.mWidth = width;
            this.mHeight = height;
            this.mTextPaint.setTextSize((float) LatinKeyboard.this.getTextSizeFromTheme(16973892, 18));
            this.mTextPaint.setColor(-1);
            this.mTextPaint.setTextAlign(Align.CENTER);
            this.mTextPaint.setAlpha(LatinKeyboard.OPACITY_FULLY_OPAQUE);
            this.mTextPaint.setAntiAlias(true);
            this.mMiddleX = (this.mWidth - this.mBackground.getIntrinsicWidth()) / 2;
            this.mThreshold = ViewConfiguration.get(LatinKeyboard.this.mContext).getScaledTouchSlop();
        }

        private void setDiff(int diff) {
            if (diff == Integer.MAX_VALUE) {
                this.mHitThreshold = false;
                this.mCurrentLanguage = null;
                return;
            }
            this.mDiff = diff;
            if (this.mDiff > this.mWidth) {
                this.mDiff = this.mWidth;
            }
            if (this.mDiff < (-this.mWidth)) {
                this.mDiff = -this.mWidth;
            }
            if (Math.abs(this.mDiff) > this.mThreshold) {
                this.mHitThreshold = true;
            }
            invalidateSelf();
        }

        public void draw(Canvas canvas) {
            canvas.save();
            if (this.mHitThreshold) {
                Paint paint = this.mTextPaint;
                int width = this.mWidth;
                int height = this.mHeight;
                int diff = this.mDiff;
                canvas.clipRect(0, 0, width, height);
                if (this.mCurrentLanguage == null) {
                    LanguageSwitcher ls = LatinKeyboard.this.mLanguageSwitcher;
                    this.mCurrentLanguage = M.reverse(M.dn(ls.getInputLanguage())).toString();
                    this.mNextLanguage = M.reverse(M.dn(ls.getNextInputLocale())).toString();
                    this.mPrevLanguage = M.reverse(M.dn(ls.getPrevInputLocale())).toString();
                }
                float baseline = (((float) this.mHeight) * LatinKeyboard.SPACEBAR_LANGUAGE_BASELINE) - paint.descent();
                paint.setColor(LatinKeyboard.this.mRes.getColor(R.color.latinkeyboard_feedback_language_text));
                char c = this.mCurrentLanguage.charAt(0);
                M.stf(paint, c);
                if (M.nsd(c)) {
                    M.rtld(canvas, this.mCurrentLanguage, diff, 0, width, height, paint);
                } else {
                    canvas.drawText(this.mCurrentLanguage, (float) ((width / 2) + diff), baseline, paint);
                }
                c = this.mNextLanguage.charAt(0);
                M.stf(paint, c);
                if (M.nsd(c)) {
                    M.rtld(canvas, this.mNextLanguage, diff - width, 0, width, height, paint);
                } else {
                    canvas.drawText(this.mNextLanguage, (float) (diff - (width / 2)), baseline, paint);
                }
                c = this.mPrevLanguage.charAt(0);
                M.stf(paint, c);
                if (M.nsd(c)) {
                    M.rtld(canvas, this.mPrevLanguage, diff + width, 0, width, height, paint);
                } else {
                    canvas.drawText(this.mPrevLanguage, (float) ((diff + width) + (width / 2)), baseline, paint);
                }
            }
            canvas.restore();
        }

        public int getOpacity() {
            return -3;
        }

        public void setAlpha(int alpha) {
        }

        public void setColorFilter(ColorFilter cf) {
        }

        public int getIntrinsicWidth() {
            return this.mWidth;
        }

        public int getIntrinsicHeight() {
            return this.mHeight;
        }
    }

    public LatinKeyboard(Context c1, int id, int mode) {
        int i;
        super(c1, id, mode);
        Resources res = c1.getResources();
        this.mMode = mode;
        this.mRes = res;
        this.mContext = c1;
        int sh = c1.getResources().getDisplayMetrics().heightPixels;
        int kh = super.getHeight();
        if (!(kh == 0 || sh == 0)) {
            if (kh * 10 < sh * 4) {
                this.mScale += ((sh * 40) / kh) - 100;
            }
            kh = getHeight();
            if (kh * 100 > sh * 65) {
                this.mScale -= 100 - ((sh * 65) / kh);
            }
            if (this.mScale < 30) {
                this.mScale = 100;
            }
        }
        List<Key> k = super.getKeys();
        int count = k.size();
        boolean rnh = M.ns && M.isM && 6881392 != M.mLC;
        int mw = M.dm.widthPixels / 2;
        for (i = 0; i < count; i++) {
            Key k2 = (Key) k.get(i);
            k2.height = ((k2.height * this.mScale) * M.ks()) / 10000;
            k2.y = ((k2.y * this.mScale) * M.ks()) / 10000;
            switch (k2.codes[0]) {
                case 19:
                    k2.repeatable = true;
                    k2.label = mAUp;
                    break;
                case 20:
                    k2.repeatable = true;
                    k2.label = mADn;
                    break;
                case 21:
                    k2.repeatable = true;
                    k2.label = mALt;
                    break;
                case 22:
                    k2.repeatable = true;
                    k2.label = mARt;
                    break;
            }
            if (!(!rnh || k2.codes[0] == -1 || k2.codes[0] == -5)) {
                if (k2.codes[0] == -2) {
                    rnh = false;
                } else {
                    String s = M.hint(i, k2, k2.x < mw);
                    if (s != null) {
                        alt(k2, s);
                    }
                }
            }
        }
        this.mMicIcon = res.getDrawable(R.drawable.sym_keyboard_mic);
        this.m123MicIcon = res.getDrawable(R.drawable.sym_keyboard_123_mic);
        sSpacebarVerticalCorrection = res.getDimensionPixelOffset(R.dimen.spacebar_vertical_correction);
        this.mSpaceKeyIndex = indexOf(32);
        this.ia = new int[]{this.mSpaceKeyIndex};
        this.mVerticalGap = super.getVerticalGap();
        if (!(this.mShiftKey == null || this.mDelKey == null)) {
            if (M.sw2) {
                int t = this.mShiftKey.x;
                this.mShiftKey.x = this.mDelKey.x;
                this.mDelKey.x = t;
                t = this.mShiftKey.edgeFlags;
                this.mShiftKey.edgeFlags = this.mDelKey.edgeFlags;
                this.mDelKey.edgeFlags = t;
            }
            this.mShiftKey.sticky = true;
            this.mDelKey.repeatable = true;
            this.mDelKey.sticky = false;
        }
        if (M.awk && !M.isLand() && !M.kid.is(R.xml.kbd_edit) && !M.kid.is(R.xml.kbd_hw)) {
            this.mSpaceKey.edgeFlags = 0;
            CharSequence[] ic = new CharSequence[]{mAUp, mADn, mALt, mARt};
            int i2 = 4;
            int[] c = new int[]{19, 20, 21, 22};
            int w = M.dm.widthPixels / 4;
            int x = 0;
            int h = this.mEnterKey.height;
            int y = this.mEnterKey.y + h;
            h = (h * 3) / 4;
            for (i = 0; i < this.mxk.length; i++) {
                this.mxk[i].y = y;
                this.mxk[i].height = h;
                Key key = this.mxk[i];
                this.mxk[i].icon = null;
                key.iconPreview = null;
                this.mxk[i].label = ic[i];
                this.mxk[i].width = w;
                this.mxk[i].x = x;
                this.mxk[i].codes[0] = c[i];
                this.mxk[i].repeatable = true;
                x += w;
                k.add(this.mxk[i]);
            }
        }
    }

    /* JADX WARNING: Missing block: B:235:0x0575, code:
            if (r4 == false) goto L_0x0577;
     */
    /* JADX WARNING: Missing block: B:237:0x0580, code:
            if (r4 == false) goto L_0x0582;
     */
    /* JADX WARNING: Missing block: B:239:0x058b, code:
            if (r4 == false) goto L_0x058d;
     */
    private void pk(android.inputmethodservice.Keyboard.Key r16, android.content.res.Resources r17) {
        /*
        r15 = this;
        r0 = r16;
        r12 = r0.codes;
        r13 = 0;
        r6 = r12[r13];
        r12 = com.klye.ime.latin.M.kid;
        r13 = 2131034129; // 0x7f050011 float:1.7678767E38 double:1.0528707533E-314;
        r5 = r12.is(r13);
        r12 = com.klye.ime.latin.M.kid;
        r13 = 2131034132; // 0x7f050014 float:1.7678773E38 double:1.0528707547E-314;
        r9 = r12.is(r13);
        r12 = com.klye.ime.latin.M.sm3;
        if (r12 == 0) goto L_0x0027;
    L_0x001d:
        if (r9 == 0) goto L_0x0053;
    L_0x001f:
        r12 = com.klye.ime.latin.M.dm;
        r12 = r12.widthPixels;
        r13 = 800; // 0x320 float:1.121E-42 double:3.953E-321;
        if (r12 >= r13) goto L_0x0053;
    L_0x0027:
        if (r5 != 0) goto L_0x002b;
    L_0x0029:
        if (r9 == 0) goto L_0x0053;
    L_0x002b:
        r0 = r16;
        r12 = r0.edgeFlags;
        r12 = r12 & 8;
        if (r12 != 0) goto L_0x0053;
    L_0x0033:
        r12 = 48;
        r13 = 57;
        r12 = com.klye.ime.latin.M.ir(r6, r12, r13);
        if (r12 != 0) goto L_0x004b;
    L_0x003d:
        r12 = 19;
        r13 = 22;
        r12 = com.klye.ime.latin.M.ir(r6, r12, r13);
        if (r12 != 0) goto L_0x004b;
    L_0x0047:
        r12 = 42;
        if (r6 != r12) goto L_0x0165;
    L_0x004b:
        r1 = 1;
    L_0x004c:
        if (r1 == 0) goto L_0x0168;
    L_0x004e:
        r12 = 0;
        r0 = r16;
        r0.width = r12;
    L_0x0053:
        r12 = com.klye.ime.latin.M.kid;
        r4 = r12.isBig();
        r12 = com.klye.ime.latin.M.sc();
        if (r12 != 0) goto L_0x01c6;
    L_0x005f:
        r12 = 6815863; // 0x680077 float:9.551058E-39 double:3.367484E-317;
        r13 = com.klye.ime.latin.M.mLC;
        if (r12 == r13) goto L_0x01c6;
    L_0x0066:
        r12 = com.klye.ime.latin.M.kid;
        r13 = 2131034123; // 0x7f05000b float:1.7678755E38 double:1.0528707503E-314;
        r12 = r12.is(r13);
        if (r12 != 0) goto L_0x007c;
    L_0x0071:
        r12 = com.klye.ime.latin.M.kid;
        r13 = 2131034124; // 0x7f05000c float:1.7678757E38 double:1.052870751E-314;
        r12 = r12.is(r13);
        if (r12 == 0) goto L_0x0086;
    L_0x007c:
        r0 = r16;
        r12 = r0.codes;
        r13 = 0;
        r12 = r12[r13];
        switch(r12) {
            case -5: goto L_0x01b5;
            case -1: goto L_0x01b5;
            case 98: goto L_0x01a4;
            case 106: goto L_0x01a4;
            case 107: goto L_0x01a4;
            case 109: goto L_0x01a4;
            case 113: goto L_0x01a4;
            case 118: goto L_0x01a4;
            case 119: goto L_0x01a4;
            case 120: goto L_0x01a4;
            case 122: goto L_0x01a4;
            case 228: goto L_0x019d;
            case 229: goto L_0x019d;
            case 246: goto L_0x019d;
            default: goto L_0x0086;
        };
    L_0x0086:
        r0 = r16;
        r7 = r0.label;
        if (r7 == 0) goto L_0x0164;
    L_0x008c:
        r2 = 0;
        r11 = com.klye.ime.latin.M.zhType();
        r12 = com.klye.ime.latin.M.arK2();
        if (r12 == 0) goto L_0x01f1;
    L_0x0097:
        r12 = "a";
        r12 = r7.equals(r12);
        if (r12 == 0) goto L_0x00ad;
    L_0x009f:
        r0 = r16;
        r12 = r0.width;
        r12 = r12 / 2;
        r0 = r16;
        r0.x = r12;
        r0 = r16;
        r0.gap = r12;
    L_0x00ad:
        r12 = "ö";
        r12 = r7.equals(r12);
        if (r12 == 0) goto L_0x00bd;
    L_0x00b5:
        r0 = r16;
        r12 = r0.width;
        r0 = r16;
        r0.gap = r12;
    L_0x00bd:
        r12 = com.klye.ime.latin.M.qwertz();
        if (r12 == 0) goto L_0x028c;
    L_0x00c3:
        r12 = "t  y";
        r12 = r7.equals(r12);
        if (r12 == 0) goto L_0x0253;
    L_0x00cb:
        r12 = "t  z";
        r0 = r16;
        r0.label = r12;
        r12 = "tz3";
        r0 = r16;
        r0.popupCharacters = r12;
    L_0x00d7:
        r0 = r16;
        r7 = r0.label;
        r12 = r7.length();
        r13 = 1;
        if (r12 != r13) goto L_0x0164;
    L_0x00e2:
        r0 = r16;
        r12 = r0.modifier;
        if (r12 != 0) goto L_0x0164;
    L_0x00e8:
        r8 = com.klye.ime.latin.M.mLC;
        r12 = 0;
        r3 = r7.charAt(r12);
        r12 = com.klye.ime.latin.M.tf();
        if (r12 == 0) goto L_0x0342;
    L_0x00f5:
        r2 = com.klye.ime.latin.M.tf(r3);
    L_0x00f9:
        r12 = com.klye.ime.latin.M.t9();
        if (r12 != 0) goto L_0x0104;
    L_0x00ff:
        r12 = com.klye.ime.latin.M.mLC;
        switch(r12) {
            case 6422629: goto L_0x08a6;
            case 7012473: goto L_0x08d6;
            case 7143531: goto L_0x091e;
            case 7602279: goto L_0x08ba;
            case 7602292: goto L_0x08ee;
            case 7667819: goto L_0x092b;
            case 7667834: goto L_0x090a;
            default: goto L_0x0104;
        };
    L_0x0104:
        if (r2 == 0) goto L_0x0130;
    L_0x0106:
        r12 = com.klye.ime.latin.M.cy();
        r13 = -1;
        if (r12 == r13) goto L_0x0121;
    L_0x010d:
        r12 = 6422631; // 0x620067 float:9.000023E-39 double:3.1732013E-317;
        r13 = com.klye.ime.latin.M.mLC;
        if (r12 == r13) goto L_0x0121;
    L_0x0114:
        r0 = r16;
        r12 = r0.label;
        r12 = r12.toString();
        r0 = r16;
        r15.alt(r0, r12);
    L_0x0121:
        r0 = r16;
        r12 = r0.codes;
        r13 = 0;
        r12[r13] = r2;
        r12 = java.lang.Character.toString(r2);
        r0 = r16;
        r0.label = r12;
    L_0x0130:
        r0 = r16;
        r7 = r0.label;
        r12 = com.klye.ime.latin.M.eth;
        if (r12 == 0) goto L_0x0938;
    L_0x0138:
        r12 = com.klye.ime.latin.M.qw_er();
        if (r12 != 0) goto L_0x0938;
    L_0x013e:
        r12 = 0;
        r12 = r7.charAt(r12);
        r12 = com.klye.ime.latin.M.ev(r12);
        if (r12 == 0) goto L_0x014e;
    L_0x0149:
        r12 = 1;
        r0 = r16;
        r0.modifier = r12;
    L_0x014e:
        r12 = r7.toString();
        r7 = r12.toUpperCase();
        r0 = r16;
        r12 = r0.label;
        if (r7 == r12) goto L_0x0164;
    L_0x015c:
        r12 = r7;
        r12 = (java.lang.String) r12;
        r0 = r16;
        r15.alt(r0, r12);
    L_0x0164:
        return;
    L_0x0165:
        r1 = 0;
        goto L_0x004c;
    L_0x0168:
        if (r9 == 0) goto L_0x018f;
    L_0x016a:
        r12 = com.klye.ime.latin.M.dm;
        r12 = r12.widthPixels;
        r12 = r12 / 12;
        r0 = r16;
        r0.width = r12;
        r0 = r16;
        r12 = r0.codes;
        r13 = 0;
        r12 = r12[r13];
        r13 = 97;
        if (r12 != r13) goto L_0x0053;
    L_0x017f:
        r0 = r16;
        r12 = r0.width;
        r12 = r12 / 2;
        r0 = r16;
        r0.x = r12;
        r0 = r16;
        r0.gap = r12;
        goto L_0x0053;
    L_0x018f:
        r0 = r16;
        r12 = r0.width;
        r12 = r12 * 15;
        r12 = r12 / 10;
        r0 = r16;
        r0.width = r12;
        goto L_0x0053;
    L_0x019d:
        r12 = 0;
        r0 = r16;
        r0.width = r12;
        goto L_0x0086;
    L_0x01a4:
        r0 = r16;
        r12 = r0.width;
        r0 = r16;
        r13 = r0.width;
        r13 = r13 / 10;
        r12 = r12 + r13;
        r0 = r16;
        r0.width = r12;
        goto L_0x0086;
    L_0x01b5:
        r0 = r16;
        r12 = r0.width;
        r0 = r16;
        r13 = r0.width;
        r13 = r13 / 2;
        r12 = r12 + r13;
        r0 = r16;
        r0.width = r12;
        goto L_0x0086;
    L_0x01c6:
        r12 = com.klye.ime.latin.M.kid;
        r13 = 2131034116; // 0x7f050004 float:1.767874E38 double:1.052870747E-314;
        r12 = r12.is(r13);
        if (r12 != 0) goto L_0x01dc;
    L_0x01d1:
        r12 = com.klye.ime.latin.M.kid;
        r13 = 2131034117; // 0x7f050005 float:1.7678742E38 double:1.0528707473E-314;
        r12 = r12.is(r13);
        if (r12 == 0) goto L_0x0086;
    L_0x01dc:
        r0 = r16;
        r12 = r0.codes;
        r13 = 0;
        r12 = r12[r13];
        switch(r12) {
            case 245: goto L_0x01e8;
            default: goto L_0x01e6;
        };
    L_0x01e6:
        goto L_0x0086;
    L_0x01e8:
        r12 = "'";
        r0 = r16;
        com.klye.ime.latin.M.md1(r0, r12);
        goto L_0x0086;
    L_0x01f1:
        r12 = com.klye.ime.latin.M.hasp();
        if (r12 == 0) goto L_0x00bd;
    L_0x01f7:
        r12 = com.klye.ime.latin.M.dvorak();
        if (r12 == 0) goto L_0x022b;
    L_0x01fd:
        r12 = "q";
        r12 = r7.equals(r12);
        if (r12 == 0) goto L_0x0219;
    L_0x0205:
        r12 = com.klye.ime.latin.M.sc();
        if (r12 != 0) goto L_0x0219;
    L_0x020b:
        r0 = r16;
        r12 = r0.width;
        r12 = r12 / 2;
        r0 = r16;
        r0.x = r12;
        r0 = r16;
        r0.gap = r12;
    L_0x0219:
        r12 = "z";
        r12 = r7.equals(r12);
        if (r12 == 0) goto L_0x00bd;
    L_0x0221:
        r0 = r16;
        r12 = r0.width;
        r0 = r16;
        r0.gap = r12;
        goto L_0x00bd;
    L_0x022b:
        r12 = "a";
        r12 = r7.equals(r12);
        if (r12 == 0) goto L_0x0241;
    L_0x0233:
        r0 = r16;
        r12 = r0.width;
        r12 = r12 / 2;
        r0 = r16;
        r0.x = r12;
        r0 = r16;
        r0.gap = r12;
    L_0x0241:
        r12 = "l";
        r12 = r7.equals(r12);
        if (r12 == 0) goto L_0x00bd;
    L_0x0249:
        r0 = r16;
        r12 = r0.width;
        r0 = r16;
        r0.gap = r12;
        goto L_0x00bd;
    L_0x0253:
        r12 = "z  x";
        r12 = r7.equals(r12);
        if (r12 == 0) goto L_0x026a;
    L_0x025b:
        r12 = "yx*";
        r0 = r16;
        r0.popupCharacters = r12;
        r12 = "y  x";
        r0 = r16;
        com.klye.ime.latin.M.md2(r0, r12);
        goto L_0x00d7;
    L_0x026a:
        r12 = "z";
        r12 = r7.equals(r12);
        if (r12 == 0) goto L_0x027b;
    L_0x0272:
        r12 = "y";
        r0 = r16;
        com.klye.ime.latin.M.md1(r0, r12);
        goto L_0x00d7;
    L_0x027b:
        r12 = "y";
        r12 = r7.equals(r12);
        if (r12 == 0) goto L_0x00d7;
    L_0x0283:
        r12 = "z";
        r0 = r16;
        com.klye.ime.latin.M.md1(r0, r12);
        goto L_0x00d7;
    L_0x028c:
        r12 = com.klye.ime.latin.M.azerty();
        if (r12 == 0) goto L_0x00d7;
    L_0x0292:
        r12 = "q  w";
        r12 = r7.equals(r12);
        if (r12 == 0) goto L_0x02a9;
    L_0x029a:
        r12 = "az1";
        r0 = r16;
        r0.popupCharacters = r12;
        r12 = "a  z";
        r0 = r16;
        com.klye.ime.latin.M.md2(r0, r12);
        goto L_0x00d7;
    L_0x02a9:
        r12 = "q";
        r12 = r7.equals(r12);
        if (r12 == 0) goto L_0x02ba;
    L_0x02b1:
        r12 = "a";
        r0 = r16;
        com.klye.ime.latin.M.md1(r0, r12);
        goto L_0x00d7;
    L_0x02ba:
        r12 = "w";
        r12 = r7.equals(r12);
        if (r12 == 0) goto L_0x02cb;
    L_0x02c2:
        r12 = "z";
        r0 = r16;
        com.klye.ime.latin.M.md1(r0, r12);
        goto L_0x00d7;
    L_0x02cb:
        r12 = "a  s";
        r12 = r7.equals(r12);
        if (r12 == 0) goto L_0x02e2;
    L_0x02d3:
        r12 = "qs6";
        r0 = r16;
        r0.popupCharacters = r12;
        r12 = "q  s";
        r0 = r16;
        com.klye.ime.latin.M.md2(r0, r12);
        goto L_0x00d7;
    L_0x02e2:
        r12 = "a";
        r12 = r7.equals(r12);
        if (r12 == 0) goto L_0x02f3;
    L_0x02ea:
        r12 = "q";
        r0 = r16;
        com.klye.ime.latin.M.md1(r0, r12);
        goto L_0x00d7;
    L_0x02f3:
        r12 = "z  x";
        r12 = r7.equals(r12);
        if (r12 == 0) goto L_0x030a;
    L_0x02fb:
        r12 = "wx'";
        r0 = r16;
        r0.popupCharacters = r12;
        r12 = "w  x";
        r0 = r16;
        com.klye.ime.latin.M.md2(r0, r12);
        goto L_0x00d7;
    L_0x030a:
        r12 = "z";
        r12 = r7.equals(r12);
        if (r12 == 0) goto L_0x031b;
    L_0x0312:
        r12 = "w";
        r0 = r16;
        com.klye.ime.latin.M.md1(r0, r12);
        goto L_0x00d7;
    L_0x031b:
        r12 = "ö'";
        r13 = r7.toString();
        r12 = r12.indexOf(r13);
        r13 = -1;
        if (r12 == r13) goto L_0x0331;
    L_0x0328:
        r12 = "m";
        r0 = r16;
        com.klye.ime.latin.M.md1(r0, r12);
        goto L_0x00d7;
    L_0x0331:
        r12 = "m";
        r12 = r7.equals(r12);
        if (r12 == 0) goto L_0x00d7;
    L_0x0339:
        r12 = "'";
        r0 = r16;
        com.klye.ime.latin.M.md1(r0, r12);
        goto L_0x00d7;
    L_0x0342:
        r12 = com.klye.ime.latin.M.ls1;
        if (r12 == 0) goto L_0x034c;
    L_0x0346:
        r2 = com.klye.ime.latin.M.ls(r3);
        goto L_0x00f9;
    L_0x034c:
        switch(r8) {
            case 6357091: goto L_0x0645;
            case 6357100: goto L_0x0495;
            case 6357101: goto L_0x067b;
            case 6357104: goto L_0x084a;
            case 6357106: goto L_0x065d;
            case 6357114: goto L_0x066f;
            case 6422578: goto L_0x06b3;
            case 6422626: goto L_0x05df;
            case 6422631: goto L_0x064b;
            case 6422638: goto L_0x0487;
            case 6422639: goto L_0x06dc;
            case 6422643: goto L_0x07e1;
            case 6422649: goto L_0x06be;
            case 6488168: goto L_0x0615;
            case 6488183: goto L_0x0627;
            case 6488185: goto L_0x05fd;
            case 6553697: goto L_0x0833;
            case 6553701: goto L_0x0495;
            case 6553710: goto L_0x0675;
            case 6553718: goto L_0x07b2;
            case 6553722: goto L_0x06dc;
            case 6619237: goto L_0x062d;
            case 6619247: goto L_0x0777;
            case 6619252: goto L_0x0479;
            case 6684769: goto L_0x0657;
            case 6684777: goto L_0x00f9;
            case 6684783: goto L_0x0457;
            case 6684786: goto L_0x05b6;
            case 6750330: goto L_0x067b;
            case 6815841: goto L_0x05e5;
            case 6815858: goto L_0x07e1;
            case 6815861: goto L_0x04ac;
            case 6815865: goto L_0x0669;
            case 6881383: goto L_0x0603;
            case 6881392: goto L_0x04d5;
            case 6881395: goto L_0x05f1;
            case 6881396: goto L_0x0527;
            case 6946913: goto L_0x0735;
            case 6946934: goto L_0x063f;
            case 6946935: goto L_0x0681;
            case 7012450: goto L_0x05df;
            case 7012463: goto L_0x06d4;
            case 7012469: goto L_0x0609;
            case 7077985: goto L_0x04fe;
            case 7078004: goto L_0x0651;
            case 7078006: goto L_0x0633;
            case 7143473: goto L_0x061b;
            case 7143521: goto L_0x065d;
            case 7143529: goto L_0x0550;
            case 7143533: goto L_0x068b;
            case 7143540: goto L_0x070c;
            case 7143542: goto L_0x0621;
            case 7143544: goto L_0x06a7;
            case 7143545: goto L_0x06a7;
            case 7143546: goto L_0x0657;
            case 7209058: goto L_0x045a;
            case 7209070: goto L_0x045a;
            case 7209078: goto L_0x084a;
            case 7471215: goto L_0x06e3;
            case 7536689: goto L_0x07e1;
            case 7536690: goto L_0x0657;
            case 7536692: goto L_0x05f7;
            case 7536745: goto L_0x0697;
            case 7536748: goto L_0x07b8;
            case 7536753: goto L_0x087b;
            case 7536757: goto L_0x05a8;
            case 7536758: goto L_0x00f9;
            case 7536760: goto L_0x055e;
            case 7602278: goto L_0x06c9;
            case 7602281: goto L_0x067b;
            case 7602283: goto L_0x060f;
            case 7602290: goto L_0x0663;
            case 7798885: goto L_0x080a;
            case 7798895: goto L_0x0639;
            case 7929967: goto L_0x05eb;
            default: goto L_0x034f;
        };
    L_0x034f:
        r12 = com.klye.ime.latin.M.zt;
        switch(r12) {
            case 80: goto L_0x03f6;
            case 89: goto L_0x0379;
            default: goto L_0x0354;
        };
    L_0x0354:
        r12 = com.klye.ime.latin.M.es();
        r13 = -1;
        if (r12 == r13) goto L_0x042e;
    L_0x035b:
        switch(r3) {
            case 39: goto L_0x0360;
            case 228: goto L_0x0425;
            case 229: goto L_0x0360;
            case 245: goto L_0x0413;
            case 246: goto L_0x041c;
            default: goto L_0x035e;
        };
    L_0x035e:
        goto L_0x00f9;
    L_0x0360:
        r12 = com.klye.ime.latin.M.mLC;
        r13 = 6619251; // 0x650073 float:9.275546E-39 double:3.2703445E-317;
        if (r12 != r13) goto L_0x0402;
    L_0x0367:
        r12 = com.klye.ime.latin.M.asp;
        if (r12 != 0) goto L_0x0402;
    L_0x036b:
        r12 = com.klye.ime.latin.M.ll;
        r13 = 1;
        if (r12 != r13) goto L_0x0402;
    L_0x0370:
        r12 = "ñ";
        r0 = r16;
        com.klye.ime.latin.M.md1(r0, r12);
        goto L_0x00f9;
    L_0x0379:
        switch(r3) {
            case 99: goto L_0x037d;
            case 106: goto L_0x03be;
            case 113: goto L_0x03d5;
            case 114: goto L_0x03ce;
            case 118: goto L_0x03e5;
            case 120: goto L_0x03dd;
            case 121: goto L_0x038a;
            case 122: goto L_0x0397;
            default: goto L_0x037c;
        };
    L_0x037c:
        goto L_0x0354;
    L_0x037d:
        r12 = com.klye.ime.latin.M.cjs;
        r13 = 71;
        if (r12 != r13) goto L_0x03b1;
    L_0x0383:
        r12 = "ts";
        r0 = r16;
        r0.label = r12;
        goto L_0x0354;
    L_0x038a:
        r12 = com.klye.ime.latin.M.cjs;
        r13 = 71;
        if (r12 == r13) goto L_0x0354;
    L_0x0390:
        r12 = "yu";
        r0 = r16;
        r0.label = r12;
        goto L_0x0354;
    L_0x0397:
        r12 = com.klye.ime.latin.M.cjs;
        r13 = 71;
        if (r12 != r13) goto L_0x03a4;
    L_0x039d:
        r12 = "dz";
        r0 = r16;
        r0.label = r12;
        goto L_0x0354;
    L_0x03a4:
        r12 = com.klye.ime.latin.M.cjs;
        r13 = 89;
        if (r12 != r13) goto L_0x0354;
    L_0x03aa:
        r12 = "j";
        r0 = r16;
        r0.label = r12;
        goto L_0x0354;
    L_0x03b1:
        r12 = com.klye.ime.latin.M.cjs;
        r13 = 89;
        if (r12 != r13) goto L_0x0354;
    L_0x03b7:
        r12 = "ch";
        r0 = r16;
        r0.label = r12;
        goto L_0x0354;
    L_0x03be:
        r12 = com.klye.ime.latin.M.cjs;
        r13 = 89;
        if (r12 != r13) goto L_0x0354;
    L_0x03c4:
        r0 = r16;
        r12 = r0.codes;
        r13 = 0;
        r14 = 122; // 0x7a float:1.71E-43 double:6.03E-322;
        r12[r13] = r14;
        goto L_0x0354;
    L_0x03ce:
        r12 = "ng";
        r0 = r16;
        r0.label = r12;
        goto L_0x0354;
    L_0x03d5:
        r12 = "kw";
        r0 = r16;
        r0.label = r12;
        goto L_0x0354;
    L_0x03dd:
        r12 = "gw";
        r0 = r16;
        r0.label = r12;
        goto L_0x0354;
    L_0x03e5:
        r12 = com.klye.ime.latin.M.cjs;
        r13 = 89;
        if (r12 != r13) goto L_0x03f3;
    L_0x03eb:
        r12 = "eu";
    L_0x03ed:
        r0 = r16;
        r0.label = r12;
        goto L_0x0354;
    L_0x03f3:
        r12 = "eo";
        goto L_0x03ed;
    L_0x03f6:
        r12 = 118; // 0x76 float:1.65E-43 double:5.83E-322;
        if (r3 != r12) goto L_0x0354;
    L_0x03fa:
        r12 = "ü";
        r0 = r16;
        r0.label = r12;
        goto L_0x0354;
    L_0x0402:
        r0 = r16;
        r12 = r0.codes;
        r13 = 0;
        r14 = 769; // 0x301 float:1.078E-42 double:3.8E-321;
        r12[r13] = r14;
        r12 = "~́`̣";
        r0 = r16;
        r0.label = r12;
        goto L_0x00f9;
    L_0x0413:
        r12 = "'";
        r0 = r16;
        com.klye.ime.latin.M.md1(r0, r12);
        goto L_0x00f9;
    L_0x041c:
        r12 = "ñ";
        r0 = r16;
        com.klye.ime.latin.M.md1(r0, r12);
        goto L_0x00f9;
    L_0x0425:
        r12 = "ç";
        r0 = r16;
        com.klye.ime.latin.M.md1(r0, r12);
        goto L_0x00f9;
    L_0x042e:
        switch(r3) {
            case 228: goto L_0x0433;
            case 229: goto L_0x043c;
            case 245: goto L_0x0445;
            case 246: goto L_0x044e;
            default: goto L_0x0431;
        };
    L_0x0431:
        goto L_0x00f9;
    L_0x0433:
        r12 = "+";
        r0 = r16;
        com.klye.ime.latin.M.md1(r0, r12);
        goto L_0x00f9;
    L_0x043c:
        r12 = "-";
        r0 = r16;
        com.klye.ime.latin.M.md1(r0, r12);
        goto L_0x00f9;
    L_0x0445:
        r12 = "'";
        r0 = r16;
        com.klye.ime.latin.M.md1(r0, r12);
        goto L_0x00f9;
    L_0x044e:
        r12 = "*";
        r0 = r16;
        com.klye.ime.latin.M.md1(r0, r12);
        goto L_0x00f9;
    L_0x0457:
        switch(r3) {
            case 39: goto L_0x0468;
            default: goto L_0x045a;
        };
    L_0x045a:
        switch(r3) {
            case 228: goto L_0x045f;
            case 246: goto L_0x0470;
            default: goto L_0x045d;
        };
    L_0x045d:
        goto L_0x00f9;
    L_0x045f:
        r12 = "æ";
        r0 = r16;
        com.klye.ime.latin.M.md1(r0, r12);
        goto L_0x00f9;
    L_0x0468:
        r12 = "ð";
        r0 = r16;
        com.klye.ime.latin.M.md1(r0, r12);
        goto L_0x045a;
    L_0x0470:
        r12 = "ø";
        r0 = r16;
        com.klye.ime.latin.M.md1(r0, r12);
        goto L_0x00f9;
    L_0x0479:
        switch(r3) {
            case 229: goto L_0x047e;
            default: goto L_0x047c;
        };
    L_0x047c:
        goto L_0x00f9;
    L_0x047e:
        r12 = "ü";
        r0 = r16;
        com.klye.ime.latin.M.md1(r0, r12);
        goto L_0x00f9;
    L_0x0487:
        switch(r3) {
            case 2544: goto L_0x048c;
            default: goto L_0x048a;
        };
    L_0x048a:
        goto L_0x00f9;
    L_0x048c:
        r12 = "র";
        r0 = r16;
        com.klye.ime.latin.M.md1(r0, r12);
        goto L_0x00f9;
    L_0x0495:
        switch(r3) {
            case 229: goto L_0x049a;
            case 245: goto L_0x04a3;
            default: goto L_0x0498;
        };
    L_0x0498:
        goto L_0x00f9;
    L_0x049a:
        r12 = "ü";
        r0 = r16;
        com.klye.ime.latin.M.md1(r0, r12);
        goto L_0x00f9;
    L_0x04a3:
        r12 = "ß";
        r0 = r16;
        com.klye.ime.latin.M.md1(r0, r12);
        goto L_0x00f9;
    L_0x04ac:
        switch(r3) {
            case 228: goto L_0x04b1;
            case 229: goto L_0x04ba;
            case 245: goto L_0x04c3;
            case 246: goto L_0x04cc;
            default: goto L_0x04af;
        };
    L_0x04af:
        goto L_0x00f9;
    L_0x04b1:
        r12 = "á";
        r0 = r16;
        com.klye.ime.latin.M.md1(r0, r12);
        goto L_0x00f9;
    L_0x04ba:
        r12 = "ó";
        r0 = r16;
        com.klye.ime.latin.M.md1(r0, r12);
        goto L_0x00f9;
    L_0x04c3:
        r12 = "ö";
        r0 = r16;
        com.klye.ime.latin.M.md1(r0, r12);
        goto L_0x00f9;
    L_0x04cc:
        r12 = "é";
        r0 = r16;
        com.klye.ime.latin.M.md1(r0, r12);
        goto L_0x00f9;
    L_0x04d5:
        switch(r3) {
            case 228: goto L_0x04da;
            case 229: goto L_0x04e3;
            case 245: goto L_0x04ec;
            case 246: goto L_0x04f5;
            default: goto L_0x04d8;
        };
    L_0x04d8:
        goto L_0x00f9;
    L_0x04da:
        r12 = "ˑ";
        r0 = r16;
        com.klye.ime.latin.M.md1(r0, r12);
        goto L_0x00f9;
    L_0x04e3:
        r12 = "|";
        r0 = r16;
        com.klye.ime.latin.M.md1(r0, r12);
        goto L_0x00f9;
    L_0x04ec:
        r12 = "‖";
        r0 = r16;
        com.klye.ime.latin.M.md1(r0, r12);
        goto L_0x00f9;
    L_0x04f5:
        r12 = "ː";
        r0 = r16;
        com.klye.ime.latin.M.md1(r0, r12);
        goto L_0x00f9;
    L_0x04fe:
        switch(r3) {
            case 228: goto L_0x0503;
            case 229: goto L_0x050c;
            case 245: goto L_0x0515;
            case 246: goto L_0x051e;
            default: goto L_0x0501;
        };
    L_0x0501:
        goto L_0x00f9;
    L_0x0503:
        r12 = "æ";
        r0 = r16;
        com.klye.ime.latin.M.md1(r0, r12);
        goto L_0x00f9;
    L_0x050c:
        r12 = "ā";
        r0 = r16;
        com.klye.ime.latin.M.md1(r0, r12);
        goto L_0x00f9;
    L_0x0515:
        r12 = "ē";
        r0 = r16;
        com.klye.ime.latin.M.md1(r0, r12);
        goto L_0x00f9;
    L_0x051e:
        r12 = "œ";
        r0 = r16;
        com.klye.ime.latin.M.md1(r0, r12);
        goto L_0x00f9;
    L_0x0527:
        switch(r3) {
            case 228: goto L_0x052c;
            case 229: goto L_0x0535;
            case 245: goto L_0x053e;
            case 246: goto L_0x0547;
            default: goto L_0x052a;
        };
    L_0x052a:
        goto L_0x00f9;
    L_0x052c:
        r12 = "'";
        r0 = r16;
        com.klye.ime.latin.M.md1(r0, r12);
        goto L_0x00f9;
    L_0x0535:
        r12 = "è";
        r0 = r16;
        com.klye.ime.latin.M.md1(r0, r12);
        goto L_0x00f9;
    L_0x053e:
        r12 = "à";
        r0 = r16;
        com.klye.ime.latin.M.md1(r0, r12);
        goto L_0x00f9;
    L_0x0547:
        r12 = "ò";
        r0 = r16;
        com.klye.ime.latin.M.md1(r0, r12);
        goto L_0x00f9;
    L_0x0550:
        switch(r3) {
            case 39: goto L_0x0555;
            default: goto L_0x0553;
        };
    L_0x0553:
        goto L_0x00f9;
    L_0x0555:
        r12 = "¯";
        r0 = r16;
        com.klye.ime.latin.M.md1(r0, r12);
        goto L_0x00f9;
    L_0x055e:
        switch(r3) {
            case 99: goto L_0x0563;
            case 100: goto L_0x0580;
            case 106: goto L_0x058b;
            case 113: goto L_0x056c;
            case 114: goto L_0x0575;
            case 120: goto L_0x0596;
            case 228: goto L_0x058d;
            case 229: goto L_0x0577;
            case 245: goto L_0x059f;
            case 246: goto L_0x0582;
            default: goto L_0x0561;
        };
    L_0x0561:
        goto L_0x00f9;
    L_0x0563:
        r12 = "č";
        r0 = r16;
        com.klye.ime.latin.M.md1(r0, r12);
        goto L_0x00f9;
    L_0x056c:
        r12 = "ǧ";
        r0 = r16;
        com.klye.ime.latin.M.md1(r0, r12);
        goto L_0x00f9;
    L_0x0575:
        if (r4 != 0) goto L_0x00f9;
    L_0x0577:
        r12 = "š";
        r0 = r16;
        com.klye.ime.latin.M.md1(r0, r12);
        goto L_0x00f9;
    L_0x0580:
        if (r4 != 0) goto L_0x00f9;
    L_0x0582:
        r12 = "ŋ";
        r0 = r16;
        com.klye.ime.latin.M.md1(r0, r12);
        goto L_0x00f9;
    L_0x058b:
        if (r4 != 0) goto L_0x00f9;
    L_0x058d:
        r12 = "ȟ";
        r0 = r16;
        com.klye.ime.latin.M.md1(r0, r12);
        goto L_0x00f9;
    L_0x0596:
        r12 = "ž";
        r0 = r16;
        com.klye.ime.latin.M.md1(r0, r12);
        goto L_0x00f9;
    L_0x059f:
        r12 = "'";
        r0 = r16;
        com.klye.ime.latin.M.md1(r0, r12);
        goto L_0x00f9;
    L_0x05a8:
        switch(r3) {
            case 39: goto L_0x05ad;
            default: goto L_0x05ab;
        };
    L_0x05ab:
        goto L_0x00f9;
    L_0x05ad:
        r12 = "é";
        r0 = r16;
        com.klye.ime.latin.M.md1(r0, r12);
        goto L_0x00f9;
    L_0x05b6:
        switch(r3) {
            case 228: goto L_0x05bb;
            case 229: goto L_0x05c4;
            case 245: goto L_0x05cd;
            case 246: goto L_0x05d6;
            default: goto L_0x05b9;
        };
    L_0x05b9:
        goto L_0x00f9;
    L_0x05bb:
        r12 = "é";
        r0 = r16;
        com.klye.ime.latin.M.md1(r0, r12);
        goto L_0x00f9;
    L_0x05c4:
        r12 = "'";
        r0 = r16;
        com.klye.ime.latin.M.md1(r0, r12);
        goto L_0x00f9;
    L_0x05cd:
        r12 = "à";
        r0 = r16;
        com.klye.ime.latin.M.md1(r0, r12);
        goto L_0x00f9;
    L_0x05d6:
        r12 = "è";
        r0 = r16;
        com.klye.ime.latin.M.md1(r0, r12);
        goto L_0x00f9;
    L_0x05df:
        r2 = com.klye.ime.latin.M.bb(r3);
        goto L_0x00f9;
    L_0x05e5:
        r2 = com.klye.ime.latin.M.ha(r3);
        goto L_0x00f9;
    L_0x05eb:
        r2 = com.klye.ime.latin.M.yo(r3);
        goto L_0x00f9;
    L_0x05f1:
        r2 = com.klye.ime.latin.M.is(r3);
        goto L_0x00f9;
    L_0x05f7:
        r2 = com.klye.ime.latin.M.s4(r3);
        goto L_0x00f9;
    L_0x05fd:
        r2 = com.klye.ime.latin.M.cy(r3);
        goto L_0x00f9;
    L_0x0603:
        r2 = com.klye.ime.latin.M.ig(r3);
        goto L_0x00f9;
    L_0x0609:
        r2 = com.klye.ime.latin.M.ku(r3);
        goto L_0x00f9;
    L_0x060f:
        r2 = com.klye.ime.latin.M.tk(r3);
        goto L_0x00f9;
    L_0x0615:
        r2 = com.klye.ime.latin.M.ch(r3);
        goto L_0x00f9;
    L_0x061b:
        r2 = com.klye.ime.latin.M.m1(r3);
        goto L_0x00f9;
    L_0x0621:
        r2 = com.klye.ime.latin.M.mv(r3);
        goto L_0x00f9;
    L_0x0627:
        r2 = com.klye.ime.latin.M.cw(r3);
        goto L_0x00f9;
    L_0x062d:
        r2 = com.klye.ime.latin.M.ee(r3);
        goto L_0x00f9;
    L_0x0633:
        r2 = com.klye.ime.latin.M.lv(r3);
        goto L_0x00f9;
    L_0x0639:
        r2 = com.klye.ime.latin.M.wo(r3);
        goto L_0x00f9;
    L_0x063f:
        r2 = com.klye.ime.latin.M.jv(r3);
        goto L_0x00f9;
    L_0x0645:
        r2 = com.klye.ime.latin.M.ac(r3);
        goto L_0x00f9;
    L_0x064b:
        r2 = com.klye.ime.latin.M.bg(r3);
        goto L_0x00f9;
    L_0x0651:
        r2 = com.klye.ime.latin.M.lt(r3);
        goto L_0x00f9;
    L_0x0657:
        r2 = com.klye.ime.latin.S.fa(r3);
        goto L_0x00f9;
    L_0x065d:
        r2 = com.klye.ime.latin.S.ar(r3);
        goto L_0x00f9;
    L_0x0663:
        r2 = com.klye.ime.latin.M.tr(r3);
        goto L_0x00f9;
    L_0x0669:
        r2 = com.klye.ime.latin.M.hy(r3);
        goto L_0x00f9;
    L_0x066f:
        r2 = com.klye.ime.latin.M.az(r3);
        goto L_0x00f9;
    L_0x0675:
        r2 = com.klye.ime.latin.M.dn(r3);
        goto L_0x00f9;
    L_0x067b:
        r2 = com.klye.ime.latin.M.eth(r3);
        goto L_0x00f9;
    L_0x0681:
        r12 = 5;
        r13 = 6;
        r0 = r16;
        r2 = com.klye.ime.latin.M.nm(r0, r3, r12, r13);
        goto L_0x00f9;
    L_0x068b:
        r12 = 11;
        r13 = 12;
        r0 = r16;
        r2 = com.klye.ime.latin.M.nm(r0, r3, r12, r13);
        goto L_0x00f9;
    L_0x0697:
        r12 = com.klye.ime.latin.M.nm;
        if (r12 == 0) goto L_0x00f9;
    L_0x069b:
        r12 = 19;
        r13 = 20;
        r0 = r16;
        r2 = com.klye.ime.latin.M.nm(r0, r3, r12, r13);
        goto L_0x00f9;
    L_0x06a7:
        r12 = 13;
        r13 = 14;
        r0 = r16;
        r2 = com.klye.ime.latin.M.nm(r0, r3, r12, r13);
        goto L_0x00f9;
    L_0x06b3:
        r12 = 15;
        r13 = -1;
        r0 = r16;
        r2 = com.klye.ime.latin.M.nm(r0, r3, r12, r13);
        goto L_0x00f9;
    L_0x06be:
        r12 = 16;
        r13 = -1;
        r0 = r16;
        r2 = com.klye.ime.latin.M.nm(r0, r3, r12, r13);
        goto L_0x00f9;
    L_0x06c9:
        r12 = 7;
        r13 = 8;
        r0 = r16;
        r2 = com.klye.ime.latin.M.nm(r0, r3, r12, r13);
        goto L_0x00f9;
    L_0x06d4:
        r12 = 21;
        r2 = com.klye.ime.latin.HKM.m(r12, r3);
        goto L_0x00f9;
    L_0x06dc:
        r12 = 4;
        r2 = com.klye.ime.latin.HKM.m(r12, r3);
        goto L_0x00f9;
    L_0x06e3:
        switch(r3) {
            case 228: goto L_0x06e8;
            case 229: goto L_0x06f1;
            case 245: goto L_0x06fa;
            case 246: goto L_0x0703;
            default: goto L_0x06e6;
        };
    L_0x06e6:
        goto L_0x00f9;
    L_0x06e8:
        r12 = com.klye.ime.latin.M.roT;
        r0 = r16;
        com.klye.ime.latin.M.md1(r0, r12);
        goto L_0x00f9;
    L_0x06f1:
        r12 = "ă";
        r0 = r16;
        com.klye.ime.latin.M.md1(r0, r12);
        goto L_0x00f9;
    L_0x06fa:
        r12 = "â";
        r0 = r16;
        com.klye.ime.latin.M.md1(r0, r12);
        goto L_0x00f9;
    L_0x0703:
        r12 = com.klye.ime.latin.M.roS;
        r0 = r16;
        com.klye.ime.latin.M.md1(r0, r12);
        goto L_0x00f9;
    L_0x070c:
        switch(r3) {
            case 228: goto L_0x0711;
            case 229: goto L_0x071a;
            case 245: goto L_0x0723;
            case 246: goto L_0x072c;
            default: goto L_0x070f;
        };
    L_0x070f:
        goto L_0x00f9;
    L_0x0711:
        r12 = "'";
        r0 = r16;
        com.klye.ime.latin.M.md1(r0, r12);
        goto L_0x00f9;
    L_0x071a:
        r12 = "¯";
        r0 = r16;
        com.klye.ime.latin.M.md1(r0, r12);
        goto L_0x00f9;
    L_0x0723:
        r12 = "´";
        r0 = r16;
        com.klye.ime.latin.M.md1(r0, r12);
        goto L_0x00f9;
    L_0x072c:
        r12 = "·";
        r0 = r16;
        com.klye.ime.latin.M.md1(r0, r12);
        goto L_0x00f9;
    L_0x0735:
        if (r4 == 0) goto L_0x0760;
    L_0x0737:
        switch(r3) {
            case 228: goto L_0x073c;
            case 229: goto L_0x0745;
            case 245: goto L_0x074e;
            case 246: goto L_0x0757;
            default: goto L_0x073a;
        };
    L_0x073a:
        goto L_0x00f9;
    L_0x073c:
        r12 = "ー";
        r0 = r16;
        com.klye.ime.latin.M.md1(r0, r12);
        goto L_0x00f9;
    L_0x0745:
        r12 = "、";
        r0 = r16;
        com.klye.ime.latin.M.md1(r0, r12);
        goto L_0x00f9;
    L_0x074e:
        r12 = "〜";
        r0 = r16;
        com.klye.ime.latin.M.md1(r0, r12);
        goto L_0x00f9;
    L_0x0757:
        r12 = "'";
        r0 = r16;
        com.klye.ime.latin.M.md1(r0, r12);
        goto L_0x00f9;
    L_0x0760:
        switch(r3) {
            case 108: goto L_0x0765;
            case 113: goto L_0x076e;
            default: goto L_0x0763;
        };
    L_0x0763:
        goto L_0x00f9;
    L_0x0765:
        r12 = "ー";
        r0 = r16;
        com.klye.ime.latin.M.md1(r0, r12);
        goto L_0x00f9;
    L_0x076e:
        r12 = "、";
        r0 = r16;
        com.klye.ime.latin.M.md1(r0, r12);
        goto L_0x00f9;
    L_0x0777:
        switch(r3) {
            case 113: goto L_0x077c;
            case 121: goto L_0x0785;
            case 228: goto L_0x07a9;
            case 229: goto L_0x078e;
            case 245: goto L_0x0797;
            case 246: goto L_0x07a0;
            default: goto L_0x077a;
        };
    L_0x077a:
        goto L_0x00f9;
    L_0x077c:
        r12 = "ŝ";
        r0 = r16;
        com.klye.ime.latin.M.md1(r0, r12);
        goto L_0x00f9;
    L_0x0785:
        r12 = "ŭ";
        r0 = r16;
        com.klye.ime.latin.M.md1(r0, r12);
        goto L_0x00f9;
    L_0x078e:
        r12 = "ĵ";
        r0 = r16;
        com.klye.ime.latin.M.md1(r0, r12);
        goto L_0x00f9;
    L_0x0797:
        r12 = "ĉ";
        r0 = r16;
        com.klye.ime.latin.M.md1(r0, r12);
        goto L_0x00f9;
    L_0x07a0:
        r12 = "ĝ";
        r0 = r16;
        com.klye.ime.latin.M.md1(r0, r12);
        goto L_0x00f9;
    L_0x07a9:
        r12 = "ĥ";
        r0 = r16;
        com.klye.ime.latin.M.md1(r0, r12);
        goto L_0x00f9;
    L_0x07b2:
        r2 = com.klye.ime.latin.S.dvqw(r3);
        goto L_0x00f9;
    L_0x07b8:
        switch(r3) {
            case 228: goto L_0x07bd;
            case 229: goto L_0x07c6;
            case 245: goto L_0x07cf;
            case 246: goto L_0x07d8;
            default: goto L_0x07bb;
        };
    L_0x07bb:
        goto L_0x00f9;
    L_0x07bd:
        r12 = "ž";
        r0 = r16;
        com.klye.ime.latin.M.md1(r0, r12);
        goto L_0x00f9;
    L_0x07c6:
        r12 = "š";
        r0 = r16;
        com.klye.ime.latin.M.md1(r0, r12);
        goto L_0x00f9;
    L_0x07cf:
        r12 = "'";
        r0 = r16;
        com.klye.ime.latin.M.md1(r0, r12);
        goto L_0x00f9;
    L_0x07d8:
        r12 = "č";
        r0 = r16;
        com.klye.ime.latin.M.md1(r0, r12);
        goto L_0x00f9;
    L_0x07e1:
        switch(r3) {
            case 228: goto L_0x07e6;
            case 229: goto L_0x07ef;
            case 245: goto L_0x07f8;
            case 246: goto L_0x0801;
            default: goto L_0x07e4;
        };
    L_0x07e4:
        goto L_0x00f9;
    L_0x07e6:
        r12 = "ć";
        r0 = r16;
        com.klye.ime.latin.M.md1(r0, r12);
        goto L_0x00f9;
    L_0x07ef:
        r12 = "š";
        r0 = r16;
        com.klye.ime.latin.M.md1(r0, r12);
        goto L_0x00f9;
    L_0x07f8:
        r12 = "đ";
        r0 = r16;
        com.klye.ime.latin.M.md1(r0, r12);
        goto L_0x00f9;
    L_0x0801:
        r12 = "č";
        r0 = r16;
        com.klye.ime.latin.M.md1(r0, r12);
        goto L_0x00f9;
    L_0x080a:
        switch(r3) {
            case 228: goto L_0x080f;
            case 229: goto L_0x0818;
            case 245: goto L_0x0821;
            case 246: goto L_0x082a;
            default: goto L_0x080d;
        };
    L_0x080d:
        goto L_0x00f9;
    L_0x080f:
        r12 = "ˊ";
        r0 = r16;
        com.klye.ime.latin.M.md1(r0, r12);
        goto L_0x00f9;
    L_0x0818:
        r12 = "ˇ";
        r0 = r16;
        com.klye.ime.latin.M.md1(r0, r12);
        goto L_0x00f9;
    L_0x0821:
        r12 = "č";
        r0 = r16;
        com.klye.ime.latin.M.md1(r0, r12);
        goto L_0x00f9;
    L_0x082a:
        r12 = "ł";
        r0 = r16;
        com.klye.ime.latin.M.md1(r0, r12);
        goto L_0x00f9;
    L_0x0833:
        switch(r3) {
            case 228: goto L_0x0838;
            case 246: goto L_0x0841;
            default: goto L_0x0836;
        };
    L_0x0836:
        goto L_0x00f9;
    L_0x0838:
        r12 = "ø";
        r0 = r16;
        com.klye.ime.latin.M.md1(r0, r12);
        goto L_0x00f9;
    L_0x0841:
        r12 = "æ";
        r0 = r16;
        com.klye.ime.latin.M.md1(r0, r12);
        goto L_0x00f9;
    L_0x084a:
        if (r4 == 0) goto L_0x0852;
    L_0x084c:
        r2 = com.klye.ime.latin.M.cw(r3);
        goto L_0x00f9;
    L_0x0852:
        switch(r3) {
            case 112: goto L_0x0857;
            case 113: goto L_0x0860;
            case 114: goto L_0x0872;
            case 115: goto L_0x0855;
            case 116: goto L_0x0855;
            case 117: goto L_0x0855;
            case 118: goto L_0x0869;
            default: goto L_0x0855;
        };
    L_0x0855:
        goto L_0x00f9;
    L_0x0857:
        r12 = "ł";
        r0 = r16;
        com.klye.ime.latin.M.md1(r0, r12);
        goto L_0x00f9;
    L_0x0860:
        r12 = "́";
        r0 = r16;
        com.klye.ime.latin.M.md1(r0, r12);
        goto L_0x00f9;
    L_0x0869:
        r12 = "ʼ";
        r0 = r16;
        com.klye.ime.latin.M.md1(r0, r12);
        goto L_0x00f9;
    L_0x0872:
        r12 = "˛";
        r0 = r16;
        com.klye.ime.latin.M.md1(r0, r12);
        goto L_0x00f9;
    L_0x087b:
        switch(r3) {
            case 119: goto L_0x0880;
            case 229: goto L_0x088b;
            case 245: goto L_0x089d;
            case 246: goto L_0x0894;
            default: goto L_0x087e;
        };
    L_0x087e:
        goto L_0x00f9;
    L_0x0880:
        if (r4 != 0) goto L_0x00f9;
    L_0x0882:
        r12 = "ç";
        r0 = r16;
        com.klye.ime.latin.M.md1(r0, r12);
        goto L_0x00f9;
    L_0x088b:
        r12 = "ç";
        r0 = r16;
        com.klye.ime.latin.M.md1(r0, r12);
        goto L_0x00f9;
    L_0x0894:
        r12 = "ë";
        r0 = r16;
        com.klye.ime.latin.M.md1(r0, r12);
        goto L_0x00f9;
    L_0x089d:
        r12 = "đ";
        r0 = r16;
        com.klye.ime.latin.M.md1(r0, r12);
        goto L_0x00f9;
    L_0x08a6:
        r0 = r16;
        r12 = r0.codes;
        r13 = 0;
        r12 = r12[r13];
        switch(r12) {
            case 1080: goto L_0x08b2;
            case 1097: goto L_0x08b6;
            default: goto L_0x08b0;
        };
    L_0x08b0:
        goto L_0x0104;
    L_0x08b2:
        r2 = 1110; // 0x456 float:1.555E-42 double:5.484E-321;
        goto L_0x0104;
    L_0x08b6:
        r2 = 1118; // 0x45e float:1.567E-42 double:5.524E-321;
        goto L_0x0104;
    L_0x08ba:
        r0 = r16;
        r12 = r0.codes;
        r13 = 0;
        r12 = r12[r13];
        switch(r12) {
            case 1094: goto L_0x08c6;
            case 1095: goto L_0x08c4;
            case 1096: goto L_0x08c4;
            case 1097: goto L_0x08ca;
            case 1098: goto L_0x08c4;
            case 1099: goto L_0x08ce;
            case 1100: goto L_0x08d2;
            default: goto L_0x08c4;
        };
    L_0x08c4:
        goto L_0x0104;
    L_0x08c6:
        r2 = 1179; // 0x49b float:1.652E-42 double:5.825E-321;
        goto L_0x0104;
    L_0x08ca:
        r2 = 1203; // 0x4b3 float:1.686E-42 double:5.944E-321;
        goto L_0x0104;
    L_0x08ce:
        r2 = 1207; // 0x4b7 float:1.691E-42 double:5.963E-321;
        goto L_0x0104;
    L_0x08d2:
        r2 = 1251; // 0x4e3 float:1.753E-42 double:6.18E-321;
        goto L_0x0104;
    L_0x08d6:
        r0 = r16;
        r12 = r0.codes;
        r13 = 0;
        r12 = r12[r13];
        switch(r12) {
            case 1094: goto L_0x08e2;
            case 1097: goto L_0x08e6;
            case 1100: goto L_0x08ea;
            default: goto L_0x08e0;
        };
    L_0x08e0:
        goto L_0x0104;
    L_0x08e2:
        r2 = 1199; // 0x4af float:1.68E-42 double:5.924E-321;
        goto L_0x0104;
    L_0x08e6:
        r2 = 1187; // 0x4a3 float:1.663E-42 double:5.865E-321;
        goto L_0x0104;
    L_0x08ea:
        r2 = 1257; // 0x4e9 float:1.761E-42 double:6.21E-321;
        goto L_0x0104;
    L_0x08ee:
        r0 = r16;
        r12 = r0.codes;
        r13 = 0;
        r12 = r12[r13];
        switch(r12) {
            case 1078: goto L_0x08fa;
            case 1094: goto L_0x08fe;
            case 1097: goto L_0x0902;
            case 1100: goto L_0x0906;
            default: goto L_0x08f8;
        };
    L_0x08f8:
        goto L_0x0104;
    L_0x08fa:
        r2 = 1187; // 0x4a3 float:1.663E-42 double:5.865E-321;
        goto L_0x0104;
    L_0x08fe:
        r2 = 1257; // 0x4e9 float:1.761E-42 double:6.21E-321;
        goto L_0x0104;
    L_0x0902:
        r2 = 1241; // 0x4d9 float:1.739E-42 double:6.13E-321;
        goto L_0x0104;
    L_0x0906:
        r2 = 1175; // 0x497 float:1.647E-42 double:5.805E-321;
        goto L_0x0104;
    L_0x090a:
        r0 = r16;
        r12 = r0.codes;
        r13 = 0;
        r12 = r12[r13];
        switch(r12) {
            case 1097: goto L_0x0916;
            case 1098: goto L_0x0914;
            case 1099: goto L_0x091a;
            default: goto L_0x0914;
        };
    L_0x0914:
        goto L_0x0104;
    L_0x0916:
        r2 = 1118; // 0x45e float:1.567E-42 double:5.524E-321;
        goto L_0x0104;
    L_0x091a:
        r2 = 1179; // 0x49b float:1.652E-42 double:5.825E-321;
        goto L_0x0104;
    L_0x091e:
        r0 = r16;
        r12 = r0.codes;
        r13 = 0;
        r12 = r12[r13];
        r2 = com.klye.ime.latin.M.mk(r12);
        goto L_0x0104;
    L_0x092b:
        r0 = r16;
        r12 = r0.codes;
        r13 = 0;
        r12 = r12[r13];
        r2 = com.klye.ime.latin.M.uk(r12);
        goto L_0x0104;
    L_0x0938:
        r12 = 6357106; // 0x610072 float:8.908203E-39 double:3.1408277E-317;
        r13 = com.klye.ime.latin.M.mLC;
        if (r12 == r13) goto L_0x0946;
    L_0x093f:
        r12 = 6684769; // 0x660061 float:9.367357E-39 double:3.3027147E-317;
        r13 = com.klye.ime.latin.M.mLC;
        if (r12 != r13) goto L_0x0958;
    L_0x0946:
        r12 = 0;
        r12 = r7.charAt(r12);
        r10 = com.klye.ime.latin.M.aralt(r12);
        if (r10 == 0) goto L_0x0164;
    L_0x0951:
        r0 = r16;
        r15.alt(r0, r10);
        goto L_0x0164;
    L_0x0958:
        r12 = com.klye.ime.latin.M.acl;
        if (r12 == 0) goto L_0x0164;
    L_0x095c:
        r12 = -1;
        if (r11 != r12) goto L_0x0164;
    L_0x095f:
        r12 = r7.length();
        r13 = 1;
        if (r12 != r13) goto L_0x0164;
    L_0x0966:
        r12 = com.klye.ime.latin.M.mLC;
        r13 = 6815865; // 0x680079 float:9.551061E-39 double:3.3674847E-317;
        if (r12 != r13) goto L_0x0971;
    L_0x096d:
        r12 = com.klye.ime.latin.M.klhy;
        if (r12 == 0) goto L_0x0164;
    L_0x0971:
        r12 = 0;
        r12 = r7.charAt(r12);
        switch(r12) {
            case 97: goto L_0x097b;
            case 98: goto L_0x0ace;
            case 99: goto L_0x09f3;
            case 100: goto L_0x0a21;
            case 101: goto L_0x0984;
            case 102: goto L_0x0a8e;
            case 103: goto L_0x0a7c;
            case 104: goto L_0x0a33;
            case 105: goto L_0x09a0;
            case 106: goto L_0x0a3c;
            case 107: goto L_0x0a45;
            case 108: goto L_0x0a60;
            case 109: goto L_0x0a97;
            case 110: goto L_0x09d7;
            case 111: goto L_0x09a9;
            case 112: goto L_0x0a85;
            case 113: goto L_0x0abc;
            case 114: goto L_0x0a2a;
            case 115: goto L_0x09bb;
            case 116: goto L_0x0a4e;
            case 117: goto L_0x09b2;
            case 118: goto L_0x0ac5;
            case 119: goto L_0x0a18;
            case 120: goto L_0x0ab3;
            case 121: goto L_0x0a0f;
            case 122: goto L_0x0a57;
            case 228: goto L_0x0b07;
            case 230: goto L_0x0ad7;
            case 246: goto L_0x0af7;
            case 248: goto L_0x0ae7;
            case 1072: goto L_0x0b4d;
            case 1074: goto L_0x0ba4;
            case 1075: goto L_0x0b5f;
            case 1077: goto L_0x0b44;
            case 1080: goto L_0x0b80;
            case 1082: goto L_0x0b20;
            case 1085: goto L_0x0b32;
            case 1086: goto L_0x0b3b;
            case 1091: goto L_0x0b17;
            case 1093: goto L_0x0b29;
            case 1095: goto L_0x0b89;
            case 1096: goto L_0x0bc6;
            case 1097: goto L_0x0bcf;
            case 1099: goto L_0x0bb4;
            case 1100: goto L_0x0b9b;
            case 1101: goto L_0x0b56;
            case 1110: goto L_0x0bbd;
            case 1175: goto L_0x0b92;
            case 1394: goto L_0x0bfc;
            case 1397: goto L_0x0bf3;
            case 1407: goto L_0x0c0e;
            case 1408: goto L_0x0bea;
            case 1410: goto L_0x0c05;
            case 1413: goto L_0x0be1;
            case 1414: goto L_0x0bd8;
            case 1580: goto L_0x0c72;
            case 1608: goto L_0x0c32;
            case 1705: goto L_0x0c5b;
            case 1711: goto L_0x0c52;
            case 1729: goto L_0x0c82;
            case 1740: goto L_0x0c42;
            case 2606: goto L_0x0c20;
            case 2622: goto L_0x0c17;
            case 2625: goto L_0x0c29;
            default: goto L_0x0979;
        };
    L_0x0979:
        goto L_0x0164;
    L_0x097b:
        r12 = com.klye.ime.latin.M.xa;
        r0 = r16;
        r15.alt(r0, r12);
        goto L_0x0164;
    L_0x0984:
        r12 = new java.lang.StringBuilder;
        r12.<init>();
        r13 = com.klye.ime.latin.M.xe;
        r12 = r12.append(r13);
        r13 = "€";
        r12 = r12.append(r13);
        r12 = r12.toString();
        r0 = r16;
        r15.alt(r0, r12);
        goto L_0x0164;
    L_0x09a0:
        r12 = com.klye.ime.latin.M.xi;
        r0 = r16;
        r15.alt(r0, r12);
        goto L_0x0164;
    L_0x09a9:
        r12 = com.klye.ime.latin.M.xo;
        r0 = r16;
        r15.alt(r0, r12);
        goto L_0x0164;
    L_0x09b2:
        r12 = com.klye.ime.latin.M.xu;
        r0 = r16;
        r15.alt(r0, r12);
        goto L_0x0164;
    L_0x09bb:
        r12 = new java.lang.StringBuilder;
        r12.<init>();
        r13 = com.klye.ime.latin.M.xs;
        r12 = r12.append(r13);
        r13 = "ſß§";
        r12 = r12.append(r13);
        r12 = r12.toString();
        r0 = r16;
        r15.alt(r0, r12);
        goto L_0x0164;
    L_0x09d7:
        r12 = new java.lang.StringBuilder;
        r12.<init>();
        r13 = "№";
        r12 = r12.append(r13);
        r13 = com.klye.ime.latin.M.xn;
        r12 = r12.append(r13);
        r12 = r12.toString();
        r0 = r16;
        r15.alt(r0, r12);
        goto L_0x0164;
    L_0x09f3:
        r12 = new java.lang.StringBuilder;
        r12.<init>();
        r13 = com.klye.ime.latin.M.xc;
        r12 = r12.append(r13);
        r13 = "¢℃";
        r12 = r12.append(r13);
        r12 = r12.toString();
        r0 = r16;
        r15.alt(r0, r12);
        goto L_0x0164;
    L_0x0a0f:
        r12 = com.klye.ime.latin.M.xy;
        r0 = r16;
        r15.alt(r0, r12);
        goto L_0x0164;
    L_0x0a18:
        r12 = com.klye.ime.latin.M.xw;
        r0 = r16;
        r15.alt(r0, r12);
        goto L_0x0164;
    L_0x0a21:
        r12 = com.klye.ime.latin.M.xd;
        r0 = r16;
        r15.alt(r0, r12);
        goto L_0x0164;
    L_0x0a2a:
        r12 = com.klye.ime.latin.M.xr;
        r0 = r16;
        r15.alt(r0, r12);
        goto L_0x0164;
    L_0x0a33:
        r12 = com.klye.ime.latin.M.xh;
        r0 = r16;
        r15.alt(r0, r12);
        goto L_0x0164;
    L_0x0a3c:
        r12 = com.klye.ime.latin.M.xj;
        r0 = r16;
        r15.alt(r0, r12);
        goto L_0x0164;
    L_0x0a45:
        r12 = com.klye.ime.latin.M.xk;
        r0 = r16;
        r15.alt(r0, r12);
        goto L_0x0164;
    L_0x0a4e:
        r12 = com.klye.ime.latin.M.xt;
        r0 = r16;
        r15.alt(r0, r12);
        goto L_0x0164;
    L_0x0a57:
        r12 = com.klye.ime.latin.M.xz;
        r0 = r16;
        r15.alt(r0, r12);
        goto L_0x0164;
    L_0x0a60:
        r12 = new java.lang.StringBuilder;
        r12.<init>();
        r13 = "£";
        r12 = r12.append(r13);
        r13 = com.klye.ime.latin.M.xl;
        r12 = r12.append(r13);
        r12 = r12.toString();
        r0 = r16;
        r15.alt(r0, r12);
        goto L_0x0164;
    L_0x0a7c:
        r12 = com.klye.ime.latin.M.xg;
        r0 = r16;
        r15.alt(r0, r12);
        goto L_0x0164;
    L_0x0a85:
        r12 = com.klye.ime.latin.M.xp;
        r0 = r16;
        r15.alt(r0, r12);
        goto L_0x0164;
    L_0x0a8e:
        r12 = "℉";
        r0 = r16;
        r15.alt(r0, r12);
        goto L_0x0164;
    L_0x0a97:
        r12 = new java.lang.StringBuilder;
        r12.<init>();
        r13 = "μ";
        r12 = r12.append(r13);
        r13 = com.klye.ime.latin.M.xm;
        r12 = r12.append(r13);
        r12 = r12.toString();
        r0 = r16;
        r15.alt(r0, r12);
        goto L_0x0164;
    L_0x0ab3:
        r12 = com.klye.ime.latin.M.xx;
        r0 = r16;
        r15.alt(r0, r12);
        goto L_0x0164;
    L_0x0abc:
        r12 = com.klye.ime.latin.M.xq;
        r0 = r16;
        r15.alt(r0, r12);
        goto L_0x0164;
    L_0x0ac5:
        r12 = com.klye.ime.latin.M.xv;
        r0 = r16;
        r15.alt(r0, r12);
        goto L_0x0164;
    L_0x0ace:
        r12 = com.klye.ime.latin.M.xb;
        r0 = r16;
        r15.alt(r0, r12);
        goto L_0x0164;
    L_0x0ad7:
        r12 = 2131296571; // 0x7f09013b float:1.8211062E38 double:1.053000417E-314;
        r0 = r17;
        r12 = r0.getString(r12);
        r0 = r16;
        r15.alt(r0, r12);
        goto L_0x0164;
    L_0x0ae7:
        r12 = 2131296573; // 0x7f09013d float:1.8211066E38 double:1.053000418E-314;
        r0 = r17;
        r12 = r0.getString(r12);
        r0 = r16;
        r15.alt(r0, r12);
        goto L_0x0164;
    L_0x0af7:
        r12 = 2131296572; // 0x7f09013c float:1.8211064E38 double:1.0530004173E-314;
        r0 = r17;
        r12 = r0.getString(r12);
        r0 = r16;
        r15.alt(r0, r12);
        goto L_0x0164;
    L_0x0b07:
        r12 = 2131296570; // 0x7f09013a float:1.821106E38 double:1.0530004163E-314;
        r0 = r17;
        r12 = r0.getString(r12);
        r0 = r16;
        r15.alt(r0, r12);
        goto L_0x0164;
    L_0x0b17:
        r12 = "ӯў";
        r0 = r16;
        r15.alt(r0, r12);
        goto L_0x0164;
    L_0x0b20:
        r12 = "қ";
        r0 = r16;
        r15.alt(r0, r12);
        goto L_0x0164;
    L_0x0b29:
        r12 = "ҳ";
        r0 = r16;
        r15.alt(r0, r12);
        goto L_0x0164;
    L_0x0b32:
        r12 = "ң";
        r0 = r16;
        r15.alt(r0, r12);
        goto L_0x0164;
    L_0x0b3b:
        r12 = "ө";
        r0 = r16;
        r15.alt(r0, r12);
        goto L_0x0164;
    L_0x0b44:
        r12 = "ёә";
        r0 = r16;
        r15.alt(r0, r12);
        goto L_0x0164;
    L_0x0b4d:
        r12 = "-";
        r0 = r16;
        r15.alt(r0, r12);
        goto L_0x0164;
    L_0x0b56:
        r12 = "є";
        r0 = r16;
        r15.alt(r0, r12);
        goto L_0x0164;
    L_0x0b5f:
        r12 = com.klye.ime.latin.M.mLC;
        r13 = 7602292; // 0x740074 float:1.065308E-38 double:3.7560313E-317;
        if (r12 == r13) goto L_0x0b74;
    L_0x0b66:
        r12 = com.klye.ime.latin.M.mLC;
        r13 = 7012459; // 0x6b006b float:9.826548E-39 double:3.464615E-317;
        if (r12 == r13) goto L_0x0b74;
    L_0x0b6d:
        r12 = com.klye.ime.latin.M.mLC;
        r13 = 7602279; // 0x740067 float:1.0653062E-38 double:3.756025E-317;
        if (r12 != r13) goto L_0x0b7d;
    L_0x0b74:
        r12 = "ғ";
    L_0x0b76:
        r0 = r16;
        r15.alt(r0, r12);
        goto L_0x0164;
    L_0x0b7d:
        r12 = "ғґ";
        goto L_0x0b76;
    L_0x0b80:
        r12 = "үұ";
        r0 = r16;
        r15.alt(r0, r12);
        goto L_0x0164;
    L_0x0b89:
        r12 = "һ";
        r0 = r16;
        r15.alt(r0, r12);
        goto L_0x0164;
    L_0x0b92:
        r12 = "ъь";
        r0 = r16;
        r15.alt1(r0, r12);
        goto L_0x0164;
    L_0x0b9b:
        r12 = "ъ";
        r0 = r16;
        r15.alt1(r0, r12);
        goto L_0x0164;
    L_0x0ba4:
        r12 = com.klye.ime.latin.M.mLC;
        r13 = 7602292; // 0x740074 float:1.065308E-38 double:3.7560313E-317;
        if (r12 != r13) goto L_0x0164;
    L_0x0bab:
        r12 = "w";
        r0 = r16;
        r15.alt(r0, r12);
        goto L_0x0164;
    L_0x0bb4:
        r12 = "їі";
        r0 = r16;
        r15.alt(r0, r12);
        goto L_0x0164;
    L_0x0bbd:
        r12 = "їы";
        r0 = r16;
        r15.alt(r0, r12);
        goto L_0x0164;
    L_0x0bc6:
        r12 = "щү";
        r0 = r16;
        r15.alt(r0, r12);
        goto L_0x0164;
    L_0x0bcf:
        r12 = "ұ";
        r0 = r16;
        r15.alt(r0, r12);
        goto L_0x0164;
    L_0x0bd8:
        r12 = "հ";
        r0 = r16;
        r15.alt(r0, r12);
        goto L_0x0164;
    L_0x0be1:
        r12 = "ո";
        r0 = r16;
        r15.alt(r0, r12);
        goto L_0x0164;
    L_0x0bea:
        r12 = "ռ";
        r0 = r16;
        r15.alt(r0, r12);
        goto L_0x0164;
    L_0x0bf3:
        r12 = "ձ";
        r0 = r16;
        r15.alt(r0, r12);
        goto L_0x0164;
    L_0x0bfc:
        r12 = "ջ";
        r0 = r16;
        r15.alt(r0, r12);
        goto L_0x0164;
    L_0x0c05:
        r12 = "-";
        r0 = r16;
        r15.alt(r0, r12);
        goto L_0x0164;
    L_0x0c0e:
        r12 = "․";
        r0 = r16;
        r15.alt(r0, r12);
        goto L_0x0164;
    L_0x0c17:
        r12 = "਼";
        r0 = r16;
        r15.alt(r0, r12);
        goto L_0x0164;
    L_0x0c20:
        r12 = "ਁ";
        r0 = r16;
        r15.alt(r0, r12);
        goto L_0x0164;
    L_0x0c29:
        r12 = "ੑ";
        r0 = r16;
        r15.alt(r0, r12);
        goto L_0x0164;
    L_0x0c32:
        r12 = 7340142; // 0x70006e float:1.028573E-38 double:3.626512E-317;
        r13 = com.klye.ime.latin.M.mLC;
        if (r12 != r13) goto L_0x0164;
    L_0x0c39:
        r12 = "ۋ";
        r0 = r16;
        r15.alt(r0, r12);
        goto L_0x0164;
    L_0x0c42:
        r12 = 7340142; // 0x70006e float:1.028573E-38 double:3.626512E-317;
        r13 = com.klye.ime.latin.M.mLC;
        if (r12 != r13) goto L_0x0164;
    L_0x0c49:
        r12 = "يې";
        r0 = r16;
        r15.alt(r0, r12);
        goto L_0x0164;
    L_0x0c52:
        r12 = "ڭ";
        r0 = r16;
        r15.alt(r0, r12);
        goto L_0x0164;
    L_0x0c5b:
        r12 = 7340142; // 0x70006e float:1.028573E-38 double:3.626512E-317;
        r13 = com.klye.ime.latin.M.mLC;
        if (r12 == r13) goto L_0x0c69;
    L_0x0c62:
        r12 = 7536740; // 0x730064 float:1.0561222E-38 double:3.7236443E-317;
        r13 = com.klye.ime.latin.M.mLC;
        if (r12 != r13) goto L_0x0164;
    L_0x0c69:
        r12 = "ڪ";
        r0 = r16;
        r15.alt(r0, r12);
        goto L_0x0164;
    L_0x0c72:
        r12 = 7536740; // 0x730064 float:1.0561222E-38 double:3.7236443E-317;
        r13 = com.klye.ime.latin.M.mLC;
        if (r12 != r13) goto L_0x0164;
    L_0x0c79:
        r12 = "ڃ";
        r0 = r16;
        r15.alt(r0, r12);
        goto L_0x0164;
    L_0x0c82:
        r12 = "ۂۃه";
        r0 = r16;
        r15.alt(r0, r12);
        goto L_0x0164;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.klye.ime.latin.LatinKeyboard.pk(android.inputmethodservice.Keyboard$Key, android.content.res.Resources):void");
    }

    private void update123Key() {
        if (this.m123Key != null && M.isAK()) {
            Key key;
            if (!this.mVoiceEnabled || (this.mF1Key != null && this.mF1Key.label == null)) {
                key = this.m123Key;
                this.m123Key.iconPreview = null;
                key.icon = null;
                this.m123Key.label = this.m123Label;
                return;
            }
            key = this.m123Key;
            Key key2 = this.m123Key;
            Drawable drawable = this.m123MicIcon;
            key2.icon = drawable;
            key.iconPreview = drawable;
            this.m123Key.label = null;
        }
    }

    private void updateF1Key() {
        if (this.mF1Key != null) {
            String s1 = ",";
            String s2 = "\u0001«―»\u0002✘∴※∵✔@€§%*,…$~★";
            snm(",", "\u0001«―»\u0002✘∴※∵✔@€§%*,…$~★");
            boolean ib = M.kid.isBig();
            if (M.isAK() || M.kid.is(R.xml.kbd_edit)) {
                switch (M.mLC) {
                    case M.ag /*6357095*/:
                    case M.el /*6619244*/:
                    case M.pc /*7340131*/:
                        snm(",ΐ", "ʹϛϝϞϠϗϣϥϧϑϒϩϖ·ύόέάήώ,ϊΐί★");
                        break;
                    case M.am /*6357101*/:
                    case M.ti /*7602281*/:
                        snm("፣", "፩፪፫፬፭፮፯፰፱፲፳፴፵፶፷፸፹፺፻፼«―»፝፞፟፠፡።፤፣፥፦፧፨");
                        break;
                    case M.az /*6357114*/:
                    case M.tr /*7602290*/:
                        if (!(M.fk() && (M.ll == 0 || M.ll == 8 || M.ll == 12))) {
                            snm(M.tsc, "€«―»§,şəæßıçğöü");
                            break;
                        }
                    case M.bb /*6422626*/:
                        if (M.ll == 1) {
                            snm(",ɣ", "ɛčḍǧɣḥṛṣṭẓ,€§%★");
                            break;
                        }
                        break;
                    case M.bo /*6422639*/:
                    case M.dz /*6553722*/:
                        snm("་", "༡༢༣༤༥༦༧༨༩༠ཾྃ༼༽༈༄༅ཿ༔༸་ྈ྾༏༑");
                        break;
                    case M.bs /*6422643*/:
                    case M.hr /*6815858*/:
                    case M.sr1 /*7536689*/:
                    case M.sl /*7536748*/:
                        snm(ib ? ",ž" : ",đ", "čćđšž,");
                        break;
                    case M.ck /*6488171*/:
                        snm(",ᏢᎴ", ",ᏢᎴ");
                        break;
                    case M.eo /*6619247*/:
                        snm(M.fk() ? ",q" : ",ŝ", M.fk() ? "ĉĝĥĵ€,qy~@" : "ĉĝĥĵ€,ŝŭ~@");
                        break;
                    case M.hu /*6815861*/:
                        snm(",ö", "áéóöő,úüű");
                        break;
                    case M.hy /*6815865*/:
                        snm(",։", "ՙ՚՛՜՝՞։«»€,§%$★");
                        break;
                    case M.ig /*6881383*/:
                        snm(",ị", "€´`  ,ịọṅụ");
                        break;
                    case M.ip /*6881392*/:
                        snm("ǁ", "ˈˌːˑ̆͜͡‿̈ʔʕʢʡ˥˦˧˨˩˩˥˥˩˦˥˩˨˧˦˧↓↑↗↘ʘǀǃǂǁ˞");
                        break;
                    case M.is /*6881395*/:
                        snm(",ö", "€§µæýáéíóú,öþð^");
                        break;
                    case M.iw /*6881399*/:
                        snm(",״", "ְֱֲֳִֵֶַָֹֻ׃֜֝׳,׆﬩״₪");
                        break;
                    case M.kk /*7012459*/:
                    case M.tv1 /*7602294*/:
                        snm(",ё", "әіңғүұқөһъ,ё★");
                        break;
                    case M.ky /*7012473*/:
                        snm(",ё", "цщьъё,-'★");
                        break;
                    case M.la /*7077985*/:
                        snm(",¯", "āēīōū,ȳæœë");
                        break;
                    case M.lt /*7078004*/:
                        snm(",ą", "ąčęėįšųūž*@§…~#,€qx„");
                        break;
                    case M.lv /*7078006*/:
                        snm(",č", "@«―»€āčēģīķļņšū,ž°~★");
                        break;
                    case M.ml /*7143532*/:
                        snm(",", "ൠൡൢൣ൹ൌ൰൱൲൳൴൵ൄഽ‍,‌₹");
                        break;
                    case M.mm /*7143533*/:
                        snm(",꯱", "꯱꯲꯳꯴꯵꯶꯷꯸꯹꯰,‍^‌₹");
                        break;
                    case M.mt /*7143540*/:
                        if (!M.fk()) {
                            snm(",ċ", ",ċġħż");
                            break;
                        }
                        break;
                    case M.mx /*7143544*/:
                    case M.my /*7143545*/:
                        snm(",၁", "၁၂၃၄၅၆၇၈၉၀ၚၛၜၝဨဍဋရဂဳ,‍^∷‌");
                        break;
                    case M.ne /*7209061*/:
                    case M.nw /*7209079*/:
                        snm(",", ",‍^∷‌");
                        break;
                    case M.pa /*7340129*/:
                        snm(",੧", "੦੧੨੩੪੫੬੭੮੯ੴ☬ਂੵਁ,豈।");
                        break;
                    case M.ro /*7471215*/:
                        snm(",î", M.kid.isBig() ? "\u0001«―»\u0002✘∴※∵✔@€§%*,…$~★".replace(8230, 238) : "ăâî" + M.roS + M.roT + ",…$~★");
                        break;
                    case M.si /*7536745*/:
                        snm(",ෞ", M.klsi == 0 ? "豈更車賈滑ෞොෛේෳෝ෴ඦ්ාැෑිීු,ූෙෘෟ" : "ෞොෛේඳෳෝ෴ඞඦ,ඬඏඟ");
                        break;
                    case M.sk /*7536747*/:
                        snm(",á", "áäčďéíĺľňóŕšťúý,ž");
                        break;
                    case M.sq /*7536753*/:
                        if (!ib) {
                            snm(",ë", ",ëç");
                            break;
                        }
                        break;
                    case M.tg /*7602279*/:
                        snm(",ғ", ",ъёғӯ");
                        break;
                    case M.tk /*7602283*/:
                        snm(",ˇ", ",žňöş");
                        break;
                    case M.tt /*7602292*/:
                        snm(",һү", M.fk() ? "wъцщжқғьһү,ё№«»" : "wъөәңҗқғһү,ё№«»");
                        break;
                    case M.uk /*7667819*/:
                        snm(",ї", "№'ъэә,їґё★");
                        break;
                    case M.we /*7798885*/:
                        snm(",ˇ", "čćěłńóřŕšś,žź");
                        break;
                    case M.wo /*7798895*/:
                        snm(",ã", "€ŋñóù,ãàéë");
                        break;
                    case M.yi /*7929961*/:
                        snm(",״", "אַאָבּכּבֿװױײוּיִײַפֿפּשׂתּ׀׃׳׆﬩,״־₪");
                        break;
                    default:
                        if (!M.dvn) {
                            if (!M.bn1) {
                                if (!M.arConAble()) {
                                    if (!M.sc() || ib || M.kid.is(R.xml.kbd_dvorak)) {
                                        if (this.mMode != 4) {
                                            if (this.mMode != 5) {
                                                if (M.mLC != M.de || ib || M.kid.is(R.xml.kbd_dvorak)) {
                                                    if (M.cy() == -1) {
                                                        snm(",", "\u0001«―»\u0002✘∴※∵✔@€§%*,…$~★");
                                                        break;
                                                    }
                                                    String str = M.mLC == M.ru ? ",ъё" : M.mLC == M.bg ? ",ѝ" : ",ё";
                                                    snm(str, "ѣіѳѵѝї«―»ә'\"№@~,ъёѝ★");
                                                    break;
                                                }
                                                snm(",¨", "✘µ€$§~²³%*°`´_★,äöüß");
                                                break;
                                            }
                                            snm("@", "\u0001«―»\u0002✘∴※∵✔@€§%*,…$~★");
                                            break;
                                        }
                                        snm("/", "\u0001«―»\u0002✘∴※∵✔@€§%*,…$~★");
                                        break;
                                    }
                                    snm(",å", "«äö―»,æøå★");
                                    break;
                                }
                                snm(M.ax, M.an() + "ٰ؛٪،-ٌٍِْ‌ـًَُّ");
                                break;
                            }
                            snm(",১", "৲৳৴৵৶৷৸৹৺ৗ১২৩৪৫৬৭৮৯০,ৠৡৢৣ");
                            break;
                        }
                        snm(",१", "१२३४५६७८९०ःऒऋऍॅ豈更車賈滑,串ऽ॥ॐ");
                        break;
                }
            }
            snm(",", "\u0001«―»\u0002✘∴※∵✔@€§%*,…$~★");
            if (this.mVoiceEnabled && this.mHasVoiceButton && this.mF1Key.label == ",") {
                setMicF1Key(this.mF1Key);
            }
        }
    }

    private void snm(String s1, String s2) {
        setNonMicF1Key(this.mF1Key, s1, R.xml.kbd_popup_template);
        this.mF1Key.popupCharacters = s2;
    }

    private void alt1(Key k2, String s) {
        alt(k2, s);
        k2.codes = new int[]{k2.codes[0], s.charAt(0)};
    }

    private void alt(Key k2, String s) {
        M.alt(k2, s);
    }

    protected Key createKeyFromXml(Resources res, Row r, int x, int y, XmlResourceParser parser) {
        int i;
        Key key = new LatinKey(res, r, x, y, parser);
        if (key.codes != null) {
            switch (key.codes[0]) {
                case -103:
                    this.mF1Key = key;
                    break;
                case -100:
                    this.mOptKey = key;
                    break;
                case -5:
                    this.mDelKey = key;
                    break;
                case -2:
                    this.m123Key = key;
                    this.m123Label = key.label;
                    break;
                case LatinKeyboardBaseView.NOT_A_TOUCH_COORDINATE /*-1*/:
                    this.mShiftKey = key;
                    key.codes = M.skc;
                    if (M.mAltCaps) {
                        key.popupResId = R.xml.altcc;
                        break;
                    }
                    break;
                case R.styleable.LatinKeyboardBaseView_verticalCorrection /*9*/:
                    this.mTabKey = key;
                    break;
                case R.styleable.LatinKeyboardBaseView_popupLayout /*10*/:
                    this.mEnterKey = key;
                    this.mxk = new Key[4];
                    for (i = 0; i < this.mxk.length; i++) {
                        this.mxk[i] = new LatinKey(res, r, x, y, parser);
                    }
                    break;
                case Suggest.APPROX_MAX_WORD_LENGTH /*32*/:
                    this.mSpaceKey = key;
                    break;
            }
        }
        boolean skb = M.kid.is(R.xml.kbd_symbols);
        boolean t9 = M.kid.is(R.xml.kbd_t9);
        if (!(skb || t9)) {
            pk(key, res);
        }
        int kc0 = key.codes[0];
        if (kc0 == 127) {
            key.width = 0;
        }
        if (M.kid.is(R.xml.kbd_t9) && M.irn(kc0)) {
            i = M.cCy != -1 ? 2 : M.zt == 66 ? 0 : (M.ko == M.mLC && M.km() == 3) ? 1 : -1;
            if (i != -1) {
                String str;
                mp = new String[10][];
                mp[0] = new String[]{"0", "ㅇㅁ", null};
                mp[1] = new String[]{"一", "ㅣ", null};
                mp[2] = new String[]{"丨", "ㆍ", "абвг"};
                mp[3] = new String[]{"ノ", "ㅡ", "дежз"};
                mp[4] = new String[]{"ヽ", "ㄱㅋㄲ", "ийкл"};
                mp[5] = new String[]{"乙", "ㄴㄹ", "мноп"};
                mp[6] = new String[]{"6", "ㄷㅌㄸ", "рсту"};
                mp[7] = new String[]{"7", "ㅂㅍㅃ", "фхцч"};
                String[] strArr = new String[3];
                strArr[0] = "8";
                strArr[1] = "ㅅㅎㅆ";
                if (M.mLC == M.bg) {
                    str = "шщъ";
                } else {
                    str = "шщъы";
                }
                strArr[2] = str;
                mp[8] = strArr;
                strArr = new String[3];
                strArr[0] = "9";
                strArr[1] = "ㅈㅊㅉ";
                if (M.mLC == M.bg) {
                    str = "ьюя";
                } else {
                    str = "ьэюя";
                }
                strArr[2] = str;
                mp[9] = strArr;
                String l = mp[kc0 - 48][i];
                if (l != null) {
                    CharSequence substring;
                    if (l.length() <= 2 || i != 1) {
                        Object substring2 = l;
                    } else {
                        substring2 = l.substring(0, 2);
                    }
                    key.label = substring2;
                    if (i == 2) {
                        key.popupResId = R.xml.kbd_popup_template;
                        l = l + M.cyT9x(kc0);
                        key.popupCharacters = l + Character.toString((char) kc0);
                    } else {
                        key.popupCharacters = Character.toString((char) kc0);
                        key.popupResId = 0;
                    }
                    if (i == 1) {
                        key.codes = M.tia(l);
                    }
                }
            }
        }
        if (M.isT9Semi()) {
            switch (M.mLC) {
                case M.tg /*7602279*/:
                    switch (key.codes[0]) {
                        case 50:
                            key.popupCharacters = "абвгґғ2";
                            break;
                        case 52:
                            key.popupCharacters = "иӣйкл4";
                            break;
                        case 54:
                            key.popupCharacters = "рстуўӯ6";
                            break;
                        case 55:
                            key.popupCharacters = "фхҳцчҷ7";
                            break;
                    }
                    break;
            }
            if (M.eth && key.codes[0] == 49) {
                M.md1(key, "ø");
            }
            switch (M.mLC) {
                case M.ja /*6946913*/:
                case M.km /*7012461*/:
                case M.ko /*7012463*/:
                case M.th /*7602280*/:
                    break;
                default:
                    if (M.mt9() && M.zt != 90 && M.t9c((char) key.codes[0])) {
                        if ((!M.isLatinC && !M.eth) || !t9) {
                            p2kc(key);
                            break;
                        }
                        l2kc(key);
                        break;
                    }
                    break;
            }
        }
        if (M.kid.isPh()) {
            switch (key.codes[0]) {
                case 50:
                    alt(key, "abc");
                    break;
                case 51:
                    alt(key, "def");
                    break;
                case 52:
                    alt(key, "ghi");
                    break;
                case 53:
                    alt(key, "jkl");
                    break;
                case 54:
                    alt(key, "mno");
                    break;
                case 55:
                    alt(key, "pqrs");
                    break;
                case 56:
                    alt(key, "tuv");
                    break;
                case 57:
                    alt(key, "wxyz");
                    break;
            }
        }
        switch (key.codes[0]) {
            case 36:
                if (M.in) {
                    M.md1(key, "₹");
                    break;
                }
                break;
            case 46:
                if (M.bho) {
                    M.md1(key, "།");
                }
                if (M.bn1) {
                    M.md1(key, "৷");
                }
                alt(key, M.mLC == M.ip ? "̴̥̬̩̝̞̟̠̃̊̋́̄̀̏̌̂᷄᷅᷈ʼ̪̺̹̜̯̤̰̼̽ˠˤ̘̙̻̚" : M.pkp);
                break;
        }
        if (skb && M.fu()) {
            char c = M.toDigitUr((char) key.codes[0]);
            if (c != 0) {
                key.codes[0] = c;
                key.label = Character.toString(c);
            }
        }
        return key;
    }

    private static void p2kc(Key k) {
        CharSequence p = k.popupCharacters;
        if (p != null) {
            int l = p.length();
            k.codes = new int[l];
            for (int i = 0; i < l; i++) {
                k.codes[i] = p.charAt(i);
            }
        }
    }

    private static void l2kc(Key k) {
        CharSequence p = k.label;
        if (p != null) {
            int l = p.length();
            int t = k.codes[0];
            k.codes = new int[(l + 1)];
            for (int i = 0; i < l; i++) {
                k.codes[i] = p.charAt(i);
            }
            k.codes[l] = t;
        }
    }

    void setImeOptions(Resources res, int mode, int options) {
        this.mMode = mode;
        if (this.mEnterKey != null) {
            this.mEnterKey.popupCharacters = null;
            this.mEnterKey.popupResId = 0;
            this.mEnterKey.text = null;
            switch (M.lpe) {
                case 1:
                    this.mEnterKey.popupResId = R.xml.popup_cr;
                    break;
                case 2:
                    this.mEnterKey.popupResId = R.xml.popup_smileys;
                    break;
            }
            Key key;
            Key key2;
            Drawable drawable;
            switch (1073742079 & options) {
                case 2:
                    this.mEnterKey.iconPreview = null;
                    this.mEnterKey.icon = null;
                    this.mEnterKey.label = res.getText(R.string.label_go_key);
                    break;
                case 3:
                    key = this.mEnterKey;
                    key2 = this.mEnterKey;
                    drawable = res.getDrawable(R.drawable.sym_keyboard_search);
                    key2.icon = drawable;
                    key.iconPreview = drawable;
                    this.mEnterKey.label = null;
                    break;
                case 4:
                    this.mEnterKey.iconPreview = null;
                    this.mEnterKey.icon = null;
                    this.mEnterKey.label = res.getText(R.string.label_send_key);
                    break;
                case 5:
                    this.mEnterKey.iconPreview = null;
                    this.mEnterKey.icon = null;
                    this.mEnterKey.label = res.getText(R.string.label_next_key);
                    break;
                case 6:
                    this.mEnterKey.iconPreview = null;
                    this.mEnterKey.icon = null;
                    this.mEnterKey.label = res.getText(R.string.label_done_key);
                    break;
                default:
                    if (!M.ws() || mode != 6) {
                        key = this.mEnterKey;
                        key2 = this.mEnterKey;
                        drawable = res.getDrawable(R.drawable.sym_keyboard_return);
                        key2.icon = drawable;
                        key.iconPreview = drawable;
                        this.mEnterKey.label = null;
                        break;
                    }
                    key = this.mEnterKey;
                    this.mEnterKey.iconPreview = null;
                    key.icon = null;
                    this.mEnterKey.label = ":-)";
                    this.mEnterKey.text = ":-) ";
                    this.mEnterKey.popupResId = R.xml.popup_smileys;
                    break;
                    break;
            }
            if (this.mEnterKey.iconPreview != null) {
                setDefaultBounds(this.mEnterKey.iconPreview);
            }
        }
    }

    void enableShiftLock() {
        if (this.mShiftKey != null && (this.mShiftKey instanceof LatinKey)) {
            ((LatinKey) this.mShiftKey).enableShiftLock();
        }
    }

    void setShiftLocked(boolean shiftLocked) {
        if (this.mShiftKey == null) {
            return;
        }
        if (shiftLocked) {
            this.mShiftKey.on = true;
            this.mShiftState = 2;
            return;
        }
        this.mShiftKey.on = false;
        this.mShiftState = 1;
    }

    boolean isShiftLocked() {
        return this.mShiftState == 2;
    }

    public boolean setShifted(boolean shiftState) {
        boolean shiftChanged = false;
        if (this.mShiftKey == null) {
            return super.setShifted(shiftState);
        }
        if (!shiftState) {
            shiftChanged = this.mShiftState != 0;
            this.mShiftState = 0;
            this.mShiftKey.on = false;
        } else if (this.mShiftState == 0) {
            if (this.mShiftState == 0) {
                shiftChanged = true;
            } else {
                shiftChanged = false;
            }
            this.mShiftState = 1;
        }
        return shiftChanged;
    }

    public boolean isShifted() {
        if (this.mShiftKey != null) {
            return this.mShiftState != 0;
        } else {
            return super.isShifted();
        }
    }

    public void setColorOfSymbolIcons(boolean isAutoCompletion, boolean isBlack) {
        boolean t;
        this.mIsBlackSym = false;
        if (this.mTabKey != null) {
            t = true;
        } else {
            t = false;
        }
        this.mMicIcon = this.mRes.getDrawable(R.drawable.sym_keyboard_mic);
        this.m123MicIcon = this.mRes.getDrawable(R.drawable.sym_keyboard_123_mic);
        if (this.mDelKey != null) {
            this.mDelKey.icon = this.mRes.getDrawable(R.drawable.sdel);
        }
        if (t) {
            this.mTabKey.icon = this.mRes.getDrawable(R.drawable.sym_keyboard_tab);
        }
        if (this.mOptKey != null) {
            this.mOptKey.icon = this.mRes.getDrawable(R.drawable.sym_keyboard_settings);
        }
        updateDynamicKeys();
        if (this.mSpaceKey != null) {
            updateSpaceBarForLocale(isAutoCompletion, false);
        }
    }

    private void setDefaultBounds(Drawable drawable) {
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
    }

    public void setVoiceMode(boolean hasVoiceButton, boolean hasVoice) {
        this.mHasVoiceButton = hasVoiceButton;
        this.mVoiceEnabled = hasVoice;
        updateDynamicKeys();
    }

    private void updateDynamicKeys() {
        updateF1Key();
        update123Key();
    }

    private void setMicF1Key(Key key) {
        key.label = null;
        key.codes[0] = -102;
        Drawable drawable = this.mMicIcon;
        key.icon = drawable;
        key.iconPreview = drawable;
    }

    private void setNonMicF1Key(Key key, String label, int popupResId) {
        key.label = label;
        key.codes[0] = label.charAt(0);
        key.popupResId = popupResId;
        key.iconPreview = null;
        key.icon = null;
    }

    public boolean isF1Key(Key key) {
        return key == this.mF1Key;
    }

    public static boolean hasPuncOrSmileysPopup(Key key) {
        return key.codes[0] == 46;
    }

    public Key onAutoCompletionStateChanged(boolean isAutoCompletion) {
        if (this.mSpaceKey != null) {
            updateSpaceBarForLocale(isAutoCompletion, this.mIsBlackSym);
        }
        return this.mSpaceKey;
    }

    public boolean isLanguageSwitchEnabled() {
        return this.mLocale != null;
    }

    private void updateSpaceBarForLocale(boolean isAutoCompletion, boolean isBlack) {
        if (this.mLocale != null) {
            this.mSpaceKey.icon = new BitmapDrawable(this.mRes, drawSpaceBar(isAutoCompletion, isBlack));
        } else if (isAutoCompletion) {
            this.mSpaceKey.icon = new BitmapDrawable(this.mRes, drawSpaceBar(isAutoCompletion, isBlack));
        } else {
            this.mSpaceKey.icon = this.mRes.getDrawable(R.drawable.sym_keyboard_space);
        }
    }

    private static int getTextWidth(Paint paint, String text, float textSize, Rect bounds) {
        paint.setTextSize(textSize);
        paint.getTextBounds(text, 0, text.length(), bounds);
        return bounds.width();
    }

    private static String layoutSpaceBar(Paint paint, String locale, int width, int height, float origTextSize, boolean allowVariableTextSize) {
        float maxTextWidth = (float) width;
        Rect bounds = new Rect();
        String s = M.dn(locale);
        M.stf(paint, s.charAt(0));
        String language = mALt + " " + M.reverse(s).toString() + " " + mARt;
        float textSize = origTextSize * Math.min(maxTextWidth / ((float) getTextWidth(paint, language, origTextSize, bounds)), 1.0f);
        if (allowVariableTextSize) {
            int textWidth = getTextWidth(paint, language, textSize, bounds);
        } else {
            textSize = origTextSize;
        }
        paint.setTextSize(textSize);
        float baseline = ((float) height) * SPACEBAR_LANGUAGE_BASELINE;
        return language;
    }

    private Bitmap drawSpaceBar(boolean isAutoCompletion, boolean isBlack) {
        int width = this.mSpaceKey.width;
        int height = this.mSpaceKey.height;
        Bitmap buffer = Bitmap.createBitmap(width, height, Config.ARGB_8888);
        Canvas canvas = new Canvas(buffer);
        canvas.drawColor(0, Mode.CLEAR);
        if (this.mLocale != null) {
            String language = layoutSpaceBar(this.paint, this.mLanguageSwitcher.getInputLanguage(), width, height, (float) getTextSizeFromTheme(16973894, 14), true);
            float baseline = ((float) height) * SPACEBAR_LANGUAGE_BASELINE;
            float descent = this.paint.descent();
            if (M.nsd(language.charAt(2))) {
                this.paint.setColor(-1);
                M.rtld(canvas, language, 0, 0, width, height, this.paint);
            } else {
                this.paint.setColor(-16777216);
                canvas.drawText(language, (float) (width / 2), (baseline - descent) - 1.0f, this.paint);
                this.paint.setColor(-1);
                canvas.drawText(language, (float) (width / 2), baseline - descent, this.paint);
            }
        }
        if (isAutoCompletion) {
            int iconWidth = (width * SPACE_LED_LENGTH_PERCENT) / 100;
            int iconHeight = height / 10;
            int x = (width - iconWidth) / 2;
            int y = height - iconHeight;
            canvas.drawRect((float) x, (float) y, (float) (x + iconWidth), (float) (y + iconHeight), M.hlp);
        }
        return buffer;
    }

    private void updateLocaleDrag(int diff) {
        if (M.swsl) {
            if (this.mSlidingLocaleIcon == null) {
                int width = Math.max(this.mSpaceKey.width, (int) (((float) getMinWidth()) * SPACEBAR_POPUP_MIN_RATIO));
                int height = this.mSpaceKey.height;
                this.mSlidingLocaleIcon = new SlidingLocaleDrawable(this.mMicIcon, width, height);
                this.mSlidingLocaleIcon.setBounds(0, 0, width, height);
                this.mSpaceKey.iconPreview = this.mSlidingLocaleIcon;
            }
            this.mSlidingLocaleIcon.setDiff(diff);
            if (Math.abs(diff) == Integer.MAX_VALUE) {
                this.mSpaceKey.iconPreview = null;
                return;
            }
            this.mSpaceKey.iconPreview = this.mSlidingLocaleIcon;
            this.mSpaceKey.iconPreview.invalidateSelf();
        }
    }

    public int getHeight() {
        return ((super.getHeight() * this.mScale) * M.ks()) / 10000;
    }

    public int getLanguageChangeDirection() {
        if (this.mSpaceKey == null || this.mLanguageSwitcher.getLocaleCount() < 2 || ((float) Math.abs(this.mSpaceDragLastDiff)) < ((float) this.mSpaceKey.width) * SPACEBAR_DRAG_THRESHOLD) {
            return 0;
        }
        return this.mSpaceDragLastDiff > 0 ? 1 : -1;
    }

    public void setLanguageSwitcher(LanguageSwitcher switcher, boolean isAutoCompletion, boolean isBlackSym) {
        this.mLanguageSwitcher = switcher;
        if (this.mLanguageSwitcher.getLocaleCount() > 1) {
            this.mLocale = this.mLanguageSwitcher.getInputLanguage();
            this.paint = new Paint();
            this.paint.setAlpha(OPACITY_FULLY_OPAQUE);
            this.paint.setAntiAlias(true);
            this.paint.setTextAlign(Align.CENTER);
        }
        setColorOfSymbolIcons(isAutoCompletion, isBlackSym);
    }

    boolean isCurrentlyInSpace() {
        return this.mCurrentlyInSpace;
    }

    void setPreferredLetters(int[] frequencies) {
        this.mPrefLetterFrequencies = frequencies;
        this.mPrefLetter = 0;
    }

    void keyReleased() {
        this.mCurrentlyInSpace = false;
        this.mSpaceDragLastDiff = 0;
        this.mPrefLetter = 0;
        this.mPrefLetterX = 0;
        this.mPrefLetterY = 0;
        this.mPrefDistance = Integer.MAX_VALUE;
        if (this.mSpaceKey != null) {
            updateLocaleDrag(Integer.MAX_VALUE);
        }
    }

    boolean isInside(LatinKey key, int x, int y) {
        int code = key.codes[0];
        if (code == -1 || code == -5) {
            y -= key.height / 10;
            if (code == -1) {
                x += key.width / 6;
            }
            if (code == -5) {
                x -= key.width / 6;
            }
        } else if (code == 32) {
            if (this.mLanguageSwitcher.getLocaleCount() > 1) {
                if (this.mCurrentlyInSpace) {
                    int diff = x - this.mSpaceDragStartX;
                    if (Math.abs(diff - this.mSpaceDragLastDiff) > 0) {
                        updateLocaleDrag(diff);
                    }
                    this.mSpaceDragLastDiff = diff;
                    return true;
                }
                boolean insideSpace = key.isInsideSuper(x, y);
                if (insideSpace) {
                    this.mCurrentlyInSpace = true;
                    this.mSpaceDragStartX = x;
                    updateLocaleDrag(0);
                }
                return insideSpace;
            }
        } else if (this.mPrefLetterFrequencies != null) {
            if (!(this.mPrefLetterX == x && this.mPrefLetterY == y)) {
                this.mPrefLetter = 0;
                this.mPrefDistance = Integer.MAX_VALUE;
            }
            int[] pref = this.mPrefLetterFrequencies;
            if (this.mPrefLetter > 0) {
                return this.mPrefLetter == code;
            } else {
                boolean inside = key.isInsideSuper(x, y);
                int[] nearby = getNearestKeys(x, y);
                List<Key> nearbyKeys = getKeys();
                Key k;
                int dist;
                if (inside && inPrefList(code, pref)) {
                    this.mPrefLetter = code;
                    this.mPrefLetterX = x;
                    this.mPrefLetterY = y;
                    for (int i : nearby) {
                        k = (Key) nearbyKeys.get(i);
                        if (k != key && inPrefList(k.codes[0], pref)) {
                            dist = distanceFrom(k, x, y);
                            if (dist < ((int) (((float) k.width) * OVERLAP_PERCENTAGE_LOW_PROB)) && pref[k.codes[0]] > pref[this.mPrefLetter] * 3) {
                                this.mPrefLetter = k.codes[0];
                                this.mPrefDistance = dist;
                                break;
                            }
                        }
                    }
                    if (this.mPrefLetter == code) {
                        return true;
                    }
                    return false;
                }
                for (int i2 : nearby) {
                    k = (Key) nearbyKeys.get(i2);
                    if (inPrefList(k.codes[0], pref)) {
                        dist = distanceFrom(k, x, y);
                        if (dist < ((int) (((float) k.width) * OVERLAP_PERCENTAGE_HIGH_PROB)) && dist < this.mPrefDistance) {
                            this.mPrefLetter = k.codes[0];
                            this.mPrefLetterX = x;
                            this.mPrefLetterY = y;
                            this.mPrefDistance = dist;
                        }
                    }
                }
                if (this.mPrefLetter == 0) {
                    return inside;
                }
                return this.mPrefLetter == code;
            }
        }
        if (this.mCurrentlyInSpace) {
            return false;
        }
        return key.isInsideSuper(x, y);
    }

    private boolean inPrefList(int code, int[] pref) {
        if (code >= pref.length || code < 0 || pref[code] <= 0) {
            return false;
        }
        return true;
    }

    private int distanceFrom(Key k, int x, int y) {
        if (y <= k.y || y >= k.y + k.height) {
            return Integer.MAX_VALUE;
        }
        return Math.abs((k.x + (k.width / 2)) - x);
    }

    public int[] getNearestKeys(int x, int y) {
        if (!this.mCurrentlyInSpace) {
            if (!this.mSpaceKey.isInside(x, y - (M.awk ? 0 : this.mSpaceKey.height / 4))) {
                return super.getNearestKeys(Math.max(0, Math.min(x, getMinWidth() - 1)), Math.max(0, Math.min(y, getHeight() - 1)));
            }
        }
        return this.ia;
    }

    private int indexOf(int code) {
        List<Key> keys = getKeys();
        int count = keys.size();
        for (int i = 0; i < count; i++) {
            if (((Key) keys.get(i)).codes[0] == code) {
                return i;
            }
        }
        return -1;
    }

    private int getTextSizeFromTheme(int style, int defValue) {
        TypedArray array = this.mContext.getTheme().obtainStyledAttributes(style, new int[]{16842901});
        try {
            return array.getDimensionPixelSize(array.getResourceId(0, 0), defValue);
        } catch (Exception e) {
            return defValue;
        } catch (Error e2) {
            return defValue;
        }
    }
}
