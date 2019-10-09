package com.kongtrolink.framework.api;

import com.kongtrolink.framework.stereotype.OperaCode;
import com.kongtrolink.framework.stereotype.Register;

@Register
public interface Service {

    @OperaCode
    void reportCI(String payload);

    @OperaCode
    String getCIModel(String payload);

    @OperaCode
    String getCIIds(String payload);

    @OperaCode
    String getCI(String payload);
}
