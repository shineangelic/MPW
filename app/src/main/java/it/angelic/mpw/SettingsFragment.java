package it.angelic.mpw;

import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.fragment.app.DialogFragment;
import androidx.preference.EditTextPreference;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;
import androidx.preference.SwitchPreferenceCompat;
import android.util.Log;
import android.widget.Toast;

import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.GooglePlayDriver;
import com.firebase.jobdispatcher.Job;
import com.google.firebase.analytics.FirebaseAnalytics;

import it.angelic.mpw.model.enums.CurrencyEnum;
import it.angelic.mpw.model.enums.PoolEnum;

public class SettingsFragment extends PreferenceFragmentCompat {

    private FirebaseAnalytics mFirebaseAnalytics;

    @Override
    public void onCreatePreferences(Bundle bundle, String s) {
        addPreferencesFromResource(R.xml.preferences);
        //Crashlytics.getInstance().crash();
        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(getActivity());

        PoolEnum mPool = PoolEnum.valueOf(prefs.getString("poolEnum", ""));
        CurrencyEnum mCur = CurrencyEnum.valueOf(prefs.getString("curEnum", ""));

        final androidx.preference.SwitchPreferenceCompat globalNotifications = (SwitchPreferenceCompat) findPreference("pref_notify");
        final androidx.preference.EditTextPreference walletAddr = (EditTextPreference) findPreference("wallet_addr");
        final androidx.preference.SwitchPreferenceCompat service = (SwitchPreferenceCompat) findPreference("pref_sync");
        final androidx.preference.ListPreference listFreqPreference = (ListPreference) findPreference("pref_sync_freq");
        final androidx.preference.SwitchPreferenceCompat offlineNotifications = (SwitchPreferenceCompat) findPreference("pref_notify_offline");
        final androidx.preference.SwitchPreferenceCompat blockNotifications = (SwitchPreferenceCompat) findPreference("pref_notify_block");
        final androidx.preference.SwitchPreferenceCompat paymentNotifications = (SwitchPreferenceCompat) findPreference("pref_notify_payment");

        //interlock single notification settings
        blockNotifications.setEnabled(globalNotifications.isChecked());
        offlineNotifications.setEnabled(globalNotifications.isChecked());
        paymentNotifications.setEnabled(globalNotifications.isChecked());

        //Service Enabled listener
        Preference.OnPreferenceChangeListener listenerServ = new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                // newValue is the value you choose
                listFreqPreference.setEnabled((Boolean) newValue);

                Boolean nv = (Boolean) newValue;
                FirebaseJobDispatcher dispatcher = new FirebaseJobDispatcher(new GooglePlayDriver(getActivity()));
                dispatcher.cancelAll();
                if (nv) {
                    Job myJob = MPWService.getJobUpdate(prefs, dispatcher);
                    int res  = dispatcher.schedule(myJob);
                    if (res != FirebaseJobDispatcher.SCHEDULE_RESULT_SUCCESS){
                        Toast.makeText(getActivity(),"Cannot enable service. Is Play Services up to date? Notifications won't work", Toast.LENGTH_SHORT).show();
                        return false;
                    }
                    Log.w(Constants.TAG, "SERVICE ACTIVE, schedule res: " +res);
                }

                //firebase log event
                Bundle bundle = new Bundle();
                bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, nv.toString());
                mFirebaseAnalytics.logEvent("service_active", bundle);


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

                //pezza perche il val ancora non c'e
                prefs.edit().putString("pref_sync_freq", ""+nv).apply();

                FirebaseJobDispatcher dispatcher = new FirebaseJobDispatcher(new GooglePlayDriver(getActivity()));
                Job myJob = MPWService.getJobUpdate(prefs, dispatcher);
                dispatcher.schedule(myJob);

                Bundle bundle = new Bundle();
                bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, ""+nv);
                mFirebaseAnalytics.logEvent("service_freq", bundle);

                return true;
            }
        };
        listFreqPreference.setOnPreferenceChangeListener(listenerServF);

        //Notification global
        Preference.OnPreferenceChangeListener listener = new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                Log.w(Constants.TAG, "Changed NOTIF setting to: " +newValue);
                Boolean nv = (Boolean) newValue;
                // newValue is the value you choose
                blockNotifications.setEnabled(nv);
                offlineNotifications.setEnabled(nv);
                paymentNotifications.setEnabled(nv);

                Bundle bundle = new Bundle();
                bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, ""+nv);
                mFirebaseAnalytics.logEvent("service_notifications", bundle);

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