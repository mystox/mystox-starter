package tech.mystox.framework.config;

import org.springframework.aop.framework.ProxyFactory;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import tech.mystox.framework.entity.OperaBean;
import tech.mystox.framework.entity.OperaClassIdBean;
import tech.mystox.framework.stereotype.Opera;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by mystoxlol on 2020/6/22, 11:29.
 * company:
 * description:
 * update record:
 */
@Configuration
public class OperaAutoConfig {
    private static final Map<OperaClassIdBean, Object> OPERA_CONSUMER_MAP =
            new ConcurrentHashMap<OperaClassIdBean, Object>();

    private final ApplicationContext applicationContext;

    public OperaAutoConfig(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    public static Object getOperaBean(OperaClassIdBean classIdBean) {
        return OPERA_CONSUMER_MAP.get(classIdBean);
    }

    @Bean
    public BeanPostProcessor beanPostProcessor() {
        return new BeanPostProcessor() {

            @Override
            public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
                Class<?> objClz;
                if (AopUtils.isAopProxy(bean)) {
                    objClz = AopUtils.getTargetClass(bean);
                } else {
                    objClz = bean.getClass();
                }

                try {
                    //todo 不同operaType类型应该注册多个
                    for (Field field : objClz.getDeclaredFields()) {
                        Opera opera = field.getAnnotation(Opera.class);
                        if (opera != null) {
                            //获取消费bean实体
                            OperaBean<Object> operaBean = OperaAutoConfig.this.getConsumerBean(beanName, field, opera);
                            Class<?> interfaceClass = operaBean.getInterfaceClass();
                            String group = operaBean.getGroup();
                            String version = operaBean.getVersion();
                            OperaClassIdBean classIdBean = new OperaClassIdBean(interfaceClass, group, version, opera.operaType(),opera.operaTimeout());
                            Object yydsOpera = OPERA_CONSUMER_MAP.get(classIdBean);
                            if (yydsOpera == null) {
                                synchronized (this) {
                                    yydsOpera = OPERA_CONSUMER_MAP.get(classIdBean);
                                    if (yydsOpera == null) {
                                        operaBean.afterPropertiesSet();
                                        yydsOpera = operaBean.getObject();
                                        OperaAutoConfig.OPERA_CONSUMER_MAP.put(classIdBean,
                                                yydsOpera);
                                    }
                                }
                            }
                            field.setAccessible(true);
                            field.set(bean, yydsOpera);
                        }
                    }
                } catch (Exception e) {
                    throw new BeanCreationException(beanName, e);
                }
                return bean;
            }

            @Override
            public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
                return bean;
            }
        };
    }

    @Bean
    public ProxyFactory proxyFactory() {
        return new ProxyFactory();
    }

    /**
     * init consumer bean
     *
     * @param opera          opera
     * @return ReferenceBean<T>
     * @throws BeansException BeansException
     */
    private <T> OperaBean<T> getConsumerBean(String beanName, Field field, Opera opera)
            throws BeansException {
        OperaBean<T> operaBean = new OperaBean<T>(opera);
        // if ((opera.interfaceClass() == null || opera.interfaceClass() == void.class)
        //         && (opera.interfaceName() == null || "".equals(opera.interfaceName()))) {
        operaBean.setInterface(field.getType());
        // }

//        Environment environment = this.applicationContext.getEnvironment();
        // String application = opera.application();
        // operaBean.setApplication(this.parseApplication(application, this.properties, environment,
        //         beanName, field.getName(), "application", application));
        // String module = opera.module();
        // operaBean.setModule(this.parseModule(module, this.properties, environment, beanName,
        //         field.getName(), "module", module));
        // String[] registries = opera.registry();
        // operaBean.setRegistries(this.parseRegistries(registries, this.properties, environment,
        //         beanName, field.getName(), "registry"));
        // String monitor = opera.monitor();
        // operaBean.setMonitor(this.parseMonitor(monitor, this.properties, environment, beanName,
        //         field.getName(), "monitor", monitor));
        // String consumer = opera.consumer();
        // operaBean.setConsumer(this.parseConsumer(consumer, this.properties, environment, beanName,
        //         field.getName(), "consumer", consumer));

        operaBean.setApplicationContext(OperaAutoConfig.this.applicationContext);
        return operaBean;
    }
}
