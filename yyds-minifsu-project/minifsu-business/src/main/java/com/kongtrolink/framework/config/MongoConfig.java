package com.kongtrolink.framework.config;

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
    public MongoTemplate mongoTemplate(MongoDbFactory factory) throws Exception {
        //额外连接参数设置
        return new MongoTemplate(factory);
    }

    @Bean
    MongoDbFactory mongoDbFactory() throws UnknownHostException {
        SimpleMongoDbFactory simpleMongoDbFactory = new SimpleMongoDbFactory(new MongoClientURI(this.uri));
        return simpleMongoDbFactory;
    }

//    @Bean
//    MappingMongoConverter mappingMongoConverter(MongoDbFactory factory) {
//        DbRefResolver dbRefResolver = new DefaultDbRefResolver(factory);
//        return  new MappingMongoConverter(dbRefResolver, new MongoMappingContext());
//    }
//

    @Bean
    public MongoMappingContext mappingContext() {
        return new MongoMappingContext();
    }

    @Bean
    public MappingMongoConverter mappingMongoConverter(MongoDbFactory factory, MongoMappingContext context, BeanFactory beanFactory) {
        DbRefResolver dbRefResolver = new DefaultDbRefResolver(factory);
        MappingMongoConverter mappingConverter = new MappingMongoConverter(dbRefResolver, context);
        try {
            mappingConverter.setCustomConversions(beanFactory.getBean(CustomConversions.class));
        } catch (NoSuchBeanDefinitionException ignore) {
        }

        // Don't save _class to mongo
        mappingConverter.setTypeMapper(new DefaultMongoTypeMapper(null));

        return mappingConverter;
    }

}
