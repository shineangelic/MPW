 package it.angelic.mpw.model.jsonpojos.blocks;

 import java.io.Serializable;
 import java.util.Date;

 public class Matured implements Serializable
{

    private Integer height;
    private Date timestamp;
    private Long difficulty;
    private Long shares;
    private Boolean uncle;
    private Integer uncleHeight;
    private Boolean orphan;
    private String hash;
    private String reward;
    private final static long serialVersionUID = 8304553049716471516L;

    public Integer getHeight() {
        return height;
    }

    public void setHeight(Integer height) {
        this.height = height;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public Long getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(Long difficulty) {
        this.difficulty = difficulty;
    }

    public Long getShares() {
        return shares;
    }

    public void setShares(Long shares) {
        this.shares = shares;
    }

    public Boolean getUncle() {
        return uncle;
    }

    public void setUncle(Boolean uncle) {
        this.uncle = uncle;
    }

    public Integer getUncleHeight() {
        return uncleHeight;
    }

    public void setUncleHeight(Integer uncleHeight) {
        this.uncleHeight = uncleHeight;
    }

    public Boolean getOrphan() {
        return orphan;
    }

    public void setOrphan(Boolean orphan) {
        this.orphan = orphan;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public String getReward() {
        return reward;
    }

    public void setReward(String reward) {
        this.reward = reward;
    }


}
