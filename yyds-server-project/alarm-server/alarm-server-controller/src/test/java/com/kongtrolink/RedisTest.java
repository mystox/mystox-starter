package com.kongtrolink;

import com.kongtrolink.framework.base.Contant;
import com.kongtrolink.framework.utils.RedisUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.*;

/**
 * @Auther: liudd
 * @Date: 2019/11/7 09:01
 * @Description:
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class RedisTest {

    @Autowired
    RedisUtils redisUtils;
    @Autowired
    RedisTemplate redisTemplate;

    private static final Logger logger = LoggerFactory.getLogger(RedisTest.class);

    @Test
    public void testSave(){
        String key = "yytd" + Contant.UNDERLINE + "zhyd" + Contant.COLON + "10010_1021006" + Contant.UNDERLINE + "863111";
        String value = "1";
        boolean result = redisUtils.set(key, value);
        logger.info("key:{}, value:{}, result:{}", key, value, result);
    }

    @Test
    public void testGet(){
        String key = "yytd" + Contant.UNDERLINE + "zhyd" + Contant.COLON + "10010_1021006" + Contant.UNDERLINE + "863111";
        Object obj = redisUtils.get(key);
        System.out.println("obj class:" + obj.getClass().getName());
        String value  = (String) obj ;
        System.out.println(value);

        boolean exist = redisUtils.hasKey(key);
        System.out.println("exist:" + exist);

        key = "yytd" + Contant.UNDERLINE + "zhyd";
        Set<String> values = redisUtils.keys(key + "*");
        for(String str : values){
            System.out.println("key:" + key + "; value:" + value);
        }
    }

    @Test
    public void testMulti(){
        String key1 = "yytd" + Contant.UNDERLINE + "zhyd" + Contant.COLON + "10010_1021006" + Contant.UNDERLINE + "863111";
        String key2 = "yytd" + Contant.UNDERLINE + "TOWER_SERVER" + Contant.COLON + "10010_1021006" + Contant.UNDERLINE + "863111";
        List<String> keys = new ArrayList<>();
        keys.add(key1);
        keys.add(key2);
        List<String> mget = redisUtils.mget(keys);
        for(String o : mget){
            System.out.println("o.class:" + o.getClass().getName() + "; O:" + o);
        }

        Map<String, String> map = new HashMap<>();
        map.put(key1, "key1value");
        map.put(key2, "key2-value");
        boolean mset = redisUtils.mset(map);
        System.out.println("mset:" + mset);


    }

}
