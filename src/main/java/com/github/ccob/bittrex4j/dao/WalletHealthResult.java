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

public class WalletHealthResult {

    private WalletHealth health;
    private Currency currency;

    @JsonCreator
    public WalletHealthResult(@JsonProperty("Health") WalletHealth health, @JsonProperty("Currency") Currency currency){
        this.health = health;
        this.currency = currency;
    }

    public WalletHealth getHealth() {
        return health;
    }

    public Currency getCurrency() {
        return currency;
    }
}
