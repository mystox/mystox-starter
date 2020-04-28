package com.kongtrolink.framework.core;

import com.kongtrolink.framework.common.util.DateUtil;
import com.kongtrolink.framework.config.IaConf;
import com.kongtrolink.framework.entity.RegisterMsg;
import com.kongtrolink.framework.entity.RegisterSub;
import com.kongtrolink.framework.scheudler.MqttMsgScheudler;
import com.kongtrolink.framework.scheudler.MsgScheudler;
import com.kongtrolink.framework.scheudler.RegScheduler;
import com.kongtrolink.framework.scheudler.ZkRegScheudler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class IaENV implements ApplicationContextAware ,callMe{
    private MsgScheudler msgScheudler;
    private RegScheduler regScheudler;
    private IaConf conf;


    private Logger logger = LoggerFactory.getLogger(IaENV.class);

    public IaENV()
    {
       logger.warn("----------------------the class "+ IaENV.class+" instructed");
    }


    public void build(IaConf conf)
    {
        this.conf=conf;
        regScheudler=createRegScheudler(getRegType(conf));
        msgScheudler=createMsgScheudler(getMsgType(conf));

    }

    public String getRegType(IaConf conf) {
       return conf.getRegistertype();
    }

    public String getMsgType(IaConf conf) {
        return conf.getMsgType();
    }

    public MsgScheudler createMsgScheudler(String regType)
    {
        switch (regType) {
  //        case MqttMsgBus :return new MqttMsgScheudler();
            case IaConf.MqttMsgBus :
            {
                MqttMsgScheudler mqttMsgScheudler=applicationContext.getBean(MqttMsgScheudler.class);
                mqttMsgScheudler.build(this);
                return   mqttMsgScheudler;
            }
            default:{
                MqttMsgScheudler mqttMsgScheudler=applicationContext.getBean(MqttMsgScheudler.class);
                mqttMsgScheudler.build(this);
                return   mqttMsgScheudler;
            }
        }
    }

    public RegScheduler createRegScheudler(String regType)
    {
        switch (regType) {
            case IaConf.RomateZKtype: {
                RegScheduler regScheduler= applicationContext.getBean(ZkRegScheudler.class);
                regScheduler.build(this);
                return regScheduler;
            }
            default:{
                RegScheduler regScheduler= applicationContext.getBean(ZkRegScheudler.class);
                regScheduler.build(this);
                return regScheduler;
            }
        }
    }


    public String getMsgBus(IaConf conf) {
        return conf.getMsgBusType();
    }
//    public MsgScheudler createMsgScheudler(String regType)
//    {
//        switch (regType) {
//            case MqttMsgBus :{this.MsgScheudler.build(this.conf);return this.MsgScheudler};
//            default: return this.MsgScheudler.build(this.conf);
//        }
//    }

    public RegScheduler getRegScheudler() {
        return regScheudler;
    }

    public MsgScheudler getMsgScheudler() {
        return msgScheudler;
    }


    public IaConf getConf() {
        return conf;
    }

    ApplicationContext applicationContext;
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
    this.applicationContext=applicationContext;
    }

    @Override
    public void call(int code) throws InterruptedException {
        switch (code)
        {
            case callMe.Disconnected:{
                logger.error("连接断开:捕获异常");
                List<RegisterSub> subList=this.regScheudler.GetRegLocalList();
                this.msgScheudler.removerSubTopic(subList);
                logger.warn("连接断开:注销MQ订阅");
                RegisterMsg registerMsg =this.msgScheudler.getIahander().whereIsCentre();

                getConf().setRegisterUrl(registerMsg.getRegistURI());
                this.regScheudler.connect(registerMsg.getRegisterUrl());
                logger.warn("连接断开:开始注册");
                this.regScheudler.register();
                this.msgScheudler.subTopic(subList);break;

            }
            case callMe.RebuidStatus:{
                logger.error("服务掉线:捕获异常");
                List<RegisterSub> subList=this.regScheudler.GetRegLocalList();
                this.msgScheudler.removerSubTopic(subList);
                logger.warn("服务掉线:注销MQ订阅");
                logger.warn("服务掉线:开始注册");
                getRegScheudler().register();
                this.msgScheudler.subTopic(subList);break;
            }
        }
    }
    public void subTopic(){
        List<RegisterSub> subList=this.regScheudler.GetRegLocalList();
        this.msgScheudler.subTopic(subList);

    }


}
