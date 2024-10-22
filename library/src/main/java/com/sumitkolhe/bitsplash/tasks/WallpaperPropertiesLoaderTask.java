package com.sumitkolhe.bitsplash.tasks;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;

import com.sumitkolhe.bitsplash.databases.Database;
import com.sumitkolhe.bitsplash.items.Wallpaper;
import com.danimahardhika.android.helpers.core.utils.LogUtil;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageSize;

import java.io.File;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.Executor;



public class WallpaperPropertiesLoaderTask extends AsyncTask<Void, Void, Boolean> {

    private Wallpaper mWallpaper;
    private WeakReference<Callback> mCallback;
    private final WeakReference<Context> mContext;

    private WallpaperPropertiesLoaderTask(Context context) {
        mContext = new WeakReference<>(context);
    }

    public WallpaperPropertiesLoaderTask wallpaper(Wallpaper wallpaper) {
        mWallpaper = wallpaper;
        return this;
    }

    public WallpaperPropertiesLoaderTask callback(@Nullable Callback callback) {
        mCallback = new WeakReference<>(callback);
        return this;
    }

    public AsyncTask start() {
        return start(SERIAL_EXECUTOR);
    }

    public AsyncTask start(@NonNull Executor executor) {
        return executeOnExecutor(executor);
    }

    public static WallpaperPropertiesLoaderTask prepare(@NonNull Context context) {
        return new WallpaperPropertiesLoaderTask(context);
    }

    @Override
    protected Boolean doInBackground(Void... voids) {
        while (!isCancelled()) {
            try {
                Thread.sleep(1);
                if (mWallpaper == null) return false;

                if (mWallpaper.getDimensions() != null &&
                        mWallpaper.getMimeType() != null &&
                        mWallpaper.getSize() > 0) {
                    return false;
                }

                final BitmapFactory.Options options = new BitmapFactory.Options();
                options.inJustDecodeBounds = true;

                URL url = new URL(mWallpaper.getUrl());
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setConnectTimeout(15000);

                if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    InputStream stream = connection.getInputStream();
                    BitmapFactory.decodeStream(stream, null, options);

                    ImageSize imageSize = new ImageSize(options.outWidth, options.outHeight);
                    mWallpaper.setDimensions(imageSize);
                    mWallpaper.setMimeType(options.outMimeType);

                    int contentLength = connection.getContentLength();
                    if (contentLength > 0) {
                        mWallpaper.setSize(contentLength);
                    }

                    Database.get(mContext.get()).updateWallpaper(mWallpaper);
                    stream.close();
                    return true;
                }
                return false;
            } catch (Exception e) {
                LogUtil.e(Log.getStackTraceString(e));
                return false;
            }
        }
        return false;
    }

    @Override
    protected void onPostExecute(Boolean aBoolean) {
        super.onPostExecute(aBoolean);
        if (aBoolean && mContext.get() != null && !((AppCompatActivity) mContext.get()).isFinishing()) {
            if (mWallpaper.getSize() <= 0) {
                File target = ImageLoader.getInstance().getDiskCache().get(mWallpaper.getUrl());
                if (target.exists()) {
                    mWallpaper.setSize((int) target.length());
                }
            }
        }

        if (mCallback != null && mCallback.get() != null) {
            mCallback.get().onPropertiesReceived(mWallpaper);
        }
    }

    public interface Callback {
        void onPropertiesReceived(Wallpaper wallpaper);
    }
}
