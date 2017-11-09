package it.angelic.noobpoolstats.model.jsonpojos.blocks;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Block implements Serializable
{

    private Object candidates;
    private Integer candidatesTotal;
    private Object immature;
    private Integer immatureTotal;
    private List<Matured> matured = null;
    private Integer maturedTotal;
    private final Map<String, Object> additionalProperties = new HashMap<>();
    private final static long serialVersionUID = -3934007317307697902L;

    public Object getCandidates() {
        return candidates;
    }

    public void setCandidates(Object candidates) {
        this.candidates = candidates;
    }

    public Integer getCandidatesTotal() {
        return candidatesTotal;
    }

    public void setCandidatesTotal(Integer candidatesTotal) {
        this.candidatesTotal = candidatesTotal;
    }

    public Object getImmature() {
        return immature;
    }

    public void setImmature(Object immature) {
        this.immature = immature;
    }

    public Integer getImmatureTotal() {
        return immatureTotal;
    }

    public void setImmatureTotal(Integer immatureTotal) {
        this.immatureTotal = immatureTotal;
    }


    public List<Matured> getMatured() {
        return matured;
    }

    public void setMatured(List<Matured> matured) {
        this.matured = matured;
    }

    public Integer getMaturedTotal() {
        return maturedTotal;
    }

    public void setMaturedTotal(Integer maturedTotal) {
        this.maturedTotal = maturedTotal;
    }

    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

}





