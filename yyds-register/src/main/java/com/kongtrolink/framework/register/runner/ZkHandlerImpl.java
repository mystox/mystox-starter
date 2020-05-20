package com.kongtrolink.framework.register.runner;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.kongtrolink.framework.common.util.MqttUtils;
import com.kongtrolink.framework.config.IaConf;
import com.kongtrolink.framework.config.OperaRouteConfig;
import com.kongtrolink.framework.config.WebPrivFuncConfig;
import com.kongtrolink.framework.core.IaENV;
import com.kongtrolink.framework.core.RegCall;
import com.kongtrolink.framework.entity.*;
import com.kongtrolink.framework.service.RegHandler;
import org.apache.commons.lang3.StringUtils;
import org.apache.zookeeper.*;
import org.apache.zookeeper.data.ACL;
import org.apache.zookeeper.data.Stat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CountDownLatch;

import static com.kongtrolink.framework.common.util.MqttUtils.*;

// @Service(value = "zkHandlerImpl")
public class ZkHandlerImpl implements RegHandler, Watcher {
    private OperaRouteConfig operaRouteConfig;
    // @Autowired
    // IaContext iaContext;
    private IaConf iaconf;
    private IaENV iaENV;
    private String groupCode;
    private String serverName;
    private String serverVersion;
    private Logger logger = LoggerFactory.getLogger(ZkHandlerImpl.class);
    private CountDownLatch latch = new CountDownLatch(1);
    private static final int SESSION_TIMEOUT = 100000;//单位毫秒
    private ZooKeeper zk;

    public ZkHandlerImpl(IaENV iaENV, ApplicationContext applicationContext) {
        this.iaENV = iaENV;
        this.iaconf = iaENV.getConf();
    }

    private void registerConsumerRoute() throws KeeperException, InterruptedException {
        Map<String, List<String>> operaRoute = operaRouteConfig.getOperaRoute();
        String groupCodeServerCode = preconditionGroupServerCode(groupCode, preconditionServerCode(serverName, serverVersion));
        if (operaRoute != null) {
            Set<String> operaCodes = operaRoute.keySet();
            for (String operaCode : operaCodes) {
                List<String> subServerArr = operaRoute.get(operaCode);
                String routePath = preconditionRoutePath(groupCodeServerCode, operaCode);
                if (!exists(routePath))
                    create(routePath, JSONArray.toJSONBytes(subServerArr), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);
                else
                    setData(routePath, JSONArray.toJSONBytes(subServerArr));
            }
        }
    }

    /**
     * 注册web 功能权限
     */
    public OperaResult registerWebPriv(WebPrivFuncConfig webPrivFuncConfig) throws KeeperException, InterruptedException {
        PrivFuncEntity privFunc = webPrivFuncConfig.getPrivFunc();
        if (privFunc != null) {
            //获取服务信息并注册至注册中心
            String privPath = TopicPrefix.PRIV_PREFIX + "/" +
                    preconditionGroupServerCode(groupCode, preconditionServerCode(serverName, serverVersion));
            if (exists(privPath))
                setData(privPath, JSONObject.toJSONBytes(privFunc));
            else
                create(privPath, JSONObject.toJSONBytes(privFunc), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
        } else {
            logger.warn("web privilege function config is null...");
        }
        return null;
    }

    /**
     * 往注册中心注册数据
     *
     * @param sub
     * @throws KeeperException
     * @throws InterruptedException
     */
    @Override
    public void setDataToRegistry(RegisterSub sub) {
        String operaCode = sub.getOperaCode();
        String nodePath = MqttUtils.preconditionSubTopicId(
                preconditionGroupServerCode(groupCode, preconditionServerCode(serverName, serverVersion)), operaCode);
        if (!exists(nodePath))
            try {
                create(nodePath, JSONObject.toJSONBytes(sub), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);
            } catch (KeeperException | InterruptedException e) {
                logger.error("set data to registry error [{}]", e.toString());
                if (logger.isDebugEnabled())
                    e.printStackTrace();
            }
        else {
            setData(nodePath, JSONObject.toJSONBytes(sub));
        }
    }

    public ZooKeeper getZk() {
        return this.zk;
    }

    /**
     * 注册服务信息
     *
     * @throws KeeperException      zk异常
     * @throws InterruptedException zk中断异常
     * @throws IOException          io异常
     */
    private void initTree() throws KeeperException, InterruptedException, IOException {
        //创建根
        if (!exists(TopicPrefix.TOPIC_PREFIX))
            create(TopicPrefix.TOPIC_PREFIX, null, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
        //订阅列表目录
        initBranch(TopicPrefix.SUB_PREFIX, groupCode);
        // //请求列表目录
        // initBranch(TopicPrefix.PUB_PREFIX, groupCode);
        //功能权限目录
        initBranch(TopicPrefix.PRIV_PREFIX, groupCode);
        //在线标志目录
        initBranch(TopicPrefix.SERVER_STATUS, groupCode);
        //操作请求路由表目录
        initBranch(TopicPrefix.OPERA_ROUTE, groupCode);
    }


    /**
     * @param topicPrefix 前缀
     * @param groupCode   服务组code
     * @param serverCode  服务code
     * @throws KeeperException      zk异常
     * @throws InterruptedException zk中断异常
     */
    void createPath(String topicPrefix, String groupCode, String serverCode) throws KeeperException, InterruptedException {
        if (!exists(topicPrefix))
            create(topicPrefix, null, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
        if (StringUtils.isNotBlank(groupCode)) {
            String groupPath = topicPrefix + "/" + groupCode;
            if (!exists(groupPath))
                create(groupPath, null, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
            if (StringUtils.isNotBlank(serverCode)) {
                String serverPath = groupPath + "/" + serverCode;
                if (!exists(serverPath))
                    create(serverPath, null, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
            }
        }
    }

    /**
     * @param topicPrefix
     * @param groupCode
     * @throws KeeperException
     * @throws InterruptedException
     */
    void initBranch(String topicPrefix, String groupCode) throws KeeperException, InterruptedException {
        if (!exists(topicPrefix))
            create(topicPrefix, null, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
        if (StringUtils.isNotBlank(groupCode)) {
            String groupPath = topicPrefix + "/" + groupCode;
            if (!exists(groupPath))
                create(groupPath, null, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
        }
    }


    /**
     * 往注册中心注册数据
     *
     * @param sub
     * @throws KeeperException
     * @throws InterruptedException
     */
    public void registeringSub(RegisterSub sub) throws KeeperException, InterruptedException {
        String operaCode = sub.getOperaCode();
        String nodePath = MqttUtils.preconditionSubTopicId(
                preconditionGroupServerCode(groupCode, preconditionServerCode(serverName, serverVersion)), operaCode);
        if (!exists(nodePath))
            create(nodePath, JSONObject.toJSONBytes(sub), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);
        else {
            setData(nodePath, JSONObject.toJSONBytes(sub));
        }
    }


    /**
     * @return void
     * @Date 0:22 2020/1/6
     * @Param No such property: code for class: Script1
     * @Author mystox
     * @Description 订阅统一AckTopic
     **/
    /*private void ackTopic(MsgHandler mqttHandlerAck) {
        String ackTopicId = preconditionSubACKTopicId(preconditionGroupServerCode(groupCode, preconditionServerCode(serverName, serverVersion)));
        logger.info("add AckTopicId is [{}]", ackTopicId);
        if (!mqttHandlerAck.isExists(ackTopicId))
            mqttHandlerAck.addSubTopic(ackTopicId, 2);
    }*/

    /**
     * 注册中心注册节点信息
     *
     * @throws IOException
     * @throws InterruptedException
     * @throws KeeperException
     */
    public void register() {
        try {
            initTree();//初始化目录信息
            if (locks()) //获取注册锁
            {
                // initConsumer();//定义消费目录
                initConsumerRoute();//定义消费路由目录
                initProvider();//定义服务供给目录
                registerWebPriv(this.iaconf.getWebPrivFuncConfig());//注册WEB功能权限
                registerProvider(getRegLocalList());//订阅
                registerConsumerRoute(); //注册路由
            }
        } catch (KeeperException | IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * //往服务节点注册订阅服务信息
     *
     * @param subList
     * @throws KeeperException
     * @throws InterruptedException
     */
    public void registerProvider(List<RegisterSub> subList) throws KeeperException, InterruptedException {
        for (RegisterSub sub : subList) {//注册provider
            registeringSub(sub);
        }
    }

    /**
     * @return void
     * @Date 0:21 2020/1/6
     * @Param No such property: code for class: Script1
     * @Author mystox
     * @Description 注册服务信息
     **/
    private boolean locks() throws KeeperException, InterruptedException, InterruptedIOException {
        //获取服务信息并注册
        ServerMsg serverMsg = new ServerMsg(iaconf.getHost(), iaconf.getPort(), iaconf.getServerName(), iaconf.getServerVersion(),
                iaconf.getRouteMark(), iaconf.getPageRoute(), iaconf.getServerUri(), iaconf.getTitle(), groupCode, iaconf.getMyId());
        String onlineStatus = preconditionGroupServerPath(TopicPrefix.SERVER_STATUS,
                preconditionGroupServerCode(groupCode,
                        preconditionServerCode(serverName, serverVersion)));

        // long sessionId = zk.getSessionId();
        // System.out.println(sessionId);
        while (true) {
            if (exists(onlineStatus)) {
                logger.warn("server[{}] status is already online ... exits", preconditionGroupServerCode(groupCode, preconditionServerCode(serverName, serverVersion)));
                //  throw new InterruptedIOException();
                String nodeData = getData(onlineStatus);
                ServerMsg nodeMsg = JSONObject.parseObject(nodeData, ServerMsg.class);
                if (nodeMsg.getMyid().equals(iaconf.getMyId())) {//如果是我，则刷新，不是我，则等待
                    logger.warn("is mine.....id is [{}]", nodeMsg.getMyid());
                    setData(onlineStatus, JSONObject.toJSONBytes(serverMsg));
                    return true;
                } else
                    Thread.sleep(3000);
            } else {
                create(onlineStatus, JSONObject.toJSONBytes(serverMsg), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);
                return true;
            }
        }
    }

    /**
     * 定义sub节点
     */
    private void initProvider() throws KeeperException, InterruptedException {

        String lock = preconditionGroupServerPath(TopicPrefix.SERVER_STATUS,
                preconditionGroupServerCode(groupCode,
                        preconditionServerCode(serverName, serverVersion)));
        if (exists(lock)) { //检测锁的情况
            String nodeName = preconditionGroupServerPath(TopicPrefix.SUB_PREFIX,
                    preconditionGroupServerCode(groupCode,
                            preconditionServerCode(serverName, serverVersion)));
            if (!exists(nodeName))
                create(nodeName, null, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
        }

    }

    /**
     * 定义Pub节点
     */
    /*public void initConsumer() throws KeeperException, InterruptedException {
        String lock = preconditionGroupServerPath(TopicPrefix.SERVER_STATUS,
                preconditionGroupServerCode(groupCode,
                        preconditionServerCode(serverName, serverVersion)));
        if (exists(lock)) { //检测锁的情况
            String nodeName = preconditionGroupServerPath(TopicPrefix.PUB_PREFIX,
                    preconditionGroupServerCode(groupCode,
                            preconditionServerCode(serverName, serverVersion)));
            if (!exists(nodeName))
                create(nodeName, null, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
        }

    }*/

    /**
     * 定义Pub节点
     */
    public void initConsumerRoute() throws KeeperException, InterruptedException {
        String lock = preconditionGroupServerPath(TopicPrefix.SERVER_STATUS,
                preconditionGroupServerCode(groupCode,
                        preconditionServerCode(serverName, serverVersion)));
        if (exists(lock)) { //检测锁的情况
            String nodeName = preconditionGroupServerPath(TopicPrefix.OPERA_ROUTE,
                    preconditionGroupServerCode(groupCode,
                            preconditionServerCode(serverName, serverVersion)));
            if (!exists(nodeName))
                create(nodeName, null, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
        }

    }


    public List<RegisterSub> getRegHttpList() {
        List<RegisterSub> subList = new ArrayList<>();
        //todo 暂不实现
        return subList;
    }


    public List<RegisterSub> getRegLocalList() {
        return this.iaconf.getLocalServiceScanner().getSubList();
    }

    public List<RegisterSub> GetRegJarList() {
        return this.iaconf.getJarServiceScanner().getSubList();
    }

    @Override
    public boolean exists(String nodeData) {
        try {
            return zk.exists(nodeData, true) != null;
        } catch (Exception e) {
            logger.error("zk exists check error...[{}]", e.toString());
            if (logger.isDebugEnabled())
                e.printStackTrace();
        }
        return false;
    }

    @Override
    public synchronized void create(String path, byte[] data, int createMode) {
        CreateMode CM = null;
        switch (createMode) {
            case IaConf.EPHEMERAL:
                CM = CreateMode.EPHEMERAL;
                break;
            case IaConf.EPHEMERAL_SEQUENTIAL:
                CM = CreateMode.EPHEMERAL_SEQUENTIAL;
                break;
            case IaConf.PERSISTENT:
                CM = CreateMode.PERSISTENT;
                break;
            case IaConf.PERSISTENT_SEQUENTIAL:
                CM = CreateMode.PERSISTENT_SEQUENTIAL;
                break;
        }
        if (!exists(path))
            try {
                zk.create(path, data, ZooDefs.Ids.OPEN_ACL_UNSAFE, CM);
            } catch (KeeperException | InterruptedException e) {
                logger.warn("zk path exists...[{}]", e.toString());
                if (logger.isDebugEnabled())
                    e.printStackTrace();
            }
    }


    public void build() {
        // IaENV iaENV = iaContext.getIaENV();
        // this.iaconf = iaContext.getIaENV().getConf();
        this.groupCode = iaconf.getGroupCode();
        this.serverName = iaconf.getServerName();
        this.serverVersion = iaconf.getServerVersion();
        this.operaRouteConfig = iaconf.getOperaRouteConfig();
        initCaller(iaENV);
    }

    @Override
    public RegCall.RegState getServerState() {
        ZooKeeper.States state = zk.getState();
        if (state.isConnected()) return RegCall.RegState.SyncConnected;
        if (ZooKeeper.States.CLOSED.equals(state)) return RegCall.RegState.Closed;
        return RegCall.RegState.Unknown;
    }


    @Override
    public void connect(String registerUrl) {
        try {
            if (zk == null) {
                zk = new ZooKeeper(registerUrl, SESSION_TIMEOUT, this);
                latch.await();
            } else {
                if (!zk.getState().isAlive()) {
                    logger.warn("zk state is not alive create new connector...");
                    zk = new ZooKeeper(registerUrl, SESSION_TIMEOUT, this);
                    latch.await();
                } else {
                    logger.warn("zk state is [{}], close zk and create new one...", JSONObject.toJSONString(zk.getState()));
                    close();
                    zk = new ZooKeeper(registerUrl, SESSION_TIMEOUT, this);
                    latch.await();
                }
            }
        } catch (IOException | InterruptedException e) {
            logger.error("zk connect error [{}]", e.toString());
            if (logger.isDebugEnabled())
                e.printStackTrace();
        }
        long sessionId = zk.getSessionId();
        iaconf.setMyId(String.valueOf(sessionId));
        logger.info("zk connected state[{}] sessionId[0x{}]", zk.getState(), Long.toHexString(sessionId));
    }


    public String create(String path, byte[] data, List<ACL> acl, CreateMode createMode) throws KeeperException, InterruptedException {
        if (!exists(path))
            return zk.create(path, data, acl, createMode);
        else return null;
    }

    public String getData(String path) {
        byte[] data = new byte[0];
        try {
            data = zk.getData(path, true, null);
        } catch (KeeperException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if (data != null)
            return new String(data);
        else return null;
    }

    public void setData(String path, byte[] data) {
        Stat stat = null;
        try {
            stat = zk.setData(path, data, -1);
        } catch (KeeperException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        logger.debug(stat.toString());
        // return stat;
    }

    @Override
    public void close() throws InterruptedException {
        if (zk != null)
            zk.close();
    }

    @Override
    public List<String> getChildren(String path) {
        try {
            return zk.getChildren(path, true);
        } catch (KeeperException | InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void deleteNode(String path) {
        try {
            zk.delete(path, -1);
        } catch (InterruptedException | KeeperException e) {
            e.printStackTrace();
        }
    }

    private RegCall regCall;

    public void initCaller(IaENV regCall) {
        this.regCall = regCall;
    }


    public void process(WatchedEvent watchedEvent) {
        Watcher.Event.KeeperState state = watchedEvent.getState();
        Watcher.Event.EventType eventType = watchedEvent.getType();
        String path = watchedEvent.getPath();
        if (state == Watcher.Event.KeeperState.SyncConnected && latch.getCount() != 0) {
            logger.info("zk connect successful...");
            latch.countDown();
        } else if (Event.EventType.NodeDeleted == eventType) {

            if (path.startsWith(TopicPrefix.SERVER_STATUS)) {
                logger.warn("serverStatus:" + path + "been delete, try recover...");
                try {
                    regCall.call(RegCall.RegState.RebuildStatus);
                    logger.warn("Node:" + path + " recovered...");
                } catch (InterruptedException e) {
                    logger.error("zk rebuild error...[{}]", e.toString());
                    if (logger.isDebugEnabled()) e.printStackTrace();
                }
            }
        } else if (state == Watcher.Event.KeeperState.Expired
                || state == Watcher.Event.KeeperState.Disconnected
                ) {
            logger.warn("zookeeper Expired|Disconnected event [{}] ...", state);
            synchronized (ZkHandlerImpl.class) {
                try {
                    latch = new CountDownLatch(1);
                    regCall.call(RegCall.RegState.Disconnected);
                } catch (Exception e) {
                    logger.error("zk disconnect error...[{}]", e.toString());
                    if (logger.isDebugEnabled()) e.printStackTrace();
                }
            }
        }
        logger.debug("zk trigger event type: [{}] content: [{}] ", watchedEvent.getType(), watchedEvent.toString());
    }
}
