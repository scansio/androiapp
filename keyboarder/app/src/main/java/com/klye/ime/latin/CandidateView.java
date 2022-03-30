package com.klye.ime.latin;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.MeasureSpec;
import android.widget.PopupWindow;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CandidateView extends View {
    private static final int MAX_SUGGESTIONS = 132;
    private static final int OUT_OF_BOUNDS_WORD_INDEX = -1;
    private static final int OUT_OF_BOUNDS_X_COORD = -1;
    private static final int SCROLL_PIXELS = 20;
    private static final int X_GAP = 10;
    private static final int mColorNormal = -1;
    private List<CharSequence> list;
    private CharSequence mAddToDictionaryHint;
    private Rect mBgPadding;
    private int mCurrentWordIndex;
    private final int mDescent;
    private Drawable mDivider;
    private final GestureDetector mGestureDetector;
    private boolean mHaveMinimalSuggestion;
    private int mMinTouchableWidth;
    private int mPopupPreviewX;
    private int mPopupPreviewY;
    private final PopupWindow mPreviewPopup;
    private final TextView mPreviewText;
    private boolean mScrolled;
    private int mSelectedIndex;
    private CharSequence mSelectedString;
    private LatinIME mService;
    private boolean mShowingAddToDictionary;
    private boolean mShowingCompletions;
    private int mTargetScrollX;
    private int mTotalWidth;
    private int mTouchX = -1;
    private boolean mTypedWordValid;
    private final int[] mWordX = new int[133];
    private RectF rt = new RectF();

    private class CandidateStripGestureListener extends SimpleOnGestureListener {
        private final int mTouchSlopSquare;

        public CandidateStripGestureListener(int touchSlop) {
            this.mTouchSlopSquare = touchSlop * touchSlop;
        }

        public void onLongPress(MotionEvent me) {
            if (CandidateView.this.list != null && CandidateView.this.list.size() > 0) {
                CandidateView.this.longPressFirstWord();
            }
        }

        public boolean onDown(MotionEvent e) {
            CandidateView.this.mScrolled = false;
            return false;
        }

        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            if (!CandidateView.this.mScrolled) {
                int deltaX = (int) (e2.getX() - e1.getX());
                int deltaY = (int) (e2.getY() - e1.getY());
                if ((deltaX * deltaX) + (deltaY * deltaY) >= this.mTouchSlopSquare) {
                    CandidateView.this.mScrolled = true;
                }
                return true;
            }
            int width = CandidateView.this.getWidth();
            CandidateView.this.mScrolled = true;
            int scrollX = CandidateView.this.getScrollX() + ((int) distanceX);
            if (scrollX < 0) {
                scrollX = 0;
            }
            if (distanceX > 0.0f && scrollX + width > CandidateView.this.mTotalWidth) {
                scrollX -= (int) distanceX;
            }
            CandidateView.this.mTargetScrollX = scrollX;
            CandidateView.this.scrollTo(scrollX, CandidateView.this.getScrollY());
            CandidateView.this.hidePreview();
            CandidateView.this.invalidate();
            return true;
        }
    }

    public CandidateView(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater inflate = (LayoutInflater) context.getSystemService("layout_inflater");
        Resources res = context.getResources();
        this.mPreviewPopup = new PopupWindow(context);
        this.mPreviewText = (TextView) inflate.inflate(R.layout.candidate_preview, null);
        this.mPreviewPopup.setWindowLayoutMode(-2, -2);
        this.mPreviewPopup.setContentView(this.mPreviewText);
        this.mPreviewPopup.setBackgroundDrawable(null);
        this.mPreviewPopup.setAnimationStyle(R.style.KeyPreviewAnimation);
        this.mDivider = res.getDrawable(R.drawable.keyboard_suggest_strip_divider);
        this.mAddToDictionaryHint = res.getString(R.string.hint_add_to_dictionary);
        M.hlp.setColor(-1);
        this.mDescent = (int) M.hlp.descent();
        this.mMinTouchableWidth = (int) res.getDimension(R.dimen.candidate_min_touchable_width);
        if (M.dm.widthPixels > 800) {
            this.mMinTouchableWidth = (this.mMinTouchableWidth * M.dm.widthPixels) / 800;
        }
        this.mGestureDetector = new GestureDetector(new CandidateStripGestureListener(this.mMinTouchableWidth));
        setWillNotDraw(false);
        setHorizontalScrollBarEnabled(false);
        setVerticalScrollBarEnabled(false);
        scrollTo(0, getScrollY());
    }

    public void setService(LatinIME listener) {
        this.mService = listener;
    }

    public int computeHorizontalScrollRange() {
        return this.mTotalWidth;
    }

    private int ww(int i) {
        return this.mWordX[i + 1] - this.mWordX[i];
    }

    protected void onDraw(Canvas canvas) {
        if (this.list != null) {
            int count;
            int l;
            if (canvas != null) {
                super.onDraw(canvas);
                if (M.cbc != 0) {
                    canvas.drawColor(M.cbc);
                }
            }
            this.mTotalWidth = 0;
            int height = getHeight();
            if (this.mBgPadding == null) {
                this.mBgPadding = new Rect(0, 0, 0, 0);
                if (getBackground() != null) {
                    getBackground().getPadding(this.mBgPadding);
                }
                this.mDivider.setBounds(0, 0, this.mDivider.getIntrinsicWidth(), this.mDivider.getIntrinsicHeight());
            }
            if (this.list == null) {
                count = 0;
            } else {
                count = Math.min(this.list.size(), MAX_SUGGESTIONS);
            }
            Rect bgPadding = this.mBgPadding;
            Paint paint = M.hlp;
            int touchX = this.mTouchX;
            int scrollX = getScrollX();
            boolean scrolled = this.mScrolled;
            boolean typedWordValid = this.mTypedWordValid;
            int y = ((int) ((((float) height) + paint.getTextSize()) - ((float) this.mDescent))) / 2;
            boolean existsAutoCompletion = false;
            Typeface tf = null;
            if (M.xtf && M.mnf) {
                if (count > 0) {
                    try {
                        char charAt = M.eth ? 4608 : M.djv ? 40960 : ((CharSequence) this.list.get(0)).charAt(0);
                        tf = M.xtf(charAt);
                    } catch (Throwable th) {
                    }
                } else {
                    tf = null;
                }
                if (tf != null) {
                    paint.setTypeface(tf);
                }
            }
            int x = 0;
            int i = 0;
            while (i < count) {
                CharSequence s = (CharSequence) this.list.get(i);
                if (s != null) {
                    l = s.length();
                    if (l != 0) {
                        CharSequence s2;
                        if (l > 32) {
                            l = 32;
                            s = s.subSequence(0, 32);
                        }
                        char c = s.charAt(0);
                        if (M.zt == -1) {
                            if (c != 8203) {
                                s2 = M.ime(s, false);
                            }
                            s2 = s;
                        } else {
                            String s22 = s.toString();
                            if (M.pyt((CharSequence) s22, l)) {
                                s2 = M.pyt(s22);
                            }
                            s2 = s;
                        }
                        l = s2.length();
                        paint.setColor(-1);
                        if (isl() || M.zt != -1 || M.mLC == M.ja || M.mLC == M.ko) {
                            paint.setColor(M.hlc);
                        } else if (this.mHaveMinimalSuggestion && ((i == 1 && !typedWordValid) || (i == 0 && typedWordValid))) {
                            if (tf == null) {
                                paint.setTypeface(Typeface.DEFAULT_BOLD);
                            }
                            paint.setColor(M.ltr(M.hlc));
                            existsAutoCompletion = true;
                        } else if (i != 0) {
                            paint.setColor(M.hlc);
                        }
                        int wordWidth = ww(i);
                        if (wordWidth <= 0) {
                            wordWidth = Math.max(this.mMinTouchableWidth, ((int) paint.measureText(s2, 0, l)) + SCROLL_PIXELS);
                        }
                        this.mWordX[i] = x;
                        if (touchX != -1 && !scrolled && touchX + scrollX >= x && touchX + scrollX < x + wordWidth) {
                            if (!(canvas == null || this.mShowingAddToDictionary)) {
                                canvas.translate((float) x, 0.0f);
                                canvas.drawRect(0.0f, (float) bgPadding.top, (float) wordWidth, (float) height, paint);
                                canvas.translate((float) (-x), 0.0f);
                            }
                            this.mSelectedString = (CharSequence) this.list.get(i);
                            this.mSelectedIndex = i;
                        }
                        if (c == 8203 && canvas != null) {
                            canvas.drawRect((float) (x + 2), (float) bgPadding.top, (float) ((x + wordWidth) - 2), (float) (bgPadding.top + 5), M.lbp);
                        }
                        if (canvas != null) {
                            if (M.nsd(c)) {
                                M.rtld(canvas, M.reverse(s2), x, (height - y) / 2, wordWidth, height, paint);
                            } else {
                                canvas.drawText(s2, 0, l, (float) ((wordWidth / 2) + x), (float) y, paint);
                                s = s2;
                            }
                            paint.setColor(M.hlc);
                            canvas.translate((float) (x + wordWidth), 0.0f);
                            if (!(this.mShowingAddToDictionary && i == 1)) {
                                this.mDivider.draw(canvas);
                            }
                            canvas.translate((float) ((-x) - wordWidth), 0.0f);
                        } else {
                            s = s2;
                        }
                        if (tf == null) {
                            paint.setTypeface(Typeface.DEFAULT);
                        }
                        x += wordWidth;
                    }
                }
                i++;
            }
            this.mService.onAutoCompletionStateChanged(existsAutoCompletion);
            this.mTotalWidth = x;
            if (this.mTargetScrollX != scrollX) {
                scrollToTarget();
            }
            if (M.sha && canvas != null) {
                String ad = "★App of the day★";
                l = "★App of the day★".length();
                paint.setColor(M.hlc);
                float textWidth = (paint.measureText("★App of the day★", 0, l) * 14.0f) / 13.0f;
                int w = canvas.getWidth();
                this.rt.top = (float) (y / 4);
                this.rt.left = (((float) w) - this.rt.top) - textWidth;
                this.rt.bottom = (float) ((y * 5) / 4);
                this.rt.right = ((float) w) - this.rt.top;
                canvas.drawRoundRect(this.rt, 5.0f, 5.0f, paint);
                paint.setColor(-16777216);
                canvas.drawText("★App of the day★", this.rt.left + (textWidth / 2.0f), (float) y, paint);
            }
        }
    }

    public boolean isl() {
        return is(M.psbl) || is(M.bgl) || is(M.dmn2);
    }

    private void scrollToTarget() {
        int scrollX = getScrollX();
        if (this.mTargetScrollX > scrollX) {
            scrollX += SCROLL_PIXELS;
            if (scrollX >= this.mTargetScrollX) {
                scrollTo(this.mTargetScrollX, getScrollY());
                requestLayout();
            } else {
                scrollTo(scrollX, getScrollY());
            }
        } else {
            scrollX -= 20;
            if (scrollX <= this.mTargetScrollX) {
                scrollTo(this.mTargetScrollX, getScrollY());
                requestLayout();
            } else {
                scrollTo(scrollX, getScrollY());
            }
        }
        invalidate();
    }

    public void setSuggestions(List<CharSequence> suggestions, boolean completions, boolean typedWordValid, boolean haveMinimalSuggestion) {
        clear();
        this.list = suggestions;
        this.mShowingCompletions = completions;
        this.mTypedWordValid = typedWordValid;
        scrollTo(0, getScrollY());
        this.mTargetScrollX = 0;
        this.mHaveMinimalSuggestion = haveMinimalSuggestion;
        onDraw(null);
        invalidate();
        requestLayout();
    }

    public boolean isShowingAddToDictionaryHint() {
        return this.mShowingAddToDictionary;
    }

    public void showAddToDictionaryHint(CharSequence word) {
        ArrayList<CharSequence> suggestions = new ArrayList();
        suggestions.add(word);
        suggestions.add(this.mAddToDictionaryHint);
        setSuggestions(suggestions, false, false, false);
        this.mShowingAddToDictionary = true;
    }

    public boolean dismissAddToDictionaryHint() {
        if (!this.mShowingAddToDictionary) {
            return false;
        }
        clear();
        return true;
    }

    CharSequence pick1stSuggestion() {
        return (this.list == null || this.list.size() <= 0) ? null : (CharSequence) this.list.get(0);
    }

    public void clear() {
        this.list = null;
        this.mTouchX = -1;
        this.mSelectedString = null;
        this.mSelectedIndex = -1;
        this.mShowingAddToDictionary = false;
        invalidate();
        Arrays.fill(this.mWordX, 0);
    }

    public boolean onTouchEvent(MotionEvent me) {
        if (!this.mGestureDetector.onTouchEvent(me)) {
            int action = me.getAction();
            int x = (int) me.getX();
            int y = (int) me.getY();
            this.mTouchX = x;
            switch (action) {
                case 0:
                    invalidate();
                    break;
                case 1:
                    if (M.sha) {
                        M.sha = false;
                        if (((float) x) > this.rt.left) {
                            M.adi(this.mService);
                            M.sa(this.mService, M.wp("http://honsosearch.appspot.com/a/r" + M.rot + ".html"));
                            M.shc = 88;
                        }
                    }
                    if (!(this.mScrolled || this.mSelectedString == null)) {
                        if (this.mShowingAddToDictionary) {
                            longPressFirstWord();
                        } else {
                            if (!this.mShowingCompletions) {
                                TextEntryState.acceptedSuggestion((CharSequence) this.list.get(0), this.mSelectedString);
                            }
                            this.mService.pickSuggestionManually(this.mSelectedIndex, this.mSelectedString);
                        }
                    }
                    this.mSelectedString = null;
                    this.mSelectedIndex = -1;
                    requestLayout();
                    hidePreview();
                    invalidate();
                    break;
                case 2:
                    if (y <= 0 && this.mSelectedString != null) {
                        if (!this.mShowingCompletions) {
                            TextEntryState.acceptedSuggestion((CharSequence) this.list.get(0), this.mSelectedString);
                        }
                        this.mService.pickSuggestionManually(this.mSelectedIndex, this.mSelectedString);
                        this.mSelectedString = null;
                        this.mSelectedIndex = -1;
                        break;
                    }
            }
        }
        return true;
    }

    private void hidePreview() {
        this.mTouchX = -1;
        this.mCurrentWordIndex = -1;
        this.mPreviewPopup.dismiss();
    }

    private void showPreview(int wordIndex, String altText) {
        int oldWordIndex = this.mCurrentWordIndex;
        this.mCurrentWordIndex = wordIndex;
        if (oldWordIndex != this.mCurrentWordIndex || altText != null) {
            if (wordIndex == -1) {
                hidePreview();
                return;
            }
            CharSequence word;
            if (altText != null) {
                word = altText;
            } else {
                word = (CharSequence) this.list.get(wordIndex);
            }
            this.mPreviewText.setText(word);
            this.mPreviewText.measure(MeasureSpec.makeMeasureSpec(0, 0), MeasureSpec.makeMeasureSpec(0, 0));
            int wordWidth = (int) (M.hlp.measureText(word, 0, word.length()) + 20.0f);
            int popupWidth = (this.mPreviewText.getPaddingLeft() + wordWidth) + this.mPreviewText.getPaddingRight();
            int popupHeight = this.mPreviewText.getMeasuredHeight();
            this.mPopupPreviewX = ((this.mWordX[wordIndex] - this.mPreviewText.getPaddingLeft()) - getScrollX()) + ((ww(wordIndex) - wordWidth) / 2);
            this.mPopupPreviewY = (-popupHeight) * 2;
            int[] offsetInWindow = new int[2];
            getLocationInWindow(offsetInWindow);
            if (this.mPreviewPopup.isShowing()) {
                this.mPreviewPopup.update(this.mPopupPreviewX, this.mPopupPreviewY + offsetInWindow[1], popupWidth, popupHeight);
            } else {
                this.mPreviewPopup.setWidth(popupWidth);
                this.mPreviewPopup.setHeight(popupHeight);
                this.mPreviewPopup.showAtLocation(this, 0, this.mPopupPreviewX, this.mPopupPreviewY + offsetInWindow[1]);
            }
            this.mPreviewText.setVisibility(0);
        }
    }

    private void longPressFirstWord() {
        showPreview(0, this.mService.addWordToDictionary(this.mSelectedString));
        this.mSelectedString = null;
    }

    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        hidePreview();
    }

    public boolean next() {
        if (this.list == null) {
            return false;
        }
        if (this.mTargetScrollX == this.mTotalWidth - M.dm.widthPixels) {
            this.mTargetScrollX = 0;
        } else {
            this.mTargetScrollX += (M.dm.widthPixels * 3) / 4;
            if (this.mTargetScrollX + M.dm.widthPixels > this.mTotalWidth) {
                this.mTargetScrollX = this.mTotalWidth - M.dm.widthPixels;
            }
        }
        if (this.mTargetScrollX < 0) {
            return false;
        }
        scrollToTarget();
        return true;
    }

    public boolean is(List<CharSequence> s) {
        return s == this.list;
    }
}
