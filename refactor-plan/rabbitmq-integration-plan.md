# RabbitMQ 集成计划

## 阶段进度

| 阶段 | 状态 | 提交 / 备注 |
| --- | --- | --- |
| 1. RabbitMQ 基础通讯 | 已完成 | `3cf1493` |
| 2. 发送侧抽象 | 已完成 | `3f3a3ca` |
| 3. Listener 接收抽象 | 已完成 | `0ada816` |
| 4. 拆分 RabbitMQ Transport | 已完成 | `f681ca2` |
| 5. Receiver support 抽象 | 已完成 | `e7e187a` |
| 6. Sender support 与 MQTT transport facade | 已完成 | `a07ed34` |
| 7. 配置文档与启动日志 | 已完成 | `8beba62` |
| 8. 内部命名清理 | 已完成 | 已验证，待提交 |
| 9. 最终回归清单 | 可选 | demo 回归已完成 |

## 当前任务

提交已完成的内部命名清理：

- `mqttSenderImpl` -> `messageBusOperator`
- `mqttHandlerAck` -> `ackTopicHandler`
- `mqttHandlerImpl` -> `requestTopicHandler`
- `mqttReceiver` -> `messageBusReceiver`

建议提交信息：

```text
refactor: clean up internal message bus naming
```

## 验证

最近一次验证已通过：

```bash
mvn -pl yyds-mqtt/yyds-mqtt-starter,yyds-iarpc-starter -am -DskipTests clean install
```

用户已完成 demo 回归。

## 剩余事项

- 提交当前命名清理和计划文件。
- 可选：补一份简短回归清单。
- 未来大版本任务：考虑 `sendToMqtt*` 公共 API 命名清理。
