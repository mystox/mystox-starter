package tech.mystox.framework.register.service;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import tech.mystox.framework.common.util.CollectionUtils;
import tech.mystox.framework.config.IaConf;
import tech.mystox.framework.config.WebPrivFuncConfig;
import tech.mystox.framework.core.IaENV;
import tech.mystox.framework.core.OperaCall;
import tech.mystox.framework.core.RegCall;
import tech.mystox.framework.entity.GroupCode;
import tech.mystox.framework.entity.OperaResult;
import tech.mystox.framework.entity.RegisterSub;
import tech.mystox.framework.entity.TopicPrefix;
import tech.mystox.framework.exception.RegisterException;
import tech.mystox.framework.register.runner.ZkHandlerImpl;
import tech.mystox.framework.scheduler.RegScheduler;
import tech.mystox.framework.service.RegHandler;

import java.util.ArrayList;
import java.util.List;

import static tech.mystox.framework.common.util.MqttUtils.preconditionGroupServerCode;
import static tech.mystox.framework.common.util.MqttUtils.preconditionGroupServerPath;

@Component("zkRegScheduler")
@Lazy
public class ZkRegScheduler implements  RegScheduler {
    private final Logger logger = LoggerFactory.getLogger(ZkRegScheduler.class);
    RegHandler regHandler;
    private IaConf iaconf;
    private String groupCode;


    @Override
    public void register() {
        regHandler.register();
    }
    @Override
    public void reRegister() {
        regHandler.register();
    }


    @Override
    public void unregister() {
        regHandler.unregister();
    }

    @Override
    public boolean exists(String nodeData) {
        return regHandler.exists(nodeData);
    }


    /**
     * @return java.util.List<java.lang.String>
     * @Date 14:05 2020/1/4
     * @Param No such property: code for class: Script1
     * @Author mystox
     * @Description 构建默认组装信息
     **/
    @Override
    public List<String> buildOperaMap(String operaCode) {
        List<String> groupCodeList = new ArrayList<>();
        if (GroupCode.ROOT.equals(this.groupCode)){ //如果是root服务，则获取所有的服务节点
            List<String> children = this.getChildren(TopicPrefix.SUB_PREFIX);
            groupCodeList.addAll(children);
        } else {
            groupCodeList.add(this.groupCode);
            groupCodeList.add(GroupCode.ROOT);
        }
        List<String> result = new ArrayList<>();
        groupCodeList.forEach(groupCode->{
            String subPath = preconditionGroupServerPath(TopicPrefix.SUB_PREFIX, groupCode);
            List<String> serverArr = this.getChildren(subPath); //获取订阅表的服务列表
            //遍历订阅服务列表
            for (String serverCode : serverArr) {
                String groupServerCode = preconditionGroupServerCode(groupCode, serverCode);
                String onlineStatus = preconditionGroupServerPath(TopicPrefix.SERVER_STATUS,
                        groupServerCode);
                String serverPath = preconditionGroupServerPath(TopicPrefix.SUB_PREFIX, groupServerCode);
                if (!exists(onlineStatus)) { //检测服务是否已经注销
                    logger.debug("Server [{}] is not online status now...", onlineStatus);
                    continue;
                }
                List<String> serverOperaCodeArr = this.getChildren(serverPath);
                if (serverOperaCodeArr.contains(operaCode)) {
                    result.add(groupServerCode);
                }
            }
        });

        return result;
    }

    @Override
    public List<RegisterSub> getSubList() {
        List<RegisterSub> registerSubList = new ArrayList<>();
        List<RegisterSub> regLocalList = getRegLocalList();
        if (CollectionUtils.isNotEmpty(regLocalList)) registerSubList.addAll(regLocalList);
        List<RegisterSub> regJarList = getRegJarList();
        if (CollectionUtils.isNotEmpty(regJarList)) registerSubList.addAll(regJarList);
        List<RegisterSub> regHttpList = getRegHttpList();
        if (CollectionUtils.isNotEmpty(regHttpList)) registerSubList.addAll(regHttpList);
        return registerSubList;
    }

    private OperaCall caller;

    @Override
    public void initCaller(OperaCall caller) {
        this.caller = caller;
    }


    @Override
    public List<RegisterSub> getRegHttpList() {
        return null;
    }

    @Override
    public List<RegisterSub> getRegLocalList() {
        return this.iaconf.getLocalServiceScanner().getSubList();
    }

    @Override
    public List<RegisterSub> getRegJarList() {
        return this.iaconf.getJarServiceScanner().getSubList();
    }


    @Override
    public void close() {

    }

    @Override
    public void create(String path, byte[] data, int createMode) {
        this.regHandler.create(path, data, createMode);
    }


    @Override
    public String getData(String path) {
        return regHandler.getData(path);
    }

    @Override
    public void setData(String path, byte[] data) {
        regHandler.setData(path, data);
    }


    @Override
    public List<String> getChildren(String path) {
        return regHandler.getChildren(path);
    }

    @Override
    public void deleteNode(String path) {
        regHandler.deleteNode(path);
    }

    @Override
    public void setDataToRegistry(RegisterSub sub) {
        regHandler.setDataToRegistry(sub);
    }

    @Override
    public void build(IaENV iaENV) {
        this.iaconf = iaENV.getConf();
        this.groupCode = iaconf.getGroupCode();
        this.regHandler = new ZkHandlerImpl(iaENV);
        this.regHandler.build();
    }


    @Override
    public void connect(String registerUrl) throws RegisterException {
        regHandler.connect(registerUrl);
    }

    @Override
    public RegCall.RegState getState() {
        return regHandler.getRegState();
    }

    @Override
    public OperaResult registerWebPriv(WebPrivFuncConfig webPrivFuncConfig) throws Exception {
        return regHandler.registerWebPriv(webPrivFuncConfig);
    }
}
