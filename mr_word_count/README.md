Map Reduce Wordcount Quickstart
==========================

This quickstart demonstrates a *map reduce* job running on *four nodes* in *Java SE*.

In theis example Map Reduce is used to count the number of words in the complete works of Shakespeare. This is not a strictly accurate count because the lines are tokenized into strings. Consequently, the following are counted as different words - 
  methinks;
  methinks,
  methinks.
  methinks
  Methinks

Also the licensing agreement is in the target text.

The example can be deployed using Maven from the command line or from Eclipse using
JBoss Tools.

For more information, including how to set up Maven or JBoss Tools in Eclipse, 
refer to the [Getting Started Guide](https://docs.jboss.org/author/display/ISPN/Getting+Started+Guide+-+Clustered+Cache+in+Java+SE).

To compile, type `mvn clean compile dependency:copy-dependencies -DstripVersion`, 
and then, to run, `java -cp target/classes:target/dependency/* Node0` in one terminal, `java -cp target/classes:target/dependency/* Node1` in another, `java -cp target/classes:target/dependency/* Node2` in another and `java -cp target/classes:target/dependency/* Node3` in another.
Alternatively run `./runNodes.sh`

TODO CREATE! => If using Windows you will need to use `runNodes.bat`

The cluster will form and start to read in the complete works. This will take a few minutes and create about 120000 keys in the distributed in-memory cache. 

Once read the count will start and produce output to the screen following the pattern:

<word1> count: <number1>
<word2> count: <number2>
<word3> count: <number3>
<word4> count: <number4>
.
.
.

