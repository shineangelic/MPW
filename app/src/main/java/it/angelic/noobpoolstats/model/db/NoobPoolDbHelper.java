package it.angelic.noobpoolstats.model.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.apache.commons.collections4.map.LinkedMap;

import java.util.Calendar;
import java.util.Date;

import it.angelic.noobpoolstats.R;
import it.angelic.noobpoolstats.model.jsonpojos.home.HomeStats;
import it.angelic.noobpoolstats.model.jsonpojos.wallet.Wallet;

import static android.content.ContentValues.TAG;


public class NoobPoolDbHelper extends SQLiteOpenHelper {
    // If you change the database schema, you must increment the database version.
    private static final int DATABASE_VERSION =3;
    private static final String DATABASE_NAME = "FeedReader.db";
    private static final String SQL_CREATE_HomeSTATS =
            "CREATE TABLE " + NoobDataBaseContract.HomeStats_.TABLE_NAME + " (" +
                    NoobDataBaseContract.HomeStats_._ID + " INTEGER PRIMARY KEY," +
                    NoobDataBaseContract.HomeStats_.COLUMN_NAME_DTM + " INTEGER," +
                    NoobDataBaseContract.HomeStats_.COLUMN_NAME_JSON + " TEXT)";
    private static final String SQL_CREATE_WALLET =
            "CREATE TABLE " + NoobDataBaseContract.Wallet_.TABLE_NAME + " (" +
                    NoobDataBaseContract.Wallet_._ID + " INTEGER PRIMARY KEY," +
                    NoobDataBaseContract.Wallet_.COLUMN_NAME_DTM + " INTEGER," +
                    NoobDataBaseContract.Wallet_.COLUMN_NAME_JSON + " TEXT)";

    private static final String SQL_CREATE_HOME_IDX =
            "CREATE INDEX " + NoobDataBaseContract.HomeStats_.TABLE_NAME + "_dtm_idx ON "
                    + NoobDataBaseContract.HomeStats_.TABLE_NAME + "(" + NoobDataBaseContract.HomeStats_.COLUMN_NAME_DTM + ");";
    private static final String SQL_DELETE_STATS_TABLE =
            "DROP TABLE IF EXISTS " + NoobDataBaseContract.HomeStats_.TABLE_NAME;
    private static final String SQL_DELETE_WALLET_TABLE =
            "DROP TABLE IF EXISTS " + NoobDataBaseContract.Wallet_.TABLE_NAME;
    private static final String SQL_TRUNCATE_WALLET =
            "DELETE FROM " + NoobDataBaseContract.Wallet_.TABLE_NAME;
    private static final String SQL_VACUUM = "VACUUM";
    private final GsonBuilder builder;


    public NoobPoolDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        builder = new GsonBuilder();
    }

    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_HomeSTATS);
        db.execSQL(SQL_CREATE_WALLET);
        db.execSQL(SQL_CREATE_HOME_IDX);
    }

    public void cleanOldDate(SQLiteDatabase db) {
        Log.w("DB", "SQL_ TODO ");
        Calendar oneMonthAgo = Calendar.getInstance();
        oneMonthAgo.add(Calendar.MONTH, -1);
        db.delete(NoobDataBaseContract.HomeStats_.TABLE_NAME,
                 NoobDataBaseContract.HomeStats_.COLUMN_NAME_DTM + " < " + oneMonthAgo.getTime().getTime(), null);
        db.delete(NoobDataBaseContract.Wallet_.TABLE_NAME,
                 NoobDataBaseContract.Wallet_.COLUMN_NAME_DTM + " < " + oneMonthAgo.getTime().getTime(), null);

        db.execSQL(SQL_VACUUM);
        db.close();
    }

    public void truncateWallets(SQLiteDatabase db) {
        Log.w("DB", "SQL_TRUNCATE_WALLET: " + SQL_TRUNCATE_WALLET);
        db.execSQL(SQL_TRUNCATE_WALLET);
        db.execSQL(SQL_VACUUM);
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        db.execSQL(SQL_DELETE_STATS_TABLE);
        db.execSQL(SQL_DELETE_WALLET_TABLE);
        onCreate(db);
    }

    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }

    // Adding new contact
    public void logHomeStats(HomeStats contact) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(NoobDataBaseContract.HomeStats_.COLUMN_NAME_DTM, contact.getNow().getTime().getTime()); // Contact Name
        values.put(NoobDataBaseContract.HomeStats_.COLUMN_NAME_JSON, new Gson().toJson(contact)); // Serializza

        // Inserting Row
        db.insert(NoobDataBaseContract.HomeStats_.TABLE_NAME, null, values);
        db.close(); // Closing database connection
    }

    public void logWalletStats(Wallet retrieved) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(NoobDataBaseContract.Wallet_.COLUMN_NAME_DTM, new Date().getTime()); // Contact Name
        values.put(NoobDataBaseContract.Wallet_.COLUMN_NAME_JSON, new Gson().toJson(retrieved)); // Serializza

        // Inserting Row
        db.insert(NoobDataBaseContract.Wallet_.TABLE_NAME, null, values);
        db.close(); // Closing database connection
    }

    public LinkedMap<Date, HomeStats> getHistoryData(int cutoff) {
        int cnt = 0;
        LinkedMap<Date, HomeStats> ret = new LinkedMap<Date, HomeStats>();
        SQLiteDatabase db = this.getReadableDatabase();
        String limitCause = "";
        Calendar now = Calendar.getInstance();
        switch (cutoff) {
            case R.id.radioButtonOneDay:
                now.add(Calendar.DATE, -1);
                limitCause = NoobDataBaseContract.HomeStats_.COLUMN_NAME_DTM + "  > " + now.getTime().getTime();
                break;
            case R.id.radioButtonOneWeek:
                now.add(Calendar.DATE, -7);
                limitCause = NoobDataBaseContract.HomeStats_.COLUMN_NAME_DTM + "  > " + now.getTime().getTime();
                break;
            case R.id.radioButtonOneMonth:
                now.add(Calendar.MONTH, -1);
                limitCause = NoobDataBaseContract.HomeStats_.COLUMN_NAME_DTM + "  > " + now.getTime().getTime();
                break;
            default:
                Log.e("DB", "Unexpected switch ERROR");
                break;
        }
        // Cursor cursor = db.rawQuery(selectQuery, null);
        Cursor cursor = db.query(NoobDataBaseContract.HomeStats_.TABLE_NAME, new String[]{
                        NoobDataBaseContract.HomeStats_._ID,
                        NoobDataBaseContract.HomeStats_.COLUMN_NAME_DTM,
                        NoobDataBaseContract.HomeStats_.COLUMN_NAME_JSON},
                limitCause,
                null,// String[] selectionArgs
                null,
                null, // HAVING
                NoobDataBaseContract.HomeStats_.COLUMN_NAME_DTM + " ASC");// ORDER BY

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Gson gson = builder.create();
                // Register an adapter to manage the date types as long values
                HomeStats retrieved = gson.fromJson(cursor.getString(cursor.getColumnIndexOrThrow(NoobDataBaseContract.HomeStats_.COLUMN_NAME_JSON)), HomeStats.class);
                cnt++;
                // Adding contact to list
                ret.put(retrieved.getNow().getTime(), retrieved);
            } while (cursor.moveToNext());
        }
        Log.i(TAG, "SELECT DONE. HOME HISTORY SIZE: " + cnt);
        cursor.close();
        db.close();
        return ret;
    }

    /**
     * This methods can serve stats/charts only, as it is not precise
     * The last block pending before payout is not being counted.
     *
     * @return average 'pending' increase per block
     */
    public Long getAveragePending() {
        int cnt = 1;
        Long pendings = 0L;
        Long prevPending = 0L;
        SQLiteDatabase db = this.getReadableDatabase();
        // Cursor cursor = db.rawQuery(selectQuery, null);
        Cursor cursor = db.query(NoobDataBaseContract.Wallet_.TABLE_NAME, new String[]{
                        NoobDataBaseContract.Wallet_._ID,
                        NoobDataBaseContract.Wallet_.COLUMN_NAME_DTM,
                        NoobDataBaseContract.Wallet_.COLUMN_NAME_JSON},
                null,
                null,// String[] selectionArgs
                null,
                null, // HAVING
                NoobDataBaseContract.Wallet_.COLUMN_NAME_DTM + " ASC");// ORDER BY

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Gson gson = builder.create();
                // Register an adapter to manage the date types as long values
                Wallet retrieved = gson.fromJson(cursor.getString(cursor.getColumnIndexOrThrow(NoobDataBaseContract.Wallet_.COLUMN_NAME_JSON)), Wallet.class);
                cnt++;
                Long curPending = retrieved.getStats().getBalance().longValue();
                if (curPending > prevPending){
                    //pending increased
                    pendings += ( curPending - prevPending  );

                }
                prevPending = curPending;
            } while (cursor.moveToNext());
        }
        Log.i(TAG, "SELECT DONE. PENDINGS HISTORY SIZE: " + cnt);
        cursor.close();
        db.close();

        return pendings / cnt;
    }


    public LinkedMap<Date, Wallet> getWalletHistoryData(int checkedRadioButtonId) {
        LinkedMap<Date, Wallet> ret = new LinkedMap();
        int cnt = 0;
        SQLiteDatabase db = this.getReadableDatabase();
        String limitCause = "";
        Calendar now = Calendar.getInstance();
        switch (checkedRadioButtonId) {
            case R.id.radioButtonOneDayMiner:
                now.add(Calendar.DATE, -1);
                limitCause = NoobDataBaseContract.Wallet_.COLUMN_NAME_DTM + "  > " + now.getTime().getTime();
                break;
            case R.id.radioButtonOneWeekMiner:
                now.add(Calendar.DATE, -7);
                limitCause = NoobDataBaseContract.Wallet_.COLUMN_NAME_DTM + "  > " + now.getTime().getTime();
                break;
            case R.id.radioButtonOneMonthMiner:
                now.add(Calendar.MONTH, -1);
                limitCause = NoobDataBaseContract.Wallet_.COLUMN_NAME_DTM + "  > " + now.getTime().getTime();
                break;
            default:
                Log.e("DB", "Unexpected switch ERROR");
                break;
        }
        // Cursor cursor = db.rawQuery(selectQuery, null);
        Cursor cursor = db.query(NoobDataBaseContract.Wallet_.TABLE_NAME, new String[]{
                        NoobDataBaseContract.Wallet_._ID,
                        NoobDataBaseContract.Wallet_.COLUMN_NAME_DTM,
                        NoobDataBaseContract.Wallet_.COLUMN_NAME_JSON},
                limitCause,
                null,// String[] selectionArgs
                null,
                null, // HAVING
                NoobDataBaseContract.Wallet_.COLUMN_NAME_DTM + " ASC");// ORDER BY

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Gson gson = builder.create();
                // Register an adapter to manage the date types as long values
                Wallet retrieved = gson.fromJson(cursor.getString(cursor.getColumnIndexOrThrow(NoobDataBaseContract.Wallet_.COLUMN_NAME_JSON)), Wallet.class);
                cnt++;
                // Adding contact to list
                Date curDate = new Date(cursor.getLong(cursor.getColumnIndexOrThrow(NoobDataBaseContract.Wallet_.COLUMN_NAME_DTM)));
                ret.put(curDate, retrieved);
            } while (cursor.moveToNext());
        }
        Log.i(TAG, "SELECT DONE. WALLET HISTORY SIZE: " + cnt);
        cursor.close();
        db.close();
        return ret;
    }

    public LinkedMap<Date, HomeStats> getLastHomeStats(int limit) {
        LinkedMap<Date, HomeStats> ret = new LinkedMap();
        int cnt = 0;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(NoobDataBaseContract.HomeStats_.TABLE_NAME, new String[]{
                        NoobDataBaseContract.HomeStats_._ID,
                        NoobDataBaseContract.HomeStats_.COLUMN_NAME_DTM,
                        NoobDataBaseContract.HomeStats_.COLUMN_NAME_JSON},
                null,
                null,// String[] selectionArgs
                null,
                null, // HAVING
                NoobDataBaseContract.HomeStats_.COLUMN_NAME_DTM + " DESC",
                "" + limit);//2 results to do compare

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Gson gson = builder.create();
                // Register an adapter to manage the date types as long values
                HomeStats retrieved = gson.fromJson(cursor.getString(cursor.getColumnIndexOrThrow(NoobDataBaseContract.Wallet_.COLUMN_NAME_JSON)), HomeStats.class);
                cnt++;
                // Adding contact to list
                Date curDate = new Date(cursor.getLong(cursor.getColumnIndexOrThrow(NoobDataBaseContract.Wallet_.COLUMN_NAME_DTM)));
                ret.put(curDate, retrieved);
            } while (cursor.moveToNext());
        }
        Log.i(TAG, "SELECT DONE. WALLET HISTORY SIZE: " + cnt);
        cursor.close();
        db.close();
        return ret;
    }
    public Wallet getLastWallet() {
        LinkedMap<Date, Wallet> ret = getLastWallets(1);
        return ret.get(ret.firstKey());
    }

    public LinkedMap<Date, Wallet> getLastWallets(int limit) {
        LinkedMap<Date, Wallet> ret = new LinkedMap();
        int cnt = 0;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(NoobDataBaseContract.Wallet_.TABLE_NAME, new String[]{
                        NoobDataBaseContract.Wallet_._ID,
                        NoobDataBaseContract.Wallet_.COLUMN_NAME_DTM,
                        NoobDataBaseContract.Wallet_.COLUMN_NAME_JSON},
                null,
                null,// String[] selectionArgs
                null,
                null, // HAVING
                NoobDataBaseContract.Wallet_.COLUMN_NAME_DTM + " DESC",
                "" + limit);//2 results to do compare

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Gson gson = builder.create();
                // Register an adapter to manage the date types as long values
                Wallet retrieved = gson.fromJson(cursor.getString(cursor.getColumnIndexOrThrow(NoobDataBaseContract.Wallet_.COLUMN_NAME_JSON)), Wallet.class);
                cnt++;
                // Adding contact to list
                Date curDate = new Date(cursor.getLong(cursor.getColumnIndexOrThrow(NoobDataBaseContract.Wallet_.COLUMN_NAME_DTM)));
                ret.put(curDate, retrieved);
            } while (cursor.moveToNext());
        }
        Log.i(TAG, "SELECT DONE. WALLET HISTORY SIZE: " + cnt);
        cursor.close();
        db.close();
        return ret;
    }



}