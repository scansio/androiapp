package install.apps;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;

public class ChangeReceiver extends BroadcastReceiver {

    public ChangeReceiver() {}

    @TargetApi(Build.VERSION_CODES.M)
    @SuppressLint("WrongConstant")
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        appDetail.getInstance().toast("onReceived");
        if (action != null
                && (action.equals("android.intent.action.PACKAGE_REMOVED")
                        || action.equals("android.intent.action.PACKAGE_INSTALL")
                        || action.equals("android.intent.action.PACKAGE_CHANGED"))) {
            appDetail.getInstance().toast("Change Detected");
            Log.i("@onReceive", action);
            appDetail
                    .getInstance()
                    .init1(
                            context.getPackageManager()
                                    .getInstalledPackages(PackageManager.MATCH_ALL),
                            false);
        }
    }
}
