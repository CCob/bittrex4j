package com.github.ccob.bittrex4j.samples;

import com.github.ccob.bittrex4j.BittrexExchange;
import com.github.ccob.bittrex4j.dao.Response;
import com.github.ccob.bittrex4j.dao.WithdrawalDeposit;

import java.io.IOException;
import java.util.Arrays;

public class PrintDepositHistory {

    public static void main(String[] args) throws IOException {

        BittrexExchange bittrexExchange = new BittrexExchange("*","*");

        Response<WithdrawalDeposit[]> markets = bittrexExchange.getDepositHistory("UBQ");

        if(!markets.isSuccess()){
            System.out.println("Failed to fetch deposit history with error " + markets.getMessage());
        }

        Arrays.stream(markets.getResult())
                .forEach(deposit -> System.out.println(String.format("Address %s, Amount %02f",deposit.getAddress(),deposit.getAmount())));

    }
}
