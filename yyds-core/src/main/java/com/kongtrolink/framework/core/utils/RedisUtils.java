package com.kongtrolink.framework.core.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.TimeUnit;


/**
 * Created by mystoxlol on 2019/3/26, 19:23.
 * company: kongtrolink
 * description:
 * update record:
 */
@Component
public class RedisUtils
{
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    public <T> T get(String key, Class<T> clazz)
    {
        return JSONObject.toJavaObject((JSON) get(key), clazz);
    }

    public Object get(String key)
    {
        return redisTemplate.opsForValue().get(key);

    }
    public String getString(String key)
    {
        return stringRedisTemplate.opsForValue().get(key);
    }

    public <T> List<T> getArray(String key, Class<T> clazz)
    {
        return JSON.parseArray(stringRedisTemplate.opsForValue().get(key), clazz);
    }

    public <T> T getHash(String hashTable, String key, Class<T> clazz)
    {
        return JSONObject.toJavaObject((JSON) getHash(hashTable,key), clazz);
    }

    public Object getHash(String hashTable, String key)
    {
        return redisTemplate.opsForHash().get(hashTable,key);

    }

    public void setHash(String hashTable, String key, Object value)
    {
        redisTemplate.opsForHash().putIfAbsent(hashTable,key,value);
    }
    public void set(String key, Object value)
    {
        redisTemplate.opsForValue().setIfAbsent(key,value);
    }
    public void deleteHash(String hashTable, String... keys)
    {
        redisTemplate.opsForHash().delete(hashTable, keys);
    }
    public void expired(String key, long time, TimeUnit unit)
    {
        redisTemplate.expire(key, time, TimeUnit.SECONDS);
    }
}
