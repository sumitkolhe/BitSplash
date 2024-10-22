package com.sumitkolhe.bitsplash.applications;

import android.app.Application;
import android.content.Intent;

import com.danimahardhika.android.helpers.core.TimeHelper;
import com.sumitkolhe.bitsplash.board.R;
import com.sumitkolhe.bitsplash.activities.WallpaperBoardCrashReport;
import com.sumitkolhe.bitsplash.databases.Database;
import com.sumitkolhe.bitsplash.helpers.LocaleHelper;
import com.sumitkolhe.bitsplash.helpers.UrlHelper;
import com.sumitkolhe.bitsplash.preferences.Preferences;
import com.sumitkolhe.bitsplash.utils.ImageConfig;
import com.danimahardhika.android.helpers.core.utils.LogUtil;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.text.SimpleDateFormat;
import java.util.Date;

import io.github.inflationx.calligraphy3.CalligraphyConfig;
import io.github.inflationx.calligraphy3.CalligraphyInterceptor;
import io.github.inflationx.calligraphy3.CalligraphyUtils;
import io.github.inflationx.viewpump.ViewPump;


public abstract class WallpaperBoardApplication extends Application implements ApplicationCallback {

    public static boolean sIsClickable = true;
    private static boolean mIsLatestWallpapersLoaded;
    private static WallpaperBoardConfiguration mConfiguration;

    private Thread.UncaughtExceptionHandler mHandler;

    @Override
    public void onCreate() {
        super.onCreate();
        Database.get(this).openDatabase();
        Preferences.get(this);

        if (!ImageLoader.getInstance().isInited())
            ImageLoader.getInstance().init(ImageConfig.getImageLoaderConfiguration(this));

        ViewPump.init(ViewPump.builder()
                .addInterceptor(new CalligraphyInterceptor(
                        new CalligraphyConfig.Builder()
                                .setDefaultFontPath("fonts/Font-Regular.ttf")
                                .setFontAttrId(R.attr.fontPath)
                                .build())).build());


        //Enable logging
        LogUtil.setLoggingTag(getString(R.string.app_name));
        LogUtil.setLoggingEnabled(true);

        mConfiguration = onInit();

        if (mConfiguration.isCrashReportEnabled()) {
            String[] urls = getResources().getStringArray(R.array.about_social_links);
            boolean isContainsValidEmail = false;
            for (String url : urls) {
                if (UrlHelper.getType(url) == UrlHelper.Type.EMAIL) {
                    isContainsValidEmail = true;
                    mConfiguration.setCrashReportEmail(url);
                    break;
                }
            }

            if (isContainsValidEmail) {
                mHandler = Thread.getDefaultUncaughtExceptionHandler();
                Thread.setDefaultUncaughtExceptionHandler(this::handleUncaughtException);
            } else {
                mConfiguration.setCrashReportEnabled(false);
                mConfiguration.setCrashReportEmail(null);
            }
        }

        LocaleHelper.setLocale(this);
    }

    public static WallpaperBoardConfiguration getConfig() {
        if (mConfiguration == null) {
            mConfiguration = new WallpaperBoardConfiguration();
        }
        return mConfiguration;
    }

    public static boolean isLatestWallpapersLoaded() {
        return mIsLatestWallpapersLoaded;
    }

    public static void setLatestWallpapersLoaded(boolean loaded) {
        mIsLatestWallpapersLoaded = loaded;
    }

    private void handleUncaughtException(Thread thread, Throwable throwable) {
        try {
            StringBuilder sb = new StringBuilder();
            SimpleDateFormat dateFormat = TimeHelper.getDefaultShortDateTimeFormat();
            String dateTime = dateFormat.format(new Date());
            sb.append("Crash Time : ").append(dateTime).append("\n");
            sb.append("Class Name : ").append(throwable.getClass().getName()).append("\n");
            sb.append("Caused By : ").append(throwable.toString()).append("\n");

            for (StackTraceElement element : throwable.getStackTrace()) {
                sb.append("\n");
                sb.append(element.toString());
            }

            Intent intent = new Intent(this, WallpaperBoardCrashReport.class);
            intent.putExtra(WallpaperBoardCrashReport.EXTRA_STACKTRACE, sb.toString());
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
            startActivity(intent);
        } catch (Exception e) {
            if (mHandler != null) {
                mHandler.uncaughtException(thread, throwable);
                return;
            }
        }
        System.exit(1);
    }
}
