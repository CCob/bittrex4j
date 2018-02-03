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

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class MarketOrder {
    private int type;
    private BigDecimal quantity;
    private BigDecimal rate;
    
    @JsonCreator
    public MarketOrder(@JsonProperty("Type") int type, @JsonProperty("Quantity") BigDecimal quantity, @JsonProperty("Rate") BigDecimal rate) {
        this.quantity = quantity;
        this.rate = rate;
        this.type = type;
    }

    public BigDecimal getQuantity() {
        return quantity;
    }

    public BigDecimal getRate() {
        return rate;
    }

    public int getType() {
        return type;
    }
}
