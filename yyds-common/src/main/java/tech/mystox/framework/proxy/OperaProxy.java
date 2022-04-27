package tech.mystox.framework.proxy;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.aop.framework.ProxyFactory;
import tech.mystox.framework.stereotype.OperaCode;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

/**
 * Created by mystoxlol on 2020/6/18, 11:12.
 * company:
 * description:
 * update record:
 */
public class OperaProxy {

    @SuppressWarnings("unchecked")
    public static <T> T createOpera(Class<T> clazz) {
        System.getProperties().setProperty("sun.misc.ProxyGenerator.saveGeneratedFiles", "false");

        ProxyFactory proxyFactory = new ProxyFactory();
        proxyFactory.setInterfaces(clazz);
        proxyFactory.addAdvice(new MethodInterceptor() {
            @Override
            public Object invoke(MethodInvocation invocation) throws Throwable {
                String operaCodeName = "";
                Method method = invocation.getMethod();
                OperaCode operaCode = method.getAnnotation(OperaCode.class);
                operaCodeName = operaCode == null?  method.getName():operaCode.code();


                Annotation[] annotations = method.getAnnotations();
                String name = method.getName();
                Object[] arguments = invocation.getArguments();

                System.out.println(name);

                System.out.println("执行泛型方法....");
                return null;
            }
        });
        return (T)proxyFactory.getProxy();


        // return (T) Proxy.newProxyInstance(clazz.getClassLoader(), clazz.getInterfaces(), new InvocationHandler() {
        //
        //     @Override
        //     public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        //         System.out.println("执行泛型方法....");
        //         return null;
        //     }
        // });

    }
}
