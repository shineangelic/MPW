package it.angelic.mpw;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.apache.commons.math3.stat.descriptive.SummaryStatistics;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import it.angelic.mpw.model.MyDateTypeAdapter;
import it.angelic.mpw.model.MyTimeStampTypeAdapter;
import it.angelic.mpw.model.db.PoolDbHelper;
import it.angelic.mpw.model.jsonpojos.blocks.Block;
import it.angelic.mpw.model.jsonpojos.blocks.Matured;

public class BlocksActivity extends DrawerActivity {
    private TextView textViewBlocksTitle;
    private RecyclerView mRecyclerView;
    private BlockAdapter mAdapter;

    private PoolDbHelper mDbHelper;
    private TextView textViewMaxBlockTimeValue;
    private TextView textViewMinBlockTimeValue;
    private TextView textViewMeanBlockTimeValue;
    private TextView textViewBlockTimeStdDevValue;
    private TextView textViewBlocksPerDayValue;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blocks);

        mDbHelper = PoolDbHelper.getInstance(this, mPool, mCur);
        textViewBlocksTitle = findViewById(R.id.textViewBlocksTitle);

        textViewMaxBlockTimeValue = findViewById(R.id.textViewMaxBlockTimeValue);
        textViewMinBlockTimeValue = findViewById(R.id.textViewMostPaidMinerValue);
        textViewMeanBlockTimeValue = findViewById(R.id.textViewMeanBlockTimeValue);
        textViewBlockTimeStdDevValue = findViewById(R.id.textViewBlockTimeStdDevValue);
        textViewBlocksPerDayValue = findViewById(R.id.textViewBlocksPerDayValue);


        GsonBuilder builder = new GsonBuilder();
        mRecyclerView = findViewById(R.id.blocks_recycler_view);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        LinearLayoutManager mLayoutManager = new GridLayoutManager(this, getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE ? 2 : 1);
        mRecyclerView.setLayoutManager(mLayoutManager);

        //gestione UNIX time lungo e non
        builder.registerTypeAdapter(Date.class, new MyDateTypeAdapter());
        builder.registerTypeAdapter(Calendar.class, new MyTimeStampTypeAdapter());

        issueRefresh(builder);
    }

    @Override
    protected void onStart() {
        super.onStart();

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationViewInterna = findViewById(R.id.navigation_view);
        navigationViewInterna.setNavigationItemSelectedListener(this);

        NavigationView navigationView = findViewById(R.id.nav_view_blocks);
        navigationView.setNavigationItemSelectedListener(this);
        navigationViewInterna.setCheckedItem(R.id.nav_blocks);
        //View headerLayout = navigationViewInterna.getHeaderView(0);

        Utils.fillEthereumStats(this, mDbHelper, navigationView, mPool, mCur);
    }

    private void issueRefresh(final GsonBuilder builder) {

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                Utils.getBlocksURL(PreferenceManager.getDefaultSharedPreferences(BlocksActivity.this)), null,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(final JSONObject response) {
                        Log.d(Constants.TAG, response.toString());
                        textViewBlocksTitle.post(new Runnable() {
                            @Override
                            public void run() {
                                Gson gson = builder.create();
                                // Register an adapter to manage the date types as long values
                                Block retrieved = gson.fromJson(response.toString(), Block.class);

                                if (retrieved.getMaturedTotal() > 0) {
                                    //Title
                                    StringBuilder txtTit = new StringBuilder();
                                    txtTit.append(retrieved.getMaturedTotal()).append(" ").append(mCur.toString());
                                    txtTit.append(" ").append("blocks");


                                    if (retrieved.getImmature() != null && retrieved.getImmature().size() > 0) {
                                        txtTit.append(" and ").append(retrieved.getImmature().size()).append(" immature");
                                    }
                                    txtTit.append(" found on ").append(mPool.toString());

                                    textViewBlocksTitle.setText(txtTit);

                                    SummaryStatistics sts = doApacheMath(retrieved.getMatured());

                                    if (retrieved.getMaturedTotal() > 1) {//otherwise inconsistent
                                        textViewMeanBlockTimeValue.setText(Utils.getScaledTime((long) sts.getMean() / 1000));
                                        textViewMaxBlockTimeValue.setText(Utils.getScaledTime((long) sts.getMax() / 1000));
                                        textViewMinBlockTimeValue.setText(Utils.getScaledTime((long) sts.getMin() / 1000));
                                        textViewBlockTimeStdDevValue.setText(Utils.getScaledTime((long) sts.getStandardDeviation() / 1000));

                                        Locale current = getResources().getConfiguration().locale;
                                        textViewBlocksPerDayValue.setText(String.format(current, "%.3f", Utils.getPoolBlockPerDay(retrieved.getMatured())));
                                    }

                                    if (mAdapter == null) {
                                        mAdapter = new BlockAdapter(retrieved.getMatured(), mCur, BlocksActivity.this);
                                        mRecyclerView.setAdapter(mAdapter);
                                    }
                                    mAdapter.setBlocksArray(retrieved.getMatured());
                                    mAdapter.notifyDataSetChanged();
                                }else
                                    textViewBlocksTitle.setText("No Block found on " + mPool.toString());
                            }
                        });

                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(Constants.TAG, "Error: " + error.getMessage());
                Snackbar.make(findViewById(android.R.id.content), "Network Error", Snackbar.LENGTH_SHORT)
                        .show();
            }
        });

        // Adding request to request queue
        JSONClientSingleton.getInstance(this).addToRequestQueue(jsonObjReq);
    }

    private SummaryStatistics doApacheMath(List<Matured> maturi) {
        ArrayList<Matured> revElements = new ArrayList<>(maturi);
        Collections.reverse(revElements);
        long[] intervals = new long[maturi.size()];
        Date prevDate = revElements.get(0).getTimestamp();
        int i = 0;
        //parto da 1 a calcolare il 1o intervallo
        for (int t = 1; t < revElements.size(); t++) {
            Date curDate = revElements.get(t).getTimestamp();
            intervals[i++] = curDate.getTime() - prevDate.getTime();
            prevDate = curDate;
        }
        //a oggi, calcolo ultimo intervallo aperto
        intervals[maturi.size() - 1] = (new Date().getTime() - revElements.get(maturi.size() - 1).getTimestamp().getTime());

        // Get a DescriptiveStatistics instance
        SummaryStatistics stats = new SummaryStatistics();

        // Add the data from the array
        for (long interval : intervals) {
            stats.addValue(interval);
        }

        return stats;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            Intent opzioni = new Intent(this, SettingsActivity.class);
            startActivity(opzioni);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}

