package com.klye.ime.latin;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.os.AsyncTask;
import android.util.Log;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

public class UserBigramDictionary extends ExpandableDictionary {
    private static final String DATABASE_NAME = "userbigram_dict.db";
    private static final int DATABASE_VERSION = 1;
    private static final int FREQUENCY_FOR_TYPED = 2;
    private static final int FREQUENCY_MAX = 127;
    private static final String FREQ_COLUMN_FREQUENCY = "freq";
    private static final String FREQ_COLUMN_ID = "_id";
    private static final String FREQ_COLUMN_PAIR_ID = "pair_id";
    private static final String FREQ_TABLE_NAME = "frequency";
    private static final String MAIN_COLUMN_ID = "_id";
    private static final String MAIN_COLUMN_LOCALE = "locale";
    private static final String MAIN_COLUMN_WORD1 = "word1";
    private static final String MAIN_COLUMN_WORD2 = "word2";
    private static final String MAIN_TABLE_NAME = "main";
    private static final String TAG = "UserBigramDictionary";
    private static int sDeleteUserBigrams = 1000;
    private static final HashMap<String, String> sDictProjectionMap = new HashMap();
    private static int sMaxUserBigrams = 10000;
    private static DatabaseHelper sOpenHelper = null;
    private static volatile boolean sUpdatingDB = false;
    private final LatinIME mIme;
    private String mLocale;
    private HashSet<Bigram> mPendingWrites = new HashSet();
    private final Object mPendingWritesLock = new Object();

    private static class Bigram {
        public final int mFrequency;
        public final String mWord1;
        public final String mWord2;

        Bigram(String word1, String word2, int frequency) {
            this.mWord1 = word1;
            this.mWord2 = word2;
            this.mFrequency = frequency;
        }

        public boolean equals(Object bigram) {
            Bigram bigram2 = (Bigram) bigram;
            return this.mWord1.equals(bigram2.mWord1) && this.mWord2.equals(bigram2.mWord2);
        }

        public int hashCode() {
            return (this.mWord1 + " " + this.mWord2).hashCode();
        }
    }

    private static class DatabaseHelper extends SQLiteOpenHelper {
        DatabaseHelper(Context context) {
            super(context, UserBigramDictionary.DATABASE_NAME, null, 1);
        }

        public void onCreate(SQLiteDatabase db) {
            db.execSQL("PRAGMA foreign_keys = ON;");
            db.execSQL("CREATE TABLE main (_id INTEGER PRIMARY KEY,word1 TEXT,word2 TEXT,locale TEXT);");
            db.execSQL("CREATE TABLE frequency (_id INTEGER PRIMARY KEY,pair_id INTEGER,freq INTEGER,FOREIGN KEY(pair_id) REFERENCES main(_id) ON DELETE CASCADE);");
        }

        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.w(UserBigramDictionary.TAG, "Upgrading database from version " + oldVersion + " to " + newVersion + ", which will destroy all old data");
            db.execSQL("DROP TABLE IF EXISTS main");
            db.execSQL("DROP TABLE IF EXISTS frequency");
            onCreate(db);
        }
    }

    private static class UpdateDbTask extends AsyncTask<Void, Void, Void> {
        private final DatabaseHelper mDbHelper;
        private final String mLocale;
        private final HashSet<Bigram> mMap;

        public UpdateDbTask(DatabaseHelper openHelper, HashSet<Bigram> pendingWrites, String locale) {
            this.mMap = pendingWrites;
            this.mLocale = locale;
            this.mDbHelper = openHelper;
        }

        private void checkPruneData(SQLiteDatabase db) {
            db.execSQL("PRAGMA foreign_keys = ON;");
            Cursor c = db.query(UserBigramDictionary.FREQ_TABLE_NAME, new String[]{UserBigramDictionary.FREQ_COLUMN_PAIR_ID}, null, null, null, null, null);
            try {
                int totalRowCount = c.getCount();
                if (totalRowCount > UserBigramDictionary.sMaxUserBigrams) {
                    int numDeleteRows = (totalRowCount - UserBigramDictionary.sMaxUserBigrams) + UserBigramDictionary.sDeleteUserBigrams;
                    int pairIdColumnId = c.getColumnIndex(UserBigramDictionary.FREQ_COLUMN_PAIR_ID);
                    c.moveToFirst();
                    for (int count = 0; count < numDeleteRows && !c.isAfterLast(); count++) {
                        String pairId = c.getString(pairIdColumnId);
                        db.delete(UserBigramDictionary.MAIN_TABLE_NAME, "_id=?", new String[]{pairId});
                        c.moveToNext();
                    }
                }
                c.close();
            } catch (Throwable th) {
                c.close();
            }
        }

        protected void onPreExecute() {
            UserBigramDictionary.sUpdatingDB = true;
        }

        protected Void doInBackground(Void... v) {
            SQLiteDatabase db = this.mDbHelper.getWritableDatabase();
            db.execSQL("PRAGMA foreign_keys = ON;");
            Iterator<Bigram> iterator = this.mMap.iterator();
            while (iterator.hasNext()) {
                int pairId;
                Bigram bi = (Bigram) iterator.next();
                Cursor c = db.query(UserBigramDictionary.MAIN_TABLE_NAME, new String[]{"_id"}, "word1=? AND word2=? AND locale=?", new String[]{bi.mWord1, bi.mWord2, this.mLocale}, null, null, null);
                if (c.moveToFirst()) {
                    pairId = c.getInt(c.getColumnIndex("_id"));
                    db.delete(UserBigramDictionary.FREQ_TABLE_NAME, "pair_id=?", new String[]{Integer.toString(pairId)});
                } else {
                    pairId = Long.valueOf(db.insert(UserBigramDictionary.MAIN_TABLE_NAME, null, UserBigramDictionary.getContentValues(bi.mWord1, bi.mWord2, this.mLocale))).intValue();
                }
                c.close();
                db.insert(UserBigramDictionary.FREQ_TABLE_NAME, null, UserBigramDictionary.getFrequencyContentValues(pairId, bi.mFrequency));
            }
            checkPruneData(db);
            UserBigramDictionary.sUpdatingDB = false;
            return null;
        }
    }

    static {
        sDictProjectionMap.put("_id", "_id");
        sDictProjectionMap.put(MAIN_COLUMN_WORD1, MAIN_COLUMN_WORD1);
        sDictProjectionMap.put(MAIN_COLUMN_WORD2, MAIN_COLUMN_WORD2);
        sDictProjectionMap.put(MAIN_COLUMN_LOCALE, MAIN_COLUMN_LOCALE);
        sDictProjectionMap.put("_id", "_id");
        sDictProjectionMap.put(FREQ_COLUMN_PAIR_ID, FREQ_COLUMN_PAIR_ID);
        sDictProjectionMap.put(FREQ_COLUMN_FREQUENCY, FREQ_COLUMN_FREQUENCY);
    }

    public void setDatabaseMax(int maxUserBigram) {
        sMaxUserBigrams = maxUserBigram;
    }

    public void setDatabaseDelete(int deleteUserBigram) {
        sDeleteUserBigrams = deleteUserBigram;
    }

    public UserBigramDictionary(Context context, LatinIME ime, String locale) {
        super(context, 3);
        this.mIme = ime;
        this.mLocale = locale;
        if (sOpenHelper == null) {
            sOpenHelper = new DatabaseHelper(getContext());
        }
        if (this.mLocale != null && this.mLocale.length() > 1) {
            loadDictionary();
        }
    }

    public void close() {
        flushPendingWrites();
        super.close();
    }

    public int addBigrams(String word1, String word2) {
        int freq = 0;
        if (this.mIme != null && this.mIme.getCurrentWord().isAutoCapitalized()) {
            word2 = Character.toLowerCase(word2.charAt(0)) + word2.substring(1);
        }
        if (!(word1.equals(word2) || (word1.equalsIgnoreCase("thus") && word1.equalsIgnoreCase("is")))) {
            freq = super.addBigram(word1, word2, 2);
            if (freq > 127) {
                freq = 127;
            }
            synchronized (this.mPendingWritesLock) {
                if (freq != 2) {
                    if (!this.mPendingWrites.isEmpty()) {
                        Bigram bi = new Bigram(word1, word2, freq);
                        this.mPendingWrites.remove(bi);
                        this.mPendingWrites.add(bi);
                    }
                }
                this.mPendingWrites.add(new Bigram(word1, word2, freq));
            }
        }
        return freq;
    }

    public void flushPendingWrites() {
        synchronized (this.mPendingWritesLock) {
            if (this.mPendingWrites.isEmpty()) {
                return;
            }
            new UpdateDbTask(sOpenHelper, this.mPendingWrites, this.mLocale).execute(new Void[0]);
            this.mPendingWrites = new HashSet();
        }
    }

    void waitUntilUpdateDBDone() {
        synchronized (this.mPendingWritesLock) {
            while (sUpdatingDB) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                }
            }
        }
    }

    public void loadDictionaryAsync() {
        Cursor cursor = query("locale=?", new String[]{this.mLocale});
        try {
            if (cursor.moveToFirst()) {
                int word1Index = cursor.getColumnIndex(MAIN_COLUMN_WORD1);
                int word2Index = cursor.getColumnIndex(MAIN_COLUMN_WORD2);
                int frequencyIndex = cursor.getColumnIndex(FREQ_COLUMN_FREQUENCY);
                while (!cursor.isAfterLast()) {
                    String word1 = cursor.getString(word1Index);
                    String word2 = cursor.getString(word2Index);
                    int frequency = cursor.getInt(frequencyIndex);
                    if (word1.length() < 32 && word2.length() < 32) {
                        super.setBigram(word1, word2, frequency);
                    }
                    cursor.moveToNext();
                }
            }
            cursor.close();
        } catch (Throwable th) {
            cursor.close();
        }
    }

    private Cursor query(String selection, String[] selectionArgs) {
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        qb.setTables("main INNER JOIN frequency ON (main._id=frequency.pair_id)");
        qb.setProjectionMap(sDictProjectionMap);
        return qb.query(sOpenHelper.getReadableDatabase(), new String[]{MAIN_COLUMN_WORD1, MAIN_COLUMN_WORD2, FREQ_COLUMN_FREQUENCY, MAIN_COLUMN_LOCALE}, selection, selectionArgs, null, null, null);
    }

    private static ContentValues getFrequencyContentValues(int pairId, int frequency) {
        ContentValues values = new ContentValues(2);
        values.put(FREQ_COLUMN_PAIR_ID, Integer.valueOf(pairId));
        values.put(FREQ_COLUMN_FREQUENCY, Integer.valueOf(frequency));
        return values;
    }

    private static ContentValues getContentValues(String word1, String word2, String locale) {
        ContentValues values = new ContentValues(3);
        values.put(MAIN_COLUMN_WORD1, word1);
        values.put(MAIN_COLUMN_WORD2, word2);
        values.put(MAIN_COLUMN_LOCALE, locale);
        return values;
    }

    public String exp() {
        flushPendingWrites();
        String s = "";
        Context c = getContext();
        try {
            Cursor cursor = query(null, null);
            if (cursor == null) {
                return s;
            }
            if (cursor.moveToFirst()) {
                s = "MLK_Bigram_Begin\n";
                while (!cursor.isAfterLast()) {
                    s = s + cursor.getString(0) + "\t" + cursor.getString(1) + "\t" + cursor.getString(2) + "\t" + cursor.getString(3) + "\n";
                    cursor.moveToNext();
                }
                s = s + "MLK_Bigram_End\n";
            }
            cursor.close();
            return s;
        } catch (Throwable th) {
            M.msg(c, "User dictionary maybe currupted");
        }
    }

    public void imp(String s) {
        Context c1 = getContext();
        SQLiteDatabase db = sOpenHelper.getWritableDatabase();
        try {
            String[] sp = s.split("MLK_Bigram_");
            if (sp.length == 3) {
                sp = sp[1].split("\n");
                db.beginTransaction();
                sOpenHelper.onUpgrade(db, 0, 0);
                for (int i = 1; i < sp.length; i++) {
                    String[] sp1 = sp[i].split("\t");
                    String mWord1 = sp1[0];
                    String mWord2 = sp1[1];
                    int mFrequency = Integer.parseInt(sp1[2]);
                    db.insert(FREQ_TABLE_NAME, null, getFrequencyContentValues(Long.valueOf(db.insert(MAIN_TABLE_NAME, null, getContentValues(mWord1, mWord2, sp1[3]))).intValue(), mFrequency));
                }
                db.setTransactionSuccessful();
                M.msg(c1, "Saved");
                return;
            }
            db.endTransaction();
            loadDictionary();
            M.msg(c1, "Invalid data or aborted");
        } catch (Throwable e) {
            M.l(e);
        } finally {
            db.endTransaction();
            loadDictionary();
        }
    }
}
