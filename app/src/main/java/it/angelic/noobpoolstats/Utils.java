package it.angelic.noobpoolstats;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.support.design.widget.NavigationView;
import android.util.Log;
import android.widget.TextView;

import java.util.Calendar;
import java.util.Date;
import java.util.IllegalFormatException;
import java.util.Locale;

import it.angelic.noobpoolstats.model.db.NoobPoolDbHelper;
import it.angelic.noobpoolstats.model.jsonpojos.etherscan.Result;
import it.angelic.noobpoolstats.model.jsonpojos.wallet.Wallet;

import static android.content.Context.MODE_PRIVATE;
import static it.angelic.noobpoolstats.MainActivity.TAG;

/**
 * Created by shine@angelic.it on 07/09/2017.
 */

class Utils {


    private static String formatHashrate(long bytes, String precision ) throws IllegalFormatException {

        // Kilobyte Check
        float kilo = bytes / 1000f;
        float mega = kilo / 1000f;
        float giga = mega / 1000f;
        float tera = giga / 1000f;
        float peta = tera / 1000f;

        // Determine which value to send back
        if(peta > 1)
            return String.format(precision + " P", peta);
        else if (tera > 1)
            return String.format(precision + " T", tera);
        else if(giga > 1)
            return String.format(precision + " G", giga);
        else if(mega > 1)
            return String.format(precision + " M", mega);
        else if(kilo > 1)
            return String.format(precision + " K", kilo);
        else
            return bytes + " b";

    }
    /**
     * Condense a file size in bytes to its highest form (i.e. KB, MB, GB, etc)
     *
     * @param bytes         the size in bytes to condense
     * @param precision     the precision of the decimal place
     * @return              the condensed file size
     */
    private static String formatHashrate(long bytes, PrecisionEnum precision){
        return formatHashrate(bytes, precision.getFormat()) + "H";
    }

    /**
     * Condense a file size in bytes to its highest form (i.e. KB, MB, GB, etc)
     *
     * @param bytes		the size in bytes
     * @return			the condensed string
     */
    public static String formatHashrate(long bytes){
        return formatHashrate(bytes, PrecisionEnum.TWO_DIGIT);
    }

    /**
     * Same as above, without unit
     *
     * @param bytes         the size in bytes to condense
     * @param precision     the precision of the decimal place
     * @return              the condensed file size
     */
    private static String formatBigNumber(long bytes, PrecisionEnum precision){
        return formatHashrate(bytes, precision.getFormat());
    }

    /**
     * Same as above, without unit
     *
     * @param bytes		the size in bytes
     * @return			the condensed string
     */
    public static String formatBigNumber(long bytes){
        return formatBigNumber(bytes, PrecisionEnum.TWO_DIGIT);
    }

    public static int condenseHashRate(Long aLong) {
        // Kilobyte Check
        float kilo = aLong / 1024f;
        float mega = kilo / 1024f;
        float giga = mega / 1024f;
        float tera = giga / 1024f;
        float peta = tera / 1024f;

        // Determine which value to send back
        if(peta > 1)
            return (int) peta;
        else if (tera > 1)
            return (int) tera;
        else if(giga > 1)
            return (int) giga;
        else if(mega > 1)
            return (int) mega;
        else if(kilo > 1)
            return (int) kilo;
        else
            return aLong.intValue();
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

    public  static String getScaledTime(long diffSeconds) {
        if (diffSeconds < 120)
            return "" + diffSeconds + " sec.";
        long diffMinutes = diffSeconds / 60;
        if (diffMinutes < 120)
            return "" + diffMinutes + " min.";
        long diffHours = diffMinutes / (60);
        if (diffHours < 72)
            return "" + diffHours + " hr.";

        float diffDays = diffHours / (24f);
        return String.format(Locale.getDefault(),"%.2f", diffDays) +" days";
    }
    /**
     * Indicates whether the specified app ins installed and can used as an intent. This
     * method checks the package manager for installed packages that can
     * respond to an intent with the specified app. If no suitable package is
     * found, this method returns false.
     *
     * @param context The application's environment.
     * @param appName The name of the package you want to check
     *
     * @return True if app is installed
     */
    public static boolean isAppAvailable(Context context, String appName)
    {
        PackageManager pm = context.getPackageManager();
        try
        {
            pm.getPackageInfo(appName, PackageManager.GET_ACTIVITIES);
            return true;
        }
        catch (PackageManager.NameNotFoundException e)
        {
            return false;
        }
    }

    public static String formatEthCurrency(Long balance) {
        return (balance / 1000000000F) + "ETH";
    }

    public static void saveEtherValues(Result result, Context ctx) {
        SharedPreferences settings = ctx.getSharedPreferences("ETHERSCAN", MODE_PRIVATE);
        SharedPreferences.Editor prefEditor = settings.edit();
        try {
            prefEditor.putString("ETHUSD", result.getEthusd());
            prefEditor.putString("ETHBTC", result.getEthbtc());
            prefEditor.putLong("ETHTIMESTAMP", result.getEthusd_timestamp().getTime());
        } catch (Exception ie) {
            Log.e(TAG, "Impossible  to save Ether values: "+ ie.getMessage());
        }


        prefEditor.commit();

    }

    public static void fillEthereumStats(Context ctx, NoobPoolDbHelper mDbHelper, NavigationView navigationView) {
        navigationView.setCheckedItem(R.id.nav_blocks);
        Wallet last = mDbHelper.getLastWallet();
        SharedPreferences settings = ctx.getSharedPreferences("ETHERSCAN", MODE_PRIVATE);
        TextView eth = navigationView.findViewById(R.id.textViewEthValue);
        TextView ethC = navigationView.findViewById(R.id.textViewEthCourtesy);
        TextView textViewCurbalance = navigationView.findViewById(R.id.textViewCurbalance);
        String val = settings.getString("ETHUSD" ,"---");
        eth.setText(val);
        ethC.setText("Courtesy of etherscan.io. Last update: "+ MainActivity.yearFormatExtended.format(new Date(settings.getLong("ETHTIMESTAMP" ,0))));
        textViewCurbalance.setText(""+(last.getStats().getPaid() /   1000000000F) * Float.valueOf(val) );
    }
}
