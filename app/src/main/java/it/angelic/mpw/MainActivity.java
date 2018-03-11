package it.angelic.mpw;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.apache.commons.collections4.map.LinkedMap;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
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

    private TextView poolLastBeat;
    private TextView textViewNetDiffValue;
    private TextView lastFoundText;
    private TextView onlineMinersText;
    private TextView textViewBlockChainHeightValue;
    private TextView poolHashrateText;
    private TextView roundSharesText;
    private LinkedMap<Date, HomeStats> storia;
    private TextView lastFoundTextLabel;
    private RadioGroup radioGroupBackTo;
    private TextView textViewNetDiffTitle;
    private TextView textViewVarianceValue;
    private GsonBuilder builder;
    private PoolDbHelper mDbHelper;


   /* private static long getAverageBlockSecondsSincePoolsBirth(HomeStats lastHit) {
        final Date firstBlockDate = new Date();//2017/07/15
        firstBlockDate.setTime(1500099900000L);
        long datediffFirst = (new Date().getTime() - firstBlockDate.getTime()) / 1000;
        //meno uno perche` il conto parte dal secondo blocco. Il primo boh
        return datediffFirst / (lastHit.getMaturedTotal() - 1);
    }*/


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Locale current = getResources().getConfiguration().locale;

        mDbHelper = new PoolDbHelper(this, mPool, mCur);
        builder = new GsonBuilder();
        //gestione UNIX time lungo e non
        builder.registerTypeAdapter(Date.class, new MyDateTypeAdapter());
        builder.registerTypeAdapter(Calendar.class, new MyTimeStampTypeAdapter());

        noobText = findViewById(R.id.textViewWalletTitle);
        hashText = findViewById(R.id.hashrateText);
        textViewNetDiffTitle = findViewById(R.id.textViewWalHashrateTitle);
        poolLastBeat = findViewById(R.id.textViewWalLastShareValue);
        textViewNetDiffValue = findViewById(R.id.textViewNetDiffValue);
        lastFoundTextLabel = findViewById(R.id.textViewLastBlock);
        lastFoundText = findViewById(R.id.textViewWalPaymentsValue);
        onlineMinersText = findViewById(R.id.textViewWalCurHashrateValue);
        textViewBlockChainHeightValue = findViewById(R.id.textViewBlockChainHeightValue);
        poolHashrateText = findViewById(R.id.textViewPoolHashrateValue);
        roundSharesText = findViewById(R.id.textViewRoundSharesValue);
        textViewVarianceValue = findViewById(R.id.textViewVarianceValue);


        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Async Refresh Sent", Snackbar.LENGTH_SHORT)
                        .setAction("Action", null).show();
                issueRefresh(mDbHelper, builder);
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
                        View radioButton =  findViewById(radioButtonID);
                        LinkedMap<Date, HomeStats> storia = mDbHelper.getHistoryData(BackToEnum.valueOf((String)radioButton.getTag()));
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
        drawer.setDrawerListener(toggle);
        toggle.syncState();



        //ADS
        AdView mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
    }

    @Override
    protected void onStart() {
        super.onStart();
        NavigationView navigationView = findViewById(R.id.navigation_view);
        navigationView.setCheckedItem(R.id.nav_home);
        Utils.fillEthereumStats(this, mDbHelper, (NavigationView) findViewById(R.id.nav_view), mPool, mCur);
        //importante refresh
        mDbHelper = new PoolDbHelper(this, mPool, mCur);
        issueRefresh(mDbHelper, builder);
    }

    private void issueRefresh(final PoolDbHelper mDbHelper, final GsonBuilder builder) {
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
                                View radioButton =  findViewById(radioButtonID);
                                storia = mDbHelper.getHistoryData(BackToEnum.valueOf((String)radioButton.getTag()));
                                updateCurrentStats();
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

                                ChartUtils.drawHashrateHistory(hashText, PoolQueryGrouper.groupAvgQueryResult(storia,granoEnum),
                                        (LineView) findViewById(R.id.line_view_hashrate),
                                        granoEnum);
                            }
                        });
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(Constants.TAG, "Error: " + error.getMessage());
                Snackbar.make(findViewById(android.R.id.content), "Network Error", Snackbar.LENGTH_SHORT)
                        .show();
                // prevent stale data appear
                updateCurrentStats();
            }
        });

        // Adding request to request queue
        JSONClientSingleton.getInstance(this).addToRequestQueue(jsonObjReq);
    }

    /**
     * Update header with last persisted DB row
     */
    private void updateCurrentStats() {
        try {
            HomeStats lastHit = storia.get(storia.lastKey());
            try {
                Calendar when = Calendar.getInstance();
                when.setTimeZone(TimeZone.getDefault());
                when.setTime(lastHit.getStats().getLastBlockFound());
                lastFoundTextLabel.setText(getString(R.string.last_block_found) + " " + Utils.getTimeAgo(when));
                lastFoundText.setText(yearFormatExtended.format(lastHit.getStats().getLastBlockFound()));
            } catch (Exception ee) {
                Log.w(Constants.TAG, "No block found yet?");
            }
            textViewNetDiffValue.setText(Utils.formatBigNumber(Long.parseLong(lastHit.getNodes().get(0).getDifficulty())));
            Calendar lastB = Calendar.getInstance();
            lastB.setTime(lastHit.getNodes().get(0).getLastBeat());
            yearFormatExtended.setTimeZone(TimeZone.getDefault());
            poolLastBeat.setText(yearFormatExtended.format(lastB.getTime()));

            onlineMinersText.setText("" + (lastHit.getMinersTotal() == null ? 0 : lastHit.getMinersTotal()));
            textViewBlockChainHeightValue.setText(Utils.formatBigNumber(Long.parseLong(lastHit.getNodes().get(0).getHeight())));
            poolHashrateText.setText(Utils.formatHashrate(Long.parseLong(lastHit.getHashrate().toString())));
            roundSharesText.setText(Utils.formatBigNumber(lastHit.getStats().getRoundShares()));
            noobText.setText(String.format(getString(R.string.tot_block_found), mPool.toString(), lastHit.getMaturedTotal(), mCur.name()));
            try {
                MathContext mc = new MathContext(4, RoundingMode.HALF_UP);
                // Variance % = Pool Shares / Network Difficulty Thanks to alfred
                BigDecimal bigDecX = new BigDecimal(lastHit.getStats().getRoundShares());
                BigDecimal bigDecY = new BigDecimal(Long.parseLong(lastHit.getNodes().get(0).getDifficulty()));
                BigDecimal bd3 = bigDecX.divide(bigDecY, mc).multiply(new BigDecimal(100));

                textViewVarianceValue.setText(bd3.stripTrailingZeros().toPlainString() + "%");
            } catch (Exception e) {
                Log.e(Constants.TAG, "Errore refresh share perc: " + e.getMessage());
                e.printStackTrace();
            }
        } catch (Exception e) {
            Log.e(Constants.TAG, "Errore refresh: " + e.getMessage());
            e.printStackTrace();
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
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
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
