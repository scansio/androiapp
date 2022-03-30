package com.mediatek.filemanager;

import android.app.Activity;
import android.app.DialogFragment;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences.Editor;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.MarginLayoutParams;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.Scroller;
import com.mediatek.filemanager.AlertDialogFragment.EditDialogFragmentBuilder;
import com.mediatek.filemanager.AlertDialogFragment.EditTextDialogFragment;
import com.mediatek.filemanager.AlertDialogFragment.EditTextDialogFragment.EditTextDoneListener;
import com.mediatek.filemanager.AlertDialogFragment.OnDialogDismissListener;
import com.mediatek.filemanager.FileInfoManager.NavigationRecord;
import com.mediatek.filemanager.MountReceiver.MountListener;
import com.mediatek.filemanager.service.FileManagerService;
import com.mediatek.filemanager.service.FileManagerService.OperationEventListener;
import com.mediatek.filemanager.service.FileManagerService.ServiceBinder;
import com.mediatek.filemanager.service.ProgressInfo;
import com.mediatek.filemanager.utils.DrmManager;
import com.mediatek.filemanager.utils.LogUtils;
import com.mediatek.filemanager.utils.LongStringUtils;
import com.mediatek.filemanager.utils.PDebug;
import com.mediatek.filemanager.utils.ToastHelper;
import java.util.ArrayList;
import java.util.List;

public abstract class AbsBaseActivity extends Activity implements OnItemClickListener, OnClickListener, MountListener, OnDialogDismissListener {
    public static final String CREATE_FOLDER_DIALOG_TAG = "CreateFolderDialog";
    protected static final int DIALOG_CREATE_FOLDER = 1;
    public static final int MSG_DO_EJECTED = 1;
    public static final int MSG_DO_MOUNTED = 0;
    public static final int MSG_DO_SDSWAP = 3;
    public static final int MSG_DO_UNMOUNTED = 2;
    private static final long NAV_BAR_AUTO_SCROLL_DELAY = 100;
    private static final String PREF_SHOW_HIDEN_FILE = "pref_show_hiden_file";
    public static final String SAVED_PATH_KEY = "saved_path";
    private static final int TAB_TET_MAX_LENGTH = 250;
    private static final String TAG = "AbsBaseActivity";
    protected FileInfoAdapter mAdapter = null;
    protected String mCurrentPath = null;
    protected FileInfoManager mFileInfoManager = null;
    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            LogUtils.d(AbsBaseActivity.TAG, "handleMessage, msg = " + msg.what);
            switch (msg.what) {
                case 0:
                    AbsBaseActivity.this.doOnMounted((String) msg.obj);
                    return;
                case 1:
                    AbsBaseActivity.this.doOnEjected((String) msg.obj);
                    return;
                case 2:
                    AbsBaseActivity.this.doOnUnMounted((String) msg.obj);
                    return;
                case 3:
                    AbsBaseActivity.this.doOnSdSwap();
                    return;
                default:
                    return;
            }
        }
    };
    protected boolean mIsAlertDialogShowing = false;
    protected ListView mListView = null;
    protected MountPointManager mMountPointManager = null;
    protected MountReceiver mMountReceiver = null;
    protected SlowHorizontalScrollView mNavigationBar = null;
    protected Bundle mSavedInstanceState = null;
    protected FileInfo mSelectedFileInfo = null;
    protected int mSelectedTop = -1;
    protected FileManagerService mService = null;
    protected boolean mServiceBinded = false;
    private final ServiceConnection mServiceConnection = new ServiceConnection() {
        public void onServiceDisconnected(ComponentName name) {
            AbsBaseActivity.this.mService.disconnected(getClass().getName());
            AbsBaseActivity.this.mServiceBinded = false;
            LogUtils.w(AbsBaseActivity.TAG, "onServiceDisconnected");
        }

        public void onServiceConnected(ComponentName name, IBinder service) {
            LogUtils.d(AbsBaseActivity.TAG, "onServiceConnected");
            AbsBaseActivity.this.mService = ((ServiceBinder) service).getServiceInstance();
            AbsBaseActivity.this.mServiceBinded = true;
            AbsBaseActivity.this.serviceConnected();
        }
    };
    protected int mSortType = 0;
    protected TabManager mTabManager = null;
    protected ToastHelper mToastHelper = null;
    protected int mTop = -1;

    protected final class CreateFolderListener implements EditTextDoneListener {
        protected CreateFolderListener() {
        }

        public void onClick(String text) {
            if (AbsBaseActivity.this.mService != null) {
                AbsBaseActivity.this.mService.createFolder(AbsBaseActivity.this.getClass().getName(), AbsBaseActivity.this.mCurrentPath + MountPointManager.SEPARATOR + text, new LightOperationListener(text));
            }
        }
    }

    protected class LightOperationListener implements OperationEventListener {
        String mDstName = null;

        LightOperationListener(String dstName) {
            this.mDstName = dstName;
        }

        public void onTaskResult(int errorType) {
            LogUtils.i(AbsBaseActivity.TAG, "LightOperationListener,TaskResult result = " + errorType);
            switch (errorType) {
                case OperationEventListener.ERROR_CODE_USER_CANCEL /*-7*/:
                case 0:
                    FileInfo fileInfo = AbsBaseActivity.this.mFileInfoManager.updateOneFileInfoList(AbsBaseActivity.this.mCurrentPath, AbsBaseActivity.this.mSortType);
                    AbsBaseActivity.this.mAdapter.notifyDataSetChanged();
                    if (fileInfo != null) {
                        int postion = AbsBaseActivity.this.mAdapter.getPosition(fileInfo);
                        LogUtils.d(AbsBaseActivity.TAG, "LightOperation postion = " + postion);
                        AbsBaseActivity.this.mListView.setSelection(postion);
                        AbsBaseActivity.this.invalidateOptionsMenu();
                        return;
                    }
                    return;
                case OperationEventListener.ERROR_CODE_NOT_ENOUGH_SPACE /*-5*/:
                    AbsBaseActivity.this.mToastHelper.showToast((int) R.string.insufficient_memory);
                    return;
                case OperationEventListener.ERROR_CODE_FILE_EXIST /*-4*/:
                    if (this.mDstName != null) {
                        AbsBaseActivity.this.mToastHelper.showToast(AbsBaseActivity.this.getResources().getString(R.string.already_exists, new Object[]{this.mDstName}));
                        return;
                    }
                    return;
                case OperationEventListener.ERROR_CODE_NAME_TOO_LONG /*-3*/:
                    AbsBaseActivity.this.mToastHelper.showToast((int) R.string.file_name_too_long);
                    return;
                case -2:
                    AbsBaseActivity.this.mToastHelper.showToast((int) R.string.invalid_empty_name);
                    return;
                case -1:
                    AbsBaseActivity.this.mToastHelper.showToast((int) R.string.operation_fail);
                    return;
                default:
                    LogUtils.e(AbsBaseActivity.TAG, "wrong errorType for LightOperationListener");
                    return;
            }
        }

        public void onTaskPrepare() {
        }

        public void onTaskProgress(ProgressInfo progressInfo) {
        }
    }

    protected class ListListener implements OperationEventListener {
        public static final String LIST_DIALOG_TAG = "ListDialogFragment";

        protected ListListener() {
        }

        protected void dismissDialogFragment() {
            LogUtils.d(AbsBaseActivity.TAG, "ListListener dismissDialogFragment");
            DialogFragment listDialogFragment = (DialogFragment) AbsBaseActivity.this.getFragmentManager().findFragmentByTag(LIST_DIALOG_TAG);
            if (listDialogFragment != null) {
                LogUtils.d(AbsBaseActivity.TAG, "ListListener listDialogFragment != null dismiss");
                listDialogFragment.dismissAllowingStateLoss();
                return;
            }
            LogUtils.d(AbsBaseActivity.TAG, "dismissDialogFragment listDialogFragment == null on dismiss....");
        }

        public void onTaskResult(int result) {
            LogUtils.i(AbsBaseActivity.TAG, "ListListener,TaskResult result = " + result);
            if (AbsBaseActivity.this.mAdapter.isMode(1)) {
                AbsBaseActivity.this.mFileInfoManager.loadFileInfoList(AbsBaseActivity.this.mCurrentPath, AbsBaseActivity.this.mSortType, AbsBaseActivity.this.mSelectedFileInfo);
                AbsBaseActivity.this.mSelectedFileInfo = AbsBaseActivity.this.mAdapter.getFirstCheckedFileInfoItem();
            } else {
                AbsBaseActivity.this.mFileInfoManager.loadFileInfoList(AbsBaseActivity.this.mCurrentPath, AbsBaseActivity.this.mSortType);
            }
            AbsBaseActivity.this.mAdapter.notifyDataSetChanged();
            int selectedItemPosition = AbsBaseActivity.this.restoreSelectedPosition();
            if (selectedItemPosition == -1) {
                AbsBaseActivity.this.mListView.setSelectionAfterHeaderView();
            } else if (selectedItemPosition >= 0 && selectedItemPosition < AbsBaseActivity.this.mAdapter.getCount()) {
                if (AbsBaseActivity.this.mSelectedTop != -1) {
                    AbsBaseActivity.this.mListView.setSelectionFromTop(selectedItemPosition, AbsBaseActivity.this.mSelectedTop);
                    AbsBaseActivity.this.mSelectedTop = -1;
                } else if (AbsBaseActivity.this.mTop != -1) {
                    AbsBaseActivity.this.mListView.setSelectionFromTop(selectedItemPosition, AbsBaseActivity.this.mTop);
                    AbsBaseActivity.this.mTop = -1;
                } else {
                    AbsBaseActivity.this.mListView.setSelection(selectedItemPosition);
                }
            }
            dismissDialogFragment();
            AbsBaseActivity.this.onPathChanged();
        }

        public void onTaskPrepare() {
        }

        public void onTaskProgress(ProgressInfo progressInfo) {
            ProgressDialogFragment listDialogFragment = (ProgressDialogFragment) AbsBaseActivity.this.getFragmentManager().findFragmentByTag(LIST_DIALOG_TAG);
            if (AbsBaseActivity.this.isResumed()) {
                if (listDialogFragment == null) {
                    LogUtils.d(AbsBaseActivity.TAG, " isResumed() onTaskProgress listDialogFragment == null on dismiss....");
                    listDialogFragment = ProgressDialogFragment.newInstance(1, -1, R.string.loading, -1);
                    listDialogFragment.setViewDirection(AbsBaseActivity.this.getViewDirection());
                    listDialogFragment.show(AbsBaseActivity.this.getFragmentManager(), LIST_DIALOG_TAG);
                    AbsBaseActivity.this.getFragmentManager().executePendingTransactions();
                }
                listDialogFragment.setProgress(progressInfo);
            }
        }
    }

    private static class SlowHorizontalScrollView extends HorizontalScrollView {
        private static final int SCROLL_DURATION = 2000;
        private static final String TAG = "SlowHorizontalScrollView";
        private final Scroller mScroller = new Scroller(getContext());

        public SlowHorizontalScrollView(Context context, AttributeSet attrs, int defStyle) {
            super(context, attrs, defStyle);
        }

        public SlowHorizontalScrollView(Context context) {
            super(context);
        }

        public SlowHorizontalScrollView(Context context, AttributeSet attrs) {
            super(context, attrs);
        }

        public void startHorizontalScroll(int startX, int dx) {
            LogUtils.d(TAG, "start scroll");
            this.mScroller.startScroll(startX, 0, dx, 0, SCROLL_DURATION);
            invalidate();
        }

        public void computeScroll() {
            if (this.mScroller.computeScrollOffset()) {
                scrollTo(this.mScroller.getCurrX(), 0);
                postInvalidate();
            }
            super.computeScroll();
        }

        public boolean onTouchEvent(MotionEvent ev) {
            this.mScroller.abortAnimation();
            return super.onTouchEvent(ev);
        }
    }

    protected class TabManager {
        private LayoutParams mBlanckBtnParam = null;
        private final Button mBlankTab;
        private String mCurFilePath = null;
        private final List<String> mTabNameList = new ArrayList();
        protected LinearLayout mTabsHolder = null;

        public TabManager() {
            this.mTabsHolder = (LinearLayout) AbsBaseActivity.this.findViewById(R.id.tabs_holder);
            this.mBlankTab = new Button(AbsBaseActivity.this);
            this.mBlankTab.setBackgroundDrawable(AbsBaseActivity.this.getResources().getDrawable(R.drawable.fm_blank_tab));
            this.mBlanckBtnParam = new LayoutParams(new MarginLayoutParams(-1, -1));
            this.mBlankTab.setLayoutParams(this.mBlanckBtnParam);
            this.mTabsHolder.addView(this.mBlankTab);
        }

        public void refreshTab(String initFileInfo) {
            LogUtils.d(AbsBaseActivity.TAG, "refreshTab,initFileInfo = " + initFileInfo);
            this.mTabsHolder.removeViews(0, this.mTabsHolder.getChildCount());
            this.mTabNameList.clear();
            if (AbsBaseActivity.this.getViewDirection() == 0) {
                this.mBlanckBtnParam.setMargins((int) AbsBaseActivity.this.getResources().getDimension(R.dimen.tab_margin_left), 0, (int) AbsBaseActivity.this.getResources().getDimension(R.dimen.tab_margin_right), 0);
            } else if (AbsBaseActivity.this.getViewDirection() == 1) {
                this.mBlanckBtnParam.setMargins((int) AbsBaseActivity.this.getResources().getDimension(R.dimen.tab_margin_right), 0, (int) AbsBaseActivity.this.getResources().getDimension(R.dimen.tab_margin_left), 0);
            }
            this.mBlankTab.setLayoutParams(this.mBlanckBtnParam);
            this.mCurFilePath = initFileInfo;
            if (this.mCurFilePath != null) {
                addTab(MountPointManager.HOME);
                if (!AbsBaseActivity.this.mMountPointManager.isRootPath(this.mCurFilePath)) {
                    for (String string : AbsBaseActivity.this.mMountPointManager.getDescriptionPath(this.mCurFilePath).split(MountPointManager.SEPARATOR)) {
                        addTab(string);
                    }
                    if (AbsBaseActivity.this.getViewDirection() == 0) {
                        startActionBarScroll();
                    } else if (AbsBaseActivity.this.getViewDirection() == 1) {
                        AbsBaseActivity.this.mNavigationBar.startHorizontalScroll(-AbsBaseActivity.this.mNavigationBar.getScrollX(), -AbsBaseActivity.this.mNavigationBar.getRight());
                    }
                }
            }
            updateHomeButton();
        }

        private void startActionBarScroll() {
            int tabHostCount = this.mTabsHolder.getChildCount();
            int navigationBarCount = AbsBaseActivity.this.mNavigationBar.getChildCount();
            if (tabHostCount > 2 && navigationBarCount >= 1) {
                View view = AbsBaseActivity.this.mNavigationBar.getChildAt(navigationBarCount - 1);
                if (view == null) {
                    LogUtils.d(AbsBaseActivity.TAG, "startActionBarScroll, navigationbar child is null");
                    return;
                }
                AbsBaseActivity.this.mNavigationBar.startHorizontalScroll(AbsBaseActivity.this.mNavigationBar.getScrollX(), view.getRight() - AbsBaseActivity.this.mNavigationBar.getScrollX());
            }
        }

        protected void updateHomeButton() {
            ImageButton homeBtn = (ImageButton) this.mTabsHolder.getChildAt(0);
            if (homeBtn == null) {
                LogUtils.w(AbsBaseActivity.TAG, "HomeBtm is null,return.");
                return;
            }
            Resources resources = AbsBaseActivity.this.getResources();
            if (this.mTabsHolder.getChildCount() == 2) {
                homeBtn.setBackgroundDrawable(resources.getDrawable(R.drawable.custom_home_ninepatch_tab));
                homeBtn.setImageDrawable(resources.getDrawable(R.drawable.ic_home_text));
                homeBtn.setPadding((int) resources.getDimension(R.dimen.home_btn_padding), 0, (int) resources.getDimension(R.dimen.home_btn_padding), 0);
                return;
            }
            homeBtn.setBackgroundDrawable(resources.getDrawable(R.drawable.custom_home_ninepatch_tab));
            homeBtn.setImageDrawable(resources.getDrawable(R.drawable.ic_home));
        }

        private void showPrevNavigationView(String newPath) {
            refreshTab(newPath);
            AbsBaseActivity.this.showDirectoryContent(newPath);
        }

        protected void addTab(String text) {
            View viewLikeBtn;
            this.mTabsHolder.removeView(this.mBlankTab);
            LayoutParams mlp;
            if (this.mTabNameList.isEmpty()) {
                viewLikeBtn = new ImageButton(AbsBaseActivity.this);
                mlp = new LayoutParams(new MarginLayoutParams(-2, -1));
                mlp.setMargins(0, 0, 0, 0);
                viewLikeBtn.setLayoutParams(mlp);
            } else {
                View button = new Button(AbsBaseActivity.this);
                button.setTextColor(-1);
                button.setBackgroundDrawable(AbsBaseActivity.this.getResources().getDrawable(R.drawable.custom_tab));
                button.setMaxWidth(AbsBaseActivity.TAB_TET_MAX_LENGTH);
                LongStringUtils.fadeOutLongString(button);
                button.setText(text + "  ");
                mlp = new LayoutParams(new MarginLayoutParams(-2, -1));
                if (AbsBaseActivity.this.getViewDirection() == 0) {
                    mlp.setMargins((int) AbsBaseActivity.this.getResources().getDimension(R.dimen.tab_margin_left), 0, 0, 0);
                } else if (AbsBaseActivity.this.getViewDirection() == 1) {
                    mlp.setMargins(0, 0, (int) AbsBaseActivity.this.getResources().getDimension(R.dimen.tab_margin_left), 0);
                }
                button.setLayoutParams(mlp);
                viewLikeBtn = button;
            }
            viewLikeBtn.setOnClickListener(AbsBaseActivity.this);
            viewLikeBtn.setId(this.mTabNameList.size());
            this.mTabsHolder.addView(viewLikeBtn);
            this.mTabNameList.add(text);
            this.mTabsHolder.addView(this.mBlankTab);
        }

        protected void updateNavigationBar(int id) {
            LogUtils.d(AbsBaseActivity.TAG, "updateNavigationBar,id = " + id);
            if (id < this.mTabNameList.size() - 1) {
                int i;
                int count = this.mTabNameList.size() - id;
                this.mTabsHolder.removeViews(id + 1, count);
                for (i = 1; i < count; i++) {
                    this.mTabNameList.remove(this.mTabNameList.size() - 1);
                }
                this.mTabsHolder.addView(this.mBlankTab);
                if (id == 0) {
                    this.mCurFilePath = AbsBaseActivity.this.mMountPointManager.getRootPath();
                } else {
                    String mntPointPath = AbsBaseActivity.this.mMountPointManager.getRealMountPointPath(this.mCurFilePath);
                    LogUtils.d(AbsBaseActivity.TAG, "mntPointPath: " + mntPointPath + " for mCurFilepath: " + this.mCurFilePath);
                    String path = this.mCurFilePath.substring(mntPointPath.length() + 1);
                    StringBuilder sb = new StringBuilder(mntPointPath);
                    String[] pathParts = path.split(MountPointManager.SEPARATOR);
                    for (i = 2; i <= id; i++) {
                        sb.append(MountPointManager.SEPARATOR);
                        sb.append(pathParts[i - 2]);
                    }
                    this.mCurFilePath = sb.toString();
                    LogUtils.d(AbsBaseActivity.TAG, "to enter file path: " + this.mCurFilePath);
                }
                if (AbsBaseActivity.this.mListView.getCount() > 0) {
                    View view = AbsBaseActivity.this.mListView.getChildAt(0);
                    if (view != null) {
                        int pos = AbsBaseActivity.this.mListView.getPositionForView(view);
                        FileInfo selectedFileInfo = AbsBaseActivity.this.mAdapter.getItem(pos);
                        int top = view.getTop();
                        LogUtils.d(AbsBaseActivity.TAG, "updateNavigationBar, pos: " + pos + " top: " + top);
                        AbsBaseActivity.this.addToNavigationList(AbsBaseActivity.this.mCurrentPath, selectedFileInfo, top);
                    }
                }
                AbsBaseActivity.this.showDirectoryContent(this.mCurFilePath);
                updateHomeButton();
            }
        }
    }

    protected abstract String initCurrentFileInfo();

    protected abstract void setMainContentView();

    public void onDialogDismiss() {
        LogUtils.d(TAG, "dialog dismissed...");
        this.mIsAlertDialogShowing = false;
    }

    private void doPrepareForMount(String mountPoint) {
        LogUtils.i(TAG, "doPrepareForMount,mountPoint = " + mountPoint);
        if ((this.mCurrentPath + MountPointManager.SEPARATOR).startsWith(mountPoint + MountPointManager.SEPARATOR) || this.mMountPointManager.isRootPath(this.mCurrentPath)) {
            LogUtils.d(TAG, "pre-onMounted");
            if (this.mService != null && this.mService.isBusy(getClass().getName())) {
                this.mService.cancel(getClass().getName());
            }
        }
        this.mMountPointManager.init(getApplicationContext());
    }

    public void onMounted(String mountPoint) {
        LogUtils.i(TAG, "onMounted,mountPoint = " + mountPoint);
        Message.obtain(this.mHandler, 0, mountPoint).sendToTarget();
    }

    private void doOnMounted(String mountPoint) {
        LogUtils.i(TAG, "doOnMounted,mountPoint = " + mountPoint);
        doPrepareForMount(mountPoint);
        if (this.mMountPointManager.isRootPath(this.mCurrentPath)) {
            LogUtils.d(TAG, "doOnMounted,mCurrentPath is root path: " + this.mCurrentPath);
            showDirectoryContent(this.mCurrentPath);
        }
    }

    public void onUnMounted(String unMountPoint) {
        LogUtils.i(TAG, "onUnMounted,unMountPoint: " + unMountPoint);
        Message.obtain(this.mHandler, 2, unMountPoint).sendToTarget();
    }

    public void onEjected(String unMountPoint) {
        LogUtils.i(TAG, "onEjected,unMountPoint: " + unMountPoint);
        Message.obtain(this.mHandler, 1, unMountPoint).sendToTarget();
    }

    public void onSdSwap() {
        LogUtils.i(TAG, "onSdSwap...");
        Message.obtain(this.mHandler, 3).sendToTarget();
    }

    private void doOnSdSwap() {
        this.mMountPointManager.init(getApplicationContext());
        backToRootPath();
    }

    private void doOnEjected(String unMountPoint) {
        if ((this.mCurrentPath + MountPointManager.SEPARATOR).startsWith(unMountPoint + MountPointManager.SEPARATOR) || this.mMountPointManager.isRootPath(this.mCurrentPath) || this.mMountPointManager.isPrimaryVolume(unMountPoint)) {
            LogUtils.d(TAG, "onEjected,Current Path = " + this.mCurrentPath);
            if (this.mService != null && this.mService.isBusy(getClass().getName())) {
                this.mService.cancel(getClass().getName());
            }
        }
    }

    private void doOnUnMounted(String unMountPoint) {
        if (this.mFileInfoManager != null) {
            int pasteCnt = this.mFileInfoManager.getPasteCount();
            LogUtils.i(TAG, "doOnUnmounted,unMountPoint: " + unMountPoint + ",pasteCnt = " + pasteCnt);
            if (pasteCnt > 0 && ((FileInfo) this.mFileInfoManager.getPasteList().get(0)).getFileAbsolutePath().startsWith(unMountPoint + MountPointManager.SEPARATOR)) {
                LogUtils.i(TAG, "doOnUnmounted,clear paste list. ");
                this.mFileInfoManager.clearPasteList();
                invalidateOptionsMenu();
            }
        }
        if ((this.mCurrentPath + MountPointManager.SEPARATOR).startsWith(unMountPoint + MountPointManager.SEPARATOR) || this.mMountPointManager.isRootPath(this.mCurrentPath)) {
            LogUtils.d(TAG, "onUnmounted,Current Path = " + this.mCurrentPath);
            if (this.mService != null && this.mService.isBusy(getClass().getName())) {
                this.mService.cancel(getClass().getName());
            }
            showToastForUnmount(unMountPoint);
            DialogFragment listFramgent = (DialogFragment) getFragmentManager().findFragmentByTag(ListListener.LIST_DIALOG_TAG);
            if (listFramgent != null) {
                LogUtils.d(TAG, "onUnmounted,listFramgent dismiss. ");
                listFramgent.dismissAllowingStateLoss();
            }
            EditTextDialogFragment createFolderDialogFragment = (EditTextDialogFragment) getFragmentManager().findFragmentByTag(CREATE_FOLDER_DIALOG_TAG);
            if (createFolderDialogFragment != null) {
                LogUtils.d(TAG, "onUnmounted,createFolderDialogFragment dismiss. ");
                createFolderDialogFragment.dismissAllowingStateLoss();
            }
            backToRootPath();
        }
    }

    public void onCreate(Bundle savedInstanceState) {
        PDebug.Start("AbsBaseActivity - onCreate");
        super.onCreate(savedInstanceState);
        IconManager.updateCustomDrableMap(this);
        this.mSavedInstanceState = savedInstanceState;
        LogUtils.d(TAG, "onCreate");
        this.mToastHelper = new ToastHelper(this);
        this.mMountPointManager = MountPointManager.getInstance();
        this.mMountPointManager.init(getApplicationContext());
        DrmManager.getInstance().init(getApplicationContext());
        PDebug.Start("AbsBaseActivity - bindService");
        bindService(new Intent(getApplicationContext(), FileManagerService.class), this.mServiceConnection, 1);
        PDebug.End("AbsBaseActivity - bindService");
        setMainContentView();
        this.mNavigationBar = (SlowHorizontalScrollView) findViewById(R.id.navigation_bar);
        if (this.mNavigationBar != null) {
            this.mNavigationBar.setVerticalScrollBarEnabled(false);
            this.mNavigationBar.setHorizontalScrollBarEnabled(false);
            this.mTabManager = new TabManager();
        }
        this.mListView = (ListView) findViewById(R.id.list_view);
        if (this.mListView != null) {
            this.mListView.setEmptyView(findViewById(R.id.empty_view));
            this.mListView.setOnItemClickListener(this);
            this.mListView.setVerticalScrollBarEnabled(true);
        }
        PDebug.End("AbsBaseActivity - onCreate");
    }

    private void reloadContent() {
        LogUtils.d(TAG, "reloadContent");
        if (this.mService != null && !this.mService.isBusy(getClass().getName())) {
            if (this.mFileInfoManager != null && this.mFileInfoManager.isPathModified(this.mCurrentPath)) {
                showDirectoryContent(this.mCurrentPath);
            } else if (this.mFileInfoManager != null && this.mAdapter != null) {
                this.mAdapter.notifyDataSetChanged();
            }
        }
    }

    protected void onResume() {
        PDebug.Start("AbsBaseActivity - onResume");
        super.onResume();
        LogUtils.d(TAG, "onResume");
        DrmManager.getInstance().init(getApplicationContext());
        IconManager.updateCustomDrableMap(this);
        DrmManager.getInstance().init(getApplicationContext());
        reloadContent();
        PDebug.End("AbsBaseActivity - onResume");
    }

    protected void onPause() {
        LogUtils.d(TAG, "onPause");
        if (this.mServiceBinded) {
            unbindService(this.mServiceConnection);
            this.mServiceBinded = false;
        }
        super.onPause();
    }

    protected void showCreateFolderDialog() {
        LogUtils.d(TAG, "showCreateFolderDialog");
        if (this.mIsAlertDialogShowing) {
            LogUtils.d(TAG, "Another Dialog showing, return!~~");
        } else if (isResumed()) {
            this.mIsAlertDialogShowing = true;
            EditDialogFragmentBuilder builder = new EditDialogFragmentBuilder();
            builder.setDefault("", 0).setDoneTitle(R.string.ok).setCancelTitle(R.string.cancel).setTitle(R.string.new_folder);
            EditTextDialogFragment createFolderDialogFragment = builder.create();
            createFolderDialogFragment.setOnEditTextDoneListener(new CreateFolderListener());
            createFolderDialogFragment.setOnDialogDismissListener(this);
            try {
                createFolderDialogFragment.show(getFragmentManager(), CREATE_FOLDER_DIALOG_TAG);
                LogUtils.d(TAG, "executing pending transactions result: " + getFragmentManager().executePendingTransactions());
            } catch (IllegalStateException e) {
                LogUtils.d(TAG, "call show dialog after onSaveInstanceState " + e);
                if (createFolderDialogFragment != null) {
                    createFolderDialogFragment.dismissAllowingStateLoss();
                }
            }
        }
    }

    protected void onDestroy() {
        LogUtils.d(TAG, "onDestroy");
        if (this.mService != null) {
            if (this.mServiceBinded) {
                unbindService(this.mServiceConnection);
                this.mServiceBinded = false;
            }
            this.mMountReceiver.unregisterMountListener(this);
            unregisterReceiver(this.mMountReceiver);
        } else {
            LogUtils.w(TAG, "#onDestroy(),the Service hasn't connected yet.");
        }
        DrmManager.getInstance().release();
        super.onDestroy();
    }

    private void backToRootPath() {
        LogUtils.d(TAG, "backToRootPath...");
        if (this.mMountPointManager != null && this.mMountPointManager.isRootPath(this.mCurrentPath)) {
            showDirectoryContent(this.mCurrentPath);
        } else if (this.mTabManager != null) {
            this.mTabManager.updateNavigationBar(0);
        }
        clearNavigationList();
    }

    private void showToastForUnmount(String path) {
        LogUtils.d(TAG, "showToastForUnmount,path = " + path);
        if (isResumed()) {
            LogUtils.d(TAG, "showToastForUnmount,unMountPointDescription:" + MountPointManager.getInstance().getDescriptionPath(path));
            this.mToastHelper.showToast(getString(R.string.unmounted, new Object[]{unMountPointDescription}));
        }
    }

    protected void addToNavigationList(String path, FileInfo selectedFileInfo, int top) {
        this.mFileInfoManager.addToNavigationList(new NavigationRecord(path, selectedFileInfo, top));
    }

    protected void clearNavigationList() {
        this.mFileInfoManager.clearNavigationList();
    }

    public void onClick(View view) {
        if (this.mService.isBusy(getClass().getName())) {
            LogUtils.d(TAG, "onClick(), service is busy.");
            return;
        }
        int id = view.getId();
        LogUtils.d(TAG, "onClick() id=" + id);
        this.mTabManager.updateNavigationBar(id);
    }

    private int restoreSelectedPosition() {
        if (this.mSelectedFileInfo == null) {
            return -1;
        }
        int curSelectedItemPosition = this.mAdapter.getPosition(this.mSelectedFileInfo);
        this.mSelectedFileInfo = null;
        return curSelectedItemPosition;
    }

    protected void showDirectoryContent(String path) {
        LogUtils.d(TAG, "showDirectoryContent,path = " + path);
        if (isFinishing()) {
            LogUtils.i(TAG, "showDirectoryContent,isFinishing: true, do not loading again");
            return;
        }
        this.mCurrentPath = path;
        if (this.mService != null) {
            this.mService.listFiles(getClass().getName(), this.mCurrentPath, new ListListener());
        }
    }

    protected void onPathChanged() {
        LogUtils.d(TAG, "onPathChanged");
        if (this.mTabManager != null) {
            this.mTabManager.refreshTab(this.mCurrentPath);
        }
        invalidateOptionsMenu();
    }

    public void onBackPressed() {
        LogUtils.d(TAG, "onBackPressed");
        if (this.mService == null || !this.mService.isBusy(getClass().getName())) {
            if (!(this.mCurrentPath == null || this.mMountPointManager.isRootPath(this.mCurrentPath))) {
                NavigationRecord navRecord = this.mFileInfoManager.getPrevNavigation();
                if (navRecord != null) {
                    String prevPath = navRecord.getRecordPath();
                    this.mSelectedFileInfo = navRecord.getSelectedFile();
                    this.mTop = navRecord.getTop();
                    if (prevPath != null) {
                        this.mTabManager.showPrevNavigationView(prevPath);
                        LogUtils.d(TAG, "sonBackPressed,prevPath = " + prevPath);
                        return;
                    }
                }
            }
            super.onBackPressed();
            return;
        }
        LogUtils.i(TAG, "onBackPressed, service is busy. ");
    }

    protected void serviceConnected() {
        LogUtils.i(TAG, "serviceConnected");
        this.mFileInfoManager = this.mService.initFileInfoManager(getClass().getName());
        this.mService.setListType(getPrefsShowHidenFile() ? 2 : 0, getClass().getName());
        this.mAdapter = new FileInfoAdapter(this, this.mService, this.mFileInfoManager);
        if (this.mListView != null) {
            this.mListView.setAdapter(this.mAdapter);
            if (this.mSavedInstanceState == null) {
                this.mCurrentPath = initCurrentFileInfo();
                if (this.mCurrentPath != null) {
                    showDirectoryContent(this.mCurrentPath);
                }
            } else {
                String savePath = this.mSavedInstanceState.getString(SAVED_PATH_KEY);
                if (savePath == null || !this.mMountPointManager.isMounted(this.mMountPointManager.getRealMountPointPath(savePath))) {
                    this.mCurrentPath = initCurrentFileInfo();
                } else {
                    this.mCurrentPath = savePath;
                }
                if (this.mCurrentPath != null) {
                    this.mTabManager.refreshTab(this.mCurrentPath);
                    reloadContent();
                }
                restoreDialog();
            }
            this.mAdapter.notifyDataSetChanged();
        }
        this.mMountReceiver = MountReceiver.registerMountReceiver(this);
        this.mMountReceiver.registerMountListener(this);
    }

    protected void restoreDialog() {
        DialogFragment listFramgent = (DialogFragment) getFragmentManager().findFragmentByTag(ListListener.LIST_DIALOG_TAG);
        if (listFramgent != null) {
            LogUtils.i(TAG, "listFramgent != null");
            if (this.mService.isBusy(getClass().getName())) {
                LogUtils.i(TAG, "list reconnected mService");
                this.mService.reconnected(getClass().getName(), new ListListener());
            } else {
                LogUtils.i(TAG, "the list is complete dismissAllowingStateLoss");
                listFramgent.dismissAllowingStateLoss();
            }
        }
        EditTextDialogFragment createFolderDialogFragment = (EditTextDialogFragment) getFragmentManager().findFragmentByTag(CREATE_FOLDER_DIALOG_TAG);
        if (createFolderDialogFragment != null) {
            createFolderDialogFragment.setOnEditTextDoneListener(new CreateFolderListener());
        }
    }

    protected void onSaveInstanceState(Bundle outState) {
        if (this.mCurrentPath != null) {
            outState.putString(SAVED_PATH_KEY, this.mCurrentPath);
        }
        super.onSaveInstanceState(outState);
    }

    protected boolean changePrefsShowHidenFile() {
        boolean z = false;
        boolean hide = getPrefsShowHidenFile();
        Editor editor = getPreferences(0).edit();
        String str = PREF_SHOW_HIDEN_FILE;
        if (!hide) {
            z = true;
        }
        editor.putBoolean(str, z);
        editor.commit();
        return hide;
    }

    protected boolean getPrefsShowHidenFile() {
        return getPreferences(0).getBoolean(PREF_SHOW_HIDEN_FILE, false);
    }

    protected int getViewDirection() {
        return this.mNavigationBar.getParent().getParent().getLayoutDirection();
    }
}
