package com.sumitkolhe.bitsplash.helpers;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Point;
import android.graphics.RectF;
import android.os.Build;
import android.os.Environment;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.danimahardhika.android.helpers.core.FileHelper;
import com.danimahardhika.android.helpers.core.WindowHelper;
import com.sumitkolhe.bitsplash.board.R;
import com.sumitkolhe.bitsplash.items.Wallpaper;
import com.sumitkolhe.bitsplash.preferences.Preferences;
import com.nostra13.universalimageloader.core.assist.ImageSize;

import java.io.File;
import java.text.DecimalFormat;
import java.text.NumberFormat;


public class WallpaperHelper {

    public static File getDefaultWallpapersDirectory(@NonNull Context context) {
        try {
            if (Preferences.get(context).getWallsDirectory().length() == 0) {
                return new File(Environment.getExternalStoragePublicDirectory(
                        Environment.DIRECTORY_PICTURES) +"/"+
                        context.getResources().getString(R.string.app_name));
            }
            return new File(Preferences.get(context).getWallsDirectory());
        } catch (Exception e) {
            return new File(context.getFilesDir().toString() +"/Pictures/"+
                    context.getResources().getString(R.string.app_name));
        }
    }

    public static String getSize(int size) {
        NumberFormat formatter = new DecimalFormat("#0.00");
        if (size > 1024) {
            double formattedSize = (double) size / FileHelper.MB;
            return formatter.format(formattedSize) +" MB";
        }

        double formattedSize = (double) size / FileHelper.KB;
        return formatter.format(formattedSize) +" KB";
    }

    public static String getFormat(String mimeType) {
        if (mimeType == null) return "jpg";
        switch (mimeType) {
            case "image/jpeg":
                return "jpg";
            case "image/png":
                return "png";
            default:
                return "jpg";
        }
    }

    public static boolean isWallpaperSaved(@NonNull Context context, @NonNull Wallpaper wallpaper) {
        String fileName = wallpaper.getName() +"."+ getFormat(wallpaper.getMimeType());
        File directory = WallpaperHelper.getDefaultWallpapersDirectory(context);
        File target = new File(directory, fileName);

        if (target.exists()) {
            long size = target.length();
            return size == wallpaper.getSize();
        }
        return false;
    }

    public static ImageSize getTargetSize(@NonNull Context context) {
        Point point = WindowHelper.getScreenSize(context);
        int targetHeight = point.y;
        int targetWidth = point.x;

        if (context.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            targetHeight = point.x;
            targetWidth = point.y;
        }

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
            int statusBarHeight = WindowHelper.getStatusBarHeight(context);
            int navBarHeight = WindowHelper.getNavigationBarHeight(context);
            targetHeight += (statusBarHeight + navBarHeight);
        }
        return new ImageSize(targetWidth, targetHeight);
    }

    @Nullable
    public static RectF getScaledRectF(@Nullable RectF rectF, float heightFactor, float widthFactor) {
        if (rectF == null) return null;

        RectF scaledRectF = new RectF(rectF);
        scaledRectF.top *= heightFactor;
        scaledRectF.bottom *= heightFactor;
        scaledRectF.left *= widthFactor;
        scaledRectF.right *= widthFactor;
        return scaledRectF;
    }
}
