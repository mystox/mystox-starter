package tech.mystox.framework.core;

import tech.mystox.framework.config.IaConf;
import tech.mystox.framework.entity.RegisterMsg;
import tech.mystox.framework.entity.RegisterSub;
import tech.mystox.framework.entity.ServerStatus;
import tech.mystox.framework.scheduler.MsgScheduler;
import tech.mystox.framework.scheduler.RegScheduler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;

/**
 * 注册类
 */
public class IaRegister {
    private Logger logger = LoggerFactory.getLogger(IaRegister.class);
    IaConf iaConf;
    IaENV iaEnv;
    RegisterMsg registerMsg;
    private MsgScheduler msgScheduler;
    private RegScheduler regScheduler;
    public IaRegister(IaENV iaEnv) {
        this.iaEnv = iaEnv;
        iaConf= iaEnv.getConf();
        this.msgScheduler = iaEnv.getMsgScheduler();
        this.regScheduler= iaEnv.getRegScheduler();
        msgScheduler.build(this.iaEnv);
    }

    List<RegisterSub> subs;



    /**
     * 注册中心注册节点信息
     *
     * @throws IOException
     * @throws InterruptedException
     */
    public void register()  {
        this.regScheduler.register();
        iaEnv.setServerStatus(ServerStatus.ONLINE);
    }

    public void unregister()  {
        iaEnv.setServerStatus(ServerStatus.UNREGISTER);
        this.regScheduler.unregister();
        this.msgScheduler.unregister();
        iaEnv.setServerStatus(ServerStatus.OFFLINE);
    }
    public void subTopic(){
        List<RegisterSub> subList=this.regScheduler.getSubList();
        this.msgScheduler.subTopic(subList);

    }
    public void connect()
    {
        registerMsg=this.msgScheduler.getIaHandler().whereIsCentre();
        this.iaConf.setRegisterUrl(registerMsg.getRegistURI());
        this.regScheduler.connect(registerMsg.getRegisterUrl());
    }

}
