package it.angelic.noobpoolstats;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.gson.JsonObject;

import org.json.JSONObject;

/**
 * Created by shine@angelic.it on 08/11/2017.
 */

class NoobJsonObjectRequest extends JsonObjectRequest {
    public NoobJsonObjectRequest(String url, JSONObject o, Response.Listener<JSONObject> listener, Response.ErrorListener errorListener) {
        super(Request.Method.GET,url,o,listener,errorListener);
    }
}
