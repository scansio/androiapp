package com.mediatek.filemanager.service;

import android.os.AsyncTask;
import android.os.AsyncTask.Status;
import com.mediatek.filemanager.FileInfoManager;
import com.mediatek.filemanager.service.FileManagerService.OperationEventListener;
import com.mediatek.filemanager.utils.LogUtils;
import com.mediatek.filemanager.utils.PDebug;

abstract class BaseAsyncTask extends AsyncTask<Void, ProgressInfo, Integer> {
    private static final String TAG = "BaseAsyncTask";
    protected FileInfoManager mFileInfoManager = null;
    protected boolean mIsTaskFinished = true;
    protected OperationEventListener mListener = null;

    public BaseAsyncTask(FileInfoManager fileInfoManager, OperationEventListener listener) {
        if (fileInfoManager == null) {
            throw new IllegalArgumentException();
        }
        this.mFileInfoManager = fileInfoManager;
        this.mListener = listener;
    }

    protected void onPreExecute() {
        this.mIsTaskFinished = false;
        if (this.mListener != null) {
            LogUtils.d(TAG, "onPreExecute");
            this.mListener.onTaskPrepare();
        }
    }

    protected void onPostExecute(Integer result) {
        PDebug.Start("BaseAsyncTask --- onPostExecute");
        if (this.mListener != null) {
            LogUtils.d(TAG, "onPostExecute");
            this.mListener.onTaskResult(result.intValue());
            this.mListener = null;
        }
        this.mIsTaskFinished = true;
        PDebug.End("BaseAsyncTask --- onPostExecute");
    }

    protected void onCancelled() {
        if (this.mListener != null) {
            LogUtils.d(TAG, "onCancelled()");
            this.mListener.onTaskResult(-7);
            this.mListener = null;
        }
        this.mIsTaskFinished = true;
    }

    protected void onProgressUpdate(ProgressInfo... values) {
        if (this.mListener != null && values != null && values[0] != null) {
            LogUtils.v(TAG, "onProgressUpdate");
            this.mListener.onTaskProgress(values[0]);
        }
    }

    protected void removeListener() {
        if (this.mListener != null) {
            LogUtils.d(TAG, "removeListener");
            this.mListener = null;
        }
    }

    public void setListener(OperationEventListener listener) {
        this.mListener = listener;
    }

    public boolean isTaskBusy() {
        LogUtils.d(TAG, "isTaskBusy,task status = " + getStatus());
        if (this.mIsTaskFinished || getStatus() == Status.FINISHED) {
            LogUtils.d(TAG, "isTaskBusy,retuen false.");
            return false;
        }
        LogUtils.d(TAG, "isTaskBusy,retuen true.");
        return true;
    }
}
