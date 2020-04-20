package com.kongtrolink.framework.scloud.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.*;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;


/**
 *
 * 基于spring和redis的redisTemplate工具类
 * 针对所有的hash 都是以h开头的方法
 * 针对所有的Set 都是以s开头的方法                    不含通用方法
 * 针对所有的List 都是以l开头的方法
 */
@Component
public class BusinessRedisUtils
{
    @Resource(name = "redisTemplateBusiness")
    private RedisTemplate redisTemplate;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    public <T> T get(String key, Class<T> clazz)
    {
        return JSONObject.toJavaObject((JSON) get(key), clazz);
    }

    public String getString(String key)
    {
        return stringRedisTemplate.opsForValue().get(key);
    }

    public <T> List<T> getArray(String key, Class<T> clazz)
    {
        return JSON.parseArray(stringRedisTemplate.opsForValue().get(key), clazz);
    }

    public Set<String> keys(String keyPattern) {
       return redisTemplate.keys(keyPattern);
    }

    public <T> T getHash(String hashTable, String key, Class<T> clazz)
    {
        return JSONObject.toJavaObject((JSON) getHash(hashTable,key), clazz);
    }

    public Object getHash(String hashTable, String key)
    {
        return redisTemplate.opsForHash().get(hashTable,key);

    }
    public void deleteHash(String hashTable, String... keys)
    {
        redisTemplate.opsForHash().delete(hashTable, keys);
    }
    public void expired(String key, long time, TimeUnit unit)
    {
        redisTemplate.expire(key, time, unit);
    }
    public void setHash(String hashTable, String key, Object value)
    {
        redisTemplate.opsForHash().put(hashTable,key,value);
    }
    //=============================common============================
    /**
     * 指定缓存失效时间
     * @param key 键
     * @param time 时间(秒)
     * @return
     */
    public boolean expire(String key,long time){
        try {
            if(time>0){
                redisTemplate.expire(key, time, TimeUnit.SECONDS);
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 根据key 获取过期时间
     * @param key 键 不能为null
     * @return 时间(秒) 返回0代表为永久有效
     */
    public long getExpire(String key){
        return redisTemplate.getExpire(key,TimeUnit.SECONDS);
    }

    /**
     * 判断key是否存在
     * @param key 键
     * @return true 存在 false不存在
     */
    public boolean hasKey(String key){
        try {
            return redisTemplate.hasKey(key);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 删除缓存
     * @param key 可以传一个值 或多个
     */
    @SuppressWarnings("unchecked")
    public void del(String ... key){
        if(key!=null&&key.length>0){
            if(key.length==1){
                redisTemplate.delete(key[0]);
            }else{
                redisTemplate.delete(CollectionUtils.arrayToList(key));
            }
        }
    }

    //============================String=============================
    /**
     * 普通缓存获取
     * @param key 键
     * @return 值
     */
    public Object get(String key){
        return key==null?null:redisTemplate.opsForValue().get(key);
    }
    /**
     * 普通缓存放入
     * @param key 键
     * @param value 值
     * @return true成功 false失败
     */
    public boolean set(String key,Object value) {
        try {
            redisTemplate.opsForValue().set(key, value);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

    }

    /**
     * 普通缓存放入并设置时间
     * @param key 键
     * @param value 值
     * @param time 时间(秒) time要大于0 如果time小于等于0 将设置无限期
     * @return true成功 false 失败
     */
    public boolean set(String key,Object value,long time){
        try {
            if(time>0){
                redisTemplate.opsForValue().set(key, value, time, TimeUnit.SECONDS);
            }else{
                set(key, value);
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }



    /**
     * 递增
     * @param key 键
     * @param delta by 要增加几(大于0)
     * @return long珂ux
     */
    public long incr(String key, long delta){
        if(delta<0){
            throw new RuntimeException("递增因子必须大于0");
        }
        return redisTemplate.opsForValue().increment(key, delta);
    }

    /**
     * 递减
     * @param key 键
     * @return
     */
    public long decr(String key, long delta){
        if(delta<0){
            throw new RuntimeException("递减因子必须大于0");
        }
        return redisTemplate.opsForValue().increment(key, -delta);
    }

    public boolean mset(Map<String ,JSONObject> stringMap){
        try{
            redisTemplate.opsForValue().multiSet(stringMap);
            return true;
        }catch (Exception e){
            return false;
        }
    }

    public  List<String> mget(List<String> keys){
        List<String> list = redisTemplate.opsForValue().multiGet(keys);
        return list;
    }

    //================================Map=================================
    /**
     * HashGet
     * @param key 键 不能为null
     * @param item 项 不能为null
     * @return 值
     */
    public Object hget(String key,String item){
        return redisTemplate.opsForHash().get(key, item);
    }

    /**
     * 获取hashKey对应的所有键值
     * @param key 键
     * @return 对应的多个键值
     */
    public Map hmget(String key){
        return redisTemplate.opsForHash().entries(key);
    }

    /**
     * HashSet
     * @param key 键
     * @param map 对应多个键值
     * @return true 成功 false 失败
     */
    public boolean hmset(String key, Map<String,JSONObject> map){
        try {
            redisTemplate.opsForHash().putAll(key, map);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * HashSet 并设置时间
     * @param key 键
     * @param map 对应多个键值
     * @param time 时间(秒)
     * @return true成功 false失败
     */
    public boolean hmset(String key, Map<String,Object> map, long time){
        try {
            redisTemplate.opsForHash().putAll(key, map);
            if(time>0){
                expire(key, time);
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 向一张hash表中放入数据,如果不存在将创建
     * @param key 键
     * @param item 项
     * @param value 值
     * @return true 成功 false失败
     */
    public boolean hset(String key,String item,Object value) {
        try {
            redisTemplate.opsForHash().put(key, item, value);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 向一张hash表中放入数据,如果不存在将创建
     * @param key 键
     * @param item 项
     * @param value 值
     * @param time 时间(秒)  注意:如果已存在的hash表有时间,这里将会替换原有的时间
     * @return true 成功 false失败
     */
    public boolean hset(String key,String item,Object value,long time) {
        try {
            redisTemplate.opsForHash().put(key, item, value);
            if(time>0){
                expire(key, time);
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 删除hash表中的值
     * @param key 键 不能为null
     * @param item 项 可以使多个 不能为null
     */
    public void hdel(String key, Object... item){
        redisTemplate.opsForHash().delete(key,item);
    }

    /**
     * 判断hash表中是否有该项的值
     * @param key 键 不能为null
     * @param item 项 不能为null
     * @return true 存在 false不存在
     */
    public boolean hHasKey(String key, String item){
        return redisTemplate.opsForHash().hasKey(key, item);
    }

    /**
     * hash递增 如果不存在,就会创建一个 并把新增后的值返回
     * @param key 键
     * @param item 项
     * @param by 要增加几(大于0)
     * @return
     */
    public double hincr(String key, String item,double by){
        return redisTemplate.opsForHash().increment(key, item, by);
    }

    /**
     * hash递减
     * @param key 键
     * @param item 项
     * @param by 要减少记(小于0)
     * @return
     */
    public double hdecr(String key, String item,double by){
        return redisTemplate.opsForHash().increment(key, item,-by);
    }

    //============================set=============================
    /**
     * 根据key获取Set中的所有值
     * @param key 键
     * @return
     */
    public Set<Object> sGet(String key){
        try {
            return redisTemplate.opsForSet().members(key);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 根据value从一个set中查询,是否存在
     * @param key 键
     * @param value 值
     * @return true 存在 false不存在
     */
    public boolean sHasKey(String key,Object value){
        try {
            return redisTemplate.opsForSet().isMember(key, value);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 将数据放入set缓存
     * @param key 键
     * @param values 值 可以是多个
     * @return 成功个数
     */
    public long sSet(String key, Object...values) {
        try {
            return redisTemplate.opsForSet().add(key, values);
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    /**
     * 将set数据放入缓存
     * @param key 键
     * @param time 时间(秒)
     * @param values 值 可以是多个
     * @return 成功个数
     */
    public long sSetAndTime(String key,long time,Object...values) {
        try {
            Long count = redisTemplate.opsForSet().add(key, values);
            if(time>0) expire(key, time);
            return count;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    /**
     * 获取set缓存的长度
     * @param key 键
     * @return
     */
    public long sGetSetSize(String key){
        try {
            return redisTemplate.opsForSet().size(key);
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    /**
     * 移除值为value的
     * @param key 键
     * @param values 值 可以是多个
     * @return 移除的个数
     */
    public long setRemove(String key, Object ...values) {
        try {
            Long count = redisTemplate.opsForSet().remove(key, values);
            return count;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }
    //===============================list=================================

    /**
     * 获取list缓存的内容
     * @param key 键
     * @param start 开始
     * @param end 结束  0 到 -1代表所有值
     * @return
     */
    public List<Object> lGet(String key,long start, long end){
        try {
            return redisTemplate.opsForList().range(key, start, end);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 获取list缓存的长度
     * @param key 键
     * @return
     */
    public long lGetListSize(String key){
        try {
            return redisTemplate.opsForList().size(key);
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    /**
     * 通过索引 获取list中的值
     * @param key 键
     * @param index 索引  index>=0时， 0 表头，1 第二个元素，依次类推；index<0时，-1，表尾，-2倒数第二个元素，依次类推
     * @return
     */
    public Object lGetIndex(String key,long index){
        try {
            return redisTemplate.opsForList().index(key, index);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 将list 左放入缓存
     * @param key 键
     * @param value 值
     * @return boolean类
     */
    public boolean llSet(String key, Object value) {
        try {
            redisTemplate.opsForList().leftPush(key, value);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 将list 右 放入缓存
     * @param key 键
     * @param value 值
     * @return  boolean类
     */
    public boolean lrSet(String key, Object value) {
        try {
            redisTemplate.opsForList().rightPush(key, value);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public Object lrGet(String key) {
        try {
            return  redisTemplate.opsForList().rightPop(key);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    public Object llGet(String key) {
        try {
            return redisTemplate.opsForList().leftPop(key);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public Object llSaveGet(String key,String k2) {
        try {
            return redisTemplate.opsForList().rightPopAndLeftPush(key,k2);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    /**
     * 将list放入缓存
     * @param key 键
     * @param value 值
     * @return boolean类
     */
    public boolean lSet(String key, List<Object> value) {
        try {
            redisTemplate.opsForList().rightPushAll(key, value);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 将list放入缓存
     * @param key 键
     * @param value 值
     * @param time 时间(秒)
     * @return boolean类
     */
    public boolean lSet(String key, List<Object> value, long time) {
        try {
            redisTemplate.opsForList().rightPushAll(key, value);
            if (time > 0) expire(key, time);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 根据索引修改list中的某条数据
     * @param key 键
     * @param index 索引
     * @param value 值
     * @return boolean类
     */
    public boolean lUpdateIndex(String key, long index,Object value) {
        try {
            redisTemplate.opsForList().set(key, index, value);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 移除N个值为value
     * @param key 键
     * @param count 移除多少个
     * @param value 值
     * @return 移除的个数
     */
    public long lRemove(String key,long count,Object value) {
        try {
            return redisTemplate.opsForList().remove(key, count, value);
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    public boolean lpubish(String channel,String message){
        try{
            //其中channel必须为string，而且“序列化”策略也是StringSerializer
            //消息内容，将会根据配置文件中指定的valueSerializer进行序列化
            //本例中，默认全部采用StringSerializer
            //那么在消息的subscribe端也要对“发序列化”保持一致。
            redisTemplate.convertAndSend(channel, message);
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
        return  false;
    }

    public Set<String> getHkeys(String hashTable, String pattern) {
        ScanOptions scanOptions = ScanOptions.scanOptions().match(pattern).build();
        Set<String> result;
        Cursor<Map.Entry<Object,Object>> cursor = null;
        try {
            cursor = redisTemplate.opsForHash().scan(hashTable, scanOptions);
            result = new HashSet();
            while (cursor.hasNext())
            {
                Map.Entry<Object,Object> entry = cursor.next();
                result.add(entry.getKey().toString());
            }
        } finally {
            if (cursor!=null & cursor.isClosed()!=true)
                try {
                    cursor.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
        }
        return result;
    }

    public <T> List hashMultiGet(String hashTable, List<String> keys, Class<T> clazz) {
        List list = redisTemplate.opsForHash().multiGet(hashTable, keys);

        return (List<T>) list;
    }

    /**
     * 加锁
     * @param key 需要加锁的id
     * @param timeout 当前线程操作时的超时时间，单位毫秒，小于0则无超时时间
     *                这个地方不需要去设置redis的expire，也不需要超时后手动去删除该key，
     *                因为会存在并发的线程都会去删除，造成上一个锁失效，结果都获得锁去执行，并发操作失败了就。
     * @return 如果取到锁返回值，未取到锁返回null
     */

    public String lock(String key, int timeout) {

        int increment = timeout < 0 ? 0 : timeout;
        String value = System.currentTimeMillis() + increment + "";

        //如果key值不存在，则返回 true，且设置 value
        if (redisTemplate.opsForValue().setIfAbsent(key, value)) {
            return value;
        }

        //获取key的值，判断是是否超时
        Object curVal = redisTemplate.opsForValue().get(key);
        if (curVal != null && (timeout >= 0 && Long.parseLong(curVal.toString()) < System.currentTimeMillis())) {
            //获得之前的key值，同时设置当前的传入的value。这个地方可能几个线程同时过来，但是redis本身天然是单线程的，所以getAndSet方法还是会安全执行，
            //首先执行的线程，此时curVal当然和oldVal值相等，因为就是同一个值，之后该线程set了自己的value，后面的线程就取不到锁了
            String oldVal = redisTemplate.opsForValue().getAndSet(key, value).toString();
            if(oldVal != null && oldVal.equals(curVal)) {
                return value;
            }
        }
        return null;
    }

    /**
     * 解锁
     * @param key
     * @param value
     */
    public void unlock(String key, String value) {
        Object curVal = redisTemplate.opsForValue().get(key);
        if (curVal != null && curVal.toString().equals(value)) {
            redisTemplate.opsForValue().getOperations().delete(key);
        }
    }

    public boolean hasKeyLocked(String key,Object value) {
        return redisTemplate.opsForValue().setIfAbsent(key, value);
    }

    /**
     * 1.* redis 的事务超时
     * @param key
     * @param value
     * @return
     */
    public boolean setIfAbsentAndExpired(String key,Object value,long timeout,TimeUnit unit) {
        SessionCallback<Boolean> sessionCallback = new SessionCallback<Boolean>() {
            List<Object> exec = null;
            @Override
            @SuppressWarnings("unchecked")
            public Boolean execute(RedisOperations operations) throws DataAccessException {
                operations.multi();
                redisTemplate.opsForValue().setIfAbsent(key,value);
                redisTemplate.expire(key,timeout, TimeUnit.SECONDS);
                exec = operations.exec();
                if(exec.size() > 0) {
                    return (Boolean) exec.get(0);
                }
                return false;
            }
        };
        return (Boolean) redisTemplate.execute(sessionCallback);
    }



    public Object getAndSet(String key,Object value) {
        return redisTemplate.opsForValue().getAndSet(key, value);
    }



    public Object getAndSetAndExpired(String key,Object value,long timeout,TimeUnit unit) {


        SessionCallback sessionCallback = new SessionCallback() {
            List<Object> exec = null;
            @Override
            @SuppressWarnings("unchecked")
            public Object execute(RedisOperations operations) throws DataAccessException {
                operations.multi();
                redisTemplate.opsForValue().getAndSet(key,value);
                redisTemplate.expire(key,timeout, TimeUnit.SECONDS);
                exec = operations.exec();
                if(exec.size() > 0) {
                    return exec.get(0);
                }
                return null;
            }
        };
        return redisTemplate.execute(sessionCallback);
    }




}