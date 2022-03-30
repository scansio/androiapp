package com.mediatek.filemanager.service;

import android.content.Context;
import com.mediatek.filemanager.FileInfo;
import com.mediatek.filemanager.FileInfoManager;
import com.mediatek.filemanager.MountPointManager;
import com.mediatek.filemanager.service.FileManagerService.OperationEventListener;
import com.mediatek.filemanager.service.MultiMediaStoreHelper.DeleteMediaStoreHelper;
import com.mediatek.filemanager.service.MultiMediaStoreHelper.PasteMediaStoreHelper;
import com.mediatek.filemanager.utils.FileUtils;
import com.mediatek.filemanager.utils.LogUtils;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

abstract class FileOperationTask extends BaseAsyncTask {
    protected static final int BUFFER_SIZE = 2097152;
    private static final String TAG = "FileOperationTask";
    protected static final int TOTAL = 100;
    protected Context mContext;
    protected MediaStoreHelper mMediaProviderHelper;

    static class CopyPasteFilesTask extends FileOperationTask {
        private static final String TAG = "CopyPasteFilesTask";
        String mDstFolder = null;
        List<FileInfo> mSrcList = null;

        public CopyPasteFilesTask(FileInfoManager fileInfoManager, OperationEventListener operationEvent, Context context, List<FileInfo> src, String destFolder) {
            super(fileInfoManager, operationEvent, context);
            this.mSrcList = src;
            this.mDstFolder = destFolder;
        }

        protected Integer doInBackground(Void... params) {
            Integer valueOf;
            synchronized (this.mContext.getApplicationContext()) {
                LogUtils.i(TAG, "doInBackground...");
                List<File> fileList = new ArrayList();
                UpdateInfo updateInfo = new UpdateInfo();
                int ret = getAllFileList(this.mSrcList, fileList, updateInfo);
                if (ret < 0) {
                    LogUtils.i(TAG, "doInBackground,ret = " + ret);
                    valueOf = Integer.valueOf(ret);
                } else {
                    PasteMediaStoreHelper pasteMediaStoreHelper = new PasteMediaStoreHelper(this.mMediaProviderHelper);
                    pasteMediaStoreHelper.setDstFolder(this.mDstFolder);
                    HashMap<File, FileInfo> copyFileInfoMap = new HashMap();
                    for (FileInfo fileInfo : this.mSrcList) {
                        copyFileInfoMap.put(fileInfo.getFile(), fileInfo);
                    }
                    if (isGreaterThan4G(updateInfo)) {
                        if (isFat32Disk(this.mDstFolder)) {
                            LogUtils.i(TAG, "doInBackground, destination is FAT32.");
                            valueOf = Integer.valueOf(-16);
                        }
                    }
                    if (isEnoughSpace(updateInfo, this.mDstFolder)) {
                        publishProgress(new ProgressInfo[]{new ProgressInfo("", 0, 100, 0, updateInfo.getTotalNumber())});
                        byte[] buffer = new byte[FileOperationTask.BUFFER_SIZE];
                        HashMap<String, String> pathMap = new HashMap();
                        if (!fileList.isEmpty()) {
                            pathMap.put(((File) fileList.get(0)).getParent(), this.mDstFolder);
                        }
                        for (File file : fileList) {
                            File dstFile = getDstFile(pathMap, file, this.mDstFolder);
                            if (isCancelled()) {
                                pasteMediaStoreHelper.updateRecords();
                                LogUtils.i(TAG, "doInBackground,user cancel.");
                                valueOf = Integer.valueOf(-7);
                                break;
                            } else if (dstFile == null) {
                                publishProgress(new ProgressInfo[]{new ProgressInfo(-14, true)});
                            } else if (file.isDirectory()) {
                                if (mkdir(pathMap, file, dstFile)) {
                                    pasteMediaStoreHelper.addRecord(dstFile.getAbsolutePath());
                                    addItem(copyFileInfoMap, file, dstFile);
                                    updateInfo.updateProgress(file.length());
                                    updateInfo.updateCurrentNumber(1);
                                    updateProgressWithTime(updateInfo, file);
                                }
                            } else if (FileInfo.isDrmFile(file.getName()) || !file.canRead()) {
                                publishProgress(new ProgressInfo[]{new ProgressInfo(-10, true)});
                                updateInfo.updateProgress(file.length());
                                updateInfo.updateCurrentNumber(1);
                            } else {
                                updateInfo.updateCurrentNumber(1);
                                ret = copyFile(buffer, file, dstFile, updateInfo);
                                if (ret == -7) {
                                    pasteMediaStoreHelper.updateRecords();
                                    valueOf = Integer.valueOf(ret);
                                    break;
                                } else if (ret < 0) {
                                    publishProgress(new ProgressInfo[]{new ProgressInfo(ret, true)});
                                    updateInfo.updateProgress(file.length());
                                    updateInfo.updateCurrentNumber(1);
                                } else {
                                    pasteMediaStoreHelper.addRecord(dstFile.getAbsolutePath());
                                    addItem(copyFileInfoMap, file, dstFile);
                                }
                            }
                        }
                        pasteMediaStoreHelper.updateRecords();
                        LogUtils.i(TAG, "doInBackground,return success.");
                        valueOf = Integer.valueOf(0);
                    } else {
                        LogUtils.i(TAG, "doInBackground, not enough space.");
                        valueOf = Integer.valueOf(-5);
                    }
                }
            }
            return valueOf;
        }
    }

    static class CreateFolderTask extends FileOperationTask {
        private static final String TAG = "CreateFolderTask";
        private final String mDstFolder;
        int mFilterType;

        public CreateFolderTask(FileInfoManager fileInfoManager, OperationEventListener operationEvent, Context context, String dstFolder, int filterType) {
            super(fileInfoManager, operationEvent, context);
            this.mDstFolder = dstFolder;
            this.mFilterType = filterType;
        }

        protected Integer doInBackground(Void... params) {
            Integer valueOf;
            synchronized (this.mContext.getApplicationContext()) {
                LogUtils.i(TAG, "doInBackground...");
                int ret = FileUtils.checkFileName(FileUtils.getFileName(this.mDstFolder));
                if (ret < 0) {
                    LogUtils.i(TAG, "doInBackground,ret = " + ret);
                    valueOf = Integer.valueOf(ret);
                } else {
                    String dstFile = this.mDstFolder.trim();
                    LogUtils.d(TAG, "Create a new folder,dstFile=" + dstFile);
                    File dir = new File(dstFile);
                    LogUtils.d(TAG, "The folder to be created exist: " + dir.exists());
                    if (dir.exists()) {
                        LogUtils.i(TAG, "doInBackground,dir is exist.");
                        valueOf = Integer.valueOf(-4);
                    } else if (new File(FileUtils.getFilePath(this.mDstFolder)).getFreeSpace() <= 0) {
                        LogUtils.i(TAG, "doInBackground,not enough space.");
                        valueOf = Integer.valueOf(-5);
                    } else {
                        if (dstFile.endsWith(".")) {
                            LogUtils.i(TAG, "doInBackground,end with dot.");
                            while (dstFile.endsWith(".")) {
                                dstFile = dstFile.substring(0, dstFile.length() - 1);
                            }
                            dir = new File(dstFile);
                        }
                        if (dir.mkdirs()) {
                            FileInfo fileInfo = new FileInfo(dir);
                            if (!fileInfo.isHideFile() || this.mFilterType == 2) {
                                this.mFileInfoManager.addItem(fileInfo);
                            }
                            this.mMediaProviderHelper.scanPathforMediaStore(fileInfo.getFileAbsolutePath());
                            LogUtils.i(TAG, "doInBackground, mkdir return success.");
                            valueOf = Integer.valueOf(0);
                        } else {
                            valueOf = Integer.valueOf(-1);
                        }
                    }
                }
            }
            return valueOf;
        }
    }

    static class CutPasteFilesTask extends FileOperationTask {
        private static final String TAG = "CutPasteFilesTask";
        private final String mDstFolder;
        private final List<FileInfo> mSrcList;

        public CutPasteFilesTask(FileInfoManager fileInfoManager, OperationEventListener operationEvent, Context context, List<FileInfo> src, String destFolder) {
            super(fileInfoManager, operationEvent, context);
            this.mSrcList = src;
            this.mDstFolder = destFolder;
        }

        protected Integer doInBackground(Void... params) {
            Integer valueOf;
            synchronized (this.mContext.getApplicationContext()) {
                LogUtils.i(TAG, "doInBackground...");
                if (this.mSrcList.isEmpty()) {
                    LogUtils.i(TAG, "doInBackground,src list is empty.");
                    valueOf = Integer.valueOf(-14);
                } else if (isSameRoot(((FileInfo) this.mSrcList.get(0)).getFileAbsolutePath(), this.mDstFolder)) {
                    valueOf = cutPasteInSameCard();
                } else {
                    valueOf = cutPasteInDiffCard();
                }
            }
            return valueOf;
        }

        private boolean isSameRoot(String srcPath, String dstPath) {
            MountPointManager mpm = MountPointManager.getInstance();
            String srcMountPoint = mpm.getRealMountPointPath(srcPath);
            String dstMountPoint = mpm.getRealMountPointPath(dstPath);
            if (srcMountPoint == null || dstMountPoint == null || !srcMountPoint.equals(dstMountPoint)) {
                return false;
            }
            return true;
        }

        private Integer cutPasteInSameCard() {
            LogUtils.i(TAG, "cutPasteInSameCard.");
            UpdateInfo updateInfo = new UpdateInfo();
            updateInfo.updateTotal((long) this.mSrcList.size());
            updateInfo.updateTotalNumber((long) this.mSrcList.size());
            publishProgress(new ProgressInfo[]{new ProgressInfo("", 0, 100, 0, (long) this.mSrcList.size())});
            PasteMediaStoreHelper pasteMediaStoreHelper = new PasteMediaStoreHelper(this.mMediaProviderHelper);
            DeleteMediaStoreHelper deleteMediaStoreHelper = new DeleteMediaStoreHelper(this.mMediaProviderHelper);
            pasteMediaStoreHelper.setDstFolder(this.mDstFolder);
            for (FileInfo fileInfo : this.mSrcList) {
                File newFile = checkFileNameAndRename(new File(this.mDstFolder + MountPointManager.SEPARATOR + fileInfo.getFileName()));
                if (isCancelled()) {
                    pasteMediaStoreHelper.updateRecords();
                    deleteMediaStoreHelper.updateRecords();
                    return Integer.valueOf(-7);
                } else if (newFile == null) {
                    LogUtils.i(TAG, "cutPasteInSameCard,newFile is null.");
                    publishProgress(new ProgressInfo[]{new ProgressInfo(-14, true)});
                } else {
                    if (fileInfo.getFile().renameTo(newFile)) {
                        updateInfo.updateProgress(1);
                        updateInfo.updateCurrentNumber(1);
                        FileInfo newFileInfo = new FileInfo(newFile);
                        this.mFileInfoManager.addItem(newFileInfo);
                        if (newFile.isDirectory()) {
                            this.mMediaProviderHelper.updateInMediaStore(newFileInfo.getFileAbsolutePath(), fileInfo.getFileAbsolutePath());
                        } else {
                            deleteMediaStoreHelper.addRecord(fileInfo.getFileAbsolutePath());
                            pasteMediaStoreHelper.addRecord(newFile.getAbsolutePath());
                        }
                    } else {
                        publishProgress(new ProgressInfo[]{new ProgressInfo(-14, true)});
                    }
                    updateProgressWithTime(updateInfo, fileInfo.getFile());
                }
            }
            pasteMediaStoreHelper.updateRecords();
            deleteMediaStoreHelper.updateRecords();
            ProgressInfo[] progressInfoArr = new ProgressInfo[1];
            progressInfoArr[0] = new ProgressInfo("", 100, 100, (int) updateInfo.getCurrentNumber(), updateInfo.getTotalNumber());
            publishProgress(progressInfoArr);
            return Integer.valueOf(0);
        }

        private Integer cutPasteInDiffCard() {
            LogUtils.i(TAG, "cutPasteInDiffCard...");
            List<File> fileList = new ArrayList();
            UpdateInfo updateInfo = new UpdateInfo();
            int ret = getAllFileList(this.mSrcList, fileList, updateInfo);
            if (ret < 0) {
                LogUtils.i(TAG, "cutPasteInDiffCard,ret = " + ret);
                return Integer.valueOf(ret);
            }
            if (isGreaterThan4G(updateInfo)) {
                if (isFat32Disk(this.mDstFolder)) {
                    LogUtils.i(TAG, "cutPasteInDiffCard, destination is FAT32.");
                    return Integer.valueOf(-16);
                }
            }
            if (isEnoughSpace(updateInfo, this.mDstFolder)) {
                List<File> romoveFolderFiles = new LinkedList();
                publishProgress(new ProgressInfo[]{new ProgressInfo("", 0, 100, 0, (long) fileList.size())});
                byte[] buffer = new byte[FileOperationTask.BUFFER_SIZE];
                HashMap<String, String> pathMap = new HashMap();
                if (!fileList.isEmpty()) {
                    pathMap.put(((File) fileList.get(0)).getParent(), this.mDstFolder);
                }
                PasteMediaStoreHelper pasteMediaStoreHelper = new PasteMediaStoreHelper(this.mMediaProviderHelper);
                DeleteMediaStoreHelper deleteMediaStoreHelper = new DeleteMediaStoreHelper(this.mMediaProviderHelper);
                pasteMediaStoreHelper.setDstFolder(this.mDstFolder);
                HashMap<File, FileInfo> cutFileInfoMap = new HashMap();
                for (FileInfo fileInfo : this.mSrcList) {
                    cutFileInfoMap.put(fileInfo.getFile(), fileInfo);
                }
                for (File file : fileList) {
                    File dstFile = getDstFile(pathMap, file, this.mDstFolder);
                    if (isCancelled()) {
                        pasteMediaStoreHelper.updateRecords();
                        deleteMediaStoreHelper.updateRecords();
                        LogUtils.i(TAG, "cutPasteInDiffCard,user cancel.");
                        return Integer.valueOf(-7);
                    } else if (dstFile == null) {
                        publishProgress(new ProgressInfo[]{new ProgressInfo(-14, true)});
                    } else if (!file.isDirectory()) {
                        updateInfo.updateCurrentNumber(1);
                        ret = copyFile(buffer, file, dstFile, updateInfo);
                        LogUtils.i(TAG, "cutPasteInDiffCard ret2 = " + ret);
                        if (ret == -7) {
                            pasteMediaStoreHelper.updateRecords();
                            deleteMediaStoreHelper.updateRecords();
                            return Integer.valueOf(ret);
                        } else if (ret < 0) {
                            publishProgress(new ProgressInfo[]{new ProgressInfo(-14, true)});
                            updateInfo.updateProgress(file.length());
                            updateInfo.updateCurrentNumber(1);
                        } else {
                            addItem(cutFileInfoMap, file, dstFile);
                            pasteMediaStoreHelper.addRecord(dstFile.getAbsolutePath());
                            if (deleteFile(file)) {
                                deleteMediaStoreHelper.addRecord(file.getAbsolutePath());
                            }
                        }
                    } else if (mkdir(pathMap, file, dstFile)) {
                        pasteMediaStoreHelper.addRecord(dstFile.getAbsolutePath());
                        addItem(cutFileInfoMap, file, dstFile);
                        updateInfo.updateProgress(file.length());
                        updateInfo.updateCurrentNumber(1);
                        romoveFolderFiles.add(0, file);
                        updateProgressWithTime(updateInfo, file);
                    }
                }
                for (File file2 : romoveFolderFiles) {
                    if (file2.delete()) {
                        deleteMediaStoreHelper.addRecord(file2.getAbsolutePath());
                    }
                }
                pasteMediaStoreHelper.updateRecords();
                deleteMediaStoreHelper.updateRecords();
                LogUtils.i(TAG, "cutPasteInDiffCard,return success.");
                return Integer.valueOf(0);
            }
            LogUtils.i(TAG, "cutPasteInDiffCard,not enough space.");
            return Integer.valueOf(-5);
        }
    }

    static class DeleteFilesTask extends FileOperationTask {
        private static final String TAG = "DeleteFilesTask";
        private final List<FileInfo> mDeletedFilesInfo;

        public DeleteFilesTask(FileInfoManager fileInfoManager, OperationEventListener operationEvent, Context context, List<FileInfo> fileInfoList) {
            super(fileInfoManager, operationEvent, context);
            this.mDeletedFilesInfo = fileInfoList;
        }

        protected Integer doInBackground(Void... params) {
            Integer valueOf;
            synchronized (this.mContext.getApplicationContext()) {
                LogUtils.i(TAG, "doInBackground...");
                List<File> deletefileList = new ArrayList();
                UpdateInfo updateInfo = new UpdateInfo();
                int ret = getAllDeleteFiles(this.mDeletedFilesInfo, deletefileList);
                if (ret < 0) {
                    LogUtils.i(TAG, "doInBackground,ret = " + ret);
                    valueOf = Integer.valueOf(ret);
                } else {
                    DeleteMediaStoreHelper deleteMediaStoreHelper = new DeleteMediaStoreHelper(this.mMediaProviderHelper);
                    HashMap<File, FileInfo> deleteFileInfoMap = new HashMap();
                    for (FileInfo fileInfo : this.mDeletedFilesInfo) {
                        deleteFileInfoMap.put(fileInfo.getFile(), fileInfo);
                    }
                    updateInfo.updateTotal((long) deletefileList.size());
                    updateInfo.updateTotalNumber((long) deletefileList.size());
                    publishProgress(new ProgressInfo[]{new ProgressInfo("", (int) updateInfo.getProgress(), updateInfo.getTotal(), (int) updateInfo.getCurrentNumber(), updateInfo.getTotalNumber())});
                    for (File file : deletefileList) {
                        if (isCancelled()) {
                            deleteMediaStoreHelper.updateRecords();
                            LogUtils.i(TAG, "doInBackground,user cancel it.");
                            valueOf = Integer.valueOf(-7);
                            break;
                        }
                        if (deleteFile(file)) {
                            deleteMediaStoreHelper.addRecord(file.getAbsolutePath());
                            removeItem(deleteFileInfoMap, file, file);
                        }
                        updateInfo.updateProgress(1);
                        updateInfo.updateCurrentNumber(1);
                        if (updateInfo.needUpdate()) {
                            publishProgress(new ProgressInfo[]{new ProgressInfo(file.getName(), (int) updateInfo.getProgress(), updateInfo.getTotal(), (int) updateInfo.getCurrentNumber(), updateInfo.getTotalNumber())});
                        }
                    }
                    deleteMediaStoreHelper.updateRecords();
                    LogUtils.i(TAG, "doInBackground,return sucsess..");
                    valueOf = Integer.valueOf(0);
                }
            }
            return valueOf;
        }
    }

    static class RenameTask extends FileOperationTask {
        private static final String TAG = "RenameTask";
        private final FileInfo mDstFileInfo;
        int mFilterType = 0;
        private final FileInfo mSrcFileInfo;

        public RenameTask(FileInfoManager fileInfoManager, OperationEventListener operationEvent, Context context, FileInfo srcFile, FileInfo dstFile, int filterType) {
            super(fileInfoManager, operationEvent, context);
            this.mDstFileInfo = dstFile;
            this.mSrcFileInfo = srcFile;
            this.mFilterType = filterType;
        }

        protected Integer doInBackground(Void... params) {
            Integer valueOf;
            synchronized (this.mContext.getApplicationContext()) {
                LogUtils.i(TAG, "doInBackground...");
                String dstFile = this.mDstFileInfo.getFileAbsolutePath().trim();
                LogUtils.d(TAG, "rename dstFile = " + dstFile);
                int ret = FileUtils.checkFileName(FileUtils.getFileName(dstFile));
                if (ret < 0) {
                    LogUtils.i(TAG, "doInBackground,ret = " + ret);
                    valueOf = Integer.valueOf(ret);
                } else {
                    File newFile = new File(dstFile);
                    File oldFile = new File(this.mSrcFileInfo.getFileAbsolutePath());
                    if (newFile.exists()) {
                        LogUtils.i(TAG, "doInBackground,new file is exist.");
                        valueOf = Integer.valueOf(-4);
                    } else {
                        if (dstFile.endsWith(".")) {
                            LogUtils.i(TAG, "doInBackground,end with dot.");
                            while (dstFile.endsWith(".")) {
                                dstFile = dstFile.substring(0, dstFile.length() - 1);
                            }
                            newFile = new File(dstFile);
                            if (newFile.exists()) {
                                LogUtils.i(TAG, "doInBackground,new file is exist.");
                                valueOf = Integer.valueOf(-4);
                            }
                        }
                        if (oldFile.renameTo(newFile)) {
                            FileInfo newFileInfo = new FileInfo(newFile);
                            this.mFileInfoManager.removeItem(this.mSrcFileInfo);
                            if (!newFileInfo.isHideFile() || this.mFilterType == 2) {
                                this.mFileInfoManager.addItem(newFileInfo);
                            }
                            this.mMediaProviderHelper.updateInMediaStore(newFileInfo.getFileAbsolutePath(), this.mSrcFileInfo.getFileAbsolutePath());
                            LogUtils.i(TAG, "doInBackground,return success.");
                            valueOf = Integer.valueOf(0);
                        } else {
                            valueOf = Integer.valueOf(-1);
                        }
                    }
                }
            }
            return valueOf;
        }
    }

    static class UpdateInfo {
        protected static final int NEED_UPDATE_TIME = 200;
        private long mCurrentNumber;
        private long mProgressSize;
        private long mStartOperationTime;
        private long mTotalNumber;
        private long mTotalSize;

        public UpdateInfo() {
            this.mStartOperationTime = 0;
            this.mProgressSize = 0;
            this.mTotalSize = 0;
            this.mCurrentNumber = 0;
            this.mTotalNumber = 0;
            this.mStartOperationTime = System.currentTimeMillis();
        }

        public long getProgress() {
            return this.mProgressSize;
        }

        public long getTotal() {
            return this.mTotalSize;
        }

        public long getCurrentNumber() {
            return this.mCurrentNumber;
        }

        public long getTotalNumber() {
            return this.mTotalNumber;
        }

        public void updateProgress(long addSize) {
            this.mProgressSize += addSize;
        }

        public void updateTotal(long addSize) {
            this.mTotalSize += addSize;
        }

        public void updateCurrentNumber(long addNumber) {
            this.mCurrentNumber += addNumber;
        }

        public void updateTotalNumber(long addNumber) {
            this.mTotalNumber += addNumber;
        }

        public boolean needUpdate() {
            if (System.currentTimeMillis() - this.mStartOperationTime <= 200) {
                return false;
            }
            this.mStartOperationTime = System.currentTimeMillis();
            return true;
        }
    }

    public FileOperationTask(FileInfoManager fileInfoManager, OperationEventListener operationEvent, Context context) {
        super(fileInfoManager, operationEvent);
        if (context == null) {
            LogUtils.e(TAG, "construct FileOperationTask exception! ");
            throw new IllegalArgumentException();
        }
        this.mContext = context;
        this.mMediaProviderHelper = new MediaStoreHelper(context, this);
    }

    protected File getDstFile(HashMap<String, String> pathMap, File file, String defPath) {
        LogUtils.d(TAG, "getDstFile.");
        String curPath = (String) pathMap.get(file.getParent());
        if (curPath == null) {
            curPath = defPath;
        }
        return checkFileNameAndRename(new File(curPath, file.getName()));
    }

    protected boolean deleteFile(File file) {
        if (file == null) {
            publishProgress(new ProgressInfo[]{new ProgressInfo(-13, true)});
        } else if (file.canWrite() && file.delete()) {
            return true;
        } else {
            LogUtils.d(TAG, "deleteFile fail,file name = " + file.getName());
            publishProgress(new ProgressInfo[]{new ProgressInfo(-15, true)});
        }
        return false;
    }

    protected boolean mkdir(HashMap<String, String> pathMap, File srcFile, File dstFile) {
        LogUtils.d(TAG, "mkdir,srcFile = " + srcFile + ",dstFile = " + dstFile);
        if (srcFile.exists() && srcFile.canRead() && dstFile.mkdirs()) {
            pathMap.put(srcFile.getAbsolutePath(), dstFile.getAbsolutePath());
            return true;
        }
        publishProgress(new ProgressInfo[]{new ProgressInfo(-14, true)});
        return false;
    }

    private long calcNeedSpace(List<File> fileList) {
        long need = 0;
        for (File file : fileList) {
            need += file.length();
        }
        return need;
    }

    protected boolean isGreaterThan4G(UpdateInfo updateInfo) {
        if (updateInfo.getTotal() > 4294967296L) {
            LogUtils.d(TAG, "isGreaterThan4G true.");
            return true;
        }
        LogUtils.d(TAG, "isGreaterThan4G false.");
        return false;
    }

    protected boolean isFat32Disk(String path) {
        return MountPointManager.getInstance().isFat32Disk(path);
    }

    protected boolean isEnoughSpace(UpdateInfo updateInfo, String dstFolder) {
        LogUtils.d(TAG, "isEnoughSpace,dstFolder = " + dstFolder);
        if (updateInfo.getTotal() > new File(dstFolder).getFreeSpace()) {
            return false;
        }
        return true;
    }

    protected int getAllDeleteFiles(List<FileInfo> fileInfoList, List<File> deleteList) {
        int ret = 0;
        for (FileInfo fileInfo : fileInfoList) {
            ret = getAllDeleteFile(fileInfo.getFile(), deleteList);
            if (ret < 0) {
                break;
            }
        }
        return ret;
    }

    protected int getAllDeleteFile(File deleteFile, List<File> deleteList) {
        if (isCancelled()) {
            LogUtils.i(TAG, "getAllDeleteFile,cancel. ");
            return -7;
        } else if (deleteFile.isDirectory()) {
            deleteList.add(0, deleteFile);
            if (!deleteFile.canWrite()) {
                return 0;
            }
            File[] files = deleteFile.listFiles();
            if (files == null) {
                LogUtils.i(TAG, "getAllDeleteFile,files is null. ");
                return -1;
            }
            for (File file : files) {
                getAllDeleteFile(file, deleteList);
            }
            return 0;
        } else {
            deleteList.add(0, deleteFile);
            return 0;
        }
    }

    protected int getAllFileList(List<FileInfo> srcList, List<File> resultList, UpdateInfo updateInfo) {
        int ret = 0;
        for (FileInfo fileInfo : srcList) {
            ret = getAllFile(fileInfo.getFile(), resultList, updateInfo);
            if (ret < 0) {
                break;
            }
        }
        return ret;
    }

    protected int getAllFile(File srcFile, List<File> fileList, UpdateInfo updateInfo) {
        if (isCancelled()) {
            LogUtils.i(TAG, "getAllFile, cancel.");
            return -7;
        }
        fileList.add(srcFile);
        updateInfo.updateTotal(srcFile.length());
        updateInfo.updateTotalNumber(1);
        if (srcFile.isDirectory() && srcFile.canRead()) {
            File[] files = srcFile.listFiles();
            if (files == null) {
                return -1;
            }
            for (File file : files) {
                int ret = getAllFile(file, fileList, updateInfo);
                if (ret < 0) {
                    return ret;
                }
            }
        }
        return 0;
    }

    /* JADX WARNING: Removed duplicated region for block: B:60:0x015b A:{SYNTHETIC, Splitter: B:60:0x015b} */
    /* JADX WARNING: Removed duplicated region for block: B:63:0x0160 A:{Catch:{ IOException -> 0x026e, all -> 0x02a3 }} */
    /* JADX WARNING: Removed duplicated region for block: B:76:0x01d3 A:{SYNTHETIC, Splitter: B:76:0x01d3} */
    /* JADX WARNING: Removed duplicated region for block: B:79:0x01d8 A:{Catch:{ IOException -> 0x0209, all -> 0x023d }} */
    protected int copyFile(byte[] r22, java.io.File r23, java.io.File r24, com.mediatek.filemanager.service.FileOperationTask.UpdateInfo r25) {
        /*
        r21 = this;
        if (r22 == 0) goto L_0x0006;
    L_0x0002:
        if (r23 == 0) goto L_0x0006;
    L_0x0004:
        if (r24 != 0) goto L_0x0010;
    L_0x0006:
        r3 = "FileOperationTask";
        r4 = "copyFile, invalid parameter.";
        com.mediatek.filemanager.utils.LogUtils.i(r3, r4);
        r17 = -14;
    L_0x000f:
        return r17;
    L_0x0010:
        r11 = 0;
        r15 = 0;
        r17 = 0;
        r3 = r24.createNewFile();	 Catch:{ IOException -> 0x047a }
        if (r3 != 0) goto L_0x005d;
    L_0x001a:
        r3 = "FileOperationTask";
        r4 = "copyFile, create new file fail.";
        com.mediatek.filemanager.utils.LogUtils.i(r3, r4);	 Catch:{ IOException -> 0x047a }
        r18 = -14;
        if (r11 == 0) goto L_0x0028;
    L_0x0025:
        r11.close();	 Catch:{ IOException -> 0x02d4 }
    L_0x0028:
        if (r15 == 0) goto L_0x002d;
    L_0x002a:
        r15.close();	 Catch:{ IOException -> 0x02d4 }
    L_0x002d:
        r3 = "FileOperationTask";
        r4 = "copyFile,update 100%.";
        com.mediatek.filemanager.utils.LogUtils.d(r3, r4);
        r3 = 1;
        r0 = new com.mediatek.filemanager.service.ProgressInfo[r3];
        r19 = r0;
        r20 = 0;
        r3 = new com.mediatek.filemanager.service.ProgressInfo;
        r4 = r23.getName();
        r5 = 100;
        r6 = 100;
        r8 = r25.getCurrentNumber();
        r8 = (int) r8;
        r9 = r25.getTotalNumber();
        r3.<init>(r4, r5, r6, r8, r9);
        r19[r20] = r3;
    L_0x0053:
        r0 = r21;
        r1 = r19;
        r0.publishProgress(r1);
        r17 = r18;
        goto L_0x000f;
    L_0x005d:
        r3 = r23.exists();	 Catch:{ IOException -> 0x047a }
        if (r3 != 0) goto L_0x00a7;
    L_0x0063:
        r3 = "FileOperationTask";
        r4 = "copyFile, src file is not exist.";
        com.mediatek.filemanager.utils.LogUtils.i(r3, r4);	 Catch:{ IOException -> 0x047a }
        r18 = -14;
        if (r11 == 0) goto L_0x0071;
    L_0x006e:
        r11.close();	 Catch:{ IOException -> 0x033a }
    L_0x0071:
        if (r15 == 0) goto L_0x0076;
    L_0x0073:
        r15.close();	 Catch:{ IOException -> 0x033a }
    L_0x0076:
        r3 = "FileOperationTask";
        r4 = "copyFile,update 100%.";
        com.mediatek.filemanager.utils.LogUtils.d(r3, r4);
        r3 = 1;
        r0 = new com.mediatek.filemanager.service.ProgressInfo[r3];
        r19 = r0;
        r20 = 0;
        r3 = new com.mediatek.filemanager.service.ProgressInfo;
        r4 = r23.getName();
        r5 = 100;
        r6 = 100;
        r8 = r25.getCurrentNumber();
        r8 = (int) r8;
        r9 = r25.getTotalNumber();
        r3.<init>(r4, r5, r6, r8, r9);
        r19[r20] = r3;
    L_0x009c:
        r0 = r21;
        r1 = r19;
        r0.publishProgress(r1);
        r17 = r18;
        goto L_0x000f;
    L_0x00a7:
        r12 = new java.io.FileInputStream;	 Catch:{ IOException -> 0x047a }
        r0 = r23;
        r12.<init>(r0);	 Catch:{ IOException -> 0x047a }
        r16 = new java.io.FileOutputStream;	 Catch:{ IOException -> 0x047d, all -> 0x046c }
        r0 = r16;
        r1 = r24;
        r0.<init>(r1);	 Catch:{ IOException -> 0x047d, all -> 0x046c }
        r14 = 0;
    L_0x00b8:
        r0 = r22;
        r14 = r12.read(r0);	 Catch:{ IOException -> 0x0149, all -> 0x0472 }
        if (r14 <= 0) goto L_0x0192;
    L_0x00c0:
        r3 = r21.isCancelled();	 Catch:{ IOException -> 0x0149, all -> 0x0472 }
        if (r3 == 0) goto L_0x0130;
    L_0x00c6:
        r3 = "FileOperationTask";
        r4 = new java.lang.StringBuilder;	 Catch:{ IOException -> 0x0149, all -> 0x0472 }
        r4.<init>();	 Catch:{ IOException -> 0x0149, all -> 0x0472 }
        r5 = "copyFile,commit copy file cancelled; break while loop thread id: ";
        r4 = r4.append(r5);	 Catch:{ IOException -> 0x0149, all -> 0x0472 }
        r5 = java.lang.Thread.currentThread();	 Catch:{ IOException -> 0x0149, all -> 0x0472 }
        r5 = r5.getId();	 Catch:{ IOException -> 0x0149, all -> 0x0472 }
        r4 = r4.append(r5);	 Catch:{ IOException -> 0x0149, all -> 0x0472 }
        r4 = r4.toString();	 Catch:{ IOException -> 0x0149, all -> 0x0472 }
        com.mediatek.filemanager.utils.LogUtils.d(r3, r4);	 Catch:{ IOException -> 0x0149, all -> 0x0472 }
        r3 = r24.delete();	 Catch:{ IOException -> 0x0149, all -> 0x0472 }
        if (r3 != 0) goto L_0x00f3;
    L_0x00ec:
        r3 = "FileOperationTask";
        r4 = "copyFile,delete fail in copyFile()";
        com.mediatek.filemanager.utils.LogUtils.w(r3, r4);	 Catch:{ IOException -> 0x0149, all -> 0x0472 }
    L_0x00f3:
        r18 = -7;
        if (r12 == 0) goto L_0x00fa;
    L_0x00f7:
        r12.close();	 Catch:{ IOException -> 0x03a0 }
    L_0x00fa:
        if (r16 == 0) goto L_0x00ff;
    L_0x00fc:
        r16.close();	 Catch:{ IOException -> 0x03a0 }
    L_0x00ff:
        r3 = "FileOperationTask";
        r4 = "copyFile,update 100%.";
        com.mediatek.filemanager.utils.LogUtils.d(r3, r4);
        r3 = 1;
        r0 = new com.mediatek.filemanager.service.ProgressInfo[r3];
        r19 = r0;
        r20 = 0;
        r3 = new com.mediatek.filemanager.service.ProgressInfo;
        r4 = r23.getName();
        r5 = 100;
        r6 = 100;
        r8 = r25.getCurrentNumber();
        r8 = (int) r8;
        r9 = r25.getTotalNumber();
        r3.<init>(r4, r5, r6, r8, r9);
        r19[r20] = r3;
    L_0x0125:
        r0 = r21;
        r1 = r19;
        r0.publishProgress(r1);
        r17 = r18;
        goto L_0x000f;
    L_0x0130:
        r3 = 0;
        r0 = r16;
        r1 = r22;
        r0.write(r1, r3, r14);	 Catch:{ IOException -> 0x0149, all -> 0x0472 }
        r3 = (long) r14;	 Catch:{ IOException -> 0x0149, all -> 0x0472 }
        r0 = r25;
        r0.updateProgress(r3);	 Catch:{ IOException -> 0x0149, all -> 0x0472 }
        r0 = r21;
        r1 = r25;
        r2 = r23;
        r0.updateProgressWithTime(r1, r2);	 Catch:{ IOException -> 0x0149, all -> 0x0472 }
        goto L_0x00b8;
    L_0x0149:
        r13 = move-exception;
        r15 = r16;
        r11 = r12;
    L_0x014d:
        r3 = "FileOperationTask";
        r4 = "copyFile,io exception!";
        com.mediatek.filemanager.utils.LogUtils.e(r3, r4);	 Catch:{ all -> 0x01ce }
        r13.printStackTrace();	 Catch:{ all -> 0x01ce }
        r17 = -14;
        if (r11 == 0) goto L_0x015e;
    L_0x015b:
        r11.close();	 Catch:{ IOException -> 0x026e }
    L_0x015e:
        if (r15 == 0) goto L_0x0163;
    L_0x0160:
        r15.close();	 Catch:{ IOException -> 0x026e }
    L_0x0163:
        r3 = "FileOperationTask";
        r4 = "copyFile,update 100%.";
        com.mediatek.filemanager.utils.LogUtils.d(r3, r4);
        r3 = 1;
        r0 = new com.mediatek.filemanager.service.ProgressInfo[r3];
        r18 = r0;
        r19 = 0;
        r3 = new com.mediatek.filemanager.service.ProgressInfo;
        r4 = r23.getName();
        r5 = 100;
        r6 = 100;
        r8 = r25.getCurrentNumber();
        r8 = (int) r8;
        r9 = r25.getTotalNumber();
        r3.<init>(r4, r5, r6, r8, r9);
        r18[r19] = r3;
    L_0x0189:
        r0 = r21;
        r1 = r18;
        r0.publishProgress(r1);
        goto L_0x000f;
    L_0x0192:
        if (r12 == 0) goto L_0x0197;
    L_0x0194:
        r12.close();	 Catch:{ IOException -> 0x0406 }
    L_0x0197:
        if (r16 == 0) goto L_0x019c;
    L_0x0199:
        r16.close();	 Catch:{ IOException -> 0x0406 }
    L_0x019c:
        r3 = "FileOperationTask";
        r4 = "copyFile,update 100%.";
        com.mediatek.filemanager.utils.LogUtils.d(r3, r4);
        r3 = 1;
        r0 = new com.mediatek.filemanager.service.ProgressInfo[r3];
        r18 = r0;
        r19 = 0;
        r3 = new com.mediatek.filemanager.service.ProgressInfo;
        r4 = r23.getName();
        r5 = 100;
        r6 = 100;
        r8 = r25.getCurrentNumber();
        r8 = (int) r8;
        r9 = r25.getTotalNumber();
        r3.<init>(r4, r5, r6, r8, r9);
        r18[r19] = r3;
    L_0x01c2:
        r0 = r21;
        r1 = r18;
        r0.publishProgress(r1);
        r15 = r16;
        r11 = r12;
        goto L_0x000f;
    L_0x01ce:
        r3 = move-exception;
        r18 = r3;
    L_0x01d1:
        if (r11 == 0) goto L_0x01d6;
    L_0x01d3:
        r11.close();	 Catch:{ IOException -> 0x0209 }
    L_0x01d6:
        if (r15 == 0) goto L_0x01db;
    L_0x01d8:
        r15.close();	 Catch:{ IOException -> 0x0209 }
    L_0x01db:
        r3 = "FileOperationTask";
        r4 = "copyFile,update 100%.";
        com.mediatek.filemanager.utils.LogUtils.d(r3, r4);
        r3 = 1;
        r0 = new com.mediatek.filemanager.service.ProgressInfo[r3];
        r19 = r0;
        r20 = 0;
        r3 = new com.mediatek.filemanager.service.ProgressInfo;
        r4 = r23.getName();
        r5 = 100;
        r6 = 100;
        r8 = r25.getCurrentNumber();
        r8 = (int) r8;
        r9 = r25.getTotalNumber();
        r3.<init>(r4, r5, r6, r8, r9);
        r19[r20] = r3;
    L_0x0201:
        r0 = r21;
        r1 = r19;
        r0.publishProgress(r1);
        throw r18;
    L_0x0209:
        r13 = move-exception;
        r3 = "FileOperationTask";
        r4 = "copyFile,io exception 2!";
        com.mediatek.filemanager.utils.LogUtils.e(r3, r4);	 Catch:{ all -> 0x023d }
        r13.printStackTrace();	 Catch:{ all -> 0x023d }
        r17 = -14;
        r3 = "FileOperationTask";
        r4 = "copyFile,update 100%.";
        com.mediatek.filemanager.utils.LogUtils.d(r3, r4);
        r3 = 1;
        r0 = new com.mediatek.filemanager.service.ProgressInfo[r3];
        r19 = r0;
        r20 = 0;
        r3 = new com.mediatek.filemanager.service.ProgressInfo;
        r4 = r23.getName();
        r5 = 100;
        r6 = 100;
        r8 = r25.getCurrentNumber();
        r8 = (int) r8;
        r9 = r25.getTotalNumber();
        r3.<init>(r4, r5, r6, r8, r9);
        r19[r20] = r3;
        goto L_0x0201;
    L_0x023d:
        r3 = move-exception;
        r18 = r3;
        r3 = "FileOperationTask";
        r4 = "copyFile,update 100%.";
        com.mediatek.filemanager.utils.LogUtils.d(r3, r4);
        r3 = 1;
        r0 = new com.mediatek.filemanager.service.ProgressInfo[r3];
        r19 = r0;
        r20 = 0;
        r3 = new com.mediatek.filemanager.service.ProgressInfo;
        r4 = r23.getName();
        r5 = 100;
        r6 = 100;
        r8 = r25.getCurrentNumber();
        r8 = (int) r8;
        r9 = r25.getTotalNumber();
        r3.<init>(r4, r5, r6, r8, r9);
        r19[r20] = r3;
        r0 = r21;
        r1 = r19;
        r0.publishProgress(r1);
        throw r18;
    L_0x026e:
        r13 = move-exception;
        r3 = "FileOperationTask";
        r4 = "copyFile,io exception 2!";
        com.mediatek.filemanager.utils.LogUtils.e(r3, r4);	 Catch:{ all -> 0x02a3 }
        r13.printStackTrace();	 Catch:{ all -> 0x02a3 }
        r17 = -14;
        r3 = "FileOperationTask";
        r4 = "copyFile,update 100%.";
        com.mediatek.filemanager.utils.LogUtils.d(r3, r4);
        r3 = 1;
        r0 = new com.mediatek.filemanager.service.ProgressInfo[r3];
        r18 = r0;
        r19 = 0;
        r3 = new com.mediatek.filemanager.service.ProgressInfo;
        r4 = r23.getName();
        r5 = 100;
        r6 = 100;
        r8 = r25.getCurrentNumber();
        r8 = (int) r8;
        r9 = r25.getTotalNumber();
        r3.<init>(r4, r5, r6, r8, r9);
        r18[r19] = r3;
        goto L_0x0189;
    L_0x02a3:
        r3 = move-exception;
        r18 = r3;
        r3 = "FileOperationTask";
        r4 = "copyFile,update 100%.";
        com.mediatek.filemanager.utils.LogUtils.d(r3, r4);
        r3 = 1;
        r0 = new com.mediatek.filemanager.service.ProgressInfo[r3];
        r19 = r0;
        r20 = 0;
        r3 = new com.mediatek.filemanager.service.ProgressInfo;
        r4 = r23.getName();
        r5 = 100;
        r6 = 100;
        r8 = r25.getCurrentNumber();
        r8 = (int) r8;
        r9 = r25.getTotalNumber();
        r3.<init>(r4, r5, r6, r8, r9);
        r19[r20] = r3;
        r0 = r21;
        r1 = r19;
        r0.publishProgress(r1);
        throw r18;
    L_0x02d4:
        r13 = move-exception;
        r3 = "FileOperationTask";
        r4 = "copyFile,io exception 2!";
        com.mediatek.filemanager.utils.LogUtils.e(r3, r4);	 Catch:{ all -> 0x0309 }
        r13.printStackTrace();	 Catch:{ all -> 0x0309 }
        r17 = -14;
        r3 = "FileOperationTask";
        r4 = "copyFile,update 100%.";
        com.mediatek.filemanager.utils.LogUtils.d(r3, r4);
        r3 = 1;
        r0 = new com.mediatek.filemanager.service.ProgressInfo[r3];
        r19 = r0;
        r20 = 0;
        r3 = new com.mediatek.filemanager.service.ProgressInfo;
        r4 = r23.getName();
        r5 = 100;
        r6 = 100;
        r8 = r25.getCurrentNumber();
        r8 = (int) r8;
        r9 = r25.getTotalNumber();
        r3.<init>(r4, r5, r6, r8, r9);
        r19[r20] = r3;
        goto L_0x0053;
    L_0x0309:
        r3 = move-exception;
        r18 = r3;
        r3 = "FileOperationTask";
        r4 = "copyFile,update 100%.";
        com.mediatek.filemanager.utils.LogUtils.d(r3, r4);
        r3 = 1;
        r0 = new com.mediatek.filemanager.service.ProgressInfo[r3];
        r19 = r0;
        r20 = 0;
        r3 = new com.mediatek.filemanager.service.ProgressInfo;
        r4 = r23.getName();
        r5 = 100;
        r6 = 100;
        r8 = r25.getCurrentNumber();
        r8 = (int) r8;
        r9 = r25.getTotalNumber();
        r3.<init>(r4, r5, r6, r8, r9);
        r19[r20] = r3;
        r0 = r21;
        r1 = r19;
        r0.publishProgress(r1);
        throw r18;
    L_0x033a:
        r13 = move-exception;
        r3 = "FileOperationTask";
        r4 = "copyFile,io exception 2!";
        com.mediatek.filemanager.utils.LogUtils.e(r3, r4);	 Catch:{ all -> 0x036f }
        r13.printStackTrace();	 Catch:{ all -> 0x036f }
        r17 = -14;
        r3 = "FileOperationTask";
        r4 = "copyFile,update 100%.";
        com.mediatek.filemanager.utils.LogUtils.d(r3, r4);
        r3 = 1;
        r0 = new com.mediatek.filemanager.service.ProgressInfo[r3];
        r19 = r0;
        r20 = 0;
        r3 = new com.mediatek.filemanager.service.ProgressInfo;
        r4 = r23.getName();
        r5 = 100;
        r6 = 100;
        r8 = r25.getCurrentNumber();
        r8 = (int) r8;
        r9 = r25.getTotalNumber();
        r3.<init>(r4, r5, r6, r8, r9);
        r19[r20] = r3;
        goto L_0x009c;
    L_0x036f:
        r3 = move-exception;
        r18 = r3;
        r3 = "FileOperationTask";
        r4 = "copyFile,update 100%.";
        com.mediatek.filemanager.utils.LogUtils.d(r3, r4);
        r3 = 1;
        r0 = new com.mediatek.filemanager.service.ProgressInfo[r3];
        r19 = r0;
        r20 = 0;
        r3 = new com.mediatek.filemanager.service.ProgressInfo;
        r4 = r23.getName();
        r5 = 100;
        r6 = 100;
        r8 = r25.getCurrentNumber();
        r8 = (int) r8;
        r9 = r25.getTotalNumber();
        r3.<init>(r4, r5, r6, r8, r9);
        r19[r20] = r3;
        r0 = r21;
        r1 = r19;
        r0.publishProgress(r1);
        throw r18;
    L_0x03a0:
        r13 = move-exception;
        r3 = "FileOperationTask";
        r4 = "copyFile,io exception 2!";
        com.mediatek.filemanager.utils.LogUtils.e(r3, r4);	 Catch:{ all -> 0x03d5 }
        r13.printStackTrace();	 Catch:{ all -> 0x03d5 }
        r17 = -14;
        r3 = "FileOperationTask";
        r4 = "copyFile,update 100%.";
        com.mediatek.filemanager.utils.LogUtils.d(r3, r4);
        r3 = 1;
        r0 = new com.mediatek.filemanager.service.ProgressInfo[r3];
        r19 = r0;
        r20 = 0;
        r3 = new com.mediatek.filemanager.service.ProgressInfo;
        r4 = r23.getName();
        r5 = 100;
        r6 = 100;
        r8 = r25.getCurrentNumber();
        r8 = (int) r8;
        r9 = r25.getTotalNumber();
        r3.<init>(r4, r5, r6, r8, r9);
        r19[r20] = r3;
        goto L_0x0125;
    L_0x03d5:
        r3 = move-exception;
        r18 = r3;
        r3 = "FileOperationTask";
        r4 = "copyFile,update 100%.";
        com.mediatek.filemanager.utils.LogUtils.d(r3, r4);
        r3 = 1;
        r0 = new com.mediatek.filemanager.service.ProgressInfo[r3];
        r19 = r0;
        r20 = 0;
        r3 = new com.mediatek.filemanager.service.ProgressInfo;
        r4 = r23.getName();
        r5 = 100;
        r6 = 100;
        r8 = r25.getCurrentNumber();
        r8 = (int) r8;
        r9 = r25.getTotalNumber();
        r3.<init>(r4, r5, r6, r8, r9);
        r19[r20] = r3;
        r0 = r21;
        r1 = r19;
        r0.publishProgress(r1);
        throw r18;
    L_0x0406:
        r13 = move-exception;
        r3 = "FileOperationTask";
        r4 = "copyFile,io exception 2!";
        com.mediatek.filemanager.utils.LogUtils.e(r3, r4);	 Catch:{ all -> 0x043b }
        r13.printStackTrace();	 Catch:{ all -> 0x043b }
        r17 = -14;
        r3 = "FileOperationTask";
        r4 = "copyFile,update 100%.";
        com.mediatek.filemanager.utils.LogUtils.d(r3, r4);
        r3 = 1;
        r0 = new com.mediatek.filemanager.service.ProgressInfo[r3];
        r18 = r0;
        r19 = 0;
        r3 = new com.mediatek.filemanager.service.ProgressInfo;
        r4 = r23.getName();
        r5 = 100;
        r6 = 100;
        r8 = r25.getCurrentNumber();
        r8 = (int) r8;
        r9 = r25.getTotalNumber();
        r3.<init>(r4, r5, r6, r8, r9);
        r18[r19] = r3;
        goto L_0x01c2;
    L_0x043b:
        r3 = move-exception;
        r18 = r3;
        r3 = "FileOperationTask";
        r4 = "copyFile,update 100%.";
        com.mediatek.filemanager.utils.LogUtils.d(r3, r4);
        r3 = 1;
        r0 = new com.mediatek.filemanager.service.ProgressInfo[r3];
        r19 = r0;
        r20 = 0;
        r3 = new com.mediatek.filemanager.service.ProgressInfo;
        r4 = r23.getName();
        r5 = 100;
        r6 = 100;
        r8 = r25.getCurrentNumber();
        r8 = (int) r8;
        r9 = r25.getTotalNumber();
        r3.<init>(r4, r5, r6, r8, r9);
        r19[r20] = r3;
        r0 = r21;
        r1 = r19;
        r0.publishProgress(r1);
        throw r18;
    L_0x046c:
        r3 = move-exception;
        r18 = r3;
        r11 = r12;
        goto L_0x01d1;
    L_0x0472:
        r3 = move-exception;
        r18 = r3;
        r15 = r16;
        r11 = r12;
        goto L_0x01d1;
    L_0x047a:
        r13 = move-exception;
        goto L_0x014d;
    L_0x047d:
        r13 = move-exception;
        r11 = r12;
        goto L_0x014d;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.mediatek.filemanager.service.FileOperationTask.copyFile(byte[], java.io.File, java.io.File, com.mediatek.filemanager.service.FileOperationTask$UpdateInfo):int");
    }

    File checkFileNameAndRename(File conflictFile) {
        File retFile = conflictFile;
        while (!isCancelled()) {
            if (retFile.exists()) {
                retFile = FileUtils.genrateNextNewName(retFile);
                if (retFile == null) {
                    LogUtils.i(TAG, "checkFileNameAndRename,retFile is null.");
                    return null;
                }
            }
            LogUtils.i(TAG, "checkFileNameAndRename,file is not exist.");
            return retFile;
        }
        LogUtils.i(TAG, "checkFileNameAndRename,cancel.");
        return null;
    }

    protected void updateProgressWithTime(UpdateInfo updateInfo, File file) {
        if (updateInfo.needUpdate()) {
            int progress = (int) ((updateInfo.getProgress() * 100) / updateInfo.getTotal());
            publishProgress(new ProgressInfo[]{new ProgressInfo(file.getName(), progress, 100, (int) updateInfo.getCurrentNumber(), updateInfo.getTotalNumber())});
        }
    }

    protected void addItem(HashMap<File, FileInfo> fileInfoMap, File file, File addFile) {
        if (fileInfoMap.containsKey(file)) {
            this.mFileInfoManager.addItem(new FileInfo(addFile));
        }
    }

    protected void removeItem(HashMap<File, FileInfo> fileInfoMap, File file, File removeFile) {
        if (fileInfoMap.containsKey(file)) {
            this.mFileInfoManager.removeItem(new FileInfo(removeFile));
        }
    }
}
