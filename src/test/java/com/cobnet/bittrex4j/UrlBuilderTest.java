package com.cobnet.bittrex4j;


import org.junit.Test;

public class UrlBuilderTest {

    @Test(expected = UnsupportedOperationException.class)
    public void shouldThrowWhenV2SecureRequested(){
        UrlBuilder.v2()
                .withApiKey("1234","5678")
                .build();
    }
}