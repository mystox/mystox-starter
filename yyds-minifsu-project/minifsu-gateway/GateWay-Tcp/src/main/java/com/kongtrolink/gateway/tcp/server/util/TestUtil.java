package com.kongtrolink.gateway.tcp.server.util;

import com.alibaba.fastjson.JSONObject;
import com.kongtrolink.gateway.tcp.server.entity.TestEntity;

import java.util.ArrayList;
import java.util.List;

/**
 * xx
 * by Mag on 2019/3/19.
 */
public class TestUtil {

    public static String getReturnMsg(){
        List<TestEntity> list = new ArrayList<TestEntity>();
        for(int i=0;i<1000;i++){
            TestEntity entity = new TestEntity("thisIsADemoTimeJson中文网致力于在中国推广Json并提供相关的Json解析",i);
            list.add(entity);
        }
        return JSONObject.toJSONString(list);
    }
}
