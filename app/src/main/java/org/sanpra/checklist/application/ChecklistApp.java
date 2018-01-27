package org.sanpra.checklist.application;

import android.app.Application;
import android.support.multidex.MultiDexApplication;

import com.squareup.leakcanary.LeakCanary;

public final class ChecklistApp extends MultiDexApplication {
    @Override
    public void onCreate() {
        super.onCreate();
        if (LeakCanary.isInAnalyzerProcess(this)) {
            return;
        }
        LeakCanary.install(this);
    }
}
