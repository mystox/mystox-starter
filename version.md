# 版本更新说明
2.0.3
- 添加增加重复注册序列功能 register.isDuplicate #default: true
- 添加baseBalancerClient,增加负载均衡动态路由的更新功能
- @Opera注解添加
2.0.4
- 注册增加扩展属性extension注册，以键值对的方式在yaml中配置
2.0.5
- 增加动态函数的实体参数及其返回值支持
- 函数动态代理的的优化
- 负载均衡的一下bug修复
2.0.6
- 增加opera函数调用超时功能可配置功能, 通过@Opera注解配置@OperaTimeout
- 功能权限注册添加字段staticPath, 含义为页面权限对应的静态js路由地址
- 修复动态函数实体参数返回值bug
2.0.7
- 服务异常恢复注册逻辑流程修改
- 优雅关闭的一些优化修改
- banner文件版本号动态生成
