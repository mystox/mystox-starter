# RabbitMQ 消息总线集成

## 目标

让 `yyds-mqtt` 可以通过 MQTT 或 RabbitMQ 通讯，同时保持现有 RPC 协议行为不变：

- topic 格式
- ACK 行为
- dotted operaCode 路由
- payload 分包与组包
- 公共 `MsgHandler` API

## 架构现状

### 共享抽象

- `MessageBusTransport`
  - `publish`
  - `subscribe`
  - `unsubscribe`
  - `isSubscribed`
  - `start`
  - `stop`
- `MessageBusListener`
- `MessageBusOperator`
- `MessageBusSenderSupport`
- `MessageBusReceiverSupport`

### MQTT 路径

- 继续使用现有 Spring Integration/Paho 运行链路。
- 通过 `MqttMessageBusTransport` 包一层 transport-neutral 的订阅与发布语义。
- 公共方法名保持不变。

### RabbitMQ 路径

- `RabbitMqTransport` 负责：
  - connection factory
  - exchange 声明
  - queue 声明
  - binding
  - listener container
  - message dispatch
- 使用：
  - `MessageBusSender`
  - `MessageBusReceiver`

## 当前任务

当前阶段任务已完成，等待提交。

## 最终回归清单

- MQTT 模式启动：
  - 配置 `server.msgBus=mqtt`
  - 日志包含 `Message bus selected: [mqtt]`
  - 普通请求成功
  - 同步 ACK 成功
- RabbitMQ 模式启动：
  - 配置 `server.msgBus=rabbitmq`
  - 日志包含 `Message bus selected: [rabbitmq]`
  - 普通请求成功
  - 同步 ACK 成功
- dotted operaCode：
  - 带点号的 `operaCode` 可以正常路由
  - 服务端可以正常执行并返回结果
- 大 payload：
  - 请求分包/组包成功
  - ACK 分包/组包成功
- RabbitMQ 重启/重复启动：
  - 不再出现队列声明冲突
  - broker 恢复后服务可以恢复通讯

## 后续建议提交

1. `refactor: clean up internal message bus naming`
2. `docs: add message bus regression checklist`
