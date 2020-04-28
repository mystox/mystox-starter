package com.kongtrolink.framework.scheudler;


import com.kongtrolink.framework.common.util.MqttUtils;
import com.kongtrolink.framework.config.IaConf;
import com.kongtrolink.framework.core.IaENV;
import com.kongtrolink.framework.core.callMe;
import com.kongtrolink.framework.entity.RegisterSub;
import com.kongtrolink.framework.service.MsgHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.kongtrolink.framework.common.util.MqttUtils.*;


@Component("mqttMsgSheculder")
@Lazy
public class MqttMsgScheduler implements MsgScheduler {

    @Autowired
    @Qualifier("MqttHandler")
    MsgHandler iaHandler;

    private IaConf iaconf;
    private String groupCode;
    private String serverName;
    private String serverVersion;
    private Logger logger = LoggerFactory.getLogger(MqttMsgScheduler.class);


    public MqttMsgScheduler()
    {
        logger.debug("-----the class "+ MqttMsgScheduler.class+" instructed");
    }
    /**
     * @Date 0:22 2020/1/6
     * @Param No such property: code for class: Script1
     * @return void
     * @Author mystox
     * @Description 订阅统一AckTopic
     **/

    @Override
    public void build(IaENV iaENV) {
        this.iaconf=iaENV.getConf();
        this.groupCode=iaconf.getGroupCode();
        this.serverName=iaconf.getServerName();
        this.serverVersion=iaconf.getServerVersion();
    }

    private void ackTopic() {
        String ackTopicId = preconditionSubACKTopicId(preconditionGroupServerCode(groupCode, preconditionServerCode(serverName, serverVersion)));
        if (!iaHandler.isAckExists(ackTopicId))
            iaHandler.addAckTopic(ackTopicId, 2);
    }
    /**
     * @Date 0:22 2020/1/6
     * @Param No such property: code for class: Script1
     * @return void
     * @Author mystox
     * @Description 订阅订阅表信息
     **/
    public  void subTopic(List<RegisterSub> subList) {
        subList.forEach(sub -> {
            String operaCode = sub.getOperaCode();
            String topicId = MqttUtils.preconditionSubTopicId(
                    preconditionGroupServerCode(groupCode, preconditionServerCode(serverName, serverVersion)), operaCode);
            if (iaHandler != null) {
                if (!iaHandler.isExists(topicId))
                    //logger.info("订阅了:{} ",topicId);
                    iaHandler.addSubTopic(topicId, 2);
            }
        });
        ackTopic();
    }

    @Override
    public void  removerSubTopic(List<RegisterSub> subList)
    {
        subList.forEach(sub -> {
            String operaCode = sub.getOperaCode();
            String topicId = MqttUtils.preconditionSubTopicId(
                    preconditionGroupServerCode(groupCode, preconditionServerCode(serverName, serverVersion)), operaCode);
            if (iaHandler != null) {
                if (iaHandler.isExists(topicId))
                    //logger.info("订阅了:{} ",topicId);
                    iaHandler.removeSubTopic(topicId);
            }
        });
        String ackTopicId = preconditionSubACKTopicId(preconditionGroupServerCode(groupCode, preconditionServerCode(serverName, serverVersion)));
       if (iaHandler.isAckExists(ackTopicId))
             iaHandler.removeAckSubTopic(ackTopicId);
    }
    @Override
    public void initCaller(callMe caller) {

    }

    @Override
    public MsgHandler getIahander() {
        return this.iaHandler;
    }

}
