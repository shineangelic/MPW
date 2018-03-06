package it.angelic.mpw.model.enums;

import java.util.ArrayList;

import it.angelic.mpw.Constants;

/**
 * Created by shine@angelic.it on 31/01/2018.
 */

public enum PoolEnum {
    NOOBPOOL(Constants.NOOB_POOL_NAME, "noobpool.com", false, new ArrayList<CurrencyEnum>() {{
        add(CurrencyEnum.ETH);
        add(CurrencyEnum.ETC);
    }}, false),
    CRYPTOPOOL("CryptoPool Network", "cryptopool.network", true, new ArrayList<CurrencyEnum>() {{
        add(CurrencyEnum.ELLA);
        add(CurrencyEnum.ETH);
        add(CurrencyEnum.ETC);
        add(CurrencyEnum.MUSIC);
        add(CurrencyEnum.PIRL);
        add(CurrencyEnum.UBQ);
    }}, false),
    /*ELLAISMDEV("Ella Dev Pool", "ellaism.org", false, new ArrayList<CurrencyEnum>() {{
        add(CurrencyEnum.ELLA);
    }}, true, "pool"),*/
    ETHTEAM("EthTeam", "ethteam.com",false, new ArrayList<CurrencyEnum>() {{
        add(CurrencyEnum.ETC);
    }}, true),
    HASHINGPARTY("Hashing Party", "hashing.party", false, new ArrayList<CurrencyEnum>() {{
        add(CurrencyEnum.ETH);
        add(CurrencyEnum.ETC);
        add(CurrencyEnum.UBQ);
        add(CurrencyEnum.DBIX);
        add(CurrencyEnum.VIC);
    }}, false),
    MINERPOOL("Minerpool", "minerpool.net", false, new ArrayList<CurrencyEnum>() {{
        add(CurrencyEnum.ELLA);
        add(CurrencyEnum.EXP);
        add(CurrencyEnum.MUSIC);
        add(CurrencyEnum.PIRL);
        add(CurrencyEnum.UBIQ);
        add(CurrencyEnum.VIC);
    }}, false),
    /*ETHERDIG("etherdig", "etherdig.net", true, new ArrayList<CurrencyEnum>() {{
        add(CurrencyEnum.ETH);
    }}, true),
    ISTPOOL("1stpool", "1stpool.com", false, new ArrayList<CurrencyEnum>() {{
        add(CurrencyEnum.ETH);
    }}, false),*/
    MAXHASH("MaxHash", "maxhash.org", true, new ArrayList<CurrencyEnum>() {{
        add(CurrencyEnum.ETH);
        add(CurrencyEnum.EXP);
        add(CurrencyEnum.MC);
        add(CurrencyEnum.UBIQ);
    }}, false, "pool"),
    NEVERMINING( "Nevermining",   "nevermining.org", false, new ArrayList<CurrencyEnum>() {{
        add(CurrencyEnum.ELLA);
        add(CurrencyEnum.EXP);
        add(CurrencyEnum.MUSIC);
        add(CurrencyEnum.PIRL);
        add(CurrencyEnum.UBIQ);
    }}, false);
   /* MINERPOOLFR("Mining Pool", "mining-pool.fr",true,new ArrayList<CurrencyEnum>() {{
        add(CurrencyEnum.ETC);
        add(CurrencyEnum.ELLA);
        add(CurrencyEnum.EXP);
        //add(CurrencyEnum.MONERO);
        add(CurrencyEnum.MUSIC);
        add(CurrencyEnum.PIRL);
        add(CurrencyEnum.UBIQ);
    }}, false );*/
    //human friendly name
    private final String friendlyName;
    //dominio di primo e secondo liv.
    private final String webRoot;
    //qualche pool funziona solo in HTTPS
    private final Boolean httpsOnly;
    //qualche genio omette la moneta se ne ha una sola nel pool
    private final Boolean omitCurrency;
    //qualche genio cambia la radice del sito
    private final String radixSuffix;
    private final ArrayList<CurrencyEnum> supportedCurrencies;
    PoolEnum(String friendlyName, String wr, Boolean https, ArrayList<CurrencyEnum> sfp, Boolean omit) {
        this.friendlyName = friendlyName;
        this.webRoot = wr;
        supportedCurrencies = sfp;
        httpsOnly = https;
        omitCurrency = omit;
        radixSuffix = "";
    }
    PoolEnum(String friendlyName, String wr, Boolean https, ArrayList<CurrencyEnum> sfp, Boolean omit,String rad) {
        this.friendlyName = friendlyName;
        this.webRoot = wr;
        supportedCurrencies = sfp;
        httpsOnly = https;
        omitCurrency = omit;
        radixSuffix = rad;
    }

    public Boolean getOmitCurrency() {
        return omitCurrency;
    }

    public Boolean getHttpsOnly() {
        return httpsOnly;
    }

    public String getTransportProtocolBase() {
        return httpsOnly ? "https://" : "http://";
    }

    public String getRadixSuffix() {
        return radixSuffix;
    }

    public String getWebRoot() {
        return webRoot;
    }

    public ArrayList<CurrencyEnum> getSupportedCurrencies() {
        return supportedCurrencies;
    }

    @Override
    public String toString() {
        return friendlyName;
    }
}
