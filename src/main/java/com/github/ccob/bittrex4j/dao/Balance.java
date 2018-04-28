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

import java.time.ZonedDateTime;

public class Balance {
    private String currency;
    private double balance;
    private double available;
    private double pending;
    private String cryptoAddress;
    private boolean requested;
    private String uuid;
    private ZonedDateTime updated;
    private Boolean autoSell;
    private Long accountId;

    @JsonCreator
    public Balance(@JsonProperty("AccountId") @JsonAlias("W") Long accountId, @JsonProperty("Currency") @JsonAlias("c") String currency, @JsonProperty("Balance") @JsonAlias("b") double balance,
                   @JsonProperty("Available") @JsonAlias("a") double available, @JsonProperty("Pending") @JsonAlias("z") double pending,
                   @JsonProperty("CryptoAddress") @JsonAlias("p") String cryptoAddress, @JsonProperty("Requested") @JsonAlias("r") boolean requested,
                   @JsonProperty("Uuid") @JsonAlias("U") String uuid, @JsonProperty("u") ZonedDateTime updated, @JsonProperty("h") Boolean autoSell) {
        this.currency = currency;
        this.balance = balance;
        this.available = available;
        this.pending = pending;
        this.cryptoAddress = cryptoAddress;
        this.requested = requested;
        this.uuid = uuid;
        this.updated = updated;
        this.autoSell = autoSell;
        this.accountId = accountId;
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

    public ZonedDateTime getUpdated() {
        return updated;
    }

    public Boolean getAutoSell() {
        return autoSell;
    }

    public Long getAccountId() {
        return accountId;
    }
}
