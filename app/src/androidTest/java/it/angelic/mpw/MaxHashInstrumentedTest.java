package it.angelic.mpw;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONObject;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;

import java.util.Calendar;
import java.util.Date;

import it.angelic.mpw.model.MyDateTypeAdapter;
import it.angelic.mpw.model.MyTimeStampTypeAdapter;
import it.angelic.mpw.model.enums.CurrencyEnum;
import it.angelic.mpw.model.enums.PoolEnum;
import it.angelic.mpw.model.jsonpojos.blocks.Block;
import it.angelic.mpw.model.jsonpojos.home.HomeStats;
import it.angelic.mpw.model.jsonpojos.miners.MinerRoot;
import it.angelic.mpw.model.jsonpojos.wallet.Wallet;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

/**
 * Instrumentation test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class MaxHashInstrumentedTest {
    private Context appContext;
    private SharedPreferences sharedPreferences;
    private String minerAddr;

    @Before
    public void useAppContext() {
        // Context of the app under test.
        appContext = InstrumentationRegistry.getTargetContext();
        String fileName = "FILE_NAME";
        assertEquals("it.angelic.mpw", appContext.getPackageName());
        sharedPreferences = appContext.getSharedPreferences(fileName, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("poolEnum", PoolEnum.MAXHASH.name());
        editor.putString("curEnum", CurrencyEnum.UBIQ.name());
        editor.commit();
    }

    @Test
    public void testJsonRequest() {
        final GsonBuilder builder = new GsonBuilder();
        //gestione UNIX time lungo e non
        builder.registerTypeAdapter(Date.class, new MyDateTypeAdapter());
        builder.registerTypeAdapter(Calendar.class, new MyTimeStampTypeAdapter());

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                Utils.getHomeStatsURL(sharedPreferences), null,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(final JSONObject response) {
                        Log.d(Constants.TAG, response.toString());
                        Gson gson = builder.create();
                        // Register an adapter to manage the date types as long values
                        HomeStats retrieved = gson.fromJson(response.toString(), HomeStats.class);
                        assertNotNull(retrieved);
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(Constants.TAG, "Error: " + error.getMessage());
                fail();
                // hide the progress dialog
            }
        });
        // Adding request to request queue
        JSONClientSingleton.getInstance(InstrumentationRegistry.getTargetContext()).addToRequestQueue(jsonObjReq);
    }

    @Test
    public void testJsonBlockRequest() {

        final GsonBuilder builder = new GsonBuilder();
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                Utils.getBlocksURL(sharedPreferences), null,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(final JSONObject response) {
                        Log.i(Constants.TAG, response.toString());
                        Gson gson = builder.create();
                        // Register an adapter to manage the date types as long values
                        Block retrieved = gson.fromJson(response.toString(), Block.class);
                        assertNotNull(retrieved);
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(Constants.TAG, "Error: " + error.getMessage());
                fail();
            }
        });

        // Adding request to request queue
        JSONClientSingleton.getInstance(appContext).addToRequestQueue(jsonObjReq);

    }

    @Test
    public void testJsonMinerRequest() {

        final GsonBuilder builder = new GsonBuilder();
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                Utils.getMinersStatsUrl(sharedPreferences), null,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(final JSONObject response) {
                        Log.i(Constants.TAG, response.toString());
                        Gson gson = builder.create();
                        // Register an adapter to manage the date types as long values
                        MinerRoot retrieved = gson.fromJson(response.toString(), MinerRoot.class);
                        minerAddr = retrieved.getMiners().values().iterator().next().getAddress();
                        assertNotNull(retrieved);
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(Constants.TAG, "Error: " + error.getMessage());
                fail();
            }
        });

        // Adding request to request queue
        JSONClientSingleton.getInstance(appContext).addToRequestQueue(jsonObjReq);
    }

    @Test
    public void testJsonWalletRequest() {

        final GsonBuilder builder = new GsonBuilder();
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                Utils.getWalletStatsUrl(sharedPreferences)+ minerAddr, null,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(final JSONObject response) {
                        Log.i(Constants.TAG, response.toString());
                        Gson gson = builder.create();
                        // Register an adapter to manage the date types as long values
                        Wallet retrieved = gson.fromJson(response.toString(), Wallet.class);
                        assertNotNull(retrieved);
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(Constants.TAG, "Error: " + error.getMessage());
            }
        });

        // Adding request to request queue
        JSONClientSingleton.getInstance(appContext).addToRequestQueue(jsonObjReq);
    }

    @Test
    public void testPref() {
        String fileName = "FILE_NAME";

        SharedPreferences sharedPreferences = appContext.getSharedPreferences(fileName, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("key", "value");
        editor.commit();

        SharedPreferences sharedPreferences2 = appContext.getSharedPreferences(fileName, Context.MODE_PRIVATE);
        assertEquals("value", sharedPreferences2.getString("key", null));
    }
}
