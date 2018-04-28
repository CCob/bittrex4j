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

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.annotation.Nullable;
import java.time.ZonedDateTime;

public class Fill {

    Long id;
    String orderType;
    String fillType;
    double price;
    double quantity;
    double total;
    ZonedDateTime timeStamp;



    @JsonCreator
    public Fill(@Nullable @JsonProperty("Id") @JsonAlias("I") Long id, @JsonProperty("OrderType") @JsonAlias("OT") String orderType, @Nullable @JsonProperty("FillType") @JsonAlias("F") String fillType,
                @Nullable @JsonProperty("Price") @JsonAlias("P") Double price, @Nullable @JsonProperty("Rate")  @JsonAlias("R")Double rate,
                @JsonProperty("Quantity") @JsonAlias("Q") double quantity, @Nullable @JsonProperty("Total") @JsonAlias("T") Double total, @JsonProperty("TimeStamp") @JsonAlias("T") ZonedDateTime timeStamp){

        if(rate == null && price == null){
            throw new IllegalArgumentException("Either rate or price should be set");
        }

        this.id = id;
        this.orderType = orderType;
        this.quantity = quantity;
        this.timeStamp = timeStamp;

        if(price!=null){
            this.price = price;
        }

        if(rate!=null){
            if(price != null){
                throw new IllegalArgumentException("Both rate and price cannot be set at the same time");
            }
            this.price = rate;
        }

        if(total!=null){
            this.total = total;
        }else{
            this.total = this.price*this.quantity;
        }
    }
    
    public @Nullable Long getId() {
        return id;
    }

    public String getOrderType() {
        return orderType;
    }

    public double getPrice() {
        return price;
    }

    public double getQuantity() {
        return quantity;
    }

    public ZonedDateTime getTimeStamp() {
        return timeStamp;
    }

    public double getTotal() {
        return total;
    }

    public @Nullable String getFillType() {
        return fillType;
    }
}
