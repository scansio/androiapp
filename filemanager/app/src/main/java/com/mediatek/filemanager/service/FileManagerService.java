package com.mediatek.filemanager.service;

import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.IBinder;
import com.mediatek.filemanager.FileInfo;
import com.mediatek.filemanager.FileInfoManager;
import com.mediatek.filemanager.MountPointManager;
import com.mediatek.filemanager.utils.DrmManager;
import com.mediatek.filemanager.utils.LogUtils;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class FileManagerService extends Service {
    public static final int FILE_FILTER_TYPE_ALL = 2;
    public static final int FILE_FILTER_TYPE_DEFAULT = 0;
    public static final int FILE_FILTER_TYPE_FOLDER = 1;
    public static final int FILE_FILTER_TYPE_UNKOWN = -1;
    private static final String TAG = "FileManagerService";
    private final HashMap<String, FileManagerActivityInfo> mActivityMap = new HashMap();
    private ServiceBinder mBinder = null;

    public interface OperationEventListener {
        public static final int ERROR_CODE_BUSY = -100;
        public static final int ERROR_CODE_COPY_GREATER_4G_TO_FAT32 = -16;
        public static final int ERROR_CODE_COPY_NO_PERMISSION = -10;
        public static final int ERROR_CODE_CUT_SAME_PATH = -12;
        public static final int ERROR_CODE_DELETE_FAILS = -6;
        public static final int ERROR_CODE_DELETE_NO_PERMISSION = -15;
        public static final int ERROR_CODE_DELETE_UNSUCCESS = -13;
        public static final int ERROR_CODE_FILE_EXIST = -4;
        public static final int ERROR_CODE_MKDIR_UNSUCCESS = -11;
        public static final int ERROR_CODE_NAME_EMPTY = -2;
        public static final int ERROR_CODE_NAME_TOO_LONG = -3;
        public static final int ERROR_CODE_NAME_VALID = 100;
        public static final int ERROR_CODE_NOT_ENOUGH_SPACE = -5;
        public static final int ERROR_CODE_PASTE_TO_SUB = -8;
        public static final int ERROR_CODE_PASTE_UNSUCCESS = -14;
        public static final int ERROR_CODE_SUCCESS = 0;
        public static final int ERROR_CODE_UNKOWN = -9;
        public static final int ERROR_CODE_UNSUCCESS = -1;
        public static final int ERROR_CODE_USER_CANCEL = -7;

        void onTaskPrepare();

        void onTaskProgress(ProgressInfo progressInfo);

        void onTaskResult(int i);
    }

    private static class FileManagerActivityInfo {
        private FileInfoManager mFileInfoManager;
        private int mFilterType;
        private BaseAsyncTask mTask;

        private FileManagerActivityInfo() {
            this.mTask = null;
            this.mFileInfoManager = null;
            this.mFilterType = 0;
        }

        public void setTask(BaseAsyncTask task) {
            this.mTask = task;
        }

        public void setFileInfoManager(FileInfoManager fileInfoManager) {
            this.mFileInfoManager = fileInfoManager;
        }

        public void setFilterType(int filterType) {
            this.mFilterType = filterType;
        }

        BaseAsyncTask getTask() {
            return this.mTask;
        }

        FileInfoManager getFileInfoManager() {
            return this.mFileInfoManager;
        }

        int getFilterType() {
            return this.mFilterType;
        }
    }

    public class ServiceBinder extends Binder {
        public FileManagerService getServiceInstance() {
            return FileManagerService.this;
        }
    }

    public void onCreate() {
        super.onCreate();
        this.mBinder = new ServiceBinder();
        AsyncTask.setDefaultExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    public int onStartCommand(Intent intent, int flags, int startId) {
        LogUtils.d(TAG, "onStartCommand...");
        super.onStartCommand(intent, flags, startId);
        return 2;
    }

    public FileInfoManager initFileInfoManager(String activityName) {
        FileManagerActivityInfo activityInfo = (FileManagerActivityInfo) this.mActivityMap.get(activityName);
        if (activityInfo == null) {
            activityInfo = new FileManagerActivityInfo();
            activityInfo.setFileInfoManager(new FileInfoManager());
            this.mActivityMap.put(activityName, activityInfo);
        }
        return activityInfo.getFileInfoManager();
    }

    public IBinder onBind(Intent intent) {
        DrmManager.getInstance().init(this);
        return this.mBinder;
    }

    public boolean isBusy(String activityName) {
        FileManagerActivityInfo activityInfo = (FileManagerActivityInfo) this.mActivityMap.get(activityName);
        if (activityInfo == null) {
            LogUtils.w(TAG, "isBusy return false,because activityInfo is null!");
            return false;
        }
        BaseAsyncTask task = activityInfo.getTask();
        if (task != null) {
            return task.isTaskBusy();
        }
        return false;
    }

    private FileManagerActivityInfo getActivityInfo(String activityName) {
        FileManagerActivityInfo activityInfo = (FileManagerActivityInfo) this.mActivityMap.get(activityName);
        if (activityInfo != null) {
            return activityInfo;
        }
        throw new IllegalArgumentException("this activity not init in Service");
    }

    public void setListType(int type, String activityName) {
        getActivityInfo(activityName).setFilterType(type);
    }

    public void createFolder(String activityName, String destFolder, OperationEventListener listener) {
        LogUtils.d(TAG, " createFolder Start ");
        if (isBusy(activityName)) {
            listener.onTaskResult(-100);
            return;
        }
        FileInfoManager fileInfoManager = getActivityInfo(activityName).getFileInfoManager();
        int filterType = getActivityInfo(activityName).getFilterType();
        if (fileInfoManager != null) {
            BaseAsyncTask task = new CreateFolderTask(fileInfoManager, listener, this, destFolder, filterType);
            getActivityInfo(activityName).setTask(task);
            task.execute(new Void[0]);
        }
    }

    public void rename(String activityName, FileInfo srcFile, FileInfo dstFile, OperationEventListener listener) {
        LogUtils.d(TAG, " rename Start,activityName = " + activityName);
        if (isBusy(activityName)) {
            listener.onTaskResult(-100);
            return;
        }
        FileInfoManager fileInfoManager = getActivityInfo(activityName).getFileInfoManager();
        int filterType = getActivityInfo(activityName).getFilterType();
        if (fileInfoManager != null) {
            BaseAsyncTask task = new RenameTask(fileInfoManager, listener, this, srcFile, dstFile, filterType);
            getActivityInfo(activityName).setTask(task);
            task.execute(new Void[0]);
        }
    }

    private int filterPasteList(List<FileInfo> fileInfoList, String destFolder) {
        int remove = 0;
        Iterator<FileInfo> iterator = fileInfoList.iterator();
        while (iterator.hasNext()) {
            FileInfo fileInfo = (FileInfo) iterator.next();
            if (fileInfo.isDirectory() && (destFolder + MountPointManager.SEPARATOR).startsWith(fileInfo.getFileAbsolutePath() + MountPointManager.SEPARATOR)) {
                iterator.remove();
                remove++;
            }
        }
        return remove;
    }

    public void deleteFiles(String activityName, List<FileInfo> fileInfoList, OperationEventListener listener) {
        LogUtils.d(TAG, " deleteFiles Start,activityName = " + activityName);
        if (isBusy(activityName)) {
            listener.onTaskResult(-100);
            return;
        }
        FileInfoManager fileInfoManager = getActivityInfo(activityName).getFileInfoManager();
        if (fileInfoManager != null) {
            BaseAsyncTask task = new DeleteFilesTask(fileInfoManager, listener, this, fileInfoList);
            getActivityInfo(activityName).setTask(task);
            task.execute(new Void[0]);
        }
    }

    public void cancel(String activityName) {
        LogUtils.d(TAG, " cancel service,activityName = " + activityName);
        BaseAsyncTask task = getActivityInfo(activityName).getTask();
        if (task != null) {
            task.cancel(true);
        }
    }

    public void pasteFiles(String activityName, List<FileInfo> fileInfoList, String dstFolder, int type, OperationEventListener listener) {
        LogUtils.d(TAG, " pasteFiles Start,activityName = " + activityName);
        if (isBusy(activityName)) {
            listener.onTaskResult(-100);
            return;
        }
        if (filterPasteList(fileInfoList, dstFolder) > 0) {
            listener.onTaskResult(-8);
        }
        FileInfoManager fileInfoManager = getActivityInfo(activityName).getFileInfoManager();
        if (fileInfoManager == null) {
            LogUtils.w(TAG, "mFileInfoManagerMap.get FileInfoManager = null");
            listener.onTaskResult(-9);
        } else if (fileInfoList.size() > 0) {
            BaseAsyncTask task;
            switch (type) {
                case 1:
                    if (isCutSamePath(fileInfoList, dstFolder)) {
                        listener.onTaskResult(-12);
                        return;
                    }
                    task = new CutPasteFilesTask(fileInfoManager, listener, getApplicationContext(), fileInfoList, dstFolder);
                    getActivityInfo(activityName).setTask(task);
                    task.execute(new Void[0]);
                    return;
                case 2:
                    task = new CopyPasteFilesTask(fileInfoManager, listener, getApplicationContext(), fileInfoList, dstFolder);
                    getActivityInfo(activityName).setTask(task);
                    task.execute(new Void[0]);
                    return;
                default:
                    listener.onTaskResult(-9);
                    return;
            }
        }
    }

    private boolean isCutSamePath(List<FileInfo> fileInfoList, String dstFolder) {
        for (FileInfo fileInfo : fileInfoList) {
            if (fileInfo.getFileParentPath().equals(dstFolder)) {
                return true;
            }
        }
        return false;
    }

    public void listFiles(String activityName, String path, OperationEventListener listener) {
        BaseAsyncTask task;
        LogUtils.d(TAG, "listFiles,activityName = " + activityName + ",path = " + path);
        if (isBusy(activityName)) {
            LogUtils.d(TAG, "listFiles, cancel other background task...");
            task = getActivityInfo(activityName).getTask();
            if (task != null) {
                task.removeListener();
                task.cancel(true);
            }
        }
        LogUtils.d(TAG, "listFiles,do list.");
        FileInfoManager fileInfoManager = getActivityInfo(activityName).getFileInfoManager();
        int filterType = getActivityInfo(activityName).getFilterType();
        if (fileInfoManager != null) {
            LogUtils.d(TAG, "listFiles fiterType = " + filterType);
            task = new ListFileTask(getApplicationContext(), fileInfoManager, listener, path, filterType);
            getActivityInfo(activityName).setTask(task);
            task.execute(new Void[0]);
        }
    }

    public void getDetailInfo(String activityName, FileInfo file, OperationEventListener listener) {
        LogUtils.d(TAG, "getDetailInfo,activityName = " + activityName);
        if (isBusy(activityName)) {
            listener.onTaskResult(-100);
            return;
        }
        FileInfoManager fileInfoManager = getActivityInfo(activityName).getFileInfoManager();
        if (fileInfoManager != null) {
            BaseAsyncTask task = new DetailInfoTask(fileInfoManager, listener, file);
            getActivityInfo(activityName).setTask(task);
            task.execute(new Void[0]);
        }
    }

    public void disconnected(String activityName) {
        LogUtils.d(TAG, "disconnected,activityName = " + activityName);
        BaseAsyncTask task = getActivityInfo(activityName).getTask();
        if (task != null) {
            task.removeListener();
        }
    }

    public void reconnected(String activityName, OperationEventListener listener) {
        LogUtils.d(TAG, "reconnected,activityName = " + activityName);
        BaseAsyncTask task = getActivityInfo(activityName).getTask();
        if (task != null) {
            task.setListener(listener);
        }
    }

    public boolean isDetailTask(String activityName) {
        FileManagerActivityInfo aInfo = (FileManagerActivityInfo) this.mActivityMap.get(activityName);
        if (aInfo == null) {
            LogUtils.d(TAG, "activity is not attach: " + activityName);
            return false;
        }
        BaseAsyncTask task = aInfo.getTask();
        if (task == null || !(task instanceof DetailInfoTask)) {
            return false;
        }
        return true;
    }

    public void search(String activityName, String searchName, String path, OperationEventListener operationEvent) {
        LogUtils.d(TAG, "search, activityName = " + activityName + ",searchName = " + searchName + ",path = " + path);
        if (isBusy(activityName)) {
            cancel(activityName);
            return;
        }
        FileInfoManager fileInfoManager = getActivityInfo(activityName).getFileInfoManager();
        if (fileInfoManager != null) {
            BaseAsyncTask task = new SearchTask(fileInfoManager, operationEvent, searchName, path, getContentResolver());
            getActivityInfo(activityName).setTask(task);
            task.execute(new Void[0]);
        }
    }
}
