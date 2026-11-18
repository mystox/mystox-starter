# RabbitMQ RPC 无注册中心模式

启用 `register.type: none` 和 `server.msg-bus: rabbitmq` 后，不创建 ZooKeeper 注册器及客户端实例负载均衡器。扫描得到的方法表保存在本地，请求按组、契约版本、operaCode 路由，同能力的提供方竞争消费一个共享队列。

支持 `IaOpera.opera`、`operaGroup`、`operaAsync`，以及 `@Opera` 接口代理。完整可运行示例见 [Demo 说明](../examples/rabbitmq-only-rpc/README.md)。

请求使用 durable 队列、persistent 消息、publisher confirm、mandatory return 和业务完成后的手动确认；回复使用每进程独享的临时队列，以 correlationId 匹配。默认队列类型 quorum，配置前缀为 `rabbitmq.rpc`。

| 参数 | 默认值 |
| --- | --- |
| contract-version | v1 |
| exchange | yyds.rpc.requests |
| queue-prefix | yyds.rpc |
| queue-type | quorum |
| request-timeout | 30000 ms |
| confirm-timeout | 5000 ms |
| max-pending | 1000 |
| concurrency | 4 |
| prefetch | 1 |
| max-payload-bytes | 1048576 |

路由由三个独立 Base64URL 编码的段组成。请求队列名为 `queue-prefix + "." + routingKey`；发布版本 `server.version` 与接口 `contract-version` 分开。实例退出只取消自身消费，共享队列不删除。

没有绑定队列时返回 `OPERA_ROUTE_EXCEPTION`；队列存在但没有消费者时可能超时。连接中断、超时均可能导致业务结果未知，不能据此认定未执行。写操作需持久化业务幂等；本模式不提供 exactly-once 或自动业务重试。

大消息超限明确拒绝，不跨实例分片组包。毒消息、回复失败等拒绝到死信队列，人工排查前要先确认业务是否已完成。

首版不支持指定实例、广播、ROOT 自动跨组发现、动态订阅/路由、注册锁、权限元数据注册或 jar/HTTP 执行单元发布。原模式继续保留。

环境恢复说明：2026-11-18 的云工作区未包含先前仅本地保存的提交；本分支从远端 `60c49a0` 按本会话实现记录恢复，并继续补充接口代理验证。它不是原本地提交哈希的恢复。
