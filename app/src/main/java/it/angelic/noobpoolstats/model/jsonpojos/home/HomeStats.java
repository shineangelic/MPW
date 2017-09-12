package it.angelic.noobpoolstats.model.jsonpojos.home;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HomeStats implements Serializable {

    private final static long serialVersionUID = -991675406872456775L;
    private Integer candidatesTotal;
    private Long hashrate;
    private Integer immatureTotal;
    private Integer maturedTotal;
    private Integer minersTotal;
    private List<Node> nodes = null;
    private Calendar now;//serve a distinguere timestamp
    private Stats stats;
    private final Map<String, Object> additionalProperties = new HashMap<>();


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

    /**
     * @param minersTotal
     * @param immatureTotal
     * @param now
     * @param stats
     * @param nodes
     * @param hashrate
     * @param maturedTotal
     * @param candidatesTotal
     */
    public HomeStats(Integer candidatesTotal, Long hashrate, Integer immatureTotal, Integer maturedTotal, Integer minersTotal, List<Node> nodes, Calendar now, Stats stats) {
        super();
        this.candidatesTotal = candidatesTotal;
        this.hashrate = hashrate;
        this.immatureTotal = immatureTotal;
        this.maturedTotal = maturedTotal;
        this.minersTotal = minersTotal;
        this.nodes = nodes;
        this.now = now;
        this.stats = stats;
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

    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

}
