package com.androi.development;

import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnClickListener;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.PermissionInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

public class PermissionDetails extends Activity implements OnCancelListener, OnItemClickListener {
    private AppListAdapter mAdapter;
    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    PermissionDetails.this.createAppList(msg.getData().getParcelableArrayList("AppsUsingPerm"));
                    return;
                default:
                    return;
            }
        }
    };
    private LayoutInflater mInflater;
    PackageManager mPm;

    class AppListAdapter extends BaseAdapter {
        private List<PackageInfo> mList;

        AppListAdapter(List<PackageInfo> list) {
            this.mList = list;
        }

        public int getCount() {
            return this.mList.size();
        }

        public Object getItem(int position) {
            return this.mList.get(position);
        }

        public long getItemId(int position) {
            return (long) position;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            AppViewHolder holder;
            if (convertView == null) {
                convertView = PermissionDetails.this.mInflater.inflate(R.layout.pkg_list_item, null);
                holder = new AppViewHolder();
                holder.pkgName = (TextView) convertView.findViewById(R.id.pkg_name);
                convertView.setTag(holder);
            } else {
                holder = (AppViewHolder) convertView.getTag();
            }
            holder.pkgName.setText(((PackageInfo) this.mList.get(position)).packageName);
            return convertView;
        }
    }

    static class AppViewHolder {
        TextView pkgName;

        AppViewHolder() {
        }
    }

    private void createAppList(List<PackageInfo> list) {
        Log.i("PermissionDetails", "list.size=" + list.size());
        for (PackageInfo pkg : list) {
            Log.i("PermissionDetails", "Adding pkg : " + pkg.packageName);
        }
        ListView listView = (ListView) findViewById(16908298);
        this.mAdapter = new AppListAdapter(list);
        ListView lv = (ListView) findViewById(16908298);
        lv.setOnItemClickListener(this);
        lv.setSaveEnabled(true);
        lv.setItemsCanFocus(true);
        listView.setAdapter(this.mAdapter);
    }

    private void getAppsUsingPerm(PermissionInfo pInfo) {
        List<PackageInfo> list = this.mPm.getInstalledPackages(4096);
        HashSet<PackageInfo> set = new HashSet();
        for (PackageInfo pkg : list) {
            if (pkg.requestedPermissions != null) {
                for (String perm : pkg.requestedPermissions) {
                    if (perm.equalsIgnoreCase(pInfo.name)) {
                        Log.i("PermissionDetails", "Pkg:" + pkg.packageName + " uses permission");
                        set.add(pkg);
                        break;
                    }
                }
            }
        }
        ArrayList<PackageInfo> retList = new ArrayList();
        Iterator it = set.iterator();
        while (it.hasNext()) {
            retList.add((PackageInfo) it.next());
        }
        Message msg = this.mHandler.obtainMessage(1);
        msg.getData().putParcelableArrayList("AppsUsingPerm", retList);
        this.mHandler.dispatchMessage(msg);
    }

    protected void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setContentView(R.layout.permission_details);
        String permName = getIntent().getStringExtra("permission");
        if (permName == null) {
            showDialogInner(1);
        }
        this.mPm = getPackageManager();
        this.mInflater = (LayoutInflater) getSystemService("layout_inflater");
        PermissionInfo pInfo = null;
        try {
            pInfo = this.mPm.getPermissionInfo(permName, 4096);
        } catch (NameNotFoundException e) {
            showDialogInner(1);
        }
        setTextView((int) R.id.perm_name, pInfo.name);
        setTextView((int) R.id.perm_desc, pInfo.descriptionRes);
        setTextView((int) R.id.perm_group, pInfo.group);
        setProtectionLevel(R.id.perm_protection, pInfo.protectionLevel);
        setTextView((int) R.id.perm_source, pInfo.packageName);
        ApplicationInfo appInfo = null;
        try {
            appInfo = this.mPm.getApplicationInfo(pInfo.packageName, 0);
            setTextView((int) R.id.source_uid, this.mPm.getNameForUid(appInfo.uid));
        } catch (NameNotFoundException e2) {
        }
        boolean sharedVisibility = false;
        LinearLayout sharedPanel = (LinearLayout) findViewById(R.id.shared_pkgs_panel);
        if (appInfo != null) {
            String[] sharedList = this.mPm.getPackagesForUid(appInfo.uid);
            if (sharedList != null && sharedList.length > 1) {
                sharedVisibility = true;
                TextView sharedView = (TextView) sharedPanel.findViewById(R.id.shared_pkgs);
                ((TextView) sharedPanel.findViewById(R.id.shared_pkgs_label)).setVisibility(0);
                StringBuilder buff = new StringBuilder();
                buff.append(sharedList[0]);
                for (int i = 1; i < sharedList.length; i++) {
                    buff.append(", ");
                    buff.append(sharedList[i]);
                }
                sharedView.setText(buff.toString());
            }
        }
        if (sharedVisibility) {
            sharedPanel.setVisibility(0);
        } else {
            sharedPanel.setVisibility(8);
        }
        getAppsUsingPerm(pInfo);
    }

    private void setProtectionLevel(int viewId, int protectionLevel) {
        String levelStr = "";
        if (protectionLevel == 0) {
            levelStr = "Normal";
        } else if (protectionLevel == 1) {
            levelStr = "Dangerous";
        } else if (protectionLevel == 2) {
            levelStr = "Signature";
        } else if (protectionLevel == 3) {
            levelStr = "SignatureOrSystem";
        } else {
            levelStr = "Invalid";
        }
        setTextView(viewId, levelStr);
    }

    private void setTextView(int viewId, int textId) {
        ((TextView) findViewById(viewId)).setText(textId);
    }

    private void setTextView(int viewId, String text) {
        ((TextView) findViewById(viewId)).setText(text);
    }

    public Dialog onCreateDialog(int id) {
        if (id == 1) {
            return new Builder(this).setTitle(R.string.dialog_title_error).setNeutralButton(R.string.ok, new OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    PermissionDetails.this.finish();
                }
            }).setMessage(R.string.invalid_perm_name).setOnCancelListener(this).create();
        }
        return null;
    }

    private void showDialogInner(int id) {
        removeDialog(id);
        showDialog(id);
    }

    protected void onResume() {
        super.onResume();
    }

    protected void onStop() {
        super.onStop();
    }

    public void onCancel(DialogInterface dialog) {
        finish();
    }

    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
    }
}
