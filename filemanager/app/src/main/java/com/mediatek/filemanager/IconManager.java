package com.mediatek.filemanager;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.text.TextUtils;
import com.mediatek.drm.OmaDrmUtils;
import com.mediatek.filemanager.ext.DefaultIconExtension;
import com.mediatek.filemanager.ext.IIconExtension;
import com.mediatek.filemanager.service.FileManagerService;
import com.mediatek.filemanager.utils.DrmManager;
import com.mediatek.filemanager.utils.LogUtils;
import com.mediatek.filemanager.utils.OptionsUtils;
import com.mediatek.pluginmanager.Plugin.ObjectCreationException;
import com.mediatek.pluginmanager.PluginManager;
import java.util.HashMap;

public final class IconManager {
    private static final int OFFX = 4;
    public static final String TAG = "IconManager";
    private static HashMap<Integer, Integer> sCustomDrableIdsMap = new HashMap();
    private static IconManager sInstance = new IconManager();
    private int mCurrentDirection = 0;
    protected HashMap<Integer, Bitmap> mDefIcons = null;
    private boolean mDirectionChanged = false;
    private IIconExtension mExt = null;
    protected Bitmap mIconsHead = null;
    private Resources mRes;
    protected HashMap<Integer, Bitmap> mSdcard2Icons = null;

    private IconManager() {
    }

    public static IconManager getInstance() {
        return sInstance;
    }

    public static int getDrawableId(Context context, String mimeType) {
        if (TextUtils.isEmpty(mimeType)) {
            return R.drawable.fm_unknown;
        }
        if (mimeType.startsWith("application/vnd.android.package-archive")) {
            return R.drawable.fm_apk;
        }
        if (mimeType.startsWith(FileInfo.MIMETYPE_UNRECOGNIZED)) {
            return R.drawable.fm_zip;
        }
        if (mimeType.startsWith("application/ogg") || mimeType.startsWith("audio/")) {
            return R.drawable.fm_audio;
        }
        if (mimeType.startsWith(FileInfo.MIME_HAED_IMAGE)) {
            return R.drawable.fm_picture;
        }
        if (mimeType.startsWith("text/")) {
            return R.drawable.fm_txt;
        }
        if (mimeType.startsWith(FileInfo.MIME_HEAD_VIDEO)) {
            return R.drawable.fm_video;
        }
        return getCustomDrawableId(context, mimeType);
    }

    public static int getUnknownTypeDrawableId() {
        return R.drawable.fm_unknown;
    }

    private static int getCustomDrawableId(Context context, String mimeType) {
        if (!OptionsUtils.isOP01Surported()) {
            return getUnknownTypeDrawableId();
        }
        int fileType = MediaFileManager.getFileTypeForMimeType(mimeType);
        if (sCustomDrableIdsMap.containsKey(Integer.valueOf(fileType))) {
            return ((Integer) sCustomDrableIdsMap.get(Integer.valueOf(fileType))).intValue();
        }
        return getUnknownTypeDrawableId();
    }

    private static boolean isSupportedByCurrentSystem(Context context, String mimeType) {
        PackageManager packageManager = context.getPackageManager();
        Intent intent = new Intent("android.intent.action.VIEW");
        intent.setType(mimeType);
        return packageManager.resolveActivity(intent, 65536) != null;
    }

    public static void updateCustomDrableMap(Context context) {
        sCustomDrableIdsMap.clear();
        if (isSupportedByCurrentSystem(context, "application/vnd.ms-excel") || isSupportedByCurrentSystem(context, "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet") || isSupportedByCurrentSystem(context, "application/vnd.openxmlformats-officedocument.spreadsheetml.template")) {
            sCustomDrableIdsMap.put(Integer.valueOf(MediaFileManager.FILE_TYPE_MS_EXCEL), Integer.valueOf(R.drawable.fm_excel));
            LogUtils.d(TAG, "add excel type drawable");
        }
        if (isSupportedByCurrentSystem(context, "application/mspowerpoint") || isSupportedByCurrentSystem(context, "application/vnd.openxmlformats-officedocument.presentationml.presentation") || isSupportedByCurrentSystem(context, "application/vnd.openxmlformats-officedocument.presentationml.template") || isSupportedByCurrentSystem(context, "application/vnd.openxmlformats-officedocument.presentationml.slideshow")) {
            sCustomDrableIdsMap.put(Integer.valueOf(MediaFileManager.FILE_TYPE_MS_POWERPOINT), Integer.valueOf(R.drawable.fm_ppt));
            LogUtils.d(TAG, "add ppt type drawable");
        }
        if (isSupportedByCurrentSystem(context, "application/msword") || isSupportedByCurrentSystem(context, "application/vnd.openxmlformats-officedocument.wordprocessingml.document") || isSupportedByCurrentSystem(context, "application/vnd.openxmlformats-officedocument.wordprocessingml.template")) {
            sCustomDrableIdsMap.put(Integer.valueOf(MediaFileManager.FILE_TYPE_MS_WORD), Integer.valueOf(R.drawable.fm_word));
            LogUtils.d(TAG, "add world type drawable");
        }
        if (isSupportedByCurrentSystem(context, "application/pdf")) {
            sCustomDrableIdsMap.put(Integer.valueOf(MediaFileManager.FILE_TYPE_PDF), Integer.valueOf(R.drawable.fm_pdf));
            LogUtils.d(TAG, "add pdf type drawable");
        }
    }

    public Bitmap getIcon(Resources res, FileInfo fileInfo, FileManagerService service, int viewDirection) {
        if (this.mCurrentDirection != viewDirection) {
            this.mDirectionChanged = true;
            this.mCurrentDirection = viewDirection;
        }
        Bitmap icon = null;
        boolean isExternal = MountPointManager.getInstance().isExternalFile(fileInfo);
        LogUtils.d(TAG, "getIcon,isExternal =" + isExternal);
        if (fileInfo.isDirectory()) {
            return getFolderIcon(fileInfo, isExternal);
        }
        String mimeType = fileInfo.getFileMimeType(service);
        LogUtils.d(TAG, "getIcon imimeType =" + mimeType);
        int iconId = getDrawableId(service, mimeType);
        if (fileInfo.isDrmFile()) {
            int actionId = OmaDrmUtils.getMediaActionType(mimeType);
            LogUtils.d(TAG, "getIcon isDrmFile & actionId=" + actionId);
            if (actionId != -1) {
                icon = DrmManager.getInstance().overlayDrmIconSkew(res, fileInfo.getFileAbsolutePath(), actionId, iconId);
                if (icon != null && isExternal) {
                    icon = createExternalIcon(icon);
                }
            }
        }
        if (icon == null) {
            return getFileIcon(iconId, isExternal);
        }
        return icon;
    }

    private Bitmap getFileIcon(int iconId, boolean isExternal) {
        if (isExternal) {
            return getExternalIcon(iconId);
        }
        return getDefaultIcon(iconId);
    }

    private Bitmap getFolderIcon(FileInfo fileInfo, boolean isExternal) {
        String path = fileInfo.getFileAbsolutePath();
        if (MountPointManager.getInstance().isInternalMountPath(path)) {
            return getDefaultIcon(R.drawable.phone_storage);
        }
        if (MountPointManager.getInstance().isExternalMountPath(path)) {
            return getDefaultIcon(R.drawable.sdcard);
        }
        if (this.mExt != null && this.mExt.isSystemFolder(path)) {
            Bitmap icon = this.mExt.getSystemFolderIcon(path);
            if (icon != null) {
                if (isExternal) {
                    return createExternalIcon(icon);
                }
                return icon;
            }
        } else if (OptionsUtils.isMtkHotKnotSupported() && fileInfo.getShowName().equalsIgnoreCase("HotKnot") && (MountPointManager.getInstance().isInternalMountPath(fileInfo.getFile().getParent()) || MountPointManager.getInstance().isExternalMountPath(fileInfo.getFile().getParent()))) {
            return getFileIcon(R.drawable.ic_hotknot_folder, isExternal);
        }
        return getFileIcon(R.drawable.fm_folder, isExternal);
    }

    public void init(Context context, String path) {
        this.mRes = context.getResources();
        try {
            this.mExt = (IIconExtension) PluginManager.createPluginObject(context, IIconExtension.class.getName(), new Signature[0]);
        } catch (ObjectCreationException e) {
            this.mExt = new DefaultIconExtension();
        }
        this.mExt.createSystemFolder(path);
    }

    public boolean isSystemFolder(FileInfo fileInfo) {
        if (fileInfo == null || this.mExt == null) {
            return false;
        }
        return this.mExt.isSystemFolder(fileInfo.getFileAbsolutePath());
    }

    public Bitmap getExternalIcon(int resId) {
        if (this.mSdcard2Icons == null) {
            this.mSdcard2Icons = new HashMap();
        }
        if (this.mDirectionChanged) {
            if (this.mDefIcons != null) {
                this.mDefIcons.clear();
            }
            this.mSdcard2Icons.clear();
            this.mIconsHead = null;
            this.mDirectionChanged = false;
        }
        if (this.mSdcard2Icons.containsKey(Integer.valueOf(resId))) {
            return (Bitmap) this.mSdcard2Icons.get(Integer.valueOf(resId));
        }
        Bitmap icon = createExternalIcon(getDefaultIcon(resId));
        this.mSdcard2Icons.put(Integer.valueOf(resId), icon);
        return icon;
    }

    public Bitmap createExternalIcon(Bitmap bitmap) {
        if (bitmap == null) {
            throw new IllegalArgumentException("parameter bitmap is null");
        }
        if (this.mIconsHead == null) {
            this.mIconsHead = BitmapFactory.decodeResource(this.mRes, R.drawable.fm_sdcard2_header);
        }
        Bitmap icon;
        Canvas c;
        if (this.mCurrentDirection == 0) {
            int offx = this.mIconsHead.getWidth() / OFFX;
            icon = Bitmap.createBitmap(offx + bitmap.getWidth(), bitmap.getHeight(), Config.ARGB_8888);
            c = new Canvas(icon);
            c.drawBitmap(bitmap, (float) offx, 0.0f, null);
            c.drawBitmap(this.mIconsHead, 0.0f, 0.0f, null);
            return icon;
        } else if (this.mCurrentDirection == 1) {
            int width = (this.mIconsHead.getWidth() / OFFX) + bitmap.getWidth();
            icon = Bitmap.createBitmap(width, bitmap.getHeight(), Config.ARGB_8888);
            c = new Canvas(icon);
            c.drawBitmap(bitmap, 0.0f, 0.0f, null);
            c.drawBitmap(this.mIconsHead, (float) (width - this.mIconsHead.getWidth()), 0.0f, null);
            return icon;
        } else {
            LogUtils.d(TAG, "createExternalIcon, unknown direction...");
            return null;
        }
    }

    public Bitmap getDefaultIcon(int resId) {
        if (this.mDefIcons == null) {
            this.mDefIcons = new HashMap();
        }
        if (this.mDirectionChanged) {
            this.mDefIcons.clear();
            if (this.mSdcard2Icons != null) {
                this.mSdcard2Icons.clear();
            }
            this.mIconsHead = null;
            this.mDirectionChanged = false;
        }
        if (this.mDefIcons.containsKey(Integer.valueOf(resId))) {
            return (Bitmap) this.mDefIcons.get(Integer.valueOf(resId));
        }
        Bitmap icon = BitmapFactory.decodeResource(this.mRes, resId);
        if (icon == null) {
            throw new IllegalArgumentException("decodeResource()fail, or invalid resId");
        }
        this.mDefIcons.put(Integer.valueOf(resId), icon);
        return icon;
    }
}
