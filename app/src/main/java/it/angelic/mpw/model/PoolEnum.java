package it.angelic.mpw.model;

import java.util.ArrayList;

import it.angelic.mpw.Constants;

/**
 * Created by shine@angelic.it on 31/01/2018.
 */

public enum PoolEnum {
    NOOBPOOL(Constants.NOOB_POOL_NAME, "noobpool.com",false, new ArrayList<CurrencyEnum>() {{
        add(CurrencyEnum.ETH);
        add(CurrencyEnum.ETC);
    }}),

    CRYPTOPOOL("CryptoPool Network", "cryptopool.network",false, new ArrayList<CurrencyEnum>() {{
        add(CurrencyEnum.ETH);
        add(CurrencyEnum.ETC);
        add(CurrencyEnum.MUSIC);
        add(CurrencyEnum.PIRL);
        add(CurrencyEnum.UBQ);
    }}),

    HASHINGPARTY("Hashing Party", "hashing.party",false, new ArrayList<CurrencyEnum>() {{
        add(CurrencyEnum.ETH);
        add(CurrencyEnum.ETC);
        add(CurrencyEnum.UBQ);
        add(CurrencyEnum.DBIX);
        add(CurrencyEnum.VIC);
    }});
    private String friendlyName;
    private String webRoot;

    public Boolean getHttpsOnly() {
        return httpsOnly;
    }

    public String getTransportProtocolBase() {
        return httpsOnly?"https://":"http://";
    }

    private Boolean httpsOnly;
    private ArrayList<CurrencyEnum> supportedCurrencies;
    private PoolEnum(String friendlyName, String wr,Boolean https, ArrayList sfp) {
        this.friendlyName = friendlyName;
        this.webRoot = wr;
        supportedCurrencies = sfp;
        httpsOnly = https;
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
