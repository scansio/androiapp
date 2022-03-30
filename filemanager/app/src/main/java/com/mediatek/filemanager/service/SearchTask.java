package com.mediatek.filemanager.service;

import android.content.ContentResolver;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.net.Uri;
import android.provider.MediaStore.Files;
import com.mediatek.filemanager.FileInfo;
import com.mediatek.filemanager.FileInfoManager;
import com.mediatek.filemanager.service.FileManagerService.OperationEventListener;
import com.mediatek.filemanager.utils.LogUtils;

class SearchTask extends BaseAsyncTask {
    private static final String TAG = "SearchTask";
    private final ContentResolver mContentResolver;
    private final String mPath;
    private final String mSearchName;

    public SearchTask(FileInfoManager fileInfoManager, OperationEventListener operationEvent, String searchName, String path, ContentResolver contentResolver) {
        super(fileInfoManager, operationEvent);
        this.mContentResolver = contentResolver;
        this.mPath = path;
        this.mSearchName = searchName;
    }

    protected Integer doInBackground(Void... params) {
        LogUtils.d(TAG, "doInBackground...");
        Uri uri = Files.getContentUri("external");
        int ret = 0;
        String[] projection = new String[]{"_data"};
        StringBuilder sb = new StringBuilder();
        sb.append("file_name like ");
        DatabaseUtils.appendEscapedSQLString(sb, "%" + this.mSearchName + "%");
        sb.append(" and ").append("_data like ");
        DatabaseUtils.appendEscapedSQLString(sb, "%" + this.mPath + "%");
        String selection = sb.toString();
        Cursor cursor = this.mContentResolver.query(uri, projection, selection, null, null);
        LogUtils.d(getClass().getName(), "projection = " + projection[0]);
        LogUtils.d(getClass().getName(), "selection = " + selection);
        if (cursor == null) {
            LogUtils.d(TAG, "doInBackground, cursor is null.");
            return Integer.valueOf(-1);
        }
        int total = cursor.getCount();
        publishProgress(new ProgressInfo[]{new ProgressInfo("", 0, (long) total, 0, (long) total)});
        int progress = 0;
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            try {
                if (isCancelled()) {
                    LogUtils.d(TAG, "doInBackground,cancel.");
                    ret = -7;
                    break;
                }
                String name = cursor.getString(cursor.getColumnIndex("_data"));
                cursor.moveToNext();
                progress++;
                ProgressInfo[] progressInfoArr = new ProgressInfo[1];
                progressInfoArr[0] = new ProgressInfo(new FileInfo(name), progress, (long) total, progress, (long) total);
                publishProgress(progressInfoArr);
            } catch (Throwable th) {
                cursor.close();
            }
        }
        cursor.close();
        LogUtils.i(TAG, "doInBackground,ret = " + ret);
        return Integer.valueOf(ret);
    }
}
