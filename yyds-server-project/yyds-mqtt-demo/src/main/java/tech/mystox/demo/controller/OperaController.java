package tech.mystox.demo.controller;

import com.alibaba.fastjson.JSONObject;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import tech.mystox.framework.api.test.BroadcastService;
import tech.mystox.framework.api.test.EntityService;
import tech.mystox.framework.api.test.LocalService;
import tech.mystox.framework.api.test.entity.OperaParam;
import tech.mystox.framework.api.test.entity.ReturnEntity;
import tech.mystox.framework.entity.OperaType;
import tech.mystox.framework.stereotype.Opera;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by mystoxlol on 2020/6/18, 10:46.
 * company: kongtrolink
 * description:
 * update record:
 */

@Api(tags = "operaBean函数动态代理测试")
@RestController
@RequestMapping("/operaBean")
public class OperaController {
    @Opera
    LocalService localService;


    @Autowired
    BroadcastService broadcastServiceAuto;
    @Opera(operaType = OperaType.Broadcast)
    BroadcastService broadcastService;

    @Opera(operaType = OperaType.Async)
    BroadcastService broadcastService2;

    @Opera(operaType = OperaType.Sync)
    BroadcastService broadcastService3;


    @ApiOperation(value = "同步/异步接口测试")
    @RequestMapping(value = "/hello",method = RequestMethod.GET)
    public JSONObject testOpera() {
        JSONObject result = new JSONObject();

        String hello = localService.hello();
        System.out.println(hello);
        result.put("hello", hello);
        List<String> strings = localService.helloList("11");
        System.out.println(strings);
        result.put("helloList", strings);
        List<String> stringnull = localService.helloList(null);
        System.out.println(stringnull);
        result.put("helloList", stringnull);
        Map map = localService.helloMap("123");
        System.out.println(map);
        result.put("helloMap", map);
        String abc = localService.helloParams("abc", 123);
        System.out.println(abc);
        result.put("helloParams", abc);
        String aaaaaaaaaa = localService.hello("aaaaaaaaaa");
        result.put("helloaaaa", aaaaaaaaaa);
        localService.helloParams("dd",1,222222222L);
        return result;
    }
    @ApiOperation(value = "广播接口测试")
    @RequestMapping(value = "/broadcast",method = RequestMethod.GET)
    public void broadcastOperaCode() {
        List<String> msg = new ArrayList<>();
        msg.add("cast");
        msg.add("dddd");
        broadcastServiceAuto.callHelloWorld2("mystoxdemo",msg);
        System.out.println("autowired动态加载end");
        broadcastService3.callHelloWorld2("msytox",msg);
        System.out.println("service2动态加载end");
        broadcastService.callHelloWorld("mystox1", msg);
        System.out.println(broadcastService.toString());
        broadcastService2.callHelloWorld("mystox2",msg);
        System.out.println(broadcastService2.toString());
        broadcastService3.callHelloWorld("mystox3",msg);
        System.out.println(broadcastService3.toString());
        //next
        //



    }

    @Opera
    EntityService entityService;

    @ApiOperation(value = "实体接口测试")
    @RequestMapping(value = "/getEntity",method = RequestMethod.GET)
    public void testEntity() {

        OperaParam operaParam = new OperaParam();
        operaParam.setParam("aaaaaaaaaaaaa");
        operaParam.setContext("1231231312312313");
        ReturnEntity entity = entityService.getEntity(operaParam);
        System.out.println(JSONObject.toJSONString(entity));


    }

    public static void main(String[] args)
    {
/*
        ReflectionUtils.doWithFields(OperaController.class, new ReflectionUtils.FieldCallback() {
            @Override
            public void doWith(Field field) throws IllegalArgumentException, IllegalAccessException {
                Opera annotation = getAnnotation(field, Opera.class);
                System.out.println(annotation);
                if (annotation!=null)
                    System.out.println("aaaaaaaaaaaaa");
                Class<?> declaringClass = field.getDeclaringClass();
                Class<? extends Field> aClass = field.getClass();
                Annotation[] declaredAnnotations = field.getDeclaredAnnotations();
                Class<?> type = field.getType();
                try {
                    Method hello = type.getMethod("hello", String.class);
                    Object invoke = hello.invoke("");
                    System.out.println(invoke);
                } catch (NoSuchMethodException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                } catch (InstantiationException e) {
                    e.printStackTrace();
                }
                System.out.println(JSONObject.toJSONString(declaredAnnotations));
                System.out.println(declaringClass.getName());
                Method[] methods = declaringClass.getMethods();
                try {
                    Method method = methods[0];
                    String name = method.getName();
                    System.out.println(name);
                    Object invoke = method.invoke("");
                    System.out.println(invoke);
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }
            }
        });*/
    }

}
