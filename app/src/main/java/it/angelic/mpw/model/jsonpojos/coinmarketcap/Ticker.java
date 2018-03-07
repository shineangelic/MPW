package it.angelic.mpw.model.jsonpojos.coinmarketcap;

import com.google.gson.annotations.SerializedName;

/**
 * Created by shine@angelic.it on 07/03/2018.
 */

public class Ticker {

    /**
     * id : bitcoin
     * name : Bitcoin
     * symbol : BTC
     * rank : 1
     * price_usd : 10852.2
     * price_btc : 1.0
     * 24h_volume_usd : 6808060000.0
     * market_cap_usd : 183446402715
     * available_supply : 16904075.0
     * total_supply : 16904075.0
     * max_supply : 21000000.0
     * percent_change_1h : 0.0
     * percent_change_24h : -6.36
     * percent_change_7d : 0.88
     * last_updated : 1520377166
     */

    private String id;
    private String name;
    private String symbol;
    private String rank;
    private String price_usd;
    private String price_btc;
    @SerializedName("24h_volume_usd")
    private String _$24h_volume_usd;
    private String market_cap_usd;
    private String available_supply;
    private String total_supply;
    private String max_supply;
    private String percent_change_1h;
    private String percent_change_24h;
    private String percent_change_7d;
    private String last_updated;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public String getRank() {
        return rank;
    }

    public void setRank(String rank) {
        this.rank = rank;
    }

    public String getPrice_usd() {
        return price_usd;
    }

    public void setPrice_usd(String price_usd) {
        this.price_usd = price_usd;
    }

    public String getPrice_btc() {
        return price_btc;
    }

    public void setPrice_btc(String price_btc) {
        this.price_btc = price_btc;
    }

    public String get_$24h_volume_usd() {
        return _$24h_volume_usd;
    }

    public void set_$24h_volume_usd(String _$24h_volume_usd) {
        this._$24h_volume_usd = _$24h_volume_usd;
    }

    public String getMarket_cap_usd() {
        return market_cap_usd;
    }

    public void setMarket_cap_usd(String market_cap_usd) {
        this.market_cap_usd = market_cap_usd;
    }

    public String getAvailable_supply() {
        return available_supply;
    }

    public void setAvailable_supply(String available_supply) {
        this.available_supply = available_supply;
    }

    public String getTotal_supply() {
        return total_supply;
    }

    public void setTotal_supply(String total_supply) {
        this.total_supply = total_supply;
    }

    public String getMax_supply() {
        return max_supply;
    }

    public void setMax_supply(String max_supply) {
        this.max_supply = max_supply;
    }

    public String getPercent_change_1h() {
        return percent_change_1h;
    }

    public void setPercent_change_1h(String percent_change_1h) {
        this.percent_change_1h = percent_change_1h;
    }

    public String getPercent_change_24h() {
        return percent_change_24h;
    }

    public void setPercent_change_24h(String percent_change_24h) {
        this.percent_change_24h = percent_change_24h;
    }

    public String getPercent_change_7d() {
        return percent_change_7d;
    }

    public void setPercent_change_7d(String percent_change_7d) {
        this.percent_change_7d = percent_change_7d;
    }

    public String getLast_updated() {
        return last_updated;
    }

    public void setLast_updated(String last_updated) {
        this.last_updated = last_updated;
    }
}
