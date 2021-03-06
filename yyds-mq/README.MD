# mqtt消息框架开发者指北



## 前提说明：
- 此消息框架基于mqtt消息中间件实现的伪远程调用。
- 此消息框架需要消息中间件mqtt、注册中心中间件zookeeper(1.0.0版本redis暂不支持)环境支持。
jdk版本为1.8+。


## maven安装依赖

```
mvn install:install-file -DgeneratePom=true -DgroupId=tech.mystox.framework -DartifactId=yyds-mqtt-starter -Dversion=1.0.0 -Dpackaging=jar -Dfile=E:\IdeaProjects\YYDS\yyds-mqtt\yyds-mqtt-starter\target\yyds-mqtt-starter-1.0.0.jar
```


项目基于springboot框架，引入yyds-mqtt-starter依赖
```
<dependency>
	<groupId>tech.mystox.framework</groupId>
	<artifactId>yyds-mqtt-starter</artifactId>
	<version>1.0.0</version>
</dependency>

```

## 配置文件说明


```
###########消息框架相关配置#############
server:
  groupCode: GROUP #组编码
  name: FOO_SERVER_DEMO #服务编码
  version: 1.0.0 #服务版本,可以扩展为 * app
  port: 52002 #服务占用web端口 占用拦截{/mqtt/*,/register*}
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

作为云管的配置文件，需要必须额外配置${register.url},作为非云管模块配置文件，${register.url}作为可配属性，作为开发态免云管注册功能的便捷开发

## 代码相关

### 初始化相关

```
@SpringBootApplication(scanBasePackages = {"tech.mystox.framework.mqtt"})
public class ServerDemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(ServerDemoApplication.class, args);
	}

}
```

如果项目的基本包路径不包含tech.mystox.framework.mqtt，则注解需要自定义配置增加scanBasePackages={"tech.mystox.framework.mqtt","项目基本包名"}。

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
{"ack":"NA","executeUnit":"local://tech.mystox.framework.demo.api.FooService/foo","operaCode":"fooo"}
```

- @Register 对应注册单元的服务名tech.mystox.framework.demo.api.FooService。
- @OperaCode 对应注册单元操作码"operaCode":"fooo"，如果code不进行配置,则默认以方法（函数）名作为operaCode。
其中ack的类型由返回值是否为void判断：如果方法返回值为void，则对应ack的值为NA,否则为ACK。
- 处理单元服务以实现类的方式实现具体消息接口的业务逻辑，并使用@Service/@Compenent。
- 处理单元的接口类需要包含在springboot启动扫描的包范围内。


### jar处理单元



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

代码示例

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

---












