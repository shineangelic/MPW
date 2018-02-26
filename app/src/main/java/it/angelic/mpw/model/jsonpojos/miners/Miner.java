package it.angelic.mpw.model.jsonpojos.miners;

import java.util.Date;

public class Miner {

    private Date lastBeat;
    private Long hr;
    private Boolean offline;
    private String address;

    public Date getLastBeat() {
        return lastBeat;
    }

    public void setLastBeat(Date lastBeat) {
        this.lastBeat = lastBeat;
    }

    public Long getHashrate() {
        return hr;
    }

    public void setHr(Long hr) {
        this.hr = hr;
    }

    public Boolean getOffline() {
        return offline;
    }

    public void setOffline(Boolean offline) {
        this.offline = offline;
    }


    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}