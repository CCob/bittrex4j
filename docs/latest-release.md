Where can I get the latest release?
-----------------------------------

bittrex4j is published on the maven central repository and can be imported into you project using the following maven coordinates.

```xml
<dependency>
  <groupId>com.github.ccob</groupId>
  <artifactId>bittrex4j</artifactId>
  <version>1.0.6</version>
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
