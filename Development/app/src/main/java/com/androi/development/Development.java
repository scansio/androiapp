package com.androi.development;

import android.app.LauncherActivity;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import java.util.List;

public class Development extends LauncherActivity {
    protected Intent getTargetIntent() {
        Intent targetIntent = new Intent("android.intent.action.MAIN", null);
        targetIntent.addCategory("android.intent.category.TEST");
        return targetIntent;
    }

    protected void onSortResultList(List<ResolveInfo> results) {
        super.onSortResultList(results);
        List<ResolveInfo> topItems = getPackageManager().queryIntentActivities(new Intent("android.settings.APPLICATION_DEVELOPMENT_SETTINGS"), 65536);
        if (topItems != null) {
            super.onSortResultList(topItems);
            results.addAll(0, topItems);
        }
    }
}
