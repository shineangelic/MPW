package it.angelic.mpw;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.preference.PreferenceDialogFragmentCompat;
import android.support.v7.preference.PreferenceManager;

public class PoolDialogPrefFragCompat extends PreferenceDialogFragmentCompat {
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
            prefs.edit().putBoolean("skipIntro",false).apply();
            // do things
            Intent opzioni = new Intent(getActivity(), ChoosePoolActivity.class);
            startActivity(opzioni);
            getActivity().finish();
        }
    }
}