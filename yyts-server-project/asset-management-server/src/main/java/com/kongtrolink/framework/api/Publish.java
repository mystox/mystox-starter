package com.kongtrolink.framework.api;

import com.kongtrolink.framework.entity.MsgResult;
public interface Publish {

    MsgResult getRegionCode(String payload);

    void publishCIProps(String payload);

    void deviceGet(String sn, String gatewayServerCode);
}
