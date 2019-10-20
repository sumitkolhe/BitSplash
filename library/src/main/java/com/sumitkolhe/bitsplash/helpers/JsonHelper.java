package com.sumitkolhe.bitsplash.helpers;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.sumitkolhe.bitsplash.applications.WallpaperBoardApplication;
import com.sumitkolhe.bitsplash.items.Category;
import com.sumitkolhe.bitsplash.items.Wallpaper;
import com.sumitkolhe.bitsplash.utils.JsonStructure;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;

import cz.msebera.android.httpclient.NameValuePair;


public class JsonHelper {

    @NonNull
    public static String getQuery(List<NameValuePair> params) throws UnsupportedEncodingException {
        StringBuilder result = new StringBuilder();
        boolean first = true;

        for (NameValuePair pair : params) {
            if (first) first = false;
            else result.append("&");

            result.append(URLEncoder.encode(pair.getName(), "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(pair.getValue(), "UTF-8"));
        }
        return result.toString();
    }

    @Nullable
    public static Wallpaper getWallpaper(@NonNull Object object) {
        if (object instanceof Map) {
            JsonStructure jsonStructure = WallpaperBoardApplication.getConfig().getJsonStructure();
            JsonStructure.WallpaperStructure wallpaperStructure = jsonStructure.getWallpaper();

            Map map = (Map) object;
            return Wallpaper.Builder()
                    .name((String) map.get(wallpaperStructure.getName()))
                    .author((String) map.get(wallpaperStructure.getAuthor()))
                    .url((String) map.get(wallpaperStructure.getUrl()))
                    .thumbUrl(getThumbUrl(map))
                    .category((String) map.get(wallpaperStructure.getCategory()))
                    .build();
        }
        return null;
    }

    @Nullable
    public static Category getCategory(@NonNull Object object) {
        if (object instanceof Map) {
            JsonStructure jsonStructure = WallpaperBoardApplication.getConfig().getJsonStructure();
            JsonStructure.CategoryStructure categoryStructure = jsonStructure.getCategory();

            Map map = (Map) object;
            return Category.Builder()
                    .name((String) map.get(categoryStructure.getName()))
                    .build();
        }
        return null;
    }

    public static String getThumbUrl(@NonNull Map map) {
        JsonStructure jsonStructure = WallpaperBoardApplication.getConfig().getJsonStructure();
        JsonStructure.WallpaperStructure wallpaperStructure = jsonStructure.getWallpaper();

        String url = (String) map.get(wallpaperStructure.getUrl());
        if (wallpaperStructure.getThumbUrl() == null) return url;

        String thumbUrl = (String) map.get(wallpaperStructure.getThumbUrl());
        if (thumbUrl == null) return url;
        return thumbUrl;
    }
}
