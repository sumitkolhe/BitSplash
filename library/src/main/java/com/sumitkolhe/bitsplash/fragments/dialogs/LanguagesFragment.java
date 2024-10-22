package com.sumitkolhe.bitsplash.fragments.dialogs;

import android.app.Dialog;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import android.util.Log;
import android.widget.ListView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.sumitkolhe.bitsplash.board.R;
import com.sumitkolhe.bitsplash.board.R2;
import com.sumitkolhe.bitsplash.adapters.LanguagesAdapter;
import com.sumitkolhe.bitsplash.helpers.LocaleHelper;
import com.sumitkolhe.bitsplash.helpers.TypefaceHelper;
import com.sumitkolhe.bitsplash.items.Language;
import com.sumitkolhe.bitsplash.preferences.Preferences;
import com.danimahardhika.android.helpers.core.utils.LogUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;



public class LanguagesFragment extends DialogFragment {

    @BindView(R2.id.listview)
    ListView mListView;

    private AsyncTask mAsyncTask;

    public static final String TAG = "com.dm.wallpaper.board.dialog.languages";

    private static LanguagesFragment newInstance() {
        return new LanguagesFragment();
    }

    public static void showLanguageChooser(@NonNull FragmentManager fm) {
        FragmentTransaction ft = fm.beginTransaction();
        Fragment prev = fm.findFragmentByTag(TAG);
        if (prev != null) {
            ft.remove(prev);
        }

        ft.add(newInstance(), TAG)
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
        builder.customView(R.layout.fragment_languages, false);
        builder.typeface(TypefaceHelper.getMedium(getActivity()), TypefaceHelper.getRegular(getActivity()));
        builder.title(R.string.pref_language_header);
        MaterialDialog dialog = builder.build();
        dialog.show();

        ButterKnife.bind(this, dialog);
        return dialog;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mAsyncTask = new LanguagesLoader().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    @Override
    public void onDestroy() {
        if (mAsyncTask != null) {
            mAsyncTask.cancel(true);
        }
        super.onDestroy();
    }

    public void setLanguage(@NonNull Language language) {
        boolean isDefault = language.getName().equalsIgnoreCase("default");
        Preferences.get(getActivity()).setLocaleDefault(isDefault);

        if (!isDefault && language.getLocale() != null) {
            Preferences.get(getActivity()).setCurrentLocale(
                    language.getLocale().toString());
        }
        dismiss();
        getActivity().recreate();
    }

    private class LanguagesLoader extends AsyncTask<Void, Void, Boolean> {

        private List<Language> languages;
        private int index = 0;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            languages = new ArrayList<>();
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            while (!isCancelled()) {
                try {
                    Thread.sleep(1);
                    languages.add(new Language("Default", null));
                    languages.addAll(LocaleHelper.getAvailableLanguages(getActivity()));

                    if (Preferences.get(getActivity()).isLocaleDefault()) {
                        return true;
                    }

                    Locale locale = Preferences.get(getActivity()).getCurrentLocale();
                    for (int i = 0; i < languages.size(); i++) {
                        Locale l = languages.get(i).getLocale();
                        if (l != null && l.toString().equals(locale.toString())) {
                            index = i;
                            break;
                        }
                    }
                    return true;
                } catch (Exception e) {
                    LogUtil.e(Log.getStackTraceString(e));
                    return false;
                }
            }
            return false;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            if (getActivity() == null) return;
            if (getActivity().isFinishing()) return;

            mAsyncTask = null;
            if (aBoolean) {
                mListView.setAdapter(new LanguagesAdapter(getActivity(), languages, index));
            } else {
                dismiss();
            }
        }
    }
}
