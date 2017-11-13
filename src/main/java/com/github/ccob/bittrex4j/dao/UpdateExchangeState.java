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

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class UpdateExchangeState  {
    String marketName;
    long nounce;
    MarketOrder[] buys;
    MarketOrder[] sells;
    Fill[] fills;

    @JsonCreator
    public UpdateExchangeState(@JsonProperty("MarketName") String marketName, @JsonProperty("Nounce")long nounce,
                               @JsonProperty("Buys") MarketOrder[] buys, @JsonProperty("Sells") MarketOrder[] sells,
                               @JsonProperty("Fills") Fill[] fills) {
        this.marketName = marketName;
        this.nounce = nounce;
        this.buys = buys;
        this.sells = sells;
        this.fills = fills;
    }

    public String getMarketName() {
        return marketName;
    }

    public long getNounce() {
        return nounce;
    }

    public MarketOrder[] getBuys() {
        return buys;
    }

    public MarketOrder[] getSells() {
        return sells;
    }

    public Fill[] getFills() {
        return fills;
    }
}
