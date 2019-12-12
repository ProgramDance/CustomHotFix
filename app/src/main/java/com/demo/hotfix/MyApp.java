package com.demo.hotfix;

import android.app.Application;
import android.content.Context;
import android.os.Environment;
import android.util.Log;

public class MyApp extends Application {
    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        String patchPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/patch.dex";
        HotFixUtil.startFix(this, patchPath);
        Log.d("tag", "app attachBaseContext");
    }
}
