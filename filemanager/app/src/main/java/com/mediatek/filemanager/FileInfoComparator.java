package com.mediatek.filemanager;

import com.mediatek.filemanager.utils.FileUtils;
import com.mediatek.filemanager.utils.LogUtils;
import java.text.Collator;
import java.text.RuleBasedCollator;
import java.util.Comparator;
import java.util.Locale;

public final class FileInfoComparator implements Comparator<FileInfo> {
    public static final int SORT_BY_NAME = 1;
    public static final int SORT_BY_SIZE = 2;
    public static final int SORT_BY_TIME = 3;
    public static final int SORT_BY_TYPE = 0;
    private static final String TAG = "FileInfoComparator";
    private static FileInfoComparator sInstance = new FileInfoComparator();
    private RuleBasedCollator mCollator = null;
    private int mSortType = 0;

    private FileInfoComparator() {
    }

    private void setSortType(int sort) {
        this.mSortType = sort;
        if (this.mCollator == null) {
            this.mCollator = (RuleBasedCollator) Collator.getInstance(Locale.CHINA);
        }
    }

    public static FileInfoComparator getInstance(int sort) {
        sInstance.setSortType(sort);
        return sInstance;
    }

    public int compare(FileInfo op, FileInfo oq) {
        boolean isOpDirectory = op.isDirectory();
        if ((isOpDirectory ^ oq.isDirectory()) != 0) {
            LogUtils.v(TAG, op.getFileName() + " vs " + oq.getFileName() + " result=" + (isOpDirectory ? -1 : 1));
            if (isOpDirectory) {
                return -1;
            }
            return 1;
        }
        switch (this.mSortType) {
            case 0:
                return sortByType(op, oq);
            case 1:
                return sortByName(op, oq);
            case 2:
                return sortBySize(op, oq);
            case 3:
                return sortByTime(op, oq);
            default:
                return sortByName(op, oq);
        }
    }

    private int sortByType(FileInfo op, FileInfo oq) {
        boolean isOpDirectory = op.isDirectory();
        boolean isOqDirectory = oq.isDirectory();
        if (isOpDirectory && isOqDirectory) {
            boolean isOpCategoryFolder = IconManager.getInstance().isSystemFolder(op);
            if ((isOpCategoryFolder ^ IconManager.getInstance().isSystemFolder(oq)) != 0) {
                int i;
                String str = TAG;
                StringBuilder append = new StringBuilder().append(op.getFileName()).append(" - ").append(oq.getFileName()).append(" result=");
                if (isOpCategoryFolder) {
                    i = -1;
                } else {
                    i = 1;
                }
                LogUtils.i(str, append.append(i).toString());
                if (isOpCategoryFolder) {
                    return -1;
                }
                return 1;
            }
        }
        if (!(isOpDirectory || isOqDirectory)) {
            String opExtension = FileUtils.getFileExtension(op.getFileName());
            String oqExtension = FileUtils.getFileExtension(oq.getFileName());
            if (opExtension == null && oqExtension != null) {
                return -1;
            }
            if (opExtension != null && oqExtension == null) {
                return 1;
            }
            if (!(opExtension == null || oqExtension == null || opExtension.equalsIgnoreCase(oqExtension))) {
                return opExtension.compareToIgnoreCase(oqExtension);
            }
        }
        return sortByName(op, oq);
    }

    private int sortByName(FileInfo op, FileInfo oq) {
        return this.mCollator.compare(this.mCollator.getCollationKey(op.getFileName()).getSourceString(), this.mCollator.getCollationKey(oq.getFileName()).getSourceString());
    }

    private int sortBySize(FileInfo op, FileInfo oq) {
        if (!(op.isDirectory() || oq.isDirectory())) {
            long opSize = op.getFileSize();
            long oqSize = oq.getFileSize();
            if (opSize != oqSize) {
                return opSize > oqSize ? -1 : 1;
            }
        }
        return sortByName(op, oq);
    }

    private int sortByTime(FileInfo op, FileInfo oq) {
        long opTime = op.getFileLastModifiedTime();
        long oqTime = oq.getFileLastModifiedTime();
        if (opTime != oqTime) {
            return opTime > oqTime ? -1 : 1;
        } else {
            return sortByName(op, oq);
        }
    }
}
