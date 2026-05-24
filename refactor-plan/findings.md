# 发现与风险

## RabbitMQ 集成发现

- 早期 RabbitMQ 队列声明冲突主要来自队列参数不一致和历史残留队列。
- 项目引入 Actuator 时，`spring.rabbitmq.*` 会触发 Rabbit health check。
- 框架现在只在用户没有配置 `spring.rabbitmq.*` 时，才用 `rabbitmq.*` 补齐默认值。
- `server.msgBus` 是消息总线选择开关：
  - 默认 `mqtt`
  - `rabbitmq` 启用 RabbitMQ transport
- `MqttHandler#getRegisterMsg()` 存在职责泄漏：
  - 当前保留兼容。
  - 注册地址解析已收口到 `IaRegister.buildRegisterMsg(...)`。
- `sendToMqtt*` 等公共方法名仍刻意保留，避免破坏兼容性。
- RabbitMQ/MQTT demo 回归清单已文档化到 `refactor-plan/areas/rabbitmq-message-bus.md`。

## 剩余风险

- 部分公共类名仍包含 MQTT 语义，这是兼容性取舍。
- 目前没有自动化集成测试覆盖 MQTT/RabbitMQ 切换。
- 自动化集成测试仍作为 P2 后续任务处理。
- RabbitMQ 运行时仍建议通过真实 broker 重启、重复启动、队列自动过期等场景持续验证。
