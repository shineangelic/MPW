package it.angelic.noobpoolstats;

import android.os.Bundle;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.support.v7.preference.SwitchPreferenceCompat;

public class SettingsFragment extends PreferenceFragmentCompat {

    @Override
    public void onCreatePreferences(Bundle bundle, String s) {
        addPreferencesFromResource(R.xml.preferences);

        android.support.v7.preference.SwitchPreferenceCompat service = (SwitchPreferenceCompat) findPreference("pref_sync");
        final android.support.v7.preference.SwitchPreferenceCompat pref_notify =(SwitchPreferenceCompat) findPreference("pref_notify");
        Preference.OnPreferenceChangeListener listener = new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                // newValue is the value you choose
                pref_notify.setEnabled( ((SwitchPreferenceCompat)preference).isChecked());
                return true;
            }
        };
        service.setOnPreferenceChangeListener(listener);
        service.setEnabled(true);

    }
}