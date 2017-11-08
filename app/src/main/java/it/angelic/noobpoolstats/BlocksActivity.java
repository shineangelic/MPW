package it.angelic.noobpoolstats;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONObject;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;

import im.dacer.androidcharts.LineView;
import it.angelic.noobpoolstats.model.MyDateTypeAdapter;
import it.angelic.noobpoolstats.model.MyTimeStampTypeAdapter;
import it.angelic.noobpoolstats.model.db.NoobPoolDbHelper;
import it.angelic.noobpoolstats.model.db.NoobPoolQueryGrouper;
import it.angelic.noobpoolstats.model.jsonpojos.blocks.Block;
import it.angelic.noobpoolstats.model.jsonpojos.blocks.Matured;
import it.angelic.noobpoolstats.model.jsonpojos.home.HomeStats;

public class BlocksActivity extends AppCompatActivity {
    private GsonBuilder builder;
    private TextView textViewBlocksTitle;
    private String blocksUrl  = "http://www.noobpool.com/api/blocks";
    private RecyclerView mRecyclerView;
    private LinearLayoutManager mLayoutManager;
    private MyAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blocks);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        textViewBlocksTitle = (TextView) findViewById(R.id.textViewBlocksTitle);
        builder = new GsonBuilder();

        mRecyclerView = (RecyclerView) findViewById(R.id.blocks_recycler_view);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);



        //gestione UNIX time lungo e non
        builder.registerTypeAdapter(Date.class, new MyDateTypeAdapter());
        builder.registerTypeAdapter(Calendar.class, new MyTimeStampTypeAdapter());

        final NoobPoolDbHelper mDbHelper = new NoobPoolDbHelper(this);
        issueRefresh(mDbHelper, builder);
    }

    private void issueRefresh(final NoobPoolDbHelper mDbHelper, final GsonBuilder builder) {
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                blocksUrl, null,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(final JSONObject response) {
                        Log.d(MainActivity.TAG, response.toString());
                        textViewBlocksTitle.post(new Runnable() {
                            @Override
                            public void run() {
                                Gson gson = builder.create();
                                // Register an adapter to manage the date types as long values
                                Block retrieved = gson.fromJson(response.toString(), Block.class);
                                textViewBlocksTitle.setText(retrieved.getMaturedTotal()+ " Blocks found");
                                Matured[] maturi = new Matured[retrieved.getMaturedTotal()];
                                retrieved.getMatured().toArray(maturi);

                                // specify an adapter (see also next example)
                                mAdapter = new MyAdapter(maturi);
                                mRecyclerView.setAdapter(mAdapter);
                            }
                        });


                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(MainActivity.TAG, "Error: " + error.getMessage());
                // hide the progress dialog
            }
        });

        // Adding request to request queue
        NoobJSONClientSingleton.getInstance(this).addToRequestQueue(jsonObjReq);
    }

}
