# 决策记录

## 消息总线选择

- 使用 `server.msgBus` 选择消息总线实现。
- 当前支持：
  - `mqtt`
  - `rabbitmq`
- 未识别的值会回退到 MQTT，并输出 warning 日志。

## 兼容性策略

- 本次集成不修改公共 `MsgHandler` 方法。
- `sendToMqtt*` 方法名暂时保留，避免影响已有用户代码。
- 先通过内部中立命名和支撑类降低 MQTT 语义依赖：
  - `MessageBusTransport`
  - `MessageBusListener`
  - `MessageBusOperator`
  - `MessageBusSenderSupport`
  - `MessageBusReceiverSupport`

## RabbitMQ 配置

- 用户显式配置 `spring.rabbitmq.*` 时优先使用它。
- `rabbitmq.*` 作为框架级配置和 fallback 来源。
- RabbitMQ payload 阈值独立配置：
  - `rabbitmq.payload.limit`
  - fallback：`messageBus.payload.limit`

## 注册地址职责

- `register.url` 的解析职责放到 `IaRegister`。
- `MqttHandler#getRegisterMsg()` 仅作为已废弃的兼容桥接保留。

