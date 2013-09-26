Map Reduce Wordcount Quickstart
==========================

This quickstart demonstrates a *map reduce* job running on *four nodes* in *Java SE*.

In this example Map Reduce is used to count the number of words in the complete works of Shakespeare. This is not a strictly accurate count because the lines are tokenized into strings. Consequently, the following are counted as different words - 
  methinks;
  methinks,
  methinks.
  methinks
  Methinks

Also the Gutenburg licensing agreement is include in the target text.

The example can be deployed using Maven from the command line or from Eclipse using
JBoss Tools.

For more information, including how to set up Maven or JBoss Tools in Eclipse, 
refer to the [Getting Started Guide](https://docs.jboss.org/author/display/ISPN/Getting+Started+Guide+-+Clustered+Cache+in+Java+SE).

To compile, type `mvn clean compile dependency:copy-dependencies -DstripVersion`, 
and then, to run, `java -Djgroups.bind_addr=127.0.0.1 -Djava.net.preferIPv4Stack=true -cp target/classes/:target/dependency/* org.infinispan.quickstart.mrwordcount.distribution.Node0` in one terminal,
`java -Djgroups.bind_addr=127.0.0.1 -Djava.net.preferIPv4Stack=true -cp target/classes/:target/dependency/* org.infinispan.quickstart.mrwordcount.distribution.Node1` in another, 
`java -Djgroups.bind_addr=127.0.0.1 -Djava.net.preferIPv4Stack=true -cp target/classes/:target/dependency/* org.infinispan.quickstart.mrwordcount.distribution.Node2` in another and 
`java -Djgroups.bind_addr=127.0.0.1 -Djava.net.preferIPv4Stack=true -cp target/classes/:target/dependency/* org.infinispan.quickstart.mrwordcount.distribution.Node3` in another.

If using Windows you will need to replace `:` with `;` on the command line.

Alternatively run `./runNodes.sh` or `runNodes.bat`


The cluster will form and start to read in the complete works. This will take a few minutes and create about 120000 keys in the distributed in-memory cache. 

Once the text is read the count will start and produce output to the screen with the following the pattern:

    word1 count: number1
    word2 count: number2
    word3 count: number3
    word4 count: number4
    .
    .
    .

