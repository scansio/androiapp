package com.klye.ime.latin;

public class S {
    static char shar(char c) {
        switch (M.klAR) {
            case 1:
                return shar2(c);
            case 2:
                return shar3(c);
            default:
                return shar1(c);
        }
    }

    static char shfa(char c) {
        switch (M.klfa) {
            case 1:
                return shar2(c);
            default:
                return shar3(c);
        }
    }

    static char ar(char c) {
        switch (M.klAR) {
            case 2:
                return ar2(c);
            default:
                return fa1(c);
        }
    }

    static char fa(char c) {
        switch (M.klfa) {
            case 1:
                return fa1(c);
            default:
                return ar2(c);
        }
    }

    static char ar2(char c) {
        switch (c) {
            case 'a':
                return 1587;
            case 'b':
                return 1572;
            case 'c':
                return M.bfa ? 1688 : 1609;
            case 'd':
                return 1576;
            case 'e':
                return 1602;
            case 'f':
                return 1604;
            case 'g':
                return 1575;
            case 'h':
                return 1578;
            case 'i':
                return 1581;
            case 'j':
                return 1606;
            case 'k':
                return 1605;
            case 'l':
                return M.bfa ? 1705 : 1603;
            case 'm':
                return !M.bfa ? 1577 : 1583;
            case 'n':
                return 1608;
            case 'o':
                return 1580;
            case 'p':
                if (M.bfa) {
                    return 1670;
                }
                return 1583;
            case 'q':
                return 1589;
            case BuildConfig.VERSION_CODE /*114*/:
                return 1601;
            case 's':
                return M.bfa ? 1740 : 1610;
            case 't':
                return 1593;
            case 'u':
                return 1582;
            case 'v':
                return 1585;
            case 'w':
                return 1579;
            case 'x':
                return 1574;
            case 'y':
                return 1607;
            case 'z':
                return 1591;
            default:
                return 0;
        }
    }

    static char fa1(char c) {
        char c2 = 1586;
        char c3 = 1583;
        switch (c) {
            case '\'':
            case 228:
                return 1711;
            case '-':
            case 245:
                return M.bfa ? 1608 : 1577;
            case '/':
                return M.bfa ? 1662 : 1609;
            case ';':
            case 246:
                return M.bfa ? 1705 : 1603;
            case '?':
                return 1574;
            case '[':
            case 229:
                return 1580;
            case ']':
                return 1670;
            case 'a':
                return 1588;
            case 'b':
                if (M.bfa) {
                    c2 = 1585;
                }
                return c2;
            case 'c':
                if (M.bfa) {
                    return 1688;
                }
                return 1584;
            case 'd':
                return M.bfa ? 1740 : 1610;
            case 'e':
                return M.mLC == M.s2 ? 1734 : 1579;
            case 'f':
                return 1576;
            case 'g':
                return 1604;
            case 'h':
                return 1575;
            case 'i':
                return 1607;
            case 'j':
                return 1578;
            case 'k':
                return 1606;
            case 'l':
                return 1605;
            case 'm':
                if (!M.bfa) {
                    c3 = 1608;
                }
                return c3;
            case 'n':
                if (M.mLC == M.s2) {
                    return 1685;
                }
                if (M.bfa) {
                    return 1584;
                }
                return 1585;
            case 'o':
                return 1582;
            case 'p':
                return 1581;
            case 'q':
                return M.mLC == M.s2 ? 1742 : 1590;
            case BuildConfig.VERSION_CODE /*114*/:
                return 1602;
            case 's':
                return 1587;
            case 't':
                return 1601;
            case 'u':
                return 1593;
            case 'v':
                return M.bfa ? 1586 : 1583;
            case 'w':
                return M.mLC == M.s2 ? 1700 : 1589;
            case 'x':
                return M.mLC == M.s2 ? 1717 : 1591;
            case 'y':
                return 1594;
            case 'z':
                return 1592;
            default:
                return 0;
        }
    }

    static char shar2(char c) {
        switch (c) {
            case 1569:
                return '/';
            case 1574:
                return '}';
            case 1575:
                return 1570;
            case 1576:
                return 1662;
            case 1577:
                return 1548;
            case 1578:
                return 1657;
            case 1579:
            case 1734:
                return M.an(3);
            case 1580:
                return 1670;
            case 1581:
                return M.an(0);
            case 1582:
                return M.an(9);
            case 1583:
                return 1688;
            case 1584:
            case 1685:
                return 1574;
            case 1585:
                return 1573;
            case 1586:
                return 1571;
            case 1587:
                return 187;
            case 1588:
                return 171;
            case 1589:
            case 1700:
                return M.an(2);
            case 1590:
            case 1742:
                return M.an(1);
            case 1591:
            case 1717:
                return 1569;
            case 1592:
                return 1567;
            case 1593:
                return M.an(7);
            case 1594:
                return M.an(6);
            case 1601:
                return M.an(5);
            case 1602:
                return M.an(4);
            case 1603:
                return 1711;
            case 1604:
                return 1700;
            case 1605:
                return 1746;
            case 1606:
                return 1722;
            case 1607:
                return M.an(8);
            case 1608:
                return 1572;
            case 1609:
                return '{';
            case 1610:
                return 1609;
            case 1670:
                return 1642;
            case 1688:
                return 1728;
            case 1705:
                return 1603;
            case 1711:
                return 1577;
            case 1740:
                return 1610;
            default:
                return c;
        }
    }

    static char shar3(char c) {
        switch (c) {
            case 1572:
                return 1571;
            case 1574:
                return 1569;
            case 1575:
                return 1570;
            case 1576:
                return 1662;
            case 1577:
                return 1567;
            case 1578:
                return 1657;
            case 1579:
                return 171;
            case 1580:
                return 1670;
            case 1581:
                return '}';
            case 1582:
                return '{';
            case 1583:
                return 1584;
            case 1585:
                return 1586;
            case 1587:
                return 1588;
            case 1589:
                return 1590;
            case 1591:
                return 1592;
            case 1593:
                return 1594;
            case 1601:
                return 1700;
            case 1602:
                return 187;
            case 1603:
            case 1705:
                return 1711;
            case 1604:
                return 1573;
            case 1605:
                return 1746;
            case 1606:
                return 1722;
            case 1607:
                return 1642;
            case 1608:
                return 1548;
            case 1609:
            case 1688:
                return 1672;
            case 1610:
                return 1609;
            case 1740:
                return 1610;
            default:
                return c;
        }
    }

    static char shar1(char c) {
        switch (c) {
            case 1575:
                return 1570;
            case 1576:
                return 1700;
            case 1577:
                return 1642;
            case 1578:
                return 1579;
            case 1580:
                return 1632;
            case 1581:
                return 1641;
            case 1582:
                return 1640;
            case 1583:
                return 1569;
            case 1584:
                return 1574;
            case 1585:
                return 1573;
            case 1586:
                return 1571;
            case 1587:
                return 1644;
            case 1588:
                return 1567;
            case 1589:
                return 1634;
            case 1590:
                return 1633;
            case 1591:
                return 1592;
            case 1593:
                return 1638;
            case 1594:
                return 1637;
            case 1601:
                return 1636;
            case 1602:
                return 1635;
            case 1603:
                return 1711;
            case 1604:
                return 1643;
            case 1605:
                return 1670;
            case 1606:
                return 1563;
            case 1607:
                return 1639;
            case 1608:
                return 1572;
            case 1610:
                return 1609;
            default:
                return c;
        }
    }

    static char shor(char c) {
        switch (c) {
            case 2818:
                return 2817;
            case 2837:
                return 2838;
            case 2839:
                return 2840;
            case 2842:
                return 2843;
            case 2844:
                return 2845;
            case 2847:
                return 2848;
            case 2849:
                return 2850;
            case 2852:
                return 2853;
            case 2854:
                return 2855;
            case 2856:
                return 2867;
            case 2858:
                return 2859;
            case 2860:
                return 2861;
            case 2862:
                return 2851;
            case 2863:
                return 63750;
            case 2866:
                return 2870;
            case 2872:
                return 2871;
            case 2873:
                return 2841;
            case 2876:
                return 2846;
            case 2878:
                return 2822;
            case 2879:
                return 2823;
            case 2880:
                return 2824;
            case 2881:
                return 2825;
            case 2882:
                return 2826;
            case 2883:
                return 2827;
            case 2887:
                return 2831;
            case 2888:
                return 2832;
            case 2891:
                return 2835;
            case 2892:
                return 2836;
            case 2893:
                return 2821;
            case 2911:
                return 2929;
            case 2918:
                return 2819;
            case 2920:
                return 63744;
            case 2921:
                return 63745;
            case 2922:
                return 63746;
            case 2923:
                return 63747;
            case 2924:
                return 63748;
            case 2925:
                return 63749;
            case 2926:
                return '(';
            case 2927:
                return ')';
            default:
                return c;
        }
    }

    static char psshf(char c) {
        switch (c) {
            case 1575:
                return 1570;
            case 1576:
                return 1662;
            case 1578:
                return 1660;
            case 1579:
                return 1613;
            case 1580:
                return ']';
            case 1581:
                return 1669;
            case 1582:
                return 1665;
            case 1583:
                return 1683;
            case 1584:
                return 1567;
            case 1585:
                return 1569;
            case 1586:
                return 1688;
            case 1587:
                return 1741;
            case 1588:
                return 1690;
            case 1589:
                return 1612;
            case 1590:
                return 1618;
            case 1591:
                return 1744;
            case 1592:
                return 1574;
            case 1593:
                return 1614;
            case 1594:
                return 1616;
            case 1601:
                return 1615;
            case 1602:
                return 1611;
            case 1604:
                return 1571;
            case 1605:
                return 1577;
            case 1606:
                return 1724;
            case 1607:
                return 1617;
            case 1608:
                return 1548;
            case 1670:
                return '[';
            case 1683:
                return 1572;
            case 1686:
                return 1746;
            case 1705:
                return 1728;
            case 1707:
                return 1673;
            case 1740:
                return 1610;
            default:
                return c;
        }
    }

    static char sdshf(char c) {
        switch (c) {
            case 1575:
                return 1570;
            case 1576:
                return 1659;
            case 1578:
                return 1661;
            case 1580:
                return 1590;
            case 1583:
                return 1676;
            case 1585:
                return 1689;
            case 1586:
                return 1584;
            case 1587:
                return 1588;
            case 1589:
                return 1579;
            case 1591:
                return 1592;
            case 1593:
                return 1594;
            case 1601:
                return 1702;
            case 1602:
                return 1679;
            case 1604:
                return 1677;
            case 1605:
                return 1713;
            case 1606:
                return 1723;
            case 1608:
                return 1572;
            case 1610:
                return 1668;
            case 1662:
                return 1674;
            case 1663:
                return 1658;
            case 1664:
                return 1663;
            case 1670:
                return 1671;
            case 1705:
                return 1582;
            case 1711:
                return 1715;
            case 1726:
                return 1581;
            case 1729:
                return 1569;
            default:
                return c;
        }
    }

    static char shml(char c) {
        switch (c) {
            case ',':
                return 3454;
            case '.':
                return 3453;
            case 3330:
                return 3450;
            case 3331:
                return 3430;
            case 3339:
                return 3438;
            case 3349:
                return 3350;
            case 3351:
                return 3352;
            case 3354:
                return 3355;
            case 3356:
                return 3357;
            case 3359:
                return 3360;
            case 3361:
                return 3362;
            case 3364:
                return 3365;
            case 3366:
                return 3367;
            case 3368:
                return 3451;
            case 3370:
                return 3371;
            case 3372:
                return 3373;
            case 3374:
                return 3363;
            case 3375:
                return 3383;
            case 3376:
                return 3377;
            case 3378:
                return 3379;
            case 3381:
                return 3380;
            case 3384:
                return 3382;
            case 3385:
                return 3353;
            case 3390:
                return 3334;
            case 3391:
                return 3335;
            case 3392:
                return 3336;
            case 3393:
                return 3337;
            case 3394:
                return 3338;
            case 3395:
                return 3358;
            case 3398:
                return 3342;
            case 3399:
                return 3343;
            case 3400:
                return 3344;
            case 3402:
                return 3346;
            case 3403:
                return 3347;
            case 3405:
                return 3333;
            case 3415:
                return 3348;
            case 3452:
                return 3439;
            case 3453:
                return 3434;
            case 3454:
                return 3435;
            case 3455:
                return 3436;
            case 8204:
                return 3432;
            case 8377:
                return 3431;
            case 63744:
                return 3433;
            case 63745:
                return 3437;
            default:
                return c;
        }
    }

    static char toUpper(char c1) {
        switch (c1) {
            case ',':
            case H.eMaxPt /*1600*/:
                if (M.zw) {
                    return M.zwnj;
                }
                break;
            case '.':
            case 2551:
                if (M.zw) {
                    return M.zwj;
                }
                break;
            case 8126:
                return c1;
        }
        char c = c1;
        switch (M.mLC) {
            case M.am /*6357101*/:
            case M.ti /*7602281*/:
                c = amshf(c);
                break;
            case M.ar /*6357106*/:
            case M.ma /*7143521*/:
                c = shar(c);
                break;
            case M.as /*6357107*/:
            case M.bn /*6422638*/:
            case M.bp /*6422640*/:
                c = bnshf(c1);
                break;
            case M.aw /*6357111*/:
            case M.bh /*6422632*/:
            case M.hi /*6815849*/:
            case M.mr /*7143538*/:
            case M.sa /*7536737*/:
                switch (c) {
                    case 2306:
                        c = 2305;
                        break;
                    case 2325:
                        c = 2326;
                        break;
                    case 2327:
                        c = 2328;
                        break;
                    case 2330:
                        c = 2331;
                        break;
                    case 2332:
                        c = 2333;
                        break;
                    case 2335:
                        c = 2336;
                        break;
                    case 2337:
                        c = 2338;
                        break;
                    case 2340:
                        c = 2341;
                        break;
                    case 2342:
                        c = 2343;
                        break;
                    case 2344:
                        c = 2345;
                        break;
                    case 2346:
                        c = 2347;
                        break;
                    case 2348:
                        c = 2349;
                        break;
                    case 2350:
                        c = 2339;
                        break;
                    case 2351:
                        c = 2399;
                        break;
                    case 2352:
                        c = 2353;
                        break;
                    case 2354:
                        c = 2355;
                        break;
                    case 2357:
                        c = 2356;
                        break;
                    case 2360:
                        c = 2358;
                        break;
                    case 2361:
                        c = 2329;
                        break;
                    case 2364:
                        c = 2334;
                        break;
                    case 2366:
                        c = 2310;
                        break;
                    case 2367:
                        c = 2311;
                        break;
                    case 2368:
                        c = 2312;
                        break;
                    case 2369:
                        c = 2313;
                        break;
                    case 2370:
                        c = 2314;
                        break;
                    case 2371:
                        c = 2404;
                        break;
                    case 2374:
                        c = 2318;
                        break;
                    case 2375:
                        c = 2319;
                        break;
                    case 2376:
                        c = 2320;
                        break;
                    case 2377:
                        c = 2321;
                        break;
                    case 2378:
                        c = 2359;
                        break;
                    case 2379:
                        c = 2323;
                        break;
                    case 2380:
                        c = 2324;
                        break;
                    case 2381:
                        c = 2309;
                        break;
                }
                break;
            case M.az /*6357114*/:
            case M.tr /*7602290*/:
                switch (c) {
                    case 'i':
                        c = 304;
                        break;
                }
                break;
            case M.bo /*6422639*/:
            case M.dz /*6553722*/:
                c = boshf(c);
                break;
            case M.ck /*6488171*/:
                switch (c) {
                    case 5024:
                        c = 5068;
                        break;
                    case 5025:
                        c = 5091;
                        break;
                    case 5026:
                        c = 5105;
                        break;
                    case 5027:
                        c = 5100;
                        break;
                    case 5028:
                        c = 5037;
                        break;
                    case 5029:
                        c = 5086;
                        break;
                    case 5030:
                        c = 5093;
                        break;
                    case 5032:
                        c = 5104;
                        break;
                    case 5033:
                        c = 5064;
                        break;
                    case 5034:
                        c = 5062;
                        break;
                    case 5036:
                        c = 5059;
                        break;
                    case 5039:
                        c = 5042;
                        break;
                    case 5043:
                        c = 5099;
                        break;
                    case 5045:
                        c = 5038;
                        break;
                    case 5046:
                        c = 5092;
                        break;
                    case 5048:
                        c = 5031;
                        break;
                    case 5054:
                        c = 5051;
                        break;
                    case 5055:
                        c = 5053;
                        break;
                    case 5057:
                        c = 5098;
                        break;
                    case 5058:
                        c = 5065;
                        break;
                    case 5060:
                        c = 5052;
                        break;
                    case 5061:
                        c = 5047;
                        break;
                    case 5067:
                        c = 5085;
                        break;
                    case 5069:
                        c = 5070;
                        break;
                    case 5074:
                        c = 5050;
                        break;
                    case 5075:
                        c = 5087;
                        break;
                    case 5076:
                        c = 5080;
                        break;
                    case 5077:
                        c = 5073;
                        break;
                    case 5078:
                        c = 5089;
                        break;
                    case 5079:
                        c = 5072;
                        break;
                    case 5081:
                        c = 5095;
                        break;
                    case 5082:
                        c = 5035;
                        break;
                    case 5083:
                        c = 5071;
                        break;
                    case 5084:
                        c = 5049;
                        break;
                    case 5094:
                        c = 5040;
                        break;
                    case 5096:
                        c = 5088;
                        break;
                    case 5097:
                        c = 5063;
                        break;
                    case 5103:
                        c = 5106;
                        break;
                    case 5107:
                        c = 5102;
                        break;
                    case 5108:
                        c = 5101;
                        break;
                    case 63744:
                        c = 5066;
                        break;
                    case 63745:
                        c = 5041;
                        break;
                }
                break;
            case M.dv /*6553718*/:
                c = dvshf(c);
                break;
            case M.fa /*6684769*/:
            case M.mz /*7143546*/:
            case M.s2 /*7536690*/:
                c = shfa(c);
                break;
            case M.gu /*6750325*/:
                c = gushf(c1);
                break;
            case M.iw /*6881399*/:
            case M.yi /*7929961*/:
                c = iwshf(c);
                break;
            case M.ja /*6946913*/:
                if (M.ir(c, 12353, 12438)) {
                    c = (char) (c + 96);
                    break;
                }
                break;
            case M.ka /*7012449*/:
            case M.m2 /*7143474*/:
                switch (c) {
                    case 4310:
                        c = 4331;
                        break;
                    case 4316:
                        c = 'N';
                        break;
                    case 4320:
                        c = 4326;
                        break;
                    case 4321:
                        c = 4328;
                        break;
                    case 4322:
                        c = 4311;
                        break;
                    case 4330:
                        c = 4329;
                        break;
                    case 4332:
                        c = 4333;
                        break;
                    case 4335:
                        c = 4319;
                        break;
                }
                break;
            case M.km /*7012461*/:
                c = kmshf(c1);
                break;
            case M.kn /*7012462*/:
                c = knshf(c1);
                break;
            case M.lo /*7077999*/:
                c = loshf(c1);
                break;
            case M.ml /*7143532*/:
                c = shml(c);
                break;
            case M.ne /*7209061*/:
            case M.nw /*7209079*/:
                switch (c) {
                    case 2309:
                        c = 2315;
                        break;
                    case 2313:
                        c = 2314;
                        break;
                    case 2325:
                        c = 63744;
                        break;
                    case 2326:
                        c = 2310;
                        break;
                    case 2327:
                        c = 2319;
                        break;
                    case 2328:
                        c = 2408;
                        break;
                    case 2329:
                        c = 2409;
                        break;
                    case 2330:
                        c = 2405;
                        break;
                    case 2331:
                        c = 2411;
                        break;
                    case 2332:
                        c = 63745;
                        break;
                    case 2333:
                        c = 2410;
                        break;
                    case 2334:
                        c = 2407;
                        break;
                    case 2335:
                        c = 2412;
                        break;
                    case 2336:
                        c = 2413;
                        break;
                    case 2337:
                        c = 2414;
                        break;
                    case 2338:
                        c = 2415;
                        break;
                    case 2339:
                        c = 2406;
                        break;
                    case 2340:
                        c = 63747;
                        break;
                    case 2341:
                        c = 2320;
                        break;
                    case 2342:
                        c = 2323;
                        break;
                    case 2343:
                        c = 2379;
                        break;
                    case 2344:
                        c = 63749;
                        break;
                    case 2346:
                        c = 63751;
                        break;
                    case 2347:
                        c = 2307;
                        break;
                    case 2348:
                        c = 63748;
                        break;
                    case 2349:
                        c = M.zwj;
                        break;
                    case 2350:
                        c = 63746;
                        break;
                    case 2351:
                        c = 2311;
                        break;
                    case 2352:
                        c = 63754;
                        break;
                    case 2354:
                        c = 2324;
                        break;
                    case 2357:
                        c = 63750;
                        break;
                    case 2358:
                        c = 63753;
                        break;
                    case 2359:
                        c = 2312;
                        break;
                    case 2360:
                        c = 63752;
                        break;
                    case 2361:
                        c = 2384;
                        break;
                    case 2366:
                        c = 2305;
                        break;
                    case 2367:
                        c = 2368;
                        break;
                    case 2369:
                        c = 2370;
                        break;
                    case 2371:
                        c = M.zwnj;
                        break;
                    case 2375:
                        c = 2376;
                        break;
                    case 2381:
                        c = 2306;
                        break;
                    case 2404:
                        c = 2380;
                        break;
                }
                break;
            case M.or /*7274610*/:
                c = shor(c);
                break;
            case M.pa /*7340129*/:
                switch (c) {
                    case 2581:
                        c = 2582;
                        break;
                    case 2583:
                        c = 2584;
                        break;
                    case 2586:
                        c = 2587;
                        break;
                    case 2588:
                        c = 2589;
                        break;
                    case 2590:
                        c = 2585;
                        break;
                    case 2591:
                        c = 2592;
                        break;
                    case 2593:
                        c = 2594;
                        break;
                    case 2595:
                        c = 2607;
                        break;
                    case 2596:
                        c = 2597;
                        break;
                    case 2598:
                        c = 2599;
                        break;
                    case 2600:
                        c = 2672;
                        break;
                    case 2602:
                        c = 2603;
                        break;
                    case 2604:
                        c = 2605;
                        break;
                    case 2606:
                        c = 2673;
                        break;
                    case 2608:
                        c = 2563;
                        break;
                    case 2610:
                        c = 2611;
                        break;
                    case 2613:
                        c = 2652;
                        break;
                    case 2616:
                        c = 2614;
                        break;
                    case 2617:
                        c = 2637;
                        break;
                    case 2622:
                        c = 2562;
                        break;
                    case 2623:
                        c = 2624;
                        break;
                    case 2625:
                        c = 2626;
                        break;
                    case 2631:
                        c = 2632;
                        break;
                    case 2635:
                        c = 2636;
                        break;
                    case 2651:
                        c = 2650;
                        break;
                    case 2654:
                        c = 2649;
                        break;
                    case 2674:
                        c = 2579;
                        break;
                    case 2675:
                        c = 2565;
                        break;
                }
                break;
            case M.pn /*7340142*/:
            case M.ur /*7667826*/:
                c = urshf(c1);
                break;
            case M.ps /*7340147*/:
                c = psshf(c);
                break;
            case M.sd /*7536740*/:
                c = sdshf(c);
                break;
            case M.si /*7536745*/:
                if (M.klsi == 1) {
                    c = sishf(c1);
                    break;
                }
                break;
            case M.ta /*7602273*/:
                switch (c) {
                    case 2965:
                        c = 63745;
                        break;
                    case 2970:
                        c = '!';
                        break;
                    case 2972:
                        c = 63744;
                        break;
                    case 2974:
                        c = '+';
                        break;
                    case 2975:
                        c = '?';
                        break;
                    case 2980:
                        c = 2947;
                        break;
                    case 2984:
                        c = 2985;
                        break;
                    case 2986:
                        c = 2954;
                        break;
                    case 2990:
                        c = 2979;
                        break;
                    case 2991:
                        c = 63746;
                        break;
                    case 2992:
                        c = 2993;
                        break;
                    case 2994:
                        c = 2995;
                        break;
                    case 2997:
                        c = 2996;
                        break;
                    case 3000:
                        c = 2999;
                        break;
                    case 3001:
                        c = 2969;
                        break;
                    case 3006:
                        c = '&';
                        break;
                    case 3007:
                        c = 2951;
                        break;
                    case 3008:
                        c = 2950;
                        break;
                    case 3009:
                        c = 2953;
                        break;
                    case 3010:
                        c = 2952;
                        break;
                    case 3014:
                        c = 2958;
                        break;
                    case 3015:
                        c = 2959;
                        break;
                    case 3016:
                        c = 2960;
                        break;
                    case 3018:
                        c = 2962;
                        break;
                    case 3019:
                        c = 2963;
                        break;
                    case 3020:
                        c = 2964;
                        break;
                    case 3021:
                        c = 2949;
                        break;
                }
                break;
            case M.te /*7602277*/:
                switch (c) {
                    case 3074:
                        c = 3073;
                        break;
                    case 3093:
                        c = 3094;
                        break;
                    case 3095:
                        c = 3096;
                        break;
                    case 3098:
                        c = 3099;
                        break;
                    case 3100:
                        c = 3101;
                        break;
                    case 3102:
                        c = ' ';
                        break;
                    case 3103:
                        c = 3104;
                        break;
                    case 3105:
                        c = 3106;
                        break;
                    case 3108:
                        c = 3109;
                        break;
                    case 3110:
                        c = 3111;
                        break;
                    case 3112:
                        c = 3112;
                        break;
                    case 3114:
                        c = 3115;
                        break;
                    case 3116:
                        c = 3117;
                        break;
                    case 3118:
                        c = 3107;
                        break;
                    case 3119:
                        c = 3127;
                        break;
                    case 3120:
                        c = 3121;
                        break;
                    case 3122:
                        c = 3123;
                        break;
                    case 3125:
                        c = 3075;
                        break;
                    case 3128:
                        c = 3126;
                        break;
                    case 3129:
                        c = 3097;
                        break;
                    case 3134:
                        c = 3078;
                        break;
                    case 3135:
                        c = 3079;
                        break;
                    case 3136:
                        c = 3080;
                        break;
                    case 3137:
                        c = 3081;
                        break;
                    case 3138:
                        c = 3082;
                        break;
                    case 3139:
                        c = 3083;
                        break;
                    case 3142:
                        c = 3086;
                        break;
                    case 3143:
                        c = 3087;
                        break;
                    case 3144:
                        c = 3088;
                        break;
                    case 3146:
                        c = 3090;
                        break;
                    case 3147:
                        c = 3091;
                        break;
                    case 3148:
                        c = 3092;
                        break;
                    case 3149:
                        c = 3077;
                        break;
                }
                break;
            case M.th /*7602280*/:
                if (!M.t9()) {
                    switch (c) {
                        case 3585:
                            c = 3599;
                            break;
                        case 3586:
                            c = 3672;
                            break;
                        case 3587:
                            c = 3666;
                            break;
                        case 3588:
                            c = 3669;
                            break;
                        case 3591:
                            c = 3675;
                            break;
                        case 3592:
                            c = 3671;
                            break;
                        case 3594:
                            c = 3673;
                            break;
                        case 3604:
                            c = 3650;
                            break;
                        case 3605:
                            c = 3670;
                            break;
                        case 3606:
                            c = 3668;
                            break;
                        case 3607:
                            c = 3674;
                            break;
                        case 3609:
                            c = 3631;
                            break;
                        case 3610:
                            c = 3600;
                            break;
                        case 3611:
                            c = ')';
                            break;
                        case 3612:
                            c = '(';
                            break;
                        case 3613:
                            c = 3622;
                            break;
                        case 3614:
                            c = 3601;
                            break;
                        case 3615:
                            c = 3620;
                            break;
                        case 3616:
                            c = 3667;
                            break;
                        case 3617:
                            c = 3602;
                            break;
                        case 3618:
                            c = 3597;
                            break;
                        case 3619:
                            c = 3603;
                            break;
                        case 3621:
                            c = 3589;
                            break;
                        case 3623:
                            c = 3595;
                            break;
                        case 3626:
                            c = 3624;
                            break;
                        case 3627:
                            c = 3590;
                            break;
                        case 3629:
                            c = 3630;
                            break;
                        case 3632:
                            c = 3608;
                            break;
                        case 3633:
                            c = 3661;
                            break;
                        case 3634:
                            c = 3625;
                            break;
                        case 3635:
                            c = 3598;
                            break;
                        case 3636:
                            c = 3642;
                            break;
                        case 3637:
                            c = 3658;
                            break;
                        case 3638:
                            c = 3647;
                            break;
                        case 3639:
                            c = 3660;
                            break;
                        case 3640:
                            c = 3641;
                            break;
                        case 3648:
                            c = 3596;
                            break;
                        case 3649:
                            c = 3593;
                            break;
                        case 3651:
                            c = 3628;
                            break;
                        case 3652:
                            c = '-';
                            break;
                        case 3653:
                            c = 3665;
                            break;
                        case 3654:
                            c = 3664;
                            break;
                        case 3656:
                            c = 3659;
                            break;
                        case 3657:
                            c = 3655;
                            break;
                    }
                }
                switch (c) {
                    case 3585:
                        c = 3590;
                        break;
                    case 3586:
                        c = 3597;
                        break;
                    case 3588:
                        c = 3616;
                        break;
                    case 3591:
                        c = 3613;
                        break;
                    case 3592:
                        c = 3603;
                        break;
                    case 3594:
                        c = 3600;
                        break;
                    case 3604:
                        c = 3599;
                        break;
                    case 3605:
                        c = 3606;
                        break;
                    case 3607:
                        c = 3630;
                        break;
                    case 3609:
                        c = 3608;
                        break;
                    case 3610:
                        c = 3624;
                        break;
                    case 3611:
                        c = 3620;
                        break;
                    case 3614:
                        c = 3635;
                        break;
                    case 3617:
                        c = 3602;
                        break;
                    case 3618:
                        c = 3625;
                        break;
                    case 3619:
                        c = 3601;
                        break;
                    case 3621:
                        c = 3595;
                        break;
                    case 3623:
                        c = 3628;
                        break;
                    case 3626:
                        c = 3631;
                        break;
                    case 3627:
                        c = 3615;
                        break;
                    case 3629:
                        c = 3593;
                        break;
                    case 3632:
                        c = 3599;
                        break;
                    case 3634:
                        c = 3596;
                        break;
                    case 3648:
                        c = 3650;
                        break;
                    case 3649:
                        c = 3612;
                        break;
                    case 3651:
                        c = 3622;
                        break;
                    case 3652:
                        c = 3654;
                        break;
                }
                break;
            case M.ug /*7667815*/:
                switch (c) {
                    case 1574:
                        c = '/';
                        break;
                    case 1575:
                        c = 1601;
                        break;
                    case 1576:
                        c = '*';
                        break;
                    case 1578:
                        c = '%';
                        break;
                    case 1583:
                        c = 1688;
                        break;
                    case 1585:
                        c = '$';
                        break;
                    case 1586:
                        c = 1567;
                        break;
                    case 1587:
                        c = 171;
                        break;
                    case 1588:
                        c = 1563;
                        break;
                    case 1594:
                        c = 1548;
                        break;
                    case 1602:
                        c = 1580;
                        break;
                    case 1603:
                        c = 1734;
                        break;
                    case 1604:
                        c = 65275;
                        break;
                    case 1605:
                        c = '\'';
                        break;
                    case 1606:
                        c = '-';
                        break;
                    case 1608:
                        c = '(';
                        break;
                    case 1609:
                        c = 1582;
                        break;
                    case 1610:
                        c = '^';
                        break;
                    case 1662:
                        c = ')';
                        break;
                    case 1670:
                        c = '!';
                        break;
                    case 1709:
                        c = '*';
                        break;
                    case 1726:
                        c = 187;
                        break;
                    case 1735:
                        c = '&';
                        break;
                    case 1736:
                        c = ':';
                        break;
                    case 1739:
                        c = '@';
                        break;
                    case 1744:
                        c = '#';
                        break;
                    case 1749:
                        c = 1711;
                        break;
                }
                break;
        }
        if (c == c1) {
            switch (c) {
                case 223:
                    if (M.xtf) {
                        c = 7838;
                        break;
                    }
                    break;
                case 12593:
                    c = 12594;
                    break;
                case 12599:
                    c = 12600;
                    break;
                case 12610:
                    c = 12611;
                    break;
                case 12613:
                    c = 12614;
                    break;
                case 12616:
                    c = 12617;
                    break;
                case 12623:
                    c = 12625;
                    break;
                case 12624:
                    c = 12626;
                    break;
                case 12627:
                    c = 12629;
                    break;
                case 12628:
                    c = 12630;
                    break;
                case 12631:
                    c = 12635;
                    break;
                case 12636:
                    c = 12640;
                    break;
                default:
                    c = Character.toUpperCase(c);
                    break;
            }
        }
        return c;
    }

    static char iwshf(int c) {
        switch (c) {
            case 1488:
                c = 8362;
                break;
            case 1489:
                c = 8220;
                break;
            case 1490:
                c = 8222;
                break;
            case 1491:
                c = 64299;
                break;
            case 1492:
                c = 8221;
                break;
            case 1493:
                c = 64309;
                break;
            case 1494:
                c = 8211;
                break;
            case 1495:
                c = 1524;
                break;
            case 1496:
                c = 1468;
                break;
            case 1497:
                c = 64287;
                break;
            case 1498:
                c = 1465;
                break;
            case 1499:
                c = 8364;
                break;
            case 1500:
                c = 63744;
                break;
            case 1501:
                c = 1470;
                break;
            case 1502:
                c = 8217;
                break;
            case 1503:
                c = 64331;
                break;
            case 1504:
                c = 8216;
                break;
            case 1505:
                c = 8212;
                break;
            case 1506:
                c = 1438;
                break;
            case 1507:
                c = 1436;
                break;
            case 1508:
                c = 1471;
                break;
            case 1509:
                c = 1458;
                break;
            case 1510:
                c = 1475;
                break;
            case 1511:
                c = 8230;
                break;
            case 1512:
                c = 1464;
                break;
            case 1513:
                c = 64298;
                break;
            case 1514:
                c = 1472;
                break;
        }
        return (char) c;
    }

    static char boshf(int codes) {
        if (M.klBO == 0) {
            switch (codes) {
                case 3933:
                    codes = 4010;
                    break;
                case 3935:
                    codes = 4011;
                    break;
                case 3944:
                    codes = 4000;
                    break;
                case 3954:
                    codes = 3993;
                    break;
                case 3956:
                    codes = 4021;
                    break;
                case 3962:
                    codes = 3990;
                    break;
                case 3964:
                    codes = 3985;
                    break;
            }
        }
        switch (codes) {
            case 3936:
                codes = 3953;
                break;
            case 3954:
                codes = 3968;
                break;
            case 3956:
                codes = 3972;
                break;
            case 3962:
                codes = 3963;
                break;
            case 3964:
                codes = 3965;
                break;
        }
        if (M.ir(codes, 3904, 3944)) {
            codes += 80;
        }
        return (char) codes;
    }

    private static char ugshf(char c) {
        return c;
    }

    private static char loshf(char c) {
        switch (c) {
            case 3713:
                return '.';
            case 3714:
                return '8';
            case 3716:
                return '5';
            case 3719:
                return '=';
            case 3720:
                return '7';
            case 3722:
                return '9';
            case 3725:
                return 3773;
            case 3732:
                return ',';
            case 3733:
                return '6';
            case 3734:
                return '4';
            case 3735:
                return 3782;
            case 3737:
                return 3804;
            case 3738:
                return '-';
            case 3739:
                return '(';
            case 3740:
                return 8365;
            case 3741:
                return ')';
            case 3742:
                return '_';
            case 3743:
                return '2';
            case 3745:
                return 3805;
            case 3746:
                return '1';
            case 3749:
                return 63748;
            case 3751:
                return '%';
            case 3754:
                return '?';
            case 3755:
                return ';';
            case 3757:
                return 'x';
            case 3758:
                return 3747;
            case 3760:
                return '+';
            case 3761:
                return 63749;
            case 3762:
                return '!';
            case 3763:
                return '*';
            case 3764:
                return 63746;
            case 3765:
                return 63747;
            case 3766:
                return 63750;
            case 3767:
                return 63751;
            case 3768:
                return 3788;
            case 3769:
                return 3772;
            case 3771:
                return 63745;
            case 3776:
                return ':';
            case 3777:
                return 3759;
            case 3778:
                return '3';
            case 3779:
                return '$';
            case 3780:
                return '0';
            case 3784:
                return 3787;
            case 3785:
                return 3786;
            case 3789:
                return 63744;
            default:
                return c;
        }
    }

    private static char kmshf(char c) {
        switch (c) {
            case 6016:
                return 6018;
            case 6017:
                return 6019;
            case 6020:
                return 6050;
            case 6021:
                return 6023;
            case 6022:
                return 6024;
            case 6026:
                return 6028;
            case 6027:
                return 6029;
            case 6031:
                return 6033;
            case 6032:
                return 6034;
            case 6035:
                return 6030;
            case 6036:
                return 6038;
            case 6037:
                return 6039;
            case 6040:
                return 6086;
            case 6041:
                return 6077;
            case 6042:
                return 6060;
            case 6043:
                return 6049;
            case 6044:
                return 6088;
            case 6047:
                return 6083;
            case 6048:
                return 6087;
            case 6053:
                return 6092;
            case 6054:
                return 6065;
            case 6058:
                return 6055;
            case 6062:
                return 6061;
            case 6066:
                return 6094;
            case 6070:
                return 6059;
            case 6071:
                return 6072;
            case 6073:
                return 6074;
            case 6075:
                return 6076;
            case 6078:
                return 6102;
            case 6080:
                return 6079;
            case 6081:
                return 6082;
            case 6084:
                return 6085;
            case 6090:
                return 6063;
            case 6091:
                return 6089;
            case 6098:
                return 6025;
            case 6100:
                return 6101;
            case 6112:
                return 6067;
            case 6113:
                return 6045;
            case 6114:
                return 6103;
            case 6115:
                return 6097;
            case 6116:
                return 6107;
            case 6117:
                return 6046;
            case 6118:
                return 6093;
            case 6119:
                return 6096;
            case 6120:
                return 6095;
            case 6121:
                return 6064;
            case 63746:
                return 63745;
            default:
                return c;
        }
    }

    static char sishf(char c) {
        switch (c) {
            case 3458:
                return 3459;
            case 3461:
                return 3467;
            case 3465:
                return 3466;
            case 3474:
                return 3476;
            case 3482:
                return 3483;
            case 3484:
                return 3485;
            case 3488:
                return 3489;
            case 3490:
                return 3491;
            case 3492:
                return 3493;
            case 3495:
                return 3496;
            case 3497:
                return 3498;
            case 3501:
                return 3502;
            case 3503:
                return 3504;
            case 3505:
                return 3499;
            case 3508:
                return 3509;
            case 3510:
                return 3511;
            case 3512:
                return 3513;
            case 3514:
                return 63745;
            case 3515:
                return 3469;
            case 3517:
                return 3525;
            case 3520:
                return 63746;
            case 3523:
                return 3522;
            case 3524:
                return 3521;
            case 3530:
                return 3551;
            case 3535:
                return 3544;
            case 3536:
                return 3537;
            case 3538:
                return 3539;
            case 3540:
                return 3542;
            case 3545:
                return 3526;
            case 63744:
                return 63747;
            default:
                return c;
        }
    }

    private static char bnshf(char c) {
        switch (c) {
            case 2434:
                return 2433;
            case 2453:
                return 2454;
            case 2455:
                return 2456;
            case 2458:
                return 2459;
            case 2460:
                return 2461;
            case 2463:
                return 2464;
            case 2465:
                return 2466;
            case 2468:
                return 2469;
            case 2470:
                return 2471;
            case 2472:
                return 2486;
            case 2474:
                return 2475;
            case 2476:
                return 2477;
            case 2478:
                return 2467;
            case 2479:
                return 2527;
            case 2480:
                return 2544;
            case 2482:
                return 2435;
            case 2488:
                return 2487;
            case 2489:
                return 2457;
            case 2492:
                return 2462;
            case 2494:
                return 2438;
            case 2495:
                return 2439;
            case 2496:
                return 2440;
            case 2497:
                return 2441;
            case 2498:
                return 2442;
            case 2499:
                return 2443;
            case 2503:
                return 2447;
            case 2504:
                return 2448;
            case 2507:
                return 2451;
            case 2508:
                return 2452;
            case 2509:
                return 2437;
            case 2544:
                return 63745;
            case 2545:
                return 2510;
            default:
                return c;
        }
    }

    private static char gushf(char c) {
        switch (c) {
            case 2690:
                return 2689;
            case 2709:
                return 2710;
            case 2711:
                return 2712;
            case 2714:
                return 2715;
            case 2716:
                return 2717;
            case 2719:
                return 2720;
            case 2721:
                return 2722;
            case 2724:
                return 2725;
            case 2726:
                return 2727;
            case 2730:
                return 2731;
            case 2732:
                return 2733;
            case 2734:
                return 2723;
            case 2738:
                return 2739;
            case 2744:
                return 2742;
            case 2745:
                return 2713;
            case 2748:
                return 2718;
            case 2750:
                return 2694;
            case 2751:
                return 2695;
            case 2752:
                return 2696;
            case 2753:
                return 2697;
            case 2754:
                return 2698;
            case 2755:
                return 2699;
            case 2756:
                return 2691;
            case 2759:
                return 2703;
            case 2760:
                return 2704;
            case 2761:
                return 2705;
            case 2763:
                return 2707;
            case 2764:
                return 2708;
            case 2765:
                return 2693;
            case 2790:
                return ')';
            case 2791:
                return 2701;
            case 2792:
                return 2757;
            case 2793:
                return 2736;
            case 2794:
                return 63744;
            case 2795:
                return 63745;
            case 2796:
                return 63746;
            case 2797:
                return 63747;
            case 2798:
                return 63748;
            case 2799:
                return '(';
            default:
                return c;
        }
    }

    private static char knshf(char c) {
        switch (c) {
            case 3221:
                return 3222;
            case 3223:
                return 3224;
            case 3226:
                return 3227;
            case 3228:
                return 3229;
            case 3231:
                return 3232;
            case 3233:
                return 3234;
            case 3236:
                return 3237;
            case 3238:
                return 3239;
            case 3240:
                return 3251;
            case 3242:
                return 3243;
            case 3244:
                return 3245;
            case 3246:
                return 3235;
            case 3247:
                return '|';
            case 3248:
                return 3249;
            case 3250:
                return 3255;
            case 3253:
                return 3254;
            case 3256:
                return 3230;
            case 3257:
                return 3225;
            case 3262:
                return 3206;
            case 3263:
                return 3207;
            case 3264:
                return 3208;
            case 3265:
                return 3209;
            case 3266:
                return 3210;
            case 3267:
                return 3268;
            case 3270:
                return 3214;
            case 3271:
                return 3215;
            case 3272:
                return 3216;
            case 3274:
                return 3218;
            case 3275:
                return 3219;
            case 3276:
                return 3220;
            case 3277:
                return 3205;
            case 3302:
                return 3211;
            case 3303:
                return 3248;
            case 3304:
                return 63744;
            case 3305:
                return 63745;
            case 3306:
                return 63746;
            case 3307:
                return 63747;
            case 3308:
                return 63748;
            case 3309:
                return 3286;
            case 3310:
                return 3285;
            case 3311:
                return 3203;
            default:
                return c;
        }
    }

    private static char urshf(char c) {
        switch (c) {
            case 1569:
                return 1574;
            case 1575:
                return 1570;
            case 1576:
                return 1616;
            case 1578:
                return 1657;
            case 1580:
                return 1590;
            case 1583:
                return 1672;
            case 1585:
                return 1681;
            case 1586:
                return 1584;
            case 1587:
                return 1589;
            case 1588:
                return 1688;
            case 1591:
                return 1592;
            case 1593:
                return 1617;
            case 1601:
                return 1613;
            case 1602:
                return 1548;
            case 1604:
                return 1614;
            case 1605:
                return 1611;
            case 1606:
                return 1722;
            case 1608:
                return 1572;
            case 1662:
                return 1615;
            case 1670:
                return 1579;
            case 1705:
                return 1582;
            case 1711:
                return 1594;
            case 1726:
                return 1581;
            case 1729:
                return 1571;
            case 1740:
                return 1648;
            case 1746:
                return '-';
            default:
                return c;
        }
    }

    private static char amshf(char c) {
        switch (c) {
            case 4608:
                return 4969;
            case 4616:
                return 4970;
            case 4624:
                return 4971;
            case 4632:
                return 4972;
            case 4640:
                return 4973;
            case 4648:
                return 4974;
            case 4656:
                return 4975;
            case 4664:
                return 4976;
            case 4672:
                return 4977;
            case 4704:
                return 4978;
            case 4712:
                return 4965;
            case 4720:
                return 4979;
            case 4728:
                return 4980;
            case 4736:
                return 4981;
            case 4752:
                return 4982;
            case 4760:
                return 4983;
            case 4768:
                return 4984;
            case 4776:
                return 4985;
            case 4792:
                return 4986;
            case 4808:
                return 4987;
            case 4816:
                return 4988;
            case 4824:
                return 5008;
            case 4832:
                return 5009;
            case 4840:
                return 5010;
            case 4848:
                return 5011;
            case 4864:
                return 5012;
            case 4872:
                return 5013;
            case 4896:
                return 5014;
            case 4904:
                return 5015;
            case 4912:
                return 5016;
            case 4920:
                return 5017;
            case 4928:
                return 4960;
            case 4936:
                return 4963;
            case 4944:
                return 4964;
            case 4961:
                return 4966;
            case 4967:
                return 4968;
            default:
                return c;
        }
    }

    public static char dvqw(char c) {
        switch (c) {
            case 'a':
                return 1958;
            case 'b':
                return 1924;
            case 'c':
                return 1943;
            case 'd':
                return 1931;
            case 'e':
                return 1964;
            case 'f':
                return 1930;
            case 'g':
                return 1934;
            case 'h':
                return 1920;
            case 'i':
                return 1960;
            case 'j':
                return 1942;
            case 'k':
                return 1926;
            case 'l':
                return 1933;
            case 'm':
                return 1929;
            case 'n':
                return 1922;
            case 'o':
                return 1966;
            case 'p':
                return 1941;
            case 'q':
                return 1968;
            case BuildConfig.VERSION_CODE /*114*/:
                return 1923;
            case 's':
                return 1936;
            case 't':
                return 1932;
            case 'u':
                return 1962;
            case 'v':
                return 1928;
            case 'w':
                return 1927;
            case 'x':
                return 1944;
            case 'y':
                return 1940;
            case 'z':
                return 1938;
            default:
                return 0;
        }
    }

    public static char dvshf(char c) {
        switch (c) {
            case 1920:
                return 1945;
            case 1922:
                return 1935;
            case 1923:
                return 1948;
            case 1924:
                return 1950;
            case 1926:
                return 1946;
            case 1927:
                return 1954;
            case 1928:
                return 1957;
            case 1929:
                return 1951;
            case 1930:
                return 65010;
            case 1931:
                return 1937;
            case 1932:
                return 1939;
            case 1933:
                return 1925;
            case 1934:
                return 1955;
            case 1936:
                return 1921;
            case 1938:
                return 1953;
            case 1940:
                return 1952;
            case 1942:
                return 1947;
            case 1943:
                return 1949;
            case 1958:
                return 1959;
            case 1960:
                return 1961;
            case 1962:
                return 1963;
            case 1964:
                return 1965;
            case 1966:
                return 1967;
            case 1968:
                return 1956;
            default:
                return c;
        }
    }
}
