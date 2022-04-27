package tech.mystox.framework.api.test;

import tech.mystox.framework.api.test.entity.OperaParam;
import tech.mystox.framework.api.test.entity.ParamEnum;
import tech.mystox.framework.api.test.entity.ReturnEntity;
import tech.mystox.framework.stereotype.OperaCode;
import tech.mystox.framework.stereotype.Register;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

/**
 * Created by mystoxlol on 2019/8/15, 13:31.
 * company: mystox
 * description:
 * update record:
 // */
@Register
public interface LocalService {
    @OperaCode
    String hello();
    @OperaCode(code = "sayHi")
    String hello(String param);
    @OperaCode(code = "sayHiList")
    List<String> helloList(String param);
    @OperaCode(code = "sayHiMap")
    Map helloMap(String param);
    @OperaCode(code = "sayHiParam")
    String helloParams(String param1,Integer param2);
    @OperaCode(code = "sayVoid")
    void helloParams(String param1,Integer param2,long param3);
    @OperaCode(code = "helloWait")
    ReturnEntity helloWait(String param, OperaParam operaParam);
    @OperaCode
    ParamEnum helloEnum(ParamEnum alarm);
    public static void main(String[] args)
    {
        try {
            Class<?> aClass = Class.forName("tech.mystox.framework.api.test.LocalService");
            Method helloParams = aClass.getDeclaredMethod("helloParams");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
    }
    @OperaCode
    ParamEnum helloEnumEntity(OperaParam operaParam, ParamEnum alarm);
    @OperaCode
    String testPackage(String payload);
}
