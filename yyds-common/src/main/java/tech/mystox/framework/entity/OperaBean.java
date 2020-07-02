package tech.mystox.framework.entity;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import tech.mystox.framework.common.util.CollectionUtils;
import tech.mystox.framework.common.util.ReflectUtils;
import tech.mystox.framework.core.IaContext;
import tech.mystox.framework.proxy.OperaInterceptor;
import tech.mystox.framework.stereotype.Opera;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.List;
import java.util.Map;

/**
 * Created by mystoxlol on 2020/6/23, 10:28.
 * company: kongtrolink
 * description:
 * update record:
 */
public class OperaBean<T> implements FactoryBean,InitializingBean,Serializable {


    private static final long serialVersionUID = 3273322183327163519L;
    Logger logger = LoggerFactory.getLogger(getClass().getName());
    private transient ApplicationContext applicationContext;
    private Class<?> interfaceClass;
    // interface name
    private String interfaceName;
    private List<String> operaCodeList;
    protected String id;
    protected String version;
    protected String group;
    // interface proxy reference
    private transient volatile T ref;
    // private transient volatile Invoker<?> invoker;
    private transient volatile boolean initialized;
    private transient volatile boolean destroyed;
    private static final ProxyFactory proxyFactory = null;
    private IaContext iaContext;
    public OperaBean() {
    }
    public OperaBean(Opera opera) {
        this.appendAnnotation(Opera.class, opera);
    }
    @Override
    public Object getObject() throws Exception {
        return get();
    }

    @Override
    public Class<?> getObjectType() {
        return null;
    }

    @Override
    public boolean isSingleton() {
        return false;
    }

    public synchronized T get() {
        if (destroyed) {
            throw new IllegalStateException("Already destroyed!");
        }
        if (ref == null) {
            init();
        }
        return ref;
    }

    /**
     * 初始化
     */
    private void init() {
        if (initialized) {
            return;
        }
        initialized = true;
        if (interfaceName == null || interfaceName.length() == 0) {
            throw new IllegalStateException("<dubbo:reference interface=\"\" /> interface not allow null!");
        }

        try {
            interfaceClass = Class.forName(interfaceName, true, Thread.currentThread()
                    .getContextClassLoader());
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        checkInterfaceAndMethods(interfaceClass, operaCodeList);
        // String resolve = System.getProperty(interfaceName);
        // Map map = new HashMap();
        this.iaContext = applicationContext.getBean(IaContext.class);

        ref = createProxy();
    }
    @SuppressWarnings({"unchecked", "rawtypes", "deprecation"})
    private T createProxy() {

        System.getProperties().setProperty("sun.misc.ProxyGenerator.saveGeneratedFiles", "false");

        ProxyFactory proxyFactory = applicationContext.getBean(ProxyFactory.class);

        proxyFactory.setInterfaces(interfaceClass);
        proxyFactory.addAdvice(new OperaInterceptor(iaContext));
        return (T)proxyFactory.getProxy();

        // return (T)  OperaProxy.createOpera(interfaceClass);
    }

    private void checkInterfaceAndMethods(Class<?> interfaceClass, List<String> operaCodeList) {
        // interface cannot be null
        if (interfaceClass == null) {
            throw new IllegalStateException("interface not allow null!");
        }
        // to verify interfaceClass is an interface
        if (!interfaceClass.isInterface()) {
            throw new IllegalStateException("The interface class " + interfaceClass + " is not a interface!");
        }
        // check if methods exist in the interface
        // if (methods != null && !methods.isEmpty()) {
        //     for (MethodConfig methodBean : methods) {
        //         String methodName = methodBean.getName();
        //         if (methodName == null || methodName.length() == 0) {
        //             throw new IllegalStateException("<dubbo:method> name attribute is required! Please check: <dubbo:service interface=\"" + interfaceClass.getName() + "\" ... ><dubbo:method name=\"\" ... /></<dubbo:reference>");
        //         }
        //         boolean hasMethod = false;
        //         for (java.lang.reflect.Method method : interfaceClass.getMethods()) {
        //             if (method.getName().equals(methodName)) {
        //                 hasMethod = true;
        //                 break;
        //             }
        //         }
        //         if (!hasMethod) {
        //             throw new IllegalStateException("The interface " + interfaceClass.getName()
        //                     + " not found method " + methodName);
        //         }
        //     }
        // }
    }

    public void setApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public Class<?> getInterfaceClass() {
        if(this.interfaceClass != null) {
            return this.interfaceClass;
        } /*else if(!this.isGeneric().booleanValue() && (this.getConsumer() == null || !this.getConsumer().isGeneric().booleanValue())) {
            try {
                if(this.interfaceName != null && this.interfaceName.length() > 0) {
                    this.interfaceClass = Class.forName(this.interfaceName, true, Thread.currentThread().getContextClassLoader());
                }
            } catch (ClassNotFoundException var2) {
                throw new IllegalStateException(var2.getMessage(), var2);
            }

            return this.interfaceClass;
        } else {
            return GenericService.class;
        }*/
        return null;
    }

    /** @deprecated */
    @Deprecated
    public void setInterfaceClass(Class<?> interfaceClass) {
        this.setInterface(interfaceClass);
    }

    public String getInterface() {
        return this.interfaceName;
    }

    public void setInterface(Class<?> interfaceClass) {
        if(interfaceClass != null && !interfaceClass.isInterface()) {
            throw new IllegalStateException("The interface class " + interfaceClass + " is not a interface!");
        } else {
            this.interfaceClass = interfaceClass;
            this.setInterface(interfaceClass == null?(String)null:interfaceClass.getName());
        }
    }

    public void setInterface(String interfaceName) {
        this.interfaceName = interfaceName;
        if(this.id == null || this.id.length() == 0) {
            this.id = interfaceName;
        }

    }

    protected void appendAnnotation(Class<?> annotationClass, Object annotation) {
        Method[] methods = annotationClass.getMethods();
        Method[] arr$ = methods;
        int len$ = methods.length;

        for(int i$ = 0; i$ < len$; ++i$) {
            Method method = arr$[i$];
            if(method.getDeclaringClass() != Object.class && method.getReturnType() != Void.TYPE && method.getParameterTypes().length == 0 && Modifier.isPublic(method.getModifiers()) && !Modifier.isStatic(method.getModifiers())) {
                try {
                    String e = method.getName();
                    if("interfaceClass".equals(e) || "interfaceName".equals(e)) {
                        e = "interface";
                    }

                    String setter = "set" + e.substring(0, 1).toUpperCase() + e.substring(1);
                    Object value = method.invoke(annotation, new Object[0]);
                    if(value != null && !value.equals(method.getDefaultValue())) {
                        Class parameterType = ReflectUtils.getBoxedClass(method.getReturnType());
                        if(!"filter".equals(e) && !"listener".equals(e)) {
                            if("parameters".equals(e)) {
                                parameterType = Map.class;
                                value = CollectionUtils.toStringMap((String[])((String[])value));
                            }
                        } else {
                            parameterType = String.class;
                            value = StringUtils.join((String[])((String[])value), ",");
                        }

                        try {
                            Method setterMethod = this.getClass().getMethod(setter, new Class[]{parameterType});
                            setterMethod.invoke(this, new Object[]{value});
                        } catch (NoSuchMethodException var13) {
                            ;
                        }
                    }
                } catch (Throwable var14) {
                    logger.error(var14.getMessage(), var14);
                }
            }
        }

    }

    @Override
    public void afterPropertiesSet() throws Exception {

    }
}
