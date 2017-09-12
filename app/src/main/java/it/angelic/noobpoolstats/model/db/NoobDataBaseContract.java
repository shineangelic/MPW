package it.angelic.noobpoolstats.model.db;

import android.provider.BaseColumns;

/**
 * Created by shine@angelic.it on 06/09/2017.
 */

class NoobDataBaseContract {

    // To prevent someone from accidentally instantiating the contract class,
    // make the constructor private.
    private NoobDataBaseContract() {}

    /* Inner class that defines the table contents */
    public static class HomeStats_ implements BaseColumns {
        public static final String TABLE_NAME = "homestats";
        public static final String COLUMN_NAME_DTM = "date";
        public static final String COLUMN_NAME_JSON = "json";
    }

    public class Wallet_ implements BaseColumns {
        public static final String TABLE_NAME = "wallet";
        public static final String COLUMN_NAME_DTM = "date";
        public static final String COLUMN_NAME_JSON = "json";
    }
}
