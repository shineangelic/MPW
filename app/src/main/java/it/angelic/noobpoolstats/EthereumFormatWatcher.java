package it.angelic.noobpoolstats;

import android.content.Context;
import android.support.v7.preference.Preference;
import android.widget.Toast;

import it.angelic.noobpoolstats.model.db.NoobPoolDbHelper;

/**
 * Formats the watched EditText to a credit card number
 */
public class EthereumFormatWatcher implements  Preference.OnPreferenceChangeListener {

    private final Context mCtx;

    public EthereumFormatWatcher(Context activity) {
        this. mCtx = activity;
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        if (!((String)newValue).startsWith("0x")) {
            Toast.makeText(mCtx,"Public addresses start with 0x", Toast.LENGTH_SHORT).show();
            return false;
        }
        //boolean isNumeric = ((String)newValue).matches("\\p{XDigit}+");
        boolean isHex = ((String)newValue).matches("^[0-9a-fA-Fx]+$");
        if(!isHex){
            Toast.makeText(mCtx,"Invalid Public address format", Toast.LENGTH_SHORT).show();
            return false;
        }
        //https://www.reddit.com/r/ethereum/comments/6l3da1/how_long_are_ethereum_addresses/
        if (((String)newValue).length() != 42){
            Toast.makeText(mCtx,"Invalid address length", Toast.LENGTH_SHORT).show();
            return false;
        }

        NoobPoolDbHelper db = new NoobPoolDbHelper(mCtx);
        db.truncateWallets(db.getWritableDatabase());
        db.close();
        return true;
    }
}