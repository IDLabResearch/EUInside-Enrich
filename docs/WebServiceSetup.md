# Setting up an Enrichment Tool Web Service

## Building the source

Building the source code requires a Java JDK 8 (or newer) and Apache Maven 3.
Build / create a jar with the command:

    mvn package -DskipTests
   
This creates 2 jar files in the `target` directory: `enrich-<version>.jar` and `enrich-all-<version>.jar`. The first
contains the class files of the enrichment tool whilst the latter also includes all dependencies.

## Option 1: Using a (remote) SPARQL endpoint

The fastest way to get the service running, is to let it query a (remote) SPAQL endpoint, e.g., DBPedia.
 
* Open the configuration file. Or copy `enrich.properties` from the `Resources` directory, and edit the copy.
* Uncomment `dataset = sparql`, and make sure other `dataset = ` lines are commented.
* Fill in the URI to a SPAQL endpoint in the property `dataset.sparql.endpoint = `. E.g., to use the public DBpedia
endpoint, fill in `dataset.sparql.endpoint = http://dbpedia.org/sparql`
* Now [run the service](#run)

*Note:* If you do *not* use an [index](#index) (which is perfectly fine), the SPARQL endpoint *must* be backed by a
[Virtuoso](http://virtuoso.openlinksw.com/) instance. This is because standard SPARQL only supports literal search.
Substring search is supported by Virtuoso, which is also the backend of DBpedia. If you use an index, the SPARQL
endpoint can be backed by any SPARQL complient server.

## Option 2: Using a HDT file

The Enrichment Tool also supports [HDT files](http://www.rdfhdt.org/). The advantage is support for fast triple search
(which is extensively used by the tool), while keeping disk usage for data storage relatively low. And it doesn't 
require network connection to another server or process. A disadvantage might be that the data is a snapshot; e.g.,
recent updates in Wikipedia (and thus DBpedia) might not be included in the HDT file.

* Grab a HDT file as data source. The English DBpedia data file for instance can be found 
[here](http://www.rdfhdt.org/datasets/).
Or you can create your own.
* Open the configuration file. Or copy `enrich.properties` from the `Resources` directory, and edit the copy.
* Uncomment `dataset = hdt`, and make sure other `dataset = ` lines are commented.
* Fill in the path to the HDT file in the property `dataset.hdt.file = `. E.g., to use the English DBPedia file, fill in
`dataset.hdt.file = DBPedia-3.9-en.hdt`.
* Now [run the service](#run)

## Index (optional)<a name="index"/>

The Enrichment Tool benefits from the use of a [Lucene index](http://lucene.apache.org/) made from your data source(s). 
It affords real fuzzy searching on the data set, resulting in broader matches. It also greatly speeds up the search
process. It doesn't matter wheter you combine it with using a SPARQL endpoint, or with using a HDT file.

* [Create](#lucene) an index directory, if you haven't done so yet.
* Open the configuration file. Or copy `enrich.properties` from the `Resources` directory, and edit the copy.
* Set `index.enabled = true`.
* Fill in the path to the Lucene index directory in the property `index.label.dir = `.
* Now [run the service](#run)

## Running the service <a name="run"/>

The next script is an *example* bash script to start the service. Modify it to work on your own system. The most 
important thing is the last line, where the right class is invoked with the right parameters.

    #!/bin/bash

    # Point this to a Java 8 jre
    JAVA_JRE=~/jdk1.8.0_05/jre

    # The port the server listens to
    PORT=8915

    # The configuration properties file to use
    CONFIG=enrich.properties

    $JAVA_JRE/bin/java -Xmx1000m -cp enrich-all-1.0-SNAPSHOT.jar be.ugent.mmlab.europeana.webservice.server.WebServer -p $PORT -c $CONFIG
    
## Creating a Lucene index <a name="lucene"/>

To speed up searching, the [label](http://www.w3.org/2000/01/rdf-schema#label) triples of a data set can be indexed.
These triples map a concept to a resource, e.g.:

    <http://dbpedia.org/resource/Ancient_Egypt> <http://www.w3.org/2000/01/rdf-schema#label> "Ancient Egypt"@en .

The class `be.ugent.mmlab.europeana.tools.LuceneIndexWriter` takes a (compressed) RDF file in N-triples format as input
and writes the output to `tmp/luceneIndex/merged`. The `merged` subdirectory is the Lucene index directory referred to
earlier in this document.

An example bash script to create an index from DBpedia labels:

    #!/bin/bash

    # Point this to a Java 8 jre
    JAVA_JRE=~/jdk1.8.0_05/jre
    
    # The triple file to index
    LABEL_FILE=enrich.properties

    $JAVA_JRE/bin/java -cp enrich-all-1.0-SNAPSHOT.jar be.ugent.mmlab.europeana.tools.LuceneIndexWriter $LABEL_FILE
