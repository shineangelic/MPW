package it.angelic.mpw;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.Nullable;
import android.util.Log;

import it.angelic.mpw.model.jsonpojos.coinmarketcap.Ticker;

import static android.content.Context.MODE_PRIVATE;
import static it.angelic.mpw.Constants.TAG;

/**
 * Created by shine@angelic.it on 12/03/2018.
 */

public class CryptoSharedPreferencesUtils {

    public static void cleanValues(Context ctx){
        SharedPreferences settings = ctx.getSharedPreferences("COINMARKETCAP", MODE_PRIVATE);
        SharedPreferences.Editor prefEditor = settings.edit();
        Log.w(Constants.TAG, "Cleaning marketCap stats");
        prefEditor.remove("BTCUSD");
        prefEditor.remove("BTCCHG");
        prefEditor.remove("BTCTIMESTAMP");

        prefEditor.remove("ETHUSD");
        prefEditor.remove("ETHCHG");
        prefEditor.remove("ETHTIMESTAMP");

        prefEditor.remove("CURUSD");
        prefEditor.remove("CURCHG");
        prefEditor.remove("CURTIMESTAMP");

        prefEditor.apply();
    }

    public static void saveBtcValues(@Nullable Ticker result, Context ctx) {
        SharedPreferences settings = ctx.getSharedPreferences("COINMARKETCAP", MODE_PRIVATE);
        SharedPreferences.Editor prefEditor = settings.edit();
        Log.w(Constants.TAG, "save BTC value: " + result);
        try {
            prefEditor.putString("BTCUSD", result.getPrice_usd());
            prefEditor.putString("BTCCHG", result.getPercent_change_24h());
            prefEditor.putLong("BTCTIMESTAMP", Long.valueOf(result.getLast_updated()) * 1000);
        } catch (Exception ie) {
            Log.e(TAG, "Impossible  to save BTC values: " + ie.getMessage());
            prefEditor.remove("BTCUSD");
            prefEditor.remove("BTCCHG");
            prefEditor.remove("BTCTIMESTAMP");
        }
        prefEditor.commit();
    }

    public static void saveEthereumValues(@Nullable Ticker result, Context ctx) {
        SharedPreferences settings = ctx.getSharedPreferences("COINMARKETCAP", MODE_PRIVATE);
        SharedPreferences.Editor prefEditor = settings.edit();
        Log.w(Constants.TAG, "save ETH value: " + result);
        try {
            prefEditor.putString("ETHUSD", result.getPrice_usd());
            prefEditor.putString("ETHCHG", result.getPercent_change_24h());
            prefEditor.putLong("ETHTIMESTAMP", Long.valueOf(result.getLast_updated()) * 1000);
        } catch (Exception ie) {
            Log.e(TAG, "Impossible  to save ETH values: " + ie.getMessage());
            prefEditor.remove("ETHUSD");
            prefEditor.remove("ETHCHG");
            prefEditor.remove("ETHTIMESTAMP");
        }
        prefEditor.commit();

    }

    public static void saveEtherValues(@Nullable Ticker result, Context ctx) {
        SharedPreferences settings = ctx.getSharedPreferences("COINMARKETCAP", MODE_PRIVATE);
        SharedPreferences.Editor prefEditor = settings.edit();
        Log.w(Constants.TAG, "save value: " + result);
        try {
            prefEditor.putString("CURUSD", result.getPrice_usd());
            prefEditor.putString("CURCHG", result.getPercent_change_24h());
            prefEditor.putLong("CURTIMESTAMP", Long.valueOf(result.getLast_updated()) * 1000);
        } catch (Exception ie) {
            Log.e(TAG, "Impossible  to save Currency values: " + ie.getMessage());
            prefEditor.remove("CURUSD");
            prefEditor.remove("CURCHG");
            prefEditor.remove("CURTIMESTAMP");
        }
        prefEditor.commit();

    }
}
