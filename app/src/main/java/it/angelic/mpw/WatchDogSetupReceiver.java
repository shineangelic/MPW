package it.angelic.mpw;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.preference.PreferenceManager;
import android.util.Log;

import java.util.Date;

import static it.angelic.mpw.Constants.TAG;


public class WatchDogSetupReceiver extends BroadcastReceiver {


    @Override
    public void onReceive(final Context ctx, final Intent intent) {
        //controlliamo al doppio della frequenza servizio
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(ctx);
        Boolean synchActive = pref.getBoolean("pref_sync", false);
        Log.i(TAG, "NoobPool Watchdog action:" + intent.getAction());
        if (synchActive) {
            Log.i(TAG + ":WDSetup", "LifeCheckerSetupReceiver.onReceive() called. Checking every:"+pref.getString("pref_sync_freq",""+AlarmManager.INTERVAL_HALF_HOUR)
                     + " Walletaddr: " + pref.getString("wallet_addr", null));
            AlarmManager alarmManager = (AlarmManager) ctx.getSystemService(Context.ALARM_SERVICE);
            Intent i = new Intent(ctx, WatchDogEventReceiver.class); // explicit
            // intent
            i.putExtra("WALLETURL", pref.getString("wallet_addr", null));
            if(pref.getBoolean("pref_notify", false)) {
                i.putExtra("NOTIFY_BLOCK", pref.getBoolean("pref_notify_block", false));
                i.putExtra("NOTIFY_OFFLINE", pref.getBoolean("pref_notify_offline", false));
                i.putExtra("NOTIFY_PAYMENT", pref.getBoolean("pref_notify_payment", false));
            }
            PendingIntent patTheDog = PendingIntent.getBroadcast(ctx, 0, i, PendingIntent.FLAG_CANCEL_CURRENT);

            //inexact default half hour
            alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, new Date().getTime() + 5000,
                 Long.valueOf(   pref.getString("pref_sync_freq",""+AlarmManager.INTERVAL_HALF_HOUR) ),
                    patTheDog);
        }
    }

}
