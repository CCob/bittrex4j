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

//{
//
//        "AccountId" : null,
//        "OrderUuid" : "0cb4c4e4-bdc7-4e13-8c13-430e587d2cc1",
//        "Exchange" : "BTC-SHLD",
//        "Type" : "LIMIT_BUY",
//        "Quantity" : 1000.00000000,
//        "QuantityRemaining" : 1000.00000000,
//        "Limit" : 0.00000001,
//        "Reserved" : 0.00001000,
//        "ReserveRemaining" : 0.00001000,
//        "CommissionReserved" : 0.00000002,
//        "CommissionReserveRemaining" : 0.00000002,
//        "CommissionPaid" : 0.00000000,
//        "Price" : 0.00000000,
//        "PricePerUnit" : null,
//        "Opened" : "2014-07-13T07:45:46.27",
//        "Closed" : null,
//        "IsOpen" : true,
//        "Sentinel" : "6c454604-22e2-4fb4-892e-179eede20972",
//        "CancelInitiated" : false,
//        "ImmediateOrCancel" : false,
//        "IsConditional" : false,
//        "Condition" : "NONE",
//        "ConditionTarget" : null
//        }


import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.ZonedDateTime;

@JsonIgnoreProperties("Uuid")
public class Order {
    private String accountId;
    private String orderUuid;
    private String exchange;
    private String type;
    private Long id;
    private double quantity;
    private double quantityRemaining;
    private double limit;
    private double reserved;
    private double reserveRemaining;
    private double commissionReserved;
    private double commissionReserveRemaining;
    private double commissionPaid;
    private double price;
    private double pricePerUnit;
    private ZonedDateTime opened;
    private ZonedDateTime closed;
    private ZonedDateTime updated;
    private boolean open;
    private String sentinel;
    private boolean cancelInitiated;
    private boolean immediateOrCancel;
    private boolean conditional;
    private String condition;
    private String conditionTarget;

    @JsonCreator
    public Order(@JsonProperty("AccountId") @JsonAlias("U") String accountId, @JsonProperty("OrderUuid") @JsonAlias({"OU"}) String orderUuid,
                 @JsonProperty("I") Long id,
                 @JsonProperty("Exchange") @JsonAlias("E") String exchange, @JsonProperty("Type") @JsonAlias({"OrderType","OT"})String type,
                 @JsonProperty("Quantity") @JsonAlias("Q") double quantity, @JsonProperty("QuantityRemaining") @JsonAlias("q") double quantityRemaining,
                 @JsonProperty("Limit") @JsonAlias("X") double limit, @JsonProperty("Reserved") double reserved,
                 @JsonProperty("ReserveRemaining") double reserveRemaining, @JsonProperty("CommissionReserved") double commissionReserved,
                 @JsonProperty("CommissionReserveRemaining") double commissionReserveRemaining, @JsonProperty("CommissionPaid") @JsonAlias({"Commission","n"}) double commissionPaid,
                 @JsonProperty("Price") @JsonAlias("P") double price, @JsonProperty("PricePerUnit") @JsonAlias("PU") double pricePerUnit,
                 @JsonProperty("Opened") @JsonAlias({"TimeStamp","Y"}) ZonedDateTime opened, @JsonProperty("Closed") @JsonAlias("C") ZonedDateTime closed,
                 @JsonProperty("IsOpen") @JsonAlias("i") boolean open, @JsonProperty("Sentinel") String sentinel,
                 @JsonProperty("CancelInitiated") @JsonAlias("CI") boolean cancelInitiated, @JsonProperty("ImmediateOrCancel") @JsonAlias("K")boolean immediateOrCancel,
                 @JsonProperty("IsConditional") @JsonAlias("k") boolean conditional, @JsonProperty("Condition") @JsonAlias("J") String condition,
                 @JsonProperty("ConditionTarget") @JsonAlias("j") String conditionTarget, @JsonProperty("u") ZonedDateTime updated) {

        this.accountId = accountId;
        this.orderUuid = orderUuid;
        this.id = id;
        this.exchange = exchange;
        this.type = type;
        this.quantity = quantity;
        this.quantityRemaining = quantityRemaining;
        this.limit = limit;
        this.reserved = reserved;
        this.reserveRemaining = reserveRemaining;
        this.commissionReserved = commissionReserved;
        this.commissionReserveRemaining = commissionReserveRemaining;
        this.commissionPaid = commissionPaid;
        this.price = price;
        this.pricePerUnit = pricePerUnit;
        this.opened = opened;
        this.closed = closed;
        this.open = open;
        this.sentinel = sentinel;
        this.cancelInitiated = cancelInitiated;
        this.immediateOrCancel = immediateOrCancel;
        this.conditional = conditional;
        this.condition = condition;
        this.conditionTarget = conditionTarget;
        this.updated = updated;
    }

    public String getAccountId() {
        return accountId;
    }

    public String getOrderUuid() {
        return orderUuid;
    }

    public String getExchange() {
        return exchange;
    }

    public String getType() {
        return type;
    }

    public double getQuantity() {
        return quantity;
    }

    public double getQuantityRemaining() {
        return quantityRemaining;
    }

    public double getLimit() {
        return limit;
    }

    public double getReserved() {
        return reserved;
    }

    public double getReserveRemaining() {
        return reserveRemaining;
    }

    public double getCommissionReserved() {
        return commissionReserved;
    }

    public double getCommissionReserveRemaining() {
        return commissionReserveRemaining;
    }

    public double getCommissionPaid() {
        return commissionPaid;
    }

    public double getPrice() {
        return price;
    }

    public double getPricePerUnit() {
        return pricePerUnit;
    }

    public ZonedDateTime getOpened() {
        return opened;
    }

    public ZonedDateTime getClosed() {
        return closed;
    }

    public boolean isOpen() {
        return open || closed == null;
    }

    public String getSentinel() {
        return sentinel;
    }

    public boolean isCancelInitiated() {
        return cancelInitiated;
    }

    public boolean isImmediateOrCancel() {
        return immediateOrCancel;
    }

    public boolean isConditional() {
        return conditional;
    }

    public String getCondition() {
        return condition;
    }

    public String getConditionTarget() {
        return conditionTarget;
    }

    public Long getId() {
        return id;
    }

    public ZonedDateTime getUpdated() {
        return updated;
    }
}

