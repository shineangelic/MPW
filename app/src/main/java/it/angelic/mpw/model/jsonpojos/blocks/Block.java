package it.angelic.mpw.model.jsonpojos.blocks;

import java.io.Serializable;
import java.util.List;

public class Block implements Serializable
{

    private List<Matured> candidates;
    private Integer candidatesTotal;
    private Integer immature;
    private Integer immatureTotal;
    private List<Matured> matured = null;
    private Integer maturedTotal;
    private final static long serialVersionUID = -3934007317307697902L;

    public List<Matured> getCandidates() {
        return candidates;
    }

    public void setCandidates(List<Matured> candidates) {
        this.candidates = candidates;
    }

    public Integer getCandidatesTotal() {
        return candidatesTotal;
    }

    public void setCandidatesTotal(Integer candidatesTotal) {
        this.candidatesTotal = candidatesTotal;
    }

    public Integer getImmature() {
        return immature;
    }

    public void setImmature(Integer immature) {
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


}





