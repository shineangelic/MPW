package it.angelic.noobpoolstats.model.jsonpojos.wallet;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Payment implements Serializable
{

private Long amount;
private Date timestamp;
private String tx;
private final Map<String, Object> additionalProperties = new HashMap<>();
private final static long serialVersionUID = 9059762457675286124L;

public Long getAmount() {
return amount;
}

public void setAmount(Long amount) {
this.amount = amount;
}

public Date getTimestamp() {
return timestamp;
}

public void setTimestamp(Date timestamp) {
this.timestamp = timestamp;
}

public String getTx() {
return tx;
}

public void setTx(String tx) {
this.tx = tx;
}

public Map<String, Object> getAdditionalProperties() {
return this.additionalProperties;
}

public void setAdditionalProperty(String name, Object value) {
this.additionalProperties.put(name, value);
}

}