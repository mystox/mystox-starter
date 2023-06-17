package tech.mystox.framework.proxy;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import tech.mystox.framework.common.util.CollectionUtils;
import tech.mystox.framework.common.util.SpringContextUtil;
import tech.mystox.framework.core.IaContext;
import tech.mystox.framework.entity.StateCode;
import tech.mystox.framework.exception.MsgResultFailException;
import tech.mystox.framework.stereotype.OperaCode;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Arrays;

/**
 * Created by mystox on 2021/5/22, 22:37.
 * company:
 * description:
 * update record:
 */
public abstract class OperaBaseInterceptor implements MethodInterceptor {
    private IaContext iaContext;
    public OperaBaseInterceptor(IaContext iaContext) {
        this.iaContext = iaContext;
    }

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        String operaCodeName;
        Method method = invocation.getMethod();
        OperaCode operaCode = method.getAnnotation(OperaCode.class);
        Object[] arguments = invocation.getArguments();
        if (operaCode == null) {
            ApplicationContext applicationContext = SpringContextUtil.getApplicationContext();
            Object bean = null;
            try {
                Class<?> declaringClass = invocation.getMethod().getDeclaringClass();
                if (declaringClass.isInstance(Object.class)){
                    Method[] methods = Object.class.getMethods();
                    if(CollectionUtils.contains(Arrays.stream(methods).iterator(), method)){
                        return method.invoke(this, arguments);
                    }
                }
                bean = applicationContext.getBean(declaringClass);
            } catch (BeansException e) {
                throw new MsgResultFailException(StateCode.StateCodeEnum.OPERA_ROUTE_EXCEPTION,"opera is null or code is blank and no local service available...");//todo 是否应该本地执行
            }
            return bean.getClass().getMethod(method.getName(), invocation.getMethod().getParameterTypes())
                    .invoke(bean, invocation.getArguments());
        }
        operaCodeName = StringUtils.isBlank(operaCode.code()) ? method.getName() : operaCode.code();
//        Type genericReturnType = method.getReturnType();
        return opera(operaCodeName, arguments,method.getGenericReturnType());

    }

    public IaContext getIaContext() {
        return iaContext;
    }

    public abstract Object opera(String operaCode, Object[] arguments, Type genericReturnType);
}
