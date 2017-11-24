package com.github.ccob.bittrex4j.dao;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by ceri on 24/11/2017.
 */
public class DepositAddress  {

    private String currency;
    private String address;

    @JsonCreator
    public DepositAddress(@JsonProperty("Currency") String currency, @JsonProperty("Address") String address) {
        this.currency = currency;
        this.address = address;
    }

    public String getCurrency() {
        return currency;
    }

    public String getAddress() {
        return address;
    }
}
