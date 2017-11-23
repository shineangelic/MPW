package it.angelic.noobpoolstats.model.jsonpojos.wallet;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Stats implements Serializable
{

private Integer balance = 0;
private Integer blocksFound;
private Integer immature;
private Date lastShare;
private Long paid;
private Long pending;

private final static long serialVersionUID = -3508834706692198488L;

public Integer getBalance() {
return balance;
}

public void setBalance(Integer balance) {
this.balance = balance;
}

public Integer getBlocksFound() {
return blocksFound;
}

public void setBlocksFound(Integer blocksFound) {
this.blocksFound = blocksFound;
}

public Integer getImmature() {
return immature;
}

public void setImmature(Integer immature) {
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