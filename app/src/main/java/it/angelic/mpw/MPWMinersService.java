package it.angelic.mpw;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;
import android.support.v7.preference.PreferenceManager;
import android.util.Log;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.crashlytics.android.Crashlytics;
import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.Job;
import com.firebase.jobdispatcher.JobParameters;
import com.firebase.jobdispatcher.JobService;
import com.firebase.jobdispatcher.Lifetime;
import com.firebase.jobdispatcher.RetryStrategy;
import com.firebase.jobdispatcher.Trigger;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import it.angelic.mpw.model.MyDateTypeAdapter;
import it.angelic.mpw.model.MyTimeStampTypeAdapter;
import it.angelic.mpw.model.db.MinerDBRecord;
import it.angelic.mpw.model.db.PoolDbHelper;
import it.angelic.mpw.model.enums.CurrencyEnum;
import it.angelic.mpw.model.enums.MinerSortEnum;
import it.angelic.mpw.model.enums.PoolEnum;
import it.angelic.mpw.model.jsonpojos.wallet.Payment;
import it.angelic.mpw.model.jsonpojos.wallet.Wallet;

import static it.angelic.mpw.Constants.TAG;

/**
 * Service to retrieve and save other miners' detail By wallet JSON call
 */
public class MPWMinersService extends JobService {

    private PoolDbHelper mDbHelper;

    @NonNull
    public static Job getJobUpdate(FirebaseJobDispatcher dispatcher) {
        // Bundle myExtrasBundle = new Bundle();

        Log.w(Constants.TAG, "Built JobBuilder Coinmarketcap");
        return dispatcher.newJobBuilder()
                // the JobService that will be called
                .setService(MPWMinersService.class)
                // uniquely identifies the job
                .setTag("mpw-miners-updater")
                // one-off job
                .setRecurring(false)

                // don't persist past a device reboot
                .setLifetime(Lifetime.UNTIL_NEXT_BOOT)
                // start between freq and 300 seconds tolerance
                .setTrigger(Trigger.executionWindow(1, 10))
                // don't overwrite an existing job with the same tag
                .setReplaceCurrent(true)
                // retry with exponential backoff
                .setRetryStrategy(RetryStrategy.DEFAULT_LINEAR)
                // constraints that need to be satisfied for the job to run
                //.setExtras(myExtrasBundle)
                .build();
    }

    @Override
    public boolean onStartJob(JobParameters job) {
        Log.e(TAG, "SERVICE MINERS START");
        final Context ctx = MPWMinersService.this;
        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(ctx);
        try {
            final PoolEnum mPool = PoolEnum.valueOf(prefs.getString("poolEnum", ""));
            final CurrencyEnum mCur = CurrencyEnum.valueOf(prefs.getString("curEnum", ""));
            Log.w(TAG, "Miner Pool Watcher MINERS Service call:" + Utils.getHomeStatsURL(PreferenceManager.getDefaultSharedPreferences(ctx)));
            Log.i(TAG, "SERVICE MINERS working on:" + mPool.toString() + " - " + mCur.toString());

            mDbHelper = PoolDbHelper.getInstance(this, mPool, mCur);
            final GsonBuilder builder = new GsonBuilder();
            //gestione UNIX time lungo e non
            builder.registerTypeAdapter(Date.class, new MyDateTypeAdapter());
            builder.registerTypeAdapter(Calendar.class, new MyTimeStampTypeAdapter());


            Log.e(TAG, "SERVICE MINERS UPDATING");
            ArrayList<MinerDBRecord> miners = mDbHelper.getMinerList(MinerSortEnum.LAST_SEEN);//ordinamento irrilevante
            //choose a random one if no empty
            for (MinerDBRecord miner : miners) {
                fetchMinerStats(miner, builder);
            }

        } catch (Exception se) {
            Log.e(TAG, "SERVICE MINERS ERROR: " + se);
            Crashlytics.logException(se);
            MPWMinersService.this.jobFinished(job, true);
            Log.e(TAG, "SERVICE MINERS END KO");
            return false;
        }
        //db.close();
        MPWMinersService.this.jobFinished(job, false);
        Log.e(TAG, "SERVICE MINERS END Ok");
        return true; // Answers the question: "Is there still work going on?"
    }

    private void fetchMinerStats(final MinerDBRecord rec, final GsonBuilder builder) {
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(
                Utils.getWalletStatsUrl(PreferenceManager.getDefaultSharedPreferences(this)) + rec.getAddress(), null,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        //update known miners TABLE
                        Log.d(Constants.TAG, response.toString());
                        Gson gson = builder.create();
                        // Register an adapter to manage the date types as long values
                        final Wallet retrieved = gson.fromJson(response.toString(), Wallet.class);

                        if (retrieved.getWorkersTotal() > (rec.getTopMiners() == null ? -1 : rec.getTopMiners()))
                            rec.setTopMiners(retrieved.getWorkersTotal());
                        if (retrieved.getCurrentHashrate() > (rec.getTopHr() == null ? -1 : rec.getTopHr()))
                            rec.setTopHr(retrieved.getCurrentHashrate());

                        rec.setPaid(retrieved.getStats().getPaid());
                        try {//compute first paymt
                            Payment pp = retrieved.getPayments().get(retrieved.getPayments().size() - 1);
                            rec.setFirstSeen(pp.getTimestamp());
                        } catch (Exception io) {
                            //dont look back in anger
                        }
                        rec.setAvgHr(rec.getAvgHr() == null ? retrieved.getHashrate() : ((rec.getAvgHr() + retrieved.getHashrate()) / 2));
                        // aggiorna UI
                        rec.setLastSeen(retrieved.getStats().getLastShare());
                        rec.setBlocksFound(retrieved.getStats().getBlocksFound());

                        mDbHelper.updateMiner(rec);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.e(Constants.TAG, "Miner Service Error: " + error.getMessage());
            }
        });

        // Adding request to request queue
        JSONClientSingleton.getInstance(this).addToRequestQueue(jsonObjReq);

    }

    @Override
    public boolean onStopJob(JobParameters job) {
        return false; // Answers the question: "Should this job be retried?"
    }
}