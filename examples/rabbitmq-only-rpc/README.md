# RabbitMQ 无注册中心 Demo

本次函数调用的环境、输入输出和验证边界见 [验证记录](functional-verification.md)。

同一个 jar 分别启动提供方和调用方。只需要 RabbitMQ，不启动 ZooKeeper；POM 也排除了 `yyds-register`、`yyds-balancer`。

## 构建与启动提供方

需要完整 JDK 21、Maven、Docker。在仓库根目录执行：

```bash
mvn -Drevision=3.5.27-rabbitmq-only-SNAPSHOT -DskipTests install
mvn -f examples/rabbitmq-only-rpc/pom.xml package

docker run --rm -d --name mystox-rpc-demo -p 127.0.0.1:5672:5672 rabbitmq:4.1-alpine
docker exec mystox-rpc-demo rabbitmq-diagnostics -q ping

java -jar examples/rabbitmq-only-rpc/target/rabbitmq-only-rpc-demo-1.0.0-SNAPSHOT.jar \
  --spring.profiles.active=provider --demo.instance=provider-A
```

broker 就绪时 ping 输出 `Ping succeeded`。提供方出现 `Rabbit RPC without registry started` 后保持运行，再开一个终端启动调用方。修改源码后先停止所有提供方，再重新打包和启动。

## 函数式调用：`@Opera` 接口代理

共享接口 [CalculatorApi.java](src/main/java/demo/rabbitmq/CalculatorApi.java)：

```java
@Register
public interface CalculatorApi {
    @OperaCode(code = "demo.add")
    int add(int left, int right);
}
```

提供方 [CalculatorService.java](src/main/java/demo/rabbitmq/CalculatorService.java) 实现该接口，用 `@Service` 和 `@Profile("provider")` 创建 bean，方法执行 `Math.addExact(left, right)`。

调用方 [FunctionCaller.java](src/main/java/demo/rabbitmq/FunctionCaller.java) 注入接口代理，像本地方法一样调用：

```java
@Opera
private CalculatorApi calculator;

int result = calculator.add(12, 30); // 真实远程调用，返回 int 42
```

启动函数调用方：

```bash
java -jar examples/rabbitmq-only-rpc/target/rabbitmq-only-rpc-demo-1.0.0-SNAPSHOT.jar \
  --spring.profiles.active=function-consumer
```

预期输出：

```text
FUNCTION result: add(12, 30) = 42
```

可通过参数覆盖输入：

```bash
java -jar examples/rabbitmq-only-rpc/target/rabbitmq-only-rpc-demo-1.0.0-SNAPSHOT.jar \
  --spring.profiles.active=function-consumer --demo.left=-7 --demo.right=2
```

预期输出 `FUNCTION result: add(-7, 2) = -5`。`0 + 0` 返回 `0`。

提供方日志包含 `FUNCTION executed remotely`。调用方没有本地 `CalculatorService` bean，并检查注入对象确实是 JDK 接口代理；消费者的本地方法扫描结果为 `[]`。调用实际经过 RabbitMQ 共享请求队列和独享回复队列。

整数溢出时（例如 `--demo.left=2147483647 --demo.right=1`），提供方抛出 `ArithmeticException`，调用方通过代理收到 `MsgResultFailException` 并以非零状态退出。接口方法返回业务类型，业务错误通过异常传播。

## 原来的 `IaOpera` 调用

```bash
java -jar examples/rabbitmq-only-rpc/target/rabbitmq-only-rpc-demo-1.0.0-SNAPSHOT.jar \
  --spring.profiles.active=consumer --demo.name=张三
```

代码为 `iaOpera.opera("demo.hello", name)`，输出 `RPC result: [provider-A] 你好，张三`。

可另开终端启动 `--spring.profiles.active=provider --demo.instance=provider-B`，再用 `--spring.profiles.active=consumer --demo.calls=6` 验证两个提供方分担请求。

## 配置与结束

配置见 [application.yml](src/main/resources/application.yml)。关键值为 `register.type=none`、`server.msg-bus=rabbitmq`；双方的组、契约版本、vhost 和请求交换机需一致。连接可通过 `RABBITMQ_HOST/PORT/USER/PASSWORD/VHOST` 覆盖。

调用方执行完自动退出，提供方用 Ctrl+C 停止。broker 由上面命令创建时，用 `docker stop mystox-rpc-demo` 清理。

无匹配队列时会报无路由；队列存在但提供方都离线时会超时。超时不代表业务未执行，写操作仍需业务幂等。本例的加法和问候均无写入副作用。
