package com.mediatek.filemanager;

import android.text.TextUtils;
import com.mediatek.filemanager.utils.LogUtils;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class FileInfoManager {
    private static final int MAX_LIST_SIZE = 20;
    public static final int PASTE_MODE_COPY = 2;
    public static final int PASTE_MODE_CUT = 1;
    public static final int PASTE_MODE_UNKOWN = 0;
    private static final String TAG = "FileInfoManager";
    private final List<FileInfo> mAddFilesInfoList = new ArrayList();
    private String mLastAccessPath = null;
    protected long mModifiedTime = -1;
    private final List<NavigationRecord> mNavigationList = new LinkedList();
    private final List<FileInfo> mPasteFilesInfoList = new ArrayList();
    private int mPasteOperation = 0;
    private final List<FileInfo> mRemoveFilesInfoList = new ArrayList();
    private final List<FileInfo> mShowFilesInfoList = new ArrayList();

    public static class NavigationRecord {
        private final String mPath;
        private final FileInfo mSelectedFile;
        private final int mTop;

        public NavigationRecord(String path, FileInfo selectedFile, int top) {
            this.mPath = path;
            this.mSelectedFile = selectedFile;
            this.mTop = top;
        }

        public String getRecordPath() {
            return this.mPath;
        }

        public int getTop() {
            return this.mTop;
        }

        public FileInfo getSelectedFile() {
            return this.mSelectedFile;
        }
    }

    public void savePasteList(int pasteType, List<FileInfo> fileInfos) {
        this.mPasteOperation = pasteType;
        this.mPasteFilesInfoList.clear();
        this.mPasteFilesInfoList.addAll(fileInfos);
    }

    public boolean isPathModified(String path) {
        if (path != null && !path.equals(this.mLastAccessPath)) {
            return true;
        }
        if (this.mLastAccessPath == null || this.mModifiedTime == new File(this.mLastAccessPath).lastModified()) {
            return false;
        }
        return true;
    }

    public List<FileInfo> getPasteList() {
        return new ArrayList(this.mPasteFilesInfoList);
    }

    public int getPasteType() {
        return this.mPasteOperation;
    }

    public void addItem(FileInfo fileInfo) {
        this.mAddFilesInfoList.add(fileInfo);
    }

    public void removeItem(FileInfo fileInfo) {
        this.mRemoveFilesInfoList.add(fileInfo);
    }

    public void updateFileInfoList(String currentPath, int sortType) {
        LogUtils.d(TAG, "updateFileInfoList,currentPath = " + currentPath + "sortType = " + sortType);
        this.mLastAccessPath = currentPath;
        this.mModifiedTime = new File(this.mLastAccessPath).lastModified();
        FileInfo[] addFilesInfos = new FileInfo[this.mAddFilesInfoList.size()];
        this.mAddFilesInfoList.toArray(addFilesInfos);
        for (FileInfo fileInfo : addFilesInfos) {
            if (fileInfo.getFileParentPath().equals(this.mLastAccessPath)) {
                this.mShowFilesInfoList.add(fileInfo);
            }
        }
        this.mShowFilesInfoList.removeAll(this.mRemoveFilesInfoList);
        this.mPasteFilesInfoList.removeAll(this.mRemoveFilesInfoList);
        this.mAddFilesInfoList.clear();
        this.mRemoveFilesInfoList.clear();
        sort(sortType);
    }

    public FileInfo updateOneFileInfoList(String path, int sortType) {
        LogUtils.d(TAG, "updateOneFileInfoList,path = " + path + "sortType = " + sortType);
        FileInfo fileInfo = null;
        this.mLastAccessPath = path;
        this.mModifiedTime = new File(this.mLastAccessPath).lastModified();
        if (this.mAddFilesInfoList.size() > 0) {
            fileInfo = (FileInfo) this.mAddFilesInfoList.get(0);
            if (fileInfo.getFileParentPath().equals(this.mLastAccessPath)) {
                this.mShowFilesInfoList.add(fileInfo);
            }
        }
        this.mShowFilesInfoList.removeAll(this.mRemoveFilesInfoList);
        this.mPasteFilesInfoList.removeAll(this.mRemoveFilesInfoList);
        this.mAddFilesInfoList.clear();
        this.mRemoveFilesInfoList.clear();
        sort(sortType);
        return fileInfo;
    }

    public void loadFileInfoList(String path, int sortType) {
        LogUtils.d(TAG, "loadFileInfoList,path = " + path + ",sortType = " + sortType);
        this.mShowFilesInfoList.clear();
        this.mLastAccessPath = path;
        this.mModifiedTime = new File(this.mLastAccessPath).lastModified();
        if (MountPointManager.getInstance().isRootPath(path)) {
            this.mAddFilesInfoList.clear();
            List<FileInfo> fileInfoList = new ArrayList();
            List<FileInfo> mountFileList = MountPointManager.getInstance().getMountPointFileInfo();
            LogUtils.d(getClass().getName(), "mountFileList size = " + mountFileList.size());
            if (mountFileList != null) {
                fileInfoList.addAll(mountFileList);
            }
            LogUtils.d(getClass().getName(), "fileInfoList size = " + fileInfoList.size());
            addItemList(fileInfoList);
        }
        LogUtils.d(TAG, " mAddFilesInfoList size :" + this.mAddFilesInfoList.size());
        FileInfo[] addFilesInfos = new FileInfo[this.mAddFilesInfoList.size()];
        this.mAddFilesInfoList.toArray(addFilesInfos);
        for (FileInfo fileInfo : addFilesInfos) {
            LogUtils.d(TAG, "fileinfo is :" + fileInfo.toString());
            if (this.mLastAccessPath.equals(fileInfo.getFileParentPath()) || MountPointManager.getInstance().isMountPoint(fileInfo.getFileAbsolutePath())) {
                LogUtils.d(TAG, "mShowFilesInfoLis add fileinfo " + fileInfo.getFileName());
                this.mShowFilesInfoList.add(fileInfo);
            }
        }
        this.mAddFilesInfoList.clear();
        sort(sortType);
    }

    public void loadFileInfoList(String path, int sortType, FileInfo selectedFileInfo) {
        LogUtils.d(TAG, "loadFileInfoList,path = " + path + ",sortType = " + sortType);
        this.mShowFilesInfoList.clear();
        this.mLastAccessPath = path;
        this.mModifiedTime = new File(this.mLastAccessPath).lastModified();
        for (FileInfo fileInfo : this.mAddFilesInfoList) {
            if (fileInfo == null) {
                LogUtils.w(TAG, "loadFileInfoList,file info is null!");
            } else if (this.mLastAccessPath.equals(fileInfo.getFileParentPath()) || MountPointManager.getInstance().isMountPoint(fileInfo.getFileAbsolutePath())) {
                this.mShowFilesInfoList.add(fileInfo);
                if (selectedFileInfo != null && fileInfo.getFileName().equals(selectedFileInfo.getFileName())) {
                    fileInfo.setChecked(true);
                }
            }
        }
        this.mAddFilesInfoList.clear();
        sort(sortType);
    }

    public void addItemList(List<FileInfo> fileInfoList) {
        LogUtils.v(TAG, "addItemList");
        this.mAddFilesInfoList.clear();
        this.mAddFilesInfoList.addAll(fileInfoList);
    }

    protected NavigationRecord getPrevNavigation() {
        while (!this.mNavigationList.isEmpty()) {
            NavigationRecord navRecord = (NavigationRecord) this.mNavigationList.get(this.mNavigationList.size() - 1);
            removeFromNavigationList();
            String path = navRecord.getRecordPath();
            if (!TextUtils.isEmpty(path) && (new File(path).exists() || MountPointManager.getInstance().isRootPath(path))) {
                return navRecord;
            }
        }
        return null;
    }

    protected void addToNavigationList(NavigationRecord navigationRecord) {
        if (this.mNavigationList.size() <= MAX_LIST_SIZE) {
            this.mNavigationList.add(navigationRecord);
            return;
        }
        this.mNavigationList.remove(0);
        this.mNavigationList.add(navigationRecord);
    }

    protected void removeFromNavigationList() {
        if (!this.mNavigationList.isEmpty()) {
            this.mNavigationList.remove(this.mNavigationList.size() - 1);
        }
    }

    protected void clearNavigationList() {
        this.mNavigationList.clear();
    }

    public boolean isPasteItem(FileInfo currentItem) {
        return this.mPasteFilesInfoList.contains(currentItem);
    }

    public int getPasteCount() {
        return this.mPasteFilesInfoList.size();
    }

    public void clearPasteList() {
        this.mPasteFilesInfoList.clear();
        this.mPasteOperation = 0;
    }

    public List<FileInfo> getShowFileList() {
        LogUtils.d(TAG, "getShowFileList");
        return this.mShowFilesInfoList;
    }

    public void sort(int sortType) {
        LogUtils.d(TAG, "sort,sortType = " + sortType);
        Collections.sort(this.mShowFilesInfoList, FileInfoComparator.getInstance(sortType));
    }

    public void updateSearchList() {
        LogUtils.d(TAG, "updateSearchList");
        this.mShowFilesInfoList.addAll(this.mAddFilesInfoList);
        this.mAddFilesInfoList.clear();
    }
}
