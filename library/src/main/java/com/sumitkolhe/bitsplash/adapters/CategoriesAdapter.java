package com.sumitkolhe.bitsplash.adapters;

import android.animation.AnimatorInflater;
import android.animation.StateListAnimator;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import androidx.annotation.NonNull;
import androidx.palette.graphics.Palette;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.danimahardhika.android.helpers.core.ColorHelper;
import com.sumitkolhe.bitsplash.board.R;
import com.sumitkolhe.bitsplash.board.R2;
import com.sumitkolhe.bitsplash.activities.WallpaperBoardBrowserActivity;
import com.sumitkolhe.bitsplash.items.Category;
import com.sumitkolhe.bitsplash.preferences.Preferences;
import com.sumitkolhe.bitsplash.utils.Extras;
import com.sumitkolhe.bitsplash.utils.ImageConfig;
import com.sumitkolhe.bitsplash.utils.views.HeaderView;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.imageaware.ImageViewAware;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.sumitkolhe.bitsplash.helpers.ViewHelper.setCardViewToFlat;

public class CategoriesAdapter extends RecyclerView.Adapter<CategoriesAdapter.ViewHolder> {

    private final Context mContext;
    private List<Category> mCategories;
    private final DisplayImageOptions.Builder mOptions;

    public CategoriesAdapter(@NonNull Context context, @NonNull List<Category> categories) {
        mContext = context;
        mCategories = categories;

        mOptions = ImageConfig.getRawDefaultImageOptions();
        mOptions.resetViewBeforeLoading(true);
        mOptions.cacheInMemory(true);
        mOptions.cacheOnDisk(true);

        mOptions.displayer(new FadeInBitmapDisplayer(700));
    }

    @Override
    public CategoriesAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(
                R.layout.fragment_categories_item_grid, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(CategoriesAdapter.ViewHolder holder, int position) {
        Category category = mCategories.get(position);
        holder.name.setText(category.getName());
        String count = category.getCount() +" "+
                mContext.getResources().getString(R.string.navigation_view_wallpapers);
        holder.count.setText(count);

        ImageLoader.getInstance().displayImage(
                category.getThumbUrl(),
                new ImageViewAware(holder.image),
                mOptions.build(),
                ImageConfig.getBigThumbnailSize(),
                new SimpleImageLoadingListener() {

                    @Override
                    public void onLoadingStarted(String imageUri, View view) {
                        super.onLoadingStarted(imageUri, view);
                        int color;
                        if (category.getColor() == 0) {
                            color = ColorHelper.getAttributeColor(
                                    mContext, R.attr.card_background);
                        } else {
                            color = category.getColor();
                        }

                        holder.card.setCardBackgroundColor(color);
                    }

                    @Override
                    public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                        super.onLoadingComplete(imageUri, view, loadedImage);
                        if (loadedImage != null && category.getColor() == 0) {
                            try {
                                Palette.from(loadedImage).generate(palette -> {
                                    int vibrant = ColorHelper.getAttributeColor(
                                            mContext, R.attr.card_background);
                                    int color = palette.getVibrantColor(vibrant);
                                    if (color == vibrant)
                                        color = palette.getMutedColor(vibrant);
                                    holder.card.setCardBackgroundColor(color);

                                    category.setColor(color);
                                });
                            } catch (Exception ignored) {}
                        }
                    }
                },
                null);
    }

    @Override
    public int getItemCount() {
        return mCategories.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @BindView(R2.id.image)
        HeaderView image;
        @BindView(R2.id.name)
        TextView name;
        @BindView(R2.id.count)
        TextView count;
        @BindView(R2.id.card)
        CardView card;

        ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            if (mContext.getResources().getInteger(R.integer.categories_column_count) == 1) {
                if (card.getLayoutParams() instanceof GridLayoutManager.LayoutParams) {
                    GridLayoutManager.LayoutParams params =
                            (GridLayoutManager.LayoutParams) card.getLayoutParams();
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

            card.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int position = getAdapterPosition();
            if (position < 0 || position > mCategories.size()) return;

            Intent intent = new Intent(mContext, WallpaperBoardBrowserActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.putExtra(Extras.EXTRA_FRAGMENT_ID, Extras.ID_CATEGORY_WALLPAPERS);
            intent.putExtra(Extras.EXTRA_CATEGORY, mCategories.get(position).getName());
            intent.putExtra(Extras.EXTRA_COUNT, mCategories.get(position).getCount());

            mContext.startActivity(intent);
            //((AppCompatActivity) mContext).overridePendingTransition(
                    //android.R.anim.fade_in, android.R.anim.fade_out);
        }
    }

    public void clear() {
        mCategories.clear();
        notifyDataSetChanged();
    }

    public void add(@NonNull Category category) {
        mCategories.add(category);
        notifyItemInserted(mCategories.size() - 1);
    }
}
