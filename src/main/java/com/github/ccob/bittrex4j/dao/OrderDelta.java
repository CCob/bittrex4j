package com.github.ccob.bittrex4j.dao;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class OrderDelta {

    private final String accountUuid;
    private final int nonce;
    private final int type;
    private final Order order;

    @JsonCreator
    public OrderDelta(@JsonProperty("AccountUuid") @JsonAlias("w")String accountUuid, @JsonProperty("Nonce") @JsonAlias("N") int nonce,
                      @JsonProperty("Type") @JsonAlias("TY")int type, @JsonProperty("Order") @JsonAlias("o") Order order) {
        this.accountUuid = accountUuid;
        this.nonce = nonce;
        this.type = type;
        this.order = order;
    }

    public String getAccountUuid() {
        return accountUuid;
    }

    public int getNonce() {
        return nonce;
    }

    public int getType() {
        return type;
    }

    public Order getOrder() {
        return order;
    }
}
