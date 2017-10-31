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


import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.type.CollectionType;
import com.fasterxml.jackson.databind.type.TypeFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


/*

public  class TickArrayDeserializer extends JsonDeserializer<List<Tick>> {

    private static final String RESULT_PROPERTY = "result";
    private static final CollectionType collectionType =
            TypeFactory
                    .defaultInstance()
                    .constructCollectionType(List.class, ComparableBaseTick.class);

    @Override
    public List<Tick> deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {

        ObjectMapper mapper = (ObjectMapper)p.getCodec();
        ObjectNode objectNode = mapper.readTree(p);
        JsonNode nodeResults = objectNode.get(RESULT_PROPERTY);

        if (null == nodeResults                     // if no results node could be found
                || !nodeResults.isArray() )          // or results node is not an array
            return null;

        if(!nodeResults.elements().hasNext()){
            return new ArrayList<>();
        }

        ArrayNode array = (ArrayNode)nodeResults;
        ArrayList<Tick> values = new ArrayList<>();

        for(JsonNode node : array){
            values.add((Tick) mapper.readerFor(ComparableBaseTick.class).readValue(node));
        }

        return values;
    }
}

*/
