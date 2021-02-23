package com.sumitkolhe.bitsplash.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.util.TypedValue;
import android.widget.TextView;

import com.danimahardhika.android.helpers.core.ColorHelper;
import com.sumitkolhe.bitsplash.board.R;
import com.sumitkolhe.bitsplash.activities.callbacks.SplashScreenCallback;
import com.sumitkolhe.bitsplash.activities.configurations.SplashScreenConfiguration;
import com.danimahardhika.android.helpers.core.utils.LogUtil;

import java.lang.ref.WeakReference;

import io.github.inflationx.viewpump.ViewPumpContextWrapper;

public abstract class WallpaperBoardSplashActivity extends AppCompatActivity implements SplashScreenCallback {

    private AsyncTask mAsyncTask;

    private SplashScreenConfiguration mConfig;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        mConfig = onInit();
        initBottomText();

        mAsyncTask = SplashScreenLoader.with(this)
                .mainActivity(mConfig.getMainActivity())
                .start();
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }

    @Override
    protected void onDestroy() {
        if (mAsyncTask != null) {
            mAsyncTask.cancel(true);
            mAsyncTask = null;
        }
        super.onDestroy();
    }

    private void initBottomText() {
        TextView splashTitle = findViewById(R.id.splash_title);
        if (splashTitle != null) {
            splashTitle.setText(mConfig.getBottomText());

            if (mConfig.getBottomTextColor() != -1) {
                splashTitle.setTextColor(mConfig.getBottomTextColor());
            } else {
                int color = ContextCompat.getColor(this, R.color.splashColor);
                splashTitle.setTextColor(ColorHelper.getBodyTextColor(color));
            }

            splashTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, mConfig.getBottomTextSize());
            splashTitle.setTypeface(mConfig.getBottomTextFont(this));
        }
    }

    private static class SplashScreenLoader extends AsyncTask<Void, Void, Boolean> {

        private final WeakReference<Context> context;
        private Class<?> mainActivity;

        private SplashScreenLoader(@NonNull Context context) {
            this.context = new WeakReference<>(context);
        }

        private SplashScreenLoader mainActivity(@NonNull Class<?> mainActivity) {
            this.mainActivity = mainActivity;
            return this;
        }

        private AsyncTask start() {
            return executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }

        private static SplashScreenLoader with(@NonNull Context context) {
            return new SplashScreenLoader(context);
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            while (!isCancelled()) {
                try {
                    Thread.sleep(500);
                    return true;
                } catch (Exception e) {
                    LogUtil.e(Log.getStackTraceString(e));
                    return false;
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            if (context.get() == null) return;
            if (context.get() instanceof Activity) {
                if (((Activity) context.get()).isFinishing()) return;
            }

            if (aBoolean) {
                Intent intent = new Intent(context.get(), mainActivity);
                intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

                if (context.get() instanceof Activity) {
                    Activity activity = (Activity) context.get();
                    activity.startActivity(intent);
                    activity.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                    activity.finish();
                }
            }
        }
    }
}
