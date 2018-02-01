package it.angelic.mpw.model;

/**
 * Created by shine@angelic.it on 31/01/2018.
 */

public enum CurrencyEnum {
    ETH("Ethereum"),
    ETC("Ethereum Classic"),
    UBQ("Ubiq"),
    PIRL("Expanse"), //ahah
    DBIX("Dubai Coin"),
    MUSIC("Musicoin"),
    VIC("Victorium");

    private String friendlyName;

    private CurrencyEnum(String friendlyName){
        this.friendlyName = friendlyName;
    }

    @Override public String toString(){
        return friendlyName;
    }
}
