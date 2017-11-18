package it.angelic.noobpoolstats;

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

import java.util.Calendar;
import java.util.Date;

import im.dacer.androidcharts.LineView;
import it.angelic.noobpoolstats.model.MyDateTypeAdapter;
import it.angelic.noobpoolstats.model.MyTimeStampTypeAdapter;
import it.angelic.noobpoolstats.model.db.NoobPoolQueryGrouper;
import it.angelic.noobpoolstats.model.jsonpojos.home.HomeStats;

import static org.junit.Assert.assertEquals;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() throws Exception {
        assertEquals(4, 2 + 2);
    }


}