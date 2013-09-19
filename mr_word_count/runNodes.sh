#!/bin/bash
exec java -Djgroups.bind_addr=127.0.0.1 -Djava.net.preferIPv4Stack=true -cp target/classes/:target/dependency/* org.infinispan.quickstart.mrwordcount.distribution.Node0 &
exec java -Djgroups.bind_addr=127.0.0.1 -Djava.net.preferIPv4Stack=true -cp target/classes/:target/dependency/* org.infinispan.quickstart.mrwordcount.distribution.Node1 &
exec java -Djgroups.bind_addr=127.0.0.1 -Djava.net.preferIPv4Stack=true -cp target/classes/:target/dependency/* org.infinispan.quickstart.mrwordcount.distribution.Node2 &
exec java -Djgroups.bind_addr=127.0.0.1 -Djava.net.preferIPv4Stack=true -cp target/classes/:target/dependency/* org.infinispan.quickstart.mrwordcount.distribution.Node3 &

