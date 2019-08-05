package it.angelic.mpw;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.preference.PreferenceDialogFragmentCompat;
import androidx.preference.PreferenceManager;

import com.google.firebase.analytics.FirebaseAnalytics;

public class PoolDialogPrefFragCompat extends PreferenceDialogFragmentCompat {
    public PoolDialogPrefFragCompat(){
        super();

    }

    public static PoolDialogPrefFragCompat newInstance(String key) {
        final PoolDialogPrefFragCompat fragment = new PoolDialogPrefFragCompat();
        final Bundle bundle = new Bundle(1);
        bundle.putString(ARG_KEY, key);
        fragment.setArguments(bundle);

        return fragment;
    }

    @Override
    public void onDialogClosed(boolean positiveResult) {
        if (positiveResult) {
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
            FirebaseAnalytics mFirebaseAnalytics = FirebaseAnalytics.getInstance(getActivity());
            prefs.edit().putBoolean("skipIntro",false).apply();
            Bundle bundle = new Bundle();
            bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, Boolean.valueOf(prefs.getBoolean("skipIntro", false)).toString());
            mFirebaseAnalytics.logEvent("skip_intro", bundle);
            // do things
            Intent opzioni = new Intent(getActivity(), ChoosePoolActivity.class);
            startActivity(opzioni);
            getActivity().finish();
        }
    }
}