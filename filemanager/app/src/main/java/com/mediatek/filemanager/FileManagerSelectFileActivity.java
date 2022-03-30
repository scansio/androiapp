package com.mediatek.filemanager;

import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import com.mediatek.filemanager.utils.LogUtils;

public class FileManagerSelectFileActivity extends AbsBaseActivity {
    private static final String TAG = "FileManagerSelectFileActivity";

    protected void setMainContentView() {
        setContentView(R.layout.select_file_main);
        ((Button) findViewById(R.id.select_cancel)).setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                LogUtils.d(FileManagerSelectFileActivity.TAG, "click 'Cancel' to quit directly ");
                FileManagerSelectFileActivity.this.finish();
            }
        });
    }

    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (this.mService == null || !this.mService.isBusy(getClass().getName())) {
            FileInfo selecteItemFileInfo = (FileInfo) parent.getItemAtPosition(position);
            if (selecteItemFileInfo.isDirectory()) {
                int top = view.getTop();
                LogUtils.v(TAG, "onItemClick directory top = " + top);
                addToNavigationList(this.mCurrentPath, selecteItemFileInfo, top);
                showDirectoryContent(selecteItemFileInfo.getFileAbsolutePath());
                return;
            }
            Intent intent = new Intent();
            Uri uri = selecteItemFileInfo.getUri();
            LogUtils.d(TAG, "onItemClick RESULT_OK, uri : " + uri);
            intent.setData(uri);
            setResult(-1, intent);
            finish();
            return;
        }
        LogUtils.d(TAG, "onItemClick,service is busy.");
    }

    protected String initCurrentFileInfo() {
        String rootPath = this.mMountPointManager.getRootPath();
        LogUtils.d(TAG, "initCurrentFileInfo,rootPath = " + rootPath);
        return rootPath;
    }
}
