package com.sumitkolhe.bitsplash.adapters;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.drawable.Drawable;
import android.os.Build;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.danimahardhika.android.helpers.core.ColorHelper;
import com.danimahardhika.android.helpers.core.DrawableHelper;
import com.danimahardhika.android.helpers.core.ViewHelper;
import com.danimahardhika.cafebar.CafeBar;
import com.danimahardhika.cafebar.CafeBarTheme;
import com.sumitkolhe.bitsplash.board.R;
import com.sumitkolhe.bitsplash.board.R2;
import com.sumitkolhe.bitsplash.helpers.TypefaceHelper;
import com.sumitkolhe.bitsplash.items.Category;
import com.sumitkolhe.bitsplash.items.ColorPalette;
import com.sumitkolhe.bitsplash.items.WallpaperProperty;
import com.sumitkolhe.bitsplash.preferences.Preferences;
import com.danimahardhika.android.helpers.core.utils.LogUtil;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;



public class WallpaperDetailsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<WallpaperProperty> mProperties;
    private ColorPalette mPalette;
    private List<Category> mCategories;
    private final Context mContext;

    private static final int TYPE_DETAILS = 0;
    private static final int TYPE_PALETTE_HEADER = 1;
    private static final int TYPE_PALETTE = 2;
    private static final int TYPE_CATEGORY = 3;

    public WallpaperDetailsAdapter(@NonNull Context context,
                                   @NonNull List<WallpaperProperty> properties,
                                   @NonNull ColorPalette palette,
                                   @NonNull List<Category> categories) {
        mContext = context;
        mProperties = properties;
        mPalette = palette;
        mCategories = categories;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_DETAILS) {
            View view = LayoutInflater.from(mContext).inflate(
                    R.layout.activity_wallpaper_preview_details, parent, false);
            return new PropertyViewHolder(view);
        } else if (viewType == TYPE_PALETTE_HEADER) {
            View view = LayoutInflater.from(mContext).inflate(
                    R.layout.activity_wallpaper_preview_palette_header, parent, false);
            return new PaletteHeaderViewHolder(view);
        } else if (viewType == TYPE_PALETTE) {
            View view = LayoutInflater.from(mContext).inflate(
                    R.layout.activity_wallpaper_preview_palette, parent, false);
            return new PaletteViewHolder(view);
        }

        View view = LayoutInflater.from(mContext).inflate(
                R.layout.activity_wallpaper_preview_category, parent, false);
        return new CategoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        try {
            if (holder.itemView != null) {
                StaggeredGridLayoutManager.LayoutParams layoutParams = (StaggeredGridLayoutManager.LayoutParams)
                        holder.itemView.getLayoutParams();
                layoutParams.setFullSpan(getItemViewType(position) == TYPE_PALETTE_HEADER ||
                        getItemViewType(position) == TYPE_CATEGORY);
            }
        } catch (Exception e) {
            LogUtil.e(Log.getStackTraceString(e));
        }

        if (holder.getItemViewType() == TYPE_DETAILS) {
            PropertyViewHolder propertyViewHolder = (PropertyViewHolder) holder;

            WallpaperProperty property = mProperties.get(position);
            propertyViewHolder.title.setText(property.getTitle());
            if (property.getIcon() != 0) {
                int color = ColorHelper.getAttributeColor(mContext, android.R.attr.textColorSecondary);
                Drawable drawable = DrawableHelper.getTintedDrawable(mContext, property.getIcon(), color);
                propertyViewHolder.title.setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null);
            }
        } else if (holder.getItemViewType() == TYPE_PALETTE_HEADER) {
            PaletteHeaderViewHolder paletteHeaderViewHolder = (PaletteHeaderViewHolder) holder;

            paletteHeaderViewHolder.container.setVisibility(View.VISIBLE);
            if (mPalette.size() <= 0) {
                paletteHeaderViewHolder.container.setVisibility(View.GONE);
            }
        } else if (holder.getItemViewType() == TYPE_PALETTE) {
            PaletteViewHolder paletteViewHolder = (PaletteViewHolder) holder;

            int finalPosition = position - mProperties.size() - 1;
            paletteViewHolder.title.setText(mPalette.getHex(finalPosition));

            Drawable drawable = DrawableHelper.getTintedDrawable(mContext,
                    R.drawable.ic_toolbar_details_palette_color, mPalette.get(finalPosition));
            paletteViewHolder.title.setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null);
        } else if (holder.getItemViewType() == TYPE_CATEGORY) {
            CategoryViewHolder categoryViewHolder = (CategoryViewHolder) holder;

            categoryViewHolder.container.setVisibility(View.VISIBLE);
            if (mCategories.size() == 0) {
                categoryViewHolder.container.setVisibility(View.GONE);
                return;
            }

            categoryViewHolder.recyclerView.setAdapter(new WallpaperDetailsCategoryAdapter(mContext, mCategories));
            int spanCount = mContext.getResources().getInteger(R.integer.wallpaper_details_column_count);
            ViewHelper.resetSpanCount(categoryViewHolder.recyclerView, spanCount);
        }
    }

    @Override
    public int getItemCount() {
        return mProperties.size() + mPalette.size() + 2;
    }

    @Override
    public int getItemViewType(int position) {
        if (position < mProperties.size()) {
            return TYPE_DETAILS;
        } else if (position == mProperties.size()) {
            return TYPE_PALETTE_HEADER;
        } else if (position > mProperties.size() && position < (getItemCount() - 1)) {
            return TYPE_PALETTE;
        }
        return TYPE_CATEGORY;
    }

    class PropertyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @BindView(R2.id.title)
        TextView title;

        PropertyViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            title.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int position = getAdapterPosition();
            if (position < 0 || position > mProperties.size()) return;

            showCafeBar(mProperties.get(position).getDesc(), "", false);
        }
    }

    class PaletteHeaderViewHolder extends RecyclerView.ViewHolder {

        @BindView(R2.id.container)
        LinearLayout container;

        PaletteHeaderViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            TextView title = itemView.findViewById(R.id.title);
            int color = ColorHelper.getAttributeColor(mContext, android.R.attr.textColorPrimary);
            Drawable drawable = DrawableHelper.getTintedDrawable(mContext,
                    R.drawable.ic_toolbar_details_palette, color);
            title.setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null);
        }
    }

    class PaletteViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @BindView(R2.id.title)
        TextView title;

        PaletteViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            title.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int position = getAdapterPosition() - mProperties.size() - 1;
            if (position < 0 || position > mPalette.size()) return;

            String content = mContext.getResources().getString(R.string.wallpaper_property_color,
                    mPalette.getHex(position));
            showCafeBar(content, mPalette.getHex(position), true);
        }
    }

    class CategoryViewHolder extends RecyclerView.ViewHolder {

        @BindView(R2.id.title)
        TextView title;
        @BindView(R2.id.recyclerview)
        RecyclerView recyclerView;
        @BindView(R2.id.container)
        LinearLayout container;

        CategoryViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            int color = ColorHelper.getAttributeColor(mContext, android.R.attr.textColorPrimary);
            Drawable drawable = DrawableHelper.getTintedDrawable(mContext,
                    R.drawable.ic_toolbar_details_category, color);
            title.setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null);

            recyclerView.setItemAnimator(new DefaultItemAnimator());
            recyclerView.setLayoutManager(new GridLayoutManager(mContext,
                    mContext.getResources().getInteger(R.integer.wallpaper_details_column_count)));
            recyclerView.setNestedScrollingEnabled(false);
        }
    }

    private void showCafeBar(String title, String content, boolean showCopy) {
        CafeBar.Builder builder = CafeBar.builder(mContext)
                .theme(Preferences.get(mContext).isDarkTheme() ? CafeBarTheme.LIGHT : CafeBarTheme.DARK)
                .typeface(TypefaceHelper.getRegular(mContext), TypefaceHelper.getBold(mContext))
                .content(title)
                .floating(true);

        if (showCopy) {
            builder.neutralText(R.string.copy)
                    .onNeutral(cafeBar -> {
                        ClipboardManager clipboard = (ClipboardManager) mContext.getSystemService(Context.CLIPBOARD_SERVICE);
                        ClipData clip = ClipData.newPlainText("content", content);
                        clipboard.setPrimaryClip(clip);
                    });
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            boolean tabletMode = mContext.getResources().getBoolean(R.bool.android_helpers_tablet_mode);
            if (!tabletMode && mContext.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
               builder.fitSystemWindow();
            }
        }

        CafeBar cafeBar = builder.build();
        cafeBar.show();
    }

    public void setWallpaperProperties(@NonNull List<WallpaperProperty> properties) {
        mProperties = properties;
        notifyDataSetChanged();
    }

    public void setColorPalette(@NonNull ColorPalette palette) {
        mPalette = palette;
        notifyDataSetChanged();
    }

    public void setCategories(@NonNull List<Category> categories) {
        mCategories = categories;
        notifyDataSetChanged();
    }
}
