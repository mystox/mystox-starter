package com.kongtrolink.framework.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.kongtrolink.framework.entity.WebMessage;
import com.kongtrolink.framework.execute.module.model.DevType;
import com.kongtrolink.framework.execute.module.model.RedisFsuBind;
import com.kongtrolink.framework.execute.module.model.RedisOnlineInfo;
import com.kongtrolink.framework.service.DeviceMatchService;
import com.kongtrolink.framework.utils.CommonUtils;
import com.kongtrolink.framework.utils.JsonResult;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 接收app发送的请求
 */
@RestController
@RequestMapping("/fsu")
public class FSUController {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    CommonUtils commonUtils;

    @Autowired
    DeviceMatchService deviceMatchService;

    /**
     * 获取设备ID列表
     * @param requestBody
     * @param sn
     * @return
     */
    @RequestMapping("/getDeviceList")
    public JsonResult getDeviceList(@RequestBody(required = false) Map<String, Object> requestBody, String sn) {

        JsonResult jsonResult = new JsonResult("", false);

        boolean result = false;
        String message = "";
        JSONArray data = new JSONArray();

        try {
            RedisOnlineInfo redisOnlineInfo = commonUtils.getRedisOnlineInfo(sn);
            if (redisOnlineInfo == null) {
                message = WebMessage.NOT_FOUND_SN;
                throw new Exception(sn);
            }

            String fsuId = redisOnlineInfo.getFsuId();
            RedisFsuBind redisFsuBind = commonUtils.getRedisFsuBind(fsuId);
            if (redisFsuBind == null) {
                message = WebMessage.NOT_FOUND_FSU_ID;
                throw new Exception(sn + "-" + fsuId);
            }

            for (String deviceId : redisFsuBind.getDeviceIdList()) {
                String cntbType = deviceMatchService.getCntbType(deviceId);
                List<DevType> devTypeList = commonUtils.getDevTypeByCntbType(cntbType);
                if (devTypeList.size() > 0) {
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("name", devTypeList.get(0).getName());
                    jsonObject.put("id", deviceId);
                    data.add(jsonObject);
                } else {
                    message = WebMessage.NOT_FOUND_DEVICE_TYPE;
                    throw new Exception(sn + "-" + fsuId + "-" + deviceId);
                }
            }

            result = true;
        } catch (Exception ex) {
            if (message.equals("")) {
                message = "获取设备列表失败";
            }
            logger.error("getDeviceList：" + message + " " + ex.getMessage());
            data = null;
        } finally {

            jsonResult.setSuccess(result);
            jsonResult.setInfo(message);
            jsonResult.setData(data);

            return jsonResult;
        }
    }

    /**
     * 获取模拟设备ID列表
     * @param requestBody
     * @param sn
     * @return
     */
    @RequestMapping("/getFakeDeviceList")
    public JsonResult getFakeDeviceList(@RequestBody(required = false) Map<String, Object> requestBody, String sn) {
        JsonResult jsonResult = new JsonResult("", false);

        boolean result = false;
        String message = "";
        JSONArray data = new JSONArray();

        try {
            String fsuId = requestBody.get("fsuId").toString();
            ArrayList list = (ArrayList) requestBody.get("devs");
            list.add(0);//添加监控设备
            list.add(-1);//添加FSU设备

            for (int i = 0; i < list.size(); ++i) {
                int type = (int) list.get(i);

                DevType devType = commonUtils.getDevTypeByType(type);
                if (devType != null) {

                    String deviceId = StringUtils.rightPad(fsuId.substring(0, 6) + "4" + devType.getCntbType(), 14, '0');
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("name", devType.getName());
                    jsonObject.put("id", deviceId);
                    data.add(jsonObject);
                } else {
                    message = WebMessage.NOT_FOUND_DEVICE_TYPE;
                    throw new Exception(String.valueOf(type));
                }
            }

            result = true;
        } catch (Exception ex) {
            if (message.equals("")) {
                message = "获取模拟设备列表失败";
            }
            logger.error("getFakeDeviceList：" + message + " " + ex.getMessage());
            data = null;
        } finally {

            jsonResult.setSuccess(result);
            jsonResult.setInfo(message);
            jsonResult.setData(data);

            return jsonResult;
        }
    }
}
