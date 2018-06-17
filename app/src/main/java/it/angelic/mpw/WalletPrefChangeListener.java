package it.angelic.mpw;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceManager;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.google.firebase.analytics.FirebaseAnalytics;

import it.angelic.mpw.model.db.PoolDbHelper;
import it.angelic.mpw.model.enums.CurrencyEnum;
import it.angelic.mpw.model.enums.PoolEnum;

/**
 * Formats the watched EditText to a public ethereum address
 *
 * @author shine@angelic.it
 */
class WalletPrefChangeListener implements  Preference.OnPreferenceChangeListener {

    private final Context mCtx;
    private final PoolEnum pool;
    private final CurrencyEnum cur;
    private final FirebaseAnalytics mFirebaseAnalytics;

    public WalletPrefChangeListener(Context activity, PoolEnum pool, CurrencyEnum curr) {
        this. mCtx = activity;
        this.pool = pool;
        this.cur= curr;
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(activity);
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        if (isValidEthAddress((String) newValue)) return false;

        PoolDbHelper db = PoolDbHelper.getInstance(mCtx,pool,cur);
        db.truncateWallets(db.getWritableDatabase());
        db.close();
        //salvo wallet in SharedPrefs preciso
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mCtx);
        //change specific
        prefs.edit().putString("wallet_addr_" + pool.name() + "_" + cur.name(), (String) newValue).apply();
        //change active one, tanto per
        prefs.edit().putString("wallet_addr", (String) newValue).commit();

        Bundle bundle = new Bundle();
        //bundle.putString(FirebaseAnalytics.Param.ITEM_ID, (String) newValue);
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.LOGIN, bundle);
        Crashlytics.setUserIdentifier((String) newValue);
        return true;
    }

    private boolean isValidEthAddress(String newValue) {
        if (!newValue.startsWith("0x")) {
            Toast.makeText(mCtx,"Public addresses start with 0x", Toast.LENGTH_SHORT).show();
            return true;
        }
        //boolean isNumeric = ((String)newValue).matches("\\p{XDigit}+");
        boolean isHex = newValue.matches("^[0-9a-fA-Fx]+$");
        if(!isHex){
            Toast.makeText(mCtx,"Invalid address format, not an Hex", Toast.LENGTH_SHORT).show();
            return true;
        }
        //https://www.reddit.com/r/ethereum/comments/6l3da1/how_long_are_ethereum_addresses/
        if (newValue.length() != 42){
            Toast.makeText(mCtx,"Invalid address format, invalid length", Toast.LENGTH_SHORT).show();
            return true;
        }
        return false;
    }
}