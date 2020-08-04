package tech.mystox.framework.balancer;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import tech.mystox.framework.config.IaConf;
import tech.mystox.framework.core.IaENV;
import tech.mystox.framework.core.MsgCall;
import tech.mystox.framework.core.OperaCall;
import tech.mystox.framework.entity.MsgResult;
import tech.mystox.framework.entity.ServerMsg;
import tech.mystox.framework.entity.StateCode;
import tech.mystox.framework.entity.TopicPrefix;
import tech.mystox.framework.scheduler.LoadBalanceScheduler;
import tech.mystox.framework.scheduler.RegScheduler;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static tech.mystox.framework.common.util.MqttUtils.*;

/**
 * Created by mystoxlol on 2020/6/8, 14:36.
 * company: kongtrolink
 * description:
 * update record:
 */
@Lazy
@Component("baseLoadBalancer")
public class BaseLoadBalancer implements ApplicationContextAware, LoadBalanceScheduler {

    Logger logger = LoggerFactory.getLogger(BaseLoadBalancer.class);


    private ApplicationContext applicationContext;
    private IaENV iaENV;


    private MsgCall msgCall;


    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;

    }

    @Override
    public void initCaller(OperaCall caller) {
    }

    @Override
    public void build(IaENV iaENV) {
        this.iaENV = iaENV;
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
        String data = regScheduler.getData(routePath);
        List<String> topicArr = JSONArray.parseArray(data, String.class);
        if (CollectionUtils.isEmpty(topicArr)) {
            //根据订阅表获取整合的订阅信息 <operaCode,[subTopic1,subTopic2]>
            List<String> subTopicArr = regScheduler.buildOperaMap(operaCode);
            logger.debug("build opera map is {}", subTopicArr);
            regScheduler.setData(routePath, JSONArray.toJSONBytes(subTopicArr));
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


    @Override
    public void markServerDown(String ser) {

    }

    @Override
    public List<String> getReachableServers() {
        return null;
    }

    @Override
    public List<String> getAllServers() {
        return null;
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
            if (StringUtils.isBlank(targetServerCode)) logger.warn("[{}]targetServerCode is null", operaCode);
            else if (result.getStateCode() != StateCode.SUCCESS)
                logger.warn("[{}]targetServerCode request failed", targetServerCode);
            IaConf iaconf = iaENV.getConf();
            RegScheduler regScheduler = iaENV.getRegScheduler();
            String serverName = iaconf.getServerName();
            String groupCode = iaconf.getGroupCode();
            String serverVersion = iaconf.getServerVersion();
            String groupCodeServerCode = preconditionGroupServerCode(groupCode, preconditionServerCode(serverName, serverVersion));
            String routePath = preconditionRoutePath(groupCodeServerCode, operaCode);
//            if (CollectionUtils.isEmpty(topicArr)) {
            if (!regScheduler.exists(routePath))
                regScheduler.create(routePath, null, IaConf.EPHEMERAL);
            String data = regScheduler.getData(routePath);
            List<String> topicArr = JSONArray.parseArray(data, String.class);
            /*boolean contains = topicArr.contains(targetServerCode);
            if (contains) {
                topicArr.remove(targetServerCode);
            }
            if (CollectionUtils.isEmpty(topicArr)) {//如果移除后可选路由为空则重建路由，并尝试发送
                logger.warn("opera map rebuild");

                logger.debug("opera map rebuild result is {}", JSON.toJSON(topicArr));
            }*/
            //如果路由配置只有一个元素，则默认直接选择单一元素进行发送
            int size = topicArr.size();
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
                            logger.debug("opera target success server, serverCode is [{}]", retryServerCode);
                            break;
                        } else {
                            logger.warn("opera target failed [{}]", retryServerCode);
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
                    logger.warn("all route was failed...rebuild and try once again");
                    topicArr = regScheduler.buildOperaMap(operaCode);
                    int size2 = topicArr.size();
                    if (!CollectionUtils.isEmpty(topicArr)) { //重试一次
                        int i = r.nextInt(size2);
                        String retryServerCode = topicArr.get(i);
                        result = (MsgResult) operaCall.operaTarget(operaCode, retryServerCode);
                        if (result.getStateCode() == StateCode.SUCCESS) {
                            logger.debug("opera target success server, serverCode is [{}]", retryServerCode);
                        } else {
                            logger.warn("opera target failed [{}]", retryServerCode);
                        }
                    }
                } /*{
                    topicArr = new ArrayList<>();
                }*/

            } else {
                logger.warn("[{}]request route topic arr is null", operaCode);
                result = new MsgResult(StateCode.OPERA_ROUTE_EXCEPTION, "request route topic arr is null");
            }
            if (size != topicArr.size())
                logger.warn("[{}] mqtt sender route code have changed...topicArr: {}", operaCode, JSONArray.toJSONString(topicArr));
            regScheduler.setData(routePath, JSONArray.toJSONBytes(topicArr));
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