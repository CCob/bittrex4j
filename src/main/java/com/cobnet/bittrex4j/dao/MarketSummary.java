/*
 * *
 *  This file is part of the bittrex4j project.
 *
 *  @author CCob
 *
 *  For the full copyright and license information, please view the LICENSE
 *  file that was distributed with this source code.
 * /
 */

package com.cobnet.bittrex4j.dao;

import com.cobnet.bittrex4j.DateTimeDeserializer;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import java.math.BigDecimal;
import java.time.ZonedDateTime;

/**
 * Created by ceri on 09/09/2017.
 */
@JsonIgnoreProperties("DisplayMarketName")
public class MarketSummary {

    @JsonProperty("MarketName")
    String marketName;

    @JsonProperty("High")
    BigDecimal high;

    @JsonProperty("Low")
    BigDecimal low;

    @JsonProperty("Volume")
    BigDecimal volume;

    @JsonProperty("Last")
    BigDecimal last;

    @JsonProperty("BaseVolume")
    BigDecimal baseVolume;

    @JsonProperty("TimeStamp")
    @JsonDeserialize(using = DateTimeDeserializer.class)
    ZonedDateTime timeStamp;

    @JsonProperty("Bid")
    BigDecimal bid;

    @JsonProperty("Ask")
    BigDecimal ask;

    @JsonProperty("OpenBuyOrders")
    int openBuyOrders;

    @JsonProperty("OpenSellOrders")
    int openSellOrders;

    @JsonProperty("PrevDay")
    BigDecimal prevDay;

    @JsonProperty("Created")
    @JsonDeserialize(using = DateTimeDeserializer.class)
    ZonedDateTime created;

    public MarketSummary(){}

    public MarketSummary(BigDecimal bid, BigDecimal ask){
        this.bid = bid;
        this.ask = ask;
    }

    public String getMarketName() {
        return marketName;
    }

    public BigDecimal getHigh() {
        return high;
    }

    public BigDecimal getLow() {
        return low;
    }

    public BigDecimal getVolume() {
        return volume;
    }

    public BigDecimal getLast() {
        return last;
    }

    public BigDecimal getBaseVolume() {
        return baseVolume;
    }

    public ZonedDateTime getTimeStamp() {
        return timeStamp;
    }

    public BigDecimal getBid() {
        return bid;
    }

    public BigDecimal getAsk() {
        return ask;
    }

    public int getOpenBuyOrders() {
        return openBuyOrders;
    }

    public int getOpenSellOrders() {
        return openSellOrders;
    }

    public BigDecimal getPrevDay() {
        return prevDay;
    }

    public ZonedDateTime getCreated() {
        return created;
    }
}
