package com.kongtrolink.framework.core;

import com.alibaba.fastjson.JSONObject;
import com.kongtrolink.framework.entity.AckEnum;
import com.kongtrolink.framework.entity.RegisterSub;
import com.kongtrolink.framework.entity.UnitHead;
import com.kongtrolink.framework.stereotype.OperaCode;
import com.kongtrolink.framework.stereotype.Register;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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

import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by mystoxlol on 2019/8/28, 15:40.
 * company: kongtrolink
 * description:
 * update record:
 */
@Service
public class LocalServiceScanner implements EnvironmentCapable, ServiceScanner {

    private Logger logger = LoggerFactory.getLogger(LocalServiceScanner.class);

    static final String DEFAULT_RESOURCE_PATTERN = "**/*.class";
    String resourcePattern = DEFAULT_RESOURCE_PATTERN;

    @Value("${register.scanBasePackage:com.kongtrolink.framework}")
    private String basePackage;


    @Autowired
    Environment environment;

    @Override
    public Environment getEnvironment() {
        if (this.environment == null) {
            this.environment = new StandardEnvironment();
        }
        return this.environment;
    }

    //TODO 扫描“basePackagePath”包下的资源，将对应注解资源封装。
    @Override
    public List<RegisterSub> getSubList() {
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
                        sub.setExecuteUnit(UnitHead.LOCAL + className + "/" + method.getName());
                        sub.setOperaCode(code);
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


}
