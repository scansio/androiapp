package com.klye.ime.latin;

public class Ko {
    public static CharSequence koIme(WordComposer wc) {
        wc.origin();
        koIme(wc.mTW, wc.mTW.length());
        switch (wc.lastChar()) {
            case 769:
            case 770:
                wc.deleteLast2();
                wc.mTW.setLength(wc.mTW.length() - 1);
                break;
        }
        return wc.mTW;
    }

    public static void koA(StringBuilder s, int l) {
        String cs = s.toString();
        s.setLength(0);
        for (int i = 0; i < l; i++) {
            s.append(koA(cs.charAt(i)));
        }
    }

    static char koA(char c) {
        if (M.ir(c, 192, 255)) {
            c = (c - 192) + 12592;
        }
        return (char) c;
    }

    public static CharSequence koIme(StringBuilder cs, int l) {
        cs = koIme1(cs, l);
        return koIme2(cs, cs.length());
    }

    public static StringBuilder koIme1(StringBuilder cs, int l) {
        while (l > 1) {
            int i;
            int m;
            char c2 = cs.charAt(l - 2);
            char c1 = cs.charAt(l - 1);
            char c3 = 0;
            if (l > 2) {
                i = isKoI(cs.charAt(l - 3));
                m = isKoM(c2);
                int f = isKoF(c1);
                if (!(i == -1 || m == -1 || f == -1)) {
                    cs.replace(l - 3, l, Character.toString((char) ((((i * 588) + (m * 28)) + f) + 44032)));
                    l = cs.length();
                }
            }
            if (null == null) {
                i = isKoI(c2);
                m = isKoM(c1);
                if (!(i == -1 || m == -1)) {
                    c3 = (char) (((i * 588) + (m * 28)) + 44032);
                }
            }
            if (c3 == 0) {
                switch (c2) {
                    case 12593:
                        switch (c1) {
                            case 769:
                                c3 = 12594;
                                break;
                            case 770:
                                c3 = 12619;
                                break;
                            case 12593:
                                if (l >= 3 && -1 != isKoM(cs.charAt(l - 3))) {
                                    c3 = 12594;
                                    break;
                                }
                            case 12613:
                                c3 = 12595;
                                break;
                        }
                        break;
                    case 12596:
                        switch (c1) {
                            case 770:
                                c3 = 12599;
                                break;
                            case 12616:
                                c3 = 12597;
                                break;
                            case 12622:
                                c3 = 12598;
                                break;
                        }
                        break;
                    case 12599:
                        switch (c1) {
                            case 769:
                                c3 = 12600;
                                break;
                            case 770:
                                c3 = 12620;
                                break;
                        }
                        break;
                    case 12601:
                        switch (c1) {
                            case 770:
                            case 12613:
                                c3 = 12605;
                                break;
                            case 12593:
                                c3 = 12602;
                                break;
                            case 12609:
                                c3 = 12603;
                                break;
                            case 12610:
                                c3 = 12604;
                                break;
                            case 12620:
                                c3 = 12606;
                                break;
                            case 12621:
                                c3 = 12607;
                                break;
                            case 12622:
                                c3 = 12608;
                                break;
                        }
                        break;
                    case 12609:
                        switch (c1) {
                            case 770:
                                c3 = 12610;
                                break;
                        }
                        break;
                    case 12610:
                        switch (c1) {
                            case 769:
                                c3 = 12611;
                                break;
                            case 770:
                                c3 = 12621;
                                break;
                            case 12613:
                                c3 = 12612;
                                break;
                        }
                        break;
                    case 12613:
                        switch (c1) {
                            case 769:
                                c3 = 12614;
                                break;
                            case 770:
                                c3 = 12616;
                                break;
                            case 12613:
                                if (l >= 3 && -1 != isKoM(cs.charAt(l - 3))) {
                                    c3 = 12614;
                                    break;
                                }
                        }
                        break;
                    case 12615:
                        switch (c1) {
                            case 770:
                                c3 = 12622;
                                break;
                        }
                        break;
                    case 12616:
                        switch (c1) {
                            case 769:
                                c3 = 12617;
                                break;
                            case 770:
                                c3 = 12618;
                                break;
                        }
                        break;
                    case 12623:
                        switch (c1) {
                            case 769:
                            case 770:
                                c3 = 12625;
                                break;
                            case 12623:
                                if (!M.t9()) {
                                    c3 = 12625;
                                    break;
                                }
                                c3 = 12627;
                                break;
                            case 12624:
                                if (!M.t9()) {
                                    c3 = 12626;
                                    break;
                                }
                                c3 = 12628;
                                break;
                            case 12625:
                                if (M.t9()) {
                                    c3 = 12629;
                                    break;
                                }
                                break;
                            case 12626:
                                if (M.t9()) {
                                    c3 = 12630;
                                    break;
                                }
                                break;
                            case 12643:
                                c3 = 12624;
                                break;
                            case 12685:
                                c3 = 12625;
                                break;
                        }
                        break;
                    case 12624:
                        switch (c1) {
                            case 769:
                            case 770:
                            case 12624:
                                c3 = 12626;
                                break;
                        }
                        break;
                    case 12625:
                        switch (c1) {
                            case 769:
                            case 770:
                            case 12643:
                                c3 = 12626;
                                break;
                        }
                        break;
                    case 12627:
                        switch (c1) {
                            case 769:
                            case 770:
                            case 12627:
                                c3 = 12629;
                                break;
                            case 12628:
                                c3 = 12630;
                                break;
                            case 12643:
                                c3 = 12628;
                                break;
                        }
                        break;
                    case 12628:
                        switch (c1) {
                            case 769:
                            case 770:
                            case 12628:
                                c3 = 12630;
                                break;
                        }
                        break;
                    case 12629:
                        switch (c1) {
                            case 769:
                            case 770:
                            case 12643:
                                c3 = 12630;
                                break;
                        }
                        break;
                    case 12631:
                        switch (c1) {
                            case 769:
                            case 770:
                                c3 = 12635;
                                break;
                            case 12623:
                                c3 = 12632;
                                break;
                            case 12624:
                                c3 = 12633;
                                break;
                            case 12631:
                                if (!M.t9()) {
                                    if (!M.hk && (M.kid == null || !M.kid.is(R.xml.kofull))) {
                                        c3 = 12635;
                                        break;
                                    }
                                }
                                c3 = 12636;
                                break;
                            case 12632:
                                if (M.t9()) {
                                    c3 = 12637;
                                    break;
                                }
                                break;
                            case 12633:
                                if (M.t9()) {
                                    c3 = 12638;
                                    break;
                                }
                                break;
                            case 12634:
                                if (M.t9()) {
                                    c3 = 12639;
                                    break;
                                }
                                break;
                            case 12635:
                                if (M.t9()) {
                                    c3 = 12640;
                                    break;
                                }
                                break;
                            case 12643:
                                c3 = 12634;
                                break;
                        }
                        break;
                    case 12632:
                    case 12634:
                        switch (c1) {
                            case 12627:
                            case 12643:
                                c3 = 12633;
                                break;
                        }
                        break;
                    case 12636:
                        switch (c1) {
                            case 770:
                            case 12636:
                            case 12685:
                                if (!M.hk && (M.kid == null || !M.kid.is(R.xml.kofull))) {
                                    c3 = 12640;
                                    break;
                                }
                            case 12626:
                                if (M.t9()) {
                                    c3 = 12638;
                                    break;
                                }
                                break;
                            case 12627:
                                c3 = 12637;
                                break;
                            case 12628:
                                c3 = 12638;
                                break;
                            case 12643:
                                c3 = 12639;
                                break;
                        }
                        break;
                    case 12637:
                    case 12639:
                        switch (c1) {
                            case 769:
                            case 770:
                            case 12643:
                                c3 = 12638;
                                break;
                        }
                        break;
                    case 12641:
                        switch (c1) {
                            case 769:
                            case 770:
                            case 12643:
                                c3 = 12642;
                                break;
                            case 12627:
                                c3 = 12639;
                                break;
                            case 12629:
                                c3 = 12637;
                                break;
                            case 12630:
                                c3 = 12638;
                                break;
                            case 12685:
                                c3 = 12636;
                                break;
                        }
                        break;
                    case 12643:
                        switch (c1) {
                            case 769:
                            case 770:
                            case 12627:
                                c3 = 12624;
                                break;
                            case 12629:
                                c3 = 12626;
                                break;
                            case 12685:
                                c3 = 12623;
                                break;
                        }
                        break;
                    case 12685:
                        switch (c1) {
                            case 12627:
                                c3 = 12629;
                                break;
                            case 12628:
                                c3 = 12630;
                                break;
                            case 12631:
                                c3 = 12635;
                                break;
                            case 12641:
                                c3 = 12631;
                                break;
                            case 12642:
                                c3 = 12634;
                                break;
                            case 12643:
                                c3 = 12627;
                                break;
                        }
                        break;
                }
            }
            if (c3 != 0) {
                cs.replace(l - 2, l, Character.toString(c3));
                l = cs.length();
            } else {
                l--;
            }
        }
        return cs;
    }

    public static CharSequence koIme2(StringBuilder cs, int l) {
        while (l > 1) {
            char c2 = cs.charAt(l - 2);
            char c1 = cs.charAt(l - 1);
            char c3 = 0;
            switch (c2) {
                case 12593:
                    switch (c1) {
                        case 12593:
                            c3 = 12594;
                            break;
                        default:
                            if (M.ir(c1, 44032, 44619)) {
                                c3 = (char) (((c1 - 48148) + 48735) + 1);
                                break;
                            }
                            break;
                    }
                case 12599:
                    switch (c1) {
                        case 12599:
                            c3 = 12600;
                            break;
                        default:
                            if (M.ir(c1, 45796, 46383)) {
                                c3 = (char) (((c1 - 48148) + 48735) + 1);
                                break;
                            }
                            break;
                    }
                case 12610:
                    switch (c1) {
                        case 12610:
                            c3 = 12611;
                            break;
                        default:
                            if (M.ir(c1, 48148, 48735)) {
                                c3 = (char) (((c1 - 48148) + 48735) + 1);
                                break;
                            }
                            break;
                    }
                case 12613:
                    switch (c1) {
                        case 12613:
                            c3 = 12614;
                            break;
                        default:
                            if (M.ir(c1, 49324, 49911)) {
                                c3 = (char) (((c1 - 48148) + 48735) + 1);
                                break;
                            }
                            break;
                    }
                case 12616:
                    switch (c1) {
                        case 12616:
                            c3 = 12617;
                            break;
                        default:
                            if (M.ir(c1, 51088, 51675)) {
                                c3 = (char) (((c1 - 48148) + 48735) + 1);
                                break;
                            }
                            break;
                    }
            }
            if (c3 != 0) {
                cs.replace(l - 2, l, Character.toString(c3));
                l = cs.length();
            } else {
                l--;
            }
        }
        return cs;
    }

    static int koI(int i) {
        return "ㄱㄲㄴㄷㄸㄹㅁㅂㅃㅅㅆㅇㅈㅉㅊㅋㅌㅍㅎ".charAt(i);
    }

    static int isKoI(char c1) {
        return M.ir(c1, 12593, 12622) ? new int[]{0, 1, -1, 2, -1, -1, 3, 4, 5, -1, -1, -1, -1, -1, -1, -1, 6, 7, 8, -1, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18}[c1 - 12593] : -1;
    }

    static int isKoM(char c) {
        return M.ir(c, 12623, 12643) ? new int[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20}[c - 12623] : -1;
    }

    static int koF(int i) {
        return " ㄱㄲㄳㄴㄵㄶㄷㄹㄺㄻㄼㄽㄾㄿㅀㅁㅂㅄㅅㅆㅇㅈㅉㅊㅋㅌㅍㅎ".charAt(i);
    }

    static int isKoF(char c) {
        return M.ir(c, 12593, 12622) ? new int[]{1, 2, 3, 4, 5, 6, 7, -1, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, -1, 18, 19, 20, 21, 22, -1, 23, 24, 25, 26, 27}[c - 12593] : -1;
    }
}
