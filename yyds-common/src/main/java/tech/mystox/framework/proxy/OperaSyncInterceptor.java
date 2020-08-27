package tech.mystox.framework.proxy;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import tech.mystox.framework.common.util.StringUtils;
import tech.mystox.framework.core.IaContext;
import tech.mystox.framework.entity.MsgResult;
import tech.mystox.framework.entity.StateCode;
import tech.mystox.framework.exception.MsgResultFailException;
import tech.mystox.framework.stereotype.OperaCode;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by mystoxlol on 2020/6/29, 20:48.
 * company: kongtrolink
 * description:
 * update record:
 */
public class OperaSyncInterceptor implements MethodInterceptor {
    private IaContext iaContext;

    public OperaSyncInterceptor(IaContext iaContext) {
        this.iaContext = iaContext;
    }

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        String operaCodeName;
        Method method = invocation.getMethod();
        OperaCode operaCode = method.getAnnotation(OperaCode.class);
        if (operaCode == null) throw new MsgResultFailException("opera is null or code is blank");
        operaCodeName = StringUtils.isBlank(operaCode.code()) ? method.getName() : operaCode.code();
        Type genericReturnType = method.getGenericReturnType();
        // Class<?> returnType = method.getReturnType();
        Object[] arguments = invocation.getArguments();
        // Class<?>[] parameterTypes = method.getParameterTypes();
        // new InvocationData(arguments,parameterTypes)
        MsgResult opera = iaContext.getIaENV().getMsgScheduler().getIaHandler().opera(operaCodeName, JSONObject.toJSONString(arguments));
        if (opera.getStateCode() != StateCode.SUCCESS) throw new MsgResultFailException("opera result is failed ["+opera.getStateCode()+"]");
        String msg = opera.getMsg();
        return deserialize(msg, genericReturnType);
    }

    private Object deserialize(String msg, Type returnType) {
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
        return parse;
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
