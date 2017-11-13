# bittrex4j ![Travis CI Status](https://travis-ci.org/CCob/bittrex4j.svg?branch=master) [![codecov](https://codecov.io/gh/CCob/bittrex4j/branch/master/graph/badge.svg)](https://codecov.io/gh/CCob/bittrex4j)

Java library for accessing the Bittrex Web API's and Web Sockets.  It currently uses a mix of v1.1 and the undocumented v2 API.    

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

Once the API has had time to stabalise I will publish it to central repository also

## TODO

### Missing APIs

* Withdrawals 
* Deposits
* ~~Single order~~

### Web sockets

* Live General market notifications
* Live Market subscription data.

## Thanks

Thanks to platelminto for the java-bittrex project and dparlevliet for the node.bittrex.api where both have been used for inspiration.

## Donations

Donation welcome: BTC **1PXx92jaFZF92jLg64GF7APAsVCU4Tsogx**
