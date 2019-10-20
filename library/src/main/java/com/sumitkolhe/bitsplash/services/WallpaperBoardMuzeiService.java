package com.sumitkolhe.bitsplash.services;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import com.sumitkolhe.bitsplash.activities.WallpaperBoardPreviewActivity;
import com.sumitkolhe.bitsplash.databases.Database;
import com.sumitkolhe.bitsplash.helpers.MuzeiHelper;
import com.sumitkolhe.bitsplash.items.Wallpaper;
import com.sumitkolhe.bitsplash.preferences.Preferences;
import com.sumitkolhe.bitsplash.utils.Extras;
import com.danimahardhika.android.helpers.core.utils.LogUtil;
import com.google.android.apps.muzei.api.Artwork;
import com.google.android.apps.muzei.api.RemoteMuzeiArtSource;



public abstract class WallpaperBoardMuzeiService extends RemoteMuzeiArtSource {

    public WallpaperBoardMuzeiService(String name) {
        super(name);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Bundle bundle = intent.getExtras();
        if (bundle != null) {
            boolean restart = intent.getBooleanExtra("restart", false);
            if (restart) {
                try {
                    onTryUpdate(UPDATE_REASON_USER_NEXT);
                } catch (RetryException ignored) {}
            }
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        setUserCommands(BUILTIN_COMMAND_ID_NEXT_ARTWORK);
    }

    @Override
    protected void onTryUpdate(int reason) throws RemoteMuzeiArtSource.RetryException {
        try {
            if (Preferences.get(this).isConnectedAsPreferred()) {
                Wallpaper wallpaper = MuzeiHelper.getRandomWallpaper(this);

                if (wallpaper != null) {
                    Uri uri = Uri.parse(wallpaper.getUrl());

                    Intent intent = new Intent(this, WallpaperBoardPreviewActivity.class);
                    intent.putExtra(Extras.EXTRA_URL, wallpaper.getUrl());
                    intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

                    publishArtwork(new Artwork.Builder()
                            .title(wallpaper.getName())
                            .byline(wallpaper.getAuthor())
                            .imageUri(uri)
                            .viewIntent(intent)
                            .build());

                    scheduleUpdate(System.currentTimeMillis() +
                            Preferences.get(this).getRotateTime());
                }

                Database.get(this).closeDatabase();
            }
        } catch (Exception e) {
            LogUtil.e(Log.getStackTraceString(e));
        }
    }
}
