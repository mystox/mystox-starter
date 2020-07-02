package tech.mystox.framework.core;

import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.env.Environment;
import org.springframework.core.env.EnvironmentCapable;
import org.springframework.core.env.StandardEnvironment;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.ClassMetadata;
import org.springframework.core.type.classreading.CachingMetadataReaderFactory;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;
import tech.mystox.framework.entity.AckEnum;
import tech.mystox.framework.entity.RegisterSub;
import tech.mystox.framework.entity.UnitHead;
import tech.mystox.framework.exception.RegisterAnalyseException;
import tech.mystox.framework.stereotype.OperaCode;
import tech.mystox.framework.stereotype.Register;

import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by mystoxlol on 2019/8/28, 15:40.
 * company: mystox
 * description:
 * update record:
 */
@Service
public class LocalServiceScanner implements EnvironmentCapable, ServiceScanner,ApplicationContextAware {

    private Logger logger = LoggerFactory.getLogger(LocalServiceScanner.class);

    static final String DEFAULT_RESOURCE_PATTERN = "**/*.class";
    String resourcePattern = DEFAULT_RESOURCE_PATTERN;

    @Value("${register.scanBasePackage:tech.mystox.framework}")
    private String basePackage;


    @Autowired
    Environment environment;
    private ApplicationContext applicationContext;

    @Override
    public Environment getEnvironment() {
        if (this.environment == null) {
            this.environment = new StandardEnvironment();
        }
        return this.environment;
    }

    /**
     * 扫描“basePackagePath”包下的资源，将对应注解资源封装。
     * @return
     */
    @Override
    public List<RegisterSub> getSubList() {
        Set<String> operaSet = new HashSet<String>();
        List<RegisterSub> subList = new ArrayList<>();
        String basePackagePath = ClassUtils.convertClassNameToResourcePath(getEnvironment().resolveRequiredPlaceholders(basePackage));
        String packageSearchPath = ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX +
                basePackagePath + '/' + resourcePattern;
        try {
            ResourcePatternResolver resourcePatternResolver = new PathMatchingResourcePatternResolver();
            Resource[] resources = resourcePatternResolver.getResources(packageSearchPath);
            for (Resource resource : resources) {
                boolean readable = resource.isReadable();
                if (!readable) continue;
                MetadataReaderFactory metadataReaderFactory = new CachingMetadataReaderFactory();
                MetadataReader metadataReader = metadataReaderFactory.getMetadataReader(resource);
                AnnotationMetadata annotationMetadata = metadataReader.getAnnotationMetadata();
                boolean b = annotationMetadata.hasAnnotation(Register.class.getName());
                if (b) {
                    ClassMetadata classMetadata = metadataReader.getClassMetadata();
                    String className = classMetadata.getClassName();
                    Class<?> aClass = Class.forName(className);
                    try {
                        Object springBean = applicationContext.getBean(aClass);
                        if (springBean == null) {
                            logger.debug("no such bean definition, jump to next register");
                            continue;
                        }
                    } catch (BeansException e) {
                        logger.debug("no such bean definition, jump to next register");
                        continue;
                    }
                    Method[] methods = aClass.getMethods();
                    for (Method method : methods) {
                        RegisterSub sub = new RegisterSub();
                        OperaCode annotation = method.getAnnotation(OperaCode.class);
                        if (annotation == null) continue;
                        String code = annotation.code();
                        if (StringUtils.isEmpty(code)) {
                            code = method.getName();
                        }
                        Type genericReturnType = method.getGenericReturnType();
                        sub.setAck("void".equals(genericReturnType.getTypeName()) ? AckEnum.NA : AckEnum.ACK);
                        Class<?>[] parameterTypes = method.getParameterTypes();
                        sub.setExecuteUnit(UnitHead.LOCAL + className + "/" + method.getName()+"/"+JSONObject.toJSON(parameterTypes));
                        sub.setOperaCode(code);
                        if (operaSet.contains(code)) throw new RegisterAnalyseException("opera duplicate:"+code);
                        operaSet.add(code);
                        subList.add(sub);
                    }
                }
            }
            logger.info("local scanner result: [{}]", JSONObject.toJSONString(subList));
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return subList;
    }

    @Override
    public boolean addSub(RegisterSub registerSub) {
        return false;
    }

    @Override
    public boolean deleteSub(String operaCode) {
        return false;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
