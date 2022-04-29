# mqtt消息框架开发者指北



## 前提说明：
- 此消息框架基于mqtt消息中间件实现的分布式远程调用。
- 此消息框架需要消息中间件mqtt、注册中心中间件zookeeper(redis暂不支持)环境支持。
- 此框架bean管理依赖于springboot 2.x。
- 此框架消息队列中间件、注册中心、负载均衡策略可自定义开发，友好性配置开发正在进行...

- jdk版本为1.8+。


## maven安装依赖

windows 环境下执行 baseJar/install.bat
bash 环境下执行 baseJar/install.sh
```
mvn install:install-file -DgeneratePom=true -DgroupId=tech.mystox.framework -DartifactId=yyds-common -Dversion=$VERSION -Dpackaging=jar -Dfile=$BASE_DIR/yyds-common-$VERSION.jar
mvn install:install-file -DgeneratePom=true -DgroupId=tech.mystox.framework -DartifactId=yyds-balancer -Dversion=$VERSION -Dpackaging=jar -Dfile=$BASE_DIR/yyds-balancer-$VERSION.jar
mvn install:install-file -DgeneratePom=true -DgroupId=tech.mystox.framework -DartifactId=yyds-register -Dversion=$VERSION -Dpackaging=jar -Dfile=$BASE_DIR/yyds-register-$VERSION.jar
mvn install:install-file -DgeneratePom=true -DgroupId=tech.mystox.framework -DartifactId=yyds-mqtt-starter -Dversion=$VERSION -Dpackaging=jar -Dfile=$BASE_DIR/yyds-mqtt-starter-$VERSION.jar

mvn install:install-file -DgeneratePom=true -DgroupId=tech.mystox.framework -DartifactId=yyds-iarpc-starter -Dversion=$VERSION -Dpackaging=pom -Dfile=$BASE_DIR/yyds-iarpc-starter-$VERSION.xml
mvn install:install-file -DgeneratePom=true -DgroupId=tech.mystox.framework -DartifactId=yyds-common -Dversion=$VERSION -Dpackaging=pom -Dfile=$BASE_DIR/yyds-common-$VERSION.xml
mvn install:install-file -DgeneratePom=true -DgroupId=tech.mystox.framework -DartifactId=yyds-balancer -Dversion=$VERSION -Dpackaging=pom -Dfile=$BASE_DIR/yyds-balancer-$VERSION.xml
mvn install:install-file -DgeneratePom=true -DgroupId=tech.mystox.framework -DartifactId=yyds-register -Dversion=$VERSION -Dpackaging=pom -Dfile=$BASE_DIR/yyds-register-$VERSION.xml
mvn install:install-file  -DgeneratePom=true -DgroupId=tech.mystox.framework -DartifactId=yyds-mqtt-starter -Dversion=$VERSION -Dpackaging=pom -Dfile=$BASE_DIR/yyds-mqtt-starter-$VERSION.xml



```


项目基于springboot框架，引入yyds-iarpc-starter依赖
```
<dependency>
    <groupId>tech.mystox.framework</groupId>
    <artifactId>yyds-iarpc-starter</artifactId>
    <version>2.1.10</version>
    <type>pom</type>
</dependency>
```

## 配置文件说明


```
###########消息框架相关配置#############
server:
  address: 127.0.0.1 #对应服务对外暴露的ip/host
  groupCode: GROUP #组编码
  name: FOO_SERVER_DEMO #服务编码
  version: 1.0.0 #服务版本,可以扩展为 * app
  port: 52002 #服务占用web端口 占用拦截{/mqtt/*,/register*}
  titile: 消息框架DEMO
jarResources:
  path: ./jarResources #可选配置 jar包和jarRes.yml资源配置文件存放路径 默认为./jarResources
register:
  scanBasePackage: tech.mystox.framework.demo.api #默认配置tech.mystox.framework 如果项目的包名不一致需要手动配置
  url: zookeeper://172.16.5.26:2181,172.16.5.26:2182,172.16.5.26:2183 #开发态的服务和云管服务配置，开发态服务可以不依赖云管进行部分功能服务的开发
mqtt:
  username: root #可选配置 mqtt配置相关
  password: 123456 #可选配置 mqtt配置相关
  url: tcp://172.16.5.26:1883 #推送信息的连接地址，如果有多个，用逗号隔开，如：tcp://127.0.0.1:61613,tcp://192.168.1.61:61613
```


## 代码相关

### 初始化相关

```
@SpringBootApplication
@EnableOpera
public class ServerDemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(ServerDemoApplication.class, args);
	}

}
```

如果项目的基本包路径不包含tech.mystox.framework，则注解需要自定义配置增加scanBasePackages={"tech.mystox.framework","项目基本包名"}。
2.0.*版本已经通过@EnableOpera注解实现组件包的注入。

### local处理单元

```
@Register
public interface FooService {

    @OperaCode(code = "fooo")
    void foo(String foo);

    @OperaCode
    String boo(String boo);
    
}

```

**说明：**
处理单元注解对应处理单元实体：

```
{"ack":"NA","executeUnit":"local://tech.mystox.framework.foo.api.FooService/foo/[\"java.lang.String\"]","operaCode":"fooo"}
```

- @Register 对应注册单元的服务名tech.mystox.framework.demo.api.FooService。
- @OperaCode 对应注册单元操作码"operaCode":"fooo"，如果code不进行配置,则默认以方法（函数）名作为operaCode。
其中ack的类型由返回值是否为void判断：如果方法返回值为void，则对应ack的值为NA,否则为ACK。
- 处理单元尾段为参数的类型集合
- 处理单元服务以实现类的方式实现具体消息接口的业务逻辑，并使用@Service/@Compenent。
- 处理单元的接口类需要包含在springboot启动扫描的包范围内。


### jar处理单元(暂时搁置)



对应配置文件jarRes.yml

```
YYTD_MQTT_DEMO_1.0.0: #服务编码
  sayHello: mystoxUtil.jar/tech.mystox.test.util.MystoxUtil/sayHello:ACK #操作码: jar包名称/服务名/方法名:是否存在返回ack(不配置表示默认返回值为void)
  fooHi: jarName.jar/**.*.*/foo
```

**说明：**
处理单元注解对应处理单元实体：

```
{"ack":"ACK","executeUnit":"jar://mystoxUtil.jar/tech.mystox.test.util.MystoxUtil/sayHello","operaCode":"sayHello"}
```

- 实体字段对应配置文件关系：
操作码: jar包名称/服务名/方法名:是否存在返回ack(不配置表示默认返回值为void)
- jarRes.yml可以通过接口进行增删和修改,修改结果对注册中心和配置文件同步刷新

### http处理单元（暂不做实现）

### 处理单元备注说明
相同操作码不同处理单元的优先级为http>jar>local

### 消息生产者

#### 示例1--函数式代码示例（此方法仅支持local处理单元的远程代理）

共同接口类（同local处理单元）

**备注： //TODO 共同接口类，待去@register优化
处理单元依赖yyds-common组件
依赖导入：
```
    <dependency>
        <groupId>tech.mystox.framework</groupId>
        <artifactId>yyds-common</artifactId>
        <version>2.1.10</version>
    </dependency>
```
common接口
```
@Register
public interface LocalService {
    @OperaCode(code = "sayHiParam")
    String helloParams(String param1,Integer param2);
}
```
描述同local处理单元

- 消费者代码
```
    ...
    @Opera
    LocalService localService;
    ...
    @RequestMapping("/hello")
    public JSONObject testOpera() {
        JSONObject result = new JSONObject();
        String helloPrams = localService.helloParams("hello", 76);
        result.put("helloParams", helloPrams);
        return result;
    }
    ...
    @Opera(operaType = OperaType.Broadcast)
    OperaRouteService operaRouteService;
    ...
    @RequestMapping("/broadcast")
    public void broadcastOperaCode() {
        List<String> msg = new ArrayList<>();
        msg.add("cat");
        msg.add("dog");
        broadcastService.callHelloWorld("mystox", msg);
    }
    ...
```
描述：@Opera注解注入远程调用接口方法，直接对接口中的方法进行参数调用即可。

#### 示例2--操作码代码示例

```
    @Autowired
    MqttOpera mqttOpera;
    ...
    {
        //同步操作接口
        MsgResult opera = mqttOpera.opera(operaCode,message);
        //同步意向接口自定义配置qos和超时时间
        MsgResult opera = mqttOpera.opera(operaCode, message, 2, 10, TimeUnit.SECONDS);
        //异步意向接口
        void operaAsync(String operaCode, String msg);
        //广播接口
        mqttOpera.broadcast(operaCode,message);
    }

```
消息发送者载入MqttOpera即可实现消息的同步和异步发送，参数分别为：operaCode(操作码)，message（发送的消息实体，默认为String）

#### 示例3--提供者代码
```
@Service
public class LocalServiceImpl implements LocalService {
    @Override
    public String helloParams(String param1, Integer param2) {
        //todo do something
        System.out.println(param1 + "  "+ param2);
        return serverName + param1 +  param2;
    }
```
描述：实现类实现LocalService接口内容，@Service注解为spring实例化注解


## 源码相关

### 代码模块描述

* yyds-iarpc-starter: 框架pom管理模块。
* yyds-common: 对外接口控制服务模块，实现服务控制选择初始化、bean实例管理、接口暴露、服务初始化、动态代理、环境配置、web资源配置、路由配置、核心工具类。
* yyds-balancer: 负载均衡模块的实现，默认基本模式BaseLoadBalancerClient。
* yyds-register: 注册模块的实现，默认实现zookeeper为注册中心。
* yyds-mqtt: 消息中间件组件，实现消息的消费和生产，集成注册模块与负载均衡模块实现消息的分布式远程调用，支持‘函数式’与‘topic式’调用。
* yyds-server-project: 使用的案例示例。
* yyds-iarpc-dependencies：版本依赖管理模块

## 性能说明
硬件条件：（cpu： Intel(R) Xeon(R) CPU E5-2678 v3 @ 2.50GHz 网络:单服务带宽100mps）
mqtt+zookeeper 8*cpu 
mqtt消息中间件作为粗略测试结果：
* 单服务的生产者: 
  * 异步 通讯 >10000/s 并发效率，受限于mqtt发布带宽
  * 同步 通讯4717/s 并发效率，受限于mqtt发布带宽
* 单服务消费者: 
  * 异步 通讯 10000/s并发效率，受限于mqtt服务的下行带宽
  * 同步 通讯 4717/s 并发效率，受限于mqtt服务的上下行带宽
* 多服务的生产者：
  * 异步 18000/s 并发效率，受限于emqtt服务的性能
  * 同步 9500/s 并发效率，，受限于emqtt服务的性能
---

contact: 
- e-mail: mystox@163.com 
- qq: 575940201












