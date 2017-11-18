package it.angelic.noobpoolstats;

import android.content.Context;
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
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Calendar;
import java.util.Date;

import it.angelic.noobpoolstats.model.MyDateTypeAdapter;
import it.angelic.noobpoolstats.model.MyTimeStampTypeAdapter;
import it.angelic.noobpoolstats.model.jsonpojos.home.HomeStats;

import static org.junit.Assert.assertEquals;

/**
 * Instrumentation test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {
    @Test
    public void useAppContext() throws Exception {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();

        assertEquals("it.angelic.noobpoolstats", appContext.getPackageName());
    }

    @Test
    public void testJsonRequest() throws Exception {
        final GsonBuilder builder = new GsonBuilder();
        //gestione UNIX time lungo e non
        builder.registerTypeAdapter(Date.class, new MyDateTypeAdapter());
        builder.registerTypeAdapter(Calendar.class, new MyTimeStampTypeAdapter());

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                Constants.HOME_STATS_URL, null,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(final JSONObject response) {
                        Log.d(Constants.TAG, response.toString());

                        Gson gson = builder.create();
                        // Register an adapter to manage the date types as long values
                        HomeStats retrieved = gson.fromJson(response.toString(), HomeStats.class);

                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(Constants.TAG, "Error: " + error.getMessage());
                // hide the progress dialog
            }
        });

        // Adding request to request queue
        NoobJSONClientSingleton.getInstance(InstrumentationRegistry.getTargetContext()).addToRequestQueue(jsonObjReq);

    }
}
