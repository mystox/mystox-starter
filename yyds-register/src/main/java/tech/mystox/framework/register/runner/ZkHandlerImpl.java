package tech.mystox.framework.register.runner;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.apache.zookeeper.*;
import org.apache.zookeeper.data.ACL;
import org.apache.zookeeper.data.Stat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tech.mystox.framework.common.util.MqttUtils;
import tech.mystox.framework.config.IaConf;
import tech.mystox.framework.config.OperaRouteConfig;
import tech.mystox.framework.config.WebPrivFuncConfig;
import tech.mystox.framework.core.IaENV;
import tech.mystox.framework.core.RegCall;
import tech.mystox.framework.entity.*;
import tech.mystox.framework.exception.RegisterException;
import tech.mystox.framework.register.utils.DistributedLock;
import tech.mystox.framework.service.RegHandler;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.LongAdder;

import static tech.mystox.framework.common.util.MqttUtils.*;

// @Service(value = "zkHandlerImpl")
public class ZkHandlerImpl implements RegHandler, Watcher {
    private OperaRouteConfig operaRouteConfig;
    //    private IaContext iaContext;
    private final IaConf iaConf;
    private final IaENV iaENV;
    private String groupCode;
    private String serverName;
    private String serverVersion;
    private final Logger logger = LoggerFactory.getLogger(ZkHandlerImpl.class);
    private CountDownLatch latch = new CountDownLatch(1);
    private ZooKeeper zk;
    private final LongAdder longAdder = new LongAdder();
    private final LongAdder connectErrorCount = new LongAdder();
    private DistributedLock registerLock;

    public ZkHandlerImpl(IaENV iaENV) {
        this.iaENV = iaENV;
        this.iaConf = iaENV.getConf();
    }

    private void registerConsumerRoute() throws KeeperException, InterruptedException {
        Map<String, List<String>> operaRoute = operaRouteConfig.getOperaRoute();
        String groupCodeServerCode = preconditionGroupServerCode(groupCode, preconditionServerCode(serverName, serverVersion));
        if (operaRoute != null) {
            Set<String> operaCodes = operaRoute.keySet();
            for (String operaCode : operaCodes) {
                List<String> subServerArr = operaRoute.get(operaCode);
                String routePath = preconditionRoutePath(groupCodeServerCode, operaCode);
                if (!exists(routePath, true))
                    create(routePath, JSONArray.toJSONBytes(subServerArr), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);
                else {
                    setData(routePath, JSONArray.toJSONBytes(subServerArr));
                }
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
            if (exists(privPath, true))
                setData(privPath, JSONObject.toJSONBytes(privFunc));
            else
                create(privPath, JSONObject.toJSONBytes(privFunc), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
        } else {
            logger.warn("Web privilege function config is null...");
        }
        return null;
    }


    /**
     * 往注册中心注册数据
     *
     * @param sub 注册数据
     */
    @Override
    public void setDataToRegistry(RegisterSub sub) {
        String operaCode = sub.getOperaCode();
        String nodePath = MqttUtils.preconditionSubTopicId(
                preconditionGroupServerCode(groupCode, preconditionServerCode(serverName, serverVersion, iaConf.getSequence())), operaCode);
        if (!exists(nodePath))
            try {
                create(nodePath, JSONObject.toJSONBytes(sub), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);
            } catch (KeeperException | InterruptedException e) {
                logger.error("Data set to node[{}] registry error", nodePath, e);
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
        if (!exists(TopicPrefix.TOPIC_PREFIX, true))
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
        if (!exists(topicPrefix, true))
            create(topicPrefix, null, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
        if (StringUtils.isNotBlank(groupCode)) {
            String groupPath = topicPrefix + "/" + groupCode;
            if (!exists(groupPath, true))
                create(groupPath, null, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
            if (StringUtils.isNotBlank(serverCode)) {
                String serverPath = groupPath + "/" + serverCode;
                if (!exists(serverPath, true))
                    create(serverPath, null, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
            }
        }
    }

    /**
     * @param topicPrefix topic前缀
     * @param groupCode   组编码前缀
     * @throws KeeperException      zookeeper异常
     * @throws InterruptedException 中断异常
     */
    void initBranch(String topicPrefix, String groupCode) throws KeeperException, InterruptedException {
        if (!exists(topicPrefix, true))
            create(topicPrefix, null, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
        if (StringUtils.isNotBlank(groupCode)) {
            String groupPath = preconditionGroupServerPath(topicPrefix, groupCode);
            if (!exists(groupPath, true))
                create(groupPath, null, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
        }
    }


    /**
     * 往注册中心注册数据
     *
     * @param sub 注册订阅数据
     * @throws KeeperException      zookeeper异常
     * @throws InterruptedException 中断异常
     */
    public void registeringSub(RegisterSub sub) throws KeeperException, InterruptedException {
        String operaCode = sub.getOperaCode();
        String nodePath = MqttUtils.preconditionSubTopicId(
                preconditionGroupServerCode(groupCode, preconditionServerCode(serverName, serverVersion, iaConf.getSequence())), operaCode);
        if (!exists(nodePath, true))
            create(nodePath, JSONObject.toJSONBytes(sub), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);
        else {
            setData(nodePath, JSONObject.toJSONBytes(sub));
        }
    }

    /**
     * 注销注册中心能力
     *
     * @param sub 注册订阅数据
     */
    public void unregisteringSub(RegisterSub sub) {
        if (!zk.getState().isAlive()) return;
        String operaCode = sub.getOperaCode();
        String nodePath = MqttUtils.preconditionSubTopicId(
                preconditionGroupServerCode(groupCode, preconditionServerCode(serverName, serverVersion, iaConf.getSequence())), operaCode);
        if (exists(nodePath, false))
            deleteNode(nodePath);
    }

    @Override
    public void unregister() {
        if (!zk.getState().isAlive()) return;
        List<RegisterSub> subList = iaENV.getRegScheduler().getSubList();
        unregisterProvider(subList); //清除服务注册表
    }


    /*
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
     */
    public void register() {
        try {
            initTree();//初始化目录信息
            if (locks()) {//获取注册锁
                //订阅消息能力
                List<RegisterSub> subList = this.iaENV.getRegScheduler().getSubList();
                registerMsgAbility(subList);
                // initConsumer();//定义消费目录
                initConsumerRoute();//定义消费路由目录
                initProvider();//定义服务供给目录
                registerWebPriv(this.iaConf.getWebPrivFuncConfig());//注册WEB功能权限
                registerProvider(subList);//订阅
                registerConsumerRoute(); //注册路由
                //                iaENV.setServerStatus(ServerStatus.ONLINE);
            }
        } catch (KeeperException | IOException | InterruptedException e) {
            logger.error("Register exception... ", e);
            System.exit(1);
            //            e.printStackTrace();
        } catch (Exception e) {
            logger.error("Register other exception... ", e);
            System.exit(1);
            //            e.printStackTrace();
        }
    }

    /**
     * 注册消息能力
     */
    private void registerMsgAbility(List<RegisterSub> subList) {
        logger.info("register msg topic ability...");
        iaENV.getMsgScheduler().subTopic(subList);
    }

    /**
     * //往服务节点注册订阅服务信息
     *
     * @param subList 注册订阅数据
     * @throws KeeperException      zookeeper异常
     * @throws InterruptedException 中断异常
     */
    public void registerProvider(List<RegisterSub> subList) throws KeeperException, InterruptedException {
        for (RegisterSub sub : subList) {//注册provider
            registeringSub(sub);
        }
    }

    public void unregisterProvider(List<RegisterSub> subList) {
        for (RegisterSub sub : subList) {//注册provider
            unregisteringSub(sub);
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
        ServerMsg serverMsg = new ServerMsg(iaConf.getHost(), iaConf.getPort(), iaConf.getServerName(), iaConf.getServerVersion(),
                iaConf.getRouteMark(), iaConf.getPageRoute(), iaConf.getServerUri(), iaConf.getTitle(), groupCode, iaConf.getMyId());
        //如果是可重复注册服务，则获取服务id
        //        if (iaconf.isDuplicate()) {
        //            logger.info("server is duplicate status append my id [{}]", iaconf.getMyId());
        //            serverVersion += "_" + iaconf.getMyId();
        //
        //            iaconf.setServerVersion(serverVersion);
        //        }
        serverMsg.setExtension(iaConf.getExtensionConfig().getExtension());
        iaENV.setServerMsg(serverMsg);
        String onlineStatus = preconditionGroupServerPath(TopicPrefix.SERVER_STATUS,
                preconditionGroupServerCode(groupCode,
                        preconditionServerCode(serverName, serverVersion, iaConf.getSequence())));
        // long sessionId = zk.getSessionId();
        // System.out.println(sessionId);
        boolean duplicate = iaConf.isDuplicate();
        if (duplicate) {
            while (exists(onlineStatus, true)) {
                longAdder.add(1);
                long sequence = longAdder.longValue();
                iaConf.setSequence(sequence);
                logger.info("Server[{}] is duplicate status append sequence [{}]", preconditionGroupServerCode(groupCode,
                        preconditionServerCode(serverName, serverVersion, iaConf.getSequence())), sequence);
                onlineStatus = preconditionGroupServerPath(TopicPrefix.SERVER_STATUS,
                        preconditionGroupServerCode(groupCode,
                                preconditionServerCode(serverName, serverVersion, sequence)));
            }
        } else {
            iaENV.setServerStatus(ServerStatus.WAITING);
            if (registerLock.isLocked()) { //重复注册需要判定解锁重新加锁
                logger.warn("Lock stat is locked unlocking...");
                registerLock.unlock();
            }
            registerLock.lock();
            boolean interrupted = Thread.currentThread().isInterrupted();
            if (interrupted) {
                throw new InterruptedException("Register lock errors happens!!");
            }
            Stat exists = zk.exists(onlineStatus, false);
            if (exists != null) {
                logger.warn("Server[{}] status is already exits ... recover it!", onlineStatus);
                //是否应该删除节点再添加注册信息
                long ephemeralOwner = exists.getEphemeralOwner();
                long sessionId = zk.getSessionId();
                if (ephemeralOwner != sessionId) {
                    logger.warn("Server status path [{}] is not mine, zk: [{}] -> local: [{}] recreate", onlineStatus, ephemeralOwner, sessionId);
                    recreate(onlineStatus, JSONObject.toJSONBytes(serverMsg));
                    return true;
                }
            }
        }
        serverMsg.setSequence(iaConf.getSequence());
        create(onlineStatus, JSONObject.toJSONBytes(serverMsg), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);
        return true;
    }

    private void recreate(String path, byte[] data) throws InterruptedException, KeeperException {
        try {
            zk.delete(path, -1);
        } catch (InterruptedException e) {
            logger.error("Recreate InterruptedException", e);
            throw e;
        } catch (KeeperException e) {
            if (KeeperException.Code.NONODE.equals(e.code())) {
                logger.warn("Recreate zookeeper node[{}] error, continue...", path, e);
            }
        }
        create(path, data, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);
    }

    /**
     * 定义sub节点
     */
    private void initProvider() throws KeeperException, InterruptedException {

        String lock = preconditionGroupServerPath(TopicPrefix.SERVER_STATUS,
                preconditionGroupServerCode(groupCode,
                        preconditionServerCode(serverName, serverVersion, iaConf.getSequence())));
        if (exists(lock, true)) { //检测服務的情况
            String nodeName = preconditionGroupServerPath(TopicPrefix.SUB_PREFIX,
                    preconditionGroupServerCode(groupCode,
                            preconditionServerCode(serverName, serverVersion, iaConf.getSequence())));
            if (!exists(nodeName, true))
                create(nodeName, null, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
        }

    }

    /*
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
                        preconditionServerCode(serverName, serverVersion, iaConf.getSequence())));
        if (exists(lock, true)) { //检测锁的情况
            String nodeName = preconditionGroupServerPath(TopicPrefix.OPERA_ROUTE,
                    preconditionGroupServerCode(groupCode,
                            preconditionServerCode(serverName, serverVersion)));
            if (!exists(nodeName, true))
                create(nodeName, null, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
        }

    }


    public List<RegisterSub> getRegHttpList() {
        //        List<RegisterSub> subList = new ArrayList<>();
        //todo 暂不实现
        return null;
    }


    public List<RegisterSub> getRegLocalList() {
        return this.iaConf.getLocalServiceScanner().getSubList();
    }

    public List<RegisterSub> getRegJarList() {
        return this.iaConf.getJarServiceScanner().getSubList();
    }

    @Override
    public boolean exists(String nodePath) {
        return exists(nodePath, false);
    }

    public boolean exists(String nodePath, boolean watch) {
        try {
            return zk.exists(nodePath, watch) != null;
        } catch (InterruptedException | KeeperException e) {
            logger.error("Zookeeper exists check error...", e);
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
        try {
            create(path, data, ZooDefs.Ids.OPEN_ACL_UNSAFE, CM);
        } catch (KeeperException | InterruptedException e) {
            logger.warn("Zookeeper path[{}] create error...", path, e);
        }
        //        if (!exists(path,true))
        //            try {
        //                zk.create(path, data, ZooDefs.Ids.OPEN_ACL_UNSAFE, CM);
        //            } catch (KeeperException | InterruptedException e) {
        //                logger.warn("Zookeeper path[{}] exists error...", path, e);
        //                if (logger.isDebugEnabled())
        //                    e.printStackTrace();
        //            }
    }


    public void build() {
        // IaENV iaENV = iaContext.getIaENV();
        // this.iaconf = iaContext.getIaENV().getConf();
        this.groupCode = iaConf.getGroupCode();
        this.serverName = iaConf.getServerName();
        this.serverVersion = iaConf.getServerVersion();
        this.operaRouteConfig = iaConf.getOperaRouteConfig();
        initCaller(iaENV);
    }

    @Override
    public RegCall.RegState getRegState() {
        if (zk != null) {
            ZooKeeper.States state = zk.getState();
            if (state.isConnected()) return RegCall.RegState.SyncConnected;
            if (ZooKeeper.States.CLOSED.equals(state)) return RegCall.RegState.Closed;
        }
        return RegCall.RegState.Unknown;
    }


    @Override
    public void connect(String registerUrl) throws RegisterException {
        try {
            if (zk == null) {
                zk = new ZooKeeper(registerUrl, iaConf.getRegSessionTimeout(), this);
                latch.await();
            } else {
                if (!zk.getState().isAlive()) {
                    logger.warn("Zookeeper state is not alive create new connector...");
                    zk = new ZooKeeper(registerUrl, iaConf.getRegSessionTimeout(), this);
                    latch.await();
                } else {
                    logger.warn("Zookeeper state is [{}], close zk and create new one...", JSONObject.toJSONString(zk.getState()));
                    close();
                    zk = new ZooKeeper(registerUrl, iaConf.getRegSessionTimeout(), this);
                    latch.await();
                }
            }
            long sessionId = zk.getSessionId();
            iaConf.setMyId(String.valueOf(sessionId));
            logger.info("Zookeeper connected state[{}] sessionId[0x{}]", zk.getState(), Long.toHexString(sessionId));
            if (!iaConf.isDuplicate())
                this.registerLock = new DistributedLock(registerUrl, iaConf.getRegSessionTimeout(), iaConf.getServerName());
        } catch (IOException | InterruptedException | KeeperException e) {
            logger.error("Zookeeper connect error", e);
            throw new RegisterException("Zookeeper connect error", e);
            //            if (logger.isDebugEnabled())
            //                e.printStackTrace();
        }
    }


    public void create(String path, byte[] data, List<ACL> acl, CreateMode createMode) throws KeeperException, InterruptedException {
        if (!exists(path, true)) {
            zk.create(path, data, acl, createMode);
        }
    }

    public String getData(String path) {
        byte[] data = new byte[0];
        try {
            data = zk.getData(path, true, null);
        } catch (KeeperException e) {
            logger.warn("Data get KeeperException error[{}]", path, e);
        } catch (InterruptedException e) {
            logger.warn("Data get InterruptedException error[{}]", path, e);
        }
        if (data != null)
            return new String(data);
        else return null;
    }

    public void setData(String path, byte[] data) {
        Stat stat;
        try {
            stat = zk.setData(path, data, -1);
            logger.debug("Zookeeper setData path[{}],stat[{}]", path, stat.toString());
        } catch (KeeperException | InterruptedException e) {
            logger.error("Zookeeper setData path[{}] error!!", path, e);
        }

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
            return zk.getChildren(path, this);
        } catch (KeeperException.ConnectionLossException | KeeperException.SessionExpiredException e) {
            logger.error("Zookeeper[{}] get child node error[{}]", path, e);
            //            if (logger.isDebugEnabled())
            //                e.printStackTrace();
            // 十次异常连接 尝试重新注册连接
            connectErrorCount.add(1);
            if (connectErrorCount.longValue() >= 10) {
                logger.warn("Zookeeper connect error count[{}], try reconnect...", connectErrorCount.sumThenReset());
                connectErrorCount.reset();
                disconnectedCall();
            }
        } catch (InterruptedException | KeeperException e) {
            logger.warn("Zookeeper[{}] get child node is null[{}]", path, e);
            //            if (logger.isDebugEnabled())
            //                e.printStackTrace();
            //            e.printStackTrace();
        }
        return null;
    }

    @Override
    public synchronized void deleteNode(String path) {
        try {
            if (exists(path)) zk.delete(path, -1);
        } catch (InterruptedException | KeeperException e) {
            logger.error("Delete node[{}] exception", path, e);
        }
    }

    private RegCall regCall;

    public void initCaller(IaENV regCall) {
        this.regCall = regCall;
    }

    boolean serverIsMine(String onlineStatus) {
        Stat exists;
        try {
            exists = zk.exists(onlineStatus, false);
        } catch (KeeperException | InterruptedException e) {
            logger.error("Charge serverIsMine zookeeper exception error!!", e);
            return false;
        }
        if (exists != null) {
            //是否应该删除节点再添加注册信息
            long ephemeralOwner = exists.getEphemeralOwner();
            long sessionId = zk.getSessionId();
            if (ephemeralOwner != sessionId)
                logger.warn("Server status path data is different zk: [{}] -∞- local: [{}]", ephemeralOwner, sessionId);
            return ephemeralOwner != sessionId;
        }
        return false;

    }

    public void process(WatchedEvent watchedEvent) {
        logger.debug("[{}]Zookeeper trigger event type: [{}] content: [{}] ", Thread.currentThread().getId(), watchedEvent.getType(), watchedEvent);
        Watcher.Event.KeeperState state = watchedEvent.getState();
        Watcher.Event.EventType eventType = watchedEvent.getType();
        String path = watchedEvent.getPath();
        String groupCodeServerCode = preconditionGroupServerCode(groupCode, preconditionServerCode(serverName, serverVersion, iaConf.getSequence()));
        String onlineStatus = preconditionGroupServerPath(TopicPrefix.SERVER_STATUS, groupCodeServerCode);
        if (state == Watcher.Event.KeeperState.SyncConnected && latch.getCount() != 0) {
            logger.info("Zookeeper connect successful...");
            latch.countDown();
        } else if (Event.EventType.NodeDeleted == eventType) { //监听服务结点被删除事件重建服务结构
            //如果当前服务状态为在线且注册
            if (ServerStatus.ONLINE.equals(iaENV.getServerStatus()) && StringUtils.equals(path, onlineStatus)) {
                logger.warn("serverStatus:" + path + "have been deleted, try recover...");
                try {
                    regCall.call(RegCall.RegState.RebuildStatus);
                    logger.warn("Node:[{}] recovered...", path);
                } catch (InterruptedException | RegisterException e) { //重建服务发生异常则升级为重连重建
                    logger.error("Zookeeper rebuild [{}] error...", path, e);
                    disconnectedCall();
                    return;
                    //                    if (logger.isDebugEnabled()) e.printStackTrace();
                }
                if (!ServerStatus.ONLINE.equals(iaENV.getServerStatus())) {
                    disconnectedCall();
                }

            }
        } else if (Event.EventType.NodeDataChanged == eventType || Event.EventType.NodeCreated == eventType) {
            //如果当前服务状态为在线且注册
            if (ServerStatus.ONLINE.equals(iaENV.getServerStatus()) && StringUtils.equals(path, onlineStatus)) {
                //                String nodeData = getData(onlineStatus);
                //                ServerMsg nodeMsg = JSONObject.parseObject(nodeData, ServerMsg.class);

                if (serverIsMine(onlineStatus)) {
                    disconnectedCall();
                }
            }


        } else if (state == Watcher.Event.KeeperState.Expired
                || state == Watcher.Event.KeeperState.Disconnected) {
            logger.warn("Zookeeper Expired|Disconnected event [{}] ...", state);
            disconnectedCall();
        }
    }

    private void disconnectedCall() {
        synchronized (ZkHandlerImpl.class) {
            try {
                if (latch.getCount() == 0)
                    latch = new CountDownLatch(1);
                regCall.call(RegCall.RegState.Disconnected);
            } catch (Exception e) {
                logger.error("DisconnectedCall zk disconnect error..", e);
                //                if (logger.isDebugEnabled()) e.printStackTrace();
            }
        }

    }
}
