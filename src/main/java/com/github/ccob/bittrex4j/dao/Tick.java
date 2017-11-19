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

import com.fasterxml.jackson.annotation.*;

import java.time.Duration;
import java.time.ZonedDateTime;

public class Tick {

    private ZonedDateTime endTime;
    private double open;
    private double high;
    private double low;
    private double close;
    private double volume;
    private double baseVolume;

    @JsonCreator
    public Tick(@JsonProperty("T") @JsonFormat (shape = JsonFormat.Shape.STRING, pattern="yyyy-MM-dd'T'HH:mm:ss[.SSS][Z]", timezone="UTC") ZonedDateTime endTime,
                @JsonProperty("O") double open, @JsonProperty("H") double high, @JsonProperty("L") double low, @JsonProperty("C") double close,
                @JsonProperty("V")double volume, @JsonProperty("BV") double baseVolume){
        this.endTime = endTime;
        this.open = open;
        this.high = high;
        this.low = low;
        this.close = close;
        this.volume = volume;
        this.baseVolume = baseVolume;
    }

    public ZonedDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(ZonedDateTime endTime) {
        this.endTime = endTime;
    }

    public double getOpen() {
        return open;
    }

    public void setOpen(double open) {
        this.open = open;
    }

    public double getHigh() {
        return high;
    }

    public void setHigh(double high) {
        this.high = high;
    }

    public double getLow() {
        return low;
    }

    public void setLow(double low) {
        this.low = low;
    }

    public double getClose() {
        return close;
    }

    public void setClose(double close) {
        this.close = close;
    }

    public double getVolume() {
        return volume;
    }

    public void setVolume(double volume) {
        this.volume = volume;
    }

    public double getBaseVolume() {
        return baseVolume;
    }

    public void setBaseVolume(double baseVolume) {
        this.baseVolume = baseVolume;
    }
}
