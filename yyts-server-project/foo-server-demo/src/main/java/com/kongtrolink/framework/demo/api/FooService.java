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
    String foo(String foo);
}
