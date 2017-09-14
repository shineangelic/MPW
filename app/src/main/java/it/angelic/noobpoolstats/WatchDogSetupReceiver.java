package it.angelic.noobpoolstats;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.SystemClock;
import android.support.v7.preference.PreferenceManager;
import android.util.Log;

import java.util.Calendar;
import java.util.Date;


public class WatchDogSetupReceiver extends BroadcastReceiver {



	@Override
	public void onReceive(final Context ctx, final Intent intent) {
		//controlliamo al doppio della frequenza servizio
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(ctx);
        Boolean synchActive = pref.getBoolean("pref_sync", false);

		if (synchActive) {
			Log.i(MainActivity.TAG + ":WDSetup", "LifeCheckerSetupReceiver.onReceive() called. Checking every sec.3600. Walletaddr: "+pref.getString("wallet_addr",null));
			AlarmManager alarmManager = (AlarmManager) ctx.getSystemService(Context.ALARM_SERVICE);
			Intent i = new Intent(ctx, WatchDogEventReceiver.class); // explicit
																		// intent
            i.putExtra("WALLETURL", pref.getString("wallet_addr",null));
			i.putExtra("NOTIFY", pref.getBoolean("pref_notify",false));
			PendingIntent patTheDog = PendingIntent.getBroadcast(ctx, 0, i, PendingIntent.FLAG_CANCEL_CURRENT);

			//now.add(Calendar.SECOND, 10);//offset
			alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, new Date().getTime() + 5000,
					  AlarmManager.INTERVAL_HALF_HOUR,
					patTheDog);
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
				Log.i(MainActivity.TAG + ":WDSetup", "LifeCheckerSetupReceiver.onReceive() next clock: "+new Date(alarmManager.getNextAlarmClock().getTriggerTime()));
			}

		}
	}

}
