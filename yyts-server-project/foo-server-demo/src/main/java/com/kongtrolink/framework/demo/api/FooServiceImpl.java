package com.kongtrolink.framework.demo.api;

import org.springframework.stereotype.Service;

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
        return "boo";
    }
}
