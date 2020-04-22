package com.kongtrolink;

import com.alibaba.fastjson.JSONObject;
import com.kongtrolink.framework.base.Contant;
import com.kongtrolink.framework.base.DateUtil;
import com.kongtrolink.framework.core.utils.RedisUtils;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
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
    @Autowired
    MongoTemplate mongoTemplate;

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

        String key = "yytd" + Contant.UNDERLINE + "zhydJson2269966";
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("flag", "1");
        jsonObject.put("targetLevel", "2");
        jsonObject.put("treport", new Date());
        redisUtils.set(key, jsonObject);

        Map<String, JSONObject> map = new HashMap<>();
        map.put(key, jsonObject);
        redisUtils.mset(map);

        boolean hasKey = redisUtils.hasKey(key);
        System.out.println("hasKey:" + hasKey + "; key:" + key);
        JSONObject returnJson = (JSONObject) redisUtils.get(key);
        System.out.println("returnJson:" + returnJson);
        if(null == returnJson){
            System.out.println("返回为空：returnJson：" + returnJson);
        }else {
            String flag = returnJson.getString("flag");
            Date treport = returnJson.getDate("treport");
            Integer targetLevel = returnJson.getInteger("targetLevel");
            System.out.println("flag:" + flag + "; treport:" + DateUtil.format(treport) + "; targetLevel:" + targetLevel);
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
//        boolean mset = redisUtils.mset(map);
//        System.out.println("mset:" + mset);


    }

    @Test
    public void testIndex(){
        String table = "yytd_TOWER_SERVER_alarm_history_2019_40";
//        table = "alarm_current";
        DBCollection collection = mongoTemplate.getCollection(table);
        boolean hasIndex = hadIndex(collection);
        if(!hasIndex){
            collection.createIndex("key");
            hadIndex(collection);
        }
    }

    public boolean hadIndex(DBCollection collection){
        List<DBObject> indexInfo = collection.getIndexInfo();
        for(DBObject dbObject : indexInfo){
            System.out.println("dbObject:" + dbObject);
            Object key = dbObject.get("key");
            String keyStr = key.toString();
            if(keyStr.contains("key")){
                System.out.println("table:" + collection.getName() + " 包含索引");
                return true;
            }
        }
        System.out.println("table:" + collection.getName() + " 不包含索引key");
        return false;
    }

}
