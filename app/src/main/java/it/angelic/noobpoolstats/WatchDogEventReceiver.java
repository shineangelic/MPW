package it.angelic.noobpoolstats;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.apache.commons.collections4.map.LinkedMap;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.Date;

import it.angelic.noobpoolstats.model.MyDateTypeAdapter;
import it.angelic.noobpoolstats.model.MyTimeStampTypeAdapter;
import it.angelic.noobpoolstats.model.db.NoobPoolDbHelper;
import it.angelic.noobpoolstats.model.jsonpojos.home.HomeStats;
import it.angelic.noobpoolstats.model.jsonpojos.wallet.Wallet;

import static android.content.Context.NOTIFICATION_SERVICE;
import static android.support.v4.app.NotificationCompat.CATEGORY_PROGRESS;
import static android.support.v4.app.NotificationCompat.CATEGORY_SERVICE;
import static android.support.v4.app.NotificationCompat.PRIORITY_LOW;
import static it.angelic.noobpoolstats.Constants.LAST_TWO;

/**
 * Receive per controllo esecuzione servizio. Viene invocato dopo il boot, e
 * all'USER_PRESENT
 * http://www.hascode.com/2011/11/managing-background-tasks-on-android
 * -using-the-alarm-manager/
 */
public class WatchDogEventReceiver extends BroadcastReceiver {
    final int NOTIFICATION_MINER_OFFLINE = 12;

    @Override
    public void onReceive(final Context ctx, final Intent intent) {
        Log.i(Constants.TAG, "NoobPool Service call");
        final NoobPoolDbHelper mDbHelper = new NoobPoolDbHelper(ctx);
        final NotificationManager mNotifyMgr =
                (NotificationManager) ctx.getSystemService(NOTIFICATION_SERVICE);
        final GsonBuilder builder = new GsonBuilder();
        //gestione UNIX time lungo e non
        builder.registerTypeAdapter(Date.class, new MyDateTypeAdapter());
        builder.registerTypeAdapter(Calendar.class, new MyTimeStampTypeAdapter());
        //load extra
        final String minerAddr = intent.getStringExtra("WALLETURL");
        final Boolean notifyBlock = intent.getBooleanExtra("NOTIFY_BLOCK", false);
        final Boolean notifyOffline = intent.getBooleanExtra("NOTIFY_OFFLINE", false);
        final Boolean notifyPayment = intent.getBooleanExtra("NOTIFY_PAYMENT", false);

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                Constants.HOME_STATS_URL, null,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(Constants.TAG, response.toString());
                        Gson gson = builder.create();
                        // Register an adapter to manage the date types as long values
                        HomeStats retrieved = gson.fromJson(response.toString(), HomeStats.class);
                        mDbHelper.logHomeStats(retrieved);
                        //dati semi grezzi
                        LinkedMap<Date, HomeStats> ultimi = mDbHelper.getLastHomeStats(LAST_TWO);
                        //controllo se manca qualcuno
                        if (notifyBlock
                                &&
                                ultimi.get(ultimi.get(0)).getMaturedTotal().compareTo(ultimi.get(ultimi.get(1)).getMaturedTotal()) > 0 ) {
                            sendBlockNotification(ctx, "NoobPool has found a new block");
                        }

                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(Constants.TAG, "Error: " + error.getMessage());
            }
        });


        if (minerAddr != null) {
            Log.i(Constants.TAG, "refreshing wallet " + minerAddr + " notify: " + notifyBlock);
            JsonObjectRequest jsonObjReqWallet = new JsonObjectRequest(Request.Method.GET,
                    MinerActivity.minerStatsUrl + minerAddr, null,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            Log.d(Constants.TAG, response.toString());
                            Gson gson = builder.create();
                            // Register an adapter to manage the date types as long values
                            Wallet retrieved = gson.fromJson(response.toString(), Wallet.class);
                            mDbHelper.logWalletStats(retrieved);
                            //dati semi grezzi

                            LinkedMap<Date, Wallet> ultimi = mDbHelper.getLastWallets(LAST_TWO);
                            //controllo se manca qualcuno
                            if (notifyOffline && ultimi.keySet().size() >= LAST_TWO) {
                                if (ultimi.get(ultimi.firstKey()).getWorkersOnline() < ultimi.get(ultimi.get(1)).getWorkersOnline()) {
                                    sendOfflineNotification(ctx, "A Worker has gone OFFLINE. Online Workers: " + ultimi.get(ultimi.firstKey()).getWorkersOnline());
                                } else if (ultimi.get(ultimi.firstKey()).getWorkersOnline() > ultimi.get(ultimi.get(1)).getWorkersOnline()){
                                    //togli notifiche di offline
                                    mNotifyMgr.cancel(NOTIFICATION_MINER_OFFLINE);
                                } // else uguali, fa nulla
                            }
                            if (notifyPayment && ultimi.keySet().size() >= LAST_TWO &&
                                    ultimi.get(ultimi.firstKey()).getPayments().size() > ultimi.get(ultimi.get(1)).getPayments().size()) {
                                sendPaymentNotification(ctx, "You received a payment: " +
                                        Utils.formatEthCurrency(ultimi.get(ultimi.firstKey()).getPayments().get(0).getAmount()));
                            }
                        }
                    }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                    VolleyLog.d(Constants.TAG, "Error: " + error.getMessage());
                    // hide the progress dialog
                }
            });
            NoobJSONClientSingleton.getInstance(ctx).addToRequestQueue(jsonObjReqWallet);
        }
        // Adding request to request queue
        NoobJSONClientSingleton.getInstance(ctx).addToRequestQueue(jsonObjReq);

    }

    private void sendOfflineNotification(Context ctx, String contentText) {
        Intent resultIntent = new Intent(ctx, MinerActivity.class);
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
                        .setContentTitle("One of your workers went offline")
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

    private void sendBlockNotification(Context ctx, String contentText) {
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
                        .setContentTitle("NoobPool Block Found")
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

    private void sendPaymentNotification(Context ctx, String contentText) {
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
                        .setContentTitle("Eth payment from NoobPool")
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
}