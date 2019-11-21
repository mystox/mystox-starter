package com.kongtrolink.framework.task;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.kongtrolink.framework.api.Publish;
import com.kongtrolink.framework.dao.DBService;
import com.kongtrolink.framework.entity.AssetManagementConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.DependsOn;
import org.springframework.core.annotation.Order;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Component
@DependsOn(value = "registerRunner")
@Order
public class AssetManagementTask implements ApplicationRunner {

    private Logger logger = LoggerFactory.getLogger(AssetManagementTask.class);

    @Value("${server.name}_${server.version}")
    private String serverCode;

    @Resource(name = "Neo4jDBService")
    private DBService dbService;

    @Autowired
    @Qualifier(value = "MqttPublish")
    private Publish publish;

    @Autowired
    AssetManagementConfig assetManagementConfig;

    private int deviceGetCount = 0;

    @Resource(name = "assetManagementExecutor")
    ThreadPoolTaskExecutor taskExecutor;

    @Override
    public void run(ApplicationArguments args) {

        ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
        scheduledExecutorService.scheduleWithFixedDelay(() -> beat(), 0, 1, TimeUnit.SECONDS);
        System.out.println("AssetManagementTask.run" + this.toString());

    }

    private void beat() {

        if((++deviceGetCount) >= assetManagementConfig.getDeviceGetInterval()) {
            deviceGetCount = 0;
            taskExecutor.execute(()->deviceGet());
        }
    }

    private void deviceGet() {

        logger.info("deviceGet");

        JSONObject request = new JSONObject();
        request.put("curPage", 1);
        request.put("pageNum", 0);

        JSONObject response = dbService.searchCI(request);
        if (response == null || response.getInteger("count") <= 0) {
            return;
        }

        int pageNum = 100;
        request.put("pageNum", pageNum);
        int count = response.getInteger("count");
        for (int curPage = 0; curPage * pageNum < count; ++curPage) {
            request.put("curPage", curPage + 1);
            response = dbService.searchCI(request);

            JSONArray ciList = response.getJSONArray("infos");
            for (int i = 0; i < ciList.size(); ++i) {
                String sn = ciList.getJSONObject(i).getString("sn");
                String gatewayServerCode = ciList.getJSONObject(i).getString("gatewayServerCode");

                if (gatewayServerCode != null && !gatewayServerCode.equals("")) {
                    publish.deviceGet(sn, gatewayServerCode);
                }
            }
        }
    }
}
