package com.mediatek.filemanager.service;

import java.util.ArrayList;
import java.util.List;

public abstract class MultiMediaStoreHelper {
    private static final int NEED_UPDATE = 200;
    protected final MediaStoreHelper mMediaStoreHelper;
    protected final List<String> mPathList = new ArrayList();

    public static class DeleteMediaStoreHelper extends MultiMediaStoreHelper {
        public DeleteMediaStoreHelper(MediaStoreHelper mediaStoreHelper) {
            super(mediaStoreHelper);
        }

        public void updateRecords() {
            this.mMediaStoreHelper.deleteFileInMediaStore(this.mPathList);
            super.updateRecords();
        }
    }

    public static class PasteMediaStoreHelper extends MultiMediaStoreHelper {
        public PasteMediaStoreHelper(MediaStoreHelper mediaStoreHelper) {
            super(mediaStoreHelper);
        }

        public void updateRecords() {
            this.mMediaStoreHelper.scanPathforMediaStore(this.mPathList);
            super.updateRecords();
        }
    }

    public MultiMediaStoreHelper(MediaStoreHelper mediaStoreHelper) {
        if (mediaStoreHelper == null) {
            throw new IllegalArgumentException("mediaStoreHelper has not been initialized.");
        }
        this.mMediaStoreHelper = mediaStoreHelper;
    }

    public void addRecord(String path) {
        this.mPathList.add(path);
        if (this.mPathList.size() > NEED_UPDATE) {
            updateRecords();
        }
    }

    public void updateRecords() {
        this.mPathList.clear();
    }

    public void setDstFolder(String dstFolder) {
        this.mMediaStoreHelper.setDstFolder(dstFolder);
    }
}
