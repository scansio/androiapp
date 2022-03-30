package com.klye.ime.latin;

class ProximityKeyDetector extends KeyDetector {
    private static final int MAX_NEARBY_KEYS = 10;
    private int[] mDistances = new int[getMaxNearbyKeys()];

    ProximityKeyDetector() {
    }

    protected int getMaxNearbyKeys() {
        return M.arConAble() ? 16 : 10;
    }

    /* JADX WARNING: Missing block: B:54:0x0128, code:
            if (r8 >= r32.mProximityThresholdSquare) goto L_0x012a;
     */
    public int getKeyIndexAndNearbyCodes(int r33, int r34, int[] r35) {
        /*
        r32 = this;
        r17 = r32.getKeys();
        r28 = r32.getTouchX(r33);
        r0 = r32;
        r1 = r34;
        r29 = r0.getTouchY(r1);
        r25 = -1;
        r6 = -1;
        r0 = r32;
        r0 = r0.mProximityThresholdSquare;
        r30 = r0;
        r7 = r30 + 1;
        r0 = r32;
        r9 = r0.mDistances;
        r30 = com.klye.ime.latin.M.iv;
        r27 = r30.isShifted();
        r30 = 2147483647; // 0x7fffffff float:NaN double:1.060997895E-314;
        r0 = r30;
        java.util.Arrays.fill(r9, r0);	 Catch:{ Throwable -> 0x0197 }
        r0 = r32;
        r0 = r0.mKeyboard;	 Catch:{ Throwable -> 0x0197 }
        r30 = r0;
        r0 = r30;
        r1 = r28;
        r2 = r29;
        r23 = r0.getNearestKeys(r1, r2);	 Catch:{ Throwable -> 0x0197 }
        r0 = r23;
        r0 = r0.length;	 Catch:{ Throwable -> 0x0197 }
        r16 = r0;
        r11 = 0;
    L_0x0043:
        r0 = r16;
        if (r11 >= r0) goto L_0x0290;
    L_0x0047:
        r30 = r23[r11];	 Catch:{ Throwable -> 0x0197 }
        r15 = r17[r30];	 Catch:{ Throwable -> 0x0197 }
        r0 = r15.width;	 Catch:{ Throwable -> 0x0197 }
        r30 = r0;
        if (r30 != 0) goto L_0x0054;
    L_0x0051:
        r11 = r11 + 1;
        goto L_0x0043;
    L_0x0054:
        r8 = 0;
        r0 = r28;
        r1 = r29;
        r13 = r15.isInside(r0, r1);	 Catch:{ Throwable -> 0x0197 }
        if (r13 == 0) goto L_0x00ff;
    L_0x005f:
        r25 = r23[r11];	 Catch:{ Throwable -> 0x0197 }
        if (r35 == 0) goto L_0x00ff;
    L_0x0063:
        r0 = r15.popupCharacters;	 Catch:{ Throwable -> 0x0197 }
        r24 = r0;
        if (r24 == 0) goto L_0x00cb;
    L_0x0069:
        r30 = r24.length();	 Catch:{ Throwable -> 0x0197 }
        r0 = r35;
        r0 = r0.length;	 Catch:{ Throwable -> 0x0197 }
        r31 = r0;
        r22 = java.lang.Math.min(r30, r31);	 Catch:{ Throwable -> 0x0197 }
    L_0x0076:
        r30 = com.klye.ime.latin.M.bho;	 Catch:{ Throwable -> 0x0197 }
        if (r30 == 0) goto L_0x0092;
    L_0x007a:
        if (r27 != 0) goto L_0x0092;
    L_0x007c:
        r0 = r15.codes;	 Catch:{ Throwable -> 0x0197 }
        r30 = r0;
        r31 = 0;
        r30 = r30[r31];	 Catch:{ Throwable -> 0x0197 }
        r0 = r30;
        r4 = (char) r0;	 Catch:{ Throwable -> 0x0197 }
        r5 = com.klye.ime.latin.S.boshf(r4);	 Catch:{ Throwable -> 0x0197 }
        if (r5 == r4) goto L_0x0092;
    L_0x008d:
        r0 = r35;
        ins(r9, r0, r5);	 Catch:{ Throwable -> 0x0197 }
    L_0x0092:
        if (r22 <= 0) goto L_0x00ff;
    L_0x0094:
        r30 = com.klye.ime.latin.M.isAK();	 Catch:{ Throwable -> 0x0197 }
        if (r30 == 0) goto L_0x00ff;
    L_0x009a:
        r30 = com.klye.ime.latin.M.isT9Semi();	 Catch:{ Throwable -> 0x0197 }
        if (r30 == 0) goto L_0x01a0;
    L_0x00a0:
        r30 = com.klye.ime.latin.M.zt;	 Catch:{ Throwable -> 0x0197 }
        r31 = -1;
        r0 = r30;
        r1 = r31;
        if (r0 == r1) goto L_0x00ce;
    L_0x00aa:
        r0 = r15.modifier;	 Catch:{ Throwable -> 0x0197 }
        r30 = r0;
        if (r30 != 0) goto L_0x00ff;
    L_0x00b0:
        r0 = r15.label;	 Catch:{ Throwable -> 0x0197 }
        r26 = r0;
        r21 = r26.length();	 Catch:{ Throwable -> 0x0197 }
        r12 = 0;
    L_0x00b9:
        r0 = r21;
        if (r12 >= r0) goto L_0x00ff;
    L_0x00bd:
        r0 = r26;
        r5 = r0.charAt(r12);	 Catch:{ Throwable -> 0x0197 }
        r0 = r35;
        ins(r9, r0, r5);	 Catch:{ Throwable -> 0x0197 }
        r12 = r12 + 1;
        goto L_0x00b9;
    L_0x00cb:
        r22 = 0;
        goto L_0x0076;
    L_0x00ce:
        r30 = 1;
        r0 = r22;
        r1 = r30;
        if (r0 <= r1) goto L_0x00ff;
    L_0x00d6:
        r14 = 0;
    L_0x00d7:
        r0 = r9.length;	 Catch:{ Throwable -> 0x0197 }
        r30 = r0;
        r0 = r30;
        if (r14 >= r0) goto L_0x00ff;
    L_0x00de:
        r30 = r9[r14];	 Catch:{ Throwable -> 0x0197 }
        r0 = r30;
        if (r0 <= r8) goto L_0x019c;
    L_0x00e4:
        r12 = 0;
    L_0x00e5:
        r30 = r22 + -1;
        r0 = r30;
        if (r12 >= r0) goto L_0x00f6;
    L_0x00eb:
        r0 = r24;
        r30 = r0.charAt(r12);	 Catch:{ Throwable -> 0x0197 }
        r35[r12] = r30;	 Catch:{ Throwable -> 0x0197 }
        r12 = r12 + 1;
        goto L_0x00e5;
    L_0x00f6:
        r30 = r14 + r22;
        r30 = r30 + -1;
        r0 = r30;
        java.util.Arrays.fill(r9, r14, r0, r8);	 Catch:{ Throwable -> 0x0197 }
    L_0x00ff:
        r0 = r32;
        r0 = r0.mProximityCorrectOn;	 Catch:{ Throwable -> 0x0197 }
        r30 = r0;
        if (r30 == 0) goto L_0x012a;
    L_0x0107:
        r30 = com.klye.ime.latin.M.fk();	 Catch:{ Throwable -> 0x0197 }
        if (r30 == 0) goto L_0x012a;
    L_0x010d:
        r30 = com.klye.ime.latin.M.mLC;	 Catch:{ Throwable -> 0x0197 }
        r31 = 6946913; // 0x6a0061 float:9.734699E-39 double:3.432231E-317;
        r0 = r30;
        r1 = r31;
        if (r0 == r1) goto L_0x012a;
    L_0x0118:
        r0 = r28;
        r1 = r29;
        r8 = r15.squaredDistanceFrom(r0, r1);	 Catch:{ Throwable -> 0x0197 }
        r0 = r32;
        r0 = r0.mProximityThresholdSquare;	 Catch:{ Throwable -> 0x0197 }
        r30 = r0;
        r0 = r30;
        if (r8 < r0) goto L_0x013c;
    L_0x012a:
        if (r13 == 0) goto L_0x0051;
    L_0x012c:
        r30 = com.klye.ime.latin.M.zt;	 Catch:{ Throwable -> 0x0197 }
        r31 = -1;
        r0 = r30;
        r1 = r31;
        if (r0 == r1) goto L_0x013c;
    L_0x0136:
        r30 = com.klye.ime.latin.M.isT9Semi();	 Catch:{ Throwable -> 0x0197 }
        if (r30 != 0) goto L_0x0051;
    L_0x013c:
        r0 = r15.codes;	 Catch:{ Throwable -> 0x0197 }
        r30 = r0;
        r31 = 0;
        r30 = r30[r31];	 Catch:{ Throwable -> 0x0197 }
        r31 = 32;
        r0 = r30;
        r1 = r31;
        if (r0 <= r1) goto L_0x0051;
    L_0x014c:
        r30 = com.klye.ime.latin.M.isAK();	 Catch:{ Throwable -> 0x0197 }
        if (r30 == 0) goto L_0x0051;
    L_0x0152:
        r0 = r15.x;	 Catch:{ Throwable -> 0x0197 }
        r30 = r0;
        r0 = r15.width;	 Catch:{ Throwable -> 0x0197 }
        r31 = r0;
        r31 = r31 / 2;
        r30 = r30 + r31;
        r19 = r28 - r30;
        r0 = r15.y;	 Catch:{ Throwable -> 0x0197 }
        r30 = r0;
        r0 = r15.height;	 Catch:{ Throwable -> 0x0197 }
        r31 = r0;
        r31 = r31 / 2;
        r30 = r30 + r31;
        r20 = r29 - r30;
        r30 = r19 * r19;
        r31 = r20 * r20;
        r18 = r30 + r31;
        if (r8 >= r7) goto L_0x0179;
    L_0x0176:
        r7 = r8;
        r6 = r23[r11];	 Catch:{ Throwable -> 0x0197 }
    L_0x0179:
        if (r35 == 0) goto L_0x0051;
    L_0x017b:
        if (r27 == 0) goto L_0x0236;
    L_0x017d:
        r30 = com.klye.ime.latin.M.tlpc;	 Catch:{ Throwable -> 0x0197 }
        if (r30 == 0) goto L_0x0236;
    L_0x0181:
        r5 = 0;
        r30 = com.klye.ime.latin.M.nm;	 Catch:{ Throwable -> 0x0197 }
        if (r30 == 0) goto L_0x0223;
    L_0x0186:
        r0 = r15.popupCharacters;	 Catch:{ Throwable -> 0x0197 }
        r30 = r0;
        r31 = 0;
        r5 = r30.charAt(r31);	 Catch:{ Throwable -> 0x0197 }
    L_0x0190:
        r0 = r35;
        ins(r9, r0, r5);	 Catch:{ Throwable -> 0x0197 }
        goto L_0x0051;
    L_0x0197:
        r10 = move-exception;
        r10.printStackTrace();
    L_0x019b:
        return r25;
    L_0x019c:
        r14 = r14 + 1;
        goto L_0x00d7;
    L_0x01a0:
        r30 = com.klye.ime.latin.M.tlpc;	 Catch:{ Throwable -> 0x0197 }
        if (r30 != 0) goto L_0x01b4;
    L_0x01a4:
        r0 = r15.codes;	 Catch:{ Throwable -> 0x0197 }
        r30 = r0;
        r31 = 0;
        r30 = r30[r31];	 Catch:{ Throwable -> 0x0197 }
        r31 = 39;
        r0 = r30;
        r1 = r31;
        if (r0 != r1) goto L_0x01dc;
    L_0x01b4:
        r12 = 0;
    L_0x01b5:
        r0 = r22;
        if (r12 >= r0) goto L_0x00ff;
    L_0x01b9:
        r0 = r24;
        r5 = r0.charAt(r12);	 Catch:{ Throwable -> 0x0197 }
        r30 = com.klye.ime.latin.M.bho;	 Catch:{ Throwable -> 0x0197 }
        if (r30 == 0) goto L_0x01c9;
    L_0x01c3:
        if (r27 == 0) goto L_0x01c9;
    L_0x01c5:
        r5 = com.klye.ime.latin.S.toUpper(r5);	 Catch:{ Throwable -> 0x0197 }
    L_0x01c9:
        r0 = r35;
        ins(r9, r0, r5);	 Catch:{ Throwable -> 0x0197 }
        r30 = r22 + -1;
        r0 = r30;
        if (r12 != r0) goto L_0x01d9;
    L_0x01d4:
        r0 = r35;
        ins(r9, r0, r5);	 Catch:{ Throwable -> 0x0197 }
    L_0x01d9:
        r12 = r12 + 1;
        goto L_0x01b5;
    L_0x01dc:
        r30 = com.klye.ime.latin.M.isM;	 Catch:{ Throwable -> 0x0197 }
        if (r30 == 0) goto L_0x0215;
    L_0x01e0:
        r0 = r25;
        r5 = com.klye.ime.latin.M.hint1(r0, r15);	 Catch:{ Throwable -> 0x0197 }
    L_0x01e6:
        if (r5 != 0) goto L_0x0217;
    L_0x01e8:
        r30 = 0;
        r0 = r24;
        r1 = r30;
        r5 = r0.charAt(r1);	 Catch:{ Throwable -> 0x0197 }
    L_0x01f2:
        if (r5 == 0) goto L_0x00ff;
    L_0x01f4:
        r30 = com.klye.ime.latin.M.scc(r5);	 Catch:{ Throwable -> 0x0197 }
        if (r30 != 0) goto L_0x00ff;
    L_0x01fa:
        r30 = com.klye.ime.latin.M.zt;	 Catch:{ Throwable -> 0x0197 }
        r31 = -1;
        r0 = r30;
        r1 = r31;
        if (r0 != r1) goto L_0x00ff;
    L_0x0204:
        r30 = com.klye.ime.latin.M.bho;	 Catch:{ Throwable -> 0x0197 }
        if (r30 == 0) goto L_0x020e;
    L_0x0208:
        if (r27 == 0) goto L_0x020e;
    L_0x020a:
        r5 = com.klye.ime.latin.S.toUpper(r5);	 Catch:{ Throwable -> 0x0197 }
    L_0x020e:
        r0 = r35;
        ins(r9, r0, r5);	 Catch:{ Throwable -> 0x0197 }
        goto L_0x00ff;
    L_0x0215:
        r5 = 0;
        goto L_0x01e6;
    L_0x0217:
        r30 = com.klye.ime.latin.M.irn(r5);	 Catch:{ Throwable -> 0x0197 }
        if (r30 != 0) goto L_0x01f2;
    L_0x021d:
        r30 = com.klye.ime.latin.M.tlpc;	 Catch:{ Throwable -> 0x0197 }
        if (r30 != 0) goto L_0x01f2;
    L_0x0221:
        r5 = 0;
        goto L_0x01f2;
    L_0x0223:
        r0 = r15.codes;	 Catch:{ Throwable -> 0x0197 }
        r30 = r0;
        r31 = 0;
        r30 = r30[r31];	 Catch:{ Throwable -> 0x0197 }
        r0 = r30;
        r0 = (char) r0;	 Catch:{ Throwable -> 0x0197 }
        r30 = r0;
        r5 = com.klye.ime.latin.S.toUpper(r30);	 Catch:{ Throwable -> 0x0197 }
        goto L_0x0190;
    L_0x0236:
        r0 = r15.codes;	 Catch:{ Throwable -> 0x0197 }
        r30 = r0;
        r0 = r30;
        r0 = r0.length;	 Catch:{ Throwable -> 0x0197 }
        r22 = r0;
        r14 = 0;
    L_0x0240:
        r0 = r9.length;	 Catch:{ Throwable -> 0x0197 }
        r30 = r0;
        r0 = r30;
        if (r14 >= r0) goto L_0x0051;
    L_0x0247:
        r30 = r9[r14];	 Catch:{ Throwable -> 0x0197 }
        r0 = r30;
        if (r0 <= r8) goto L_0x028d;
    L_0x024d:
        r30 = r14 + r22;
        r0 = r9.length;	 Catch:{ Throwable -> 0x0197 }
        r31 = r0;
        r31 = r31 - r14;
        r31 = r31 - r22;
        r0 = r30;
        r1 = r31;
        java.lang.System.arraycopy(r9, r14, r9, r0, r1);	 Catch:{ Throwable -> 0x0197 }
        r30 = r14 + r22;
        r0 = r35;
        r0 = r0.length;	 Catch:{ Throwable -> 0x0197 }
        r31 = r0;
        r31 = r31 - r14;
        r31 = r31 - r22;
        r0 = r35;
        r1 = r35;
        r2 = r30;
        r3 = r31;
        java.lang.System.arraycopy(r0, r14, r1, r2, r3);	 Catch:{ Throwable -> 0x0197 }
        r0 = r15.codes;	 Catch:{ Throwable -> 0x0197 }
        r30 = r0;
        r31 = 0;
        r0 = r30;
        r1 = r31;
        r2 = r35;
        r3 = r22;
        java.lang.System.arraycopy(r0, r1, r2, r14, r3);	 Catch:{ Throwable -> 0x0197 }
        r30 = r14 + r22;
        r0 = r30;
        java.util.Arrays.fill(r9, r14, r0, r8);	 Catch:{ Throwable -> 0x0197 }
        goto L_0x0051;
    L_0x028d:
        r14 = r14 + 1;
        goto L_0x0240;
    L_0x0290:
        r30 = -1;
        r0 = r25;
        r1 = r30;
        if (r0 != r1) goto L_0x019b;
    L_0x0298:
        r25 = r6;
        goto L_0x019b;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.klye.ime.latin.ProximityKeyDetector.getKeyIndexAndNearbyCodes(int, int, int[]):int");
    }

    private static void ins(int[] distances, int[] allKeys, char c1) {
        System.arraycopy(distances, 0, distances, 1, distances.length - 1);
        System.arraycopy(allKeys, 0, allKeys, 1, allKeys.length - 1);
        allKeys[0] = c1;
        distances[0] = 0;
    }
}
