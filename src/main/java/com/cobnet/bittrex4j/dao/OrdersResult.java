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

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class OrdersResult {

    private Order[] buys;
    private Order[] sells;

    @JsonCreator
    public OrdersResult(@JsonProperty("buy") Order[] buys, @JsonProperty("sell") Order[] sells) {
        this.buys = buys;
        this.sells = sells;
    }

    public Order[] getBuys() {
        return buys;
    }

    public Order[] getSells() {
        return sells;
    }
}
