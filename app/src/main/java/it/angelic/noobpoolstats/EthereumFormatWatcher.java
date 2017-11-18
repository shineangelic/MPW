package it.angelic.noobpoolstats;

import android.content.Context;
import android.support.v7.preference.Preference;
import android.widget.Toast;

import it.angelic.noobpoolstats.model.db.NoobPoolDbHelper;

/**
 * Formats the watched EditText to a public ethereum address
 *
 * @author shine@angelic.it
 */
public class EthereumFormatWatcher implements  Preference.OnPreferenceChangeListener {

    private final Context mCtx;

    public EthereumFormatWatcher(Context activity) {
        this. mCtx = activity;
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        if (isValidEthAddress((String) newValue)) return false;

        NoobPoolDbHelper db = new NoobPoolDbHelper(mCtx);
        db.truncateWallets(db.getWritableDatabase());
        db.close();
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
            Toast.makeText(mCtx,"Invalid Ethereum address format, not an Hex", Toast.LENGTH_SHORT).show();
            return true;
        }
        //https://www.reddit.com/r/ethereum/comments/6l3da1/how_long_are_ethereum_addresses/
        if (newValue.length() != 42){
            Toast.makeText(mCtx,"Invalid Ethereum address format, invalid length", Toast.LENGTH_SHORT).show();
            return true;
        }
        return false;
    }
}