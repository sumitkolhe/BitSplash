package com.sumitkolhe.bitsplash.helpers;

import android.content.Context;
import androidx.annotation.NonNull;
import android.util.Log;

import com.bluelinelabs.logansquare.LoganSquare;
import com.sumitkolhe.bitsplash.board.R;
import com.sumitkolhe.bitsplash.applications.WallpaperBoardApplication;
import com.sumitkolhe.bitsplash.databases.Database;
import com.sumitkolhe.bitsplash.items.Wallpaper;
import com.sumitkolhe.bitsplash.utils.JsonStructure;
import com.danimahardhika.android.helpers.core.utils.LogUtil;

import java.io.DataOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.Random;

import cz.msebera.android.httpclient.NameValuePair;



public class MuzeiHelper {

    public static Wallpaper getRandomWallpaper(@NonNull Context context) {
        if (Database.get(context).getWallpapersCount() > 0) {
            return Database.get(context).getRandomWallpaper();
        }

        try {
            String wallpaperUrl = WallpaperBoardApplication.getConfig().getJsonStructure().getUrl();
            if (wallpaperUrl == null) {
                wallpaperUrl = context.getResources().getString(R.string.wallpaper_json);
            }

            URL url = new URL(wallpaperUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setConnectTimeout(15000);

            if (WallpaperBoardApplication.getConfig().getJsonStructure().getUrl() != null) {
                connection.setRequestMethod("POST");
                connection.setUseCaches(false);
                connection.setDoOutput(true);

                List<NameValuePair> values = WallpaperBoardApplication.getConfig()
                        .getJsonStructure().getPosts();
                if (values.size() > 0) {
                    DataOutputStream stream = new DataOutputStream(connection.getOutputStream());
                    stream.writeBytes(JsonHelper.getQuery(values));
                    stream.flush();
                    stream.close();
                }
            }

            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                InputStream stream = connection.getInputStream();
                JsonStructure.WallpaperStructure wallpaperStructure = WallpaperBoardApplication
                        .getConfig().getJsonStructure().getWallpaper();

                Map<String, List> map = LoganSquare.parseMap(stream, List.class);
                if (map == null) return null;

                stream.close();
                List wallpaperList = map.get(wallpaperStructure.getArrayName());
                if (wallpaperList == null) {
                    LogUtil.e("Muzei error: wallpaper array with name "
                            + wallpaperStructure.getArrayName() + " not found");
                    return null;
                }

                if (wallpaperList.size() > 0) {
                    int position = getRandomInt(wallpaperList.size());
                    return JsonHelper.getWallpaper(wallpaperList.get(position));
                }
            }
            return null;
        } catch (Exception e) {
            LogUtil.e(Log.getStackTraceString(e));
            return null;
        }
    }

    private static int getRandomInt(int size) {
        try {
            Random random = new Random();
            return random.nextInt(size);
        } catch (Exception e) {
            return 0;
        }
    }
}
