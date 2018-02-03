package it.angelic.mpw.model.db;

import java.util.Date;

/**
 * Catalogo minatori del pool
 *
 * Created by shine@angelic.it on 03/02/2018.
 */

public class MinerDBRecord {
    private Date lastSeen;
    private Date firstSeen;
    private Long hashRate;
    private Boolean offline;
    private String address;
    private Long paid;
    private Integer topMiners;
    private Long topHr;
    private Long avgHr;
    private Integer blocksFound;

    public Date getLastSeen() {
        return lastSeen;
    }

    public void setLastSeen(Date lastSeen) {
        this.lastSeen = lastSeen;
    }

    public Date getFirstSeen() {
        return firstSeen;
    }

    public void setFirstSeen(Date firstSeen) {
        this.firstSeen = firstSeen;
    }

    public Long getHashRate() {
        return hashRate;
    }

    public void setHashRate(Long hashRate) {
        this.hashRate = hashRate;
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

    public Long getPaid() {
        return paid;
    }

    public void setPaid(Long paid) {
        this.paid = paid;
    }

    public Integer getTopMiners() {
        return topMiners;
    }

    public void setTopMiners(Integer topMiners) {
        this.topMiners = topMiners;
    }

    public Long getTopHr() {
        return topHr;
    }

    public void setTopHr(Long topHr) {
        this.topHr = topHr;
    }

    public Long getAvgHr() {
        return avgHr;
    }

    public void setAvgHr(Long avgHr) {
        this.avgHr = avgHr;
    }

    public void setBlocksFound(Integer blocksFound) {
        this.blocksFound = blocksFound;
    }

    public Integer getBlocksFound() {
        return blocksFound;
    }
}
