<a name="documentr_top"></a>

# bittrex4j


![bittrex4j Logo](docs/bittrex4j.png)  
![Travis CI Status](https://travis-ci.org/CCob/bittrex4j.svg?branch=master) [![codecov](https://codecov.io/gh/CCob/bittrex4j/branch/master/graph/badge.svg)](https://codecov.io/gh/CCob/bittrex4j)  [![Maven metadata URI](https://img.shields.io/maven-metadata/v/http/central.maven.org/maven2/com/github/ccob/bittrex4j/maven-metadata.xml.svg)]()



> Java library for accessing the Bittrex Web API's and Web Sockets.  It currently uses a mix of v1.1 and the undocumented v2 API. 


Where can I get the latest release?
-----------------------------------

bittrex4j is published on the maven central repository and can be imported into you project using the following maven coordinates.

```xml
<dependency>
  <groupId>com.github.ccob</groupId>
  <artifactId>bittrex4j</artifactId>
  <version>1.0.4</version>
</dependency>
```

Snapshot builds are also available and published to the Sonatype Nexus repository.  You need to enable The Sonatype snapshot repository, for example:

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
Then add the latest bittrex4j snapshot to your dependencies section

## Examples

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
import org.java_websocket.WebSocketImpl;

import java.io.IOException;
import java.util.Arrays;

public class ShowRealTimeFills {

    public static void main(String[] args) throws IOException {

        System.out.println("Press any key to quit");

        BittrexExchange bittrexExchange = new BittrexExchange();

        bittrexExchange.onUpdateSummaryState(exchangeSummaryState -> {
            if (exchangeSummaryState.getDeltas().length > 0) {

                Arrays.stream(exchangeSummaryState.getDeltas())
                        .filter(marketSummary -> marketSummary.getMarketName().equals("BTC-BCC") || marketSummary.getMarketName().equals("BTC-ETH") )
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

            System.out.println(String.format("N: %d, %02f volume across %d fill(s) for %s",updateExchangeState.getNounce(),
                    volume, updateExchangeState.getFills().length, updateExchangeState.getMarketName()));
        });

        bittrexExchange.connectToWebSocket( () -> {
            bittrexExchange.subscribeToExchangeDeltas("BTC-ETH", null);
            bittrexExchange.subscribeToExchangeDeltas("BTC-BCC",null);
            bittrexExchange.subscribeToMarketSummaries(null);
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

        BittrexExchange bittrexExchange = new BittrexExchange(5, apikey,secret);

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
  * ETH **0xC7DC0CADbb497d3e11379c7A2aEE8b08bEc9F30b**   
