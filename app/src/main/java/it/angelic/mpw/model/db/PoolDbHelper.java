package it.angelic.mpw.model.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.apache.commons.collections4.map.LinkedMap;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import it.angelic.mpw.Constants;
import it.angelic.mpw.model.enums.BackToEnum;
import it.angelic.mpw.model.enums.CurrencyEnum;
import it.angelic.mpw.model.enums.MinerSortEnum;
import it.angelic.mpw.model.enums.PoolEnum;
import it.angelic.mpw.model.jsonpojos.home.HomeStats;
import it.angelic.mpw.model.jsonpojos.miners.Miner;
import it.angelic.mpw.model.jsonpojos.wallet.Wallet;

import static android.content.ContentValues.TAG;


public class PoolDbHelper extends SQLiteOpenHelper {
    // If you change the database schema, you must increment the database version.
    private static final int DATABASE_VERSION = 6;
    private static final String DATABASE_NAME = "MinerPoolWatcher.db";
    private static final String SQL_CREATE_HomeSTATS =
            "CREATE TABLE " + DataBaseContract.HomeStats_.TABLE_NAME + " (" +
                    DataBaseContract.HomeStats_._ID + " INTEGER PRIMARY KEY," +
                    DataBaseContract.HomeStats_.COLUMN_NAME_DTM + " INTEGER," +
                    DataBaseContract.HomeStats_.COLUMN_NAME_JSON + " TEXT)";
    private static final String SQL_CREATE_WALLET =
            "CREATE TABLE " + DataBaseContract.Wallet_.TABLE_NAME + " (" +
                    DataBaseContract.Wallet_._ID + " INTEGER PRIMARY KEY," +
                    DataBaseContract.Wallet_.COLUMN_NAME_DTM + " INTEGER," +
                    DataBaseContract.Wallet_.COLUMN_NAME_JSON + " TEXT)";
    private static final String SQL_CREATE_MINER =
            "CREATE TABLE " + DataBaseContract.Miner_.TABLE_NAME + " (" +
                    DataBaseContract.Miner_._ID + " INTEGER PRIMARY KEY," +
                    DataBaseContract.Miner_.COLUMN_NAME_ADDRESS + " TEXT UNIQUE NOT NULL," +
                    DataBaseContract.Miner_.COLUMN_NAME_LASTSEEN + " INTEGER NOT NULL," +
                    DataBaseContract.Miner_.COLUMN_NAME_FIRSTSEEN + " INTEGER," +
                    DataBaseContract.Miner_.COLUMN_NAME_TOPMINERS + " INTEGER," +
                    DataBaseContract.Miner_.COLUMN_NAME_BLOCKS_FOUND + " INTEGER," +
                    DataBaseContract.Miner_.COLUMN_NAME_TOPHR + " INTEGER," +
                    DataBaseContract.Miner_.COLUMN_NAME_AVGHR + " INTEGER," +
                    DataBaseContract.Miner_.COLUMN_NAME_CURHR + " INTEGER," +
                    DataBaseContract.Miner_.COLUMN_NAME_CUROFFLINE + " INTEGER," +
                    DataBaseContract.Miner_.COLUMN_NAME_PAID + " INTEGER)";

    private static final String SQL_CREATE_HOME_IDX =
            "CREATE INDEX " + DataBaseContract.HomeStats_.TABLE_NAME + "_dtm_idx ON "
                    + DataBaseContract.HomeStats_.TABLE_NAME + "(" + DataBaseContract.HomeStats_.COLUMN_NAME_DTM + ");";
    private static final String SQL_CREATE_MINERS_IDX =
            "CREATE INDEX " + DataBaseContract.Miner_.TABLE_NAME + "_addr_idx ON "
                    + DataBaseContract.Miner_.TABLE_NAME + "(" + DataBaseContract.Miner_.COLUMN_NAME_ADDRESS + ");";
    private static final String SQL_DELETE_STATS_TABLE =
            "DROP TABLE IF EXISTS " + DataBaseContract.HomeStats_.TABLE_NAME;
    private static final String SQL_DELETE_WALLET_TABLE =
            "DROP TABLE IF EXISTS " + DataBaseContract.Wallet_.TABLE_NAME;
    private static final String SQL_DELETE_MINERS_TABLE =
            "DROP TABLE IF EXISTS " + DataBaseContract.Miner_.TABLE_NAME;
    private static final String SQL_TRUNCATE_WALLET =
            "DELETE FROM " + DataBaseContract.Wallet_.TABLE_NAME;
    private static final String SQL_VACUUM = "VACUUM";
    private final GsonBuilder builder;


    public PoolDbHelper(Context context, PoolEnum pool, CurrencyEnum cur) {
        super(context, pool.name() + "_" + cur.name() + "_" + DATABASE_NAME, null, DATABASE_VERSION);
        Log.i(Constants.TAG, "Using DB: " + getDatabaseName());
        builder = new GsonBuilder();
    }

    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_HomeSTATS);
        db.execSQL(SQL_CREATE_WALLET);
        db.execSQL(SQL_CREATE_MINER);
        db.execSQL(SQL_CREATE_HOME_IDX);
        db.execSQL(SQL_CREATE_MINERS_IDX);
    }

    @Override
    protected void finalize() throws Throwable {
        this.close();
        super.finalize();
    }

    public void cleanOldData(SQLiteDatabase db) {

        Calendar oneMonthAgo = Calendar.getInstance();
        oneMonthAgo.add(Calendar.MONTH, -1);
        Log.w("DB", "SQL_ CLEANING older than: " + oneMonthAgo.getTime());
        db.delete(DataBaseContract.HomeStats_.TABLE_NAME,
                DataBaseContract.HomeStats_.COLUMN_NAME_DTM + " < " + oneMonthAgo.getTime().getTime(), null);
        db.delete(DataBaseContract.Wallet_.TABLE_NAME,
                DataBaseContract.Wallet_.COLUMN_NAME_DTM + " < " + oneMonthAgo.getTime().getTime(), null);
        db.delete(DataBaseContract.Miner_.TABLE_NAME,
                DataBaseContract.Miner_.COLUMN_NAME_LASTSEEN + " < " + oneMonthAgo.getTime().getTime(), null);
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
        db.execSQL(SQL_DELETE_MINERS_TABLE);
        onCreate(db);
    }

    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }

    // Adding new contact
    public void logHomeStats(HomeStats contact) {
        Gson gson = builder.create();
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(DataBaseContract.HomeStats_.COLUMN_NAME_DTM, contact.getNow().getTime().getTime()); // Contact Name
        values.put(DataBaseContract.HomeStats_.COLUMN_NAME_JSON, gson.toJson(contact)); // Serializza

        // Inserting Row
        db.insert(DataBaseContract.HomeStats_.TABLE_NAME, null, values);
        db.close(); // Closing database connection
    }

    public void logWalletStats(Wallet retrieved) {
        Gson gson = builder.create();
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DataBaseContract.Wallet_.COLUMN_NAME_DTM, new Date().getTime()); // Contact Name
        values.put(DataBaseContract.Wallet_.COLUMN_NAME_JSON, gson.toJson(retrieved)); // Serializza

        // Inserting Row
        db.insert(DataBaseContract.Wallet_.TABLE_NAME, null, values);
        db.close(); // Closing database connection
    }


    public int updateMiner(MinerDBRecord retrieved) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(DataBaseContract.Miner_.COLUMN_NAME_LASTSEEN, retrieved.getLastSeen().getTime());
        values.put(DataBaseContract.Miner_.COLUMN_NAME_FIRSTSEEN, retrieved.getFirstSeen().getTime());
        values.put(DataBaseContract.Miner_.COLUMN_NAME_CUROFFLINE, retrieved.getOffline());
        values.put(DataBaseContract.Miner_.COLUMN_NAME_TOPHR, retrieved.getTopHr()); // Contact Name
        values.put(DataBaseContract.Miner_.COLUMN_NAME_TOPMINERS, retrieved.getTopMiners());
        values.put(DataBaseContract.Miner_.COLUMN_NAME_AVGHR, retrieved.getAvgHr());
        values.put(DataBaseContract.Miner_.COLUMN_NAME_CURHR, retrieved.getHashRate());
        values.put(DataBaseContract.Miner_.COLUMN_NAME_BLOCKS_FOUND, retrieved.getBlocksFound());
        values.put(DataBaseContract.Miner_.COLUMN_NAME_PAID, retrieved.getPaid());
        return db.update(DataBaseContract.Miner_.TABLE_NAME, values,
                DataBaseContract.Miner_.COLUMN_NAME_ADDRESS + " = CAST('" + retrieved.getAddress() + "' AS TEXT)", null);
    }

    public void createOrUpdateMiner(Miner retrieved) {

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(DataBaseContract.Miner_.COLUMN_NAME_LASTSEEN, retrieved.getLastBeat().getTime());
        values.put(DataBaseContract.Miner_.COLUMN_NAME_CURHR, retrieved.getHashrate());
        values.put(DataBaseContract.Miner_.COLUMN_NAME_CUROFFLINE, retrieved.getOffline() ? 1 : 0);

        int upd = db.update(DataBaseContract.Miner_.TABLE_NAME, values,
                DataBaseContract.Miner_.COLUMN_NAME_ADDRESS + " = CAST('" + retrieved.getAddress() + "' AS TEXT)", null);
        if (upd == 0) {
            values.put(DataBaseContract.Miner_.COLUMN_NAME_ADDRESS, retrieved.getAddress()); // Contact Name
            values.put(DataBaseContract.Miner_.COLUMN_NAME_AVGHR, retrieved.getHashrate()); // Contact Name
            values.put(DataBaseContract.Miner_.COLUMN_NAME_TOPHR, retrieved.getHashrate()); // Contact Name
            values.put(DataBaseContract.Miner_.COLUMN_NAME_FIRSTSEEN, new Date().getTime()); // Serializza
            db.insert(DataBaseContract.Miner_.TABLE_NAME, null, values);
        }
        // Inserting Row
    }

    public LinkedMap<Date, HomeStats> getHistoryData(@NonNull BackToEnum cutoff) {
        int cnt = 0;
        LinkedMap<Date, HomeStats> ret = new LinkedMap<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String limitCause = "";
        Calendar now = Calendar.getInstance();
        switch (cutoff) {
            case ONE_DAY:
                now.add(Calendar.DATE, -1);
                limitCause = DataBaseContract.HomeStats_.COLUMN_NAME_DTM + "  > " + now.getTime().getTime();
                break;
            case ONE_WEEK:
                now.add(Calendar.DATE, -7);
                limitCause = DataBaseContract.HomeStats_.COLUMN_NAME_DTM + "  > " + now.getTime().getTime();
                break;
            case ONE_MONTH:
                now.add(Calendar.MONTH, -1);
                limitCause = DataBaseContract.HomeStats_.COLUMN_NAME_DTM + "  > " + now.getTime().getTime();
                break;
            default:
                Log.e("DB", "Unexpected switch ERROR");
                break;
        }
        // Cursor cursor = db.rawQuery(selectQuery, null);
        Cursor cursor = db.query(DataBaseContract.HomeStats_.TABLE_NAME, new String[]{
                        DataBaseContract.HomeStats_._ID,
                        DataBaseContract.HomeStats_.COLUMN_NAME_DTM,
                        DataBaseContract.HomeStats_.COLUMN_NAME_JSON},
                limitCause,
                null,// String[] selectionArgs
                null,
                null, // HAVING
                DataBaseContract.HomeStats_.COLUMN_NAME_DTM + " ASC");// ORDER BY

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            Gson gson = builder.create();
            do {
                try {
                    // Register an adapter to manage the date types as long values
                    HomeStats retrieved = gson.fromJson(cursor.getString(cursor.getColumnIndexOrThrow(DataBaseContract.HomeStats_.COLUMN_NAME_JSON)), HomeStats.class);
                    // Adding contact to list
                    ret.put(retrieved.getNow().getTime(), retrieved);
                    cnt++;
                } catch (Exception ce) {
                    Log.e(TAG, "Cant read HistoryData entry: " + ce.getMessage());
                }
            } while (cursor.moveToNext());
        }
        Log.i(TAG, "SELECT DONE. HOME HISTORY SIZE: " + cnt);
        cursor.close();
        return ret;
    }

    /**
     * This HEAVY method can serve stats/charts only, as it is not precise
     * The last block pending before payout is not being counted.
     *
     * @return average 'pending' increase per block
     */

    public Long getAveragePending() {
        int cnt = 0;
        int rec = 0;
        Long pendings = 0L;
        Long prevPending = 0L;

        SQLiteDatabase db = this.getReadableDatabase();
        // Cursor cursor = db.rawQuery(selectQuery, null);
        Cursor cursor = db.query(DataBaseContract.Wallet_.TABLE_NAME, new String[]{
                        DataBaseContract.Wallet_._ID,
                        DataBaseContract.Wallet_.COLUMN_NAME_DTM,
                        DataBaseContract.Wallet_.COLUMN_NAME_JSON},
                null,
                null,// String[] selectionArgs
                null,
                null, // HAVING
                DataBaseContract.Wallet_.COLUMN_NAME_DTM + " ASC");// ORDER BY

        if (cursor.moveToFirst()) {
            Gson gson = builder.create();
            try {

                Wallet camp = gson.fromJson(cursor.getString(cursor.getColumnIndexOrThrow(DataBaseContract.Wallet_.COLUMN_NAME_JSON)), Wallet.class);
                prevPending = camp.getStats().getBalance().longValue();
                do {
                    Wallet retrieved = gson.fromJson(cursor.getString(cursor.getColumnIndexOrThrow(DataBaseContract.Wallet_.COLUMN_NAME_JSON)), Wallet.class);
                    rec++;
                    Long curPending = retrieved.getStats().getBalance().longValue();
                    if (curPending > prevPending) {
                        Log.d(TAG, "Block detected in history. Prev balance: " + prevPending + " current: " + curPending);
                        cnt++;
                        //pending increased
                        pendings += (curPending - prevPending);
                    }
                    prevPending = curPending;
                } while (cursor.moveToNext());
            }catch (Exception caz){
                Log.e(TAG, "Cant read getAveragePending entry: " + caz.getMessage());
            }
        }
        Log.i(TAG, "SELECT DONE. PENDINGS HISTORY SIZE: " + cnt + " FROM RECORDS: " + rec);
        cursor.close();
        if (cnt == 0)
            return 0L;
        return pendings / cnt;
    }


    public LinkedMap<Date, Wallet> getWalletHistoryData(@NonNull BackToEnum cutoff) {
        LinkedMap<Date, Wallet> ret = new LinkedMap<>();
        int cnt = 0;
        SQLiteDatabase db = this.getReadableDatabase();
        String limitCause = "";
        Calendar now = Calendar.getInstance();
        switch (cutoff) {
            case ONE_DAY:
                now.add(Calendar.DATE, -1);
                limitCause = DataBaseContract.Wallet_.COLUMN_NAME_DTM + "  > " + now.getTime().getTime();
                break;
            case ONE_WEEK:
                now.add(Calendar.DATE, -7);
                limitCause = DataBaseContract.Wallet_.COLUMN_NAME_DTM + "  > " + now.getTime().getTime();
                break;
            case ONE_MONTH:
                now.add(Calendar.MONTH, -1);
                limitCause = DataBaseContract.Wallet_.COLUMN_NAME_DTM + "  > " + now.getTime().getTime();
                break;
            default:
                Log.e("DB", "Unexpected switch ERROR");
                break;
        }
        // Cursor cursor = db.rawQuery(selectQuery, null);
        Cursor cursor = db.query(DataBaseContract.Wallet_.TABLE_NAME, new String[]{
                        DataBaseContract.Wallet_._ID,
                        DataBaseContract.Wallet_.COLUMN_NAME_DTM,
                        DataBaseContract.Wallet_.COLUMN_NAME_JSON},
                limitCause,
                null,// String[] selectionArgs
                null,
                null, // HAVING
                DataBaseContract.Wallet_.COLUMN_NAME_DTM + " ASC");// ORDER BY

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            Gson gson = builder.create();
            do {
                try {
                    // Register an adapter to manage the date types as long values
                    Wallet retrieved = gson.fromJson(cursor.getString(cursor.getColumnIndexOrThrow(DataBaseContract.Wallet_.COLUMN_NAME_JSON)), Wallet.class);
                    cnt++;
                    // Adding contact to list
                    Date curDate = new Date(cursor.getLong(cursor.getColumnIndexOrThrow(DataBaseContract.Wallet_.COLUMN_NAME_DTM)));
                    ret.put(curDate, retrieved);
                } catch (Exception ce) {
                    Log.e(TAG, "Cant read WalletHistoryData entry: " + ce.getMessage());
                }
            } while (cursor.moveToNext());
        }
        Log.i(TAG, "SELECT DONE. WALLET HISTORY SIZE: " + cnt);
        cursor.close();
        return ret;
    }

    public LinkedMap<Date, HomeStats> getLastHomeStats(int limit) {
        LinkedMap<Date, HomeStats> ret = new LinkedMap<>();
        int cnt = 0;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(DataBaseContract.HomeStats_.TABLE_NAME, new String[]{
                        DataBaseContract.HomeStats_._ID,
                        DataBaseContract.HomeStats_.COLUMN_NAME_DTM,
                        DataBaseContract.HomeStats_.COLUMN_NAME_JSON},
                null,
                null,// String[] selectionArgs
                null,
                null, // HAVING
                DataBaseContract.HomeStats_.COLUMN_NAME_DTM + " DESC",
                "" + limit);//2 results to do compare

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            Gson gson = builder.create();
            do {
                try {
                    HomeStats retrieved = gson.fromJson(cursor.getString(cursor.getColumnIndexOrThrow(DataBaseContract.HomeStats_.COLUMN_NAME_JSON)), HomeStats.class);
                    cnt++;
                    Date curDate = new Date(cursor.getLong(cursor.getColumnIndexOrThrow(DataBaseContract.HomeStats_.COLUMN_NAME_DTM)));
                    ret.put(curDate, retrieved);
                } catch (Exception ce) {
                    Log.e(TAG, "Cant read HomeStats entry: " + ce.getMessage());
                }
            } while (cursor.moveToNext());
        }
        Log.i(TAG, "SELECT DONE. WALLET HISTORY SIZE: " + cnt);
        cursor.close();
        return ret;
    }

    public Wallet getLastWallet() {
        LinkedMap<Date, Wallet> ret = getLastWallets(1);
        return ret.get(ret.firstKey());
    }

    public LinkedMap<Date, Wallet> getLastWallets(int limit) {
        LinkedMap<Date, Wallet> ret = new LinkedMap<>();
        int cnt = 0;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(DataBaseContract.Wallet_.TABLE_NAME, new String[]{
                        DataBaseContract.Wallet_._ID,
                        DataBaseContract.Wallet_.COLUMN_NAME_DTM,
                        DataBaseContract.Wallet_.COLUMN_NAME_JSON},
                null,
                null,// String[] selectionArgs
                null,
                null, // HAVING
                DataBaseContract.Wallet_.COLUMN_NAME_DTM + " DESC",
                "" + limit);//2 results to do compare

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            Gson gson = builder.create();
            do {
                try {
                    Wallet retrieved = gson.fromJson(cursor.getString(cursor.getColumnIndexOrThrow(DataBaseContract.Wallet_.COLUMN_NAME_JSON)), Wallet.class);
                    cnt++;
                    // Adding contact to list
                    Date curDate = new Date(cursor.getLong(cursor.getColumnIndexOrThrow(DataBaseContract.Wallet_.COLUMN_NAME_DTM)));
                    ret.put(curDate, retrieved);
                } catch (Exception ce) {
                    Log.e(TAG, "Cant read wallet entry: " + ce.getMessage());
                }
            } while (cursor.moveToNext());
        }
        Log.i(TAG, "SELECT DONE. WALLET HISTORY SIZE: " + cnt);
        cursor.close();
        return ret;
    }


    public ArrayList<MinerDBRecord> getMinerList(MinerSortEnum sortorder) {
        ArrayList<MinerDBRecord> retL = new ArrayList<>();
        int cnt = 0;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(DataBaseContract.Miner_.TABLE_NAME, new String[]{
                        DataBaseContract.Miner_.COLUMN_NAME_ADDRESS,
                        DataBaseContract.Miner_.COLUMN_NAME_PAID,
                        DataBaseContract.Miner_.COLUMN_NAME_TOPHR,
                        DataBaseContract.Miner_.COLUMN_NAME_TOPMINERS,
                        DataBaseContract.Miner_.COLUMN_NAME_BLOCKS_FOUND,
                        DataBaseContract.Miner_.COLUMN_NAME_AVGHR,
                        DataBaseContract.Miner_.COLUMN_NAME_CUROFFLINE,
                        DataBaseContract.Miner_.COLUMN_NAME_CURHR,
                        DataBaseContract.Miner_.COLUMN_NAME_FIRSTSEEN,
                        DataBaseContract.Miner_.COLUMN_NAME_LASTSEEN},
                null,
                null,// String[] selectionArgs
                null,
                null, // HAVING
                sortorder.getDbColumn() + " DESC", null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                MinerDBRecord ret = new MinerDBRecord();
                try {
                    ret.setAddress(cursor.getString(cursor.getColumnIndexOrThrow(DataBaseContract.Miner_.COLUMN_NAME_ADDRESS)));

                    ret.setLastSeen(new Date(cursor.getLong(cursor.getColumnIndexOrThrow(DataBaseContract.Miner_.COLUMN_NAME_LASTSEEN))));
                    ret.setFirstSeen(new Date(cursor.getLong(cursor.getColumnIndexOrThrow(DataBaseContract.Miner_.COLUMN_NAME_FIRSTSEEN))));
                    ret.setAvgHr(cursor.getLong(cursor.getColumnIndexOrThrow(DataBaseContract.Miner_.COLUMN_NAME_AVGHR)));
                    ret.setTopHr(cursor.getLong(cursor.getColumnIndexOrThrow(DataBaseContract.Miner_.COLUMN_NAME_TOPHR)));
                    ret.setHashRate(cursor.getLong(cursor.getColumnIndexOrThrow(DataBaseContract.Miner_.COLUMN_NAME_CURHR)));
                    ret.setOffline(cursor.getInt(cursor.getColumnIndexOrThrow(DataBaseContract.Miner_.COLUMN_NAME_CUROFFLINE)) == 1);
                    if (!cursor.isNull(cursor.getColumnIndexOrThrow(DataBaseContract.Miner_.COLUMN_NAME_TOPMINERS)))
                        ret.setTopMiners(cursor.getInt(cursor.getColumnIndexOrThrow(DataBaseContract.Miner_.COLUMN_NAME_TOPMINERS)));
                    if (!cursor.isNull(cursor.getColumnIndexOrThrow(DataBaseContract.Miner_.COLUMN_NAME_PAID)))
                        ret.setPaid(cursor.getLong(cursor.getColumnIndexOrThrow(DataBaseContract.Miner_.COLUMN_NAME_PAID)));
                    if (!cursor.isNull(cursor.getColumnIndexOrThrow(DataBaseContract.Miner_.COLUMN_NAME_BLOCKS_FOUND)))
                        ret.setBlocksFound(cursor.getInt(cursor.getColumnIndexOrThrow(DataBaseContract.Miner_.COLUMN_NAME_BLOCKS_FOUND)));
                    retL.add(ret);
                    cnt++;
                } catch (Exception ce) {
                    Log.e(TAG, "Cant read wallet entry: " + ce.getMessage());
                }
            } while (cursor.moveToNext());
        }
        Log.i(TAG, "SELECT DONE. MINERS SIZE: " + cnt);
        cursor.close();
        return retL;
    }


}