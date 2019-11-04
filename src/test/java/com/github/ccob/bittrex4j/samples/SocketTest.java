package com.github.ccob.bittrex4j.samples;

import com.github.ccob.bittrex4j.BittrexExchange;
import com.github.ccob.bittrex4j.dao.Order;
import com.github.ccob.bittrex4j.dao.Response;
import org.junit.Test;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class SocketTest {

    @Test
    public void connectTest() throws IOException, InterruptedException {

        System.setProperty("http.proxyHost", "localhost");
        System.setProperty("http.proxyPort", "3128");
//        System.setProperty("http.nonProxyHosts","bittrex.com|localhost");

        Properties prop = new Properties();
        prop.load(new FileInputStream("test_keys.properties"));
        BittrexExchange bittrexExchange = new BittrexExchange(0,prop.getProperty("apiKey"),prop.getProperty("apiSecret"));

        CountDownLatch latch = new CountDownLatch(1);
            bittrexExchange.onWebsocketError(e -> {
                System.out.println("onWebsocketError: " + e);
            });

        bittrexExchange.onWebsocketStateChange( state -> {
            System.out.println("reconnectSubscriptions.onWebsocketStateChange: "+ state.getNewState());
        });

        String marketName = "USDT-BTC";
        bittrexExchange.connectToWebSocket( () -> {
            try {
                System.out.println("connectToWebSocket - ok");

                bittrexExchange
                        .queryExchangeState(marketName, exchangeState -> {
                            System.out.println("queryExchangeState - " + exchangeState.getMarketName());
                            bittrexExchange.subscribeToExchangeDeltas(marketName, b -> System.out.println("subscribeToExchangeDeltas - " + b));
                        });

                bittrexExchange.onUpdateExchangeState(updateExchangeState -> {
                    if (marketName.equals(updateExchangeState.getMarketName())) {
                        System.out.println("updateExchangeState - " + updateExchangeState.getMarketName());
                        bittrexExchange.disconnectFromWebSocket();
                        latch.countDown();
                    }
                });

            } catch (Exception e) {
                System.out.println("connectToWebSocket"+ e);
            }
        });

        latch.await(15, TimeUnit.SECONDS);
        assertThat(latch.getCount(), is(0L));

        System.out.println("connectTest - " + (latch.getCount() == 0));
    }
}
