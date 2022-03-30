package com.mediatek.filemanager.ext;

import android.graphics.Bitmap;

public class DefaultIconExtension implements IIconExtension {
    public void createSystemFolder(String defaultPath) {
    }

    public Bitmap getSystemFolderIcon(String path) {
        return null;
    }

    public boolean isSystemFolder(String path) {
        return false;
    }
}
