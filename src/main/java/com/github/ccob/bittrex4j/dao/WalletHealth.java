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

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.ZonedDateTime;

public class WalletHealth {
    private  String currency;
    private int depositQueueDepth;
    private int withdrawQueueDepth;
    private long blockHeight;
    private int walletBalance;
    private int walletConnections;
    private int minutesSinceBHUpdated;
    private ZonedDateTime lastChecked;
    private boolean isActive;

    @JsonCreator
    public WalletHealth(@JsonProperty("Currency") String currency, @JsonProperty("DepositQueueDepth") int depositQueueDepth, @JsonProperty("WithdrawQueueDepth") int withdrawQueueDepth,
                        @JsonProperty("BlockHeight") long blockHeight, @JsonProperty("WalletBalance") int walletBalance,
                        @JsonProperty("WalletConnections") int walletConnections, @JsonProperty("MinutesSinceBHUpdated") int minutesSinceBHUpdated,
                        @JsonProperty("LastChecked") ZonedDateTime lastChecked, @JsonProperty("IsActive") boolean isActive) {
        this.currency = currency;
        this.depositQueueDepth = depositQueueDepth;
        this.withdrawQueueDepth = withdrawQueueDepth;
        this.blockHeight = blockHeight;
        this.walletBalance = walletBalance;
        this.walletConnections = walletConnections;
        this.minutesSinceBHUpdated = minutesSinceBHUpdated;
        this.lastChecked = lastChecked;
        this.isActive = isActive;
    }

    public String getCurrency() {
        return currency;
    }

    public int getDepositQueueDepth() {
        return depositQueueDepth;
    }

    public int getWithdrawQueueDepth() {
        return withdrawQueueDepth;
    }

    public long getBlockHeight() {
        return blockHeight;
    }

    public int getWalletBalance() {
        return walletBalance;
    }

    public int getWalletConnections() {
        return walletConnections;
    }

    public int getMinutesSinceBHUpdated() {
        return minutesSinceBHUpdated;
    }

    public ZonedDateTime getLastChecked() {
        return lastChecked;
    }

    public boolean isActive() {
        return isActive;
    }
}
