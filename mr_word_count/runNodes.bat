rem starts 4 nodes in seperate cmd windows


start "node0" "java -Djgroups.bind_addr=127.0.0.1 -Djava.net.preferIPv4Stack=true -cp target/classes/;target/dependency/* org.infinispan.quickstart.mrwordcount.distribution.Node0"
start "node1" "java -Djgroups.bind_addr=127.0.0.1 -Djava.net.preferIPv4Stack=true -cp target/classes/;target/dependency/* org.infinispan.quickstart.mrwordcount.distribution.Node1"
start "node2" "java -Djgroups.bind_addr=127.0.0.1 -Djava.net.preferIPv4Stack=true -cp target/classes/;target/dependency/* org.infinispan.quickstart.mrwordcount.distribution.Node2"
start "node3" "java -Djgroups.bind_addr=127.0.0.1 -Djava.net.preferIPv4Stack=true -cp target/classes/;target/dependency/* org.infinispan.quickstart.mrwordcount.distribution.Node3"
