package com.klye.ime.latin;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Paint.FontMetrics;
import android.graphics.Paint.Style;
import android.graphics.Rect;
import android.os.Handler;
import android.os.Message;
import android.os.Process;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class H implements Runnable {
    protected static final int DoneRec = 0;
    public static final int eMaxPt = 1600;
    static int fhch;
    static Paint p = new Paint(1);
    static Paint p2 = new Paint(1);
    public Candy candy;
    private char[] defChr;
    private FontMetrics fm2;
    private FontMetrics fm4;
    private Handler hRefresh;
    Method m1;
    Method m2;
    Method m3;
    Method m4;
    Method m5;
    private Paint mBitmapPaint;
    private GestureDetector mGestureDetector;
    private int mTargetScrollX;
    private int mTargetScrollY;
    private EMode mode;
    public int n;
    private int nPad;
    boolean old;
    private Paint p3;
    private Paint p4;
    private int pi;
    private float[] pt;
    private LatinKeyboardBaseView pv;
    private Rect rt;
    private int sPad;
    EState state;
    private String[] sug;
    private Thread thread;
    private float xRem;
    private float yRem;
    private KanjiBoard[] yb;

    private class Candy {
        int b;
        int cH;
        int cW;
        int h;
        private char[] list;
        int nCol;
        int nRow;
        int pad;
        int pg;
        int r;

        private Candy() {
            this.list = new char[]{25163, 23531, 36664, 20837, 9670, 25163, 26360, 12365, 20837, 21147, 54596, 44592, 51077, 47141, 9670, 25163, 20889, 36755, 20837, 9670, 28204, 35430, 29256, 9670, 9670, 12505, 12540, 12479, 29256, 9670, 48288, 53440, 9670, 9670, 9670, 9670, 9670, 9670, 9670, 9670, 9670, 28450, 26360, 9670, 9670, 9670, 9670, 9670, 9670, 9670, 9670, 9670, 28450, 26360, 9670, 9670, 9670, 9670, 9670, 9670, 9670, 28450, 26360, 9670, 9670, 9670, 9670, 9670, 9670, 9670, 9670, 9670, 28450, 26360, 9670, 9670, 9670, 9670, 9670, 9670, 9670, 9670, 9670, 9670, 9670, 9670, 9670, 9670, 9670, 9670, 9670, 9670, 9670, 9670, 9670, 9670, 9670, 9670, 9670, 9670, 25163, 23531, 36664, 20837, 9670, 25163, 26360, 12365, 20837, 21147, 54596, 44592, 51077, 47141, 9670, 25163, 20889, 36755, 20837, 9670, 28204, 35430, 29256, 9670, 9670, 12505, 12540, 12479, 29256, 9670, 48288, 53440, 9670, 9670, 9670, 9670, 9670, 9670, 9670, 9670, 9670, 28450, 26360, 9670, 9670, 9670, 9670, 9670, 9670, 9670, 9670, 9670, 28450, 26360, 9670, 9670, 9670, 9670, 9670, 9670, 9670, 28450, 26360, 9670, 9670, 9670, 9670, 9670, 9670, 9670, 9670, 9670, 28450, 26360, 9670, 9670, 9670, 9670, 9670, 9670, 9670, 9670, 9670, 9670, 9670, 9670, 9670, 9670, 9670, 9670, 9670, 9670, 9670, 9670, 9670, 9670, 9670, 9670, 9670, 9670};
            this.nRow = 5;
            this.pg = 0;
            this.b = 1;
            this.pad = 4;
        }

        /* synthetic */ Candy(H x0, AnonymousClass1 x1) {
            this();
        }

        private void pgDown(int i) {
            this.pg += i;
            if (this.pg < 0) {
                this.pg = 0;
            }
            if (9670 == this.list[(this.nCol * this.nRow) * this.pg]) {
                this.pg--;
            }
            if (this.pg < 0) {
                this.pg = 0;
            }
            H.this.mTargetScrollY = 0;
            H.this.invalidate();
        }

        protected void onSizeChanged(int w, int h1, int oldw, int oldh) {
            this.h = h1;
            this.cH = this.h / this.nRow;
            this.cW = H.this.yb[0].xBM / 2;
            this.r = this.h % this.nRow;
            if (this.cW == 0) {
                this.cW = 1;
            }
            this.nCol = H.this.yb[0].xBM / this.cW;
            if (this.nCol == 0) {
                this.nCol = 1;
            }
            this.cW = H.this.yb[0].xBM / this.nCol;
            if (this.nCol < 1) {
                this.nCol = 1;
            }
            if (this.nCol > 10) {
                this.nCol = 10;
            }
        }

        private void onDraw(Canvas c) {
            int shade = this.nCol < 4 ? 6 : 1;
            int i = 0;
            while (i < this.nRow * 2) {
                try {
                    for (int j = 0; j < this.nCol; j++) {
                        int s = (this.nRow * j) + i;
                        int l = ((this.nCol - 1) - j) * this.cW;
                        int t = (((this.cH * i) + (this.r / 2)) + this.pad) - H.this.mTargetScrollY;
                        int a = 38 - ((i + j) * shade);
                        if (a < 3) {
                            a = 3;
                        }
                        H.this.p3.setAlpha(a);
                        s += (this.nCol * this.nRow) * this.pg;
                        c.drawRect((float) (this.b + l), (float) (this.b + t), (float) ((this.cW + l) - this.b), (float) ((this.cH + t) - this.b), H.this.p3);
                        H.p2.setColor(H.charColor(this.list[s]));
                        c.drawText(this.list, s, 1, (float) ((this.cW / 2) + l), (float) (((H.fhch + this.cH) / 2) + t), H.p2);
                    }
                    i++;
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
            }
            H.this.p3.setAlpha(40);
        }

        private int x2i(int x, int sz) {
            return (x - this.pad) / sz;
        }

        private int pt2i(int x, int y) {
            return (((this.nCol * this.nRow) * this.pg) + (((this.nCol - x2i(x, this.cW)) - 1) * this.nRow)) + x2i(y, this.cH);
        }

        private char get(int x, int y) {
            int i = pt2i(x, y);
            if (i > 499) {
                i = 499;
            }
            return this.list[i];
        }

        public String[] xa() {
            StringBuilder sb = new StringBuilder();
            int i = 0;
            while (i < this.list.length && this.list[i] != 9670) {
                if (M.isLatin(this.list[i])) {
                    sb.append(this.list[i]);
                }
                i++;
            }
            int l = sb.length();
            String[] s = l > 0 ? new String[l] : null;
            for (i = 0; i < l; i++) {
                s[i] = sb.substring(i, i + 1);
            }
            return s;
        }
    }

    private enum EMode {
        eNull,
        eWrite,
        eCandy
    }

    private enum EState {
        eHibernate,
        eWaking,
        eRun
    }

    private class KanjiBoard {
        private int bg = 0;
        private char[] c = new char[]{28450};
        private final byte lenJP = (byte) 7;
        private final byte lenJa = (byte) 4;
        private final byte lenKo = (byte) 1;
        private final byte lenPy = (byte) 12;
        private Bitmap mBitmap;
        private Canvas mCanvas;
        private final byte nJP = (byte) 7;
        private final byte nJa = (byte) 8;
        private final byte nKo = (byte) 4;
        private final byte nPy = (byte) 8;
        private char[] py = new char[]{'H', 'a', 'n', '4', ',', 12559, 12578, 715, ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', 'h', 'o', 'n', '2', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', 12363, 12435, ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', 54620, ' ', ' ', ' ', ' '};
        private int xBM;

        public KanjiBoard(int curW, int curH, int x, int bg1) {
            CreateCanvas(curW, curH);
            this.xBM = x;
            this.bg = bg1;
        }

        int H() {
            return this.mCanvas.getHeight();
        }

        int W() {
            return this.mCanvas.getWidth();
        }

        private int Py(int i) {
            return i * 12;
        }

        private int JP(int i) {
            return (i * 7) + 96;
        }

        private int Ja(int i) {
            return (i * 4) + 145;
        }

        private int Ko(int i) {
            return i + 177;
        }

        private void clear() {
            int n = this.py.length;
            while (true) {
                int n2 = n;
                n = n2 - 1;
                if (n2 > 0) {
                    this.py[n] = ' ';
                } else {
                    return;
                }
            }
        }

        private void set(char u, char cs) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {
            if (this.c[0] != u) {
                this.c[0] = u;
                clear();
                getYBiao();
                H.this.convert(H.this.candy.list, cs, get());
            }
        }

        private char get() {
            return this.c[0];
        }

        public void clearStrokes() {
            H.this.pi = 0;
            if (this.mCanvas != null) {
                int i;
                int h = this.mCanvas.getHeight();
                int w = this.mCanvas.getWidth();
                this.mBitmap.eraseColor(this.bg);
                H.this.p3.setAlpha(51);
                this.mCanvas.drawLine((float) (w / 2), (float) 5, (float) (w / 2), (float) (h - 5), H.this.p3);
                this.mCanvas.drawLine((float) 5, (float) (h / 2), (float) (w - 5), (float) (h / 2), H.this.p3);
                H.p2.setColor(1442840575);
                this.mCanvas.drawText(this.c, 0, 1, (float) (w / 2), ((((float) h) - H.this.fm4.ascent) - H.this.fm4.descent) / 2.0f, H.this.p4);
                H.p2.setTextAlign(Align.LEFT);
                for (i = 0; i < 8; i++) {
                    this.mCanvas.drawText(this.py, Py(i), 11, (float) 5, (((float) ((H.fhch * (i + 1)) + 5)) + H.this.fm2.ascent) + H.this.fm2.descent, H.p2);
                }
                for (i = 0; i < 4; i++) {
                    this.mCanvas.drawText(this.py, Ko(i), 1, (float) 5, (float) ((h - 5) - (H.fhch * i)), H.p2);
                }
                H.p2.setTextAlign(Align.RIGHT);
                for (i = 0; i < 7; i++) {
                    this.mCanvas.drawText(this.py, JP(i), 7, (float) (w - 5), (((float) ((H.fhch * (i + 1)) + 5)) + H.this.fm2.ascent) + H.this.fm2.descent, H.p2);
                }
                for (i = 0; i < 8; i++) {
                    this.mCanvas.drawText(this.py, Ja(i), 4, (float) (w - 5), (float) ((h - 5) - (H.fhch * i)), H.p2);
                }
                H.p2.setTextAlign(Align.CENTER);
                H.this.invalidate();
            }
        }

        private void CreateCanvas(int curW, int curH) {
            Bitmap newBitmap = Bitmap.createBitmap(curW, curH, Config.ARGB_8888);
            Canvas newCanvas = new Canvas();
            newCanvas.setBitmap(newBitmap);
            if (this.mBitmap != null) {
                newCanvas.drawBitmap(this.mBitmap, 0.0f, 0.0f, null);
                this.mBitmap = newBitmap;
                this.mCanvas = newCanvas;
                return;
            }
            this.mBitmap = newBitmap;
            this.mCanvas = newCanvas;
        }

        private void onDraw(Canvas c) {
            if (this.mBitmap != null) {
                c.drawBitmap(this.mBitmap, (float) this.xBM, 0.0f, H.this.mBitmapPaint);
            }
        }

        private void getYBiao() throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {
            H.this.getYinBiao(this.py, get());
        }

        float X(float x) {
            int w = W();
            x -= (float) this.xBM;
            if (x < 0.0f) {
                return 0.0f;
            }
            return x > ((float) w) ? (float) w : x;
        }

        float Y(float y) {
            int h = H();
            if (y < 0.0f) {
                return 0.0f;
            }
            return y > ((float) h) ? (float) h : y;
        }
    }

    static /* synthetic */ int access$1416(H x0, float x1) {
        int i = (int) (((float) x0.mTargetScrollX) + x1);
        x0.mTargetScrollX = i;
        return i;
    }

    static /* synthetic */ int access$916(H x0, float x1) {
        int i = (int) (((float) x0.mTargetScrollY) + x1);
        x0.mTargetScrollY = i;
        return i;
    }

    private static boolean InRange(char a, char c, char b) {
        return a <= c && c <= b;
    }

    static boolean IsHiragana(char c) {
        return InRange(12352, c, 12447);
    }

    private static boolean IsKatakana(char c) {
        return InRange(12448, c, 12543);
    }

    static boolean IsBopomofoAll(char c) {
        return InRange(12544, c, 12591);
    }

    static boolean IsHangul(char c) {
        return InRange(44032, c, 55215);
    }

    private static boolean IsJamu(char c) {
        return InRange(4352, c, 4607);
    }

    private static boolean IsCJamu(char c) {
        return InRange(12592, c, 12687);
    }

    static boolean IsHangulAll(char c) {
        return IsCJamu(c) || IsJamu(c) || IsHangul(c);
    }

    static boolean IsHWDigit(char c) {
        return InRange('0', c, '9');
    }

    private static boolean IsHWUpper(char c) {
        return InRange('A', c, 'Z');
    }

    private static boolean IsHWLower(char c) {
        return InRange('a', c, 'z');
    }

    private static boolean IsHWAscii(char c) {
        return InRange(0, c, 255);
    }

    private static boolean IsEnclosed(char c) {
        return InRange(12800, c, 13054);
    }

    static boolean IsFWAscii(char c) {
        return InRange(65281, c, 65374);
    }

    static boolean IsCJKPunct(char c) {
        return InRange(12288, c, 12351) || IsFWAscii(c);
    }

    static boolean IsAsciiPunct(char c) {
        return (!InRange(' ', c, '~') || IsEnglish(c) || IsHWDigit(c)) ? false : true;
    }

    static boolean IsPunct(char c) {
        return IsCJKPunct(c) || IsAsciiPunct(c);
    }

    static boolean IsEnglish(char c) {
        return IsHWUpper(c) || IsHWLower(c);
    }

    static boolean isH(char c) {
        return InRange(19968, c, 40959);
    }

    private static int charColor(char c) {
        if (IsBopomofoAll(c)) {
            return -11171585;
        }
        if (IsHiragana(c)) {
            return -217600;
        }
        if (IsKatakana(c)) {
            return -8913796;
        }
        if (IsCJKPunct(c)) {
            return -7842305;
        }
        if (IsEnclosed(c)) {
            return -5592559;
        }
        if (IsHangulAll(c)) {
            return -4478396;
        }
        if (IsHWDigit(c)) {
            return -20836;
        }
        if (IsHWUpper(c)) {
            return -11163068;
        }
        if (IsHWLower(c)) {
            return -5618603;
        }
        if (IsHWAscii(c)) {
            return -7820630;
        }
        return isH(c) ? -4473925 : -4478430;
    }

    public H() {
        this.candy = new Candy(this, null);
        this.nPad = 1;
        this.sPad = 0;
        this.old = false;
        this.p3 = new Paint(1);
        this.p4 = new Paint(1);
        this.mode = EMode.eNull;
        this.pt = null;
        this.defChr = new char[]{28450, 26360};
        this.mGestureDetector = new GestureDetector(new SimpleOnGestureListener() {
            public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
                H.access$1416(H.this, distanceX);
                H.access$916(H.this, distanceY);
                if (Math.abs(H.this.mTargetScrollY) > 13) {
                    H.this.invalidate();
                    H.this.pv.mHandler.cancelKeyTimers();
                }
                return true;
            }
        });
        this.hRefresh = new Handler() {
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case 0:
                        if (H.this.sug == null || H.this.sug.length <= 0) {
                            H.this.sug = H.this.candy.xa();
                            if (H.this.sug != null) {
                                M.mIme.showSuggestions(M.cvt(H.this.sug), H.this.sug[0], true, true);
                            }
                        } else {
                            M.mIme.showSuggestions(H.this.sug);
                        }
                        H.this.candy.pg = 0;
                        H.this.invalidate();
                        H.this.mode = EMode.eNull;
                        return;
                    default:
                        return;
                }
            }
        };
        this.rt = new Rect();
        this.state = EState.eHibernate;
        this.pt = new float[1603];
        float[] fArr = this.pt;
        this.pi = 0;
        fArr[0] = -1.0f;
        this.mBitmapPaint = new Paint(2);
        p.setStyle(Style.STROKE);
        p.setStrokeWidth(3.0f);
        p2.setColor(-1);
        p2.setAntiAlias(true);
        p2.setTextAlign(Align.CENTER);
        this.fm2 = p2.getFontMetrics();
        this.p3.setStyle(Style.FILL);
        this.p3.setStrokeWidth(0.0f);
        this.p3.setColor(1157627903);
        this.p4.setStyle(Style.FILL);
        this.p4.setStrokeWidth(2.0f);
        this.p4.setColor(587202559);
        this.p4.setTextAlign(Align.CENTER);
        Process.setThreadPriority(-8);
        this.thread = new Thread(this);
        this.thread.start();
    }

    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        if (w != 0 && h != 0) {
            try {
                fhch = h / 10;
                p2.setTextSize((float) fhch);
                this.p4.setTextSize((float) ((h * 2) / 3));
                this.fm4 = this.p4.getFontMetrics();
                int curH = h;
                int curW = (w * 75) / 100;
                this.yb = new KanjiBoard[this.nPad];
                for (int i = 0; i < this.nPad; i++) {
                    this.yb[i] = new KanjiBoard(curW, curH, w - ((this.nPad - i) * curW), 14540253 + (285212672 * i));
                    this.yb[i].c[0] = this.defChr[i];
                    this.yb[i].clearStrokes();
                }
            } catch (Throwable th) {
            }
            this.candy.onSizeChanged(w, h, oldw, oldh);
        }
    }

    private void pgDown(int i) {
        this.candy.pgDown(i);
        invalidate();
    }

    public void onDraw(Canvas c) {
        if (this.yb != null) {
            c.save();
            c.getClipBounds(this.rt);
            c.clipRect(0, 0, M.dm.widthPixels, h());
            if (this.rt.left == 0) {
                this.candy.onDraw(c);
            }
            for (KanjiBoard access$2100 : this.yb) {
                access$2100.onDraw(c);
            }
            c.restore();
        }
    }

    private void add(float x, float y) {
        KanjiBoard b = this.yb[this.sPad];
        x = b.X(x);
        y = b.Y(y);
        if (this.pi < eMaxPt) {
            int l;
            boolean drw = this.pt[this.pi] != -1.0f;
            synchronized (this.pt) {
                float[] fArr = this.pt;
                int i = this.pi + 1;
                this.pi = i;
                fArr[i] = x;
                fArr = this.pt;
                i = this.pi + 1;
                this.pi = i;
                fArr[i] = y;
            }
            p.setColor(M.hlc);
            if (drw) {
                this.yb[this.sPad].mCanvas.drawLine(this.pt[this.pi - 3], this.pt[this.pi - 2], this.pt[this.pi - 1], this.pt[this.pi], p);
            } else {
                this.yb[this.sPad].mCanvas.drawCircle(this.pt[this.pi - 1], this.pt[this.pi], 1.0f, p);
            }
            int sz = this.yb[this.sPad].W() / 3;
            int xi = ((int) x) + this.yb[this.sPad].xBM;
            int yi = (int) y;
            if (x > ((float) sz)) {
                l = (-sz) + xi;
            } else {
                l = xi;
            }
            postInvalidateDelayed(100, l, (-sz) + yi, sz + xi, sz + yi);
        }
    }

    public void invalidate() {
        this.pv.invalidate();
    }

    private void postInvalidateDelayed(int i, int l, int j, int k, int m) {
        this.pv.postInvalidateDelayed((long) i, l, j, k, m);
    }

    private void up() {
        if (this.pi < eMaxPt) {
            synchronized (this.pt) {
                float[] fArr = this.pt;
                int i = this.pi + 1;
                this.pi = i;
                fArr[i] = -1.0f;
            }
        }
    }

    void process() {
        synchronized (this.thread) {
            signalAbort();
            this.thread.notify();
        }
    }

    boolean isw() {
        return this.state == EState.eWaking;
    }

    /* JADX WARNING: Missing block: B:21:0x002b, code:
            if (r13 == r14.pi) goto L_0x003e;
     */
    /* JADX WARNING: Missing block: B:22:0x002d, code:
            r13 = r14.pi;
            r1 = r14.thread;
     */
    /* JADX WARNING: Missing block: B:23:0x0031, code:
            monitor-enter(r1);
     */
    /* JADX WARNING: Missing block: B:25:?, code:
            r14.thread.wait(com.klye.ime.latin.M.hwt);
     */
    /* JADX WARNING: Missing block: B:26:0x0039, code:
            monitor-exit(r1);
     */
    /* JADX WARNING: Missing block: B:32:0x003e, code:
            r14.state = com.klye.ime.latin.H.EState.eRun;
     */
    public void run() {
        /*
        r14 = this;
        r13 = -1;
    L_0x0001:
        r0 = com.klye.ime.latin.H.AnonymousClass3.$SwitchMap$com$klye$ime$latin$H$EState;	 Catch:{ Throwable -> 0x001d }
        r1 = r14.state;	 Catch:{ Throwable -> 0x001d }
        r1 = r1.ordinal();	 Catch:{ Throwable -> 0x001d }
        r0 = r0[r1];	 Catch:{ Throwable -> 0x001d }
        switch(r0) {
            case 1: goto L_0x000f;
            case 2: goto L_0x0029;
            case 3: goto L_0x0043;
            default: goto L_0x000e;
        };	 Catch:{ Throwable -> 0x001d }
    L_0x000e:
        goto L_0x0001;
    L_0x000f:
        r1 = r14.thread;	 Catch:{ Throwable -> 0x001d }
        monitor-enter(r1);	 Catch:{ Throwable -> 0x001d }
        r0 = r14.thread;	 Catch:{ all -> 0x0026 }
        r0.wait();	 Catch:{ all -> 0x0026 }
        monitor-exit(r1);	 Catch:{ all -> 0x0026 }
        r0 = com.klye.ime.latin.H.EState.eWaking;	 Catch:{ Throwable -> 0x001d }
        r14.state = r0;	 Catch:{ Throwable -> 0x001d }
        goto L_0x0001;
    L_0x001d:
        r12 = move-exception;
        r12.printStackTrace();
        r0 = com.klye.ime.latin.H.EState.eHibernate;
        r14.state = r0;
        goto L_0x0001;
    L_0x0026:
        r0 = move-exception;
        monitor-exit(r1);	 Catch:{ all -> 0x0026 }
        throw r0;	 Catch:{ Throwable -> 0x001d }
    L_0x0029:
        r0 = r14.pi;	 Catch:{ Throwable -> 0x001d }
        if (r13 == r0) goto L_0x003e;
    L_0x002d:
        r13 = r14.pi;	 Catch:{ Throwable -> 0x001d }
        r1 = r14.thread;	 Catch:{ Throwable -> 0x001d }
        monitor-enter(r1);	 Catch:{ Throwable -> 0x001d }
        r0 = r14.thread;	 Catch:{ all -> 0x003b }
        r2 = com.klye.ime.latin.M.hwt;	 Catch:{ all -> 0x003b }
        r0.wait(r2);	 Catch:{ all -> 0x003b }
        monitor-exit(r1);	 Catch:{ all -> 0x003b }
        goto L_0x0029;
    L_0x003b:
        r0 = move-exception;
        monitor-exit(r1);	 Catch:{ all -> 0x003b }
        throw r0;	 Catch:{ Throwable -> 0x001d }
    L_0x003e:
        r0 = com.klye.ime.latin.H.EState.eRun;	 Catch:{ Throwable -> 0x001d }
        r14.state = r0;	 Catch:{ Throwable -> 0x001d }
        goto L_0x0001;
    L_0x0043:
        r0 = r14.yb;	 Catch:{ Throwable -> 0x001d }
        r1 = r14.sPad;	 Catch:{ Throwable -> 0x001d }
        r11 = r0[r1];	 Catch:{ Throwable -> 0x001d }
        r0 = r14.pi;	 Catch:{ Throwable -> 0x001d }
        if (r0 <= 0) goto L_0x0080;
    L_0x004d:
        r0 = r14.candy;	 Catch:{ Throwable -> 0x001d }
        r1 = r0.list;	 Catch:{ Throwable -> 0x001d }
        r2 = r14.pt;	 Catch:{ Throwable -> 0x001d }
        r3 = r14.pi;	 Catch:{ Throwable -> 0x001d }
        r4 = 0;
        r5 = 0;
        r6 = r11.W();	 Catch:{ Throwable -> 0x001d }
        r7 = r11.H();	 Catch:{ Throwable -> 0x001d }
        r0 = com.klye.ime.latin.M.mIme;	 Catch:{ Throwable -> 0x001d }
        r0 = r0.mWord;	 Catch:{ Throwable -> 0x001d }
        r0 = r0.mTW;	 Catch:{ Throwable -> 0x001d }
        r8 = r0.toString();	 Catch:{ Throwable -> 0x001d }
        r9 = com.klye.ime.latin.M.cs();	 Catch:{ Throwable -> 0x001d }
        r10 = bits();	 Catch:{ Throwable -> 0x001d }
        r0 = r14;
        r0 = r0.recognize(r1, r2, r3, r4, r5, r6, r7, r8, r9, r10);	 Catch:{ Throwable -> 0x001d }
        r14.sug = r0;	 Catch:{ Throwable -> 0x001d }
        r0 = r14.hRefresh;	 Catch:{ Throwable -> 0x001d }
        r1 = 0;
        r0.sendEmptyMessage(r1);	 Catch:{ Throwable -> 0x001d }
    L_0x0080:
        r0 = r14.pi;	 Catch:{ Throwable -> 0x001d }
        if (r13 == r0) goto L_0x008a;
    L_0x0084:
        r0 = com.klye.ime.latin.H.EState.eWaking;	 Catch:{ Throwable -> 0x001d }
    L_0x0086:
        r14.state = r0;	 Catch:{ Throwable -> 0x001d }
        goto L_0x0001;
    L_0x008a:
        r0 = com.klye.ime.latin.H.EState.eHibernate;	 Catch:{ Throwable -> 0x001d }
        goto L_0x0086;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.klye.ime.latin.H.run():void");
    }

    static int bits() {
        int i = 33;
        switch (M.zhType()) {
            case 80:
                i = 33 | 64;
                break;
            case 89:
                i = (33 | 256) & -2;
                break;
            case 90:
                i = 33 | 8;
                break;
        }
        if (M.mLC == M.en) {
            return i | 16;
        }
        if (M.mLC == M.ja) {
            return i | 4;
        }
        if (M.mLC == M.ko) {
            return i | 2;
        }
        return i;
    }

    void getHW() {
        if (!loadHW()) {
            M.noti(M.mIme, "HW Plugin not found. Please install it if you haven't done so", M.hp("hw.html"));
        }
    }

    boolean loadHW() {
        String pn = "klye.hanwriting";
        try {
            ClassLoader cl = M.mIme.createPackageContext("klye.hanwriting", 3).getClassLoader();
            String className = "klye.hanwriting.M";
            if (className == null) {
                return true;
            }
            Class<?> cls = cl.loadClass(className);
            this.m1 = cls.getMethod("predict", new Class[]{String.class, Character.TYPE, Integer.TYPE});
            this.m2 = cls.getMethod("recognize", new Class[]{char[].class, float[].class, Integer.TYPE, Integer.TYPE, Integer.TYPE, Integer.TYPE, Integer.TYPE, String.class, Character.TYPE, Integer.TYPE});
            this.m3 = cls.getMethod("convert", new Class[]{char[].class, Character.TYPE, Character.TYPE});
            this.m4 = cls.getMethod("getYinBiao", new Class[]{char[].class, Character.TYPE});
            this.m5 = cls.getMethod("predict2", new Class[]{int[].class, Integer.TYPE, Integer.TYPE, Character.TYPE, Integer.TYPE});
            return true;
        } catch (Throwable th) {
            return false;
        }
    }

    private void signalAbort() {
    }

    private String[] recognize(char[] list, float[] pt, int pi, int l, int t, int r, int b, String string, char cv, int cs) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {
        if (this.m2 == null) {
            getHW();
        }
        return (String[]) this.m2.invoke(null, new Object[]{list, pt, Integer.valueOf(pi), Integer.valueOf(l), Integer.valueOf(t), Integer.valueOf(r), Integer.valueOf(b), string, Character.valueOf(cv), Integer.valueOf(cs)});
    }

    public String[] predict2(int[] buf, int n1, int nAlt, char cv, int cs) {
        try {
            if (this.m5 == null) {
                getHW();
            }
            if (this.m5 == null) {
                M.noti(M.mIme, "請更新插件", M.hp("hw.html"));
            }
            String[] r = (String[]) this.m5.invoke(null, new Object[]{buf, Integer.valueOf(n1), Integer.valueOf(nAlt), Character.valueOf(cv), Integer.valueOf(cs)});
            this.n = r.length;
            return r;
        } catch (Throwable e) {
            M.l(e);
            this.m1 = null;
            return null;
        }
    }

    private void convert(char[] list, char cs, char l) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {
        if (this.m3 == null) {
            getHW();
        }
        this.m3.invoke(null, new Object[]{list, Character.valueOf(cs), Character.valueOf(l)});
    }

    private void getYinBiao(char[] s, char u) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {
        if (this.m4 == null) {
            getHW();
        }
        this.m4.invoke(null, new Object[]{s, Character.valueOf(u)});
    }

    public String[] suggest(String s, char cs, int f) {
        try {
            int l = s.length();
            if (l > 0) {
                if (this.yb != null && M.hw()) {
                    KanjiBoard b = this.yb[this.sPad];
                    if (b != null) {
                        b.set(s.charAt(l - 1), cs);
                    }
                    clearStrokes();
                }
                return predict2(M.icb, s.length(), 16, cs, f);
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean onTouchEvent(android.view.MotionEvent r12) {
        /*
        r11 = this;
        r10 = 1;
        r8 = 0;
        r4 = r12.getX();
        r5 = r12.getY();
        r6 = r11.yb;
        r6 = r6[r8];
        r6 = r6.xBM;
        r6 = (float) r6;
        r6 = (r4 > r6 ? 1 : (r4 == r6 ? 0 : -1));
        if (r6 >= 0) goto L_0x0026;
    L_0x0017:
        r6 = r11.mode;
        r7 = com.klye.ime.latin.H.EMode.eWrite;
        if (r6 == r7) goto L_0x0026;
    L_0x001d:
        r6 = r11.mGestureDetector;
        r6 = r6.onTouchEvent(r12);
        if (r6 == 0) goto L_0x0026;
    L_0x0025:
        return r10;
    L_0x0026:
        r6 = r12.getAction();
        switch(r6) {
            case 0: goto L_0x002e;
            case 1: goto L_0x0092;
            case 2: goto L_0x005e;
            default: goto L_0x002d;
        };
    L_0x002d:
        goto L_0x0025;
    L_0x002e:
        r11.mTargetScrollX = r8;
        r11.mTargetScrollY = r8;
        r6 = com.klye.ime.latin.H.EMode.eWrite;
        r11.mode = r6;
        r11.remember(r4, r5);
        r6 = r11.yb;
        r6 = r6[r8];
        r6 = r6.xBM;
        r6 = (float) r6;
        r6 = (r4 > r6 ? 1 : (r4 == r6 ? 0 : -1));
        if (r6 >= 0) goto L_0x004b;
    L_0x0046:
        r6 = com.klye.ime.latin.H.EMode.eCandy;
        r11.mode = r6;
        goto L_0x0025;
    L_0x004b:
        r6 = r11.nPad;
        if (r6 <= r10) goto L_0x008b;
    L_0x004f:
        r6 = r11.yb;
        r6 = r6[r10];
        r6 = r6.xBM;
        r6 = (float) r6;
        r6 = (r4 > r6 ? 1 : (r4 == r6 ? 0 : -1));
        if (r6 <= 0) goto L_0x008b;
    L_0x005c:
        r11.sPad = r10;
    L_0x005e:
        r6 = r11.pv;
        r6.slpt();
        r6 = com.klye.ime.latin.H.AnonymousClass3.$SwitchMap$com$klye$ime$latin$H$EMode;
        r7 = r11.mode;
        r7 = r7.ordinal();
        r6 = r6[r7];
        switch(r6) {
            case 1: goto L_0x0071;
            default: goto L_0x0070;
        };
    L_0x0070:
        goto L_0x0025;
    L_0x0071:
        r11.signalAbort();
        r11.old = r8;
        r0 = r12.getHistorySize();
        r3 = 0;
    L_0x007b:
        if (r3 >= r0) goto L_0x008e;
    L_0x007d:
        r6 = r12.getHistoricalX(r3);
        r7 = r12.getHistoricalY(r3);
        r11.add(r6, r7);
        r3 = r3 + 6;
        goto L_0x007b;
    L_0x008b:
        r11.sPad = r8;
        goto L_0x005e;
    L_0x008e:
        r11.add(r4, r5);
        goto L_0x0025;
    L_0x0092:
        r6 = r11.pv;
        r6.clpt();
        r6 = com.klye.ime.latin.H.AnonymousClass3.$SwitchMap$com$klye$ime$latin$H$EMode;
        r7 = r11.mode;
        r7 = r7.ordinal();
        r6 = r6[r7];
        switch(r6) {
            case 1: goto L_0x00a5;
            case 2: goto L_0x00b0;
            default: goto L_0x00a4;
        };
    L_0x00a4:
        goto L_0x0025;
    L_0x00a5:
        r11.add(r4, r5);
        r11.up();
        r11.process();
        goto L_0x0025;
    L_0x00b0:
        r6 = r11.mTargetScrollY;	 Catch:{ Exception -> 0x00bc }
        r7 = -33;
        if (r6 >= r7) goto L_0x00c2;
    L_0x00b6:
        r6 = -1;
        r11.pgDown(r6);	 Catch:{ Exception -> 0x00bc }
        goto L_0x0025;
    L_0x00bc:
        r2 = move-exception;
        r2.printStackTrace();
        goto L_0x0025;
    L_0x00c2:
        r6 = r11.mTargetScrollY;	 Catch:{ Exception -> 0x00bc }
        r7 = 33;
        if (r6 <= r7) goto L_0x00ce;
    L_0x00c8:
        r6 = 1;
        r11.pgDown(r6);	 Catch:{ Exception -> 0x00bc }
        goto L_0x0025;
    L_0x00ce:
        r6 = r11.mTargetScrollX;	 Catch:{ Exception -> 0x00bc }
        r7 = -70;
        if (r6 >= r7) goto L_0x00db;
    L_0x00d4:
        r6 = com.klye.ime.latin.M.mIme;	 Catch:{ Exception -> 0x00bc }
        r6.swipeRight();	 Catch:{ Exception -> 0x00bc }
        goto L_0x0025;
    L_0x00db:
        r6 = r11.mTargetScrollX;	 Catch:{ Exception -> 0x00bc }
        r7 = 70;
        if (r6 <= r7) goto L_0x00e8;
    L_0x00e1:
        r6 = com.klye.ime.latin.M.mIme;	 Catch:{ Exception -> 0x00bc }
        r6.swipeLeft();	 Catch:{ Exception -> 0x00bc }
        goto L_0x0025;
    L_0x00e8:
        r6 = r11.howFarY(r5);	 Catch:{ Exception -> 0x00bc }
        r7 = r11.candy;	 Catch:{ Exception -> 0x00bc }
        r7 = r7.cH;	 Catch:{ Exception -> 0x00bc }
        r7 = (float) r7;	 Catch:{ Exception -> 0x00bc }
        r6 = (r6 > r7 ? 1 : (r6 == r7 ? 0 : -1));
        if (r6 >= 0) goto L_0x0025;
    L_0x00f5:
        r6 = r11.candy;	 Catch:{ Exception -> 0x00bc }
        r7 = (int) r4;	 Catch:{ Exception -> 0x00bc }
        r8 = (int) r5;	 Catch:{ Exception -> 0x00bc }
        r1 = r6.get(r7, r8);	 Catch:{ Exception -> 0x00bc }
        r6 = r11.yb;	 Catch:{ Exception -> 0x00bc }
        r7 = r11.sPad;	 Catch:{ Exception -> 0x00bc }
        r6 = r6[r7];	 Catch:{ Exception -> 0x00bc }
        r6 = r6.get();	 Catch:{ Exception -> 0x00bc }
        if (r1 == r6) goto L_0x010f;
    L_0x0109:
        r6 = IsHWDigit(r1);	 Catch:{ Exception -> 0x00bc }
        if (r6 == 0) goto L_0x0136;
    L_0x010f:
        r6 = 0;
        r11.old = r6;	 Catch:{ Exception -> 0x00bc }
    L_0x0112:
        r6 = r11.old;	 Catch:{ Exception -> 0x00bc }
        if (r6 == 0) goto L_0x011d;
    L_0x0116:
        r6 = com.klye.ime.latin.M.mIme;	 Catch:{ Exception -> 0x00bc }
        r6 = r6.mWord;	 Catch:{ Exception -> 0x00bc }
        r6.deleteLast();	 Catch:{ Exception -> 0x00bc }
    L_0x011d:
        r6 = com.klye.ime.latin.M.mIme;	 Catch:{ Exception -> 0x00bc }
        r7 = 0;
        r8 = 0;
        r9 = 0;
        r6.onKey(r1, r7, r8, r9);	 Catch:{ Exception -> 0x00bc }
        r6 = 1;
        r11.old = r6;	 Catch:{ Exception -> 0x00bc }
        r6 = r11.candy;	 Catch:{ Exception -> 0x00bc }
        r7 = 0;
        r6.pg = r7;	 Catch:{ Exception -> 0x00bc }
        r11.clearStrokes();	 Catch:{ Exception -> 0x00bc }
        r6 = com.klye.ime.latin.H.EMode.eNull;	 Catch:{ Exception -> 0x00bc }
        r11.mode = r6;	 Catch:{ Exception -> 0x00bc }
        goto L_0x0025;
    L_0x0136:
        r6 = r11.yb;	 Catch:{ Exception -> 0x00bc }
        r7 = r11.sPad;	 Catch:{ Exception -> 0x00bc }
        r6 = r6[r7];	 Catch:{ Exception -> 0x00bc }
        r7 = com.klye.ime.latin.M.cs();	 Catch:{ Exception -> 0x00bc }
        r6.set(r1, r7);	 Catch:{ Exception -> 0x00bc }
        goto L_0x0112;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.klye.ime.latin.H.onTouchEvent(android.view.MotionEvent):boolean");
    }

    private void remember(float x, float y) {
        this.xRem = x;
        this.yRem = y;
    }

    private float howFarY(float y) {
        return Math.abs(this.yRem - y);
    }

    public void del() {
        if (this.pi > 0) {
            clearStrokes();
        } else {
            M.mIme.handleBackspace();
        }
        this.mTargetScrollX = 0;
        this.mTargetScrollY = 0;
    }

    public void clearStrokes() {
        try {
            this.mode = EMode.eNull;
            for (int i = 0; i < this.nPad; i++) {
                this.yb[i].clearStrokes();
            }
            invalidate();
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    public String strokesCode() {
        KanjiBoard b = this.yb[this.sPad];
        String s = (new String() + "int w=" + b.W() + ",h=" + b.H() + ",n=" + this.pi + ";") + "float ft[]={";
        for (int i = 0; i < this.pi; i++) {
            s = (s + ((int) this.pt[i])) + ",";
        }
        return s + "};";
    }

    public void setV(LatinKeyboardView mInputView) {
        this.pv = mInputView;
        if (this.m1 == null) {
            getHW();
        }
    }

    public boolean isOK() {
        return (this.m1 == null || M.ja == M.mLC) ? false : true;
    }

    public boolean is(LatinKeyboardBaseView v) {
        return this.pv == v;
    }

    public int h() {
        return this.candy.h;
    }

    public boolean iss() {
        return Math.abs(this.mTargetScrollY) > h() / 3;
    }

    public static boolean isKana(char c) {
        return IsHiragana(c) || IsKatakana(c);
    }

    public boolean wm() {
        return this.mode == EMode.eWrite;
    }
}
