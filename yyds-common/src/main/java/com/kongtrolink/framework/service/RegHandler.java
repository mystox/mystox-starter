package com.kongtrolink.framework.service;

import com.kongtrolink.framework.config.WebPrivFuncConfig;
import com.kongtrolink.framework.core.RegCall;
import com.kongtrolink.framework.entity.OperaResult;
import com.kongtrolink.framework.entity.RegisterSub;

import java.util.List;

public interface RegHandler {
    public void register();

    public boolean exists(String nodeData);

    void create(final String path, byte data[], int createMode);

    public void close() throws InterruptedException;

    String getData(String path);

    void setData(final String path,byte data[]);
    List<String> getChildren(String path);

    void deleteNode(String path);

    public void setDataToRegistry(RegisterSub sub);

    public void connect(String url);

    public void build();

    RegCall.RegState getServerState();

    public OperaResult registerWebPriv(WebPrivFuncConfig webPrivFuncConfig) throws Exception;

}
