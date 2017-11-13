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

public class CompletedOrder {
    private long id;
    private ZonedDateTime timeStamp;
    private int quantity;
    private double price;
    private double total;
    private String fillType;
    private String orderType;

    @JsonCreator
    public CompletedOrder(@JsonProperty("Id") long id, @JsonProperty("TimeStamp") ZonedDateTime timeStamp,
                          @JsonProperty("Quantity") int quantity, @JsonProperty("Price") double price,
                          @JsonProperty("Total") double total, @JsonProperty("FillType") String fillType,
                          @JsonProperty("OrderType") String orderType) {
        this.id = id;
        this.timeStamp = timeStamp;
        this.quantity = quantity;
        this.price = price;
        this.total = total;
        this.fillType = fillType;
        this.orderType = orderType;
    }

    public long getId() {
        return id;
    }

    public ZonedDateTime getTimeStamp() {
        return timeStamp;
    }

    public int getQuantity() {
        return quantity;
    }

    public double getPrice() {
        return price;
    }

    public double getTotal() {
        return total;
    }

    public String getFillType() {
        return fillType;
    }

    public String getOrderType() {
        return orderType;
    }
}
