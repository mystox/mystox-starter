package com.kongtrolink.framework.foo.api;

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

    @OperaCode(code = "fooo")
    void foo(String foo);

    @OperaCode
    String boo(String boo);


    @OperaCode
    String coo(String coo);

    @OperaCode(code = "poo")
    String packageSum(String poo);

}
