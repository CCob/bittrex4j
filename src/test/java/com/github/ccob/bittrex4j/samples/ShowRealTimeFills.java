package com.github.ccob.bittrex4j.samples;

import com.github.ccob.bittrex4j.BittrexExchange;
import com.github.ccob.bittrex4j.dao.Fill;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Properties;

public class ShowRealTimeFills {

    public static void main(String[] args) throws IOException {

        System.out.println("Press any key to quit");

        Properties prop = new Properties();
        prop.load(new FileInputStream("test_keys.properties"));

        try(BittrexExchange bittrexExchange = new BittrexExchange(prop.getProperty("apikey"),prop.getProperty("secret"))) {

            bittrexExchange.onUpdateSummaryState(exchangeSummaryState -> {
                if (exchangeSummaryState.getDeltas().length > 0) {

                    Arrays.stream(exchangeSummaryState.getDeltas())
                            .filter(marketSummary -> marketSummary.getMarketName().equals("BTC-BCC") || marketSummary.getMarketName().equals("BTC-ETH"))
                            .forEach(marketSummary -> System.out.println(
                                    String.format("24 hour volume for market %s: %s",
                                            marketSummary.getMarketName(),
                                            marketSummary.getVolume().toString())));
                }
            });

            bittrexExchange.onUpdateExchangeState(updateExchangeState -> {
                double volume = Arrays.stream(updateExchangeState.getFills())
                        .mapToDouble(Fill::getQuantity)
                        .sum();

                System.out.println(String.format("N: %d, %02f volume across %d fill(s) for %s", updateExchangeState.getNounce(),
                        volume, updateExchangeState.getFills().length, updateExchangeState.getMarketName()));
            });

            bittrexExchange.connectToWebSocket(() -> {
                bittrexExchange.subscribeToExchangeDeltas("BTC-ETH", null);
                bittrexExchange.subscribeToExchangeDeltas("BTC-BCC", null);
                bittrexExchange.subscribeToMarketSummaries(null);
            });

            System.in.read();
        }

        System.out.println("Closing websocket and exiting");
    }
}