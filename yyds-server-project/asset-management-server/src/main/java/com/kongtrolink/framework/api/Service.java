package com.kongtrolink.framework.api;

import com.kongtrolink.framework.stereotype.OperaCode;
import com.kongtrolink.framework.stereotype.Register;

@Register
public interface Service {

    @OperaCode
    String getCIModel(String payload);

    @OperaCode
    String getCIIds(String payload);

    @OperaCode
    String getCIProp(String payload);

    @OperaCode
    String getCI(String payload);

    @OperaCode
    void deviceReport(String payload);

    @OperaCode
    String getCISCloud(String payload);

    @OperaCode
    String addCISCloud(String payload);

    @OperaCode
    String deleteCISCloud(String payload);

    @OperaCode
    String modifyCISCloud(String payload);
}
