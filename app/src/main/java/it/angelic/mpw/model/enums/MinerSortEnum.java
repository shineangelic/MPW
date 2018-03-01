package it.angelic.mpw.model.enums;

import it.angelic.mpw.model.db.DataBaseContract;

public enum MinerSortEnum {
    LAST_SEEN(DataBaseContract.Miner_.COLUMN_NAME_LASTSEEN), HASHRATE(DataBaseContract.Miner_.COLUMN_NAME_CURHR);

    MinerSortEnum(String dbColumn) {
        this.dbColumn = dbColumn;
    }

    private String dbColumn;
    public String getDbColumn() {
        return dbColumn;
    }
}