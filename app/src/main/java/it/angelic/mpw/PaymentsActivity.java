package it.angelic.mpw;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
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

import org.json.JSONObject;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import im.dacer.androidcharts.LineView;
import it.angelic.mpw.model.MyDateTypeAdapter;
import it.angelic.mpw.model.MyTimeStampTypeAdapter;
import it.angelic.mpw.model.db.PoolDbHelper;
import it.angelic.mpw.model.enums.CurrencyEnum;
import it.angelic.mpw.model.enums.PrecisionEnum;
import it.angelic.mpw.model.jsonpojos.blocks.Block;
import it.angelic.mpw.model.jsonpojos.home.HomeStats;
import it.angelic.mpw.model.jsonpojos.wallet.Payment;
import it.angelic.mpw.model.jsonpojos.wallet.Wallet;

public class PaymentsActivity extends DrawerActivity {
    private static final SimpleDateFormat yearFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);

    private String minerAddr;
    private GsonBuilder builder;
    private LineView paymentsChart;
    private TextView textViewPaymentsTitle;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payments);

        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
        minerAddr = pref.getString("wallet_addr", null);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(this.getTitle());
        setSupportActionBar(toolbar);

        final PoolDbHelper mDbHelper = new PoolDbHelper(this, mPool, mCur);
        builder = new GsonBuilder();
        builder.registerTypeAdapter(Date.class, new MyDateTypeAdapter());
        builder.registerTypeAdapter(Calendar.class, new MyTimeStampTypeAdapter());

        TextView textViewWalletValue = findViewById(R.id.textViewWalletValue);
        textViewPaymentsTitle = findViewById(R.id.textViewPaymentTitle);
        paymentsChart = findViewById(R.id.lineViewPaymentss);
        textViewWalletValue.setText(Utils.formatEthAddress(minerAddr));
        textViewWalletValue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(Constants.HTTPS_ETHERSCAN_IO_ADDRESS + minerAddr));
                startActivity(i);
            }
        });

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        Utils.fillEthereumStats(this, mDbHelper, (NavigationView) findViewById(R.id.nav_view), mPool, mCur);
    }

    @Override
    protected void onStart() {
        super.onStart();

        NavigationView navigationView = findViewById(R.id.navigation_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setCheckedItem(R.id.nav_payment);
        final PoolDbHelper mDbHelper = new PoolDbHelper(this, mPool, mCur);
        issueRefresh(mDbHelper, builder, Utils.getWalletStatsUrl(PreferenceManager.getDefaultSharedPreferences(this)) + minerAddr);
    }

    private void issueRefresh(final PoolDbHelper mDbHelper, final GsonBuilder builder, String url) {
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                url, null,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(Constants.TAG, response.toString());

                        Gson gson = builder.create();
                        // Register an adapter to manage the date types as long values
                        Wallet retrieved = gson.fromJson(response.toString(), Wallet.class);
                        mDbHelper.logWalletStats(retrieved);
                        //dati semi grezzi
                        if (retrieved.getPayments() != null) {
                            drawPaymentsTable(retrieved);
                            //la seguente inverte ordine lista
                            ChartUtils.drawPaymentsHistory(paymentsChart, retrieved);
                            textViewPaymentsTitle.setText(String.format(getString(R.string.paid_out), mPool.toString()) + " " + retrieved.getPayments().size() + " times");
                        } else {
                            textViewPaymentsTitle.setText("No payment Yet");
                        }
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(Constants.TAG, "Error: " + error.getMessage());
                Snackbar.make(findViewById(android.R.id.content), "Network Error", Snackbar.LENGTH_SHORT)
                        .show();
            }
        });

        JsonObjectRequest jsonObjBlockReq = new JsonObjectRequest(Request.Method.GET,
                Utils.getBlocksURL(PreferenceManager.getDefaultSharedPreferences(PaymentsActivity.this)), null,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(final JSONObject response) {
                        Log.d(Constants.TAG, response.toString());
                        Gson gson = builder.create();
                        // Register an adapter to manage the date types as long values
                        Block matured = gson.fromJson(response.toString(), Block.class);
                        drawProjectionTable(mDbHelper, matured);
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
        JSONClientSingleton.getInstance(this).addToRequestQueue(jsonObjBlockReq);
    }

    private void drawProjectionTable(final PoolDbHelper mDbHelper, Block matured) {
        Double shareperc = 0d;

        TableLayout minersTable = findViewById(R.id.tableLayoutProjections);
        minersTable.removeAllViews();
        //table header
        TableRow row = (TableRow) LayoutInflater.from(PaymentsActivity.this).inflate(R.layout.row_projection, null);
        minersTable.addView(row);

        try {
            MathContext mc = new MathContext(4, RoundingMode.HALF_UP);
            HomeStats last = mDbHelper.getLastHomeStats(1).getValue(0);
            // bigIntX is a BigInteger
            BigDecimal bigDecX = new BigDecimal(mDbHelper.getLastWallet().getRoundShares());
            BigDecimal bigDecY = new BigDecimal(last.getStats().getRoundShares());

            shareperc = bigDecX.divide(bigDecY, mc).doubleValue();
            //By current currency
            TableRow rowCur = (TableRow) LayoutInflater.from(PaymentsActivity.this).inflate(R.layout.row_projection_color, null);
            ((TextView) rowCur.findViewById(R.id.textViewProjEmpty)).setText(mCur.name());
            Double dayP = Utils.getDailyProfitProjection(shareperc, matured.getMatured());
            ((TextView) rowCur.findViewById(R.id.textViewProjDayCur)).setText(Utils.formatGenericCurrency(PaymentsActivity.this, dayP, PrecisionEnum.TWO_DIGIT));
            ((TextView) rowCur.findViewById(R.id.textViewProjWeekCur)).setText(Utils.formatGenericCurrency(PaymentsActivity.this, dayP * 7, PrecisionEnum.TWO_DIGIT));
            Calendar c = Calendar.getInstance();
            int monthMaxDays = c.getActualMaximum(Calendar.DAY_OF_MONTH);
            ((TextView) rowCur.findViewById(R.id.textViewProjMonthCur)).setText(Utils.formatGenericCurrency(PaymentsActivity.this, dayP * monthMaxDays, PrecisionEnum.TWO_DIGIT));

            minersTable.addView(rowCur);
            //CAZZO DI DOLLARI
            try {
                TableRow rowCurEl = (TableRow) LayoutInflater.from(PaymentsActivity.this).inflate(R.layout.row_projection_color, null);
                ((TextView) rowCurEl.findViewById(R.id.textViewProjEmpty)).setText(" $ ");
                SharedPreferences settings = PaymentsActivity.this.getSharedPreferences("COINMARKETCAP", MODE_PRIVATE);
                Double val = Double.valueOf(settings.getString("CURUSD", "0"));
               Double dayPD = dayP * val.doubleValue();
                ((TextView) rowCurEl.findViewById(R.id.textViewProjDayCur)).setText(Utils.formatUSDCurrency(PaymentsActivity.this, dayPD));
                ((TextView) rowCurEl.findViewById(R.id.textViewProjWeekCur)).setText(Utils.formatUSDCurrency(PaymentsActivity.this, dayPD * 7));
                ((TextView) rowCurEl.findViewById(R.id.textViewProjMonthCur)).setText(Utils.formatUSDCurrency(PaymentsActivity.this, dayPD * monthMaxDays));

                minersTable.addView(rowCurEl);
                if (!CurrencyEnum.ETH.equals(mCur)) {
                    try {
                        TableRow rowCurEl2 = (TableRow) LayoutInflater.from(PaymentsActivity.this).inflate(R.layout.row_projection_color, null);
                        ((TextView) rowCurEl2.findViewById(R.id.textViewProjEmpty)).setText(CurrencyEnum.ETH.name());
                        Double valUsdETH = 1 / Double.valueOf(settings.getString("ETHUSD", "0"));
                        Double dayPDETH = dayPD * valUsdETH;
                        ((TextView) rowCurEl2.findViewById(R.id.textViewProjDayCur)).setText(Utils.formatGenericCurrency(PaymentsActivity.this, dayPDETH, PrecisionEnum.TWO_DIGIT));
                        ((TextView) rowCurEl2.findViewById(R.id.textViewProjWeekCur)).setText(Utils.formatGenericCurrency(PaymentsActivity.this, dayPDETH * 7, PrecisionEnum.TWO_DIGIT));
                        ((TextView) rowCurEl2.findViewById(R.id.textViewProjMonthCur)).setText(Utils.formatGenericCurrency(PaymentsActivity.this, dayPDETH * monthMaxDays, PrecisionEnum.TWO_DIGIT));

                        minersTable.addView(rowCurEl2);
                    } catch (Exception oioi) {
                        Log.w(Constants.TAG, "Internal Row 3 fail",oioi);
                    }
                }
            } catch (Exception oioi) {
                Log.w(Constants.TAG, "Internal Row 2 fail");
            }

        } catch (Exception e) {
            Log.e(Constants.TAG, "Errore refresh share perc: ", e);
            // textViewWalRoundSharesPercValue.setText("NA");
        }


    }

    private void drawPaymentsTable(Wallet retrieved) {
        TableLayout minersTable = findViewById(R.id.tableLayoutPayments);
        minersTable.removeAllViews();
        //table header
        TableRow row = (TableRow) LayoutInflater.from(PaymentsActivity.this).inflate(R.layout.row_payment, null);
        (row.findViewById(R.id.buttonPay)).setVisibility(View.INVISIBLE);
        minersTable.addView(row);

        for (final Payment thispay : retrieved.getPayments()) {
            //one row for each payment
            TableRow rowt = (TableRow) LayoutInflater.from(PaymentsActivity.this).inflate(R.layout.row_payment, null);
            ((TextView) rowt.findViewById(R.id.textViewWorkerName)).setText(yearFormat.format(thispay.getTimestamp()));
            ((TextView) rowt.findViewById(R.id.textViewWorkerHashrate)).setText(Utils.formatCurrency(PaymentsActivity.this, thispay.getAmount(), mCur));
            rowt.findViewById(R.id.buttonPay).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mCur.getScannerSite() != null) {
                        //mostra transazione pagamento
                        Intent i = new Intent(Intent.ACTION_VIEW);
                        i.setData(Uri.parse(mCur.getScannerSite() +"/tx/" + thispay.getTx()));
                        startActivity(i);
                    } else {
                        //ClipboardManager clipboard = (ClipboardManager) ctx.getSystemService(Context.CLIPBOARD_SERVICE);
                        //ClipData clip = ClipData.newPlainText("NoobPool Client", "0xbba4e04fe3692ae8ddc8599a65f64cdc00606a13");
                        //clipboard.setPrimaryClip(clip);
                        Snackbar.make(view, "Blockchain explorer not available for "+mCur.toString(), Snackbar.LENGTH_SHORT)
                                .setAction("Action", null).show();
                    }
                }
            });
            minersTable.addView(rowt);
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
}
