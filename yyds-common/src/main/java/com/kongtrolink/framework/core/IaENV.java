package com.kongtrolink.framework.core;

import com.kongtrolink.framework.config.IaConf;
import com.kongtrolink.framework.entity.RegisterMsg;
import com.kongtrolink.framework.entity.RegisterSub;
import com.kongtrolink.framework.scheudler.*;
import com.kongtrolink.framework.scheudler.MqttMsgScheduler;
import com.kongtrolink.framework.scheudler.MsgScheduler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class IaENV implements ApplicationContextAware, RegCall {
    private MsgScheduler msgScheduler;
    private RegScheduler regScheudler;
    private IaConf conf;


    private Logger logger = LoggerFactory.getLogger(IaENV.class);


    public void build(IaConf conf) {
        this.conf = conf;
        regScheudler = createRegScheudler(getRegType(conf));
        msgScheduler = createMsgScheudler(getMsgType(conf));

    }

    public String getRegType(IaConf conf) {
        return conf.getRegistertype();
    }

    public String getMsgType(IaConf conf) {
        return conf.getMsgType();
    }

    public MsgScheduler createMsgScheudler(String regType) {
        switch (regType) {
            //        case MqttMsgBus :return new MqttMsgScheduler();
            case IaConf.MqttMsgBus: {
                MqttMsgScheduler mqttMsgScheudler = applicationContext.getBean(MqttMsgScheduler.class);
                mqttMsgScheudler.build(this);
                return mqttMsgScheudler;
            }
            default: {
                MqttMsgScheduler mqttMsgScheudler = applicationContext.getBean(MqttMsgScheduler.class);
                mqttMsgScheudler.build(this);
                return mqttMsgScheudler;
            }
        }
    }

    public RegScheduler createRegScheudler(String regType) {
        switch (regType) {
            case IaConf.RomateZKtype: {
                RegScheduler regScheduler = applicationContext.getBean(ZkRegScheudler.class);
                regScheduler.build(this);
                return regScheduler;
            }
            default: {
                RegScheduler regScheduler = applicationContext.getBean(ZkRegScheudler.class);
                regScheduler.build(this);
                return regScheduler;
            }
        }
    }


    public String getMsgBus(IaConf conf) {
        return conf.getMsgBusType();
    }
//    public MsgScheduler createMsgScheudler(String regType)
//    {
//        switch (regType) {
//            case MqttMsgBus :{this.MsgScheduler.build(this.conf);return this.MsgScheduler};
//            default: return this.MsgScheduler.build(this.conf);
//        }
//    }

    public RegScheduler getRegScheudler() {
        return regScheudler;
    }

    public MsgScheduler getMsgScheduler() {
        return msgScheduler;
    }


    public IaConf getConf() {
        return conf;
    }

    ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Override
    public void call(int code) throws InterruptedException {
        switch (code) {
            case RegCall.Disconnected: {
                logger.error("[call] disconnected...");
                List<RegisterSub> subList = this.regScheudler.GetRegLocalList();
                logger.warn("[call] cancellation msg-schedule sub session");
                this.msgScheduler.removerSubTopic(subList);
                RegisterMsg registerMsg = this.msgScheduler.getIahander().whereIsCentre();
                getConf().setRegisterUrl(registerMsg.getRegistURI());
                this.regScheudler.connect(registerMsg.getRegisterUrl());
                logger.warn("register reconnected [{}]", registerMsg.getRegisterUrl());
                this.regScheudler.register();
                this.msgScheduler.subTopic(subList);
                break;

            }

            case RegCall.RebuildStatus: {
                logger.error("[call]: register rebuild");
                List<RegisterSub> subList = this.regScheudler.GetRegLocalList();
                this.msgScheduler.removerSubTopic(subList);
                logger.warn("服务掉线:注销MQ订阅");
                logger.warn("服务掉线:开始等待注册");
                getRegScheudler().register();
                this.msgScheduler.subTopic(subList);
                break;
            }
        }
    }

    public void subTopic() {
        List<RegisterSub> subList = this.regScheudler.GetRegLocalList();
        this.msgScheduler.subTopic(subList);

    }


}
