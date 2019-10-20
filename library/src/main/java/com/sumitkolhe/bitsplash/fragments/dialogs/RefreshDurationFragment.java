package com.sumitkolhe.bitsplash.fragments.dialogs;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.appcompat.widget.AppCompatRadioButton;
import android.util.Log;
import android.view.View;
import android.widget.NumberPicker;

import com.afollestad.materialdialogs.MaterialDialog;
import com.danimahardhika.android.helpers.core.ColorHelper;
import com.danimahardhika.android.helpers.core.DrawableHelper;
import com.sumitkolhe.bitsplash.board.R;
import com.sumitkolhe.bitsplash.board.R2;
import com.sumitkolhe.bitsplash.helpers.TypefaceHelper;
import com.danimahardhika.android.helpers.core.utils.LogUtil;
import com.sumitkolhe.bitsplash.utils.listeners.RefreshDurationListener;

import butterknife.BindView;
import butterknife.ButterKnife;



public class RefreshDurationFragment extends DialogFragment implements View.OnClickListener {

    @BindView(R2.id.number_picker)
    NumberPicker mNumberPicker;
    @BindView(R2.id.minute)
    AppCompatRadioButton mMinute;
    @BindView(R2.id.hour)
    AppCompatRadioButton mHour;

    private int mRotateTime;
    private boolean mIsMinute;

    private static final String MINUTE = "minute";
    private static final String ROTATE_TIME = "rotate_time";
    private static final String TAG = "com.dm.wallpaper.board.dialog.refresh.duration";

    private static RefreshDurationFragment newInstance(int rotateTime, boolean isMinute) {
        RefreshDurationFragment fragment = new RefreshDurationFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(ROTATE_TIME, rotateTime);
        bundle.putBoolean(MINUTE, isMinute);
        fragment.setArguments(bundle);
        return fragment;
    }

    public static void showRefreshDurationDialog(FragmentManager fm, int rotateTime, boolean isMinute) {
        FragmentTransaction ft = fm.beginTransaction();
        Fragment prev = fm.findFragmentByTag(TAG);
        if (prev != null) {
            ft.remove(prev);
        }

        ft.add(newInstance(rotateTime, isMinute), TAG)
                .setTransitionStyle(FragmentTransaction.TRANSIT_FRAGMENT_FADE);

        try {
            ft.commit();
        } catch (IllegalStateException e) {
            ft.commitAllowingStateLoss();
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        MaterialDialog.Builder builder = new MaterialDialog.Builder(getActivity());
        builder.customView(R.layout.fragment_refresh_duration, true);
        builder.typeface(TypefaceHelper.getMedium(getActivity()), TypefaceHelper.getRegular(getActivity()));
        builder.title(R.string.muzei_refresh_duration);
        builder.positiveText(R.string.close);

        MaterialDialog dialog = builder.build();
        dialog.show();

        ButterKnife.bind(this, dialog);
        return dialog;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mRotateTime = getArguments().getInt(ROTATE_TIME, 1);
        mIsMinute = getArguments().getBoolean(MINUTE, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mNumberPicker.setMinValue(1);
        mNumberPicker.setMaxValue(100);
        setDividerColor(mNumberPicker);
        mMinute.setOnClickListener(this);
        mHour.setOnClickListener(this);

        mMinute.setChecked(mIsMinute);
        mHour.setChecked(!mIsMinute);
        mNumberPicker.setValue(mRotateTime);
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        RefreshDurationListener listener = (RefreshDurationListener) getActivity();
        listener.onRefreshDurationSet(mNumberPicker.getValue(), mMinute.isChecked());
        super.onDismiss(dialog);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.minute) {
            mMinute.setChecked(true);
            mHour.setChecked(false);
        } else if (id == R.id.hour) {
            mHour.setChecked(true);
            mMinute.setChecked(false);
        }
    }

    private void setDividerColor (NumberPicker picker) {
        java.lang.reflect.Field[] pickerFields = NumberPicker.class.getDeclaredFields();
        for (java.lang.reflect.Field pf : pickerFields) {
            if (pf.getName().equals("mSelectionDivider")) {
                pf.setAccessible(true);
                try {
                    int color = ColorHelper.getAttributeColor(getActivity(), R.attr.colorAccent);
                    pf.set(picker, DrawableHelper.getTintedDrawable(
                            getActivity(), R.drawable.numberpicker_divider, color));
                } catch (Exception e) {
                    LogUtil.e(Log.getStackTraceString(e));
                }
                break;
            }
        }
    }
}
