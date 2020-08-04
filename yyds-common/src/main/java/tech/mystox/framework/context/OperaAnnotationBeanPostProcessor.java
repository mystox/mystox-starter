package tech.mystox.framework.context;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.ConfigurationClassPostProcessor;
import org.springframework.context.annotation.ScannedGenericBeanDefinition;
import org.springframework.core.type.classreading.CachingMetadataReaderFactory;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import tech.mystox.framework.OperaApplication;

import java.io.IOException;

public class OperaAnnotationBeanPostProcessor extends ConfigurationClassPostProcessor {
    Logger logger = LoggerFactory.getLogger(OperaAnnotationBeanPostProcessor.class);

    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) {
        MetadataReaderFactory metadataReaderFactory = new CachingMetadataReaderFactory();
        try {
            MetadataReader metadataReader = metadataReaderFactory.getMetadataReader(OperaApplication.class.getName());
            BeanDefinition beanDefinition = new ScannedGenericBeanDefinition(metadataReader);
            registry.registerBeanDefinition("operaApplication", beanDefinition);
        } catch (IOException e) {
            logger.error("opera application scanner init false...", e);
            if (logger.isDebugEnabled())
                e.printStackTrace();
        }
        processConfigBeanDefinitions(registry);
    }
}
