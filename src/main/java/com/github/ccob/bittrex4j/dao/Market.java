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

/**
 * Created by ceri on 29/10/2017.
 */

public class Market {

    private String marketCurrency;
    private String marketCurrencyLong;
    private String baseCurrency;
    private String baseCurrencyLong;
    private double minTradeSize;
    private String marketName;
    private boolean isActive;
    private ZonedDateTime created;
    private String notice;
    private boolean isSponsored;
    private String logoUrl;

    @JsonCreator
    public Market( @JsonProperty("MarketCurrency") String marketCurrency, @JsonProperty("BaseCurrency") String baseCurrency,
                   @JsonProperty("MarketCurrencyLong") String marketCurrencyLong, @JsonProperty("BaseCurrencyLong") String baseCurrencyLong, @JsonProperty("MinTradeSize") double minTradeSize,
                   @JsonProperty("MarketName") String marketName, @JsonProperty("IsActive") boolean isActive,
                   @JsonProperty("Created") ZonedDateTime created, @JsonProperty("Notice") String notice,
                   @JsonProperty("IsSponsored") boolean isSponsored, @JsonProperty("LogoUrl") String logoUrl) {
        this.marketCurrency = marketCurrency;
        this.baseCurrency = baseCurrency;
        this.marketCurrencyLong = marketCurrencyLong;
        this.baseCurrencyLong = baseCurrencyLong;
        this.minTradeSize = minTradeSize;
        this.marketName = marketName;
        this.isActive = isActive;
        this.created = created;
        this.notice = notice;
        this.isSponsored = isSponsored;
        this.logoUrl = logoUrl;
    }

    public String getMarketCurrency() {
        return marketCurrency;
    }

    public String getBaseCurrency() {
        return baseCurrency;
    }

    public String getMarketCurrencyLong() {
        return marketCurrencyLong;
    }

    public String getBaseCurrencyLong() {
        return baseCurrencyLong;
    }

    public double getMinTradeSize() {
        return minTradeSize;
    }

    public String getMarketName() {
        return marketName;
    }

    public boolean isActive() {
        return isActive;
    }

    public ZonedDateTime getCreated() {
        return created;
    }

    public String getNotice() {
        return notice;
    }

    public boolean isSponsored() {
        return isSponsored;
    }

    public String getLogoUrl() {
        return logoUrl;
    }
}
