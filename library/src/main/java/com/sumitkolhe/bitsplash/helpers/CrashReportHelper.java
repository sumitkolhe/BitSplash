package com.sumitkolhe.bitsplash.helpers;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.util.DisplayMetrics;
import android.util.Log;

import com.sumitkolhe.bitsplash.board.BuildConfig;
import com.sumitkolhe.bitsplash.board.R;
import com.danimahardhika.android.helpers.core.utils.LogUtil;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;



public class CrashReportHelper {

    @Nullable
    public static String buildCrashLog(@NonNull Context context, @NonNull File folder, String stackTrace) {
        try {
            if (stackTrace.length() == 0) return null;

            File fileDir = new File(folder.toString() + "/crashlog.txt");
            String deviceInfo = getDeviceInfoForCrashReport(context);
            Writer out = new BufferedWriter(new OutputStreamWriter(
                    new FileOutputStream(fileDir), "UTF8"));
            out.append(deviceInfo).append(stackTrace);
            out.flush();
            out.close();

            return fileDir.toString();
        } catch (Exception | OutOfMemoryError e) {
            LogUtil.e(Log.getStackTraceString(e));
        }
        return null;
    }

    @NonNull
    private static String getDeviceInfo(@NonNull Context context) {
        DisplayMetrics displaymetrics = context.getResources().getDisplayMetrics();
        StringBuilder sb = new StringBuilder();
        final int height = displaymetrics.heightPixels;
        final int width = displaymetrics.widthPixels;

        String appVersion = "";
        try {
            appVersion = context.getPackageManager().getPackageInfo(
                    context.getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException ignored) {}

        sb.append("Manufacturer : ").append(Build.MANUFACTURER)
                .append("\nModel : ").append(Build.MODEL)
                .append("\nProduct : ").append(Build.PRODUCT)
                .append("\nScreen Resolution : ")
                .append(width).append(" x ").append(height).append(" pixels")
                .append("\nAndroid Version : ").append(Build.VERSION.RELEASE)
                .append("\nApp Version : ").append(appVersion)
                .append("\n");
        return sb.toString();
    }

    @NonNull
    public static String getDeviceInfoForCrashReport(@NonNull Context context) {
        return "WallpaperBoard Version : " + BuildConfig.VERSION_NAME +
                "\nApp Name : " +context.getResources().getString(R.string.app_name)
                + "\n"+ getDeviceInfo(context);
    }
}
