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

public class MarketOrdersResult {

    private MarketOrder[] buys;
    private MarketOrder[] sells;

    @JsonCreator
    public MarketOrdersResult(@JsonProperty("buy") MarketOrder[] buys, @JsonProperty("sell") MarketOrder[] sells) {
        this.buys = buys;
        this.sells = sells;
    }

    public MarketOrder[] getBuys() {
        return buys;
    }

    public MarketOrder[] getSells() {
        return sells;
    }
}
