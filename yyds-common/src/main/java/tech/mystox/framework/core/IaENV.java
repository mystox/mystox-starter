package tech.mystox.framework.core;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tech.mystox.framework.config.IaConf;
import tech.mystox.framework.entity.RegisterMsg;
import tech.mystox.framework.entity.RegisterSub;
import tech.mystox.framework.entity.ServerMsg;
import tech.mystox.framework.entity.ServerStatus;
import tech.mystox.framework.exception.RegisterException;
import tech.mystox.framework.scheduler.LoadBalanceScheduler;
import tech.mystox.framework.scheduler.MsgScheduler;
import tech.mystox.framework.scheduler.RegScheduler;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

import static tech.mystox.framework.common.util.MqttUtils.preconditionGroupServerCode;
import static tech.mystox.framework.common.util.MqttUtils.preconditionServerCode;
import static tech.mystox.framework.constants.OperaConstants.MqttMsgBus;
import static tech.mystox.framework.constants.OperaConstants.ZkRegType;

//@Component
public class IaENV implements RegCall {
    private MsgScheduler msgScheduler;
    private RegScheduler regScheduler;
    private LoadBalanceScheduler loadBalanceScheduler;
    private IaConf conf;
    private IaContext iaContext;
    private ServerStatus serverStatus = ServerStatus.OFFLINE;
    private ServerMsg serverMsg;
    private ServiceScanner localServiceScanner;
    private ServiceScanner jarServiceScanner;
    private Logger logger = LoggerFactory.getLogger(IaENV.class);

    private final BeanProvider beanProvider;
    private List<String> scanBasePackage;

    public IaENV(List<String> scanBasePackage, BeanProvider beanProvider) {
        this.beanProvider = beanProvider;
        this.scanBasePackage = scanBasePackage;
    }


    public void build(IaContext iaContext) {
        setServerStatus(ServerStatus.STARTING);
        this.iaContext = iaContext;
        this.conf = iaContext.getConf();
        //扫描注册信息
        String scannedBasePackage = this.conf.scanBasePackage();
        if (StringUtils.isNotBlank(scannedBasePackage)) {
            scanBasePackage = List.of(scannedBasePackage);
        }
        scanBasePackage.add("tech.mystox.framework");
        localServiceScanner = new LocalServiceScannerCore(
                scanBasePackage, beanProvider);
        jarServiceScanner = new JarServiceScanner(conf);
        regScheduler = createRegScheduler(getRegType(conf));
        msgScheduler = createMsgScheduler(getMsgType(conf));
        loadBalanceScheduler = createLoadBalancerScheduler(getLoadBalancer(conf));
    }

    public BeanProvider getBeanProvider() {
        return beanProvider;
    }

    private IaConf.LoadBalanceType getLoadBalancer(IaConf conf) {
        String loadBalancerType = conf.getLoadBalancerType();
        return IaConf.LoadBalanceType.valueOf(StringUtils.upperCase(loadBalancerType));
    }

    public ServerMsg getServerMsg() {
        return serverMsg;
    }

    public void setServerMsg(ServerMsg serverMsg) {
        this.serverMsg = serverMsg;
    }

    public ServerStatus getServerStatus() {
        return serverStatus;
    }

    public boolean setServerStatus(ServerStatus serverStatus) {
        switch (serverStatus) {
            case ONLINE: {//切换至在线状态
                this.serverStatus = serverStatus;
                String onlineStatus = preconditionGroupServerCode(serverMsg.getGroupCode(),
                        preconditionServerCode(serverMsg.getServerName(), serverMsg.getServerVersion(), serverMsg.getSequence()));
                logger.info("Server[{}] status is [{}]", onlineStatus, serverStatus);
                break;
            }
            case UNREGISTER:
            case RESTARTING: { //重启命令 在注销状态和启动状态时 不修改状态
                if (getServerStatus().equals(ServerStatus.UNREGISTER)
                        || getServerStatus().equals(ServerStatus.STARTING)) {
                    return false;
                }
            }
            default:
                logger.info("Server status is [{}]", serverStatus);
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
            case MqttMsgBus: {
                //MsgScheduler mqttMsgScheduler = applicationContext.getBean("mqttMsgScheduler", MsgScheduler.class);
                //mqttMsgScheduler.build(this);
                //return mqttMsgScheduler;
            }
            default: {
                try {
                    Class<?> aClass = Class.forName("tech.mystox.framework.mqtt.service.impl.DefaultMqttMsgScheduler", false, Thread.currentThread()
                            .getContextClassLoader());
                    Constructor<?> declaredConstructor = aClass.getDeclaredConstructor(IaContext.class);
                    MsgScheduler mqttMsgScheduler = (MsgScheduler) declaredConstructor.newInstance(iaContext);
                    //MsgScheduler mqttMsgScheduler = new DefaultMsgScheduler();
                    mqttMsgScheduler.build(this);
                    return mqttMsgScheduler;
                } catch (ClassNotFoundException | NoSuchMethodException | InstantiationException |
                         InvocationTargetException | IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    public MsgScheduler createMsgScheduler() {
        return createMsgScheduler("");
    }

    public RegScheduler createRegScheduler(String regType) {
        switch (regType) {
            case ZkRegType: {
                //    RegScheduler regScheduler = applicationContext.getBean("zkRegScheduler", RegScheduler.class);
                //    regScheduler.build(this);
                //    return regScheduler;
            }
            default: {
                try {
                    Class<?> aClass = Class.forName("tech.mystox.framework.register.service.ZkRegScheduler", false, Thread.currentThread()
                            .getContextClassLoader());
                    RegScheduler regScheduler = (RegScheduler) aClass.getDeclaredConstructor().newInstance();
                    regScheduler.build(this);
                    return regScheduler;
                } catch (ClassNotFoundException | InstantiationException | IllegalAccessException |
                         InvocationTargetException | NoSuchMethodException e) {
                    throw new RuntimeException(e);
                }
            }
            //default: {
            //    RegScheduler regScheduler = applicationContext.getBean("zkRegScheduler", RegScheduler.class);
            //    regScheduler.build(this);
            //    return regScheduler;
            //}
        }
    }

    public LoadBalanceScheduler createLoadBalancerScheduler(IaConf.LoadBalanceType balanceType) {
        switch (balanceType) {
            case BASE: {
                //LoadBalanceScheduler loadBalanceScheduler = applicationContext.getBean("baseLoadBalancer", LoadBalanceScheduler.class);
                //loadBalanceScheduler.build(this);
                //return loadBalanceScheduler;
            }
            default: {
                //LoadBalanceScheduler loadBalanceScheduler = applicationContext.getBean("baseLoadBalancer", LoadBalanceScheduler.class);
                try {
                    Class<?> aClass = Class.forName("tech.mystox.framework.balancer.BaseLoadBalancer", false, Thread.currentThread()
                            .getContextClassLoader());
                    LoadBalanceScheduler loadBalanceScheduler = (LoadBalanceScheduler) aClass.getDeclaredConstructor().newInstance();
                    loadBalanceScheduler.build(this);
                    return loadBalanceScheduler;
                } catch (ClassNotFoundException | InstantiationException | IllegalAccessException |
                         InvocationTargetException | NoSuchMethodException e) {
                    throw new RuntimeException(e);
                }
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

    //ApplicationContext applicationContext;

    //public ApplicationContext getApplicationContext() {
    //    return applicationContext;
    //}

    //@Override
    //public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
    //    this.applicationContext = applicationContext;
    //}


    @Override
    public void call(RegState state) throws RegisterException {
        switch (state) {
            case Disconnected: {
                setServerStatus(ServerStatus.OFFLINE);
                logger.warn("[operaCall] Register Disconnected...");
                List<RegisterSub> subList = this.regScheduler.getSubList();
                logger.warn("[operaCall] Cancel msg-schedule sub session");
                this.msgScheduler.removerSubTopic(subList);
                RegisterMsg registerMsg = this.msgScheduler.getIaHandler().getRegisterMsg();
                //getConf().setRegisterUrl(registerMsg.getRegistURI());
                logger.warn("[operaCall] Register reconnected [{}]", registerMsg.getRegisterUrl());
                this.regScheduler.connect(registerMsg.getRegisterUrl());
                logger.warn("[operaCall] Register waiting for rebuilding");
                this.regScheduler.reRegister();
                this.msgScheduler.subTopic(subList);
                setServerStatus(ServerStatus.ONLINE);
                break;
            }

            case RebuildStatus: {
                setServerStatus(ServerStatus.RESTARTING);
                logger.warn("[operaCall] Register rebuilding");
                List<RegisterSub> subList = this.regScheduler.getSubList();
                logger.warn("[operaCall] Cancel msg-schedule sub session");
                this.msgScheduler.removerSubTopic(subList);
                logger.warn("[operaCall] Register waiting for rebuilding");
                getRegScheduler().reRegister();
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


    public ServiceScanner getLocalServiceScanner() {
        return localServiceScanner;
    }

    public ServiceScanner getJarServiceScanner() {
        return jarServiceScanner;
    }
}
