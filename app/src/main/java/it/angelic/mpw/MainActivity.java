package it.angelic.mpw;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import android.widget.RadioGroup;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
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
import it.angelic.mpw.model.CurrencyEnum;
import it.angelic.mpw.model.MyDateTypeAdapter;
import it.angelic.mpw.model.MyTimeStampTypeAdapter;
import it.angelic.mpw.model.PoolEnum;
import it.angelic.mpw.model.db.NoobPoolDbHelper;
import it.angelic.mpw.model.db.NoobPoolQueryGrouper;
import it.angelic.mpw.model.jsonpojos.etherscan.EtherscanStats;
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
    private RadioGroup radioGroupChartGranularity;
    private TextView textViewVarianceValue;
    private TextView textViewAvgBlockTime;
    private GsonBuilder builder;
    private NoobPoolDbHelper mDbHelper;


    private static long getAverageBlockSecondsSincePoolsBirth(HomeStats lastHit) {
        final Date firstBlockDate = new Date();//2017/07/15
        firstBlockDate.setTime(1500099900000L);
        long datediffFirst = (new Date().getTime() - firstBlockDate.getTime()) / 1000;
        //meno uno perche` il conto parte dal secondo blocco. Il primo boh
        return datediffFirst / (lastHit.getMaturedTotal() - 1);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        mDbHelper = new NoobPoolDbHelper(this,mPool ,mCur);
        builder = new GsonBuilder();
        //gestione UNIX time lungo e non
        builder.registerTypeAdapter(Date.class, new MyDateTypeAdapter());
        builder.registerTypeAdapter(Calendar.class, new MyTimeStampTypeAdapter());


        noobText = (TextView) findViewById(R.id.textViewWalletTitle);
        hashText = (TextView) findViewById(R.id.hashrateText);
        textViewNetDiffTitle = (TextView) findViewById(R.id.textViewWalHashrateTitle);
        poolLastBeat = (TextView) findViewById(R.id.textViewWalLastShareValue);
        textViewNetDiffValue = (TextView) findViewById(R.id.textViewNetDiffValue);
        lastFoundTextLabel = (TextView) findViewById(R.id.textViewLastBlock);
        lastFoundText = (TextView) findViewById(R.id.textViewWalPaymentsValue);
        onlineMinersText = (TextView) findViewById(R.id.textViewWalCurHashrateValue);
        textViewBlockChainHeightValue = (TextView) findViewById(R.id.textViewBlockChainHeightValue);
        poolHashrateText = (TextView) findViewById(R.id.textViewPoolHashrateValue);
        roundSharesText = (TextView) findViewById(R.id.textViewRoundSharesValue);
        textViewVarianceValue = (TextView) findViewById(R.id.textViewVarianceValue);
        textViewAvgBlockTime = (TextView) findViewById(R.id.textViewAvgBlockTime);


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Async Refresh Sent", Snackbar.LENGTH_SHORT)
                        .setAction("Action", null).show();
                issueRefresh(mDbHelper, builder);
            }
        });

        //i grafici hanno controlli globali
        radioGroupChartGranularity = (RadioGroup) findViewById(R.id.radioDifficultyGranularity);
        radioGroupBackTo = (RadioGroup) findViewById(R.id.radioBackto);

        final RadioGroup.OnCheckedChangeListener mescola = new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                radioGroupBackTo.post(new Runnable() {
                    @Override
                    public void run() {
                        LinkedMap<Date, HomeStats> storia = mDbHelper.getHistoryData(radioGroupBackTo.getCheckedRadioButtonId());
                        NoobChartUtils.drawDifficultyHistory(textViewNetDiffTitle, NoobPoolQueryGrouper.groupAvgQueryResult(storia, radioGroupChartGranularity.getCheckedRadioButtonId()), (LineView) findViewById(R.id.line_view_difficulty), radioGroupChartGranularity.getCheckedRadioButtonId());
                        NoobChartUtils.drawHashrateHistory(hashText, NoobPoolQueryGrouper.groupAvgQueryResult(storia, radioGroupChartGranularity.getCheckedRadioButtonId()), (LineView) findViewById(R.id.line_view_hashrate), radioGroupChartGranularity.getCheckedRadioButtonId());
                    }
                });
            }
        };
        radioGroupBackTo.setOnCheckedChangeListener(mescola);
        radioGroupChartGranularity.setOnCheckedChangeListener(mescola);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();


        Utils.fillEthereumStats(this, mDbHelper, (NavigationView) findViewById(R.id.nav_view),mPool);
    }

    @Override
    protected void onStart() {
        super.onStart();
        NavigationView navigationView = (NavigationView) findViewById(R.id.navigation_view);
        navigationView.setCheckedItem(R.id.nav_home);
        //importante refresh
        mDbHelper = new NoobPoolDbHelper(this,mPool,mCur);
        issueRefresh(mDbHelper, builder);
    }

    private void issueRefresh(final NoobPoolDbHelper mDbHelper, final GsonBuilder builder) {
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                getHomeStatsURL(MainActivity.this), null,
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
                                storia = mDbHelper.getHistoryData(radioGroupBackTo.getCheckedRadioButtonId());
                                updateCurrentStats();

                                NoobChartUtils.drawDifficultyHistory(textViewNetDiffTitle,
                                        NoobPoolQueryGrouper.groupAvgQueryResult(storia, radioGroupChartGranularity.getCheckedRadioButtonId()),
                                        (LineView) findViewById(R.id.line_view_difficulty), radioGroupChartGranularity.getCheckedRadioButtonId());
                                NoobChartUtils.drawHashrateHistory(hashText, NoobPoolQueryGrouper.groupAvgQueryResult(storia,
                                        radioGroupChartGranularity.getCheckedRadioButtonId()),
                                        (LineView) findViewById(R.id.line_view_hashrate),
                                        radioGroupChartGranularity.getCheckedRadioButtonId());
                            }
                        });


                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(Constants.TAG, "Error: " + error.getMessage());
                // hide the progress dialog
            }
        });

        JsonObjectRequest jsonEtherObjReq = new JsonObjectRequest(Request.Method.GET,
                Constants.ETHER_STATS_URL, null,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(final JSONObject response) {
                        Log.d(Constants.TAG, response.toString());
                        hashText.post(new Runnable() {
                            @Override
                            public void run() {
                                Gson gson = builder.create();
                                // Register an adapter to manage the date types as long values
                                EtherscanStats retrieved = gson.fromJson(response.toString(), EtherscanStats.class);

                                if ("OK".equals(retrieved.getMessage())) {
                                    Utils.saveEtherValues(retrieved.getResult(), MainActivity.this);
                                }
                            }
                        });


                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(Constants.TAG, "Error: " + error.getMessage());
                // hide the progress dialog
            }
        });

        // Adding request to request queue
        NoobJSONClientSingleton.getInstance(this).addToRequestQueue(jsonObjReq);
        NoobJSONClientSingleton.getInstance(this).addToRequestQueue(jsonEtherObjReq);
    }

    public static String getHomeStatsURL(Context ctx) {
        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(ctx);
        String mPool = prefs.getString("poolEnum", "");
        String mCur = prefs.getString("curEnum", "");
        //prefs.getString("wallet_addr" + PoolEnum.valueOf(mPool).name() + "_" + CurrencyEnum.valueOf(mCur).name(), "");
        return "http://"+ mCur + "." + PoolEnum.valueOf(mPool).getWebRoot() + Constants.HOME_STATS_URL;
    }

    /**
     * Update header with last persisted DB row
     */
    private void updateCurrentStats() {

        HomeStats lastHit = storia.get(storia.lastKey());
        try {
            Calendar when = Calendar.getInstance();
            when.setTimeZone(TimeZone.getDefault());
            when.setTime(lastHit.getStats().getLastBlockFound());
            lastFoundTextLabel.setText(getString(R.string.last_block_found) + " " + Utils.getTimeAgo(when));
            textViewNetDiffValue.setText(Utils.formatBigNumber(Long.parseLong(lastHit.getNodes().get(0).getDifficulty())));
            Calendar lastB = Calendar.getInstance();
            lastB.setTime(lastHit.getNodes().get(0).getLastBeat());
            yearFormatExtended.setTimeZone(TimeZone.getDefault());
            poolLastBeat.setText(yearFormatExtended.format(lastB.getTime()));
            lastFoundText.setText(yearFormatExtended.format(lastHit.getStats().getLastBlockFound()));
            onlineMinersText.setText("" + lastHit.getMinersTotal());

            textViewBlockChainHeightValue.setText(Utils.formatBigNumber(Long.parseLong(lastHit.getNodes().get(0).getHeight())));
            poolHashrateText.setText(Utils.formatHashrate(Long.parseLong(lastHit.getHashrate().toString())));
            roundSharesText.setText(Utils.formatBigNumber(lastHit.getStats().getRoundShares()));
            noobText.setText(String.format(getString(R.string.tot_block_found),mPool.toString(), lastHit.getMaturedTotal(), mCur.name()));
        } catch (Exception e) {
            Log.e(Constants.TAG, "Errore refresh: " + e.getMessage());
            e.printStackTrace();
        }
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
        try {
            long dtDiff = getAverageBlockSecondsSincePoolsBirth(lastHit);
            textViewAvgBlockTime.setText("It takes an average of "
                    + Utils.getScaledTime(dtDiff) + " to find a block. See blocks section for more accurate stats");

        } catch (Exception e) {
            Log.e(Constants.TAG, "Errore refresh share textViewAvgBlockTime: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
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
