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
    private boolean open;
    private String sentinel;
    private boolean cancelInitiated;
    private boolean immediateOrCancel;
    private boolean conditional;
    private String condition;
    private String conditionTarget;

    @JsonCreator
    public Order(@JsonProperty("AccountId") String accountId, @JsonProperty("OrderUuid") String orderUuid,
                 @JsonProperty("Exchange") String exchange, @JsonProperty("Type") @JsonAlias("OrderType") String type,
                 @JsonProperty("Quantity") double quantity, @JsonProperty("QuantityRemaining") double quantityRemaining,
                 @JsonProperty("Limit") double limit, @JsonProperty("Reserved") double reserved,
                 @JsonProperty("ReserveRemaining") double reserveRemaining, @JsonProperty("CommissionReserved") double commissionReserved,
                 @JsonProperty("CommissionReserveRemaining") double commissionReserveRemaining, @JsonProperty("CommissionPaid") double commissionPaid,
                 @JsonProperty("Price") double price, @JsonProperty("PricePerUnit") double pricePerUnit,
                 @JsonProperty("Opened") ZonedDateTime opened, @JsonProperty("Closed") ZonedDateTime closed,
                 @JsonProperty("IsOpen") boolean open, @JsonProperty("Sentinel") String sentinel,
                 @JsonProperty("CancelInitiated") boolean cancelInitiated, @JsonProperty("ImmediateOrCancel")boolean immediateOrCancel,
                 @JsonProperty("IsConditional") boolean conditional, @JsonProperty("Condition") String condition, @JsonProperty("ConditionTarget") String conditionTarget) {

        this.accountId = accountId;
        this.orderUuid = orderUuid;
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
}

