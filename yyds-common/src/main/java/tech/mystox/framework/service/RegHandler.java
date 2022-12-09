package tech.mystox.framework.service;

import tech.mystox.framework.config.WebPrivFuncConfig;
import tech.mystox.framework.core.RegCall;
import tech.mystox.framework.entity.OperaResult;
import tech.mystox.framework.entity.RegisterSub;
import tech.mystox.framework.exception.RegisterException;

import java.util.List;

public interface RegHandler {
    public void register();


    void unregister();
    public boolean exists(String nodeData);

    void create(final String path, byte data[], int createMode);

    public void close() throws InterruptedException;

    String getData(String path);

    void setData(final String path,byte data[]);
    List<String> getChildren(String path);

    void deleteNode(String path);

    public void setDataToRegistry(RegisterSub sub);

    public void connect(String url) throws RegisterException;

    public void build();

    RegCall.RegState getRegState();

    public OperaResult registerWebPriv(WebPrivFuncConfig webPrivFuncConfig) throws Exception;

}
