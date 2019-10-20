package com.sumitkolhe.bitsplash.helpers;

import android.content.Context;
import android.os.Environment;
import androidx.annotation.NonNull;

import java.io.File;


public abstract class BackupHelper {

    public static final String NOMEDIA = ".nomedia";
    public static final String FILE_BACKUP = ".backup";
    private static final String DIRECTORY_BACKUP = ".wallpaperboard";

    public static File getDefaultDirectory(@NonNull Context context) {
        return new File(Environment.getExternalStorageDirectory(),
                DIRECTORY_BACKUP +"/"+ context.getPackageName());
    }
}
