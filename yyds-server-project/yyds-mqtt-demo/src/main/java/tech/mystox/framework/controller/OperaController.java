package tech.mystox.framework.controller;

import com.alibaba.fastjson.JSONObject;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tech.mystox.framework.api.test.BroadcastService;
import tech.mystox.framework.api.test.LocalService;
import tech.mystox.framework.common.util.CollectionUtils;
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
@RestController
@RequestMapping("/operaBean")
public class OperaController {
    @Opera
    LocalService localService;

    @Opera(operaType = OperaType.Broadcast)
    BroadcastService broadcastService;

    @RequestMapping("/hello")
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
    @RequestMapping("/broadcast")
    public void broadcastOperaCode() {
        List<String> msg = new ArrayList<>();
        msg.add("cast");
        msg.add("dddd");
        broadcastService.callHelloWorld("mystox", msg);
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
