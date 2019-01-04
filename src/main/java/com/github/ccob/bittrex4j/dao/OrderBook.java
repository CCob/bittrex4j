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

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class OrderBook {

    @JsonProperty("buy")
    private List<OrderBookEntry> buy;

    @JsonProperty("sell")
    private List<OrderBookEntry> sell;

    public List<OrderBookEntry> getBuy() {
        return buy;
    }

    public List<OrderBookEntry> getSell() {
        return sell;
    }

}
