package it.angelic.noobpoolstats;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
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

import java.util.Calendar;
import java.util.Date;

import it.angelic.noobpoolstats.model.MyDateTypeAdapter;
import it.angelic.noobpoolstats.model.MyTimeStampTypeAdapter;
import it.angelic.noobpoolstats.model.db.NoobPoolDbHelper;
import it.angelic.noobpoolstats.model.jsonpojos.blocks.Block;
import it.angelic.noobpoolstats.model.jsonpojos.blocks.Matured;

import static it.angelic.noobpoolstats.Constants.BLOCKS_URL;

public class BlocksActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private TextView textViewBlocksTitle;
    private RecyclerView mRecyclerView;
    private BlockAdapter mAdapter;
    private TextView textViewBlockWhenValue;
    private TextView textViewBlockSharesValue;
    private NoobPoolDbHelper mDbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blocks);

        mDbHelper = new NoobPoolDbHelper(this);
        textViewBlocksTitle = (TextView) findViewById(R.id.textViewBlocksTitle);

        GsonBuilder builder = new GsonBuilder();

        mRecyclerView = (RecyclerView) findViewById(R.id.blocks_recycler_view);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        LinearLayoutManager mLayoutManager = new GridLayoutManager(this, getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE ? 2 : 1);
        mRecyclerView.setLayoutManager(mLayoutManager);

        //gestione UNIX time lungo e non
        builder.registerTypeAdapter(Date.class, new MyDateTypeAdapter());
        builder.registerTypeAdapter(Calendar.class, new MyTimeStampTypeAdapter());

        issueRefresh(mDbHelper, builder);
    }

    @Override
    protected void onStart() {
        super.onStart();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationViewInterna = (NavigationView) findViewById(R.id.navigation_view);
        navigationViewInterna.setNavigationItemSelectedListener(this);

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view_blocks);
        navigationView.setNavigationItemSelectedListener(this);
        navigationViewInterna.setCheckedItem(R.id.nav_blocks);

        Utils.fillEthereumStats(this, mDbHelper, navigationView);
    }

    private void issueRefresh(final NoobPoolDbHelper mDbHelper, final GsonBuilder builder) {

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                BLOCKS_URL, null,
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
                                textViewBlocksTitle.setText(retrieved.getMaturedTotal() + " Blocks found");

                                Matured[] maturi = new Matured[retrieved.getMaturedTotal()];
                                retrieved.getMatured().toArray(maturi);

                                if (mAdapter == null) {
                                    mAdapter = new BlockAdapter(maturi);
                                    mRecyclerView.setAdapter(mAdapter);
                                }
                                mAdapter.setBlocksArray(maturi);
                                mAdapter.notifyDataSetChanged();
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
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            Intent opzioni = new Intent(BlocksActivity.this, MainActivity.class);
            startActivity(opzioni);
        } else if (id == R.id.nav_wallet) {
            SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
            String minerAddr = pref.getString("wallet_addr", null);

            if (minerAddr == null || minerAddr.length() == 0) {
                Snackbar.make(textViewBlocksTitle, "Insert Public Address in Preferences", Snackbar.LENGTH_LONG)
                        .setAction("GO", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent miner = new Intent(BlocksActivity.this, SettingsActivity.class);
                                startActivity(miner);
                            }
                        }).show();
            } else {
                Intent miner = new Intent(this, MinerActivity.class);
                startActivity(miner);
            }
        } else if (id == R.id.nav_payment) {
            SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
            String minerAddr = pref.getString("wallet_addr", null);

            if (minerAddr == null || minerAddr.length() == 0) {
                Snackbar.make(textViewBlocksTitle, "Insert Public Address in Preferences", Snackbar.LENGTH_LONG)
                        .setAction("GO", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent miner = new Intent(BlocksActivity.this, SettingsActivity.class);
                                startActivity(miner);
                            }
                        }).show();
            } else {
                Intent miner = new Intent(this, PaymentsActivity.class);
                startActivity(miner);
            }
        } else if (id == R.id.nav_send) {
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse("https://telegram.me/joinchat/FT9nb0I2lftHlyL_H6A_Qg"));
            final String appName = "org.telegram.messenger";

            if (Utils.isAppAvailable(this.getApplicationContext(), appName))
                i.setPackage(appName);

            startActivity(i);
        } else if (id == R.id.nav_support) {
            Intent opzioni = new Intent(BlocksActivity.this, EncourageActivity.class);
            startActivity(opzioni);
        } else if (id == R.id.nav_blocks) {
            //gia qui, non far nulla
        } else {
            Snackbar.make(textViewBlocksTitle, "Function not implemented yet. Please encourage development", Snackbar.LENGTH_LONG)
                    .setAction("WHAT?", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent miner = new Intent(BlocksActivity.this, EncourageActivity.class);
                            startActivity(miner);
                        }
                    }).show();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}

