<a name="documentr_top"></a>

# bittrex4j

![Travis CI Status](https://travis-ci.org/CCob/bittrex4j.svg?branch=master) [![codecov](https://codecov.io/gh/CCob/bittrex4j/branch/master/graph/badge.svg)](https://codecov.io/gh/CCob/bittrex4j)



> Java library for accessing the Bittrex Web API's and Web Sockets.  It currently uses a mix of v1.1 and the undocumented v2 API. 


Where can I get the latest release?
-----------------------------------
Currently snapshot builds a published to the Sonatype Nexus repository.  You need to enable The Sonatype snapshot repository, for example:

```xml
  <repositories>
      <repository>
          <id>sonatype-snapshots</id>
          <url>https://oss.sonatype.org/content/repositories/snapshots/</url>
          <snapshots>
              <enabled>true</enabled>
          </snapshots>
      </repository>
  </repositories>
```
Then add the latest bittrex4j snapshot to your dependencies section:

```xml
<dependency>
  <groupId>comb.github.ccob</groupId>
  <artifactId>bittrex4j</artifactId>
  <version>0.0.1-SNAPSHOT</version>
</dependency>
```

Once the API has had time to stabilise I will publish it to maven central
##Examples

**Print Markets by Volume (REST API)**

```java
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

```
**Show Realtime Fills(WebSocket API)**

```java
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
```
**Show Deposit History for BTC (Authenticated REST API)**

```java
package com.github.ccob.bittrex4j.samples;

import com.github.ccob.bittrex4j.BittrexExchange;
import com.github.ccob.bittrex4j.dao.Response;
import com.github.ccob.bittrex4j.dao.WithdrawalDeposit;

import java.io.IOException;
import java.util.Arrays;

public class PrintDepositHistory {

    /* Replace apikey and secret values below */
    private static final String apikey = "*";
    private static final String secret = "*";

    public static void main(String[] args) throws IOException {

        BittrexExchange bittrexExchange = new BittrexExchange(apikey,secret);

        Response<WithdrawalDeposit[]> markets = bittrexExchange.getDepositHistory("BTC");

        if(!markets.isSuccess()){
            System.out.println("Failed to fetch deposit history with error " + markets.getMessage());
        }

        Arrays.stream(markets.getResult())
                .forEach(deposit -> System.out.println(String.format("Address %s, Amount %02f",deposit.getAddress(),deposit.getAmount())));

    }
}

```

## Thanks

Thanks to platelminto for the java-bittrex project and dparlevliet for the node.bittrex.api where both have been used for inspiration.

## Donations

Donation welcome: 
  * BTC **1PXx92jaFZF92jLg64GF7APAsVCU4Tsogx**
  * UBQ **0xAa14EdE8541d1022121a39892821f271A9cdAF33**