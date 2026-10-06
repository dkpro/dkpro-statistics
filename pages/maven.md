---
layout: page-fullwidth
title: "Downloads"
permalink: "/maven/"
---

## Maven

DKPro Statistics is available via Maven Central. It requires Java 21 or higher (since version
2.3.0). To use it, add a dependency on the module(s) you need to your POM file:

{% highlight xml %}
<properties>
  <dkpro.statistics.version>{{site.data.releases[0].version}}</dkpro.statistics.version>
</properties>

<dependencies>
  <dependency>
    <groupId>org.dkpro.statistics</groupId>
    <artifactId>dkpro-statistics-agreement</artifactId>
    <version>${dkpro.statistics.version}</version>
  </dependency>
  <dependency>
    <groupId>org.dkpro.statistics</groupId>
    <artifactId>dkpro-statistics-correlation</artifactId>
    <version>${dkpro.statistics.version}</version>
  </dependency>
  <dependency>
    <groupId>org.dkpro.statistics</groupId>
    <artifactId>dkpro-statistics-significance</artifactId>
    <version>${dkpro.statistics.version}</version>
  </dependency>
</dependencies>
{% endhighlight xml %}

A full list of artifacts is available from [Maven Central][1].

## Sources

Get the sources from [GitHub](https://github.com/dkpro/dkpro-statistics/releases/tag/dkpro-statistics-{{ site.data.releases[0].version }}).

[1]: https://central.sonatype.com/search?q=g%3Aorg.dkpro.statistics
