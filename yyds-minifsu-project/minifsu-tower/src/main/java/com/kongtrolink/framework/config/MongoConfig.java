package com.kongtrolink.framework.config;

import com.mongodb.MongoClientURI;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.MongoDbFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoDbFactory;

import java.net.UnknownHostException;

@Configuration
public class MongoConfig {
    @Value("${mongodb.uri}")
    private String uri;

    public final static String T_VPN = "t_vpn";
    public final static String T_STATION = "t_station";
    public final static String T_DEVICE_LIST = "t_device_list";
    public final static String T_LOGIN_PARAM = "t_login_param";
    public final static String T_DEV_TYPE_MAP = "t_dev_type_map";

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
}
