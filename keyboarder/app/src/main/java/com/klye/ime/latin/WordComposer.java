package com.klye.ime.latin;

import java.util.ArrayList;
import java.util.List;

public class WordComposer {
    private boolean mAutoCapitalized;
    private int mCapsCount;
    private List<int[]> mCodes;
    private final ArrayList<int[]> mCodesO;
    private boolean mIsFirstCharCapitalized;
    final StringBuilder mOTW;
    private String mPreferredWord;
    final StringBuilder mTW;
    List<StringBuilder> mTWs;

    public WordComposer() {
        this.mCodes = new ArrayList(12);
        this.mCodesO = new ArrayList(12);
        this.mTW = new StringBuilder(20);
        this.mOTW = new StringBuilder(20);
    }

    WordComposer(WordComposer copy) {
        this.mCodesO = new ArrayList(copy.mCodesO);
        this.mCodes = new ArrayList(copy.mCodes);
        this.mPreferredWord = copy.mPreferredWord;
        this.mTW = new StringBuilder(copy.mTW);
        this.mOTW = new StringBuilder(copy.mOTW);
        this.mCapsCount = copy.mCapsCount;
        this.mAutoCapitalized = copy.mAutoCapitalized;
        this.mIsFirstCharCapitalized = copy.mIsFirstCharCapitalized;
    }

    public void reset() {
        this.mCodesO.clear();
        this.mCodes.clear();
        this.mIsFirstCharCapitalized = false;
        this.mPreferredWord = null;
        this.mTW.setLength(0);
        this.mOTW.setLength(0);
        this.mCapsCount = 0;
        this.mTWs = null;
    }

    public void set(CharSequence s) {
        reset();
        this.mTW.append(s);
        this.mOTW.append(s);
        SyncCodes();
    }

    public int size() {
        return this.mCodes.size();
    }

    public int[] getCodesAt(int index) {
        return (int[]) this.mCodes.get(index);
    }

    public void add1(int primaryCode, int[] codes) {
        if (codes == null) {
            codes = new int[]{primaryCode};
        }
        this.mTW.append((char) primaryCode);
        correctPrimaryJuxtapos(primaryCode, codes);
        this.mCodes.add(codes);
        if (Character.isUpperCase((char) primaryCode)) {
            this.mCapsCount++;
            if (size() == 1) {
                this.mIsFirstCharCapitalized = true;
            }
        }
    }

    public void add2(int primaryCode, int[] codes) {
        this.mOTW.append((char) primaryCode);
    }

    public void add3(int primaryCode, int[] codes) {
        this.mOTW.append((char) primaryCode);
        correctPrimaryJuxtapos(primaryCode, codes);
        this.mCodesO.add(codes);
        if (Character.isUpperCase((char) primaryCode)) {
            this.mCapsCount++;
            if (size() == 1) {
                this.mIsFirstCharCapitalized = true;
            }
        }
    }

    private void correctPrimaryJuxtapos(int primaryCode, int[] codes) {
        if (codes != null && codes.length >= 2 && codes[0] > 0 && codes[1] > 0 && codes[0] != primaryCode && codes[1] == primaryCode) {
            codes[1] = codes[0];
            codes[0] = primaryCode;
        }
    }

    public void deleteLast() {
        switch (M.mLC) {
            case M.ko /*7012463*/:
                deleteLast2();
                return;
            case M.vi /*7733353*/:
                deleteLast3();
                return;
            default:
                deleteLast1();
                return;
        }
    }

    private void deleteLast1() {
        int codesSize = this.mCodes.size();
        if (codesSize > 0) {
            this.mCodes.remove(codesSize - 1);
            int lastPos = this.mTW.length() - 1;
            char last = this.mTW.charAt(lastPos);
            this.mTW.deleteCharAt(lastPos);
            if (Character.isUpperCase(last)) {
                this.mCapsCount--;
            }
        }
    }

    private void deleteLast3() {
        int codesSize = this.mCodesO.size();
        if (codesSize > 0) {
            this.mCodesO.remove(codesSize - 1);
            int lastPos = this.mOTW.length() - 1;
            char last = this.mOTW.charAt(lastPos);
            this.mOTW.deleteCharAt(lastPos);
            if (Character.isUpperCase(last)) {
                this.mCapsCount--;
            }
        }
    }

    void deleteLast2() {
        int l = this.mOTW.length();
        if (l > 0) {
            this.mOTW.setLength(l - 1);
        }
    }

    public CharSequence getTypedWord() {
        return this.mTW.length() > 0 ? this.mTW : null;
    }

    public void setFirstCharCapitalized(boolean capitalized) {
        this.mIsFirstCharCapitalized = capitalized;
    }

    public boolean isFirstCharCapitalized() {
        return this.mIsFirstCharCapitalized;
    }

    public boolean isAllUpperCase() {
        return this.mCapsCount > 0 && this.mCapsCount == size();
    }

    public void setPreferredWord(String preferred) {
        this.mPreferredWord = preferred;
    }

    public CharSequence getPreferredWord() {
        return this.mPreferredWord != null ? this.mPreferredWord : getTypedWord();
    }

    public boolean hasCap(int i) {
        return M.zhType() == -1 && this.mCapsCount >= i;
    }

    public void setAutoCapitalized(boolean auto) {
        this.mAutoCapitalized = auto;
    }

    public boolean isAutoCapitalized() {
        return this.mAutoCapitalized;
    }

    public void replaceLast(char c) {
        replace(c, this.mTW.length() - 1);
    }

    public void replace(char c, int i) {
        if (this.mCodes.size() > i) {
            char last = this.mTW.charAt(i);
            this.mTW.setCharAt(i, c);
            this.mCodes.set(i, new int[]{c});
            if (Character.isUpperCase(last)) {
                this.mCapsCount--;
            }
        }
    }

    public void origin() {
        this.mTW.setLength(0);
        this.mTW.append(this.mOTW);
    }

    public void SyncCodes() {
        this.mCodes.clear();
        int l = this.mTW.length();
        for (int i = 0; i < l; i++) {
            this.mCodes.add(new int[]{this.mTW.charAt(i)});
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:48:0x0100  */
    /* JADX WARNING: Removed duplicated region for block: B:15:0x0075  */
    public java.lang.CharSequence viIme() {
        /*
        r22 = this;
        r0 = r22;
        r12 = r0.mOTW;
        r15 = r12.length();
        r17 = 0;
        r18 = 0;
        r0 = r22;
        r0 = r0.mTW;
        r20 = r0;
        r21 = 0;
        r20.setLength(r21);
        r0 = r22;
        r0 = r0.mCodes;
        r20 = r0;
        r20.clear();
        if (r15 != 0) goto L_0x0029;
    L_0x0022:
        r0 = r22;
        r0 = r0.mTW;
        r20 = r0;
    L_0x0028:
        return r20;
    L_0x0029:
        r20 = 0;
        r0 = r20;
        r2 = r12.charAt(r0);
        r0 = r22;
        r0 = r0.mCodesO;
        r20 = r0;
        r21 = 0;
        r20 = r20.get(r21);
        r20 = (int[]) r20;
        r0 = r22;
        r1 = r20;
        r0.add1(r2, r1);
        r20 = java.lang.Character.toLowerCase(r2);
        r20 = com.klye.ime.latin.M.viV(r20);
        if (r20 == 0) goto L_0x0052;
    L_0x0050:
        r17 = 1;
    L_0x0052:
        r14 = 1;
    L_0x0053:
        if (r14 >= r15) goto L_0x0116;
    L_0x0055:
        r4 = r12.charAt(r14);
        r6 = r22.lastChar();
        r3 = java.lang.Character.toLowerCase(r4);
        r5 = java.lang.Character.toLowerCase(r6);
        r20 = com.klye.ime.latin.M.viV(r3);
        if (r20 == 0) goto L_0x006f;
    L_0x006b:
        if (r17 != 0) goto L_0x006f;
    L_0x006d:
        r17 = 1;
    L_0x006f:
        r7 = 0;
        switch(r3) {
            case 97: goto L_0x00d1;
            case 98: goto L_0x0073;
            case 99: goto L_0x0073;
            case 100: goto L_0x00a1;
            case 101: goto L_0x00ca;
            case 102: goto L_0x00df;
            case 103: goto L_0x0073;
            case 104: goto L_0x0073;
            case 105: goto L_0x0073;
            case 106: goto L_0x00df;
            case 107: goto L_0x0073;
            case 108: goto L_0x0073;
            case 109: goto L_0x0073;
            case 110: goto L_0x0073;
            case 111: goto L_0x00d8;
            case 112: goto L_0x0073;
            case 113: goto L_0x0073;
            case 114: goto L_0x00df;
            case 115: goto L_0x00df;
            case 116: goto L_0x0073;
            case 117: goto L_0x0073;
            case 118: goto L_0x0073;
            case 119: goto L_0x00a8;
            case 120: goto L_0x00df;
            case 121: goto L_0x0073;
            case 122: goto L_0x00df;
            default: goto L_0x0073;
        };
    L_0x0073:
        if (r7 == 0) goto L_0x0100;
    L_0x0075:
        if (r7 != r5) goto L_0x00f3;
    L_0x0077:
        r20 = com.klye.ime.latin.BinaryDictionary.toAccentLess(r6);
        r0 = r20;
        r0 = (char) r0;
        r20 = r0;
        r0 = r22;
        r1 = r20;
        r0.replaceLast(r1);
        r0 = r22;
        r0 = r0.mCodesO;
        r20 = r0;
        r0 = r20;
        r20 = r0.get(r14);
        r20 = (int[]) r20;
        r0 = r22;
        r1 = r20;
        r0.add1(r4, r1);
        r17 = 101; // 0x65 float:1.42E-43 double:5.0E-322;
    L_0x009e:
        r14 = r14 + 1;
        goto L_0x0053;
    L_0x00a1:
        switch(r5) {
            case 100: goto L_0x00a5;
            case 273: goto L_0x00a5;
            default: goto L_0x00a4;
        };
    L_0x00a4:
        goto L_0x0073;
    L_0x00a5:
        r7 = 273; // 0x111 float:3.83E-43 double:1.35E-321;
        goto L_0x0073;
    L_0x00a8:
        switch(r5) {
            case 97: goto L_0x00be;
            case 111: goto L_0x00c1;
            case 117: goto L_0x00c4;
            case 259: goto L_0x00be;
            case 417: goto L_0x00c1;
            case 432: goto L_0x00c4;
            default: goto L_0x00ab;
        };
    L_0x00ab:
        if (r18 != 0) goto L_0x00b5;
    L_0x00ad:
        r20 = 101; // 0x65 float:1.42E-43 double:5.0E-322;
        r0 = r17;
        r1 = r20;
        if (r0 == r1) goto L_0x0073;
    L_0x00b5:
        if (r18 != 0) goto L_0x00b9;
    L_0x00b7:
        if (r17 != 0) goto L_0x00c7;
    L_0x00b9:
        r18 = 0;
        r17 = 101; // 0x65 float:1.42E-43 double:5.0E-322;
        goto L_0x0073;
    L_0x00be:
        r7 = 259; // 0x103 float:3.63E-43 double:1.28E-321;
        goto L_0x0073;
    L_0x00c1:
        r7 = 417; // 0x1a1 float:5.84E-43 double:2.06E-321;
        goto L_0x0073;
    L_0x00c4:
        r7 = 432; // 0x1b0 float:6.05E-43 double:2.134E-321;
        goto L_0x0073;
    L_0x00c7:
        r18 = 1;
        goto L_0x009e;
    L_0x00ca:
        switch(r5) {
            case 101: goto L_0x00ce;
            case 234: goto L_0x00ce;
            default: goto L_0x00cd;
        };
    L_0x00cd:
        goto L_0x0073;
    L_0x00ce:
        r7 = 234; // 0xea float:3.28E-43 double:1.156E-321;
        goto L_0x0073;
    L_0x00d1:
        switch(r5) {
            case 97: goto L_0x00d5;
            case 226: goto L_0x00d5;
            default: goto L_0x00d4;
        };
    L_0x00d4:
        goto L_0x0073;
    L_0x00d5:
        r7 = 226; // 0xe2 float:3.17E-43 double:1.117E-321;
        goto L_0x0073;
    L_0x00d8:
        switch(r5) {
            case 111: goto L_0x00dc;
            case 244: goto L_0x00dc;
            default: goto L_0x00db;
        };
    L_0x00db:
        goto L_0x0073;
    L_0x00dc:
        r7 = 244; // 0xf4 float:3.42E-43 double:1.206E-321;
        goto L_0x0073;
    L_0x00df:
        r20 = 101; // 0x65 float:1.42E-43 double:5.0E-322;
        r0 = r17;
        r1 = r20;
        if (r0 == r1) goto L_0x0073;
    L_0x00e7:
        if (r17 == 0) goto L_0x0073;
    L_0x00e9:
        r0 = r17;
        if (r0 != r3) goto L_0x00f0;
    L_0x00ed:
        r17 = 101; // 0x65 float:1.42E-43 double:5.0E-322;
        goto L_0x0073;
    L_0x00f0:
        r17 = r3;
        goto L_0x009e;
    L_0x00f3:
        r8 = r7;
        if (r5 == r6) goto L_0x00fa;
    L_0x00f6:
        r8 = java.lang.Character.toUpperCase(r7);
    L_0x00fa:
        r0 = r22;
        r0.replaceLast(r8);
        goto L_0x009e;
    L_0x0100:
        r0 = r22;
        r0 = r0.mCodesO;
        r20 = r0;
        r0 = r20;
        r20 = r0.get(r14);
        r20 = (int[]) r20;
        r0 = r22;
        r1 = r20;
        r0.add1(r4, r1);
        goto L_0x009e;
    L_0x0116:
        r0 = r22;
        r0 = r0.mTW;
        r20 = r0;
        r16 = r20.length();
        r0 = r22;
        r0 = r0.mTW;
        r20 = r0;
        r20 = r20.toString();
        r19 = r20.toLowerCase();
        r20 = "ưo";
        r14 = r19.indexOf(r20);
        r20 = -1;
        r0 = r20;
        if (r14 != r0) goto L_0x0140;
    L_0x013a:
        r20 = "uơ";
        r14 = r19.indexOf(r20);
    L_0x0140:
        if (r18 != 0) goto L_0x0148;
    L_0x0142:
        r20 = -1;
        r0 = r20;
        if (r14 == r0) goto L_0x0155;
    L_0x0148:
        r0 = r22;
        r0 = r0.mTW;
        r20 = r0;
        r0 = r20;
        r1 = r16;
        viW(r0, r1);
    L_0x0155:
        r20 = 101; // 0x65 float:1.42E-43 double:5.0E-322;
        r0 = r17;
        r1 = r20;
        if (r0 <= r1) goto L_0x0190;
    L_0x015d:
        r0 = r22;
        r0 = r0.mTW;
        r20 = r0;
        r21 = r16 + -1;
        r13 = viFV(r20, r21);
        r20 = -1;
        r0 = r20;
        if (r13 == r0) goto L_0x0190;
    L_0x016f:
        r0 = r22;
        r0 = r0.mTW;
        r20 = r0;
        r0 = r20;
        r10 = r0.charAt(r13);
        r9 = java.lang.Character.toLowerCase(r10);
        r0 = r17;
        r11 = com.klye.ime.latin.M.viTone(r0, r9);
        if (r10 == r9) goto L_0x018b;
    L_0x0187:
        r11 = java.lang.Character.toUpperCase(r11);
    L_0x018b:
        r0 = r22;
        r0.replace(r11, r13);
    L_0x0190:
        r0 = r22;
        r0 = r0.mTW;
        r20 = r0;
        goto L_0x0028;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.klye.ime.latin.WordComposer.viIme():java.lang.CharSequence");
    }

    public static int viFV(CharSequence cs, int l) {
        int i = l;
        while (i >= 0 && !M.viD(cs.charAt(i))) {
            i--;
        }
        if (i != -1) {
            return i;
        }
        i = l;
        while (i >= 0 && !M.viV(cs.charAt(i))) {
            i--;
        }
        if (i > 0 && i == l) {
            char c = cs.charAt(i - 1);
            if (M.viV(c)) {
                if (i > 1) {
                    switch (Character.toLowerCase(cs.charAt(i - 2))) {
                        case 'g':
                            if (c == 'i') {
                                return i;
                            }
                            break;
                        case 'q':
                            break;
                    }
                    if (c == 'u') {
                        return i;
                    }
                }
                return i - 1;
            }
        }
        return i;
    }

    public static void viW(StringBuilder s, int l) {
        for (int i = 0; i < l; i++) {
            char w = viW(s.charAt(i));
            if (w != 0) {
                s.replace(i, i + 1, Character.toString(w));
            }
        }
    }

    private static char viW(char c) {
        char c3 = 0;
        char c2 = Character.toLowerCase(c);
        boolean iu = c2 != c;
        switch (c2) {
            case 'o':
                c3 = 417;
                break;
            case 'u':
                c3 = 432;
                break;
        }
        return iu ? Character.toUpperCase(c3) : c3;
    }

    public CharSequence jaIme() {
        int l = size();
        this.mTWs = new ArrayList();
        for (int i = 0; i < l; i++) {
            int[] c1 = getCodesAt(i);
            if (c1[0] == -1) {
                return this.mTW;
            }
            jaIme(c1);
        }
        if (this.mTWs.size() > 0) {
            jaMN();
            this.mTW.setLength(0);
            this.mTW.append((CharSequence) this.mTWs.get(0));
        }
        return this.mTW;
    }

    public void jaMN() {
        int l = this.mTWs.size();
        for (int i = 0; i < l; i++) {
            StringBuilder sb = (StringBuilder) this.mTWs.get(i);
            String s = sb.toString().replaceAll("[m|n]", M.shf() ? "ン" : "ん");
            sb.setLength(0);
            sb.append(s);
        }
    }

    /* JADX WARNING: Incorrect type for fill-array insn 0x0093, element type: char, insn element type: null */
    private void jaAdd(int r12, int[] r13) {
        /*
        r11 = this;
        r9 = r11.mTWs;
        r7 = r9.get(r12);
        r7 = (java.lang.StringBuilder) r7;
        r4 = r13.length;
        r8 = 0;
        r3 = 0;
    L_0x000b:
        if (r3 >= r4) goto L_0x040f;
    L_0x000d:
        if (r8 != 0) goto L_0x040f;
    L_0x000f:
        r9 = r13[r3];
        r10 = -1;
        if (r9 == r10) goto L_0x040f;
    L_0x0014:
        r5 = r7.length();
        r6 = r5;
        r9 = r13[r3];
        r2 = (char) r9;
    L_0x001c:
        if (r5 <= 0) goto L_0x0031;
    L_0x001e:
        r9 = r6 + -2;
        if (r5 <= r9) goto L_0x0031;
    L_0x0022:
        r9 = r5 + -1;
        r9 = r7.charAt(r9);
        r9 = com.klye.ime.latin.M.isLatin(r9);
        if (r9 == 0) goto L_0x0031;
    L_0x002e:
        r5 = r5 + -1;
        goto L_0x001c;
    L_0x0031:
        if (r6 != r5) goto L_0x008b;
    L_0x0033:
        switch(r2) {
            case 97: goto L_0x007c;
            case 101: goto L_0x0085;
            case 105: goto L_0x007f;
            case 111: goto L_0x0088;
            case 117: goto L_0x0082;
            default: goto L_0x0036;
        };
    L_0x0036:
        r9 = com.klye.ime.latin.M.shf();
        r2 = com.klye.ime.latin.M.toKata(r9, r2);
        r7.append(r2);
        r8 = 1;
    L_0x0042:
        r9 = r6 - r5;
        r10 = 1;
        if (r9 != r10) goto L_0x0079;
    L_0x0047:
        r0 = 0;
        r1 = 0;
        switch(r2) {
            case 39: goto L_0x0246;
            case 97: goto L_0x0260;
            case 101: goto L_0x035d;
            case 105: goto L_0x02b7;
            case 109: goto L_0x023c;
            case 110: goto L_0x023c;
            case 111: goto L_0x03b8;
            case 116: goto L_0x022f;
            case 117: goto L_0x030e;
            case 119: goto L_0x0253;
            default: goto L_0x004c;
        };
    L_0x004c:
        r9 = com.klye.ime.latin.M.irabc(r2);
        if (r9 == 0) goto L_0x005b;
    L_0x0052:
        r9 = r7.charAt(r5);
        if (r2 != r9) goto L_0x005b;
    L_0x0058:
        r0 = 12387; // 0x3063 float:1.7358E-41 double:6.12E-320;
        r1 = r2;
    L_0x005b:
        if (r0 == 0) goto L_0x0079;
    L_0x005d:
        r8 = 1;
        r7.setLength(r5);
        r9 = com.klye.ime.latin.M.shf();
        r9 = com.klye.ime.latin.M.toKata(r9, r0);
        r7.append(r9);
        if (r1 == 0) goto L_0x0079;
    L_0x006e:
        r9 = com.klye.ime.latin.M.shf();
        r9 = com.klye.ime.latin.M.toKata(r9, r1);
        r7.append(r9);
    L_0x0079:
        r3 = r3 + 1;
        goto L_0x000b;
    L_0x007c:
        r2 = 12354; // 0x3042 float:1.7312E-41 double:6.1037E-320;
        goto L_0x0036;
    L_0x007f:
        r2 = 12356; // 0x3044 float:1.7314E-41 double:6.1047E-320;
        goto L_0x0036;
    L_0x0082:
        r2 = 12358; // 0x3046 float:1.7317E-41 double:6.1057E-320;
        goto L_0x0036;
    L_0x0085:
        r2 = 12360; // 0x3048 float:1.732E-41 double:6.1067E-320;
        goto L_0x0036;
    L_0x0088:
        r2 = 12362; // 0x304a float:1.7323E-41 double:6.1076E-320;
        goto L_0x0036;
    L_0x008b:
        r9 = r6 - r5;
        r10 = 2;
        if (r9 != r10) goto L_0x0042;
    L_0x0090:
        r9 = 2;
        r0 = new char[r9];
        r0 = {0, 0};
        r9 = r5 + 1;
        r9 = r7.charAt(r9);
        switch(r9) {
            case 104: goto L_0x00d2;
            case 107: goto L_0x017d;
            case 115: goto L_0x01f6;
            case 119: goto L_0x019e;
            case 121: goto L_0x00d2;
            default: goto L_0x009f;
        };
    L_0x009f:
        r9 = 0;
        r9 = r0[r9];
        if (r9 == 0) goto L_0x022b;
    L_0x00a4:
        r9 = 1;
        r9 = r0[r9];
        if (r9 == 0) goto L_0x022b;
    L_0x00a9:
        r8 = 1;
        r7.setLength(r5);
        r9 = com.klye.ime.latin.M.shf();
        r10 = 0;
        r10 = r0[r10];
        r9 = com.klye.ime.latin.M.toKata(r9, r10);
        r7.append(r9);
        r9 = 1;
        r9 = r0[r9];
        r10 = 32;
        if (r9 == r10) goto L_0x0042;
    L_0x00c2:
        r9 = com.klye.ime.latin.M.shf();
        r10 = 1;
        r10 = r0[r10];
        r9 = com.klye.ime.latin.M.toKata(r9, r10);
        r7.append(r9);
        goto L_0x0042;
    L_0x00d2:
        switch(r2) {
            case 97: goto L_0x00e3;
            case 101: goto L_0x00f5;
            case 105: goto L_0x00fb;
            case 111: goto L_0x00ef;
            case 117: goto L_0x00e9;
            default: goto L_0x00d5;
        };
    L_0x00d5:
        r9 = r7.charAt(r5);
        switch(r9) {
            case 98: goto L_0x00dd;
            case 99: goto L_0x0119;
            case 100: goto L_0x011f;
            case 101: goto L_0x00dc;
            case 102: goto L_0x0142;
            case 103: goto L_0x0107;
            case 104: goto L_0x013b;
            case 105: goto L_0x00dc;
            case 106: goto L_0x00dc;
            case 107: goto L_0x0101;
            case 108: goto L_0x00dc;
            case 109: goto L_0x012d;
            case 110: goto L_0x0126;
            case 111: goto L_0x00dc;
            case 112: goto L_0x0134;
            case 113: goto L_0x00dc;
            case 114: goto L_0x0149;
            case 115: goto L_0x010d;
            case 116: goto L_0x0119;
            case 117: goto L_0x00dc;
            case 118: goto L_0x00dc;
            case 119: goto L_0x00dc;
            case 120: goto L_0x0150;
            case 121: goto L_0x00dc;
            case 122: goto L_0x0113;
            default: goto L_0x00dc;
        };
    L_0x00dc:
        goto L_0x009f;
    L_0x00dd:
        r9 = 0;
        r10 = 12403; // 0x3073 float:1.738E-41 double:6.128E-320;
        r0[r9] = r10;
        goto L_0x009f;
    L_0x00e3:
        r9 = 1;
        r10 = 12419; // 0x3083 float:1.7403E-41 double:6.136E-320;
        r0[r9] = r10;
        goto L_0x00d5;
    L_0x00e9:
        r9 = 1;
        r10 = 12421; // 0x3085 float:1.7406E-41 double:6.137E-320;
        r0[r9] = r10;
        goto L_0x00d5;
    L_0x00ef:
        r9 = 1;
        r10 = 12423; // 0x3087 float:1.7408E-41 double:6.138E-320;
        r0[r9] = r10;
        goto L_0x00d5;
    L_0x00f5:
        r9 = 1;
        r10 = 12359; // 0x3047 float:1.7319E-41 double:6.106E-320;
        r0[r9] = r10;
        goto L_0x00d5;
    L_0x00fb:
        r9 = 1;
        r10 = 32;
        r0[r9] = r10;
        goto L_0x00d5;
    L_0x0101:
        r9 = 0;
        r10 = 12365; // 0x304d float:1.7327E-41 double:6.109E-320;
        r0[r9] = r10;
        goto L_0x009f;
    L_0x0107:
        r9 = 0;
        r10 = 12366; // 0x304e float:1.7328E-41 double:6.1096E-320;
        r0[r9] = r10;
        goto L_0x009f;
    L_0x010d:
        r9 = 0;
        r10 = 12375; // 0x3057 float:1.7341E-41 double:6.114E-320;
        r0[r9] = r10;
        goto L_0x009f;
    L_0x0113:
        r9 = 0;
        r10 = 12376; // 0x3058 float:1.7342E-41 double:6.1146E-320;
        r0[r9] = r10;
        goto L_0x009f;
    L_0x0119:
        r9 = 0;
        r10 = 12385; // 0x3061 float:1.7355E-41 double:6.119E-320;
        r0[r9] = r10;
        goto L_0x009f;
    L_0x011f:
        r9 = 0;
        r10 = 12386; // 0x3062 float:1.7356E-41 double:6.1195E-320;
        r0[r9] = r10;
        goto L_0x009f;
    L_0x0126:
        r9 = 0;
        r10 = 12395; // 0x306b float:1.7369E-41 double:6.124E-320;
        r0[r9] = r10;
        goto L_0x009f;
    L_0x012d:
        r9 = 0;
        r10 = 12415; // 0x307f float:1.7397E-41 double:6.134E-320;
        r0[r9] = r10;
        goto L_0x009f;
    L_0x0134:
        r9 = 0;
        r10 = 12404; // 0x3074 float:1.7382E-41 double:6.1284E-320;
        r0[r9] = r10;
        goto L_0x009f;
    L_0x013b:
        r9 = 0;
        r10 = 12402; // 0x3072 float:1.7379E-41 double:6.1274E-320;
        r0[r9] = r10;
        goto L_0x009f;
    L_0x0142:
        r9 = 0;
        r10 = 12405; // 0x3075 float:1.7383E-41 double:6.129E-320;
        r0[r9] = r10;
        goto L_0x009f;
    L_0x0149:
        r9 = 0;
        r10 = 12426; // 0x308a float:1.7413E-41 double:6.1393E-320;
        r0[r9] = r10;
        goto L_0x009f;
    L_0x0150:
        r9 = 1;
        r10 = 32;
        r0[r9] = r10;
        switch(r2) {
            case 97: goto L_0x015a;
            case 101: goto L_0x0161;
            case 105: goto L_0x0168;
            case 111: goto L_0x016f;
            case 117: goto L_0x0176;
            default: goto L_0x0158;
        };
    L_0x0158:
        goto L_0x009f;
    L_0x015a:
        r9 = 0;
        r10 = 12419; // 0x3083 float:1.7403E-41 double:6.136E-320;
        r0[r9] = r10;
        goto L_0x009f;
    L_0x0161:
        r9 = 0;
        r10 = 12359; // 0x3047 float:1.7319E-41 double:6.106E-320;
        r0[r9] = r10;
        goto L_0x009f;
    L_0x0168:
        r9 = 0;
        r10 = 12355; // 0x3043 float:1.7313E-41 double:6.104E-320;
        r0[r9] = r10;
        goto L_0x009f;
    L_0x016f:
        r9 = 0;
        r10 = 12423; // 0x3087 float:1.7408E-41 double:6.138E-320;
        r0[r9] = r10;
        goto L_0x009f;
    L_0x0176:
        r9 = 0;
        r10 = 12421; // 0x3085 float:1.7406E-41 double:6.137E-320;
        r0[r9] = r10;
        goto L_0x009f;
    L_0x017d:
        r9 = r7.charAt(r5);
        switch(r9) {
            case 120: goto L_0x0186;
            default: goto L_0x0184;
        };
    L_0x0184:
        goto L_0x009f;
    L_0x0186:
        r9 = 1;
        r10 = 32;
        r0[r9] = r10;
        switch(r2) {
            case 97: goto L_0x0190;
            case 101: goto L_0x0197;
            default: goto L_0x018e;
        };
    L_0x018e:
        goto L_0x009f;
    L_0x0190:
        r9 = 0;
        r10 = 12437; // 0x3095 float:1.7428E-41 double:6.1447E-320;
        r0[r9] = r10;
        goto L_0x009f;
    L_0x0197:
        r9 = 0;
        r10 = 12438; // 0x3096 float:1.743E-41 double:6.145E-320;
        r0[r9] = r10;
        goto L_0x009f;
    L_0x019e:
        r9 = r7.charAt(r5);
        switch(r9) {
            case 99: goto L_0x01c9;
            case 100: goto L_0x01cf;
            case 101: goto L_0x01a5;
            case 102: goto L_0x01d5;
            case 103: goto L_0x01b7;
            case 104: goto L_0x01a5;
            case 105: goto L_0x01a5;
            case 106: goto L_0x01a5;
            case 107: goto L_0x01b1;
            case 108: goto L_0x01db;
            case 109: goto L_0x01a5;
            case 110: goto L_0x01a5;
            case 111: goto L_0x01a5;
            case 112: goto L_0x01a5;
            case 113: goto L_0x01a5;
            case 114: goto L_0x01db;
            case 115: goto L_0x01bd;
            case 116: goto L_0x01c9;
            case 117: goto L_0x01a5;
            case 118: goto L_0x01a5;
            case 119: goto L_0x01a5;
            case 120: goto L_0x01a5;
            case 121: goto L_0x01a5;
            case 122: goto L_0x01c3;
            default: goto L_0x01a5;
        };
    L_0x01a5:
        switch(r2) {
            case 97: goto L_0x01aa;
            case 101: goto L_0x01e1;
            case 105: goto L_0x01e8;
            case 111: goto L_0x01ef;
            default: goto L_0x01a8;
        };
    L_0x01a8:
        goto L_0x009f;
    L_0x01aa:
        r9 = 1;
        r10 = 12353; // 0x3041 float:1.731E-41 double:6.103E-320;
        r0[r9] = r10;
        goto L_0x009f;
    L_0x01b1:
        r9 = 0;
        r10 = 12367; // 0x304f float:1.733E-41 double:6.11E-320;
        r0[r9] = r10;
        goto L_0x01a5;
    L_0x01b7:
        r9 = 0;
        r10 = 12368; // 0x3050 float:1.7331E-41 double:6.1106E-320;
        r0[r9] = r10;
        goto L_0x01a5;
    L_0x01bd:
        r9 = 0;
        r10 = 12377; // 0x3059 float:1.7344E-41 double:6.115E-320;
        r0[r9] = r10;
        goto L_0x01a5;
    L_0x01c3:
        r9 = 0;
        r10 = 12378; // 0x305a float:1.7345E-41 double:6.1155E-320;
        r0[r9] = r10;
        goto L_0x01a5;
    L_0x01c9:
        r9 = 0;
        r10 = 12392; // 0x3068 float:1.7365E-41 double:6.1225E-320;
        r0[r9] = r10;
        goto L_0x01a5;
    L_0x01cf:
        r9 = 0;
        r10 = 12393; // 0x3069 float:1.7366E-41 double:6.123E-320;
        r0[r9] = r10;
        goto L_0x01a5;
    L_0x01d5:
        r9 = 0;
        r10 = 12405; // 0x3075 float:1.7383E-41 double:6.129E-320;
        r0[r9] = r10;
        goto L_0x01a5;
    L_0x01db:
        r9 = 0;
        r10 = 12426; // 0x308a float:1.7413E-41 double:6.1393E-320;
        r0[r9] = r10;
        goto L_0x01a5;
    L_0x01e1:
        r9 = 1;
        r10 = 12359; // 0x3047 float:1.7319E-41 double:6.106E-320;
        r0[r9] = r10;
        goto L_0x009f;
    L_0x01e8:
        r9 = 1;
        r10 = 12355; // 0x3043 float:1.7313E-41 double:6.104E-320;
        r0[r9] = r10;
        goto L_0x009f;
    L_0x01ef:
        r9 = 1;
        r10 = 12361; // 0x3049 float:1.7321E-41 double:6.107E-320;
        r0[r9] = r10;
        goto L_0x009f;
    L_0x01f6:
        r9 = r7.charAt(r5);
        switch(r9) {
            case 116: goto L_0x0209;
            default: goto L_0x01fd;
        };
    L_0x01fd:
        switch(r2) {
            case 97: goto L_0x0202;
            case 101: goto L_0x020f;
            case 105: goto L_0x0216;
            case 111: goto L_0x021d;
            case 117: goto L_0x0224;
            default: goto L_0x0200;
        };
    L_0x0200:
        goto L_0x009f;
    L_0x0202:
        r9 = 1;
        r10 = 12353; // 0x3041 float:1.731E-41 double:6.103E-320;
        r0[r9] = r10;
        goto L_0x009f;
    L_0x0209:
        r9 = 0;
        r10 = 12388; // 0x3064 float:1.7359E-41 double:6.1205E-320;
        r0[r9] = r10;
        goto L_0x01fd;
    L_0x020f:
        r9 = 1;
        r10 = 12359; // 0x3047 float:1.7319E-41 double:6.106E-320;
        r0[r9] = r10;
        goto L_0x009f;
    L_0x0216:
        r9 = 1;
        r10 = 12355; // 0x3043 float:1.7313E-41 double:6.104E-320;
        r0[r9] = r10;
        goto L_0x009f;
    L_0x021d:
        r9 = 1;
        r10 = 12361; // 0x3049 float:1.7321E-41 double:6.107E-320;
        r0[r9] = r10;
        goto L_0x009f;
    L_0x0224:
        r9 = 1;
        r10 = 32;
        r0[r9] = r10;
        goto L_0x009f;
    L_0x022b:
        r5 = r5 + 1;
        goto L_0x0042;
    L_0x022f:
        r9 = r7.charAt(r5);
        switch(r9) {
            case 120: goto L_0x0238;
            default: goto L_0x0236;
        };
    L_0x0236:
        goto L_0x004c;
    L_0x0238:
        r0 = 12387; // 0x3063 float:1.7358E-41 double:6.12E-320;
        goto L_0x004c;
    L_0x023c:
        r9 = r7.charAt(r5);
        if (r2 != r9) goto L_0x005b;
    L_0x0242:
        r0 = 12435; // 0x3093 float:1.7425E-41 double:6.1437E-320;
        goto L_0x005b;
    L_0x0246:
        r9 = r7.charAt(r5);
        switch(r9) {
            case 109: goto L_0x024f;
            case 110: goto L_0x024f;
            default: goto L_0x024d;
        };
    L_0x024d:
        goto L_0x005b;
    L_0x024f:
        r0 = 12435; // 0x3093 float:1.7425E-41 double:6.1437E-320;
        goto L_0x005b;
    L_0x0253:
        r9 = r7.charAt(r5);
        switch(r9) {
            case 120: goto L_0x025c;
            default: goto L_0x025a;
        };
    L_0x025a:
        goto L_0x005b;
    L_0x025c:
        r0 = 12430; // 0x308e float:1.7418E-41 double:6.141E-320;
        goto L_0x005b;
    L_0x0260:
        r9 = r7.charAt(r5);
        switch(r9) {
            case 98: goto L_0x0269;
            case 99: goto L_0x0271;
            case 100: goto L_0x0285;
            case 101: goto L_0x0267;
            case 102: goto L_0x0299;
            case 103: goto L_0x0275;
            case 104: goto L_0x0295;
            case 105: goto L_0x0267;
            case 106: goto L_0x02ab;
            case 107: goto L_0x0271;
            case 108: goto L_0x0267;
            case 109: goto L_0x028d;
            case 110: goto L_0x0289;
            case 111: goto L_0x0267;
            case 112: goto L_0x0291;
            case 113: goto L_0x0267;
            case 114: goto L_0x029f;
            case 115: goto L_0x0279;
            case 116: goto L_0x0281;
            case 117: goto L_0x0267;
            case 118: goto L_0x02b1;
            case 119: goto L_0x02a3;
            case 120: goto L_0x026d;
            case 121: goto L_0x02a7;
            case 122: goto L_0x027d;
            default: goto L_0x0267;
        };
    L_0x0267:
        goto L_0x005b;
    L_0x0269:
        r0 = 12400; // 0x3070 float:1.7376E-41 double:6.1264E-320;
        goto L_0x005b;
    L_0x026d:
        r0 = 12353; // 0x3041 float:1.731E-41 double:6.103E-320;
        goto L_0x005b;
    L_0x0271:
        r0 = 12363; // 0x304b float:1.7324E-41 double:6.108E-320;
        goto L_0x005b;
    L_0x0275:
        r0 = 12364; // 0x304c float:1.7326E-41 double:6.1086E-320;
        goto L_0x005b;
    L_0x0279:
        r0 = 12373; // 0x3055 float:1.7338E-41 double:6.113E-320;
        goto L_0x005b;
    L_0x027d:
        r0 = 12374; // 0x3056 float:1.734E-41 double:6.1136E-320;
        goto L_0x005b;
    L_0x0281:
        r0 = 12383; // 0x305f float:1.7352E-41 double:6.118E-320;
        goto L_0x005b;
    L_0x0285:
        r0 = 12384; // 0x3060 float:1.7354E-41 double:6.1185E-320;
        goto L_0x005b;
    L_0x0289:
        r0 = 12394; // 0x306a float:1.7368E-41 double:6.1234E-320;
        goto L_0x005b;
    L_0x028d:
        r0 = 12414; // 0x307e float:1.7396E-41 double:6.1333E-320;
        goto L_0x005b;
    L_0x0291:
        r0 = 12401; // 0x3071 float:1.7378E-41 double:6.127E-320;
        goto L_0x005b;
    L_0x0295:
        r0 = 12399; // 0x306f float:1.7375E-41 double:6.126E-320;
        goto L_0x005b;
    L_0x0299:
        r0 = 12405; // 0x3075 float:1.7383E-41 double:6.129E-320;
        r1 = 12353; // 0x3041 float:1.731E-41 double:6.103E-320;
        goto L_0x005b;
    L_0x029f:
        r0 = 12425; // 0x3089 float:1.7411E-41 double:6.139E-320;
        goto L_0x005b;
    L_0x02a3:
        r0 = 12431; // 0x308f float:1.742E-41 double:6.1417E-320;
        goto L_0x005b;
    L_0x02a7:
        r0 = 12420; // 0x3084 float:1.7404E-41 double:6.1363E-320;
        goto L_0x005b;
    L_0x02ab:
        r0 = 12376; // 0x3058 float:1.7342E-41 double:6.1146E-320;
        r1 = 12419; // 0x3083 float:1.7403E-41 double:6.136E-320;
        goto L_0x005b;
    L_0x02b1:
        r0 = 12436; // 0x3094 float:1.7427E-41 double:6.144E-320;
        r1 = 12353; // 0x3041 float:1.731E-41 double:6.103E-320;
        goto L_0x005b;
    L_0x02b7:
        r9 = r7.charAt(r5);
        switch(r9) {
            case 98: goto L_0x02c0;
            case 99: goto L_0x02d0;
            case 100: goto L_0x02dc;
            case 101: goto L_0x02be;
            case 102: goto L_0x02f0;
            case 103: goto L_0x02cc;
            case 104: goto L_0x02ec;
            case 105: goto L_0x02be;
            case 106: goto L_0x0304;
            case 107: goto L_0x02c8;
            case 108: goto L_0x02be;
            case 109: goto L_0x02e4;
            case 110: goto L_0x02e0;
            case 111: goto L_0x02be;
            case 112: goto L_0x02e8;
            case 113: goto L_0x02be;
            case 114: goto L_0x02f6;
            case 115: goto L_0x02d0;
            case 116: goto L_0x02d8;
            case 117: goto L_0x02be;
            case 118: goto L_0x0308;
            case 119: goto L_0x02fa;
            case 120: goto L_0x02c4;
            case 121: goto L_0x0300;
            case 122: goto L_0x02d4;
            default: goto L_0x02be;
        };
    L_0x02be:
        goto L_0x005b;
    L_0x02c0:
        r0 = 12403; // 0x3073 float:1.738E-41 double:6.128E-320;
        goto L_0x005b;
    L_0x02c4:
        r0 = 12355; // 0x3043 float:1.7313E-41 double:6.104E-320;
        goto L_0x005b;
    L_0x02c8:
        r0 = 12365; // 0x304d float:1.7327E-41 double:6.109E-320;
        goto L_0x005b;
    L_0x02cc:
        r0 = 12366; // 0x304e float:1.7328E-41 double:6.1096E-320;
        goto L_0x005b;
    L_0x02d0:
        r0 = 12375; // 0x3057 float:1.7341E-41 double:6.114E-320;
        goto L_0x005b;
    L_0x02d4:
        r0 = 12376; // 0x3058 float:1.7342E-41 double:6.1146E-320;
        goto L_0x005b;
    L_0x02d8:
        r0 = 12385; // 0x3061 float:1.7355E-41 double:6.119E-320;
        goto L_0x005b;
    L_0x02dc:
        r0 = 12386; // 0x3062 float:1.7356E-41 double:6.1195E-320;
        goto L_0x005b;
    L_0x02e0:
        r0 = 12395; // 0x306b float:1.7369E-41 double:6.124E-320;
        goto L_0x005b;
    L_0x02e4:
        r0 = 12415; // 0x307f float:1.7397E-41 double:6.134E-320;
        goto L_0x005b;
    L_0x02e8:
        r0 = 12404; // 0x3074 float:1.7382E-41 double:6.1284E-320;
        goto L_0x005b;
    L_0x02ec:
        r0 = 12402; // 0x3072 float:1.7379E-41 double:6.1274E-320;
        goto L_0x005b;
    L_0x02f0:
        r0 = 12405; // 0x3075 float:1.7383E-41 double:6.129E-320;
        r1 = 12355; // 0x3043 float:1.7313E-41 double:6.104E-320;
        goto L_0x005b;
    L_0x02f6:
        r0 = 12426; // 0x308a float:1.7413E-41 double:6.1393E-320;
        goto L_0x005b;
    L_0x02fa:
        r0 = 12358; // 0x3046 float:1.7317E-41 double:6.1057E-320;
        r1 = 12355; // 0x3043 float:1.7313E-41 double:6.104E-320;
        goto L_0x005b;
    L_0x0300:
        r0 = 12356; // 0x3044 float:1.7314E-41 double:6.1047E-320;
        goto L_0x005b;
    L_0x0304:
        r0 = 12376; // 0x3058 float:1.7342E-41 double:6.1146E-320;
        goto L_0x005b;
    L_0x0308:
        r0 = 12436; // 0x3094 float:1.7427E-41 double:6.144E-320;
        r1 = 12355; // 0x3043 float:1.7313E-41 double:6.104E-320;
        goto L_0x005b;
    L_0x030e:
        r9 = r7.charAt(r5);
        switch(r9) {
            case 98: goto L_0x0317;
            case 99: goto L_0x031f;
            case 100: goto L_0x0333;
            case 101: goto L_0x0315;
            case 102: goto L_0x0343;
            case 103: goto L_0x0323;
            case 104: goto L_0x0343;
            case 105: goto L_0x0315;
            case 106: goto L_0x0353;
            case 107: goto L_0x031f;
            case 108: goto L_0x0315;
            case 109: goto L_0x033b;
            case 110: goto L_0x0337;
            case 111: goto L_0x0315;
            case 112: goto L_0x033f;
            case 113: goto L_0x0315;
            case 114: goto L_0x0347;
            case 115: goto L_0x0327;
            case 116: goto L_0x032f;
            case 117: goto L_0x0315;
            case 118: goto L_0x0359;
            case 119: goto L_0x034b;
            case 120: goto L_0x031b;
            case 121: goto L_0x034f;
            case 122: goto L_0x032b;
            default: goto L_0x0315;
        };
    L_0x0315:
        goto L_0x005b;
    L_0x0317:
        r0 = 12406; // 0x3076 float:1.7385E-41 double:6.1294E-320;
        goto L_0x005b;
    L_0x031b:
        r0 = 12357; // 0x3045 float:1.7316E-41 double:6.105E-320;
        goto L_0x005b;
    L_0x031f:
        r0 = 12367; // 0x304f float:1.733E-41 double:6.11E-320;
        goto L_0x005b;
    L_0x0323:
        r0 = 12368; // 0x3050 float:1.7331E-41 double:6.1106E-320;
        goto L_0x005b;
    L_0x0327:
        r0 = 12377; // 0x3059 float:1.7344E-41 double:6.115E-320;
        goto L_0x005b;
    L_0x032b:
        r0 = 12378; // 0x305a float:1.7345E-41 double:6.1155E-320;
        goto L_0x005b;
    L_0x032f:
        r0 = 12388; // 0x3064 float:1.7359E-41 double:6.1205E-320;
        goto L_0x005b;
    L_0x0333:
        r0 = 12389; // 0x3065 float:1.736E-41 double:6.121E-320;
        goto L_0x005b;
    L_0x0337:
        r0 = 12396; // 0x306c float:1.737E-41 double:6.1244E-320;
        goto L_0x005b;
    L_0x033b:
        r0 = 12416; // 0x3080 float:1.7399E-41 double:6.1343E-320;
        goto L_0x005b;
    L_0x033f:
        r0 = 12407; // 0x3077 float:1.7386E-41 double:6.13E-320;
        goto L_0x005b;
    L_0x0343:
        r0 = 12405; // 0x3075 float:1.7383E-41 double:6.129E-320;
        goto L_0x005b;
    L_0x0347:
        r0 = 12427; // 0x308b float:1.7414E-41 double:6.14E-320;
        goto L_0x005b;
    L_0x034b:
        r0 = 12358; // 0x3046 float:1.7317E-41 double:6.1057E-320;
        goto L_0x005b;
    L_0x034f:
        r0 = 12422; // 0x3086 float:1.7407E-41 double:6.1373E-320;
        goto L_0x005b;
    L_0x0353:
        r0 = 12376; // 0x3058 float:1.7342E-41 double:6.1146E-320;
        r1 = 12421; // 0x3085 float:1.7406E-41 double:6.137E-320;
        goto L_0x005b;
    L_0x0359:
        r0 = 12436; // 0x3094 float:1.7427E-41 double:6.144E-320;
        goto L_0x005b;
    L_0x035d:
        r9 = r7.charAt(r5);
        switch(r9) {
            case 98: goto L_0x0366;
            case 99: goto L_0x0376;
            case 100: goto L_0x0382;
            case 101: goto L_0x0364;
            case 102: goto L_0x0396;
            case 103: goto L_0x0372;
            case 104: goto L_0x0392;
            case 105: goto L_0x0364;
            case 106: goto L_0x03ac;
            case 107: goto L_0x036e;
            case 108: goto L_0x0364;
            case 109: goto L_0x038a;
            case 110: goto L_0x0386;
            case 111: goto L_0x0364;
            case 112: goto L_0x038e;
            case 113: goto L_0x0364;
            case 114: goto L_0x039c;
            case 115: goto L_0x0376;
            case 116: goto L_0x037e;
            case 117: goto L_0x0364;
            case 118: goto L_0x03b2;
            case 119: goto L_0x03a0;
            case 120: goto L_0x036a;
            case 121: goto L_0x03a6;
            case 122: goto L_0x037a;
            default: goto L_0x0364;
        };
    L_0x0364:
        goto L_0x005b;
    L_0x0366:
        r0 = 12409; // 0x3079 float:1.7389E-41 double:6.131E-320;
        goto L_0x005b;
    L_0x036a:
        r0 = 12359; // 0x3047 float:1.7319E-41 double:6.106E-320;
        goto L_0x005b;
    L_0x036e:
        r0 = 12369; // 0x3051 float:1.7333E-41 double:6.111E-320;
        goto L_0x005b;
    L_0x0372:
        r0 = 12370; // 0x3052 float:1.7334E-41 double:6.1116E-320;
        goto L_0x005b;
    L_0x0376:
        r0 = 12379; // 0x305b float:1.7347E-41 double:6.116E-320;
        goto L_0x005b;
    L_0x037a:
        r0 = 12380; // 0x305c float:1.7348E-41 double:6.1165E-320;
        goto L_0x005b;
    L_0x037e:
        r0 = 12390; // 0x3066 float:1.7362E-41 double:6.1215E-320;
        goto L_0x005b;
    L_0x0382:
        r0 = 12391; // 0x3067 float:1.7363E-41 double:6.122E-320;
        goto L_0x005b;
    L_0x0386:
        r0 = 12397; // 0x306d float:1.7372E-41 double:6.125E-320;
        goto L_0x005b;
    L_0x038a:
        r0 = 12417; // 0x3081 float:1.74E-41 double:6.135E-320;
        goto L_0x005b;
    L_0x038e:
        r0 = 12410; // 0x307a float:1.739E-41 double:6.1314E-320;
        goto L_0x005b;
    L_0x0392:
        r0 = 12408; // 0x3078 float:1.7387E-41 double:6.1304E-320;
        goto L_0x005b;
    L_0x0396:
        r0 = 12405; // 0x3075 float:1.7383E-41 double:6.129E-320;
        r1 = 12359; // 0x3047 float:1.7319E-41 double:6.106E-320;
        goto L_0x005b;
    L_0x039c:
        r0 = 12428; // 0x308c float:1.7415E-41 double:6.1402E-320;
        goto L_0x005b;
    L_0x03a0:
        r0 = 12358; // 0x3046 float:1.7317E-41 double:6.1057E-320;
        r1 = 12359; // 0x3047 float:1.7319E-41 double:6.106E-320;
        goto L_0x005b;
    L_0x03a6:
        r0 = 12356; // 0x3044 float:1.7314E-41 double:6.1047E-320;
        r1 = 12359; // 0x3047 float:1.7319E-41 double:6.106E-320;
        goto L_0x005b;
    L_0x03ac:
        r0 = 12376; // 0x3058 float:1.7342E-41 double:6.1146E-320;
        r1 = 12359; // 0x3047 float:1.7319E-41 double:6.106E-320;
        goto L_0x005b;
    L_0x03b2:
        r0 = 12436; // 0x3094 float:1.7427E-41 double:6.144E-320;
        r1 = 12359; // 0x3047 float:1.7319E-41 double:6.106E-320;
        goto L_0x005b;
    L_0x03b8:
        r9 = r7.charAt(r5);
        switch(r9) {
            case 98: goto L_0x03c1;
            case 99: goto L_0x03c9;
            case 100: goto L_0x03dd;
            case 101: goto L_0x03bf;
            case 102: goto L_0x03f1;
            case 103: goto L_0x03cd;
            case 104: goto L_0x03ed;
            case 105: goto L_0x03bf;
            case 106: goto L_0x0403;
            case 107: goto L_0x03c9;
            case 108: goto L_0x03bf;
            case 109: goto L_0x03e5;
            case 110: goto L_0x03e1;
            case 111: goto L_0x03bf;
            case 112: goto L_0x03e9;
            case 113: goto L_0x03bf;
            case 114: goto L_0x03f7;
            case 115: goto L_0x03d1;
            case 116: goto L_0x03d9;
            case 117: goto L_0x03bf;
            case 118: goto L_0x0409;
            case 119: goto L_0x03fb;
            case 120: goto L_0x03c5;
            case 121: goto L_0x03ff;
            case 122: goto L_0x03d5;
            default: goto L_0x03bf;
        };
    L_0x03bf:
        goto L_0x005b;
    L_0x03c1:
        r0 = 12412; // 0x307c float:1.7393E-41 double:6.1323E-320;
        goto L_0x005b;
    L_0x03c5:
        r0 = 12361; // 0x3049 float:1.7321E-41 double:6.107E-320;
        goto L_0x005b;
    L_0x03c9:
        r0 = 12371; // 0x3053 float:1.7335E-41 double:6.112E-320;
        goto L_0x005b;
    L_0x03cd:
        r0 = 12372; // 0x3054 float:1.7337E-41 double:6.1126E-320;
        goto L_0x005b;
    L_0x03d1:
        r0 = 12381; // 0x305d float:1.735E-41 double:6.117E-320;
        goto L_0x005b;
    L_0x03d5:
        r0 = 12382; // 0x305e float:1.7351E-41 double:6.1175E-320;
        goto L_0x005b;
    L_0x03d9:
        r0 = 12392; // 0x3068 float:1.7365E-41 double:6.1225E-320;
        goto L_0x005b;
    L_0x03dd:
        r0 = 12393; // 0x3069 float:1.7366E-41 double:6.123E-320;
        goto L_0x005b;
    L_0x03e1:
        r0 = 12398; // 0x306e float:1.7373E-41 double:6.1254E-320;
        goto L_0x005b;
    L_0x03e5:
        r0 = 12418; // 0x3082 float:1.7401E-41 double:6.1353E-320;
        goto L_0x005b;
    L_0x03e9:
        r0 = 12413; // 0x307d float:1.7394E-41 double:6.133E-320;
        goto L_0x005b;
    L_0x03ed:
        r0 = 12411; // 0x307b float:1.7392E-41 double:6.132E-320;
        goto L_0x005b;
    L_0x03f1:
        r0 = 12405; // 0x3075 float:1.7383E-41 double:6.129E-320;
        r1 = 12361; // 0x3049 float:1.7321E-41 double:6.107E-320;
        goto L_0x005b;
    L_0x03f7:
        r0 = 12429; // 0x308d float:1.7417E-41 double:6.1407E-320;
        goto L_0x005b;
    L_0x03fb:
        r0 = 12434; // 0x3092 float:1.7424E-41 double:6.143E-320;
        goto L_0x005b;
    L_0x03ff:
        r0 = 12424; // 0x3088 float:1.741E-41 double:6.1383E-320;
        goto L_0x005b;
    L_0x0403:
        r0 = 12376; // 0x3058 float:1.7342E-41 double:6.1146E-320;
        r1 = 12423; // 0x3087 float:1.7408E-41 double:6.138E-320;
        goto L_0x005b;
    L_0x0409:
        r0 = 12436; // 0x3094 float:1.7427E-41 double:6.144E-320;
        r1 = 12361; // 0x3049 float:1.7321E-41 double:6.107E-320;
        goto L_0x005b;
    L_0x040f:
        if (r8 != 0) goto L_0x0418;
    L_0x0411:
        r9 = 0;
        r9 = r13[r9];
        r9 = (char) r9;
        r7.append(r9);
    L_0x0418:
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.klye.ime.latin.WordComposer.jaAdd(int, int[]):void");
    }

    private void jaIme(int[] c1) {
        int l = this.mTWs.size();
        if (l == 0) {
            this.mTWs.add(new StringBuilder());
            jaAdd(0, c1);
            return;
        }
        for (int i = 0; i < l; i++) {
            jaAdd(i, c1);
        }
    }

    public String[] gs() {
        if (this.mTWs != null) {
            int l = this.mTWs.size();
            if (l != 0) {
                String[] s = new String[l];
                for (int i = 0; i < l; i++) {
                    s[i] = ((StringBuilder) this.mTWs.get(i)).toString();
                }
                return s;
            }
        }
        return null;
    }

    public String[] kogs() {
        String s0 = ko2A();
        if (s0 != null) {
            int l = s0.length();
            if (l != 0) {
                String dc = koDC(s0.charAt(l - 1));
                String[] s = new String[(dc == null ? 1 : 2)];
                s[0] = s0;
                if (dc == null) {
                    return s;
                }
                s[1] = s0.substring(0, l - 1) + dc;
                return s;
            }
        }
        return null;
    }

    private String koDC(char c) {
        switch (c) {
            case 194:
                return "ÁÁ";
            case 195:
                return "ÁÕ";
            case 197:
                return "ÄØ";
            case 198:
                return "ÄÞ";
            case 202:
                return "ÉÁ";
            case 203:
                return "ÉÑ";
            case 204:
                return "ÉÒ";
            case 205:
                return "ÉÕ";
            case 206:
                return "ÉÜ";
            case 207:
                return "ÉÝ";
            case 208:
                return "ÉÞ";
            case 212:
                return "ÒÕ";
            case 214:
                return "ÕÕ";
            default:
                return null;
        }
    }

    public String ko2A() {
        int l = this.mTW.length();
        if (l == 0) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < l; i++) {
            ko2A(this.mTW.charAt(i), sb);
        }
        return sb.toString();
    }

    static char koI(int i) {
        String s = "ㄱㄲㄴㄷㄸㄹㅁㅂㅃㅅㅆㅇㅈㅉㅊㅋㅌㅍㅎ";
        return "ㄱㄲㄴㄷㄸㄹㅁㅂㅃㅅㅆㅇㅈㅉㅊㅋㅌㅍㅎ".charAt(i);
    }

    static char koM(int i) {
        String s = "ㅏㅐㅑㅒㅓㅔㅕㅖㅗㅘㅙㅚㅛㅜㅝㅞㅟㅠㅡㅢㅣ";
        return "ㅏㅐㅑㅒㅓㅔㅕㅖㅗㅘㅙㅚㅛㅜㅝㅞㅟㅠㅡㅢㅣ".charAt(i);
    }

    static char koF(int i) {
        String s = "㄰ㄱㄲㄳㄴㄵㄶㄷㄹㄺㄻㄼㄽㄾㄿㅀㅁㅂㅄㅅㅆㅇㅈㅊㅋㅌㅍㅎ";
        return "㄰ㄱㄲㄳㄴㄵㄶㄷㄹㄺㄻㄼㄽㄾㄿㅀㅁㅂㅄㅅㅆㅇㅈㅊㅋㅌㅍㅎ".charAt(i);
    }

    static char koB(char i) {
        return (char) ((i - 12592) + 192);
    }

    private void ko2A(char i, StringBuilder sb) {
        if (M.isJamo(i)) {
            sb.append(koB(i));
        } else if (H.IsHangul(i)) {
            i = (char) (i - 44032);
            sb.append(koB(koI((i / 588) % 19)));
            sb.append(koB(koM((i / 28) % 21)));
            int f = i % 28;
            if (f > 0) {
                sb.append(koB(koF(f)));
            }
        }
    }

    public boolean eq(CharSequence s) {
        return s.equals(this.mTW.toString());
    }

    public void modi() {
        int l = this.mTW.length();
        if (!M.aad && l >= 2) {
            char c2 = this.mTW.charAt(l - 2);
            char c3 = this.mTW.charAt(l - 1);
            char r = M.pdk(c2, c3, l >= 3 ? this.mTW.charAt(l - 3) : 0);
            if (r != 0) {
                if (c2 == '\'') {
                    deleteLast1();
                }
                deleteLast1();
                deleteLast1();
                if (M.ir(r, 772, 774)) {
                    add1(c3, null);
                    add1(r, null);
                } else if (r == 1) {
                    add1(c3, null);
                    add1(c2, null);
                } else {
                    add1(r, null);
                }
                modi();
            }
        }
    }

    public boolean isH() {
        return this.mTW.length() > 0 ? H.isH(this.mTW.charAt(0)) : false;
    }

    public char lastChar() {
        int l = this.mTW.length();
        return l == 0 ? 0 : this.mTW.charAt(l - 1);
    }

    public String gac() {
        return this.mTW.toString();
    }

    public void set(int k, int l) {
        try {
            String s = this.mTW.substring(k, l);
            this.mTW.setLength(0);
            this.mTW.append(s);
            this.mCodes = this.mCodes.subList(k, l);
        } catch (Throwable e) {
            M.l(e);
        }
    }

    public void jaK(StringBuilder sb, int j) {
        int l = this.mTW.length();
        sb.setLength(l);
        for (int i = 0; i < l; i++) {
            sb.setCharAt(i, M.toKata(this.mTW.charAt(i), j));
        }
    }

    public boolean fc(char fc) {
        int[] c = getCodesAt(0);
        char fc1 = Character.toLowerCase(fc);
        for (char c2 : c) {
            if (c2 == fc1) {
                return true;
            }
        }
        return false;
    }
}
