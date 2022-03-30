package com.mediatek.filemanager.service;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteFullException;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.provider.MediaStore.Files;
import android.text.TextUtils;
import com.mediatek.filemanager.utils.LogUtils;
import com.mediatek.filemanager.utils.OptionsUtils;
import java.util.List;

public final class MediaStoreHelper {
    private static final int SCAN_FOLDER_NUM = 20;
    private static final String TAG = "MediaStoreHelper";
    private BaseAsyncTask mBaseAsyncTask;
    private final Context mContext;
    private String mDstFolder;

    public MediaStoreHelper(Context context) {
        this.mContext = context;
    }

    public MediaStoreHelper(Context context, BaseAsyncTask baseAsyncTask) {
        this.mContext = context;
        this.mBaseAsyncTask = baseAsyncTask;
    }

    public void updateInMediaStore(String newPath, String oldPath) {
        LogUtils.d(TAG, "updateInMediaStore,newPath = " + newPath + ",oldPath = " + oldPath);
        if (this.mContext != null && !TextUtils.isEmpty(newPath) && !TextUtils.isEmpty(newPath)) {
            Uri uri = Files.getMtpObjectsUri("external").buildUpon().appendQueryParameter("need_update_media_values", "true").build();
            String where = "_data=?";
            new String[1][0] = oldPath;
            ContentResolver cr = this.mContext.getContentResolver();
            ContentValues values = new ContentValues();
            values.put("_data", newPath);
            String[] whereArgs = new String[]{oldPath};
            if (OptionsUtils.isMtkSDSwapSurpported()) {
                try {
                    LogUtils.d(TAG, "updateInMediaStore,update.");
                    cr.update(uri, values, where, whereArgs);
                    scanPathforMediaStore(newPath);
                    return;
                } catch (UnsupportedOperationException e) {
                    LogUtils.e(TAG, "Error, database is closed!!!");
                    return;
                } catch (NullPointerException e2) {
                    LogUtils.e(TAG, "Error, NullPointerException:" + e2 + ",update db may failed!!!");
                    return;
                } catch (SQLiteFullException e3) {
                    LogUtils.e(TAG, "Error, database or disk is full!!!" + e3);
                    if (this.mBaseAsyncTask != null) {
                        this.mBaseAsyncTask.cancel(true);
                        return;
                    }
                    return;
                }
            }
            try {
                LogUtils.d(TAG, "updateInMediaStore,update.");
                cr.update(uri, values, where, whereArgs);
                scanPathforMediaStore(newPath);
            } catch (NullPointerException e22) {
                LogUtils.e(TAG, "Error, NullPointerException:" + e22 + ",update db may failed!!!");
            } catch (SQLiteFullException e32) {
                LogUtils.e(TAG, "Error, database or disk is full!!!" + e32);
                if (this.mBaseAsyncTask != null) {
                    this.mBaseAsyncTask.cancel(true);
                }
            } catch (UnsupportedOperationException e4) {
                LogUtils.e(TAG, "Error, database is closed!!!");
            }
        }
    }

    public void scanPathforMediaStore(String path) {
        LogUtils.d(TAG, "scanPathforMediaStore.path =" + path);
        if (this.mContext != null && !TextUtils.isEmpty(path)) {
            String[] paths = new String[]{path};
            LogUtils.d(TAG, "scanPathforMediaStore,scan file .");
            MediaScannerConnection.scanFile(this.mContext, paths, null, null);
        }
    }

    public void scanPathforMediaStore(List<String> scanPaths) {
        LogUtils.d(TAG, "scanPathforMediaStore,scanPaths.");
        int length = scanPaths.size();
        if (this.mContext != null && length > 0) {
            String[] paths;
            if (this.mDstFolder == null || length <= SCAN_FOLDER_NUM) {
                paths = new String[length];
                scanPaths.toArray(paths);
            } else {
                paths = new String[]{this.mDstFolder};
            }
            LogUtils.d(TAG, "scanPathforMediaStore, scanFiles.");
            MediaScannerConnection.scanFile(this.mContext, paths, null, null);
        }
    }

    public void deleteFileInMediaStore(List<String> paths) {
        LogUtils.d(TAG, "deleteFileInMediaStore.");
        Uri uri = Files.getContentUri("external");
        StringBuilder whereClause = new StringBuilder();
        whereClause.append("?");
        for (int i = 0; i < paths.size() - 1; i++) {
            whereClause.append(",?");
        }
        String where = "_data IN(" + whereClause.toString() + ")";
        if (this.mContext != null && !paths.isEmpty()) {
            ContentResolver cr = this.mContext.getContentResolver();
            String[] whereArgs = new String[paths.size()];
            paths.toArray(whereArgs);
            LogUtils.d(TAG, "deleteFileInMediaStore,delete.");
            try {
                cr.delete(uri, where, whereArgs);
            } catch (SQLiteFullException e) {
                LogUtils.e(TAG, "Error, database or disk is full!!!" + e);
                if (this.mBaseAsyncTask != null) {
                    this.mBaseAsyncTask.cancel(true);
                }
            } catch (UnsupportedOperationException e2) {
                LogUtils.e(TAG, "Error, database is closed!!!");
                if (this.mBaseAsyncTask != null) {
                    this.mBaseAsyncTask.cancel(true);
                }
            }
        }
    }

    public void deleteFileInMediaStore(String path) {
        LogUtils.d(TAG, "deleteFileInMediaStore,path =" + path);
        if (!TextUtils.isEmpty(path)) {
            Uri uri = Files.getContentUri("external");
            String where = "_data=?";
            String[] whereArgs = new String[]{path};
            if (this.mContext != null) {
                ContentResolver cr = this.mContext.getContentResolver();
                LogUtils.d(TAG, "deleteFileInMediaStore,delete.");
                try {
                    if (OptionsUtils.isMtkSDSwapSurpported()) {
                        try {
                            cr.delete(uri, where, whereArgs);
                            return;
                        } catch (UnsupportedOperationException e) {
                            LogUtils.e(TAG, "Error, database is closed!!!");
                            if (this.mBaseAsyncTask != null) {
                                this.mBaseAsyncTask.cancel(true);
                                return;
                            }
                            return;
                        }
                    }
                    cr.delete(uri, where, whereArgs);
                } catch (SQLiteFullException e2) {
                    LogUtils.e(TAG, "Error, database or disk is full!!!" + e2);
                    if (this.mBaseAsyncTask != null) {
                        this.mBaseAsyncTask.cancel(true);
                    }
                }
            }
        }
    }

    public void setDstFolder(String dstFolder) {
        this.mDstFolder = dstFolder;
    }
}
