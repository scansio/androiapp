package com.mediatek.filemanager;

import android.app.SearchManager;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings.Global;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MenuItem.OnActionExpandListener;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout.LayoutParams;
import android.widget.SearchView;
import android.widget.TextView;
import com.mediatek.filemanager.service.FileManagerService.OperationEventListener;
import com.mediatek.filemanager.service.ProgressInfo;
import com.mediatek.filemanager.utils.LogUtils;

public class FileManagerSearchActivity extends AbsBaseActivity {
    public static final String CURRENT_PATH = "current_path";
    public static final String SEARCH_TEXT = "search_text";
    public static final String SEARCH_TOTAL = "search_total";
    private static final String TAG = "FileManagerSearchActivity";
    private TextView mResultView = null;
    private MenuItem mSearchItem;
    private String mSearchPath = null;
    private String mSearchText = null;
    private SearchView mSearchView = null;
    private long mTotal = 0;

    protected class SearchListener implements OperationEventListener {
        private static final int FRIST_UPDATE_COUNT = 20;
        private static final int NEED_UPDATE_LIST = 6;
        private int mCount = 0;
        private boolean mIsResultSet = false;

        public SearchListener(String text) {
            if (text == null) {
                throw new IllegalArgumentException();
            }
            FileManagerSearchActivity.this.mSearchText = text;
        }

        public void onTaskResult(int result) {
            FileManagerSearchActivity.this.mFileInfoManager.updateSearchList();
            FileManagerSearchActivity.this.mAdapter.notifyDataSetChanged();
        }

        public void onTaskPrepare() {
            FileManagerSearchActivity.this.mAdapter.changeMode(2);
        }

        public void onTaskProgress(ProgressInfo progressInfo) {
            if (!progressInfo.isFailInfo()) {
                if (!(FileManagerSearchActivity.this.mResultView == null || this.mIsResultSet)) {
                    FileManagerSearchActivity.this.mTotal = progressInfo.getTotal();
                    FileManagerSearchActivity.this.mResultView.setVisibility(0);
                    FileManagerSearchActivity.this.mResultView.setText(FileManagerSearchActivity.this.getResources().getString(R.string.search_result, new Object[]{FileManagerSearchActivity.this.mSearchText, Long.valueOf(FileManagerSearchActivity.this.mTotal)}));
                    this.mIsResultSet = true;
                }
                if (progressInfo.getFileInfo() != null) {
                    FileManagerSearchActivity.this.mFileInfoManager.addItem(progressInfo.getFileInfo());
                }
                this.mCount++;
                if (this.mCount > FRIST_UPDATE_COUNT && FileManagerSearchActivity.this.mListView.getLastVisiblePosition() + NEED_UPDATE_LIST > FileManagerSearchActivity.this.mAdapter.getCount()) {
                    FileManagerSearchActivity.this.mFileInfoManager.updateSearchList();
                    FileManagerSearchActivity.this.mAdapter.notifyDataSetChanged();
                    this.mCount = 0;
                }
            }
        }
    }

    public void onCreate(Bundle savedInstanceState) {
        Global.putInt(getApplicationContext().getContentResolver(), "nochange_dim", 1);
        Log.d("zdx", "onCreate->" + Global.getInt(getApplicationContext().getContentResolver(), "nochange_dim", 0));
        super.onCreate(savedInstanceState);
    }

    protected void onDestroy() {
        if (this.mService != null) {
            this.mService.cancel(getClass().getName());
        }
        Global.putInt(getApplicationContext().getContentResolver(), "nochange_dim", 0);
        Log.d("zdx", "onDestroy->" + Global.getInt(getApplicationContext().getContentResolver(), "nochange_dim", 0));
        super.onDestroy();
    }

    protected void onNewIntent(Intent intent) {
        setIntent(intent);
        handleIntent(intent);
    }

    protected void onSaveInstanceState(Bundle outState) {
        if (this.mSearchText != null) {
            outState.putString(SEARCH_TEXT, this.mSearchText);
            outState.putLong(SEARCH_TOTAL, this.mTotal);
        }
        super.onSaveInstanceState(outState);
    }

    protected void serviceConnected() {
        super.serviceConnected();
        Intent intent = getIntent();
        this.mSearchPath = intent.getStringExtra(CURRENT_PATH);
        if (this.mSearchPath == null) {
            this.mSearchPath = this.mMountPointManager.getRootPath();
        }
        if (!this.mSearchPath.endsWith(MountPointManager.SEPARATOR)) {
            this.mSearchPath += MountPointManager.SEPARATOR;
        }
        if (this.mSavedInstanceState == null || this.mResultView == null) {
            this.mAdapter.changeMode(2);
        } else {
            this.mSearchText = this.mSavedInstanceState.getString(SEARCH_TEXT);
            if (!TextUtils.isEmpty(this.mSearchText)) {
                this.mTotal = this.mSavedInstanceState.getLong(SEARCH_TOTAL);
                this.mResultView.setVisibility(0);
                this.mResultView.setText(getResources().getString(R.string.search_result, new Object[]{this.mSearchText, Long.valueOf(this.mTotal)}));
            }
        }
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {
        if ("android.intent.action.VIEW".equals(intent.getAction())) {
            String path = null;
            if (intent.getData() != null) {
                path = intent.getData().toString();
            }
            if (TextUtils.isEmpty(path)) {
                LogUtils.w(TAG, "handleIntent intent uri path == null");
            } else {
                onItemClick(new FileInfo(path));
            }
        } else if ("android.intent.action.SEARCH".equals(intent.getAction())) {
            requestSearch(intent.getStringExtra("query"));
        }
    }

    private void requestSearch(String query) {
        if (query == null || query.isEmpty()) {
            this.mToastHelper.showToast((int) R.string.search_text_empty);
        } else if (this.mService != null) {
            this.mService.search(getClass().getName(), query, this.mSearchPath, new SearchListener(query));
            if (this.mSearchView != null) {
                this.mSearchView.setQuery(query, false);
                this.mSearchView.clearFocus();
            }
        }
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search_options_menu, menu);
        this.mSearchItem = menu.findItem(R.id.search);
        this.mSearchView = (SearchView) menu.findItem(R.id.search).getActionView();
        this.mSearchView.setSearchEditBg(R.drawable.tecno_actionbar_edit_bg);
        this.mSearchView.setSearchHintColor(-11184811);
        this.mSearchView.setLayoutParams(new LayoutParams(-1, -2));
        SearchManager searchManager = (SearchManager) getSystemService("search");
        if (searchManager != null) {
            this.mSearchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        }
        this.mSearchItem.expandActionView();
        this.mSearchItem.setOnActionExpandListener(new OnActionExpandListener() {
            public boolean onMenuItemActionExpand(MenuItem item) {
                return true;
            }

            public boolean onMenuItemActionCollapse(MenuItem item) {
                FileManagerSearchActivity.this.finish();
                return false;
            }
        });
        if (!TextUtils.isEmpty(this.mSearchText)) {
            this.mSearchView.setQuery(this.mSearchText, false);
            this.mSearchView.clearFocus();
        }
        return true;
    }

    protected String initCurrentFileInfo() {
        return null;
    }

    protected void setMainContentView() {
        if ("android.intent.action.VIEW".equals(getIntent().getAction())) {
            finish();
            handleIntent(getIntent());
            return;
        }
        setTheme(R.style.FileManagerOperTheme);
        setContentView(R.layout.search_main);
        this.mResultView = (TextView) findViewById(R.id.search_result);
    }

    public void onItemClick(AdapterView<?> adapterView, View view, int position, long arg3) {
        LogUtils.d(TAG, "Selected position: " + position);
        if (position >= this.mAdapter.getCount() || position < 0) {
            LogUtils.e(TAG, "click events error");
            LogUtils.e(TAG, "mFileInfoList.size(): " + this.mAdapter.getCount());
            return;
        }
        onItemClick(this.mAdapter.getItem(position));
    }

    private void onItemClick(FileInfo selectedFileInfo) {
        if (this.mService != null) {
            Intent intent;
            if (selectedFileInfo.isDirectory()) {
                intent = new Intent(this, FileManagerOperationActivity.class);
                intent.putExtra(FileManagerOperationActivity.INTENT_EXTRA_SELECT_PATH, selectedFileInfo.getFileAbsolutePath());
                intent.setFlags(268435456);
                startActivity(intent);
            } else {
                boolean canOpen = true;
                String mimeType = selectedFileInfo.getFileMimeType(this.mService);
                if (selectedFileInfo.isDrmFile() && TextUtils.isEmpty(mimeType)) {
                    canOpen = false;
                    this.mToastHelper.showToast((int) R.string.msg_unable_open_file);
                }
                if (canOpen) {
                    intent = new Intent("android.intent.action.VIEW");
                    Uri uri = selectedFileInfo.getUri();
                    LogUtils.d(TAG, "Open uri file: " + uri);
                    intent.setDataAndType(uri, mimeType);
                    try {
                        startActivity(intent);
                    } catch (ActivityNotFoundException e) {
                        this.mToastHelper.showToast((int) R.string.msg_unable_open_file);
                        LogUtils.w(TAG, "Cannot open file: " + selectedFileInfo.getFileAbsolutePath());
                    }
                }
            }
            finish();
        }
    }
}
