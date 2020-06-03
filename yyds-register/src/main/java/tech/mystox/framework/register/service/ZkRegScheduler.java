package tech.mystox.framework.register.service;


import tech.mystox.framework.config.IaConf;
import tech.mystox.framework.config.OperaRouteConfig;
import tech.mystox.framework.config.WebPrivFuncConfig;
import tech.mystox.framework.core.IaENV;
import tech.mystox.framework.core.RegCall;
import tech.mystox.framework.entity.OperaResult;
import tech.mystox.framework.entity.RegisterSub;
import tech.mystox.framework.register.runner.ZkHandlerImpl;
import tech.mystox.framework.scheduler.RegScheduler;
import tech.mystox.framework.service.RegHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.util.List;

@Component("zkRegScheduler")
@Lazy
public class ZkRegScheduler implements ApplicationContextAware, RegScheduler {
    private Logger logger = LoggerFactory.getLogger(ZkRegScheduler.class);
    // @Autowired
    // @Qualifier("zkHandlerImpl")
    RegHandler regHandler;
    private ApplicationContext applicationContext;
    private OperaRouteConfig operaRouteConfig;
    private IaConf iaconf;
    private String groupCode;
    private String serverName;
    private String serverVersion;
    // private RegCall callme;
    // //
    // public ZkRegScheduler()
    // {
    //     logger.debug("-----the class "+ ZkRegScheduler.class+" instructed");
    // }


    @Override
    public void register() {
        regHandler.register();
    }

    @Override
    public boolean exists(String nodeData) {
        return regHandler.exists(nodeData);
    }

    private RegCall caller;
    @Override
    public void initCaller(RegCall caller) {
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
        regHandler.setData(path,data);
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
        this.regHandler = new ZkHandlerImpl(iaENV,applicationContext);
        this.regHandler.build();
    }

    @Override
    public void connect(String registerUrl)
    {
       regHandler.connect(registerUrl);
    }

    @Override
    public RegCall.RegState getState() {
        RegCall.RegState state = regHandler.getServerState();
        return state;
    }

    @Override
    public OperaResult registerWebPriv(WebPrivFuncConfig webPrivFuncConfig) throws Exception {
        OperaResult operaResult = regHandler.registerWebPriv(webPrivFuncConfig);
        return operaResult;
    }


    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
