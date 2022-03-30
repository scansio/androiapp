package com.androi.development;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class RunningProcesses extends ListActivity {
    private AppListAdapter mAdapter;
    PackageManager mPm;
    private final Comparator sDisplayNameComparator = new Comparator() {
        private final Collator collator = Collator.getInstance();

        public final int compare(Object a, Object b) {
            return this.collator.compare(((ListItem) a).procInfo.processName, ((ListItem) b).procInfo.processName);
        }
    };

    private final class AppListAdapter extends BaseAdapter {
        protected final Context mContext;
        protected final LayoutInflater mInflater;
        protected List<ListItem> mList;

        public AppListAdapter(Context context) {
            this.mContext = context;
            this.mInflater = (LayoutInflater) context.getSystemService("layout_inflater");
            for (RunningAppProcessInfo app : ((ActivityManager) RunningProcesses.this.getSystemService("activity")).getRunningAppProcesses()) {
                if (this.mList == null) {
                    this.mList = new ArrayList();
                }
                this.mList.add(new ListItem(app));
            }
            if (this.mList != null) {
                Collections.sort(this.mList, RunningProcesses.this.sDisplayNameComparator);
            }
        }

        public ListItem appForPosition(int position) {
            if (this.mList == null) {
                return null;
            }
            return (ListItem) this.mList.get(position);
        }

        public int getCount() {
            return this.mList != null ? this.mList.size() : 0;
        }

        public Object getItem(int position) {
            return Integer.valueOf(position);
        }

        public long getItemId(int position) {
            return (long) position;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            View view;
            if (convertView == null) {
                view = this.mInflater.inflate(17367043, parent, false);
            } else {
                view = convertView;
            }
            bindView(view, (ListItem) this.mList.get(position));
            return view;
        }

        private final void bindView(View view, ListItem info) {
            ((TextView) view.findViewById(16908308)).setText(info != null ? info.procInfo.processName : "(none)");
        }
    }

    private class ListItem {
        RunningAppProcessInfo procInfo;

        public ListItem(RunningAppProcessInfo pInfo) {
            this.procInfo = pInfo;
        }
    }

    protected void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        this.mPm = getPackageManager();
        this.mAdapter = new AppListAdapter(this);
        if (this.mAdapter.getCount() <= 0) {
            finish();
        } else {
            setListAdapter(this.mAdapter);
        }
    }

    protected void onResume() {
        super.onResume();
    }

    protected void onStop() {
        super.onStop();
    }

    protected void onListItemClick(ListView l, View v, int position, long id) {
        ListItem app = this.mAdapter.appForPosition(position);
        Intent intent = new Intent("android.intent.action.VIEW");
        intent.setClass(this, ProcessInfo.class);
        intent.putExtra("processName", app.procInfo.processName);
        intent.putExtra("packageList", app.procInfo.pkgList);
        startActivity(intent);
    }
}
