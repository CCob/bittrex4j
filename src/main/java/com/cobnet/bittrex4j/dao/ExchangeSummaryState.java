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

import com.fasterxml.jackson.annotation.JsonProperty;

public class ExchangeSummaryState extends Deltas<MarketSummary> {
    public ExchangeSummaryState(@JsonProperty("Nounce") long nounce, @JsonProperty("Deltas") MarketSummary[] deltas) {
        super(nounce, deltas);
    }
}
