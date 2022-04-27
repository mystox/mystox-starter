package tech.mystox.framework.proxy;

import com.alibaba.fastjson.JSONObject;
import tech.mystox.framework.core.IaContext;

import java.lang.reflect.Type;

/**
 * Created by mystoxlol on 2020/6/29, 20:48.
 * description: 异步处理
 * update record:
 */
public class OperaAsyncInterceptor extends OperaBaseInterceptor {
    private IaContext iaContext;

    public OperaAsyncInterceptor(IaContext iaContext) {
        this.iaContext = iaContext;
    }


    @Override
    public Object opera(String operaCode, Object[] arguments, Type genericReturnType) {
        iaContext.getIaENV().getMsgScheduler().getIaHandler().operaAsync(operaCode, JSONObject.toJSONString(arguments));
        return null;
    }

   /* public static void main(String[] args) {

        Class<?> returnType = String.class;
        Class<?> stringClass = String.class;
        boolean equals = stringClass.equals(String.class);
        System.out.println(equals);
        System.out.println();
        String r = "hello service";
        String s = "{}";
        String a = "[]";
        String i = "3333333333";
        Object result = "";
        Object parse = JSON.parse(i);
        if (parse instanceof JSONObject) {
            System.out.println("jsonobject");
        } else if (parse instanceof JSONArray) {
            System.out.println("jsonArray");
        } else {
            Long aLong = JSON.parseObject(i, Long.class);
            System.out.println(aLong);
            System.out.println(parse.getClass().getTypeName());
        }
        result = parse instanceof JSONObject ? ((JSONObject) parse) : null;
        System.out.println(parse);
        JSONObject.parseObject(a, MsgResult.class);

    }*/
}
