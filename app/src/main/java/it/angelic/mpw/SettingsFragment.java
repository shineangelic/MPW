package it.angelic.mpw;

import android.os.Bundle;
import android.support.v7.preference.EditTextPreference;
import android.support.v7.preference.ListPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.support.v7.preference.SwitchPreferenceCompat;

public class SettingsFragment extends PreferenceFragmentCompat {

    @Override
    public void onCreatePreferences(Bundle bundle, String s) {
        addPreferencesFromResource(R.xml.preferences);

        final android.support.v7.preference.SwitchPreferenceCompat globalNotifications = (SwitchPreferenceCompat) findPreference("pref_notify");
        final android.support.v7.preference.SwitchPreferenceCompat service = (SwitchPreferenceCompat) findPreference("pref_sync");
        final android.support.v7.preference.ListPreference listFreqPreference = (ListPreference) findPreference("pref_sync_freq");
        final android.support.v7.preference.SwitchPreferenceCompat offlineNotifications =(SwitchPreferenceCompat) findPreference("pref_notify_offline");
        final android.support.v7.preference.SwitchPreferenceCompat blockNotifications =(SwitchPreferenceCompat) findPreference("pref_notify_block");
        final android.support.v7.preference.SwitchPreferenceCompat paymentNotifications =(SwitchPreferenceCompat) findPreference("pref_notify_payment");

        //Service Enabled listener
        Preference.OnPreferenceChangeListener listenerServ = new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                // newValue is the value you choose
                listFreqPreference.setEnabled( (Boolean) newValue);
                return true;
            }
        };
        service.setOnPreferenceChangeListener(listenerServ);

        //Notification global
        Preference.OnPreferenceChangeListener listener = new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                // newValue is the value you choose
                blockNotifications.setEnabled( (Boolean) newValue);
                offlineNotifications.setEnabled( (Boolean) newValue);
                paymentNotifications.setEnabled( (Boolean) newValue);
                return true;
            }
        };
        globalNotifications.setOnPreferenceChangeListener(listener);

        //service.setEnabled(true);

        //Listener x controllo correttezza
        EditTextPreference walletAddrPref = (EditTextPreference) findPreference("wallet_addr");
        walletAddrPref.setOnPreferenceChangeListener(new EthereumFormatWatcher(getActivity()));

        /////

    }
}