package com.klye.ime.latin;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.provider.UserDictionary.Words;
import com.klye.ime.latin.Dictionary.WordCallback;

public class UserDictionary extends ExpandableDictionary {
    private static final int INDEX_FREQUENCY = 2;
    private static final int INDEX_WORD = 1;
    private static final String[] PROJECTION = new String[]{"_id", "word", "frequency"};
    private static final String[] PROJECTION2 = new String[]{"word", "frequency", "locale"};
    private String mLocale;
    private ContentObserver mObserver;

    public UserDictionary(Context context, String locale) {
        super(context, 2);
        this.mLocale = locale;
        ContentResolver cres = context.getContentResolver();
        Uri uri = Words.CONTENT_URI;
        ContentObserver anonymousClass1 = new ContentObserver(null) {
            public void onChange(boolean self) {
                UserDictionary.this.setRequiresReload(true);
            }
        };
        this.mObserver = anonymousClass1;
        cres.registerContentObserver(uri, true, anonymousClass1);
        loadDictionary();
        this.ratio1 = 13;
    }

    public synchronized void close() {
        if (this.mObserver != null) {
            getContext().getContentResolver().unregisterContentObserver(this.mObserver);
            this.mObserver = null;
        }
        super.close();
    }

    public synchronized String exp() {
        String s;
        String s2 = "";
        Context c = getContext();
        try {
            Cursor cursor = c.getContentResolver().query(Words.CONTENT_URI, PROJECTION2, null, null, null);
            if (cursor == null) {
                s = s2;
            } else {
                if (cursor.moveToFirst()) {
                    s2 = "MLK_UDic_Begin\n";
                    while (!cursor.isAfterLast()) {
                        s2 = s2 + cursor.getString(0) + "\t" + cursor.getString(1) + "\t" + cursor.getString(2) + "\n";
                        cursor.moveToNext();
                    }
                    s2 = s2 + "MLK_UDic_End\n";
                }
                cursor.close();
                s = s2;
            }
        } catch (Throwable th) {
            M.msg(c, "User dictionary maybe currupted");
        }
        return s;
    }

    public void imp(String s) {
        Context c1 = getContext();
        try {
            String[] sp = s.split("MLK_UDic_");
            if (sp.length == 3) {
                sp = sp[1].split("\n");
                ContentResolver p = c1.getContentResolver();
                p.delete(Words.CONTENT_URI, null, null);
                for (int i = 1; i < sp.length; i++) {
                    String[] sp1 = sp[i].split("\t");
                    ContentValues cv = new ContentValues();
                    cv.put("word", sp1[0]);
                    cv.put("frequency", sp1[1]);
                    cv.put("locale", sp1[2]);
                    p.insert(Words.CONTENT_URI, cv);
                }
                M.msg(c1, "Saved");
                return;
            }
        } catch (Throwable e) {
            M.l(e);
        }
        M.msg(c1, "Invalid data or aborted");
    }

    public void loadDictionaryAsync() {
        Context c = getContext();
        try {
            addWords(c.getContentResolver().query(Words.CONTENT_URI, PROJECTION, "(locale IS NULL) or (locale=?)", new String[]{this.mLocale}, null));
        } catch (Throwable th) {
            M.msg(c, "User dictionary maybe currupted");
        }
    }

    public synchronized void addWord(String word, int f) {
        Thread t;
        if (getRequiresReload()) {
            loadDictionaryAsync();
        }
        int l = getMaxWordLength();
        if (word.length() >= l) {
            word = word.substring(0, l);
        }
        final String w2 = word;
        super.addWord(word, f);
        final ContentValues values = new ContentValues(5);
        values.put("word", word);
        values.put("frequency", Integer.valueOf(f));
        values.put("locale", this.mLocale);
        values.put("appid", Integer.valueOf(0));
        final ContentResolver contentResolver = getContext().getContentResolver();
        switch (f) {
            case 0:
            case 129:
                t = new Thread("removeWord") {
                    public void run() {
                        contentResolver.delete(Words.CONTENT_URI, "locale=? and word=? ", new String[]{UserDictionary.this.mLocale, w2});
                    }
                };
                break;
            case 1:
                t = new Thread("updateword") {
                    public void run() {
                        contentResolver.update(Words.CONTENT_URI, values, "locale=? and word=? ", new String[]{UserDictionary.this.mLocale, w2});
                    }
                };
                break;
            default:
                t = new Thread("addWord") {
                    public void run() {
                        contentResolver.insert(Words.CONTENT_URI, values);
                    }
                };
                break;
        }
        t.start();
        setRequiresReload(false);
    }

    public synchronized void getWords(WordComposer codes, WordCallback callback, int[] nextLettersFrequencies) {
        super.getWords(codes, callback, nextLettersFrequencies);
    }

    public synchronized boolean isValidWord(CharSequence word) {
        return super.isValidWord(word);
    }

    private void addWords(Cursor cursor) {
        if (cursor != null) {
            clearDictionary();
            int maxWordLength = getMaxWordLength();
            if (cursor.moveToFirst()) {
                while (!cursor.isAfterLast()) {
                    String word = cursor.getString(1);
                    int frequency = cursor.getInt(2);
                    if (word.length() < maxWordLength) {
                        super.addWord(word, frequency);
                    }
                    cursor.moveToNext();
                }
            }
            cursor.close();
        }
    }
}
