package it.angelic.mpw.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import it.angelic.mpw.model.jsonpojos.home.Node;
import it.angelic.mpw.model.jsonpojos.home.Stats;

public class HomeStatsChartData implements Serializable {

    private final static long serialVersionUID = -991675406872456775L;
    private Long hashrate;
    private Long hashrateMax;
    private Long hashrateMin;
    private Integer immatureTotal;
    private Integer maturedTotal;
    private List<Node> nodes ;
    private Calendar now;//serve a distinguere timestamp
    private Stats stats;

    public Long getHashrateMax() {
        return hashrateMax;
    }

    public void setHashrateMax(Long hashrateMax) {
        this.hashrateMax = hashrateMax;
    }

    public Long getHashrateMin() {
        return hashrateMin;
    }

    public void setHashrateMin(Long hashrateMin) {
        this.hashrateMin = hashrateMin;
    }

    /**
     * No args constructor for use in serialization
     */
    public HomeStatsChartData() {

        hashrate = 0L;
        hashrateMax = 0L;
        hashrateMin = Long.MAX_VALUE;
        immatureTotal = 0;
        maturedTotal = 0;
        nodes = new ArrayList<>();
        now = Calendar.getInstance();
        stats = new Stats();
        nodes.add(new Node());
    }




    public Long getHashrate() {
        return hashrate;
    }

    public void setHashrate(Long hashrate) {
        this.hashrate = hashrate;
    }

    public Integer getImmatureTotal() {
        return immatureTotal;
    }

    public void setImmatureTotal(Integer immatureTotal) {
        this.immatureTotal = immatureTotal;
    }

    public Integer getMaturedTotal() {
        return maturedTotal;
    }

    public void setMaturedTotal(Integer maturedTotal) {
        this.maturedTotal = maturedTotal;
    }

    public List<Node> getNodes() {
        return nodes;
    }

    public void setNodes(List<Node> nodes) {
        this.nodes = nodes;
    }

    public Calendar getNow() {
        return now;
    }

    public void setNow(Calendar now) {
        this.now = now;
    }

    public Stats getStats() {
        return stats;
    }

    public void setStats(Stats stats) {
        this.stats = stats;
    }

}
