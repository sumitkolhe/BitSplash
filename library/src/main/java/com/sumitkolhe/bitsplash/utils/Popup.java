package com.sumitkolhe.bitsplash.utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatCheckBox;
import androidx.appcompat.widget.ListPopupWindow;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.danimahardhika.android.helpers.core.ColorHelper;
import com.danimahardhika.android.helpers.core.DrawableHelper;
import com.danimahardhika.android.helpers.core.utils.LogUtil;
import com.sumitkolhe.bitsplash.board.R;
import com.sumitkolhe.bitsplash.board.R2;
import com.sumitkolhe.bitsplash.helpers.TypefaceHelper;
import com.sumitkolhe.bitsplash.items.PopupItem;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;



public class Popup {

    private ListPopupWindow mPopupWindow;
    private PopupAdapter mAdapter;

    private Popup(Builder builder) {
        mPopupWindow = new ListPopupWindow(builder.mContext);
        mAdapter = new PopupAdapter(builder.mContext, builder.mItems);


        int width = getMeasuredWidth(builder.mContext);
        mPopupWindow.setContentWidth(width);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Drawable drawable = mPopupWindow.getBackground();
            if (drawable != null) {
                drawable.setColorFilter(ColorHelper.getAttributeColor(
                        builder.mContext, R.attr.card_background), PorterDuff.Mode.SRC_IN);
            }
        } else {
            mPopupWindow.setBackgroundDrawable(new ColorDrawable(
                    ColorHelper.getAttributeColor(builder.mContext, R.attr.card_background)));
        }

        mPopupWindow.setAnchorView(builder.mTo);
        mPopupWindow.setAdapter(mAdapter);
        mPopupWindow.setOnItemClickListener((adapterView, view, i, l) -> {
            if (builder.mCallback != null) {
                builder.mCallback.onClick(this, i);
                return;
            }

            mPopupWindow.dismiss();
        });
    }

    public void show() {
        if (mAdapter.getCount() == 0) {
            LogUtil.e("Popup size = 0, show() ignored");
            return;
        }
        mPopupWindow.show();
    }

    public void dismiss() {
        if (mPopupWindow.isShowing())
            mPopupWindow.dismiss();
    }

    public List<PopupItem> getItems() {
        return mAdapter.getItems();
    }

    public void updateItem(int position, PopupItem item) {
        mAdapter.updateItem(position, item);
    }

    public void removeItem(int position) {
        mAdapter.removeItem(position);
    }

    public static Builder Builder(@NonNull Context context) {
        return new Builder(context);
    }

    private int getMeasuredWidth(@NonNull Context context) {
        DisplayMetrics metrics = new DisplayMetrics();
        ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(metrics);

        int maxWidth = context.getResources().getDimensionPixelSize(R.dimen.popup_max_width);
        int minWidth = context.getResources().getDimensionPixelSize(R.dimen.popup_min_width);
        String longestText = "";
        for (PopupItem item : mAdapter.getItems()) {
            if (item.getTitle().length() > longestText.length())
                longestText = item.getTitle();
        }

        int padding = context.getResources().getDimensionPixelSize(R.dimen.content_margin);
        int iconSize = context.getResources().getDimensionPixelSize(R.dimen.icon_size_small);
        TextView textView = new TextView(context);
        textView.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        textView.setTypeface(TypefaceHelper.getRegular(context));
        textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, context.getResources()
                .getDimension(R.dimen.text_content_subtitle));
        textView.setPadding(padding + iconSize + padding, 0, padding, 0);
        textView.setText(longestText);

        int widthMeasureSpec = View.MeasureSpec.makeMeasureSpec(metrics.widthPixels, View.MeasureSpec.AT_MOST);
        int heightMeasureSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        textView.measure(widthMeasureSpec, heightMeasureSpec);

        int measuredWidth = textView.getMeasuredWidth() + padding;
        if (measuredWidth <= minWidth) {
            return minWidth;
        }

        if (measuredWidth >= minWidth && measuredWidth <= maxWidth) {
            return measuredWidth;
        }
        return maxWidth;
    }

    public static class Builder {

        private final Context mContext;
        private Callback mCallback;
        private View mTo;
        private List<PopupItem> mItems;

        private Builder(Context context) {
            mContext = context;
            mItems = new ArrayList<>();
        }

        public Builder to(@Nullable View to) {
            mTo = to;
            return this;
        }

        public Builder list(@NonNull List<PopupItem> items) {
            mItems = items;
            return this;
        }

        public Builder callback(@Nullable Callback callback) {
            mCallback = callback;
            return this;
        }

        public Popup build() {
            return new Popup(this);
        }

        public void show() {
            build().show();
        }
    }

    class PopupAdapter extends BaseAdapter {

        private List<PopupItem> mItems;
        private final Context mContext;

        PopupAdapter(@NonNull Context context, @NonNull List<PopupItem> items) {
            mContext = context;
            mItems = items;
        }

        @Override
        public int getCount() {
            return mItems.size();
        }

        @Override
        public PopupItem getItem(int position) {
            return mItems.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View view, ViewGroup viewGroup) {
            ViewHolder holder;
            if (view == null) {
                view = View.inflate(mContext, R.layout.popup_item_list, null);
                holder = new ViewHolder(view);
                view.setTag(holder);
            } else {
                holder = (ViewHolder) view.getTag();
            }

            PopupItem item = mItems.get(position);
            holder.checkBox.setVisibility(View.GONE);
            if (item.isShowCheckbox()) {
                holder.checkBox.setChecked(item.getCheckboxValue());
                holder.checkBox.setVisibility(View.VISIBLE);
            }

            int color = ColorHelper.getAttributeColor(mContext, android.R.attr.textColorPrimary);
            if (item.isSelected()) {
                color = ColorHelper.getAttributeColor(mContext, R.attr.colorAccent);
            }

            if (item.getIcon() != 0) {
                Drawable drawable = DrawableHelper.getTintedDrawable(mContext, item.getIcon(), color);
                holder.title.setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null);
            }

            holder.title.setText(item.getTitle());
            holder.title.setTextColor(color);
            return view;
        }

        class ViewHolder {

            @BindView(R2.id.checkbox)
            AppCompatCheckBox checkBox;
            @BindView(R2.id.title)
            TextView title;

            ViewHolder(@NonNull View view) {
                ButterKnife.bind(this, view);
            }
        }

        List<PopupItem> getItems() {
            return mItems;
        }

        void updateItem(int position, PopupItem item) {
            mItems.set(position, item);
            notifyDataSetChanged();
        }

        void removeItem(int position) {
            mItems.remove(position);
            notifyDataSetChanged();
        }
    }

    public interface Callback {
        void onClick(Popup popup, int position);
    }
}
