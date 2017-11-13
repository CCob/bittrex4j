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

public class MarketOrder {
    private int type;
    private double quantity;
    private double rate;
    
    @JsonCreator
    public MarketOrder(@JsonProperty("Type") int type, @JsonProperty("Quantity") double quantity, @JsonProperty("Rate") double rate) {
        this.quantity = quantity;
        this.rate = rate;
        this.type = type;
    }

    public double getQuantity() {
        return quantity;
    }

    public double getRate() {
        return rate;
    }

    public int getType() {
        return type;
    }
}
