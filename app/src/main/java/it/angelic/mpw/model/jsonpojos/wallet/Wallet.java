package it.angelic.mpw.model.jsonpojos.wallet;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Wallet implements Serializable {

    private final static long serialVersionUID = 7538259390724172001L;
    private Long currentHashrate= 0L;
    private Long hashrate = 0L;
    private Integer pageSize;

    private Integer paymentsTotal;
    private Long roundShares;
    private Stats stats = new Stats();

    private Integer workersOffline = 0;
    private Integer workersOnline = 0;
    private Integer workersTotal = 0;

    private List<Payment> payments = new ArrayList<>();
    private HashMap<String, Worker> workers = new HashMap<>();


    public Long getCurrentHashrate() {
        return currentHashrate;
    }

    public void setCurrentHashrate(Long currentHashrate) {
        this.currentHashrate = currentHashrate;
    }

    public Long getHashrate() {
        return hashrate;
    }

    public void setHashrate(Long hashrate) {
        this.hashrate = hashrate;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public List<Payment> getPayments() {
        return payments;
    }

    public void setPayments(List<Payment> payments) {
        this.payments = payments;
    }

    public Integer getPaymentsTotal() {
        return paymentsTotal;
    }

    public void setPaymentsTotal(Integer paymentsTotal) {
        this.paymentsTotal = paymentsTotal;
    }

    public Long getRoundShares() {
        return roundShares;
    }

    public void setRoundShares(Long roundShares) {
        this.roundShares = roundShares;
    }

    public Stats getStats() {
        return stats;
    }

    public void setStats(Stats stats) {
        this.stats = stats;
    }


    public Integer getWorkersOffline() {
        return workersOffline;
    }

    public void setWorkersOffline(Integer workersOffline) {
        this.workersOffline = workersOffline;
    }

    public Integer getWorkersOnline() {
        return workersOnline;
    }

    public void setWorkersOnline(Integer workersOnline) {
        this.workersOnline = workersOnline;
    }

    public Integer getWorkersTotal() {
        return workersTotal;
    }

    public void setWorkersTotal(Integer workersTotal) {
        this.workersTotal = workersTotal;
    }

    public HashMap<String, Worker> getWorkers() {
        return workers;
    }

    public void setWorkers(HashMap<String, Worker> workers) {
        this.workers = workers;
    }
}