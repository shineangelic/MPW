package it.angelic.mpw.model.jsonpojos.wallet;

import java.io.Serializable;
import java.util.Date;

public class Payment implements Serializable
{

private Double amount;
private Date timestamp;
private String tx;

private final static long serialVersionUID = 9059762457675286124L;

public Double getAmount() {
return amount;
}

public void setAmount(Double amount) {
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

}