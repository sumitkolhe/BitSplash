package com.sumitkolhe.bitsplash.adapters;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatRadioButton;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sumitkolhe.bitsplash.board.R;
import com.sumitkolhe.bitsplash.board.R2;
import com.sumitkolhe.bitsplash.fragments.dialogs.LanguagesFragment;
import com.sumitkolhe.bitsplash.items.Language;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;



public class LanguagesAdapter extends BaseAdapter {

    private Context mContext;
    private List<Language> mLanguages;
    private int mSelectedIndex;

    public LanguagesAdapter(@NonNull Context context, @NonNull List<Language> languages, int selectedIndex) {
        mContext = context;
        mLanguages = languages;
        mSelectedIndex = selectedIndex;
    }

    @Override
    public int getCount() {
        return mLanguages.size();
    }

    @Override
    public Language getItem(int position) {
        return mLanguages.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {
        LanguagesAdapter.ViewHolder holder;
        if (view == null) {
            view = View.inflate(mContext, R.layout.fragment_inappbilling_item_list, null);
            holder = new LanguagesAdapter.ViewHolder(view);
            view.setTag(holder);
        } else {
            holder = (LanguagesAdapter.ViewHolder) view.getTag();
        }

        holder.radio.setChecked(mSelectedIndex == position);
        holder.name.setText(mLanguages.get(position).getName());

        holder.container.setOnClickListener(v -> {
            FragmentManager fm = ((AppCompatActivity) mContext).getSupportFragmentManager();
            if (fm == null) return;

            Fragment fragment = fm.findFragmentByTag(LanguagesFragment.TAG);
            if (fragment == null) return;

            if (fragment instanceof LanguagesFragment) {
                ((LanguagesFragment) fragment).setLanguage(mLanguages.get(position));
            }
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
}
