package com.klye.ime.latin;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.inputmethodservice.Keyboard.Key;
import android.net.Uri;
import android.os.Build;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputConnection;
import android.view.inputmethod.InputMethodInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout.LayoutParams;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;
import java.nio.CharBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class M extends Activity implements OnClickListener {
    public static final String DEFAULT_LAYOUT_ID = "4";
    static int FULL_WORD_FREQ_MULTIPLIER = 0;
    public static int JF = 0;
    private static final int MAX_ALTERNATIVES = 16;
    private static final int MAX_WORD_LENGTH = 48;
    public static final String N = "";
    static final String PREF_KEYBOARD_LAYOUT = "pref_keyboard_layout_20100902";
    static int TYPED_LETTER_MULTIPLIER = 0;
    static boolean aad = false;
    public static final int ac = 6357091;
    public static int ac1 = 0;
    private static String acc = null;
    static boolean acl = true;
    public static final int af = 6357094;
    public static final int ag = 6357095;
    public static final int al = 6357100;
    public static final int am = 6357101;
    public static final int ap = 6357104;
    public static final int ar = 6357106;
    public static final String arNum = "٠١٢٣٤٥٦٧٨٩";
    public static final int as = 6357107;
    static boolean asb = false;
    static boolean asp = false;
    public static final int at = 6357108;
    public static final int aw = 6357111;
    static boolean awk = false;
    public static final String ax = "ـ";
    public static final int az = 6357114;
    static boolean azertyC = false;
    public static final int b2 = 6422578;
    public static final int bb = 6422626;
    public static final int be = 6422629;
    static boolean bfa = false;
    public static final int bg = 6422631;
    static int bga = 0;
    static int bgb = 0;
    static boolean bgd = false;
    static List<CharSequence> bgl = new ArrayList();
    public static final int bh = 6422632;
    static boolean bho = false;
    public static final int bn = 6422638;
    static boolean bn1 = false;
    public static final int bo = 6422639;
    public static final int bp = 6422640;
    public static final int bs = 6422643;
    public static final int by = 6422649;
    static int cCy = -1;
    static int cHasp = 0;
    public static final int ca = 6488161;
    public static final String car = "ophone_arabic";
    public static final String cas = "ع ⇄ ـﻌـ";
    public static final int cb = 6488162;
    public static int cbc = 0;
    public static final int ch = 6488168;
    public static int cjs = 0;
    static boolean cjsp = false;
    public static final int ck = 6488171;
    public static final int cl = 6488172;
    public static final String cp = ".→。";
    public static final int cr = 6488178;
    public static final int cs = 6488179;
    public static final int cw = 6488183;
    public static final int cy = 6488185;
    public static int cy2 = -1;
    static boolean cyr = false;
    public static final int da = 6553697;
    public static final char date = '\u0001';
    static DC dc = new DC();
    public static final int de = 6553701;
    public static final int del = 127;
    public static final int di = 6553705;
    static AutoDictionary dicA = null;
    static UserBigramDictionary dicB = null;
    static ContactsDictionary dicC = null;
    static CC dicJa = null;
    static BinaryDictionary dicM = null;
    static UserDictionary dicU = null;
    static UText dicUt = new UText();
    public static boolean djv = false;
    static boolean dk = false;
    static boolean dkf = false;
    public static DisplayMetrics dm = null;
    public static String dmn = null;
    public static final String dmn1 = "dmn";
    static ArrayList<CharSequence> dmn2 = null;
    public static final int dn = 6553710;
    static boolean dsp = false;
    public static boolean dsw = false;
    public static final int dv = 6553718;
    static boolean dvn = false;
    public static final int dz = 6553722;
    private static String[] e0 = new String[]{"👦", "👧", "💋", "👨", "👩", "👕", "👟", "📷", "☎", "📱", "📠", "💻", "👊", "👍", "☝", "✊", "✌", "✋", "🎿", "⛳", "🎾", "⚾", "🏄", "⚽", "🐟", "🐴", "🚗", "⛵", "✈", "🚃", "🚅", "❓", "❗", "❤", "💔", "🕐", "🕑", "🕒", "🕓", "🕔", "🕕", "🕖", "🕗", "🕘", "🕙", "🕚", "🕛", "🌸", "🔱", "🌹", "🎄", "💍", "💎", "🏠", "⛪", "🏢", "🚉", "⛽", "🗻", "🎤", "🎥", "🎵", "🔑", "🎷", "🎸", "🎺", "🍴", "🍸", "☕", "🍰", "🍺", "⛄", "☁", "☀", "☔", "🌙", "🌄", "👼", "🐱", "🐯", "🐻", "🐶", "🐭", "🐳", "🐧", "😊", "😃", "😞", "😠", "💩"};
    private static String[] e1 = new String[]{"📫", "📮", "📩", "📲", "😜", "😍", "😱", "😓", "🐵", "🐙", "🐷", "👽", "🚀", "👑", "💡", "🍀", "💏", "🎁", "🔫", "🔍", "🏃", "🔨", "🎆", "🍁", "🍂", "👿", "👻", "💀", "🔥", "💼", "💺", "🍔", "⛲", "⛺", "♨", "🎡", "🎫", "💿", "📀", "📻", "📼", "📺", "👾", "〽", "🀄", "🆚", "💰", "🎯", "🏆", "🏁", "🎰", "🐎", "🚤", "🚲", "🚧", "🚹", "🚺", "🚼", "💉", "💤", "⚡", "👠", "🛀", "🚽", "🔊", "📢", "🎌", "🔒", "🔓", "🌆", "🍳", "📖", "💱", "💹", "📡", "💪", "🏦", "🚥", "🅿", "🚏", "🚻", "👮", "🏣", "🏧", "🏥", "🏪", "🏫", "🏨", "🚌", "🚕"};
    private static String[] e2 = new String[]{"🚶", "🚢", "🈁", "💟", "✴", "✳", "🔞", "🚭", "🔰", "♿", "📶", "♥", "♦", "♠", "♣", "#⃣", "➿", "🆕", "🆙", "🆒", "🈶", "🈚", "🈷", "🈸", "🔴", "⬛", "🔳", "1⃣", "2⃣", "3⃣", "4⃣", "5⃣", "6⃣", "7⃣", "8⃣", "9⃣", "0⃣", "🉐", "🈹", "🈂", "🆔", "🈵", "🈳", "🈯", "🈺", "👆", "👇", "👈", "👉", "⬆", "⬇", "➡", "🔙", "↗", "↖", "↘", "↙", "▶", "◀", "⏩", "⏪", "🔯", "♈", "♉", "♊", "♋", "♌", "♍", "♎", "♏", "♐", "♑", "♒", "♓", "⛎", "🔝", "🆗", "©", "®", "📳", "📴", "⚠", "💁"};
    private static String[] e3 = new String[]{"📝", "👔", "🌺", "🌷", "🌻", "💐", "🌴", "🌵", "🚾", "🎧", "🍶", "🍻", "㊗", "🚬", "💊", "🎈", "💣", "🎉", "✂", "🎀", "㊙", "💽", "📣", "👒", "👗", "👡", "👢", "💄", "💅", "💆", "💇", "💈", "👘", "👙", "👜", "🎬", "🔔", "🎶", "💓", "💗", "💘", "💙", "💚", "💛", "💜", "✨", "⭐", "💨", "💦", "⭕", "❌", "💢", "🌟", "❔", "❕", "🍵", "🍞", "🍦", "🍟", "🍡", "🍘", "🍚", "🍝", "🍜", "🍛", "🍙", "🍢", "🍣", "🍎", "🍊", "🍓", "🍉", "🍅", "🍆", "🎂", "🍱", "🍲"};
    private static String[] e4 = new String[]{"😥", "😏", "😔", "😁", "😉", "😣", "😖", "😪", "😝", "😌", "😨", "😷", "😳", "😒", "😰", "😲", "😭", "😂", "😢", "☺", "😄", "😡", "😚", "😘", "👀", "👃", "👂", "👄", "🙏", "👋", "👏", "👌", "👎", "👐", "🙅", "🙆", "💑", "🙇", "🙌", "👫", "👯", "🏀", "🏈", "🎱", "🏊", "🚙", "🚚", "🚒", "🚑", "🚓", "🎢", "🚇", "🚄", "🎍", "💝", "🎎", "🎓", "🎒", "🎏", "🌂", "💒", "🌊", "🍧", "🎇", "🐚", "🎐", "🌀", "🌾", "🎃", "🎑", "🍃", "🎅", "🌅", "🌇", "🌃", "🌈"};
    private static String[] e5 = new String[]{"🏩", "🎨", "🎩", "🏬", "🏯", "🏰", "🎦", "🏭", "🗼", "", "🇯🇵", "🇺🇸", "🇫🇷", "🇪", "🇮🇹", "🇬🇧", "🇪🇸", "🇷🇺", "🇨🇳", "🇰🇷", "👱", "👲", "👳", "👴", "👵", "👶", "👷", "👸", "🗽", "💂", "💃", "🐬", "🐦", "🐠", "🐤", "🐹", "🐛", "🐘", "🐨", "🐒", "🐑", "🐺", "🐮", "🐰", "🐍", "🐔", "🐗", "🐫", "🐸", "🅰", "🅱", "🆎", "🅾", "👣"};
    public static final int ee = 6619237;
    public static final int ee1 = 6619237;
    public static final int el = 6619244;
    public static int emj = R.xml.kbd_emj_o;
    public static AssetManager emjAm = null;
    public static final int en = 6619246;
    public static final int eo = 6619247;
    public static final int es = 6619251;
    static int esC = -1;
    public static final int et = 6619252;
    static boolean eth = false;
    public static final int eu = 6619253;
    public static final int fa = 6684769;
    public static final String faNum = "۰۱۲۳۴۵۶۷۸۹";
    public static final String fd = "εχ๑тïς";
    static boolean fdb = false;
    public static final int fi = 6684777;
    public static final int fj = 6684778;
    public static final int fo = 6684783;
    public static int fontSize = 100;
    public static final int fr = 6684786;
    public static int fsl = 0;
    public static int fsp = 0;
    public static final int ga = 6750305;
    static int gaC = -1;
    public static int gb = 0;
    public static int gbl = 0;
    public static int gbr = 0;
    public static final int gd = 6750308;
    public static int gl = 0;
    public static final int gl1 = 6750316;
    public static int gr = 0;
    public static int gt = 0;
    public static int gtl = 0;
    public static int gtr = 0;
    public static final int gu = 6750325;
    public static final int gz = 6750330;
    static H h = null;
    public static final int ha = 6815841;
    static boolean hard = false;
    static boolean hat = false;
    public static final int he = 6815845;
    public static final int hi = 6815849;
    public static boolean hk = false;
    public static int hlc = 6;
    public static int[] hlcs = new int[]{-217515, -6310145, -4457348, -20836, -4506, -39580, -11289374, -3355444, -2250002};
    public static final Paint hlp = new Paint();
    public static final int hm = 6815853;
    public static final int hr = 6815858;
    static boolean hsk = false;
    public static final int ht = 6815860;
    public static final int hu = 6815861;
    public static final int hw = 6815863;
    public static int hwF = 255;
    static boolean hwa = false;
    public static int hwk = 0;
    public static long hwt = 0;
    public static final int hy = 6815865;
    public static InputConnection ic = null;
    static int[] icb = new int[768];
    public static final int ig = 6881383;
    public static final int ii = 6881385;
    static boolean in = false;
    public static final int in1 = 6881390;
    static boolean insf = false;
    public static final int ip = 6881392;
    public static final String ipa = "iphone_arabic";
    public static final int is = 6881395;
    public static int isHW = 0;
    static boolean isLatinC = true;
    public static boolean isM = false;
    public static final int it = 6881396;
    public static final int iu = 6881397;
    public static LatinKeyboardView iv = null;
    public static final int iw = 6881399;
    public static final int ja = 6946913;
    static final boolean jb;
    static boolean jbe = true;
    static boolean jl = false;
    public static final int jv = 6946934;
    public static final int jw = 6946935;
    public static final int ka = 7012449;
    public static final int kb = 7012450;
    public static LatinKeyboard kbd = null;
    public static int kbh = 0;
    static int kcs = 0;
    public static int kfs = 0;
    public static KID kid = null;
    public static final int kk = 7012459;
    public static int kl = 0;
    public static final int kl1 = 7012460;
    public static int klAM = 0;
    public static int klAR = 1;
    public static int klBO = -1;
    public static int klJA = 0;
    public static int klKM = 1;
    public static int klKO = 0;
    public static int klKO1 = 0;
    public static int klRU = 0;
    public static int klTH = 0;
    public static int klZY = 0;
    public static int klbg = 2;
    public static int klfa = 1;
    public static int klhy = 0;
    public static int klsi = 0;
    public static int klzh = 0;
    public static final int km = 7012461;
    public static final int kn = 7012462;
    public static final int ko = 7012463;
    static boolean kof = false;
    public static final int kr = 7012466;
    public static int ksl = 100;
    public static int ksp = 100;
    public static final int ku = 7012469;
    public static final int ky = 7012473;
    public static final int la = 7077985;
    public static final Paint lbp = new Paint();
    static String lg = LatinIME.SELECTED_LANGUAGES;
    public static final int lj = 7077994;
    public static int ll = 0;
    public static final int ln = 7077998;
    public static final int lo = 7077999;
    public static int lpe = 0;
    static final String lps = "";
    static boolean lpsl = false;
    public static long lpt = 450;
    public static final String lpt1 = "lpt";
    static boolean lrn = false;
    public static final int ls = 7078003;
    static boolean ls1 = false;
    public static final int lt = 7078004;
    public static final int lu = 7078005;
    public static final int lv = 7078006;
    public static final int lx = 7078008;
    public static int lzt = 80;
    public static final int m1 = 7143473;
    public static final int m2 = 7143474;
    static boolean mAltCaps = false;
    static boolean mArConn = true;
    static boolean mArDisconn = false;
    static int mDD = 2;
    static boolean mDG = false;
    static boolean mDebugMode = false;
    public static int mHanja = 1;
    public static String mIL = null;
    public static LatinIME mIme = null;
    static String mKBc = null;
    public static int mLC = 0;
    static boolean mReverse = false;
    public static String mSL = null;
    static boolean mSms = false;
    static boolean mWideKey = false;
    public static final int ma = 7143521;
    static boolean me = false;
    static Meta meta = new Meta();
    public static final int mg = 7143527;
    public static final int mi = 7143529;
    public static final int mk = 7143531;
    public static final int ml = 7143532;
    public static final int mm = 7143533;
    public static int mm1 = 35;
    public static final int mn = 7143534;
    public static boolean mnf = false;
    public static final int mr = 7143538;
    public static final int ms = 7143539;
    public static final int mt = 7143540;
    public static int mt9 = 0;
    public static long mtt = 0;
    public static final int mv = 7143542;
    public static final int mx = 7143544;
    public static final int my = 7143545;
    public static final int mz = 7143546;
    public static final int nb = 7209058;
    static boolean ncf = true;
    public static final int ne = 7209061;
    static boolean nfs = false;
    public static final int nl = 7209068;
    static boolean nm = false;
    public static final int nn = 7209070;
    public static boolean noAutoCapC = false;
    public static char nose = '\u0000';
    static boolean ns = true;
    public static String nsa = null;
    public static final String nsa1 = "nsa";
    public static final String nsa2 = ".copilot,.teamviewer,.splashtop.remote,.Relmtech.Remote,.apps.tvremote,.dosbox,.rtsoft.tanked,.androidterm,wyse.pocketcloudfree,p5sys.android.jump,.terminal,.connectbot";
    public static final int nv = 7209078;
    static boolean nvo = false;
    public static final int nw = 7209079;
    public static final int oj = 7274602;
    public static final int om = 7274605;
    static boolean oo = false;
    public static final int or = 7274610;
    static boolean ots = false;
    public static final int pa = 7340129;
    public static final int pc = 7340131;
    public static String pkp = null;
    public static final int pl = 7340140;
    public static final int pn = 7340142;
    public static final int ps = 7340147;
    public static String psb = null;
    static List<CharSequence> psbl = null;
    public static final int pt = 7340148;
    static PopupWindow pw = null;
    public static CharSequence pw1 = null;
    static boolean pyfzzcs = false;
    static boolean qwertzC = false;
    static boolean rc = false;
    public static Resources res = null;
    public static long rld = 0;
    public static int rldc = 0;
    public static int rmd = 0;
    static boolean rmp = false;
    public static final int ro = 7471215;
    public static String roS = null;
    static boolean roST = false;
    public static String roT = null;
    public static int rot = 0;
    public static final String rtl = "rtl";
    public static final int ru = 7471221;
    public static final int s2 = 7536690;
    public static final int s3 = 7536691;
    public static final int s4 = 7536692;
    public static final int sa = 7536737;
    public static final int sd = 7536740;
    public static boolean sha = false;
    static int shc = 9;
    static boolean shd = false;
    public static final int si = 7536745;
    public static final int sk = 7536747;
    public static int[] skc = new int[]{-1, -8};
    public static final int sl = 7536748;
    public static boolean sld = false;
    static boolean sm = false;
    public static final String sm1 = "split";
    static boolean sm2 = false;
    public static final String sm2s = "splitp";
    static boolean sm3 = false;
    public static final String sms = "À→A";
    static boolean snl = false;
    public static final int so = 7536751;
    public static final int sq = 7536753;
    public static final int sr = 7536754;
    public static final int sr1 = 7536689;
    public static int ss = 0;
    static boolean sstl = true;
    static boolean stky = false;
    public static final int su = 7536757;
    public static final int sv = 7536758;
    public static final int sw = 7536759;
    static boolean sw2 = false;
    static boolean swp = false;
    static boolean swsl = false;
    public static final int sx = 7536760;
    public static String sym = null;
    public static final String sym1 = "sym";
    public static String sym2 = null;
    public static final int ta = 7602273;
    public static final int te = 7602277;
    public static final int tf = 7602278;
    static Typeface tf1 = null;
    static Typeface tf2 = null;
    static Typeface tf3 = null;
    public static final int tg = 7602279;
    public static final int th = 7602280;
    public static final int ti = 7602281;
    public static int tid = -1;
    public static final char time = '\u0002';
    public static final int tk = 7602283;
    public static final int tl = 7602284;
    static int tlc = 204;
    static boolean tlpc = false;
    public static final int tr = 7602290;
    public static final String tsc = "ı,";
    public static final int tt = 7602292;
    static TextView tv = null;
    public static final int tv1 = 7602294;
    public static final String tvo = "สระ";
    static boolean uc = false;
    public static final int ug = 7667815;
    public static final int uk = 7667819;
    public static final int ur = 7667826;
    static boolean usd = false;
    public static final int uz = 7667834;
    public static Vibrator v = null;
    public static final int vi = 7733353;
    public static Integer vib = null;
    static boolean vlcr = true;
    public static float volf = 0.0f;
    public static final int wa = 7798881;
    public static final int we = 7798885;
    public static int wfw = 1;
    static final String[] wl = new String[]{"cl", "hm", "s4", "mm", "my", "by", "ht", "b2", "lx", "pc", "tf", "jw", "sn", "gn", "bb", "jv", "ac", "wr", "wa", "yu", "nw", "ma", "pn", "di", "cb", "mz", "m2", "kl", "s2", "sx", "so", "dn", "lu", "ls", "gz", "fo", "sw", "ee", "oj", "m1", "mg", "mv", "cw", "ch", "ag", "am", "af", "ap", "al", "az", "ar", "as", "at", "aw", "bp", "bh", "bo", "be", "bg", "bn", "bs", "cy", "ck", "cs", "ca", "cr", "da", "de", "dv", "dz", "el", LatinIME.CURR_LANGUAGES, "en_GB", "eo", "es", "et", "eu", "fa", "fi", "fr", "ga", "gl", "gd", "gu", "ha", "hi", "hr", "hu", "hw", "hy", "ig", "ii", "in", "ip", "is", "it", "iu", "iw", "ja", "ka", "kn", "ko", "km", "kk", "ku", "ky", "la", "lj", "lo", "lt", "lv", "mi", "ml", "mr", "ms", "mn", "mk", "mt", "mx", "ne", "nb", "nn", "nl", "nv", "om", "or", "pa", "pl", "ps", "pt", "pt_BR", "ro", "ru", "rm", "sa", "sd", "si", "sk", "sl", "sq", "sr", "s1", "sv", "su", "ta", "te", "tg", "th", "ti", "tk", "tl", "tr", "tt", "tv", "ur", "ug", "uk", "uz_UZ", "uz_RU", "we", "wo", "vi", "yi", "zh_CJ", "zh_C3", "zh_PY", "zh_ZY", "zh_YP", "zh_YG", "zh_YY", "zh_CS", "zh_JB", "zh_BH", "zh_WB", "yo", "xh", "zu"};
    static int wlp = 50;
    public static final int wo = 7798895;
    public static final int wr = 7798898;
    static boolean ws = false;
    public static String xa = null;
    public static String xb = "";
    public static String xc = "";
    public static String xd = null;
    public static String xe = "";
    static Context xfc = null;
    public static String xg = null;
    public static String xh = null;
    public static final int xh1 = 7864424;
    public static String xi = null;
    public static String xj = "";
    public static String xk = null;
    public static String xl = "";
    public static String xm = "";
    public static String xn = "";
    public static String xo = null;
    public static String xp = null;
    public static String xq = "";
    public static String xr = null;
    public static String xs = "";
    public static String xt = null;
    static boolean xtf = false;
    public static String xu = null;
    public static String xv = "";
    public static String xw = "";
    public static String xx = null;
    public static String xy = null;
    public static String xz = null;
    public static final int yi = 7929961;
    public static final int yo = 7929967;
    public static final int yu = 7929973;
    public static final int zh = 7995496;
    public static int zhVo = 0;
    static boolean zhlx = false;
    static boolean zhsr = false;
    public static int zt = 80;
    public static final int zu = 7995509;
    static boolean zw = false;
    public static final char zwj = '‍';
    public static final char zwnj = '‌';
    public static final char zwsp = '​';
    private EditText ebox;
    private View test;

    public class C {
        static final int COPY = -108;
        static final int CUT = -109;
        static final int DONATE = -130;
        static final int F1 = -103;
        static final int FDEL = -99;
        static final int HELP = -128;
        static final int HW = -122;
        static final int HWTIPS = -127;
        static final int HW_CODE = -123;
        static final int JA_SMALL = -107;
        static final int JF = -119;
        static final int MISC = -120;
        static final int MODE_BASE = 120;
        static final int MODE_EDIT = -117;
        static final int MODE_EMOJI = -114;
        static final int MODE_NUM = -118;
        static final int MODE_SMILEY = -113;
        static final int MODE_STAR = -115;
        static final int MODE_T9 = -124;
        static final int NEXT_LANGUAGE = -104;
        static final int OPTIONS = -100;
        static final int OPTIONS_LONGPRESS = -101;
        static final int PASTE = -110;
        static final int PREV_LANGUAGE = -105;
        static final int SELECT_ALL = -111;
        static final int SEND_STROKE = -125;
        static final int SHARE = -126;
        static final int TRANS = -129;
        static final int VOICE = -102;
    }

    static class DC {
        boolean dc = false;
        long jn = 0;
        char pc = 0;

        DC() {
        }

        public void c(char c) {
            long now = System.currentTimeMillis();
            boolean z = c == this.pc && now < this.jn + 300;
            this.dc = z;
            this.jn = now;
            this.pc = c;
        }
    }

    static {
        boolean z = true;
        if (VERSION.SDK_INT < MAX_ALTERNATIVES) {
            z = false;
        }
        jb = z;
    }

    public static void l(Throwable e) {
        e.printStackTrace();
    }

    public static void w(String s) {
    }

    public static void e(String s) {
    }

    static String emj6(int codes) {
        if (ir(codes, 57345, 57434)) {
            return e0[codes - 57345];
        }
        if (ir(codes, 57601, 57690)) {
            return e1[codes - 57601];
        }
        if (ir(codes, 57857, 57939)) {
            return e2[codes - 57857];
        }
        if (ir(codes, 58113, 58189)) {
            return e3[codes - 58113];
        }
        if (ir(codes, 58369, 58444)) {
            return e4[codes - 58369];
        }
        if (ir(codes, 58625, 58678)) {
            return e5[codes - 58625];
        }
        return null;
    }

    static String emj2(int g) {
        int c = g / 1024;
        int a = g % 1024;
        return ("" + ((char) (55232 + c))) + ((char) (56320 + a));
    }

    private static String th(int f, int i) {
        String s = Integer.toHexString(f);
        switch (s.length()) {
            case 1:
                return "000" + s;
            case 2:
                return "00" + s;
            case 3:
                return "0" + s;
            default:
                return s;
        }
    }

    public static void adi(Context mService) {
        sa(mService, wp("http://honsosearch.appspot.com/a/" + rot + ".html"));
        slp();
        rot = (rot + 1) % 10;
    }

    public static void msg(Context c, String s) {
        msg(c, s, 0);
    }

    public static void msg(Context c, String s, int i) {
        try {
            Toast.makeText(c, s, i).show();
        } catch (Throwable th) {
        }
    }

    static String dn() {
        return dn(mIL);
    }

    static String dn(String l) {
        String c = "";
        String y = null;
        switch (lc(l)) {
            case ac /*6357091*/:
                y = "Acèh";
                break;
            case ag /*6357095*/:
                y = "Ἑλληνιστί";
                break;
            case al /*6357100*/:
                y = "Alemannisch";
                break;
            case am /*6357101*/:
                y = "አማርኛ";
                break;
            case ap /*6357104*/:
                y = "Apachean";
                break;
            case ar /*6357106*/:
                y = "العربية";
                if (mArConn) {
                    y = arConnect(y).toString();
                    break;
                }
                break;
            case as /*6357107*/:
                y = "অসমীয়া";
                break;
            case at /*6357108*/:
                y = "Asturian";
                break;
            case aw /*6357111*/:
                y = "अवधी";
                break;
            case b2 /*6422578*/:
                y = "ᨅᨔ ᨕᨘᨁᨗ";
                break;
            case bb /*6422626*/:
                y = "Berber";
                break;
            case be /*6422629*/:
                y = "Беларуская";
                break;
            case bg /*6422631*/:
                y = "български";
                break;
            case bh /*6422632*/:
                y = "भोजपुरी";
                break;
            case bn /*6422638*/:
                y = "বাংলা";
                break;
            case bo /*6422639*/:
                y = "བོད་ཡིག";
                break;
            case bp /*6422640*/:
                y = "বিষ্ণুপ্রিয়া মণিপুরী";
                break;
            case by /*6422649*/:
                y = "ᜊᜌ᜔ᜊᜌᜒᜈ᜔";
                break;
            case cb /*6488162*/:
                y = "Sinugboanong";
                break;
            case ck /*6488171*/:
                y = "ᏣᎳᎩ ᎦᏬᏂᎯᏍᏗ";
                break;
            case cl /*6488172*/:
                y = "Klallam";
                break;
            case cr /*6488178*/:
                y = "ᓀᐦᐃᔭᐍᐏᐣ";
                break;
            case cw /*6488183*/:
                y = "Choctaw";
                break;
            case di /*6553705*/:
                y = "Zazaki";
                break;
            case dn /*6553710*/:
                y = "Dinka";
                break;
            case dv /*6553718*/:
                y = "ދިވެހި ";
                break;
            case dz /*6553722*/:
                y = "རྫོང་ཁ་";
                break;
            case 6619237:
                y = "Èʋegbe";
                break;
            case el /*6619244*/:
                y = "Ελληνικά";
                break;
            case fa /*6684769*/:
                y = "فارسی";
                if (mArConn) {
                    y = arConnect(y).toString();
                    break;
                }
                break;
            case gu /*6750325*/:
                y = "ગુજરાતી";
                break;
            case gz /*6750330*/:
                y = "ግዕዝ";
                break;
            case hi /*6815849*/:
                y = "हिन्दी";
                break;
            case hm /*6815853*/:
                y = "hən̓q̓əmin̓əm ";
                break;
            case hr /*6815858*/:
                y = "Hrvatski";
                break;
            case hw /*6815863*/:
                y = "Hawaiian";
                break;
            case hy /*6815865*/:
                y = "Հայերեն";
                break;
            case ii /*6881385*/:
                y = "ꆈꌠꁱꂷ";
                break;
            case ip /*6881392*/:
                y = "IPA";
                break;
            case iu /*6881397*/:
                y = "ᐃᓄᒃᑎᑐᑦ";
                break;
            case iw /*6881399*/:
                y = "עברית";
                break;
            case ja /*6946913*/:
                y = "日本語";
                break;
            case jw /*6946935*/:
                y = "بهاس ملايو";
                break;
            case ka /*7012449*/:
                y = "ქართული";
                break;
            case kb /*7012450*/:
                y = "Taqbaylit";
                break;
            case kk /*7012459*/:
                y = "Қазақша";
                break;
            case kl1 /*7012460*/:
                y = "Kalaallisut";
                break;
            case km /*7012461*/:
                y = "ភាសាខ្មែរ";
                break;
            case kn /*7012462*/:
                y = "ಕನ್ನಡ";
                break;
            case ko /*7012463*/:
                y = "한국어";
                break;
            case ky /*7012473*/:
                y = "Кыргызча";
                break;
            case la /*7077985*/:
                y = "Latine";
                break;
            case lj /*7077994*/:
                y = "Lojban";
                break;
            case lo /*7077999*/:
                y = "ພາສາລາວ";
                break;
            case ls /*7078003*/:
                y = "Lushootseed";
                break;
            case lx /*7078008*/:
                y = "Linux";
                break;
            case m1 /*7143473*/:
                y = "Mingo";
                break;
            case m2 /*7143474*/:
                y = "მარგალური ნინა ";
                break;
            case ma /*7143521*/:
                y = "مصرى";
                break;
            case mk /*7143531*/:
                y = "Македонски";
                break;
            case ml /*7143532*/:
                y = "മലയാളം";
                break;
            case mm /*7143533*/:
                y = "ꯃꯤꯇꯩ";
                break;
            case mn /*7143534*/:
                y = "Монгол";
                break;
            case mr /*7143538*/:
                y = "मराठी";
                break;
            case mv /*7143542*/:
                y = "Mvskoke";
                break;
            case mx /*7143544*/:
                y = "ဘာသာ မန်";
                break;
            case my /*7143545*/:
                y = "မြန်မာဘာသာ";
                break;
            case mz /*7143546*/:
                y = "مازِرونی";
                if (mArConn) {
                    y = arConnect(y).toString();
                    break;
                }
                break;
            case nb /*7209058*/:
                y = "Norsk(Bokmål)";
                break;
            case ne /*7209061*/:
                y = "नेपाली";
                break;
            case nn /*7209070*/:
                y = "Norsk nynorsk";
                break;
            case nw /*7209079*/:
                y = "नेपाल भाषा";
                break;
            case oj /*7274602*/:
                y = "ᐊᓂᔑᓈᐯᒧᐎᓐ";
                break;
            case or /*7274610*/:
                y = "ଓଡ଼ିଆ";
                break;
            case pa /*7340129*/:
                y = "ਪੰਜਾਬੀ";
                break;
            case pc /*7340131*/:
                y = "Ποντιακή";
                break;
            case pn /*7340142*/:
                y = "پنجابی‎";
                break;
            case ps /*7340147*/:
                y = "پښتو";
                if (mArConn) {
                    y = arConnect(y).toString();
                    break;
                }
                break;
            case ru /*7471221*/:
                y = "Русский";
                break;
            case sr1 /*7536689*/:
                y = "Srpski";
                break;
            case s2 /*7536690*/:
                y = "کوردی";
                break;
            case s3 /*7536691*/:
                y = "ᮞᮥᮔ᮪ᮓ";
                break;
            case s4 /*7536692*/:
                y = "SENĆOŦEN";
                break;
            case sa /*7536737*/:
                y = "संस्कृतम्";
                break;
            case sd /*7536740*/:
                y = "سنڌي";
                if (mArConn) {
                    y = arConnect(y).toString();
                    break;
                }
                break;
            case si /*7536745*/:
                y = "සිංහල";
                break;
            case sq /*7536753*/:
                y = "Gjuha Shqipe";
                break;
            case sr /*7536754*/:
                y = "Српски";
                break;
            case sx /*7536760*/:
                y = "Lakȟóta";
                break;
            case ta /*7602273*/:
                y = "தமிழ்";
                break;
            case te /*7602277*/:
                y = "తెలుగు";
                break;
            case tf /*7602278*/:
                y = "ⵜⵉⴼⵉⵏⴰⵖ";
                break;
            case tg /*7602279*/:
                y = "Тоҷикӣ";
                break;
            case th /*7602280*/:
                y = "ไทย";
                break;
            case ti /*7602281*/:
                y = "ትግርኛ";
                break;
            case tt /*7602292*/:
                y = "Татарча";
                break;
            case tv1 /*7602294*/:
                y = "Тыва дыл";
                break;
            case ug /*7667815*/:
                y = "ئۇيغۇرچه";
                if (mArConn) {
                    y = arConnect(y).toString();
                    break;
                }
                break;
            case uk /*7667819*/:
                y = "Українська";
                break;
            case ur /*7667826*/:
                y = "اردو";
                if (mArConn) {
                    y = arConnect(y).toString();
                    break;
                }
                break;
            case uz /*7667834*/:
                if (l.charAt(3) != 'R') {
                    y = "O‘zbek";
                    break;
                }
                y = "Ўзбекча";
                break;
            case we /*7798885*/:
                y = "Serbšćina";
                break;
            case wr /*7798898*/:
                y = "Winaray";
                break;
            case yi /*7929961*/:
                y = "ייִדיש";
                break;
            case yu /*7929973*/:
                y = "Yukatek Maya";
                break;
            default:
                int zt = zhType(l);
                if (zt != -1) {
                    y = "中文";
                    switch (zt) {
                        case 66:
                            c = "笔画";
                            break;
                        case 67:
                            switch (l.charAt(4)) {
                                case '3':
                                    c = "倉頡3+5";
                                    break;
                                case 'J':
                                    c = "倉頡5";
                                    break;
                                default:
                                    c = "速成";
                                    break;
                            }
                        case 74:
                            y = "中.粵";
                            c = "雜牌";
                            break;
                        case 80:
                            c = "拼音";
                            break;
                        case 87:
                            c = "五笔";
                            break;
                        case 89:
                            y = "中.粵";
                            switch (l.charAt(4)) {
                                case 'G':
                                    c = "教院";
                                    break;
                                case 'Y':
                                    c = "Yale";
                                    break;
                                default:
                                    c = "粵拼";
                                    y = "中";
                                    break;
                            }
                        case 90:
                            c = "注音";
                            break;
                    }
                }
                break;
        }
        if (y == null) {
            Locale lc = lcl(l);
            c = lc.getDisplayCountry(lc);
            if (c.indexOf("United K") != -1) {
                c = "UK";
            }
            y = lc.getDisplayLanguage(lc);
            y = Character.toUpperCase(y.charAt(0)) + y.substring(1);
        }
        if (c.length() > 0) {
            c = "." + c;
        }
        return y + c;
    }

    static String dne(String l) {
        String y = null;
        switch (lc(l)) {
            case am /*6357101*/:
                y = "Ethiopian Amharic";
                break;
            case ar /*6357106*/:
                y = "Arabic";
                break;
            case as /*6357107*/:
                y = "Assamese";
                break;
            case aw /*6357111*/:
                y = "Awadhi";
                break;
            case b2 /*6422578*/:
                y = "Buginese";
                break;
            case bh /*6422632*/:
                y = "Bihari";
                break;
            case bn /*6422638*/:
                y = "Bengali";
                break;
            case bo /*6422639*/:
                y = "Tibetan";
                break;
            case bp /*6422640*/:
                y = "Bishnupriya Manipuri";
                break;
            case by /*6422649*/:
                y = "Baybayin";
                break;
            case ck /*6488171*/:
                y = "Cherokee";
                break;
            case cr /*6488178*/:
                y = "Cree";
                break;
            case dv /*6553718*/:
                y = "Dhivehi";
                break;
            case dz /*6553722*/:
                y = "Dzongkha";
                break;
            case fa /*6684769*/:
                y = "Farsi";
                break;
            case gu /*6750325*/:
                y = "Gujarati";
                break;
            case gz /*6750330*/:
                y = "Ge'ez";
                break;
            case hi /*6815849*/:
                y = "Hindi";
                break;
            case hy /*6815865*/:
                y = "Armenian";
                break;
            case ii /*6881385*/:
                y = "Yi彝";
                break;
            case iu /*6881397*/:
                y = "Inuktitut";
                break;
            case iw /*6881399*/:
                y = "Hebrew";
                break;
            case jw /*6946935*/:
                y = "Jawi";
                break;
            case ka /*7012449*/:
                y = "Georgian";
                break;
            case km /*7012461*/:
                y = "Khmer";
                break;
            case kn /*7012462*/:
                y = "Kannada";
                break;
            case lo /*7077999*/:
                y = "Lao";
                break;
            case m2 /*7143474*/:
                y = "Margaluri nina";
                break;
            case ma /*7143521*/:
                y = "Masri";
                break;
            case ml /*7143532*/:
                y = "Malayalam";
                break;
            case mm /*7143533*/:
                y = "Meetei Mayek";
                break;
            case mr /*7143538*/:
                y = "Marathi";
                break;
            case mx /*7143544*/:
                y = "Mon";
                break;
            case my /*7143545*/:
                y = "Myanmar";
                break;
            case mz /*7143546*/:
                y = "Mazandarani";
                break;
            case ne /*7209061*/:
                y = "Nepali";
                break;
            case nw /*7209079*/:
                y = "Newari";
                break;
            case oj /*7274602*/:
                break;
            case or /*7274610*/:
                y = "Oriya";
                break;
            case pa /*7340129*/:
            case pn /*7340142*/:
                y = "Punjabi";
                break;
            case ps /*7340147*/:
                y = "Pashto";
                break;
            case s2 /*7536690*/:
                y = "Soranî";
                break;
            case s3 /*7536691*/:
                y = "Sundanese";
                break;
            case sa /*7536737*/:
                y = "Sanskrit";
                break;
            case sd /*7536740*/:
                y = "Sindhi";
                break;
            case si /*7536745*/:
                y = "Sinhala";
                break;
            case sx /*7536760*/:
                y = "Lakota";
                break;
            case ta /*7602273*/:
                y = "Tamil";
                break;
            case te /*7602277*/:
                y = "Telugu";
                break;
            case tf /*7602278*/:
                y = "Tifinagh";
                break;
            case th /*7602280*/:
                y = "Thai";
                break;
            case ti /*7602281*/:
                y = "Tigrinya";
                break;
            case ug /*7667815*/:
                y = "Uighur";
                break;
            case ur /*7667826*/:
                y = "Urdu";
                break;
            case yi /*7929961*/:
                y = "Yiddish";
                break;
        }
        y = "Ojibwa";
        if (y == null) {
            return dne2(l);
        }
        return "( " + y + " )\nFont issue? Install MyAlpha\n\n";
    }

    static String dne2(String l) {
        String c = "";
        String y = null;
        int zt = zhType(l);
        if (zt != -1) {
            y = "Chinese";
            switch (zt) {
                case 66:
                    c = "Strokes";
                    break;
                case 67:
                    if (l.charAt(4) != 'J') {
                        c = "Sucheng";
                        break;
                    }
                    c = "Cangjie";
                    break;
                case 80:
                    c = "Pinyin";
                    break;
                case 87:
                    c = "Wubi";
                    break;
                case 89:
                    c = "Cantonese";
                    break;
                case 90:
                    c = "Zhuyin";
                    break;
            }
        }
        if (y == null) {
            return "";
        }
        if (c.length() > 0) {
            c = "." + c;
        }
        return "(" + y + c + ")\n\n";
    }

    static boolean isLatin2(String s) {
        return !(cyr || arConAble() || noAutoCap() || nonLatinEu()) || eth || uc || "YP".indexOf(zt) != -1 || ls1 || (ja == mLC && fk());
    }

    static boolean dfqwz(int c) {
        switch (c) {
            case al /*6357100*/:
            case bs /*6422643*/:
            case de /*6553701*/:
            case hr /*6815858*/:
            case hu /*6815861*/:
            case sr1 /*7536689*/:
            case sl /*7536748*/:
            case sq /*7536753*/:
            case we /*7798885*/:
                return true;
            default:
                return false;
        }
    }

    public static boolean sslv() {
        switch (mLC) {
            case bs /*6422643*/:
            case hr /*6815858*/:
            case sr1 /*7536689*/:
            case sl /*7536748*/:
                return true;
            default:
                return false;
        }
    }

    static boolean sc(int c) {
        switch (c) {
            case da /*6553697*/:
            case fi /*6684777*/:
            case fo /*6684783*/:
            case nb /*7209058*/:
            case nn /*7209070*/:
            case sv /*7536758*/:
                return true;
            default:
                return false;
        }
    }

    static boolean qb() {
        boolean z = false;
        switch (mLC) {
            case ac /*6357091*/:
            case am /*6357101*/:
            case cy /*6488185*/:
            case eo /*6619247*/:
            case et /*6619252*/:
            case gz /*6750330*/:
            case jv /*6946934*/:
            case mt /*7143540*/:
            case ro /*7471215*/:
            case ti /*7602281*/:
            case tk /*7602283*/:
            case wo /*7798895*/:
                if (ll == 1) {
                    return false;
                }
                return true;
            case b2 /*6422578*/:
            case bg /*6422631*/:
            case ch /*6488168*/:
            case cw /*6488183*/:
            case 6619237:
            case ha /*6815841*/:
            case ig /*6881383*/:
            case ip /*6881392*/:
            case is /*6881395*/:
            case la /*7077985*/:
            case m1 /*7143473*/:
            case mv /*7143542*/:
            case s4 /*7536692*/:
            case yo /*7929967*/:
                return true;
            case bo /*6422639*/:
            case by /*6422649*/:
            case cs /*6488179*/:
            case di /*6553705*/:
            case dz /*6553722*/:
            case en /*6619246*/:
            case ht /*6815860*/:
            case in1 /*6881390*/:
            case mi /*7143529*/:
            case mm /*7143533*/:
            case ms /*7143539*/:
            case tf /*7602278*/:
            case tl /*7602284*/:
                return false;
            default:
                if ((ll == 0 && (sc() || anh() || dfqwz(mLC) || esC != -1)) || ((ll == 9 || ll == 8) && isLatinC && zt == -1)) {
                    z = true;
                }
                return z;
        }
    }

    static boolean anh() {
        switch (mLC) {
            case ap /*6357104*/:
            case nv /*7209078*/:
                return true;
            default:
                return false;
        }
    }

    static boolean sc() {
        return sc(mLC);
    }

    static boolean dvgr(String s) {
        return "mr,sa,hi,bh,aw".contains(s);
    }

    static boolean dvorak() {
        return ll == 4 && isLatinC;
    }

    static boolean neo() {
        return ll == 10 && isLatinC;
    }

    static boolean bepo() {
        return ll == 11 && isLatinC;
    }

    static boolean tf() {
        return ll == 12 && isLatinC;
    }

    static boolean colemak() {
        return ll == 5 && isLatinC;
    }

    static boolean dvlh() {
        return ll == 6 && isLatinC;
    }

    static boolean dvrh() {
        return ll == 7 && isLatinC;
    }

    static void qwertz1() {
        boolean z = (ll == 0 && (dfqwz(mLC) || mLC == cs)) || ((ll == 2 || ll == 8) && isLatinC);
        qwertzC = z;
    }

    static boolean qwertz() {
        return qwertzC;
    }

    static boolean azerty() {
        return azertyC;
    }

    static void azerty1() {
        boolean z = (ll == 0 && (mIL.equals("fr") || mLC == ig || mLC == wo || mLC == bb)) || (ll == 3 && isLatinC);
        azertyC = z;
    }

    static int dk(boolean sm1) {
        int k;
        if (tf()) {
            k = R.xml.kbd_lt;
        } else {
            k = sm1 ? qb() ? R.xml.kbd_big_split : R.xml.kbd_split : qb() ? R.xml.kbd_big : R.xml.kbd_qwerty;
        }
        switch (mLC) {
            case ar /*6357106*/:
            case ma /*7143521*/:
                switch (klAR) {
                    case 1:
                        return sm3 ? R.xml.kbd_big_split : R.xml.kbd_big;
                    case 2:
                        return sm3 ? R.xml.kbd_split : R.xml.kbd_qwerty;
                    default:
                        return R.xml.kbd_ar;
                }
            case az /*6357114*/:
            case bb /*6422626*/:
            case kb /*7012450*/:
            case lt /*7078004*/:
            case tr /*7602290*/:
                if (ll == 0 || ll == 9 || ll == 8) {
                    return R.xml.kbd_lt;
                }
                return k;
            case bg /*6422631*/:
                switch (klbg) {
                    case 0:
                        return R.xml.kbd_lt;
                    default:
                        return k;
                }
            case bo /*6422639*/:
                break;
            case dz /*6553722*/:
                if (klBO == -1) {
                    klBO = 1;
                    break;
                }
                break;
            case fa /*6684769*/:
            case mz /*7143546*/:
            case s2 /*7536690*/:
                switch (klfa) {
                    case 1:
                        return R.xml.kbd_lt;
                    default:
                        return R.xml.kbd_qwerty;
                }
            case hy /*6815865*/:
                if (klhy == 1) {
                    return R.xml.kbd_lt;
                }
                return k;
            case iw /*6881399*/:
            case yi /*7929961*/:
                return R.xml.kbd_iw;
            case ko /*7012463*/:
                if (!sm1 && (isLand() || kof)) {
                    k = R.xml.kofull;
                }
                if (k != R.xml.kbd_split || kof) {
                    return k;
                }
                return R.xml.ko_split;
            case mx /*7143544*/:
            case my /*7143545*/:
                return R.xml.kbd_lt;
            case si /*7536745*/:
                if (klsi == 1) {
                    return R.xml.kbd_si;
                }
                return k;
            default:
                return k;
        }
        if (klBO == -1) {
            klBO = 0;
        }
        if (klBO == 1) {
            return R.xml.kbd_bo;
        }
        return k;
    }

    static String mergeKB(String s) {
        return mKBc;
    }

    static void mergeKB1(String s) {
        if (sc(mLC)) {
            s = "sv";
        } else if (es(s) != -1) {
            s = "es";
        } else if (s.equals("he") || s.equals("yi")) {
            s = "iw";
        } else if ("ti,gz".indexOf(s) != -1) {
            s = "am";
        } else if ("nw".indexOf(s) != -1) {
            s = "ne";
        } else if ("ag,pc".indexOf(s) != -1) {
            s = "el";
        } else if ("ma".indexOf(s) != -1) {
            s = "ar";
        } else if ("s2".indexOf(s) != -1) {
            s = "fa";
        } else if ("as,bp".indexOf(s) != -1) {
            s = "bn";
        } else if ("m2".indexOf(s) != -1) {
            s = "ka";
        } else if (s.equals("mk")) {
            s = "sr";
        } else if (s.equals("mz")) {
            s = "fa";
        } else if (s.equals("az")) {
            s = "tr";
        } else if (s.equals("dz")) {
            s = "bo";
        } else if (s.equals("pn")) {
            s = "ur";
        } else if (s.equals("ap")) {
            s = "nv";
        } else if (dfqwz(mLC)) {
            s = "de";
        } else if (dvgr(s)) {
            s = "hi";
        } else if (s.equals("fr_CA")) {
            s = LatinIME.CURR_LANGUAGES;
        } else if (cy(s) != -1) {
            s = "ru";
        }
        mKBc = s;
    }

    static int lc(CharSequence s) {
        return (s.charAt(0) << MAX_ALTERNATIVES) | s.charAt(1);
    }

    static int es(CharSequence s) {
        return "ca,eu,gl,es,es_US,at".contains(s) ? lc(s) : -1;
    }

    static void es1() {
        esC = es(mIL);
    }

    static int es() {
        return esC;
    }

    static int ga(CharSequence s) {
        return "ga,gd".contains(s) ? lc(s) : -1;
    }

    static void ga1() {
        gaC = ga(mIL);
    }

    static int ga() {
        return gaC;
    }

    static int cy(String s) {
        return "ru,be,bg,kk,ky,tt,tg,uk,tv,uz_RU".contains(s) ? lc(s) : -1;
    }

    static void cy1() {
        cCy = cy(mIL);
    }

    static int cy() {
        return cCy;
    }

    static String mergeDict(String s) {
        if (s.equals("dz")) {
            s = "bo";
        } else if ("s1,bs".indexOf(s) != -1) {
            s = "hr";
        } else if ("iu,oj".indexOf(s) != -1) {
            s = "cr";
        } else if ("en_GB".indexOf(s) != -1) {
            s = LatinIME.CURR_LANGUAGES;
        } else if (s.equals("he")) {
            s = "iw";
        }
        return s.toLowerCase();
    }

    static boolean hasDict(Context c, String l) {
        try {
            String p;
            if ("zh_YP".equalsIgnoreCase(l)) {
                p = "klye.hanwriting";
            } else {
                p = "klye.plugin." + mergeDict(l);
            }
            c.getPackageManager().getApplicationEnabledSetting(p);
            return true;
        } catch (Throwable th) {
            return false;
        }
    }

    static Context getDictContext(Context c1, String lang) {
        try {
            return c1.createPackageContext("klye.plugin." + lang, 0);
        } catch (Throwable th) {
            return null;
        }
    }

    static boolean isLand() {
        return dm != null && dm.heightPixels < dm.widthPixels;
    }

    static String fcu(String s) {
        return s.length() == 0 ? s : Character.toUpperCase(s.charAt(0)) + s.substring(1);
    }

    static char cc(String s) {
        return (s == null || s.length() <= 3) ? 0 : s.charAt(3);
    }

    static Locale lcl(String l) {
        return new Locale(l.substring(0, 2), l.length() >= 5 ? l.substring(3, 5) : "");
    }

    static int zhType(String s) {
        return lc(s) == zh ? cc(s) : -1;
    }

    static int cy2(String s) {
        switch (mLC) {
            case bg /*6422631*/:
            case mk /*7143531*/:
            case mn /*7143534*/:
            case sr /*7536754*/:
                return cc(s);
            default:
                return -1;
        }
    }

    static int zhType() {
        return zt;
    }

    public static String lsmap(int c) {
        return null;
    }

    static CharSequence to(CharSequence cs, int j) {
        if (j == -1) {
            return cs;
        }
        int n = cs.length();
        String s = "";
        for (int i = 0; i < n; i++) {
            char c = cs.charAt(i);
            switch (j) {
                case 45:
                    if (cs.charAt(i) == '-') {
                        if (nose == ')') {
                            break;
                        }
                        s = s + nose;
                        break;
                    }
                    s = s + c;
                    break;
                case 67:
                    s = s + toCJ(c);
                    break;
                case 69:
                    char c2 = am1(c);
                    StringBuilder append = new StringBuilder().append(s);
                    if (c2 != 0) {
                        c = c2;
                    }
                    s = append.append(c).toString();
                    break;
                case 80:
                    s = s + toPunc(c);
                    break;
                case 83:
                    s = s + S.toUpper(c);
                    break;
                case 87:
                    s = s + toWB(c);
                    break;
                case 229:
                    s = s + sms(c);
                    break;
                default:
                    return cs;
            }
        }
        return s;
    }

    private static char sms(char c) {
        if (!isLatinC) {
            return c;
        }
        switch (Character.toLowerCase(c)) {
            case 178:
                return '2';
            case 179:
                return '3';
            case 230:
                return c;
            default:
                return (char) BinaryDictionary.toAccentLess(c);
        }
    }

    static char toEu(char c) {
        if (t9()) {
            return c;
        }
        switch (c) {
            case '$':
                return 8364;
            case 8364:
                return '$';
            default:
                return c;
        }
    }

    static char toPuncBo(char c) {
        switch (c) {
            case '*':
                return 1645;
            case ',':
                return 1548;
            case ';':
                return 1563;
            case '?':
                return 1567;
            default:
                return c;
        }
    }

    static char toPuncAr(char c) {
        switch (c) {
            case '*':
                return 1645;
            case ',':
                return 1548;
            case ';':
                return 1563;
            case '?':
                return 1567;
            default:
                return c;
        }
    }

    static char toDigitUr(char c) {
        if (irn(c)) {
            return (char) (c + 1728);
        }
        return 0;
    }

    static char toPuncCJK(char c) {
        switch (c) {
            case Suggest.APPROX_MAX_WORD_LENGTH /*32*/:
                if (mLC == ja) {
                    return 12288;
                }
                return c;
            case '!':
                return 65281;
            case ',':
                return 65292;
            case '.':
                return 12290;
            case ':':
                return 65306;
            case ';':
                return 65307;
            case '?':
                return 65311;
            case '{':
                return 12310;
            case '}':
                return 12311;
            case '~':
                return 65374;
            case 161:
                return '~';
            case 191:
                return '.';
            default:
                return c;
        }
    }

    static char toPuncHy(char c) {
        switch (c) {
            case '!':
                return 1372;
            case '\'':
                return 1370;
            case ',':
                return 1373;
            case '-':
                return 1418;
            case ':':
                return 1417;
            case ';':
                return 1371;
            case '?':
                return 1374;
            default:
                return c;
        }
    }

    static char toPuncEth(char c) {
        switch (c) {
            case '.':
                return 4962;
            default:
                return c;
        }
    }

    static char toCJ(char c) {
        switch (c) {
            case 'a':
                return 26085;
            case 'b':
                return 26376;
            case 'c':
                return 37329;
            case 'd':
                return 26408;
            case 'e':
                return 27700;
            case 'f':
                return 28779;
            case 'g':
                return 22303;
            case 'h':
                return 31481;
            case 'i':
                return 25096;
            case 'j':
                return 21313;
            case 'k':
                return 22823;
            case 'l':
                return 20013;
            case 'm':
                return 19968;
            case 'n':
                return 24339;
            case 'o':
                return 20154;
            case 'p':
                return 24515;
            case 'q':
                return 25163;
            case BuildConfig.VERSION_CODE /*114*/:
                return 21475;
            case 's':
                return 23608;
            case 't':
                return 24319;
            case 'u':
                return 23665;
            case 'v':
                return 22899;
            case 'w':
                return 30000;
            case 'x':
                return 38627;
            case 'y':
                return 21340;
            case 'z':
                return 37325;
            default:
                return c;
        }
    }

    static char toWB(char c) {
        switch (c) {
            case 'a':
                return 24037;
            case 'b':
                return 23376;
            case 'c':
                return 21448;
            case 'd':
                return 22823;
            case 'e':
                return 26376;
            case 'f':
                return 22303;
            case 'g':
                return 29579;
            case 'h':
                return 30446;
            case 'i':
                return 27700;
            case 'j':
                return 26085;
            case 'k':
                return 21475;
            case 'l':
                return 30000;
            case 'm':
                return 23665;
            case 'n':
                return 24050;
            case 'o':
                return 28779;
            case 'p':
                return 20043;
            case 'q':
                return 37329;
            case BuildConfig.VERSION_CODE /*114*/:
                return 30333;
            case 's':
                return 26408;
            case 't':
                return 31166;
            case 'u':
                return 31435;
            case 'v':
                return 22899;
            case 'w':
                return 20154;
            case 'x':
                return 32415;
            case 'y':
                return 35328;
            default:
                return c;
        }
    }

    static char jaTog(char c) {
        switch (c) {
            case 12353:
            case 12355:
            case 12359:
            case 12361:
            case 12363:
            case 12365:
            case 12367:
            case 12369:
            case 12371:
            case 12373:
            case 12375:
            case 12377:
            case 12379:
            case 12381:
            case 12383:
            case 12385:
            case 12390:
            case 12392:
            case 12399:
            case 12400:
            case 12402:
            case 12403:
            case 12405:
            case 12406:
            case 12408:
            case 12409:
            case 12411:
            case 12412:
            case 12419:
            case 12421:
            case 12423:
            case 12430:
            case 12449:
            case 12451:
            case 12455:
            case 12457:
            case 12459:
            case 12461:
            case 12463:
            case 12465:
            case 12467:
            case 12469:
            case 12471:
            case 12473:
            case 12475:
            case 12477:
            case 12479:
            case 12481:
            case 12486:
            case 12488:
            case 12495:
            case 12496:
            case 12498:
            case 12499:
            case 12501:
            case 12502:
            case 12504:
            case 12505:
            case 12507:
            case 12508:
            case 12515:
            case 12517:
            case 12519:
            case 12526:
                return (char) (c + 1);
            case 12354:
            case 12356:
            case 12360:
            case 12362:
            case 12364:
            case 12366:
            case 12368:
            case 12370:
            case 12372:
            case 12374:
            case 12376:
            case 12378:
            case 12380:
            case 12382:
            case 12384:
            case 12386:
            case 12388:
            case 12389:
            case 12391:
            case 12393:
            case 12420:
            case 12422:
            case 12424:
            case 12431:
            case 12450:
            case 12452:
            case 12456:
            case 12458:
            case 12460:
            case 12462:
            case 12464:
            case 12466:
            case 12468:
            case 12470:
            case 12472:
            case 12474:
            case 12476:
            case 12478:
            case 12480:
            case 12482:
            case 12484:
            case 12485:
            case 12487:
            case 12489:
            case 12516:
            case 12518:
            case 12520:
            case 12527:
                return (char) (c - 1);
            case 12357:
                return 12436;
            case 12358:
                return 12357;
            case 12387:
            case 12483:
                return (char) (c + 2);
            case 12401:
            case 12404:
            case 12407:
            case 12410:
            case 12413:
            case 12497:
            case 12500:
            case 12503:
            case 12506:
            case 12509:
                return (char) (c - 2);
            case 12436:
                return 12358;
            case 12453:
                return 12532;
            case 12454:
                return 12453;
            case 12532:
                return 12454;
            default:
                return c;
        }
    }

    static char toKata(boolean shf, char c) {
        return shf ? toKata(c, 12449) : c;
    }

    static char toKata(char c, int i) {
        if (ir(c, 12353, 12438)) {
            return (char) ((i - 12353) + c);
        }
        return c;
    }

    static char an(int i) {
        switch (mLC) {
            case ar /*6357106*/:
            case ma /*7143521*/:
            case ug /*7667815*/:
                i += 1632;
                break;
            case fa /*6684769*/:
            case mz /*7143546*/:
            case ps /*7340147*/:
            case s2 /*7536690*/:
            case sd /*7536740*/:
            case ur /*7667826*/:
                i += 1776;
                break;
            default:
                i = 0;
                break;
        }
        return (char) i;
    }

    static String an() {
        switch (mLC) {
            case ar /*6357106*/:
            case ug /*7667815*/:
                return arNum;
            default:
                return faNum;
        }
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.m, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.exit:
                ex();
                break;
        }
        return true;
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View v = getLayoutInflater().inflate(R.layout.help, null);
        setContentView(v);
        v.findViewById(R.id.enable).setOnClickListener(this);
        v.findViewById(R.id.enable2).setOnClickListener(this);
        v.findViewById(R.id.enable3).setOnClickListener(this);
        v.findViewById(R.id.enable4).setOnClickListener(this);
        v.findViewById(R.id.share).setOnClickListener(this);
        v.findViewById(R.id.home).setOnClickListener(this);
        v.findViewById(R.id.settings).setOnClickListener(this);
        View findViewById = v.findViewById(R.id.test);
        this.test = findViewById;
        findViewById.setOnClickListener(this);
        this.ebox = (EditText) v.findViewById(R.id.ebox);
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.enable:
                sa(this, new Intent("android.settings.INPUT_METHOD_SETTINGS"));
                return;
            case R.id.enable2:
                en();
                return;
            case R.id.enable3:
                if (!cm()) {
                    adi(this);
                }
                sa(this, hp("plugin.html"));
                return;
            case R.id.enable4:
                lsl(this);
                return;
            case R.id.settings:
                launchSettings(this);
                return;
            case R.id.share:
                sa(this, share(getString(R.string.hw_share), getString(R.string.hw_share_text)));
                return;
            case R.id.home:
                if (!cm()) {
                    adi(this);
                }
                sa(this, hp("ml.html"));
                return;
            case R.id.test:
                if (this.ebox.getVisibility() == 0) {
                    sa(this, hp("ma.html"));
                    return;
                }
                this.ebox.setVisibility(0);
                ((TextView) this.test).setText(getString(R.string.myalpha));
                return;
            default:
                return;
        }
    }

    public static void sa(Context c, Intent i) {
        try {
            c.startActivity(i);
        } catch (Throwable th) {
            msg(c, "Sorry, semi-automatic setup doesn't work for your device. However, you can set it up manually. Please read the FAQ and let developer know about this.");
        }
    }

    public static Intent sS() {
        Intent i = new Intent("android.intent.action.SENDTO", Uri.parse("mailto:honsohanwriting@gmail.com"));
        i.putExtra("android.intent.extra.SUBJECT", "[HW.Strokes]");
        i.putExtra("android.intent.extra.TEXT", h.strokesCode());
        i.addFlags(268435456);
        return i;
    }

    public static Intent share(String t, String s) {
        Intent intent = new Intent("android.intent.action.SEND", null);
        intent.setType("text/plain");
        intent.putExtra("android.intent.extra.TEXT", s);
        return Intent.createChooser(intent, t);
    }

    static void lsl(Context c) {
        launch(c, InputLanguageSelection.class);
    }

    static void lw(Context c) {
        launch(c, M.class);
    }

    static void launchSettings(Context c) {
        launch(c, LatinIMESettings.class);
    }

    static void launch(Context c, Class cls) {
        Intent intent = new Intent();
        intent.setClass(c, cls);
        intent.setFlags(268435456);
        sa(c, intent);
    }

    public static Intent hp(String s) {
        return wp("http://multiling-kbd.appspot.com/" + s);
    }

    public static Intent hp1(String s) {
        return wp("http://honsoapps.appspot.com/1/" + s);
    }

    public static Intent wp(String url) {
        Intent i = new Intent("android.intent.action.VIEW", Uri.parse(url));
        i.addFlags(268435456);
        return i;
    }

    static void noti(Context c, String s, Intent i) {
        try {
            noti1(c, s, i);
        } catch (Throwable th) {
            msg(c, s);
        }
    }

    static void noti1(Context c, CharSequence contentText, Intent i) {
        NotificationManager nm = (NotificationManager) c.getSystemService("notification");
        if (contentText == null) {
            nm.cancel(1);
            return;
        }
        CharSequence tickerText = c.getString(R.string.english_ime_name);
        Notification notification = new Notification(R.drawable.ic_application, contentText, System.currentTimeMillis());
        Context context = c.getApplicationContext();
        PendingIntent contentIntent = PendingIntent.getActivity(c, 0, i, 0);
        notification.flags |= MAX_ALTERNATIVES;
        notification.setLatestEventInfo(context, tickerText, contentText, contentIntent);
        nm.notify(1, notification);
    }

    private void en() {
        if (!ie()) {
            Toast.makeText(this, R.string.hw_enabled, 0).show();
        }
    }

    private boolean ie() {
        String s2 = getPackageName();
        InputMethodManager im = (InputMethodManager) getSystemService("input_method");
        for (InputMethodInfo imi : im.getEnabledInputMethodList()) {
            if (s2.equalsIgnoreCase(imi.getPackageName())) {
                im.showInputMethodPicker();
                return true;
            }
        }
        return false;
    }

    public static char ar2Iso(char c) {
        switch (c) {
            case 1569:
            case 65152:
                return 65152;
            case 1570:
            case 65153:
            case 65154:
                return 65153;
            case 1571:
            case 65155:
            case 65156:
                return 65155;
            case 1572:
            case 65157:
            case 65158:
                return 65157;
            case 1573:
            case 65159:
            case 65160:
                return 65159;
            case 1574:
            case 65161:
            case 65162:
            case 65163:
            case 65164:
                return 65161;
            case 1575:
            case 65165:
            case 65166:
                return 65165;
            case 1576:
            case 65167:
            case 65168:
            case 65169:
            case 65170:
                return 65167;
            case 1577:
            case 65171:
            case 65172:
                return 65171;
            case 1578:
            case 65173:
            case 65174:
            case 65175:
            case 65176:
                return 65173;
            case 1579:
            case 65177:
            case 65178:
            case 65179:
            case 65180:
                return 65177;
            case 1580:
            case 65181:
            case 65182:
            case 65183:
            case 65184:
                return 65181;
            case 1581:
            case 65185:
            case 65186:
            case 65187:
            case 65188:
                return 65185;
            case 1582:
            case 65189:
            case 65190:
            case 65191:
            case 65192:
                return 65189;
            case 1583:
            case 65193:
            case 65194:
                return 65193;
            case 1584:
            case 65195:
            case 65196:
                return 65195;
            case 1585:
            case 65197:
            case 65198:
                return 65197;
            case 1586:
            case 65199:
            case 65200:
                return 65199;
            case 1587:
            case 65201:
            case 65202:
            case 65203:
            case 65204:
                return 65201;
            case 1588:
            case 65205:
            case 65206:
            case 65207:
            case 65208:
                return 65205;
            case 1589:
            case 65209:
            case 65210:
            case 65211:
            case 65212:
                return 65209;
            case 1590:
            case 65213:
            case 65214:
            case 65215:
            case 65216:
                return 65213;
            case 1591:
            case 65217:
            case 65218:
            case 65219:
            case 65220:
                return 65217;
            case 1592:
            case 65221:
            case 65222:
            case 65223:
            case 65224:
                return 65221;
            case 1593:
            case 65225:
            case 65226:
            case 65227:
            case 65228:
                return 65225;
            case 1594:
            case 65229:
            case 65230:
            case 65231:
            case 65232:
                return 65229;
            case 1601:
            case 65233:
            case 65234:
            case 65235:
            case 65236:
                return 65233;
            case 1602:
            case 65237:
            case 65238:
            case 65239:
            case 65240:
                return 65237;
            case 1603:
            case 65241:
            case 65242:
            case 65243:
            case 65244:
                return 65241;
            case 1604:
            case 65245:
            case 65246:
            case 65247:
            case 65248:
                return 65245;
            case 1605:
            case 65249:
            case 65250:
            case 65251:
            case 65252:
                return 65249;
            case 1606:
            case 65253:
            case 65254:
            case 65255:
            case 65256:
                return 65253;
            case 1607:
            case 1749:
            case 65257:
            case 65258:
            case 65259:
            case 65260:
                return 65257;
            case 1608:
            case 65261:
            case 65262:
                return 65261;
            case 1609:
            case 65263:
            case 65264:
                return 65263;
            case 1610:
            case 65265:
            case 65266:
            case 65267:
            case 65268:
                return 65265;
            case 1657:
            case 64358:
            case 64359:
            case 64360:
            case 64361:
                return 64358;
            case 1662:
            case 64342:
            case 64343:
            case 64344:
            case 64345:
                return 64342;
            case 1668:
            case 64370:
            case 64371:
            case 64372:
            case 64373:
                return 64370;
            case 1670:
            case 64378:
            case 64379:
            case 64380:
            case 64381:
                return 64378;
            case 1671:
            case 64382:
            case 64383:
            case 64384:
            case 64385:
                return 64382;
            case 1672:
            case 64392:
            case 64393:
                return 64392;
            case 1681:
            case 64396:
            case 64397:
                return 64396;
            case 1688:
            case 64394:
            case 64395:
                return 64394;
            case 1705:
            case 64398:
            case 64399:
            case 64400:
            case 64401:
                return 64398;
            case 1709:
            case 64467:
            case 64468:
            case 64469:
            case 64470:
                return 64467;
            case 1711:
            case 64402:
            case 64403:
            case 64404:
            case 64405:
                return 64402;
            case 1713:
            case 64410:
            case 64411:
            case 64412:
            case 64413:
                return 64410;
            case 1715:
            case 64406:
            case 64407:
            case 64408:
            case 64409:
                return 64406;
            case 1722:
            case 64414:
            case 64415:
                return 64414;
            case 1726:
            case 64426:
            case 64427:
            case 64428:
            case 64429:
                return 64426;
            case 1728:
            case 64420:
            case 64421:
                return 64420;
            case 1729:
            case 64422:
            case 64423:
            case 64424:
            case 64425:
                return 64422;
            case 1735:
            case 64471:
            case 64472:
                return 64471;
            case 1736:
            case 64475:
            case 64476:
                return 64475;
            case 1739:
            case 64478:
            case 64479:
                return 64478;
            case 1740:
            case 64508:
            case 64509:
            case 64510:
            case 64511:
                return 64508;
            case 1744:
            case 64484:
            case 64485:
            case 64486:
            case 64487:
                return 64484;
            case 1746:
            case 64430:
            case 64431:
                return 64430;
            case 1747:
            case 64432:
            case 64433:
                return 64432;
            case 65269:
            case 65270:
                return 65269;
            case 65271:
            case 65272:
                return 65271;
            case 65273:
            case 65274:
                return 65273;
            case 65275:
            case 65276:
                return 65275;
            default:
                return c;
        }
    }

    public static boolean isAA(char c) {
        return ir(c, 1611, 1630) || c == 1648;
    }

    public static char ar2Init(char c) {
        if (isArWordSep(c) || c == 1600 || isAA(c)) {
            return c;
        }
        char c2 = ar2Iso(c);
        char c3 = (char) (c2 + 2);
        if (ar2Iso(c3) != c2) {
            c3 = c2;
        } else if (c3 == 64510) {
            c3 = 65267;
        }
        return c3;
    }

    public static char ar2Mid(char c) {
        if (isArWordSep(c)) {
            return 0;
        }
        if (c == 1600 || isAA(c)) {
            return c;
        }
        char c2 = ar2Iso(c);
        char c3 = (char) (c2 + 3);
        if (ar2Iso(c3) != c2) {
            c3 = 0;
        } else if (c3 == 64511) {
            c3 = 65268;
        }
        return c3;
    }

    public static char ar2Final(char c) {
        if (isArWordSep(c) || c == 1600 || isAA(c)) {
            return c;
        }
        char c2 = ar2Iso(c);
        char c3 = (char) (c2 + 1);
        if (ar2Iso(c3) != c2) {
            c3 = c2;
        }
        return c3;
    }

    public static CharSequence arConnect(CharSequence cs) {
        if (!g233()) {
            return cs;
        }
        int l = cs.length();
        if (l < 2) {
            return cs;
        }
        String s = "";
        boolean begin = true;
        char cp = 0;
        char cpp = 0;
        int i = 0;
        while (i < l) {
            int cmb = 0;
            char co = cs.charAt(i);
            char c1 = co;
            if (isAA(c1) || isArWordSep(c1)) {
                s = s + c1;
            } else {
                char c;
                c1 = ar2Iso(c1);
                if (cp == 65245) {
                    switch (c1) {
                        case 64422:
                        case 65257:
                            if (cpp == 65245 && l > 2) {
                                if (l < 4) {
                                    if (cpp == 65245 && l == 3) {
                                        c1 = 65010;
                                        begin = true;
                                        cmb = 2;
                                        break;
                                    }
                                }
                                c1 = 65010;
                                begin = true;
                                cmb = 3;
                                break;
                            }
                        case 65153:
                        case 65155:
                        case 65159:
                            c1 = (char) (c1 + 116);
                            begin = i == 1;
                            cmb = 1;
                            break;
                        case 65165:
                            c1 = 65275;
                            begin = i == 1;
                            cmb = 1;
                            break;
                    }
                }
                char cn = i < l + -1 ? cs.charAt(i + 1) : 0;
                boolean ux = co == 1749;
                boolean ia = i == l + -1 || ((i == l - 2 && isAA(cn)) || ux);
                if (begin) {
                    c = ia ? ar2Iso(c1) : ar2Init(c1);
                    if (ar2Mid(c1) == 0 || ux) {
                        begin = true;
                    } else {
                        begin = false;
                    }
                } else {
                    c = ar2Mid(c1);
                    if (c == 0 || ia || isArWordSep(cn) || ux) {
                        c = l == 1 ? ar2Iso(c1) : ar2Final(c1);
                        begin = true;
                    }
                }
                int l1 = s.length() - cmb;
                if (l1 < 0) {
                    l1 = 0;
                }
                if (cmb > 0) {
                    s = s.substring(0, l1);
                }
                s = s + c;
                if (null != null) {
                    s = s + 0;
                }
                cpp = cp;
                cp = c1;
            }
            i++;
        }
        return s;
    }

    public static boolean viD(char c) {
        switch (Character.toLowerCase(c)) {
            case 226:
            case 234:
            case 244:
            case 259:
            case 417:
            case 432:
                return true;
            default:
                return false;
        }
    }

    public static boolean viV(char c) {
        switch (Character.toLowerCase(c)) {
            case 'a':
            case 'e':
            case 'i':
            case 'o':
            case 'u':
            case 'y':
                return true;
            default:
                return false;
        }
    }

    public static boolean arConAble() {
        switch (mLC) {
            case ar /*6357106*/:
            case jw /*6946935*/:
            case ma /*7143521*/:
            case ug /*7667815*/:
                return true;
            default:
                return fu();
        }
    }

    public static boolean fu() {
        switch (mLC) {
            case fa /*6684769*/:
            case mz /*7143546*/:
            case pn /*7340142*/:
            case ps /*7340147*/:
            case s2 /*7536690*/:
            case sd /*7536740*/:
            case ur /*7667826*/:
                return true;
            default:
                return false;
        }
    }

    public static CharSequence ime(WordComposer wc) {
        switch (mLC) {
            case ja /*6946913*/:
                return wc.jaIme();
            case ko /*7012463*/:
                return Ko.koIme(wc);
            case vi /*7733353*/:
                return wc.viIme();
            default:
                wc.modi();
                return ime(wc.mTW, false);
        }
    }

    public static CharSequence ime(CharSequence s, boolean cmt) {
        if (arConAble() && mArConn && !(mArDisconn && cmt)) {
            s = arConnect(s);
        }
        switch (zt) {
            case 66:
            case 87:
                return to(s, zt);
            default:
                switch (mLC) {
                    case at /*6357108*/:
                        s = sud(s);
                        break;
                    default:
                        s = ime2(ime(s));
                        break;
                }
                return (mSms && cmt && isLatinC) ? to(s, 229) : s;
        }
    }

    private static CharSequence sud(CharSequence s) {
        int l = s.length();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < l; i++) {
            char c1 = s.charAt(i);
            char c2 = sud(c1);
            sb.append(c2);
            if (c1 != c2) {
                sb.append(803);
            }
        }
        return sb;
    }

    private static char sud(char c) {
        switch (c) {
            case 7716:
                return 'H';
            case 7717:
                return 'h';
            case 7734:
                return 'L';
            case 7735:
                return 'l';
            default:
                return c;
        }
    }

    static CharSequence reverse(CharSequence cs1) {
        return (mReverse && isRTL(cs1.charAt(0))) ? rev(cs1) : cs1;
    }

    static CharSequence rev(CharSequence cs1) {
        if (cs1 == null) {
            return cs1;
        }
        int n = cs1.length();
        char[] cs = new char[n];
        for (int i = 0; i < n; i++) {
            cs[i] = cs1.charAt((n - i) - 1);
        }
        return CharBuffer.wrap(cs);
    }

    static boolean isRTL(char c) {
        return isIw(c) || isAr(c) || dv(c);
    }

    private static boolean isIw(char c) {
        return (c > 1424 && c < 1535) || (c > 64285 && c < 64335);
    }

    static boolean isAr(char c) {
        return (c > 1536 && c < 1791) || ((c > 1872 && c < 1919) || ((c > 64336 && c < 65023) || (c > 65136 && c < 65279)));
    }

    public static CharSequence toUpper(CharSequence cs, boolean isTurk) {
        int l = cs.length();
        String s = "";
        for (int i = 0; i < l; i++) {
            char c = cs.charAt(i);
            StringBuilder append = new StringBuilder().append(s);
            char toUpperCase = (isTurk && c == 'i') ? 304 : Character.toUpperCase(c);
            s = append.append(toUpperCase).toString();
        }
        return s;
    }

    public static boolean np() {
        switch (mLC) {
            case am /*6357101*/:
            case gz /*6750330*/:
            case ja /*6946913*/:
            case ko /*7012463*/:
            case ti /*7602281*/:
            case vi /*7733353*/:
                return true;
            default:
                return arConAble() || es() != -1;
        }
    }

    public static boolean smo() {
        switch (mLC) {
            case cr /*6488178*/:
            case ii /*6881385*/:
            case iu /*6881397*/:
            case ja /*6946913*/:
            case oj /*7274602*/:
            case zh /*7995496*/:
                return true;
            default:
                return hw();
        }
    }

    public static boolean isT9Semi() {
        return (kid == null || "ZB".indexOf(zt) != -1 || mLC == th || mLC == km || mLC == ja || (!kid.is(R.xml.kbd_t9) && (!kid.is(R.xml.kbd_qw_er_ty) || cy() != -1))) ? false : true;
    }

    static boolean fk() {
        int zt = zhType();
        return !(t9() || ((qw_er() && cy() == -1) || zt == 66)) || (qw_er() && zt == 90);
    }

    static void rjf() {
        rmb("JF", JF);
    }

    static void rmb(String s, int i) {
        Editor e = PreferenceManager.getDefaultSharedPreferences(mIme).edit();
        e.putInt(s, i);
        e.commit();
    }

    static void rmb(String s, boolean i) {
        Editor e = PreferenceManager.getDefaultSharedPreferences(mIme).edit();
        e.putBoolean(s, i);
        e.commit();
    }

    static void rmb(String s, String v) {
        Editor e = PreferenceManager.getDefaultSharedPreferences(mIme).edit();
        e.putString(s, v);
        e.commit();
    }

    static void kmRo() {
        switch (mLC) {
            case am /*6357101*/:
            case gz /*6750330*/:
            case ti /*7602281*/:
                klAM = (klAM + 1) % 2;
                rmb("klam", klAM);
                return;
            case ar /*6357106*/:
            case ma /*7143521*/:
                klAR = (klAR + 1) % 3;
                rmb("klar", klAR);
                cl(mIL);
                return;
            case bg /*6422631*/:
                klbg = (klbg + 1) % 4;
                rmb("klbg", klbg);
                return;
            case bo /*6422639*/:
            case dz /*6553722*/:
                klBO = (klBO + 1) % 2;
                rmb("klbo", klBO);
                return;
            case fa /*6684769*/:
            case mz /*7143546*/:
            case s2 /*7536690*/:
                klfa = (klfa + 1) % 2;
                rmb("klfa", klfa);
                cl(mIL);
                return;
            case hy /*6815865*/:
                klhy = (klhy + 1) % 2;
                rmb("klhy", klhy);
                return;
            case ja /*6946913*/:
                klJA = (klJA + 1) % 2;
                rmb("klja", klJA);
                return;
            case km /*7012461*/:
                klKM = (klKM + 1) % 2;
                rmb("klkm", klKM);
                return;
            case ko /*7012463*/:
                if (isLand()) {
                    klKO1 = (klKO1 + 1) % 4;
                    rmb("klko1", klKO1);
                    return;
                }
                klKO = (klKO + 1) % 4;
                rmb("klko", klKO);
                return;
            case si /*7536745*/:
                klsi = (klsi + 1) % 2;
                nm = klsi == 0;
                rmb("klsi", klsi);
                return;
            case th /*7602280*/:
                klTH = (klTH + 1) % 2;
                rmb("klth", klTH);
                return;
            case zh /*7995496*/:
                switch (zt) {
                    case 80:
                    case 89:
                        klzh = (klzh + 1) % 3;
                        rmb("klzh", klzh);
                        return;
                    case 90:
                        klZY = (klZY + 1) % 3;
                        rmb("klzy", klZY);
                        return;
                    default:
                        return;
                }
            default:
                if (cy() != -1) {
                    klRU = (klRU + 1) % 3;
                    rmb("klru", klRU);
                    return;
                }
                kl = (kl + 1) % 3;
                rmb("kl", kl);
                return;
        }
    }

    static boolean fko() {
        switch (zhType()) {
            case LatinKeyboardBaseView.NOT_A_TOUCH_COORDINATE /*-1*/:
                switch (lc(mergeKB(mIL))) {
                    case ap /*6357104*/:
                    case aw /*6357111*/:
                    case b2 /*6422578*/:
                    case bh /*6422632*/:
                    case by /*6422649*/:
                    case ck /*6488171*/:
                    case dn /*6553710*/:
                    case dv /*6553718*/:
                    case gu /*6750325*/:
                    case hi /*6815849*/:
                    case ip /*6881392*/:
                    case iw /*6881399*/:
                    case ka /*7012449*/:
                    case kn /*7012462*/:
                    case lo /*7077999*/:
                    case m2 /*7143474*/:
                    case ml /*7143532*/:
                    case mm /*7143533*/:
                    case mn /*7143534*/:
                    case mr /*7143538*/:
                    case mx /*7143544*/:
                    case my /*7143545*/:
                    case ne /*7209061*/:
                    case nv /*7209078*/:
                    case or /*7274610*/:
                    case pa /*7340129*/:
                    case sa /*7536737*/:
                    case sr /*7536754*/:
                    case ta /*7602273*/:
                    case te /*7602277*/:
                    case tf /*7602278*/:
                    case vi /*7733353*/:
                    case yi /*7929961*/:
                        return true;
                    case ar /*6357106*/:
                    case fa /*6684769*/:
                    case ma /*7143521*/:
                    case mz /*7143546*/:
                    case s2 /*7536690*/:
                    case si /*7536745*/:
                        return false;
                    default:
                        if (in || bn1 || arConAble()) {
                            return true;
                        }
                        return false;
                }
            default:
                return false;
        }
    }

    static int km() {
        switch (mLC) {
            case am /*6357101*/:
            case gz /*6750330*/:
            case ti /*7602281*/:
                return klAM;
            case bg /*6422631*/:
                return klbg;
            case ja /*6946913*/:
                return klJA;
            case km /*7012461*/:
                return klKM;
            case ko /*7012463*/:
                return isLand() ? klKO1 : klKO;
            case si /*7536745*/:
                return klsi;
            case th /*7602280*/:
                return klTH;
            case zh /*7995496*/:
                switch (zt) {
                    case 80:
                    case 89:
                        return klzh;
                    case 90:
                        return klZY;
                    default:
                        return 0;
                }
            default:
                if (cy() != -1) {
                    return klRU;
                }
                return kl;
        }
    }

    static boolean t9() {
        if (fko() || km() != 1 || ((isLand() && mLC != ko) || arConAble() || bho || mLC == hy || in)) {
            return false;
        }
        return true;
    }

    static boolean t9r() {
        switch (mLC) {
            case km /*7012461*/:
            case ko /*7012463*/:
            case th /*7602280*/:
                return false;
            default:
                return true;
        }
    }

    static boolean qw_er() {
        if (fko() || bg == mLC || km() != 2 || ((isLand() && zt != 90 && cy() == -1 && ko != mLC) || arConAble() || bho)) {
            return false;
        }
        return true;
    }

    static boolean hw() {
        return isHW == 1;
    }

    public static String voIL() {
        String[] vo = new String[]{"zh_HK", "zh_CN", "zh_TW"};
        if (zhType() != -1) {
            return vo[zhVo];
        }
        return mLC == sr1 ? "sr" : mIL;
    }

    public static boolean udx() {
        switch (mLC) {
            case ko /*7012463*/:
                return true;
            default:
                return dicU == null || isCJ() || hw();
        }
    }

    public static char toSimp(char u) {
        switch (u) {
            case 17329:
                return 17324;
            case 18017:
                return 17996;
            case 18300:
                return 18317;
            case 18759:
                return 18818;
            case 18843:
                return 18870;
            case 18847:
                return 18871;
            case 19575:
                return 19619;
            case 19999:
                return 20002;
            case 20006:
                return 24182;
            case 20040:
                return 24186;
            case 20094:
            case 24185:
                return 24178;
            case 20098:
                return 20081;
            case 20121:
                return 20120;
            case 20126:
                return 20122;
            case 20295:
                return 20267;
            case 20358:
                return 26469;
            case 20374:
                return 20177;
            case 20406:
                return 20387;
            case 20417:
                return 20451;
            case 20418:
            case 32363:
                return 31995;
            case 20448:
                return 20384;
            case 20480:
                return 20261;
            case 20486:
                return 20457;
            case 20489:
                return 20179;
            case 20491:
                return 20010;
            case 20497:
                return 20204;
            case 20523:
                return 20262;
            case 20553:
                return 20255;
            case 20596:
                return 20391;
            case 20597:
                return 20390;
            case 20605:
            case 20702:
                return 20266;
            case 20625:
                return 26480;
            case 20630:
                return 20263;
            case 20632:
                return 20254;
            case 20633:
                return 22791;
            case 20642:
                return 23478;
            case 20653:
                return 20323;
            case 20655:
                return 20588;
            case 20659:
                return 20256;
            case 20660:
                return 20251;
            case 20661:
                return 20538;
            case 20663:
                return 20260;
            case 20670:
                return 20542;
            case 20674:
                return 20603;
            case 20677:
                return 20165;
            case 20681:
                return 20325;
            case 20689:
                return 20392;
            case 20693:
                return 20166;
            case 20709:
                return 20389;
            case 20712:
                return 20606;
            case 20729:
                return 20215;
            case 20736:
                return 20202;
            case 20738:
                return 20396;
            case 20740:
                return 20159;
            case 20744:
                return 20393;
            case 20745:
                return 20461;
            case 20752:
                return 20647;
            case 20756:
                return 20454;
            case 20757:
                return 20394;
            case 20760:
            case 30433:
                return 23613;
            case 20767:
                return 20607;
            case 20778:
                return 20248;
            case 20786:
                return 20648;
            case 20791:
                return 20458;
            case 20794:
                return 20649;
            case 20795:
                return 20645;
            case 20796:
                return 20456;
            case 20812:
                return 20817;
            case 20818:
                return 20799;
            case 20823:
                return 20822;
            case 20839:
                return 20869;
            case 20841:
                return 20004;
            case 20863:
            case 34269:
                return 33402;
            case 20874:
                return 20876;
            case 20906:
                return 24130;
            case 20941:
                return 20923;
            case 20956:
                return 20955;
            case 20977:
                return 20975;
            case 21029:
            case 24390:
                return 21035;
            case 21034:
                return 21024;
            case 21060:
                return 21037;
            case 21063:
                return 21017;
            case 21067:
                return 20811;
            case 21079:
                return 21036;
            case 21083:
                return 21018;
            case 21085:
                return 21093;
            case 21102:
                return 21072;
            case 21108:
                return 21056;
            case 21109:
                return 21019;
            case 21123:
                return 21010;
            case 21124:
                return 26413;
            case 21127:
                return 21095;
            case 21128:
                return 22140;
            case 21129:
                return 21016;
            case 21130:
                return 21053;
            case 21132:
                return 21055;
            case 21133:
                return 21073;
            case 21137:
                return 21058;
            case 21185:
                return 21170;
            case 21205:
                return 21160;
            case 21209:
                return 21153;
            case 21211:
            case 21235:
                return 21195;
            case 21213:
                return 32988;
            case 21214:
                return 21171;
            case 21218:
                return 21183;
            case 21225:
                return 21210;
            case 21233:
                return 21154;
            case 21237:
                return 21169;
            case 21240:
                return 21149;
            case 21243:
                return 21248;
            case 21293:
                return 21286;
            case 21295:
            case 24409:
                return 27719;
            case 21297:
                return 21294;
            case 21312:
                return 21306;
            case 21332:
                return 21327;
            case 21371:
                return 21364;
            case 21401:
                return 21389;
            case 21408:
            case 24257:
                return 21397;
            case 21421:
                return 21388;
            case 21426:
                return 21385;
            case 21428:
                return 21411;
            case 21443:
                return 21442;
            case 21474:
                return 19995;
            case 21522:
                return 21668;
            case 21555:
                return 21556;
            case 21570:
                return 21525;
            case 21621:
                return 21996;
            case 21692:
                return 21593;
            case 21729:
                return 21592;
            case 21730:
                return 21652;
            case 21764:
                return 21591;
            case 21781:
                return 21795;
            case 21786:
                return 21539;
            case 21839:
                return 38382;
            case 21843:
            case 21855:
                return 21551;
            case 21854:
                return 21713;
            case 21858:
                return 21793;
            case 21914:
                return 21796;
            case 21930:
                return 20007;
            case 21932:
                return 20052;
            case 21934:
                return 21333;
            case 21938:
                return 21727;
            case 21958:
                return 21595;
            case 21959:
                return 21868;
            case 21962:
                return 21789;
            case 21966:
                return 21527;
            case 21978:
                return 21596;
            case 21993:
                return 21794;
            case 22006:
                return 21716;
            case 22022:
            case 27470:
                return 21497;
            case 22029:
                return 21949;
            case 22036:
                return 21589;
            case 22038:
                return 21863;
            case 22039:
                return 23581;
            case 22044:
                return 21787;
            case 22057:
                return 21719;
            case 22062:
                return 21792;
            case 22063:
                return 21880;
            case 22064:
                return 21501;
            case 22069:
                return 21715;
            case 22072:
                return 21586;
            case 22081:
            case 24801:
                return 24694;
            case 22085:
                return 21684;
            case 22099:
                return 22040;
            case 22109:
                return 21661;
            case 22112:
                return 21714;
            case 22117:
                return 21725;
            case 22118:
                return 21717;
            case 22127:
                return 22003;
            case 22130:
                return 21721;
            case 22132:
                return 21943;
            case 22136:
                return 21544;
            case 22137:
            case 30070:
                return 24403;
            case 22144:
                return 21659;
            case 22151:
                return 21523;
            case 22156:
                return 21724;
            case 22165:
                return 22108;
            case 22169:
            case 40807:
                return 21870;
            case 22182:
                return 21590;
            case 22184:
                return 21657;
            case 22190:
                return 21521;
            case 22195:
                return 21950;
            case 22196:
                return 20005;
            case 22198:
                return 22052;
            case 22208:
                return 21869;
            case 22209:
                return 21995;
            case 22210:
                return 22179;
            case 22213:
                return 20865;
            case 22216:
                return 21587;
            case 22217:
                return 21872;
            case 22220:
            case 34311:
                return 33487;
            case 22225:
                return 22065;
            case 22250:
                return 22257;
            case 22279:
                return 22261;
            case 22283:
                return 22269;
            case 22285:
                return 22260;
            case 22290:
                return 22253;
            case 22291:
                return 22278;
            case 22294:
                return 22270;
            case 22296:
            case 31984:
                return 22242;
            case 22384:
                return 22439;
            case 22453:
                return 22511;
            case 22497:
                return 22445;
            case 22518:
            case 22519:
                return 25191;
            case 22533:
                return 22362;
            case 22538:
                return 22441;
            case 22550:
                return 22452;
            case 22557:
                return 22490;
            case 22575:
                return 23591;
            case 22577:
                return 25253;
            case 22580:
                return 22330;
            case 22591:
                return 30897;
            case 22602:
                return 22359;
            case 22603:
                return 33556;
            case 22607:
                return 22450;
            case 22610:
                return 22488;
            case 22615:
                return 28034;
            case 22618:
                return 20898;
            case 22626:
                return 22366;
            case 22628:
                return 22489;
            case 22645:
                return 23576;
            case 22649:
                return 22545;
            case 22666:
                return 22443;
            case 22684:
                return 22368;
            case 22702:
                return 22549;
            case 22707:
                return 22367;
            case 22715:
            case 29254:
                return 22681;
            case 22718:
                return 22438;
            case 22727:
            case 32590:
                return 22363;
            case 22736:
            case 29885:
                return 29626;
            case 22739:
                return 21387;
            case 22744:
                return 22418;
            case 22745:
                return 22329;
            case 22746:
                return 22406;
            case 22750:
                return 22351;
            case 22751:
                return 22404;
            case 22754:
                return 22364;
            case 22761:
                return 22365;
            case 22767:
                return 22766;
            case 22778:
                return 22774;
            case 22781:
            case 22784:
                return 23551;
            case 22816:
                return 22815;
            case 22818:
                return 26790;
            case 22821:
                return 20249;
            case 22846:
                return 22841;
            case 22864:
                return 22850;
            case 22887:
                return 22885;
            case 22889:
                return 22849;
            case 22890:
                return 22842;
            case 22892:
            case 29518:
                return 22870;
            case 22894:
                return 22859;
            case 22941:
                return 22918;
            case 22989:
                return 22999;
            case 23067:
                return 23089;
            case 23105:
                return 23044;
            case 23142:
                return 22919;
            case 23149:
                return 23045;
            case 23207:
                return 23090;
            case 23215:
                return 22955;
            case 23228:
                return 23210;
            case 23229:
                return 22920;
            case 23243:
            case 35018:
                return 34949;
            case 23255:
                return 22954;
            case 23285:
                return 22953;
            case 23291:
                return 23092;
            case 23295:
                return 23155;
            case 23304:
                return 23046;
            case 23307:
                return 23157;
            case 23308:
                return 23047;
            case 23321:
                return 23281;
            case 23329:
                return 23250;
            case 23332:
                return 23351;
            case 23338:
                return 23252;
            case 23344:
                return 23156;
            case 23352:
                return 23158;
            case 23372:
                return 23048;
            case 23403:
                return 23385;
            case 23416:
                return 23398;
            case 23423:
                return 23402;
            case 23470:
                return 23467;
            case 23516:
            case 23527:
                return 23425;
            case 23522:
                return 23517;
            case 23526:
                return 23454;
            case 23529:
                return 23457;
            case 23531:
                return 20889;
            case 23532:
                return 23485;
            case 23541:
                return 23456;
            case 23542:
                return 23453;
            case 23555:
                return 29995;
            case 23559:
                return 23558;
            case 23560:
                return 19987;
            case 23563:
                return 23547;
            case 23565:
                return 23545;
            case 23566:
                return 23548;
            case 23607:
                return 23604;
            case 23622:
                return 23626;
            case 23629:
                return 23608;
            case 23641:
                return 30132;
            case 23644:
                return 23625;
            case 23650:
                return 23649;
            case 23652:
                return 23618;
            case 23656:
                return 23654;
            case 23660:
                return 23646;
            case 23713:
                return 20872;
            case 23796:
                return 23704;
            case 23798:
                return 23707;
            case 23805:
                return 23777;
            case 23821:
                return 23811;
            case 23831:
                return 23703;
            case 23840:
            case 23852:
                return 23741;
            case 23842:
                return 23781;
            case 23859:
                return 23899;
            case 23888:
                return 23706;
            case 23895:
            case 27506:
                return 23681;
            case 23937:
                return 23901;
            case 23940:
                return 23853;
            case 23943:
                return 23702;
            case 23956:
                return 23898;
            case 23959:
                return 23810;
            case 23968:
                return 23780;
            case 23970:
                return 23779;
            case 23975:
                return 23748;
            case 23976:
                return 23747;
            case 23988:
                return 23705;
            case 23992:
                return 23896;
            case 23994:
                return 23725;
            case 23996:
                return 23679;
            case 23997:
                return 23731;
            case 24011:
                return 23743;
            case 24018:
                return 23782;
            case 24020:
                return 24005;
            case 24048:
                return 24047;
            case 24057:
                return 21370;
            case 24101:
                return 24069;
            case 24107:
                return 24072;
            case 24115:
                return 24080;
            case 24118:
                return 24102;
            case 24128:
                return 24103;
            case 24131:
                return 24079;
            case 24151:
                return 24124;
            case 24152:
                return 24123;
            case 24159:
                return 24092;
            case 24163:
                return 24065;
            case 24171:
                return 24110;
            case 24172:
                return 24113;
            case 24190:
                return 20960;
            case 24235:
                return 24211;
            case 24258:
                return 21410;
            case 24260:
                return 21417;
            case 24264:
                return 21414;
            case 24270:
                return 24252;
            case 24282:
                return 21416;
            case 24285:
                return 21422;
            case 24287:
                return 24217;
            case 24288:
                return 21378;
            case 24289:
                return 24209;
            case 24290:
                return 24223;
            case 24291:
                return 24191;
            case 24297:
                return 24298;
            case 24300:
                return 24208;
            case 24307:
                return 21381;
            case 24371:
                return 24362;
            case 24373:
                return 24352;
            case 24375:
                return 24378;
            case 24392:
                return 24377;
            case 24396:
            case 28720:
                return 24357;
            case 24398:
                return 24367;
            case 24421:
                return 24422;
            case 24460:
                return 21518;
            case 24465:
                return 24452;
            case 24478:
                return 20174;
            case 24480:
                return 24469;
            case 24489:
            case 35079:
                return 22797;
            case 24501:
                return 24449;
            case 24505:
                return 24443;
            case 24629:
                return 25015;
            case 24677:
                return 32827;
            case 24709:
                return 24742;
            case 24757:
                return 24581;
            case 24758:
                return 38391;
            case 24817:
                return 24700;
            case 24818:
                return 24701;
            case 24827:
                return 24699;
            case 24859:
                return 29233;
            case 24860:
                return 24812;
            case 24872:
            case 24932:
                return 24747;
            case 24884:
                return 24582;
            case 24887:
                return 24698;
            case 24894:
                return 24574;
            case 24907:
                return 24577;
            case 24909:
                return 24864;
            case 24920:
                return 24808;
            case 24922:
                return 24813;
            case 24927:
                return 24696;
            case 24931:
                return 24815;
            case 24938:
                return 24580;
            case 24939:
                return 24578;
            case 24942:
                return 34385;
            case 24947:
                return 24749;
            case 24950:
                return 24198;
            case 24962:
                return 24551;
            case 24970:
                return 24811;
            case 24976:
                return 24604;
            case 24977:
                return 20973;
            case 24978:
                return 24870;
            case 24986:
                return 24814;
            case 24996:
                return 24868;
            case 25003:
                return 24751;
            case 25006:
                return 24579;
            case 25010:
                return 23466;
            case 25014:
                return 24518;
            case 25031:
                return 24691;
            case 25033:
                return 24212;
            case 25036:
                return 24639;
            case 25037:
                return 25044;
            case 25054:
            case 28635:
            case 30663:
                return 33945;
            case 25055:
                return 24636;
            case 25059:
                return 25041;
            case 25064:
                return 24697;
            case 25074:
                return 24809;
            case 25078:
                return 25042;
            case 25079:
                return 24576;
            case 25080:
                return 24748;
            case 25082:
                return 24527;
            case 25084:
                return 24807;
            case 25086:
                return 24913;
            case 25088:
                return 24651;
            case 25095:
                return 25094;
            case 25108:
                return 25099;
            case 25127:
                return 25111;
            case 25129:
                return 25132;
            case 25136:
                return 25112;
            case 25138:
                return 25103;
            case 25142:
                return 25143;
            case 25185:
                return 25190;
            case 25291:
                return 25243;
            case 25406:
                return 25375;
            case 25448:
                return 33293;
            case 25451:
                return 25194;
            case 25458:
                return 21367;
            case 25475:
                return 25195;
            case 25476:
                return 25249;
            case 25478:
                return 14799;
            case 25495:
                return 25372;
            case 25497:
                return 25379;
            case 25499:
                return 25346;
            case 25536:
                return 25315;
            case 25562:
                return 25196;
            case 25563:
                return 25442;
            case 25582:
                return 25381;
            case 25613:
                return 25439;
            case 25622:
                return 25671;
            case 25623:
                return 25443;
            case 25654:
                return 25250;
            case 25681:
                return 25524;
            case 25692:
                return 25532;
            case 25695:
                return 25602;
            case 25699:
                return 25592;
            case 25711:
                return 25370;
            case 25715:
                return 25248;
            case 25718:
                return 25247;
            case 25722:
                return 25240;
            case 25723:
                return 25530;
            case 25736:
                return 25438;
            case 25743:
                return 25382;
            case 25744:
                return 25745;
            case 25747:
                return 25376;
            case 25754:
                return 25467;
            case 25759:
                return 25378;
            case 25763:
                return 25528;
            case 25765:
                return 25320;
            case 25771:
                return 25242;
            case 25778:
                return 25169;
            case 25779:
                return 25599;
            case 25787:
                return 25374;
            case 25790:
                return 25373;
            case 25791:
                return 25441;
            case 25793:
                return 25317;
            case 25796:
                return 25523;
            case 25799:
                return 25321;
            case 25802:
                return 20987;
            case 25803:
                return 25377;
            case 25811:
                return 14815;
            case 25812:
                return 25285;
            case 25818:
                return 25454;
            case 25824:
                return 25380;
            case 25836:
                return 25311;
            case 25839:
                return 25672;
            case 25840:
                return 25319;
            case 25841:
                return 25601;
            case 25842:
                return 25527;
            case 25844:
                return 25193;
            case 25847:
                return 25783;
            case 25849:
            case 25892:
                return 25674;
            case 25850:
            case 35180:
                return 25670;
            case 25851:
                return 25822;
            case 25852:
                return 25784;
            case 25854:
                return 25200;
            case 25860:
                return 25669;
            case 25862:
                return 25781;
            case 25871:
                return 25314;
            case 25876:
                return 25318;
            case 25878:
                return 25732;
            case 25881:
                return 25600;
            case 25883:
                return 25786;
            case 25884:
                return 25658;
            case 25885:
                return 25668;
            case 25890:
                return 25874;
            case 25891:
                return 25371;
            case 25898:
                return 25605;
            case 25900:
                return 25597;
            case 25943:
                return 36133;
            case 25944:
                return 21465;
            case 25973:
                return 25932;
            case 25976:
                return 25968;
            case 25986:
                return 25947;
            case 25987:
                return 27609;
            case 26005:
                return 26003;
            case 26028:
                return 26025;
            case 26039:
                return 26029;
            case 26044:
                return 20110;
            case 26178:
                return 26102;
            case 26185:
                return 26187;
            case 26205:
                return 26172;
            case 26248:
                return 26197;
            case 26249:
                return 26198;
            case 26264:
                return 26104;
            case 26274:
                return 30021;
            case 26283:
                return 26242;
            case 26308:
                return 26196;
            case 26310:
            case 27511:
                return 21382;
            case 26311:
                return 26137;
            case 26313:
                return 26195;
            case 26326:
                return 26279;
            case 26336:
                return 26103;
            case 26344:
                return 26173;
            case 26348:
                return 26194;
            case 26360:
                return 20070;
            case 26371:
                return 20250;
            case 26407:
                return 32999;
            case 26481:
                return 19996;
            case 26613:
                return 26629;
            case 26772:
                return 26624;
            case 26776:
                return 26535;
            case 26781:
                return 26465;
            case 26783:
                return 26541;
            case 26820:
                return 24323;
            case 26838:
                return 26536;
            case 26839:
                return 26531;
            case 26847:
                return 26635;
            case 26849:
                return 15182;
            case 26855:
                return 26632;
            case 26860:
                return 26698;
            case 26866:
                return 26646;
            case 26895:
                return 26720;
            case 26930:
                return 15183;
            case 26954:
                return 26472;
            case 26963:
                return 26539;
            case 26984:
                return 26722;
            case 26989:
                return 19994;
            case 26997:
                return 26497;
            case 27030:
                return 35895;
            case 27050:
                return 26473;
            case 27054:
                return 33635;
            case 27071:
                return 26724;
            case 27083:
                return 26500;
            case 27085:
                return 26538;
            case 27111:
                return 26912;
            case 27112:
                return 26881;
            case 27123:
                return 26728;
            case 27137:
                return 26729;
            case 27138:
                return 20048;
            case 27141:
                return 26526;
            case 27155:
                return 27004;
            case 27161:
                return 26631;
            case 27166:
                return 26530;
            case 27171:
                return 26679;
            case 27192:
                return 26420;
            case 27193:
                return 26641;
            case 27194:
                return 26726;
            case 27208:
                return 26721;
            case 27211:
                return 26725;
            case 27231:
                return 26426;
            case 27234:
                return 26925;
            case 27243:
                return 27178;
            case 27265:
                return 27305;
            case 27273:
                return 26621;
            case 27284:
                return 26723;
            case 27292:
                return 26727;
            case 27295:
                return 27098;
            case 27298:
                return 26816;
            case 27299:
                return 27183;
            case 27311:
            case 33274:
            case 39089:
                return 21488;
            case 27315:
                return 27103;
            case 27320:
                return 26592;
            case 27323:
                return 27099;
            case 27326:
                return 33496;
            case 27331:
                return 26588;
            case 27347:
                return 27257;
            case 27354:
                return 27016;
            case 27355:
                return 26633;
            case 27357:
                return 26911;
            case 27358:
                return 27260;
            case 27359:
                return 26638;
            case 27365:
                return 27249;
            case 27367:
                return 27104;
            case 27368:
                return 26636;
            case 27370:
                return 26533;
            case 27371:
                return 27237;
            case 27372:
                return 27015;
            case 27379:
                return 26634;
            case 27384:
                return 27017;
            case 27387:
                return 27185;
            case 27396:
                return 26639;
            case 27402:
                return 26435;
            case 27407:
                return 26916;
            case 27410:
                return 26686;
            case 27414:
                return 27012;
            case 27422:
                return 26818;
            case 27453:
                return 38054;
            case 27472:
                return 27431;
            case 27487:
                return 27428;
            case 27489:
                return 27426;
            case 27512:
                return 24402;
            case 27519:
                return 27521;
            case 27544:
                return 27531;
            case 27550:
                return 27538;
            case 27556:
                return 27527;
            case 27560:
                return 15470;
            case 27563:
                return 27546;
            case 27566:
                return 27539;
            case 27567:
                return 27553;
            case 27570:
                return 27516;
            case 27578:
                return 26432;
            case 27580:
                return 22771;
            case 27584:
                return 27585;
            case 27590:
                return 27572;
            case 27647:
                return 27637;
            case 27656:
                return 27617;
            case 27660:
                return 27655;
            case 27683:
                return 27668;
            case 27691:
                return 27682;
            case 27692:
                return 27689;
            case 27699:
                return 27698;
            case 27705:
                return 20988;
            case 27770:
                return 20915;
            case 27794:
                return 27809;
            case 27841:
                return 20917;
            case 27958:
                return 27769;
            case 28025:
                return 27971;
            case 28039:
                return 27902;
            case 28114:
                return 20932;
            case 28122:
                return 27882;
            case 28133:
                return 28172;
            case 28136:
                return 20928;
            case 28138:
                return 27814;
            case 28149:
                return 28170;
            case 28150:
                return 28062;
            case 28154:
                return 27973;
            case 28185:
                return 28067;
            case 28187:
                return 20943;
            case 28194:
                return 27816;
            case 28198:
                return 28065;
            case 28204:
                return 27979;
            case 28222:
                return 27985;
            case 28234:
                return 20945;
            case 28254:
                return 27976;
            case 28259:
                return 24845;
            case 28263:
                return 28044;
            case 28271:
                return 27748;
            case 28296:
                return 27817;
            case 28310:
                return 20934;
            case 28317:
                return 27807;
            case 28331:
                return 28201;
            case 28334:
                return 27977;
            case 28339:
                return 28066;
            case 28356:
                return 27815;
            case 28357:
                return 28781;
            case 28364:
                return 28068;
            case 28366:
                return 33637;
            case 28396:
                return 27818;
            case 28399:
                return 28382;
            case 28402:
                return 28183;
            case 28407:
            case 40565:
                return 21348;
            case 28408:
                return 27986;
            case 28411:
                return 27984;
            case 28414:
                return 28378;
            case 28415:
                return 28385;
            case 28417:
                return 28180;
            case 28426:
                return 28295;
            case 28442:
                return 27812;
            case 28450:
                return 27721;
            case 28451:
                return 28063;
            case 28460:
                return 28173;
            case 28466:
                return 28072;
            case 28469:
                return 28294;
            case 28472:
                return 28176;
            case 28479:
                return 27974;
            case 28481:
                return 39053;
            case 28497:
                return 27900;
            case 28500:
                return 27905;
            case 28507:
                return 28508;
            case 28516:
                return 28070;
            case 28527:
                return 27988;
            case 28528:
                return 28291;
            case 28535:
                return 28375;
            case 28543:
                return 28064;
            case 28544:
                return 28073;
            case 28550:
                return 27975;
            case 28551:
                return 28061;
            case 28567:
                return 28071;
            case 28576:
                return 28177;
            case 28580:
                return 27901;
            case 28582:
                return 28394;
            case 28585:
                return 27894;
            case 28590:
                return 27981;
            case 28593:
                return 28096;
            case 28606:
                return 15584;
            case 28609:
                return 27978;
            case 28611:
                return 27987;
            case 28629:
                return 28287;
            case 28632:
                return 27870;
            case 28636:
                return 27989;
            case 28639:
                return 27982;
            case 28644:
                return 28059;
            case 28651:
                return 28389;
            case 28656:
                return 28493;
            case 28657:
                return 28392;
            case 28666:
                return 28293;
            case 28668:
                return 27898;
            case 28670:
                return 28388;
            case 28677:
                return 28386;
            case 28678:
                return 28174;
            case 28681:
                return 27899;
            case 28683:
                return 27784;
            case 28687:
                return 27983;
            case 28693:
                return 28626;
            case 28696:
                return 27896;
            case 28701:
                return 27813;
            case 28703:
                return 28487;
            case 28704:
                return 28486;
            case 28710:
                return 28532;
            case 28711:
                return 27895;
            case 28712:
                return 28625;
            case 28722:
                return 28491;
            case 28734:
                return 28572;
            case 28739:
                return 27811;
            case 28740:
                return 28384;
            case 28753:
                return 27922;
            case 28757:
                return 28435;
            case 28760:
                return 28393;
            case 28765:
                return 28751;
            case 28771:
                return 28286;
            case 28772:
                return 28390;
            case 28775:
            case 28777:
                return 28383;
            case 28797:
                return 28798;
            case 28858:
            case 29234:
                return 20026;
            case 28879:
                return 20044;
            case 28916:
                return 28867;
            case 28961:
                return 26080;
            case 29001:
                return 28860;
            case 29010:
                return 28828;
            case 29017:
                return 28895;
            case 29026:
                return 33557;
            case 29029:
                return 28949;
            case 29033:
                return 28902;
            case 29036:
                return 28800;
            case 29074:
                return 33639;
            case 29079:
                return 28829;
            case 29105:
                return 28909;
            case 29118:
                return 28861;
            case 29121:
                return 28904;
            case 29128:
                return 28783;
            case 29129:
                return 28822;
            case 29138:
                return 28903;
            case 29145:
                return 28907;
            case 29148:
                return 28950;
            case 29151:
                return 33829;
            case 29158:
                return 28799;
            case 29165:
                return 28891;
            case 29172:
                return 28905;
            case 29180:
                return 28908;
            case 29182:
                return 28952;
            case 29197:
                return 28865;
            case 29200:
                return 28809;
            case 29211:
                return 28866;
            case 29229:
                return 20105;
            case 29242:
                return 29239;
            case 29246:
                return 23572;
            case 29247:
                return 20012;
            case 29272:
                return 29261;
            case 29309:
                return 29301;
            case 29334:
                return 33638;
            case 29339:
                return 29286;
            case 29346:
                return 29322;
            case 29351:
                return 29306;
            case 29376:
                return 29366;
            case 29433:
                return 29421;
            case 29437:
                return 29384;
            case 29465:
                return 29424;
            case 29494:
                return 29369;
            case 29499:
                return 29426;
            case 29505:
                return 29368;
            case 29508:
                return 29425;
            case 29509:
                return 29422;
            case 29544:
                return 29420;
            case 29546:
                return 29423;
            case 29547:
                return 29443;
            case 29552:
                return 29406;
            case 29554:
            case 31339:
                return 33719;
            case 29557:
                return 29454;
            case 29559:
                return 29367;
            case 29560:
                return 20861;
            case 29562:
                return 29549;
            case 29563:
                return 29486;
            case 29564:
                return 29461;
            case 29568:
                return 29473;
            case 29608:
                return 29647;
            case 29694:
                return 29616;
            case 29754:
                return 29648;
            case 29759:
                return 29682;
            case 29769:
                return 29641;
            case 29771:
                return 29614;
            case 29795:
                return 29712;
            case 29796:
                return 29814;
            case 29801:
                return 33721;
            case 29802:
                return 29595;
            case 29810:
                return 29617;
            case 29833:
                return 29711;
            case 29857:
                return 29710;
            case 29859:
                return 29585;
            case 29862:
                return 29815;
            case 29872:
                return 29615;
            case 29887:
                return 29831;
            case 29898:
                return 29756;
            case 29903:
                return 29649;
            case 29908:
                return 29838;
            case 29914:
                return 29906;
            case 29964:
                return 29935;
            case 29973:
                return 29934;
            case 29986:
            case 29987:
                return 20135;
            case 30045:
                return 20137;
            case 30050:
                return 27605;
            case 30059:
                return 30011;
            case 30064:
                return 24322;
            case 30087:
                return 30068;
            case 30090:
                return 21472;
            case 30169:
                return 30153;
            case 30210:
                return 30166;
            case 30219:
                return 30127;
            case 30221:
                return 30113;
            case 30227:
                return 30186;
            case 30238:
                return 30231;
            case 30241:
                return 30126;
            case 30247:
                return 30111;
            case 30254:
                return 30214;
            case 30266:
            case 30267:
                return 30232;
            case 30274:
                return 30103;
            case 30278:
                return 30184;
            case 30279:
                return 30187;
            case 30281:
                return 30213;
            case 30296:
                return 30112;
            case 30303:
                return 30250;
            case 30305:
                return 30196;
            case 30306:
                return 30162;
            case 30308:
                return 30102;
            case 30309:
                return 30151;
            case 30311:
            case 39681:
                return 30124;
            case 30313:
                return 30302;
            case 30316:
                return 30307;
            case 30317:
                return 30271;
            case 30318:
                return 30270;
            case 30320:
                return 30152;
            case 30321:
                return 30251;
            case 30322:
                return 30315;
            case 30332:
            case 39662:
                return 21457;
            case 30362:
                return 30353;
            case 30384:
                return 30129;
            case 30392:
                return 30386;
            case 30394:
                return 30385;
            case 30428:
                return 30423;
            case 30430:
                return 30415;
            case 30435:
                return 30417;
            case 30436:
                return 30424;
            case 30439:
                return 21346;
            case 30501:
                return 30502;
            case 30526:
            case 34886:
                return 20247;
            case 30543:
                return 22256;
            case 30556:
                return 30529;
            case 30558:
                return 30544;
            case 30616:
                return 30477;
            case 30620:
                return 16470;
            case 30622:
                return 30610;
            case 30637:
                return 20102;
            case 30652:
                return 30545;
            case 30682:
                return 30633;
            case 30703:
                return 30699;
            case 30787:
                return 26417;
            case 30820:
                return 30806;
            case 30824:
                return 30743;
            case 30831:
                return 30746;
            case 30889:
                return 30805;
            case 30893:
                return 30720;
            case 30904:
                return 30748;
            case 30906:
                return 30830;
            case 30908:
                return 30721;
            case 30929:
                return 30809;
            case 30938:
                return 30742;
            case 30947:
                return 30876;
            case 30951:
                return 30875;
            case 30959:
                return 30710;
            case 30973:
                return 30807;
            case 30980:
                return 30810;
            case 30990:
                return 30784;
            case 31001:
                return 30861;
            case 31014:
                return 30719;
            case 31018:
                return 30778;
            case 31019:
                return 30782;
            case 31020:
                return 30718;
            case 31025:
                return 30779;
            case 31063:
            case 34937:
            case 38587:
                return 21482;
            case 31103:
                return 31108;
            case 31117:
                return 31096;
            case 31118:
                return 31087;
            case 31125:
                return 31054;
            case 31142:
                return 24481;
            case 31146:
                return 31109;
            case 31150:
                return 31036;
            case 31152:
                return 31074;
            case 31153:
                return 31095;
            case 31167:
                return 31171;
            case 31176:
                return 31868;
            case 31237:
                return 31246;
            case 31240:
                return 31174;
            case 31263:
                return 31104;
            case 31278:
                return 31181;
            case 31281:
                return 31216;
            case 31303:
                return 16735;
            case 31308:
                return 31267;
            case 31309:
                return 31215;
            case 31310:
                return 39062;
            case 31329:
                return 31313;
            case 31330:
                return 31229;
            case 31337:
                return 31283;
            case 31341:
                return 31238;
            case 31401:
                return 31389;
            case 31402:
                return 27964;
            case 31406:
                return 31351;
            case 31407:
                return 31377;
            case 31413:
                return 31374;
            case 31414:
                return 31405;
            case 31418:
                return 31397;
            case 31428:
                return 31388;
            case 31429:
                return 31373;
            case 31431:
                return 31398;
            case 31432:
                return 28790;
            case 31434:
                return 31363;
            case 31466:
            case 35918:
                return 31446;
            case 31478:
                return 31454;
            case 31558:
                return 31508;
            case 31565:
                return 31499;
            case 31591:
                return 31509;
            case 31627:
                return 31546;
            case 31631:
                return 31581;
            case 31680:
                return 33410;
            case 31684:
                return 33539;
            case 31689:
                return 31569;
            case 31691:
                return 31655;
            case 31716:
                return 31491;
            case 31721:
                return 31579;
            case 31731:
                return 31578;
            case 31744:
                return 31654;
            case 31757:
                return 31699;
            case 31774:
                return 31658;
            case 31777:
                return 31616;
            case 31779:
                return 31697;
            case 31787:
                return 31659;
            case 31799:
                return 27280;
            case 31805:
            case 31844:
                return 31614;
            case 31806:
                return 24088;
            case 31811:
                return 31726;
            case 31820:
                return 31609;
            case 31833:
                return 31635;
            case 31836:
                return 31656;
            case 31839:
                return 31809;
            case 31840:
                return 31548;
            case 31849:
                return 31550;
            case 31850:
                return 31766;
            case 31852:
                return 31729;
            case 31854:
                return 31657;
            case 31858:
                return 21505;
            case 31925:
                return 31908;
            case 31965:
                return 31937;
            case 31966:
                return 31914;
            case 31975:
                return 31918;
            case 31986:
                return 31901;
            case 31988:
                return 31860;
            case 31990:
                return 31900;
            case 31998:
                return 32416;
            case 32000:
                return 32426;
            case 32002:
                return 32419;
            case 32004:
                return 32422;
            case 32005:
                return 32418;
            case 32006:
                return 32417;
            case 32007:
                return 32421;
            case 32008:
                return 32424;
            case 32009:
                return 32427;
            case 32011:
                return 32441;
            case 32013:
                return 32435;
            case 32016:
                return 32445;
            case 32019:
                return 32446;
            case 32020:
                return 32431;
            case 32021:
                return 32432;
            case 32022:
                return 32444;
            case 32023:
                return 32433;
            case 32025:
                return 32440;
            case 32026:
                return 32423;
            case 32027:
                return 32439;
            case 32028:
                return 32429;
            case 32029:
                return 32436;
            case 32033:
                return 32442;
            case 32044:
                return 17207;
            case 32046:
                return 25166;
            case 32048:
                return 32454;
            case 32049:
                return 32450;
            case 32050:
            case 32079:
                return 32449;
            case 32051:
                return 32453;
            case 32057:
                return 32461;
            case 32058:
                return 32448;
            case 32060:
                return 32459;
            case 32063:
                return 32464;
            case 32064:
                return 32460;
            case 32066:
                return 32456;
            case 32068:
                return 32452;
            case 32070:
                return 32458;
            case 32078:
                return 32471;
            case 32080:
                return 32467;
            case 32085:
            case 32118:
                return 32477;
            case 32091:
            case 32295:
                return 32486;
            case 32093:
                return 32468;
            case 32094:
                return 32478;
            case 32097:
                return 32476;
            case 32098:
                return 32474;
            case 32102:
                return 32473;
            case 32104:
                return 32466;
            case 32112:
                return 32470;
            case 32113:
                return 32479;
            case 32114:
                return 19997;
            case 32115:
                return 32475;
            case 32121:
                return 32482;
            case 32129:
                return 32465;
            case 32131:
                return 32481;
            case 32134:
                return 32480;
            case 32136:
                return 32488;
            case 32137:
            case 32353:
                return 32483;
            case 32143:
                return 32485;
            case 32147:
                return 32463;
            case 32156:
                return 32508;
            case 32158:
                return 32525;
            case 32160:
            case 32209:
                return 32511;
            case 32162:
                return 32504;
            case 32163:
                return 32507;
            case 32171:
            case 32218:
                return 32447;
            case 32172:
                return 32502;
            case 32173:
                return 32500;
            case 32176:
                return 32510;
            case 32177:
                return 32434;
            case 32178:
                return 32593;
            case 32179:
            case 32323:
                return 32503;
            case 32180:
                return 32512;
            case 32184:
                return 32438;
            case 32185:
                return 32506;
            case 32186:
                return 32494;
            case 32187:
                return 32509;
            case 32189:
                return 32496;
            case 32190:
                return 32491;
            case 32191:
                return 32501;
            case 32196:
                return 32498;
            case 32199:
                return 32513;
            case 32202:
                return 32039;
            case 32203:
                return 32495;
            case 32210:
                return 32490;
            case 32212:
                return 32497;
            case 32215:
                return 32515;
            case 32216:
                return 32516;
            case 32217:
                return 32514;
            case 32221:
                return 32521;
            case 32222:
                return 32526;
            case 32224:
                return 32532;
            case 32225:
                return 32535;
            case 32227:
                return 32536;
            case 32230:
                return 32524;
            case 32232:
                return 32534;
            case 32233:
                return 32531;
            case 32236:
                return 32517;
            case 32239:
                return 32428;
            case 32241:
                return 32529;
            case 32242:
                return 32520;
            case 32244:
                return 32451;
            case 32246:
                return 32527;
            case 32249:
                return 32519;
            case 32251:
                return 33268;
            case 32252:
                return 32522;
            case 32264:
                return 33830;
            case 32265:
                return 32537;
            case 32266:
                return 32546;
            case 32267:
                return 32530;
            case 32272:
                return 32457;
            case 32273:
                return 32547;
            case 32279:
                return 32542;
            case 32283:
                return 32538;
            case 32285:
                return 32540;
            case 32286:
                return 32543;
            case 32287:
                return 32539;
            case 32291:
                return 21439;
            case 32299:
                return 32541;
            case 32301:
                return 32545;
            case 32302:
                return 32553;
            case 32305:
                return 32437;
            case 32306:
                return 32551;
            case 32308:
            case 32406:
                return 32420;
            case 32309:
                return 32550;
            case 32310:
                return 32119;
            case 32311:
                return 32533;
            case 32313:
                return 32549;
            case 32317:
                return 24635;
            case 32318:
                return 32489;
            case 32325:
                return 32555;
            case 32326:
                return 32554;
            case 32328:
                return 35137;
            case 32338:
                return 32559;
            case 32340:
                return 32455;
            case 32341:
                return 32558;
            case 32346:
                return 32557;
            case 32350:
                return 32469;
            case 32354:
                return 32523;
            case 32361:
                return 32499;
            case 32362:
                return 32472;
            case 32365:
                return 33575;
            case 32366:
            case 38849:
                return 32560;
            case 32367:
                return 32563;
            case 32368:
                return 32562;
            case 32371:
                return 32564;
            case 32377:
                return 32462;
            case 32380:
                return 32487;
            case 32381:
                return 32548;
            case 32382:
                return 32561;
            case 32392:
                return 32556;
            case 32394:
                return 32425;
            case 32396:
                return 32493;
            case 32397:
                return 32047;
            case 32399:
                return 32544;
            case 32403:
                return 32552;
            case 32404:
                return 25165;
            case 32408:
                return 32565;
            case 32412:
                return 32518;
            case 32573:
            case 37474:
                return 38069;
            case 32588:
                return 32578;
            case 32624:
            case 32632:
                return 32602;
            case 32629:
            case 39393:
                return 39554;
            case 32631:
                return 32610;
            case 32645:
                return 32599;
            case 32646:
                return 32628;
            case 32648:
            case 35210:
                return 32641;
            case 32651:
                return 33416;
            case 32677:
                return 32671;
            case 32680:
                return 32673;
            case 32681:
                return 20041;
            case 32722:
                return 20064;
            case 32748:
                return 32730;
            case 32761:
                return 32728;
            case 32812:
                return 32807;
            case 32814:
                return 32802;
            case 32854:
                return 22307;
            case 32862:
                return 38395;
            case 32879:
                return 32852;
            case 32880:
                return 32874;
            case 32882:
                return 22768;
            case 32883:
                return 32824;
            case 32885:
                return 32873;
            case 32886:
                return 32834;
            case 32887:
                return 32844;
            case 32889:
                return 32845;
            case 32893:
                return 21548;
            case 32894:
                return 32843;
            case 32901:
                return 32899;
            case 33029:
                return 32961;
            case 33032:
                return 33033;
            case 33051:
                return 33003;
            case 33067:
                return 33073;
            case 33081:
                return 32960;
            case 33102:
                return 32958;
            case 33110:
                return 33000;
            case 33121:
                return 33078;
            case 33126:
                return 33041;
            case 33131:
                return 32959;
            case 33139:
                return 33050;
            case 33144:
                return 32928;
            case 33153:
                return 32951;
            case 33155:
                return 33149;
            case 33173:
                return 33112;
            case 33178:
                return 32932;
            case 33182:
                return 17373;
            case 33184:
                return 33014;
            case 33193:
                return 33147;
            case 33213:
                return 32966;
            case 33214:
                return 33037;
            case 33215:
                return 33043;
            case 33225:
                return 33080;
            case 33229:
                return 33040;
            case 33231:
                return 33169;
            case 33234:
                return 30319;
            case 33235:
            case 36115:
                return 36163;
            case 33240:
                return 33098;
            case 33242:
                return 33002;
            case 33248:
                return 33044;
            case 33250:
                return 33244;
            case 33253:
                return 21351;
            case 33256:
                return 20020;
            case 33287:
                return 19982;
            case 33288:
                return 20852;
            case 33289:
                return 20030;
            case 33290:
                return 26087;
            case 33369:
                return 33329;
            case 33380:
                return 33315;
            case 33382:
                return 33328;
            case 33387:
                return 33339;
            case 33393:
                return 33392;
            case 33398:
            case 35924:
                return 33395;
            case 33467:
                return 21005;
            case 33511:
                return 33486;
            case 33586:
                return 20857;
            case 33610:
                return 33606;
            case 33674:
                return 24196;
            case 33686:
                return 33550;
            case 33698:
                return 33626;
            case 33703:
                return 33483;
            case 33775:
                return 21326;
            case 33799:
                return 33484;
            case 33802:
                return 33713;
            case 33836:
                return 19975;
            case 33845:
                return 33716;
            case 33865:
                return 21494;
            case 33874:
                return 33645;
            case 33892:
                return 33646;
            case 33894:
                return 33479;
            case 33911:
                return 33636;
            case 33939:
            case 34036:
                return 33724;
            case 33940:
                return 33715;
            case 33950:
                return 33669;
            case 33980:
                return 33485;
            case 33984:
                return 33642;
            case 33995:
                return 30422;
            case 34023:
                return 33692;
            case 34030:
                return 33714;
            case 34031:
                return 33473;
            case 34045:
                return 33628;
            case 34068:
                return 21340;
            case 34078:
                return 33932;
            case 34083:
                return 33931;
            case 34085:
                return 33905;
            case 34086:
                return 33553;
            case 34093:
                return 33643;
            case 34113:
                return 33640;
            case 34118:
                return 33927;
            case 34126:
                return 33630;
            case 34130:
                return 33644;
            case 34131:
                return 33464;
            case 34133:
                return 33720;
            case 34136:
                return 33627;
            case 34146:
                return 33929;
            case 34153:
                return 33633;
            case 34154:
                return 33436;
            case 34157:
                return 33831;
            case 34167:
                return 34019;
            case 34184:
                return 33631;
            case 34186:
                return 34015;
            case 34188:
                return 33431;
            case 34193:
                return 23004;
            case 34196:
                return 34103;
            case 34207:
                return 33718;
            case 34214:
                return 33616;
            case 34217:
                return 33832;
            case 34234:
                return 33632;
            case 34249:
                return 20511;
            case 34253:
                return 34013;
            case 34254:
                return 33641;
            case 34277:
                return 33647;
            case 34282:
                return 34222;
            case 34285:
                return 17622;
            case 34292:
            case 34314:
                return 34164;
            case 34294:
                return 33480;
            case 34297:
                return 34108;
            case 34298:
                return 34106;
            case 34304:
                return 33818;
            case 34308:
                return 34162;
            case 34310:
                return 33446;
            case 34315:
                return 33529;
            case 34330:
                return 34259;
            case 34334:
                return 34105;
            case 34338:
                return 33551;
            case 34349:
                return 20848;
            case 34362:
                return 34016;
            case 34367:
                return 33821;
            case 34389:
                return 22788;
            case 34395:
                return 34394;
            case 34396:
                return 34383;
            case 34399:
                return 21495;
            case 34407:
                return 20111;
            case 34415:
                return 34412;
            case 34554:
                return 34545;
            case 34555:
                return 34581;
            case 34566:
                return 34476;
            case 34645:
                return 34432;
            case 34655:
                return 29484;
            case 34662:
                return 34430;
            case 34680:
                return 34583;
            case 34692:
                return 34547;
            case 34718:
                return 34434;
            case 34722:
                return 33828;
            case 34747:
                return 34684;
            case 34756:
                return 34544;
            case 34760:
                return 34632;
            case 34766:
                return 34728;
            case 34787:
                return 34414;
            case 34796:
                return 34633;
            case 34799:
                return 34546;
            case 34802:
                return 34411;
            case 34806:
                return 34511;
            case 34811:
                return 34433;
            case 34821:
                return 34631;
            case 34822:
                return 34431;
            case 34829:
                return 34638;
            case 34832:
                return 34548;
            case 34833:
                return 34686;
            case 34836:
                return 34461;
            case 34847:
                return 34593;
            case 34851:
                return 34510;
            case 34856:
                return 34767;
            case 34865:
                return 34506;
            case 34870:
                return 34453;
            case 34875:
                return 34542;
            case 34890:
                return 34065;
            case 34899:
                return 26415;
            case 34907:
                return 21355;
            case 34909:
                return 20914;
            case 34974:
                return 34926;
            case 35023:
            case 35041:
                return 37324;
            case 35036:
                return 34917;
            case 35037:
                return 35013;
            case 35069:
                return 21046;
            case 35122:
                return 35044;
            case 35123:
                return 35042;
            case 35128:
                return 35099;
            case 35131:
                return 20149;
            case 35142:
                return 24158;
            case 35143:
                return 35045;
            case 35158:
                return 34948;
            case 35165:
                return 35043;
            case 35168:
                return 35014;
            case 35172:
                return 35124;
            case 35178:
                return 34972;
            case 35183:
                return 34924;
            case 35186:
                return 34989;
            case 35211:
                return 35265;
            case 35214:
                return 35267;
            case 35215:
                return 35268;
            case 35219:
                return 35269;
            case 35222:
                return 35270;
            case 35224:
                return 35271;
            case 35233:
                return 35275;
            case 35238:
                return 35278;
            case 35239:
            case 35261:
                return 35272;
            case 35242:
                return 20146;
            case 35244:
                return 35274;
            case 35247:
                return 35279;
            case 35250:
                return 35280;
            case 35255:
                return 35281;
            case 35258:
                return 35273;
            case 35263:
                return 35276;
            case 35264:
                return 35266;
            case 35316:
                return 35294;
            case 35318:
                return 35311;
            case 35320:
                return 35302;
            case 35329:
                return 35744;
            case 35330:
                return 35746;
            case 35331:
                return 35747;
            case 35336:
                return 35745;
            case 35338:
                return 35759;
            case 35340:
                return 35751;
            case 35342:
                return 35752;
            case 35344:
                return 35750;
            case 35347:
                return 35757;
            case 35349:
                return 35754;
            case 35350:
                return 35755;
            case 35351:
                return 25176;
            case 35352:
                return 35760;
            case 35355:
                return 35769;
            case 35357:
                return 35766;
            case 35359:
                return 35772;
            case 35362:
                return 18211;
            case 35363:
                return 35776;
            case 35365:
                return 35767;
            case 35369:
                return 35771;
            case 35370:
                return 35775;
            case 35373:
                return 35774;
            case 35377:
                return 35768;
            case 35380:
                return 35785;
            case 35382:
                return 35779;
            case 35386:
                return 35786;
            case 35393:
                return 35778;
            case 35398:
                return 35787;
            case 35406:
                return 35765;
            case 35408:
                return 35784;
            case 35410:
                return 35794;
            case 35412:
                return 35791;
            case 35413:
                return 35780;
            case 35415:
                return 35783;
            case 35416:
                return 35790;
            case 35419:
                return 35781;
            case 35422:
                return 35789;
            case 35424:
                return 21647;
            case 35425:
                return 35817;
            case 35426:
                return 35810;
            case 35427:
                return 35811;
            case 35430:
                return 35797;
            case 35433:
                return 35799;
            case 35435:
                return 35815;
            case 35436:
                return 35807;
            case 35437:
                return 35809;
            case 35438:
                return 35808;
            case 35440:
                return 35800;
            case 35441:
                return 35805;
            case 35442:
                return 35813;
            case 35443:
                return 35814;
            case 35445:
                return 35804;
            case 35452:
                return 35801;
            case 35455:
                return 35798;
            case 35460:
                return 35796;
            case 35461:
                return 35803;
            case 35462:
                return 35795;
            case 35463:
                return 22840;
            case 35469:
                return 35748;
            case 35473:
                return 35827;
            case 35474:
                return 35830;
            case 35477:
                return 35806;
            case 35480:
                return 35825;
            case 35482:
                return 35822;
            case 35486:
                return 35821;
            case 35488:
                return 35802;
            case 35489:
                return 35819;
            case 35491:
                return 35820;
            case 35492:
                return 35823;
            case 35493:
                return 35824;
            case 35494:
                return 35829;
            case 35496:
                return 35826;
            case 35498:
            case 35500:
                return 35828;
            case 35504:
                return 35841;
            case 35506:
                return 35838;
            case 35510:
                return 35847;
            case 35513:
                return 35837;
            case 35516:
                return 35850;
            case 35519:
                return 35843;
            case 35522:
                return 35844;
            case 35524:
                return 35846;
            case 35527:
                return 35848;
            case 35529:
                return 35839;
            case 35531:
                return 35831;
            case 35533:
                return 35812;
            case 35535:
                return 35833;
            case 35537:
                return 35836;
            case 35538:
                return 35845;
            case 35542:
                return 35770;
            case 35543:
                return 35842;
            case 35547:
                return 35840;
            case 35548:
                return 35853;
            case 35549:
                return 35870;
            case 35550:
                return 35869;
            case 35553:
            case 35610:
                return 35877;
            case 35554:
                return 35816;
            case 35556:
                return 35860;
            case 35558:
                return 35867;
            case 35559:
                return 35856;
            case 35563:
                return 35855;
            case 35565:
                return 35861;
            case 35566:
                return 35864;
            case 35569:
                return 35763;
            case 35571:
                return 35865;
            case 35574:
                return 35852;
            case 35575:
                return 35773;
            case 35576:
                return 35832;
            case 35578:
                return 35866;
            case 35580:
                return 35862;
            case 35582:
                return 35834;
            case 35584:
                return 35851;
            case 35585:
                return 35858;
            case 35586:
                return 35859;
            case 35588:
                return 35466;
            case 35589:
                return 35788;
            case 35594:
                return 35854;
            case 35598:
                return 35868;
            case 35600:
                return 35879;
            case 35604:
                return 35857;
            case 35606:
                return 35873;
            case 35607:
                return 35876;
            case 35609:
                return 35878;
            case 35611:
                return 35762;
            case 35613:
                return 35874;
            case 35616:
            case 35617:
                return 35875;
            case 35624:
                return 35871;
            case 35627:
                return 35882;
            case 35628:
                return 35884;
            case 35635:
                return 35764;
            case 35641:
                return 35880;
            case 35646:
                return 35881;
            case 35657:
                return 35777;
            case 35662:
                return 35890;
            case 35663:
                return 35749;
            case 35670:
                return 35886;
            case 35672:
                return 35782;
            case 35673:
                return 35887;
            case 35674:
                return 35885;
            case 35676:
                return 35889;
            case 35691:
                return 35893;
            case 35695:
                return 35793;
            case 35696:
                return 35758;
            case 35700:
                return 35892;
            case 35703:
                return 25252;
            case 35709:
                return 35465;
            case 35710:
                return 35883;
            case 35712:
                return 35835;
            case 35717:
                return 35849;
            case 35722:
                return 21464;
            case 35723:
                return 35423;
            case 35724:
                return 18217;
            case 35726:
                return 38624;
            case 35730:
                return 35863;
            case 35731:
                return 35753;
            case 35733:
                return 35888;
            case 35734:
                return 35894;
            case 35740:
                return 35872;
            case 35742:
                return 35891;
            case 35912:
                return 23682;
            case 35920:
                return 20016;
            case 35948:
                return 29482;
            case 35958:
                return 35950;
            case 35987:
                return 29483;
            case 35997:
                return 36125;
            case 35998:
                return 36126;
            case 36000:
                return 36127;
            case 36001:
                return 36130;
            case 36002:
                return 36129;
            case 36007:
                return 36139;
            case 36008:
                return 36135;
            case 36009:
                return 36137;
            case 36010:
                return 36138;
            case 36011:
                return 36143;
            case 36012:
                return 36131;
            case 36013:
            case 36074:
                return 36136;
            case 36015:
                return 36142;
            case 36016:
                return 36147;
            case 36018:
                return 36160;
            case 36019:
                return 36144;
            case 36020:
                return 36149;
            case 36022:
                return 36140;
            case 36023:
                return 20080;
            case 36024:
                return 36151;
            case 36026:
                return 36150;
            case 36027:
                return 36153;
            case 36028:
                return 36148;
            case 36029:
                return 36155;
            case 36031:
                return 36152;
            case 36032:
                return 36154;
            case 36033:
                return 36146;
            case 36034:
                return 36162;
            case 36035:
                return 36161;
            case 36036:
                return 36159;
            case 36037:
                return 36165;
            case 36039:
                return 36164;
            case 36040:
                return 36158;
            case 36042:
                return 36156;
            case 36049:
                return 36168;
            case 36050:
                return 36170;
            case 36051:
                return 23486;
            case 36053:
                return 36167;
            case 36057:
                return 36178;
            case 36058:
                return 36169;
            case 36060:
                return 36176;
            case 36062:
                return 36175;
            case 36064:
                return 36180;
            case 36065:
                return 36179;
            case 36066:
                return 36132;
            case 36067:
                return 21334;
            case 36068:
                return 36145;
            case 36070:
                return 36171;
            case 36071:
                return 36181;
            case 36075:
            case 40782:
                return 36173;
            case 36076:
                return 36134;
            case 36077:
                return 36172;
            case 36084:
                return 36182;
            case 36085:
                return 36183;
            case 36090:
                return 36186;
            case 36091:
                return 36185;
            case 36092:
                return 36141;
            case 36093:
                return 36187;
            case 36094:
                return 36188;
            case 36100:
                return 36157;
            case 36101:
                return 36184;
            case 36104:
                return 36192;
            case 36106:
                return 36190;
            case 36107:
            case 36119:
                return 36189;
            case 36109:
                return 36193;
            case 36111:
                return 36194;
            case 36112:
                return 36166;
            case 36118:
                return 36174;
            case 36123:
                return 36195;
            case 36124:
            case 39634:
                return 33039;
            case 36204:
                return 36202;
            case 36245:
                return 36214;
            case 36249:
                return 36213;
            case 36264:
                return 36235;
            case 36274:
                return 36273;
            case 36321:
                return 36857;
            case 36368:
                return 36341;
            case 36404:
                return 36362;
            case 36428:
                return 36292;
            case 36437:
                return 36344;
            case 36448:
                return 36310;
            case 36451:
                return 36434;
            case 36452:
                return 36394;
            case 36474:
                return 36343;
            case 36489:
                return 36280;
            case 36490:
                return 36364;
            case 36491:
                return 36347;
            case 36493:
                return 36291;
            case 36497:
                return 36399;
            case 36498:
                return 36318;
            case 36499:
                return 36396;
            case 36501:
                return 36464;
            case 36506:
                return 36345;
            case 36513:
                return 36433;
            case 36517:
                return 36479;
            case 36518:
                return 36508;
            case 36522:
                return 36495;
            case 36544:
                return 36527;
            case 36554:
                return 36710;
            case 36555:
                return 36711;
            case 36556:
                return 36712;
            case 36557:
                return 20891;
            case 36562:
                return 36713;
            case 36564:
                return 36715;
            case 36571:
                return 36717;
            case 36575:
                return 36719;
            case 36580:
                return 36727;
            case 36587:
                return 36728;
            case 36594:
                return 36721;
            case 36600:
                return 36724;
            case 36601:
                return 36725;
            case 36602:
                return 36730;
            case 36603:
                return 36722;
            case 36604:
                return 36726;
            case 36606:
                return 36732;
            case 36611:
                return 36739;
            case 36613:
                return 36738;
            case 36615:
                return 36737;
            case 36617:
                return 36733;
            case 36618:
                return 36734;
            case 36626:
                return 36740;
            case 36628:
                return 36741;
            case 36629:
                return 36731;
            case 36635:
                return 36742;
            case 36636:
                return 36750;
            case 36637:
                return 36745;
            case 36638:
                return 36747;
            case 36639:
                return 36749;
            case 36645:
                return 36746;
            case 36646:
                return 36743;
            case 36649:
                return 36744;
            case 36650:
                return 36718;
            case 36655:
                return 36753;
            case 36659:
                return 36751;
            case 36664:
                return 36755;
            case 36667:
                return 36752;
            case 36670:
                return 36759;
            case 36671:
                return 33286;
            case 36674:
                return 27586;
            case 36676:
                return 36758;
            case 36677:
                return 36757;
            case 36678:
                return 36760;
            case 36681:
                return 36716;
            case 36685:
                return 36761;
            case 36686:
                return 36735;
            case 36692:
                return 36762;
            case 36703:
                return 36720;
            case 36705:
                return 36756;
            case 36706:
                return 36729;
            case 36708:
                return 36723;
            case 36774:
                return 21150;
            case 36781:
                return 36766;
            case 36782:
                return 36779;
            case 36783:
                return 36777;
            case 36786:
                return 20892;
            case 36852:
                return 22238;
            case 36885:
                return 36851;
            case 36889:
                return 36825;
            case 36899:
                return 36830;
            case 36914:
                return 36827;
            case 36939:
                return 36816;
            case 36942:
                return 36807;
            case 36948:
                return 36798;
            case 36949:
                return 36829;
            case 36953:
                return 36965;
            case 36956:
                return 36874;
            case 36957:
                return 27795;
            case 36958:
                return 36882;
            case 36960:
                return 36828;
            case 36969:
                return 36866;
            case 36978:
                return 36831;
            case 36983:
                return 36801;
            case 36984:
                return 36873;
            case 36986:
                return 36951;
            case 36988:
                return 36797;
            case 36993:
                return 36808;
            case 36996:
                return 36824;
            case 36999:
                return 36841;
            case 37002:
                return 36793;
            case 37007:
                return 36923;
            case 37008:
                return 36902;
            case 37087:
                return 37071;
            case 37092:
                return 37060;
            case 37109:
                return 37038;
            case 37126:
                return 37075;
            case 37129:
                return 20065;
            case 37138:
                return 37049;
            case 37140:
                return 37036;
            case 37142:
                return 37095;
            case 37159:
                return 37011;
            case 37165:
                return 37073;
            case 37168:
                return 37051;
            case 37170:
                return 37112;
            case 37172:
                return 37050;
            case 37174:
                return 37072;
            case 37178:
                return 37021;
            case 37192:
                return 37094;
            case 37251:
                return 33100;
            case 37270:
            case 37278:
                return 37213;
            case 37276:
                return 19985;
            case 37284:
            case 37292:
                return 37233;
            case 37291:
                return 21307;
            case 37295:
                return 37232;
            case 37312:
                return 37247;
            case 37313:
                return 34885;
            case 37315:
                return 37246;
            case 37317:
                return 37245;
            case 37323:
                return 37322;
            case 37331:
                return 38022;
            case 37332:
                return 38023;
            case 37333:
                return 38028;
            case 37335:
                return 38026;
            case 37336:
                return 38025;
            case 37337:
                return 38027;
            case 37341:
                return 38024;
            case 37347:
                return 38035;
            case 37348:
                return 38032;
            case 37351:
                return 38031;
            case 37353:
                return 38034;
            case 37365:
                return 38039;
            case 37367:
                return 38029;
            case 37369:
                return 38037;
            case 37370:
                return 38030;
            case 37374:
                return 18810;
            case 37376:
                return 38063;
            case 37377:
                return 38059;
            case 37379:
                return 38040;
            case 37380:
                return 38061;
            case 37384:
                return 38042;
            case 37385:
                return 38048;
            case 37389:
                return 38045;
            case 37390:
            case 37476:
                return 38057;
            case 37392:
                return 38052;
            case 37393:
                return 38051;
            case 37396:
                return 38046;
            case 37397:
                return 38062;
            case 37406:
                return 38055;
            case 37411:
                return 38041;
            case 37413:
                return 38060;
            case 37414:
                return 38043;
            case 37415:
                return 38058;
            case 37422:
                return 38092;
            case 37424:
                return 38088;
            case 37427:
                return 38070;
            case 37428:
                return 38083;
            case 37431:
                return 38068;
            case 37432:
                return 38073;
            case 37433:
                return 38093;
            case 37434:
                return 38064;
            case 37437:
                return 38072;
            case 37438:
                return 38080;
            case 37439:
                return 38079;
            case 37440:
                return 38078;
            case 37448:
                return 38090;
            case 37449:
                return 38089;
            case 37453:
                return 38091;
            case 37457:
                return 38082;
            case 37461:
                return 38071;
            case 37463:
                return 38067;
            case 37466:
                return 38086;
            case 37467:
                return 38085;
            case 37470:
                return 38074;
            case 37478:
                return 38066;
            case 37484:
                return 38076;
            case 37485:
                return 38077;
            case 37490:
            case 37926:
                return 38158;
            case 37494:
                return 38095;
            case 37496:
                return 38128;
            case 37498:
                return 38098;
            case 37499:
                return 38124;
            case 37503:
                return 38122;
            case 37504:
                return 38134;
            case 37507:
                return 38131;
            case 37509:
                return 38108;
            case 37521:
                return 38115;
            case 37523:
                return 38120;
            case 37526:
                return 38114;
            case 37528:
                return 38125;
            case 37530:
                return 38123;
            case 37532:
                return 34900;
            case 37536:
                return 38097;
            case 37539:
                return 38135;
            case 37541:
                return 38129;
            case 37542:
                return 38111;
            case 37544:
                return 38133;
            case 37545:
                return 38117;
            case 37546:
                return 38101;
            case 37547:
                return 38127;
            case 37548:
                return 38096;
            case 37553:
                return 38110;
            case 37555:
            case 37613:
                return 38160;
            case 37559:
                return 38144;
            case 37561:
            case 37885:
                return 38152;
            case 37563:
                return 38161;
            case 37564:
                return 38153;
            case 37569:
                return 38109;
            case 37570:
            case 37767:
                return 38213;
            case 37571:
                return 38162;
            case 37573:
                return 38156;
            case 37575:
                return 38049;
            case 37580:
                return 38116;
            case 37583:
                return 38103;
            case 37586:
                return 38155;
            case 37597:
                return 38154;
            case 37599:
                return 38163;
            case 37603:
                return 38104;
            case 37604:
                return 38148;
            case 37605:
                return 38147;
            case 37606:
                return 38164;
            case 37608:
                return 38151;
            case 37609:
                return 38099;
            case 37610:
                return 38138;
            case 37614:
                return 38102;
            case 37615:
                return 38150;
            case 37616:
                return 38146;
            case 37617:
                return 38141;
            case 37622:
                return 38157;
            case 37624:
                return 38191;
            case 37628:
                return 38050;
            case 37633:
                return 38174;
            case 37636:
            case 37682:
                return 24405;
            case 37638:
                return 38166;
            case 37639:
                return 38187;
            case 37640:
                return 38185;
            case 37648:
                return 38181;
            case 37650:
                return 38165;
            case 37653:
                return 38175;
            case 37656:
                return 38180;
            case 37657:
                return 38193;
            case 37658:
                return 38126;
            case 37659:
                return 38171;
            case 37663:
                return 38188;
            case 37664:
                return 38189;
            case 37666:
                return 38065;
            case 37670:
                return 38182;
            case 37672:
                return 38170;
            case 37675:
                return 38177;
            case 37678:
                return 38178;
            case 37679:
                return 38169;
            case 37683:
                return 38192;
            case 37686:
                return 34920;
            case 37688:
                return 38140;
            case 37692:
            case 37823:
                return 38222;
            case 37696:
                return 38173;
            case 37697:
                return 38184;
            case 37699:
                return 38186;
            case 37702:
                return 38036;
            case 37703:
                return 38196;
            case 37707:
                return 38149;
            case 37709:
                return 38208;
            case 37716:
                return 38199;
            case 37720:
                return 38113;
            case 37722:
                return 38038;
            case 37723:
                return 38203;
            case 37732:
                return 38200;
            case 37733:
                return 38194;
            case 37737:
                return 38168;
            case 37740:
                return 38201;
            case 37744:
                return 38206;
            case 37749:
                return 38190;
            case 37750:
                return 38198;
            case 37754:
                return 38167;
            case 37758:
                return 38202;
            case 37762:
                return 38209;
            case 37764:
                return 38207;
            case 37770:
                return 38225;
            case 37782:
                return 38145;
            case 37784:
                return 38217;
            case 37793:
                return 38211;
            case 37794:
                return 38056;
            case 37795:
                return 34021;
            case 37798:
                return 38223;
            case 37799:
                return 38112;
            case 37801:
                return 38121;
            case 37802:
                return 38204;
            case 37804:
                return 38224;
            case 37806:
                return 38215;
            case 37808:
                return 38226;
            case 37811:
                return 38221;
            case 37813:
                return 38227;
            case 37816:
            case 37931:
                return 38220;
            case 37827:
                return 38238;
            case 37831:
                return 38239;
            case 37832:
                return 38142;
            case 37836:
                return 38214;
            case 37837:
                return 38233;
            case 37841:
                return 38237;
            case 37847:
                return 38143;
            case 37848:
                return 38197;
            case 37852:
                return 38231;
            case 37853:
                return 38232;
            case 37854:
                return 38235;
            case 37855:
                return 38130;
            case 37857:
                return 38236;
            case 37858:
                return 38230;
            case 37860:
                return 38210;
            case 37864:
                return 37694;
            case 37872:
                return 38234;
            case 37877:
                return 38119;
            case 37879:
                return 38244;
            case 37881:
                return 38250;
            case 37882:
                return 18813;
            case 37891:
                return 38105;
            case 37899:
                return 38132;
            case 37904:
                return 38243;
            case 37906:
                return 38137;
            case 37907:
                return 38246;
            case 37908:
                return 38241;
            case 37912:
                return 38047;
            case 37913:
                return 38251;
            case 37917:
                return 38242;
            case 37920:
                return 38248;
            case 37925:
                return 18821;
            case 37927:
                return 38159;
            case 37928:
                return 38212;
            case 37934:
                return 38256;
            case 37935:
                return 18819;
            case 37938:
                return 38255;
            case 37939:
                return 38253;
            case 37941:
                return 38081;
            case 37942:
                return 38254;
            case 37944:
                return 38094;
            case 37946:
                return 38107;
            case 37951:
                return 38257;
            case 37956:
                return 38136;
            case 37962:
                return 38252;
            case 37964:
                return 38228;
            case 37970:
                return 37492;
            case 37972:
                return 38258;
            case 37973:
                return 38183;
            case 37982:
                return 38260;
            case 37984:
                return 38084;
            case 37987:
                return 38259;
            case 37989:
                return 38245;
            case 37997:
                return 38247;
            case 38000:
                return 38053;
            case 38002:
                return 38262;
            case 38007:
                return 38218;
            case 38009:
                return 38249;
            case 38012:
                return 38179;
            case 38013:
                return 38075;
            case 38014:
                return 37550;
            case 38015:
                return 20991;
            case 38017:
                return 18822;
            case 38018:
                return 38219;
            case 38239:
                return 30905;
            case 38263:
                return 38271;
            case 38272:
                return 38376;
            case 38274:
                return 38377;
            case 38275:
                return 38378;
            case 38278:
                return 38379;
            case 38281:
                return 38381;
            case 38283:
                return 24320;
            case 38284:
                return 38390;
            case 38286:
                return 38387;
            case 38287:
                return 38384;
            case 38289:
                return 38386;
            case 38291:
                return 38388;
            case 38292:
                return 38389;
            case 38296:
                return 38392;
            case 38297:
            case 39719:
                return 38393;
            case 38305:
                return 38402;
            case 38307:
                return 38401;
            case 38308:
                return 21512;
            case 38309:
                return 38400;
            case 38312:
                return 38394;
            case 38313:
                return 38397;
            case 38315:
                return 38403;
            case 38316:
                return 38406;
            case 38317:
                return 38398;
            case 38321:
            case 38322:
                return 38405;
            case 38326:
                return 38410;
            case 38329:
                return 38409;
            case 38331:
                return 38414;
            case 38332:
                return 38415;
            case 38333:
                return 38413;
            case 38334:
                return 38408;
            case 38335:
                return 38412;
            case 38339:
                return 38418;
            case 38342:
                return 26495;
            case 38344:
                return 38385;
            case 38346:
                return 38420;
            case 38347:
                return 38421;
            case 38348:
                return 38417;
            case 38352:
                return 38423;
            case 38355:
                return 38399;
            case 38356:
                return 38422;
            case 38357:
                return 38425;
            case 38358:
                return 38383;
            case 38364:
                return 20851;
            case 38366:
                return 38426;
            case 38369:
                return 38416;
            case 38370:
                return 36767;
            case 38373:
                return 38396;
            case 38442:
                return 22338;
            case 38488:
                return 38473;
            case 38493:
                return 38485;
            case 38499:
                return 38453;
            case 38512:
                return 38452;
            case 38515:
                return 38472;
            case 38520:
                return 38470;
            case 38525:
                return 38451;
            case 38537:
                return 38503;
            case 38538:
                return 38431;
            case 38542:
                return 38454;
            case 38549:
                return 38504;
            case 38555:
                return 38469;
            case 38568:
                return 38543;
            case 38570:
                return 38505;
            case 38577:
                return 38544;
            case 38580:
                return 38471;
            case 38583:
            case 38584:
                return 38582;
            case 38603:
                return 38589;
            case 38614:
                return 34429;
            case 38617:
                return 21452;
            case 38619:
                return 38607;
            case 38620:
                return 26434;
            case 38622:
            case 40388:
                return 40481;
            case 38626:
                return 31163;
            case 38627:
                return 38590;
            case 38642:
                return 20113;
            case 38651:
                return 30005;
            case 38695:
                return 38654;
            case 38717:
                return 38657;
            case 38722:
                return 38643;
            case 38724:
                return 38701;
            case 38726:
                return 21447;
            case 38728:
                return 28789;
            case 38729:
                return 21446;
            case 38746:
                return 38739;
            case 38748:
                return 38745;
            case 38758:
                return 33148;
            case 38760:
                return 38757;
            case 38799:
                return 24041;
            case 38822:
                return 31179;
            case 38845:
                return 38802;
            case 38851:
                return 38801;
            case 38854:
                return 21315;
            case 38857:
                return 38831;
            case 38859:
                return 38886;
            case 38860:
                return 38887;
            case 38861:
                return 38888;
            case 38867:
                return 38889;
            case 38873:
                return 38890;
            case 38876:
                return 38892;
            case 38878:
                return 38891;
            case 38907:
                return 38901;
            case 38911:
                return 21709;
            case 38913:
                return 39029;
            case 38914:
                return 39030;
            case 38915:
                return 39031;
            case 38917:
                return 39033;
            case 38918:
                return 39034;
            case 38919:
                return 39032;
            case 38920:
            case 39706:
                return 39035;
            case 38922:
                return 39036;
            case 38924:
                return 39042;
            case 38926:
                return 39040;
            case 38927:
                return 39043;
            case 38928:
                return 39044;
            case 38929:
                return 39037;
            case 38930:
                return 39041;
            case 38931:
                return 39039;
            case 38935:
                return 39047;
            case 38936:
                return 39046;
            case 38940:
                return 39052;
            case 38945:
                return 39049;
            case 38948:
                return 39056;
            case 38950:
                return 39055;
            case 38957:
                return 22836;
            case 38960:
                return 39050;
            case 38962:
                return 39051;
            case 38967:
                return 39060;
            case 38968:
                return 39048;
            case 38969:
            case 38973:
                return 39059;
            case 38971:
                return 39057;
            case 38982:
                return 39063;
            case 38988:
                return 39064;
            case 38989:
                return 39069;
            case 38990:
                return 39066;
            case 38991:
            case 38996:
                return 39068;
            case 38994:
                return 39065;
            case 38995:
                return 39067;
            case 39000:
                return 24895;
            case 39001:
                return 39073;
            case 39003:
                return 39072;
            case 39006:
                return 31867;
            case 39010:
                return 39071;
            case 39013:
                return 39074;
            case 39015:
                return 39038;
            case 39019:
                return 39076;
            case 39020:
                return 39077;
            case 39023:
                return 26174;
            case 39024:
                return 39078;
            case 39025:
                return 39045;
            case 39027:
                return 39070;
            case 39028:
                return 39079;
            case 39080:
                return 39118;
            case 39086:
                return 39121;
            case 39087:
                return 39122;
            case 39091:
                return 21038;
            case 39094:
                return 39123;
            case 39096:
                return 39124;
            case 39100:
                return 39125;
            case 39104:
                return 39127;
            case 39108:
                return 39128;
            case 39110:
                return 39129;
            case 39112:
                return 39130;
            case 39131:
                return 39134;
            case 39141:
                return 39270;
            case 39145:
                return 39272;
            case 39146:
                return 39274;
            case 39147:
                return 39275;
            case 39149:
                return 39276;
            case 39151:
                return 39277;
            case 39154:
                return 39278;
            case 39156:
                return 39284;
            case 39164:
                return 39282;
            case 39165:
                return 39281;
            case 39166:
                return 39280;
            case 39167:
                return 39283;
            case 39171:
                return 39290;
            case 39172:
                return 39288;
            case 39173:
            case 39200:
                return 39292;
            case 39177:
                return 39287;
            case 39178:
                return 20859;
            case 39180:
                return 39285;
            case 39182:
                return 39289;
            case 39183:
                return 39291;
            case 39185:
                return 39293;
            case 39186:
                return 39297;
            case 39187:
                return 39295;
            case 39192:
                return 20313;
            case 39194:
                return 32948;
            case 39195:
                return 39300;
            case 39196:
                return 39299;
            case 39198:
                return 39279;
            case 39201:
                return 39301;
            case 39208:
                return 39302;
            case 39217:
                return 31943;
            case 39219:
                return 39271;
            case 39222:
                return 39305;
            case 39223:
                return 39303;
            case 39226:
                return 39310;
            case 39228:
                return 39273;
            case 39230:
                return 39311;
            case 39231:
                return 39306;
            case 39235:
                return 39309;
            case 39237:
                return 39314;
            case 39240:
                return 39312;
            case 39241:
                return 39313;
            case 39242:
                return 39315;
            case 39243:
                return 39304;
            case 39244:
                return 39316;
            case 39249:
                return 39269;
            case 39250:
                return 39286;
            case 39255:
                return 39144;
            case 39260:
                return 39181;
            case 39262:
                return 39307;
            case 39263:
            case 39266:
                return 39317;
            case 39340:
                return 39532;
            case 39341:
                return 39533;
            case 39342:
                return 20911;
            case 39345:
                return 39534;
            case 39347:
                return 39536;
            case 39348:
                return 39535;
            case 39361:
                return 39539;
            case 39376:
                return 39547;
            case 39377:
                return 39549;
            case 39378:
                return 39545;
            case 39380:
                return 39541;
            case 39381:
                return 39550;
            case 39384:
                return 39552;
            case 39385:
                return 39544;
            case 39387:
                return 39542;
            case 39389:
                return 39548;
            case 39391:
                return 39543;
            case 39394:
                return 39560;
            case 39405:
                return 39559;
            case 39409:
                return 39558;
            case 39416:
                return 39566;
            case 39423:
                return 39567;
            case 39425:
                return 39563;
            case 39429:
                return 39571;
            case 39437:
                return 39570;
            case 39438:
                return 39569;
            case 39439:
                return 39568;
            case 39446:
                return 39579;
            case 39449:
                return 39575;
            case 39467:
                return 39582;
            case 39469:
                return 39576;
            case 39470:
                return 39581;
            case 39472:
                return 33150;
            case 39478:
                return 39546;
            case 39479:
                return 39578;
            case 39480:
                return 39583;
            case 39486:
                return 39585;
            case 39488:
                return 34022;
            case 39489:
                return 39580;
            case 39490:
                return 39574;
            case 39491:
                return 39584;
            case 39492:
                return 39586;
            case 39493:
                return 39537;
            case 39498:
                return 39557;
            case 39501:
                return 39553;
            case 39503:
                return 39587;
            case 39509:
                return 39556;
            case 39511:
                return 39564;
            case 39514:
                return 24778;
            case 39515:
                return 39551;
            case 39519:
                return 39588;
            case 39522:
                return 39540;
            case 39524:
                return 39591;
            case 39525:
                return 39589;
            case 39530:
                return 39562;
            case 39599:
                return 32942;
            case 39631:
                return 39621;
            case 39636:
                return 20307;
            case 39637:
                return 39628;
            case 39638:
                return 39627;
            case 39686:
                return 26494;
            case 39693:
                return 32993;
            case 39714:
                return 39699;
            case 39717:
                return 26007;
            case 39721:
                return 38411;
            case 39726:
                return 38404;
            case 39729:
                return 37057;
            case 39758:
                return 39753;
            case 39768:
                return 39751;
            case 39770:
                return 40060;
            case 39771:
                return 40061;
            case 39791:
                return 40065;
            case 39796:
                return 40066;
            case 39799:
                return 40063;
            case 39809:
                return 40069;
            case 39811:
                return 40070;
            case 39821:
                return 40079;
            case 39824:
                return 40080;
            case 39825:
                return 40077;
            case 39826:
                return 40075;
            case 39827:
                return 40074;
            case 39834:
                return 40082;
            case 39838:
                return 40085;
            case 39843:
                return 19615;
            case 39846:
                return 40086;
            case 39850:
                return 40084;
            case 39851:
                return 40091;
            case 39853:
                return 40081;
            case 39854:
                return 40092;
            case 39866:
                return 40093;
            case 39872:
                return 40103;
            case 39873:
                return 40096;
            case 39879:
                return 40105;
            case 39881:
                return 40100;
            case 39882:
                return 40104;
            case 39892:
                return 40123;
            case 39894:
                return 40109;
            case 39895:
                return 40094;
            case 39899:
                return 40119;
            case 39901:
                return 40116;
            case 39905:
                return 40113;
            case 39906:
                return 40117;
            case 39908:
                return 40114;
            case 39911:
                return 40115;
            case 39912:
                return 40120;
            case 39914:
                return 40110;
            case 39915:
                return 40112;
            case 39920:
                return 40071;
            case 39924:
                return 40122;
            case 39933:
                return 40107;
            case 39935:
                return 40138;
            case 39938:
                return 40087;
            case 39942:
                return 19616;
            case 39944:
                return 40125;
            case 39945:
                return 40135;
            case 39948:
                return 19617;
            case 39949:
                return 40133;
            case 39952:
            case 40055:
                return 40132;
            case 39954:
                return 40134;
            case 39955:
                return 40131;
            case 39963:
                return 40129;
            case 39964:
                return 40146;
            case 39967:
                return 40145;
            case 39968:
                return 40139;
            case 39971:
                return 40101;
            case 39973:
                return 40143;
            case 39975:
                return 19618;
            case 39976:
                return 40142;
            case 39977:
                return 40144;
            case 39981:
                return 40141;
            case 39985:
                return 40098;
            case 39986:
            case 40711:
                return 40140;
            case 39987:
                return 40147;
            case 39989:
                return 40152;
            case 39991:
                return 40102;
            case 39993:
                return 40099;
            case 39995:
                return 40151;
            case 39996:
                return 40155;
            case 39998:
                return 40148;
            case 40005:
                return 40153;
            case 40008:
                return 40149;
            case 40009:
                return 40150;
            case 40018:
                return 40159;
            case 40020:
                return 40157;
            case 40022:
                return 40156;
            case 40023:
                return 40158;
            case 40024:
                return 40095;
            case 40029:
                return 40124;
            case 40031:
                return 40078;
            case 40032:
                return 40089;
            case 40035:
                return 40163;
            case 40039:
                return 40162;
            case 40040:
                return 40127;
            case 40045:
                return 40090;
            case 40056:
                return 40072;
            case 40058:
                return 40097;
            case 40165:
                return 40479;
            case 40167:
            case 40172:
                return 20971;
            case 40169:
                return 40480;
            case 40179:
                return 20964;
            case 40180:
                return 40483;
            case 40182:
                return 40482;
            case 40190:
                return 19731;
            case 40198:
                return 40489;
            case 40199:
                return 40488;
            case 40201:
                return 40486;
            case 40210:
                return 40496;
            case 40213:
                return 40501;
            case 40219:
                return 40499;
            case 40221:
                return 40498;
            case 40222:
                return 40494;
            case 40223:
                return 40497;
            case 40227:
                return 40490;
            case 40230:
                return 40495;
            case 40232:
                return 40493;
            case 40239:
                return 40504;
            case 40240:
                return 40505;
            case 40244:
                return 40507;
            case 40247:
                return 19733;
            case 40251:
                return 40511;
            case 40255:
                return 40509;
            case 40257:
                return 19732;
            case 40258:
                return 40506;
            case 40259:
                return 40508;
            case 40265:
            case 40478:
                return 40510;
            case 40273:
                return 40515;
            case 40274:
                return 40518;
            case 40275:
                return 40513;
            case 40284:
                return 40520;
            case 40285:
                return 40517;
            case 40288:
                return 40516;
            case 40289:
                return 40521;
            case 40298:
                return 40524;
            case 40300:
                return 40527;
            case 40302:
                return 40528;
            case 40303:
                return 40526;
            case 40306:
                return 40522;
            case 40324:
                return 19734;
            case 40327:
                return 40491;
            case 40329:
                return 40529;
            case 40330:
                return 40530;
            case 40339:
                return 40523;
            case 40342:
                return 40537;
            case 40344:
                return 40533;
            case 40346:
                return 40535;
            case 40353:
                return 40534;
            case 40357:
                return 40539;
            case 40361:
                return 40540;
            case 40362:
                return 19735;
            case 40364:
                return 40487;
            case 40367:
                return 33722;
            case 40372:
                return 40548;
            case 40378:
                return 40545;
            case 40379:
                return 40536;
            case 40380:
                return 40547;
            case 40383:
            case 40384:
                return 40538;
            case 40386:
                return 40542;
            case 40393:
                return 19736;
            case 40403:
                return 40551;
            case 40406:
                return 40549;
            case 40407:
                return 40485;
            case 40409:
                return 40503;
            case 40410:
                return 40552;
            case 40421:
                return 40502;
            case 40422:
                return 40554;
            case 40431:
                return 40553;
            case 40434:
                return 40555;
            case 40435:
                return 40519;
            case 40440:
                return 40556;
            case 40441:
                return 40560;
            case 40442:
                return 40557;
            case 40445:
                return 40500;
            case 40455:
                return 40559;
            case 40458:
                return 19737;
            case 40460:
                return 40561;
            case 40469:
                return 40492;
            case 40474:
                return 40550;
            case 40475:
                return 40563;
            case 40477:
                return 40514;
            case 40568:
            case 40572:
                return 30839;
            case 40569:
                return 21688;
            case 40570:
                return 40574;
            case 40573:
                return 30416;
            case 40581:
                return 29389;
            case 40599:
                return 20029;
            case 40613:
                return 40614;
            case 40617:
                return 40632;
            case 40623:
                return 26354;
            case 40629:
                return 38754;
            case 40636:
            case 40637:
                return 20040;
            case 40643:
                return 40644;
            case 40652:
                return 40649;
            case 40655:
                return 31896;
            case 40670:
                return 28857;
            case 40680:
                return 20826;
            case 40690:
                return 40682;
            case 40692:
                return 38665;
            case 40694:
                return 40673;
            case 40695:
                return 40681;
            case 40701:
                return 40702;
            case 40703:
                return 40715;
            case 40713:
                return 40717;
            case 40725:
                return 20908;
            case 40756:
                return 40761;
            case 40775:
                return 40772;
            case 40778:
                return 40784;
            case 40779:
                return 25995;
            case 40783:
                return 40785;
            case 40786:
                return 40831;
            case 40788:
                return 40832;
            case 40793:
                return 40837;
            case 40796:
                return 40839;
            case 40799:
                return 40835;
            case 40800:
                return 40838;
            case 40801:
                return 40836;
            case 40803:
                return 20986;
            case 40806:
                return 40840;
            case 40810:
                return 40842;
            case 40812:
                return 40841;
            case 40818:
                return 40843;
            case 40822:
                return 33133;
            case 40823:
                return 40844;
            case 40845:
                return 40857;
            case 40848:
                return 24222;
            case 40849:
                return 19886;
            case 40852:
                return 40858;
            case 40853:
                return 40859;
            case 40860:
                return 40863;
            default:
                return u;
        }
    }

    public static CharSequence toSimp(CharSequence s) {
        StringBuilder sb = new StringBuilder(s);
        toSimp(sb);
        return sb;
    }

    public static void toSimp(StringBuilder sb) {
        int n = sb.length();
        for (int i = 0; i < n; i++) {
            sb.setCharAt(i, toSimp(sb.charAt(i)));
        }
    }

    public static boolean donut() {
        return VERSION.SDK_INT <= 4;
    }

    public static boolean ecl() {
        return VERSION.SDK_INT < 8;
    }

    public static boolean g233() {
        return VERSION.SDK_INT <= 10;
    }

    public static boolean ms() {
        String s = Build.MODEL;
        return s.equalsIgnoreCase("MotoA953") || s.equalsIgnoreCase("Droid") || s.equalsIgnoreCase("Milestone") || s.equalsIgnoreCase("LG-P990");
    }

    public static boolean htcv() {
        return hwk == 2;
    }

    public static boolean htcv1() {
        return Build.MODEL.equalsIgnoreCase("HTC Vision");
    }

    public static boolean jfs() {
        return (zt == -1 || zt == 87 || !isAK()) ? false : true;
    }

    public static boolean jfs1() {
        return jfs();
    }

    public static boolean emjIs(int i) {
        return ir(i, 57344, 58880);
    }

    public static boolean emjDIs(int i) {
        return ir(i, 8192, 16383);
    }

    public static boolean emj2Is(int i) {
        return ir(i, 49152, 53247);
    }

    public static Context emjCT(String s) {
        try {
            return mIme.createPackageContext(s, 0);
        } catch (Throwable th) {
            return null;
        }
    }

    public static boolean emjAM() {
        Context c = emjCT("kl.emjdec");
        if (c != null) {
            emj = R.xml.kbd_emj;
        } else {
            c = emjCT("kl.emoji");
        }
        if (c == null) {
            c = emjCT("KLye.Goomoji");
        }
        if (c != null) {
            emjAm = c.getAssets();
            return true;
        } else if (jb) {
            return true;
        } else {
            noti(mIme, "Emoji codec not found. Please install it if you haven't done so", hp("1.html"));
            return false;
        }
    }

    public static Typeface gf(String s) {
        return xfc == null ? null : Typeface.createFromAsset(xfc.getAssets(), s);
    }

    public static int emjLoad(Key k) {
        try {
            int c = k.codes[0];
            String s1;
            if (emj2Is(c)) {
                c -= 49152;
                if (k.text == null) {
                    s1 = emj2(126976 + c);
                    k.text = s1;
                    k.label = s1;
                }
                if (k.icon != null) {
                    return 0;
                }
                k.icon = Drawable.createFromStream(emjAm.open("o/" + c + ".png"), null);
                return 0;
            } else if (!emjDIs(c)) {
                if (emjIs(c)) {
                    if (jbe) {
                        s1 = emj6(c);
                        if (s1 != null) {
                            k.text = s1;
                            if (jb && emjAm == null) {
                                k.label = s1;
                                return 1;
                            }
                        }
                    }
                    if (k.icon != null) {
                        return 0;
                    }
                    k.icon = Drawable.createFromStream(emjAm.open("e/" + Integer.toHexString(c).substring(1).toUpperCase() + ".png"), null);
                    return 0;
                }
                return -1;
            } else if (k.icon != null) {
                return 0;
            } else {
                k.icon = Drawable.createFromStream(emjAm.open("e/" + Integer.toHexString(emjE(c)).substring(1, 4).toUpperCase() + ".png"), null);
                return 0;
            }
        } catch (Throwable th) {
        }
    }

    private static int emjE(int i) {
        switch (i) {
            case 169:
                return 57934;
            case 174:
                return 57935;
            case 8194:
                return 58765;
            case 8195:
                return 58764;
            case 8197:
                return 58766;
            case 8252:
                return 60208;
            case 8265:
                return 60207;
            case 8482:
                return 58679;
            case 8505:
                return 58675;
            case 8596:
                return 60282;
            case 8597:
                return 60283;
            case 8598:
                return 57911;
            case 8599:
                return 57910;
            case 8600:
                return 57912;
            case 8601:
                return 57913;
            case 8617:
                return 58717;
            case 8618:
                return 58716;
            case 8986:
                return 58746;
            case 8987:
                return 58747;
            case 9193:
                return 57916;
            case 9194:
                return 57917;
            case 9195:
                return 58693;
            case 9196:
                return 58692;
            case 9200:
                return 57389;
            case 9203:
                return 58492;
            case 9410:
                return 58420;
            case 9642:
                return 57882;
            case 9643:
                return 57883;
            case 9654:
                return 57914;
            case 9664:
                return 57915;
            case 9723:
                return 57883;
            case 9724:
                return 57882;
            case 9725:
                return 57883;
            case 9726:
                return 57882;
            case 9728:
                return 57418;
            case 9729:
            case 9925:
                return 57417;
            case 9742:
                return 57353;
            case 9745:
                return 60162;
            case 9748:
                return 57419;
            case 9749:
                return 57413;
            case 9757:
                return 57359;
            case 9786:
                return 58388;
            case 9800:
                return 57919;
            case 9801:
                return 57920;
            case 9802:
                return 57921;
            case 9803:
                return 57922;
            case 9804:
                return 57923;
            case 9805:
                return 57924;
            case 9806:
                return 57925;
            case 9807:
                return 57926;
            case 9808:
                return 57927;
            case 9809:
                return 57928;
            case 9810:
                return 57929;
            case 9811:
                return 57930;
            case 9824:
                return 57870;
            case 9827:
                return 57871;
            case 9829:
                return 57868;
            case 9830:
                return 57869;
            case 9832:
                return 57635;
            case 9851:
                return 60281;
            case 9855:
                return 57866;
            case 9875:
                return 57858;
            case 9888:
                return 57938;
            case 9889:
                return 57661;
            case 9898:
                return 57881;
            case 9899:
                return 57881;
            case 9917:
                return 57368;
            case 9918:
                return 57366;
            case 9924:
                return 57416;
            case 9934:
                return 57931;
            case 9940:
                return 57655;
            case 9962:
                return 57399;
            case 9970:
                return 57633;
            case 9971:
                return 57364;
            case 9973:
                return 57372;
            case 9978:
                return 57634;
            case 9981:
                return 57402;
            case 9986:
                return 58131;
            case 9989:
                return 58718;
            case 9992:
                return 57373;
            case 9993:
                return 57603;
            case 9994:
                return 57360;
            case 9995:
                return 57362;
            case 9996:
                return 57361;
            case 9999:
                return 58113;
            case 10002:
                return 60163;
            case 10004:
                return 58711;
            case 10006:
                return 58163;
            case 10024:
                return 58158;
            case 10035:
                return 57862;
            case 10036:
                return 57861;
            case 10052:
                return 58506;
            case 10055:
                return 58158;
            case 10060:
                return 58163;
            case 10062:
                return 58163;
            case 10067:
                return 57376;
            case 10068:
                return 58166;
            case 10069:
                return 58167;
            case 10071:
                return 57377;
            case 10084:
                return 57378;
            case 10133:
                return 58684;
            case 10134:
                return 58685;
            case 10135:
                return 58708;
            case 10145:
                return 57908;
            case 10160:
                return 60209;
            case 10175:
                return 57873;
            case 10548:
                return 57910;
            case 10549:
                return 57912;
            case 11013:
                return 57909;
            case 11014:
                return 57906;
            case 11015:
                return 57907;
            case 11035:
                return 57882;
            case 11036:
                return 57883;
            case 11088:
                return 58159;
            case 11093:
                return 58162;
            case 12336:
                return 59145;
            case 12349:
                return 57644;
            case 12951:
                return 58125;
            case 12953:
                return 58133;
            default:
                return -1;
        }
    }

    public static boolean noAutoCap() {
        switch (mLC) {
            case am /*6357101*/:
            case aw /*6357111*/:
            case b2 /*6422578*/:
            case bh /*6422632*/:
            case bo /*6422639*/:
            case by /*6422649*/:
            case ck /*6488171*/:
            case cr /*6488178*/:
            case dv /*6553718*/:
            case dz /*6553722*/:
            case gu /*6750325*/:
            case gz /*6750330*/:
            case hi /*6815849*/:
            case ii /*6881385*/:
            case iu /*6881397*/:
            case iw /*6881399*/:
            case ja /*6946913*/:
            case ka /*7012449*/:
            case km /*7012461*/:
            case kn /*7012462*/:
            case ko /*7012463*/:
            case lo /*7077999*/:
            case m2 /*7143474*/:
            case ml /*7143532*/:
            case mm /*7143533*/:
            case mr /*7143538*/:
            case mx /*7143544*/:
            case my /*7143545*/:
            case ne /*7209061*/:
            case oj /*7274602*/:
            case or /*7274610*/:
            case pa /*7340129*/:
            case s4 /*7536692*/:
            case sa /*7536737*/:
            case si /*7536745*/:
            case ta /*7602273*/:
            case te /*7602277*/:
            case tf /*7602278*/:
            case th /*7602280*/:
            case ti /*7602281*/:
            case yi /*7929961*/:
            case zh /*7995496*/:
                return true;
            default:
                return in || bn1 || ls1 || arConAble();
        }
    }

    public static boolean nonLatinEu() {
        switch (mLC) {
            case ag /*6357095*/:
            case el /*6619244*/:
            case hy /*6815865*/:
            case ka /*7012449*/:
            case m2 /*7143474*/:
            case pc /*7340131*/:
                return true;
            default:
                return false;
        }
    }

    public static boolean wantlock() {
        switch (mLC) {
            case ja /*6946913*/:
            case s4 /*7536692*/:
                return true;
            default:
                return zhType() != -1;
        }
    }

    public static boolean ir(char a, char b, char c) {
        return a >= b && a <= c;
    }

    public static boolean ss(int c) {
        return ss1(c) || irn(c);
    }

    public static boolean ss1(int c) {
        switch (c) {
            case 35:
            case 38:
            case 39:
            case 42:
            case 43:
            case 45:
            case 94:
            case 96:
            case 168:
            case 175:
            case 180:
            case 183:
            case 184:
            case 728:
            case 8204:
            case 8205:
            case 8228:
                return true;
            default:
                return ir(c, 7936, 8190);
        }
    }

    public static boolean isWordSep(char c) {
        if (mLC == s4) {
            switch (c) {
                case ',':
                case '/':
                case '_':
                    return false;
            }
        } else if (zt != -1 && ((kid == null || !kid.is(R.xml.kbd_t9)) && irn(c))) {
            return true;
        }
        if (ss(c) || (!iss(c) && !lp(c) && !ir(c, 65281, 65376) && !ir(c, 1632, 1645) && !ir(c, 1757, 1758) && !ir(c, 1563, 1567) && !ir(c, 12288, 12351) && !ir(c, 8192, 8303) && !ir(c, 9728, 9983))) {
            return false;
        }
        return true;
    }

    public static boolean iss(int c) {
        switch (c) {
            case R.styleable.LatinKeyboardBaseView_verticalCorrection /*9*/:
            case R.styleable.LatinKeyboardBaseView_popupLayout /*10*/:
            case 33:
            case 41:
            case 44:
            case 45:
            case 46:
            case 58:
            case 59:
            case 62:
            case 63:
            case 93:
            case 125:
            case 187:
            case 1369:
            case 1370:
            case 1371:
            case 1372:
            case 1373:
            case 1374:
            case 1417:
            case 1548:
            case 1563:
            case 1566:
            case 1567:
            case 1748:
            case 8212:
            case 8230:
            case 12299:
            case 12301:
            case 12303:
            case 12305:
                return true;
            default:
                return false;
        }
    }

    public static boolean lp(char c) {
        return ir(c, ' ', '/') || ir(c, ':', '@') || ir(c, '[', '`') || ir(c, '{', '~') || ir(c, 160, 191);
    }

    public static boolean isArWordSep(char c) {
        return c == '-' || isWordSep(c);
    }

    public static char toPunc1(char c) {
        if (mLC == hy) {
            return toPuncHy(c);
        }
        return toPunc(c);
    }

    public static char toPunc(char pc) {
        if ((isCJ() || (hw() && H.isH(mIme.mWord.lastChar()))) && wfw == 1) {
            return toPuncCJK(pc);
        }
        if (eth) {
            return toPuncEth(pc);
        }
        if (arConAble()) {
            return toPuncAr(pc);
        }
        return pc;
    }

    static boolean isCJ() {
        return zhType() != -1 || mLC == ja;
    }

    public static boolean isJamo(char c) {
        return ir(c, 12592, 12684);
    }

    public static boolean isHangul(char c) {
        return ir(c, 44032, 55215);
    }

    public static boolean isLatin(char c) {
        return ir(c, '!', 591) || ir(c, 7680, 7935);
    }

    public static boolean sslw(ArrayList<CharSequence> sa, int[] codes, int c) {
        boolean z = true;
        boolean b = false;
        if (!isT9Semi()) {
            b = add((ArrayList) sa, codes, mLC != en);
            if (mLC == en) {
                switch (c) {
                    case 105:
                        sa.add("I'll");
                        if (codes[0] != 105) {
                            z = false;
                        }
                        return z;
                    case 110:
                        sa.add("no");
                        return false;
                    case 116:
                        sa.add("the");
                        sa.add("that");
                        sa.add("there");
                        sa.add("this");
                        return false;
                    case 121:
                        sa.add("yes");
                        sa.add("yeah");
                        return false;
                }
            } else if (zhType() == -1) {
                switch (c) {
                    case 97:
                        return add((ArrayList) sa, xa);
                    case 98:
                        return add((ArrayList) sa, xb);
                    case 99:
                        return add((ArrayList) sa, xc);
                    case 100:
                        sa.add("d'");
                        return add((ArrayList) sa, xd);
                    case 101:
                        return add((ArrayList) sa, xe);
                    case 103:
                        return add((ArrayList) sa, xg);
                    case 104:
                        return add((ArrayList) sa, xh);
                    case 105:
                        return add((ArrayList) sa, xi);
                    case 106:
                        return add((ArrayList) sa, xj);
                    case 107:
                        return add((ArrayList) sa, xk);
                    case 108:
                        sa.add("l'");
                        sa.add("'l");
                        sa.add("'ls");
                        return add((ArrayList) sa, xl);
                    case 109:
                        return add((ArrayList) sa, xm);
                    case 110:
                        sa.add("n'");
                        sa.add("'n");
                        return add((ArrayList) sa, xn);
                    case 111:
                        return add((ArrayList) sa, xo);
                    case 112:
                        return add((ArrayList) sa, xp);
                    case 113:
                        return add((ArrayList) sa, xq);
                    case BuildConfig.VERSION_CODE /*114*/:
                        return add((ArrayList) sa, xr);
                    case 115:
                        sa.add("s'");
                        sa.add("'s");
                        return add((ArrayList) sa, xs);
                    case 116:
                        return add((ArrayList) sa, xt);
                    case 117:
                        return add((ArrayList) sa, xu);
                    case 118:
                        return add((ArrayList) sa, xv);
                    case 119:
                        return add((ArrayList) sa, xw);
                    case 120:
                        return add((ArrayList) sa, xx);
                    case 121:
                        return add((ArrayList) sa, xy);
                    case 122:
                        return add((ArrayList) sa, xz);
                }
            }
        } else if (!mt9()) {
            if (mLC == en) {
                z = false;
            }
            return add((ArrayList) sa, codes, z);
        }
        return b;
    }

    public static char ivw(char c) {
        switch (mLC) {
            case cs /*6488179*/:
                switch (c) {
                    case 'k':
                    case 's':
                    case 'u':
                    case 'v':
                    case 'z':
                        return c;
                }
                break;
            case da /*6553697*/:
                switch (c) {
                    case 'i':
                    case 229:
                    case 248:
                        return c;
                }
                break;
            case en /*6619246*/:
                switch (c) {
                    case 'a':
                        return c;
                    case 'i':
                        return 'I';
                    default:
                        return 0;
                }
            case es /*6619251*/:
                switch (c) {
                    case 'y':
                        return c;
                }
                break;
            case fi /*6684777*/:
            case nb /*7209058*/:
            case nn /*7209070*/:
            case sv /*7536758*/:
                switch (c) {
                    case 229:
                    case 246:
                        return c;
                }
                break;
            case fr /*6684786*/:
                switch (c) {
                    case 'y':
                    case 224:
                        return c;
                }
                break;
            case nl /*7209068*/:
                switch (c) {
                    case 'u':
                        return c;
                }
                break;
        }
        switch (c) {
            case 'a':
            case 'e':
            case 'i':
                return c;
            default:
                return 0;
        }
    }

    public static boolean add(ArrayList<CharSequence> sa, String s) {
        boolean b = false;
        if (s == null) {
            return 0;
        }
        for (int i = 0; i < s.length(); i++) {
            b |= add((ArrayList) sa, s.charAt(i));
        }
        return b;
    }

    public static boolean add(ArrayList<CharSequence> sa, char c1) {
        char c = ivw(c1);
        if (mIme.mWord.isFirstCharCapitalized()) {
            c1 = Character.toUpperCase(c1);
        }
        sa.add(Character.toString(c1));
        if (c == 0) {
            return false;
        }
        if (mIme.mWord.isFirstCharCapitalized()) {
            c = Character.toUpperCase(c);
        }
        sa.add(1, Character.toString(c));
        return true;
    }

    public static boolean add(ArrayList<CharSequence> sa, int[] c, boolean ac) {
        int l = c.length;
        boolean b = false;
        int i = 0;
        while (i < l && c[i] != -1) {
            char c1 = (char) c[i];
            if (eth) {
                add((ArrayList) sa, am1(c1));
            } else if (zt == 67) {
                add((ArrayList) sa, toCJ(c1));
            } else {
                b |= add((ArrayList) sa, c1);
                if (ac) {
                    switch (c1) {
                        case 'a':
                            b |= add((ArrayList) sa, xa);
                            break;
                        case 'b':
                            b |= add((ArrayList) sa, xb);
                            break;
                        case 'c':
                            b |= add((ArrayList) sa, xc);
                            break;
                        case 'd':
                            b |= add((ArrayList) sa, xd);
                            break;
                        case 'e':
                            b |= add((ArrayList) sa, xe);
                            break;
                        case 'g':
                            b |= add((ArrayList) sa, xg);
                            break;
                        case 'h':
                            b |= add((ArrayList) sa, xh);
                            break;
                        case 'i':
                            b |= add((ArrayList) sa, xi);
                            break;
                        case 'j':
                            b |= add((ArrayList) sa, xj);
                            break;
                        case 'k':
                            b |= add((ArrayList) sa, xk);
                            break;
                        case 'l':
                            b |= add((ArrayList) sa, xl);
                            break;
                        case 'm':
                            b |= add((ArrayList) sa, xm);
                            break;
                        case 'n':
                            b |= add((ArrayList) sa, xn);
                            break;
                        case 'o':
                            b |= add((ArrayList) sa, xo);
                            break;
                        case 'p':
                            b |= add((ArrayList) sa, xp);
                            break;
                        case 'q':
                            b |= add((ArrayList) sa, xq);
                            break;
                        case BuildConfig.VERSION_CODE /*114*/:
                            b |= add((ArrayList) sa, xr);
                            break;
                        case 's':
                            b |= add((ArrayList) sa, xs);
                            break;
                        case 't':
                            b |= add((ArrayList) sa, xt);
                            break;
                        case 'u':
                            b |= add((ArrayList) sa, xu);
                            break;
                        case 'v':
                            b |= add((ArrayList) sa, xv);
                            break;
                        case 'w':
                            b |= add((ArrayList) sa, xw);
                            break;
                        case 'x':
                            b |= add((ArrayList) sa, xx);
                            break;
                        case 'y':
                            b |= add((ArrayList) sa, xy);
                            break;
                        case 'z':
                            b |= add((ArrayList) sa, xz);
                            break;
                        default:
                            break;
                    }
                }
            }
            i++;
        }
        return b;
    }

    public static String cc1(char c) {
        String s = cc((int) c);
        return s == null ? Character.toString(c) : s;
    }

    public static String cc(int i) {
        switch (mLC) {
            case as /*6357107*/:
            case bn /*6422638*/:
            case bp /*6422640*/:
                switch (i) {
                    case 63745:
                        return "ক্ষ";
                }
                break;
            case ck /*6488171*/:
                switch (i) {
                    case 63744:
                        return "ᏣᎳᎩ";
                    case 63745:
                        return "ᎣᏏᏲ";
                    case 63746:
                        return "ᏩᏙ";
                }
                break;
            case gu /*6750325*/:
                switch (i) {
                    case 63744:
                        return "ર્";
                    case 63745:
                        return "જ્ઞ";
                    case 63746:
                        return "ત્ર";
                    case 63747:
                        return "ક્ષ";
                    case 63748:
                        return "શ્ર";
                }
                break;
            case iw /*6881399*/:
                break;
            case kn /*7012462*/:
                switch (i) {
                    case 63744:
                        return "ರ್";
                    case 63745:
                        return "ಜ್ಞ";
                    case 63746:
                        return "ತ್ರ";
                    case 63747:
                        return "ಕ್ಷ";
                    case 63748:
                        return "ಶ್ರ";
                }
                break;
            case lo /*7077999*/:
                switch (i) {
                    case 63744:
                        return "ໍ່";
                    case 63745:
                        return " ົ້";
                    case 63746:
                        return " ິ້";
                    case 63747:
                        return " ີ້";
                    case 63748:
                        return "ຫຼ";
                    case 63749:
                        return " ັ້";
                    case 63750:
                        return " ຶ້";
                    case 63751:
                        return " ື້";
                }
                break;
            case ml /*7143532*/:
                switch (i) {
                    case 63744:
                        return "്ര";
                    case 63745:
                        return "ക്ഷ";
                }
                break;
            case mt /*7143540*/:
                switch (i) {
                    case 266:
                        return "C·";
                    case 267:
                        return "c·";
                    case 288:
                        return "G·";
                    case 289:
                        return "g·";
                    case 294:
                        return "H¯";
                    case 295:
                        return "h¯";
                    case 379:
                        return "Z·";
                    case 380:
                        return "z·";
                }
                break;
            case ne /*7209061*/:
            case nw /*7209079*/:
                switch (i) {
                    case 63744:
                        return "क्ष";
                    case 63745:
                        return "ज्ञ";
                    case 63746:
                        return "कौ";
                    case 63747:
                        return "त्र";
                    case 63748:
                        return "न्ह";
                    case 63749:
                        return "म्ह";
                    case 63750:
                        return "ल्ह";
                    case 63751:
                        return "ह्र";
                    case 63752:
                        return "अं";
                    case 63753:
                        return "अः";
                    case 63754:
                        return "रू";
                }
                break;
            case or /*7274610*/:
                switch (i) {
                    case 63744:
                        return "୍ର";
                    case 63745:
                        return "ର୍";
                    case 63746:
                        return "ଜ୍ଞ";
                    case 63747:
                        return "ତ୍ର";
                    case 63748:
                        return "କ୍ଷ";
                    case 63749:
                        return "ଶ୍ର";
                    case 63750:
                        return "ଯ଼";
                }
                break;
            case pa /*7340129*/:
                switch (i) {
                    case 63744:
                        return "ਮ੍ਰਿ";
                }
                break;
            case si /*7536745*/:
                switch (i) {
                    case 63744:
                        return "්‍ර";
                    case 63745:
                        return "්‍ය";
                    case 63746:
                        return "ළු";
                    case 63747:
                        return "ර්‍";
                    case 63748:
                        return "්‍";
                }
                break;
            case ta /*7602273*/:
                switch (i) {
                    case 63744:
                        return "க்ஷ";
                    case 63745:
                        return "ஷ்ர";
                    case 63746:
                        return "ஸ்ரீ";
                }
                break;
            case yi /*7929961*/:
                switch (i) {
                    case 64287:
                        return "ײַ";
                    case 64332:
                        return "בֿ";
                    case 64334:
                        return "פֿ";
                }
                break;
            default:
                switch (i) {
                    case 7717:
                        return "ḥ";
                    case 7779:
                        return "ṣ";
                }
                break;
        }
        switch (i) {
            case 63744:
                return "לֹ";
        }
        if (dvn) {
            return ccdvn(i);
        }
        return null;
    }

    private static String ccdvn(int i) {
        switch (i) {
            case 63744:
                return "्र";
            case 63745:
                return "र्";
            case 63746:
                return "ज्ञ";
            case 63747:
                return "त्र";
            case 63748:
                return "क्ष";
            case 63749:
                return "श्र";
            default:
                return null;
        }
    }

    public static boolean kHW(Key k) {
        return k.codes[0] == -123;
    }

    public static void hw(LatinKeyboardView v, int w, int h1) {
        if (h == null) {
            h = new H();
        }
        h.setV(v);
        h.onSizeChanged(w, h1, 0, 0);
        mIme.p();
    }

    public static void hw1() {
        if (h == null) {
            h = new H();
        }
        h.loadHW();
    }

    public static List<CharSequence> cvt(String[] s) {
        List cs = new ArrayList();
        add(cs, s, 0);
        return cs;
    }

    public static void add(List<CharSequence> cs, String[] s, int i) {
        while (i < s.length) {
            cs.add(s[i]);
            char tfc = tfc(s[i]);
            if (tfc != 0) {
                cs.add(Character.toString(tfc));
            }
            i++;
        }
    }

    public static void removeDupes(List<CharSequence> suggestions) {
        if (suggestions.size() >= 2) {
            int i = 1;
            while (i < suggestions.size()) {
                CharSequence cur = (CharSequence) suggestions.get(i);
                for (int j = 0; j < i; j++) {
                    CharSequence previous = (CharSequence) suggestions.get(j);
                    if (j == 0) {
                        previous = to(previous, zhType());
                    }
                    if (TextUtils.equals(cur, previous)) {
                        suggestions.remove(i);
                        i--;
                        break;
                    }
                }
                i++;
            }
        }
    }

    public static boolean noSpace() {
        switch (mLC) {
            case bo /*6422639*/:
            case cr /*6488178*/:
            case dz /*6553722*/:
            case ii /*6881385*/:
            case ip /*6881392*/:
            case iu /*6881397*/:
            case ja /*6946913*/:
            case km /*7012461*/:
            case lo /*7077999*/:
            case mx /*7143544*/:
            case my /*7143545*/:
            case oj /*7274602*/:
            case th /*7602280*/:
            case zh /*7995496*/:
                return true;
            default:
                return false;
        }
    }

    public static boolean shf() {
        return iv == null ? false : iv.isShifted();
    }

    public static long lpto(Key k) {
        if (ir((char) k.codes[0], 4608, 5017) || k.codes[0] == 39 || k.codes[0] == 714) {
            return 200;
        }
        if (jaT9(k.popupResId) && isLatinC && mLC != ja) {
            return 190;
        }
        if (jaT9(k.popupResId) || (spp(k.label) && isAK())) {
            return ru != mLC ? 110 : 200;
        } else {
            if (k.codes[0] == -2) {
                return 350;
            }
            return (k.codes[0] == 32 || k.codes[0] == 10) ? 500 : lpt;
        }
    }

    static boolean spp(CharSequence s1) {
        if (s1 == null) {
            return false;
        }
        if (s1.charAt(0) == ',' && s1.length() > 1) {
            return true;
        }
        for (CharSequence s2 : new CharSequence[]{tvo, ax, tsc, "་", "ៀ", "់"}) {
            if (s2.equals(s1)) {
                return true;
            }
        }
        return false;
    }

    static boolean jaT9(int kid) {
        return kid == R.xml.popup_ja;
    }

    public static boolean spk(CharSequence l) {
        return !tvo.equals(l);
    }

    public static boolean wds() {
        return dsp && !noSpace();
    }

    public static boolean thTone(char pc) {
        return ir(pc, 3656, 3659);
    }

    public static void sk(List<Key> ks, int i, CharSequence pc, int j) {
        char c;
        Key k = (Key) ks.get(i);
        if (pc.length() > j) {
            c = pc.charAt(j);
        } else {
            c = 0;
        }
        k.codes[0] = c;
        k.label = c == 0 ? " " : "" + c;
    }

    public static int kh(Key k) {
        int h = k.popupResId == R.xml.popup_hw ? k.height / 4 : k.height;
        if (k.codes[0] == 32) {
            return (h * 5) / 6;
        }
        return h;
    }

    public static int vc(Key k) {
        return (donut() || jaT9(k.popupResId)) ? 0 : -kh(k);
    }

    public static String hj() {
        return mHanja == 0 ? "한(漢)" : "한글";
    }

    public static void skh(KB kb, Key k, int d) {
        List<Key> ks = kb.getKeys();
        int l = ks.size();
        if (l > 0) {
            int nh = (kh(k) * 3) / 4;
            int oh = (((Key) ks.get(0)).height * d) / 2;
            if (oh != 0) {
                for (int i = 0; i < l; i++) {
                    Key k1 = (Key) ks.get(i);
                    k1.height = nh;
                    k1.y = (k1.y * nh) / oh;
                }
            }
        }
    }

    public static int fa() {
        int i = ("ZCW".indexOf(zhType()) != -1 || (t9() && t9r())) ? 75 : 100;
        if (!isLand()) {
            return i;
        }
        i += 20;
        if (mLC == th) {
            return i + 20;
        }
        return i;
    }

    public static boolean ws() {
        return ws && !isCJ();
    }

    static boolean irabc(char c) {
        return ir(c, 'a', 'z');
    }

    static boolean irPY(char c) {
        return irabc(c) || c == 252;
    }

    public static boolean htm(String s) {
        if (s == null) {
            return true;
        }
        int l = s.length();
        if (l == 0 || H.isH(s.charAt(l - 1))) {
            return true;
        }
        char[] tm = new char[]{'\'', 715, 711, 714, 729, 175};
        int n = tm.length;
        while (true) {
            int n2 = n;
            n = n2 - 1;
            if (n2 <= 0) {
                return false;
            }
            if (s.indexOf(tm[n]) != -1) {
                return true;
            }
        }
    }

    public static char cs() {
        if (mLC == ja) {
            return 26085;
        }
        return JF == 1 ? 31616 : 27491;
    }

    public static boolean hws() {
        return hw() && h != null && h.iss();
    }

    public static boolean hasp() {
        int i = -1;
        if (cHasp == 0) {
            if (!asp && ((kid.is(R.xml.kbd_qwerty) || kid.is(R.xml.kbd_split) || dvorak()) && mLC != fr && !azerty() && ((!asp() || dvorak()) && "YP".indexOf(zhType()) == -1))) {
                i = 1;
            }
            cHasp = i;
        }
        if (cHasp == 1) {
            return true;
        }
        if (kid.is(R.xml.kbd_qwerty) && mLC == ar && !isLand()) {
            return true;
        }
        return false;
    }

    private static boolean asp() {
        switch (mLC) {
            case am /*6357101*/:
            case bb /*6422626*/:
            case cy /*6488185*/:
            case di /*6553705*/:
            case es /*6619251*/:
            case gz /*6750330*/:
            case hw /*6815863*/:
            case ja /*6946913*/:
            case kb /*7012450*/:
            case ku /*7012469*/:
            case lj /*7077994*/:
            case lv /*7078006*/:
            case mg /*7143527*/:
            case mi /*7143529*/:
            case mt /*7143540*/:
            case om /*7274605*/:
            case su /*7536757*/:
            case sx /*7536760*/:
            case tf /*7602278*/:
            case ti /*7602281*/:
            case uz /*7667834*/:
            case wa /*7798881*/:
            case yu /*7929973*/:
                return true;
            default:
                return ga() != -1 || ls1;
        }
    }

    public static void cl(String l) {
        boolean z;
        boolean z2 = false;
        mIL = l;
        mLC = lc(mIL);
        eth = false;
        bho = false;
        zw = false;
        nvo = false;
        uc = false;
        bn1 = false;
        dvn = false;
        in = false;
        aad = false;
        bfa = false;
        nm = false;
        ls1 = false;
        switch (mLC) {
            case am /*6357101*/:
            case gz /*6750330*/:
            case ti /*7602281*/:
                eth = true;
                break;
            case as /*6357107*/:
            case bn /*6422638*/:
            case bp /*6422640*/:
                bn1 = true;
                break;
            case aw /*6357111*/:
            case bh /*6422632*/:
            case hi /*6815849*/:
            case mr /*7143538*/:
            case ne /*7209061*/:
            case nw /*7209079*/:
            case sa /*7536737*/:
                dvn = true;
                break;
            case b2 /*6422578*/:
            case by /*6422649*/:
            case jw /*6946935*/:
            case mm /*7143533*/:
            case mx /*7143544*/:
            case my /*7143545*/:
            case tf /*7602278*/:
                nm = true;
                break;
            case bb /*6422626*/:
            case cy /*6488185*/:
            case ga /*6750305*/:
            case gd /*6750308*/:
            case hw /*6815863*/:
            case ku /*7012469*/:
            case mg /*7143527*/:
            case mi /*7143529*/:
            case mt /*7143540*/:
            case s4 /*7536692*/:
            case yo /*7929967*/:
                aad = true;
                break;
            case bo /*6422639*/:
            case dz /*6553722*/:
                bho = true;
                break;
            case cl /*6488172*/:
            case hm /*6815853*/:
            case ls /*7078003*/:
                ls1 = true;
                break;
            case cr /*6488178*/:
            case ii /*6881385*/:
            case iu /*6881397*/:
            case oj /*7274602*/:
                uc = true;
                break;
            case fa /*6684769*/:
            case mz /*7143546*/:
            case s2 /*7536690*/:
                bfa = true;
                break;
            case gu /*6750325*/:
            case kn /*7012462*/:
            case ml /*7143532*/:
            case or /*7274610*/:
            case pa /*7340129*/:
            case si /*7536745*/:
            case ta /*7602273*/:
            case te /*7602277*/:
                in = true;
                break;
        }
        if (mLC == si && klsi == 0) {
            nm = true;
        }
        in |= bn1 | dvn;
        aad |= in | ls1;
        if (my == mLC || arConAble() || (in && ml != mLC)) {
            z = true;
        } else {
            z = false;
        }
        zw = z;
        xtf(mIme);
        mergeKB1(l);
        zt = zhType(mIL);
        if (zt != -1) {
            cjs = mIL.charAt(4);
        }
        cy1();
        cy2 = cy2(mIL);
        if (!(cCy == -1 && cy2 == -1)) {
            z2 = true;
        }
        cyr = z2;
        es1();
        isLatinC = isLatin2(mIL);
        qwertz1();
        azerty1();
        noAutoCapC = noAutoCap();
        mnf = mnf();
        djv = djv();
        ga1();
        acc = acc();
        tlpc = tlpc();
        dsw = dsw();
        zhFlag();
    }

    private static String acc() {
        String s = "ˆ¨ˉ`ˇ´";
        switch (mLC) {
            case ag /*6357095*/:
            case el /*6619244*/:
            case pc /*7340131*/:
                return "᾿῾῍῝῎῞῏῟῁῭΅¨ι῀˘ˉ`";
            case bb /*6422626*/:
            case kb /*7012450*/:
                return "ˇ`´·";
            case ca /*6488161*/:
                return "ˆ¨ˉ`ˇ´·";
            case di /*6553705*/:
                return "¸^";
            case dn /*6553710*/:
                return "̤";
            case ku /*7012469*/:
                return "¨´`¸^";
            case mt /*7143540*/:
            case yo /*7929967*/:
                return "`´¯·";
            case om /*7274605*/:
                return "`";
            case sx /*7536760*/:
                return "´";
            case wa /*7798881*/:
                return "`´˚^";
            default:
                return "ˆ¨ˉ`ˇ´";
        }
    }

    private static boolean dsw() {
        switch (mLC) {
            case cr /*6488178*/:
            case ii /*6881385*/:
            case iu /*6881397*/:
            case oj /*7274602*/:
                return true;
            default:
                return zt != -1 || mLC == ja || mLC == ko;
        }
    }

    public static void ial(Context c1) {
        boolean ss = sslv();
        String str = "";
        xz = str;
        xy = str;
        xx = str;
        xw = str;
        xv = str;
        xt = str;
        xs = str;
        xr = str;
        xq = str;
        xp = str;
        xn = str;
        xm = str;
        xl = str;
        xk = str;
        xj = str;
        xh = str;
        xg = str;
        xd = str;
        xc = str;
        xb = str;
        xu = str;
        xo = str;
        xi = str;
        xe = str;
        xa = str;
        switch (mLC) {
            case af /*6357094*/:
                xa = "á";
                xe = "éèêë";
                xi = "íîï";
                xo = "óô";
                xu = "úû";
                xy = "ý";
                return;
            case bb /*6422626*/:
                xc = "čç";
                xd = "ḍ";
                xe = "ɛéè";
                xg = "ǧ";
                xq = "ɣ";
                xr = "ṛ";
                xs = "ṣ";
                xt = "ṭ";
                xz = "ẓ";
                xw = "ʷ";
                return;
            case eo /*6619247*/:
                xc = "ĉ";
                xu = "ŭ";
                xh = "ĥ";
                xj = "ĵ";
                xs = "ŝ";
                xg = "ĝ";
                return;
            case ip /*6881392*/:
                xa = "ɑæɐ";
                xb = "βɓʙ";
                xc = "ɕç";
                xd = "ðd͡ʒɖɗ";
                xe = "əɚɵɘɛɜɝɞᵊ";
                xg = "ɠɢʛ";
                xh = "ɥɦħɧʜʱʰ";
                xi = "ɪɪ̈ɨ";
                xj = "ʝɟʄʲ";
                xl = "ɫɬʟɭɮˡ";
                xm = "ɱ";
                xn = "ŋɲɴɳⁿ";
                xo = "ɔœøɒɔ̃ɶ";
                xp = "ɸ";
                xr = "ɾʁɹɻʀɽɺʳ";
                xs = "ʃʂ";
                xt = "θt͡ʃt͡sʈ";
                xu = "ʊʊ̈ʉ";
                xv = "ʌʋⱱ";
                xw = "ʍɯɰʷ";
                xx = "χ";
                xy = "ʎɣʏɤ";
                xz = "ʒʐʑ";
                return;
            case ku /*7012469*/:
                xc = "ç";
                xe = "ê";
                xi = "î";
                xu = "û";
                xs = "ş";
                return;
            case la /*7077985*/:
                xa = "āæ";
                xe = "ēë";
                xi = "ī";
                xo = "ōœ";
                xu = "ū";
                xy = "ȳ";
                return;
            case mt /*7143540*/:
                xc = "ċ";
                xz = "ż";
                xh = "ħ";
                xg = "ġ";
                return;
            case ro /*7471215*/:
                xa = "ăâ";
                xi = "î";
                xs = roS;
                xt = roT;
                return;
            case sk /*7536747*/:
                xa = "áä";
                xe = "é";
                xi = "í";
                xo = "óô";
                xu = "ú";
                xy = "ý";
                xc = "č";
                xd = "ď";
                xc = "č";
                xl = "ľĺ";
                xn = "ň";
                xr = "ŕ";
                xs = "š";
                xt = "ť";
                xz = "ž";
                return;
            case sx /*7536760*/:
                xa = "á";
                xe = "é";
                xi = "í";
                xo = "ó";
                xu = "ú";
                xc = "c";
                return;
            case we /*7798885*/:
                xa = "ä";
                xo = "öó";
                xu = "ü";
                xc = "čć";
                xe = "ě";
                xi = "í";
                xl = "ł";
                xn = "ń";
                xr = "řŕ";
                xs = "šś";
                xz = "žź";
                return;
            default:
                str = ss ? "â" : mLC == hu ? "á" : mLC == wo ? "ãà" : c1.getString(R.string.alternates_for_a);
                xa = str;
                str = ss ? "" : mLC == sq ? "ë" : (mLC == hu || mLC == wo) ? "ëé" : c1.getString(R.string.alternates_for_e);
                xe = str;
                xi = ss ? "" : c1.getString(R.string.alternates_for_i);
                str = ss ? "" : mLC == wo ? "ó" : c1.getString(R.string.alternates_for_o);
                xo = str;
                str = ss ? "" : mLC == wo ? "ù" : c1.getString(R.string.alternates_for_u);
                xu = str;
                xy = ss ? "" : c1.getString(R.string.alternates_for_y);
                xs = ss ? "š" : c1.getString(R.string.alternates_for_s);
                xn = mLC == wo ? "ñŋ" : c1.getString(R.string.alternates_for_n);
                xt = c1.getString(R.string.alternates_for_t);
                xc = ss ? "čć" : c1.getString(R.string.alternates_for_c);
                xd = ss ? "đ" : c1.getString(R.string.alternates_for_d);
                xr = c1.getString(R.string.alternates_for_r);
                xh = mLC == at ? "ḥ" : "";
                str = (mLC == at || mLC == lv) ? "ķ" : "";
                xk = str;
                xz = xz();
                xg = xg();
                if (mLC == pl) {
                    xx = "ź";
                }
                xl = mLC == lv ? "ļ" : "ļł";
                return;
        }
    }

    private static boolean mnf() {
        switch (mLC) {
            case am /*6357101*/:
            case ap /*6357104*/:
            case aw /*6357111*/:
            case b2 /*6422578*/:
            case bh /*6422632*/:
            case bo /*6422639*/:
            case by /*6422649*/:
            case ck /*6488171*/:
            case cw /*6488183*/:
            case dv /*6553718*/:
            case dz /*6553722*/:
            case 6619237:
            case gu /*6750325*/:
            case gz /*6750330*/:
            case ha /*6815841*/:
            case hi /*6815849*/:
            case hy /*6815865*/:
            case ip /*6881392*/:
            case ka /*7012449*/:
            case km /*7012461*/:
            case kn /*7012462*/:
            case lo /*7077999*/:
            case m2 /*7143474*/:
            case ml /*7143532*/:
            case mm /*7143533*/:
            case mr /*7143538*/:
            case mx /*7143544*/:
            case my /*7143545*/:
            case ne /*7209061*/:
            case nv /*7209078*/:
            case or /*7274610*/:
            case pa /*7340129*/:
            case s3 /*7536691*/:
            case s4 /*7536692*/:
            case sa /*7536737*/:
            case si /*7536745*/:
            case ta /*7602273*/:
            case te /*7602277*/:
            case ti /*7602281*/:
                break;
            case ar /*6357106*/:
            case fa /*6684769*/:
            case iw /*6881399*/:
            case th /*7602280*/:
            case yi /*7929961*/:
                if (!ecl()) {
                    return false;
                }
                break;
            default:
                if (in || bn1 || djv() || ((arConAble() && g233()) || el())) {
                    return true;
                }
                return false;
        }
        return true;
    }

    private static boolean djv() {
        switch (mLC) {
            case bb /*6422626*/:
            case bo /*6422639*/:
            case cr /*6488178*/:
            case dn /*6553710*/:
            case dz /*6553722*/:
            case ii /*6881385*/:
            case iu /*6881397*/:
            case lo /*7077999*/:
            case oj /*7274602*/:
            case s4 /*7536692*/:
            case sx /*7536760*/:
            case tf /*7602278*/:
                return true;
            default:
                return ls1;
        }
    }

    static void xtf(Context c) {
        if (xtf) {
            xfc = emjCT("kl.myscript");
        }
    }

    public static boolean scc(int i) {
        return ir((char) i, 63744, 63759);
    }

    public static int lp(int pc) {
        return pc - 63696;
    }

    public static boolean isLP(int i) {
        return i > 0 && ir((char) i, 63696, 63743);
    }

    public static int nl() {
        return Math.min(lps.length(), mIme.mLanguageSwitcher.getLocaleCount());
    }

    public static int nlc() {
        return (nl() + 5) / 6;
    }

    public static int ks() {
        return isLand() ? ksl : ksp;
    }

    public static void ls(SharedPreferences sp) {
        Editor ed = sp.edit();
        mWideKey = gv(sp, ed, "wide_key", true);
        tid = Integer.valueOf(gv(sp, ed, PREF_KEYBOARD_LAYOUT, DEFAULT_LAYOUT_ID)).intValue();
        roST = gv(sp, ed, "roST", g233());
        if (roST) {
            roS = "ş";
            roT = "ţ";
        } else {
            roS = "ș";
            roT = "ț";
        }
        shd = sp.getBoolean("shd", shd);
        jbe = sp.getBoolean("jbe", jbe);
        hsk = sp.getBoolean("hsk", hsk);
        ots = sp.getBoolean("ots", false);
        snl = sp.getBoolean("snl", true);
        acl = sp.getBoolean("al", acl);
        pyfzzcs = sp.getBoolean("pyfzzcs", false);
        xtf = gv(sp, ed, "ms", xtf);
        ws = sp.getBoolean("ws", true);
        zhsr = sp.getBoolean("zhsr", true);
        stky = sp.getBoolean("stky", VERSION.SDK_INT < 11);
        rmd = sp.getInt("rmd", 0);
        rc = sp.getBoolean("read_contact", false);
        if (!(rc || dicC == null)) {
            dicC.close();
            dicC = null;
        }
        usd = sp.getBoolean("usd", true);
        rmp = sp.getBoolean("remap", false);
        dkf = sp.getBoolean("dkf", false);
        mSms = sp.getBoolean("sms", false);
        if (!(usd || dicU == null)) {
            dicU.close();
            dicU = null;
        }
        lrn = sp.getBoolean("lrn", false);
        if (!((lrn && usd) || dicA == null)) {
            dicA.close();
            dicA = null;
        }
        sm = sp.getBoolean(sm1, true);
        sm2 = sp.getBoolean(sm2s, false);
        boolean il = isLand();
        boolean z = dm.widthPixels >= 800 && ((!il && sm2) || (il && sm));
        sm3 = z;
        sw2 = sp.getBoolean("sw", false);
        awk = sp.getBoolean("awk", false);
        kof = sp.getBoolean("kof", false);
        jl = sp.getBoolean("jl", false);
        zhlx = sp.getBoolean("zhlx", true);
        dk = sp.getBoolean("dk", true);
        cjsp = sp.getBoolean("cjsp", true);
        mt9 = Integer.valueOf(sp.getString("mt9", "0")).intValue();
        ss = Integer.valueOf(sp.getString("ss", "1")).intValue();
        fsp = Integer.valueOf(sp.getString("fsp", "0")).intValue();
        fsl = Integer.valueOf(sp.getString("fsl", "1")).intValue();
        if (sp.getBoolean("insf", !g233()) || isT9Semi()) {
            z = true;
        } else {
            z = false;
        }
        insf = z;
        lpsl = sp.getBoolean("lpsl", true);
        swsl = sp.getBoolean("swsl", true);
        kl = sp.getInt("kl", 0);
        klzh = sp.getInt("klzh", klzh);
        klJA = sp.getInt("klja", klJA);
        klAR = sp.getInt("klar", klAR);
        klfa = sp.getInt("klfa", klfa);
        klhy = sp.getInt("klhy", klhy);
        klAM = sp.getInt("klam", klAM);
        klZY = sp.getInt("klzy", klZY);
        klBO = sp.getInt("klbo", klBO);
        klTH = sp.getInt("klth", klTH);
        klKM = sp.getInt("klkm", klKM);
        klKO = sp.getInt("klko", klKO);
        klsi = sp.getInt("klsi", klsi);
        klKO1 = sp.getInt("klko1", klKO1);
        klRU = sp.getInt("klru", klRU);
        klbg = sp.getInt("klbg", klRU);
        isHW = sp.getInt("hw", 0);
        hlc = Integer.valueOf(sp.getString("hlcolor", "6")).intValue();
        if (hlc >= hlcs.length) {
            hlc = 6;
        }
        hlc = hlcs[hlc];
        hlp.setAntiAlias(true);
        H.p.setColor(hlc);
        lbp.setColor(hlc);
        lbp.setAlpha(72);
        hlp.setStrokeWidth(0.0f);
        hlp.setTextAlign(Align.CENTER);
        hlp.setTextSize((float) (((ss + 4) * mIme.getResources().getDimensionPixelSize(R.dimen.key_preview_text_size_large)) / 11));
        dmn = sp.getString(dmn1, "com,org,edu,net,gov,html,apk,google");
        ial();
        nsa = sp.getString(nsa1, "");
        if (nsa.length() == 0) {
            nsa = nsa2;
        }
        sym = sp.getString(sym1, "");
        if (sym.length() == 0) {
            sym = "!@#$%&*();~|☆";
        }
        fontSize = Integer.valueOf(sp.getString("key_font_size", "100")).intValue();
        mArDisconn = sp.getBoolean(ipa, false);
        mArConn = sp.getBoolean(car, false);
        mReverse = false;
        bgd = gv(sp, ed, "bg1", false);
        if (!(bgd || dicB == null)) {
            dicB.close();
            dicB = null;
        }
        lpt = (long) gv(sp, ed, lpt1, 450, 150, 1000);
        mtt = (long) gv(sp, ed, "mtt", 500, 50, 1000);
        tlc = gv(sp, ed, "tlc", tlc, 0, 1000);
        bgb = gv(sp, ed, "bgb", bgb, 0, 100);
        kcs = gv(sp, ed, "kcs", kcs, 0, 1000);
        wlp = gv(sp, ed, "wlp", wlp, 1, 100);
        int vol = gv(sp, ed, "vol", 0, 0, 1000);
        volf = vol == 0 ? -1.0f : ((float) vol) / 1000.0f;
        psb = gv(sp, ed, "psb", "!?,\"':;()-—/@_[]\\|");
        pkp = gv(sp, ed, "pkp", "♥。¿¡{}<>\\『』「」【】《》、:·_…&()-+;@/=\"'!?,");
        sym2 = gv(sp, ed, "sym2", "=\"'+-:?/[]{}");
        psbl();
        sstl = sp.getBoolean("sstl", sstl);
        hwa = sp.getBoolean("hwa", true);
        hwt = hwa ? (long) gv(sp, ed, "hwt", 300, 200, 2000) : 10000;
        hwk = Integer.valueOf(sp.getString("hwk", htcv1() ? "2" : "0")).intValue();
        ed.putString("hwk", Integer.toString(hwk));
        ed.putString(nsa1, nsa);
        ed.putString(dmn1, dmn);
        ed.putString(sym1, sym);
        ed.putBoolean(ipa, mArDisconn);
        ed.putBoolean(car, mArConn);
        ed.putBoolean(rtl, mReverse);
        ed.putBoolean("stky", stky);
        try {
            ed.commit();
        } catch (Throwable th) {
        }
    }

    private static int gv(SharedPreferences sp, Editor ed, String s, int i, int b, int c) {
        boolean f = false;
        try {
            i = Integer.valueOf(sp.getString(s, "")).intValue();
        } catch (Throwable th) {
            f = true;
        }
        if (i < b) {
            i = b;
            f = true;
        }
        if (i > c) {
            i = c;
            f = true;
        }
        if (f) {
            ed.putString(s, Long.toString((long) i));
        }
        return i;
    }

    private static String gv(SharedPreferences sp, Editor ed, String s, String d) {
        String r = sp.getString(s, "");
        if (r.length() != 0) {
            return r;
        }
        r = d;
        ed.putString(s, r);
        return r;
    }

    private static boolean gv(SharedPreferences sp, Editor ed, String s, boolean d) {
        boolean r = sp.getBoolean(s, d);
        ed.putBoolean(s, r);
        return r;
    }

    private static void ial() {
        dmn2 = new ArrayList();
        ics(dmn2, dmn.split(","));
    }

    private static void ics(List<CharSequence> l, CharSequence[] cs) {
        for (CharSequence s : cs) {
            l.add(s);
        }
    }

    public static int ltr(int i) {
        int c = -1 - i;
        return (((((c & 16711680) / 3) & 16711680) | (((c & 65280) / 3) & 65280)) | (((c & 255) / 3) & 255)) + i;
    }

    public static int dkr(int i, int f, int d) {
        int c = i;
        return i - ((((((c & 16711680) * f) / d) & 16711680) | ((((c & 65280) * f) / d) & 65280)) | ((((c & 255) * f) / d) & 255));
    }

    public static void md1(Key k2, String s) {
        k2.codes[0] = s.charAt(0);
        k2.label = s;
    }

    public static void md2(Key k2, String s) {
        k2.label = s;
    }

    public static boolean nb(char c) {
        return H.isH(c) || H.IsBopomofoAll(c) || H.IsHangulAll(c) || isRTL(c) || H.isKana(c) || bmk(c) || ec(c) || in(c);
    }

    private static boolean bmk(char c) {
        return ir(c, 3840, 4351);
    }

    static boolean in(char c) {
        return ir(c, 2304, 3583);
    }

    static boolean mm(char c) {
        return ir(c, 43968, 44025);
    }

    static boolean by(char c) {
        return ir(c, 5888, 5919);
    }

    static boolean my(char c) {
        return ir(c, 4096, 4255) || ir(c, 43616, 43643);
    }

    static boolean ugi(char c) {
        return ir(c, 6656, 6687);
    }

    static boolean su(char c) {
        return ir(c, 7040, 7103);
    }

    private static boolean dv(char c) {
        return ir(c, 1920, 1983);
    }

    static boolean bo(char c) {
        return ir(c, 3840, 4095);
    }

    private static boolean am(char c) {
        return ir(c, 4608, 5023);
    }

    static boolean irn(int c) {
        return ir(c, (int) MAX_WORD_LENGTH, 57);
    }

    private static boolean th(char c) {
        return ir(c, 3584, 3711);
    }

    private static boolean ii(char c) {
        return ir(c, 40960, 42159);
    }

    private static boolean km(char c) {
        return ir(c, 6016, 6143);
    }

    private static boolean ec(char c) {
        return ir(c, 4608, 7295);
    }

    public static int enc(int c) {
        switch (mLC) {
            case ag /*6357095*/:
            case el /*6619244*/:
            case pc /*7340131*/:
                return ela(c);
            case as /*6357107*/:
            case bn /*6422638*/:
            case bp /*6422640*/:
                return enc(c, 2432);
            case az /*6357114*/:
                return encaz(c);
            case bb /*6422626*/:
                return encbb(c);
            case ck /*6488171*/:
                return enc(c, 5024);
            case dv /*6553718*/:
                return enc(c, 1920);
            case gu /*6750325*/:
                return encgu(c);
            case hy /*6815865*/:
                return enchy(c);
            case jw /*6946935*/:
                if (c == 1890) {
                    c = 1708;
                }
                return enc(c, 1569, 64);
            case kn /*7012462*/:
                return enc(c, 3200);
            case lo /*7077999*/:
                return enc(c, 3712);
            case ml /*7143532*/:
                return enc(c, 3328);
            case mm /*7143533*/:
                return enc(c, 43968);
            case mx /*7143544*/:
            case my /*7143545*/:
                return enc(c, 4096);
            case or /*7274610*/:
                return enc(c, 2816);
            case pa /*7340129*/:
                return enc(c, 2560);
            case ro /*7471215*/:
                return enro(c);
            case si /*7536745*/:
                return enc(c, 3456);
            case ta /*7602273*/:
                return enc(c, 2944);
            case te /*7602277*/:
                return enc(c, 3072);
            case uk /*7667819*/:
                return encuk(c);
            case yi /*7929961*/:
                return encyi(c);
            case yo /*7929967*/:
                return encyo(c);
            default:
                if (cyr) {
                    return encru(c);
                }
                if (eth) {
                    return encgz(c);
                }
                if (dvn) {
                    return enc(c, 2304);
                }
                if (arConAble()) {
                    return enc(c, 1569, 64);
                }
                if (zt == 66 && c == MAX_WORD_LENGTH) {
                    return 46;
                }
                if (c == 713) {
                    return 175;
                }
                return c;
        }
    }

    private static int enc(int c, int i) {
        return enc(c, i, 65);
    }

    private static int enc1(int c, int i) {
        return enc1(c, i, 65);
    }

    private static int enc(int c, int i, int a) {
        switch (c) {
            case 8204:
                return 35;
            case 8205:
                return 36;
            default:
                if (c < i) {
                    return c;
                }
                return (c - i) + a;
        }
    }

    private static int enc1(int c, int i, int a) {
        switch (c) {
            case 35:
                return 8204;
            case 36:
                return 8205;
            default:
                if (c < a) {
                    return c;
                }
                return (c + i) - a;
        }
    }

    public static int enc1(int c) {
        switch (mLC) {
            case ag /*6357095*/:
            case el /*6619244*/:
            case pc /*7340131*/:
                return ela1(c);
            case as /*6357107*/:
            case bn /*6422638*/:
            case bp /*6422640*/:
                return enc1(c, 2432);
            case az /*6357114*/:
                return enc1az(c);
            case bb /*6422626*/:
                return enc1bb(c);
            case ck /*6488171*/:
                return enc1(c, 5024);
            case dv /*6553718*/:
                return enc1(c, 1920);
            case gu /*6750325*/:
                return encgu1(c);
            case hy /*6815865*/:
                return enchy1(c);
            case jw /*6946935*/:
                return c == 203 ? 1890 : enc1(c, 1569, 64);
            case kn /*7012462*/:
                return enc1(c, 3200);
            case lo /*7077999*/:
                return enc1(c, 3712);
            case ml /*7143532*/:
                return enc1(c, 3328);
            case mm /*7143533*/:
                return enc1(c, 43968);
            case mx /*7143544*/:
            case my /*7143545*/:
                return enc1(c, 4096);
            case or /*7274610*/:
                return enc1(c, 2816);
            case pa /*7340129*/:
                return enc1(c, 2560);
            case ro /*7471215*/:
                return en1ro(c);
            case si /*7536745*/:
                return enc1(c, 3456);
            case ta /*7602273*/:
                return enc1(c, 2944);
            case te /*7602277*/:
                return enc1(c, 3072);
            case uk /*7667819*/:
                return enc1uk(c);
            case yi /*7929961*/:
                return enc1yi(c);
            case yo /*7929967*/:
                return enc1yo(c);
            default:
                if (cyr) {
                    return enc1ru(c);
                }
                if (eth) {
                    return enc1gz(c);
                }
                if (dvn) {
                    return enc1(c, 2304);
                }
                if (arConAble()) {
                    return enc1(c, 1569, 64);
                }
                return c == 175 ? 713 : c;
        }
    }

    public static int enchy1(int c) {
        return HKM.m(2, (char) c);
    }

    public static int encgu1(int c) {
        switch (c) {
            case 39:
                return 8217;
            case 46:
                return 2748;
            case 58:
                return 2691;
            case 66:
                return 2733;
            case 67:
                return 2715;
            case 68:
                return 2727;
            case 70:
                return 2720;
            case 71:
                return 2712;
            case 74:
                return 2717;
            case 75:
                return 2710;
            case 76:
                return 2739;
            case 78:
                return 2723;
            case 80:
                return 2731;
            case 81:
                return 2784;
            case 83:
                return 2743;
            case 84:
                return 2725;
            case 96:
                return 2690;
            case 97:
                return 2693;
            case 98:
                return 2732;
            case 99:
                return 2714;
            case 100:
                return 2726;
            case 102:
                return 2719;
            case 103:
                return 2711;
            case 104:
                return 2745;
            case 106:
                return 2716;
            case 107:
                return 2709;
            case 108:
                return 2738;
            case 109:
                return 2734;
            case 110:
                return 2728;
            case 112:
                return 2730;
            case 113:
                return 2756;
            case BuildConfig.VERSION_CODE /*114*/:
                return 2736;
            case 115:
                return 2744;
            case 116:
                return 2724;
            case 118:
                return 2741;
            case 120:
                return 2742;
            case 121:
                return 2735;
            case 124:
                return 2749;
            case 126:
                return 2689;
            case 172:
                return 2765;
            case 194:
                return 2701;
            case 196:
                return 2694;
            case 198:
                return 2704;
            case 200:
                return 2699;
            case 201:
                return 2703;
            case 206:
                return 2696;
            case 207:
                return 2695;
            case 208:
                return 2722;
            case 209:
                return 2713;
            case 212:
                return 2705;
            case 216:
                return 2707;
            case 217:
                return 2697;
            case 219:
                return 2708;
            case 220:
                return 2698;
            case 226:
                return 2757;
            case 228:
                return 2750;
            case 230:
                return 2760;
            case 232:
                return 2755;
            case 233:
                return 2759;
            case 238:
                return 2752;
            case 239:
                return 2751;
            case 240:
                return 2721;
            case 241:
                return 2718;
            case 243:
                return 2768;
            case 244:
                return 2761;
            case 248:
                return 2763;
            case 249:
                return 2753;
            case 251:
                return 2764;
            case 252:
                return 2754;
            default:
                if (c < MAX_WORD_LENGTH || c > 57) {
                    return c;
                }
                return (c + 2790) - 48;
        }
    }

    public static int enchy(int c) {
        char[] c1 = new char[]{'a', 'b', 'g', 'd', 'e', 'z', 234, 'y', 254, 246, 'i', 'l', 230, 231, 'k', 'h', 253, 'x', 248, 'm', 'j', 'n', 241, 'w', 236, 'p', 255, 252, 's', 'v', 't', 'r', 'c', 'u', 235, 'q', 'o', 'f', 240};
        switch (c) {
            case 1417:
                return 58;
            case 8228:
                return 46;
            default:
                return ir(c, 1377, 1415) ? c1[c - 1377] : c;
        }
    }

    public static int encaz(int c) {
        switch (c) {
            case 1241:
                return 601;
            default:
                return c;
        }
    }

    public static int enc1az(int c) {
        switch (c) {
            case 601:
                return 1241;
            default:
                return c;
        }
    }

    public static int encgu(int c) {
        switch (c) {
            case 2689:
                return 126;
            case 2690:
                return 96;
            case 2691:
                return 58;
            case 2693:
                return 97;
            case 2694:
                return 196;
            case 2695:
                return 207;
            case 2696:
                return 206;
            case 2697:
                return 217;
            case 2698:
                return 220;
            case 2699:
                return 200;
            case 2701:
                return 194;
            case 2703:
                return 201;
            case 2704:
                return 198;
            case 2705:
                return 212;
            case 2707:
                return 216;
            case 2708:
                return 219;
            case 2709:
                return 107;
            case 2710:
                return 75;
            case 2711:
                return 103;
            case 2712:
                return 71;
            case 2713:
                return 209;
            case 2714:
                return 99;
            case 2715:
                return 67;
            case 2716:
                return 106;
            case 2717:
                return 74;
            case 2718:
                return 241;
            case 2719:
                return 102;
            case 2720:
                return 70;
            case 2721:
                return 240;
            case 2722:
                return 208;
            case 2723:
                return 78;
            case 2724:
                return 116;
            case 2725:
                return 84;
            case 2726:
                return 100;
            case 2727:
                return 68;
            case 2728:
                return 110;
            case 2730:
                return 112;
            case 2731:
                return 80;
            case 2732:
                return 98;
            case 2733:
                return 66;
            case 2734:
                return 109;
            case 2735:
                return 121;
            case 2736:
                return BuildConfig.VERSION_CODE;
            case 2738:
                return 108;
            case 2739:
                return 76;
            case 2741:
                return 118;
            case 2742:
                return 120;
            case 2743:
                return 83;
            case 2744:
                return 115;
            case 2745:
                return 104;
            case 2748:
                return 46;
            case 2749:
                return 124;
            case 2750:
                return 228;
            case 2751:
                return 239;
            case 2752:
                return 238;
            case 2753:
                return 249;
            case 2754:
                return 252;
            case 2755:
                return 232;
            case 2756:
                return 113;
            case 2757:
                return 226;
            case 2759:
                return 233;
            case 2760:
                return 230;
            case 2761:
                return 244;
            case 2763:
                return 248;
            case 2764:
                return 251;
            case 2765:
                return 172;
            case 2768:
                return 243;
            case 2784:
                return 81;
            case 8217:
                return 39;
            default:
                if (c < 2790 || c > 2799) {
                    return c;
                }
                return (c - 2790) + MAX_WORD_LENGTH;
        }
    }

    public static int encgz(int c) {
        switch (c) {
            case 66:
                return 199;
            case 76:
                return 231;
            case 78:
                return 241;
            case 79:
                return 216;
            case 80:
                return 254;
            case 88:
                return 39;
            case 120:
                return 45;
            default:
                return c;
        }
    }

    public static int enc1gz(int c) {
        switch (c) {
            case 39:
                return 88;
            case 45:
                return 120;
            default:
                return c;
        }
    }

    public static int encyi(int c) {
        switch (c) {
            case 1520:
                return 91;
            case 1521:
                return 93;
            case 1522:
                return 123;
            case 1523:
                return 39;
            case 1524:
                return 34;
            case 64285:
                return 53;
            case 64287:
                return 41;
            case 64299:
                return 56;
            case 64302:
                return MAX_WORD_LENGTH;
            case 64303:
                return 49;
            case 64305:
                return 50;
            case 64309:
                return 51;
            case 64315:
                return 54;
            case 64324:
                return 55;
            case 64330:
                return 57;
            case 64331:
                return 52;
            case 64332:
                return 40;
            case 64334:
                return 94;
            default:
                return ir((char) c, 1488, 1514) ? (c - 1488) + 96 : c;
        }
    }

    public static int encru(int c) {
        switch (c) {
            case 1171:
                return 227;
            case 1175:
                return 246;
            case 1179:
                return 245;
            case 1187:
                return 241;
            case 1199:
                return 252;
            case 1201:
                return 246;
            case 1203:
                return 229;
            case 1207:
                return 246;
            case 1211:
                return 244;
            case 1241:
                return 228;
            case 1251:
            case 1257:
                return 248;
            case 1263:
                return 252;
            default:
                return HKM.cy2a((char) c);
        }
    }

    public static int enc1ru(int c) {
        return HKM.m(1, (char) c);
    }

    public static int enc1uk(int c) {
        switch (c) {
            case 97:
                return 1072;
            case 98:
                return 1073;
            case 99:
                return 1094;
            case 100:
                return 1076;
            case 101:
                return 1077;
            case 102:
                return 1092;
            case 103:
                return 1169;
            case 104:
                return 1075;
            case 105:
                return 1110;
            case 106:
                return 1108;
            case 107:
                return 1082;
            case 108:
                return 1083;
            case 109:
                return 1084;
            case 110:
                return 1085;
            case 111:
                return 1086;
            case 112:
                return 1087;
            case 113:
                return 1097;
            case BuildConfig.VERSION_CODE /*114*/:
                return 1088;
            case 115:
                return 1089;
            case 116:
                return 1090;
            case 117:
                return 1091;
            case 118:
                return 1074;
            case 119:
                return 1103;
            case 120:
                return 1093;
            case 121:
                return 1080;
            case 122:
                return 1079;
            case 231:
                return 1095;
            case 239:
                return 1111;
            case 240:
                return 1078;
            case 241:
                return 1102;
            case 253:
                return 1081;
            case 254:
                return 1096;
            case 255:
                return 1100;
            default:
                return c;
        }
    }

    public static int encuk(int c) {
        switch (c) {
            case 1072:
                return 97;
            case 1073:
                return 98;
            case 1074:
                return 118;
            case 1075:
                return 104;
            case 1076:
                return 100;
            case 1077:
                return 101;
            case 1078:
                return 240;
            case 1079:
                return 122;
            case 1080:
                return 121;
            case 1081:
                return 253;
            case 1082:
                return 107;
            case 1083:
                return 108;
            case 1084:
                return 109;
            case 1085:
                return 110;
            case 1086:
                return 111;
            case 1087:
                return 112;
            case 1088:
                return BuildConfig.VERSION_CODE;
            case 1089:
                return 115;
            case 1090:
                return 116;
            case 1091:
                return 117;
            case 1092:
                return 102;
            case 1093:
                return 120;
            case 1094:
                return 99;
            case 1095:
                return 231;
            case 1096:
                return 254;
            case 1097:
                return 113;
            case 1100:
                return 255;
            case 1102:
                return 241;
            case 1103:
                return 119;
            case 1108:
                return 106;
            case 1110:
                return 105;
            case 1111:
                return 239;
            case 1169:
                return 103;
            default:
                return c;
        }
    }

    public static int enc1yo(int c) {
        switch (c) {
            case 96:
                return 768;
            case 180:
                return 769;
            case 183:
                return 803;
            default:
                return c;
        }
    }

    public static int encyo(int c) {
        switch (c) {
            case 768:
                return 96;
            case 769:
                return 180;
            case 803:
                return 183;
            default:
                return c;
        }
    }

    public static int encbb(int c) {
        switch (c) {
            case 603:
                return 230;
            case 611:
                return 254;
            case 711:
                return 215;
            default:
                return c;
        }
    }

    public static int enc1bb(int c) {
        switch (c) {
            case 215:
                return 711;
            case 230:
                return 603;
            case 254:
                return 611;
            default:
                return c;
        }
    }

    public static int enc1yi(int c) {
        switch (c) {
            case 34:
                return 1524;
            case 39:
                return 1523;
            case 40:
                return 64332;
            case 41:
                return 64287;
            case MAX_WORD_LENGTH /*48*/:
                return 64302;
            case 49:
                return 64303;
            case 50:
                return 64305;
            case 51:
                return 64309;
            case 52:
                return 64331;
            case 53:
                return 64285;
            case 54:
                return 64315;
            case 55:
                return 64324;
            case 56:
                return 64299;
            case 57:
                return 64330;
            case 91:
                return 1520;
            case 93:
                return 1521;
            case 94:
                return 64334;
            case 123:
                return 1522;
            default:
                if (ir((char) c, '`', 'z')) {
                    return (c + 1488) - 96;
                }
                return 0;
        }
    }

    public static int ela(int c) {
        switch (c) {
            case 181:
            case 956:
                return 109;
            case 912:
                return 238;
            case 940:
                return 225;
            case 941:
                return 233;
            case 942:
                return 7717;
            case 943:
                return 237;
            case 945:
                return 97;
            case 946:
                return 98;
            case 947:
                return 103;
            case 948:
                return 100;
            case 949:
                return 101;
            case 950:
                return 122;
            case 951:
                return 104;
            case 952:
                return 119;
            case 953:
                return 105;
            case 954:
                return 99;
            case 955:
                return 108;
            case 957:
                return 110;
            case 958:
                return 120;
            case 959:
                return 111;
            case 960:
                return 112;
            case 961:
                return BuildConfig.VERSION_CODE;
            case 962:
                return 106;
            case 963:
                return 115;
            case 964:
                return 116;
            case 965:
                return 121;
            case 966:
                return 102;
            case 967:
                return 107;
            case 968:
                return 113;
            case 969:
                return 117;
            case 970:
                return 239;
            case 971:
                return 255;
            case 972:
                return 243;
            case 973:
                return 253;
            case 974:
                return 250;
            default:
                return c;
        }
    }

    public static int en1ro(int c) {
        switch (c) {
            case 537:
                return 351;
            case 539:
                return 355;
            default:
                return c;
        }
    }

    public static int enro(int c) {
        switch (c) {
            case 351:
                return 537;
            case 355:
                return 539;
            default:
                return c;
        }
    }

    public static int ela1(int c) {
        switch (c) {
            case 97:
                return 945;
            case 98:
                return 946;
            case 99:
                return 954;
            case 100:
                return 948;
            case 101:
                return 949;
            case 102:
            case 118:
                return 966;
            case 103:
                return 947;
            case 104:
                return 951;
            case 105:
                return 953;
            case 106:
                return 962;
            case 107:
                return 967;
            case 108:
                return 955;
            case 109:
                return 956;
            case 110:
                return 957;
            case 111:
                return 959;
            case 112:
                return 960;
            case 113:
                return 968;
            case BuildConfig.VERSION_CODE /*114*/:
                return 961;
            case 115:
                return 963;
            case 116:
                return 964;
            case 117:
                return 969;
            case 119:
                return 952;
            case 120:
                return 958;
            case 121:
                return 965;
            case 122:
                return 950;
            case 225:
                return 940;
            case 233:
                return 941;
            case 237:
                return 943;
            case 238:
                return 912;
            case 239:
                return 970;
            case 243:
                return 972;
            case 250:
                return 974;
            case 253:
                return 973;
            case 255:
                return 971;
            case 7717:
                return 942;
            default:
                return c;
        }
    }

    public static CharSequence hns(CharSequence pc, int i, Key k) {
        if (pc == null) {
            return null;
        }
        if (!ns && isM) {
            return null;
        }
        int l = pc.length();
        if (l == 0) {
            return null;
        }
        boolean sh = iv.isShifted();
        if (!sh && zt == 67) {
            return k.label;
        }
        if (l == 1) {
            int c = pc.charAt(0);
            if (siu()) {
                if (c == 4024) {
                    if (!sh) {
                        pc = null;
                    }
                    return pc;
                } else if (sh && !k.modifier) {
                    char c2 = S.toUpper(c);
                    if (c != c2) {
                        return cc1(c2);
                    }
                    return null;
                }
            }
            CharSequence s = cc(c);
            if (s != null) {
                pc = s;
            }
            return pc;
        } else if (!nm || k.modifier) {
            if (fk() && isAK() && !k.modifier) {
                if (k.codes[0] == 39) {
                    return acc.substring(0, 1);
                }
                if (siu() && sh) {
                    return pc.subSequence(l - 1, l);
                }
                char c3 = hint1(i, k);
                if (c3 != 0) {
                    return Character.toString(c3);
                }
            }
            if (H.IsHWDigit(pc.charAt(l - 1))) {
                return pc.subSequence(l - 1, l);
            }
            int c0 = pc.charAt(0);
            String cc1 = mLC == mt ? null : cc(c0);
            if (cc1 == null) {
                cc1 = (hns(c0) || (sh && siu() && !k.modifier)) ? pc.subSequence(0, 1) : pc.subSequence(l - 1, l);
            }
            return cc1;
        } else {
            if (sh) {
                pc = pc.subSequence(l - 1, l);
            }
            return pc;
        }
    }

    public static boolean hns(char c) {
        return H.IsHWDigit(c) || ir(c, ' ', '@') || ir(c, '[', '`') || km(c) || ir(c, '{', '~');
    }

    /* JADX WARNING: Missing block: B:38:0x00bf, code:
            if (ncf != false) goto L_0x00c4;
     */
    /* JADX WARNING: Missing block: B:39:0x00c1, code:
            if (r9 != false) goto L_0x0111;
     */
    /* JADX WARNING: Missing block: B:40:0x00c3, code:
            r9 = true;
     */
    /* JADX WARNING: Missing block: B:41:0x00c4, code:
            if (r2 != null) goto L_0x0113;
     */
    /* JADX WARNING: Missing block: B:44:0x00df, code:
            if (zt != 90) goto L_0x00e1;
     */
    /* JADX WARNING: Missing block: B:45:0x00e1, code:
            if (r9 == false) goto L_0x00fa;
     */
    /* JADX WARNING: Missing block: B:48:0x0111, code:
            r9 = false;
     */
    /* JADX WARNING: Missing block: B:49:0x0113, code:
            if (r9 == false) goto L_0x0128;
     */
    /* JADX WARNING: Missing block: B:55:?, code:
            return "" + r1;
     */
    /* JADX WARNING: Missing block: B:56:?, code:
            return r8.label + acc;
     */
    /* JADX WARNING: Missing block: B:57:?, code:
            return acc + r8.label;
     */
    /* JADX WARNING: Missing block: B:58:?, code:
            return r1 + r2;
     */
    /* JADX WARNING: Missing block: B:59:?, code:
            return r2 + r1;
     */
    public static java.lang.String hint(int r7, android.inputmethodservice.Keyboard.Key r8, boolean r9) {
        /*
        r3 = 1;
        r4 = 0;
        r1 = hint1(r7, r8);
        r2 = r8.popupCharacters;
        r2 = (java.lang.String) r2;
        if (r1 != 0) goto L_0x0018;
    L_0x000c:
        r5 = 10;
        if (r7 != r5) goto L_0x0018;
    L_0x0010:
        if (r2 != 0) goto L_0x0018;
    L_0x0012:
        r5 = cyr;
        if (r5 == 0) goto L_0x001c;
    L_0x0016:
        r1 = 1105; // 0x451 float:1.548E-42 double:5.46E-321;
    L_0x0018:
        if (r1 != 0) goto L_0x001f;
    L_0x001a:
        r3 = 0;
    L_0x001b:
        return r3;
    L_0x001c:
        r1 = 94;
        goto L_0x0018;
    L_0x001f:
        r1 = toPunc(r1);
        if (r8 != 0) goto L_0x0039;
    L_0x0025:
        r3 = new java.lang.StringBuilder;
        r3.<init>();
        r4 = "";
        r3 = r3.append(r4);
        r3 = r3.append(r1);
        r3 = r3.toString();
        goto L_0x001b;
    L_0x0039:
        r0 = arConAble();
        if (r0 == 0) goto L_0x008d;
    L_0x003f:
        if (r9 != 0) goto L_0x0079;
    L_0x0041:
        r9 = r3;
    L_0x0042:
        if (r2 != 0) goto L_0x0046;
    L_0x0044:
        r2 = "";
    L_0x0046:
        r3 = irn(r1);
        if (r3 == 0) goto L_0x007b;
    L_0x004c:
        r3 = new java.lang.StringBuilder;
        r3.<init>();
        r3 = r3.append(r1);
        r3 = r3.append(r2);
        r4 = r1 + -48;
        r4 = an(r4);
        r3 = r3.append(r4);
        r2 = r3.toString();
    L_0x0067:
        if (r9 != 0) goto L_0x006f;
    L_0x0069:
        r3 = siu();
        if (r3 == 0) goto L_0x0077;
    L_0x006f:
        r3 = rev(r2);
        r2 = r3.toString();
    L_0x0077:
        r3 = r2;
        goto L_0x001b;
    L_0x0079:
        r9 = r4;
        goto L_0x0042;
    L_0x007b:
        r3 = new java.lang.StringBuilder;
        r3.<init>();
        r3 = r3.append(r2);
        r3 = r3.append(r1);
        r2 = r3.toString();
        goto L_0x0067;
    L_0x008d:
        r5 = mLC;
        r6 = 7602280; // 0x740068 float:1.0653063E-38 double:3.7560254E-317;
        if (r5 != r6) goto L_0x00b6;
    L_0x0094:
        r5 = 3664; // 0xe50 float:5.134E-42 double:1.8103E-320;
        r6 = 3673; // 0xe59 float:5.147E-42 double:1.8147E-320;
        r5 = ir(r1, r5, r6);
        if (r5 == 0) goto L_0x00b6;
    L_0x009e:
        r5 = new java.lang.StringBuilder;
        r5.<init>();
        r6 = "";
        r5 = r5.append(r6);
        r6 = r1 + 48;
        r6 = r6 + -3664;
        r6 = (char) r6;
        r5 = r5.append(r6);
        r2 = r5.toString();
    L_0x00b6:
        r5 = r8.codes;
        r5 = r5[r4];
        switch(r5) {
            case 39: goto L_0x00e1;
            case 714: goto L_0x00db;
            case 776: goto L_0x00db;
            default: goto L_0x00bd;
        };
    L_0x00bd:
        r5 = ncf;
        if (r5 != 0) goto L_0x00c4;
    L_0x00c1:
        if (r9 != 0) goto L_0x0111;
    L_0x00c3:
        r9 = r3;
    L_0x00c4:
        if (r2 != 0) goto L_0x0113;
    L_0x00c6:
        r3 = new java.lang.StringBuilder;
        r3.<init>();
        r4 = "";
        r3 = r3.append(r4);
        r3 = r3.append(r1);
        r3 = r3.toString();
        goto L_0x001b;
    L_0x00db:
        r5 = zt;
        r6 = 90;
        if (r5 == r6) goto L_0x00bd;
    L_0x00e1:
        if (r9 == 0) goto L_0x00fa;
    L_0x00e3:
        r3 = new java.lang.StringBuilder;
        r3.<init>();
        r4 = r8.label;
        r3 = r3.append(r4);
        r4 = acc;
        r3 = r3.append(r4);
        r3 = r3.toString();
        goto L_0x001b;
    L_0x00fa:
        r3 = new java.lang.StringBuilder;
        r3.<init>();
        r4 = acc;
        r3 = r3.append(r4);
        r4 = r8.label;
        r3 = r3.append(r4);
        r3 = r3.toString();
        goto L_0x001b;
    L_0x0111:
        r9 = r4;
        goto L_0x00c4;
    L_0x0113:
        if (r9 == 0) goto L_0x0128;
    L_0x0115:
        r3 = new java.lang.StringBuilder;
        r3.<init>();
        r3 = r3.append(r1);
        r3 = r3.append(r2);
        r3 = r3.toString();
        goto L_0x001b;
    L_0x0128:
        r3 = new java.lang.StringBuilder;
        r3.<init>();
        r3 = r3.append(r2);
        r3 = r3.append(r1);
        r3 = r3.toString();
        goto L_0x001b;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.klye.ime.latin.M.hint(int, android.inputmethodservice.Keyboard$Key, boolean):java.lang.String");
    }

    public static char hint1(int i, Key k2) {
        char co = (char) k2.codes[0];
        if (bho) {
            return bohi(co);
        }
        if (siu()) {
            return nm ? 0 : S.toUpper(co);
        } else {
            char c = 0;
            char[] c1 = sym.toCharArray();
            char[] c2 = sym2.toCharArray();
            int i2 = 11;
            char[] c3 = new char[]{171, 187, 8230, 8756, 8757, 8251, 8213, 8672, 8673, 8674, 8634};
            i2 = 10;
            i2 = 10;
            int[] p1 = new int[]{6, 4, 6, 5, 5, 5, 1, 4, 5, 4};
            int[] iArr = new int[10];
            iArr = new int[]{5, 5, 5, 5, 5, 5, 1, 4, 4, 3};
            int[] iArr2 = new int[10];
            iArr2 = new int[]{6, 5, 6, 5, 5, 6, 1, 5, 5, 5};
            int[] iArr3 = new int[10];
            iArr3 = new int[]{5, 0, 6, 5, 0, 6, 1, 4, 0, 4};
            int[] iArr4 = new int[10];
            iArr4 = new int[]{5, 4, 6, 5, 4, 6, 1, 4, 4, 5};
            int[] p = new int[]{5, 0, 5, 5, 0, 5, 1, 4, 0, 3};
            int r1 = 0;
            int l = 1000;
            if (cyr && mLC != 6422631) {
                switch (kid.mXml) {
                    case R.xml.kbd_cyph:
                    case R.xml.kbd_split:
                        r1 = 1000;
                        p = iArr4;
                        break;
                    default:
                        p = iArr3;
                        break;
                }
            }
            switch (kid.mXml) {
                case R.xml.kbd_big:
                    p = iArr3;
                    break;
                case R.xml.kbd_big_split:
                    r1 = 1000;
                    p = p1;
                    break;
                case R.xml.kbd_lt:
                    p = iArr2;
                    break;
                case R.xml.kbd_split:
                    r1 = 1000;
                    p = iArr;
                    break;
            }
            int j = p[0] + p[2];
            int k = (p[3] + j) + p[5];
            int i1 = adj(i, p);
            if (i1 == -1) {
                return 0;
            }
            i = i1;
            boolean dvorak = kid.is(R.xml.kbd_dvorak);
            if (!kid.is(R.xml.kbd_dvrks)) {
                if (!kid.is(R.xml.kbd_clmks)) {
                    switch (mLC) {
                        case hy /*6815865*/:
                            return 0;
                        case iw /*6881399*/:
                            switch (co) {
                                case 1493:
                                    return 1520;
                                case 1501:
                                    return 1521;
                                case 1503:
                                    return 1522;
                                default:
                                    if (!sm3) {
                                        switch (co) {
                                            case 1490:
                                                return '3';
                                            case 1491:
                                                return '2';
                                            case 1495:
                                                return '7';
                                            case 1497:
                                                return '6';
                                            case 1498:
                                                return '9';
                                            case 1499:
                                                return '4';
                                            case 1500:
                                                return '8';
                                            case 1506:
                                                return '5';
                                            case 1507:
                                                return '0';
                                            case 1513:
                                                return '1';
                                        }
                                    }
                                    return 0;
                            }
                        case yi /*7929961*/:
                            switch (co) {
                                case 1488:
                                    return 64302;
                                case 1489:
                                    return 64305;
                                case 1492:
                                    return 64332;
                                case 1493:
                                    return 1520;
                                case 1495:
                                    return 1522;
                                case 1496:
                                    return 64287;
                                case 1497:
                                    return 64285;
                                case 1499:
                                    return 64315;
                                case 1500:
                                    return 1521;
                                case 1501:
                                    return 64334;
                                case 1503:
                                    return 64309;
                                case 1508:
                                    return 64324;
                                case 1512:
                                    return 64303;
                                case 1513:
                                    return 64299;
                                case 1514:
                                    return 64330;
                                default:
                                    return 0;
                            }
                        default:
                            if (!tlpc) {
                                if (dvorak) {
                                    r1 = 10;
                                    j = 19;
                                    k = 1;
                                }
                                int hn = ((i - r1) + 1) % 10;
                                if (i >= r1 && i <= r1 + 9) {
                                    c = (char) ((bn1 ? 2534 : MAX_WORD_LENGTH) + hn);
                                }
                                if (c == 0 && !dvorak) {
                                    switch (mLC) {
                                        case aw /*6357111*/:
                                        case bh /*6422632*/:
                                        case bo /*6422639*/:
                                        case ck /*6488171*/:
                                        case dz /*6553722*/:
                                        case gu /*6750325*/:
                                        case hi /*6815849*/:
                                        case km /*7012461*/:
                                        case lo /*7077999*/:
                                        case mr /*7143538*/:
                                        case sa /*7536737*/:
                                        case te /*7602277*/:
                                            j = 13;
                                            k = 26;
                                            break;
                                        case ps /*7340147*/:
                                            j = 13;
                                            k = 25;
                                            break;
                                        case sd /*7536740*/:
                                        case ug /*7667815*/:
                                        case ur /*7667826*/:
                                            k = 20;
                                            break;
                                        case th /*7602280*/:
                                            if (kid.is(R.xml.kbd_qwerty)) {
                                                j = 11;
                                                k = 23;
                                                break;
                                            }
                                            break;
                                        default:
                                            if (kid.is(R.xml.kbd_neo) || kid.is(R.xml.kbd_bepo)) {
                                                j = 11;
                                                k = 23;
                                                break;
                                            }
                                    }
                                }
                            }
                            return 0;
                            break;
                    }
                }
                k = 14;
                j = 0;
                l = 28 + 1;
            } else {
                j = 14;
                l = 2;
                k = 28 + 2;
            }
            if (c == 0) {
                if (i >= l && i < c3.length + l) {
                    c = c3[i - l];
                } else if (i >= k && i < c2.length + k) {
                    c = c2[i - k];
                } else if (i >= j && i < c1.length + j) {
                    c = c1[i - j];
                }
            }
            if (c == 0 && i == 10 && mLC != 7667834) {
                c = cyr ? 1105 : '^';
            }
            if (c == 0) {
                return 0;
            }
            return toPunc(c);
        }
    }

    private static char bohi(int c) {
        if (klBO == 1) {
            switch (c) {
                case 3919:
                    return 3914;
                case 3920:
                    return 3915;
                case 3921:
                    return 3916;
                case 3923:
                    return 3918;
                case 3940:
                    return 3941;
            }
        }
        switch (c) {
            case 3904:
                return 3905;
            case 3909:
                return 3910;
            case 3919:
                return 3914;
            case 3920:
            case 3956:
                return 3915;
            case 3921:
                return 3916;
            case 3923:
                return 3918;
            case 3924:
                return 3925;
            case 3926:
                return 3913;
            case 3929:
                return 3930;
            case 3933:
                return 4013;
            case 3935:
                return 3931;
            case 3937:
                return 3920;
            case 3940:
                return 3941;
            case 3942:
                return 3934;
            case 3944:
                return 4024;
            case 3954:
                return 3968;
            case 3962:
                return 3963;
            case 3964:
                return 3965;
        }
        return 0;
    }

    private static int adj(int i, int[] p) {
        int j = p[0] - 1;
        if (i <= j) {
            return i;
        }
        j += p[1];
        if (i <= j) {
            return -1;
        }
        j += p[2] + p[3];
        if (i <= j) {
            return i - p[1];
        }
        j += p[4];
        if (i <= j) {
            return -1;
        }
        j += p[5];
        if (i <= j) {
            return i - (p[1] + p[4]);
        }
        j += p[6];
        if (i <= j) {
            return -1;
        }
        j += p[7];
        if (i > j) {
            return i > j + p[8] ? i - (((p[1] + p[4]) + p[6]) + p[8]) : -1;
        } else {
            return i - ((p[1] + p[4]) + p[6]);
        }
    }

    public static void dicJa(Context c) {
        try {
            if (dicJa == null) {
                dicJa = new CC("content://klye.plugin.ja.dict", new String[]{"_id", "word"});
            }
            dicJa.init(c);
        } catch (Throwable th) {
            String s = "日本語plugin not found. Please install it if you haven't done so";
            noti(c, "日本語plugin not found. Please install it if you haven't done so", hp("plugin.html"));
        }
    }

    public static void kid(KID id) {
        kid = id;
        isM = isM1();
    }

    private static boolean isM1() {
        switch (mLC) {
            case am /*6357101*/:
            case gz /*6750330*/:
            case ko /*7012463*/:
            case lo /*7077999*/:
            case ti /*7602281*/:
                return false;
            default:
                if (dvn || kid == null) {
                    return false;
                }
                return kid.is(R.xml.kbd_neo) || kid.is(R.xml.kbd_bepo) || kid.is(R.xml.kbd_ar) || kid.is(R.xml.kbd_lt) || kid.is(R.xml.kbd_bo) || kid.is(R.xml.kbd_cyph) || kid.is(R.xml.kbd_colemak) || kid.is(R.xml.kbd_iw) || kid.is(R.xml.kbd_dvorak) || kid.is(R.xml.kbd_big) || kid.isSplit() || ((kid.is(R.xml.kbd_qw_er_ty) && cy() != -1) || ((kid.is(R.xml.kbd_t9) && mLC == th) || kid.is(R.xml.kbd_qwerty)));
        }
    }

    public static boolean isAK() {
        if (kid != null) {
            switch (kid.mXml) {
                case R.xml.kbd_ar:
                case R.xml.kbd_bepo:
                case R.xml.kbd_big:
                case R.xml.kbd_big_split:
                case R.xml.kbd_bo:
                case R.xml.kbd_clmks:
                case R.xml.kbd_colemak:
                case R.xml.kbd_cyph:
                case R.xml.kbd_dvorak:
                case R.xml.kbd_dvrks:
                case R.xml.kbd_iw:
                case R.xml.kbd_lh:
                case R.xml.kbd_lt:
                case R.xml.kbd_neo:
                case R.xml.kbd_qw_er_ty:
                case R.xml.kbd_qwerty:
                case R.xml.kbd_rh:
                case R.xml.kbd_si:
                case R.xml.kbd_split:
                case R.xml.kbd_t9:
                case R.xml.kofull:
                    return true;
            }
        }
        return false;
    }

    public static boolean nfs(String pn) {
        boolean z = true;
        if (na(pn, new String[]{"com.android.browser", "com.android.chrome", "com.tencent.", "mobi.mgeek.", "com.android.email", "com.evernote"})) {
            z = false;
        }
        nfs = z;
        return nfs;
    }

    public static boolean nb(String pn) {
        return na(pn, new String[]{"com.android.browser", "com.tencent.", "mobi.mgeek.", "com.android.email"});
    }

    public static boolean na(String pn) {
        me = pn.indexOf("com.klye.") != -1;
        if (pn.indexOf("kl.myscript") != -1) {
            xtf = true;
            xtf(mIme);
            rmb("ms", xtf);
        }
        oo = na(pn, nsa.split(","));
        return oo;
    }

    public static boolean na(String pn, String[] bl) {
        boolean b;
        int i = 0;
        while (true) {
            int i2 = i + 1;
            b = pn.indexOf(bl[i]) == -1;
            if (!b || i2 >= bl.length) {
                return b;
            }
            i = i2;
        }
        return b;
    }

    public static void rtlv(View v) {
        try {
            tv = new TextView(mIme);
            pw = new PopupWindow(mIme);
            tv.setPadding(0, 0, 0, 0);
            pw.setWidth(480);
            pw.setHeight(100);
            tv.setLayoutParams(new LayoutParams(-2, -2));
            tv.setGravity(17);
            tv.setVisibility(8);
            pw.setContentView(tv);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    public static void rtld(Canvas canvas, CharSequence s, int x, int y, int wordWidth, int height, Paint paint) {
        try {
            if (tv == null) {
                rtlv(iv);
            }
            tv.setText(s);
            tv.setTextColor(paint.getColor());
            tv.setTypeface(paint.getTypeface());
            tv.setTextSize(0, paint.getTextSize());
            tv.layout(0, 0, wordWidth, height);
            canvas.translate((float) x, (float) y);
            tv.draw(canvas);
            canvas.translate((float) (-x), (float) (-y));
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    public static String yif(CharSequence s) {
        return s.toString().replace("בֿ", "בֿ").replace("פֿ", "פֿ").replace("ײַ", "ײַ");
    }

    public static boolean rdd() {
        switch (mLC) {
            case cr /*6488178*/:
            case en /*6619246*/:
            case iu /*6881397*/:
            case oj /*7274602*/:
                return true;
            default:
                return false;
        }
    }

    public static String xg() {
        switch (mLC) {
            case az /*6357114*/:
            case tr /*7602290*/:
                return "ğ";
            case lv /*7078006*/:
                return "ģ";
            default:
                return "";
        }
    }

    public static String xz() {
        switch (mLC) {
            case bs /*6422643*/:
            case cs /*6488179*/:
            case et /*6619252*/:
            case hr /*6815858*/:
            case lt /*7078004*/:
            case lv /*7078006*/:
            case sr1 /*7536689*/:
            case sk /*7536747*/:
            case sl /*7536748*/:
                return "ž";
            case pl /*7340140*/:
                return "ż";
            case we /*7798885*/:
                return "žź";
            default:
                return "";
        }
    }

    static final void dicParam() {
        FULL_WORD_FREQ_MULTIPLIER = 2;
        TYPED_LETTER_MULTIPLIER = 2;
        if (zhType() != -1 || mLC == ja) {
            switch (zhType()) {
                case 67:
                    TYPED_LETTER_MULTIPLIER = 4;
                    FULL_WORD_FREQ_MULTIPLIER = 1;
                    return;
                case 90:
                    TYPED_LETTER_MULTIPLIER = 6;
                    FULL_WORD_FREQ_MULTIPLIER = 5;
                    return;
                default:
                    TYPED_LETTER_MULTIPLIER = 3;
                    FULL_WORD_FREQ_MULTIPLIER = 1;
                    return;
            }
        } else if (!fk() || arConAble()) {
            TYPED_LETTER_MULTIPLIER = 1;
            FULL_WORD_FREQ_MULTIPLIER = 3;
        }
    }

    public static boolean ncu(int pc) {
        switch (pc) {
            case 39:
            case 42:
            case 43:
            case 45:
            case 714:
                return true;
            default:
                return false;
        }
    }

    public static boolean ned() {
        switch (mLC) {
            case ag /*6357095*/:
            case am /*6357101*/:
            case as /*6357107*/:
            case aw /*6357111*/:
            case bb /*6422626*/:
            case bh /*6422632*/:
            case bn /*6422638*/:
            case bp /*6422640*/:
            case ck /*6488171*/:
            case dv /*6553718*/:
            case el /*6619244*/:
            case gu /*6750325*/:
            case gz /*6750330*/:
            case hi /*6815849*/:
            case hw /*6815863*/:
            case hy /*6815865*/:
            case km /*7012461*/:
            case kn /*7012462*/:
            case lo /*7077999*/:
            case ml /*7143532*/:
            case mm /*7143533*/:
            case mr /*7143538*/:
            case mx /*7143544*/:
            case my /*7143545*/:
            case ne /*7209061*/:
            case or /*7274610*/:
            case pa /*7340129*/:
            case pc /*7340131*/:
            case sa /*7536737*/:
            case si /*7536745*/:
            case ta /*7602273*/:
            case te /*7602277*/:
            case ti /*7602281*/:
            case yi /*7929961*/:
            case yo /*7929967*/:
                return true;
            case az /*6357114*/:
                return g233();
            case ro /*7471215*/:
                return roST;
            default:
                if (in || zt == 66 || cyr || arConAble()) {
                    return true;
                }
                return false;
        }
    }

    public static void dw(InputConnection ic) {
        try {
            ic.finishComposingText();
            String s = ic.getTextBeforeCursor(200, 0).toString();
            int l = s.length();
            if (l != 0) {
                ic.deleteSurroundingText((l - s.lastIndexOf(32, s.charAt(l + -1) == ' ' ? l - 2 : l - 1)) - 1, 0);
            }
        } catch (Throwable th) {
        }
    }

    public static boolean nd() {
        return mLC == da || mLC == nb || mLC == nn;
    }

    public static boolean t9c(char c) {
        switch (c) {
            case '*':
            case '+':
            case '-':
                return true;
            default:
                return irn(c);
        }
    }

    public static boolean mt9() {
        return (mt9 == 0 && !mIme.hs()) || mt9 == 2;
    }

    public static void sbf(Paint paint, boolean bf, char c) {
        if (nb(c)) {
            paint.setFakeBoldText(bf);
            paint.setTypeface(Typeface.DEFAULT);
        } else {
            paint.setFakeBoldText(false);
            paint.setTypeface(bf ? Typeface.DEFAULT_BOLD : Typeface.DEFAULT);
        }
        Typeface tf = xtf(c);
        if (tf != null) {
            paint.setTypeface(tf);
        }
    }

    public static CharSequence fd(CharSequence cs) {
        StringBuilder s = new StringBuilder();
        int l = cs.length();
        for (int i = 0; i < l; i++) {
            s.append(fd(cs.charAt(i)));
        }
        return s;
    }

    static char fd(char c) {
        switch (c) {
            case 'B':
                return 946;
            case 'C':
                return 8834;
            case 'E':
                return 958;
            case 'I':
                return 3652;
            case 'J':
                return 9833;
            case 'L':
                return 163;
            case 'N':
                return 1048;
            case 'O':
                return 920;
            case 'P':
                return 8472;
            case 'R':
                return 1071;
            case 'S':
                return 8747;
            case 'T':
                return 12636;
            case 'U':
                return 1062;
            case 'V':
                return 3611;
            case 'W':
                return 3615;
            case 'X':
                return 1078;
            case 'Y':
                return 1059;
            case 'Z':
                return 950;
            case 'a':
                return 945;
            case 'b':
                return 1074;
            case 'c':
                return 962;
            case 'd':
                return 948;
            case 'e':
                return 949;
            case 'f':
                return 402;
            case 'h':
                return 1085;
            case 'i':
                return 239;
            case 'k':
                return 1082;
            case 'l':
                return 8467;
            case 'm':
                return 3667;
            case 'n':
                return 3607;
            case 'o':
                return 3665;
            case 'p':
                return 961;
            case BuildConfig.VERSION_CODE /*114*/:
                return 1103;
            case 's':
                return 3619;
            case 't':
                return 1090;
            case 'u':
                return 3610;
            case 'v':
                return 957;
            case 'w':
                return 3614;
            case 'x':
                return 967;
            case 'y':
                return 947;
            default:
                return c;
        }
    }

    public static Typeface xtf(char c) {
        if (xtf) {
            try {
                if (in(c) || am(c) || mm(c)) {
                    if (tf2 == null) {
                        tf2 = gf("fonts/in.ttf");
                    }
                    return tf2;
                } else if (by(c) || my(c) || su(c) || ugi(c) || isAr(c) || dv(c) || isRTL(c) || th(c)) {
                    if (tf1 == null) {
                        tf1 = gf("fonts/iwArTh.ttf");
                    }
                    return tf1;
                } else if ((c > 1328 && c < 11904) || ii(c) || mnf) {
                    if (tf3 == null) {
                        tf3 = gf("fonts/djv.ttf");
                    }
                    return tf3;
                }
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public static void stf(Paint p, char c) {
        Typeface tf = xtf(c);
        if (tf != null) {
            p.setTypeface(tf);
        }
    }

    public static void trans(Context c, String et) {
        String turl = "http://multi-lingo.appspot.com/t/1.html?";
        if (!cm()) {
            adi(c);
        }
        sa(c, wp(turl + et.replace("?", "%3F").replace("\n", "%0d")));
    }

    public static boolean ir(int a, int b, int c) {
        return ir((char) a, (char) b, (char) c);
    }

    public static char ha(char c) {
        switch (c) {
            case 228:
                return 599;
            case 229:
                return 436;
            case 245:
                return 595;
            case 246:
                return 409;
            default:
                return 0;
        }
    }

    public static char bb(char c) {
        switch (c) {
            case '-':
                return 183;
            case '/':
                return '`';
            case ';':
                if (azertyC) {
                    return 'm';
                }
                return 611;
            case '?':
                return 711;
            case '[':
                return 603;
            case ']':
                return '-';
            case 'm':
                if (azertyC) {
                    return 611;
                }
                break;
        }
        return 0;
    }

    public static char yo(char c) {
        switch (c) {
            case 228:
                return 769;
            case 229:
                return 803;
            case 245:
                return '\'';
            case 246:
                return 768;
            default:
                return 0;
        }
    }

    public static char is(char c) {
        switch (c) {
            case 228:
                return 180;
            case 229:
                return 240;
            case 245:
                return 254;
            case 246:
                return 230;
            default:
                return 0;
        }
    }

    public static char ig(char c) {
        switch (c) {
            case '\'':
            case 246:
                return 7883;
            case 228:
                return 7909;
            case 229:
                return 7885;
            case 245:
                return 7749;
            default:
                return 0;
        }
    }

    public static char s4(char c) {
        switch (c) {
            case 228:
                return '_';
            case 229:
                return '-';
            case 245:
                return '/';
            case 246:
                return '\'';
            default:
                return 0;
        }
    }

    public static char cy(char c) {
        switch (c) {
            case 228:
                return 180;
            case 229:
                return '^';
            case 245:
                return 168;
            case 246:
                return '`';
            default:
                return 0;
        }
    }

    public static char ku(char c) {
        switch (c) {
            case 228:
                return '\'';
            case 229:
                return '^';
            case 245:
                return 168;
            case 246:
                return 184;
            default:
                return 0;
        }
    }

    public static char tk(char c) {
        switch (c) {
            case 'c':
                return 231;
            case 'q':
                return 228;
            case 'v':
                return 253;
            case 'x':
                return 382;
            case 228:
                return 252;
            case 229:
                return 246;
            case 245:
                return 328;
            case 246:
                return 351;
            default:
                return 0;
        }
    }

    public static char tf(char c) {
        switch (c) {
            case '\'':
                return 351;
            case '-':
                return 'b';
            case '/':
                return 'x';
            case ';':
                return 'y';
            case '?':
                return '\'';
            case '[':
                return 'q';
            case ']':
                return 'w';
            default:
                return HKM.m(9, c);
        }
    }

    public static char mv(char c) {
        switch (c) {
            case 228:
                return '\'';
            case 229:
                return 275;
            case 245:
                return 8217;
            case 246:
                return 230;
            default:
                return 0;
        }
    }

    public static char m1(char c) {
        switch (c) {
            case 228:
                return 230;
            case 229:
                return 235;
            case 245:
                return 8217;
            default:
                return 0;
        }
    }

    public static char ch(char c) {
        switch (c) {
            case 228:
                return '\'';
            case 229:
                return 8217;
            case 245:
                return 241;
            case 246:
                return 229;
            default:
                return 0;
        }
    }

    public static char ee(char c) {
        switch (c) {
            case 'c':
                return 651;
            case 'j':
                return 331;
            case 'q':
                return 611;
            case 228:
                return 603;
            case 229:
                return 596;
            case 245:
                return 598;
            case 246:
                return 402;
            default:
                return 0;
        }
    }

    public static char cw(char c) {
        switch (c) {
            case 228:
                return mLC == cw ? 711 : 700;
            case 229:
                return 714;
            case 245:
                return 808;
            case 246:
                return 322;
            default:
                return 0;
        }
    }

    public static char lv(char c) {
        switch (c) {
            case '\'':
            case 229:
                return 711;
            case 228:
                return 311;
            case 245:
                return 326;
            case 246:
                return 316;
            default:
                return 0;
        }
    }

    public static char wo(char c) {
        switch (c) {
            case '\'':
            case 246:
                return 241;
            case 228:
                return '`';
            case 229:
                return 714;
            case 245:
                return 331;
            default:
                return 0;
        }
    }

    public static char ac(char c) {
        switch (c) {
            case 228:
                return 235;
            case 229:
                return 232;
            case 245:
                return 244;
            default:
                return 0;
        }
    }

    public static char bg(char c) {
        switch (klbg) {
            case 2:
                return bg2(c);
            case 3:
                return bg3(c);
            default:
                return bg1(c);
        }
    }

    public static char bg1(char c) {
        switch (c) {
            case '\'':
                return 1095;
            case '-':
                return 1088;
            case '/':
                return 1073;
            case ';':
                return 1084;
            case '?':
                return 1083;
            case '[':
                return 1094;
            case ']':
                return '\'';
            default:
                return HKM.m(10, c);
        }
    }

    public static char bg2(char c) {
        switch (c) {
            case 'h':
                return 1093;
            case 'v':
                return 1078;
            case 'w':
                return 1074;
            case 'x':
                return 1100;
            case 'y':
                return 1098;
            case 228:
                return 1097;
            case 229:
                return 1096;
            case 245:
                return 1102;
            case 246:
                return 1095;
            default:
                return HKM.m(1, c);
        }
    }

    public static char bg3(char c) {
        switch (c) {
            case 'h':
                return 1093;
            case 'q':
                return 1095;
            case 'x':
                return 1078;
            case 'y':
                return 1098;
            case 228:
                return 1097;
            case 229:
                return 1103;
            case 245:
                return 1102;
            case 246:
                return 1100;
            default:
                return HKM.m(1, c);
        }
    }

    public static char jv(char c) {
        switch (c) {
            case 228:
                return '-';
            case 229:
                return 232;
            case 245:
                return '\'';
            case 246:
                return 233;
            default:
                return 0;
        }
    }

    public static char lt(char c) {
        if (ll == 0) {
            switch (c) {
                case ']':
                    return 'w';
                case 'f':
                    return 353;
                case 'q':
                    return 261;
                case 'w':
                    return 382;
                case 'x':
                    return 363;
            }
        }
        switch (c) {
            case '\'':
                return 279;
            case '-':
            case 245:
                return 269;
            case '/':
            case 228:
                return 281;
            case ';':
            case 246:
                return 371;
            case '?':
                if (ll == 0) {
                    return 'f';
                }
                return 353;
            case '[':
            case 229:
                return 303;
            case ']':
                return 261;
            default:
                return 0;
        }
    }

    public static char az(char c) {
        if (ll == 0) {
            switch (c) {
                case 'w':
                    return 252;
            }
        }
        switch (c) {
            case '\'':
                return 1241;
            case '-':
                return 231;
            case '/':
                return ll == 0 ? 'w' : 252;
            case ';':
                return 305;
            case '?':
                return 351;
            case '[':
                return 246;
            case ']':
                return 287;
            default:
                return 0;
        }
    }

    static void alt(Key k2, String s) {
        k2.popupCharacters = s;
        k2.popupResId = R.xml.kbd_popup_template;
    }

    public static char nm(Key k2, char c, int i, int j) {
        char c1 = HKM.m(j, c);
        if (c1 != 0) {
            String s = Character.toString(c1);
            switch (c1) {
                case 11572:
                    s = s + 11573;
                    break;
                case 11621:
                    s = s + 11620;
                    break;
            }
            alt(k2, s);
        }
        switch (i) {
            case 5:
                switch (c) {
                    case 'p':
                        return 1700;
                    case 'x':
                        return 1582;
                }
                break;
            case 7:
                switch (c) {
                    case '\'':
                        alt(k2, "ⴺ");
                        return 11577;
                }
                break;
            case R.styleable.LatinKeyboardBaseView_backgroundDimAmount /*13*/:
                c1 = my(k2, c);
                if (c1 != 0) {
                    return c1;
                }
                break;
            case R.styleable.LatinKeyboardBaseView_symbolColorScheme /*15*/:
                c1 = b2(k2, c);
                if (c1 != 0) {
                    return c1;
                }
                break;
        }
        return HKM.m(i, c);
    }

    private static char my(Key k2, char c) {
        switch (c) {
            case '\'':
                alt(k2, "ဓ");
                return 4114;
            case '-':
                alt(k2, "ဎ");
                return 4122;
            case '/':
                alt(k2, "။");
                return 4170;
            case ';':
                alt(k2, "ဴ");
                return 4152;
            case '?':
                alt(k2, "ဦ");
                return 4104;
            case '[':
                alt(k2, "ဿ");
                return 4127;
            case ']':
                alt(k2, "ဪ");
                return 4137;
            default:
                return 0;
        }
    }

    private static char b2(Key k2, char c) {
        switch (c) {
            case 228:
                return 6687;
            case 229:
                return 6670;
            case 245:
                return 6686;
            case 246:
                return 6663;
            default:
                return 0;
        }
    }

    public static char dn(char c) {
        switch (c) {
            case 'f':
                return 611;
            case 'q':
                return 331;
            case 's':
                return 603;
            case 'x':
                return 596;
            case 'z':
                return 776;
            default:
                return 0;
        }
    }

    /* JADX WARNING: Missing block: B:4:0x000e, code:
            if (r0 == false) goto L_0x0010;
     */
    /* JADX WARNING: Missing block: B:6:0x0013, code:
            if (r0 == false) goto L_0x0015;
     */
    /* JADX WARNING: Missing block: B:10:?, code:
            return 0;
     */
    /* JADX WARNING: Missing block: B:12:?, code:
            return 240;
     */
    /* JADX WARNING: Missing block: B:13:?, code:
            return 254;
     */
    public static char ls(char r2) {
        /*
        r1 = kid;
        r0 = r1.isBig();
        switch(r2) {
            case 111: goto L_0x000e;
            case 114: goto L_0x0013;
            case 118: goto L_0x001b;
            case 228: goto L_0x0015;
            case 229: goto L_0x000b;
            case 245: goto L_0x0018;
            case 246: goto L_0x0010;
            default: goto L_0x0009;
        };
    L_0x0009:
        r1 = 0;
    L_0x000a:
        return r1;
    L_0x000b:
        r1 = 39;
        goto L_0x000a;
    L_0x000e:
        if (r0 != 0) goto L_0x0009;
    L_0x0010:
        r1 = 240; // 0xf0 float:3.36E-43 double:1.186E-321;
        goto L_0x000a;
    L_0x0013:
        if (r0 != 0) goto L_0x0009;
    L_0x0015:
        r1 = 254; // 0xfe float:3.56E-43 double:1.255E-321;
        goto L_0x000a;
    L_0x0018:
        r1 = 8730; // 0x221a float:1.2233E-41 double:4.313E-320;
        goto L_0x000a;
    L_0x001b:
        r1 = 711; // 0x2c7 float:9.96E-43 double:3.513E-321;
        goto L_0x000a;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.klye.ime.latin.M.ls(char):char");
    }

    public static char tr(char c) {
        boolean b = kid.is(R.xml.kbd_lt);
        switch (c) {
            case '\'':
                if (b) {
                    return 'i';
                }
                return 305;
            case '-':
                return 246;
            case '/':
                return '\'';
            case ';':
                return 351;
            case '?':
                return 231;
            case '[':
                return 287;
            case ']':
                return 252;
            case 'i':
                if (b) {
                    return 305;
                }
                break;
        }
        return 0;
    }

    public static char hy(char c) {
        boolean b = kid.is(R.xml.kbd_lt);
        switch (c) {
            case '\'':
                return 1411;
            case '-':
                return 1399;
            case '/':
                return 1390;
            case ';':
                return 1381;
            case '?':
                return 1389;
            case '[':
                return 1394;
            case ']':
                return 1397;
            case 'c':
                return 1401;
            case 'e':
                return 1384;
            case 'h':
                return 1383;
            case 'j':
                return 1385;
            case 'v':
                return 1386;
            case 'w':
                return 1406;
            case 'x':
                return 1382;
            case 'z':
                return 1409;
            default:
                char c1 = HKM.m(2, c);
                return c1 == c ? 0 : c1;
        }
    }

    public static void a5t(ArrayList<CharSequence> s, String w) {
        w = w.toLowerCase().replace('v', 252);
        CharSequence t = w;
        for (int i = 1; i <= 5 && t != null; i++) {
            s.add(t);
            t = w + i;
        }
    }

    static String pyt(String w) {
        int l = w.length();
        return pyt(w.substring(0, l - 1), w.charAt(l - 1) - 48);
    }

    private static String pyt(String w, int t) {
        int p = pyfv(w);
        return p == -1 ? w : w.substring(0, p) + pyt(t, w.charAt(p)) + w.substring(p + 1);
    }

    public static boolean pyt(CharSequence s, int l) {
        return zt == 80 && irPY(s.charAt(0)) && irn(s.charAt(l - 1));
    }

    private static int pyfv(String w) {
        int l = w.length() - 1;
        int i = l;
        while (i >= 0 && pyfv(w.charAt(i), "ae")) {
            i--;
        }
        if (i >= 0) {
            return i;
        }
        i = l;
        while (i >= 0 && pyfv(w.charAt(i), "o")) {
            i--;
        }
        if (i >= 0) {
            return i;
        }
        i = l;
        while (i >= 0 && pyfv(w.charAt(i), "iuü")) {
            i--;
        }
        return i;
    }

    private static boolean pyfv(char c, String s) {
        return s.indexOf(c) == -1;
    }

    private static char pyt(int t, char c) {
        switch (t) {
            case 1:
                return pyt1(c);
            case 2:
                return viTf(c);
            case 3:
                return pyt3(c);
            case 4:
                return viTs(c);
            default:
                return 0;
        }
    }

    private static char pyt3(char c) {
        switch (c) {
            case 'a':
                return 462;
            case 'e':
                return 283;
            case 'i':
                return 464;
            case 'o':
                return 466;
            case 'u':
                return 468;
            case 252:
                return 474;
            default:
                return 0;
        }
    }

    private static char pyt1(char c) {
        switch (c) {
            case 'a':
                return 257;
            case 'e':
                return 275;
            case 'i':
                return 299;
            case 'o':
                return 333;
            case 'u':
                return 363;
            case 252:
                return 470;
            default:
                return 0;
        }
    }

    static char viTone(char t, char c) {
        switch (t) {
            case 'f':
                return viTf(c);
            case 'j':
                return viTj(c);
            case BuildConfig.VERSION_CODE /*114*/:
                return viTr(c);
            case 's':
                return viTs(c);
            case 'x':
                return viTx(c);
            default:
                return c;
        }
    }

    private static char viTs(char c) {
        switch (c) {
            case 'a':
                return 225;
            case 'e':
                return 233;
            case 'i':
                return 237;
            case 'o':
                return 243;
            case 'u':
                return 250;
            case 'y':
                return 253;
            case 226:
                return 7845;
            case 234:
                return 7871;
            case 244:
                return 7889;
            case 252:
                return 472;
            case 259:
                return 7855;
            case 417:
                return 7899;
            case 432:
                return 7913;
            default:
                return 0;
        }
    }

    private static char viTr(char c) {
        switch (c) {
            case 'a':
                return 7843;
            case 'e':
                return 7867;
            case 'i':
                return 7881;
            case 'o':
                return 7887;
            case 'u':
                return 7911;
            case 'y':
                return 7927;
            case 226:
                return 7849;
            case 234:
                return 7875;
            case 244:
                return 7893;
            case 259:
                return 7859;
            case 417:
                return 7903;
            case 432:
                return 7917;
            default:
                return 0;
        }
    }

    private static char viTx(char c) {
        switch (c) {
            case 'a':
                return 227;
            case 'e':
                return 7869;
            case 'i':
                return 297;
            case 'o':
                return 245;
            case 'u':
                return 361;
            case 'y':
                return 7929;
            case 226:
                return 7851;
            case 234:
                return 7877;
            case 244:
                return 7895;
            case 259:
                return 7861;
            case 417:
                return 7905;
            case 432:
                return 7919;
            default:
                return 0;
        }
    }

    private static char viTj(char c) {
        switch (c) {
            case 'a':
                return 7841;
            case 'e':
                return 7865;
            case 'i':
                return 7883;
            case 'o':
                return 7885;
            case 'u':
                return 7909;
            case 'y':
                return 7925;
            case 226:
                return 7853;
            case 234:
                return 7879;
            case 244:
                return 7897;
            case 259:
                return 7863;
            case 417:
                return 7907;
            case 432:
                return 7921;
            default:
                return 0;
        }
    }

    private static char viTf(char c) {
        switch (c) {
            case 'a':
                return 224;
            case 'e':
                return 232;
            case 'i':
                return 236;
            case 'o':
                return 242;
            case 'u':
                return 249;
            case 'y':
                return 7923;
            case 226:
                return 7847;
            case 234:
                return 7873;
            case 244:
                return 7891;
            case 252:
                return 476;
            case 259:
                return 7857;
            case 417:
                return 7901;
            case 432:
                return 7915;
            default:
                return 0;
        }
    }

    public static char ethd(char c) {
        switch (c) {
            case 353:
                return 223;
            case 7717:
                return 253;
            case 7779:
                return 231;
            case 7789:
                return 'T';
            default:
                return c;
        }
    }

    public static char eth(char c) {
        switch (c) {
            case '\'':
            case 228:
                return 248;
            case 229:
                return 254;
            case 245:
                return 241;
            case 246:
                return 231;
            default:
                return 0;
        }
    }

    public static char am1(char c) {
        c = ethd(c);
        if (!ev(c)) {
            return am(c, '1');
        }
        char lc = mIme.mPredicting ? mIme.mWord.lastChar() : 0;
        lc = (lc == 0 || ev(lc)) ? ' ' : am(lc, c);
        return lc < 256 ? ' ' : lc;
    }

    static boolean ev(char c) {
        switch (Character.toLowerCase(c)) {
            case 'a':
            case 'e':
            case 'i':
            case 'o':
            case 'u':
            case 248:
                return true;
            default:
                return false;
        }
    }

    public static char am(char c, char c2) {
        switch (c2) {
            case 'W':
                c2 = 'U';
                break;
            case 216:
                c2 = 'O';
                break;
        }
        switch (c) {
            case '\'':
            case 'X':
                switch (c2) {
                    case '1':
                        return 4816;
                    case 'a':
                        return 4819;
                    case 'e':
                        return 4821;
                    case 'i':
                        return 4818;
                    case 'o':
                        return 4822;
                    case 'u':
                        return 4817;
                    case 248:
                        return 4820;
                    default:
                        return c;
                }
            case '-':
            case 'x':
                switch (c2) {
                    case '1':
                        return 4768;
                    case 'A':
                        return 4775;
                    case 'a':
                        return 4771;
                    case 'e':
                        return 4773;
                    case 'i':
                        return 4770;
                    case 'o':
                        return 4774;
                    case 'u':
                        return 4769;
                    case 248:
                        return 4772;
                    default:
                        return c;
                }
            case 'B':
            case 199:
                switch (c2) {
                    case '1':
                        return 4920;
                    case 'A':
                        return 4927;
                    case 'a':
                        return 4923;
                    case 'e':
                        return 4925;
                    case 'i':
                        return 4922;
                    case 'o':
                        return 4926;
                    case 'u':
                        return 4921;
                    case 248:
                        return 4924;
                    default:
                        return c;
                }
            case 'C':
                switch (c2) {
                    case '1':
                        return 4728;
                    case 'A':
                        return 4735;
                    case 'a':
                        return 4731;
                    case 'e':
                        return 4733;
                    case 'i':
                        return 4730;
                    case 'o':
                        return 4734;
                    case 'u':
                        return 4729;
                    case 248:
                        return 4732;
                    default:
                        return c;
                }
            case 'D':
                switch (c2) {
                    case '1':
                        return 4856;
                    case 'A':
                        return 4863;
                    case 'a':
                        return 4859;
                    case 'e':
                        return 4861;
                    case 'i':
                        return 4858;
                    case 'o':
                        return 4862;
                    case 'u':
                        return 4857;
                    case 248:
                        return 4860;
                    default:
                        return c;
                }
            case 'F':
            case 'M':
            case 'R':
            case 'W':
            case 'Y':
            case 209:
            case 222:
            case 352:
            case 7778:
            case 7788:
                return ' ';
            case 'G':
                switch (c2) {
                    case '1':
                        return 4888;
                    case 'A':
                        return 4895;
                    case 'E':
                        return 11669;
                    case 'I':
                        return 11668;
                    case 'O':
                        return 11670;
                    case 'U':
                        return 11667;
                    case 'a':
                        return 4891;
                    case 'e':
                        return 4893;
                    case 'i':
                        return 4890;
                    case 'o':
                        return 4894;
                    case 'u':
                        return 4889;
                    case 248:
                        return 4892;
                    default:
                        return c;
                }
            case 'H':
                switch (c2) {
                    case '1':
                        return 4624;
                    case 'A':
                        return 4631;
                    case 'a':
                        return 4627;
                    case 'e':
                        return 4629;
                    case 'i':
                        return 4626;
                    case 'o':
                        return 4630;
                    case 'u':
                        return 4625;
                    case 248:
                        return 4628;
                    default:
                        return c;
                }
            case 'J':
                switch (c2) {
                    case '1':
                        return 4736;
                    case 'A':
                        return 4747;
                    case 'E':
                        return 4748;
                    case 'I':
                        return 4746;
                    case 'O':
                        return 4749;
                    case 'U':
                        return 4744;
                    case 'a':
                        return 4739;
                    case 'e':
                        return 4741;
                    case 'i':
                        return 4738;
                    case 'o':
                        return 4742;
                    case 'u':
                        return 4737;
                    case 248:
                        return 4740;
                    default:
                        return c;
                }
            case 'K':
                switch (c2) {
                    case '1':
                        return 4792;
                    case 'A':
                        return 4803;
                    case 'E':
                        return 4804;
                    case 'I':
                        return 4802;
                    case 'O':
                        return 4805;
                    case 'U':
                        return 4800;
                    case 'a':
                        return 4795;
                    case 'e':
                        return 4797;
                    case 'i':
                        return 4794;
                    case 'o':
                        return 4798;
                    case 'u':
                        return 4793;
                    case 248:
                        return 4796;
                    default:
                        return c;
                }
            case 'L':
            case 231:
                switch (c2) {
                    case '1':
                        return 4928;
                    case 'A':
                        return 4935;
                    case 'a':
                        return 4931;
                    case 'e':
                        return 4933;
                    case 'i':
                        return 4930;
                    case 'o':
                        return 4934;
                    case 'u':
                        return 4929;
                    case 248:
                        return 4932;
                    default:
                        return c;
                }
            case 'N':
            case 241:
                switch (c2) {
                    case '1':
                        return 4760;
                    case 'A':
                        return 4767;
                    case 'a':
                        return 4763;
                    case 'e':
                        return 4765;
                    case 'i':
                        return 4762;
                    case 'o':
                        return 4766;
                    case 'u':
                        return 4761;
                    case 248:
                        return 4764;
                    default:
                        return c;
                }
            case 'P':
            case 254:
                switch (c2) {
                    case '1':
                        return 4912;
                    case 'A':
                        return 4919;
                    case 'a':
                        return 4915;
                    case 'e':
                        return 4917;
                    case 'i':
                        return 4914;
                    case 'o':
                        return 4918;
                    case 'u':
                        return 4913;
                    case 248:
                        return 4916;
                    default:
                        return c;
                }
            case 'Q':
                switch (c2) {
                    case '1':
                        return 4688;
                    case 'E':
                        return 4700;
                    case 'I':
                        return 4699;
                    case 'O':
                        return 4698;
                    case 'U':
                        return 4696;
                    case 'a':
                        return 4691;
                    case 'e':
                        return 4693;
                    case 'i':
                        return 4690;
                    case 'o':
                        return 4694;
                    case 'u':
                        return 4689;
                    case 248:
                        return 4692;
                    default:
                        return c;
                }
            case 'S':
                switch (c2) {
                    case '1':
                        return 4640;
                    case 'A':
                        return 4647;
                    case 'a':
                        return 4643;
                    case 'e':
                        return 4645;
                    case 'i':
                        return 4642;
                    case 'o':
                        return 4646;
                    case 'u':
                        return 4641;
                    case 248:
                        return 4644;
                    default:
                        return c;
                }
            case 'T':
                switch (c2) {
                    case '1':
                        return 4896;
                    case 'A':
                        return 4903;
                    case 'a':
                        return 4899;
                    case 'e':
                        return 4901;
                    case 'i':
                        return 4898;
                    case 'o':
                        return 4902;
                    case 'u':
                        return 4897;
                    case 248:
                        return 4900;
                    default:
                        return c;
                }
            case 'V':
                switch (c2) {
                    case '1':
                        return 4712;
                    case 'A':
                        return 4719;
                    case 'a':
                        return 4715;
                    case 'e':
                        return 4717;
                    case 'i':
                        return 4714;
                    case 'o':
                        return 4718;
                    case 'u':
                        return 4713;
                    case 248:
                        return 4716;
                    default:
                        return c;
                }
            case 'Z':
                switch (c2) {
                    case '1':
                        return 4832;
                    case 'A':
                        return 4839;
                    case 'a':
                        return 4835;
                    case 'e':
                        return 4837;
                    case 'i':
                        return 4834;
                    case 'o':
                        return 4838;
                    case 'u':
                        return 4833;
                    case 248:
                        return 4836;
                    default:
                        return c;
                }
            case 'b':
                switch (c2) {
                    case '1':
                        return 4704;
                    case 'A':
                        return 4711;
                    case 'E':
                        return 4998;
                    case 'I':
                        return 4997;
                    case 'O':
                        return 4999;
                    case 'U':
                        return 4996;
                    case 'a':
                        return 4707;
                    case 'e':
                        return 4709;
                    case 'i':
                        return 4706;
                    case 'o':
                        return 4710;
                    case 'u':
                        return 4705;
                    case 248:
                        return 4708;
                    default:
                        return c;
                }
            case 'c':
                switch (c2) {
                    case '1':
                        return 4904;
                    case 'A':
                        return 4911;
                    case 'a':
                        return 4907;
                    case 'e':
                        return 4909;
                    case 'i':
                        return 4906;
                    case 'o':
                        return 4910;
                    case 'u':
                        return 4905;
                    case 248:
                        return 4908;
                    default:
                        return c;
                }
            case 'd':
                switch (c2) {
                    case '1':
                        return 4848;
                    case 'A':
                        return 4855;
                    case 'a':
                        return 4851;
                    case 'e':
                        return 4853;
                    case 'i':
                        return 4850;
                    case 'o':
                        return 4854;
                    case 'u':
                        return 4849;
                    case 248:
                        return 4852;
                    default:
                        return c;
                }
            case 'f':
                switch (c2) {
                    case '1':
                        return 4936;
                    case 'A':
                        return 4943;
                    case 'E':
                        return 5002;
                    case 'I':
                        return 5001;
                    case 'O':
                        return 5003;
                    case 'U':
                        return 5000;
                    case 'Y':
                        return 4954;
                    case 'a':
                        return 4939;
                    case 'e':
                        return 4941;
                    case 'i':
                        return 4938;
                    case 'o':
                        return 4942;
                    case 'u':
                        return 4937;
                    case 248:
                        return 4940;
                    default:
                        return c;
                }
            case 'g':
                switch (c2) {
                    case '1':
                        return 4872;
                    case 'A':
                        return 4883;
                    case 'E':
                        return 4884;
                    case 'I':
                        return 4882;
                    case 'O':
                        return 4885;
                    case 'U':
                        return 4880;
                    case 'a':
                        return 4875;
                    case 'e':
                        return 4877;
                    case 'i':
                        return 4874;
                    case 'o':
                        return 4878;
                    case 'u':
                        return 4873;
                    case 248:
                        return 4876;
                    default:
                        return c;
                }
            case 'h':
                switch (c2) {
                    case '1':
                        return 4608;
                    case 'A':
                        return 4615;
                    case 'a':
                        return 4611;
                    case 'e':
                        return 4613;
                    case 'i':
                        return 4610;
                    case 'o':
                        return 4614;
                    case 'u':
                        return 4609;
                    case 248:
                        return 4612;
                    default:
                        return c;
                }
            case 'j':
                switch (c2) {
                    case '1':
                        return 4864;
                    case 'A':
                        return 4871;
                    case 'a':
                        return 4867;
                    case 'e':
                        return 4869;
                    case 'i':
                        return 4866;
                    case 'o':
                        return 4870;
                    case 'u':
                        return 4865;
                    case 248:
                        return 4868;
                    default:
                        return c;
                }
            case 'k':
                switch (c2) {
                    case '1':
                        return 4776;
                    case 'A':
                        return 4787;
                    case 'E':
                        return 4788;
                    case 'I':
                        return 4786;
                    case 'O':
                        return 4789;
                    case 'U':
                        return 4784;
                    case 'a':
                        return 4779;
                    case 'e':
                        return 4781;
                    case 'i':
                        return 4778;
                    case 'o':
                        return 4782;
                    case 'u':
                        return 4777;
                    case 248:
                        return 4780;
                    default:
                        return c;
                }
            case 'l':
                switch (c2) {
                    case '1':
                        return 4616;
                    case 'A':
                        return 4623;
                    case 'a':
                        return 4619;
                    case 'e':
                        return 4621;
                    case 'i':
                        return 4618;
                    case 'o':
                        return 4622;
                    case 'u':
                        return 4617;
                    case 248:
                        return 4620;
                    default:
                        return c;
                }
            case 'm':
                switch (c2) {
                    case '1':
                        return 4632;
                    case 'A':
                        return 4639;
                    case 'E':
                        return 4994;
                    case 'I':
                        return 4993;
                    case 'O':
                        return 4995;
                    case 'U':
                        return 4992;
                    case 'Y':
                        return 4953;
                    case 'a':
                        return 4635;
                    case 'e':
                        return 4637;
                    case 'i':
                        return 4634;
                    case 'o':
                        return 4638;
                    case 'u':
                        return 4633;
                    case 248:
                        return 4636;
                    default:
                        return c;
                }
            case 'n':
                switch (c2) {
                    case '1':
                        return 4752;
                    case 'A':
                        return 4759;
                    case 'a':
                        return 4755;
                    case 'e':
                        return 4757;
                    case 'i':
                        return 4754;
                    case 'o':
                        return 4758;
                    case 'u':
                        return 4753;
                    case 248:
                        return 4756;
                    default:
                        return c;
                }
            case 'p':
                switch (c2) {
                    case '1':
                        return 4944;
                    case 'A':
                        return 4951;
                    case 'E':
                        return 5006;
                    case 'I':
                        return 5005;
                    case 'O':
                        return 5007;
                    case 'U':
                        return 5004;
                    case 'a':
                        return 4947;
                    case 'e':
                        return 4949;
                    case 'i':
                        return 4946;
                    case 'o':
                        return 4950;
                    case 'u':
                        return 4945;
                    case 248:
                        return 4948;
                    default:
                        return c;
                }
            case 'q':
                switch (c2) {
                    case '1':
                        return 4672;
                    case 'A':
                        return 4683;
                    case 'E':
                        return 4684;
                    case 'I':
                        return 4682;
                    case 'O':
                        return 4685;
                    case 'U':
                        return 4680;
                    case 'a':
                        return 4675;
                    case 'e':
                        return 4677;
                    case 'i':
                        return 4674;
                    case 'o':
                        return 4678;
                    case 'u':
                        return 4673;
                    case 248:
                        return 4676;
                    default:
                        return c;
                }
            case BuildConfig.VERSION_CODE /*114*/:
                switch (c2) {
                    case '1':
                        return 4648;
                    case 'A':
                        return 4655;
                    case 'Y':
                        return 4952;
                    case 'a':
                        return 4651;
                    case 'e':
                        return 4653;
                    case 'i':
                        return 4650;
                    case 'o':
                        return 4654;
                    case 'u':
                        return 4649;
                    case 248:
                        return 4652;
                    default:
                        return c;
                }
            case 's':
                switch (c2) {
                    case '1':
                        return 4656;
                    case 'A':
                        return 4663;
                    case 'a':
                        return 4659;
                    case 'e':
                        return 4661;
                    case 'i':
                        return 4658;
                    case 'o':
                        return 4662;
                    case 'u':
                        return 4657;
                    case 248:
                        return 4660;
                    default:
                        return c;
                }
            case 't':
                switch (c2) {
                    case '1':
                        return 4720;
                    case 'A':
                        return 4727;
                    case 'a':
                        return 4723;
                    case 'e':
                        return 4725;
                    case 'i':
                        return 4722;
                    case 'o':
                        return 4726;
                    case 'u':
                        return 4721;
                    case 248:
                        return 4724;
                    default:
                        return c;
                }
            case 'v':
                switch (c2) {
                    case '1':
                        return 4664;
                    case 'A':
                        return 4671;
                    case 'a':
                        return 4667;
                    case 'e':
                        return 4669;
                    case 'i':
                        return 4666;
                    case 'o':
                        return 4670;
                    case 'u':
                        return 4665;
                    case 248:
                        return 4668;
                    default:
                        return c;
                }
            case 'w':
                switch (c2) {
                    case '1':
                        return 4808;
                    case 'A':
                        return 4815;
                    case 'a':
                        return 4811;
                    case 'e':
                        return 4813;
                    case 'i':
                        return 4810;
                    case 'o':
                        return 4814;
                    case 'u':
                        return 4809;
                    case 248:
                        return 4812;
                    default:
                        return c;
                }
            case 'y':
                switch (c2) {
                    case '1':
                        return 4840;
                    case 'A':
                        return 4847;
                    case 'a':
                        return 4843;
                    case 'e':
                        return 4845;
                    case 'i':
                        return 4842;
                    case 'o':
                        return 4846;
                    case 'u':
                        return 4841;
                    case 248:
                        return 4844;
                    default:
                        return c;
                }
            case 'z':
                switch (c2) {
                    case '1':
                        return 4824;
                    case 'A':
                        return 4831;
                    case 'a':
                        return 4827;
                    case 'e':
                        return 4829;
                    case 'i':
                        return 4826;
                    case 'o':
                        return 4830;
                    case 'u':
                        return 4825;
                    case 248:
                        return 4828;
                    default:
                        return c;
                }
            default:
                return c;
        }
    }

    public static char ca(CharSequence cs2, int i, int l) {
        return i < l ? cs2.charAt(i) : 0;
    }

    public static String amIme(CharSequence cs) {
        StringBuilder s = new StringBuilder();
        int l = cs.length();
        int i = 0;
        while (i < l) {
            char c1 = ca(cs, i, l);
            char c2 = ca(cs, i + 1, l);
            char c4 = am(c1, c2);
            if (c4 == c1) {
                c4 = am(c2, ca(cs, i + 2, l));
                if (c4 != c2) {
                    char c5 = am(c1, c4);
                    if (c5 != c1) {
                        s.append(c5);
                    } else {
                        s.append(am1(c1));
                        s.append(c4);
                    }
                    i += 3;
                } else {
                    s.append(am1(c1));
                    i++;
                }
            } else {
                s.append(c4);
                i += 2;
            }
        }
        return s.toString();
    }

    public static String lsIme(CharSequence cs) {
        if (cs == null) {
            return null;
        }
        StringBuilder s = new StringBuilder();
        int l = cs.length();
        int i = 0;
        while (i < l) {
            char c1 = ca(cs, i, l);
            char c2 = ca(cs, i + 1, l);
            switch (c2) {
                case '\'':
                case 180:
                    switch (c1) {
                        case '\'':
                            c2 = '\'';
                            break;
                        case 'E':
                            c1 = 233;
                            c2 = 0;
                            break;
                        case 'a':
                            c1 = 225;
                            c2 = 0;
                            break;
                        case 'c':
                        case 'g':
                        case 'k':
                        case 'm':
                        case 'n':
                        case 'p':
                        case 'q':
                        case 'w':
                        case 'y':
                        case 711:
                            c2 = 787;
                            break;
                        case 'e':
                            c2 = 769;
                            break;
                        case 'i':
                            c1 = 237;
                            c2 = 0;
                            break;
                        case 'l':
                            c1 = 318;
                            c2 = 0;
                            break;
                        case 't':
                            c1 = 357;
                            c2 = 0;
                            break;
                        case 'u':
                            c1 = 250;
                            c2 = 0;
                            break;
                    }
                    break;
                case '/':
                    switch (c1) {
                        case 'a':
                            c1 = 11365;
                            c2 = 0;
                            break;
                        case 'c':
                            c1 = 572;
                            c2 = 0;
                            break;
                        case 't':
                            c2 = 11366;
                            break;
                    }
                    break;
            }
            switch (c1) {
                case 'B':
                    c1 = 768;
                    break;
                case 'C':
                    c1 = 784;
                    break;
                case 'E':
                    if (mLC == cl) {
                        c1 = 'e';
                        break;
                    }
                    break;
                case 'J':
                    if (mLC == hm) {
                        c1 = 7615;
                        break;
                    }
                    break;
                case 'W':
                    c1 = 695;
                    break;
                case 'e':
                    c1 = 601;
                    break;
                case 'f':
                    c1 = 660;
                    break;
                case 'g':
                    c1 = 331;
                    break;
                case 'j':
                    switch (mLC) {
                        case hm /*6815853*/:
                            c1 = 952;
                            break;
                        case ls /*7078003*/:
                            c1 = 496;
                            break;
                    }
                    break;
                case 'z':
                    switch (mLC) {
                        case cl /*6488172*/:
                            c1 = 'x';
                            c2 = 803;
                            break;
                        case hm /*6815853*/:
                            c1 = 967;
                            break;
                        default:
                            c1 = 7611;
                            break;
                    }
                case 180:
                    c1 = 769;
                    break;
                case 208:
                case 240:
                    c1 = 620;
                    break;
                case 222:
                case 254:
                    c1 = 411;
                    c2 = 789;
                    break;
                case 711:
                    c1 = 780;
                    break;
                case 10004:
                    c1 = 8730;
                    break;
            }
            s.append(c1);
            i++;
            switch (c2) {
                case 0:
                    break;
                case 769:
                case 787:
                    s.append(c2);
                    break;
                case 789:
                case 803:
                    s.append(c2);
                    continue;
                default:
                    continue;
            }
            i++;
        }
        return s.toString();
    }

    public static String s4Ime(CharSequence cs) {
        if (cs == null) {
            return null;
        }
        StringBuilder s = new StringBuilder();
        int l = cs.length();
        int i = 0;
        while (i < l) {
            char c = ca(cs, i, l);
            char c1 = Character.toLowerCase(c);
            boolean up = c != c1;
            char c2 = ca(cs, i + 1, l);
            switch (c2) {
                case '\'':
                case 180:
                    switch (c1) {
                        case '\'':
                            c2 = '\'';
                            break;
                        case 'a':
                            c1 = 225;
                            c2 = 0;
                            break;
                        case 'c':
                            c1 = 263;
                            c2 = 0;
                            break;
                        case 'i':
                            c1 = 237;
                            c2 = 0;
                            break;
                        case 'k':
                            c1 = 7729;
                            c2 = 0;
                            break;
                        case 's':
                            c1 = 347;
                            c2 = 0;
                            break;
                        case 'u':
                            c1 = 250;
                            c2 = 0;
                            break;
                    }
                    break;
                case '-':
                    switch (c1) {
                        case 'k':
                            c1 = 8365;
                            c2 = 0;
                            break;
                        case 'l':
                            c1 = 410;
                            c2 = 0;
                            break;
                        case 't':
                            c1 = 359;
                            c2 = 0;
                            break;
                    }
                    break;
                case '/':
                    switch (c1) {
                        case 'a':
                            c1 = 11365;
                            c2 = 0;
                            break;
                        case 'c':
                            c1 = 572;
                            c2 = 0;
                            break;
                        case 't':
                            c1 = 11366;
                            c2 = 0;
                            break;
                    }
                    break;
                case '_':
                    switch (c1) {
                        case 'k':
                            c1 = 7733;
                            c2 = 0;
                            break;
                        case 'n':
                            c1 = 7753;
                            c2 = 0;
                            break;
                        case 't':
                            c1 = 7791;
                            c2 = 0;
                            break;
                        case 'w':
                        case 'x':
                            c2 = 817;
                            break;
                    }
                    break;
            }
            if (up) {
                c1 = Character.toUpperCase(c1);
            }
            s.append(c1);
            i++;
            switch (c2) {
                case 0:
                    break;
                case 817:
                case 821:
                    s.append(c2);
                    break;
                default:
                    continue;
            }
            i++;
        }
        return s.toString();
    }

    public static String mtIme(CharSequence cs) {
        if (cs == null) {
            return null;
        }
        StringBuilder s = new StringBuilder();
        int l = cs.length();
        int i = 0;
        while (i < l) {
            char c1 = ca(cs, i, l);
            char c2 = ca(cs, i + 1, l);
            char r = pdk(c1, c2, 0);
            if (r != 0) {
                c1 = r;
                c2 = 0;
            }
            s.append(c1);
            i++;
            switch (c2) {
                case 0:
                    i++;
                    break;
                default:
                    break;
            }
        }
        return s.toString();
    }

    public static String aralt(char c) {
        switch (klAR) {
            case 2:
                return aralt2(c);
            default:
                return aralt1(c);
        }
    }

    public static String aralt1(char c) {
        switch (c) {
            case 1575:
                return "أإآء";
            case 1576:
                return "پ";
            case 1580:
                return "چ";
            case 1584:
                return "ڈ";
            case 1586:
                return "ژڑ";
            case 1601:
                if (bfa) {
                    return null;
                }
                return "ڤ";
            case 1603:
                return "گ";
            case 1606:
                return "ں";
            case 1608:
                return "ؤ";
            case 1610:
                return "ىئ";
            default:
                return null;
        }
    }

    public static String aralt2(char c) {
        switch (c) {
            case 1583:
                return "ذڈ";
            case 1585:
                return "زژڑ";
            default:
                return null;
        }
    }

    public static boolean arK2() {
        return ar == mLC && kid.is(R.xml.kbd_big);
    }

    public static boolean nsd(char c) {
        return isRTL(c) || km(c);
    }

    public static char pdk(char c, char pc, char c4) {
        if (mLC == om) {
            return 0;
        }
        boolean el = el() || dkf || mLC == pa;
        if (el) {
            char t = c;
            c = pc;
            pc = t;
        }
        char c2 = Character.toLowerCase(c);
        boolean iu = c2 != c;
        int es = es();
        char r = 0;
        switch (pc) {
            case '\'':
                if (!el) {
                    switch (c2) {
                        case '\'':
                            if (c4 != 0) {
                                char c5 = Character.toLowerCase(c4);
                                iu = Character.isUpperCase(c4);
                                switch (mLC) {
                                    case al /*6357100*/:
                                    case de /*6553701*/:
                                        switch (c5) {
                                            case 'a':
                                                if (!iu) {
                                                    r = 228;
                                                    break;
                                                }
                                                r = 196;
                                                break;
                                            case 'o':
                                                if (!iu) {
                                                    r = 246;
                                                    break;
                                                }
                                                r = 214;
                                                break;
                                            case 's':
                                                r = 223;
                                                break;
                                            case 'u':
                                                if (!iu) {
                                                    r = 252;
                                                    break;
                                                }
                                                r = 220;
                                                break;
                                        }
                                        break;
                                    case fr /*6684786*/:
                                        switch (c5) {
                                            case 'a':
                                                r = 224;
                                                break;
                                            case 'c':
                                                r = 231;
                                                break;
                                            case 'e':
                                                r = 233;
                                                break;
                                            case 'i':
                                                r = 239;
                                                break;
                                            case 'o':
                                                r = 244;
                                                break;
                                            case 'u':
                                                r = 249;
                                                break;
                                        }
                                        break;
                                }
                            }
                            break;
                        case 224:
                            r = 226;
                            break;
                        case 232:
                            r = 234;
                            break;
                        case 233:
                            r = 232;
                            break;
                        case 234:
                            r = 235;
                            break;
                        case 235:
                            r = 'e';
                            break;
                        case 239:
                            r = 238;
                            break;
                        case 249:
                            r = 251;
                            break;
                    }
                }
                break;
            case '^':
            case 710:
            case 770:
                switch (c2) {
                    case 'a':
                        r = 226;
                        break;
                    case 'e':
                        r = 234;
                        break;
                    case 'h':
                        r = 293;
                        break;
                    case 'i':
                        r = 238;
                        break;
                    case 'o':
                        r = 244;
                        break;
                    case 'u':
                        r = 251;
                        break;
                    case 'w':
                        r = 373;
                        break;
                    case 'y':
                        r = 375;
                        break;
                }
                break;
            case '`':
            case 768:
                switch (c2) {
                    case 'a':
                        r = 224;
                        break;
                    case 'e':
                        r = 232;
                        break;
                    case 'i':
                        r = 236;
                        break;
                    case 'o':
                        r = 242;
                        break;
                    case 'u':
                        r = 249;
                        break;
                    case 'y':
                        r = 7923;
                        break;
                    case 713:
                    case 728:
                        r = 1;
                        break;
                    case 945:
                        r = 8048;
                        break;
                    case 949:
                        r = 8050;
                        break;
                    case 951:
                        r = 8052;
                        break;
                    case 953:
                        r = 8054;
                        break;
                    case 959:
                        r = 8056;
                        break;
                    case 965:
                        r = 8058;
                        break;
                    case 969:
                        r = 8060;
                        break;
                    case 8115:
                        r = 8114;
                        break;
                    case 8131:
                        r = 8130;
                        break;
                    case 8179:
                        r = 8178;
                        break;
                }
                break;
            case 'x':
                if (mLC == eo) {
                    switch (c2) {
                        case 'c':
                            r = 265;
                            break;
                        case 'g':
                            r = 285;
                            break;
                        case 'h':
                            r = 293;
                            break;
                        case 'j':
                            r = 309;
                            break;
                        case 's':
                            r = 349;
                            break;
                        case 'u':
                            r = 365;
                            break;
                    }
                }
                break;
            case 168:
            case 776:
                switch (c2) {
                    case 'a':
                        r = 228;
                        break;
                    case 'e':
                        r = 235;
                        break;
                    case 'i':
                        r = 239;
                        break;
                    case 'o':
                        r = 246;
                        break;
                    case 'u':
                        r = 252;
                        break;
                    case 713:
                    case 728:
                        r = 1;
                        break;
                    case 953:
                        r = 970;
                        break;
                    case 965:
                        r = 971;
                        break;
                }
                break;
            case 175:
            case 713:
                switch (c2) {
                    case 'a':
                        if (!iu) {
                            r = 257;
                            break;
                        }
                        r = 256;
                        break;
                    case 'e':
                        if (!iu) {
                            r = 275;
                            break;
                        }
                        r = 274;
                        break;
                    case 'h':
                        r = 295;
                        break;
                    case 'i':
                        if (!iu) {
                            r = 299;
                            break;
                        }
                        r = 298;
                        break;
                    case 'l':
                        if (!iu) {
                            r = 322;
                            break;
                        }
                        r = 321;
                        break;
                    case 'n':
                        if (!iu) {
                            r = 7753;
                            break;
                        }
                        r = 7752;
                        break;
                    case 'o':
                        if (!iu) {
                            r = 333;
                            break;
                        }
                        r = 332;
                        break;
                    case 'u':
                        if (!iu) {
                            r = 363;
                            break;
                        }
                        r = 362;
                        break;
                    case 945:
                        r = 8113;
                        break;
                    case 953:
                        r = 8145;
                        break;
                    case 965:
                        r = 8161;
                        break;
                    default:
                        if (ir(c2, 7936, 8119) || ir(c2, 940, 1023)) {
                            r = 772;
                            break;
                        }
                }
            case 180:
            case 714:
            case 769:
                switch (c2) {
                    case 'a':
                        if (!iu) {
                            r = 225;
                            break;
                        }
                        r = 193;
                        break;
                    case 'c':
                        if (mLC != we) {
                            if (!iu) {
                                r = 231;
                                break;
                            }
                            r = 199;
                            break;
                        }
                        r = 263;
                        break;
                    case 'e':
                        if (!iu) {
                            r = 233;
                            break;
                        }
                        r = 201;
                        break;
                    case 'h':
                        if (!iu) {
                            r = 7717;
                            break;
                        }
                        r = 7716;
                        break;
                    case 'i':
                        if (!iu) {
                            r = 237;
                            break;
                        }
                        r = 205;
                        break;
                    case 'l':
                        if (es == -1) {
                            if (!iu) {
                                r = 322;
                                break;
                            }
                            r = 321;
                            break;
                        } else if (!iu) {
                            r = 7735;
                            break;
                        } else {
                            r = 7734;
                            break;
                        }
                    case 'n':
                        if (es == -1) {
                            if (!iu) {
                                r = 324;
                                break;
                            }
                            r = 323;
                            break;
                        } else if (!iu) {
                            r = 241;
                            break;
                        } else {
                            r = 209;
                            break;
                        }
                    case 'o':
                        if (!iu) {
                            r = 243;
                            break;
                        }
                        r = 211;
                        break;
                    case BuildConfig.VERSION_CODE /*114*/:
                        r = 341;
                        break;
                    case 's':
                        r = 347;
                        break;
                    case 'u':
                        if (!iu) {
                            r = 250;
                            break;
                        }
                        r = 218;
                        break;
                    case 'y':
                        r = 253;
                        break;
                    case 'z':
                        r = 378;
                        break;
                    case 225:
                        if (!iu) {
                            r = 224;
                            break;
                        }
                        r = 192;
                        break;
                    case 233:
                        if (!iu) {
                            r = 232;
                            break;
                        }
                        r = 200;
                        break;
                    case 237:
                        if (!iu) {
                            r = 236;
                            break;
                        }
                        r = 204;
                        break;
                    case 243:
                        if (!iu) {
                            r = 242;
                            break;
                        }
                        r = 210;
                        break;
                    case 250:
                        if (!iu) {
                            r = 249;
                            break;
                        }
                        r = 217;
                        break;
                    case 713:
                    case 728:
                        r = 1;
                        break;
                    case 945:
                        if (!iu) {
                            r = 940;
                            break;
                        }
                        r = 902;
                        break;
                    case 949:
                        if (!iu) {
                            r = 941;
                            break;
                        }
                        r = 904;
                        break;
                    case 951:
                        if (!iu) {
                            r = 942;
                            break;
                        }
                        r = 905;
                        break;
                    case 953:
                        if (!iu) {
                            r = 943;
                            break;
                        }
                        r = 906;
                        break;
                    case 959:
                        if (!iu) {
                            r = 972;
                            break;
                        }
                        r = 908;
                        break;
                    case 965:
                        if (!iu) {
                            r = 973;
                            break;
                        }
                        r = 910;
                        break;
                    case 969:
                        if (!iu) {
                            r = 974;
                            break;
                        }
                        r = 911;
                        break;
                    case 8115:
                        r = 8116;
                        break;
                    case 8131:
                        r = 8132;
                        break;
                    case 8179:
                        r = 8180;
                        break;
                }
                break;
            case 183:
            case 803:
                switch (c2) {
                    case 'I':
                        r = 304;
                        break;
                    case 'c':
                        r = 267;
                        break;
                    case 'd':
                        r = 7693;
                        break;
                    case 'e':
                        r = 7865;
                        break;
                    case 'g':
                        r = 289;
                        break;
                    case 'h':
                        r = 7717;
                        break;
                    case 'i':
                        r = 305;
                        break;
                    case 'o':
                        r = 7885;
                        break;
                    case BuildConfig.VERSION_CODE /*114*/:
                        r = 7771;
                        break;
                    case 's':
                        r = 7779;
                        break;
                    case 't':
                        r = 7789;
                        break;
                    case 'z':
                        if (mLC != bb) {
                            r = 380;
                            break;
                        }
                        r = 7827;
                        break;
                }
                break;
            case 184:
                switch (c2) {
                    case 'c':
                        r = 231;
                        break;
                    case 's':
                        r = 351;
                        break;
                }
                break;
            case 711:
                switch (c2) {
                    case 'a':
                        r = 462;
                        break;
                    case 'c':
                        r = 269;
                        break;
                    case 'e':
                        r = 283;
                        break;
                    case 'g':
                        r = 487;
                        break;
                    case 'i':
                        r = 464;
                        break;
                    case 'o':
                        r = 466;
                        break;
                    case BuildConfig.VERSION_CODE /*114*/:
                        r = 345;
                        break;
                    case 's':
                        r = 353;
                        break;
                    case 'u':
                        r = 468;
                        break;
                    case 'z':
                        r = 382;
                        break;
                    case 252:
                        r = 474;
                        break;
                }
                switch (mLC) {
                    case lv /*7078006*/:
                        switch (c2) {
                            case 'a':
                                r = 257;
                                break;
                            case 'e':
                                r = 275;
                                break;
                            case 'g':
                                r = 291;
                                break;
                            case 'i':
                                r = 299;
                                break;
                            case 'k':
                                r = 311;
                                break;
                            case 'l':
                                r = 316;
                                break;
                            case 'n':
                                r = 326;
                                break;
                            case 'u':
                                r = 363;
                                break;
                        }
                        break;
                }
                break;
            case 728:
                switch (c2) {
                    case 713:
                    case 728:
                        r = 1;
                        break;
                    case 945:
                        r = 8112;
                        break;
                    case 953:
                        r = 8144;
                        break;
                    case 965:
                        r = 8160;
                        break;
                    default:
                        if (ir(c2, 7936, 8119) || ir(c2, 940, 1023)) {
                            r = 774;
                            break;
                        }
                }
            case 731:
            case 808:
                switch (c2) {
                    case 'a':
                        if (!iu) {
                            r = 261;
                            break;
                        }
                        r = 260;
                        break;
                    case 'e':
                        if (!iu) {
                            r = 281;
                            break;
                        }
                        r = 280;
                        break;
                    case 'i':
                        if (!iu) {
                            r = 303;
                            break;
                        }
                        r = 302;
                        break;
                    case 'l':
                        if (!iu) {
                            r = 322;
                            break;
                        }
                        r = 321;
                        break;
                    case 'n':
                        if (!iu) {
                            r = 7753;
                            break;
                        }
                        r = 7752;
                        break;
                    case 'o':
                        if (!iu) {
                            r = 491;
                            break;
                        }
                        r = 490;
                        break;
                    case 'u':
                        if (!iu) {
                            r = 371;
                            break;
                        }
                        r = 370;
                        break;
                }
                break;
            case 1468:
                switch (c2) {
                    case 64298:
                        r = 64301;
                        break;
                    case 64299:
                        r = 64300;
                        break;
                }
                break;
            case 2565:
                switch (c2) {
                    case 2622:
                        r = 2566;
                        break;
                    case 2632:
                        r = 2576;
                        break;
                    case 2636:
                        r = 2580;
                        break;
                }
                break;
            case 2674:
                switch (c2) {
                    case 2623:
                        r = 2567;
                        break;
                    case 2624:
                        r = 2568;
                        break;
                    case 2631:
                        r = 2575;
                        break;
                }
                break;
            case 2675:
                switch (c2) {
                    case 2625:
                        r = 2569;
                        break;
                    case 2626:
                        r = 2570;
                        break;
                }
                break;
            case 8126:
                switch (c2) {
                    case 713:
                    case 728:
                        r = 1;
                        break;
                    case 940:
                        r = 8116;
                        break;
                    case 942:
                        r = 8132;
                        break;
                    case 945:
                        r = 8115;
                        break;
                    case 951:
                        r = 8131;
                        break;
                    case 969:
                        r = 8179;
                        break;
                    case 974:
                        r = 8180;
                        break;
                    case 7936:
                        r = 8064;
                        break;
                    case 7937:
                        r = 8065;
                        break;
                    case 7938:
                        r = 8066;
                        break;
                    case 7939:
                        r = 8067;
                        break;
                    case 7940:
                        r = 8068;
                        break;
                    case 7941:
                        r = 8069;
                        break;
                    case 7942:
                        r = 8070;
                        break;
                    case 7943:
                        r = 8071;
                        break;
                    case 7968:
                        r = 8080;
                        break;
                    case 7969:
                        r = 8081;
                        break;
                    case 7970:
                        r = 8082;
                        break;
                    case 7971:
                        r = 8083;
                        break;
                    case 7972:
                        r = 8100;
                        break;
                    case 7973:
                        r = 8085;
                        break;
                    case 7974:
                        r = 8086;
                        break;
                    case 7975:
                        r = 8087;
                        break;
                    case 8032:
                        r = 8096;
                        break;
                    case 8033:
                        r = 8097;
                        break;
                    case 8034:
                        r = 8098;
                        break;
                    case 8035:
                        r = 8099;
                        break;
                    case 8036:
                        r = 8084;
                        break;
                    case 8037:
                        r = 8101;
                        break;
                    case 8038:
                        r = 8102;
                        break;
                    case 8039:
                        r = 8103;
                        break;
                    case 8048:
                        r = 8114;
                        break;
                    case 8052:
                        r = 8130;
                        break;
                    case 8060:
                        r = 8178;
                        break;
                    case 8118:
                        r = 8119;
                        break;
                    case 8134:
                        r = 8135;
                        break;
                    case 8182:
                        r = 8183;
                        break;
                }
                break;
            case 8127:
                switch (c2) {
                    case 713:
                    case 728:
                        r = 1;
                        break;
                    case 945:
                        r = 7936;
                        break;
                    case 949:
                        r = 7952;
                        break;
                    case 951:
                        r = 7968;
                        break;
                    case 953:
                        r = 7984;
                        break;
                    case 959:
                        r = 8000;
                        break;
                    case 961:
                        r = 8164;
                        break;
                    case 965:
                        r = 8016;
                        break;
                    case 969:
                        r = 8032;
                        break;
                    case 8115:
                        r = 8064;
                        break;
                    case 8131:
                        r = 8080;
                        break;
                    case 8179:
                        r = 8096;
                        break;
                }
                break;
            case 8128:
                switch (c2) {
                    case 945:
                        r = 8118;
                        break;
                    case 951:
                        r = 8134;
                        break;
                    case 953:
                        r = 8150;
                        break;
                    case 965:
                        r = 8166;
                        break;
                    case 969:
                        r = 8182;
                        break;
                    case 8115:
                        r = 8119;
                        break;
                    case 8131:
                        r = 8135;
                        break;
                    case 8179:
                        r = 8183;
                        break;
                }
                break;
            case 8129:
                switch (c2) {
                    case 713:
                    case 728:
                        r = 1;
                        break;
                    case 953:
                        r = 8151;
                        break;
                    case 965:
                        r = 8167;
                        break;
                }
                break;
            case 8141:
                switch (c2) {
                    case 713:
                    case 728:
                        r = 1;
                        break;
                    case 945:
                        r = 7938;
                        break;
                    case 949:
                        r = 7954;
                        break;
                    case 951:
                        r = 7970;
                        break;
                    case 953:
                        r = 7986;
                        break;
                    case 959:
                        r = 8002;
                        break;
                    case 965:
                        r = 8018;
                        break;
                    case 969:
                        r = 8034;
                        break;
                    case 8115:
                        r = 8066;
                        break;
                    case 8131:
                        r = 8082;
                        break;
                    case 8179:
                        r = 8098;
                        break;
                }
                break;
            case 8142:
                switch (c2) {
                    case 713:
                    case 728:
                        r = 1;
                        break;
                    case 945:
                        r = 7940;
                        break;
                    case 949:
                        r = 7956;
                        break;
                    case 951:
                        r = 7972;
                        break;
                    case 953:
                        r = 7988;
                        break;
                    case 959:
                        r = 8004;
                        break;
                    case 965:
                        r = 8020;
                        break;
                    case 969:
                        r = 8036;
                        break;
                    case 8115:
                        r = 8068;
                        break;
                    case 8131:
                        r = 8084;
                        break;
                    case 8179:
                        r = 8100;
                        break;
                }
                break;
            case 8143:
                switch (c2) {
                    case 713:
                    case 728:
                        r = 1;
                        break;
                    case 945:
                        r = 7942;
                        break;
                    case 949:
                        r = 7954;
                        break;
                    case 951:
                        r = 7974;
                        break;
                    case 953:
                        r = 7990;
                        break;
                    case 959:
                        r = 8002;
                        break;
                    case 965:
                        r = 8022;
                        break;
                    case 969:
                        r = 8038;
                        break;
                    case 8115:
                        r = 8070;
                        break;
                    case 8131:
                        r = 8086;
                        break;
                    case 8179:
                        r = 8102;
                        break;
                }
                break;
            case 8157:
                switch (c2) {
                    case 713:
                    case 728:
                        r = 1;
                        break;
                    case 945:
                        r = 7939;
                        break;
                    case 949:
                        r = 7955;
                        break;
                    case 951:
                        r = 7971;
                        break;
                    case 953:
                        r = 7987;
                        break;
                    case 959:
                        r = 8003;
                        break;
                    case 965:
                        r = 8019;
                        break;
                    case 969:
                        r = 8035;
                        break;
                    case 8115:
                        r = 8067;
                        break;
                    case 8131:
                        r = 8083;
                        break;
                    case 8179:
                        r = 8099;
                        break;
                }
                break;
            case 8158:
                switch (c2) {
                    case 713:
                    case 728:
                        r = 1;
                        break;
                    case 945:
                        r = 7941;
                        break;
                    case 949:
                        r = 7957;
                        break;
                    case 951:
                        r = 7973;
                        break;
                    case 953:
                        r = 7989;
                        break;
                    case 959:
                        r = 8005;
                        break;
                    case 965:
                        r = 8021;
                        break;
                    case 969:
                        r = 8037;
                        break;
                    case 8115:
                        r = 8069;
                        break;
                    case 8131:
                        r = 8085;
                        break;
                    case 8179:
                        r = 8101;
                        break;
                }
                break;
            case 8159:
                switch (c2) {
                    case 713:
                    case 728:
                        r = 1;
                        break;
                    case 945:
                        r = 7943;
                        break;
                    case 951:
                        r = 7975;
                        break;
                    case 953:
                        r = 7991;
                        break;
                    case 965:
                        r = 8023;
                        break;
                    case 969:
                        r = 8039;
                        break;
                    case 8115:
                        r = 8071;
                        break;
                    case 8131:
                        r = 8087;
                        break;
                    case 8179:
                        r = 8103;
                        break;
                }
                break;
            case 8173:
                switch (c2) {
                    case 713:
                    case 728:
                        r = 1;
                        break;
                    case 953:
                        r = 8146;
                        break;
                    case 965:
                        r = 8162;
                        break;
                }
                break;
            case 8174:
                switch (c2) {
                    case 953:
                        r = 912;
                        break;
                    case 965:
                        r = 944;
                        break;
                }
                break;
            case 8190:
                switch (c2) {
                    case 713:
                    case 728:
                        r = 1;
                        break;
                    case 945:
                        r = 7937;
                        break;
                    case 949:
                        r = 7953;
                        break;
                    case 951:
                        r = 7969;
                        break;
                    case 953:
                        r = 7985;
                        break;
                    case 959:
                        r = 8001;
                        break;
                    case 961:
                        r = 8165;
                        break;
                    case 965:
                        r = 8017;
                        break;
                    case 969:
                        r = 8033;
                        break;
                    case 8115:
                        r = 8065;
                        break;
                    case 8131:
                        r = 8081;
                        break;
                    case 8179:
                        r = 8097;
                        break;
                }
                break;
        }
        if (r > 1 && iu) {
            char r1 = Character.toUpperCase(r);
            if (r1 != r) {
                return r1;
            }
            if (ir(r, 8064, 8071) || ir(r, 8096, 8103) || ir(r, 8080, 8087)) {
                r = (char) (r + 8);
            }
        }
        return r;
    }

    public static boolean siu() {
        switch (mLC) {
            case ar /*6357106*/:
            case ma /*7143521*/:
                if (klAR != 2) {
                    return false;
                }
                return true;
            case fa /*6684769*/:
            case mz /*7143546*/:
            case s2 /*7536690*/:
                return klfa == 0;
            default:
                return ncmd();
        }
    }

    public static boolean ncmd() {
        switch (mLC) {
            case b2 /*6422578*/:
            case by /*6422649*/:
            case ck /*6488171*/:
            case dv /*6553718*/:
            case km /*7012461*/:
            case lo /*7077999*/:
            case mm /*7143533*/:
            case mx /*7143544*/:
            case my /*7143545*/:
            case tf /*7602278*/:
            case th /*7602280*/:
                return true;
            default:
                return bho || in || arConAble();
        }
    }

    public static boolean tlpc() {
        switch (mLC) {
            case iw /*6881399*/:
            case kk /*7012459*/:
            case ky /*7012473*/:
            case tg /*7602279*/:
            case tt /*7602292*/:
            case yi /*7929961*/:
                return true;
            default:
                return siu();
        }
    }

    public static void psbl() {
        psbl = new ArrayList();
        int n = psb.length();
        for (int i = 0; i < n; i++) {
            psbl.add(Character.toString(toPunc1(psb.charAt(i))));
        }
    }

    public static void acp() {
        boolean b;
        if (((CharSequence) psbl.get(0)).charAt(0) == 12290) {
            b = true;
        } else {
            b = false;
        }
        if (hw()) {
            if (!b) {
                psbl.add(0, "。");
            }
        } else if (b) {
            psbl.remove(0);
        }
    }

    public static int[] tia(String s) {
        int l = s.length();
        int[] r = new int[l];
        for (int i = 0; i < l; i++) {
            r[i] = s.charAt(i);
        }
        return r;
    }

    public static CharSequence ime(CharSequence s) {
        switch (mLC) {
            case yi /*7929961*/:
                return yif(s);
            default:
                if (aad) {
                    return mtIme(s);
                }
                return s;
        }
    }

    public static CharSequence ime2(CharSequence s) {
        if (ls1) {
            return lsIme(s);
        }
        if (mLC == s4) {
            return s4Ime(s);
        }
        if (eth) {
            return amIme(s);
        }
        return s;
    }

    public static char tfc(String s) {
        if (s != null && s.length() == 1) {
            switch (s.charAt(0)) {
                case 21371:
                    return 32570;
                case 25277:
                    break;
                case 29699:
                    if ((hwF & 72) != 0) {
                        return 31179;
                    }
                    break;
                case 30097:
                    return 21670;
                case 33775:
                    return 21010;
            }
            if ((hwF & 256) != 0) {
                return 31179;
            }
        }
        return 0;
    }

    public static String cyT9x(int c) {
        switch (c) {
            case 50:
                return "ґ";
            case 51:
                return "ё";
            case 52:
                return mLC == bg ? "ѝ" : "їі";
            default:
                return "";
        }
    }

    public static void zhFlag() {
        hwF = 39;
        if (zhsr || isT9Semi()) {
            hwF |= 1024;
        }
        switch (zt == -1 ? lzt : zt) {
            case 67:
                hwF |= 2048;
                break;
            case 80:
                hwF |= 64;
                break;
            case 89:
                hwF |= 256;
                break;
            case 90:
                hwF |= 8;
                break;
        }
        if (!hw()) {
            hwF &= -2;
        }
        if (pyfzzcs) {
            hwF |= 4096;
        }
    }

    public static int fs() {
        return isLand() ? fsp : fsl;
    }

    public static String scl(char c) {
        switch (c) {
            case 1:
                return "date";
            case 2:
                return "time";
            case 8204:
                return "zwnj";
            case 8205:
                return "zwj";
            default:
                return null;
        }
    }

    /* JADX WARNING: Missing block: B:4:0x000b, code:
            if (cjs == 83) goto L_0x000d;
     */
    /* JADX WARNING: Missing block: B:6:?, code:
            return false;
     */
    /* JADX WARNING: Missing block: B:7:?, code:
            return true;
     */
    public static boolean phw() {
        /*
        r0 = zt;
        switch(r0) {
            case 67: goto L_0x0007;
            case 80: goto L_0x000d;
            case 89: goto L_0x000d;
            case 90: goto L_0x000d;
            default: goto L_0x0005;
        };
    L_0x0005:
        r0 = 0;
    L_0x0006:
        return r0;
    L_0x0007:
        r0 = cjs;
        r1 = 83;
        if (r0 != r1) goto L_0x0005;
    L_0x000d:
        r0 = 1;
        goto L_0x0006;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.klye.ime.latin.M.phw():boolean");
    }

    public static boolean el() {
        switch (mLC) {
            case ag /*6357095*/:
            case el /*6619244*/:
            case pc /*7340131*/:
                return true;
            default:
                return false;
        }
    }

    public static boolean shd() {
        return shd && !((zt == 90 && zt != -1) || eth || kid.is(R.xml.kbd_t9) || sm3);
    }

    public static void dicClose() {
        if (dicA != null) {
            dicA.flushPendingWrites();
        }
        if (dicB != null) {
            dicB.flushPendingWrites();
        }
    }

    public static void ex() {
        if (mIme != null) {
            mIme.pex();
        }
    }

    public static char uk(int i) {
        switch (i) {
            case 1098:
                return 1111;
            case 1099:
                return 1110;
            case 1101:
                return 1108;
            case 1105:
                return 1169;
            default:
                return 0;
        }
    }

    public static char mk(int i) {
        switch (i) {
            case 1079:
                return 1109;
            case 1106:
                return 1107;
            case 1109:
                return 1079;
            case 1115:
                return 1116;
            default:
                return 0;
        }
    }

    public static boolean cm(Activity c) {
        return true;
    }

    public static boolean cm() {
        if (emjCT("k.mk.m.p") == null && emjCT("k.mk.m.g") == null && emjCT("k.mk.m.s") == null && emjCT("k.mk.m.b") == null) {
            return false;
        }
        return true;
    }

    public static void ut(Context c) {
        try {
            Intent intent = new Intent("android.intent.action.MAIN", null);
            intent.addCategory("android.intent.category.LAUNCHER");
            intent.setComponent(new ComponentName("klye.usertext", "klye.usertext.NotesList"));
            intent.setFlags(268435456);
            sa(c, intent);
            dicUt.init(c);
        } catch (Throwable th) {
            String s = c.getString(R.string.utextdl);
            noti(c, s, hp("dlat.html"));
            msg(c, s);
        }
    }

    public static void slp() {
        try {
            Thread.sleep(2222);
        } catch (InterruptedException e) {
        }
    }
}
