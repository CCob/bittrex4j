package com.github.ccob.bittrex4j.dao;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class BalanceDelta {

    private final int nonce;
    private final Balance balance;

    @JsonCreator
    public BalanceDelta(@JsonProperty("Nonce") @JsonAlias("N") int nonce, @JsonProperty("Delta") @JsonAlias("d") Balance balance) {
        this.nonce = nonce;
        this.balance = balance;
    }

    public int getNonce() {
        return nonce;
    }

    public Balance getBalance() {
        return balance;
    }
}