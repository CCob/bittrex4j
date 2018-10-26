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
import java.math.BigDecimal;

public class Fill {

    Long id;
    String orderType;
    String fillType;
    BigDecimal price;
    BigDecimal quantity;
    BigDecimal total;
    ZonedDateTime timeStamp;
    Integer fillId;



    @JsonCreator
    public Fill(@Nullable @JsonProperty("Id") @JsonAlias("I") Long id, @JsonProperty("OrderType") @JsonAlias("OT") String orderType, @Nullable @JsonProperty("FillType") @JsonAlias("F") String fillType,
                @Nullable @JsonProperty("Price") @JsonAlias("P") BigDecimal price, @Nullable @JsonProperty("Rate")  @JsonAlias("R") BigDecimal rate,
                @JsonProperty("Quantity") @JsonAlias("Q") BigDecimal quantity, @Nullable @JsonProperty("Total") @JsonAlias("t") BigDecimal total, @JsonProperty("TimeStamp") @JsonAlias("T") ZonedDateTime timeStamp,
                @Nullable @JsonProperty("FI") Integer fillId){

        if(rate == null && price == null){
            throw new IllegalArgumentException("Either rate or price should be set");
        }

        this.id = id;
        this.fillId = fillId;
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
            this.total = this.price.multiply(this.quantity);
        }
    }
    
    public @Nullable Long getId() {
        return id;
    }

    public String getOrderType() {
        return orderType;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public BigDecimal getQuantity() {
        return quantity;
    }

    public ZonedDateTime getTimeStamp() {
        return timeStamp;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public @Nullable String getFillType() {
        return fillType;
    }

    public int getFillId() {
        return fillId;
    }
}
