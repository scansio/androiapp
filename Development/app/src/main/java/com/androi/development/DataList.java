package com.androi.development;

import android.app.ListActivity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MenuItem.OnMenuItemClickListener;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import java.util.ArrayList;

public class DataList extends ListActivity {
    private Cursor mCursor;
    private String mDisplay;
    OnMenuItemClickListener mRequery = new OnMenuItemClickListener() {
        public boolean onMenuItemClick(MenuItem item) {
            DataList.this.mCursor.requery();
            if (DataList.this.mCursor != null) {
                DataList.this.setListAdapter(new SimpleCursorAdapter(DataList.this, R.layout.url_list, DataList.this.mCursor, new String[]{DataList.this.mDisplay}, new int[]{16908308}));
            }
            return true;
        }
    };

    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        Intent intent = getIntent();
        this.mCursor = getContentResolver().query(intent.getData(), null, null, null, null);
        this.mDisplay = intent.getStringExtra("display");
        if (this.mDisplay == null) {
            this.mDisplay = "_id";
        }
        if (this.mCursor != null) {
            setListAdapter(new SimpleCursorAdapter(this, R.layout.url_list, this.mCursor, new String[]{this.mDisplay}, new int[]{16908308}));
        }
    }

    public void onStop() {
        super.onStop();
        if (this.mCursor != null) {
            this.mCursor.deactivate();
        }
    }

    public void onResume() {
        super.onResume();
        if (this.mCursor != null) {
            this.mCursor.requery();
        }
        setTitle("Showing " + this.mDisplay);
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        menu.add(0, 0, 0, "Requery").setOnMenuItemClickListener(this.mRequery);
        return true;
    }

    protected void onListItemClick(ListView l, View v, int position, long id) {
        this.mCursor.moveToPosition(position);
        ArrayList<ColumnData> data = new ArrayList();
        String[] columnNames = this.mCursor.getColumnNames();
        for (int i = 0; i < columnNames.length; i++) {
            data.add(new ColumnData(columnNames[i], this.mCursor.getString(i)));
        }
        Uri uri = null;
        int idCol = this.mCursor.getColumnIndex("_id");
        if (idCol >= 0) {
            uri = Uri.withAppendedPath(getIntent().getData(), this.mCursor.getString(idCol));
        }
        Intent intent = new Intent("android.intent.action.VIEW", uri);
        intent.setClass(this, Details.class);
        intent.putExtra("data", data);
        int displayColumn = this.mCursor.getColumnIndex(this.mDisplay);
        if (displayColumn >= 0) {
            intent.putExtra("title", ((ColumnData) data.get(displayColumn)).value);
        }
        startActivity(intent);
    }
}
