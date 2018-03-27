package it.angelic.mpw;

import android.app.AlarmManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.preference.PreferenceManager;
import android.util.Log;

import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.GooglePlayDriver;
import com.firebase.jobdispatcher.Job;
import com.firebase.jobdispatcher.Lifetime;
import com.firebase.jobdispatcher.RetryStrategy;
import com.firebase.jobdispatcher.Trigger;

import static it.angelic.mpw.Constants.TAG;

@Deprecated
public class WatchDogSetupReceiver extends BroadcastReceiver {
    //The BroadcastReceiver that listens for bluetooth broadcasts


    @Override
    public void onReceive(final Context ctx, final Intent intent) {
        //controlliamo al doppio della frequenza servizio
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(ctx);
        Boolean synchActive = pref.getBoolean("pref_sync", true);
        Log.i(TAG, "NoobPool Watchdog action:" + intent.getAction());
        if (synchActive) {
            // Create a new dispatcher using the Google Play driver.
            FirebaseJobDispatcher dispatcher = new FirebaseJobDispatcher(new GooglePlayDriver(ctx));


            Log.i(TAG, "REGISTERED RECEIVER " + intent.getAction());

            Log.i(TAG, "LifeCheckerSetupReceiver.onReceive() called. Checking every:" + pref.getString("pref_sync_freq", "" + AlarmManager.INTERVAL_HALF_HOUR)
                    + " Walletaddr: " + pref.getString("wallet_addr", null));
            AlarmManager alarmManager = (AlarmManager) ctx.getSystemService(Context.ALARM_SERVICE);

            Bundle myExtrasBundle = new Bundle();
            myExtrasBundle.putString("WALLETURL", pref.getString("wallet_addr", null));
            myExtrasBundle.putBoolean("NOTIFY_BLOCK", pref.getBoolean("wallet_addr", true));
            myExtrasBundle.putBoolean("NOTIFY_OFFLINE", pref.getBoolean("wallet_addr", true));
            myExtrasBundle.putBoolean("NOTIFY_PAYMENT", pref.getBoolean("wallet_addr", true));


            Job myJob = dispatcher.newJobBuilder()
                    // the JobService that will be called
                    .setService(MPWService.class)
                    // uniquely identifies the job
                    .setTag("mpw-updater")
                    // one-off job
                    .setRecurring(true)
                    // don't persist past a device reboot
                    .setLifetime(Lifetime.FOREVER)
                    // start between 0 and 60 seconds from now
                    .setTrigger(Trigger.executionWindow(60, 120))

                    // don't overwrite an existing job with the same tag
                    .setReplaceCurrent(true)
                    // retry with exponential backoff
                    .setRetryStrategy(RetryStrategy.DEFAULT_EXPONENTIAL)
                    // constraints that need to be satisfied for the job to run
                    .setExtras(myExtrasBundle)
                    .build();

            dispatcher.mustSchedule(myJob);

           /* Intent i = getUpdatingIntent(ctx, pref);
            PendingIntent patTheDog = PendingIntent.getBroadcast(ctx, 0, i, PendingIntent.FLAG_CANCEL_CURRENT);


            //inexact default half hour
            alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, new Date().getTime() + 5000,
                    Long.valueOf(pref.getString("pref_sync_freq", "" + AlarmManager.INTERVAL_HALF_HOUR)),
                    patTheDog);*/
        }
    }

    @NonNull
    private Intent getUpdatingIntent(Context ctx, SharedPreferences pref) {
        Intent i = new Intent(ctx, WatchDogEventReceiver.class); // explicit
        // intent
        i.putExtra("WALLETURL", pref.getString("wallet_addr", null));
        if (pref.getBoolean("pref_notify", false)) {
            i.putExtra("NOTIFY_BLOCK", pref.getBoolean("pref_notify_block", false));
            i.putExtra("NOTIFY_OFFLINE", pref.getBoolean("pref_notify_offline", false));
            i.putExtra("NOTIFY_PAYMENT", pref.getBoolean("pref_notify_payment", false));
        }
        return i;
    }

   /* // schedule the start of the service every 10 - 30 seconds
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public static void scheduleJob(Context context) {
        ComponentName serviceComponent = new ComponentName(context, TestJobService.class);
        JobInfo.Builder builder = new JobInfo.Builder(0, serviceComponent);
        builder.setMinimumLatency(1 * 1000); // wait at least
        builder.setOverrideDeadline(3 * 1000); // maximum delay
        //builder.setRequiredNetworkType(JobInfo.NETWORK_TYPE_UNMETERED); // require unmetered network
        //builder.setRequiresDeviceIdle(true); // device should be idle
        //builder.setRequiresCharging(false); // we don't care if the device is charging or not
        JobScheduler jobScheduler = context.getSystemService(JobScheduler.class);
        jobScheduler.schedule(builder.build());
    }*/

}
