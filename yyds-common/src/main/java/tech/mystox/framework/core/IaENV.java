package tech.mystox.framework.core;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;
import tech.mystox.framework.config.IaConf;
import tech.mystox.framework.entity.RegisterMsg;
import tech.mystox.framework.entity.RegisterSub;
import tech.mystox.framework.entity.ServerStatus;
import tech.mystox.framework.scheduler.LoadBalanceScheduler;
import tech.mystox.framework.scheduler.MsgScheduler;
import tech.mystox.framework.scheduler.RegScheduler;

import java.util.List;

@Component
public class IaENV implements ApplicationContextAware, RegCall {
    private MsgScheduler msgScheduler;
    private RegScheduler regScheduler;
    private LoadBalanceScheduler loadBalanceScheduler;
    private IaConf conf;
    private ServerStatus serverStatus = ServerStatus.OFFLINE;
    private Logger logger = LoggerFactory.getLogger(IaENV.class);


    public void build(IaConf conf) {
        setServerStatus(ServerStatus.STARTING);
        this.conf = conf;
        regScheduler = createRegScheduler(getRegType(conf));
        msgScheduler = createMsgScheduler(getMsgType(conf));
        loadBalanceScheduler = createLoadBalancerScheduler(getLoadBalancer(conf));
    }

    private IaConf.LoadBalanceType getLoadBalancer(IaConf conf) {
        String loadBalancerType = conf.getLoadBalancerType();
        return IaConf.LoadBalanceType.valueOf(StringUtils.upperCase(loadBalancerType));
    }


    public ServerStatus getServerStatus() {
        return serverStatus;
    }

    public boolean setServerStatus(ServerStatus serverStatus) {
        switch (serverStatus) {
            case ONLINE: {//切换至在线状态
                logger.info("server status is [{}]", serverStatus);
                this.serverStatus = serverStatus;
                break;
            }
            case UNREGISTER: { //注销状态
                this.serverStatus = serverStatus;
            }
            case RESTARTING:{ //重启命令 在注销状态和启动状态时 不修改状态
                if (getServerStatus().equals(ServerStatus.UNREGISTER)
                        || getServerStatus().equals(ServerStatus.STARTING)) {
                    return false;
                }
            }
            default:
                this.serverStatus = serverStatus;
        }
        return true;
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
                MsgScheduler mqttMsgScheduler = applicationContext.getBean("mqttMsgScheduler", MsgScheduler.class);
                mqttMsgScheduler.build(this);
                return mqttMsgScheduler;
            }
            default: {
                MsgScheduler mqttMsgScheduler = applicationContext.getBean("mqttMsgScheduler", MsgScheduler.class);
                mqttMsgScheduler.build(this);
                return mqttMsgScheduler;
            }
        }
    }

    public RegScheduler createRegScheduler(String regType) {
        switch (regType) {
            case IaConf.ZkRegType: {
                RegScheduler regScheduler = applicationContext.getBean("zkRegScheduler", RegScheduler.class);
                regScheduler.build(this);
                return regScheduler;
            }
            default: {
                RegScheduler regScheduler = applicationContext.getBean("zkRegScheduler", RegScheduler.class);
                regScheduler.build(this);
                return regScheduler;
            }
        }
    }

    public LoadBalanceScheduler createLoadBalancerScheduler(IaConf.LoadBalanceType balanceType) {
        switch (balanceType) {
            case BASE: {
                LoadBalanceScheduler loadBalanceScheduler = applicationContext.getBean("baseLoadBalancer", LoadBalanceScheduler.class);
                loadBalanceScheduler.build(this);
                return loadBalanceScheduler;
            }
            default: {
                LoadBalanceScheduler loadBalanceScheduler = applicationContext.getBean("baseLoadBalancer", LoadBalanceScheduler.class);
                loadBalanceScheduler.build(this);
                return loadBalanceScheduler;
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

    public LoadBalanceScheduler getLoadBalanceScheduler() {
        return loadBalanceScheduler;
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
                setServerStatus(ServerStatus.OFFLINE);
                logger.error("[operaCall] disconnected...");
                List<RegisterSub> subList = this.regScheduler.getSubList();
                logger.warn("[operaCall] cancellation msg-schedule sub session");
                this.msgScheduler.removerSubTopic(subList);
                RegisterMsg registerMsg = this.msgScheduler.getIaHandler().whereIsCentre();
                getConf().setRegisterUrl(registerMsg.getRegistURI());
                this.regScheduler.connect(registerMsg.getRegisterUrl());
                logger.warn("[operaCall] register reconnected [{}]", registerMsg.getRegisterUrl());
                this.regScheduler.register();
                this.msgScheduler.subTopic(subList);
                setServerStatus(ServerStatus.ONLINE);
                break;
            }

            case RebuildStatus: {
                setServerStatus(ServerStatus.STARTING);
                logger.error("[operaCall] register rebuild");
                List<RegisterSub> subList = this.regScheduler.getSubList();
                logger.warn("[operaCall] cancellation msg-schedule sub session");
                this.msgScheduler.removerSubTopic(subList);
                logger.warn("[operaCall] register offline, waiting for rebuild");
                getRegScheduler().register();
                this.msgScheduler.subTopic(subList);
                setServerStatus(ServerStatus.ONLINE);
                break;
            }
        }
    }

    public void subTopic() {
        List<RegisterSub> subList = this.regScheduler.getSubList();
        this.msgScheduler.subTopic(subList);

    }


}
