package com.klye.ime.latin;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.PorterDuff.Mode;
import android.graphics.Rect;
import android.graphics.Region.Op;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.Keyboard.Key;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewGroup.LayoutParams;
import android.widget.PopupWindow;
import android.widget.TextView;
import com.klye.ime.latin.PointerTracker.UIProxy;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.WeakHashMap;

public class LatinKeyboardBaseView extends View implements UIProxy {
    private static final boolean DEBUG = false;
    private static final int[] LONG_PRESSABLE_STATE_SET = new int[]{16843324};
    static final int NOT_A_KEY = -1;
    public static final int NOT_A_TOUCH_COORDINATE = -1;
    private static final int NUMBER_HINT_VERTICAL_ADJUSTMENT_PIXEL = -1;
    private static final String TAG = "LatinKeyboardBaseView";
    private final String KEY_LABEL_HEIGHT_REFERENCE_CHAR;
    private final float KEY_LABEL_VERTICAL_ADJUSTMENT_FACTOR;
    protected G g;
    private float mBackgroundDimAmount;
    private Bitmap mBuffer;
    private Canvas mCanvas;
    private final Rect mClipRegion;
    private final int mDelayAfterPreview;
    private final int mDelayBeforePreview;
    private final Rect mDirtyRect;
    private boolean mDrawPending;
    final UIHandler mHandler;
    private boolean mHasDistinctMultitouch;
    private Key mInvalidatedKey;
    private Drawable mKeyBackground;
    protected KeyDetector mKeyDetector;
    private float mKeyHysteresisDistance;
    private final int mKeyRepeatInterval;
    private int mKeyTextColor;
    private Keyboard mKeyboard;
    private OnKeyboardActionListener mKeyboardActionListener;
    private boolean mKeyboardChanged;
    private int mKeyboardVerticalGap;
    Key[] mKeys;
    private int mLabelTextSize;
    private LatinKeyboardBaseView mMiniKeyboard;
    private final WeakHashMap<Key, View> mMiniKeyboardCache;
    private int mMiniKeyboardOriginX;
    private int mMiniKeyboardOriginY;
    private View mMiniKeyboardParent;
    private PopupWindow mMiniKeyboardPopup;
    private long mMiniKeyboardPopupTime;
    private final float mMiniKeyboardSlideAllowance;
    private int mMiniKeyboardTrackerId;
    private int[] mOffsetInWindow;
    private int mOldPointerCount;
    private int mOldPreviewKeyIndex;
    private final Rect mPadding;
    private final Paint mPaint;
    private final PointerQueue mPointerQueue;
    private final ArrayList<PointerTracker> mPointerTrackers;
    private int mPopupLayout;
    private int mPopupPreviewDisplayedY;
    private int mPopupPreviewOffsetX;
    private int mPopupPreviewOffsetY;
    private int mPreviewHeight;
    private int mPreviewOffset;
    private PopupWindow mPreviewPopup;
    private TextView mPreviewText;
    private int mPreviewTextSizeLarge;
    private int mShadowColor;
    private float mShadowRadius;
    private boolean mShowPreview;
    private boolean mShowTouchPoints;
    private int mSymbolColorScheme;
    private final HashMap<Integer, Integer> mTextHeightCache;
    private float mVerticalCorrection;
    private int[] mWindowOffset;
    private int mWindowY;

    public interface OnKeyboardActionListener {
        void onCancel();

        void onKey(int i, int[] iArr, int i2, int i3);

        void onPress(int i);

        void onRelease(int i);

        void onText(CharSequence charSequence);

        void swipeDown();

        void swipeLeft();

        void swipeRight();

        void swipeUp();
    }

    /* renamed from: com.klye.ime.latin.LatinKeyboardBaseView$2 */
    static /* synthetic */ class AnonymousClass2 {
        static final /* synthetic */ int[] $SwitchMap$com$klye$ime$latin$LatinKeyboardBaseView$E = new int[E.values().length];

        static {
            try {
                $SwitchMap$com$klye$ime$latin$LatinKeyboardBaseView$E[E.r.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                $SwitchMap$com$klye$ime$latin$LatinKeyboardBaseView$E[E.l.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
            try {
                $SwitchMap$com$klye$ime$latin$LatinKeyboardBaseView$E[E.t.ordinal()] = 3;
            } catch (NoSuchFieldError e3) {
            }
            try {
                $SwitchMap$com$klye$ime$latin$LatinKeyboardBaseView$E[E.b.ordinal()] = 4;
            } catch (NoSuchFieldError e4) {
            }
            try {
                $SwitchMap$com$klye$ime$latin$LatinKeyboardBaseView$E[E.tr.ordinal()] = 5;
            } catch (NoSuchFieldError e5) {
            }
            try {
                $SwitchMap$com$klye$ime$latin$LatinKeyboardBaseView$E[E.tl.ordinal()] = 6;
            } catch (NoSuchFieldError e6) {
            }
            try {
                $SwitchMap$com$klye$ime$latin$LatinKeyboardBaseView$E[E.br.ordinal()] = 7;
            } catch (NoSuchFieldError e7) {
            }
            try {
                $SwitchMap$com$klye$ime$latin$LatinKeyboardBaseView$E[E.bl.ordinal()] = 8;
            } catch (NoSuchFieldError e8) {
            }
        }
    }

    enum E {
        n,
        r,
        b,
        t,
        l,
        tr,
        br,
        tl,
        bl
    }

    class G {
        float cx;
        float cy;
        float dx = 0.0f;
        float dy = 0.0f;
        int m = 0;
        float ox = -1.0f;
        float oy = -1.0f;
        boolean sj = false;

        G() {
        }

        E detect(MotionEvent me) {
            if (!M.mDG && !M.hw()) {
                return E.n;
            }
            float x = me.getX();
            float y = me.getY();
            switch (me.getAction()) {
                case 0:
                    this.ox = x;
                    this.cx = x;
                    this.oy = y;
                    this.cy = y;
                    this.sj = false;
                    break;
                case 1:
                    if (!(this.sj || M.hws() || (M.hw() && M.h != null && M.h.wm()))) {
                        this.dx = x - this.ox;
                        this.dy = y - this.oy;
                        if (this.dx * 2.0f > ((float) M.dm.widthPixels)) {
                            if (this.dy * 2.0f > ((float) LatinKeyboardBaseView.this.getHeight())) {
                                return E.br;
                            }
                            return (-this.dy) * 3.0f > ((float) LatinKeyboardBaseView.this.getHeight()) ? E.tr : E.r;
                        } else if ((-this.dx) * 2.0f > ((float) M.dm.widthPixels)) {
                            if (this.dy * 2.0f > ((float) LatinKeyboardBaseView.this.getHeight())) {
                                return E.bl;
                            }
                            return (-this.dy) * 3.0f > ((float) LatinKeyboardBaseView.this.getHeight()) ? E.tl : E.l;
                        } else if (this.dy * 4.0f > ((float) (LatinKeyboardBaseView.this.getHeight() * 3))) {
                            return E.b;
                        } else {
                            if ((-this.dy) * 4.0f > ((float) (LatinKeyboardBaseView.this.getHeight() * 3))) {
                                return E.t;
                            }
                        }
                    }
                    break;
                case 2:
                    this.dx = Math.abs(x - this.cx);
                    this.dy = Math.abs(y - this.cy);
                    this.cx = x;
                    this.cy = y;
                    if (this.dx * 2.0f > ((float) M.dm.widthPixels) || this.dy * 2.0f > ((float) LatinKeyboardBaseView.this.getHeight())) {
                        this.sj = true;
                        break;
                    }
                case 5:
                case 6:
                    this.sj = true;
                    break;
            }
            return E.n;
        }
    }

    class KB extends Keyboard {
        public KB(Context context, int layoutTemplateResId, CharSequence characters, int columns, int horizontalPadding) {
            super(context, layoutTemplateResId, characters, columns, horizontalPadding);
        }

        public KB(Context context, int id) {
            super(context, id);
            if (id == R.xml.popup_modes) {
                List<Key> k = super.getKeys();
                int count = k.size();
                for (int i = 0; i < count; i++) {
                    Key k2 = (Key) k.get(i);
                    if (k2.width > k2.height * 2) {
                        k2.width = (k2.width * 2) / 3;
                        k2.x = (k2.x * 2) / 3;
                    }
                }
            }
        }

        void skh(int i) {
            setKeyHeight(i);
        }
    }

    static class PointerQueue {
        private LinkedList<PointerTracker> mQueue = new LinkedList();

        PointerQueue() {
        }

        public void add(PointerTracker tracker) {
            this.mQueue.add(tracker);
        }

        public int lastIndexOf(PointerTracker tracker) {
            LinkedList<PointerTracker> queue = this.mQueue;
            for (int index = queue.size() - 1; index >= 0; index--) {
                if (((PointerTracker) queue.get(index)) == tracker) {
                    return index;
                }
            }
            return -1;
        }

        public void releaseAllPointersOlderThan(PointerTracker tracker, long eventTime) {
            LinkedList<PointerTracker> queue = this.mQueue;
            int oldestPos = 0;
            for (PointerTracker t = (PointerTracker) queue.get(0); t != tracker; t = (PointerTracker) queue.get(oldestPos)) {
                if (t.isModifier()) {
                    oldestPos++;
                } else {
                    t.onUpEvent(t.getLastX(), t.getLastY(), eventTime);
                    t.setAlreadyProcessed();
                    queue.remove(oldestPos);
                }
            }
        }

        public void releaseAllPointersExcept(PointerTracker tracker, long eventTime) {
            Iterator i$ = this.mQueue.iterator();
            while (i$.hasNext()) {
                PointerTracker t = (PointerTracker) i$.next();
                if (t != tracker) {
                    t.onUpEvent(t.getLastX(), t.getLastY(), eventTime);
                    t.setAlreadyProcessed();
                }
            }
            this.mQueue.clear();
            if (tracker != null) {
                this.mQueue.add(tracker);
            }
        }

        public void remove(PointerTracker tracker) {
            this.mQueue.remove(tracker);
        }
    }

    class UIHandler extends Handler {
        private static final int MSG_DISMISS_PREVIEW = 2;
        private static final int MSG_LONGPRESS_KEY = 4;
        private static final int MSG_POPUP_PREVIEW = 1;
        private static final int MSG_REPEAT_KEY = 3;
        private boolean mInKeyRepeat;

        UIHandler() {
        }

        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    LatinKeyboardBaseView.this.showKey(msg.arg1, (PointerTracker) msg.obj);
                    return;
                case 2:
                    try {
                        LatinKeyboardBaseView.this.mPreviewPopup.dismiss();
                        return;
                    } catch (Throwable th) {
                        return;
                    }
                case 3:
                    PointerTracker tracker = msg.obj;
                    tracker.repeatKey(msg.arg1);
                    startKeyRepeatTimer((long) LatinKeyboardBaseView.this.mKeyRepeatInterval, msg.arg1, tracker);
                    return;
                case 4:
                    LatinKeyboardBaseView.this.openPopupIfRequired(msg.arg1, (PointerTracker) msg.obj);
                    return;
                default:
                    return;
            }
        }

        public void popupPreview(long delay, int keyIndex, PointerTracker tracker) {
            removeMessages(1);
            if (LatinKeyboardBaseView.this.mPreviewPopup.isShowing() && LatinKeyboardBaseView.this.mPreviewText.getVisibility() == 0) {
                LatinKeyboardBaseView.this.showKey(keyIndex, tracker);
            } else {
                sendMessageDelayed(obtainMessage(1, keyIndex, 0, tracker), delay);
            }
        }

        public void cancelPopupPreview() {
            removeMessages(1);
        }

        public void dismissPreview(long delay) {
            if (LatinKeyboardBaseView.this.mPreviewPopup.isShowing()) {
                sendMessageDelayed(obtainMessage(2), delay);
            }
        }

        public void cancelDismissPreview() {
            removeMessages(2);
        }

        public void startKeyRepeatTimer(long delay, int keyIndex, PointerTracker tracker) {
            this.mInKeyRepeat = true;
            sendMessageDelayed(obtainMessage(3, keyIndex, 0, tracker), delay);
        }

        public void cancelKeyRepeatTimer() {
            this.mInKeyRepeat = false;
            removeMessages(3);
        }

        public boolean isInKeyRepeat() {
            return this.mInKeyRepeat;
        }

        public void startLongPressTimer(long delay, int keyIndex, PointerTracker tracker) {
            removeMessages(4);
            sendMessageDelayed(obtainMessage(4, keyIndex, 0, tracker), delay);
        }

        public void cancelLongPressTimer() {
            removeMessages(4);
        }

        public void cancelKeyTimers() {
            cancelKeyRepeatTimer();
            cancelLongPressTimer();
        }

        public void cancelAllMessages() {
            cancelKeyTimers();
            cancelPopupPreview();
            cancelDismissPreview();
        }
    }

    public void slpt() {
        this.mHandler.startLongPressTimer(600, 0, getPointerTracker(0));
    }

    public void clpt() {
        this.mHandler.cancelLongPressTimer();
        this.mPointerQueue.mQueue.clear();
        dismissKeyPreview();
    }

    public LatinKeyboardBaseView(Context context, AttributeSet attrs) {
        this(context, attrs, R.attr.keyboardViewStyle);
    }

    public LatinKeyboardBaseView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.mSymbolColorScheme = 0;
        this.mOldPreviewKeyIndex = -1;
        this.mShowPreview = true;
        this.mShowTouchPoints = true;
        this.mMiniKeyboardCache = new WeakHashMap();
        this.mPointerTrackers = new ArrayList();
        this.mPointerQueue = new PointerQueue();
        this.mOldPointerCount = 1;
        this.mKeyDetector = new ProximityKeyDetector();
        this.mDirtyRect = new Rect();
        this.mClipRegion = new Rect(0, 0, 0, 0);
        this.mTextHeightCache = new HashMap();
        this.KEY_LABEL_VERTICAL_ADJUSTMENT_FACTOR = 0.55f;
        this.KEY_LABEL_HEIGHT_REFERENCE_CHAR = "H";
        this.mHandler = new UIHandler();
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.LatinKeyboardBaseView, defStyle, R.style.LatinKeyboardBaseView);
        LayoutInflater inflate = (LayoutInflater) context.getSystemService("layout_inflater");
        int previewLayout = 0;
        int n = a.getIndexCount();
        for (int i = 0; i < n; i++) {
            int attr = a.getIndex(i);
            switch (attr) {
                case 1:
                    this.mKeyBackground = a.getDrawable(attr);
                    break;
                case 2:
                    M.kfs = a.getDimensionPixelSize(attr, 18);
                    break;
                case 3:
                    this.mLabelTextSize = a.getDimensionPixelSize(attr, 14);
                    break;
                case 4:
                    this.mKeyTextColor = a.getColor(attr, -16777216);
                    break;
                case 5:
                    previewLayout = a.getResourceId(attr, 0);
                    break;
                case 6:
                    this.mPreviewOffset = a.getDimensionPixelOffset(attr, 0);
                    break;
                case 7:
                    this.mPreviewHeight = a.getDimensionPixelSize(attr, 80);
                    break;
                case R.styleable.LatinKeyboardBaseView_keyHysteresisDistance /*8*/:
                    this.mKeyHysteresisDistance = (float) a.getDimensionPixelOffset(attr, 0);
                    break;
                case R.styleable.LatinKeyboardBaseView_verticalCorrection /*9*/:
                    this.mVerticalCorrection = (float) a.getDimensionPixelOffset(attr, 0);
                    break;
                case R.styleable.LatinKeyboardBaseView_popupLayout /*10*/:
                    this.mPopupLayout = a.getResourceId(attr, 0);
                    break;
                case R.styleable.LatinKeyboardBaseView_shadowColor /*11*/:
                    this.mShadowColor = a.getColor(attr, 0);
                    break;
                case R.styleable.LatinKeyboardBaseView_shadowRadius /*12*/:
                    this.mShadowRadius = a.getFloat(attr, 0.0f);
                    break;
                case R.styleable.LatinKeyboardBaseView_backgroundDimAmount /*13*/:
                    this.mBackgroundDimAmount = a.getFloat(attr, 0.5f);
                    break;
                case R.styleable.LatinKeyboardBaseView_symbolColorScheme /*15*/:
                    this.mSymbolColorScheme = a.getInt(attr, 0);
                    break;
                default:
                    break;
            }
        }
        Resources res = getResources();
        this.mPreviewPopup = new PopupWindow(context);
        this.mPreviewPopup.setAnimationStyle(0);
        if (previewLayout != 0) {
            this.mPreviewText = (TextView) inflate.inflate(previewLayout, null);
            this.mPreviewTextSizeLarge = (int) res.getDimension(R.dimen.key_preview_text_size_large);
            this.mPreviewPopup.setContentView(this.mPreviewText);
            this.mPreviewPopup.setBackgroundDrawable(null);
        } else {
            this.mShowPreview = false;
        }
        this.mPreviewPopup.setTouchable(false);
        this.mDelayBeforePreview = 10;
        this.mDelayAfterPreview = 120;
        this.mMiniKeyboardParent = this;
        this.mMiniKeyboardPopup = new PopupWindow(context);
        this.mMiniKeyboardPopup.setBackgroundDrawable(null);
        this.mPaint = new Paint();
        this.mPaint.setAntiAlias(true);
        this.mPaint.setTextSize((float) 0);
        this.mPaint.setTextAlign(Align.CENTER);
        this.mPaint.setAlpha(255);
        this.mPadding = new Rect(0, 0, 0, 0);
        if (this.mKeyBackground != null) {
            this.mKeyBackground.getPadding(this.mPadding);
        }
        this.mMiniKeyboardSlideAllowance = res.getDimension(R.dimen.mini_keyboard_slide_allowance);
        try {
            this.mHasDistinctMultitouch = context.getPackageManager().hasSystemFeature("android.hardware.touchscreen.multitouch.distinct");
        } catch (Throwable th) {
            this.mHasDistinctMultitouch = false;
        }
        this.mKeyRepeatInterval = res.getInteger(R.integer.config_key_repeat_interval);
    }

    public void setOnKeyboardActionListener(OnKeyboardActionListener listener) {
        this.mKeyboardActionListener = listener;
        Iterator i$ = this.mPointerTrackers.iterator();
        while (i$.hasNext()) {
            ((PointerTracker) i$.next()).setOnKeyboardActionListener(listener);
        }
    }

    protected OnKeyboardActionListener getOnKeyboardActionListener() {
        return this.mKeyboardActionListener;
    }

    public void setKeyboard(Keyboard keyboard, int vc) {
        if (this.mKeyboard != null) {
            dismissKeyPreview();
        }
        this.mHandler.cancelKeyTimers();
        this.mHandler.cancelPopupPreview();
        this.mKeyboard = keyboard;
        this.mKeys = this.mKeyDetector.setKeyboard(keyboard, (float) (-getPaddingLeft()), (float) vc);
        this.mKeyboardVerticalGap = (int) getResources().getDimension(R.dimen.key_bottom_gap);
        Iterator i$ = this.mPointerTrackers.iterator();
        while (i$.hasNext()) {
            ((PointerTracker) i$.next()).setKeyboard(this.mKeys, this.mKeyHysteresisDistance);
        }
        requestLayout();
        this.mKeyboardChanged = true;
        invalidateAllKeys();
        computeProximityThreshold(keyboard);
        this.mMiniKeyboardCache.clear();
    }

    public LatinKeyboard getLKB() {
        return (LatinKeyboard) this.mKeyboard;
    }

    public Keyboard getKeyboard() {
        return this.mKeyboard;
    }

    public boolean hasDistinctMultitouch() {
        return this.mHasDistinctMultitouch;
    }

    public boolean setShifted(boolean shifted) {
        if (this.mKeyboard == null || !this.mKeyboard.setShifted(shifted)) {
            return false;
        }
        invalidateAllKeys();
        return true;
    }

    public boolean isShifted() {
        if (this.mKeyboard != null) {
            return this.mKeyboard.isShifted();
        }
        return false;
    }

    public void setPreviewEnabled(boolean previewEnabled) {
        this.mShowPreview = previewEnabled;
    }

    public boolean isPreviewEnabled() {
        return this.mShowPreview;
    }

    public int getSymbolColorScheme() {
        return this.mSymbolColorScheme;
    }

    public void setPopupParent(View v) {
        this.mMiniKeyboardParent = v;
    }

    public void setPopupOffset(int x, int y) {
        this.mPopupPreviewOffsetX = x;
        this.mPopupPreviewOffsetY = y;
        this.mPreviewPopup.dismiss();
    }

    public void setProximityCorrectionEnabled(boolean enabled) {
        this.mKeyDetector.setProximityCorrectionEnabled(enabled);
    }

    public boolean isProximityCorrectionEnabled() {
        return this.mKeyDetector.isProximityCorrectionEnabled();
    }

    protected CharSequence adjustCase(CharSequence s) {
        if (!(s == null || M.kid.is(R.xml.kbd_edit) || M.kid.is(R.xml.kbd_symbols))) {
            int l = s.length();
            if (M.nose != '-' && l > 2 && s.charAt(1) == '-') {
                s = M.to(s, 45);
            }
            if (!M.kid.is(R.xml.kbd_ding)) {
                if (this.mKeyboard.isShifted() && s != null && l < 5 && !M.tvo.equals(s)) {
                    s = M.to(s, 83);
                }
                String s1;
                if (M.ls1 && l == 1) {
                    s1 = M.lsIme(s);
                    if (s1 != null) {
                        return s1;
                    }
                } else if (M.mLC == M.s4 && l == 1) {
                    s1 = M.s4Ime(s);
                    if (s1 != null) {
                        return s1;
                    }
                }
                if (M.isAK() && isMini()) {
                    s = M.to(s, M.eth ? 69 : M.zhType());
                }
                if (l == 1) {
                    s = M.to(s, 80);
                }
            }
        }
        return s;
    }

    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int i = 0;
        if (this.mKeyboard != null) {
            List<Key> ks = this.mKeyboard.getKeys();
            int l = ks.size();
            if (l > 0) {
                Key k = (Key) ks.get(0);
                Key kl = (Key) ks.get(l - 1);
                boolean lp = M.isLP(k.codes[0]);
                int width = (this.mKeyboard.getMinWidth() + getPaddingLeft()) + getPaddingRight();
                if (MeasureSpec.getSize(widthMeasureSpec) < width + 10) {
                    width = MeasureSpec.getSize(widthMeasureSpec);
                }
                if (lp) {
                    width = (M.dm.widthPixels * 9) / 10;
                }
                int paddingTop = (((kl.y + kl.height) - k.y) + getPaddingTop()) + getPaddingBottom();
                if (!M.isLand()) {
                    i = M.bgb;
                }
                setMeasuredDimension(width, i + paddingTop);
                return;
            }
        }
        setMeasuredDimension(getPaddingLeft() + getPaddingRight(), getPaddingTop() + getPaddingBottom());
    }

    private void computeProximityThreshold(Keyboard keyboard) {
        if (keyboard != null) {
            Key[] keys = this.mKeys;
            if (keys != null) {
                int dimensionSum = 0;
                for (Key key : keys) {
                    dimensionSum += Math.min(key.width, key.height + this.mKeyboardVerticalGap) + key.gap;
                }
                if (dimensionSum >= 0 && length != 0) {
                    this.mKeyDetector.setProximityThreshold((dimensionSum * (M.zt != -1 ? 10 : 11)) / (length * 10));
                }
            }
        }
    }

    public void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (this.mMiniKeyboardParent == this) {
            M.kbh = h;
            if (M.h != null && M.h.is(this)) {
                M.h.onSizeChanged(w, this.mKeys[0].height, oldw, oldh);
            }
        }
        if (this.mBuffer != null) {
            this.mBuffer.recycle();
        }
        this.mBuffer = null;
    }

    public void onDraw(Canvas c) {
        super.onDraw(c);
        try {
            if (this.mBuffer != null && this.mBuffer.isRecycled()) {
                this.mBuffer = null;
            }
            if (this.mDrawPending || this.mBuffer == null || this.mKeyboardChanged) {
                onBufferDraw();
            }
            c.drawBitmap(this.mBuffer, 0.0f, 0.0f, null);
            if (M.kid.is(R.xml.kbd_hw) && M.h != null && M.h.is(this)) {
                M.h.onDraw(c);
            }
        } catch (Throwable e) {
            M.l(e);
            M.ex();
        }
    }

    private void onBufferDraw() {
        if (this.mBuffer == null || this.mKeyboardChanged) {
            if (this.mBuffer == null || (this.mKeyboardChanged && !(this.mBuffer.getWidth() == getWidth() && this.mBuffer.getHeight() == getHeight()))) {
                try {
                    this.mBuffer = Bitmap.createBitmap(Math.max(1, getWidth()), Math.max(1, getHeight()), Config.ARGB_8888);
                    this.mBuffer.setDensity(M.dm.densityDpi);
                    this.mCanvas = new Canvas(this.mBuffer);
                } catch (Throwable th) {
                    M.msg(M.mIme, "Out of Memory :(");
                    return;
                }
            }
            invalidateAllKeys();
            this.mKeyboardChanged = false;
        }
        Canvas canvas = this.mCanvas;
        canvas.clipRect(this.mDirtyRect, Op.REPLACE);
        if (this.mKeyboard != null) {
            int hc;
            int ha;
            int h;
            Paint paint = this.mPaint;
            Drawable keyBackground = this.mKeyBackground;
            Rect clipRegion = this.mClipRegion;
            Rect padding = this.mPadding;
            int kbdPaddingLeft = getPaddingLeft();
            int kbdPaddingTop = getPaddingTop();
            Key[] keys = this.mKeys;
            Key invalidKey = this.mInvalidatedKey;
            boolean drawSingleKey = false;
            if (invalidKey != null && canvas.getClipBounds(clipRegion) && (invalidKey.x + kbdPaddingLeft) - 1 <= clipRegion.left && (invalidKey.y + kbdPaddingTop) - 1 <= clipRegion.top && ((invalidKey.x + invalidKey.width) + kbdPaddingLeft) + 1 >= clipRegion.right && ((invalidKey.y + invalidKey.height) + kbdPaddingTop) + 1 >= clipRegion.bottom) {
                drawSingleKey = true;
            }
            canvas.drawColor(0, Mode.CLEAR);
            if (isMini()) {
                canvas.drawColor((M.hlc & 16777215) | 285212672);
            }
            int hcl = M.hlc;
            if (this.mSymbolColorScheme == 1) {
                hc = M.dkr(M.hlc, 2, 5);
                hcl = -1;
                ha = 255;
            } else {
                if (M.tid == 0) {
                    hc = M.ltr(hcl);
                } else {
                    hc = hcl;
                }
                hc = M.ltr(hc);
                ha = 160;
            }
            boolean zw = false;
            int w = M.mWideKey ? 1 : ((M.dm.widthPixels * 300) / 160) / (getHeight() + 1);
            if (M.mWideKey) {
                h = 1;
            } else {
                h = canvas.getHeight() / 67;
            }
            int keyCount = keys.length;
            int i = 0;
            while (i < keyCount) {
                Key key = keys[i];
                boolean islp = M.isLP(key.codes[0]);
                if ((!drawSingleKey || invalidKey == key) && (key.width != 0 || islp)) {
                    String s;
                    int emj = M.kid.is(M.emj) ? M.emjLoad(key) : -1;
                    boolean ilm = false;
                    boolean rtl = false;
                    int labelSize1 = (key.height * M.fontSize) / 250;
                    boolean shf = M.shf();
                    String label = null;
                    CharSequence lo = key.label;
                    if (lo != null) {
                        switch (key.codes[0]) {
                            case -120:
                                if (!M.arConAble()) {
                                    if (!M.isCJ()) {
                                        if (M.mLC != M.ko) {
                                            label = M.sms;
                                            break;
                                        } else {
                                            label = M.hj();
                                            break;
                                        }
                                    }
                                    label = M.cp;
                                    break;
                                }
                                label = M.cas;
                                break;
                            case -119:
                                if (M.mLC != M.zh && !M.hw()) {
                                    if (M.isLatinC) {
                                        label = M.fd;
                                        break;
                                    }
                                }
                                if (((M.JF == 1 ? 1 : 0) ^ key.pressed) == 0) {
                                    label = "繁";
                                    break;
                                } else {
                                    label = "简";
                                    break;
                                }
                                break;
                            case R.styleable.LatinKeyboardBaseView_popupLayout /*10*/:
                                label = lo.toString();
                                break;
                            default:
                                if (islp) {
                                    label = M.reverse(M.dn(M.mIme.mLanguageSwitcher.getEnabledLanguages()[M.lp(key.codes[0])])).toString();
                                    rtl = M.nsd(label.charAt(0));
                                    key.width = getWidth() - (getPaddingLeft() * 2);
                                    int m = M.nlc();
                                    if (m > 4) {
                                        labelSize1 = (labelSize1 * 4) / m;
                                    }
                                    key.width /= m;
                                    key.x = key.width * (M.lp(key.codes[0]) % m);
                                    ilm = true;
                                } else {
                                    label = adjustCase(lo).toString();
                                }
                                if (M.isAK() && M.nm && !key.modifier && shf && key.popupCharacters != null) {
                                    label = Character.toString(key.popupCharacters.charAt(0));
                                }
                                if (M.mLC != M.mt) {
                                    s = M.cc(label.charAt(0));
                                    if (s != null) {
                                        label = s;
                                        break;
                                    }
                                }
                                break;
                        }
                        if (label != null && label.length() > 0) {
                            String l = M.scl(label.charAt(0));
                            if (l != null) {
                                zw = true;
                                label = l;
                            }
                        }
                    }
                    if (key.codes[0] == -1 && M.jfs1() && M.isAK()) {
                        label = M.JF == 1 ? "简" : "繁";
                    }
                    int w1 = M.dm.widthPixels / 3;
                    boolean shade = M.shd() && key.x > w1 - (key.width / 2) && key.x < (M.dm.widthPixels - w1) - key.width;
                    if (M.kHW(key)) {
                        paint.setColor(-872415232);
                        canvas.drawRect((float) key.x, (float) key.y, (float) key.width, (float) key.height, paint);
                    } else {
                        int i2;
                        canvas.translate((float) (key.x + kbdPaddingLeft), (float) (key.y + kbdPaddingTop));
                        if (keyBackground != null) {
                            Rect bounds = keyBackground.getBounds();
                            if (!(key.width == bounds.right && key.height == bounds.bottom)) {
                                keyBackground.setBounds(0, 0, key.width, key.height);
                            }
                            keyBackground.setBounds(w, h, key.width - (w * 2), key.height - (h * 2));
                            i2 = (key.modifier || shade) ? this.mSymbolColorScheme != 0 ? 96 : 128 : 250;
                            keyBackground.setAlpha(i2);
                            keyBackground.draw(canvas);
                        }
                        CharSequence pc = key.popupCharacters;
                        boolean shouldDrawIcon = true;
                        if (!(label == null || emj == 0)) {
                            int labelSize;
                            int l1 = label.length();
                            boolean bf = false;
                            if (l1 > 2 && key.codes.length < 2 && ((zw || pc == null || pc.length() < 2) && !M.ls1)) {
                                labelSize1 = (labelSize1 * 7) / 10;
                                bf = true;
                            } else if (this.mSymbolColorScheme != 0) {
                                bf = true;
                            }
                            M.sbf(paint, bf, label.charAt(l1 - 1));
                            if (ilm) {
                                labelSize = labelSize1;
                            } else {
                                labelSize = (M.fa() * labelSize1) / 100;
                            }
                            if (M.th == M.mLC && M.kid.is(R.xml.kbd_qwerty)) {
                                labelSize = (labelSize * 4) / 3;
                            }
                            int centerX = ((key.width + padding.left) - padding.right) / 2;
                            int centerY = ((key.height + padding.top) - padding.bottom) / 2;
                            float baseline = (float) (((key.height + centerY) / 2) - padding.bottom);
                            CharSequence hint = M.kid.is(R.xml.kbd_emj) ? null : M.hns(pc, i, key);
                            if (!(((!M.kid.is(R.xml.kbd_t9) || M.mLC != M.ja) && (!M.kid.is(R.xml.kbd_qw_er_ty) || M.mLC != M.ko)) || hint == null || key.codes[0] == 44 || key.codes[0] == 46)) {
                                baseline -= (float) (((key.height - padding.top) - padding.bottom) / 10);
                                paint.setTextSize((float) (labelSize / 2));
                                paint.clearShadowLayer();
                                if (key.modifier) {
                                    i2 = hcl;
                                } else {
                                    i2 = hc;
                                }
                                paint.setColor(i2);
                                paint.setAlpha(ha);
                                int l2 = pc.length();
                                if (l2 > 1) {
                                    canvas.drawText(Character.toString(M.toKata(shf, pc.charAt(1))), (float) ((padding.left + w) + (key.width / 10)), baseline, paint);
                                }
                                if (l2 > 2) {
                                    canvas.drawText(Character.toString(M.toKata(shf, pc.charAt(2))), (float) centerX, (float) ((padding.top + (key.height / 6)) + h), paint);
                                }
                                if (l2 > 3) {
                                    canvas.drawText(Character.toString(M.toKata(shf, pc.charAt(3))), (float) (((key.width - (key.width / 10)) - w) - padding.right), baseline, paint);
                                }
                                if (l2 > 4) {
                                    canvas.drawText(Character.toString(M.toKata(shf, pc.charAt(4))), (float) centerX, (float) (((key.height - padding.bottom) - h) - (key.height / 12)), paint);
                                }
                                hint = null;
                            }
                            paint.setTextSize((float) labelSize);
                            i2 = (key.modifier || shade) ? -1 : this.mKeyTextColor;
                            paint.setColor(i2);
                            paint.setAlpha(255);
                            float f = this.mShadowRadius;
                            i2 = (key.modifier || shade) ? -16777216 : this.mShadowColor;
                            paint.setShadowLayer(f, 0.0f, 0.0f, i2);
                            if (hint != null) {
                                baseline += (float) (key.height / 18);
                            }
                            if (rtl) {
                                M.rtld(canvas, label, 0, centerY - (labelSize / 2), key.width, key.height, paint);
                            } else {
                                canvas.drawText(label, (float) centerX, baseline, paint);
                            }
                            paint.clearShadowLayer();
                            if (hint != null) {
                                paint.setTextSize((float) ((labelSize1 * 4) / 7));
                                if (key.modifier) {
                                    i2 = hcl;
                                } else {
                                    i2 = hc;
                                }
                                paint.setColor(i2);
                                paint.setAlpha(ha);
                                canvas.drawText((String) hint, (float) (((key.width - (key.width / 5)) - w) - padding.right), (float) ((padding.top + (key.height / 7)) + h), paint);
                            }
                            shouldDrawIcon = shouldDrawLabelAndIcon(key) && label.length() < 2;
                        }
                        if (key.pressed) {
                            paint.setColor(M.hlc);
                            paint.setAlpha(136);
                            canvas.drawRect((float) ((padding.left / 2) + w), (float) ((padding.top / 2) + h), (float) ((key.width - w) - padding.right), (float) ((key.height - h) - padding.bottom), paint);
                        }
                        if (emj == 0 || (key.icon != null && shouldDrawIcon)) {
                            int drawableWidth;
                            int drawableHeight;
                            int drawableX;
                            int drawableY;
                            if (emj == 0) {
                                drawableWidth = (Math.min(key.width, (key.height * 2) / 3) * 3) / 5;
                                drawableHeight = drawableWidth;
                                drawableX = (((key.width + padding.left) - padding.right) - drawableWidth) / 2;
                                drawableY = (((key.height + padding.top) - padding.bottom) - drawableHeight) / 2;
                            } else {
                                drawableWidth = key.icon.getIntrinsicWidth();
                                drawableHeight = key.icon.getIntrinsicHeight();
                                drawableX = (((key.width + padding.left) - padding.right) - drawableWidth) / 2;
                                drawableY = (((key.height + padding.top) - padding.bottom) - drawableHeight) / 2;
                            }
                            canvas.translate((float) drawableX, (float) drawableY);
                            key.icon.setBounds(0, 0, drawableWidth, drawableHeight);
                            key.icon.draw(canvas);
                            canvas.translate((float) (-drawableX), (float) (-drawableY));
                        }
                        if (key.sticky) {
                            paint.setColor(M.hlc);
                            paint.setShadowLayer(2.0f, 0.0f, 0.0f, 1996488704);
                            if (key.on) {
                                canvas.drawCircle((float) ((key.width - (key.width / 5)) - w), (float) (padding.top + (key.height / 6)), (float) (key.height / 15), paint);
                            } else if (M.isLatinC) {
                                switch (M.ac1) {
                                    case -7:
                                        s = "^";
                                        break;
                                    case -6:
                                        s = "alt";
                                        break;
                                    default:
                                        s = null;
                                        break;
                                }
                                if (s != null) {
                                    canvas.drawText(s, (float) (((key.width - (key.width / 5)) - w) - padding.right), (float) ((padding.top + (key.height / 5)) + h), paint);
                                }
                            }
                        }
                        canvas.translate((float) ((-key.x) - kbdPaddingLeft), (float) ((-key.y) - kbdPaddingTop));
                    }
                }
                i++;
            }
            this.mInvalidatedKey = null;
            this.mDrawPending = false;
            this.mDirtyRect.setEmpty();
        }
    }

    private boolean isMini() {
        return this.mMiniKeyboardParent == this;
    }

    private void dismissKeyPreview() {
        Iterator i$ = this.mPointerTrackers.iterator();
        while (i$.hasNext()) {
            ((PointerTracker) i$.next()).updateKey(-1);
        }
        showPreview(-1, null);
    }

    public void showPreview(int keyIndex, PointerTracker tracker) {
        boolean hidePreviewOrShowSpaceKeyPreview = false;
        int oldKeyIndex = this.mOldPreviewKeyIndex;
        this.mOldPreviewKeyIndex = keyIndex;
        boolean isLanguageSwitchEnabled;
        if ((this.mKeyboard instanceof LatinKeyboard) && ((LatinKeyboard) this.mKeyboard).isLanguageSwitchEnabled()) {
            isLanguageSwitchEnabled = true;
        } else {
            isLanguageSwitchEnabled = false;
        }
        if (tracker == null || tracker.isSpaceKey(keyIndex) || tracker.isSpaceKey(oldKeyIndex)) {
            hidePreviewOrShowSpaceKeyPreview = true;
        }
        if ((oldKeyIndex != keyIndex && this.mShowPreview) || (hidePreviewOrShowSpaceKeyPreview && isLanguageSwitchEnabled && M.sld)) {
            if (keyIndex == -1) {
                this.mHandler.cancelPopupPreview();
                this.mHandler.dismissPreview((long) this.mDelayAfterPreview);
            } else if (tracker != null) {
                this.mHandler.popupPreview((long) this.mDelayBeforePreview, keyIndex, tracker);
            }
        }
    }

    private void showKey(int keyIndex, PointerTracker tracker) {
        try {
            Key key = tracker.getKey(keyIndex);
            if (key != null && !M.kHW(key)) {
                if (key.icon != null) {
                    this.mPreviewText.setCompoundDrawables(null, null, null, key.iconPreview != null ? key.iconPreview : key.icon);
                    this.mPreviewText.setText(null);
                } else {
                    this.mPreviewText.setCompoundDrawables(null, null, null, null);
                    CharSequence s = tracker.getPreviewText(key);
                    TextView textView = this.mPreviewText;
                    if (key.codes[0] != 10) {
                        s = adjustCase(s);
                    }
                    textView.setText(s);
                    if (key.label == null || key.label.length() <= 1 || key.codes.length >= 2) {
                        this.mPreviewText.setTextSize(0, (float) this.mPreviewTextSizeLarge);
                        this.mPreviewText.setTypeface(Typeface.DEFAULT);
                    } else {
                        this.mPreviewText.setTextSize(0, (float) ((M.kfs * M.fontSize) / 100));
                        this.mPreviewText.setTypeface(Typeface.DEFAULT_BOLD);
                    }
                    Typeface tf = M.xtf(key.label.charAt(0));
                    if (tf != null) {
                        this.mPreviewText.setTypeface(tf);
                    }
                }
                this.mPreviewText.measure(MeasureSpec.makeMeasureSpec(0, 0), MeasureSpec.makeMeasureSpec(0, 0));
                int popupWidth = Math.max(this.mPreviewText.getMeasuredWidth(), (key.width + this.mPreviewText.getPaddingLeft()) + this.mPreviewText.getPaddingRight());
                int popupHeight = this.mPreviewHeight;
                LayoutParams lp = this.mPreviewText.getLayoutParams();
                if (lp != null) {
                    lp.width = popupWidth;
                    lp.height = popupHeight;
                }
                int popupPreviewX = key.x - ((popupWidth - key.width) / 2);
                int popupPreviewY = (key.y - popupHeight) + this.mPreviewOffset;
                this.mHandler.cancelDismissPreview();
                if (this.mOffsetInWindow == null) {
                    this.mOffsetInWindow = new int[2];
                    getLocationInWindow(this.mOffsetInWindow);
                    int[] iArr = this.mOffsetInWindow;
                    iArr[0] = iArr[0] + this.mPopupPreviewOffsetX;
                    iArr = this.mOffsetInWindow;
                    iArr[1] = iArr[1] + this.mPopupPreviewOffsetY;
                    int[] windowLocation = new int[2];
                    getLocationOnScreen(windowLocation);
                    this.mWindowY = windowLocation[1];
                }
                this.mPreviewText.getBackground().setState(key.popupResId != 0 ? LONG_PRESSABLE_STATE_SET : EMPTY_STATE_SET);
                popupPreviewX += this.mOffsetInWindow[0];
                popupPreviewY += this.mOffsetInWindow[1];
                if (this.mWindowY + popupPreviewY < 0) {
                    if (key.x + key.width <= getWidth() / 2) {
                        popupPreviewX += (int) (((double) key.width) * 2.5d);
                    } else {
                        popupPreviewX -= (int) (((double) key.width) * 2.5d);
                    }
                    popupPreviewY += popupHeight;
                }
                if (M.jb && !M.sld) {
                    this.mPreviewPopup.dismiss();
                }
                if (this.mPreviewPopup.isShowing()) {
                    try {
                        this.mPreviewPopup.update(popupPreviewX, popupPreviewY, popupWidth, popupHeight);
                    } catch (Throwable th) {
                    }
                } else {
                    this.mPreviewPopup.setWidth(popupWidth);
                    this.mPreviewPopup.setHeight(popupHeight);
                    this.mPreviewPopup.showAtLocation(this.mMiniKeyboardParent, 0, popupPreviewX, popupPreviewY);
                }
                this.mPopupPreviewDisplayedY = popupPreviewY;
                this.mPreviewText.setVisibility(0);
            }
        } catch (Throwable th2) {
        }
    }

    public void invalidateAllKeys() {
        this.mDirtyRect.union(0, 0, getWidth(), getHeight());
        this.mDrawPending = true;
        postInvalidateDelayed(100);
    }

    public void invalidateKey(Key key) {
        if (key != null) {
            this.mInvalidatedKey = key;
            this.mDirtyRect.union(key.x + getPaddingLeft(), key.y + getPaddingTop(), (key.x + key.width) + getPaddingLeft(), (key.y + key.height) + getPaddingTop());
            onBufferDraw();
            invalidate(key.x + getPaddingLeft(), key.y + getPaddingTop(), (key.x + key.width) + getPaddingLeft(), (key.y + key.height) + getPaddingTop());
        }
    }

    private boolean openPopupIfRequired(int keyIndex, PointerTracker tracker) {
        boolean result = false;
        if (this.mPopupLayout != 0) {
            Key popupKey = tracker.getKey(keyIndex);
            if (popupKey != null) {
                result = onLongPress(popupKey);
                if (result) {
                    dismissKeyPreview();
                    this.mMiniKeyboardTrackerId = tracker.mPointerId;
                    tracker.setAlreadyProcessed();
                    this.mPointerQueue.remove(tracker);
                }
            }
        }
        return result;
    }

    private View inflateMiniKeyboardContainer(Key popupKey) {
        int popupKeyboardId = popupKey.popupResId;
        View container = ((LayoutInflater) getContext().getSystemService("layout_inflater")).inflate(this.mPopupLayout, null);
        if (container == null) {
            throw new NullPointerException();
        }
        KB keyboard;
        int i;
        LatinKeyboardBaseView miniKeyboard = (LatinKeyboardBaseView) container.findViewById(R.id.LatinKeyboardBaseView);
        miniKeyboard.setOnKeyboardActionListener(new OnKeyboardActionListener() {
            public void onKey(int primaryCode, int[] keyCodes, int x, int y) {
                if (primaryCode != -1) {
                    LatinKeyboardBaseView.this.mKeyboardActionListener.onKey(primaryCode, keyCodes, x, y);
                }
                LatinKeyboardBaseView.this.dismissPopupKeyboard();
            }

            public void onText(CharSequence text) {
                LatinKeyboardBaseView.this.mKeyboardActionListener.onText(text);
                LatinKeyboardBaseView.this.dismissPopupKeyboard();
            }

            public void onCancel() {
                LatinKeyboardBaseView.this.dismissPopupKeyboard();
            }

            public void swipeLeft() {
            }

            public void swipeRight() {
            }

            public void swipeUp() {
            }

            public void swipeDown() {
            }

            public void onPress(int primaryCode) {
                LatinKeyboardBaseView.this.mKeyboardActionListener.onPress(primaryCode);
            }

            public void onRelease(int primaryCode) {
                LatinKeyboardBaseView.this.mKeyboardActionListener.onRelease(primaryCode);
            }
        });
        miniKeyboard.mKeyDetector = new MiniKeyboardKeyDetector(this.mMiniKeyboardSlideAllowance);
        CharSequence pc = popupKey.popupCharacters;
        if (pc == null) {
            keyboard = new KB(getContext(), popupKeyboardId);
        } else if (M.jaT9(popupKeyboardId)) {
            keyboard = new KB(getContext(), popupKeyboardId);
            List<Key> ks = keyboard.getKeys();
            M.sk(ks, 4, pc, 0);
            M.sk(ks, 3, pc, 1);
            M.sk(ks, 1, pc, 2);
            M.sk(ks, 5, pc, 3);
            M.sk(ks, 7, pc, 4);
        } else {
            Context context = getContext();
            int nlc = (popupKey.codes[0] == 44 || M.spp(popupKey.label)) ? 5 : popupKey.codes[0] == 714 ? 6 : popupKey.codes[0] == 32 ? M.nlc() : (!M.eth || popupKey.modifier) ? 9 : 6;
            keyboard = new KB(context, popupKeyboardId, pc, nlc, getPaddingRight() + getPaddingLeft());
        }
        if (M.kid.is(R.xml.kbd_emj)) {
            i = 3;
        } else {
            i = 2;
        }
        M.skh(keyboard, popupKey, i);
        miniKeyboard.setKeyboard(keyboard, M.vc(popupKey));
        miniKeyboard.setPopupParent(this);
        container.measure(MeasureSpec.makeMeasureSpec(getWidth(), Integer.MIN_VALUE), MeasureSpec.makeMeasureSpec(M.dm.heightPixels, Integer.MIN_VALUE));
        return container;
    }

    protected boolean onLongPress(Key popupKey) {
        if (popupKey.popupResId == 0) {
            return false;
        }
        View container = (View) this.mMiniKeyboardCache.get(popupKey);
        if (container == null) {
            container = inflateMiniKeyboardContainer(popupKey);
            this.mMiniKeyboardCache.put(popupKey, container);
        }
        this.mMiniKeyboard = (LatinKeyboardBaseView) container.findViewById(R.id.LatinKeyboardBaseView);
        if (this.mWindowOffset == null) {
            this.mWindowOffset = new int[2];
            getLocationInWindow(this.mWindowOffset);
        }
        List<Key> miniKeys = this.mMiniKeyboard.getKeyboard().getKeys();
        int miniKeyWidth = miniKeys.size() > 0 ? ((Key) miniKeys.get(0)).width : 0;
        int popupX = (popupKey.x + this.mWindowOffset[0]) + getPaddingLeft();
        if (M.jaT9(popupKey.popupResId) || popupX >= getWidth() / 2) {
            popupX = ((popupX + miniKeyWidth) - container.getMeasuredWidth()) + container.getPaddingRight();
        } else {
            popupX = (popupX + (popupKey.width - miniKeyWidth)) - container.getPaddingLeft();
        }
        int popupY = ((popupKey.y + this.mWindowOffset[1]) + ((M.isLand() ? 0 : M.bgb) + getPaddingTop())) - container.getMeasuredHeight();
        int x = popupX;
        int y = popupY;
        int adjustedX = x;
        if (M.jaT9(popupKey.popupResId)) {
            y += popupKey.height * 2;
            adjustedX += popupKey.width;
            popupY += container.getPaddingBottom();
        } else if (x < 0) {
            adjustedX = 0;
        } else if (x > getMeasuredWidth() - container.getMeasuredWidth()) {
            adjustedX = getMeasuredWidth() - container.getMeasuredWidth();
        }
        if ((getTop() - y) * 3 > getHeight()) {
            y = (getTop() + this.mWindowOffset[1]) - (getHeight() / 2);
        }
        if (y < 0 && M.mIme.isFullscreenMode()) {
            y = 0;
        }
        this.mMiniKeyboardOriginX = (container.getPaddingLeft() + adjustedX) - this.mWindowOffset[0];
        this.mMiniKeyboardOriginY = (container.getPaddingTop() + y) - this.mWindowOffset[1];
        this.mMiniKeyboard.setShifted(isShifted());
        this.mMiniKeyboard.setPreviewEnabled(false);
        this.mMiniKeyboardPopup.setContentView(container);
        this.mMiniKeyboardPopup.setWidth(container.getMeasuredWidth());
        this.mMiniKeyboardPopup.setHeight(container.getMeasuredHeight());
        this.mMiniKeyboardPopup.setAnimationStyle(0);
        this.mMiniKeyboardPopup.setClippingEnabled(false);
        long eventTime = SystemClock.uptimeMillis();
        this.mMiniKeyboardPopupTime = eventTime;
        MotionEvent downEvent = generateMiniKeyboardMotionEvent(0, (popupKey.width / 2) + popupKey.x, (popupKey.height / 2) + popupKey.y, eventTime);
        this.mMiniKeyboard.onTouchEvent(downEvent);
        downEvent.recycle();
        invalidateAllKeys();
        try {
            this.mMiniKeyboardPopup.showAtLocation(this, 0, adjustedX, y);
        } catch (Throwable th) {
            M.ex();
        }
        return true;
    }

    private boolean shouldDrawLabelAndIcon(Key key) {
        return (key.sticky || key.codes[0] == 32) ? false : true;
    }

    private boolean isLatinF1Key(Key key) {
        return (this.mKeyboard instanceof LatinKeyboard) && ((LatinKeyboard) this.mKeyboard).isF1Key(key);
    }

    private static boolean isAsciiDigit(char c) {
        return c < 128 && Character.isDigit(c);
    }

    private MotionEvent generateMiniKeyboardMotionEvent(int action, int x, int y, long eventTime) {
        return MotionEvent.obtain(this.mMiniKeyboardPopupTime, eventTime, action, (float) (x - this.mMiniKeyboardOriginX), (float) (y - this.mMiniKeyboardOriginY), 0);
    }

    private PointerTracker getPointerTracker(int id) {
        ArrayList<PointerTracker> pointers = this.mPointerTrackers;
        Key[] keys = this.mKeys;
        OnKeyboardActionListener listener = this.mKeyboardActionListener;
        for (int i = pointers.size(); i <= id; i++) {
            PointerTracker tracker = new PointerTracker(i, this.mHandler, this.mKeyDetector, this, getResources());
            if (keys != null) {
                tracker.setKeyboard(keys, this.mKeyHysteresisDistance);
            }
            if (listener != null) {
                tracker.setOnKeyboardActionListener(listener);
            }
            pointers.add(tracker);
        }
        return (PointerTracker) pointers.get(id);
    }

    @TargetApi(8)
    public boolean onTouchEvent(MotionEvent me) {
        int action;
        int x;
        int y;
        int pointerCount = 1;
        int id = 0;
        try {
            pointerCount = me.getPointerCount();
            action = me.getActionMasked();
            int index = me.getActionIndex();
            id = me.getPointerId(index);
            x = (int) me.getX(index);
            y = (int) me.getY(index);
        } catch (Throwable th) {
            action = me.getAction();
            x = (int) me.getX();
            y = (int) me.getY();
        }
        if (!this.mHasDistinctMultitouch && pointerCount > 1 && this.mOldPointerCount > 1) {
            return true;
        }
        long eventTime = me.getEventTime();
        if (this.mMiniKeyboard != null) {
            int miniKeyboardPointerIndex = me.findPointerIndex(this.mMiniKeyboardTrackerId);
            if (miniKeyboardPointerIndex >= 0 && miniKeyboardPointerIndex < pointerCount) {
                MotionEvent translated = generateMiniKeyboardMotionEvent(action, (int) me.getX(miniKeyboardPointerIndex), (int) me.getY(miniKeyboardPointerIndex), eventTime);
                this.mMiniKeyboard.onTouchEvent(translated);
                translated.recycle();
            }
            return true;
        }
        if (this.g != null) {
            switch (AnonymousClass2.$SwitchMap$com$klye$ime$latin$LatinKeyboardBaseView$E[this.g.detect(me).ordinal()]) {
                case 1:
                    clpt();
                    M.mIme.hg(M.gr);
                    return true;
                case 2:
                    clpt();
                    M.mIme.hg(M.gl);
                    return true;
                case 3:
                    clpt();
                    M.mIme.hg(M.gt);
                    return true;
                case 4:
                    clpt();
                    M.mIme.hg(M.gb);
                    return true;
                case 5:
                    clpt();
                    M.mIme.hg(M.gtr);
                    return true;
                case 6:
                    clpt();
                    M.mIme.hg(M.gtl);
                    return true;
                case 7:
                    clpt();
                    M.mIme.hg(M.gbr);
                    return true;
                case R.styleable.LatinKeyboardBaseView_keyHysteresisDistance /*8*/:
                    clpt();
                    M.mIme.hg(M.gbl);
                    return true;
            }
        }
        if (M.kid.is(R.xml.kbd_hw) && M.h != null && M.h.is(this) && y < M.h.h() && M.h.onTouchEvent(me)) {
            return true;
        }
        PointerTracker tracker;
        if (this.mHandler.isInKeyRepeat()) {
            if (action == 2) {
                return true;
            }
            tracker = getPointerTracker(id);
            if (pointerCount > 1 && !tracker.isModifier()) {
                this.mHandler.cancelKeyRepeatTimer();
            }
        }
        if (this.mHasDistinctMultitouch) {
            if (action != 2) {
                tracker = getPointerTracker(id);
                switch (action) {
                    case 0:
                    case 5:
                        onDownEvent(tracker, x, y, eventTime);
                        break;
                    case 1:
                    case 6:
                        onUpEvent(tracker, x, y, eventTime);
                        break;
                    case 3:
                        onCancelEvent(tracker, x, y, eventTime);
                        break;
                }
            }
            for (int i = 0; i < pointerCount; i++) {
                int x1;
                float y2;
                tracker = getPointerTracker(id);
                try {
                    x1 = (int) me.getX(i);
                    y2 = me.getY(i);
                } catch (Throwable th2) {
                    x1 = (int) me.getX();
                    y2 = me.getY();
                }
                tracker.onMoveEvent(x1, (int) y2, eventTime);
            }
            return true;
        }
        tracker = getPointerTracker(0);
        int oldPointerCount = this.mOldPointerCount;
        if (pointerCount == 1 && oldPointerCount == 2) {
            tracker.onDownEvent(x, y, eventTime);
        } else if (pointerCount == 2 && oldPointerCount == 1) {
            tracker.onUpEvent(tracker.getLastX(), tracker.getLastY(), eventTime);
        } else if (pointerCount == 1 && oldPointerCount == 1) {
            tracker.onTouchEvent(action, x, y, eventTime);
        } else {
            Log.w(TAG, "Unknown touch panel behavior: pointer count is " + pointerCount + " (old " + oldPointerCount + ")");
        }
        this.mOldPointerCount = pointerCount;
        return true;
    }

    private void onDownEvent(PointerTracker tracker, int x, int y, long eventTime) {
        if (tracker.isOnModifierKey(x, y)) {
            this.mPointerQueue.releaseAllPointersExcept(null, eventTime);
        }
        tracker.onDownEvent(x, y, eventTime);
        this.mPointerQueue.add(tracker);
    }

    private void onUpEvent(PointerTracker tracker, int x, int y, long eventTime) {
        if (tracker.isModifier()) {
            this.mPointerQueue.releaseAllPointersExcept(tracker, eventTime);
        } else if (this.mPointerQueue.lastIndexOf(tracker) < 0) {
            Log.w(TAG, "onUpEvent: corresponding down event not found for pointer " + tracker.mPointerId);
        }
        tracker.onUpEvent(x, y, eventTime);
        this.mPointerQueue.remove(tracker);
    }

    private void onCancelEvent(PointerTracker tracker, int x, int y, long eventTime) {
        tracker.onCancelEvent(x, y, eventTime);
        this.mPointerQueue.remove(tracker);
    }

    protected void swipeRight() {
        this.mKeyboardActionListener.swipeRight();
    }

    protected void swipeLeft() {
        this.mKeyboardActionListener.swipeLeft();
    }

    protected void swipeUp() {
        this.mKeyboardActionListener.swipeUp();
    }

    protected void swipeDown() {
        this.mKeyboardActionListener.swipeDown();
    }

    public void closing() {
        try {
            this.mPreviewPopup.dismiss();
        } catch (Throwable th) {
        }
        this.mHandler.cancelAllMessages();
        dismissPopupKeyboard();
        this.mBuffer = null;
        this.mCanvas = null;
        this.mMiniKeyboardCache.clear();
    }

    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        closing();
    }

    void dismissPopupKeyboard() {
        if (this.mMiniKeyboardPopup.isShowing()) {
            try {
                this.mMiniKeyboardPopup.dismiss();
            } catch (Throwable th) {
            }
            this.mMiniKeyboard = null;
            this.mMiniKeyboardOriginX = 0;
            this.mMiniKeyboardOriginY = 0;
            invalidateAllKeys();
        }
    }

    public boolean handleBack() {
        if (!this.mMiniKeyboardPopup.isShowing()) {
            return false;
        }
        dismissPopupKeyboard();
        return true;
    }
}
