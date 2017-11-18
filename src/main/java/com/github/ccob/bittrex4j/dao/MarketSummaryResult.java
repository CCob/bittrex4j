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

public class MarketSummaryResult {

    Market market;
    MarketSummary summary;
    Boolean verified;

    @JsonCreator
    public MarketSummaryResult(@JsonProperty("Market") Market market, @JsonProperty("Summary") MarketSummary summary,
                               @JsonProperty("IsVerified") Boolean isVerified) {
        this.market = market;
        this.summary = summary;
        this.verified = isVerified;
    }

    public Market getMarket() {
        return market;
    }

    public MarketSummary getSummary() {
        return summary;
    }

    public Boolean isVerified() {
        return verified;
    }
}
