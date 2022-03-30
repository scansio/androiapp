package install.apps;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class appDetail implements Serializable {
    public static final long serialVersionUID = 1122L;
    public int refCount;
    public int count;
    public Context context;
    public PackageManager pkm;
    public Handler handler;
    public LinearLayout parentLayout;
    public HorizontalScrollView searchLayout;
    public LinearLayout searchResultLayout;
    public Map<String, Intent> mp;
    public static final String FILENAME = "data.o";
    public static final String DIRECTORY = Environment.getExternalStorageDirectory().getAbsolutePath() + "/inApp/";
    Iterator<Map.Entry<String, Intent>> labelIterator;
    List<PackageInfo> appInfo;
    private ArrayList<String> labels;
    private ArrayList<String> pkgName;
    private EditText searchBar;
    public static appDetail instance;


    @SuppressLint({"WrongConstant", "InlinedApi"})
    @SuppressWarnings("unchecked")
    public appDetail(Context context, View[] layouts) {
        try {
            this.context = context;
            this.parentLayout = (LinearLayout) layouts[0];
            this.searchLayout = (HorizontalScrollView) layouts[1];
            searchBar = (EditText) layouts[2];
            handler = new Handler(this.context.getMainLooper());
            pkm = this.context.getPackageManager();
            appInfo = pkm.getInstalledPackages(PackageManager.MATCH_ALL);
            count = appInfo.size();
            mp = new TreeMap<>(new AlphabeticalComparator<>());
         
            if (new File(DIRECTORY + FILENAME).exists()) {
                try {
                    ObjectInputStream out =
                            new ObjectInputStream(new FileInputStream(DIRECTORY + FILENAME));
                    labels = (ArrayList<String>) out.readObject();
                    pkgName = (ArrayList<String>) out.readObject();
                    refCount = out.readInt();
                    int tempCount = out.readInt();
                    out.close();
                    if (count != tempCount) {
                        helper();
                    } else {
                        new Thread(
                                new Runnable() {
                                    public void run() {
                                        init1(null, true);
                                    }
                                })
                                .start();
                    }
                } catch (Exception e) {
                    toast(e.getMessage() + " occur while reading saved");
                    helper();
                }
            } else {
                helper();
            }
        } catch (Exception e) {
            toast(e.getMessage());
        }
        save();
        
    }
    
    public static void setInstance(appDetail i){
    if(instance == null)
      instance = i;
    }
    
    private appDetail(){}
    
    public static appDetail getInstance(){
      return instance != null ? instance : new appDetail();
    }

    void helper() {
        labels = new ArrayList<>();
        pkgName = new ArrayList<>();
        count = appInfo.size();
        refCount = 0;
        new Thread(
                new Runnable() {
                    public void run() {
                        init1(appInfo, false);
                    }
                })
                .start();
    }

    void init1(List<PackageInfo> packageInfo, boolean fromSaved) {
        labelAndPackageName(packageInfo, fromSaved);
        save();
    }

    void labelAndPackageName(List<PackageInfo> packageInfo, boolean s) {
        try {
            if (!s) {
                mp = new TreeMap<>(new AlphabeticalComparator<>());
                labels = new ArrayList<>();
                pkgName = new ArrayList<>();
                refCount = 0;
            }
            if (packageInfo == null) {

                for (int i = 0; i < pkgName.size(); i++) {
                    try {
                        mp.put(labels.get(i), pkm.getLaunchIntentForPackage(pkgName.get(i)));
                    } catch (Exception e) {
                        toast(e.getMessage());
                    }
                }
            } else {
                count = packageInfo.size();
                for (PackageInfo pi : packageInfo) {
                    String label = "" + pkm.getApplicationLabel(pi.applicationInfo);
                    String pkg = pi.packageName;
                    Intent intent = pkm.getLaunchIntentForPackage(pkg);
                    if (intent != null) {
                        mp.put(label, intent);
                        labels.add(label);
                        pkgName.add(pkg);
                        refCount++;
                    }
                }
            }
            labelIterator = mp.entrySet().iterator();
            run();
        } catch (Exception e) {
            toast(e.getMessage());
        }
    }

    public LinearLayout button() {
        LinearLayout linearLayout = null;
        if (labelIterator.hasNext()) {
            Map.Entry<String, Intent> item = labelIterator.next();
            String str = item.getKey();
            linearLayout = getView(str);
            registerActivityForViewClick(linearLayout.getChildAt(0), item.getValue());
        }
        return linearLayout;
    } // End of buttons

    public void run() {
        searchResultLayout =
                new LinearLayoutFactory.Builder(context)
                        .setSize(-1, 150)
                        .setVisibility(View.GONE)
                        .setBackground(R.color.btn_state)
                        .build();
        handler.post(
                new Runnable() {
                    public void run() {
                        searchLayout.removeAllViews();
                        searchLayout.addView(searchResultLayout);
                    }
                });
        handler.post(
                new Runnable() {
                    public void run() {
                        parentLayout.removeAllViews();
                    }
                });
        for (int i = 0; i < refCount - 1; ) {
            try {
                final LinearLayout linearLayout =
                        new LinearLayoutFactory.Builder(context)
                                .setSize(-1, 150)
                                .setBackground(R.color.btn_state)
                                .build();
                handler.post(
                        new Runnable() {
                            public void run() {
                                try {
                                    parentLayout.addView(linearLayout);
                                } catch (Exception e) {
                                    toast(e.getMessage());
                                }
                            }
                        });

                for (int j = 0; j < 5 & i < refCount - 1; j++, i++) {
                    handler.post(
                            new Runnable() {
                                public void run() {
                                    try {
                                        linearLayout.addView(button());
                                    } catch (Exception e) {
                                        toast(e.getMessage());
                                    }
                                }
                            });
                }
            } catch (Exception e) {
                toast(e.getMessage());
            }
        }
        handler.post(
                new Runnable() {
                    public void run() {
                        searchBar.setVisibility(View.VISIBLE);
                    }
                });
    } // End of run()

    public void addResult(String mLayout, boolean refresh) {
        try {
            if (refresh) {
                handler.post(
                        new Runnable() {
                            public void run() {
                                searchResultLayout.removeAllViews();
                            }
                        });

            } else {
                handler.post(
                        new Runnable() {
                            public void run() {
                                searchResultLayout.setVisibility(View.VISIBLE);
                            }
                        });
            }
            Intent intent = mp.get(mLayout);
            final LinearLayout layout = getView(mLayout);
            registerActivityForViewClick(layout.getChildAt(0), intent);
            handler.post(
                    new Runnable() {
                        public void run() {
                            searchResultLayout.addView(layout);
                        }
                    });
        } catch (Exception e) {
            toast(e.getMessage());
            init1(appInfo, false);
        }
    }

    void registerActivityForViewClick(View view, final Intent intent) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                view.setBackground(pkm.getActivityIcon(intent));
            }
            view.setOnClickListener(
                    new View.OnClickListener() {
                        public void onClick(View view) {
                            try {
                                context.startActivity(intent);
                            } catch (Exception e) {
                                toast(e.getMessage());
                                init1(appInfo, false);
                            }
                        }
                    });
        } catch (Exception e) {
            toast(e.getMessage());
            init1(appInfo, false);
        }
    }

    public LinearLayout getView(String str) {

        TextView tv = new TextView(context);
        tv.setLayoutParams(new ViewGroup.LayoutParams(-2, -2));
        tv.setGravity(Gravity.CENTER);
        int k = str.length();
        tv.setText(k > 10 ? str.substring(0, 7) + "..." : str);
        tv.setTextSize(10);
        
        tv.setTextColor(context.getResources().getColor(R.color.k));

        return new LinearLayoutFactory.Builder(context)
                .setSize(-2, -2)
                .setOrientation(1)
                .setPadding(5, 5, 5, 2)
                .addChild(
                        new ButtonFactory.Builder(context)
                                .setSize(100, 100)
                                .setPadding(5, 5, 5, 2)
                                .build())
                .addChild(tv)
                .build();
    }

    public List<String> getLabels() {
        return labels;
    }

    void toast(final Object message) {
        handler.post(
                new Thread(
                        new Runnable() {
                            public void run() {
                                Toast.makeText(context, message.toString(), Toast.LENGTH_LONG)
                                        .show();
                            }
                        }));
    }

    void save() {
        new File(DIRECTORY).mkdirs();
        new Thread(new Save(refCount, count, labels, pkgName, DIRECTORY + FILENAME)).start();
    }
}
