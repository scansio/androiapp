package com.klye.ime.latin;

import android.content.ContentProviderClient;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.RemoteException;
import com.klye.ime.latin.Dictionary.WordCallback;

public class CC extends Dictionary {
    private String[] mPrjtn;
    private Uri mUrl;
    private ContentProviderClient p;

    public CC(String url, String[] prjtn) {
        this.mUrl = Uri.parse(url);
        this.mPrjtn = prjtn;
    }

    public void init(Context context) throws Throwable {
        if (this.p == null) {
            this.p = context.getContentResolver().acquireContentProviderClient(this.mUrl);
            if (this.p == null) {
                throw new Throwable();
            }
        }
    }

    public void finalize() {
        if (this.p != null) {
            this.p.release();
            this.p = null;
        }
    }

    public synchronized void getWords(WordComposer codes, WordCallback cb, int[] nlf) {
        if (this.p != null) {
            getWord(codes.gac(), cb, nlf);
        }
    }

    public void getWords(String[] ss, WordCallback cb, int[] nlf) {
        if (this.p != null) {
            for (String s : ss) {
                getWord(s, cb, nlf);
            }
        }
    }

    public void getWord(String s, WordCallback callback, int[] mNextLettersFrequencies) {
        if (this.p != null) {
            try {
                int l = s.length();
                Cursor cursor = this.p.query(this.mUrl, this.mPrjtn, s, null, null);
                if (cursor != null) {
                    if (cursor.moveToLast()) {
                        while (!cursor.isBeforeFirst()) {
                            callback.addWord(cursor.getString(1), 18888);
                            cursor.moveToPrevious();
                        }
                    }
                    cursor.close();
                }
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    public void learnWord(CharSequence s) {
        if (this.p != null) {
            try {
                ContentValues v = new ContentValues(5);
                v.put("word", (String) s);
                this.p.insert(this.mUrl, v);
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }
    }

    public synchronized boolean isValidWord(CharSequence word) {
        return false;
    }
}
