package com.mediatek.filemanager.service;

import android.content.Context;
import com.mediatek.filemanager.FileInfo;
import com.mediatek.filemanager.FileInfoManager;
import com.mediatek.filemanager.service.FileManagerService.OperationEventListener;
import com.mediatek.filemanager.utils.LogUtils;
import com.mediatek.filemanager.utils.PDebug;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

class ListFileTask extends BaseAsyncTask {
    private static final int FIRST_NEED_PROGRESS = 250;
    private static final int NEXT_NEED_PROGRESS = 200;
    private static final String TAG = "ListFileTask";
    private Context mContext;
    private final int mFilterType;
    private final String mPath;

    public ListFileTask(Context context, FileInfoManager fileInfoManager, OperationEventListener operationEvent, String path, int filterType) {
        super(fileInfoManager, operationEvent);
        this.mContext = context;
        this.mPath = path;
        this.mFilterType = filterType;
    }

    protected Integer doInBackground(Void... params) {
        Integer valueOf;
        PDebug.Start("ListFileTask --- doInBackground");
        synchronized (this.mContext.getApplicationContext()) {
            List<FileInfo> fileInfoList = new ArrayList();
            int progress = 0;
            long startLoadTime = System.currentTimeMillis();
            LogUtils.d(TAG, "doInBackground path = " + this.mPath);
            File dir = new File(this.mPath);
            if (dir.exists()) {
                File[] files = dir.listFiles();
                if (files == null) {
                    LogUtils.w(TAG, "doInBackground,directory is null");
                    PDebug.End("ListFileTask --- doInBackground");
                    valueOf = Integer.valueOf(-1);
                } else {
                    int total = files.length;
                    int nextUpdateTime = FIRST_NEED_PROGRESS;
                    LogUtils.d(TAG, "doInBackground, total = " + total);
                    int i = 0;
                    while (i < files.length) {
                        if (isCancelled()) {
                            LogUtils.w(TAG, " doInBackground,calcel.");
                            PDebug.End("ListFileTask --- doInBackground");
                            valueOf = Integer.valueOf(-1);
                            break;
                        }
                        if (this.mFilterType == 0 && files[i].getName().startsWith(".")) {
                            LogUtils.i(TAG, " doInBackground,start with.,contine.");
                        } else if (this.mFilterType != 1 || files[i].isDirectory()) {
                            this.mFileInfoManager.addItem(new FileInfo(files[i]));
                            progress++;
                            if (System.currentTimeMillis() - startLoadTime > ((long) nextUpdateTime)) {
                                startLoadTime = System.currentTimeMillis();
                                nextUpdateTime = NEXT_NEED_PROGRESS;
                                LogUtils.d(TAG, "doInBackground,pulish progress.");
                                ProgressInfo[] progressInfoArr = new ProgressInfo[1];
                                progressInfoArr[0] = new ProgressInfo("", progress, (long) total, progress, (long) total);
                                publishProgress(progressInfoArr);
                            }
                        } else {
                            LogUtils.i(TAG, " doInBackground,is not directory,continue..");
                        }
                        i++;
                    }
                    LogUtils.d(TAG, "doInBackground ERROR_CODE_SUCCESS");
                    PDebug.End("ListFileTask --- doInBackground");
                    valueOf = Integer.valueOf(0);
                }
            } else {
                LogUtils.w(TAG, "doInBackground,directory is not exist.");
                PDebug.End("ListFileTask --- doInBackground");
                valueOf = Integer.valueOf(-1);
            }
        }
        return valueOf;
    }
}
