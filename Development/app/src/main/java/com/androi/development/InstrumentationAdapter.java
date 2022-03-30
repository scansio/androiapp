package com.androi.development;

import android.content.ComponentName;
import android.content.Context;
import android.content.pm.InstrumentationInfo;
import android.content.pm.PackageItemInfo.DisplayNameComparator;
import android.content.pm.PackageManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import java.util.Collections;
import java.util.List;

/* compiled from: InstrumentationList */
class InstrumentationAdapter extends BaseAdapter {
    protected final Context mContext;
    protected final LayoutInflater mInflater;
    protected List<InstrumentationInfo> mList;
    private PackageManager mPM;
    protected final String mTargetPackage;

    public InstrumentationAdapter(Context context, String targetPackage) {
        this.mContext = context;
        this.mTargetPackage = targetPackage;
        this.mInflater = (LayoutInflater) context.getSystemService("layout_inflater");
        this.mPM = context.getPackageManager();
        this.mList = context.getPackageManager().queryInstrumentation(this.mTargetPackage, 0);
        if (this.mList != null) {
            Collections.sort(this.mList, new DisplayNameComparator(this.mPM));
        }
    }

    public ComponentName instrumentationForPosition(int position) {
        if (this.mList == null) {
            return null;
        }
        InstrumentationInfo ii = (InstrumentationInfo) this.mList.get(position);
        return new ComponentName(ii.packageName, ii.name);
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
        bindView(view, (InstrumentationInfo) this.mList.get(position));
        return view;
    }

    private final void bindView(View view, InstrumentationInfo info) {
        TextView text = (TextView) view.findViewById(16908308);
        CharSequence label = info.loadLabel(this.mPM);
        if (label == null) {
            label = info.name;
        }
        text.setText(label);
    }
}
