package com.kongtrolink.framework.core;

import com.kongtrolink.framework.config.IaConf;
import com.kongtrolink.framework.entity.RegisterMsg;
import com.kongtrolink.framework.entity.RegisterSub;
import com.kongtrolink.framework.scheduler.*;
import com.kongtrolink.framework.scheduler.MqttMsgScheduler;
import com.kongtrolink.framework.scheduler.MsgScheduler;
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
    private RegScheduler regScheduler;
    private IaConf conf;


    private Logger logger = LoggerFactory.getLogger(IaENV.class);


    public void build(IaConf conf) {
        this.conf = conf;
        regScheduler = createRegScheduler(getRegType(conf));
        msgScheduler = createMsgScheduler(getMsgType(conf));

    }

    public String getRegType(IaConf conf) {
        return conf.getRegisterType();
    }

    public String getMsgType(IaConf conf) {
        return conf.getMsgType();
    }

    public MsgScheduler createMsgScheduler(String regType) {
        switch (regType) {
            //        case MqttMsgBus :return new MqttMsgScheduler();
            case IaConf.MqttMsgBus: {
                MqttMsgScheduler mqttMsgScheduler = applicationContext.getBean(MqttMsgScheduler.class);
                mqttMsgScheduler.build(this);
                return mqttMsgScheduler;
            }
            default: {
                MqttMsgScheduler mqttMsgScheduler = applicationContext.getBean(MqttMsgScheduler.class);
                mqttMsgScheduler.build(this);
                return mqttMsgScheduler;
            }
        }
    }

    public RegScheduler createRegScheduler(String regType) {
        switch (regType) {
            case IaConf.ZkRegType: {
                RegScheduler regScheduler = applicationContext.getBean(ZkRegScheduler.class);
                regScheduler.build(this);
                return regScheduler;
            }
            default: {
                RegScheduler regScheduler = applicationContext.getBean(ZkRegScheduler.class);
                regScheduler.build(this);
                return regScheduler;
            }
        }
    }


    public String getMsgBus(IaConf conf) {
        return conf.getMsgBusType();
    }
//    public MsgScheduler createMsgScheduler(String regType)
//    {
//        switch (regType) {
//            case MqttMsgBus :{this.MsgScheduler.build(this.conf);return this.MsgScheduler};
//            default: return this.MsgScheduler.build(this.conf);
//        }
//    }

    public RegScheduler getRegScheduler() {
        return regScheduler;
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
    public void call(RegState state) throws InterruptedException {
        switch (state) {
            case Disconnected: {
                logger.error("[call] disconnected...");
                List<RegisterSub> subList = this.regScheduler.GetRegLocalList();
                logger.warn("[call] cancellation msg-schedule sub session");
                this.msgScheduler.removerSubTopic(subList);
                RegisterMsg registerMsg = this.msgScheduler.getIaHandler().whereIsCentre();
                getConf().setRegisterUrl(registerMsg.getRegistURI());
                this.regScheduler.connect(registerMsg.getRegisterUrl());
                logger.warn("[call] register reconnected [{}]", registerMsg.getRegisterUrl());
                this.regScheduler.register();
                this.msgScheduler.subTopic(subList);
                break;

            }

            case RebuildStatus: {
                logger.error("[call] register rebuild");
                List<RegisterSub> subList = this.regScheduler.GetRegLocalList();
                logger.warn("[call] cancellation msg-schedule sub session");
                this.msgScheduler.removerSubTopic(subList);
                logger.warn("[call] register offline, waiting for rebuild");
                getRegScheduler().register();
                this.msgScheduler.subTopic(subList);
                break;
            }
        }
    }

    public void subTopic() {
        List<RegisterSub> subList = this.regScheduler.GetRegLocalList();
        this.msgScheduler.subTopic(subList);

    }


}
