package tech.mystox.framework.balancer;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import tech.mystox.framework.balancer.client.BaseLoadBalancerClient;
import tech.mystox.framework.balancer.client.LoadBalancerClient;
import tech.mystox.framework.config.IaConf;
import tech.mystox.framework.core.IaENV;
import tech.mystox.framework.core.MsgCall;
import tech.mystox.framework.core.OperaCall;
import tech.mystox.framework.entity.*;
import tech.mystox.framework.scheduler.LoadBalanceScheduler;
import tech.mystox.framework.scheduler.RegScheduler;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

import static tech.mystox.framework.common.util.MqttUtils.*;

/**
 * Created by mystoxlol on 2020/6/8, 14:36.
 * company: ink
 * description:
 * update record:
 */
@Lazy
@Component("baseLoadBalancer")
@DependsOn("zkRegScheduler")
public class BaseLoadBalancer implements LoadBalanceScheduler {

    Logger logger = LoggerFactory.getLogger(BaseLoadBalancer.class);


    private IaENV iaENV;
    private LoadBalancerClient loadBalancerClient;


    private MsgCall msgCall;


    public void initCaller(OperaCall caller) {
    }

    @Override
    public void build(IaENV iaENV) {
        this.loadBalancerClient = new BaseLoadBalancerClient(iaENV);
        loadBalancerClient.execute();
        this.iaENV = iaENV;
    }

    @Override
    public void unregister() {

    }

    @Override
    public void addServers(List<String> serList) {

    }

    @Override
    public ServerMsg chooseServer(Object ser) {
        IaConf iaconf = iaENV.getConf();
        RegScheduler regScheduler = iaENV.getRegScheduler();
        String serverName = iaconf.getServerName();
        String groupCode = iaconf.getGroupCode();
        String operaCode = String.valueOf(ser);
        String serverVersion = iaconf.getServerVersion();
        //优先配置中获取
        // 获取operaCode 路由表 /mqtt/operaRoute/groupCode/serverCode/operaCode
        String groupCodeServerCode = preconditionGroupServerCode(groupCode, preconditionServerCode(serverName, serverVersion));
        String routePath = preconditionRoutePath(groupCodeServerCode, operaCode);
        //            if (CollectionUtils.isEmpty(topicArr)) {
        if (!regScheduler.exists(routePath))
            regScheduler.create(routePath, null, IaConf.EPHEMERAL);
        //        String data = regScheduler.getData(routePath);
        //        List<String> topicArr = JSONArray.parseArray(data, String.class);
        List<String> topicArr = loadBalancerClient.getOperaRouteMap().get(operaCode);
        if (CollectionUtils.isEmpty(topicArr)) {
            //根据订阅表获取整合的订阅信息 <operaCode,[subTopic1,subTopic2]>
            List<String> subTopicArr = regScheduler.buildOperaMap(operaCode);
            //            List<String> subTopicArr = loadBalancerClient.getOperaRouteMap().get(operaCode);
            logger.debug("build opera map is {}", subTopicArr);
            String data = regScheduler.getData(routePath);
            List<String> exists = JSONArray.parseArray(data, String.class);
            if (CollectionUtils.isEmpty(exists)) {
                setRouteMap(routePath, subTopicArr);
            } else {
                exists.sort(Comparator.comparing(String::hashCode));
                subTopicArr.sort(Comparator.comparing(String::hashCode));
                if (!exists.toString().equals(subTopicArr.toString())) {
                    setRouteMap(routePath, subTopicArr);
                }
            }

            topicArr = subTopicArr;
        }
        //如果路由配置只有一个元素，则默认直接选择单一元素进行发送
        if (CollectionUtils.isEmpty(topicArr)) {
            logger.error("[{}] route topic list size is null error...", operaCode);
            //       mqttLogUtil.OPERA_ERROR(StateCode.OPERA_ROUTE_EXCEPTION, operaCode);
            return null;
        }
        int size = topicArr.size();
        String groupServerCode = "";
        // if (size == 1) {
        //     groupServerCode = topicArr.get(0);
        //     // result = operaTarget(operaCode, msg, qos, timeout, timeUnit, setFlag, async, groupServerCode);
        //     String groupServerPath = preconditionGroupServerPath(TopicPrefix.SERVER_STATUS, groupServerCode);
        //     return regScheduler.exists(groupServerPath) ? JSONObject.parseObject(regScheduler.getData(groupServerPath), ServerMsg.class) : null;
        // }
        if (size > 0) {
            Random r = new Random();
            int i = r.nextInt(size);
            groupServerCode = topicArr.get(i);
            String groupServerPath = preconditionGroupServerPath(TopicPrefix.SERVER_STATUS, groupServerCode);
            logger.debug("[{}] choose server is [{}]", operaCode, groupServerCode);
            return regScheduler.exists(groupServerPath) ? JSONObject.parseObject(regScheduler.getData(groupServerPath), ServerMsg.class) : null;
            /*MsgResult result = operaTarget(operaCode, msg, qos, timeout, timeUnit, setFlag, async, groupServerCode);
            if (result.getStateCode() != StateCode.SUCCESS) {
                //移除路由
                topicArr.remove(i);
                logger.warn("[{}] mqtt sender state code is failed, retry another server opera...topicArr: {}", operaCode, JSONArray.toJSONString(topicArr));
                regScheduler.setData(routePath, JSONArray.toJSONBytes(topicArr));
                //重新请求
                return operaBalance(operaCode, msg, qos, timeout, timeUnit, setFlag, async, topicArr, routePath); //
            }
            return result;*/
        }


        return null;
    }

    void setRouteMap(String routePath, List<String> subTopicArr) {
        logger.info("Route map set [{}] data {}", routePath, subTopicArr);
        RegScheduler regScheduler = iaENV.getRegScheduler();
        regScheduler.setData(routePath, JSONArray.toJSONBytes(subTopicArr));
    }

    @Override
    public void markServerDown(String ser) {

    }

    public LoadBalancerClient getLoadBalancerClient() {
        return loadBalancerClient;
    }

    public void setLoadBalancerClient(LoadBalancerClient loadBalancerClient) {
        this.loadBalancerClient = loadBalancerClient;
    }

    @Override
    public List<String> getReachableServers() {
        return null;
    }

    @Override
    public List<String> getAllServers() {
        //获取可以消息框架可以访达的服务列表
        List<String> groupServerCodes = new ArrayList<>();
        RegScheduler regScheduler = iaENV.getRegScheduler();
        String thisGroupCode = iaENV.getConf().getGroupCode();
        String statusGroupPath = TopicPrefix.SERVER_STATUS + "/" + thisGroupCode;//状态组路径
        String rootStatusGroupPtah = TopicPrefix.SERVER_STATUS + "/" + GroupCode.ROOT;//root状态组路径
        List<String> children = regScheduler.getChildren(statusGroupPath);
        List<String> rootChildren = regScheduler.getChildren(rootStatusGroupPtah);
        if (children != null)
            children.forEach(serverCode -> {
                String groupServerCode = preconditionGroupServerPath(thisGroupCode, serverCode); //构造
                groupServerCodes.add(groupServerCode);
            });
        if (rootChildren != null)
            rootChildren.forEach(serverCode -> {
                String groupServerCode = preconditionGroupServerCode(GroupCode.ROOT, serverCode); //构造
                groupServerCodes.add(groupServerCode);
            });
        return groupServerCodes;
    }

    @Override
    public <T> T operaCall(OperaCall<T> operaCall, String targetServerCode, Object ser) {
        // OperaCall msgCall = (MsgCall) operaCall;
        String operaCode = String.valueOf(ser);
        MsgResult result = null;
        if (StringUtils.isNotBlank(targetServerCode))
            result = (MsgResult) operaCall.operaTarget(operaCode, targetServerCode);
        assert result != null;
        if (StringUtils.isBlank(targetServerCode) || result.getStateCode() != StateCode.SUCCESS) {
            if (StringUtils.isBlank(targetServerCode))
                logger.warn("[{}]targetServerCode is null", operaCode);
            else if (result.getStateCode() != StateCode.SUCCESS)
                logger.warn("[{}]targetServerCode request failed", targetServerCode);
            IaConf iaconf = iaENV.getConf();
            RegScheduler regScheduler = iaENV.getRegScheduler();
            String serverName = iaconf.getServerName();
            String groupCode = iaconf.getGroupCode();
            String serverVersion = iaconf.getServerVersion();
            String groupCodeServerCode = preconditionGroupServerCode(groupCode, preconditionServerCode(
                    serverName, serverVersion));
            String routePath = preconditionRoutePath(groupCodeServerCode, operaCode);
            //            if (CollectionUtils.isEmpty(topicArr)) {
            //            if (!regScheduler.exists(routePath))
            //                regScheduler.create(routePath, null, IaConf.EPHEMERAL);
            //            String data = regScheduler.getData(routePath);
            List<String> localTopicArr = loadBalancerClient.getOperaRouteMap().get(operaCode);
            if (localTopicArr == null) localTopicArr = new ArrayList<>();
            /*boolean contains = topicArr.contains(targetServerCode);
            if (contains) {
                topicArr.remove(targetServerCode);
            }
            if (CollectionUtils.isEmpty(topicArr)) {//如果移除后可选路由为空则重建路由，并尝试发送
                logger.warn("opera map rebuild");

                logger.debug("opera map rebuild result is {}", JSON.toJSON(topicArr));
            }*/
            //如果路由配置只有一个元素，则默认直接选择单一元素进行发送
            int size = localTopicArr.size();
            List<String> topicArr = new ArrayList<>(localTopicArr);
            if (size > 0) { //默认数组多于1的情况下，识别为负载均衡
                Random r = new Random();
                int count = 0;
                do {
                    int bound = size - count;
                    int i = 0;
                    if (bound != 0)
                        i = r.nextInt(bound);
                    String retryServerCode = topicArr.get(i);
                    if (!StringUtils.equals(retryServerCode, targetServerCode)) {
                        result = (MsgResult) operaCall.operaTarget(operaCode, retryServerCode);
                        if (result.getStateCode() == StateCode.SUCCESS) {
                            logger.debug("opera[{}] target success server, serverCode is [{}]", operaCode, retryServerCode);
                            break;
                        } else {
                            logger.warn("opera[{}] target failed [{}]", operaCode, retryServerCode);
                        }
                    }
                    if (bound != 0)
                        topicArr.set(i, topicArr.get(size - count - 1));//尾数交换
                    count += 1;
                } while (count < size);
                if (count > 0 && count < size) { //删除错误服务，重置路由表
                    logger.warn("delete error server reset opera route...");
                    // System.out.println(count);
                    topicArr = topicArr.subList(0, size - count);
                } else if (count == size) { //遍历所有路由皆请求错误，重建路由
                    logger.warn("operaCode[{}] all route was failed...rebuild and try once again", operaCode);
                    topicArr = regScheduler.buildOperaMap(operaCode);
                    int size2 = topicArr.size();
                    if (!CollectionUtils.isEmpty(topicArr)) { //重试一次
                        int i = r.nextInt(size2);
                        String retryServerCode = topicArr.get(i);
                        result = (MsgResult) operaCall.operaTarget(operaCode, retryServerCode);
                        if (result.getStateCode() == StateCode.SUCCESS) {
                            logger.debug("opera[{}] target success server, serverCode is [{}]", operaCode, retryServerCode);
                        } else {
                            logger.warn("opera[{}] target failed [{}]", operaCode, retryServerCode);
                        }
                    }
                } /*{
                    topicArr = new ArrayList<>();
                }*/

            } else {
                logger.warn("[{}]request route topic arr is null", operaCode);
                result = new MsgResult(StateCode.OPERA_ROUTE_EXCEPTION, "request route topic arr is null");
            }
            if (size != topicArr.size()) {
                logger.warn("[{}] mqtt sender route code have changed...topicArr: {}", operaCode, JSONArray.toJSONString(topicArr));
                loadBalancerClient.getOperaRouteMap().put(operaCode, topicArr);
            }
            //            regScheduler.setData(routePath, JSONArray.toJSONBytes(topicArr));
        }

        // targetServerCode = topicArr.get(i);
        // MsgResult result = operaTarget(operaCode, msg, qos, timeout, timeUnit, setFlag, async, groupServerCode);
        // if (result.getStateCode() != StateCode.SUCCESS) {
        //     //移除路由
        //     topicArr.remove(i);
        //     logger.warn("[{}] mqtt sender state code is failed, retry another server opera...topicArr: {}", operaCode, JSONArray.toJSONString(topicArr));
        //     regScheduler.setData(routePath, JSONArray.toJSONBytes(topicArr));
        //     //重新请求
        //     return operaBalance(operaCode, msg, qos, timeout, timeUnit, setFlag, async, topicArr, routePath); //
        // }
        return (T) result;
    }

    @Override
    public List<String> getOperaRouteArr(String operaCode) {
        return getLoadBalancerClient().getOperaRouteMap().get(operaCode);
    }

    public static void main(String[] args) {
        args = new String[]{"5"};
        int size = 5;
        int count = 0;
        do {
            if (count == Integer.parseInt(args[0])) {
                break;
            }
            count += 1;
        } while (count < size);
        if (count > 0 && count < size) {
            System.out.println(count);
        } else if (count == size) {

        }
        System.out.println(count);

    }

}