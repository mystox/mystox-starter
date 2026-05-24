# 待办池

## P0

- 暂无。

## P1

- [x] 增加一份独立的 RabbitMQ/MQTT 回归清单。
  - 已收口到 `refactor-plan/areas/rabbitmq-message-bus.md` 的“最终回归清单”。
- [ ] 如仍需要，补充更具体的 broker 启动日志：
  - MQTT URL
  - RabbitMQ host/port/exchange
  - RabbitMQ queuePrefix/prefetch

## P2

- [ ] 规划未来公共 API 命名阶段。
  - `sendToMqtt*` 仍是公共兼容接口。
  - 只有在废弃策略明确后再考虑重命名。
- [ ] 未来大版本考虑 transport-neutral 的 handler 基类命名。
- [ ] 在具备测试环境后，补自动化集成测试覆盖消息总线切换。
