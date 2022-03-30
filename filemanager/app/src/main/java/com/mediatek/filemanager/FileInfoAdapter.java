package com.mediatek.filemanager;

import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.mediatek.filemanager.service.FileManagerService;
import com.mediatek.filemanager.utils.FileUtils;
import com.mediatek.filemanager.utils.LogUtils;
import com.mediatek.filemanager.utils.ThemeUtils;
import java.util.ArrayList;
import java.util.List;

public class FileInfoAdapter extends BaseAdapter {
    private static final float CUT_ICON_ALPHA = 0.6f;
    private static final float DEFAULT_ICON_ALPHA = 1.0f;
    private static final int DEFAULT_PRIMARY_TEXT_COLOR = -16777216;
    private static final int DEFAULT_SECONDARY_SIZE_TEXT_COLOR = -12500671;
    private static final float HIDE_ICON_ALPHA = 0.3f;
    public static final int MODE_EDIT = 1;
    public static final int MODE_NORMAL = 0;
    public static final int MODE_SEARCH = 2;
    private static final String TAG = "FileInfoAdapter";
    private Context mContext;
    private final List<FileInfo> mFileInfoList;
    private final FileInfoManager mFileInfoManager;
    private final LayoutInflater mInflater;
    private int mMode = 0;
    private final Resources mResources;
    FileManagerService mService = null;

    static class FileViewHolder {
        protected ImageView mIcon;
        protected TextView mName;
        protected TextView mSize;

        public FileViewHolder(TextView name, TextView size, ImageView icon) {
            this.mName = name;
            this.mSize = size;
            this.mIcon = icon;
        }
    }

    public FileInfoAdapter(Context context, FileManagerService fileManagerService, FileInfoManager fileInfoManager) {
        this.mContext = context;
        this.mResources = context.getResources();
        this.mInflater = LayoutInflater.from(context);
        this.mService = fileManagerService;
        this.mFileInfoManager = fileInfoManager;
        this.mFileInfoList = fileInfoManager.getShowFileList();
    }

    public int getPosition(FileInfo fileInfo) {
        return this.mFileInfoList.indexOf(fileInfo);
    }

    public void setChecked(int id, boolean checked) {
        FileInfo checkInfo = (FileInfo) this.mFileInfoList.get(id);
        if (checkInfo != null) {
            checkInfo.setChecked(checked);
        }
    }

    public void setAllItemChecked(boolean checked) {
        for (FileInfo info : this.mFileInfoList) {
            info.setChecked(checked);
        }
        notifyDataSetChanged();
    }

    public int getCheckedItemsCount() {
        int count = 0;
        for (FileInfo fileInfo : this.mFileInfoList) {
            if (fileInfo.isChecked()) {
                count++;
            }
        }
        return count;
    }

    public List<FileInfo> getCheckedFileInfoItemsList() {
        List<FileInfo> fileInfoCheckedList = new ArrayList();
        for (FileInfo fileInfo : this.mFileInfoList) {
            if (fileInfo.isChecked()) {
                fileInfoCheckedList.add(fileInfo);
            }
        }
        return fileInfoCheckedList;
    }

    public FileInfo getFirstCheckedFileInfoItem() {
        for (FileInfo fileInfo : this.mFileInfoList) {
            if (fileInfo.isChecked()) {
                return fileInfo;
            }
        }
        return null;
    }

    public int getCount() {
        return this.mFileInfoList.size();
    }

    public FileInfo getItem(int pos) {
        return (FileInfo) this.mFileInfoList.get(pos);
    }

    public long getItemId(int pos) {
        return (long) pos;
    }

    private void clearChecked() {
        for (FileInfo fileInfo : this.mFileInfoList) {
            if (fileInfo.isChecked()) {
                fileInfo.setChecked(false);
            }
        }
    }

    public void changeMode(int mode) {
        LogUtils.d(TAG, "changeMode, mode = " + mode);
        switch (mode) {
            case 0:
                clearChecked();
                break;
            case 2:
                this.mFileInfoList.clear();
                break;
        }
        this.mMode = mode;
        notifyDataSetChanged();
    }

    public int getMode() {
        return this.mMode;
    }

    public boolean isMode(int mode) {
        return this.mMode == mode;
    }

    public View getView(int pos, View convertView, ViewGroup parent) {
        FileViewHolder viewHolder;
        LogUtils.d(TAG, "getView, pos = " + pos + ",mMode = " + this.mMode);
        View view = convertView;
        if (view == null) {
            view = this.mInflater.inflate(R.layout.adapter_fileinfos, null);
            viewHolder = new FileViewHolder((TextView) view.findViewById(R.id.edit_adapter_name), (TextView) view.findViewById(R.id.edit_adapter_size), (ImageView) view.findViewById(R.id.edit_adapter_img));
            view.setTag(viewHolder);
        } else {
            viewHolder = (FileViewHolder) view.getTag();
        }
        FileInfo currentItem = (FileInfo) this.mFileInfoList.get(pos);
        viewHolder.mName.setText(currentItem.getShowName());
        viewHolder.mName.setTextColor(DEFAULT_PRIMARY_TEXT_COLOR);
        viewHolder.mSize.setTextColor(DEFAULT_SECONDARY_SIZE_TEXT_COLOR);
        view.setBackgroundColor(0);
        switch (this.mMode) {
            case 0:
                setSizeText(viewHolder.mSize, currentItem);
                break;
            case 1:
                if (currentItem.isChecked()) {
                    view.setBackgroundColor(ThemeUtils.getThemeColor(this.mContext));
                }
                setSizeText(viewHolder.mSize, currentItem);
                break;
            case 2:
                setSearchSizeText(viewHolder.mSize, currentItem);
                break;
        }
        setIcon(viewHolder, currentItem, parent.getLayoutDirection());
        return view;
    }

    private void setSearchSizeText(TextView textView, FileInfo fileInfo) {
        textView.setText(fileInfo.getShowParentPath());
        textView.setVisibility(0);
    }

    private void setSizeText(TextView textView, FileInfo fileInfo) {
        StringBuilder sb;
        if (!fileInfo.isDirectory()) {
            sb = new StringBuilder();
            sb.append(this.mResources.getString(R.string.size)).append(" ");
            sb.append(fileInfo.getFileSizeStr(this.mContext));
            textView.setText(sb.toString());
            textView.setVisibility(0);
        } else if (MountPointManager.getInstance().isMountPoint(fileInfo.getFileAbsolutePath())) {
            sb = new StringBuilder();
            long freeSpace = fileInfo.getFile().getFreeSpace();
            String freeSpaceString = FileUtils.sizeToString(this.mContext, freeSpace);
            long totalSpace = fileInfo.getFile().getTotalSpace();
            String totalSpaces = FileUtils.sizeToString(this.mContext, totalSpace);
            LogUtils.d(TAG, "setSizeText, file name = " + fileInfo.getFileName() + ",file path = " + fileInfo.getFileAbsolutePath());
            LogUtils.d(TAG, "setSizeText, freeSpace = " + freeSpace + ",totalSpace = " + totalSpace);
            String[] temp0 = freeSpaceString.split(" ");
            String[] temp1 = totalSpaces.split(" ");
            TextView textView2 = textView;
            textView2.setText(this.mContext.getString(R.string.swaped_storage_room_info, new Object[]{this.mResources.getString(R.string.free_space), " ", Float.valueOf(temp0[0]), temp0[1], "\n", this.mResources.getString(R.string.total_space), " ", Float.valueOf(temp1[0]), temp1[1]}));
            textView.setVisibility(0);
        } else {
            textView.setVisibility(8);
        }
    }

    private void setIcon(FileViewHolder viewHolder, FileInfo fileInfo, int viewDirection) {
        viewHolder.mIcon.setImageBitmap(IconManager.getInstance().getIcon(this.mResources, fileInfo, this.mService, viewDirection));
        viewHolder.mIcon.setAlpha(DEFAULT_ICON_ALPHA);
        if (1 == this.mFileInfoManager.getPasteType() && this.mFileInfoManager.isPasteItem(fileInfo)) {
            viewHolder.mIcon.setAlpha(CUT_ICON_ALPHA);
        }
        if (fileInfo.isHideFile()) {
            viewHolder.mIcon.setAlpha(HIDE_ICON_ALPHA);
        }
    }
}
