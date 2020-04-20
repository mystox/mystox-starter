package com.kongtrolink.framework.scloud.config;

import com.alibaba.fastjson.support.spring.FastJsonRedisSerializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisClusterConfiguration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.util.StringUtils;
import redis.clients.jedis.JedisPoolConfig;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.UnknownHostException;

/**
 * Created by mystoxlol on 2019/3/26, 15:00.
 * company: kongtrolink
 * description:
 * update record:
 */
@Configuration("RedisConfigBusiness")
@EnableConfigurationProperties(BusinessRedisProperties.class)
public class BusinessRedisConfig
{

    @Autowired
    private BusinessRedisProperties businessRedisProperties;

    @Bean("redisTemplateBusiness")
    @ConditionalOnMissingBean(name = "redisTemplateBusiness")
    public RedisTemplate<Object, Object> redisTemplateBusiness(
            @Qualifier(value = "jedisConnectionFactoryBusiness")RedisConnectionFactory jedisConnectionFactoryBusiness)
            throws UnknownHostException
    {
        RedisTemplate<Object, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(jedisConnectionFactoryBusiness);
        FastJsonRedisSerializer fastJsonRedisSerializer = new FastJsonRedisSerializer(Object.class);
        template.setKeySerializer(new StringRedisSerializer());
        template.setHashKeySerializer(new StringRedisSerializer());
        template.setHashValueSerializer(fastJsonRedisSerializer);
        template.setValueSerializer(fastJsonRedisSerializer);
        template.afterPropertiesSet();
        return template;
    }

    @Bean(name = "jedisConnectionFactoryBusiness")
//    @Primary
    public RedisConnectionFactory jedisConnectionFactoryBusiness()
    {

        JedisConnectionFactory factory = createJedisConnectionFactory();
        if (StringUtils.hasText(this.businessRedisProperties.getUrl()))
        {
            configureConnectionFromUrl(factory);
        } else
        {
            factory.setHostName(this.businessRedisProperties.getHost());
            factory.setPort(this.businessRedisProperties.getPort());
            factory.setDatabase(this.businessRedisProperties.getDatabase());
            if (this.businessRedisProperties.getPassword() != null)
            {
                factory.setPassword(this.businessRedisProperties.getPassword());
            }
        }
        return factory;
    }

    private void configureConnectionFromUrl(JedisConnectionFactory factory)
    {
        String url = this.businessRedisProperties.getUrl();
        if (url.startsWith("rediss://"))
        {
            factory.setUseSsl(true);
        }
        try
        {
            URI uri = new URI(url);
            factory.setHostName(uri.getHost());
            factory.setPort(uri.getPort());
            if (uri.getUserInfo() != null)
            {
                String password = uri.getUserInfo();
                int index = password.indexOf(":");
                if (index >= 0)
                {
                    password = password.substring(index + 1);
                }
                factory.setPassword(password);
            }
        } catch (URISyntaxException ex)
        {
            throw new IllegalArgumentException("Malformed 'spring.redis.url' " + url,
                    ex);
        }
    }


    private JedisConnectionFactory createJedisConnectionFactory()
    {
        JedisPoolConfig poolConfig = (this.businessRedisProperties.getPool() != null
                ? jedisPoolConfig() : new JedisPoolConfig());

        if (getClusterConfiguration() != null)
        {
            return new JedisConnectionFactory(getClusterConfiguration(), poolConfig);
        }
        return new JedisConnectionFactory(poolConfig);
    }

    private JedisPoolConfig jedisPoolConfig()
    {
        JedisPoolConfig config = new JedisPoolConfig();
        RedisProperties.Pool props = this.businessRedisProperties.getPool();
        config.setMaxTotal(props.getMaxActive());
        config.setMaxIdle(props.getMaxIdle());
        config.setMinIdle(props.getMinIdle());
        config.setMaxWaitMillis(props.getMaxWait());
        return config;
    }

    /**
     * Create a {@link RedisClusterConfiguration} if necessary.
     *
     * @return {@literal null} if no cluster settings are set.
     */
    protected final RedisClusterConfiguration getClusterConfiguration()
    {
        if (this.businessRedisProperties.getCluster() == null)
        {
            return null;
        }
        RedisProperties.Cluster clusterProperties = this.businessRedisProperties.getCluster();
        RedisClusterConfiguration config = new RedisClusterConfiguration(
                clusterProperties.getNodes());

        if (clusterProperties.getMaxRedirects() != null)
        {
            config.setMaxRedirects(clusterProperties.getMaxRedirects());
        }
        return config;
    }

}