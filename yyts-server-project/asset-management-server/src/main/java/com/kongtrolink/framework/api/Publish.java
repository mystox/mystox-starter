package com.kongtrolink.framework.api;

import com.kongtrolink.framework.entity.MsgResult;
import com.kongtrolink.framework.stereotype.OperaCode;

public interface Publish {

    MsgResult getRegionCode(String payload);

    void publishCIProps(String payload);
}
