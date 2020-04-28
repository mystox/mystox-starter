package com.kongtrolink.framework.scheudler;


import com.kongtrolink.framework.config.IaConf;
import com.kongtrolink.framework.config.OperaRouteConfig;
import com.kongtrolink.framework.core.IaENV;
import com.kongtrolink.framework.core.callMe;
import com.kongtrolink.framework.entity.RegisterSub;
import com.kongtrolink.framework.service.RegHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.util.List;

@Component("zkRegscheudler")
@Lazy
public class ZkRegScheudler implements RegScheduler  {
    private Logger logger = LoggerFactory.getLogger(MqttMsgScheduler.class);
    @Autowired
    @Qualifier("zkHandlerImpl")
    RegHandler regHandler;
    private OperaRouteConfig operaRouteConfig;
    private IaConf iaconf;
    private String groupCode;
    private String serverName;
    private String serverVersion;
    private callMe callme;

    public ZkRegScheudler()
    {
        logger.debug("-----the class "+ ZkRegScheudler.class+" instructed");
    }


    @Override
    public void register() {
        regHandler.register();
    }

    @Override
    public boolean exists(String nodeData) {
        return regHandler.exists(nodeData);
    }

    private callMe caller;
    @Override
    public void initCaller(callMe caller) {
        this.caller = caller;
    }


    @Override
    public List<RegisterSub> getRegHttpList() {
        return null;
    }

    @Override
    public List<RegisterSub> GetRegLocalList() {
        return this.iaconf.getLocalServiceScanner().getSubList();
    }

    @Override
    public List<RegisterSub> GetRegJarList() {
         return this.iaconf.getJarServiceScanner().getSubList();
    }


    @Override
    public void close() throws InterruptedException {

    }

    @Override
    public void  create(String path, byte[] data, int createMode) {
       this.regHandler.create(path,data,createMode);
    }


    @Override
    public String getData(String path)  {
        return regHandler.getData(path);
    }

    @Override
    public void setData(String path, byte[] data) {

    }


    @Override
    public List<String> getChildren(String path) {
        return regHandler.getChildren(path);
    }

    @Override
    public void deleteNode(String path)  {
        regHandler.deleteNode(path);
    }

    @Override
    public void setDataToRegistry(RegisterSub sub)  {
        regHandler.setDataToRegistry(sub);
    }
    @Override
    public void build(IaENV iaENV)  {
        this.iaconf=iaENV.getConf();
        this.groupCode=iaconf.getGroupCode();
        this.serverName=iaconf.getServerName();
        this.serverVersion=iaconf.getServerVersion();
        this.operaRouteConfig=iaconf.getOperaRouteConfig();
        this.regHandler.build();
    }

    @Override
    public void connect(String registerUrl)
    {
       regHandler.connect(registerUrl);
    }


}
