package tech.mystox.framework.core;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tech.mystox.framework.config.IaConf;
import tech.mystox.framework.entity.RegisterMsg;
import tech.mystox.framework.entity.RegisterType;
import tech.mystox.framework.entity.RegisterSub;
import tech.mystox.framework.entity.ServerStatus;
import tech.mystox.framework.exception.RegisterException;
import tech.mystox.framework.scheduler.MsgScheduler;
import tech.mystox.framework.scheduler.RegScheduler;

import java.io.IOException;
import java.util.List;

/**
 * 注册类
 */
public class IaRegister {
    private static final Logger logger = LoggerFactory.getLogger(IaRegister.class);

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
//        msgScheduler.build(this.iaEnv);
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
    public void connect() throws RegisterException {
        registerMsg = buildRegisterMsg(iaConf);
        //this.iaConf.setRegisterUrl(registerMsg.getRegisterURI());
        logger.info("{} registerUrl is: [{}]", iaConf.getServerName(), registerMsg.getRegisterURI());
        this.regScheduler.connect(registerMsg.getRegisterUrl());
    }

    public static RegisterMsg buildRegisterMsg(IaConf iaConf) {
        String registerUrl = iaConf.getRegisterUrl();
        String[] split = StringUtils.splitByWholeSeparator(registerUrl, "://");
        if (split == null || split.length != 2
                || StringUtils.isBlank(split[0])
                || StringUtils.isBlank(split[1])) {
            throw new IllegalArgumentException("Register url is invalid: " + registerUrl);
        }
        String registerUrlHeader = split[0];
        RegisterMsg registerMsg = new RegisterMsg();
        registerMsg.setRegisterUrl(split[1]);
        registerMsg.setRegisterUrlHeader(registerUrlHeader);
        if (StringUtils.equals(RegisterType.ZOOKEEPER.toString(), StringUtils.upperCase(registerUrlHeader))) {
            registerMsg.setRegisterType(RegisterType.ZOOKEEPER);
        }
        return registerMsg;
    }

}
