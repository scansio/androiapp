package com.klye.ime.latin;

import android.content.ContentProviderClient;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.RemoteException;
import android.provider.BaseColumns;
import com.klye.ime.latin.Dictionary.WordCallback;

public class UText extends Dictionary {
    private static final int INDEX_WORD = 1;
    private static final String[] PROJECTION = new String[]{"_id", Notes.TITLE, Notes.NOTE};
    private ContentProviderClient p;

    public static final class Notes implements BaseColumns {
        public static final String AUTHORITY = "klye.provider.usertext";
        public static final Uri CONTENT_URI = Uri.parse("content://klye.provider.usertext/notes");
        public static final String NOTE = "note";
        public static final String TITLE = "title";

        private Notes() {
        }
    }

    public void init(Context context) {
        if (this.p == null) {
            this.p = context.getContentResolver().acquireContentProviderClient(Notes.CONTENT_URI);
        }
    }

    protected void finalize() {
        if (this.p != null) {
            this.p.release();
        }
    }

    public synchronized void getWords(WordComposer codes, WordCallback callback, int[] nextLettersFrequencies) {
        if (this.p != null) {
            try {
                int l = codes.getTypedWord().length();
                Cursor cursor = this.p.query(Notes.CONTENT_URI, PROJECTION, "title like '" + codes.gac().replace("'", "''") + "%'", null, null);
                if (cursor != null) {
                    if (cursor.moveToFirst()) {
                        while (!cursor.isAfterLast()) {
                            String word = cursor.getString(2);
                            String sh = cursor.getString(1);
                            if (word.length() == 0) {
                                word = sh;
                            }
                            int d = sh.length() - l;
                            if (d < 0) {
                                d = 0;
                            }
                            WordCallback wordCallback = callback;
                            wordCallback.addWord(M.zwsp + word, 388888 / (d + 50));
                            cursor.moveToNext();
                        }
                    }
                    cursor.close();
                }
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        return;
    }

    public synchronized String exp() {
        String s;
        String s2 = "";
        if (this.p == null) {
            s = s2;
        } else {
            try {
                Cursor cursor = this.p.query(Notes.CONTENT_URI, PROJECTION, null, null, null);
                if (cursor == null) {
                    s = s2;
                } else {
                    if (cursor.moveToFirst()) {
                        s2 = "MLK_AText_Begin\n";
                        while (!cursor.isAfterLast()) {
                            s2 = s2 + cursor.getString(1) + "\t" + cursor.getString(2).replace("\n", "\\n") + "\n";
                            cursor.moveToNext();
                        }
                        s2 = s2 + "MLK_AText_End\n";
                    }
                    cursor.close();
                    s = s2;
                }
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        return s;
    }

    public synchronized boolean isValidWord(CharSequence word) {
        return false;
    }

    public void imp(String s, Context c1) {
        try {
            String[] sp = s.split("MLK_AText_");
            if (sp.length == 3) {
                sp = sp[1].split("\n");
                this.p.delete(Notes.CONTENT_URI, null, null);
                for (int i = 1; i < sp.length; i++) {
                    String[] sp1 = sp[i].split("\t");
                    ContentValues cv = new ContentValues();
                    cv.put(Notes.TITLE, sp1[0]);
                    cv.put(Notes.NOTE, sp1[1].replace("\\n", "\n"));
                    this.p.insert(Notes.CONTENT_URI, cv);
                }
                M.msg(c1, "Saved");
                return;
            }
        } catch (Throwable e) {
            M.l(e);
        }
        M.msg(c1, "Invalid data or aborted");
    }
}
