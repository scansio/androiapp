package com.mediatek.filemanager.service;

import com.mediatek.filemanager.FileInfo;
import com.mediatek.filemanager.FileInfoManager;
import com.mediatek.filemanager.MountPointManager;
import com.mediatek.filemanager.service.FileManagerService.OperationEventListener;
import com.mediatek.filemanager.utils.LogUtils;
import java.io.File;

class DetailInfoTask extends BaseAsyncTask {
    private static final String TAG = "DetailInfoTask";
    private final FileInfo mDetailfileInfo;
    private long mTotal = 0;

    public DetailInfoTask(FileInfoManager fileInfoManager, OperationEventListener operationEvent, FileInfo file) {
        super(fileInfoManager, operationEvent);
        this.mDetailfileInfo = file;
    }

    protected Integer doInBackground(Void... params) {
        LogUtils.d(TAG, "doInBackground...");
        if (!this.mDetailfileInfo.isDirectory()) {
            long size = this.mDetailfileInfo.getFileSize();
            publishProgress(new ProgressInfo[]{new ProgressInfo("", 0, size, 0, size)});
        } else if (!MountPointManager.getInstance().isRootPath(this.mDetailfileInfo.getFileAbsolutePath())) {
            File[] files = this.mDetailfileInfo.getFile().listFiles();
            if (files != null) {
                for (File file : files) {
                    int ret = getContentSize(file);
                    if (ret < 0) {
                        LogUtils.i(TAG, "doInBackground,ret = " + ret);
                        return Integer.valueOf(ret);
                    }
                }
            }
        }
        return Integer.valueOf(0);
    }

    public int getContentSize(File root) {
        LogUtils.d(TAG, "getContentSize...");
        if (root.isDirectory()) {
            for (File file : root.listFiles()) {
                if (isCancelled()) {
                    return -7;
                }
                int ret = getContentSize(file);
                if (ret < 0) {
                    LogUtils.i(TAG, "getContentSize ,ret = " + ret);
                    return ret;
                }
            }
        }
        this.mTotal += root.length();
        publishProgress(new ProgressInfo[]{new ProgressInfo("", 0, this.mTotal, 0, this.mTotal)});
        return 0;
    }
}
