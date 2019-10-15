package com.kongtrolink.framework.api;

import com.kongtrolink.framework.stereotype.OperaCode;
import com.kongtrolink.framework.stereotype.Register;

@Register
public interface Service {

    @OperaCode
    String getRegionCode(String payload);
}
