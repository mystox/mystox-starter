package com.kongtrolink.framework.demo.api;

import com.kongtrolink.framework.stereotype.OperaCode;
import com.kongtrolink.framework.stereotype.Register;

/**
 * Created by mystoxlol on 2019/9/9, 12:55.
 * company: kongtrolink
 * description:
 * update record:
 */
@Register
public interface FooService {

    @OperaCode
    void foo(String foo);

    @OperaCode
    String boo(String foo);

    @OperaCode
    void testVoid(String payload);

}
