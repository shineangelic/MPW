package it.angelic.noobpoolstats;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
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
import it.angelic.noobpoolstats.model.jsonpojos.wallet.Worker;

import static android.content.Context.NOTIFICATION_SERVICE;

/**
 * Receive per controllo esecuzione servizio. Viene invocato dopo il boot, e
 * all'USER_PRESENT
 * http://www.hascode.com/2011/11/managing-background-tasks-on-android
 * -using-the-alarm-manager/
 */
public class WatchDogEventReceiver extends BroadcastReceiver {


    @Override
    public void onReceive(final Context ctx, final Intent intent) {
        Log.i(MainActivity.TAG, "DOG called");
        final NoobPoolDbHelper mDbHelper = new NoobPoolDbHelper(ctx);
        final GsonBuilder builder = new GsonBuilder();
        //gestione UNIX time lungo e non
        builder.registerTypeAdapter(Date.class, new MyDateTypeAdapter());
        builder.registerTypeAdapter(Calendar.class, new MyTimeStampTypeAdapter());

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                MainActivity.homeStatsUrl, null,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(MainActivity.TAG, response.toString());

                        Gson gson = builder.create();
                        // Register an adapter to manage the date types as long values
                        HomeStats retrieved = gson.fromJson(response.toString(), HomeStats.class);
                        mDbHelper.logHomeStats(retrieved);
                        //dati semi grezzi

                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(MainActivity.TAG, "Error: " + error.getMessage());
                // hide the progress dialog
            }
        });
        final String minerAddr = intent.getStringExtra("WALLETURL");
        final Boolean notify = intent.getBooleanExtra("NOTIFY", false);

        if (minerAddr != null) {
            Log.i(MainActivity.TAG, "refreshing wallet " + minerAddr + " notify: "+ notify);
            JsonObjectRequest jsonObjReqWallet = new JsonObjectRequest(Request.Method.GET,
                    MinerActivity.minerStatsUrl + minerAddr, null,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            Log.d(MainActivity.TAG, response.toString());
                            Gson gson = builder.create();
                            // Register an adapter to manage the date types as long values
                            Wallet retrieved = gson.fromJson(response.toString(), Wallet.class);
                            mDbHelper.logWalletStats(retrieved);
                            //dati semi grezzi
                            LinkedMap<Date, Wallet> ultimi = mDbHelper.getLastTwoWallet();
                            //controllo se manca qualcuno
                            if (notify && ultimi.get(ultimi.get(0)).getWorkersOnline() < ultimi.get(ultimi.get(1)).getWorkersOnline()) {
                                //trovo chi manca
                                for (String workerInFullSet  : ultimi.get(ultimi.get(1)).getWorkers().keySet()){
                                    if (!ultimi.get(ultimi.get(0)).getWorkers().keySet().contains(workerInFullSet)){
                                        Worker missingOne = ultimi.get(ultimi.get(1)).getWorkers().get(workerInFullSet);
                                        Calendar ref = Calendar.getInstance();
                                        ref.setTime(missingOne.getLastBeat());
                                        sendOfflineNotification(ctx, "Worker "+workerInFullSet +" is OFFLINE. Last beat" +Utils.getTimeAgo(ref)+" ago ");
                                    }
                                }
                            }

                        }
                    }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                    VolleyLog.d(MainActivity.TAG, "Error: " + error.getMessage());
                    // hide the progress dialog
                }
            });
            NoobJSONClientSingleton.getInstance(ctx).addToRequestQueue(jsonObjReqWallet);
        }
        // Adding request to request queue
        NoobJSONClientSingleton.getInstance(ctx).addToRequestQueue(jsonObjReq);

    }

    private void sendOfflineNotification(Context ctx,String contentText ) {
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
                        .setSmallIcon(R.drawable.ic_insert_link_chain_24dp)
                        .setContentTitle("One of your workers went offline")
                        .setContentText(contentText);
        mBuilder.setContentIntent(resultPendingIntent);

        // Sets an ID for the notification
        int mNotificationId = 001;
        // Gets an instance of the NotificationManager service
        NotificationManager mNotifyMgr =
                (NotificationManager) ctx.getSystemService(NOTIFICATION_SERVICE);
        // Builds the notification and issues it.
        mNotifyMgr.notify(mNotificationId, mBuilder.build());
    }


}