package com.kongtrolink.framework.api;

import com.kongtrolink.framework.stereotype.OperaCode;
import com.kongtrolink.framework.stereotype.Register;

/**
 * @Auther: liudd
 * @Date: 2019/9/25 15:44
 * @Description:
 */
@Register
public interface Service {

    @OperaCode
    String getUniqueService();
}
