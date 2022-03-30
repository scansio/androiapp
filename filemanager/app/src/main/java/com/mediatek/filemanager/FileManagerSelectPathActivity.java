package com.mediatek.filemanager;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import com.mediatek.filemanager.service.FileManagerService.OperationEventListener;
import com.mediatek.filemanager.service.ProgressInfo;
import com.mediatek.filemanager.utils.LogUtils;
import java.io.File;

public class FileManagerSelectPathActivity extends AbsBaseActivity {
    public static final String DOWNLOAD_PATH_KEY = "download path";
    private static final int SHOW_PATH = 1;
    private static final String TAG = "FileManagerSelectPathActivity";
    private ImageButton mBtnCreateFolder = null;
    private Button mBtnSave = null;
    private final Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            if (msg.what == 1) {
                FileManagerSelectPathActivity.this.showDirectoryContent((String) msg.obj);
            }
        }
    };

    public void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(1);
        super.onCreate(savedInstanceState);
    }

    protected void setMainContentView() {
        setContentView(R.layout.select_path_main);
        this.mBtnSave = (Button) findViewById(R.id.download_btn_save);
        this.mBtnSave.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.putExtra(FileManagerSelectPathActivity.DOWNLOAD_PATH_KEY, FileManagerSelectPathActivity.this.mCurrentPath);
                LogUtils.i(FileManagerSelectPathActivity.TAG, "setMainContentView,OK confirmed,Current Path = " + FileManagerSelectPathActivity.this.mCurrentPath);
                FileManagerSelectPathActivity.this.setResult(-1, intent);
                FileManagerSelectPathActivity.this.finish();
            }
        });
        ((Button) findViewById(R.id.download_btn_cancel)).setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                FileManagerSelectPathActivity.this.setResult(0, new Intent());
                FileManagerSelectPathActivity.this.finish();
            }
        });
        this.mBtnCreateFolder = (ImageButton) findViewById(R.id.btn_create_folder);
        this.mBtnCreateFolder.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                FileManagerSelectPathActivity.this.showCreateFolderDialog();
            }
        });
    }

    protected void serviceConnected() {
        LogUtils.i(TAG, "serviceConnected...");
        super.serviceConnected();
        this.mService.setListType(2, getClass().getName());
    }

    protected void onPause() {
        LogUtils.d(TAG, "onPause...");
        if (this.mService != null) {
            this.mService.setListType(0, getClass().getName());
        }
        super.onPause();
    }

    protected String initCurrentFileInfo() {
        LogUtils.d(TAG, "initCurrentFileInfo...");
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            final String downloadPathKey = extras.getString(DOWNLOAD_PATH_KEY);
            if (downloadPathKey != null && this.mMountPointManager.isRootPathMount(downloadPathKey)) {
                LogUtils.d(TAG, "initCurrentFileInfo,downloadPathKey = " + downloadPathKey);
                if (this.mService != null) {
                    this.mService.createFolder(getClass().getName(), downloadPathKey, new OperationEventListener() {
                        public void onTaskResult(int result) {
                            if (result == 0 || result == -4) {
                                Message.obtain(FileManagerSelectPathActivity.this.mHandler, 1, downloadPathKey).sendToTarget();
                            } else {
                                Message.obtain(FileManagerSelectPathActivity.this.mHandler, 1, FileManagerSelectPathActivity.this.mMountPointManager.getRootPath()).sendToTarget();
                            }
                        }

                        public void onTaskProgress(ProgressInfo progressInfo) {
                        }

                        public void onTaskPrepare() {
                        }
                    });
                    return null;
                }
            }
        }
        return this.mMountPointManager.getRootPath();
    }

    protected void onPathChanged() {
        LogUtils.d(TAG, "onPathChanged...");
        super.onPathChanged();
        boolean enable = false;
        if (this.mMountPointManager.isRootPathMount(this.mCurrentPath) && new File(this.mCurrentPath).canWrite()) {
            enable = true;
        }
        updateButtonsState(enable);
    }

    private void updateButtonsState(boolean flag) {
        LogUtils.d(TAG, "updateButtonsState flag=" + flag);
        this.mBtnSave.setEnabled(flag);
        this.mBtnSave.setClickable(flag);
        this.mBtnCreateFolder.setEnabled(flag);
        this.mBtnCreateFolder.setClickable(flag);
    }

    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (this.mService == null || !this.mService.isBusy(getClass().getName())) {
            FileInfo selecteItemFileInfo = (FileInfo) parent.getItemAtPosition(position);
            if (selecteItemFileInfo.isDirectory()) {
                int top = view.getTop();
                LogUtils.v(TAG, "top = " + top);
                addToNavigationList(this.mCurrentPath, selecteItemFileInfo, top);
                showDirectoryContent(selecteItemFileInfo.getFileAbsolutePath());
                return;
            }
            LogUtils.d(TAG, "onItemClick,click file,return.");
            return;
        }
        LogUtils.d(TAG, "onItemClick,service is busy,return.");
    }
}
