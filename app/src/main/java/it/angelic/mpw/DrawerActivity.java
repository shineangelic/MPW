package it.angelic.mpw;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.preference.PreferenceManager;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.math.BigDecimal;

import it.angelic.mpw.model.db.PoolDbHelper;
import it.angelic.mpw.model.enums.CurrencyEnum;
import it.angelic.mpw.model.enums.PoolEnum;
import it.angelic.mpw.model.jsonpojos.home.HomeStats;

import static android.app.UiModeManager.MODE_NIGHT_AUTO;

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
        //SET APP'S THEME
        AppCompatDelegate.setDefaultNightMode(Integer.valueOf(prefs.getString("pref_theme", "" + MODE_NIGHT_AUTO)));
    }

    @Override
    protected void onStart() {
        super.onStart();
        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        mPool = PoolEnum.valueOf(prefs.getString("poolEnum", ""));
        mCur = CurrencyEnum.valueOf(prefs.getString("curEnum", ""));
        NavigationView navigationView = findViewById(R.id.navigation_view);



        final DrawerLayout drawer = findViewById(R.id.drawer_layout);

        navigationView.setNavigationItemSelectedListener(this);
        //set proper pool info
        View headerLayout = navigationView.getHeaderView(0);
        ImageView curLogo = headerLayout.findViewById(R.id.imageViewCurrencyLogo);
        ImageView backgroundPool = headerLayout.findViewById(R.id.backgroundPool);
        ImageView imageViewCurrencyLogoFoot = drawer.findViewById(R.id.imageViewCurrencyLogoFoot);
        //LinearLayout linearSideDrawer = headerLayout.findViewById(R.id.linearSideDrawer);

        try {
            PoolDbHelper mDbHelper = PoolDbHelper.getInstance(this, mPool, mCur);
            HomeStats lHit = mDbHelper.getLastHomeStats(1).getValue(0);
            refreshHeaderInfo(lHit);
        }catch (Exception amen){
            Log.w(Constants.TAG, "errore refresh nav panel:",amen);
        }
        imageViewCurrencyLogoFoot.setImageResource(R.drawable.ic_ethereum_logo);
        backgroundPool.setImageResource(R.drawable.side_nav_bar);
        curLogo.setImageResource(R.mipmap.ic_pool_watcher);
        switch (mPool) {

            case NOOBPOOL:
                curLogo.setImageResource(R.mipmap.pool_noob);
                break;
            case MAXHASH:
                curLogo.setImageResource(R.mipmap.ic_maxhash_logo);
                break;
            case NEVERMINING:
                curLogo.setImageResource(R.mipmap.ic_nevermining_logo);
                break;
            case TWOMINERS:
                curLogo.setImageResource(R.mipmap.ic_2miners_logo);
                break;
            case KRATOS:
                curLogo.setImageResource(R.mipmap.ic_kratos_logo);
                break;
            case XEMINERS:
                curLogo.setImageResource(R.mipmap.ic_xeminer_logo);
                break;
            case SOYMINERO:
                curLogo.setImageResource(R.mipmap.ic_soyminero_logo);
                break;
            case GIGANTPOOL:
                curLogo.setImageResource(R.mipmap.ic_gigantpool_logo);
                break;
            //case MININGPOOLITA:
             //   curLogo.setImageResource(R.mipmap.ic_europool_logo);
              //  break;
        }
        switch (mCur) {
            case ETH:
            case ETC:
                //gia` cosi, e` default
                break;
            case META:
            case ETP:
                imageViewCurrencyLogoFoot.setImageResource(R.drawable.ic_etp_logo);
                break;
            case UBIQ:
            case UBQ:
                imageViewCurrencyLogoFoot.setImageResource(R.drawable.ic_ubiq_logo);
                break;
            case MUSIC:
            case MC:
                imageViewCurrencyLogoFoot.setImageResource(R.drawable.ic_musicoin_logo);
                break;
            case PIRL:
                imageViewCurrencyLogoFoot.setImageResource(R.drawable.ic_pirl_logo);
                break;
            case KRB:
                imageViewCurrencyLogoFoot.setImageResource(R.drawable.ic_krb_logo);
                break;
            case ELLA:
                imageViewCurrencyLogoFoot.setImageResource(R.drawable.ic_ella_logo);
                break;
            case EXP:
                imageViewCurrencyLogoFoot.setImageResource(R.drawable.ic_exp_logo);
                break;
        }

    }

    protected void refreshHeaderInfo(HomeStats lHit  ) {
        NavigationView navigationView = findViewById(R.id.navigation_view);
        View headerLayout = navigationView.getHeaderView(0);
        final DrawerLayout drawer = findViewById(R.id.drawer_layout);
        TextView navTextPoolStats = headerLayout.findViewById(R.id.navTextPoolStats);
        TextView poolTW = headerLayout.findViewById(R.id.navTextPoolWebSite);
        TextView sidbarPoolTitle = headerLayout.findViewById(R.id.navTextPool);
        ImageView imageViewWrench = headerLayout.findViewById(R.id.imageViewWrench);
        imageViewWrench.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawer.closeDrawers();
                Intent miner = new Intent(DrawerActivity.this, SettingsActivity.class);
                startActivity(miner);
            }
        });
        sidbarPoolTitle.setText(String.format("%s - %s", mPool.toString(), mCur.name()));
        poolTW.setText(String.format("%s%s", Constants.BASE_WEBSITE_URL, mPool.getWebRoot()));
        try {
            BigDecimal bVar = Utils.computeBlockVariance(lHit.getStats().getRoundShares(), Long.parseLong(lHit.getNodes().get(0).getDifficulty()));
            navTextPoolStats.setText(String.format("%s miners - Variance %s", lHit.getMinersTotal(),   bVar.stripTrailingZeros().toPlainString() + "%"));
        } catch (Exception er) {
            Log.w(Constants.TAG, "Cant write sidebar stats");
            navTextPoolStats.setText("");
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        // Handle navigation view item clicks here.
        int id = item.getItemId();

        switch (id) {
            case R.id.nav_home: {
                Intent opzioni = new Intent(DrawerActivity.this, MainActivity.class);
                startActivity(opzioni);
                break;
            }
            case R.id.nav_wallet: {
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
                    DrawerLayout drawer = findViewById(R.id.drawer_layout);
                    drawer.closeDrawer(GravityCompat.START);
                    return false;//non cambia selezione
                } else {
                    Intent miner = new Intent(this, WalletActivity.class);
                    startActivity(miner);
                }
                break;
            }
            case R.id.nav_payment: {
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
                    DrawerLayout drawer = findViewById(R.id.drawer_layout);
                    drawer.closeDrawer(GravityCompat.START);
                    return false;//non cambia selezione
                } else {
                    Intent miner = new Intent(this, PaymentsActivity.class);
                    startActivity(miner);
                }
                break;
            }
            case R.id.nav_blocks: {
                Intent bb = new Intent(DrawerActivity.this, BlocksActivity.class);
                startActivity(bb);
                break;
            }
            case R.id.nav_miners: {
                Intent bb = new Intent(DrawerActivity.this, MinersActivity.class);
                startActivity(bb);
                break;
            }
            default:
                break;
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
