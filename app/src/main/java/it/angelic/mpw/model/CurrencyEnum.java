package it.angelic.mpw.model;

/**
 * Created by shine@angelic.it on 31/01/2018.
 */

public enum CurrencyEnum {
    //BTC("Bitcoin"),
    BTG("Bitcoin Gold"),
    BCN("Bytecoin"),
    BTX("Bitcore"),
    DASH("Dash"),
	DBIX("Dubai Coin"),
	DOGE("Dogecoin"),
	EMC2("Einstenium"), //argh!
	ETH("Ethereum"),
    ETC("Ethereum Classic"),
	EXP("Expanse"),
	KMD("Komodo"),
	LTC("Litecoin"),
    MC("Musicoin"),
    MONA("MonaCoin"), //lol
    XMR("Monero"),
    PIRL("Expanse"), //ahah
    THC("HempCoin"),
    UBQ("Ubiq"),
    VIC("Victorium"),
    XVG("Verge"),
    ZEN("Zencash"),
    ZEC("ZCash");

    private String friendlyName;

    private CurrencyEnum(String friendlyName){
        this.friendlyName = friendlyName;
    }

    @Override public String toString(){
        return friendlyName;
    }
}
