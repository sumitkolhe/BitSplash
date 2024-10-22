package com.sumitkolhe.bitsplash.helpers;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Handler;
import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.danimahardhika.android.helpers.core.ColorHelper;
import com.sumitkolhe.bitsplash.board.R;
import com.sumitkolhe.bitsplash.preferences.Preferences;
import com.danimahardhika.android.helpers.core.utils.LogUtil;
import com.getkeepsafe.taptargetview.TapTarget;
import com.getkeepsafe.taptargetview.TapTargetSequence;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;



public class TapIntroHelper {

    public static void showWallpaperPreviewIntro(@NonNull Context context, @ColorInt int color) {
        if (Preferences.get(context).isTimeToShowWallpaperPreviewIntro()) {
            AppCompatActivity activity = (AppCompatActivity) context;

            RelativeLayout rootView = activity.findViewById(R.id.bottom_panel);
            if (rootView == null) return;

            new Handler().postDelayed(() -> {
                try {
                    int baseColor = color;
                    if (baseColor == 0) {
                        baseColor = ColorHelper.getAttributeColor(context, R.attr.colorAccent);
                    }

                    int primary = ColorHelper.getTitleTextColor(baseColor);
                    int secondary = ColorHelper.setColorAlpha(primary, 0.7f);

                    TapTargetSequence tapTargetSequence = new TapTargetSequence(activity);
                    tapTargetSequence.continueOnCancel(true);

                    Typeface title = TypefaceHelper.getMedium(context);
                    //Todo:
                    //Typeface description = TypefaceHelper.getRegular(context);

                    ImageView apply = rootView.findViewById(R.id.menu_apply);
                    ImageView save = rootView.findViewById(R.id.menu_save);
                    ImageView preview = rootView.findViewById(R.id.menu_preview);

                    TapTarget tapTarget = TapTarget.forView(apply,
                            context.getResources().getString(R.string.tap_intro_wallpaper_preview_apply),
                            context.getResources().getString(R.string.tap_intro_wallpaper_preview_apply_desc))
                            .titleTextColorInt(primary)
                            .descriptionTextColorInt(secondary)
                            .targetCircleColorInt(primary)
                            .outerCircleColorInt(baseColor)
                            .drawShadow(true);

                    TapTarget tapTarget1 = TapTarget.forView(save,
                            context.getResources().getString(R.string.tap_intro_wallpaper_preview_save),
                            context.getResources().getString(R.string.tap_intro_wallpaper_preview_save_desc))
                            .titleTextColorInt(primary)
                            .descriptionTextColorInt(secondary)
                            .targetCircleColorInt(primary)
                            .outerCircleColorInt(baseColor)
                            .drawShadow(true);

                    TapTarget tapTarget2 = TapTarget.forView(preview,
                            context.getResources().getString(R.string.tap_intro_wallpaper_preview_full),
                            context.getResources().getString(R.string.tap_intro_wallpaper_preview_full_desc))
                            .titleTextColorInt(primary)
                            .descriptionTextColorInt(secondary)
                            .targetCircleColorInt(primary)
                            .outerCircleColorInt(baseColor)
                            .drawShadow(true);

                    if (title != null) {
                        //Todo:
                        //tapTarget.titleTypeface(title);
                        //tapTarget1.titleTypeface(title);
                        //tapTarget2.titleTypeface(title);
                        tapTarget.textTypeface(title);
                        tapTarget1.textTypeface(title);
                        tapTarget2.textTypeface(title);
                    }

                    //if (description != null) {
                        //Todo:
                        //tapTarget.descriptionTypeface(description);
                        //tapTarget1.descriptionTypeface(description);
                        //tapTarget2.descriptionTypeface(description);
                    //}

                    tapTargetSequence.target(tapTarget);
                    if (context.getResources().getBoolean(R.bool.enable_wallpaper_download)) {
                        tapTargetSequence.target(tapTarget1);
                    }
                    tapTargetSequence.target(tapTarget2);

                    tapTargetSequence.listener(new TapTargetSequence.Listener() {
                        @Override
                        public void onSequenceFinish() {
                            Preferences.get(context).setTimeToShowWallpaperPreviewIntro(false);

                            SlidingUpPanelLayout panelLayout = activity.findViewById(R.id.sliding_layout);
                            if (panelLayout != null) {
                                new Handler().postDelayed(() -> panelLayout.setPanelState(
                                        SlidingUpPanelLayout.PanelState.EXPANDED), 300);
                            }
                        }

                        @Override
                        public void onSequenceStep(TapTarget tapTarget, boolean b) {

                        }

                        @Override
                        public void onSequenceCanceled(TapTarget tapTarget) {

                        }
                    });
                    tapTargetSequence.start();
                } catch (Exception e) {
                    LogUtil.e(Log.getStackTraceString(e));
                }
            }, 100);
        }
    }
}
