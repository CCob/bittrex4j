package com.github.ccob.bittrex4j.samples;

import com.github.ccob.bittrex4j.BittrexExchange;

import java.io.IOException;

public class SubscribeToExchange {

    public static void main(String[] args) throws IOException {

        BittrexExchange bittrexExchange = new BittrexExchange();

        bittrexExchange.onUpdateExchangeState(updateExchangeState -> {
            int bp = 0;
        });

        bittrexExchange.connectToWebSocket( () -> bittrexExchange.subscribeToExchangeDeltas("BTC-ETH",
                result -> bittrexExchange.queryExchangeState("BTC-ETH")));

        System.in.read();
    }
}