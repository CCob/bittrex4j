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

public class UuidResult {
    private String uuid;

    @JsonCreator
    public UuidResult(@JsonProperty("Uuid") String uuid) {
        this.uuid = uuid;
    }

    public String getUuid() {
        return uuid;
    }
}
