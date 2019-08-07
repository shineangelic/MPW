package it.angelic.mpw;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.preference.PreferenceManager;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.GooglePlayDriver;
import com.firebase.jobdispatcher.Job;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.apache.commons.collections4.map.LinkedMap;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import im.dacer.androidcharts.LineView;
import it.angelic.mpw.model.MyDateTypeAdapter;
import it.angelic.mpw.model.MyTimeStampTypeAdapter;
import it.angelic.mpw.model.db.GranularityEnum;
import it.angelic.mpw.model.db.PoolDbHelper;
import it.angelic.mpw.model.db.PoolQueryGrouper;
import it.angelic.mpw.model.enums.BackToEnum;
import it.angelic.mpw.model.jsonpojos.home.HomeStats;

public class MainActivity extends DrawerActivity {

    public static final SimpleDateFormat dayFormat = new SimpleDateFormat("MM-dd", Locale.US);
    public static final SimpleDateFormat hourFormat = new SimpleDateFormat("MM-dd HH", Locale.US);
    public static final SimpleDateFormat yearFormat = new SimpleDateFormat("MM-dd HH:mm", Locale.US);
    public static final SimpleDateFormat yearFormatExtended = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);

    private TextView noobText;
    private TextView hashText;

    private TextView roundSharesText;
    private LinkedMap<Date, HomeStats> storia;
    private RadioGroup radioGroupBackTo;
    private TextView textViewNetDiffTitle;
    private GsonBuilder builder;
    private PoolDbHelper mDbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Locale current = getResources().getConfiguration().locale;

        mDbHelper = PoolDbHelper.getInstance(this, mPool, mCur);
        builder = new GsonBuilder();
        //gestione UNIX time lungo e non
        builder.registerTypeAdapter(Date.class, new MyDateTypeAdapter());
        builder.registerTypeAdapter(Calendar.class, new MyTimeStampTypeAdapter());

        noobText = findViewById(R.id.textViewWalletTitle);
        hashText = findViewById(R.id.hashrateText);
        textViewNetDiffTitle = findViewById(R.id.textViewWalHashrateTitle);

        roundSharesText = findViewById(R.id.textViewRoundSharesValue);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Async Refresh Sent", Snackbar.LENGTH_SHORT)
                        .setAction("Action", null).show();
                issueAsynchRefresh(mDbHelper, builder);
            }
        });

        //i grafici hanno controlli globali
        RadioGroup radioGroupChartGranularity = findViewById(R.id.radioDifficultyGranularity);
        radioGroupBackTo = findViewById(R.id.radioBackto);
        final RadioButton radioDay = findViewById(R.id.radioButtonDay);
        final RadioButton radioMin = findViewById(R.id.radioButtonMinutes);

        final RadioGroup.OnCheckedChangeListener mescola = new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                radioGroupBackTo.post(new Runnable() {
                    @Override
                    public void run() {
                        GranularityEnum granoEnum = GranularityEnum.HOUR;
                        if (radioDay.isChecked())
                            granoEnum = GranularityEnum.DAY;
                        else if (radioMin.isChecked())
                            granoEnum = GranularityEnum.MINUTE;
                        int radioButtonID = radioGroupBackTo.getCheckedRadioButtonId();
                        View radioButton = findViewById(radioButtonID);
                        LinkedMap<Date, HomeStats> storia = mDbHelper.getHistoryData(BackToEnum.valueOf((String) radioButton.getTag()));
                        ChartUtils.drawDifficultyHistory(textViewNetDiffTitle, PoolQueryGrouper.groupAvgQueryResult(storia, granoEnum), (LineView) findViewById(R.id.line_view_difficulty), granoEnum);
                        ChartUtils.drawHashrateHistory(hashText, PoolQueryGrouper.groupAvgQueryResult(storia, granoEnum), (LineView) findViewById(R.id.line_view_hashrate), granoEnum);
                    }
                });
            }
        };
        radioGroupBackTo.setOnCheckedChangeListener(mescola);
        radioGroupChartGranularity.setOnCheckedChangeListener(mescola);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();


    }

    @Override
    protected void onStart() {
        FirebaseJobDispatcher dispatcher = new FirebaseJobDispatcher(new GooglePlayDriver(this));
        Job myJob = MPWCoinmarketcapService.getJobUpdate(dispatcher);
        dispatcher.schedule(myJob);

        //ADS
        AdView mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        super.onStart();

        NavigationView navigationViewInterna = findViewById(R.id.navigation_view);
        navigationViewInterna.setNavigationItemSelectedListener(this);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationViewInterna.setCheckedItem(R.id.nav_home);
        navigationView.setNavigationItemSelectedListener(this);

        Utils.fillEthereumStats(this, mDbHelper, navigationView, mPool, mCur);
        //importante refresh
        mDbHelper = PoolDbHelper.getInstance(this, mPool, mCur);
        issueAsynchRefresh(mDbHelper, builder);
    }

    private void issueAsynchRefresh(final PoolDbHelper mDbHelper, final GsonBuilder builder) {
        Log.i(Constants.TAG, "JsonObjectRequest for: " + Utils.getHomeStatsURL(PreferenceManager.getDefaultSharedPreferences(MainActivity.this)));
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                Utils.getHomeStatsURL(PreferenceManager.getDefaultSharedPreferences(MainActivity.this)), null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(final JSONObject response) {
                        Log.d(Constants.TAG, response.toString());
                        hashText.post(new Runnable() {
                            @Override
                            public void run() {
                                Gson gson = builder.create();
                                // Register an adapter to manage the date types as long values
                                HomeStats retrieved = gson.fromJson(response.toString(), HomeStats.class);
                                mDbHelper.logHomeStats(retrieved);
                                //dati semi grezzi
                                int radioButtonID = radioGroupBackTo.getCheckedRadioButtonId();
                                View radioButton = findViewById(radioButtonID);
                                storia = mDbHelper.getHistoryData(BackToEnum.valueOf((String) radioButton.getTag()));
                                updateCurrentStatsPanel();
                                final RadioButton radioDay = findViewById(R.id.radioButtonDay);
                                final RadioButton radioMin = findViewById(R.id.radioButtonMinutes);
                                GranularityEnum granoEnum = GranularityEnum.HOUR;
                                if (radioDay.isChecked())
                                    granoEnum = GranularityEnum.DAY;
                                else if (radioMin.isChecked())
                                    granoEnum = GranularityEnum.MINUTE;

                                ChartUtils.drawDifficultyHistory(textViewNetDiffTitle,
                                        PoolQueryGrouper.groupAvgQueryResult(storia, granoEnum),
                                        (LineView) findViewById(R.id.line_view_difficulty), granoEnum);

                                ChartUtils.drawHashrateHistory(hashText, PoolQueryGrouper.groupAvgQueryResult(storia, granoEnum),
                                        (LineView) findViewById(R.id.line_view_hashrate),
                                        granoEnum);

                                final HorizontalScrollView horizontalScrollView = findViewById(R.id.horizontalScrollView);
                                final HorizontalScrollView horizontalScrollViewHashrate = findViewById(R.id.horizontalScrollViewHashrate);
                                // autoscroll to the right
                                horizontalScrollView.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        horizontalScrollView.fullScroll(HorizontalScrollView.FOCUS_RIGHT);
                                        horizontalScrollViewHashrate.fullScroll(HorizontalScrollView.FOCUS_RIGHT);
                                        //hsv.smoothScrollBy(hsv.getWidth(), 0);
                                    }
                                }, 200);
                            }
                        });
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.e(Constants.TAG, "Error: " + error.getMessage());
                Snackbar.make(findViewById(android.R.id.content), "Network Error", Snackbar.LENGTH_SHORT)
                .setAction("CHECK POOL", new MyUndoListener()).show();
                // prevent stale data appear
                updateCurrentStatsPanel();
            }
        });


        // Adding request to request queue
        JSONClientSingleton.getInstance(this).addToRequestQueue(jsonObjReq);
        //JSONClientSingleton.getInstance(this).addToRequestQueue(jsonArrayCurrenciesReq);
    }

    /**
     * Update header with last persisted DB row
     */
    private void updateCurrentStatsPanel() {
        try {

            TextView onlineMinersText = findViewById(R.id.textViewWalCurHashrateValue);
            TextView poolHashrateText = findViewById(R.id.textViewPoolHashrateValue);
            TextView textViewNetDiffValue = findViewById(R.id.textViewNetDiffValue);
            TextView textViewBlockChainHeightValue = findViewById(R.id.textViewBlockChainHeightValue);
            HomeStats lastHomeStats = storia.get(storia.lastKey());
            //header from superclass
            refreshHeaderInfo(lastHomeStats);
            //refresh panel values
            refreshLastFindings(lastHomeStats);
            refreshPoolLastBeat(lastHomeStats);

            textViewNetDiffValue.setText(Utils.formatBigNumber(Long.parseLong(lastHomeStats.getNodes().get(0).getDifficulty())));

            onlineMinersText.setText(String.format(Locale.getDefault(),"%d", lastHomeStats.getMinersTotal() == null ? 0 : lastHomeStats.getMinersTotal()));
            textViewBlockChainHeightValue.setText(Utils.formatBigNumber(Long.parseLong(lastHomeStats.getNodes().get(0).getHeight())));
            poolHashrateText.setText(Utils.formatHashrate(Long.parseLong(lastHomeStats.getHashrate().toString())));
            roundSharesText.setText(Utils.formatBigNumber(lastHomeStats.getStats().getRoundShares()));
            noobText.setText(String.format(getString(R.string.tot_block_found), mPool.toString(), lastHomeStats.getMaturedTotal(), mCur.name()));
            try {
                TextView textViewVarianceValue = findViewById(R.id.textViewVarianceValue);
                BigDecimal bVar = Utils.computeBlockVariance(lastHomeStats.getStats().getRoundShares(), Long.parseLong(lastHomeStats.getNodes().get(0).getDifficulty()));
                textViewVarianceValue.setText(String.format("%s%%", bVar.stripTrailingZeros().toPlainString()));
            } catch (Exception e) {
                Log.e(Constants.TAG, "Errore refresh computeBlockVariance: " + e.getMessage());
                e.printStackTrace();
            }
        } catch (Exception e) {
            Log.e(Constants.TAG, "ERROR refresh: " + e.getMessage());
            e.printStackTrace();
        }

    }

    private void refreshPoolLastBeat(HomeStats lastHomeStats) {
        TextView poolLastBeat = findViewById(R.id.textViewPoolLastBeatValue);
        Calendar lastB = Calendar.getInstance();
        lastB.setTime(lastHomeStats.getNodes().get(0).getLastBeat());
        yearFormatExtended.setTimeZone(TimeZone.getDefault());
        poolLastBeat.setText(yearFormatExtended.format(lastB.getTime()));
    }

    private void refreshLastFindings(HomeStats lastHomeStats) {
        try {
            TextView lastFoundTextLabel = findViewById(R.id.textViewLastBlock);
            TextView lastFoundText = findViewById(R.id.textViewWalPaymentsValue);
            Calendar when = Calendar.getInstance();
            when.setTimeZone(TimeZone.getDefault());
            when.setTime(lastHomeStats.getStats().getLastBlockFound());
            lastFoundTextLabel.setText(String.format("%s %s", getString(R.string.last_block_found), Utils.getTimeAgo(when)));
            lastFoundText.setText(yearFormatExtended.format(lastHomeStats.getStats().getLastBlockFound()));
        } catch (Exception ee) {
            Log.w(Constants.TAG, "No block found yet?");
        }
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
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
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

    private class MyUndoListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            if (mPool.getWebRoot() != null) {
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(Utils.getHomeStatsURL(PreferenceManager.getDefaultSharedPreferences(MainActivity.this))));
                startActivity(i);
            }
        }
    }
}
