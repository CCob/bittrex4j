package com.github.ccob.bittrex4j.dao;

import org.apache.commons.codec.binary.Base64;
import org.apache.http.client.entity.DeflateInputStream;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;

import static org.junit.Assert.*;

public class OrderDeltaTest {

    public void shouldParseBittrexWireData() throws IOException {
        String wireData = "jY9BT8MwDIX/i88JipM4bnKkqkShtCA6CU6o7RoJqIYQmjhM+++YjU0c8el9tp/8vIMvSMC+IHIO9UzI2g8z63HKQU8Uo53serQ5g4IWklPQP0EyCt4h7WD1f3MNKQQsIhtvg4Lux4puinGdUWcmq73xg46MQY95wJxHJBxJrJWsXvalrvoroa4XbOrbun9+qJpGOvcS6MKg3IuhUPBxQEOGXGAFjweU02SM5N4cp1IYooK7XwzMaAVX5+0YC/OnFMjfSNYxsqcCUTolpM12WRS8nEQpb+Zh+ZwV3JzV21ldS/S2aysJ/XqybI9iv/8G";
        java.util.Scanner s = new java.util.Scanner(new DeflateInputStream( new ByteArrayInputStream(Base64.decodeBase64(wireData)))).useDelimiter("\\A");
        String minifiedJson =  s.hasNext() ? s.next() : "";




    }

}