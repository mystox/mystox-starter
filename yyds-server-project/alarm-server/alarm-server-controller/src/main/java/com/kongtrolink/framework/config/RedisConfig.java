package com.kongtrolink.framework.config;

import com.alibaba.fastjson.support.spring.FastJsonRedisSerializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisClusterConfiguration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;
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
@Configuration
@EnableConfigurationProperties(RedisProperties.class)
@EnableRedisRepositories(basePackages = {"com.kongtrolink.auth.repository.redis"})
public class RedisConfig
{

    @Autowired
    private RedisProperties properties;

    @Bean
    @ConditionalOnMissingBean(name = "redisTemplate")
    public RedisTemplate<Object, Object> redisTemplate(
            RedisConnectionFactory redisConnectionFactory)
            throws UnknownHostException
    {
        RedisTemplate<Object, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(redisConnectionFactory);
        FastJsonRedisSerializer fastJsonRedisSerializer = new FastJsonRedisSerializer(Object.class);
        template.setKeySerializer(new StringRedisSerializer());
        template.setHashKeySerializer(new StringRedisSerializer());
        template.setHashValueSerializer(fastJsonRedisSerializer);
        template.setValueSerializer(fastJsonRedisSerializer);
        template.afterPropertiesSet();
        return template;
    }
    @Bean
    public RedisConnectionFactory jedisConnectionFactory()
    {

        JedisConnectionFactory factory = createJedisConnectionFactory();
        if (StringUtils.hasText(this.properties.getUrl()))
        {
            configureConnectionFromUrl(factory);
        } else
        {
            factory.setHostName(this.properties.getHost());
            factory.setPort(this.properties.getPort());
            factory.setDatabase(this.properties.getDatabase());
            if (this.properties.getPassword() != null)
            {
                factory.setPassword(this.properties.getPassword());
            }
        }
        return factory;
    }

    private void configureConnectionFromUrl(JedisConnectionFactory factory)
    {
        String url = this.properties.getUrl();
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
        JedisPoolConfig poolConfig = (this.properties.getPool() != null
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
        RedisProperties.Pool props = this.properties.getPool();
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
        if (this.properties.getCluster() == null)
        {
            return null;
        }
        RedisProperties.Cluster clusterProperties = this.properties.getCluster();
        RedisClusterConfiguration config = new RedisClusterConfiguration(
                clusterProperties.getNodes());

        if (clusterProperties.getMaxRedirects() != null)
        {
            config.setMaxRedirects(clusterProperties.getMaxRedirects());
        }
        return config;
    }

}
