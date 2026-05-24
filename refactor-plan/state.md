# 重构状态

## 项目

`yyds-mqtt` RabbitMQ 消息总线集成。

## 当前重点

RabbitMQ/MQTT 消息总线集成已收口。

## 当前状态

- RabbitMQ 通讯链路已实现，demo 回归已完成。
- MQTT 路径仍保持原有 Spring Integration/Paho 实现兼容。
- RabbitMQ 路径已接入：
  - `MessageBusTransport`
  - `MessageBusListener`
  - `MessageBusSender`
  - `MessageBusReceiver`
- 配置文档和启动日志已补充。
- 当前可做的内部命名清理已完成并通过验证，等待提交。
- 最终 demo 回归清单已补充，当前阶段任务已清空。

## 已完成提交

- `3cf1493 feat: add rabbitmq message bus support`
- `3f3a3ca refactor: abstract message bus sender for rabbitmq`
- `0ada816 refactor: route rabbitmq messages through bus listener`
- `f681ca2 refactor: extract rabbitmq transport management`
- `e7e187a refactor: generalize message bus receiver support`
- `a07ed34 refactor: generalize message bus sender and mqtt transport`
- `8beba62 docs: add message bus config and startup logs`

## 验证

- 最近一次验证已通过：
  `mvn -pl yyds-mqtt/yyds-mqtt-starter,yyds-iarpc-starter -am -DskipTests clean install`
- 用户已完成 RabbitMQ/MQTT demo 回归。

## 阻塞

- 暂无。

## 下一步

1. 提交当前内部命名清理和计划文件。
2. `sendToMqtt*` 等公共 API 名称保留到未来大版本兼容性阶段再处理。
