# 版本更新说明
## 3.5.25.RELEASE !!!重要升级
- 修复2.5.24的扫描包注册的重要错误
## 3.5.24.RELEASE !!!重要升级(废弃)
- 扫描包bug的修复
- 一些配置的优化修改
## 2.0.22.RELEASE
## 3.5.23.RELEASE !!!重要升级
- 升级jdk21 及其springboot3.5.0支持
- 部分虚拟线程优化改造
- 一些代码的优化整理
## 2.0.22.RELEASE
- 新增operaGroup同步接口,OperaCode 添加withClass标记
- 添加线程局部变量对象MsgHandlerThreadContext
- mqtt配置容错，mqtt连接添加连接校验
- 优化关闭服务优化
- IaOpera注入提示配置优化
## 2.0.21.RELEASE(Deprecated)
- 该版本仓库废弃！
## 2.0.15
- 异步和广播做线程异步优化处理，广播不做路由表重建逻辑
- opera()选择服务路由表做优化处理
## 2.0.14
- 注册逻辑的优化，及其zk.exists方法异常处理优化
- 注册vueRouter.js文件目录位置
- fastjson upgrade to 2.0.51
- serverMsg类优化
- zk分布式锁优化
## 2.0.13
- 注册功能权限信息添加组标签
- fastjson upgrade to 2.0.40
- 消费者异常执行的异常抛出处理日志优化
- 功能权限注册PrivFuncEntity添加extension字段
# 历史版本说明
## 2.0.3
- 添加增加重复注册序列功能 register.isDuplicate #default: true
- 添加baseBalancerClient,增加负载均衡动态路由的更新功能
- @Opera注解添加
## 2.0.4
- 注册增加扩展属性extension注册，以键值对的方式在yaml中配置
## 2.0.5
- 增加动态函数的实体参数及其返回值支持
- 函数动态代理的的优化
- 负载均衡的一下bug修复
## 2.0.6
- 增加opera函数调用超时功能可配置功能, 通过@Opera注解配置@OperaTimeout
- 功能权限注册添加字段staticPath, 含义为页面权限对应的静态js路由地址
- 修复动态函数实体参数返回值bug
## 2.0.7
- 服务异常恢复注册逻辑流程修改
- 优雅关闭的一些优化修改
- banner文件版本号动态生成
## 2.0.8
- 修改发送具备目标服务serverCode'
- 强制需要配置server.address配置，banner打印内容新增相关内容
- 安装脚本版本号控制处理
- 新增环境配置获取服务完整的注册信息
- 同步接口返回序列化的优化和修改
- 相关日志及其代码格式的优化和修改
## 2.0.9
- 消息框架函数调用先判断当前服务状态
- 消息机制重要调整，groupCode为ROOT的服务消息路由可以通往所有组模块
## 2.0.10
- 消息框架发送拆包粘包功能增加
- list泛型实例参数的序列化bug修改
- 一些代码结构autowired注入的一些结构调整以符合实例化风格,及其日志显示的修改,消息通讯异常枚举优化，及其一些日志优化
- 注册逻辑的一些优化修改,iaEnv设置serverMsg对象
- 添加jar包(非服务包)调用消息框架的实例实现demo，参考模块yyds-server-example
## 2.0.11
- 重复注册锁修改为zk分布式锁, 重复标记isDuplicate=true启动流程重构
- 负载均衡路由表更新的优化
- fastjson upgrade to 2.0.21
- 默认关闭日志的一些优化操作，及其一些日志显示的优化
- 优雅关闭服务的优化，优化while-sleep相关过程逻辑
## 2.0.12
- fastjson upgrade to 2.0.34 and fastjson->fastjson2
- 注销的一些优化
- 注解的一些优化
- 其他代码结构及其日志显示的优化
- [fix] 重复注册的问题修复
## 2.0.20
- 开源发布maven-central仓库相关配置集成
- zookeeper-client upgrade to 3.9.2
- fastjson upgrade to 2.0.53
- 各模块非注入式实例化改造
- 一些代码的修剪和优化
- 添加注册服务健康状态查看接口
- 一些日志和响应优化
- iaOpera添加指定服务请求接口operaTarget&operaTargetAsync及其消息参数的优化为多参数
