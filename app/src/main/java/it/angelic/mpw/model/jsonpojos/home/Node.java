package it.angelic.mpw.model.jsonpojos.home;

import java.io.Serializable;
import java.util.Date;

public class Node implements Serializable {

    private final static long serialVersionUID = 5318690168702939443L;
    private String difficulty;
    private String height;
    private Date lastBeat;
    private String name;

    /**
     * No args constructor for use in serialization
     */
    public Node() {
    }

    /**
     * @param height
     * @param name
     * @param difficulty
     * @param lastBeat
     */
    public Node(String difficulty, String height, Date lastBeat, String name) {
        super();
        this.difficulty = difficulty;
        this.height = height;
        this.lastBeat = lastBeat;
        this.name = name;
    }

    public String getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(String difficulty) {
        this.difficulty = difficulty;
    }

    public String getHeight() {
        return height;
    }

    public void setHeight(String height) {
        this.height = height;
    }

    public Date getLastBeat() {
        return lastBeat;
    }

    public void setLastBeat(Date lastBeat) {
        this.lastBeat = lastBeat;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
