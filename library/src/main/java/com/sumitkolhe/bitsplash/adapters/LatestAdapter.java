package com.sumitkolhe.bitsplash.adapters;

import android.animation.AnimatorInflater;
import android.animation.StateListAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Build;
import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.interpolator.view.animation.LinearOutSlowInInterpolator;
import androidx.appcompat.app.AppCompatActivity;
import androidx.palette.graphics.Palette;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.danimahardhika.android.helpers.animation.AnimationHelper;
import com.danimahardhika.android.helpers.core.ColorHelper;
import com.danimahardhika.android.helpers.core.DrawableHelper;
import com.danimahardhika.android.helpers.core.WindowHelper;
import com.danimahardhika.android.helpers.permission.PermissionHelper;
import com.danimahardhika.cafebar.CafeBar;
import com.danimahardhika.cafebar.CafeBarTheme;
import com.sumitkolhe.bitsplash.board.R;
import com.sumitkolhe.bitsplash.board.R2;
import com.sumitkolhe.bitsplash.activities.WallpaperBoardPreviewActivity;
import com.sumitkolhe.bitsplash.applications.WallpaperBoardApplication;
import com.sumitkolhe.bitsplash.databases.Database;
import com.sumitkolhe.bitsplash.helpers.TypefaceHelper;
import com.sumitkolhe.bitsplash.items.PopupItem;
import com.sumitkolhe.bitsplash.items.Wallpaper;
import com.sumitkolhe.bitsplash.preferences.Preferences;
import com.sumitkolhe.bitsplash.tasks.WallpaperApplyTask;
import com.sumitkolhe.bitsplash.utils.Popup;
import com.sumitkolhe.bitsplash.utils.Extras;
import com.sumitkolhe.bitsplash.utils.ImageConfig;
import com.sumitkolhe.bitsplash.utils.WallpaperDownloader;
import com.kogitune.activitytransition.ActivityTransitionLauncher;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageSize;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.imageaware.ImageViewAware;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.sumitkolhe.bitsplash.helpers.ViewHelper.setCardViewToFlat;

public class LatestAdapter extends RecyclerView.Adapter<LatestAdapter.ViewHolder> {

    private final Context mContext;
    private List<Wallpaper> mWallpapers;
    private final DisplayImageOptions.Builder mOptions;

    public LatestAdapter(@NonNull Context context, @NonNull List<Wallpaper> wallpapers) {
        mContext = context;
        mWallpapers = wallpapers;
        WallpaperBoardApplication.sIsClickable = true;

        mOptions = ImageConfig.getRawDefaultImageOptions();
        mOptions.resetViewBeforeLoading(true);
        mOptions.cacheInMemory(true);
        mOptions.cacheOnDisk(true);
        mOptions.displayer(new FadeInBitmapDisplayer(700));
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(
                R.layout.fragment_latest_item_grid, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Wallpaper wallpaper = mWallpapers.get(position);
        holder.name.setText(wallpaper.getName());
        holder.name.setVisibility(View.GONE);   //remove this line to show wallpaper name in latest wallpapers.
        holder.author.setText(wallpaper.getAuthor());
        holder.author.setVisibility(View.GONE); //remove this line to show author name in latest wallpapers.

        if (mContext.getResources().getBoolean(R.bool.enable_wallpaper_download)) {
            holder.download.setVisibility(View.VISIBLE);
        } else {
            holder.download.setVisibility(View.GONE);
        }

        setFavorite(holder.favorite, Color.WHITE, position, false);
        resetImageViewHeight(holder.image, wallpaper.getDimensions());

        ImageLoader.getInstance().displayImage(
                wallpaper.getThumbUrl(),
                new ImageViewAware(holder.image),
                mOptions.build(),
                ImageConfig.getBigThumbnailSize(),
                new SimpleImageLoadingListener() {

                    @Override
                    public void onLoadingStarted(String imageUri, View view) {
                        super.onLoadingStarted(imageUri, view);
                        int color;
                        if (wallpaper.getColor() == 0) {
                            color = ColorHelper.getAttributeColor(
                                    mContext, R.attr.card_background);
                        } else {
                            color = wallpaper.getColor();
                        }

                        holder.card.setCardBackgroundColor(color);
                    }

                    @Override
                    public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                        super.onLoadingComplete(imageUri, view, loadedImage);
                        if (loadedImage != null && wallpaper.getColor() == 0) {
                            Palette.from(loadedImage).generate(palette -> {
                                if (mContext == null) return;
                                if (((Activity) mContext).isFinishing()) return;

                                int vibrant = ColorHelper.getAttributeColor(
                                        mContext, R.attr.card_background);
                                int color = palette.getVibrantColor(vibrant);
                                if (color == vibrant)
                                    color = palette.getMutedColor(vibrant);
                                holder.card.setCardBackgroundColor(color);

                                wallpaper.setColor(color);
                                Database.get(mContext).updateWallpaper(wallpaper);
                            });
                        }
                    }
                },
                null);
    }

    @Override
    public int getItemCount() {
        return mWallpapers.size();
    }

    private void resetImageViewHeight(@NonNull ImageView imageView, ImageSize imageSize) {
        if (imageSize == null) imageSize = new ImageSize(400, 300);

        int width = WindowHelper.getScreenSize(mContext).x;
        int spanCount = mContext.getResources().getInteger(R.integer.latest_wallpapers_column_count);
        if (spanCount > 1) {
            width = width/spanCount;
        }
        double scaleFactor = (double) width / (double) imageSize.getWidth();
        double measuredHeight = (double) imageSize.getHeight() * scaleFactor;
        imageView.getLayoutParams().height = Double.valueOf(measuredHeight).intValue();
        imageView.requestLayout();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @BindView(R2.id.image)
        ImageView image;
        @BindView(R2.id.name)
        TextView name;
        @BindView(R2.id.author)
        TextView author;
        @BindView(R2.id.favorite)
        ImageView favorite;
        @BindView(R2.id.download)
        ImageView download;
        @BindView(R2.id.apply)
        ImageView apply;
        @BindView(R2.id.card)
        CardView card;

        ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            if (mContext.getResources().getInteger(R.integer.latest_wallpapers_column_count) == 1) {
                if (card.getLayoutParams() instanceof StaggeredGridLayoutManager.LayoutParams) {
                    StaggeredGridLayoutManager.LayoutParams params =
                            (StaggeredGridLayoutManager.LayoutParams) card.getLayoutParams();
                    params.leftMargin = 0;
                    params.rightMargin = 0;
                    params.topMargin = 0;
                    params.bottomMargin = 0;

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                        params.setMarginEnd(0);
                    }
                }
            } else {
                setCardViewToFlat(card);
            }

            if (!Preferences.get(mContext).isShadowEnabled()) {
                card.setCardElevation(0f);
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                StateListAnimator stateListAnimator = AnimatorInflater
                        .loadStateListAnimator(mContext, R.animator.card_lift_long);
                card.setStateListAnimator(stateListAnimator);
            }

            if (mContext.getResources().getBoolean(R.bool.enable_wallpaper_download)) {
                download.setImageDrawable(DrawableHelper.getTintedDrawable(
                        mContext, R.drawable.ic_toolbar_download, Color.WHITE));
                download.setOnClickListener(this);
            }

            apply.setImageDrawable(DrawableHelper.getTintedDrawable(
                    mContext, R.drawable.ic_toolbar_apply_options, Color.WHITE));

            card.setOnClickListener(this);
            favorite.setOnClickListener(this);
            apply.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int id = view.getId();
            int position = getAdapterPosition();
            if (position < 0 || position > mWallpapers.size()) return;

            if (id == R.id.favorite) {
                boolean isFavorite = mWallpapers.get(position).isFavorite();
                Database.get(mContext).favoriteWallpaper(
                        mWallpapers.get(position).getUrl(), !isFavorite);

                mWallpapers.get(position).setFavorite(!isFavorite);
                setFavorite(favorite, name.getCurrentTextColor(), position, true);

                CafeBar.builder(mContext)
                        .theme(Preferences.get(mContext).isDarkTheme() ? CafeBarTheme.LIGHT : CafeBarTheme.DARK)
                        .fitSystemWindow()
                        .floating(true)
                        .typeface(TypefaceHelper.getRegular(mContext), TypefaceHelper.getBold(mContext))
                        .content(String.format(
                                mContext.getResources().getString(mWallpapers.get(position).isFavorite() ?
                                        R.string.wallpaper_favorite_added : R.string.wallpaper_favorite_removed),
                                mWallpapers.get(position).getName()))
                        .icon(mWallpapers.get(position).isFavorite() ?
                                R.drawable.ic_toolbar_love : R.drawable.ic_toolbar_unlove)
                        .show();
            } else if (id == R.id.download) {
                if (PermissionHelper.isStorageGranted(mContext)) {
                    WallpaperDownloader.prepare(mContext)
                            .wallpaper(mWallpapers.get(position))
                            .start();
                    return;
                }

                PermissionHelper.requestStorage(mContext);
            } else if (id == R.id.apply) {
                Popup popup = Popup.Builder(mContext)
                        .to(apply)
                        .list(PopupItem.getApplyItems(mContext))
                        .callback((applyPopup, i) -> {
                            PopupItem item = applyPopup.getItems().get(i);
                            if (item.getType() == PopupItem.Type.WALLPAPER_CROP) {
                                Preferences.get(mContext).setCropWallpaper(!item.getCheckboxValue());
                                item.setCheckboxValue(Preferences.get(mContext).isCropWallpaper());

                                applyPopup.updateItem(i, item);
                                return;
                            } else if (item.getType() == PopupItem.Type.LOCKSCREEN) {
                                WallpaperApplyTask.prepare(mContext)
                                        .wallpaper(mWallpapers.get(position))
                                        .to(WallpaperApplyTask.Apply.LOCKSCREEN)
                                        .start(AsyncTask.THREAD_POOL_EXECUTOR);
                            } else if (item.getType() == PopupItem.Type.HOMESCREEN) {
                                WallpaperApplyTask.prepare(mContext)
                                        .wallpaper(mWallpapers.get(position))
                                        .to(WallpaperApplyTask.Apply.HOMESCREEN)
                                        .start(AsyncTask.THREAD_POOL_EXECUTOR);
                            }else if (item.getType() == PopupItem.Type.HOMESCREEN_LOCKSCREEN) {
                                WallpaperApplyTask.prepare(mContext)
                                        .wallpaper(mWallpapers.get(position))
                                        .to(WallpaperApplyTask.Apply.HOMESCREEN_LOCKSCREEN)
                                        .start(AsyncTask.THREAD_POOL_EXECUTOR);
                            }
                            applyPopup.dismiss();
                        })
                        .build();

                if (mContext.getResources().getBoolean(R.bool.enable_wallpaper_download)) {
                    popup.removeItem(popup.getItems().size() - 1);
                }

                popup.show();
            } else if (id == R.id.card) {
                if (WallpaperBoardApplication.sIsClickable) {
                    WallpaperBoardApplication.sIsClickable = false;
                    try {
                        Bitmap bitmap = null;
                        if (image.getDrawable() != null) {
                            bitmap = ((BitmapDrawable) image.getDrawable()).getBitmap();
                        }

                        final Intent intent = new Intent(mContext, WallpaperBoardPreviewActivity.class);
                        intent.putExtra(Extras.EXTRA_URL, mWallpapers.get(position).getUrl());
                        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

                        ActivityTransitionLauncher.with((AppCompatActivity) mContext)
                                .from(image, Extras.EXTRA_IMAGE)
                                .image(bitmap)
                                .launch(intent);
                    } catch (Exception e) {
                        WallpaperBoardApplication.sIsClickable = true;
                    }
                }
            }
        }
    }
    public void setWallpapers(@NonNull List<Wallpaper> wallpapers) {
        mWallpapers = wallpapers;
        notifyDataSetChanged();
    }


    private void setFavorite(@NonNull ImageView imageView, @ColorInt int color, int position, boolean animate) {
        if (position < 0 || position > mWallpapers.size()) return;

        boolean isFavorite = mWallpapers.get(position).isFavorite();

        if (animate) {
            AnimationHelper.show(imageView)
                    .interpolator(new LinearOutSlowInInterpolator())
                    .callback(new AnimationHelper.Callback() {
                        @Override
                        public void onAnimationStart() {
                            imageView.setImageDrawable(DrawableHelper.getTintedDrawable(mContext,
                                    isFavorite ? R.drawable.ic_toolbar_love : R.drawable.ic_toolbar_unlove, color));
                        }

                        @Override
                        public void onAnimationEnd() {

                        }
                    })
                    .start();
            return;
        }

        imageView.setImageDrawable(DrawableHelper.getTintedDrawable(mContext,
                isFavorite ? R.drawable.ic_toolbar_love : R.drawable.ic_toolbar_unlove, color));
    }
}
