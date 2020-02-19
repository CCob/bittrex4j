package com.github.ccob.bittrex4j.samples;

import com.github.ccob.bittrex4j.BittrexExchange;
import com.github.ccob.bittrex4j.dao.MarketSummaryResult;
import com.github.ccob.bittrex4j.dao.Response;

import java.io.IOException;
import java.util.Arrays;
import java.util.Comparator;

import static java.util.Comparator.comparing;

public class PrintMarketsByVolume {

    public static void main(String[] args) throws IOException {

        BittrexExchange bittrexExchange = new BittrexExchange();

        Response<MarketSummaryResult[]> markets = bittrexExchange.getMarketSummaries();

        if(!markets.isSuccess()){
            System.out.println("Failed to fetch available markets with error " + markets.getMessage());
        }

        System.out.println(String.format("Fetched %d available markets",markets.getResult().length));

        Arrays.stream(markets.getResult())
                .sorted(comparing(m -> m.getSummary().getBaseVolume(),Comparator.reverseOrder()))
                .forEachOrdered(m -> System.out.println(String.format("Market Name: %s, Volume %s",m.getMarket().getMarketName(),m.getSummary().getBaseVolume())));

    }
}
