package it.angelic.mpw.model.db;

import android.provider.BaseColumns;

/**
 *
 * Created by shine@angelic.it on 06/09/2017.
 */

public class DataBaseContract {

    // To prevent someone from accidentally instantiating the contract class,
    // make the constructor private.
    private DataBaseContract() {}

    /* Inner class that defines the table contents */
    public static class HomeStats_ implements BaseColumns {
        public static final String TABLE_NAME = "homestats";
        public static final String COLUMN_NAME_DTM = "date";
        public static final String COLUMN_NAME_JSON = "json";
    }

    /**
     * Storicizza JSON
     */
    public class Wallet_ implements BaseColumns {
        public static final String TABLE_NAME = "wallet";
        public static final String COLUMN_NAME_DTM = "date";
        public static final String COLUMN_NAME_JSON = "json";
    }

    /**
     * Record attuali per ogni minatore
     */
    public class Miner_ implements BaseColumns {
        public static final String TABLE_NAME = "miner";
        public static final String COLUMN_NAME_LASTSEEN = "date_lastseen";
        public static final String COLUMN_NAME_FIRSTSEEN = "date_firstseen";
        public static final String COLUMN_NAME_TOPMINERS = "int_topminers";
        public static final String COLUMN_NAME_TOPHR = "int_tophr";
        public static final String COLUMN_NAME_PAID = "long_paid";
        public static final String COLUMN_NAME_AVGHR = "long_avghr";
        public static final String COLUMN_NAME_ADDRESS = "text_address";
        public static final String COLUMN_NAME_CURHR = "long_curhr";
        public static final String COLUMN_NAME_CUROFFLINE = "long_curoffline";
        public static final String COLUMN_NAME_BLOCKS_FOUND = "int_blocksfound";
    }
}
