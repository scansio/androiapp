package com.androi.development;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import java.util.List;

public abstract class ArrayAdapter<E> extends BaseAdapter {
    private final Context mContext;
    private final LayoutInflater mInflater;
    private final int mLayoutRes;
    private List<E> mList;

    public abstract void bindView(View view, E e);

    public ArrayAdapter(Context context, int layoutRes) {
        this.mContext = context;
        this.mInflater = (LayoutInflater) context.getSystemService("layout_inflater");
        this.mLayoutRes = layoutRes;
    }

    public void setSource(List<E> list) {
        this.mList = list;
    }

    public E itemForPosition(int position) {
        if (this.mList == null) {
            return null;
        }
        return this.mList.get(position);
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
            view = this.mInflater.inflate(this.mLayoutRes, parent, false);
        } else {
            view = convertView;
        }
        bindView(view, this.mList.get(position));
        return view;
    }
}
