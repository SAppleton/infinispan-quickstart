Map Reduce Word Count Quickstart
==========================

This quickstart demonstrates *map reduce* running on *three nodes* in 
*Java SE*. One of the three nodes is the controller node which populates the cache
and starts the word count job.

In this example Map Reduce is used to count the number of words in the complete works of Shakespeare. This is not a strictly accurate count because the lines are tokenized into strings. Consequently, the following are counted as different words - 
  methinks;
  methinks,
  methinks.
  methinks
  Methinks

The Gutenburg licensing agreement is included in the target text.

The example can be deployed using Maven from the command line or from Eclipse using
JBoss Tools.

For more information, including how to set up Maven or JBoss Tools in Eclipse, 
refer to the [Getting Started Guide](https://docs.jboss.org/author/display/ISPN/Getting+Started+Guide+-+Clustered+Cache+in+Java+SE).

* Compile the application by running `mvn clean compile dependency:copy-dependencies -DstripVersion`

* To try first run two *distributed* cache nodes, by running the following commands in separated terminals:
    * `java -cp "target/classes:target/dependency/*" org.infinispan.quickstart.clusteredcache.Node B`
    * `java -cp "target/classes:target/dependency/*" org.infinispan.quickstart.clusteredcache.Node C`

* Next run the controller node by typing the following command in another separated terminals
    * `java -cp "target/classes:target/dependency/*" org.infinispan.quickstart.clusteredcache.Node -c A`


It will take a few minutes and create about 120000 keys in the distributed in-memory cache. 

Once the text is read the count will start and produce output to the screen (and the results.txt file) with the following the pattern:

    word1 count: number1
    word2 count: number2
    word3 count: number3
    word4 count: number4
    .
    .
    .
