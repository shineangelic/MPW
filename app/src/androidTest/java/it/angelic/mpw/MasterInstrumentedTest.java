package it.angelic.mpw;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.support.v7.preference.PreferenceManager;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.ads.internal.gmsg.HttpClient;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

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
 * <p>
 * Copy this test to new pools
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class MasterInstrumentedTest {
    private final PoolEnum toBeTested = PoolEnum.NOOBPOOL;
    private Context appContext;
    private SharedPreferences sharedPreferences;
    private String minerAddr;
    private HomeStats retrievedHomeStats;

    @Before
    public void useAppContext() {
        // Context of the app under test.
        appContext = InstrumentationRegistry.getTargetContext();
        String fileName = "FILE_NAME";
        assertEquals("it.angelic.mpw", appContext.getPackageName());
        sharedPreferences = appContext.getSharedPreferences(fileName, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("poolEnum", toBeTested.name());
        editor.putString("curEnum", CurrencyEnum.ETH.name());
        editor.commit();
        assertNotNull(sharedPreferences);
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

    public void testURL(String strUrl) throws Exception {
        try {
            URL url = new URL(strUrl);
            HttpURLConnection urlConn = (HttpURLConnection) url.openConnection();
            urlConn.connect();
            assertEquals(HttpURLConnection.HTTP_OK, urlConn.getResponseCode());
        } catch (IOException e) {
            System.err.println("Error creating HTTP connection");
            e.printStackTrace();
            throw e;
        }
    }

    @Test
    public void testBaseExplorer() {
        List<CurrencyEnum> curs = toBeTested.getSupportedCurrencies();
        for (CurrencyEnum cur : curs) {
            if (cur.getScannerSite() != null) {
                try {
                   testURL(cur.getScannerSite().getBaseAddress().toString());
                } catch (Exception e) {
                    fail("Blockchain explorer error for "+ cur.toString());
                }

            }
        }
    }



    @Test
    public void testJsonHomeStatsRequest() {
        final GsonBuilder builder = getGsonBuilder();

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                Utils.getHomeStatsURL(sharedPreferences), null,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(final JSONObject response) {
                        Log.d(Constants.TAG, response.toString());
                        Gson gson = builder.create();
                        // Register an adapter to manage the date types as long values
                        retrievedHomeStats = gson.fromJson(response.toString(), HomeStats.class);
                        assertNotNull(retrievedHomeStats);
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

    @NonNull
    private GsonBuilder getGsonBuilder() {
        final GsonBuilder builder = new GsonBuilder();
        //gestione UNIX time lungo e non
        builder.registerTypeAdapter(Date.class, new MyDateTypeAdapter());
        builder.registerTypeAdapter(Calendar.class, new MyTimeStampTypeAdapter());
        return builder;
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
                        Block retrievedBlocks = gson.fromJson(response.toString(), Block.class);
                        assertNotNull(retrievedBlocks);

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
        final GsonBuilder builder = getGsonBuilder();
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
                        assertNotNull(minerAddr);
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
    public void testJsonWalletRequest() {

        final GsonBuilder builder = getGsonBuilder();
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                Utils.getWalletStatsUrl(sharedPreferences) + minerAddr, null,
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


    /**
     * This cant be done async
     *
     @After public void testZPersist()  {
     // Context of the app under test.
     String mPool = sharedPreferences.getString("poolEnum", "");
     String mCur = sharedPreferences.getString("curEnum", "");
     PoolDbHelper db = new PoolDbHelper(appContext,PoolEnum.valueOf(mPool),CurrencyEnum.valueOf(mCur));
     assertNotNull(minerAddr);
     assertNotNull(retrievedHomeStats);
     db.logHomeStats(retrievedHomeStats);
     assertNotNull(db.getLastHomeStats(1));
     PoolDbHelper.cleanOldData(db.getWritableDatabase());
     }
     */
}
