package it.angelic.mpw;

import android.content.Context;
import android.text.TextUtils;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

/**
 * Created by shine@angelic.it on 06/09/2017.
 */

public class JSONClientSingleton {

    private static final String TAG = JSONClientSingleton.class
            .getSimpleName();
    private final Context mCtx;
    // Instantiate the cache


    // Instantiate the RequestQueue.
    private RequestQueue queue;

    //itself instance
    private static JSONClientSingleton mInstance;

    private JSONClientSingleton(Context context) {
        mCtx = context;
        queue = getRequestQueue();
    }

    public static synchronized JSONClientSingleton getInstance(Context cx) {
        if (mInstance == null) {
            mInstance = new JSONClientSingleton(cx);
        }
        return mInstance;
    }

    private RequestQueue getRequestQueue() {
        if (queue == null) {
            queue = Volley.newRequestQueue(mCtx.getApplicationContext());
        }
        return queue;
    }

    public <T> void addToRequestQueue(Request<T> req, String tag) {
        // set the default tag if tag is empty
        req.setTag(TextUtils.isEmpty(tag) ? TAG : tag);
        getRequestQueue().add(req);
    }

    public <T> void addToRequestQueue(Request<T> req) {
        req.setTag(TAG);
        getRequestQueue().add(req);
    }
}
