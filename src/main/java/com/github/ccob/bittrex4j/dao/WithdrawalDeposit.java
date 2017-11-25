package com.github.ccob.bittrex4j.dao;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.ZonedDateTime;

/**
 * Created by ceri on 24/11/2017.
 */
public class WithdrawalDeposit {

    private String paymentUuid;
    private String currency;
    private double amount;
    private String address;
    private ZonedDateTime opened;
    private boolean authorized;
    private boolean pendingPayment;
    private double txCost;
    private String txId;
    private boolean cancelled;
    private boolean invalidAddress;
    private int confirmations;
    private ZonedDateTime lastUpdated;

    @JsonCreator
    public WithdrawalDeposit(@JsonProperty("Id") String paymentUuid, @JsonProperty("Currency") String currency,
                             @JsonProperty("Amount") double amount, @JsonProperty("CryptoAddress") String address,
                             @JsonProperty("Opened") ZonedDateTime opened, @JsonProperty("Authorized") boolean authorized,
                             @JsonProperty("PendingPayment") boolean pendingPayment, @JsonProperty("TxCost") double txCost,
                             @JsonProperty("TxId") String txId, @JsonProperty("Canceled") boolean cancelled,
                             @JsonProperty("InvalidAddress") boolean invalidAddress, @JsonProperty("Confirmations") int confirmations,
                             @JsonProperty("LastUpdated") ZonedDateTime lastUpdated) {
        this.paymentUuid = paymentUuid;
        this.currency = currency;
        this.amount = amount;
        this.address = address;
        this.opened = opened;
        this.authorized = authorized;
        this.pendingPayment = pendingPayment;
        this.txCost = txCost;
        this.txId = txId;
        this.cancelled = cancelled;
        this.invalidAddress = invalidAddress;
        this.confirmations = confirmations;
        this.lastUpdated = lastUpdated;
    }

    public String getPaymentUuid() {
        return paymentUuid;
    }

    public String getCurrency() {
        return currency;
    }

    public double getAmount() {
        return amount;
    }

    public String getAddress() {
        return address;
    }

    public ZonedDateTime getOpened() {
        return opened;
    }

    public boolean isAuthorized() {
        return authorized;
    }

    public boolean isPendingPayment() {
        return pendingPayment;
    }

    public double getTxCost() {
        return txCost;
    }

    public String getTxId() {
        return txId;
    }

    public boolean isCancelled() {
        return cancelled;
    }

    public boolean isInvalidAddress() {
        return invalidAddress;
    }

    public int getConfirmations() {
        return confirmations;
    }

    public ZonedDateTime getLastUpdated() {
        return lastUpdated;
    }
}
