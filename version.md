# 版本更新说明
2.1.2
- 添加增加重复注册序列功能 register.isDuplicate #default: true
- 添加baseBalancerClient,增加负载均衡动态路由的更新功能
- @Opera注解添加

2.1.3
- 修复负载均衡和负载均衡的优化，减少zk的并发连接频率，轮询方式更新路由信息
- 增加服务的优雅退出功能，检测是否有任务线程执行，在服务离线后再关闭服务
- 测试案例增加docker镜像插件
- 优化注册配置及其环境IaEnv增加服务状态属性ServerStatus，跟踪服务的启动、等待、注销、在线、离线、重启等状态

2.1.4
- 增加kafka消息组件，导入yyds-iarpc-kafka-starter，同时配置kafka配置信息即可，详见README.md
- mqtt模块目录调整，kafka与mqtt统一为mq模块
- 日志的配置显示新增默认配置文件logger.yml