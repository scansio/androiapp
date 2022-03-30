package com.mediatek.filemanager.ext;

import android.graphics.Bitmap;

public interface IIconExtension {
    void createSystemFolder(String str);

    Bitmap getSystemFolderIcon(String str);

    boolean isSystemFolder(String str);
}
