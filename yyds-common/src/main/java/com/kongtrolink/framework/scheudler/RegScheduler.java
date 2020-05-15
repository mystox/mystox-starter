package com.kongtrolink.framework.scheudler;


import com.kongtrolink.framework.config.WebPrivFuncConfig;
import com.kongtrolink.framework.core.IaENV;
import com.kongtrolink.framework.core.RegCall;
import com.kongtrolink.framework.entity.OperaResult;
import com.kongtrolink.framework.entity.RegisterSub;

import java.util.List;


/**
 * Created by mystoxlol on 2019/8/28, 13:31.
 * company: kongtrolink
 * description: 注册启动类
 * update record:
 */

public interface RegScheduler {
    public void register();

    public boolean exists(String nodeData);

    public void build(IaENV iaENV);

    public void initCaller(RegCall caller);//设置回调方法

    public List<RegisterSub> getRegHttpList();

    public List<RegisterSub> GetRegLocalList();

    public List<RegisterSub> GetRegJarList();

    public void close() throws InterruptedException;

    void  create(final String path, byte data[], int createMode) ;
    String getData(String path) ;

    void setData(String path, byte data[]) ;

    List<String> getChildren(String path);

    void deleteNode(String path) ;

    public void setDataToRegistry(RegisterSub sub) ;

    public void connect(String url);

    public RegCall.RegState getState();

    OperaResult registerWebPriv(WebPrivFuncConfig webPrivFuncConfig) throws Exception;
}
