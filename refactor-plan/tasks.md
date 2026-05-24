# 重构任务

## 进行中

- 暂无。

## 待处理

- [ ] 补充或精简 demo 回归清单
  - 覆盖 MQTT 启动、RabbitMQ 启动、ACK、dotted operaCode、大 payload、RabbitMQ 重启。

## 已完成

- [x] 添加 RabbitMQ 消息总线基础支持
- [x] 抽象 RabbitMQ 发送侧
- [x] RabbitMQ 消息接入 listener 分发
- [x] 拆分 `RabbitMqTransport`
- [x] 抽出 `MessageBusReceiverSupport`
- [x] 抽出 `MessageBusSenderSupport`
- [x] 添加 MQTT transport facade
- [x] 补充 MQTT/RabbitMQ 配置文档
- [x] 添加消息总线和 registerUrl 启动日志
- [x] 清理内部 message bus 命名
  - `mqttSenderImpl` -> `messageBusOperator`
  - `mqttHandlerAck` -> `ackTopicHandler`
  - `mqttHandlerImpl` -> `requestTopicHandler`
  - `mqttReceiver` -> `messageBusReceiver`
  - 保留 `sendToMqtt*` 等公共 API 到未来兼容性阶段
