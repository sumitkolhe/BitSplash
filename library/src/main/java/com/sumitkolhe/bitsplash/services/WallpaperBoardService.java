package com.sumitkolhe.bitsplash.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import androidx.annotation.Nullable;

import com.sumitkolhe.bitsplash.databases.Database;
import com.danimahardhika.android.helpers.core.utils.LogUtil;


public class WallpaperBoardService extends Service {

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        LogUtil.d("App removed from recent task, database connection closed");
        Database.get(this).closeDatabase();

        stopSelf();
    }
}
