package com.kongtrolink.framework.demo.api;

import org.springframework.stereotype.Component;

/**
 * Created by mystoxlol on 2019/9/9, 13:03.
 * company: kongtrolink
 * description:
 * update record:
 */
@Component
public class FooServiceImpl implements FooService {
    @Override
    public String foo(String foo) {
        System.out.println(foo);
        return foo;
    }

    @Override
    public void testVoid(String payload) {
        System.out.println(payload);
        return;
    }
}
