package it.angelic.mpw;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import org.json.JSONArray;

import java.util.Calendar;
import java.util.Date;
import java.util.IllegalFormatException;
import java.util.Locale;

import it.angelic.mpw.model.enums.CurrencyEnum;
import it.angelic.mpw.model.enums.PoolEnum;
import it.angelic.mpw.model.db.PoolDbHelper;
import it.angelic.mpw.model.enums.PrecisionEnum;
import it.angelic.mpw.model.jsonpojos.coinmarketcap.Ticker;
import it.angelic.mpw.model.jsonpojos.wallet.Wallet;

import static android.content.Context.MODE_PRIVATE;
import static it.angelic.mpw.Constants.TAG;

/**
 * Created by shine@angelic.it on 07/09/2017.
 */

class Utils {


    private static String formatHashrate(long bytes, String precision) throws IllegalFormatException {

        // Kilobyte Check
        float kilo = bytes / 1024f;
        float mega = kilo / 1024f;
        float giga = mega / 1024f;
        float tera = giga / 1024f;
        float peta = tera / 1024f;

        // Determine which value to send back
        if (peta > 1)
            return String.format(Locale.getDefault(), precision + " P", peta);
        else if (tera > 1)
            return String.format(Locale.getDefault(), precision + " T", tera);
        else if (giga > 1)
            return String.format(Locale.getDefault(), precision + " G", giga);
        else if (mega > 1)
            return String.format(Locale.getDefault(), precision + " M", mega);
        else if (kilo > 1)
            return String.format(Locale.getDefault(), precision + " K", kilo);
        else
            return bytes + " b";

    }

    /**
     * Condense a file size in bytes to its highest form (i.e. KB, MB, GB, etc)
     *
     * @param bytes     the size in bytes to condense
     * @param precision the precision of the decimal place
     * @return the condensed file size
     */
    private static String formatHashrate(long bytes, PrecisionEnum precision) {
        return formatHashrate(bytes, precision.getFormat()) + "H";
    }

    /**
     * Condense a file size in bytes to its highest form (i.e. KB, MB, GB, etc)
     *
     * @param bytes the size in bytes
     * @return the condensed string
     */
    public static String formatHashrate(long bytes) {
        return formatHashrate(bytes, PrecisionEnum.TWO_DIGIT);
    }

    /**
     * Same as above, without unit
     *
     * @param bytes     the size in bytes to condense
     * @param precision the precision of the decimal place
     * @return the condensed file size
     */
    private static String formatBigNumber(long bytes, PrecisionEnum precision) {
        return formatHashrate(bytes, precision.getFormat());
    }

    /**
     * Same as above, without unit
     *
     * @param bytes the size in bytes
     * @return the condensed string
     */
    public static String formatBigNumber(long bytes) {
        return formatBigNumber(bytes, PrecisionEnum.TWO_DIGIT);
    }

    public static float condenseHashRate(Long aLong) {
        // Kilobyte Check
        //double roundOff = Math.round(a * 100.0) / 100.0;
        float kilo = aLong / 1024f;
        float mega = kilo / 1024f;
        float giga = mega / 1024f;
        float tera = giga / 1024f;
        float peta = tera / 1024f;

        // Determine which value to send back
        if (peta > 1)
            return peta;
        else if (tera > 1)
            return Math.round(tera * 100.0) / 100.0f;
        else if (giga > 1)
            return Math.round(giga * 100.0) / 100.0f;
        else if (mega > 1)
            return Math.round(mega * 100.0) / 100.0f;
        else if (kilo > 1)
            return Math.round(kilo * 100.0) / 100.0f;
        else
            return Math.round(aLong * 100.0) / 100.0f;
    }

    public static String getTimeAgo(Date ref) {
        Calendar cp = Calendar.getInstance();
        cp.setTime(ref);
        return getTimeAgo(cp);
    }

    /**
     * utility minutes
     *
     * @param ref
     * @return
     */
    public static String getTimeAgo(Calendar ref) {
        Calendar now = Calendar.getInstance();

        long milliseconds1 = ref.getTimeInMillis();
        long milliseconds2 = now.getTimeInMillis();
        long diff = milliseconds2 - milliseconds1;
        long diffSeconds = diff / 1000;
        return getScaledTime(diffSeconds) + " ago";
    }

    public static String getScaledTime(long diffSeconds) {
        if (diffSeconds < 120)
            return "" + diffSeconds + " sec.";
        long diffMinutes = diffSeconds / 60;
        if (diffMinutes < 120)
            return "" + diffMinutes + " min.";
        long diffHours = diffMinutes / (60);
        if (diffHours < 72)
            return "" + diffHours + " hr.";

        float diffDays = diffHours / (24f);
        return String.format(Locale.getDefault(), "%.2f", diffDays) + " days";
    }

    /**
     * Indicates whether the specified app ins installed and can used as an intent. This
     * method checks the package manager for installed packages that can
     * respond to an intent with the specified app. If no suitable package is
     * found, this method returns false.
     *
     * @param context The application's environment.
     * @param appName The name of the package you want to check
     * @return True if app is installed
     */
    public static boolean isAppAvailable(Context context, String appName) {
        PackageManager pm = context.getPackageManager();
        try {
            pm.getPackageInfo(appName, PackageManager.GET_ACTIVITIES);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    public static String formatCurrency(Long balance, CurrencyEnum cur) {
        return String.format(Locale.getDefault(), PrecisionEnum.SIX_DIGIT.getFormat() + " " + cur.name(), (balance / 1000000000F));
    }

    public static String formatEthCurrency(Long balance) {
        return formatCurrency(balance, CurrencyEnum.ETH);
    }

    public static void saveEtherValues(@Nullable Ticker result, Context ctx) {
        SharedPreferences settings = ctx.getSharedPreferences("COINMARKETCAP", MODE_PRIVATE);
        SharedPreferences.Editor prefEditor = settings.edit();

        try {
            prefEditor.putString("CURUSD", result.getPrice_usd());
            prefEditor.putString("CURCHG", result.getPercent_change_24h());
            prefEditor.putLong("CURTIMESTAMP", Long.valueOf(result.getLast_updated()) *1000);
        } catch (Exception ie) {
            Log.e(TAG, "Impossible  to save Currency values: " + ie.getMessage());
            prefEditor.remove("CURUSD");
            prefEditor.remove("CURCHG");
            prefEditor.remove("CURTIMESTAMP");
        }
        prefEditor.commit();

    }

    public static void fillEthereumStats(Context ctx, PoolDbHelper mDbHelper, NavigationView navigationView, PoolEnum activePool, CurrencyEnum cur) {
        TextView ethPlaceholder = navigationView.findViewById(R.id.textView10);
        TextView ethC = navigationView.findViewById(R.id.textViewEthCourtesy);
        TextView textViewWhoPaid = navigationView.findViewById(R.id.textViewWhoPaid);
        SharedPreferences settings = ctx.getSharedPreferences("COINMARKETCAP", MODE_PRIVATE);
        try {
            String val = settings.getString("CURUSD", "---");
            String chg = settings.getString("CURCHG", "---");
            ethC.setText("Courtesy of coinmarketcap. Last update: " + MainActivity.yearFormatExtended.format(new Date(settings.getLong("CURTIMESTAMP", 0))));
            ethPlaceholder.setText(String.format(ctx.getString(R.string.currency_placeholder),cur.name(), val,chg));
        } catch (Exception e) {
            Log.e(TAG, "Error eth currency panel:" + e.getMessage());
            ethC.setVisibility(View.INVISIBLE);
            ethPlaceholder.setVisibility(View.INVISIBLE);
        }
        try {
            Wallet last = mDbHelper.getLastWallet();
            textViewWhoPaid.setText(String.format(ctx.getString(R.string.paid_out_full), activePool.toString(),""+Utils.formatCurrency(last.getStats().getPaid(),cur)));
        } catch (Exception e) {
            Log.e(TAG, "Errore aggiornamento eth paid panel: " + e.getMessage());
            textViewWhoPaid.setVisibility(View.INVISIBLE);
        }
    }
    public static String getHomeStatsURL( SharedPreferences prefs) {
        String mPool = prefs.getString("poolEnum", "");
        String mCur = prefs.getString("curEnum", "");
        //prefs.getString("wallet_addr" + PoolEnum.valueOf(mPool).name() + "_" + CurrencyEnum.valueOf(mCur).name(), "");
        PoolEnum puil = PoolEnum.valueOf(mPool);
        String compose = (puil.getOmitCurrency()?"":mCur.toLowerCase())+ puil.getRadixSuffix();
        return puil.getTransportProtocolBase() + compose +(compose.length()==0?"":".")  + puil.getWebRoot() + Constants.HOME_STATS_URL;
    }
    public static String getWalletStatsUrl(SharedPreferences prefs) {
        String mPool = prefs.getString("poolEnum", "");
        String mCur = prefs.getString("curEnum", "");
        PoolEnum tgtpool = PoolEnum.valueOf(mPool);
        String compose = (tgtpool.getOmitCurrency()?"":mCur.toLowerCase())+ tgtpool.getRadixSuffix();
        return tgtpool.getTransportProtocolBase() + compose +(compose.length()==0?"":".")  + tgtpool.getWebRoot() + Constants.ACCOUNTS_STATS_URL;
    }

    public static String getMinersStatsUrl( SharedPreferences prefs) {
        String mPool = prefs.getString("poolEnum", "");
        String mCur = prefs.getString("curEnum", "");
        PoolEnum tgtpool = PoolEnum.valueOf(mPool);
        String compose = (tgtpool.getOmitCurrency()?"":mCur.toLowerCase())+ tgtpool.getRadixSuffix();
        return tgtpool.getTransportProtocolBase() + compose +(compose.length()==0?"":".")  + tgtpool.getWebRoot() + Constants.MINERS_STATS_URL;
    }

    public static String getBlocksURL( SharedPreferences prefs) {
        String mPool = prefs.getString("poolEnum", "");
        String mCur = prefs.getString("curEnum", "");
        PoolEnum tgtpool = PoolEnum.valueOf(mPool);
        String compose = (tgtpool.getOmitCurrency()?"":mCur.toLowerCase())+ tgtpool.getRadixSuffix();
        return tgtpool.getTransportProtocolBase()+ compose +(compose.length()==0?"":".")  + tgtpool.getWebRoot() + Constants.BLOCKS_URL;
    }


    public static String formatEthAddress(String minerAddr) {
        if (minerAddr==null||minerAddr.isEmpty())
            return "";
        //boh
        if (!minerAddr.startsWith("0x"))
            return minerAddr;

        return "0x" + minerAddr.substring(2).toUpperCase();
    }
}
