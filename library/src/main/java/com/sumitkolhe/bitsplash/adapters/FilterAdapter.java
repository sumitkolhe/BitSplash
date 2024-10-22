package com.sumitkolhe.bitsplash.adapters;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.core.view.ViewCompat;
import androidx.appcompat.widget.AppCompatCheckBox;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.danimahardhika.android.helpers.core.ColorHelper;
import com.danimahardhika.android.helpers.core.DrawableHelper;
import com.sumitkolhe.bitsplash.board.R;
import com.sumitkolhe.bitsplash.board.R2;
import com.sumitkolhe.bitsplash.databases.Database;
import com.sumitkolhe.bitsplash.items.Category;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


public class FilterAdapter extends BaseAdapter {

    private final Context mContext;
    private final List<Category> mCategories;
    private final boolean mIsMuzei;
    private boolean mIsEnabled;

    public FilterAdapter(@NonNull Context context, @NonNull List<Category> categories, boolean isMuzei) {
        mContext = context;
        mCategories = categories;
        mIsMuzei = isMuzei;
        mIsEnabled = true;
    }

    @Override
    public int getCount() {
        return mCategories.size();
    }

    @Override
    public Category getItem(int position) {
        return mCategories.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {
        ViewHolder holder;
        if (view == null) {
            view = View.inflate(mContext, R.layout.fragment_filter_item_list, null);
            holder = new ViewHolder(view);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        Category category = mCategories.get(position);
        holder.title.setText(category.getName());
        holder.checkBox.setChecked(mIsMuzei ?
                category.isMuzeiSelected() :
                category.isSelected());
        holder.counter.setText(category.getCategoryCount());
        holder.container.setOnClickListener(v -> {
            if (!mIsEnabled) return;

            Database database = Database.get(mContext);
            if (mIsMuzei) {
                database.selectCategoryForMuzei(category.getId(),
                        !category.isMuzeiSelected());
                category.setMuzeiSelected(
                        !category.isMuzeiSelected());
            } else {
                database.selectCategory(category.getId(),
                        !category.isSelected());
                mCategories.get(position).setSelected(
                        !category.isSelected());
            }

            notifyDataSetChanged();
        });
        return view;
    }

    class ViewHolder {

        @BindView(R2.id.container)
        LinearLayout container;
        @BindView(R2.id.checkbox)
        AppCompatCheckBox checkBox;
        @BindView(R2.id.title)
        TextView title;
        @BindView(R2.id.counter)
        TextView counter;

        ViewHolder(@NonNull View view) {
            ButterKnife.bind(this, view);
            int color = ColorHelper.getAttributeColor(mContext, android.R.attr.textColorPrimary);
            ViewCompat.setBackground(counter, DrawableHelper.getTintedDrawable(
                    mContext, R.drawable.ic_toolbar_circle, color));
            counter.setTextColor(ColorHelper.getTitleTextColor(color));
        }
    }

    public int getSelectedCount() {
        int selected = 0;
        for (Category category : mCategories) {
            if (mIsMuzei) {
                if (category.isMuzeiSelected()) {
                    selected += 1;
                }
            } else {
                if (category.isSelected()) {
                    selected += 1;
                }
            }
        }
        return selected;
    }

    public void setEnabled(boolean enabled) {
        mIsEnabled = enabled;
    }

    public boolean selectAll() {
        boolean isAllSelected = getCount() == getSelectedCount();
        for (Category category : mCategories) {
            if (mIsMuzei) {
                Database.get(mContext).selectCategoryForMuzei(category.getId(), !isAllSelected);
                category.setMuzeiSelected(!isAllSelected);
            } else {
                Database.get(mContext).selectCategory(category.getId(), !isAllSelected);
                category.setSelected(!isAllSelected);
            }
        }
        return !isAllSelected;
    }
}
