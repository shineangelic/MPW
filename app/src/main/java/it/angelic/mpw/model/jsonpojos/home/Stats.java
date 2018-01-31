package it.angelic.mpw.model.jsonpojos.home;

import java.io.Serializable;
import java.util.Date;

public class Stats implements Serializable {

    private final static long serialVersionUID = -8772158729012648157L;
    private Date lastBlockFound;
    private Long roundShares;

    /**
     * No args constructor for use in serialization
     */
    public Stats() {
    }

    public Date getLastBlockFound() {
        return lastBlockFound;
    }

    public void setLastBlockFound(Date lastBlockFound) {
        this.lastBlockFound = lastBlockFound;
    }

    public Long getRoundShares() {
        return roundShares;
    }

    public void setRoundShares(Long roundShares) {
        this.roundShares = roundShares;
    }


}