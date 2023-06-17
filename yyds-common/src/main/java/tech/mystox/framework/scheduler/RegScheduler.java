package tech.mystox.framework.scheduler;


import tech.mystox.framework.config.WebPrivFuncConfig;
import tech.mystox.framework.core.RegCall;
import tech.mystox.framework.entity.OperaResult;
import tech.mystox.framework.entity.RegisterSub;
import tech.mystox.framework.exception.RegisterException;

import java.util.List;


/**
 * Created by mystoxlol on 2019/8/28, 13:31.
 * company: mystox
 * description: 注册启动类
 * update record:
 */

public interface RegScheduler extends Schedule {

    /**
     * 注册
     */
    public void register();

    /**
     * 重复注册
     */
    public void reRegister();

    public boolean exists(String nodeData);

    List<String> buildOperaMap(String operaCode);

    public List<RegisterSub> getSubList();
    public List<RegisterSub> getRegHttpList();

    public List<RegisterSub> getRegLocalList();

    public List<RegisterSub> getRegJarList();

    public void close() throws InterruptedException;

    void  create(final String path, byte data[], int createMode) ;
    String getData(String path) ;

    void setData(String path, byte data[]) ;

    List<String> getChildren(String path);

    void deleteNode(String path) ;

    public void setDataToRegistry(RegisterSub sub) ;

    public void connect(String url) throws RegisterException;

    public RegCall.RegState getState();

    OperaResult registerWebPriv(WebPrivFuncConfig webPrivFuncConfig) throws Exception;
}
