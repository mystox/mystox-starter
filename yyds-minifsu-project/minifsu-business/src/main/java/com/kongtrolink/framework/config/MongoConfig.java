package com.kongtrolink.framework.config;

import com.mongodb.MongoClientURI;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoDbFactory;

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
    public MongoTemplate mongoTemplate() throws Exception {
        //额外连接参数设置
        SimpleMongoDbFactory simpleMongoDbFactory = new SimpleMongoDbFactory(new MongoClientURI(this.uri));
        return new MongoTemplate(simpleMongoDbFactory);
    }
}
