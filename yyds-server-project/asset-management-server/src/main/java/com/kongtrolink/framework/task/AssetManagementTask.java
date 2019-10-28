package com.kongtrolink.framework.task;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.kongtrolink.framework.api.Publish;
import com.kongtrolink.framework.dao.DBService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class AssetManagementTask {

    @Resource(name = "Neo4jDBService")
    private DBService dbService;

    @Resource(name = "MqttPublish")
    private Publish publish;

    private void deviceGet() {

        JSONObject request = new JSONObject();
        request.put("curPage", 1);
        request.put("pageNum", 0);

        JSONObject response = dbService.searchCI(request);
        if (response == null || request.getInteger("count") <= 0) {
            return;
        }

        int pageNum = 100;
        request.put("pageNum", pageNum);
        int count = request.getInteger("count");
        for (int curPage = 0; curPage * pageNum < count; ++curPage) {
            request.put("curPage", curPage + 1);
            response = dbService.searchCI(request);

            JSONArray ciList = response.getJSONArray("infos");
            for (int i = 0; i < ciList.size(); ++i) {
                String sn = ciList.getJSONObject(i).getString("sn");
                String gatewayServerCode = ciList.getJSONObject(i).getString("gatewayServerCode");

                if (!gatewayServerCode.equals("")) {
                    publish.deviceGet(sn, gatewayServerCode);
                }
            }
        }
    }
}
