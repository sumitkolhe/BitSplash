package com.sumitkolhe.bitsplash.tasks;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import androidx.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import com.danimahardhika.android.helpers.permission.PermissionHelper;
import com.sumitkolhe.bitsplash.board.R;
import com.sumitkolhe.bitsplash.databases.Database;
import com.sumitkolhe.bitsplash.helpers.BackupHelper;
import com.sumitkolhe.bitsplash.preferences.Preferences;
import com.sumitkolhe.bitsplash.utils.Extras;
import com.danimahardhika.android.helpers.core.utils.LogUtil;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.lang.ref.WeakReference;
import java.net.URLDecoder;
import java.util.concurrent.Executor;



public class LocalFavoritesRestoreTask extends AsyncTask<Void, Void, Boolean> {

    private boolean mIsBackupExist;
    private boolean mIsStorageGranted;
    private WeakReference<Context> mContext;

    private LocalFavoritesRestoreTask(Context context) {
        mContext = new WeakReference<>(context);
        mIsStorageGranted = PermissionHelper.isStorageGranted(mContext.get());
    }

    public AsyncTask start() {
        return start(SERIAL_EXECUTOR);
    }

    public AsyncTask start(@NonNull Executor executor) {
        return executeOnExecutor(executor);
    }

    public static LocalFavoritesRestoreTask with(@NonNull Context context) {
        return new LocalFavoritesRestoreTask(context);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        File file = new File(BackupHelper.getDefaultDirectory(mContext.get()), BackupHelper.FILE_BACKUP);
        mIsBackupExist = file.exists();
    }

    @Override
    protected Boolean doInBackground(Void... voids) {
        while (!isCancelled()) {
            try {
                Thread.sleep(1);
                if (!mIsStorageGranted) {
                    LogUtil.e("Storage permission not granted, it's required to restore local backup");
                    return false;
                }

                if (mIsBackupExist) {
                    File file = new File(BackupHelper.getDefaultDirectory(mContext.get()), BackupHelper.FILE_BACKUP);
                    XmlPullParserFactory xmlPullParserFactory = XmlPullParserFactory.newInstance();
                    xmlPullParserFactory.setNamespaceAware(true);
                    XmlPullParser xmlPullParser = xmlPullParserFactory.newPullParser();
                    xmlPullParser.setInput(new InputStreamReader(new FileInputStream(file)));

                    while (xmlPullParser.getEventType() != XmlPullParser.END_DOCUMENT) {
                        if (xmlPullParser.getEventType() == XmlPullParser.START_TAG) {
                            if (xmlPullParser.getName().equals(Extras.EXTRA_ITEM)) {
                                String url = xmlPullParser.getAttributeValue(null, Extras.EXTRA_URL);
                                Database.get(mContext.get()).favoriteWallpaper(
                                        URLDecoder.decode(url, "utf-8"), true);
                            }
                        }
                        xmlPullParser.next();
                    }
                    Preferences.get(mContext.get()).setBackupRestored(true);
                    Preferences.get(mContext.get()).setPreviousBackupExist(false);
                    return true;
                }

                LogUtil.d("Backup file not found");
                Preferences.get(mContext.get()).setBackupRestored(true);
                Preferences.get(mContext.get()).setPreviousBackupExist(false);
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
        if (mContext.get() == null) return;
        if (mContext.get() instanceof Activity) {
            if (((Activity) mContext.get()).isFinishing()) return;
        }

        if (aBoolean) {
            Toast.makeText(mContext.get(), R.string.wallpapers_favorite_restored, Toast.LENGTH_LONG).show();
        }
    }
}
