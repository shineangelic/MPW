package it.angelic.mpw;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.preference.EditTextPreference;
import android.support.v7.preference.ListPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.preference.SwitchPreferenceCompat;
import android.util.Log;

import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.GooglePlayDriver;
import com.firebase.jobdispatcher.Job;

import it.angelic.mpw.model.enums.CurrencyEnum;
import it.angelic.mpw.model.enums.PoolEnum;

public class SettingsFragment extends PreferenceFragmentCompat {

    @Override
    public void onCreatePreferences(Bundle bundle, String s) {
        addPreferencesFromResource(R.xml.preferences);
        //Crashlytics.getInstance().crash();
        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        PoolEnum mPool = PoolEnum.valueOf(prefs.getString("poolEnum", ""));
        CurrencyEnum mCur = CurrencyEnum.valueOf(prefs.getString("curEnum", ""));

        final android.support.v7.preference.SwitchPreferenceCompat globalNotifications = (SwitchPreferenceCompat) findPreference("pref_notify");
        final android.support.v7.preference.EditTextPreference walletAddr = (EditTextPreference) findPreference("wallet_addr");
        final android.support.v7.preference.SwitchPreferenceCompat service = (SwitchPreferenceCompat) findPreference("pref_sync");
        final android.support.v7.preference.ListPreference listFreqPreference = (ListPreference) findPreference("pref_sync_freq");
        final android.support.v7.preference.SwitchPreferenceCompat offlineNotifications = (SwitchPreferenceCompat) findPreference("pref_notify_offline");
        final android.support.v7.preference.SwitchPreferenceCompat blockNotifications = (SwitchPreferenceCompat) findPreference("pref_notify_block");
        final android.support.v7.preference.SwitchPreferenceCompat paymentNotifications = (SwitchPreferenceCompat) findPreference("pref_notify_payment");

        //Service Enabled listener
        Preference.OnPreferenceChangeListener listenerServ = new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                // newValue is the value you choose
                listFreqPreference.setEnabled((Boolean) newValue);
                Log.w(Constants.TAG, "Changed SERVICE setting to: " + newValue);
                Boolean nv = (Boolean) newValue;
                FirebaseJobDispatcher dispatcher = new FirebaseJobDispatcher(new GooglePlayDriver(getActivity()));
                dispatcher.cancelAll();
                if (nv) {
                    Job myJob = MPWService.getJobUpdate(prefs, dispatcher);
                    dispatcher.schedule(myJob);
                }
                return true;
            }
        };
        service.setOnPreferenceChangeListener(listenerServ);

        //Service FREQ listener
        Preference.OnPreferenceChangeListener listenerServF = new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                Integer nv = Integer.valueOf((String) newValue);
                // newValue is the value you choose
                Log.w(Constants.TAG, "Changed FREQ setting to: " + nv);

                FirebaseJobDispatcher dispatcher = new FirebaseJobDispatcher(new GooglePlayDriver(getActivity()));
                dispatcher.cancelAll();

                Job myJob = MPWService.getJobUpdate(prefs, dispatcher);
                dispatcher.schedule(myJob);

                return true;
            }
        };
        listFreqPreference.setOnPreferenceChangeListener(listenerServF);

        //Notification global
        Preference.OnPreferenceChangeListener listener = new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                Log.w(Constants.TAG, "Changed NOTIF setting to: " + prefs.getString("pref_sync_freq", null));
                Boolean nv = (Boolean) newValue;
                FirebaseJobDispatcher dispatcher = new FirebaseJobDispatcher(new GooglePlayDriver(getActivity()));
                dispatcher.cancelAll();
                if (nv) {
                    Job myJob = MPWService.getJobUpdate(prefs, dispatcher);
                    dispatcher.schedule(myJob);
                }
                // newValue is the value you choose
                blockNotifications.setEnabled(nv);
                offlineNotifications.setEnabled(nv);
                paymentNotifications.setEnabled(nv);
                return true;
            }
        };
        globalNotifications.setOnPreferenceChangeListener(listener);


        //Listener x controllo correttezza
        walletAddr.setOnPreferenceChangeListener(new WalletPrefChangeListener(getActivity(), mPool, mCur));
        walletAddr.setSummary(getString(R.string.wallet_info, mPool.toString(), mCur.toString()));
        walletAddr.setDialogTitle(mPool.toString() + " Network Login");

    }

    @Override
    public void onDisplayPreferenceDialog(Preference preference) {
        if (preference instanceof PoolDialogPreference) {
            DialogFragment dialogFragment = PoolDialogPrefFragCompat.newInstance(preference.getKey());
            dialogFragment.setTargetFragment(this, 0);
            dialogFragment.show(getFragmentManager(), null);
        } else super.onDisplayPreferenceDialog(preference);
    }
}