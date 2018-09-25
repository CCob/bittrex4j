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

public class Response<Result> {

    boolean success;
    String message;
    Result result;
    String explanation;

    @JsonCreator
    public Response(@JsonProperty("success") boolean success,
                    @JsonProperty("message") String message,
                    @JsonProperty("result") Result result, @JsonProperty("explanation") String explanation) {
        this.success = success;
        this.message = message;
        this.result = result;
        this.explanation = explanation;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public Result getResult() {
        return result;
    }

    public String getExplanation() {return explanation;}
}
