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
    ELLA("Ellaism"),
	EMC2("Einstenium"), //argh!
	ETH("Ethereum", "https://etherscan.io"),
    ETC("Ethereum Classic", "https://gastracker.io"),
	EXP("Expanse"),
	KMD("Komodo"),
	LTC("Litecoin"),
    MUSIC("Musicoin"),
    MC("Musicoin"),//MAXPOOL names its way
    MONA("MonaCoin"), //lol
    XMR("Monero"),
    PIRL("Pirl"), //ahah
    THC("HempCoin"),
    UBQ("Ubiq", "https://ubiqscan.io"),
    UBIQ("Ubiq", "https://ubiqscan.io"),//MAXPOOL names its way
    VIC("Victorium"),
    XVG("Verge"),
    ZEN("Zencash"),
    ZEC("ZCash");

    private String friendlyName;

    public String getScannerSite() {
        return scannerSite;
    }

    private String scannerSite;

    private CurrencyEnum(String friendlyName){
        this.friendlyName = friendlyName;
    }
    private CurrencyEnum(String friendlyName,String scanner){
        this.friendlyName = friendlyName;
        this.scannerSite = scanner;
    }

    @Override public String toString(){
        return friendlyName;
    }
}
