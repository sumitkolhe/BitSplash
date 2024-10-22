package com.sumitkolhe.bitsplash.helpers;

import android.content.Context;
import android.content.res.Configuration;
import android.os.Build;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import android.view.View;

import com.danimahardhika.android.helpers.core.ContextHelper;
import com.sumitkolhe.bitsplash.board.R;
import com.sumitkolhe.bitsplash.applications.WallpaperBoardApplication;
import com.sumitkolhe.bitsplash.applications.WallpaperBoardConfiguration;

import static com.danimahardhika.android.helpers.core.ViewHelper.getToolbarHeight;
import static com.danimahardhika.android.helpers.core.WindowHelper.getStatusBarHeight;
import static com.danimahardhika.android.helpers.core.WindowHelper.getNavigationBarHeight;



public class ViewHelper {

    public static void setCardViewToFlat(@Nullable CardView cardView) {
        if (cardView == null) return;

        if (WallpaperBoardApplication.getConfig().getWallpapersGrid() ==
                WallpaperBoardConfiguration.GridStyle.FLAT) {
            cardView.setRadius(0f);
            cardView.setUseCompatPadding(false);

            Context context = ContextHelper.getBaseContext(cardView);
            int margin = context.getResources().getDimensionPixelSize(R.dimen.card_margin);

            if (cardView.getLayoutParams() instanceof GridLayoutManager.LayoutParams) {
                GridLayoutManager.LayoutParams params = (GridLayoutManager.LayoutParams) cardView.getLayoutParams();
                params.setMargins(0, 0, margin, margin);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                    params.setMarginEnd(margin);
                }
            } else if (cardView.getLayoutParams() instanceof StaggeredGridLayoutManager.LayoutParams) {
                StaggeredGridLayoutManager.LayoutParams params =
                        (StaggeredGridLayoutManager.LayoutParams) cardView.getLayoutParams();
                params.setMargins(0, 0, margin, margin);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                    params.setMarginEnd(margin);
                }
            }
        }
    }

    public static void resetViewBottomPadding(@Nullable View view, boolean scroll) {
        if (view == null) return;

        Context context = ContextHelper.getBaseContext(view);
        int orientation = context.getResources().getConfiguration().orientation;

        int left = view.getPaddingLeft();
        int right = view.getPaddingRight();
        int bottom = view.getPaddingTop();
        int top = view.getPaddingTop();
        int navBar = 0;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            boolean tabletMode = context.getResources().getBoolean(R.bool.android_helpers_tablet_mode);
            if (tabletMode || orientation == Configuration.ORIENTATION_PORTRAIT) {
                navBar = getNavigationBarHeight(context);
            }

            if (!scroll) {
                navBar += getStatusBarHeight(context);
            }
        }

        if (!scroll) {
            navBar += getToolbarHeight(context);
        }
        view.setPadding(left, top, right, (bottom + navBar));
    }
}
