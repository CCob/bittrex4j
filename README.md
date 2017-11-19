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

**Print Markets by Volume**

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

## TODO

### Missing APIs

* Withdrawals 
* Deposits
## Thanks

Thanks to platelminto for the java-bittrex project and dparlevliet for the node.bittrex.api where both have been used for inspiration.

## Donations

Donation welcome: 
  * BTC **1PXx92jaFZF92jLg64GF7APAsVCU4Tsogx**
  * UBQ **0xAa14EdE8541d1022121a39892821f271A9cdAF33**