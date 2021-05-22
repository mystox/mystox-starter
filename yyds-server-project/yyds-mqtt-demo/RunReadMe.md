`
serverHost=192.168.0.234 && serverPort=18204 && \
docker run --name mqtt-demo --net=host  -d \
-p $serverPort:$serverPort \
-e JAVA_OPTS='-server -Xms128m -Xmx512m ' \
-e server.port=$serverPort \
-e server.host=$serverHost \
-e spring.cloud.zookeeper.discovery.instance-host=$serverHost \
192.168.0.234:23760/yyds-mqtt-demo:1.0.0
`

查看日志
`docker logs -f --tail 10 cmcc-user-demo`

容器自启动

`docker update --restart=always cmcc-user-demo`

serverHost=172.16.5.27 && serverPort=18203 && \
docker service create --name mqtt-demo --replicas 4 -d \
-p $serverPort:$serverPort \
-e JAVA_OPTS='-server -Xmx1g -Xms1g -XX:MetaspaceSize=64m -verbose:gc -verbose:sizes -XX:+UseG1GC -XX:MaxGCPauseMillis=50 -XX:+UnlockDiagnosticVMOptions -XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=/ -XX:+PrintGCDetails -XX:+PrintGCTimeStamps -XX:+PrintGCDateStamps -XX:+PrintTenuringDistribution -Xloggc:/opt/gc.log -XX:+UseGCLogFileRotation -XX:NumberOfGCLogFiles=5 -XX:GCLogFileSize=20M -Djava.io.tmpdir=/tmp ' \
-e server.port=$serverPort \
-e server.host=$serverHost \
172.16.5.27:23760/yyds-mqtt-demo:2.0.4