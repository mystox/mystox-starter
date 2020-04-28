package com.kongtrolink.framework.core;

import com.kongtrolink.framework.config.IaConf;
import com.kongtrolink.framework.entity.RegisterMsg;
import com.kongtrolink.framework.entity.RegisterSub;
import com.kongtrolink.framework.scheudler.MsgScheudler;
import com.kongtrolink.framework.scheudler.RegScheduler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;

public class IaRegister {
    private Logger logger = LoggerFactory.getLogger(IaRegister.class);
    IaConf iaConf;
    IaENV iaENV;
    RegisterMsg registerMsg;
    private MsgScheudler msgScheudler;
    private RegScheduler regScheduler;
    public IaRegister(IaENV iaENV) {
        this.iaENV=iaENV;
        iaConf=iaENV.getConf();
        this.msgScheudler=iaENV.getMsgScheudler();
        this.regScheduler=iaENV.getRegScheudler();
        msgScheudler.build(this.iaENV);
    }

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
        this.msgScheudler.subTopic(subList);

    }
    public void connect()
    {
        registerMsg=this.msgScheudler.getIahander().whereIsCentre();
        this.iaConf.setRegisterUrl(registerMsg.getRegistURI());
        this.regScheduler.connect(registerMsg.getRegisterUrl());
    }

}
