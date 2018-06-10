package it.angelic.mpw.model.jsonpojos.home;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class HomeStats implements Serializable {

    private final static long serialVersionUID = -991675406872456775L;
    private Integer candidatesTotal;
    private Long hashrate;
    private Integer immatureTotal;
    private Integer maturedTotal;
    private Integer minersTotal;
    private List<Node> nodes;
    private Calendar now;//serve a distinguere timestamp
    private Stats stats;


    /**
     * No args constructor for use in serialization
     */
    public HomeStats() {
        candidatesTotal = 0;
        hashrate = 0L;
        immatureTotal = 0;
        maturedTotal = 0;
        nodes = new ArrayList<>();
        now = Calendar.getInstance();
        stats = new Stats();
        nodes.add(new Node());
    }


    public Integer getCandidatesTotal() {
        return candidatesTotal;
    }

    public void setCandidatesTotal(Integer candidatesTotal) {
        this.candidatesTotal = candidatesTotal;
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

    public Integer getMinersTotal() {
        return minersTotal;
    }

    public void setMinersTotal(Integer minersTotal) {
        this.minersTotal = minersTotal;
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
