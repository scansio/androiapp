package com.mediatek.filemanager;

import android.content.Context;
import android.os.storage.StorageManager;
import android.os.storage.StorageVolume;
import android.text.TextUtils;
import com.mediatek.filemanager.utils.LogUtils;
import com.mediatek.storage.StorageManagerEx;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public final class MountPointManager {
    public static final String HOME = "Home";
    public static final String ROOT_PATH = "/storage";
    public static final String SEPARATOR = "/";
    private static final String TAG = "MountPointManager";
    private static MountPointManager sInstance = new MountPointManager();
    private final CopyOnWriteArrayList<MountPoint> mMountPathList = new CopyOnWriteArrayList();
    private String mRootPath = "Root Path";
    private StorageManager mStorageManager = null;

    private static class MountPoint {
        String mDescription;
        boolean mIsExternal;
        boolean mIsMounted;
        long mMaxFileSize;
        String mPath;

        private MountPoint() {
        }
    }

    private MountPointManager() {
    }

    public void init(Context context) {
        this.mStorageManager = (StorageManager) context.getSystemService("storage");
        String defaultPath = getDefaultPath();
        LogUtils.d(TAG, "init,defaultPath = " + defaultPath);
        if (!TextUtils.isEmpty(defaultPath)) {
            this.mRootPath = ROOT_PATH;
        }
        this.mMountPathList.clear();
        StorageVolume[] storageVolumeList = this.mStorageManager.getVolumeList();
        if (storageVolumeList != null) {
            for (StorageVolume volume : storageVolumeList) {
                MountPoint mountPoint = new MountPoint();
                mountPoint.mDescription = volume.getDescription(context);
                mountPoint.mPath = volume.getPath();
                mountPoint.mIsMounted = isMounted(volume.getPath());
                mountPoint.mIsExternal = volume.isRemovable();
                mountPoint.mMaxFileSize = volume.getMaxFileSize();
                LogUtils.d(TAG, "init,description :" + mountPoint.mDescription + ",path : " + mountPoint.mPath + ",isMounted : " + mountPoint.mIsMounted + ",isExternal : " + mountPoint.mIsExternal + ", mMaxFileSize: " + mountPoint.mMaxFileSize);
                this.mMountPathList.add(mountPoint);
            }
        }
        IconManager.getInstance().init(context, defaultPath + SEPARATOR);
    }

    public static MountPointManager getInstance() {
        return sInstance;
    }

    public boolean isRootPath(String path) {
        return this.mRootPath.equals(path);
    }

    public String getRootPath() {
        return this.mRootPath;
    }

    public List<FileInfo> getMountPointFileInfo() {
        List<FileInfo> fileInfos = new ArrayList(0);
        Iterator i$ = this.mMountPathList.iterator();
        while (i$.hasNext()) {
            MountPoint mp = (MountPoint) i$.next();
            if (mp.mIsMounted) {
                fileInfos.add(new FileInfo(mp.mPath));
            }
        }
        return fileInfos;
    }

    public int getMountCount() {
        int count = 0;
        Iterator i$ = this.mMountPathList.iterator();
        while (i$.hasNext()) {
            if (((MountPoint) i$.next()).mIsMounted) {
                count++;
            }
        }
        LogUtils.d(TAG, "getMountCount,count = " + count);
        return count;
    }

    public String getDefaultPath() {
        LogUtils.d(TAG, "getDefaultPath:" + StorageManagerEx.getDefaultPath());
        return StorageManagerEx.getDefaultPath();
    }

    protected boolean isMounted(String mountPoint) {
        LogUtils.d(TAG, "isMounted, mountPoint = " + mountPoint);
        if (TextUtils.isEmpty(mountPoint)) {
            return false;
        }
        String state = this.mStorageManager.getVolumeState(mountPoint);
        LogUtils.d(TAG, "state = " + state);
        return "mounted".equals(state);
    }

    protected boolean isRootPathMount(String path) {
        LogUtils.d(TAG, "isRootPathMount,  path = " + path);
        if (path == null) {
            return false;
        }
        boolean ret = isMounted(getRealMountPointPath(path));
        LogUtils.d(TAG, "isRootPathMount,  ret = " + ret);
        return ret;
    }

    public String getRealMountPointPath(String path) {
        LogUtils.d(TAG, "getRealMountPointPath ,path =" + path);
        Iterator i$ = this.mMountPathList.iterator();
        while (i$.hasNext()) {
            MountPoint mountPoint = (MountPoint) i$.next();
            if ((path + SEPARATOR).startsWith(mountPoint.mPath + SEPARATOR)) {
                LogUtils.d(TAG, "getRealMountPointPath = " + mountPoint.mPath);
                return mountPoint.mPath;
            }
        }
        LogUtils.d(TAG, "getRealMountPointPath = \"\" ");
        return "";
    }

    public boolean isFat32Disk(String path) {
        LogUtils.d(TAG, "isFat32Disk ,path =" + path);
        Iterator i$ = this.mMountPathList.iterator();
        while (i$.hasNext()) {
            MountPoint mountPoint = (MountPoint) i$.next();
            if ((path + SEPARATOR).startsWith(mountPoint.mPath + SEPARATOR)) {
                LogUtils.d(TAG, "isFat32Disk = " + mountPoint.mPath);
                if (mountPoint.mMaxFileSize > 0) {
                    LogUtils.d(TAG, "isFat32Disk = true.");
                    return true;
                }
                LogUtils.d(TAG, "isFat32Disk = false.");
                return false;
            }
        }
        LogUtils.d(TAG, "isFat32Disk = false.");
        return false;
    }

    public boolean changeMountState(String path, Boolean isMounted) {
        boolean ret = false;
        Iterator i$ = this.mMountPathList.iterator();
        while (i$.hasNext()) {
            MountPoint mountPoint = (MountPoint) i$.next();
            if (mountPoint.mPath.equals(path)) {
                if (mountPoint.mIsMounted != isMounted.booleanValue()) {
                    mountPoint.mIsMounted = isMounted.booleanValue();
                    ret = true;
                }
                LogUtils.d(TAG, "changeMountState ,path =" + path + ",ret = " + ret);
                return ret;
            }
        }
        LogUtils.d(TAG, "changeMountState ,path =" + path + ",ret = " + ret);
        return ret;
    }

    public boolean isMountPoint(String path) {
        boolean ret = false;
        LogUtils.d(TAG, "isMountPoint ,path =" + path);
        if (path == null) {
            return 0;
        }
        Iterator i$ = this.mMountPathList.iterator();
        while (i$.hasNext()) {
            if (path.equals(((MountPoint) i$.next()).mPath)) {
                ret = true;
                break;
            }
        }
        LogUtils.d(TAG, "isMountPoint ,ret =" + ret);
        return ret;
    }

    public boolean isInternalMountPath(String path) {
        LogUtils.d(TAG, "isInternalMountPath ,path =" + path);
        if (path == null) {
            return false;
        }
        Iterator i$ = this.mMountPathList.iterator();
        while (i$.hasNext()) {
            MountPoint mountPoint = (MountPoint) i$.next();
            if (!mountPoint.mIsExternal && mountPoint.mPath.equals(path)) {
                return true;
            }
        }
        return false;
    }

    public boolean isExternalMountPath(String path) {
        LogUtils.d(TAG, "isExternalMountPath ,path =" + path);
        if (path == null) {
            return false;
        }
        Iterator i$ = this.mMountPathList.iterator();
        while (i$.hasNext()) {
            MountPoint mountPoint = (MountPoint) i$.next();
            if (mountPoint.mIsExternal && mountPoint.mPath.equals(path)) {
                return true;
            }
        }
        return false;
    }

    public boolean isExternalFile(FileInfo fileInfo) {
        boolean ret = false;
        if (fileInfo != null) {
            String mountPath = getRealMountPointPath(fileInfo.getFileAbsolutePath());
            if (mountPath.equals(fileInfo.getFileAbsolutePath())) {
                LogUtils.d(TAG, "isExternalFile,return false .mountPath = " + mountPath);
                ret = false;
            }
            if (isExternalMountPath(mountPath)) {
                ret = true;
            }
        }
        LogUtils.d(TAG, "isExternalFile,ret = " + ret);
        return ret;
    }

    public String getDescriptionPath(String path) {
        LogUtils.d(TAG, "getDescriptionPath ,path =" + path);
        if (this.mMountPathList != null) {
            Iterator i$ = this.mMountPathList.iterator();
            while (i$.hasNext()) {
                MountPoint mountPoint = (MountPoint) i$.next();
                if ((path + SEPARATOR).startsWith(mountPoint.mPath + SEPARATOR)) {
                    return path.length() > mountPoint.mPath.length() + 1 ? mountPoint.mDescription + SEPARATOR + path.substring(mountPoint.mPath.length() + 1) : mountPoint.mDescription;
                }
            }
        }
        return path;
    }

    public boolean isPrimaryVolume(String path) {
        LogUtils.d(TAG, "isPrimaryVolume ,path =" + path);
        if (this.mMountPathList.size() > 0) {
            return ((MountPoint) this.mMountPathList.get(0)).mPath.equals(path);
        }
        LogUtils.w(TAG, "mMountPathList null!");
        return false;
    }
}
