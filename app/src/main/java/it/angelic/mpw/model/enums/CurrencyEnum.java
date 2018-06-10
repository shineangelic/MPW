package it.angelic.mpw.model.enums;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import it.angelic.mpw.model.blockchainexplorers.BlockChainExplorer;
import it.angelic.mpw.model.blockchainexplorers.CallistoExplorer;
import it.angelic.mpw.model.blockchainexplorers.ETCExplorer;
import it.angelic.mpw.model.blockchainexplorers.EllaismExplorer;
import it.angelic.mpw.model.blockchainexplorers.EtherscanExplorer;
import it.angelic.mpw.model.blockchainexplorers.PirlExplorer;
import it.angelic.mpw.model.blockchainexplorers.UbiqExplorer;
import it.angelic.mpw.model.blockchainexplorers.WhaleExplorer;

/**
 * Enums names matter, as webroot is decided upon that
 * Created by shine@angelic.it on 31/01/2018.
 */
public enum CurrencyEnum {
    BTC("Bitcoin"),
    BCN("Bytecoin"),
    BTX("Bitcore"),
    CLO("Callisto",new CallistoExplorer()),
    DASH("Dash"),
    DCR("Decred"),
	DBIX("Dubai Coin"),
    ELLA("Ellaism", new EllaismExplorer()),
	EMC2("Einstenium"), //argh!
	ETH("Ethereum", new EtherscanExplorer()),
    ETC("Ethereum Classic", new ETCExplorer()),
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
    PIRL("Pirl",new PirlExplorer()), //ahah
    SUMO("Sumo"),
    THC("HempCoin"),
    UBQ("Ubiq", new UbiqExplorer()),
    UBIQ("Ubiq", new UbiqExplorer()),//MAXPOOL names its way
    VIC("Victorium"),
    XVG("Verge"),
    WHALE("Whalecoin", new WhaleExplorer()),
    AKROMA("Akroma"),
    ZEN("Zencash"),
    ZEC("ZCash");

    @NonNull
    private final String friendlyName;

    @Nullable
    public BlockChainExplorer getScannerSite() {
        return scannerSite;
    }

    @Nullable
    private BlockChainExplorer scannerSite;

    CurrencyEnum(@NonNull String friendlyName){
        this.friendlyName = friendlyName;
    }
    CurrencyEnum(@NonNull String friendlyName, @NonNull BlockChainExplorer scanner){
        this.friendlyName = friendlyName;
        this.scannerSite = scanner;
    }

    @Override public String toString(){
        return friendlyName;
    }
}
