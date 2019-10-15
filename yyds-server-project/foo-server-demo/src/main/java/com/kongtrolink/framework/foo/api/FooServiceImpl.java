package com.kongtrolink.framework.foo.api;

import com.alibaba.fastjson.JSONObject;
import org.springframework.stereotype.Service;

import java.nio.charset.Charset;

/**
 * Created by mystoxlol on 2019/9/9, 13:03.
 * company: kongtrolink
 * description:
 * update record:
 */
@Service
public class FooServiceImpl implements FooService {
    @Override
    public void foo(String foo) {
        System.out.println(foo);
    }

    @Override
    public String boo(String foo) {
        JSONObject jsonObject = JSONObject.parseObject(foo);
        Integer a = jsonObject.getInteger("a");
        String s = "你我他";
        StringBuilder buffer = new StringBuilder();
        for (int i = 0; i < 8 * 1024; i++) {
            buffer.append(s + i);
        }
        int length = buffer.length();
        System.out.println(length);
        System.out.println(buffer.toString().getBytes(Charset.defaultCharset()).length / 1024);


        return buffer.toString();
    }
}
