package it.angelic.mpw;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

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
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
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
import it.angelic.mpw.model.jsonpojos.wallet.Wallet;
import it.angelic.mpw.model.jsonpojos.wallet.Worker;

public class WalletActivity extends DrawerActivity {

    private static final SimpleDateFormat yearFormatExtended = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
    private TextView walletValueText;
    private TextView hashRateChartTitleText;

    private TextView textViewWalPaymentsValue;
    private TextView walOnlineWorkersText;
    private TextView textViewWalLastShareValue;
    private TextView walCurHashrateText;
    private TextView walTotSharesText;
    private TextView walCurHashrate3HText;
    private TextView textViewWalLastShare;
    private LinkedMap<Date, Wallet> storia;
    private RadioGroup radioGroupBackTo;
    private String minerAddr;
    private LineView lineView;
    private TextView textViewWalRoundSharesPercValue;
    private TextView textViewPendingBalanceValue;
    private TextView textViewAvgPending;
    private TextView textViewPaidValue;
    private LineView lineViewRate;
    private GsonBuilder builder;
    private FloatingActionButton fab;
    private TextView walletTitleText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wallet);

        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
        minerAddr = pref.getString("wallet_addr", null);


        final PoolDbHelper mDbHelper = new PoolDbHelper(this, mPool, mCur);
        builder = new GsonBuilder();

        builder.registerTypeAdapter(Date.class, new MyDateTypeAdapter());
        builder.registerTypeAdapter(Calendar.class, new MyTimeStampTypeAdapter());


        hashRateChartTitleText = findViewById(R.id.hashrateText);
        walletTitleText = findViewById(R.id.textViewWalletTitle);
        walletValueText = findViewById(R.id.textViewWalletValue);
        walCurHashrateText = findViewById(R.id.textViewWalCurHashrateValue);
        walCurHashrate3HText = findViewById(R.id.textViewWalHashrate3hValue);
        walTotSharesText = findViewById(R.id.textViewWalSharesValue);
        walOnlineWorkersText = findViewById(R.id.textViewWalOnlineMinersValue);
        lineView = findViewById(R.id.line_view_onlineminers);
        lineViewRate = findViewById(R.id.line_view_hrate);
        textViewWalPaymentsValue = findViewById(R.id.textViewWalPaymentsValue);
        textViewWalLastShareValue = findViewById(R.id.textViewWalLastShareValue);
        textViewWalLastShare = findViewById(R.id.textViewWalLastShare);
        textViewWalRoundSharesPercValue = findViewById(R.id.textViewWalRoundSharesPercValue);
        textViewPendingBalanceValue = findViewById(R.id.textViewPendingBalanceValue);
        textViewAvgPending = findViewById(R.id.textViewAvgPendingValue);
        textViewPaidValue = findViewById(R.id.textViewPaidValue);

        fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Async Refresh Sent", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                issueRefresh(mDbHelper, builder, Utils.getWalletStatsUrl(PreferenceManager.getDefaultSharedPreferences(WalletActivity.this)) + minerAddr);
            }
        });

        //i grafici hanno controlli globali
        RadioGroup radioGroupChartGranularity = findViewById(R.id.radioDifficultyGranularity);
        radioGroupBackTo = findViewById(R.id.radioBackto);

        RadioGroup.OnCheckedChangeListener mescola = new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                new UpdateUIAsynchTask().execute();
            }
        };
        radioGroupBackTo.setOnCheckedChangeListener(mescola);
        radioGroupChartGranularity.setOnCheckedChangeListener(mescola);

        NavigationView navigationView = findViewById(R.id.navigation_view);
        navigationView.setNavigationItemSelectedListener(this);

        //ADS
        AdView mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
    }

    @Override
    protected void onStart() {
        super.onStart();
        final PoolDbHelper mDbHelper = new PoolDbHelper(this, mPool, mCur);
        issueRefresh(mDbHelper, builder, Utils.getWalletStatsUrl(PreferenceManager.getDefaultSharedPreferences(this)) + minerAddr);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        Utils.fillEthereumStats(this, mDbHelper, (NavigationView) findViewById(R.id.nav_view_wallet), mPool, mCur);
        NavigationView navigationViewInterna = findViewById(R.id.navigation_view);
        navigationViewInterna.setNavigationItemSelectedListener(this);
        navigationViewInterna.setCheckedItem(R.id.nav_wallet);

    }

    private void issueRefresh(final PoolDbHelper mDbHelper, final GsonBuilder builder,final String url) {
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(
                url, null,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(Constants.TAG,"URL "+url);
                        Log.d(Constants.TAG,"RESPONSE "+ response.toString());

                        Gson gson = builder.create();
                        // Register an adapter to manage the date types as long values
                        final Wallet retrieved = gson.fromJson(response.toString(), Wallet.class);
                        mDbHelper.logWalletStats(retrieved);
                        // aggiorna UI
                        new UpdateUIAsynchTask().execute();
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(Constants.TAG, "Error: " + error.getMessage());
            }
        });

        // Adding request to request queue
        JSONClientSingleton.getInstance(this).addToRequestQueue(jsonObjReq);
    }

    private void drawMinersTable(Wallet retrieved) {
        TableLayout minersTable = findViewById(R.id.tableLayoutWorkers);
        minersTable.removeAllViews();
        //table header
        TableRow row = (TableRow) LayoutInflater.from(WalletActivity.this).inflate(R.layout.tablerow_miner, null);
        (row.findViewById(R.id.buttonworkerOnline)).setVisibility(View.INVISIBLE);
        minersTable.addView(row);
        for (String workerName : retrieved.getWorkers().keySet()) {
            Worker worker = retrieved.getWorkers().get(workerName);

            TableRow rowt = (TableRow) LayoutInflater.from(WalletActivity.this).inflate(R.layout.tablerow_miner, null);
            ((TextView) rowt.findViewById(R.id.textViewWorkerName)).setText(workerName);
            ((TextView) rowt.findViewById(R.id.textViewWorkerHashrate)).setText(Utils.formatHashrate(worker.getHr()));
            ((TextView) rowt.findViewById(R.id.textViewWorkerHashrate3h)).setText(Utils.formatHashrate(worker.getHr2()));
            if (worker.getOffline()) {
                (rowt.findViewById(R.id.buttonworkerOnline)).setBackgroundColor(ContextCompat.getColor(WalletActivity.this, R.color.colorAccent));
            }
            Calendar workBeat = Calendar.getInstance();
            workBeat.setTime(worker.getLastBeat());
            ((TextView) rowt.findViewById(R.id.textViewWorkerLastBeat)).setText(Utils.getTimeAgo(workBeat));

            minersTable.addView(rowt);
        }
    }

    /**
     * Update header with last persisted DB row
     */
    private void updateCurrentStats(final Wallet lastHit, final PoolDbHelper mDbHelper, Long avgPending) {

        Calendar when = Calendar.getInstance();
        try {
            when.setTime(lastHit.getStats().getLastShare());
            when.setTimeZone(TimeZone.getDefault());

            textViewWalLastShareValue.setText(yearFormatExtended.format(when.getTime()));
            textViewWalLastShare.setText(getString(R.string.last_share_found) + " " + Utils.getTimeAgo(when));

            walCurHashrateText.setText(Utils.formatHashrate(Long.parseLong(lastHit.getCurrentHashrate().toString())));
            walCurHashrate3HText.setText(Utils.formatHashrate(Long.parseLong(lastHit.getHashrate().toString())));
            walTotSharesText.setText(Utils.formatBigNumber(lastHit.getRoundShares()));
            walOnlineWorkersText.setText(lastHit.getWorkersOnline().toString());
            textViewWalPaymentsValue.setText("" + lastHit.getPaymentsTotal());
            walletValueText.setText(Utils.formatEthAddress(minerAddr));
            walletValueText.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mCur.getScannerSite() != null) {
                        Intent i = new Intent(Intent.ACTION_VIEW);
                        i.setData(Uri.parse(mCur.getScannerSite() + "/address/" + minerAddr));
                        startActivity(i);
                    } else {
                        Snackbar.make(view, "Blockchain explorer not available for "+mCur.toString(), Snackbar.LENGTH_SHORT)
                                .setAction("Action", null).show();
                    }
                }
            });
        } catch (Exception e) {
            Log.e(Constants.TAG, "Errore refresh: " + e.getMessage());
        }
        try {
            MathContext mc = new MathContext(4, RoundingMode.HALF_UP);
            HomeStats last = mDbHelper.getLastHomeStats(1).getValue(0);
            // bigIntX is a BigInteger
            BigDecimal bigDecX = new BigDecimal(lastHit.getRoundShares());
            BigDecimal bigDecY = new BigDecimal(last.getStats().getRoundShares());

            BigDecimal bd3 = bigDecX.divide(bigDecY, mc);
            // to divide:
            //Formatter formatter = new Formatter();
            Locale current = getResources().getConfiguration().locale;
            //1 %1$s = %2$s$\n%3+$s%% last 24hr
            //%+10.4f%%
            DecimalFormat df = new DecimalFormat("0%", DecimalFormatSymbols.getInstance(current));
            df.setMaximumFractionDigits(12); //340 = DecimalFormat.DOUBLE_FRACTION_DIGITS
            textViewWalRoundSharesPercValue.setText(df.format(bd3));
        } catch (Exception e) {
            Log.e(Constants.TAG, "Errore refresh share perc: ",e);
            textViewWalRoundSharesPercValue.setText("NA");
        }
        try {
            textViewPendingBalanceValue.setText(Utils.formatCurrency(WalletActivity.this,lastHit.getStats().getBalance().longValue(), mCur));
        } catch (Exception ie) {
            Log.e(Constants.TAG, "Errore refresh Pending: " + ie.getMessage());
            textViewPendingBalanceValue.setText("NA");
        }

        try {
            textViewPaidValue.setText(Utils.formatCurrency(WalletActivity.this,lastHit.getStats().getPaid(), mCur));
        } catch (Exception ie) {
            Log.e(Constants.TAG, "Errore refresh Paid: " + ie.getMessage());
            textViewPaidValue.setText("NA");
        }

        try {
            textViewAvgPending.setText(avgPending == 0 ? "NA" : Utils.formatCurrency(WalletActivity.this,avgPending, mCur));
        } catch (Exception mie) {
            Log.e(Constants.TAG, "Errore refresh Agerage pending: " + mie.getMessage());
            textViewAvgPending.setText("NA");
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


    private class UpdateUIAsynchTask extends AsyncTask<String, Void, String> {

        private Wallet last;
        private PoolDbHelper mDbHelper;
        private Long avg;
        private ObjectAnimator objectanimator;
        private boolean mCanceled;

        @Override
        protected String doInBackground(String... params) {
            mDbHelper = new PoolDbHelper(WalletActivity.this, mPool, mCur);

            int radioButtonID = radioGroupBackTo.getCheckedRadioButtonId();
            View radioButton =  findViewById(radioButtonID);
            storia = mDbHelper.getWalletHistoryData( BackToEnum.valueOf((String)radioButton.getTag()));
            if (storia != null && storia.size() > 0) {
                last = storia.get(storia.lastKey());
                //metodo peso
                avg = mDbHelper.getAveragePending();
            }

            return "Executed";
        }

        @Override
        protected void onPostExecute(String result) {
            walletTitleText.setText(String.format(WalletActivity.this.getString(R.string.wallet_stats_title), mPool.toString(), mCur.toString()));
            updateCurrentStats(last, mDbHelper, avg);
            final RadioButton radioDay = findViewById(R.id.radioButtonDay);
            final RadioButton radioMin = findViewById(R.id.radioButtonMinutes);

            GranularityEnum granoEnum = GranularityEnum.HOUR;
            if (radioDay.isChecked())
                granoEnum = GranularityEnum.DAY;
            else if (radioMin.isChecked())
                granoEnum = GranularityEnum.MINUTE;
            if (storia != null && storia.size() > 0) {
                ChartUtils.drawWorkersHistory(lineView, PoolQueryGrouper.groupAvgWalletQueryResult(storia, granoEnum), granoEnum);
                ChartUtils.drawWalletHashRateHistory(hashRateChartTitleText, lineViewRate,
                        PoolQueryGrouper.groupAvgWalletQueryResult(storia,
                                granoEnum),
                        granoEnum);
                drawMinersTable(last);
            }

            objectanimator.cancel();
        }

        @Override
        protected void onPreExecute() {
            // final FloatingActionButton fabb = findViewById(R.id.fab);
            objectanimator = ObjectAnimator.ofFloat(fab, "rotation", 360);
            objectanimator.setDuration(1000);
            objectanimator.setRepeatCount(ObjectAnimator.INFINITE);
            objectanimator.start();
        }

        @Override
        protected void onProgressUpdate(Void... values) {
        }
    }
}
