package com.kongtrolink.framework;
import com.alibaba.fastjson.JSONObject;
import com.kongtrolink.framework.api.impl.MqttPublish;
import com.kongtrolink.framework.common.util.MqttUtils;
import com.kongtrolink.framework.controller.CITypeController;
import com.kongtrolink.framework.core.IaContext;
import com.kongtrolink.framework.dao.impl.Neo4jDBService;
import com.kongtrolink.framework.entity.MsgResult;
import com.kongtrolink.framework.entity.ServerName;
import com.kongtrolink.framework.service.MsgHandler;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.session.data.redis.RedisOperationsSessionRepository;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.Date;

@RunWith(SpringRunner.class)
@SpringBootTest
public class AssetManagementServerApplicationTest {

    @Autowired
    Neo4jDBService neo4jDBService;

    @Autowired
    MqttPublish mqttPublish;

//    @Autowired
//    MqttSender mqttSender;

    @Autowired
    IaContext iaContext;

    @Test
    public void testCIType() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("title", "测试1111");
        jsonObject.put("name", "test1111");
        jsonObject.put("code", "test1");
        jsonObject.put("level", 2);

        System.out.println("deleteCIType:" + neo4jDBService.deleteCIType("test1111"));
        jsonObject.put("relationship", "Application");

        System.out.println("addCIType:" + neo4jDBService.addCIType(jsonObject));

        jsonObject.put("title", "测试2222");
        System.out.println("modifyCIType:" + neo4jDBService.modifyCIType(jsonObject));

        jsonObject.put("title", "");
        jsonObject.put("name", "BusinessDevice");
        jsonObject.put("code", "");
//        JSONArray array = neo4jDBService.searchCIType(jsonObject);
        System.out.println(neo4jDBService.searchCIType(jsonObject).getJsonArray());
    }

    @Test
    public void testGetRegionCode() {

        JSONObject jsonObject = new JSONObject();
//        jsonObject.put("userId", "392e4847-abf5-48a7-b6a4-f2bdd41bf1c2");
//        jsonObject.put("enterpriseCode", "Skongtrolink");
//        jsonObject.put("serverCode", "");
        jsonObject.put("structure", 0);

        MsgResult msgResult = mqttPublish.getRegionCode(JSONObject.toJSONString(jsonObject));

        System.out.println(JSONObject.toJSONString(msgResult));
    }

    @Test
    public void testGetCIModel() {

        MsgHandler msgHandler= iaContext.getIaENV().getMsgScheduler().getIahander();
        String serverCode = MqttUtils.preconditionServerCode(ServerName.ASSET_MANAGEMENT_SERVER, "1.0.0");
        String operaCode = "getCIModel";

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("enterpriseCode", "Skongtrolink");
        jsonObject.put("serverCode", "AUTH_PLATFORM");

        MsgResult result = msgHandler.sendToMqttSync(serverCode, operaCode, JSONObject.toJSONString(jsonObject));

        System.out.println(JSONObject.toJSONString(result));
    }

    @MockBean
    RedisOperationsSessionRepository redisOperationsSessionRepository;

    @Test
    public void testDate() {
        long t1 = System.currentTimeMillis();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        Date now = new Date(t1);
        System.out.println(t1 + "," + simpleDateFormat.format(now));
    }


    private String enterpriseCode = "YYDS";
    private String serverCode = "TOWER_SERVER_1.0.0";

    @Test
    public void testCreateCI() {
        JSONObject jsonObjectFsu = new JSONObject();
        jsonObjectFsu.put("enterpriseCode", enterpriseCode);
        jsonObjectFsu.put("serverCode", serverCode);
        jsonObjectFsu.put("address", "000000");
        jsonObjectFsu.put("type", "yy38");
        jsonObjectFsu.put("sn", "000000");
        jsonObjectFsu.put("model", "fsu");
        jsonObjectFsu.put("status", true);
        jsonObjectFsu.put("user", "test");
        jsonObjectFsu.put("remarks", "测试");
        jsonObjectFsu.put("resourceNo", 1);
        jsonObjectFsu.put("versionMajor", 1);
        jsonObjectFsu.put("versionMinor", 1);
        jsonObjectFsu.put("versionRevision", 2);

        JSONObject jsonObjectPW = new JSONObject();
        jsonObjectPW.put("enterpriseCode", enterpriseCode);
        jsonObjectPW.put("serverCode", serverCode);
        jsonObjectPW.put("address", "000000");
        jsonObjectPW.put("type", "yy6");
        jsonObjectPW.put("sn", "000000");
        jsonObjectPW.put("model", "power");
        jsonObjectPW.put("status", true);
        jsonObjectPW.put("user", "test");
        jsonObjectPW.put("remarks", "测试");
        jsonObjectPW.put("resourceNo", 1);
        jsonObjectPW.put("versionMajor", 1);
        jsonObjectPW.put("versionMinor", 0);
        jsonObjectPW.put("versionRevision", 5);

        JSONObject jsonObjectENV = new JSONObject();
        jsonObjectENV.put("enterpriseCode", enterpriseCode);
        jsonObjectENV.put("serverCode", serverCode);
        jsonObjectENV.put("address", "000000");
        jsonObjectENV.put("type", "yy18");
        jsonObjectENV.put("sn", "000000");
        jsonObjectENV.put("model", "environment");
        jsonObjectENV.put("status", true);
        jsonObjectENV.put("user", "test");
        jsonObjectENV.put("remarks", "测试");
        jsonObjectENV.put("resourceNo", 1);
        jsonObjectENV.put("versionMajor", 1);
        jsonObjectENV.put("versionMinor", 2);
        jsonObjectENV.put("versionRevision", 9);

        neo4jDBService.searchCIConnectionType();

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        Date now = new Date();
        System.out.println(simpleDateFormat.format(now) + ":Start");

        for (int i = 1; i <= 0; ++i) {
            String sn = "00000" + i;
            sn = sn.substring(sn.length() - 5);
            jsonObjectFsu.put("sn", "438" + sn);
            jsonObjectPW.put("sn", "406" + sn);
            jsonObjectENV.put("sn", "418" + sn);

            String fsuId = neo4jDBService.addCI(jsonObjectFsu).getJsonObject().getString("id");
            String powerId = neo4jDBService.addCI(jsonObjectPW).getJsonObject().getString("id");
            String envId = neo4jDBService.addCI(jsonObjectENV).getJsonObject().getString("id");

            JSONObject relationship = new JSONObject();

            relationship.put("id1", fsuId);
            relationship.put("id2", powerId);
            relationship.put("type", "Physical");
            neo4jDBService.addCIRelationship(relationship);

            relationship.put("id2", envId);
            neo4jDBService.addCIRelationship(relationship);

            if (i % 1000 == 0) {
                now = new Date();
                System.out.println(simpleDateFormat.format(now) + ":" + i);
            }
        }
    }

    @Resource(name = "assetManagementExecutor")
    ThreadPoolTaskExecutor taskExecutor;

    final private int threadCount = 2;
    private Boolean[] threadBoolean = new Boolean[threadCount];

    @Test
    public void testSearchCI() {

        neo4jDBService.searchCIConnectionType();

        for (int i = 0; i < threadCount; ++i) {
            threadBoolean[i] = false;
            final int index = i;
            taskExecutor.execute(()->searchCI(index));
        }

        for (int i = 0; i < threadCount;) {
            if (!threadBoolean[i]) {
                try {
                    Thread.sleep(1000*3);
                } catch (Exception e) {

                }
                continue;
            } else {
                ++i;
            }
        }
    }

    private void searchCI(int index) {

        int times = 10;
        int pageNum = 20000;
        int pageTotal = 60000 / pageNum;

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("enterpriseCode", enterpriseCode);
        jsonObject.put("serverCode", serverCode);
        jsonObject.put("pageNum", pageNum);

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        Date now = new Date();
        System.out.println(simpleDateFormat.format(now) + ":Thread-" + index + " Start");

        long sum = 0;
        for (int count = 0; count < times; ++count) {
            for (int i = 1; i <= pageTotal; ++i) {
                jsonObject.put("curPage", i);

                Date startDate = new Date();
//                JSONObject result = neo4jDBService.searchCI(jsonObject);
                Date endDate = new Date();

                long diff = endDate.getTime() - startDate.getTime();

                System.out.println(simpleDateFormat.format(startDate) + ":Thread-" + index + ",count-" + count + ",i-" + i + ",Begin");
                System.out.println(simpleDateFormat.format(endDate) + ":Thread-" + index + ",count-" + count + ",i-" + i + ",End,interval-" + diff);

                sum += diff;
            }
        }

        double average = sum * 1.0 / times / pageTotal;
        System.out.println("Thread-" + index + ",average-" + average);
        threadBoolean[index] = true;
    }

    @Test
    public void testMsg() {

        JSONObject request = new JSONObject();
        request.put("sn", "38");
        request.put("enterpriseCode", "1");
        request.put("serverCode", "1");
        request.put("curPage", 1);
        request.put("pageNum", 20);

        String serverCode = MqttUtils.preconditionServerCode("ASSET_MANAGEMENT_SERVER", "1.0.0");
        String operaCode = "getCI";


        request(0, request, serverCode, operaCode);
    }

    private void request(int threadIndex, JSONObject request, String serverCode, String operaCode) {
        MsgHandler msgHandler= iaContext.getIaENV().getMsgScheduler().getIahander();
        for (int i = 0; i < 100; ++i) {
            MsgResult msgResult = msgHandler.sendToMqttSync(serverCode, operaCode, JSONObject.toJSONString(request));
            System.out.println((new Date()).toString() + ":threadIndex:" + threadIndex + ",count:" + i + " " + msgResult.getStateCode());
        }
    }

    @Autowired
    CITypeController ciTypeController;

    @Test
    public void testDeleteCIType() {

        JSONObject request = new JSONObject();
        request.put("name", "1");

        ciTypeController.delete(request);
    }
}
