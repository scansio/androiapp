package com.mediatek.filemanager;

import android.media.MediaFile;

public class MediaFileManager {
    public static final int FILE_TYPE_MS_EXCEL = 705;
    public static final int FILE_TYPE_MS_POWERPOINT = 706;
    public static final int FILE_TYPE_MS_WORD = 704;
    public static final int FILE_TYPE_PDF = 702;

    public static int getFileTypeForMimeType(String mimeType) {
        return MediaFile.getFileTypeForMimeType(mimeType);
    }
}
