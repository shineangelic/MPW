package it.angelic.mpw;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.preference.PreferenceManager;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import it.angelic.mpw.model.CurrencyEnum;
import it.angelic.mpw.model.PoolEnum;

/**
 * Created by shine@angelic.it on 20/11/2017.
 */

public class DrawerActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {


    PoolEnum mPool;
    CurrencyEnum mCur;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        mPool = PoolEnum.valueOf(prefs.getString("poolEnum", ""));
        mCur = CurrencyEnum.valueOf(prefs.getString("curEnum", ""));
    }

    @Override
    protected void onStart() {
        super.onStart();
        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        mPool = PoolEnum.valueOf(prefs.getString("poolEnum", ""));
        mCur = CurrencyEnum.valueOf(prefs.getString("curEnum", ""));
        NavigationView navigationView = (NavigationView) findViewById(R.id.navigation_view);

       final DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

         navigationView.setNavigationItemSelectedListener(this);
        //set proper pool info
        View headerLayout = navigationView.getHeaderView(0);
        ImageView poolLogo = headerLayout.findViewById(R.id.imageView);
        ImageView backgroundPool = headerLayout.findViewById(R.id.backgroundPool);
        LinearLayout linearSideDrawer = headerLayout.findViewById(R.id.linearSideDrawer);
        TextView poolT = headerLayout.findViewById(R.id.navTextPool);
        TextView poolTW = headerLayout.findViewById(R.id.navTextPoolWebSite);
        ImageView imageViewWrench = headerLayout.findViewById(R.id.imageViewWrench);
        imageViewWrench.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawer.closeDrawers();
                Intent miner = new Intent(DrawerActivity.this, SettingsActivity.class);
                startActivity(miner);
            }
        });
        poolT.setText(mPool.toString());
        poolTW.setText(Constants.BASE_WEBSITE_URL + mPool.getWebRoot());

        switch (mPool){
            case HASHINGPARTY:
                backgroundPool.setImageResource(R.mipmap.ic_hashparty_foreground);
                poolLogo.setImageResource(R.mipmap.ic_hashparty_launcher);
                break;
            case NOOBPOOL:
                backgroundPool.setImageResource(R.drawable.side_nav_bar);
                poolLogo.setImageResource(R.mipmap.ic_noobpool_launcher);
                break;
            case CRYPTOPOOL:
                backgroundPool.setImageResource(R.drawable.side_nav_bar);
                break;

        }



    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {


        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            Intent opzioni = new Intent(DrawerActivity.this, MainActivity.class);
            startActivity(opzioni);
        } else if (id == R.id.nav_wallet) {
            SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
            String minerAddr = pref.getString("wallet_addr", null);

            if (minerAddr == null || minerAddr.length() == 0) {
                Snackbar.make(findViewById(android.R.id.content), "Insert Public Address in Preferences", Snackbar.LENGTH_LONG)
                        .setAction("GO", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent miner = new Intent(DrawerActivity.this, SettingsActivity.class);
                                startActivity(miner);
                            }
                        }).show();
            } else {
                Intent miner = new Intent(this, WalletActivity.class);
                startActivity(miner);
            }
        } else if (id == R.id.nav_payment) {
            SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
            String minerAddr = pref.getString("wallet_addr", null);

            if (minerAddr == null || minerAddr.length() == 0) {
                Snackbar.make(findViewById(android.R.id.content), "Insert Public Address in Preferences", Snackbar.LENGTH_LONG)
                        .setAction("GO", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent miner = new Intent(DrawerActivity.this, SettingsActivity.class);
                                startActivity(miner);
                            }
                        }).show();
            } else {
                Intent miner = new Intent(this, PaymentsActivity.class);
                startActivity(miner);
            }
        } else if (id == R.id.nav_send) {
            Intent i = new Intent(Intent.ACTION_VIEW);

            final String appName = "org.telegram.messenger";

            switch (mPool){
                case HASHINGPARTY:
                    i.setData(Uri.parse("https://telegram.me/joinchat/FT9nb0I2lftHlyL_H6A_Qg"));
                    break;
                case NOOBPOOL:
                    i.setData(Uri.parse("https://telegram.me/"));
                    break;
                case CRYPTOPOOL:
                    i.setData(Uri.parse("https://telegram.me/joinchat/@CryptoPool_Network"));
                    break;
            }

            if (Utils.isAppAvailable(this.getApplicationContext(), appName)) {
                i.setPackage(appName);
            } else {//tg not available
                i.setData(Uri.parse("http://www.t.me/" + mPool.name().toLowerCase()));
            }

            startActivity(i);
        } else if (id == R.id.nav_support) {
            Intent opzioni = new Intent(DrawerActivity.this, EncourageActivity.class);
            startActivity(opzioni);
        } else if (id == R.id.nav_blocks) {
            Intent bb = new Intent(DrawerActivity.this, BlocksActivity.class);
            startActivity(bb);
        } else if (id == R.id.nav_miners) {
            Intent bb = new Intent(DrawerActivity.this, MinersActivity.class);
            startActivity(bb);
        } else {
            Snackbar.make(findViewById(android.R.id.content), "Function not implemented yet. Please encourage development", Snackbar.LENGTH_LONG)
                    .setAction("WHAT?", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent miner = new Intent(DrawerActivity.this, EncourageActivity.class);
                            startActivity(miner);
                        }
                    }).show();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
