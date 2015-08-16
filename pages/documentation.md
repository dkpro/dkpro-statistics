---
layout: page-fullwidth
title: "Getting started"
permalink: "/documentation/"
---

DKPro Agreement
---------------

Before working with DKPro Agreement, we recommend to get a basic understanding of inter-rater agreement and the various metrics for quantifying agreement. [Christian M. Meyer](http://www.ukp.tu-darmstadt.de/people/meyer) has composed a brief tutorial including a number of literature pointers:

* [A Brief Tutorial on Inter-Rater Agreement](/dkpro-statistics/inter-rater-agreement-tutorial.pdf)

Once you have this basic understanding of inter-rater agreement, add DKPro Agreement as a dependency to your project. If you use Maven, you can just add the dependency as specified on the [overview page](/dkpro-statistics/) and the corresponding JAR will be downloaded automatically. If you prefer to work without Maven, please click on the download link in the top menu and get the JAR file for DKPro Agreement from Maven Central.

DKPro Agreement suggests a simple workflow with three steps in order to measure the inter-rater agreement of your data: First, represent your data in a way that DKPro Agreement can understand. This involves the fundamental decision whether your annotation experiment has a so-called *coding setup* or a *unitizing setup*. Second, chose one or more suitable inter-rater agreement measures and compute the scores. Third, interpret their values and analyze the disagreement. DKPro Agreement provides a number of visualizations and diagnostic devices to help you with this important step. The following slides explain the three steps in more detail, including short code snippets:

* [Getting Started with DKPro Agreement](/dkpro-statistics/dkpro-agreement-tutorial.pdf)

Besides these tutorial slides, you may want to take a look at and cite our [COLING 2014 paper](http://anthology.aclweb.org/C/C14/C14-2023.pdf), which gives an overview of the implementation and its evaluation. Multiple colleagues found it very helpful to take a look at some of the many test cases in DKPro Agreement. They provide quite simple examples and thus can help you with writing the necessary code snippets more easily. Last, but not least, check out the Javadoc documentation, which also references the original papers of each inter-rater agreement measure.


Other modules
-------------

The documentation for DKPro Correlation and DKPro Significance is currently under construction.
