package it.angelic.noobpoolstats;

import android.content.Context;
import android.content.pm.PackageManager;

import java.util.Calendar;
import java.util.IllegalFormatException;

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
     * Condense a file size in bytes to it's highest form (i.e. KB, MB, GB, etc)
     *
     * @param bytes         the size in bytes to condense
     * @param precision     the precision of the decimal place
     * @return              the condensed file size
     */
    private static String formatHashrate(long bytes, PrecisionEnum precision){
        return formatHashrate(bytes, precision.getFormat()) + "H";
    }

    /**
     * Condense a file size in bytes to it's highest form (i.e. KB, MB, GB, etc)
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
            return "" + diffMinutes + " minutes";
        long diffHours = diffMinutes / (60);
        if (diffHours < 72)
            return "" + diffHours + " hours";

        float diffDays = diffHours / (24f);
        return diffDays +" days";
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
}
