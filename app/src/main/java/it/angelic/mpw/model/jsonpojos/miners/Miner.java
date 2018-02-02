package it.angelic.mpw.model.jsonpojos.miners;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Miner {

private Date lastBeat;
private Long hr;
private Boolean offline;
private Map<String, Object> additionalProperties = new HashMap<String, Object>();
    private String address;

    public Date getLastBeat() {
return lastBeat;
}

public void setLastBeat(Date lastBeat) {
this.lastBeat = lastBeat;
}

public Long getHr() {
return hr;
}

public void setHr(Long hr) {
this.hr = hr;
}

public Boolean getOffline() {
return offline;
}

public void setOffline(Boolean offline) {
this.offline = offline;
}

public Map<String, Object> getAdditionalProperties() {
return this.additionalProperties;
}

public void setAdditionalProperty(String name, Object value) {
this.additionalProperties.put(name, value);
}

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}