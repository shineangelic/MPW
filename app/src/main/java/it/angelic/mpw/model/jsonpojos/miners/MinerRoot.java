package it.angelic.mpw.model.jsonpojos.miners;

import java.util.Calendar;
import java.util.HashMap;

public class MinerRoot {

    private Long hashrate;
    private HashMap<String, Miner> miners;
    private Integer minersTotal;
    private Calendar now;

    public Long getHashrate() {
        return hashrate;
    }

    public void setHashrate(Long hashrate) {
        this.hashrate = hashrate;
    }

    public HashMap<String, Miner> getMiners() {
        return miners;
    }

    public void setMiners(HashMap<String, Miner> miners) {
        this.miners = miners;
    }

    public Integer getMinersTotal() {
        return minersTotal;
    }

    public void setMinersTotal(Integer minersTotal) {
        this.minersTotal = minersTotal;
    }

    public Calendar getNow() {
        return now;
    }

    public void setNow(Calendar now) {
        this.now = now;
    }

}