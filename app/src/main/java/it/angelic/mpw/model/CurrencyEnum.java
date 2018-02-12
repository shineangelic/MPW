package it.angelic.mpw.model;

/**
 * Enums names matter, as webroot is decided upon that
 * Created by shine@angelic.it on 31/01/2018.
 */
public enum CurrencyEnum {
    //BTC("Bitcoin"),
    BTG("Bitcoin Gold"),
    BCN("Bytecoin"),
    BTX("Bitcore"),
    DASH("Dash"),
    DCR("Decred"),
	DBIX("Dubai Coin"),
	DOGE("Dogecoin"),
	EMC2("Einstenium"), //argh!
	ETH("Ethereum"),
    ETC("Ethereum Classic"),
	EXP("Expanse"),
	KMD("Komodo"),
	LTC("Litecoin"),
    MUSIC("Musicoin"),
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
