package com.sumitkolhe.bitsplash.adapters;

import android.content.Context;
import android.graphics.Color;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.URLUtil;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.danimahardhika.android.helpers.core.ColorHelper;
import com.danimahardhika.android.helpers.core.DrawableHelper;
import com.sumitkolhe.bitsplash.board.R;
import com.sumitkolhe.bitsplash.board.R2;
import com.sumitkolhe.bitsplash.fragments.dialogs.CreditsFragment;
import com.sumitkolhe.bitsplash.fragments.dialogs.LicensesFragment;
import com.sumitkolhe.bitsplash.preferences.Preferences;
import com.sumitkolhe.bitsplash.utils.Extras;
import com.sumitkolhe.bitsplash.utils.ImageConfig;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.sufficientlysecure.htmltextview.HtmlTextView;

import butterknife.BindView;
import butterknife.ButterKnife;


public class AboutAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final Context mContext;

    private int mItemCount;

    private final boolean mShowContributors;

    private static final int TYPE_HEADER = 0;
    private static final int TYPE_CONTRIBUTORS = 1;
    private static final int TYPE_FOOTER = 2;
    private static final int TYPE_BOTTOM_SHADOW = 3;

    public AboutAdapter(@NonNull Context context, int spanCount) {
        mContext = context;

        mItemCount = 2;
        boolean cardMode = (spanCount > 1);
        if (!cardMode) {
            mItemCount += 1;
        }

        mShowContributors = mContext.getResources().getBoolean(R.bool.show_contributors_dialog);
        if (mShowContributors) {
            mItemCount += 1;
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_HEADER) {
            View view = LayoutInflater.from(mContext).inflate(
                    R.layout.fragment_about_item_header, parent, false);
            return new HeaderViewHolder(view);
        } else if (viewType == TYPE_CONTRIBUTORS) {
            View view = LayoutInflater.from(mContext).inflate(
                    R.layout.fragment_about_item_sub, parent, false);
            return new ContributorsViewHolder(view);
        } else if (viewType == TYPE_FOOTER) {
            View view = LayoutInflater.from(mContext).inflate(
                    R.layout.fragment_about_item_footer, parent, false);
            return new FooterViewHolder(view);
        }

        View view = LayoutInflater.from(mContext).inflate(
                R.layout.fragment_settings_item_footer, parent, false);
        return new ShadowViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder.getItemViewType() == TYPE_HEADER) {
            HeaderViewHolder headerViewHolder = (HeaderViewHolder) holder;
            String imageUri = mContext.getString(R.string.about_image);

            if (ColorHelper.isValidColor(imageUri)) {
                headerViewHolder.image.setBackgroundColor(Color.parseColor(imageUri));
            } else if (!URLUtil.isValidUrl(imageUri)) {
                imageUri = "drawable://" + DrawableHelper.getResourceId(mContext, imageUri);
                ImageLoader.getInstance().displayImage(imageUri, headerViewHolder.image,
                        ImageConfig.getDefaultImageOptions());
            } else {
                ImageLoader.getInstance().displayImage(imageUri, headerViewHolder.image,
                        ImageConfig.getDefaultImageOptions());
            }

            String profileUri = mContext.getResources().getString(R.string.about_profile_image);
            if (!URLUtil.isValidUrl(profileUri)) {
                profileUri = "drawable://" + DrawableHelper.getResourceId(mContext, profileUri);
            }

            ImageLoader.getInstance().displayImage(profileUri, headerViewHolder.profile,
                    ImageConfig.getDefaultImageOptions());
        }
    }

    @Override
    public int getItemCount() {
        return mItemCount;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) return TYPE_HEADER;
        if (position == 1) {
            if (mShowContributors) return TYPE_CONTRIBUTORS;
            else return TYPE_FOOTER;
        }

        if (position == 2 && mShowContributors)  return TYPE_FOOTER;
        return TYPE_BOTTOM_SHADOW;
    }

    class HeaderViewHolder extends RecyclerView.ViewHolder {

        @BindView(R2.id.image)
        ImageView image;
        @BindView(R2.id.profile)
        CircularImageView profile;
        @BindView(R2.id.subtitle)
        HtmlTextView subtitle;

        HeaderViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            RecyclerView recyclerView = itemView.findViewById(R.id.recyclerview);
            recyclerView.setItemAnimator(new DefaultItemAnimator());
            recyclerView.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, true));
            recyclerView.setHasFixedSize(true);

            String[] urls = mContext.getResources().getStringArray(R.array.about_social_links);
            if (urls.length == 0) {
                recyclerView.setVisibility(View.GONE);

                subtitle.setPadding(
                        subtitle.getPaddingLeft(),
                        subtitle.getPaddingTop(),
                        subtitle.getPaddingRight(),
                        subtitle.getPaddingBottom() + mContext.getResources().getDimensionPixelSize(R.dimen.content_margin));
            } else {
                if (recyclerView.getLayoutParams() instanceof LinearLayout.LayoutParams) {
                    LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) recyclerView.getLayoutParams();
                    if (urls.length < 7) {
                        params.width = LinearLayout.LayoutParams.WRAP_CONTENT;
                        params.gravity = Gravity.CENTER_HORIZONTAL;
                        recyclerView.setOverScrollMode(View.OVER_SCROLL_NEVER);
                    }
                }
                recyclerView.setAdapter(new AboutSocialAdapter(mContext, urls));
            }

            subtitle.setHtml(mContext.getResources().getString(R.string.about_desc));

            CardView card = itemView.findViewById(R.id.card);
            if (!Preferences.get(mContext).isShadowEnabled()) {
                if (card != null) card.setCardElevation(0);

                profile.setShadowRadius(0f);
                profile.setShadowColor(Color.TRANSPARENT);
            }
        }
    }

    class ContributorsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        ContributorsViewHolder(View itemView) {
            super(itemView);
            TextView title = itemView.findViewById(R.id.title);

            CardView card = itemView.findViewById(R.id.card);
            if (!Preferences.get(mContext).isShadowEnabled() && card != null) {
                card.setCardElevation(0);
            }

            int color = ColorHelper.getAttributeColor(mContext, android.R.attr.textColorPrimary);
            title.setCompoundDrawablesWithIntrinsicBounds(DrawableHelper.getTintedDrawable(
                    mContext, R.drawable.ic_toolbar_people, color), null, null, null);
            title.setText(mContext.getResources().getString(R.string.about_contributors_title));

            title.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            CreditsFragment.showCreditsDialog(((AppCompatActivity) mContext).getSupportFragmentManager(),
                    Extras.TYPE_CONTRIBUTORS);
        }
    }

    class FooterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {


        @BindView(R2.id.about_dashboard_licenses)
        TextView licenses;

        FooterViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            CardView card = itemView.findViewById(R.id.card);
            if (!Preferences.get(mContext).isShadowEnabled() && card != null) {
                card.setCardElevation(0);
            }

            int color = ColorHelper.getAttributeColor(mContext, android.R.attr.textColorPrimary);
            TextView title = itemView.findViewById(R.id.about_dashboard_title);
            title.setCompoundDrawablesWithIntrinsicBounds(DrawableHelper.getTintedDrawable(
                    mContext, R.drawable.ic_toolbar_dashboard, color), null, null, null);

            licenses.setOnClickListener(this);

        }

        @Override
        public void onClick(View view) {
            int id = view.getId();
            if (id == R.id.about_dashboard_licenses) {
                LicensesFragment.showLicensesDialog(((AppCompatActivity) mContext).getSupportFragmentManager());
                return;
            }

        }
    }

    class ShadowViewHolder extends RecyclerView.ViewHolder {

        ShadowViewHolder(View itemView) {
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
