package com.sumitkolhe.bitsplash.adapters;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatRadioButton;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sumitkolhe.bitsplash.board.R;
import com.sumitkolhe.bitsplash.board.R2;
import com.sumitkolhe.bitsplash.items.InAppBilling;

import butterknife.BindView;
import butterknife.ButterKnife;

public class InAppBillingAdapter extends BaseAdapter {

    private final Context mContext;
    private final InAppBilling[] mInAppBillings;

    private int mSelectedPosition = 0;

    public InAppBillingAdapter(@NonNull Context context, @NonNull InAppBilling[] inAppBillings) {
        mContext = context;
        mInAppBillings = inAppBillings;
    }

    @Override
    public int getCount() {
        return mInAppBillings.length;
    }

    @Override
    public InAppBilling getItem(int position) {
        return mInAppBillings[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {
        ViewHolder holder;
        if (view == null) {
            view = View.inflate(mContext, R.layout.fragment_inappbilling_item_list, null);
            holder = new ViewHolder(view);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        holder.radio.setChecked(mSelectedPosition == position);

        String product = mInAppBillings[position].getPrice() +" - "+
                mInAppBillings[position].getProductName();
        holder.name.setText(product);

        holder.container.setOnClickListener(v -> {
            mSelectedPosition = position;
            notifyDataSetChanged();
        });
        return view;
    }

    class ViewHolder {

        @BindView(R2.id.radio)
        AppCompatRadioButton radio;
        @BindView(R2.id.name)
        TextView name;
        @BindView(R2.id.container)
        LinearLayout container;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }

    public InAppBilling getSelectedProduct() {
        return mInAppBillings[mSelectedPosition];
    }

}
