package com.kongtrolink.framework.core;

import com.kongtrolink.framework.config.IaConf;
import com.kongtrolink.framework.entity.RegisterMsg;
import com.kongtrolink.framework.entity.RegisterSub;
import com.kongtrolink.framework.scheudler.MsgScheduler;
import com.kongtrolink.framework.scheudler.RegScheduler;
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
    }

    public void subTopic(){
        List<RegisterSub> subList=this.regScheduler.GetRegLocalList();
        this.msgScheduler.subTopic(subList);

    }
    public void connect()
    {
        registerMsg=this.msgScheduler.getIahander().whereIsCentre();
        this.iaConf.setRegisterUrl(registerMsg.getRegistURI());
        this.regScheduler.connect(registerMsg.getRegisterUrl());
    }

}
