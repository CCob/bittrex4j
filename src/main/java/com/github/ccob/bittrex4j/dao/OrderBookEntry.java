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

public class OrderBookEntry {

    @JsonProperty("Quantity")
    @JsonAlias("Q")
    private BigDecimal quantity;

    @JsonProperty("Rate")
    @JsonAlias("R")
    private BigDecimal rate;

    public BigDecimal getQuantity() {
        return quantity;
    }

    public BigDecimal getRate() {
        return rate;
    }

    @Override
    public String toString() {
        return "OrderBookEntry [quantity=" + quantity + ", rate=" + rate + "]";
    }

}
