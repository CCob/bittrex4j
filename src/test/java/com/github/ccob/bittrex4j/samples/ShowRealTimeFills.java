package com.github.ccob.bittrex4j.samples;

import com.github.ccob.bittrex4j.BittrexExchange;
import com.github.ccob.bittrex4j.dao.Fill;

import java.io.IOException;
import java.util.Arrays;

public class ShowRealTimeFills {

    public static void main(String[] args) throws IOException {

        System.out.println("Press any key to quit");

        BittrexExchange bittrexExchange = new BittrexExchange();

        bittrexExchange.onUpdateExchangeState(updateExchangeState -> {
            if(updateExchangeState.getFills().length > 0) {
                double volume = Arrays.stream(updateExchangeState.getFills())
                        .mapToDouble(Fill::getQuantity)
                        .sum();

                System.out.println(String.format("%02f volume across %d fill(s) for %s", volume,
                        updateExchangeState.getFills().length, updateExchangeState.getMarketName()));
            }
        });

        bittrexExchange.connectToWebSocket( () -> {
            bittrexExchange.subscribeToExchangeDeltas("BTC-ETH", null);
            bittrexExchange.subscribeToExchangeDeltas("BTC-BCC",null);
        });

        System.in.read();
        bittrexExchange.disconnectFromWebSocket();
    }
}