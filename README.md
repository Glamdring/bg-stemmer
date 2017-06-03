# BG-Stemmer

This is an _experimental_ stemmer for Bulgarian. The two alternatives are the light <a href="http://members.unine.ch/jacques.savoy/Papers/BUIR.pdf">default rule-based Lucene stemmer</a>, and <a href="http://lml.bas.bg/~nakov/bulstem/">Preslav Nakov's BulStem</a>, which is an inflectional stemmer.

This one relies on initially loading all word forms into a trie, and then for each word fetching the corresponding base form. It is less space-efficient than the other two which rely just on rules, but benchmarks show that it is significantly faster than BulStem and on par with the default Lucene stemmer.

The dictionary alongside with the affixation rules are taken from OpenOffice.    

## Integrating with Solr

You need to simply add the jar file on the classpath and add the following in your Solr configuration

```
<filter class="solr.BulgarianStemFilterFactory"/>
```

## Integrating with ElasticSearch

TODO