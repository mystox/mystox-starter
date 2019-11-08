package com.kongtrolink.framework.register.config;

import org.springframework.beans.factory.config.YamlPropertiesFactoryBean;
import org.springframework.core.env.PropertiesPropertySource;
import org.springframework.core.env.PropertySource;
import org.springframework.core.io.support.EncodedResource;
import org.springframework.core.io.support.PropertySourceFactory;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

/**
 * Created by mystox on 2019/8/13, 17:28.
 * company: kongtrolink
 * description:
 * update record:
 */
public class YamlPropertySourceFactory implements PropertySourceFactory
{
    @Override
    public PropertySource<?> createPropertySource(String s, EncodedResource encodedResource) throws IOException
    {
        Properties propertiesFromYaml = loadYamlIntoProperties(encodedResource);
        String sourceName = s != null ? s : encodedResource.getResource().getFilename();
        return new PropertiesPropertySource(sourceName, propertiesFromYaml);
    }

    private Properties loadYamlIntoProperties(EncodedResource resource) throws FileNotFoundException
    {
        try
        {
            YamlPropertiesFactoryBean factory = new YamlPropertiesFactoryBean();
            factory.setResources(resource.getResource());
            factory.afterPropertiesSet();
            return factory.getObject();
        } catch (IllegalStateException e)
        {
            Throwable cause = e.getCause();
            if (cause instanceof FileNotFoundException)
                throw (FileNotFoundException) e.getCause();
            throw e;
        }
    }

}
