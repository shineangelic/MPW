package it.angelic.mpw;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.Calendar;
import java.util.Date;
import java.util.IllegalFormatException;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import it.angelic.mpw.model.db.PoolDbHelper;
import it.angelic.mpw.model.enums.CurrencyEnum;
import it.angelic.mpw.model.enums.PoolEnum;
import it.angelic.mpw.model.enums.PrecisionEnum;
import it.angelic.mpw.model.jsonpojos.blocks.Matured;
import it.angelic.mpw.model.jsonpojos.wallet.Wallet;

import static android.content.Context.MODE_PRIVATE;
import static it.angelic.mpw.Constants.TAG;

/**
 * utilities varie, ispirate da stackovrflw
 * Created by shine@angelic.it on 07/09/2017.
 */

class Utils {


    private static String formatHashrate(long bytes, String precision) throws IllegalFormatException {

        // Kilobyte Check
        float kilo = bytes / 1000f;
        float mega = kilo / 1000f;
        float giga = mega / 1000f;
        float tera = giga / 1000f;
        float peta = tera / 1000f;

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

    public static float condenseHashRate(Long aLong) {
        // Kilobyte Check
        //double roundOff = Math.round(a * 100.0) / 100.0;
        float kilo = aLong / 1000f;
        float mega = kilo / 1000f;
        float giga = mega / 1000f;
        float tera = giga / 1000f;
        float peta = tera / 1000f;

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
        return String.format(Locale.getDefault(), "%.0f", diffDays) + " days";
    }

    private static String formatGenericCurrency(Context ctx, Double balance) {
        Locale current = ctx.getResources().getConfiguration().locale;
        return String.format(current, PrecisionEnum.FIVE_DIGIT.getFormat(), (balance / 1000000000F));
    }

    public static String formatGenericCurrency(Context ctx, Double balance, PrecisionEnum fmt) {
        Locale current = ctx.getResources().getConfiguration().locale;
        return String.format(current, fmt.getFormat(), (balance / 1000000000F));
    }

    public static String formatUSDCurrency(Context ctx, Double balance) {
        Locale current = ctx.getResources().getConfiguration().locale;
        return String.format(current, PrecisionEnum.TWO_DIGIT.getFormat(), (balance / 1000000000F));
    }

    public static String formatCurrency(Context ctx, Double balance, CurrencyEnum cur) {
        return formatGenericCurrency(ctx, balance) + " " + cur.name();
    }

    public static String formatEthCurrency(Context ctx, Double balance) {
        return formatCurrency(ctx, balance, CurrencyEnum.ETH);
    }

    public static void fillEthereumStats(Context ctx, PoolDbHelper mDbHelper, NavigationView navigationView, PoolEnum activePool, CurrencyEnum cur) {
        TextView ethPlaceholder = navigationView.findViewById(R.id.textView10);
        TextView ethC = navigationView.findViewById(R.id.textViewEthCourtesy);
        TextView textViewWhoPaid = navigationView.findViewById(R.id.textViewWhoPaid);
        SharedPreferences settings = ctx.getSharedPreferences("COINMARKETCAP", MODE_PRIVATE);
        try {
            //String val = settings.getString("CURUSD", "---");
            String chg = CryptoSharedPreferencesUtils.readEtherChange24hValue(ctx);
            ethC.setText("Courtesy of coinmarketcap. Last update: " + MainActivity.yearFormatExtended.format(new Date(settings.getLong("CURTIMESTAMP", 0))));
            ethPlaceholder.setText(String.format(ctx.getString(R.string.currency_placeholder), cur.name(), CryptoSharedPreferencesUtils.readEtherValues(ctx), Double.valueOf(chg)));
        } catch (Exception e) {
            Log.e(TAG, "Error eth currency panel:" + e.getMessage());
            ethC.setVisibility(View.INVISIBLE);
            ethPlaceholder.setVisibility(View.INVISIBLE);
        }
        try {
            Wallet last = mDbHelper.getLastWallet();
            textViewWhoPaid.setText(String.format(ctx.getString(R.string.paid_out_full), activePool.toString(), "" + Utils.formatCurrency(ctx, last.getStats().getPaid(), cur)));
        } catch (Exception e) {
            Log.e(TAG, "Errore aggiornamento eth paid panel: " + e.getMessage());
            textViewWhoPaid.setVisibility(View.INVISIBLE);
        }
    }

    public static String getHomeStatsURL(SharedPreferences prefs) {
        return getPoolBaseURL(prefs) + Constants.HOME_STATS_URL;
    }

    public static String getWalletStatsUrl(SharedPreferences prefs) {
        return getPoolBaseURL(prefs) + Constants.ACCOUNTS_STATS_URL;
    }

    public static String getMinersStatsUrl(SharedPreferences prefs) {
        return getPoolBaseURL(prefs) + Constants.MINERS_STATS_URL;
    }

    public static String getBlocksURL(SharedPreferences prefs) {
        return getPoolBaseURL(prefs) + Constants.BLOCKS_URL;
    }

    @NonNull
    private static String getPoolBaseURL(SharedPreferences prefs) {
        String mPool = prefs.getString("poolEnum", "");
        String mCur = prefs.getString("curEnum", "");
        PoolEnum chosenPool = PoolEnum.valueOf(mPool);
        String currencyPath = (chosenPool.getOmitCurrency() ? "" : mCur.toLowerCase()) + chosenPool.getRadixSuffix();
        return chosenPool.getTransportProtocolBase()+currencyPath+ (currencyPath.length() == 0 ? "" : ".") + chosenPool.getWebRoot();
    }


    public static String formatEthAddress(String minerAddr) {
        if (minerAddr == null || minerAddr.isEmpty())
            return "";
        //boh
        if (!minerAddr.startsWith("0x") || minerAddr.length() < 3)
            return minerAddr;

        return "0x" + minerAddr.substring(2).toUpperCase();
    }

    public static double getPoolBlockPerDay(List<Matured> matured) {
        if (matured == null || matured.size() < 2)
            return 0;

        Date firstDate = matured.get(0).getTimestamp();
        Date lastD = matured.get(matured.size() - 1).getTimestamp();

        long difference = firstDate.getTime() - lastD.getTime();
        long diffHours = TimeUnit.HOURS.convert(difference, TimeUnit.MILLISECONDS);
        Log.d("blocks interval:", diffHours + "");

        return matured.size() / ((double) diffHours / 24);
    }

    private static double getPoolBlockAvgReward(List<Matured> matured) {
        if (matured == null || matured.size() < 1)
            return 0;

        double summer = 0;
        int cnt = 0;

        // Add the data from the array
        for (Matured m : matured) {
            //sort of 'works, dunno why' thing
            summer += Double.valueOf(m.getReward()) / 1000000000d;
            cnt++;
        }
        return summer / cnt;
    }

    public static double getDailyProfitProjection(double sharePercent, List<Matured> matured) {
        double avp = getPoolBlockAvgReward(matured);
        double blockEarnProj = sharePercent * avp;
        return blockEarnProj * getPoolBlockPerDay(matured);
    }

    /**
     *  Variance % = Pool Shares / Network Difficulty Thanks to alfred
     *
     * @param roundShares
     * @param difficulty
     * @return Variance in perc.
     */
    public static BigDecimal computeBlockVariance(long roundShares, long difficulty) {
        MathContext mc = new MathContext(4, RoundingMode.HALF_UP);
        BigDecimal bigDecX = new BigDecimal(roundShares);
        BigDecimal bigDecY = new BigDecimal(difficulty);
        return bigDecX.divide(bigDecY, mc).multiply(new BigDecimal(100));
    }

}
