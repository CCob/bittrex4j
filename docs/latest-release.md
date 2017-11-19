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
