
DKPro Statistics is a collection of open-licensed statistical tools
written in Java. The software library is divided into the following modules:

* DKPro Agreement (dkpro-statistics-agreement) is a module for computing 
  multiple inter-rater agreement measures using a shared interface and data 
  model. Based on this model, the software allows for analyzing coding 
  (i.e., assigning categories to fixed items) and unitizing studies 
  (i.e., segmenting the data into codable units). The software has been 
  described in our COLING 2014 demo paper (see below). 

* DKPro Correlation (dkpro-statistics-correlation) is a module for 
  computing correlation and association measures. It is currently 
  under construction. 

* DKPro Significance (dkpro-statistics-significance) is a module for 
  assessing statistical significance. It is currently under construction. 


License and Availability
------------------------

The latest version of DKPro Statistics is available via Maven Central. 
If you use Maven as your build tool, then you can add DKPro Statistics 
as a dependency in your pom.xml file:

<dependency>
   <groupId>de.tudarmstadt.ukp.dkpro.statistics</groupId>
   <artifactId>dkpro-statistics</artifactId>
   <version>2.0.0</version>
</dependency>

In addition to that, you can add each of the modules described above 
separately (e.g., artifactId dkpro-statistics-agreement).

DKPro Statistics is available as open source software under the 
Apache License 2.0 (ASL). The software thus comes "as is" without any 
warranty (see license text for more details).


Publications and Citation Information
-------------------------------------

A more detailed description of DKPro Statistics is available in our 
scientific articles:

* Christian M. Meyer, Margot Mieskes, Christian Stab, and Iryna Gurevych: 
  DKPro Agreement: An Open-Source Java Library for Measuring Inter-Rater 
  Agreement, in: Proceedings of the 25th International Conference on 
  Computational Linguistics (COLING), (to appear), August 2014. 
  Dublin, Ireland.
  <https://www.ukp.tu-darmstadt.de/publications/details/?no_cache=1&tx_bibtex_pi1%5Bpub_id%5D=TUD-CS-2014-0863> 

Please cite our COLING 2014 paper if you use the software in your 
scientific work.


Project Background
------------------

Prior to being available as open source software, DKPro Statistics has been 
a research project at the Ubiquitous Knowledge Processing (UKP) Lab of 
the Technische Universität Darmstadt, Germany. The following people have 
mainly contributed to this project (in alphabetical order):

* Richard Eckart de Castilho
* Iryna Gurevych
* Christian M. Meyer
* Margot Mieskes
* Christian Stab
* Torsten Zesch


Project Homepage
----------------
* http://code.google.com/p/dkpro-statistics/
* https://www.ukp.tu-darmstadt.de/software/dkpro-statistics/
