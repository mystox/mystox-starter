package com.kongtrolink.framework.base;

import com.mongodb.MongoClientURI;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.MongoDbFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoDbFactory;
import org.springframework.data.mongodb.core.convert.*;
import org.springframework.data.mongodb.core.mapping.MongoMappingContext;
import java.net.UnknownHostException;

/**
 * Created by mystoxlol on 2018/8/1, 14:57.
 * company: kongtrolink
 * description:
 * update record:
 */
@Configuration
public class MongoConfig
{
    @Value("${mongodb.uri}")
    private String uri;

    @Bean
    public MongoTemplate mongoTemplate(MongoDbFactory mongoDbFactory, MappingMongoConverter converter) throws Exception {

        //额外连接参数设置
        return new MongoTemplate(mongoDbFactory,converter);
    }

    @Bean
    MongoDbFactory mongoDbFactory() throws UnknownHostException {
        SimpleMongoDbFactory simpleMongoDbFactory = new SimpleMongoDbFactory(new MongoClientURI(this.uri));
        return simpleMongoDbFactory;
    }

    @Bean
    public MongoMappingContext mappingContext() {
        return new MongoMappingContext();
    }

    @Bean
    public MappingMongoConverter mappingMongoConverter(MongoDbFactory mongoDbFactory, MongoMappingContext mappingContext, BeanFactory beanFactory) {
        DbRefResolver dbRefResolver = new DefaultDbRefResolver(mongoDbFactory);
        MappingMongoConverter mappingConverter = new MappingMongoConverter(dbRefResolver, mappingContext);
        try {
            mappingConverter.setCustomConversions(beanFactory.getBean(CustomConversions.class));
        } catch (NoSuchBeanDefinitionException ignore) {
            ignore.printStackTrace();
        }
        // Don't save _class to mongo
        mappingConverter.setTypeMapper(new DefaultMongoTypeMapper(null));
        return mappingConverter;
    }

}
