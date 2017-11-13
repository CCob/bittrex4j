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

import java.time.ZonedDateTime;

public class Fill {

    String orderType;
    double rate;
    double quantity;
    ZonedDateTime timeStamp;

    @JsonCreator
    public Fill(@JsonProperty("OrderType") String orderType, @JsonProperty("Rate") double rate,
                @JsonProperty("Quantity") double quantity, @JsonProperty("TimeStamp") ZonedDateTime timeStamp) {
        this.orderType = orderType;
        this.rate = rate;
        this.quantity = quantity;
        this.timeStamp = timeStamp;
    }

    public String getOrderType() {
        return orderType;
    }

    public double getRate() {
        return rate;
    }

    public double getQuantity() {
        return quantity;
    }

    public ZonedDateTime getTimeStamp() {
        return timeStamp;
    }
}
