package it.angelic.mpw;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;
import android.support.v7.preference.PreferenceManager;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.crashlytics.android.Crashlytics;
import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.Job;
import com.firebase.jobdispatcher.JobParameters;
import com.firebase.jobdispatcher.JobService;
import com.firebase.jobdispatcher.JobTrigger;
import com.firebase.jobdispatcher.Lifetime;
import com.firebase.jobdispatcher.Trigger;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.apache.commons.collections4.map.LinkedMap;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.Date;

import it.angelic.mpw.model.MyDateTypeAdapter;
import it.angelic.mpw.model.MyTimeStampTypeAdapter;
import it.angelic.mpw.model.db.PoolDbHelper;
import it.angelic.mpw.model.enums.CurrencyEnum;
import it.angelic.mpw.model.enums.PoolEnum;
import it.angelic.mpw.model.jsonpojos.home.HomeStats;
import it.angelic.mpw.model.jsonpojos.wallet.Wallet;

import static android.support.v4.app.NotificationCompat.CATEGORY_PROGRESS;
import static android.support.v4.app.NotificationCompat.CATEGORY_SERVICE;
import static android.support.v4.app.NotificationCompat.PRIORITY_LOW;
import static it.angelic.mpw.Constants.LAST_TWO;
import static it.angelic.mpw.Constants.TAG;

public class MPWService extends JobService {
    final int NOTIFICATION_MINER_OFFLINE = 12;

    @Override
    public boolean onStartJob(JobParameters job) {
        Log.e(TAG, "SERVICE START");
        final Context ctx = getApplicationContext();
        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(ctx);
        try {
            final PoolEnum mPool = PoolEnum.valueOf(prefs.getString("poolEnum", ""));
            final CurrencyEnum mCur = CurrencyEnum.valueOf(prefs.getString("curEnum", ""));
            Log.i(TAG, "Miner Pool Watcher Service call:" + Utils.getHomeStatsURL(PreferenceManager.getDefaultSharedPreferences(ctx)));
            final PoolDbHelper mDbHelper = new PoolDbHelper(ctx, mPool, mCur);
            final NotificationManager mNotifyMgr =
                    (NotificationManager) ctx.getSystemService(NOTIFICATION_SERVICE);
            final GsonBuilder builder = new GsonBuilder();
            //gestione UNIX time lungo e non
            builder.registerTypeAdapter(Date.class, new MyDateTypeAdapter());
            builder.registerTypeAdapter(Calendar.class, new MyTimeStampTypeAdapter());
            //load extra

            final String minerAddr = job.getExtras().getString("WALLETURL");
            final Boolean notifyBlock = job.getExtras().getBoolean("NOTIFY_BLOCK", false);
            final Boolean notifyOffline = job.getExtras().getBoolean("NOTIFY_OFFLINE", false);
            final Boolean notifyPayment = job.getExtras().getBoolean("NOTIFY_PAYMENT", false);

            JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                    Utils.getHomeStatsURL(PreferenceManager.getDefaultSharedPreferences(ctx)), null,
                    new Response.Listener<JSONObject>() {

                        @Override
                        public void onResponse(JSONObject response) {
                            Log.d(TAG, response.toString());
                            Gson gson = builder.create();
                            // Register an adapter to manage the date types as long values
                            HomeStats retrieved = gson.fromJson(response.toString(), HomeStats.class);
                            mDbHelper.logHomeStats(retrieved);
                            //dati semi grezzi
                            LinkedMap<Date, HomeStats> ultimi = mDbHelper.getLastHomeStats(LAST_TWO);
                            //controllo se manca qualcuno
                            if (notifyBlock
                                    && ultimi.size() > 1
                                    && ultimi.get(ultimi.get(0)).getMaturedTotal().compareTo(ultimi.get(ultimi.get(1)).getMaturedTotal()) > 0) {
                                sendBlockNotification(ctx, mPool.toString() + " has found " + ultimi.get(ultimi.get(0)).getMaturedTotal() + " blocks", mPool);
                            }

                        }
                    }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                    VolleyLog.d(TAG, "Error: " + error.getMessage());
                }
            });


            if (minerAddr != null) {
                Log.i(TAG, "refreshing wallet " + minerAddr + " notify: " + notifyBlock);
                JsonObjectRequest jsonObjReqWallet = new JsonObjectRequest(Request.Method.GET,
                        Utils.getWalletStatsUrl(PreferenceManager.getDefaultSharedPreferences(ctx)) + minerAddr, null,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                Log.d(TAG, response.toString());
                                Gson gson = builder.create();
                                // Register an adapter to manage the date types as long values
                                Wallet retrieved = gson.fromJson(response.toString(), Wallet.class);
                                mDbHelper.logWalletStats(retrieved);
                                //dati semi grezzi

                                LinkedMap<Date, Wallet> ultimi = mDbHelper.getLastWallets(LAST_TWO);
                                //controllo se manca qualcuno
                                if (notifyOffline && ultimi.keySet().size() >= LAST_TWO) {
                                    if (ultimi.get(ultimi.firstKey()).getWorkersOnline() < ultimi.get(ultimi.get(1)).getWorkersOnline()) {
                                        sendOfflineNotification(ctx, "A Worker has gone OFFLINE. Online Workers: " + ultimi.get(ultimi.firstKey()).getWorkersOnline(), mPool);
                                    } else if (ultimi.get(ultimi.firstKey()).getWorkersOnline() > ultimi.get(ultimi.get(1)).getWorkersOnline()) {
                                        //togli notifiche di offline
                                        mNotifyMgr.cancel(NOTIFICATION_MINER_OFFLINE);
                                    } // else uguali, fa nulla
                                }
                                if (notifyPayment && ultimi.keySet().size() >= LAST_TWO &&
                                        ultimi.get(ultimi.firstKey()).getPayments().size() > ultimi.get(ultimi.get(1)).getPayments().size()) {
                                    sendPaymentNotification(ctx, "You received a payment: " +
                                            Utils.formatEthCurrency(ctx,ultimi.get(ultimi.firstKey()).getPayments().get(0).getAmount()), mCur.toString() + " payment from " + mPool.toString());
                                }
                            }
                        }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        VolleyLog.d(TAG, "Error: " + error.getMessage());
                        // hide the progress dialog
                    }
                });
                JSONClientSingleton.getInstance(ctx).addToRequestQueue(jsonObjReqWallet);
            }
            // Adding request to request queue
            JSONClientSingleton.getInstance(ctx).addToRequestQueue(jsonObjReq);

            //REFRESH coin values sincrono
            Log.e(TAG, "SERVICE UPDATING CURRENCIES");
            Utils.synchCurrenciesFromCoinmarketcap(ctx, mCur);

        }catch (Exception se){
            Log.e(TAG, "SERVICE ERROR: "+se);
            Crashlytics.logException(se);
        }

        return false; // Answers the question: "Is there still work going on?"
    }

    @Override
    public boolean onStopJob(JobParameters job) {
        return false; // Answers the question: "Should this job be retried?"
    }

    private void sendOfflineNotification(Context ctx, String contentText, PoolEnum pool) {
        Intent resultIntent = new Intent(ctx, WalletActivity.class);
        // Because clicking the notification opens a new ("special") activity, there's
        // no need to create an artificial back stack.
        PendingIntent resultPendingIntent =
                PendingIntent.getActivity(
                        ctx,
                        0,
                        resultIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(ctx)
                        .setSmallIcon(R.drawable.ic_money_off_black_24dp)
                        .setContentTitle("One of your "+pool.toString() +" workers went offline")
                        .setCategory(CATEGORY_SERVICE)
                        .setAutoCancel(true)
                        .setPriority(PRIORITY_LOW)
                        .setContentText(contentText);
        mBuilder.setContentIntent(resultPendingIntent);
        mBuilder.setVibrate(new long[]{1000, 1000});
        //LED
        mBuilder.setLights(Color.WHITE, 500, 500);

        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        mBuilder.setSound(alarmSound);

        // Gets an instance of the NotificationManager service
        NotificationManager mNotifyMgr =
                (NotificationManager) ctx.getSystemService(NOTIFICATION_SERVICE);
        // Builds the notification and issues it.
        mNotifyMgr.notify(NOTIFICATION_MINER_OFFLINE, mBuilder.build());

    }

    private void sendBlockNotification(Context ctx, String contentText, PoolEnum pool) {
        Intent resultIntent = new Intent(ctx, BlocksActivity.class);
        // Because clicking the notification opens a new ("special") activity, there's
        // no need to create an artificial back stack.
        PendingIntent resultPendingIntent =
                PendingIntent.getActivity(
                        ctx,
                        0,
                        resultIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(ctx)
                        .setSmallIcon(R.drawable.ic_insert_link_chain_24dp)
                        .setContentTitle("Block Found on " + pool.toString())
                        .setCategory(CATEGORY_PROGRESS)
                        .setAutoCancel(true)
                        .setContentText(contentText);
        mBuilder.setContentIntent(resultPendingIntent);
        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        mBuilder.setSound(alarmSound);
        //Vibration
        mBuilder.setVibrate(new long[]{1000, 1000});
        //LED
        mBuilder.setLights(Color.WHITE, 3000, 3000);
        // Sets an ID for the notification
        int mNotificationId = 13;
        // Gets an instance of the NotificationManager service
        NotificationManager mNotifyMgr =
                (NotificationManager) ctx.getSystemService(NOTIFICATION_SERVICE);
        // Builds the notification and issues it.
        mNotifyMgr.notify(mNotificationId, mBuilder.build());
    }

    private void sendPaymentNotification(Context ctx, String contentText, String contentTitle) {
        Intent resultIntent = new Intent(ctx, PaymentsActivity.class);
        // Because clicking the notification opens a new ("special") activity, there's
        // no need to create an artificial back stack.
        PendingIntent resultPendingIntent =
                PendingIntent.getActivity(
                        ctx,
                        0,
                        resultIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(ctx)
                        .setSmallIcon(R.drawable.ic_payment_black_24dp)
                        .setContentTitle(contentTitle)
                        .setCategory(CATEGORY_PROGRESS)
                        .setAutoCancel(true)
                        .setContentText(contentText);
        mBuilder.setContentIntent(resultPendingIntent);
        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        mBuilder.setSound(alarmSound);
        //Vibration
        mBuilder.setVibrate(new long[]{1000, 1000, 1000});
        //LED
        mBuilder.setLights(Color.WHITE, 500, 1000);
        // Sets an ID for the notification
        int mNotificationId = 14;
        // Gets an instance of the NotificationManager service
        NotificationManager mNotifyMgr =
                (NotificationManager) ctx.getSystemService(NOTIFICATION_SERVICE);
        // Builds the notification and issues it.
        mNotifyMgr.notify(mNotificationId, mBuilder.build());
    }

    @NonNull
    public static Job getJobUpdate(SharedPreferences prefs, FirebaseJobDispatcher dispatcher) {
        Bundle myExtrasBundle = new Bundle();
        Integer intervalMsec = Integer.valueOf(prefs.getString("pref_sync_freq", "" + AlarmManager.INTERVAL_HALF_HOUR)) /1000;
        myExtrasBundle.putString("WALLETURL", prefs.getString("wallet_addr", null));
        myExtrasBundle.putBoolean("NOTIFY_BLOCK", prefs.getBoolean("pref_notify_block", true));
        myExtrasBundle.putBoolean("NOTIFY_OFFLINE", prefs.getBoolean("pref_notify_offline", true));
        myExtrasBundle.putBoolean("NOTIFY_PAYMENT", prefs.getBoolean("pref_notify_payment", true));
        return dispatcher.newJobBuilder()
                // the JobService that will be called
                .setService(MPWService.class)
                // uniquely identifies the job
                .setTag("mpw-updater")
                // one-off job
                .setRecurring(true)
                // don't persist past a device reboot
                .setLifetime(Lifetime.FOREVER)
                // start between freq and 300 seconds tolerance
                .setTrigger(periodicTrigger(intervalMsec, 300))
                // don't overwrite an existing job with the same tag
                .setReplaceCurrent(true)
                // retry with exponential backoff
                //.setRetryStrategy(RetryStrategy.DEFAULT_EXPONENTIAL)
                // constraints that need to be satisfied for the job to run
                .setExtras(myExtrasBundle)
                .build();
    }

    public static JobTrigger periodicTrigger(int frequency, int tolerance) {
        return Trigger.executionWindow(frequency - tolerance, frequency);
    }
}