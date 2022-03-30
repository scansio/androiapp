package com.mediatek.filemanager.service;

import com.mediatek.filemanager.FileInfo;
import com.mediatek.filemanager.utils.LogUtils;

public class ProgressInfo {
    private static final String TAG = "ProgressInfo";
    private final int mCurrentNumber;
    private int mErrorCode = 0;
    private FileInfo mFileInfo = null;
    private final boolean mIsFailInfo;
    private final int mProgress;
    private final long mTotal;
    private final long mTotalNumber;
    private String mUpdateInfo = null;

    public ProgressInfo(String update, int progeress, long total, int currentNumber, long totalNumber) {
        this.mUpdateInfo = update;
        this.mProgress = progeress;
        this.mTotal = total;
        this.mIsFailInfo = false;
        this.mCurrentNumber = currentNumber;
        this.mTotalNumber = totalNumber;
    }

    public ProgressInfo(FileInfo fileInfo, int progeress, long total, int currentNumber, long totalNumber) {
        this.mFileInfo = fileInfo;
        this.mProgress = progeress;
        this.mTotal = total;
        this.mIsFailInfo = false;
        this.mCurrentNumber = currentNumber;
        this.mTotalNumber = totalNumber;
    }

    public ProgressInfo(int errorCode, boolean isFailInfo) {
        LogUtils.d(TAG, "ProgressInfo,errorCode=" + errorCode);
        this.mErrorCode = errorCode;
        this.mProgress = 0;
        this.mTotal = 0;
        this.mIsFailInfo = isFailInfo;
        this.mCurrentNumber = 0;
        this.mTotalNumber = 0;
    }

    public boolean isFailInfo() {
        return this.mIsFailInfo;
    }

    public FileInfo getFileInfo() {
        return this.mFileInfo;
    }

    public int getErrorCode() {
        return this.mErrorCode;
    }

    public String getUpdateInfo() {
        return this.mUpdateInfo;
    }

    public int getProgeress() {
        return this.mProgress;
    }

    public long getTotal() {
        return this.mTotal;
    }

    public int getCurrentNumber() {
        return this.mCurrentNumber;
    }

    public long getTotalNumber() {
        return this.mTotalNumber;
    }
}
