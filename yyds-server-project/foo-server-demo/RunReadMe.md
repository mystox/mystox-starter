`
serverHost=192.168.0.234 && serverPort=18203 && \
docker run --name foo-demo5 -d \
-e JAVA_OPTS='-server -Xmx1g -Xms1g -XX:MetaspaceSize=64m -verbose:gc -verbose:sizes -XX:+UseG1GC -XX:MaxGCPauseMillis=50 -XX:+UnlockDiagnosticVMOptions -XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=/ -XX:+PrintGCDetails -XX:+PrintGCTimeStamps -XX:+PrintGCDateStamps -XX:+PrintTenuringDistribution -Xloggc:/opt/gc.log -XX:+UseGCLogFileRotation -XX:NumberOfGCLogFiles=5 -XX:GCLogFileSize=20M -Djava.io.tmpdir=/tmp ' \
-e server.port=$serverPort \
-e spring.cloud.zookeeper.discovery.instance-host=$serverHost \
192.168.0.234:23760/foo-server-demo:1.0.0
`

查看日志
`docker logs -f --tail 10 cmcc-user-demo`

容器自启动

`docker update --restart=always cmcc-user-demo`

serverHost=172.16.5.27 && serverPort=18203 && \
docker service create --name foo-demo --replicas 4 -d \
-p $serverPort:$serverPort
-e JAVA_OPTS='-server -Xmx1g -Xms1g -XX:MetaspaceSize=64m -verbose:gc -verbose:sizes -XX:+UseG1GC -XX:MaxGCPauseMillis=50 -XX:+UnlockDiagnosticVMOptions -XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=/ -XX:+PrintGCDetails -XX:+PrintGCTimeStamps -XX:+PrintGCDateStamps -XX:+PrintTenuringDistribution -Xloggc:/opt/gc.log -XX:+UseGCLogFileRotation -XX:NumberOfGCLogFiles=5 -XX:GCLogFileSize=20M -Djava.io.tmpdir=/tmp ' \
-e server.port=$serverPort \
-e server.host=$serverHost \
172.16.5.27:23760/foo-server-demo:2.0.4