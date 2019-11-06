package com.kongtrolink.framework.task;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.kongtrolink.framework.api.Publish;
import com.kongtrolink.framework.dao.DBService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Lazy
@Component
@EnableScheduling
public class AssetManagementTask {

    private Logger logger = LoggerFactory.getLogger(AssetManagementTask.class);

    @Value("${server.name}_${server.version}")
    private String serverCode;

    @Resource(name = "Neo4jDBService")
    private DBService dbService;

    @Autowired
    @Qualifier(value = "MqttPublish")
    private Publish publish;

    @Scheduled(fixedRate=60000)
    private void deviceGet() {

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
