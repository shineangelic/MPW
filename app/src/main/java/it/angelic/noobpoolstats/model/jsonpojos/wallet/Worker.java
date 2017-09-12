package it.angelic.noobpoolstats.model.jsonpojos.wallet;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Worker implements Serializable
{

private Date lastBeat;
private Long hr;
private Boolean offline;
private Long hr2;
private final Map<String, Object> additionalProperties = new HashMap<>();
private final static long serialVersionUID = -5021013711718345521L;

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

public Long getHr2() {
return hr2;
}

public void setHr2(Long hr2) {
this.hr2 = hr2;
}

public Map<String, Object> getAdditionalProperties() {
return this.additionalProperties;
}

public void setAdditionalProperty(String name, Object value) {
this.additionalProperties.put(name, value);
}

}