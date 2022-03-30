package com.androi.development;

import android.app.AlertDialog.Builder;
import android.app.ListActivity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.IPackageDeleteObserver.Stub;
import android.content.pm.PackageInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.RemoteException;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MenuItem.OnMenuItemClickListener;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class PackageBrowser extends ListActivity {
    private static final Comparator<MyPackageInfo> sDisplayNameComparator = new Comparator<MyPackageInfo>() {
        private final Collator collator = Collator.getInstance();

        public final int compare(MyPackageInfo a, MyPackageInfo b) {
            return this.collator.compare(a.label, b.label);
        }
    };
    private PackageListAdapter mAdapter;
    private Handler mHandler;
    private List<MyPackageInfo> mPackageInfoList = new ArrayList();
    private BroadcastReceiver mRegisteredReceiver;

    private class ApplicationsIntentReceiver extends BroadcastReceiver {
        private ApplicationsIntentReceiver() {
        }

        /* synthetic */ ApplicationsIntentReceiver(PackageBrowser x0, AnonymousClass1 x1) {
            this();
        }

        public void onReceive(Context context, Intent intent) {
            PackageBrowser.this.setupAdapter();
        }
    }

    static class MyPackageInfo {
        PackageInfo info;
        String label;

        MyPackageInfo() {
        }
    }

    public class PackageListAdapter extends ArrayAdapter<MyPackageInfo> {
        public PackageListAdapter(Context context) {
            super(context, R.layout.package_list_item);
            List<PackageInfo> pkgs = context.getPackageManager().getInstalledPackages(0);
            for (int i = 0; i < pkgs.size(); i++) {
                MyPackageInfo info = new MyPackageInfo();
                info.info = (PackageInfo) pkgs.get(i);
                info.label = info.info.applicationInfo.loadLabel(PackageBrowser.this.getPackageManager()).toString();
                PackageBrowser.this.mPackageInfoList.add(info);
            }
            if (PackageBrowser.this.mPackageInfoList != null) {
                Collections.sort(PackageBrowser.this.mPackageInfoList, PackageBrowser.sDisplayNameComparator);
            }
            setSource(PackageBrowser.this.mPackageInfoList);
        }

        public void bindView(View view, MyPackageInfo info) {
            TextView name = (TextView) view.findViewById(R.id.name);
            TextView description = (TextView) view.findViewById(R.id.description);
            ((ImageView) view.findViewById(R.id.icon)).setImageDrawable(info.info.applicationInfo.loadIcon(PackageBrowser.this.getPackageManager()));
            name.setText(info.label);
            description.setText(info.info.packageName);
        }
    }

    protected void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setupAdapter();
        this.mHandler = new Handler();
        registerIntentReceivers();
    }

    protected void onDestroy() {
        super.onDestroy();
        if (this.mRegisteredReceiver != null) {
            unregisterReceiver(this.mRegisteredReceiver);
        }
    }

    private void setupAdapter() {
        this.mAdapter = new PackageListAdapter(this);
        setListAdapter(this.mAdapter);
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(0, 0, 0, "Delete package").setOnMenuItemClickListener(new OnMenuItemClickListener() {
            public boolean onMenuItemClick(MenuItem item) {
                PackageBrowser.this.deletePackage();
                return true;
            }
        });
        return true;
    }

    private void deletePackage() {
        final int curSelection = getSelectedItemPosition();
        if (curSelection >= 0) {
            final MyPackageInfo packageInfo = (MyPackageInfo) this.mAdapter.itemForPosition(curSelection);
            if (packageInfo != null) {
                getPackageManager().deletePackage(packageInfo.info.packageName, new Stub() {
                    public void packageDeleted(String packageName, int returnCode) throws RemoteException {
                        if (returnCode == 1) {
                            PackageBrowser.this.mPackageInfoList.remove(curSelection);
                            PackageBrowser.this.mHandler.post(new Runnable() {
                                public void run() {
                                    PackageBrowser.this.mAdapter.notifyDataSetChanged();
                                }
                            });
                            String str = packageInfo.info.applicationInfo.dataDir;
                            return;
                        }
                        PackageBrowser.this.mHandler.post(new Runnable() {
                            public void run() {
                                new Builder(PackageBrowser.this).setTitle("Oops").setMessage("Could not delete package.  Maybe it is in /system/app rather than /data/app?").show();
                            }
                        });
                    }
                }, 0);
            }
        }
    }

    private void registerIntentReceivers() {
        IntentFilter filter = new IntentFilter("android.intent.action.PACKAGE_ADDED");
        filter.addAction("android.intent.action.PACKAGE_REMOVED");
        filter.addAction("android.intent.action.PACKAGE_CHANGED");
        filter.addDataScheme("package");
        this.mRegisteredReceiver = new ApplicationsIntentReceiver(this, null);
        registerReceiver(this.mRegisteredReceiver, filter);
    }

    protected void onListItemClick(ListView l, View v, int position, long id) {
        MyPackageInfo info = (MyPackageInfo) this.mAdapter.itemForPosition(position);
        if (info != null) {
            Intent intent = new Intent(null, Uri.fromParts("package", info.info.packageName, null));
            intent.setClass(this, PackageSummary.class);
            startActivity(intent);
        }
    }
}
