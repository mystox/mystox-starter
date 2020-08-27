package tech.mystox.framework.proxy;

import com.alibaba.fastjson.JSONObject;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.apache.commons.lang3.StringUtils;
import tech.mystox.framework.core.IaContext;
import tech.mystox.framework.exception.MsgResultFailException;
import tech.mystox.framework.stereotype.OperaCode;

import java.lang.reflect.Method;

/**
 * Created by mystoxlol on 2020/6/29, 20:48.
 * company: kongtrolink
 * description: 异步处理
 * update record:
 */
public class OperaAsyncInterceptor implements MethodInterceptor {
    private IaContext iaContext;

    public OperaAsyncInterceptor(IaContext iaContext) {
        this.iaContext = iaContext;
    }

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        String operaCodeName;
        Method method = invocation.getMethod();
        OperaCode operaCode = method.getAnnotation(OperaCode.class);
        if (operaCode == null) throw new MsgResultFailException("opera is null or code is blank");
        operaCodeName = StringUtils.isBlank(operaCode.code()) ? method.getName() : operaCode.code();
        Object[] arguments = invocation.getArguments();
        //广播
        iaContext.getIaENV().getMsgScheduler().getIaHandler().operaAsync(operaCodeName, JSONObject.toJSONString(arguments));
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
