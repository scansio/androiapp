package com.androi.development;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.sqlite.SQLiteConstraintException;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore.Audio.Media;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import java.util.Random;

public class MediaScannerActivity extends Activity {
    private String[] elements = new String[]{"ab", "am", "bra", "bri", "ci", "co", "de", "di", "do", "fa", "fi", "ki", "la", "li", "ma", "me", "mi", "mo", "na", "ni", "pa", "ta", "ti", "vi", "vo"};
    private int mAlbums;
    private int mArtists;
    private Uri mAudioUri;
    StringBuilder mBuilder = new StringBuilder();
    Runnable mDisplayUpdater = new Runnable() {
        public void run() {
            MediaScannerActivity.this.mTitle.setText("Added " + MediaScannerActivity.this.mArtists + " artists, " + MediaScannerActivity.this.mAlbums + " albums, " + MediaScannerActivity.this.mSongs + " songs.");
        }
    };
    Handler mInsertHandler = new Handler() {
        public void handleMessage(Message msg) {
            if (MediaScannerActivity.this.mNumToInsert = MediaScannerActivity.this.mNumToInsert - 1 > 0) {
                MediaScannerActivity.this.addAlbum();
                MediaScannerActivity.this.runOnUiThread(MediaScannerActivity.this.mDisplayUpdater);
                if (!MediaScannerActivity.this.isFinishing()) {
                    sendEmptyMessage(0);
                }
            }
        }
    };
    private int mNumToInsert = 20;
    Random mRandom = new Random();
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals("android.intent.action.MEDIA_SCANNER_STARTED")) {
                MediaScannerActivity.this.mTitle.setText("Media Scanner started scanning " + intent.getData().getPath());
            } else if (intent.getAction().equals("android.intent.action.MEDIA_SCANNER_FINISHED")) {
                MediaScannerActivity.this.mTitle.setText("Media Scanner finished scanning " + intent.getData().getPath());
            }
        }
    };
    private ContentResolver mResolver;
    private int mSongs;
    private TextView mTitle;
    ContentValues[] mValues = new ContentValues[10];

    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setContentView(R.layout.media_scanner_activity);
        IntentFilter intentFilter = new IntentFilter("android.intent.action.MEDIA_SCANNER_STARTED");
        intentFilter.addAction("android.intent.action.MEDIA_SCANNER_FINISHED");
        intentFilter.addDataScheme("file");
        registerReceiver(this.mReceiver, intentFilter);
        ((EditText) findViewById(R.id.numsongs)).addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {
                try {
                    MediaScannerActivity.this.mNumToInsert = Integer.valueOf(s.toString()).intValue();
                } catch (NumberFormatException e) {
                    MediaScannerActivity.this.mNumToInsert = 20;
                }
                MediaScannerActivity.this.setInsertButtonText();
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
        });
        this.mTitle = (TextView) findViewById(R.id.title);
        this.mResolver = getContentResolver();
        this.mAudioUri = Media.EXTERNAL_CONTENT_URI;
        for (int i = 0; i < 10; i++) {
            this.mValues[i] = new ContentValues();
        }
        setInsertButtonText();
    }

    public void onDestroy() {
        unregisterReceiver(this.mReceiver);
        this.mInsertHandler.removeMessages(0);
        super.onDestroy();
    }

    public void startScan(View v) {
        sendBroadcast(new Intent("android.intent.action.MEDIA_MOUNTED", Uri.parse("file://" + Environment.getExternalStorageDirectory())));
        this.mTitle.setText("Sent ACTION_MEDIA_MOUNTED to trigger the Media Scanner.");
    }

    private void setInsertButtonText() {
        ((Button) findViewById(R.id.insertbutton)).setText(getString(R.string.insertbutton, new Object[]{Integer.valueOf(this.mNumToInsert)}));
    }

    public void insertItems(View v) {
        if (this.mInsertHandler.hasMessages(0)) {
            this.mInsertHandler.removeMessages(0);
            setInsertButtonText();
            return;
        }
        this.mInsertHandler.sendEmptyMessage(0);
    }

    private void addAlbum() {
        try {
            String albumArtist = "Various Artists";
            String albumName = getRandomWord(3);
            int baseYear = this.mRandom.nextInt(30) + 1969;
            for (int i = 0; i < 10; i++) {
                this.mValues[i].clear();
                String artist = getRandomName();
                ContentValues map = this.mValues[i];
                map.put("_data", "http://bogus/" + albumName + "/" + artist + "_" + i);
                map.put("title", getRandomWord(4) + " " + getRandomWord(2) + " " + (i + 1));
                map.put("mime_type", "audio/mp3");
                map.put("artist", artist);
                map.put("album_artist", albumArtist);
                map.put("album", albumName);
                map.put("track", Integer.valueOf(i + 1));
                map.put("duration", Integer.valueOf(240000));
                map.put("is_music", Integer.valueOf(1));
                map.put("year", Integer.valueOf(this.mRandom.nextInt(10) + baseYear));
            }
            this.mResolver.bulkInsert(this.mAudioUri, this.mValues);
            this.mSongs += 10;
            this.mAlbums++;
            this.mArtists += 11;
        } catch (SQLiteConstraintException ex) {
            Log.d("@@@@", "insert failed", ex);
        }
    }

    private String getRandomWord(int len) {
        int max = this.elements.length;
        this.mBuilder.setLength(0);
        for (int i = 0; i < len; i++) {
            this.mBuilder.append(this.elements[this.mRandom.nextInt(max)]);
        }
        this.mBuilder.setCharAt(0, Character.toUpperCase(this.mBuilder.charAt(0)));
        return this.mBuilder.toString();
    }

    private String getRandomName() {
        String first = getRandomWord(this.mRandom.nextInt(5) < 3 ? 3 : 2);
        String last = getRandomWord(3);
        switch (this.mRandom.nextInt(6)) {
            case 1:
                if (!last.startsWith("Di")) {
                    last = "di " + last;
                    break;
                }
                break;
            case 2:
                last = "van " + last;
                break;
            case 3:
                last = "de " + last;
                break;
        }
        return first + " " + last;
    }
}
