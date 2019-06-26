更新记录
### version 1.1.0
1. 集成云管session，实现登录权限相关功能
2. 同步终端添加至NbIot平台，配置文件需要新增添加
    
```
nb:
  url:
    cmcc: http://172.16.6.240:17703 #移动NB服务的WEB接口,可以为外网映射,与nb订阅关联的地址同义
    ctcc: http://
```

3. 实现app端地图查询、session轮询等接口
4. 修复若干bug