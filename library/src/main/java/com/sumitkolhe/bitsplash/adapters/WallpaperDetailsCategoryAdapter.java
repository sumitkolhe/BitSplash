package com.sumitkolhe.bitsplash.adapters;

import android.animation.AnimatorInflater;
import android.animation.StateListAnimator;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.sumitkolhe.bitsplash.board.R;
import com.sumitkolhe.bitsplash.board.R2;
import com.sumitkolhe.bitsplash.activities.WallpaperBoardBrowserActivity;
import com.sumitkolhe.bitsplash.items.Category;
import com.sumitkolhe.bitsplash.utils.Extras;
import com.sumitkolhe.bitsplash.utils.ImageConfig;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.imageaware.ImageViewAware;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


public class WallpaperDetailsCategoryAdapter extends RecyclerView.Adapter<WallpaperDetailsCategoryAdapter.ViewHolder> {

    private final Context mContext;
    private List<Category> mCategories;
    private final DisplayImageOptions.Builder mOptions;

    public WallpaperDetailsCategoryAdapter(@NonNull Context context, @NonNull List<Category> categories) {
        mContext = context;
        mCategories = categories;

        mOptions = ImageConfig.getRawDefaultImageOptions();
        mOptions.resetViewBeforeLoading(true);
        mOptions.cacheInMemory(true);
        mOptions.cacheOnDisk(true);
        mOptions.displayer(new FadeInBitmapDisplayer(700));
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(
                R.layout.activity_wallpaper_preview_category_item_grid, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Category category = mCategories.get(position);
        holder.title.setText(category.getName());

        ImageLoader.getInstance().displayImage(
                category.getThumbUrl(),
                new ImageViewAware(holder.image),
                mOptions.build(),
                ImageConfig.getBigThumbnailSize(),
                null,
                null);
    }

    @Override
    public int getItemCount() {
        return mCategories.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @BindView(R2.id.image)
        ImageView image;
        @BindView(R2.id.title)
        TextView title;
        @BindView(R2.id.card)
        CardView card;

        ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
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
        }
    }
}
