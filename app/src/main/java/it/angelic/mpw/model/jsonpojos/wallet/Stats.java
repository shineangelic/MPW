package it.angelic.mpw.model.jsonpojos.wallet;

import java.io.Serializable;
import java.util.Date;

public class Stats implements Serializable
{

private Double balance = 0d;
private Integer blocksFound;
private Long immature;
private Date lastShare;
private Long paid;
private Long pending;

private final static long serialVersionUID = -3508834706692198488L;

public Double getBalance() {
return balance;
}

public void setBalance(Double balance) {
this.balance = balance;
}

public Integer getBlocksFound() {
return blocksFound;
}

public void setBlocksFound(Integer blocksFound) {
this.blocksFound = blocksFound;
}

public Long getImmature() {
return immature;
}

public void setImmature(Long immature) {
this.immature = immature;
}

public Date getLastShare() {
return lastShare;
}

public void setLastShare(Date lastShare) {
this.lastShare = lastShare;
}

public Long getPaid() {
return paid;
}

public void setPaid(Long paid) {
this.paid = paid;
}

public Long getPending() {
return pending;
}

public void setPending(Long pending) {
this.pending = pending;
}



}