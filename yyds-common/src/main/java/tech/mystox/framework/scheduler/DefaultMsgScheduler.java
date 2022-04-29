package tech.mystox.framework.scheduler;

import org.springframework.context.ApplicationContext;
import tech.mystox.framework.config.IaConf;
import tech.mystox.framework.core.IaENV;
import tech.mystox.framework.core.OperaCall;
import tech.mystox.framework.entity.RegisterSub;
import tech.mystox.framework.service.Impl.DefaultMsgHandler;
import tech.mystox.framework.service.MsgHandler;

import java.util.List;

public class DefaultMsgScheduler implements MsgScheduler {
    MsgHandler iaHandler;

    private ApplicationContext applicationContext;
    private IaConf iaconf;
    private IaENV iaENV;
    private String groupCode;
    private String serverName;
    private String serverVersion;
    @Override
    public void subTopic(List<RegisterSub> subList) {

    }

    @Override
    public void removerSubTopic(List<RegisterSub> subList) {

    }

    @Override
    public void initCaller(OperaCall caller) {

    }

    @Override
    public void build(IaENV iaENV) {
        this.iaENV = iaENV;
        this.iaconf = iaENV.getConf();
        this.groupCode = iaconf.getGroupCode();
        this.serverName = iaconf.getServerName();
        this.serverVersion = iaconf.getServerVersion();
        this.iaHandler = new DefaultMsgHandler(iaENV, applicationContext);
    }

    @Override
    public void unregister() {

    }

    @Override
    public MsgHandler getIaHandler() {
        return null;
    }
}
