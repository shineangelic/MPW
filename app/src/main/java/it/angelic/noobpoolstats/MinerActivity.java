package it.angelic.noobpoolstats;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RadioGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
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
import it.angelic.noobpoolstats.model.MyDateTypeAdapter;
import it.angelic.noobpoolstats.model.MyTimeStampTypeAdapter;
import it.angelic.noobpoolstats.model.db.NoobPoolDbHelper;
import it.angelic.noobpoolstats.model.db.NoobPoolQueryGrouper;
import it.angelic.noobpoolstats.model.jsonpojos.home.HomeStats;
import it.angelic.noobpoolstats.model.jsonpojos.wallet.Wallet;
import it.angelic.noobpoolstats.model.jsonpojos.wallet.Worker;

public class MinerActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    public static final String minerStatsUrl = "http://www.noobpool.com/api/accounts/";
    private static final SimpleDateFormat yearFormat = new SimpleDateFormat("MM-dd HH:mm", Locale.US);
    private static final SimpleDateFormat yearFormatExtended = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
    private TextView walletValueText;
    private TextView hashText;

    private TextView textViewWalPaymentsValue;
    private TextView walOnlineWorkersText;
    private TextView textViewWalLastShareValue;
    private TextView walCurHashrateText;
    private TextView walTotSharesText;
    private TextView walCurHashrate3HText;
    private TextView textViewWalLastShare;
    private LinkedMap<Date, Wallet> storia;
    private RadioGroup radioGroupBackTo;
    private RadioGroup radioGroupChartGranularity;
    private String minerAddr;
    private LineView lineView;
    private TextView textViewWalRoundSharesPercValue;
    private TextView textViewPendingBalanceValue;
    private TextView textViewPaidValue;
    private LineView lineViewRate;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_miner);

        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
        minerAddr = pref.getString("wallet_addr", null);


        final NoobPoolDbHelper mDbHelper = new NoobPoolDbHelper(this);
        final GsonBuilder builder = new GsonBuilder();

        builder.registerTypeAdapter(Date.class, new MyDateTypeAdapter());
        builder.registerTypeAdapter(Calendar.class, new MyTimeStampTypeAdapter());

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(this.getTitle());
        setSupportActionBar(toolbar);

        hashText = (TextView) findViewById(R.id.hashrateText);

        walletValueText = (TextView) findViewById(R.id.textViewWalletValue);
        walCurHashrateText = (TextView) findViewById(R.id.textViewWalCurHashrateValue);
        walCurHashrate3HText = (TextView) findViewById(R.id.textViewWalHashrate3hValue);
        walTotSharesText = (TextView) findViewById(R.id.textViewWalSharesValue);
        walOnlineWorkersText = (TextView) findViewById(R.id.textViewWalOnlineMinersValue);
        lineView = (LineView) findViewById(R.id.line_view_onlineminers);
        lineViewRate = (LineView) findViewById(R.id.line_view_hrate);
        textViewWalPaymentsValue = (TextView) findViewById(R.id.textViewWalPaymentsValue);
        textViewWalLastShareValue = (TextView) findViewById(R.id.textViewWalLastShareValue);
        textViewWalLastShare = (TextView) findViewById(R.id.textViewWalLastShare);
        textViewWalRoundSharesPercValue = (TextView) findViewById(R.id.textViewWalRoundSharesPercValue);
        textViewPendingBalanceValue= (TextView) findViewById(R.id.textViewPendingBalanceValue);
        textViewPaidValue= (TextView) findViewById(R.id.textViewPaidValue);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Async Refresh Sent", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                issueRefresh(mDbHelper, builder, minerStatsUrl + minerAddr);
            }
        });

        //i grafici hanno controlli globali
        radioGroupChartGranularity = (RadioGroup) findViewById(R.id.radioDifficultyGranularity);
        radioGroupBackTo = (RadioGroup) findViewById(R.id.radioBackto);

        RadioGroup.OnCheckedChangeListener mescola = new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                storia = mDbHelper.getWalletHistoryData(radioGroupBackTo.getCheckedRadioButtonId());
                NoobChartUtils.drawWorkersHistory(lineView, NoobPoolQueryGrouper.groupAvgWalletQueryResult(storia, radioGroupChartGranularity.getCheckedRadioButtonId()));
            }
        };
        radioGroupBackTo.setOnCheckedChangeListener(mescola);
        radioGroupChartGranularity.setOnCheckedChangeListener(mescola);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view_miner);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setCheckedItem(R.id.nav_wallet);

        issueRefresh(mDbHelper, builder, minerStatsUrl + minerAddr);

    }

    private void issueRefresh(final NoobPoolDbHelper mDbHelper, final GsonBuilder builder, String url) {
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                url, null,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(MainActivity.TAG, response.toString());

                        Gson gson = builder.create();
                        // Register an adapter to manage the date types as long values
                        Wallet retrieved = gson.fromJson(response.toString(), Wallet.class);
                        mDbHelper.logWalletStats(retrieved);
                        //dati semi grezzi
                        storia = mDbHelper.getWalletHistoryData(radioGroupBackTo.getCheckedRadioButtonId());

                        updateCurrentStats(retrieved, mDbHelper);
                        NoobChartUtils.drawWorkersHistory(lineView, NoobPoolQueryGrouper.groupAvgWalletQueryResult(storia, radioGroupChartGranularity.getCheckedRadioButtonId()));
                        NoobChartUtils.drawWalletHashRateHistory(lineViewRate,
                                NoobPoolQueryGrouper.groupAvgWalletQueryResult(storia,
                                        radioGroupChartGranularity.getCheckedRadioButtonId()),
                                radioGroupChartGranularity.getCheckedRadioButtonId());

                        drawMinersTable(retrieved);

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

    private void drawMinersTable(Wallet retrieved) {
        TableLayout minersTable = (TableLayout) findViewById(R.id.tableLayout);
        minersTable.removeAllViews();
        //table header
        TableRow row = (TableRow) LayoutInflater.from(MinerActivity.this).inflate(R.layout.row_miner, null);
        (row.findViewById(R.id.buttonworkerOnline)).setVisibility(View.INVISIBLE);
        minersTable.addView(row);
        for (String workerName : retrieved.getWorkers().keySet()) {
            Worker worker = retrieved.getWorkers().get(workerName);

            TableRow rowt = (TableRow) LayoutInflater.from(MinerActivity.this).inflate(R.layout.row_miner, null);
            ((TextView) rowt.findViewById(R.id.textViewWorkerName)).setText(workerName);
            ((TextView) rowt.findViewById(R.id.textViewWorkerHashrate)).setText(Utils.formatHashrate(worker.getHr()));
            ((TextView) rowt.findViewById(R.id.textViewWorkerHashrate3h)).setText(Utils.formatHashrate(worker.getHr2()));
            if (worker.getOffline()) {
                (rowt.findViewById(R.id.buttonworkerOnline)).setBackgroundColor(ContextCompat.getColor(MinerActivity.this, R.color.colorAccent));
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
    private void updateCurrentStats(Wallet lastHit, final NoobPoolDbHelper mDbHelper) {

        try {
            Calendar when = Calendar.getInstance();

            when.setTime(lastHit.getStats().getLastShare());
            when.setTimeZone(TimeZone.getDefault());

            //textViewWalLastBeat = (TextView) findViewById(R.id.textViewWalLastBeat);
            //textViewWalPaymentsValue.setText(yearFormatExtended.format(lastHit.getStats().getLastBlockFound()));

            textViewWalLastShareValue.setText(yearFormatExtended.format(when.getTime()));
            textViewWalLastShare.setText(getString(R.string.last_share_found) + " " + Utils.getTimeAgo(when));

            walCurHashrateText.setText(Utils.formatHashrate(Long.parseLong(lastHit.getCurrentHashrate().toString())));
            walCurHashrate3HText.setText(Utils.formatHashrate(Long.parseLong(lastHit.getHashrate().toString())));
            walTotSharesText.setText(Utils.formatBigNumber(lastHit.getRoundShares()));
            walOnlineWorkersText.setText(lastHit.getWorkersOnline().toString());
            textViewWalPaymentsValue.setText("" + lastHit.getPaymentsTotal());
            walletValueText.setText(minerAddr);
            walletValueText.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setData(Uri.parse("https://etherscan.io/address/" + minerAddr));
                    startActivity(i);
                }
            });
        } catch (Exception e) {
            Log.e(MainActivity.TAG, "Errore refresh: " + e.getMessage());
        }
        try {
            MathContext mc = new MathContext(4, RoundingMode.HALF_UP);
            HomeStats last = mDbHelper.getLastHomeStats(1).getValue(0);
            // bigIntX is a BigInteger
            BigDecimal bigDecX = new BigDecimal(lastHit.getRoundShares());
            BigDecimal bigDecY = new BigDecimal(last.getStats().getRoundShares());

            BigDecimal bd3 = bigDecX.divide(bigDecY, mc).multiply(new BigDecimal(100));
            // to divide:
            textViewWalRoundSharesPercValue.setText(bd3.stripTrailingZeros().toString() + "%");
        } catch (Exception e) {
            Log.e(MainActivity.TAG, "Errore refresh share perc: " + e.getMessage());
            e.printStackTrace();
        }
        try {
            textViewPendingBalanceValue.setText(Utils.formatEthCurrency(lastHit.getStats().getBalance().longValue()));
            textViewPaidValue.setText(Utils.formatEthCurrency(lastHit.getStats().getPaid()));
        }catch (Exception ie){
            Log.e(MainActivity.TAG, "Errore refresh Paid/pending: " + ie.getMessage());
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

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            Intent opzioni = new Intent(MinerActivity.this, MainActivity.class);
            startActivity(opzioni);
        } else if (id == R.id.nav_wallet) {
            //siamo gia qui
        } else if (id == R.id.nav_send) {
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse("https://telegram.me/Noobpool"));
            final String appName = "org.telegram.messenger";

            if (Utils.isAppAvailable(this.getApplicationContext(), appName))
                i.setPackage(appName);

            startActivity(i);
        } else if (id == R.id.nav_support) {
            Intent opzioni = new Intent(MinerActivity.this, EncourageActivity.class);
            startActivity(opzioni);
        } else {
            Snackbar.make(hashText, "Function not implemented yet. Please encourage development", Snackbar.LENGTH_LONG)
                    .setAction("WHAT?", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent miner = new Intent(MinerActivity.this, EncourageActivity.class);
                            startActivity(miner);
                        }
                    }).show();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
