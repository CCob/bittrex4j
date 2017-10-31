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

public class Currency {

    private String name;
    private String nameLong;
    private int minConfirmation;
    private double txFee;
    private boolean isActive;
    private String coinType;
    private String baseAddress;
    private String notice;

    @JsonCreator
    public Currency(@JsonProperty("Currency") String name, @JsonProperty("CurrencyLong") String nameLong, @JsonProperty("MinConfirmation") int minConfirmation,
                    @JsonProperty("TxFee") double txFee, @JsonProperty("IsActive") boolean isActive, @JsonProperty("CoinType") String coinType,
                    @JsonProperty("BaseAddress") String baseAddress, @JsonProperty("Notice") String notice) {

        this.name = name;
        this.nameLong = nameLong;
        this.minConfirmation = minConfirmation;
        this.txFee = txFee;
        this.isActive = isActive;
        this.coinType = coinType;
        this.baseAddress = baseAddress;
        this.notice = notice;
    }

    public String getName() {
        return name;
    }

    public String getNameLong() {
        return nameLong;
    }

    public int getMinConfirmation() {
        return minConfirmation;
    }

    public double getTxFee() {
        return txFee;
    }

    public boolean isActive() {
        return isActive;
    }

    public String getCoinType() {
        return coinType;
    }

    public String getBaseAddress() {
        return baseAddress;
    }

    public String getNotice() {
        return notice;
    }
}
