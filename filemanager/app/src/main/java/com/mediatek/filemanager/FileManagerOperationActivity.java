package com.mediatek.filemanager;

import android.app.ActionBar;
import android.content.ActivityNotFoundException;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.content.res.Configuration;
import android.content.res.Resources.NotFoundException;
import android.database.Cursor;
import android.media.MediaFile;
import android.media.MediaFile.MediaFileType;
import android.media.RingtoneManager;
import android.net.Uri;
import android.nfc.NfcAdapter;
import android.nfc.NfcAdapter.CreateBeamUrisCallback;
import android.nfc.NfcEvent;
import android.os.Bundle;
import android.os.Parcelable;
import android.os.SystemProperties;
import android.provider.MediaStore.Audio.Media;
import android.provider.Settings.System;
import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.ActionMode;
import android.view.ActionMode.Callback;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.PopupMenu;
import android.widget.PopupMenu.OnMenuItemClickListener;
import android.widget.TextView;
import android.widget.Toast;
import com.mediatek.filemanager.AlertDialogFragment.AlertDialogFragmentBuilder;
import com.mediatek.filemanager.AlertDialogFragment.ChoiceDialogFragment;
import com.mediatek.filemanager.AlertDialogFragment.ChoiceDialogFragmentBuilder;
import com.mediatek.filemanager.AlertDialogFragment.EditDialogFragmentBuilder;
import com.mediatek.filemanager.AlertDialogFragment.EditTextDialogFragment;
import com.mediatek.filemanager.AlertDialogFragment.EditTextDialogFragment.EditTextDoneListener;
import com.mediatek.filemanager.service.FileManagerService.OperationEventListener;
import com.mediatek.filemanager.service.ProgressInfo;
import com.mediatek.filemanager.utils.DrmManager;
import com.mediatek.filemanager.utils.FileUtils;
import com.mediatek.filemanager.utils.LogUtils;
import com.mediatek.filemanager.utils.OptionsUtils;
import com.mediatek.filemanager.utils.PDebug;
import java.io.File;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class FileManagerOperationActivity extends AbsBaseActivity implements OnItemLongClickListener, CreateBeamUrisCallback {
    private static final String ACTION_HOTKNOT_RECEIVED = "com.mediatek.hotknot.action.FILEMANAGER_FILE_RECEIVED";
    private static final String CURRENT_POSTION_KEY = "current_postion_key";
    private static final String CURRENT_TOP_KEY = "current_top_key";
    private static final String CURRENT_VIEW_MODE_KEY = "view_mode_key";
    public static final String DELETE_DIALOG_TAG = "delete_dialog_fragment_tag";
    private static final String DETAIL_INFO_KEY = "detail_info_key";
    public static final String FORBIDDEN_DIALOG_TAG = "forbidden_dialog_fragment_tag";
    private static final String HOTKNOT_INTENT_EXTRA = "?intent=com.mediatek.hotknot.action.FILEMANAGER_FILE_RECEIVED&isMimeType=no";
    public static final String INTENT_EXTRA_SELECT_PATH = "select_path";
    private static final int MAX_SHARE_FILES_COUNT = 2000;
    private static final String NEW_FILE_PATH_KEY = "new_file_path_key";
    private static final String PREF_SORT_BY = "pref_sort_by";
    public static final String RENAME_DIALOG_TAG = "rename_dialog_fragment_tag";
    public static final String RENAME_EXTENSION_DIALOG_TAG = "rename_extension_dialog_fragment_tag";
    private static final String SAVED_SELECTED_PATH_KEY = "saved_selected_path";
    private static final String SAVED_SELECTED_TOP_KEY = "saved_selected_top_key";
    private static final String STRING_HOTKNOT = "HotKnot";
    private static final String TAG = "FileManagerOperationActivity";
    private static final String TXT_MIME_TYPE = "text/plain";
    private final int AS_ALARM = 3;
    private final int AS_NONE = 0;
    private final int AS_NOTIFICATION = 2;
    private final int AS_RINGTONE = 1;
    private ActionMode mActionMode;
    public final ActionModeCallBack mActionModeCallBack = new ActionModeCallBack();
    private boolean mIsConfigChanged = false;
    private View mNavigationView = null;
    private NfcAdapter mNfcAdapter;
    private int mOrientationConfig;
    private FileInfo mTxtFile = null;
    private File musicRingtoneFile = null;
    private long musicRingtoneId = -1;

    protected class ActionModeCallBack implements Callback, OnMenuItemClickListener {
        private PopupMenu mSelectPopupMenu = null;
        private boolean mSelectedAll = true;
        private Button mTextSelect = null;

        protected ActionModeCallBack() {
        }

        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            View customView = ((LayoutInflater) FileManagerOperationActivity.this.getSystemService("layout_inflater")).inflate(R.layout.actionbar_edit, null);
            mode.setCustomView(customView);
            this.mTextSelect = (Button) customView.findViewById(R.id.text_select);
            this.mTextSelect.setOnClickListener(new OnClickListener() {
                public void onClick(View v) {
                    if (ActionModeCallBack.this.mSelectPopupMenu == null) {
                        ActionModeCallBack.this.mSelectPopupMenu = ActionModeCallBack.this.createSelectPopupMenu(ActionModeCallBack.this.mTextSelect);
                        return;
                    }
                    ActionModeCallBack.this.updateSelectPopupMenu();
                    ActionModeCallBack.this.mSelectPopupMenu.show();
                }
            });
            mode.getMenuInflater().inflate(R.menu.edit_view_menu, menu);
            return true;
        }

        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            FileInfo fileInfo;
            int selectedCount = FileManagerOperationActivity.this.mAdapter.getCheckedItemsCount();
            MenuItem cutItem = menu.findItem(R.id.cut);
            if (cutItem != null && OptionsUtils.isMtkHotKnotSupported()) {
                cutItem.setShowAsAction(0);
            } else if (!(cutItem == null || OptionsUtils.isMtkHotKnotSupported())) {
                cutItem.setShowAsAction(2);
            }
            if (!OptionsUtils.isMtkHotKnotSupported()) {
                menu.removeItem(R.id.hotknot_share);
            }
            if (selectedCount == 0) {
                menu.findItem(R.id.copy).setEnabled(false);
                menu.findItem(R.id.delete).setEnabled(false);
                menu.findItem(R.id.cut).setEnabled(false);
            } else {
                menu.findItem(R.id.copy).setEnabled(true);
                menu.findItem(R.id.delete).setEnabled(true);
                menu.findItem(R.id.cut).setEnabled(true);
            }
            if (selectedCount == 0 || selectedCount > FileManagerOperationActivity.MAX_SHARE_FILES_COUNT) {
                menu.findItem(R.id.share).setEnabled(false);
                if (OptionsUtils.isMtkHotKnotSupported()) {
                    menu.findItem(R.id.hotknot_share).setEnabled(false);
                }
            } else if (selectedCount == 1) {
                fileInfo = (FileInfo) FileManagerOperationActivity.this.mAdapter.getCheckedFileInfoItemsList().get(0);
                if ((fileInfo.isDrmFile() && DrmManager.getInstance().isRightsStatus(fileInfo.getFileAbsolutePath())) || fileInfo.isDirectory()) {
                    menu.findItem(R.id.share).setEnabled(false);
                    if (OptionsUtils.isMtkHotKnotSupported()) {
                        menu.findItem(R.id.hotknot_share).setEnabled(false);
                    }
                } else {
                    menu.findItem(R.id.share).setEnabled(true);
                    if (OptionsUtils.isMtkHotKnotSupported()) {
                        if (fileInfo.isDirectory()) {
                            menu.findItem(R.id.hotknot_share).setEnabled(false);
                        } else {
                            menu.findItem(R.id.hotknot_share).setEnabled(true);
                        }
                    }
                }
            } else {
                menu.findItem(R.id.share).setEnabled(true);
                if (OptionsUtils.isMtkHotKnotSupported()) {
                    menu.findItem(R.id.hotknot_share).setEnabled(true);
                }
                for (FileInfo info : FileManagerOperationActivity.this.mAdapter.getCheckedFileInfoItemsList()) {
                    if (info.getFile().isDirectory()) {
                        menu.findItem(R.id.share).setEnabled(false);
                        if (OptionsUtils.isMtkHotKnotSupported()) {
                            menu.findItem(R.id.hotknot_share).setEnabled(false);
                        }
                    }
                }
            }
            menu.removeItem(R.id.protection_info);
            menu.removeItem(R.id.addRingtone);
            menu.removeItem(R.id.cancelRingtone);
            menu.removeItem(R.id.addNotificationSound);
            menu.removeItem(R.id.cancelNotificationSound);
            menu.removeItem(R.id.addAlarmSound);
            menu.removeItem(R.id.cancelAlarmSound);
            if (selectedCount == 0) {
                menu.findItem(R.id.rename).setEnabled(false);
                menu.findItem(R.id.details).setEnabled(false);
            } else if (selectedCount == 1) {
                menu.findItem(R.id.details).setEnabled(true);
                if (((FileInfo) FileManagerOperationActivity.this.mAdapter.getCheckedFileInfoItemsList().get(0)).getFile().canWrite()) {
                    menu.findItem(R.id.rename).setEnabled(true);
                }
                fileInfo = (FileInfo) FileManagerOperationActivity.this.mAdapter.getCheckedFileInfoItemsList().get(0);
                if (fileInfo.isDrmFile()) {
                    String path = fileInfo.getFileAbsolutePath();
                    if (DrmManager.getInstance().checkDrmObjectType(path)) {
                        String mimeType = DrmManager.getInstance().getOriginalMimeType(path);
                        if (!(mimeType == null || mimeType.trim().length() == 0)) {
                            menu.add(0, R.id.protection_info, 0, 33882210);
                        }
                    }
                }
                if (fileInfo != null && isAudioFileType(fileInfo.getFileAbsolutePath())) {
                    FileManagerOperationActivity.this.musicRingtoneFile = ((FileInfo) FileManagerOperationActivity.this.mAdapter.getCheckedFileInfoItemsList().get(0)).getFile();
                    FileManagerOperationActivity.this.getMusicID(FileManagerOperationActivity.this.musicRingtoneFile);
                    if (FileManagerOperationActivity.this.isAlreadyAsRingtone(FileManagerOperationActivity.this.musicRingtoneFile, 1)) {
                        menu.removeItem(R.id.addRingtone);
                        menu.add(0, R.id.cancelRingtone, 0, R.string.cancel_ringtone);
                    } else if (FileManagerOperationActivity.this.musicRingtoneFile != null) {
                        menu.removeItem(R.id.cancelRingtone);
                        menu.add(0, R.id.addRingtone, 0, R.string.add_ringtone);
                    }
                    if (FileManagerOperationActivity.this.isAlreadyAsRingtone(FileManagerOperationActivity.this.musicRingtoneFile, 2)) {
                        menu.removeItem(R.id.addNotificationSound);
                        menu.add(0, R.id.cancelNotificationSound, 0, R.string.cancel_notification_sound);
                    } else if (FileManagerOperationActivity.this.musicRingtoneFile != null) {
                        menu.removeItem(R.id.cancelNotificationSound);
                        menu.add(0, R.id.addNotificationSound, 0, R.string.add_notification_sound);
                    }
                    if (FileManagerOperationActivity.this.isAlreadyAsRingtone(FileManagerOperationActivity.this.musicRingtoneFile, 3)) {
                        menu.removeItem(R.id.addAlarmSound);
                        menu.add(0, R.id.cancelAlarmSound, 0, R.string.cancel_alarm_sound);
                    } else if (FileManagerOperationActivity.this.musicRingtoneFile != null) {
                        menu.removeItem(R.id.cancelAlarmSound);
                        menu.add(0, R.id.addAlarmSound, 0, R.string.add_alarm_sound);
                    }
                }
            } else {
                menu.findItem(R.id.details).setEnabled(false);
                menu.findItem(R.id.rename).setEnabled(false);
            }
            return true;
        }

        public boolean isAudioFileType(String path) {
            MediaFileType type = MediaFile.getFileType(path);
            if (type != null) {
                return MediaFile.isAudioFileType(type.fileType);
            }
            return false;
        }

        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            ContentResolver resolver;
            Uri ringUri;
            ContentValues values;
            switch (item.getItemId()) {
                case R.id.share:
                    FileManagerOperationActivity.this.share();
                    break;
                case R.id.hotknot_share:
                    FileManagerOperationActivity.this.hotknotShare();
                    break;
                case R.id.copy:
                    FileManagerOperationActivity.this.mFileInfoManager.savePasteList(2, FileManagerOperationActivity.this.mAdapter.getCheckedFileInfoItemsList());
                    mode.finish();
                    break;
                case R.id.delete:
                    FileManagerOperationActivity.this.showDeleteDialog();
                    break;
                case R.id.cut:
                    FileManagerOperationActivity.this.mFileInfoManager.savePasteList(1, FileManagerOperationActivity.this.mAdapter.getCheckedFileInfoItemsList());
                    mode.finish();
                    break;
                case R.id.rename:
                    FileManagerOperationActivity.this.showRenameDialog();
                    break;
                case R.id.details:
                    FileManagerOperationActivity.this.mService.getDetailInfo(FileManagerOperationActivity.this.getClass().getName(), (FileInfo) FileManagerOperationActivity.this.mAdapter.getCheckedFileInfoItemsList().get(0), new DetailInfoListener((FileInfo) FileManagerOperationActivity.this.mAdapter.getCheckedFileInfoItemsList().get(0)));
                    break;
                case R.id.protection_info:
                    DrmManager.getInstance().showProtectionInfoDialog(FileManagerOperationActivity.this, FileManagerOperationActivity.this.mCurrentPath + MountPointManager.SEPARATOR + ((FileInfo) FileManagerOperationActivity.this.mAdapter.getCheckedFileInfoItemsList().get(0)).getFileName());
                    if (FileManagerOperationActivity.this.mActionMode != null) {
                        FileManagerOperationActivity.this.mActionMode.finish();
                        break;
                    }
                    break;
                case R.id.addRingtone:
                    Log.d("zyw", "addRingtone ");
                    if (-1 == FileManagerOperationActivity.this.musicRingtoneId) {
                        Toast.makeText(FileManagerOperationActivity.this, FileManagerOperationActivity.this.getResources().getString(R.string.add_nonsucess_ringtone_toast, new Object[]{FileManagerOperationActivity.this.musicRingtoneFile.getName()}), 0).show();
                        break;
                    }
                    resolver = FileManagerOperationActivity.this.getContentResolver();
                    ringUri = ContentUris.withAppendedId(Media.EXTERNAL_CONTENT_URI, FileManagerOperationActivity.this.musicRingtoneId);
                    try {
                        values = new ContentValues(1);
                        values.put("is_ringtone", "1");
                        resolver.update(ringUri, values, null, null);
                        Log.d("zyw", "addRingtone old uriString == " + System.getString(resolver, "ringtone"));
                        RingtoneManager.setActualDefaultRingtoneUri(FileManagerOperationActivity.this, 1, ringUri);
                        Log.d("zyw", "addRingtone new uriString == " + ringUri.toString());
                        Toast.makeText(FileManagerOperationActivity.this, FileManagerOperationActivity.this.getResources().getString(R.string.add_ringtone_toast, new Object[]{FileManagerOperationActivity.this.musicRingtoneFile.getName()}), 0).show();
                        break;
                    } catch (UnsupportedOperationException ex) {
                        ex.printStackTrace();
                        Toast.makeText(FileManagerOperationActivity.this, FileManagerOperationActivity.this.getResources().getString(R.string.add_nonsucess_ringtone_toast, new Object[]{FileManagerOperationActivity.this.musicRingtoneFile.getName()}), 0).show();
                        break;
                    }
                case R.id.cancelRingtone:
                    Log.d("zyw", "cancelRingtone ");
                    FileManagerOperationActivity.this.cancelRingtone(1);
                    Toast.makeText(FileManagerOperationActivity.this, FileManagerOperationActivity.this.getResources().getString(R.string.cancel_ringtone_toast, new Object[]{FileManagerOperationActivity.this.musicRingtoneFile.getName()}), 0).show();
                    break;
                case R.id.addNotificationSound:
                    Log.d("hyj", "addNotificationSound");
                    if (-1 == FileManagerOperationActivity.this.musicRingtoneId) {
                        Toast.makeText(FileManagerOperationActivity.this, FileManagerOperationActivity.this.getResources().getString(R.string.add_nonsucess_notification_sound_toast, new Object[]{FileManagerOperationActivity.this.musicRingtoneFile.getName()}), 0).show();
                        break;
                    }
                    resolver = FileManagerOperationActivity.this.getContentResolver();
                    ringUri = ContentUris.withAppendedId(Media.EXTERNAL_CONTENT_URI, FileManagerOperationActivity.this.musicRingtoneId);
                    try {
                        values = new ContentValues(1);
                        values.put("is_notification", "1");
                        resolver.update(ringUri, values, null, null);
                        Log.d("hyj", "addNotificationSound old uriString == " + System.getString(resolver, "notification_sound"));
                        RingtoneManager.setActualDefaultRingtoneUri(FileManagerOperationActivity.this, 2, ringUri);
                        Log.d("hyj", "addNotificationSound new uriString == " + ringUri.toString());
                        Toast.makeText(FileManagerOperationActivity.this, FileManagerOperationActivity.this.getResources().getString(R.string.add_notification_sound_toast, new Object[]{FileManagerOperationActivity.this.musicRingtoneFile.getName()}), 0).show();
                        break;
                    } catch (UnsupportedOperationException ex2) {
                        ex2.printStackTrace();
                        Toast.makeText(FileManagerOperationActivity.this, FileManagerOperationActivity.this.getResources().getString(R.string.add_nonsucess_notification_sound_toast, new Object[]{FileManagerOperationActivity.this.musicRingtoneFile.getName()}), 0).show();
                        break;
                    }
                case R.id.cancelNotificationSound:
                    Log.d("hyj", "cancelNotificationSound");
                    FileManagerOperationActivity.this.cancelRingtone(2);
                    Toast.makeText(FileManagerOperationActivity.this, FileManagerOperationActivity.this.getResources().getString(R.string.cancel_notification_sound_toast, new Object[]{FileManagerOperationActivity.this.musicRingtoneFile.getName()}), 0).show();
                    break;
                case R.id.addAlarmSound:
                    Log.d("hyj", "addAlarmSound");
                    if (-1 == FileManagerOperationActivity.this.musicRingtoneId) {
                        Toast.makeText(FileManagerOperationActivity.this, FileManagerOperationActivity.this.getResources().getString(R.string.add_nonsucess_alarm_sound_toast, new Object[]{FileManagerOperationActivity.this.musicRingtoneFile.getName()}), 0).show();
                        break;
                    }
                    resolver = FileManagerOperationActivity.this.getContentResolver();
                    ringUri = ContentUris.withAppendedId(Media.EXTERNAL_CONTENT_URI, FileManagerOperationActivity.this.musicRingtoneId);
                    try {
                        values = new ContentValues(1);
                        values.put("is_alarm", "1");
                        resolver.update(ringUri, values, null, null);
                        Log.d("hyj", "addAlarmSound old uriString == " + System.getString(resolver, "alarm_alert"));
                        RingtoneManager.setActualDefaultRingtoneUri(FileManagerOperationActivity.this, 4, ringUri);
                        Log.d("hyj", "addAlarmSound new uriString == " + ringUri.toString());
                        Toast.makeText(FileManagerOperationActivity.this, FileManagerOperationActivity.this.getResources().getString(R.string.add_alarm_sound_toast, new Object[]{FileManagerOperationActivity.this.musicRingtoneFile.getName()}), 0).show();
                        break;
                    } catch (UnsupportedOperationException ex22) {
                        ex22.printStackTrace();
                        Toast.makeText(FileManagerOperationActivity.this, FileManagerOperationActivity.this.getResources().getString(R.string.add_nonsucess_alarm_sound_toast, new Object[]{FileManagerOperationActivity.this.musicRingtoneFile.getName()}), 0).show();
                        break;
                    }
                case R.id.cancelAlarmSound:
                    Log.d("hyj", "cancelAlarmSound");
                    FileManagerOperationActivity.this.cancelRingtone(3);
                    Toast.makeText(FileManagerOperationActivity.this, FileManagerOperationActivity.this.getResources().getString(R.string.cancel_alarm_sound_toast, new Object[]{FileManagerOperationActivity.this.musicRingtoneFile.getName()}), 0).show();
                    break;
                case R.id.select:
                    if (this.mSelectedAll) {
                        FileManagerOperationActivity.this.mAdapter.setAllItemChecked(true);
                    } else {
                        FileManagerOperationActivity.this.mAdapter.setAllItemChecked(false);
                    }
                    updateActionMode();
                    FileManagerOperationActivity.this.invalidateOptionsMenu();
                    break;
                default:
                    return false;
            }
            return true;
        }

        public void onDestroyActionMode(ActionMode mode) {
            FileManagerOperationActivity.this.switchToNavigationView();
            if (FileManagerOperationActivity.this.mActionMode != null) {
                FileManagerOperationActivity.this.mActionMode = null;
            }
            if (this.mSelectPopupMenu != null) {
                this.mSelectPopupMenu.dismiss();
                this.mSelectPopupMenu = null;
            }
        }

        private PopupMenu createSelectPopupMenu(View anchorView) {
            PopupMenu popupMenu = new PopupMenu(FileManagerOperationActivity.this, anchorView);
            popupMenu.inflate(R.menu.select_popup_menu);
            popupMenu.setOnMenuItemClickListener(this);
            return popupMenu;
        }

        private void updateSelectPopupMenu() {
            if (this.mSelectPopupMenu == null) {
                this.mSelectPopupMenu = createSelectPopupMenu(this.mTextSelect);
                return;
            }
            Menu menu = this.mSelectPopupMenu.getMenu();
            int selectedCount = FileManagerOperationActivity.this.mAdapter.getCheckedItemsCount();
            if (FileManagerOperationActivity.this.mAdapter.getCount() == 0) {
                menu.findItem(R.id.select).setEnabled(false);
                return;
            }
            menu.findItem(R.id.select).setEnabled(true);
            if (FileManagerOperationActivity.this.mAdapter.getCount() != selectedCount) {
                menu.findItem(R.id.select).setTitle(R.string.select_all);
                this.mSelectedAll = true;
                return;
            }
            menu.findItem(R.id.select).setTitle(R.string.deselect_all);
            this.mSelectedAll = false;
        }

        public boolean onMenuItemClick(MenuItem menuItem) {
            switch (menuItem.getItemId()) {
                case R.id.select:
                    if (this.mSelectedAll) {
                        FileManagerOperationActivity.this.mAdapter.setAllItemChecked(true);
                    } else {
                        FileManagerOperationActivity.this.mAdapter.setAllItemChecked(false);
                    }
                    updateActionMode();
                    FileManagerOperationActivity.this.invalidateOptionsMenu();
                    return true;
                default:
                    return false;
            }
        }

        public void updateActionMode() {
            int selectedCount = FileManagerOperationActivity.this.mAdapter.getCheckedItemsCount();
            String selected = "";
            if (!Locale.getDefault().getLanguage().equals("fr") || selectedCount <= 1) {
                selected = FileManagerOperationActivity.this.getResources().getString(R.string.selected);
            } else {
                try {
                    selected = FileManagerOperationActivity.this.getResources().getString(R.string.mutil_selected);
                } catch (NotFoundException e) {
                    selected = FileManagerOperationActivity.this.getResources().getString(R.string.selected);
                }
            }
            this.mTextSelect.setText("" + selectedCount + " " + selected);
            FileManagerOperationActivity.this.mActionModeCallBack.updateSelectPopupMenu();
            if (FileManagerOperationActivity.this.mActionMode != null) {
                FileManagerOperationActivity.this.mActionMode.invalidate();
            }
        }
    }

    private class DeleteListener implements DialogInterface.OnClickListener {
        private DeleteListener() {
        }

        public void onClick(DialogInterface dialog, int id) {
            LogUtils.d(FileManagerOperationActivity.TAG, "onClick() method for alertDeleteDialog, OK button");
            if (FileManagerOperationActivity.this.mService != null) {
                FileManagerOperationActivity.this.mService.deleteFiles(FileManagerOperationActivity.this.getClass().getName(), FileManagerOperationActivity.this.mAdapter.getCheckedFileInfoItemsList(), new HeavyOperationListener(R.string.deleting));
            }
            if (FileManagerOperationActivity.this.mActionMode != null) {
                FileManagerOperationActivity.this.mActionMode.finish();
            }
            if (id == -1) {
                FileManagerOperationActivity.this.cancelRingtone(0);
            }
        }
    }

    protected class DetailInfoListener implements OperationEventListener, OnDismissListener {
        public static final String DETAIL_DIALOG_TAG = "detaildialogtag";
        private TextView mDetailsText;
        private final String mModifiedTime;
        private final String mName;
        private final String mPermission;
        private String mSize;
        private final StringBuilder mStringBuilder = new StringBuilder();

        public DetailInfoListener(FileInfo fileInfo) {
            this.mStringBuilder.setLength(0);
            this.mName = this.mStringBuilder.append(FileManagerOperationActivity.this.getString(R.string.name)).append(": ").append(fileInfo.getFileName()).append("\n").toString();
            this.mStringBuilder.setLength(0);
            this.mSize = this.mStringBuilder.append(FileManagerOperationActivity.this.getString(R.string.size)).append(": ").append(FileUtils.sizeToString(FileManagerOperationActivity.this, 0)).append(" \n").toString();
            long time = fileInfo.getFileLastModifiedTime();
            this.mStringBuilder.setLength(0);
            this.mModifiedTime = this.mStringBuilder.append(FileManagerOperationActivity.this.getString(R.string.modified_time)).append(": ").append(DateFormat.getDateInstance().format(new Date(time))).append("\n").toString();
            this.mStringBuilder.setLength(0);
            this.mPermission = getPermission(fileInfo.getFile());
        }

        private void appendPermission(boolean hasPermission, int title) {
            this.mStringBuilder.append(FileManagerOperationActivity.this.getString(title) + ": ");
            if (hasPermission) {
                this.mStringBuilder.append(FileManagerOperationActivity.this.getString(R.string.yes));
            } else {
                this.mStringBuilder.append(FileManagerOperationActivity.this.getString(R.string.no));
            }
        }

        private String getPermission(File file) {
            appendPermission(file.canRead(), R.string.readable);
            this.mStringBuilder.append("\n");
            appendPermission(file.canWrite(), R.string.writable);
            this.mStringBuilder.append("\n");
            appendPermission(file.canExecute(), R.string.executable);
            return this.mStringBuilder.toString();
        }

        public void onTaskPrepare() {
            AlertDialogFragment detailFragment = new AlertDialogFragmentBuilder().setCancelTitle(R.string.ok).setLayout(R.layout.dialog_details).setTitle(R.string.details).create();
            detailFragment.setDismissListener(this);
            detailFragment.show(FileManagerOperationActivity.this.getFragmentManager(), DETAIL_DIALOG_TAG);
            LogUtils.d(FileManagerOperationActivity.TAG, "executing pending transactions result: " + FileManagerOperationActivity.this.getFragmentManager().executePendingTransactions());
            if (detailFragment.getDialog() != null) {
                this.mDetailsText = (TextView) detailFragment.getDialog().findViewById(R.id.details_text);
                this.mStringBuilder.setLength(0);
                if (this.mDetailsText != null) {
                    this.mDetailsText.setText(this.mStringBuilder.append(this.mName).append(this.mSize).append(this.mModifiedTime).append(this.mPermission).toString());
                    this.mDetailsText.setMovementMethod(ScrollingMovementMethod.getInstance());
                }
            }
        }

        public void onTaskProgress(ProgressInfo progressInfo) {
            this.mSize = FileManagerOperationActivity.this.getString(R.string.size) + ": " + FileUtils.sizeToString(FileManagerOperationActivity.this, progressInfo.getTotal()) + " \n";
            if (this.mDetailsText != null) {
                this.mStringBuilder.setLength(0);
                this.mStringBuilder.append(this.mName).append(this.mSize).append(this.mModifiedTime).append(this.mPermission);
                this.mDetailsText.setText(this.mStringBuilder.toString());
            }
        }

        public void onTaskResult(int result) {
            LogUtils.d(FileManagerOperationActivity.TAG, "DetailInfoListener onTaskResult.");
            try {
                FileManagerOperationActivity.this.getFragmentManager().findFragmentByTag(DETAIL_DIALOG_TAG).getArguments().putString(FileManagerOperationActivity.DETAIL_INFO_KEY, this.mStringBuilder.toString());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        public void onDismiss(DialogInterface dialog) {
            if (FileManagerOperationActivity.this.mService != null) {
                LogUtils.d(getClass().getName(), "onDismiss");
                FileManagerOperationActivity.this.mService.cancel(FileManagerOperationActivity.this.getClass().getName());
            }
        }
    }

    protected class HeavyOperationListener implements OperationEventListener, OnClickListener {
        public static final String HEAVY_DIALOG_TAG = "HeavyDialogFragment";
        private boolean mOperationToast = false;
        private boolean mPermissionToast = false;
        int mTitle = R.string.deleting;

        public HeavyOperationListener(int titleID) {
            this.mTitle = titleID;
        }

        public void onTaskPrepare() {
            ProgressDialogFragment heavyDialogFragment = ProgressDialogFragment.newInstance(1, this.mTitle, R.string.wait, R.string.cancel);
            heavyDialogFragment.setCancelListener(this);
            heavyDialogFragment.setViewDirection(FileManagerOperationActivity.this.getViewDirection());
            heavyDialogFragment.show(FileManagerOperationActivity.this.getFragmentManager(), HEAVY_DIALOG_TAG);
            LogUtils.d(FileManagerOperationActivity.TAG, "executing pending transactions result: " + FileManagerOperationActivity.this.getFragmentManager().executePendingTransactions());
        }

        public void onTaskProgress(ProgressInfo progressInfo) {
            if (progressInfo.isFailInfo()) {
                switch (progressInfo.getErrorCode()) {
                    case OperationEventListener.ERROR_CODE_DELETE_NO_PERMISSION /*-15*/:
                        if (!this.mPermissionToast) {
                            FileManagerOperationActivity.this.mToastHelper.showToast((int) R.string.delete_deny);
                            this.mPermissionToast = true;
                            return;
                        }
                        return;
                    case OperationEventListener.ERROR_CODE_PASTE_UNSUCCESS /*-14*/:
                        if (!this.mOperationToast) {
                            FileManagerOperationActivity.this.mToastHelper.showToast((int) R.string.some_paste_fail);
                            this.mOperationToast = true;
                            return;
                        }
                        return;
                    case OperationEventListener.ERROR_CODE_DELETE_UNSUCCESS /*-13*/:
                        if (!this.mOperationToast) {
                            FileManagerOperationActivity.this.mToastHelper.showToast((int) R.string.some_delete_fail);
                            this.mOperationToast = true;
                            return;
                        }
                        return;
                    case OperationEventListener.ERROR_CODE_COPY_NO_PERMISSION /*-10*/:
                        if (!this.mPermissionToast) {
                            FileManagerOperationActivity.this.mToastHelper.showToast((int) R.string.copy_deny);
                            this.mPermissionToast = true;
                            return;
                        }
                        return;
                    default:
                        if (!this.mPermissionToast) {
                            FileManagerOperationActivity.this.mToastHelper.showToast((int) R.string.operation_fail);
                            this.mPermissionToast = true;
                            return;
                        }
                        return;
                }
            }
            ProgressDialogFragment heavyDialogFragment = (ProgressDialogFragment) FileManagerOperationActivity.this.getFragmentManager().findFragmentByTag(HEAVY_DIALOG_TAG);
            if (heavyDialogFragment != null) {
                heavyDialogFragment.setProgress(progressInfo);
            }
        }

        public void onTaskResult(int errorType) {
            LogUtils.d(FileManagerOperationActivity.TAG, "HeavyOperationListener,onTaskResult result = " + errorType);
            switch (errorType) {
                case OperationEventListener.ERROR_CODE_COPY_GREATER_4G_TO_FAT32 /*-16*/:
                    FileManagerOperationActivity.this.mToastHelper.showToast((int) R.string.operation_fail);
                    break;
                case OperationEventListener.ERROR_CODE_CUT_SAME_PATH /*-12*/:
                    FileManagerOperationActivity.this.mToastHelper.showToast((int) R.string.paste_same_folder);
                    break;
                case OperationEventListener.ERROR_CODE_COPY_NO_PERMISSION /*-10*/:
                    FileManagerOperationActivity.this.mToastHelper.showToast((int) R.string.copy_deny);
                    break;
                case OperationEventListener.ERROR_CODE_PASTE_TO_SUB /*-8*/:
                    FileManagerOperationActivity.this.mToastHelper.showToast((int) R.string.paste_sub_folder);
                    break;
                case OperationEventListener.ERROR_CODE_DELETE_FAILS /*-6*/:
                    FileManagerOperationActivity.this.mToastHelper.showToast((int) R.string.delete_fail);
                    break;
                case OperationEventListener.ERROR_CODE_NOT_ENOUGH_SPACE /*-5*/:
                    FileManagerOperationActivity.this.mToastHelper.showToast((int) R.string.insufficient_memory);
                    break;
                default:
                    FileManagerOperationActivity.this.mFileInfoManager.updateFileInfoList(FileManagerOperationActivity.this.mCurrentPath, FileManagerOperationActivity.this.mSortType);
                    FileManagerOperationActivity.this.mAdapter.notifyDataSetChanged();
                    break;
            }
            ProgressDialogFragment heavyDialogFragment = (ProgressDialogFragment) FileManagerOperationActivity.this.getFragmentManager().findFragmentByTag(HEAVY_DIALOG_TAG);
            if (heavyDialogFragment != null) {
                heavyDialogFragment.dismissAllowingStateLoss();
            }
            if (FileManagerOperationActivity.this.mFileInfoManager.getPasteType() == 1) {
                FileManagerOperationActivity.this.mFileInfoManager.clearPasteList();
                FileManagerOperationActivity.this.mAdapter.notifyDataSetChanged();
            }
            FileManagerOperationActivity.this.invalidateOptionsMenu();
        }

        public void onClick(View v) {
            if (FileManagerOperationActivity.this.mService != null) {
                LogUtils.i(getClass().getName(), "onClick cancel");
                FileManagerOperationActivity.this.mService.cancel(FileManagerOperationActivity.this.getClass().getName());
            }
        }
    }

    protected class RenameDoneListener implements EditTextDoneListener {
        FileInfo mSrcfileInfo;

        public RenameDoneListener(FileInfo srcFile) {
            this.mSrcfileInfo = srcFile;
        }

        public void onClick(String text) {
            String newFilePath = FileManagerOperationActivity.this.mCurrentPath + MountPointManager.SEPARATOR + text;
            if (this.mSrcfileInfo == null) {
                LogUtils.w(FileManagerOperationActivity.TAG, "mSrcfileInfo is null.");
            } else if (FileUtils.isExtensionChange(newFilePath, this.mSrcfileInfo.getFileAbsolutePath())) {
                FileManagerOperationActivity.this.showRenameExtensionDialog(this.mSrcfileInfo, newFilePath);
            } else if (FileManagerOperationActivity.this.mService != null) {
                if (FileManagerOperationActivity.this.mActionMode != null) {
                    FileManagerOperationActivity.this.mActionMode.finish();
                }
                FileManagerOperationActivity.this.mService.rename(FileManagerOperationActivity.this.getClass().getName(), this.mSrcfileInfo, new FileInfo(newFilePath), new LightOperationListener(FileUtils.getFileName(newFilePath)));
            }
        }
    }

    private class RenameExtensionListener implements DialogInterface.OnClickListener {
        private final String mNewFilePath;
        private final FileInfo mSrcFile;

        public RenameExtensionListener(FileInfo fileInfo, String newFilePath) {
            this.mNewFilePath = newFilePath;
            this.mSrcFile = fileInfo;
        }

        public void onClick(DialogInterface dialog, int which) {
            if (FileManagerOperationActivity.this.mService != null) {
                if (FileManagerOperationActivity.this.mActionMode != null) {
                    FileManagerOperationActivity.this.mActionMode.finish();
                }
                FileManagerOperationActivity.this.mService.rename(FileManagerOperationActivity.this.getClass().getName(), this.mSrcFile, new FileInfo(this.mNewFilePath), new LightOperationListener(FileUtils.getFileName(this.mNewFilePath)));
            }
        }
    }

    private class SortClickListner implements DialogInterface.OnClickListener {
        private SortClickListner() {
        }

        public void onClick(DialogInterface dialog, int id) {
            if (id != FileManagerOperationActivity.this.mSortType) {
                FileManagerOperationActivity.this.setPrefsSortBy(id);
                dialog.dismiss();
                FileManagerOperationActivity.this.sortFileInfoList();
            }
        }
    }

    public void onEjected(String unMountPoint) {
        super.onEjected(unMountPoint);
    }

    public ActionMode getActionMode() {
        return this.mActionMode;
    }

    public void onUnMounted(String unMountPoint) {
        LogUtils.d(TAG, "onUnMounted,unMountPoint :" + unMountPoint);
        if (this.mCurrentPath.startsWith(unMountPoint) || this.mMountPointManager.isRootPath(this.mCurrentPath)) {
            if (!(this.mAdapter == null || this.mAdapter.getMode() != 1 || this.mActionMode == null)) {
                this.mActionMode.finish();
            }
            ProgressDialogFragment pf = (ProgressDialogFragment) getFragmentManager().findFragmentByTag(HeavyOperationListener.HEAVY_DIALOG_TAG);
            if (pf != null) {
                pf.dismissAllowingStateLoss();
            }
            AlertDialogFragment af = (AlertDialogFragment) getFragmentManager().findFragmentByTag(DetailInfoListener.DETAIL_DIALOG_TAG);
            if (af != null) {
                af.dismissAllowingStateLoss();
            }
            af = (AlertDialogFragment) getFragmentManager().findFragmentByTag(DELETE_DIALOG_TAG);
            if (af != null) {
                af.dismissAllowingStateLoss();
            }
            af = (AlertDialogFragment) getFragmentManager().findFragmentByTag(RENAME_EXTENSION_DIALOG_TAG);
            if (af != null) {
                af.dismissAllowingStateLoss();
            }
            af = (AlertDialogFragment) getFragmentManager().findFragmentByTag(FORBIDDEN_DIALOG_TAG);
            if (af != null) {
                af.dismissAllowingStateLoss();
            }
            ChoiceDialogFragment sortDialogFragment = (ChoiceDialogFragment) getFragmentManager().findFragmentByTag(ChoiceDialogFragment.CHOICE_DIALOG_TAG);
            if (sortDialogFragment != null) {
                sortDialogFragment.dismissAllowingStateLoss();
            }
            EditTextDialogFragment renameDialogFragment = (EditTextDialogFragment) getFragmentManager().findFragmentByTag(RENAME_DIALOG_TAG);
            if (renameDialogFragment != null) {
                renameDialogFragment.dismissAllowingStateLoss();
            }
        }
        super.onUnMounted(unMountPoint);
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        PDebug.Start("FileManagerOperationActivity -- onCreate");
        LogUtils.d(TAG, "onCreate()");
        this.mSortType = getPrefsSortBy();
        this.mOrientationConfig = getResources().getConfiguration().orientation;
        this.mNfcAdapter = NfcAdapter.getDefaultAdapter(this);
        if (this.mNfcAdapter == null) {
            LogUtils.w(TAG, "mNfcAdapter == null");
        } else if (OptionsUtils.isMtkBeamSurpported()) {
            this.mNfcAdapter.setMtkBeamPushUrisCallback(this, this);
        }
        PDebug.End("FileManagerOperationActivity -- onCreate");
    }

    protected void onResume() {
        if (this.mTxtFile != null) {
            if (this.mTxtFile.getFileLastModifiedTime() != this.mTxtFile.getNewModifiedTime()) {
                this.mTxtFile.updateFileInfo();
            }
            this.mTxtFile = null;
        }
        super.onResume();
    }

    public Uri[] createBeamUris(NfcEvent event) {
        LogUtils.d(TAG, "Call createBeamUris() in FileManagerOperationActivity.");
        if (!OptionsUtils.isMtkBeamSurpported()) {
            LogUtils.d(TAG, "MtkBeam is not surpport!");
            return null;
        } else if (!this.mAdapter.isMode(1)) {
            LogUtils.d(TAG, "current mode is not Edit Mode.");
            return null;
        } else if (this.mAdapter.getCheckedItemsCount() == 0) {
            LogUtils.d(TAG, "Edit Mode; select count == 0.");
            return null;
        } else {
            List<Uri> sendFiles = new ArrayList();
            List<FileInfo> fileInfos = this.mAdapter.getCheckedFileInfoItemsList();
            for (FileInfo fileInfo : fileInfos) {
                if (fileInfo.isDirectory()) {
                    showForbiddenDialog(R.string.folder_beam_forbidden_title, R.string.folder_beam_forbidden_message);
                    return null;
                }
            }
            for (FileInfo fileInfo2 : fileInfos) {
                if (!(fileInfo2.isDrmFile() && DrmManager.getInstance().isRightsStatus(fileInfo2.getFileAbsolutePath())) && fileInfo2.getFile().canRead()) {
                    sendFiles.add(fileInfo2.getUri());
                } else {
                    showForbiddenDialog(R.string.drm_beam_forbidden_title, R.string.drm_beam_forbidden_message);
                    return null;
                }
            }
            LogUtils.d(TAG, "The number of sending files is: " + sendFiles.size());
            Uri[] uris = new Uri[sendFiles.size()];
            sendFiles.toArray(uris);
            return uris;
        }
    }

    protected void showForbiddenDialog(int title, int message) {
        LogUtils.d(TAG, "show ForbiddenDialog...");
        if (this.mIsAlertDialogShowing) {
            LogUtils.d(TAG, "Another Dialog is exist, return!~~");
            return;
        }
        this.mIsAlertDialogShowing = true;
        AlertDialogFragment forbiddenDialogFragment = (AlertDialogFragment) getFragmentManager().findFragmentByTag(FORBIDDEN_DIALOG_TAG);
        if (forbiddenDialogFragment != null) {
            forbiddenDialogFragment.dismissAllowingStateLoss();
        }
        forbiddenDialogFragment = new AlertDialogFragmentBuilder().setTitle(title).setIcon(R.drawable.ic_dialog_alert_holo_light).setMessage(message).setCancelable(false).setCancelTitle(R.string.ok).create();
        forbiddenDialogFragment.setOnDialogDismissListener(this);
        forbiddenDialogFragment.show(getFragmentManager(), FORBIDDEN_DIALOG_TAG);
        LogUtils.d(TAG, "executing pending transactions result: " + getFragmentManager().executePendingTransactions());
    }

    protected void serviceConnected() {
        LogUtils.d(TAG, "serviceConnected...");
        super.serviceConnected();
        if (this.mSavedInstanceState != null) {
            int mode = this.mSavedInstanceState.getInt(CURRENT_VIEW_MODE_KEY, 0);
            int position = this.mSavedInstanceState.getInt(CURRENT_POSTION_KEY, 0);
            int top = this.mSavedInstanceState.getInt(CURRENT_TOP_KEY, -1);
            LogUtils.d(TAG, "serviceConnected mode=" + mode);
            restoreViewMode(mode, position, top);
        }
        this.mListView.setOnItemLongClickListener(this);
    }

    private void restoreViewMode(int mode, int position, int top) {
        if (mode == 1) {
            this.mAdapter.changeMode(mode);
            this.mActionMode = startActionMode(this.mActionModeCallBack);
            this.mActionModeCallBack.updateActionMode();
            String saveSelectedPath = this.mSavedInstanceState.getString(SAVED_SELECTED_PATH_KEY);
            if (!(saveSelectedPath == null || saveSelectedPath.equals(""))) {
                this.mSelectedFileInfo = new FileInfo(saveSelectedPath);
                this.mSelectedTop = this.mSavedInstanceState.getInt(SAVED_SELECTED_TOP_KEY);
            }
        } else {
            this.mNavigationView.setVisibility(0);
            this.mAdapter.changeMode(0);
            invalidateOptionsMenu();
        }
        this.mListView.setSelectionFromTop(position, top);
    }

    protected void restoreDialog() {
        DetailInfoListener listener;
        ProgressDialogFragment pf = (ProgressDialogFragment) getFragmentManager().findFragmentByTag(HeavyOperationListener.HEAVY_DIALOG_TAG);
        if (pf != null) {
            if (this.mService.isBusy(getClass().getName())) {
                listener = new HeavyOperationListener(-1);
                this.mService.reconnected(getClass().getName(), listener);
                pf.setCancelListener(listener);
            } else {
                pf.dismissAllowingStateLoss();
            }
        }
        String saveSelectedPath = this.mSavedInstanceState.getString(SAVED_SELECTED_PATH_KEY);
        FileInfo saveSelectedFile = null;
        if (saveSelectedPath != null) {
            saveSelectedFile = new FileInfo(saveSelectedPath);
        }
        AlertDialogFragment af = (AlertDialogFragment) getFragmentManager().findFragmentByTag(DetailInfoListener.DETAIL_DIALOG_TAG);
        if (af != null && saveSelectedFile != null && this.mService != null) {
            listener = new DetailInfoListener(saveSelectedFile);
            af.setDismissListener(listener);
            String savedDetailInfo = af.getArguments().getString(DETAIL_INFO_KEY);
            if (this.mService.isBusy(getClass().getName()) && this.mService.isDetailTask(getClass().getName())) {
                this.mService.reconnected(getClass().getName(), listener);
            } else if (savedDetailInfo != null && !savedDetailInfo.equals("")) {
                TextView mDetailsText = (TextView) af.getDialog().findViewById(R.id.details_text);
                if (mDetailsText != null) {
                    mDetailsText.setText(savedDetailInfo);
                }
            } else if (this.mService.isBusy(getClass().getName())) {
                af.dismissAllowingStateLoss();
            } else {
                af.dismissAllowingStateLoss();
                this.mService.getDetailInfo(getClass().getName(), saveSelectedFile, listener);
            }
        } else if (af != null && saveSelectedFile == null) {
            af.dismissAllowingStateLoss();
            this.mIsAlertDialogShowing = false;
        }
        af = (AlertDialogFragment) getFragmentManager().findFragmentByTag(DELETE_DIALOG_TAG);
        if (af != null) {
            af.setOnDoneListener(new DeleteListener());
        }
        af = (AlertDialogFragment) getFragmentManager().findFragmentByTag(RENAME_EXTENSION_DIALOG_TAG);
        if (af != null) {
            String newFilePath = af.getArguments().getString(NEW_FILE_PATH_KEY);
            if (!(newFilePath == null || saveSelectedFile == null)) {
                af.setOnDoneListener(new RenameExtensionListener(saveSelectedFile, newFilePath));
            }
        }
        ChoiceDialogFragment sortDialogFragment = (ChoiceDialogFragment) getFragmentManager().findFragmentByTag(ChoiceDialogFragment.CHOICE_DIALOG_TAG);
        if (sortDialogFragment != null) {
            sortDialogFragment.setItemClickListener(new SortClickListner());
        }
        EditTextDialogFragment renameDialogFragment = (EditTextDialogFragment) getFragmentManager().findFragmentByTag(RENAME_DIALOG_TAG);
        if (!(renameDialogFragment == null || saveSelectedFile == null)) {
            renameDialogFragment.setOnEditTextDoneListener(new RenameDoneListener(saveSelectedFile));
        }
        super.restoreDialog();
    }

    protected void onSaveInstanceState(Bundle outState) {
        View view;
        int top;
        int currentMode;
        super.onSaveInstanceState(outState);
        if (this.mAdapter != null && this.mAdapter.getCheckedItemsCount() == 1) {
            FileInfo selectFileInfo = (FileInfo) this.mAdapter.getCheckedFileInfoItemsList().get(0);
            if (selectFileInfo != null) {
                outState.putString(SAVED_SELECTED_PATH_KEY, selectFileInfo.getFileAbsolutePath());
                int pos = this.mAdapter.getPosition(selectFileInfo);
                LogUtils.d(TAG, "onSaveInstanceSteate selected pos: " + pos);
                view = this.mListView.getChildAt(pos);
                top = -1;
                if (view != null) {
                    top = view.getTop();
                }
                outState.putInt(SAVED_SELECTED_TOP_KEY, top);
            }
        }
        if (this.mAdapter != null) {
            currentMode = this.mAdapter.getMode();
        } else {
            currentMode = 0;
        }
        outState.putInt(CURRENT_VIEW_MODE_KEY, currentMode);
        if (this.mListView.getChildCount() > 0) {
            view = this.mListView.getChildAt(0);
            if (view == null) {
                LogUtils.d(TAG, "get child at first is null.");
                return;
            }
            int position = this.mListView.getPositionForView(view);
            top = view.getTop();
            outState.putInt(CURRENT_POSTION_KEY, position);
            outState.putInt(CURRENT_TOP_KEY, top);
        }
    }

    protected void setMainContentView() {
        setContentView(R.layout.main);
        ActionBar actionBar = getActionBar();
        if (actionBar != null) {
            View customActionBarView = ((LayoutInflater) getSystemService("layout_inflater")).inflate(R.layout.actionbar, null);
            actionBar.setDisplayOptions(16, 26);
            this.mNavigationView = customActionBarView.findViewById(R.id.bar_background);
            actionBar.setCustomView(customActionBarView);
            actionBar.setBackgroundDrawable(getResources().getDrawable(R.drawable.title_bar_bg));
            actionBar.setSplitBackgroundDrawable(getResources().getDrawable(R.drawable.bottom_bg));
        }
    }

    private void switchToNavigationView() {
        LogUtils.d(TAG, "Switch to navigation view");
        this.mNavigationView.setVisibility(0);
        this.mAdapter.changeMode(0);
        invalidateOptionsMenu();
    }

    private void switchToEditView(int position, int top) {
        LogUtils.d(TAG, "switchToEditView position and top" + position + MountPointManager.SEPARATOR + top);
        this.mAdapter.setChecked(position, true);
        this.mListView.setSelectionFromTop(position, top);
        switchToEditView();
    }

    private void switchToEditView() {
        LogUtils.d(TAG, "Switch to edit view");
        this.mAdapter.changeMode(1);
        this.mActionMode = startActionMode(this.mActionModeCallBack);
        this.mActionModeCallBack.updateActionMode();
    }

    private void hotknotShare() {
        if (this.mAdapter.isMode(1)) {
            List<FileInfo> files = this.mAdapter.getCheckedFileInfoItemsList();
            boolean forbidden = false;
            if (files.size() >= 1) {
                for (FileInfo info : files) {
                    if (info.isDrmFile() && DrmManager.getInstance().isRightsStatus(info.getFileAbsolutePath())) {
                        forbidden = true;
                        break;
                    }
                }
                if (forbidden) {
                    showForbiddenDialog(33882303, 33882304);
                    return;
                }
                Uri[] uris = new Uri[files.size()];
                String fPath = ((FileInfo) files.get(0)).getFileAbsolutePath();
                for (int i = 0; i < uris.length; i++) {
                    if (i == 0) {
                        uris[i] = Uri.parse("file://" + fPath + HOTKNOT_INTENT_EXTRA);
                    } else {
                        uris[i] = Uri.fromFile(((FileInfo) files.get(i)).getFile());
                    }
                }
                Intent sIntent = new Intent();
                sIntent.setAction("com.mediatek.hotknot.action.SHARE");
                sIntent.putExtra("com.mediatek.hotknot.extra.SHARE_URIS", uris);
                try {
                    startActivity(sIntent);
                    this.mActionMode.finish();
                    return;
                } catch (ActivityNotFoundException e) {
                    LogUtils.d(TAG, "hotknot share activity not found");
                    return;
                }
            }
            return;
        }
        LogUtils.w(TAG, "Maybe dispatch events twice, view mode error.");
    }

    private void share() {
        boolean forbidden = false;
        ArrayList<Parcelable> sendList = new ArrayList();
        if (this.mAdapter.isMode(1)) {
            List<FileInfo> files = this.mAdapter.getCheckedFileInfoItemsList();
            Intent intent;
            if (files.size() > 1) {
                LogUtils.d(TAG, "Share multiple files");
                for (FileInfo info : files) {
                    if (info.isDrmFile() && DrmManager.getInstance().isRightsStatus(info.getFileAbsolutePath())) {
                        forbidden = true;
                        break;
                    }
                    sendList.add(info.getUri());
                }
                if (!forbidden) {
                    intent = new Intent();
                    intent.setAction("android.intent.action.SEND_MULTIPLE");
                    intent.setType(FileUtils.getMultipleMimeType(this.mService, this.mCurrentPath, files));
                    intent.putParcelableArrayListExtra("android.intent.extra.STREAM", sendList);
                    try {
                        startActivity(Intent.createChooser(intent, getString(R.string.send_file)));
                    } catch (ActivityNotFoundException e) {
                        LogUtils.e(TAG, "Cannot find any activity", e);
                    }
                }
            } else {
                LogUtils.d(TAG, "Share a single file");
                FileInfo fileInfo = (FileInfo) files.get(0);
                String mimeType = fileInfo.getFileMimeType(this.mService);
                if (fileInfo.isDrmFile() && DrmManager.getInstance().isRightsStatus(fileInfo.getFileAbsolutePath())) {
                    forbidden = true;
                }
                if (mimeType == null || mimeType.startsWith("unknown")) {
                    mimeType = FileInfo.MIMETYPE_UNRECOGNIZED;
                }
                if (!forbidden) {
                    intent = new Intent();
                    intent.setAction("android.intent.action.SEND");
                    intent.setType(mimeType);
                    Uri uri = Uri.fromFile(fileInfo.getFile());
                    intent.putExtra("android.intent.extra.STREAM", uri);
                    LogUtils.d(TAG, "Share Uri file: " + uri);
                    LogUtils.d(TAG, "Share file mimetype: " + mimeType);
                    try {
                        startActivity(Intent.createChooser(intent, getString(R.string.send_file)));
                    } catch (ActivityNotFoundException e2) {
                        LogUtils.e(TAG, "Cannot find any activity", e2);
                    }
                }
            }
            if (forbidden) {
                showForbiddenDialog(33882303, 33882304);
                return;
            } else if (this.mActionMode != null) {
                this.mActionMode.finish();
                return;
            } else {
                return;
            }
        }
        LogUtils.w(TAG, "Maybe dispatch events twice, view mode error.");
    }

    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
        LogUtils.d(TAG, "onItemClick, position = " + position);
        if (this.mService != null && this.mService.isBusy(getClass().getName())) {
            LogUtils.d(TAG, "onItemClick, service is busy,return. ");
        } else if (this.mAdapter.isMode(0)) {
            LogUtils.d(TAG, "onItemClick,Selected position: " + position);
            if (position >= this.mAdapter.getCount() || position < 0) {
                LogUtils.e(TAG, "onItemClick,events error,mFileInfoList.size(): " + this.mAdapter.getCount());
                return;
            }
            FileInfo selecteItemFileInfo = this.mAdapter.getItem(position);
            if (selecteItemFileInfo.isDirectory()) {
                int top = view.getTop();
                LogUtils.v(TAG, "onItemClick,fromTop = " + top);
                addToNavigationList(this.mCurrentPath, selecteItemFileInfo, top);
                showDirectoryContent(selecteItemFileInfo.getFileAbsolutePath());
                return;
            }
            boolean canOpen = true;
            String mimeType = selecteItemFileInfo.getFileMimeType(this.mService);
            if (selecteItemFileInfo.isDrmFile()) {
                mimeType = DrmManager.getInstance().getOriginalMimeType(selecteItemFileInfo.getFileAbsolutePath());
                if (TextUtils.isEmpty(mimeType)) {
                    canOpen = false;
                    this.mToastHelper.showToast((int) R.string.msg_unable_open_file);
                }
            }
            if (canOpen) {
                Intent intent = new Intent("android.intent.action.VIEW");
                Uri uri = selecteItemFileInfo.getUri();
                LogUtils.d(TAG, "onItemClick,Open uri file: " + uri);
                intent.setDataAndType(uri, mimeType);
                if (mimeType != null && mimeType.equals(TXT_MIME_TYPE)) {
                    this.mTxtFile = selecteItemFileInfo;
                }
                intent.putExtra("isFileManager", true);
                try {
                    startActivity(intent);
                } catch (ActivityNotFoundException e) {
                    this.mTxtFile = null;
                    this.mToastHelper.showToast((int) R.string.msg_unable_open_file);
                    LogUtils.w(TAG, "onItemClick,Cannot open file: " + selecteItemFileInfo.getFileAbsolutePath());
                }
            }
        } else {
            LogUtils.d(TAG, "onItemClick,edit view .");
            this.mAdapter.setChecked(position, !this.mAdapter.getItem(position).isChecked());
            this.mActionModeCallBack.updateActionMode();
            this.mAdapter.notifyDataSetChanged();
        }
    }

    public void onClick(View view) {
        if (this.mService.isBusy(getClass().getName())) {
            LogUtils.d(TAG, "onClick, service is busy,return.");
            return;
        }
        LogUtils.d(TAG, "onClick,id: " + view.getId());
        boolean isMounted = this.mMountPointManager.isRootPathMount(this.mCurrentPath);
        if (this.mAdapter.isMode(1) && isMounted) {
            this.mActionModeCallBack.updateActionMode();
            LogUtils.d(TAG, "onClick,retuen.");
            return;
        }
        super.onClick(view);
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        LogUtils.d(TAG, "onCreateOptionsMenu...");
        super.onCreateOptionsMenu(menu);
        MenuInflater inflater = getMenuInflater();
        menu.clear();
        if (this.mService == null) {
            LogUtils.i(TAG, "onCreateOptionsMenu, invalid service,return true.");
        } else {
            inflater.inflate(R.menu.navigation_view_menu, menu);
        }
        return true;
    }

    public boolean onPrepareOptionsMenu(Menu menu) {
        LogUtils.d(TAG, "onPrepareOptionsMenu...");
        if (menu == null || menu.findItem(R.id.paste) == null || menu.findItem(R.id.search) == null || menu.findItem(R.id.hide) == null || menu.findItem(R.id.create_folder) == null || menu.findItem(R.id.sort) == null || menu.findItem(R.id.change_mode) == null) {
            return false;
        }
        if (this.mCurrentPath == null || !this.mMountPointManager.isRootPath(this.mCurrentPath)) {
            if (this.mFileInfoManager == null || this.mFileInfoManager.getPasteCount() <= 0) {
                menu.findItem(R.id.paste).setVisible(false);
                menu.findItem(R.id.paste).setEnabled(false);
            } else {
                menu.findItem(R.id.paste).setVisible(true);
                menu.findItem(R.id.paste).setEnabled(true);
            }
            if (this.mCurrentPath == null || new File(this.mCurrentPath).canWrite()) {
                menu.findItem(R.id.create_folder).setEnabled(true);
            } else {
                menu.findItem(R.id.create_folder).setEnabled(false);
                menu.findItem(R.id.paste).setVisible(false);
            }
            if (this.mAdapter == null || this.mAdapter.getCount() != 0) {
                menu.findItem(R.id.search).setEnabled(true);
            } else {
                menu.findItem(R.id.search).setEnabled(false);
            }
            if (getPrefsShowHidenFile()) {
                menu.findItem(R.id.hide).setTitle(R.string.hide_file);
            } else {
                menu.findItem(R.id.hide).setTitle(R.string.show_file);
            }
            if ((this.mAdapter == null || this.mAdapter.getCount() != 0) && (this.mCurrentPath == null || !this.mMountPointManager.isRootPath(this.mCurrentPath))) {
                menu.findItem(R.id.change_mode).setEnabled(true);
            } else {
                menu.findItem(R.id.change_mode).setEnabled(false);
            }
            if (this.mActionMode == null || this.mActionModeCallBack == null) {
                return true;
            }
            this.mActionModeCallBack.updateActionMode();
            return true;
        }
        menu.findItem(R.id.create_folder).setEnabled(false);
        menu.findItem(R.id.paste).setVisible(false);
        menu.findItem(R.id.paste).setEnabled(false);
        menu.findItem(R.id.search).setEnabled(true);
        menu.findItem(R.id.change_mode).setEnabled(false);
        if (getPrefsShowHidenFile()) {
            menu.findItem(R.id.hide).setTitle(R.string.hide_file);
        } else {
            menu.findItem(R.id.hide).setTitle(R.string.show_file);
        }
        menu.findItem(R.id.sort).setEnabled(true);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        LogUtils.d(TAG, "onOptionsItemSelected: " + item.getItemId());
        if (this.mService == null || !this.mService.isBusy(getClass().getName())) {
            switch (item.getItemId()) {
                case R.id.create_folder:
                    showCreateFolderDialog();
                    break;
                case R.id.paste:
                    if (this.mService != null) {
                        this.mService.pasteFiles(getClass().getName(), this.mFileInfoManager.getPasteList(), this.mCurrentPath, this.mFileInfoManager.getPasteType(), new HeavyOperationListener(R.string.pasting));
                        break;
                    }
                    break;
                case R.id.search:
                    Intent intent = new Intent();
                    intent.setClass(this, FileManagerSearchActivity.class);
                    intent.putExtra(FileManagerSearchActivity.CURRENT_PATH, this.mCurrentPath);
                    intent.setFlags(268435456);
                    startActivity(intent);
                    break;
                case R.id.change_mode:
                    switchToEditView();
                    break;
                case R.id.hide:
                    if (this.mService != null) {
                        this.mService.setListType(changePrefsShowHidenFile() ? 0 : 2, getClass().getName());
                        this.mService.listFiles(getClass().getName(), this.mCurrentPath, new ListListener());
                        break;
                    }
                    break;
                case R.id.sort:
                    showSortDialog();
                    break;
                default:
                    return super.onOptionsItemSelected(item);
            }
            return true;
        }
        LogUtils.i(TAG, "onOptionsItemSelected,service is busy. ");
        return true;
    }

    private void sortFileInfoList() {
        LogUtils.d(TAG, "Start sortFileInfoList()");
        int selection = this.mListView.getFirstVisiblePosition();
        this.mFileInfoManager.sort(this.mSortType);
        this.mAdapter.notifyDataSetChanged();
        this.mListView.setSelection(selection);
        LogUtils.d(TAG, "End sortFileInfoList()");
    }

    private void setPrefsSortBy(int sort) {
        this.mSortType = sort;
        Editor editor = getPreferences(0).edit();
        editor.putInt(PREF_SORT_BY, sort);
        editor.commit();
    }

    private int getPrefsSortBy() {
        return getPreferences(0).getInt(PREF_SORT_BY, 0);
    }

    protected void showDeleteDialog() {
        LogUtils.d(TAG, "show DeleteDialog...");
        if (this.mIsAlertDialogShowing) {
            LogUtils.d(TAG, "Another Dialog is exist, return!~~");
            return;
        }
        int alertMsgId;
        this.mIsAlertDialogShowing = true;
        if (this.mAdapter.getCheckedItemsCount() == 1) {
            alertMsgId = R.string.alert_delete_single;
        } else {
            alertMsgId = R.string.alert_delete_multiple;
        }
        AlertDialogFragment deleteDialogFragment = new AlertDialogFragmentBuilder().setMessage(alertMsgId).setDoneTitle(R.string.ok).setCancelTitle(R.string.cancel).setIcon(R.drawable.ic_dialog_alert_holo_light).setTitle(R.string.delete).create();
        deleteDialogFragment.setOnDoneListener(new DeleteListener());
        deleteDialogFragment.setOnDialogDismissListener(this);
        deleteDialogFragment.show(getFragmentManager(), DELETE_DIALOG_TAG);
        LogUtils.d(TAG, "executing pending transactions result: " + getFragmentManager().executePendingTransactions());
    }

    protected void showRenameExtensionDialog(FileInfo srcfileInfo, String newFilePath) {
        LogUtils.d(TAG, "show RenameExtensionDialog...");
        AlertDialogFragment renameExtensionDialogFragment = new AlertDialogFragmentBuilder().setTitle(R.string.confirm_rename).setIcon(R.drawable.ic_dialog_alert_holo_light).setMessage(R.string.msg_rename_ext).setCancelTitle(R.string.cancel).setDoneTitle(R.string.ok).create();
        renameExtensionDialogFragment.getArguments().putString(NEW_FILE_PATH_KEY, newFilePath);
        renameExtensionDialogFragment.setOnDoneListener(new RenameExtensionListener(srcfileInfo, newFilePath));
        renameExtensionDialogFragment.show(getFragmentManager(), RENAME_EXTENSION_DIALOG_TAG);
        LogUtils.d(TAG, "executing pending transactions result: " + getFragmentManager().executePendingTransactions());
    }

    protected void showSortDialog() {
        LogUtils.d(TAG, "show SortDialog...");
        if (this.mIsAlertDialogShowing) {
            LogUtils.d(TAG, "Another Dialog is exist, return!~~");
            return;
        }
        this.mIsAlertDialogShowing = true;
        ChoiceDialogFragmentBuilder builder = new ChoiceDialogFragmentBuilder();
        builder.setDefault(R.array.sort_by, this.mSortType).setTitle(R.string.sort_by).setCancelTitle(R.string.cancel);
        ChoiceDialogFragment sortDialogFragment = builder.create();
        sortDialogFragment.setItemClickListener(new SortClickListner());
        sortDialogFragment.setOnDialogDismissListener(this);
        sortDialogFragment.show(getFragmentManager(), ChoiceDialogFragment.CHOICE_DIALOG_TAG);
        LogUtils.d(TAG, "executing pending transactions result: " + getFragmentManager().executePendingTransactions());
    }

    protected void showRenameDialog() {
        LogUtils.d(TAG, "show RenameDialog...");
        if (this.mIsAlertDialogShowing) {
            LogUtils.d(TAG, "Another Dialog showing, return!~~");
            return;
        }
        this.mIsAlertDialogShowing = true;
        FileInfo fileInfo = this.mAdapter.getFirstCheckedFileInfoItem();
        if (fileInfo != null) {
            String name = fileInfo.getFileName();
            String fileExtension = FileUtils.getFileExtension(name);
            int selection = name.length();
            if (!(fileInfo.isDirectory() || fileExtension == null)) {
                selection = (selection - fileExtension.length()) - 1;
            }
            EditDialogFragmentBuilder builder = new EditDialogFragmentBuilder();
            builder.setDefault(name, selection).setDoneTitle(R.string.done).setCancelTitle(R.string.cancel).setTitle(R.string.rename);
            EditTextDialogFragment renameDialogFragment = builder.create();
            renameDialogFragment.setOnEditTextDoneListener(new RenameDoneListener(fileInfo));
            renameDialogFragment.setOnDialogDismissListener(this);
            renameDialogFragment.show(getFragmentManager(), RENAME_DIALOG_TAG);
            LogUtils.d(TAG, "executing pending transactions result: " + getFragmentManager().executePendingTransactions());
        }
    }

    public void onBackPressed() {
        if (this.mAdapter == null || !this.mAdapter.isMode(1)) {
            super.onBackPressed();
        } else if (this.mActionMode != null) {
            this.mActionMode.finish();
        }
    }

    protected void onNewIntent(Intent intent) {
        String action = intent.getAction();
        String hotknotPath = null;
        if (action == null || !action.equalsIgnoreCase(ACTION_HOTKNOT_RECEIVED)) {
            String path = intent.getStringExtra(INTENT_EXTRA_SELECT_PATH);
            if (path != null && this.mService != null && !this.mService.isBusy(getClass().getName())) {
                if (!new File(path).exists()) {
                    this.mToastHelper.showToast(getString(R.string.path_not_exists, new Object[]{path}));
                    path = this.mMountPointManager.getRootPath();
                }
                addToNavigationList(this.mCurrentPath, null, -1);
                showDirectoryContent(path);
                return;
            }
            return;
        }
        Uri uri = (Uri) intent.getExtra("com.mediatek.hotknot.extra.DATA");
        if (uri != null) {
            hotknotPath = uri.getPath();
        }
        LogUtils.d(TAG, "onNewIntent: " + hotknotPath);
        if (hotknotPath != null) {
            hotknotPath = hotknotPath.substring(0, hotknotPath.lastIndexOf(STRING_HOTKNOT)) + STRING_HOTKNOT;
        }
        if (hotknotPath == null || hotknotPath.isEmpty()) {
            hotknotPath = this.mCurrentPath == null ? this.mMountPointManager.getRootPath() : this.mCurrentPath;
        }
        if (!this.mCurrentPath.equalsIgnoreCase(hotknotPath)) {
            addToNavigationList(this.mCurrentPath, null, -1);
            showDirectoryContent(hotknotPath);
        }
    }

    protected String initCurrentFileInfo() {
        String action = getIntent().getAction();
        String hotknotPath = null;
        if (action == null || !action.equalsIgnoreCase(ACTION_HOTKNOT_RECEIVED)) {
            String path = getIntent().getStringExtra(INTENT_EXTRA_SELECT_PATH);
            if (path != null) {
                if (new File(path).exists()) {
                    return path;
                }
                this.mToastHelper.showToast(getString(R.string.path_not_exists, new Object[]{path}));
            }
            return this.mMountPointManager.getRootPath();
        }
        Uri uri = (Uri) getIntent().getExtra("com.mediatek.hotknot.extra.DATA");
        if (uri != null) {
            hotknotPath = uri.getPath();
        }
        LogUtils.d(TAG, "initCurrentFileInfo: " + hotknotPath);
        if (hotknotPath != null) {
            hotknotPath = hotknotPath.substring(0, hotknotPath.lastIndexOf(STRING_HOTKNOT)) + STRING_HOTKNOT;
        }
        if (hotknotPath == null || hotknotPath.isEmpty()) {
            if (this.mCurrentPath == null) {
                hotknotPath = this.mMountPointManager.getRootPath();
            } else {
                hotknotPath = this.mCurrentPath;
            }
        }
        return hotknotPath;
    }

    public boolean onItemLongClick(AdapterView<?> adapterView, View v, int position, long id) {
        if (!this.mAdapter.isMode(0) || this.mMountPointManager.isRootPath(this.mCurrentPath) || this.mService.isBusy(getClass().getName())) {
            return false;
        }
        switchToEditView(position, v.getTop());
        return true;
    }

    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (newConfig.orientation != this.mOrientationConfig) {
            this.mIsConfigChanged = true;
            this.mOrientationConfig = newConfig.orientation;
        }
    }

    protected void onPathChanged() {
        super.onPathChanged();
        if (this.mActionMode != null && this.mActionModeCallBack != null) {
            this.mActionModeCallBack.updateActionMode();
        }
    }

    private void getMusicID(File musicFile) {
        this.musicRingtoneId = -1;
        if (musicFile != null) {
            ContentResolver resolver = getContentResolver();
            String curFilePath = musicFile.getAbsolutePath();
            Cursor cursor = resolver.query(Media.EXTERNAL_CONTENT_URI, new String[]{"_id", "_data"}, null, null, null);
            if (curFilePath != null && cursor != null && cursor.getCount() > 0) {
                cursor.moveToFirst();
                while (!cursor.getString(1).equalsIgnoreCase(curFilePath)) {
                    if (!cursor.moveToNext()) {
                        break;
                    }
                }
                this.musicRingtoneId = cursor.getLong(0);
            }
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    private boolean isAlreadyAsRingtone(File musicFile, int type) {
        if (musicFile == null) {
            return false;
        }
        if (this.musicRingtoneId == -1) {
            return false;
        }
        ContentResolver resolver = getContentResolver();
        String curFileUri = Media.EXTERNAL_CONTENT_URI + MountPointManager.SEPARATOR + this.musicRingtoneId;
        String defaultRingUri = System.getString(resolver, "ringtone");
        String defaultNotificationUri = System.getString(resolver, "notification_sound");
        String defaultAlarmUri = System.getString(resolver, "alarm_alert");
        Log.v("hyj", "get current audio id = " + this.musicRingtoneId);
        Log.v("hyj", "get current audio uri = " + curFileUri);
        Log.v("hyj", "isAlreadyAsRingtone defaultNotificationUri = " + defaultNotificationUri + ",defaultRingUri=" + defaultRingUri + ",defaultAlarmUri=" + defaultAlarmUri);
        if (type != 0 || defaultRingUri == null) {
            if (1 != type || defaultRingUri == null) {
                if (2 != type || defaultNotificationUri == null) {
                    if (3 == type && defaultAlarmUri != null && defaultAlarmUri.equalsIgnoreCase(curFileUri)) {
                        return true;
                    }
                    return false;
                } else if (defaultNotificationUri.equalsIgnoreCase(curFileUri)) {
                    return true;
                } else {
                    return false;
                }
            } else if (defaultRingUri.equalsIgnoreCase(curFileUri)) {
                return true;
            } else {
                return false;
            }
        } else if (defaultRingUri.equalsIgnoreCase(curFileUri) || defaultNotificationUri.equalsIgnoreCase(curFileUri) || defaultAlarmUri.equalsIgnoreCase(curFileUri)) {
            return true;
        } else {
            return false;
        }
    }

    private void cancelRingtone(int type) {
        ContentResolver resolver = getContentResolver();
        if (-1 != this.musicRingtoneId) {
            Uri ringUri = ContentUris.withAppendedId(Media.EXTERNAL_CONTENT_URI, this.musicRingtoneId);
            String defaultUri = null;
            if (ringUri != null) {
                try {
                    ContentValues contentValues = new ContentValues(1);
                    if (1 == type) {
                        contentValues.put("is_ringtone", "0");
                        defaultUri = System.getString(resolver, "ringtone");
                    } else if (2 == type) {
                        contentValues.put("is_notification", "0");
                        defaultUri = System.getString(resolver, "notification_sound");
                    } else if (3 == type) {
                        contentValues.put("is_alarm", "0");
                        defaultUri = System.getString(resolver, "alarm_alert");
                    } else if (type == 0) {
                        contentValues.put("is_ringtone", "0");
                        contentValues.put("is_notification", "0");
                        contentValues.put("is_alarm", "0");
                    }
                    resolver.update(ringUri, contentValues, null, null);
                } catch (UnsupportedOperationException ex) {
                    ex.printStackTrace();
                }
                if (ringUri.toString().equals(defaultUri) || defaultUri == null) {
                    String uriString;
                    Log.d("hyj", ">>>>>>>>>>>>>>>>>>>>>>>should change default ");
                    if (1 == type || type == 0) {
                        uriString = System.getString(resolver, "mtk_audioprofile_default_ringtone");
                        if (uriString != null) {
                            RingtoneManager.setActualDefaultRingtoneUri(this, 1, Uri.parse(uriString));
                        }
                        Log.d("hyj", "cancelRingtone defaultRingToneUri = " + uriString);
                    }
                    if (2 == type || type == 0) {
                        uriString = System.getString(resolver, "mtk_audioprofile_default_notification");
                        if (uriString != null) {
                            RingtoneManager.setActualDefaultRingtoneUri(this, 2, Uri.parse(uriString));
                        }
                        Log.d("hyj", "cancelRingtone defaultNotificationSoundUri = " + uriString);
                    }
                    if (3 == type || type == 0) {
                        String[] seletionArgs = new String[]{SystemProperties.get("ro.config.alarm_alert")};
                        Cursor cursor = resolver.query(Media.INTERNAL_CONTENT_URI, null, "_display_name=?", seletionArgs, null);
                        if (cursor != null && cursor.getCount() > 0) {
                            cursor.moveToFirst();
                            Uri newRingUri = ContentUris.withAppendedId(Media.INTERNAL_CONTENT_URI, cursor.getLong(0));
                            if (newRingUri != null) {
                                RingtoneManager.setActualDefaultRingtoneUri(this, 4, newRingUri);
                            }
                            Log.d("hyj", "cancelRingtone defaultAlarmSoundUri = " + newRingUri.toString());
                        }
                        if (cursor != null) {
                            cursor.close();
                        }
                    }
                }
            }
        }
    }
}
