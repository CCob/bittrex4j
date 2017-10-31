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

package com.cobnet.bittrex4j;

import com.fasterxml.jackson.annotation.*;

import java.time.Duration;
import java.time.ZonedDateTime;

@JsonIgnoreProperties("BV")
public abstract class TickMixin {

    /*
    @JsonCreator(mode=JsonCreator.Mode.DELEGATING)
    public TickMixin(@JacksonInject("timePeriod") Duration timePeriod,
                     @JsonProperty("T") @JsonFormat
                             (shape = JsonFormat.Shape.STRING, pattern="yyyy-MM-dd'T'HH:mm:ss[.SSS][Z]", timezone="UTC") ZonedDateTime endTime,
                     @JsonProperty("O") Decimal openPrice, @JsonProperty("H") Decimal highPrice, @JsonProperty("L") Decimal lowPrice, @JsonProperty("C")Decimal closePrice,
                     @JsonProperty("V")Decimal volume){
    }
    */
}
