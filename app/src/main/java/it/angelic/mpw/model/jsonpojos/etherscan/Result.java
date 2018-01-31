package it.angelic.mpw.model.jsonpojos.etherscan;

import java.util.Date;

public class Result {

    private String ethbtc;
    private Date ethbtc_timestamp;
    private String ethusd;
    private Date ethusd_timestamp;


    public String getEthbtc() {
        return ethbtc;
    }

    public void setEthbtc(String ethbtc) {
        this.ethbtc = ethbtc;
    }

    public Date getEthbtc_timestamp() {
        return ethbtc_timestamp;
    }

    public void setEthbtc_timestamp(Date ethbtc_timestamp) {
        this.ethbtc_timestamp = ethbtc_timestamp;
    }

    public String getEthusd() {
        return ethusd;
    }

    public void setEthusd(String ethusd) {
        this.ethusd = ethusd;
    }

    public Date getEthusd_timestamp() {
        return ethusd_timestamp;
    }

    public void setEthusd_timestamp(Date ethusdTimestamp) {
        this.ethusd_timestamp = ethusdTimestamp;
    }



}