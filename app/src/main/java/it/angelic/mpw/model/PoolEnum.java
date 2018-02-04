package it.angelic.mpw.model;

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

    CRYPTOPOOL("CryptoPool Network", "cryptopool.network", false, new ArrayList<CurrencyEnum>() {{
        add(CurrencyEnum.ETH);
        add(CurrencyEnum.ETC);
        add(CurrencyEnum.MC);
        add(CurrencyEnum.PIRL);
        add(CurrencyEnum.UBQ);
    }}, false),

    HASHINGPARTY("Hashing Party", "hashing.party", false, new ArrayList<CurrencyEnum>() {{
        add(CurrencyEnum.ETH);
        add(CurrencyEnum.ETC);
        add(CurrencyEnum.UBQ);
        add(CurrencyEnum.DBIX);
        add(CurrencyEnum.VIC);
    }}, false),

    ETHERDIG("etherdig", "etherdig.net", true, new ArrayList<CurrencyEnum>() {{
        add(CurrencyEnum.ETH);
    }}, true),
    ISTPOOL("1spool", "1stpool.com", false, new ArrayList<CurrencyEnum>() {{
        add(CurrencyEnum.ETH);
    }}, false);
    private String friendlyName;
    private String webRoot;
    private Boolean httpsOnly;

    public Boolean getOmitCurrency() {
        return omitCurrency;
    }

    private Boolean omitCurrency;
    private ArrayList<CurrencyEnum> supportedCurrencies;

    private PoolEnum(String friendlyName, String wr, Boolean https, ArrayList sfp,Boolean omit) {
        this.friendlyName = friendlyName;
        this.webRoot = wr;
        supportedCurrencies = sfp;
        httpsOnly = https;
        omitCurrency = omit;
    }

    public Boolean getHttpsOnly() {
        return httpsOnly;
    }

    public String getTransportProtocolBase() {
        return httpsOnly ? "https://" : "http://";
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
