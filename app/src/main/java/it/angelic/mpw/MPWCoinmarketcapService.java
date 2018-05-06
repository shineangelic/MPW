package it.angelic.mpw;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.preference.PreferenceManager;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonArrayRequest;
import com.crashlytics.android.Crashlytics;
import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.Job;
import com.firebase.jobdispatcher.JobParameters;
import com.firebase.jobdispatcher.JobService;
import com.firebase.jobdispatcher.JobTrigger;
import com.firebase.jobdispatcher.Lifetime;
import com.firebase.jobdispatcher.RetryStrategy;
import com.firebase.jobdispatcher.Trigger;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;

import java.lang.reflect.Type;
import java.util.List;

import it.angelic.mpw.model.enums.CurrencyEnum;
import it.angelic.mpw.model.enums.PoolEnum;
import it.angelic.mpw.model.jsonpojos.coinmarketcap.Ticker;

import static it.angelic.mpw.Constants.ETHER_STATS_COIN_LIMIT;
import static it.angelic.mpw.Constants.TAG;

public class MPWCoinmarketcapService extends JobService {
    final int NOTIFICATION_MINER_OFFLINE = 12;

    @Override
    public boolean onStartJob(JobParameters job) {
        Log.e(TAG, "SERVICE2 START");
        final Context ctx = MPWCoinmarketcapService.this;
        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(ctx);
        try {
            final PoolEnum mPool = PoolEnum.valueOf(prefs.getString("poolEnum", ""));
            final CurrencyEnum mCur = CurrencyEnum.valueOf(prefs.getString("curEnum", ""));
            Log.w(TAG, "Miner Pool Watcher Coinmarketcap Service call:" + Utils.getHomeStatsURL(PreferenceManager.getDefaultSharedPreferences(ctx)));
            Log.i(TAG, "SERVICE MARKETCAP working on:" +mPool.toString() + " - " + mCur.toString());
            //load extra

            //REFRESH coin values sincrono
            Log.e(TAG, "SERVICE MARKETCAP UPDATING CURRENCIES");
            asynchCurrenciesFromCoinmarketcap(ctx, mCur, job);

        }catch (Exception se){
            Log.e(TAG, "SERVICE MARKETCAP ERROR: "+se);
            Crashlytics.logException(se);
        }

        return true; // Answers the question: "Is there still work going on?"
    }

    private  void asynchCurrenciesFromCoinmarketcap(final Context ctx, final CurrencyEnum mCur,final JobParameters job) {
        try {
            JsonArrayRequest jsonArrayCurrenciesReq = new JsonArrayRequest(Request.Method.GET,
                    Constants.ETHER_STATS_URL + ETHER_STATS_COIN_LIMIT, null,
                    new Response.Listener<JSONArray>() {

                        @Override
                        public void onResponse(final JSONArray response) {
                            Log.d(Constants.TAG, response.toString());
                            GsonBuilder builder = new GsonBuilder();
                            Gson gson = builder.create();
                            Log.d(Constants.TAG, response.toString());
                            Type listType = new TypeToken<List<Ticker>>() {
                            }.getType();
                            List<Ticker> posts = gson.fromJson(response.toString(), listType);
                            Ticker fnd = null;
                            for (Ticker currency : posts) {
                                if (mCur.name().equalsIgnoreCase(currency.getSymbol()) || mCur.toString().equalsIgnoreCase(currency.getName())) {
                                    fnd = currency;
                                }
                                //always save ETH
                                if (CurrencyEnum.ETH.name().equalsIgnoreCase(currency.getSymbol())) {
                                    CryptoSharedPreferencesUtils.saveEthereumValues(currency, ctx);
                                }
                                //always save BTC
                                if (CurrencyEnum.BTC.name().equalsIgnoreCase(currency.getSymbol())) {
                                    CryptoSharedPreferencesUtils.saveBtcValues(currency, ctx);
                                }
                            }
                            //eventually resets  when fnd = null
                            CryptoSharedPreferencesUtils.saveEtherValues(fnd, ctx);
                            Log.e(TAG, "SERVICE MARKETCAP END Ok2");
                            MPWCoinmarketcapService.this.jobFinished(job,false);

                        }
                    }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e(TAG, "SERVICE MARKETCAP END KO2");
                    VolleyLog.d(Constants.TAG, "Error: " + error.getMessage());
                    Crashlytics.logException(error);
                    MPWCoinmarketcapService.this.jobFinished(job,true);
                }
            });

            // Adding request to request queue
            JSONClientSingleton.getInstance(ctx).addToRequestQueue(jsonArrayCurrenciesReq);
        } catch (Exception e) {
            Log.d(Constants.TAG, "ERROR DURING COINMARKETCAP: " + e.getMessage());
        }
    }

    @Override
    public boolean onStopJob(JobParameters job) {
        return false; // Answers the question: "Should this job be retried?"
    }



    @NonNull
    public static Job getJobUpdate(  FirebaseJobDispatcher dispatcher) {
        Bundle myExtrasBundle = new Bundle();

        Log.w(Constants.TAG,"Built JobBuilder Coinmarketcap");
        return dispatcher.newJobBuilder()
                // the JobService that will be called
                .setService(MPWCoinmarketcapService.class)
                // uniquely identifies the job
                .setTag("mpw-cmc-updater")
                // one-off job
                .setRecurring(false)

                // don't persist past a device reboot
                .setLifetime(Lifetime.UNTIL_NEXT_BOOT)
                // start between freq and 300 seconds tolerance
                .setTrigger(Trigger.executionWindow(2,30))
                // don't overwrite an existing job with the same tag
                .setReplaceCurrent(false)
                // retry with exponential backoff
                .setRetryStrategy(RetryStrategy.DEFAULT_EXPONENTIAL)
                // constraints that need to be satisfied for the job to run
                .setExtras(myExtrasBundle)
                .build();
    }


}