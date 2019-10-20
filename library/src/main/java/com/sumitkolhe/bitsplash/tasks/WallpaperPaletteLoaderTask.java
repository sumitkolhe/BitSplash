package com.sumitkolhe.bitsplash.tasks;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import androidx.annotation.Nullable;
import androidx.palette.graphics.Palette;

import com.sumitkolhe.bitsplash.items.ColorPalette;
import com.danimahardhika.android.helpers.core.utils.LogUtil;



public class WallpaperPaletteLoaderTask {

    private Callback mCallback;
    private Bitmap mBitmap;

    private WallpaperPaletteLoaderTask(Bitmap bitmap) {
        mBitmap = bitmap;
    }

    public WallpaperPaletteLoaderTask callback(@Nullable Callback callback) {
        mCallback = callback;
        return this;
    }

    public AsyncTask start() {
        if (mBitmap == null) {
            LogUtil.e("PaletteLoader cancelled, bitmap is null");
            return null;
        }

        try {
            return Palette.from(mBitmap).generate(palette -> {
                int dominant = palette.getDominantColor(0);
                int vibrant = palette.getVibrantColor(0);
                int vibrantLight = palette.getLightVibrantColor(0);
                int vibrantDark = palette.getDarkVibrantColor(0);
                int muted = palette.getMutedColor(0);
                int mutedLight = palette.getLightMutedColor(0);
                int mutedDark = palette.getDarkMutedColor(0);

                ColorPalette colorPalette = new ColorPalette();
                colorPalette.add(dominant);
                colorPalette.add(vibrant);
                colorPalette.add(vibrantLight);
                colorPalette.add(vibrantDark);
                colorPalette.add(muted);
                colorPalette.add(mutedLight);
                colorPalette.add(mutedDark);
                if (mCallback != null) {
                    mCallback.onPaletteGenerated(colorPalette);
                }
            });
        } catch (Exception ignored) {}
        return null;
    }

    public static WallpaperPaletteLoaderTask with(Bitmap bitmap) {
        return new WallpaperPaletteLoaderTask(bitmap);
    }

    public interface Callback {
        void onPaletteGenerated(ColorPalette palette);
    }
}
