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

package com.github.ccob.bittrex4j;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;

public class DateTimeDeserializer extends JsonDeserializer<ZonedDateTime> {
    @Override
    public ZonedDateTime deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {

        DateTimeFormatter formatter =
                DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss[.SSS]").withZone(ZoneId.of("UTC"));

        String value = p.getValueAsString();
        ZonedDateTime result = null;

        if(value != null) {
            int index;
            if ((index = value.indexOf('.')) > -1) {
                char[] chars = new char[4 - (value.length() - index)];
                Arrays.fill(chars, '0');
                value += new String(chars);
            }
            result = ZonedDateTime.from(formatter.parse(value));
        }

        return result;
    }
}
