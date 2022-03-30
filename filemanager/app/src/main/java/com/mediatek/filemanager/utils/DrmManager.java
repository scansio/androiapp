package com.mediatek.filemanager.utils;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import com.mediatek.drm.OmaDrmClient;
import com.mediatek.drm.OmaDrmUiUtils;

public final class DrmManager {
    public static final int ACTIONID_INVALID_DRM = -2;
    public static final int ACTIONID_NOT_DRM = -1;
    public static final String APP_DRM = "application/vnd.oma.drm";
    public static final String EXT_DRM_CONTENT = "dcf";
    private static final String TAG = "DrmManager";
    private static DrmManager sInstance = new DrmManager();
    private OmaDrmClient mDrmManagerClient = null;

    private DrmManager() {
    }

    public void init(Context context) {
        if (OptionsUtils.isMtkDrmApp() && this.mDrmManagerClient == null) {
            this.mDrmManagerClient = new OmaDrmClient(context);
        }
    }

    public static DrmManager getInstance() {
        return sInstance;
    }

    public Bitmap overlayDrmIconSkew(Resources resources, String path, int actionId, int iconId) {
        if (this.mDrmManagerClient == null || !OptionsUtils.isMtkDrmApp()) {
            return null;
        }
        return OmaDrmUiUtils.overlayDrmIconSkew(this.mDrmManagerClient, resources, path, actionId, iconId);
    }

    public String getOriginalMimeType(String path) {
        if (this.mDrmManagerClient == null || !OptionsUtils.isMtkDrmApp()) {
            return "";
        }
        String mimeType = this.mDrmManagerClient.getOriginalMimeType(path);
        if (mimeType != null) {
            return mimeType;
        }
        LogUtils.w(TAG, "#getOriginalMimeType(),mDrmManagerClient.getOriginalMimeType(path) return null.path:" + path);
        return "";
    }

    public boolean isRightsStatus(String path) {
        if (this.mDrmManagerClient == null || !OptionsUtils.isMtkDrmApp() || this.mDrmManagerClient.checkRightsStatus(path, 3) == 0) {
            return false;
        }
        return true;
    }

    public boolean checkDrmObjectType(String path) {
        if (this.mDrmManagerClient == null || !OptionsUtils.isMtkDrmApp() || this.mDrmManagerClient.getDrmObjectType(path, null) == 0) {
            return false;
        }
        return true;
    }

    public void showProtectionInfoDialog(Activity activity, String path) {
        if (this.mDrmManagerClient != null && OptionsUtils.isMtkDrmApp()) {
            OmaDrmUiUtils.showProtectionInfoDialog(activity, path);
        }
    }

    public void release() {
        if (this.mDrmManagerClient != null) {
            this.mDrmManagerClient.release();
            this.mDrmManagerClient = null;
            LogUtils.d(TAG, "release drm manager client.");
        }
    }
}
