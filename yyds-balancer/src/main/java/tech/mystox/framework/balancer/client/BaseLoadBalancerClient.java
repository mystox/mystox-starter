package tech.mystox.framework.balancer.client;

import com.alibaba.fastjson.JSONArray;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import tech.mystox.framework.common.util.MqttUtils;
import tech.mystox.framework.config.CommonExecutorConfig;
import tech.mystox.framework.config.IaConf;
import tech.mystox.framework.config.OperaRouteConfig;
import tech.mystox.framework.core.IaENV;
import tech.mystox.framework.core.RegCall;
import tech.mystox.framework.entity.TopicPrefix;
import tech.mystox.framework.scheduler.MsgScheduler;
import tech.mystox.framework.scheduler.RegScheduler;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static ch.qos.logback.core.CoreConstants.CORE_POOL_SIZE;
import static ch.qos.logback.core.CoreConstants.MAX_POOL_SIZE;
import static tech.mystox.framework.common.util.MqttUtils.*;

/**
 * Created by mystoxlol on 2020/6/17, 10:29.
 * company: kongtrolink
 * description:
 * update record:
 */
public class BaseLoadBalancerClient extends CommonExecutorConfig implements LoadBalancerClient {

    private Logger logger = LoggerFactory.getLogger(BaseLoadBalancerClient.class);
    private IaENV iaENV;

    private Map<String, List<String>> operaRouteMap = new ConcurrentHashMap<>();


    private ScheduledExecutorService scheduledExecutorService;

    public BaseLoadBalancerClient(IaENV iaENV) {
        this.scheduledExecutorService = Executors.newScheduledThreadPool(1);
        this.iaENV = iaENV;
    }

    public void execute() {
        scheduledExecutorService.scheduleWithFixedDelay(this::runner, 1, 1, TimeUnit.SECONDS);
    }

    void runner() {
        try {
            System.out.println(1111111111);
            RegScheduler regScheduler = iaENV.getRegScheduler();
            RegCall.RegState state = regScheduler.getState();
            System.out.println(state);
            if (RegCall.RegState.SyncConnected != state) {
                logger.debug("register state is not connected ...");
                return;
            }
//        logger.debug("balancer client update ...");
            IaConf conf = iaENV.getConf();
            String groupServerCode = preconditionGroupServerCode(conf.getGroupCode(),
                    preconditionServerCode(conf.getServerName(), conf.getServerVersion()));
//        String nodeData = preconditionGroupServerPath(TopicPrefix.SERVER_STATUS, groupServerCode);
            List<String> children = regScheduler.getChildren(preconditionGroupServerPath(TopicPrefix.OPERA_ROUTE,
                    groupServerCode));
            Map<String, List<String>> operaMap = new ConcurrentHashMap<>();
            OperaRouteConfig operaRouteConfig = conf.getOperaRouteConfig();
            Map<String, List<String>> localOperaRouteMap = operaRouteConfig.getOperaRoute();
            children.forEach(operaCode -> {
                String routePath = preconditionRoutePath(groupServerCode, operaCode);
                //判断本地是否存在自定义配置，如有，使用本地配置文件的配置 本地配置不进行重新注册，只有在接受广播后会改变路由
                List<String> operaRouteArr = new ArrayList<>();
                if (localOperaRouteMap.containsKey(operaCode)) {
                    operaRouteArr = localOperaRouteMap.get(operaCode);
                } else {
                    operaRouteArr = regScheduler.buildOperaMap(operaCode);
                    regScheduler.setData(routePath, JSONArray.toJSONBytes(operaRouteArr));
                }
                operaMap.put(operaCode, operaRouteArr);
                logger.info("operaCode [{}] route update result: {}", operaCode, operaRouteArr);
            });
            this.operaRouteMap = operaMap;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Map<String, List<String>> getOperaRouteMap() {
        return operaRouteMap;
    }

    public void setOperaRouteMap(Map<String, List<String>> operaRouteMap) {
        this.operaRouteMap = operaRouteMap;
    }

//    public static void main(String[] args) {
//        System.out.println(RegCall.RegState.SyncConnected == RegCall.RegState.SyncConnected );
//    }
}
