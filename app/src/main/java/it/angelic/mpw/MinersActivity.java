package it.angelic.mpw;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Random;

import it.angelic.mpw.model.MyDateTypeAdapter;
import it.angelic.mpw.model.MyTimeStampTypeAdapter;
import it.angelic.mpw.model.db.MinerDBRecord;
import it.angelic.mpw.model.db.NoobPoolDbHelper;
import it.angelic.mpw.model.jsonpojos.miners.Miner;
import it.angelic.mpw.model.jsonpojos.miners.MinerRoot;
import it.angelic.mpw.model.jsonpojos.wallet.Payment;
import it.angelic.mpw.model.jsonpojos.wallet.Wallet;


public class MinersActivity extends DrawerActivity {
    private TextView textViewBlocksTitle;
    private RecyclerView mRecyclerView;
    private MinerAdapter mAdapter;

    private NoobPoolDbHelper mDbHelper;
    private TextView textViewHighestHashrateValue;
    private TextView textViewMostPaidMinerValue;
    private TextView textViewolde;
    private GsonBuilder builder;
    private TextView textViewOldestMinerValue;


    private void fetchRandomGuy() {
        ArrayList<MinerDBRecord> miners = mDbHelper.getMinerList();
        //choose a random one if no empty
        if (miners.size() >0) {
            for (int i = 0; i < 5; i++) {
                final MinerDBRecord rec = miners.get(new Random(new Date().getTime()).nextInt(miners.size()));
                fetchMinerStats(rec);
            }
        }
    }

    private void fetchMinerStats(final MinerDBRecord rec) {
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(
                Utils.getWalletStatsUrl(this) + rec.getAddress(), null,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        //update known miners TABLE
                        Log.d(Constants.TAG, response.toString());

                        Gson gson = builder.create();
                        // Register an adapter to manage the date types as long values
                        final Wallet retrieved = gson.fromJson(response.toString(), Wallet.class);

                        if (retrieved.getWorkersTotal() > (rec.getTopMiners()==null?-1:rec.getTopMiners()))
                            rec.setTopMiners(retrieved.getWorkersTotal());
                        if (retrieved.getCurrentHashrate() > (rec.getTopHr()==null?-1:rec.getTopHr()))
                            rec.setTopHr(retrieved.getCurrentHashrate());

                        rec.setPaid(retrieved.getStats().getPaid());
                        try {//compute first paymt
                            Payment pp = retrieved.getPayments().get(retrieved.getPayments().size() - 1);
                            rec.setFirstSeen(pp.getTimestamp());
                        }catch (Exception io){
                            //dont look back in anger
                        }
                        rec.setAvgHr(rec.getAvgHr()==null?retrieved.getHashrate():((rec.getAvgHr() + retrieved.getHashrate()) / 2));
                        // aggiorna UI
                        rec.setLastSeen(retrieved.getStats().getLastShare());
                        rec.setBlocksFound(retrieved.getStats().getBlocksFound());
                        mDbHelper.updateMiner(rec);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.e(Constants.TAG, "Error: " + error.getMessage());
            }
        });

        // Adding request to request queue
        NoobJSONClientSingleton.getInstance(this).addToRequestQueue(jsonObjReq);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_miners);
        builder = new GsonBuilder();

        builder.registerTypeAdapter(Date.class, new MyDateTypeAdapter());
        builder.registerTypeAdapter(Calendar.class, new MyTimeStampTypeAdapter());

        mDbHelper = new NoobPoolDbHelper(this, mPool, mCur);
        textViewBlocksTitle = findViewById(R.id.textViewBlocksTitle);
        textViewHighestHashrateValue = findViewById(R.id.textViewHighestHashrateValue);
        textViewMostPaidMinerValue = findViewById(R.id.textViewMostPaidMinerValue);
        textViewOldestMinerValue = findViewById(R.id.textViewOldestMinerValue);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Async Refresh Sent", Snackbar.LENGTH_SHORT)
                        .setAction("Action", null).show();
                fetchRandomGuy();
                issueRefresh();
            }
        });

        mRecyclerView = (RecyclerView) findViewById(R.id.miners_recycler_view);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        LinearLayoutManager mLayoutManager = new GridLayoutManager(this, getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE ? 2 : 1);
        mRecyclerView.setLayoutManager(mLayoutManager);

        issueRefresh( );
    }

    @Override
    protected void onStart() {
        super.onStart();
         fetchRandomGuy();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationViewInterna = (NavigationView) findViewById(R.id.navigation_view);
        navigationViewInterna.setNavigationItemSelectedListener(this);

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view_miners);
        navigationView.setNavigationItemSelectedListener(this);
        navigationViewInterna.setCheckedItem(R.id.nav_miners);


        Utils.fillEthereumStats(this, mDbHelper, navigationView, mPool, mCur);
    }

    private void issueRefresh( ) {

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                Utils.getMinersStatsUrl(MinersActivity.this), null,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(final JSONObject response) {
                        Log.d(Constants.TAG, response.toString());
                        textViewBlocksTitle.post(new Runnable() {
                            @Override
                            public void run() {
                                Gson gson = builder.create();
                                // Register an adapter to manage the date types as long values
                                MinerRoot retrieved = gson.fromJson(response.toString(), MinerRoot.class);
                                textViewBlocksTitle.setText(retrieved.getMinersTotal() + " " + mCur.name() + " Miners on " + mPool.toString());
                                //update DB from JSON
                                ArrayList<MinerDBRecord> min = fillRecordStats(retrieved);
                                mAdapter.setMinersArray(min);
                                mAdapter.notifyDataSetChanged();
                            }
                        });


                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.e(Constants.TAG, "Error: " + error.getMessage());
                Snackbar.make(findViewById(android.R.id.content), "Network Error", Snackbar.LENGTH_SHORT)
                        .show();
            }
        });

        // Adding request to request queue
        NoobJSONClientSingleton.getInstance(this).addToRequestQueue(jsonObjReq);
    }

    @NonNull
    private ArrayList<MinerDBRecord> fillRecordStats(MinerRoot retrieved) {
        HashMap<String, Miner> minatoriJSON = retrieved.getMiners();
        long hihr = 0;
        Long hiPaid = new Long(0);
        Integer hiPaidIdx = null;
        Date oldestDt = new Date();
        Integer minDateSeeIdx = null;
        int hihrIdx = 0;
        int cnt = 0;

        ArrayList<MinerDBRecord> minerDbList = mDbHelper.getMinerList();
        for (MinerDBRecord minerDbrec : minerDbList) {
            //JSON-also list
            if (minatoriJSON.containsKey(minerDbrec.getAddress())) {
                minerDbrec.setHashRate(minatoriJSON.get(minerDbrec.getAddress()).getHr());
            }
            if (minerDbrec.getHashRate() > hihr) {
                hihr = minerDbrec.getHashRate();
                hihrIdx = cnt;
            }
            //DB only
            long minp = minerDbrec.getPaid() == null ? 0 : minerDbrec.getPaid();
            if (minp > hiPaid) {
                hiPaid = minp;
                hiPaidIdx = cnt;
            }
            Date minDateSee = minerDbrec.getFirstSeen();
            if (minDateSee.before(oldestDt)) {
                oldestDt = minDateSee;
                minDateSeeIdx = cnt;
            }
            if (minatoriJSON.containsKey(minerDbrec.getAddress())) {
                Miner fis = minatoriJSON.get(minerDbrec.getAddress());
                fis.setAddress(minerDbrec.getAddress());
                //aggiornalo sul DB
                mDbHelper.createOrUpdateMiner(fis);
                //alla fine lo tolgo
                minatoriJSON.remove(minerDbrec.getAddress());
            }
            cnt++;
        }
        //aggiungo i rimanenti, ma verranno visualizzati alla prox
        for (String minK : minatoriJSON.keySet()) {
            //ricopio address
            Miner pip = minatoriJSON.get(minK);
            pip.setAddress(minK);
            //aggiorno su DB
            mDbHelper.createOrUpdateMiner(pip);
        }

        if (mAdapter == null) {
            mAdapter = new MinerAdapter(minerDbList, mPool, mCur);
            mRecyclerView.setAdapter(mAdapter);
        }
        textViewHighestHashrateValue.setText(Utils.formatBigNumber(hihr));

        //Based on these final copied values, we scroll to winner
        final Integer hiPaidIdxCopy = hiPaidIdx;
        if (hiPaidIdx != null) {
            textViewMostPaidMinerValue.setText(Utils.formatCurrency(hiPaid, mCur));
            ImageButton link = findViewById(R.id.textViewMostPaidMinerLink);
            link.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                   // mRecyclerView.smoothScrollToPosition(hiPaidIdxCopy );
                    ((LinearLayoutManager)mRecyclerView.getLayoutManager()).scrollToPositionWithOffset(hiPaidIdxCopy, 20);
                }
            });
        }
        final Integer minDateSeeIdxCopy = minDateSeeIdx;
        if (minDateSeeIdx != null) {
            textViewOldestMinerValue.setText(Utils.getTimeAgo(oldestDt));
            ImageButton link = findViewById(R.id.textViewOldestMinerLink);
            link.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ((LinearLayoutManager)mRecyclerView.getLayoutManager()).scrollToPositionWithOffset(minDateSeeIdxCopy, 20);
                }
            });
        }
        final int hihrIdxCopy = hihrIdx;
        ImageButton textViewHighestHashrateLink = findViewById(R.id.textViewHighestHashrateLink);
        textViewHighestHashrateLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // +1 per 'centrarlo
                ((LinearLayoutManager)mRecyclerView.getLayoutManager()).scrollToPositionWithOffset(hihrIdxCopy, 20);
            }
        });
        return minerDbList;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent opzioni = new Intent(this, SettingsActivity.class);
            startActivity(opzioni);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


}

