package it.angelic.mpw.model;

import java.util.ArrayList;

import it.angelic.mpw.Constants;

/**
 * Created by shine@angelic.it on 31/01/2018.
 */

public enum PoolEnum {
    NOOBPOOL(Constants.NOOB_POOL_NAME, "noobpool.com",new ArrayList<CurrencyEnum>() {{
        add(CurrencyEnum.ETH);
    }}),

    CRYPTOPOOL("Crypto Pool", "cryptopool.network",new ArrayList<CurrencyEnum>() {{
        add(CurrencyEnum.ETH);
        add(CurrencyEnum.PIRL);
        add(CurrencyEnum.UBQ);
    }}),

    VICPOOL("Hashing Party", "hashing.party",new ArrayList<CurrencyEnum>() {{
        add(CurrencyEnum.ETH);
        add(CurrencyEnum.VIC);
    }});
    private String friendlyName;
    private String webRoot;
    private ArrayList<CurrencyEnum> supportedCurrencies;

    private PoolEnum(String friendlyName, String wr, ArrayList sfp) {
        this.friendlyName = friendlyName;
        this.webRoot = wr;
        supportedCurrencies = sfp;
    }

    public ArrayList<CurrencyEnum> getSupportedCurrencies(){
        return supportedCurrencies;
    }

    @Override
    public String toString() {
        return friendlyName;
    }
}
