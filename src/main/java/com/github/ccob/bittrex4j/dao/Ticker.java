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

package com.github.ccob.bittrex4j.dao;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Ticker {

    @JsonProperty("Bid")
    @JsonAlias("B")
    private BigDecimal bid;

    @JsonProperty("Ask")
    @JsonAlias("A")
    private BigDecimal ask;

    @JsonProperty("Last")
    @JsonAlias("l")
    private BigDecimal last;

    public Ticker() {
    }

    public Ticker(BigDecimal bid, BigDecimal ask, BigDecimal last) {
        super();
        this.bid = bid;
        this.ask = ask;
        this.last = last;
    }

    public BigDecimal getBid() {
        return bid;
    }

    public BigDecimal getAsk() {
        return ask;
    }

    public BigDecimal getLast() {
        return last;
    }

}
