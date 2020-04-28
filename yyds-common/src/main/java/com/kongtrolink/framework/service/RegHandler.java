package com.kongtrolink.framework.service;

import com.kongtrolink.framework.entity.RegisterSub;

import java.util.List;

public interface RegHandler {
    public void register();

    public boolean exists(String nodeData);

    void create(final String path, byte data[], int createMode);

    public void close() throws InterruptedException;

    String getData(String path);

    List<String> getChildren(String path);

    void deleteNode(String path);

    public void setDataToRegistry(RegisterSub sub);

    public void connect(String url);

    public void build();

}
