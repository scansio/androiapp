package com.mediatek.filemanager.utils;

import android.content.Context;
import android.text.TextUtils;
import com.mediatek.filemanager.FileInfo;
import com.mediatek.filemanager.MountPointManager;
import com.mediatek.filemanager.service.FileManagerService;
import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.List;

public final class FileUtils {
    private static final int DECIMAL_NUMBER = 100;
    private static final double ROUNDING_OFF = 0.005d;
    private static final String TAG = "FileUtils";
    public static final String UNIT_B = "B";
    public static final String UNIT_GB = "GB";
    private static final int UNIT_INTERVAL = 1024;
    public static final String UNIT_KB = "KB";
    public static final String UNIT_MB = "MB";
    public static final String UNIT_TB = "TB";

    public static int checkFileName(String fileName) {
        if (TextUtils.isEmpty(fileName) || fileName.trim().length() == 0) {
            return -2;
        }
        try {
            int length = fileName.getBytes("UTF-8").length;
            LogUtils.d(TAG, "checkFileName: " + fileName + ",lenth= " + length);
            if (length <= FileInfo.FILENAME_MAX_LENGTH) {
                return 100;
            }
            LogUtils.d(TAG, "checkFileName,fileName is too long,len=" + length);
            return -3;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return -2;
        }
    }

    public static String getFileExtension(String fileName) {
        if (fileName == null) {
            return null;
        }
        int lastDot = fileName.lastIndexOf(46);
        if (lastDot >= 0) {
            return fileName.substring(lastDot + 1).toLowerCase();
        }
        return null;
    }

    public static String getFileName(String absolutePath) {
        int sepIndex = absolutePath.lastIndexOf(MountPointManager.SEPARATOR);
        if (sepIndex >= 0) {
            return absolutePath.substring(sepIndex + 1);
        }
        return absolutePath;
    }

    public static String getFilePath(String filePath) {
        int sepIndex = filePath.lastIndexOf(MountPointManager.SEPARATOR);
        if (sepIndex >= 0) {
            return filePath.substring(0, sepIndex);
        }
        return "";
    }

    public static File genrateNextNewName(File file) {
        String parentDir = file.getParent();
        String fileName = file.getName();
        String ext = "";
        int newNumber = 0;
        if (file.isFile()) {
            int extIndex = fileName.lastIndexOf(".");
            if (extIndex != -1) {
                ext = fileName.substring(extIndex);
                fileName = fileName.substring(0, extIndex);
            }
        }
        if (fileName.endsWith(")")) {
            int leftBracketIndex = fileName.lastIndexOf("(");
            if (leftBracketIndex != -1) {
                String numeric = fileName.substring(leftBracketIndex + 1, fileName.length() - 1);
                if (numeric.matches("[0-9]+")) {
                    LogUtils.v(TAG, "Conflict folder name already contains (): " + fileName + "thread id: " + Thread.currentThread().getId());
                    try {
                        newNumber = Integer.parseInt(numeric) + 1;
                        fileName = fileName.substring(0, leftBracketIndex);
                    } catch (NumberFormatException e) {
                        LogUtils.e(TAG, "Fn-findSuffixNumber(): " + e.toString());
                    }
                }
            }
        }
        StringBuffer sb = new StringBuffer();
        sb.append(fileName).append("(").append(newNumber).append(")").append(ext);
        if (checkFileName(sb.toString()) < 0) {
            return null;
        }
        return new File(parentDir, sb.toString());
    }

    public static String sizeToString(Context context, long size) {
        if (context == null) {
            return "";
        }
        if (size < 100) {
            LogUtils.d(TAG, "sizeToString(),size = " + size);
            return Long.toString(size) + " " + context.getResources().getString(17039432);
        }
        int unit = 17039433;
        double sizeDouble = ((double) size) / 1024.0d;
        if (sizeDouble > 1024.0d) {
            sizeDouble /= 1024.0d;
            unit = 17039434;
        }
        if (sizeDouble > 1024.0d) {
            sizeDouble /= 1024.0d;
            unit = 17039435;
        }
        if (sizeDouble > 1024.0d) {
            sizeDouble /= 1024.0d;
            unit = 17039436;
        }
        double formatedSize = ((double) ((long) ((ROUNDING_OFF + sizeDouble) * 100.0d))) / 100.0d;
        LogUtils.d(TAG, "sizeToString(): " + formatedSize + unit);
        if (formatedSize == 0.0d) {
            return "0 " + context.getResources().getString(unit);
        }
        return Double.toString(formatedSize) + " " + context.getResources().getString(unit);
    }

    public static String getMultipleMimeType(FileManagerService service, String currentDirPath, List<FileInfo> files) {
        String mimeType = null;
        for (FileInfo info : files) {
            mimeType = info.getFileMimeType(service);
            if (mimeType != null && (mimeType.startsWith(FileInfo.MIME_HAED_IMAGE) || mimeType.startsWith(FileInfo.MIME_HEAD_VIDEO))) {
                break;
            }
        }
        if (mimeType == null || mimeType.startsWith("unknown")) {
            mimeType = FileInfo.MIMETYPE_UNRECOGNIZED;
        }
        LogUtils.d(TAG, "Multiple files' mimetype is " + mimeType);
        return mimeType;
    }

    public static boolean isExtensionChange(String newFilePath, String oldFilePath) {
        if (new File(oldFilePath).isDirectory()) {
            return false;
        }
        String origFileExtension = getFileExtension(oldFilePath);
        String newFileExtension = getFileExtension(newFilePath);
        if ((origFileExtension == null || origFileExtension.equals(newFileExtension)) && (newFileExtension == null || newFileExtension.equals(origFileExtension))) {
            return false;
        }
        return true;
    }
}
