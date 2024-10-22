package com.sumitkolhe.bitsplash.adapters;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatCheckBox;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.afollestad.materialdialogs.MaterialDialog;
import com.danimahardhika.android.helpers.core.ColorHelper;
import com.danimahardhika.android.helpers.core.DrawableHelper;
import com.danimahardhika.android.helpers.core.FileHelper;
import com.sumitkolhe.bitsplash.board.R;
import com.sumitkolhe.bitsplash.board.R2;
import com.sumitkolhe.bitsplash.fragments.dialogs.LanguagesFragment;
import com.sumitkolhe.bitsplash.helpers.TypefaceHelper;
import com.sumitkolhe.bitsplash.items.Setting;
import com.sumitkolhe.bitsplash.preferences.Preferences;
import com.danimahardhika.android.helpers.core.utils.LogUtil;

import java.io.File;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


public class SettingsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final Context mContext;
    private final List<Setting> mSettings;

    private static final int TYPE_CONTENT = 0;
    private static final int TYPE_FOOTER = 1;

    public SettingsAdapter(@NonNull Context context, @NonNull List<Setting> settings) {
        mContext = context;
        mSettings = settings;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_CONTENT) {
            View view = LayoutInflater.from(mContext).inflate(
                    R.layout.fragment_settings_item_list, parent, false);
            return new ContentViewHolder(view);
        }

        View view = LayoutInflater.from(mContext).inflate(
                R.layout.fragment_settings_item_footer, parent, false);
        return new FooterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder.getItemViewType() == TYPE_CONTENT) {
            ContentViewHolder contentViewHolder = (ContentViewHolder) holder;
            Setting setting = mSettings.get(position);

            if (setting.getTitle().length() == 0) {
                contentViewHolder.title.setVisibility(View.GONE);
                contentViewHolder.divider.setVisibility(View.GONE);
                contentViewHolder.container.setVisibility(View.VISIBLE);

                contentViewHolder.subtitle.setText(setting.getSubtitle());

                if (setting.getContent().length() == 0) {
                    contentViewHolder.content.setVisibility(View.GONE);
                } else {
                    contentViewHolder.content.setText(setting.getContent());
                    contentViewHolder.content.setVisibility(View.VISIBLE);
                }

                if (setting.getFooter().length() == 0) {
                    contentViewHolder.footer.setVisibility(View.GONE);
                } else {
                    contentViewHolder.footer.setText(setting.getFooter());
                }

                if (setting.getCheckState() >= 0) {
                    contentViewHolder.checkBox.setVisibility(View.VISIBLE);
                    contentViewHolder.checkBox.setChecked(setting.getCheckState() == 1);
                } else {
                    contentViewHolder.checkBox.setVisibility(View.GONE);
                }
            } else {
                contentViewHolder.container.setVisibility(View.GONE);
                contentViewHolder.title.setVisibility(View.VISIBLE);
                contentViewHolder.title.setText(setting.getTitle());

                if (position > 0) {
                    contentViewHolder.divider.setVisibility(View.VISIBLE);
                } else {
                    contentViewHolder.divider.setVisibility(View.GONE);
                }

                if (setting.getIcon() != -1) {
                    int color = ColorHelper.getAttributeColor(mContext, android.R.attr.textColorPrimary);
                    contentViewHolder.title.setCompoundDrawablesWithIntrinsicBounds(DrawableHelper.getTintedDrawable(
                            mContext, setting.getIcon(), color), null, null, null);
                }
            }
        }
    }

    @Override
    public int getItemCount() {
        return mSettings.size() + 1;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == (getItemCount() - 1)) return TYPE_FOOTER;
        return TYPE_CONTENT;
    }

    class ContentViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @BindView(R2.id.title)
        TextView title;
        @BindView(R2.id.subtitle)
        TextView subtitle;
        @BindView(R2.id.content)
        TextView content;
        @BindView(R2.id.footer)
        TextView footer;
        @BindView(R2.id.container)
        LinearLayout container;
        @BindView(R2.id.checkbox)
        AppCompatCheckBox checkBox;
        @BindView(R2.id.divider)
        View divider;

        ContentViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            container.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int id = view.getId();
            if (id == R.id.container) {
                int position = getAdapterPosition();

                if (position < 0 || position > mSettings.size()) return;

                Setting setting = mSettings.get(position);
                switch (setting.getType()) {
                    case CACHE:
                        new MaterialDialog.Builder(mContext)
                                .typeface(TypefaceHelper.getMedium(mContext), TypefaceHelper.getRegular(mContext))
                                .content(R.string.pref_data_cache_clear_dialog)
                                .positiveText(R.string.clear)
                                .negativeText(android.R.string.cancel)
                                .onPositive((dialog, which) -> {
                                    try {
                                        File cache = mContext.getCacheDir();
                                        FileHelper.clearDirectory(cache);

                                        double size = (double) FileHelper.getDirectorySize(
                                                mContext.getCacheDir()) / FileHelper.MB;
                                        NumberFormat formatter = new DecimalFormat("#0.00");

                                        setting.setFooter(String.format(mContext.getResources().getString(
                                                R.string.pref_data_cache_size),
                                                formatter.format(size) + " MB"));
                                        notifyItemChanged(position);

                                        Toast.makeText(mContext, R.string.pref_data_cache_cleared,
                                                Toast.LENGTH_LONG).show();
                                    } catch (Exception e) {
                                        LogUtil.e(Log.getStackTraceString(e));
                                    }
                                })
                                .show();
                        break;
                    case THEME:
                        Preferences.get(mContext).setDarkTheme(!checkBox.isChecked());
                        ((AppCompatActivity) mContext).recreate();
                        break;
                    case PREVIEW_QUALITY:
                        String[] strings = new String[] {
                                mContext.getResources().getString(R.string.pref_wallpaper_high_quality_preview_low),
                                mContext.getResources().getString(R.string.pref_wallpaper_high_quality_preview_high)
                        };

                        new MaterialDialog.Builder(mContext)
                                .typeface(TypefaceHelper.getMedium(mContext), TypefaceHelper.getRegular(mContext))
                                .title(R.string.pref_wallpaper_high_quality_preview)
                                .items((CharSequence[]) strings)
                                .itemsCallbackSingleChoice(Preferences.get(mContext).isHighQualityPreviewEnabled() ? 1 : 0,
                                        (dialog, itemView1, which, text) -> {
                                            Preferences.get(mContext).setHighQualityPreviewEnabled(which == 1);
                                            setting.setContent(strings[which]);
                                            notifyItemChanged(position);
                                            return true;
                                        })
                                .show();
                        break;
                    case LANGUAGE:
                        LanguagesFragment.showLanguageChooser(((AppCompatActivity) mContext).getSupportFragmentManager());
                        break;
                    case RESET_TUTORIAL:
                        Preferences.get(mContext).setTimeToShowWallpaperPreviewIntro(true);
                        Preferences.get(mContext).setShowWallpaperTooltip(true);

                        Toast.makeText(mContext, R.string.pref_others_reset_tutorial_reset, Toast.LENGTH_LONG).show();
                        break;
                    case APP_VERSION:

                    default:
                        break;
                }
            }
        }
    }

    class FooterViewHolder extends RecyclerView.ViewHolder {

        FooterViewHolder(View itemView) {
            super(itemView);
            if (!Preferences.get(mContext).isShadowEnabled()) {
                View shadow = itemView.findViewById(R.id.shadow);
                shadow.setVisibility(View.GONE);

                View root = shadow.getRootView();
                root.setPadding(0, 0, 0, 0);
            }
        }
    }
}
