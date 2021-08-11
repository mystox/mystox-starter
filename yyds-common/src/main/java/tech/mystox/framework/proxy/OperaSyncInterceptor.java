package tech.mystox.framework.proxy;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.JSONValidator;
import com.alibaba.fastjson.util.TypeUtils;
import tech.mystox.framework.core.IaContext;
import tech.mystox.framework.entity.MsgResult;
import tech.mystox.framework.entity.OperaContext;
import tech.mystox.framework.entity.StateCode;
import tech.mystox.framework.exception.MsgResultFailException;

import java.util.concurrent.TimeUnit;

/**
 * Created by mystoxlol on 2020/6/29, 20:48.
 * company: kongtrolink
 * description:
 * update record:
 */
public class OperaSyncInterceptor extends OperaBaseInterceptor {
    private IaContext iaContext;

    public OperaSyncInterceptor(IaContext iaContext) {
        this.iaContext = iaContext;
    }

    private long timeout;

    private TimeUnit timeUnit;

    @Override
    public Object opera(
            String operaCode, Object[] arguments, Class<?> genericReturnType) {
        MsgResult opera = iaContext.getIaENV().getMsgScheduler().getIaHandler().opera(
                new OperaContext(operaCode, JSONObject.toJSONString(arguments), 2, timeout, timeUnit,
                        iaContext.getIaENV().getLoadBalanceScheduler(),
                        true, false));
        if (opera.getStateCode() != StateCode.SUCCESS) throw new MsgResultFailException("opera result is failed ["+opera.getStateCode()+"]");
        String msg = opera.getMsg();
        return deserialize(msg, genericReturnType);
    }

    public long getTimeout() {
        return timeout;
    }

    public void setTimeout(long timeout) {
        this.timeout = timeout;
    }

    public TimeUnit getTimeUnit() {
        return timeUnit;
    }

    public void setTimeUnit(TimeUnit timeUnit) {
        this.timeUnit = timeUnit;
    }

    private Object deserialize(String msg, Class<?> returnType) {
        if (String.class == returnType) {
            return msg;
        } else {
            boolean validate = JSONValidator.from(msg).validate();
            return validate ? TypeUtils.castToJavaBean(JSON.parse(msg), returnType) : TypeUtils.castToJavaBean(msg, String.class);
        }
/*
        if (String.class == returnType) return msg;
        Object parse = JSON.parse(msg);
        if (parse instanceof JSONObject) {
            return ((JSONObject) parse).toJavaObject(returnType);
        } else if (parse instanceof JSONArray) {
            if (returnType == List.class) {
                Type type = ((ParameterizedType) returnType).getActualTypeArguments()[0];
                return ((JSONArray) parse).toJavaList(type.getClass());
            } else if (returnType == Map.class) {
                return parse;
            } else if (returnType == Set.class) {
                Type type = ((ParameterizedType) returnType).getActualTypeArguments()[0];
                return ((JSONArray) parse).toJavaList(type.getClass());
            }
        } else if (returnType.getClass().isInstance(parse)) {
            return parse;
        } else if (returnType == Long.class|| "long".equals(returnType.getTypeName()))
            return Long.parseLong(msg);
        else if (returnType == Byte.class|| "byte".equals(returnType.getTypeName()))
            return Byte.parseByte(msg);
        return parse;*/
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

   /* public static void main(String[] args) {
        Map<String, String> map = new HashMap<>();
        map.put("abc", "ddd");
        OperaContext operaContext = new OperaContext();
        operaContext.setOperaCode("123123123123123");
        String msg = JSONObject.toJSONString(operaContext);
        msg = "1111111111111111111111111.11111111111";
        boolean validate = JSONValidator.from(msg).validate();
        System.out.println(validate);
        if (validate) {
            Object parse = JSON.parse(msg);
            String abc = TypeUtils.castToJavaBean(parse, String.class);
        System.out.println(JSONObject.toJSON(abc));
        }else {
            String s = TypeUtils.castToJavaBean(msg, String.class);
            System.out.println(s);
        }

    }*/
}
