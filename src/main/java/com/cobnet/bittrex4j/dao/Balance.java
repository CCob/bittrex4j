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

package com.cobnet.bittrex4j.dao;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Balance {
    private String currency;
    private double balance;
    private double available;
    private double pending;
    private String cryptoAddress;
    private boolean requested;
    private String uuid;

    @JsonCreator
    public Balance(@JsonProperty("Currency") String currency, @JsonProperty("Balance") double balance,
                   @JsonProperty("Available") double available, @JsonProperty("Pending") double pending,
                   @JsonProperty("CryptoAddress") String cryptoAddress, @JsonProperty("Requested") boolean requested,
                   @JsonProperty("Uuid") String uuid) {
        this.currency = currency;
        this.balance = balance;
        this.available = available;
        this.pending = pending;
        this.cryptoAddress = cryptoAddress;
        this.requested = requested;
        this.uuid = uuid;
    }

    public String getCurrency() {
        return currency;
    }

    public double getBalance() {
        return balance;
    }

    public double getAvailable() {
        return available;
    }

    public double getPending() {
        return pending;
    }

    public String getCryptoAddress() {
        return cryptoAddress;
    }

    public boolean isRequested() {
        return requested;
    }

    public String getUuid() {
        return uuid;
    }
}
