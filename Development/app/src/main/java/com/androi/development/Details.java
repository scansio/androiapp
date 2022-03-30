package com.androi.development;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MenuItem.OnMenuItemClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import java.util.ArrayList;

public class Details extends Activity {
    ArrayList<ColumnData> mData;
    LinearLayout mLinearLayout;
    OnMenuItemClickListener mPrintToStdout = new OnMenuItemClickListener() {
        public boolean onMenuItemClick(MenuItem item) {
            System.out.println("=== begin data ===");
            int count = Details.this.mData.size();
            for (int i = 0; i < count; i++) {
                ColumnData cd = (ColumnData) Details.this.mData.get(i);
                System.out.println("  " + cd.key + ": " + cd.value);
            }
            System.out.println("=== end data ===");
            return true;
        }
    };
    OnMenuItemClickListener mRequery = new OnMenuItemClickListener() {
        public boolean onMenuItemClick(MenuItem item) {
            Intent intent = Details.this.getIntent();
            Cursor c = Details.this.getContentResolver().query(intent.getData(), null, null, null, null);
            if (c == null || !c.moveToNext()) {
                TextView error = new TextView(Details.this);
                error.setText("Showing old data.\nURL couldn't be requeried:\n" + intent.getData());
                error.setTextColor(-65536);
                error.setTextSize(11.0f);
                Details.this.mLinearLayout.addView(error, 0, Details.this.lazy());
            } else {
                Details.this.mData.clear();
                String[] columnNames = c.getColumnNames();
                for (int i = 0; i < columnNames.length; i++) {
                    Details.this.mData.add(new ColumnData(columnNames[i], c.getString(i)));
                }
                Details.this.addDataViews();
            }
            return true;
        }
    };
    ScrollView mScrollView;

    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        String title = getIntent().getStringExtra("title");
        if (title == null) {
            title = "Details";
        }
        setTitle(title);
        this.mScrollView = new ScrollView(this);
        setContentView(this.mScrollView);
        this.mScrollView.setFocusable(true);
        this.mData = (ArrayList) getIntent().getExtra("data");
        addDataViews();
    }

    public void onResume() {
        super.onResume();
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        menu.add(0, 0, 0, "Requery").setOnMenuItemClickListener(this.mRequery);
        menu.add(0, 0, 0, "Print to stdout").setOnMenuItemClickListener(this.mPrintToStdout);
        return true;
    }

    void addDataViews() {
        if (this.mLinearLayout != null) {
            this.mScrollView.removeView(this.mLinearLayout);
        }
        this.mLinearLayout = new LinearLayout(this);
        this.mScrollView.addView(this.mLinearLayout, new LayoutParams(-1, -1));
        this.mLinearLayout.setOrientation(1);
        int count = this.mData.size();
        for (int i = 0; i < count; i++) {
            int i2;
            ColumnData cd = (ColumnData) this.mData.get(i);
            TextView label = makeView(cd.key, true, 12);
            TextView contents = makeView(cd.value, false, 12);
            if (i == count - 1) {
                i2 = 0;
            } else {
                i2 = 3;
            }
            contents.setPadding(3, 0, 0, i2);
            this.mLinearLayout.addView(label, lazy());
            this.mLinearLayout.addView(contents, lazy());
        }
    }

    TextView makeView(String str, boolean bold, int fontSize) {
        if (str == null) {
            str = "(null)";
        }
        TextView v = new TextView(this);
        v.setText(str);
        v.setTextSize((float) fontSize);
        if (bold) {
            v.setTypeface(Typeface.DEFAULT_BOLD);
        }
        return v;
    }

    LinearLayout.LayoutParams lazy() {
        return new LinearLayout.LayoutParams(-1, -2, 0.0f);
    }
}
