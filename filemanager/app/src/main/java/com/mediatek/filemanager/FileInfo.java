package com.mediatek.filemanager;

import android.content.Context;
import android.media.MediaFile;
import android.net.Uri;
import com.mediatek.filemanager.service.FileManagerService;
import com.mediatek.filemanager.utils.DrmManager;
import com.mediatek.filemanager.utils.FileUtils;
import com.mediatek.filemanager.utils.LogUtils;
import com.mediatek.filemanager.utils.OptionsUtils;
import java.io.File;

public class FileInfo {
    public static final int FILENAME_MAX_LENGTH = 255;
    public static final String MIMETYPE_3GPP2_VIDEO = "video/3gpp2";
    public static final String MIMETYPE_3GPP_AUDIO = "audio/3gpp";
    public static final String MIMETYPE_3GPP_UNKONW = "unknown_3gpp_mimeType";
    public static final String MIMETYPE_3GPP_VIDEO = "video/3gpp";
    public static final String MIMETYPE_EXTENSION_NULL = "unknown_ext_null_mimeType";
    public static final String MIMETYPE_EXTENSION_UNKONW = "unknown_ext_mimeType";
    public static final String MIMETYPE_UNRECOGNIZED = "application/zip";
    public static final String MIME_HAED_IMAGE = "image/";
    public static final String MIME_HEAD_VIDEO = "video/";
    private static final String TAG = "FileInfo";
    private final String mAbsolutePath;
    private final File mFile;
    private String mFileSizeStr = null;
    private boolean mIsChecked = false;
    private final boolean mIsDir;
    private long mLastModifiedTime = -1;
    private final String mName;
    private String mParentPath = null;
    private long mSize = -1;

    public FileInfo(File file) throws IllegalArgumentException {
        if (file == null) {
            throw new IllegalArgumentException();
        }
        this.mFile = file;
        this.mName = this.mFile.getName();
        this.mAbsolutePath = this.mFile.getAbsolutePath();
        this.mLastModifiedTime = this.mFile.lastModified();
        this.mIsDir = this.mFile.isDirectory();
        if (!this.mIsDir) {
            this.mSize = this.mFile.length();
        }
    }

    public FileInfo(String absPath) {
        if (absPath == null) {
            throw new IllegalArgumentException();
        }
        this.mAbsolutePath = absPath;
        this.mFile = new File(absPath);
        this.mName = this.mFile.getName();
        this.mLastModifiedTime = this.mFile.lastModified();
        this.mIsDir = this.mFile.isDirectory();
        if (!this.mIsDir) {
            this.mSize = this.mFile.length();
        }
    }

    public String getFileParentPath() {
        if (this.mParentPath == null) {
            this.mParentPath = FileUtils.getFilePath(this.mAbsolutePath);
        }
        return this.mParentPath;
    }

    public String getShowParentPath() {
        LogUtils.d(TAG, "getShowParentPath...");
        return MountPointManager.getInstance().getDescriptionPath(getFileParentPath());
    }

    public String getShowPath() {
        LogUtils.d(TAG, "getShowPath...");
        return MountPointManager.getInstance().getDescriptionPath(getFileAbsolutePath());
    }

    public String getFileName() {
        return this.mName;
    }

    public String getShowName() {
        LogUtils.d(TAG, "getShowName...");
        String strShowName = FileUtils.getFileName(getShowPath());
        LogUtils.d(TAG, "getShowName, name = " + strShowName);
        return strShowName;
    }

    public long getFileSize() {
        return this.mSize;
    }

    public String getFileSizeStr(Context context) {
        if (this.mFileSizeStr == null) {
            this.mFileSizeStr = FileUtils.sizeToString(context, this.mSize);
        }
        return this.mFileSizeStr;
    }

    public boolean isDirectory() {
        return this.mIsDir;
    }

    public String getFileMimeType(FileManagerService service) {
        LogUtils.d(TAG, "getFileMimeType,service.");
        if (isDirectory()) {
            return null;
        }
        String mimeType;
        if (isDrmFile()) {
            mimeType = DrmManager.getInstance().getOriginalMimeType(this.mAbsolutePath);
            LogUtils.d(TAG, "getFileMimeType, is drm file,mimetype is: " + mimeType);
            return mimeType;
        }
        mimeType = getMimeType(this.mFile);
        LogUtils.d(TAG, "getFileMimeType, mimetype is : " + mimeType);
        return mimeType;
    }

    public boolean isDrmFile() {
        if (this.mIsDir) {
            return false;
        }
        return isDrmFile(this.mAbsolutePath);
    }

    public static boolean isDrmFile(String fileName) {
        if (OptionsUtils.isMtkDrmApp()) {
            String extension = FileUtils.getFileExtension(fileName);
            if (extension != null && extension.equalsIgnoreCase(DrmManager.EXT_DRM_CONTENT)) {
                return true;
            }
        }
        return false;
    }

    private String getMimeType(File file) {
        String fileName = getFileName();
        String extension = FileUtils.getFileExtension(fileName);
        LogUtils.d(TAG, "getMimeType fileName=" + fileName + ",extension = " + extension);
        if (extension == null) {
            return MIMETYPE_EXTENSION_NULL;
        }
        String mimeType = MediaFile.getMimeTypeForFile(fileName);
        LogUtils.d(TAG, "getMimeType mimeType =" + mimeType);
        if (mimeType == null) {
            return MIMETYPE_EXTENSION_UNKONW;
        }
        return mimeType;
    }

    public long getFileLastModifiedTime() {
        return this.mLastModifiedTime;
    }

    public long getNewModifiedTime() {
        this.mLastModifiedTime = this.mFile.lastModified();
        return this.mLastModifiedTime;
    }

    public String getFileAbsolutePath() {
        return this.mAbsolutePath;
    }

    public File getFile() {
        return this.mFile;
    }

    public Uri getUri() {
        return Uri.fromFile(this.mFile);
    }

    public int hashCode() {
        return getFileAbsolutePath().hashCode();
    }

    public boolean equals(Object o) {
        if (super.equals(o)) {
            return true;
        }
        if ((o instanceof FileInfo) && ((FileInfo) o).getFileAbsolutePath().equals(getFileAbsolutePath())) {
            return true;
        }
        return false;
    }

    public boolean isChecked() {
        return this.mIsChecked;
    }

    public void setChecked(boolean checked) {
        this.mIsChecked = checked;
    }

    public boolean isHideFile() {
        if (getFileName().startsWith(".")) {
            return true;
        }
        return false;
    }

    public void updateFileInfo() {
        LogUtils.d(TAG, "newModifiedTime: " + this.mLastModifiedTime);
        if (!this.mIsDir) {
            this.mSize = this.mFile.length();
            this.mFileSizeStr = null;
            LogUtils.d(TAG, "FileSize: " + this.mSize);
        }
    }
}
