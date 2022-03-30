package com.mediatek.filemanager;

import android.app.Application;
import android.content.Intent;
import com.mediatek.filemanager.service.FileManagerService;
import com.mediatek.filemanager.utils.LogUtils;
import com.mediatek.filemanager.utils.PDebug;

public class FileManagerApplication extends Application {
    public static final String TAG = "FileManagerApplication";

    public void onCreate() {
        PDebug.Start("FileManagerApplication - onCreate");
        super.onCreate();
        if (startService(new Intent(getApplicationContext(), FileManagerService.class)) == null) {
            LogUtils.e(TAG, "startService Fails");
        }
        PDebug.End("FileManagerApplication - onCreate");
    }
}
