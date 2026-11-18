# @Opera 函数调用验证

验证日期：2026-11-18。环境：JDK 21、Maven 3.9.11、Docker RabbitMQ 4.1，未启动 ZooKeeper。

本地基线从远端 `60c49a0` 按会话记录恢复无注册模式；本次验证针对恢复后的代码，不能作为此前已丢失本地提交 `4ef3933` 的哈希验证。

## 实测结果

提供方和调用方是两个独立 Java 进程，通信只经过 RabbitMQ。本次测试端口为 5678。

| 调用 | 结果 | 调用方退出码 |
| --- | --- | --- |
| `calculator.add(12, 30)` | `42` | 0 |
| `calculator.add(-7, 2)` | `-5` | 0 |
| `calculator.add(0, 0)` | `0` | 0 |
| `calculator.add(2147483647, 1)` | `MsgResultFailException`，业务状态 `EXCEPTION(12)`，原因 `ArithmeticException: integer overflow` | 1 |

同时确认：

- 调用方的接口对象是通过 `@Opera` 注入的 JDK 代理。
- 调用方的本地方法扫描结果是 `[]`，没有本地 CalculatorService 实现。
- 提供方日志逐次记录上述四组参数的远端执行。
- 可执行 jar 内不含 `zookeeper`、`yyds-register`、`yyds-balancer` 依赖。
- 正常调用以 `int` 返回，而非把 `MsgResult` 或 JSON 字符串交给业务调用方。
- 回归原 `IaOpera` 问候调用，得到 `RPC result: [function-provider] 你好，张三`，调用方退出码为 0。

## 重现命令

先按 [README](README.md) 构建并启动 broker 和提供方，然后在另一个终端运行：

```bash
java -jar examples/rabbitmq-only-rpc/target/rabbitmq-only-rpc-demo-1.0.0-SNAPSHOT.jar \
  --spring.profiles.active=function-consumer --spring.rabbitmq.port=5678 \
  --demo.left=12 --demo.right=30
```

替换最后两个参数即可重现其他三组输入。正常输出为 `FUNCTION result: add(12, 30) = 42`；溢出案例预期以异常和非零状态退出。

本轮是实际进程间的函数调用验证，不代表生产负载测试、quorum 集群故障测试或前次全部测试套件的重新运行。
