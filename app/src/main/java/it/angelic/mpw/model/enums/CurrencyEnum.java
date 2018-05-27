package it.angelic.mpw.model.enums;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * Enums names matter, as webroot is decided upon that
 * Created by shine@angelic.it on 31/01/2018.
 */
public enum CurrencyEnum {
    BTC("Bitcoin"),
    BCN("Bytecoin"),
    BTX("Bitcore"),
    DASH("Dash"),
    DCR("Decred"),
	DBIX("Dubai Coin"),
    ELLA("Ellaism"),
	EMC2("Einstenium"), //argh!
	ETH("Ethereum", "https://etherscan.io"),
    ETC("Ethereum Classic", "https://gastracker.io"),
    ETP("Metaverse"),
    ETZ("EtherZero"),
	EXP("Expanse"),
	KMD("Komodo"),
    KRB("Karbo"),
    MUSIC("Musicoin"),
    MC("Musicoin"),//MAXPOOL names its way
    MONA("MonaCoin"), //lol
    MSR("Masari"),
    XMR("Monero"),
    PIRL("Pirl","https://poseidon.pirl.io/explorer"), //ahah
    SUMO("Sumo"),
    THC("HempCoin"),
    UBQ("Ubiq", "https://ubiqscan.io"),
    UBIQ("Ubiq", "https://ubiqscan.io"),//MAXPOOL names its way
    VIC("Victorium"),
    XVG("Verge"),
    WHALE("Whalecoin", "https://explorer.whalecoin.org/"),
    AKROMA("Akroma"),
    ZEN("Zencash"),
    ZEC("ZCash");

    @NonNull
    private final String friendlyName;

    @Nullable
    public String getScannerSite() {
        return scannerSite;
    }

    @Nullable
    private String scannerSite;

    CurrencyEnum(@NonNull String friendlyName){
        this.friendlyName = friendlyName;
    }
    CurrencyEnum(@NonNull String friendlyName, @NonNull String scanner){
        this.friendlyName = friendlyName;
        this.scannerSite = scanner;
    }

    @Override public String toString(){
        return friendlyName;
    }
}
