package com.kongtrolink.framework.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.kongtrolink.framework.entity.WebMessage;
import com.kongtrolink.framework.execute.module.dao.DeviceDao;
import com.kongtrolink.framework.execute.module.model.*;
import com.kongtrolink.framework.jsonType.JsonDevice;
import com.kongtrolink.framework.service.DeviceMatchService;
import com.kongtrolink.framework.service.RpcService;
import com.kongtrolink.framework.service.TowerService;
import com.kongtrolink.framework.utils.CommonUtils;
import com.kongtrolink.framework.utils.JsonResult;
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
@RequestMapping("/dataMnt")
public class DataController {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    CommonUtils commonUtils;
    @Autowired
    RpcService rpcService;

    @Autowired
    DeviceMatchService deviceMatchService;

    @Autowired
    TowerService towerService;
    @Autowired
    DeviceDao deviceDao;

    @RequestMapping("/getSignalList")
    public JsonResult getSignalList(@RequestBody(required = false) Map<String, Object> requestBody, String sn) {

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

            String deviceId = requestBody.get("dev").toString();
            if (!redisFsuBind.getDeviceIdList().contains(deviceId)) {
                message = WebMessage.NOT_FOUND_DEVICE_ID;
                throw new Exception(sn + "-" + fsuId + "-" + deviceId);
            }

            List<Signal> signalList = towerService.getSignalListByDeviceId(deviceId);
            if (signalList.size() == 0) {
                message = WebMessage.NOT_FOUND_SIGNAL_LIST;
                throw new Exception(sn + "-" + fsuId + "-" + deviceId);
            }

            RedisData redisData = commonUtils.getRedisData(fsuId, deviceId);

            if (redisData == null || redisData.getValues().isEmpty()) {
                message = WebMessage.NOT_FOUND_DATA;
                throw new Exception(sn + "-" + fsuId + "-" + deviceId);
            }

            long datetime = System.currentTimeMillis();
            for (Signal signal : signalList) {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("name", signal.getName());
                jsonObject.put("coId", signal.getCntbId());
                jsonObject.put("type", signal.getIdType());
                jsonObject.put("unit", signal.getUnit());
                jsonObject.put("value", 0);
                jsonObject.put("dateTime", datetime);
                jsonObject.put("dataType", commonUtils.getSignalTypeName(signal.getIdType()));
                if (redisData.getValues().containsKey(signal.getCntbId())) {
                    jsonObject.put("value", Float.valueOf(redisData.getValues().get(signal.getCntbId()).toString()));
                }
                data.add(jsonObject);
            }

            jsonResult.setData(data);
            result = true;
        } catch (Exception ex) {
            if (message.equals("")) {
                message = "获取数据点失败";
            }
            logger.error("getSignalList：" + message + " " + ex.getMessage());
            data = null;
        } finally {

            jsonResult.setSuccess(result);
            jsonResult.setInfo(message);
            jsonResult.setData(data);

            return jsonResult;
        }
    }

    @RequestMapping("/setSignal")
    public JsonResult setSignal(@RequestBody(required = false) Map<String, Object> requestBody, String sn) {
        JsonResult jsonResult = new JsonResult("", false);

        boolean result = false;
        String message = "";
        JSONObject data = new JSONObject();

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

            String deviceId = requestBody.get("dev").toString();
            if (!redisFsuBind.getDeviceIdList().contains(deviceId)) {
                message = WebMessage.NOT_FOUND_DEVICE_ID;
                throw new Exception(sn + "-" + fsuId + "-" + deviceId);
            }

            String cntbType = deviceMatchService.getCntbType(deviceId);
            List<DevType> devTypeList = commonUtils.getDevTypeByCntbType(cntbType);
            if (devTypeList.size() == 0) {
                message = WebMessage.NOT_FOUND_DEVICE_TYPE;
                throw new Exception(sn + "-" + fsuId + "-" + deviceId);
            }

            JsonDevice jsonDevice = deviceDao.getInfoByFsuIdAndDeviceId(fsuId, deviceId);
            if (jsonDevice == null || (devTypeList.get(0).getType()!= -1 && jsonDevice.getPort() == null)) {
                message = WebMessage.NOT_FOUND_DEVICE;
                throw new Exception(sn + "-" + fsuId + "-" + deviceId);
            }

            String cntbId = requestBody.get("stePoint").toString();
            Signal signal = commonUtils.getSignalByTypeAndCntbId(devTypeList.get(0).getType(), cntbId);
            if (signal == null || signal.getSignalId() == null) {
                message = WebMessage.NOT_FOUND_SIGNAL;
                throw new Exception(sn + "-" + fsuId + "-" + deviceId + "-" + cntbId);
            }

            String value = requestBody.get("steData").toString();
            JSONObject responseObject = rpcService.setData(redisOnlineInfo.getSn(),
                    redisOnlineInfo.getInnerIp(), redisOnlineInfo.getInnerPort(),
                    jsonDevice.getType() + "-" + jsonDevice.getResNo(),
                    Integer.valueOf(signal.getSignalId()), Float.valueOf(value));

            if (responseObject != null && responseObject.containsKey("result")
                    && (responseObject.getInteger("result") == 1)) {
                result = true;
            } else {
                message = "设置数据点失败";
            }

        } catch (Exception ex) {
            if (message.equals("")) {
                message = "设置数据点失败";
            }
            logger.error("setSignal：" + message + " " + ex.getMessage());
            data = null;
        } finally {

            jsonResult.setSuccess(result);
            jsonResult.setInfo(message);
            jsonResult.setData(data);

            return jsonResult;
        }
    }

    @RequestMapping("/getHisSignalList")
    public JsonResult getHisSignalList(@RequestBody(required = false) Map<String, Object> requestBody, String sn) {

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

            String deviceId = requestBody.get("dev").toString();
            if (!redisFsuBind.getDeviceIdList().contains(deviceId)) {
                message = WebMessage.NOT_FOUND_DEVICE_ID;
                throw new Exception(sn + "-" + fsuId + "-" + deviceId);
            }

            String cntbType = deviceMatchService.getCntbType(deviceId);
            List<DevType> devTypeList = commonUtils.getDevTypeByCntbType(cntbType);
            if (devTypeList.size() == 0) {
                message = WebMessage.NOT_FOUND_DEVICE_TYPE;
                throw new Exception(sn + "-" + fsuId + "-" + deviceId);
            }

            JsonDevice jsonDevice = deviceDao.getInfoByFsuIdAndDeviceId(fsuId, deviceId);
            if (jsonDevice == null || (devTypeList.get(0).getType()!= -1 && jsonDevice.getPort() == null)) {
                message = WebMessage.NOT_FOUND_DEVICE;
                throw new Exception(sn + "-" + fsuId + "-" + deviceId);
            }

            String cntbId = requestBody.get("coId").toString();
            Signal signal = commonUtils.getSignalByTypeAndCntbId(devTypeList.get(0).getType(), cntbId);
            if (signal == null || signal.getSignalId() == null) {
                message = WebMessage.NOT_FOUND_SIGNAL;
                throw new Exception(sn + "-" + fsuId + "-" + deviceId + "-" + cntbId);
            }

            long startTime = Long.valueOf(requestBody.get("startTime").toString());
            long endTime = Long.valueOf(requestBody.get("endTime").toString());
            List<HisData> hisDataList = commonUtils.getHisDataList(fsuId, deviceId, cntbId, startTime, endTime);

            for (HisData hisData : hisDataList) {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("name", signal.getName());
                jsonObject.put("coId", signal.getCntbId());
                jsonObject.put("type", signal.getIdType());
                jsonObject.put("unit", signal.getUnit());
                jsonObject.put("value", hisData.getValue());
                jsonObject.put("dateTime", hisData.getTime());
                jsonObject.put("dataType", commonUtils.getSignalTypeName(signal.getIdType()));

                data.add(jsonObject);
            }

            result = true;
        } catch (Exception ex) {
            if (message.equals("")) {
                message = "获取历史数据点失败";
            }
            logger.error("getHisSignalList：" + message + " " + ex.getMessage());
            data = null;
        } finally {

            jsonResult.setSuccess(result);
            jsonResult.setInfo(message);
            jsonResult.setData(data);

            return jsonResult;
        }
    }

    @RequestMapping("/getThresholdList")
    public JsonResult getThresholdList(@RequestBody Map<String, Object> requestBody, String sn) {

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

            String deviceId = requestBody.get("dev").toString();
            if (!redisFsuBind.getDeviceIdList().contains(deviceId)) {
                message = WebMessage.NOT_FOUND_DEVICE_ID;
                throw new Exception(sn + "-" + fsuId + "-" + deviceId);
            }

            String cntbType = deviceMatchService.getCntbType(deviceId);
            List<DevType> devTypeList = commonUtils.getDevTypeByCntbType(cntbType);
            if (devTypeList.size() == 0) {
                message = WebMessage.NOT_FOUND_DEVICE_TYPE;
                throw new Exception(sn + "-" + fsuId + "-" + deviceId);
            }

            JsonDevice jsonDevice = deviceDao.getInfoByFsuIdAndDeviceId(fsuId, deviceId);
            if (jsonDevice == null || (devTypeList.get(0).getType()!= -1 && jsonDevice.getPort() == null)) {
                message = WebMessage.NOT_FOUND_DEVICE;
                throw new Exception(sn + "-" + fsuId + "-" + deviceId);
            }

            List<Alarm> alarmList = commonUtils.getAlarmListByType(devTypeList.get(0).getType());

            List<String> alarmIdList = new ArrayList<>();
            for (Alarm alarm : alarmList) {
                alarmIdList.add(alarm.getAlarmId());
            }
            JSONArray responseArray = rpcService.getThreshold(redisFsuBind.getSn(), redisOnlineInfo.getInnerIp(),
                    redisOnlineInfo.getInnerPort(), jsonDevice.getType(),
                    jsonDevice.getResNo(), jsonDevice.getPort(), alarmIdList);

            if (responseArray == null) {
                message = "获取门限值失败";
                throw new Exception(sn + "-" + fsuId + "-" + deviceId);
            }

            long datetime = System.currentTimeMillis();
            for (Alarm alarm : alarmList) {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("name", alarm.getDesc());
                jsonObject.put("coId", alarm.getCntbId());
                jsonObject.put("deviceId", deviceId);
                jsonObject.put("dateTime", datetime);
                jsonObject.put("threshold", 0);
                for (int i = 0; i < responseArray.size(); ++i) {
                    //遍历报文中的告警点信息
                    JSONObject value = responseArray.getJSONObject(i);
                    if (alarm.getAlarmId().equals(value.getString("alarmId"))) {
                        jsonObject.put("threshold", value.getFloatValue("threshold"));
                        break;
                    }
                }

                data.add(jsonObject);
            }

            result = true;
        } catch (Exception ex) {
            if (message.equals("")) {
                message = "获取门限值失败";
            }
            logger.error("getThresholdList：" + message + " " + ex.getMessage());
            data = null;
        } finally {

            jsonResult.setSuccess(result);
            jsonResult.setInfo(message);
            jsonResult.setData(data);

            return jsonResult;
        }
    }

    @RequestMapping("/setThreshold")
    public JsonResult setThreshold(@RequestBody(required = false) Map<String, Object> requestBody, String sn) {

        JsonResult jsonResult = new JsonResult("", false);

        boolean result = false;
        String message = "";
        JSONObject data = null;

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

            String deviceId = requestBody.get("dev").toString();
            if (!redisFsuBind.getDeviceIdList().contains(deviceId)) {
                message = WebMessage.NOT_FOUND_DEVICE_ID;
                throw new Exception(sn + "-" + fsuId + "-" + deviceId);
            }

            String cntbType = deviceMatchService.getCntbType(deviceId);
            List<DevType> devTypeList = commonUtils.getDevTypeByCntbType(cntbType);
            if (devTypeList.size() == 0) {
                message = WebMessage.NOT_FOUND_DEVICE_TYPE;
                throw new Exception(sn + "-" + fsuId + "-" + deviceId);
            }

            JsonDevice jsonDevice = deviceDao.getInfoByFsuIdAndDeviceId(fsuId, deviceId);
            if (jsonDevice == null || (devTypeList.get(0).getType()!= -1 && jsonDevice.getPort() == null)) {
                message = WebMessage.NOT_FOUND_DEVICE;
                throw new Exception(sn + "-" + fsuId + "-" + deviceId);
            }

            String cntbId = requestBody.get("coId").toString();
            Alarm alarm = commonUtils.getAlarmByTypeAndCntbId(devTypeList.get(0).getType(), cntbId);
            if (alarm == null || alarm.getAlarmId() == null) {
                message = WebMessage.NOT_FOUND_ALARM;
                throw new Exception(sn + "-" + fsuId + "-" + deviceId + "-" + cntbId);
            }

            String value = requestBody.get("threshold").toString();
            JSONObject responseObject = rpcService.setThreshold(redisOnlineInfo.getSn(),
                    redisOnlineInfo.getInnerIp(), redisOnlineInfo.getInnerPort(),
                    jsonDevice.getType() + "-" + jsonDevice.getResNo(),
                    alarm.getAlarmId(), Float.valueOf(value));

            if (responseObject != null && responseObject.containsKey("result")
                    && (responseObject.getInteger("result") == 1)) {
                result = true;
            } else {
                message = "设置数据点失败";
            }

        } catch (Exception ex) {
            if (message.equals("")) {
                message = "设置门限值失败";
            }
            logger.error("setThreshold：" + message + " " + ex.getMessage());
            data = null;
        } finally {

            jsonResult.setSuccess(result);
            jsonResult.setInfo(message);
            jsonResult.setData(data);

            return jsonResult;
        }
    }

    @RequestMapping("/getAlarmList")
    public JsonResult getAlarmList(@RequestBody(required = false) Map<String, Object> requestBody, String sn) {

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

            boolean isDesc = true;
            if (requestBody.containsKey("isDesc")) {
                isDesc = (boolean)requestBody.get("isDesc");
            }

            List<RedisAlarm> redisAlarmList = commonUtils.getRedisAlarmList(redisOnlineInfo.getFsuId());
            sortRedisAlarmListByStartTime(redisAlarmList, isDesc);
            for (RedisAlarm redisAlarm : redisAlarmList) {

                String deviceId = redisAlarm.getDeviceId();

                String cntbType = deviceMatchService.getCntbType(deviceId);
                List<DevType> devTypeList = commonUtils.getDevTypeByCntbType(cntbType);
                if (devTypeList.size() == 0) {
                    message = WebMessage.NOT_FOUND_DEVICE_TYPE;
                    throw new Exception(sn + "-" + fsuId + "-" + deviceId);
                }

                JSONObject jsonObject = new JSONObject();
                jsonObject.put("serialNo", redisAlarm.getSerialNo());
                jsonObject.put("alarmId", redisAlarm.getId());
                jsonObject.put("deviceId", deviceId);
                jsonObject.put("name", devTypeList.get(0).getName());
                jsonObject.put("alarmTime", redisAlarm.getStartTime());
                jsonObject.put("level", redisAlarm.getAlarmLevel());
                jsonObject.put("desc", redisAlarm.getAlarmDesc());
                jsonObject.put("value", redisAlarm.getValue());
                jsonObject.put("threshold", 0);

                JsonDevice jsonDevice = deviceDao.getInfoByFsuIdAndDeviceId(fsuId, deviceId);
                if (jsonDevice == null || jsonDevice.getPort() == null) {
                    message = WebMessage.NOT_FOUND_DEVICE;
                    throw new Exception(sn + "-" + fsuId + "-" + deviceId);
                }

                Alarm alarm = commonUtils.getAlarmByTypeAndCntbId(devTypeList.get(0).getType(), redisAlarm.getId());
                List<String> alarmIdList = new ArrayList<>();
                alarmIdList.add(alarm.getAlarmId());
                JSONArray responseArray = rpcService.getThreshold(sn, redisOnlineInfo.getInnerIp(),
                        redisOnlineInfo.getInnerPort(), jsonDevice.getType(),
                        jsonDevice.getResNo(), jsonDevice.getPort(), alarmIdList);

                if (responseArray.size() > 0) {
                    jsonObject.put("threshold", responseArray.getJSONObject(0).getFloatValue("threshold"));
                } else {
                    logger.error("getAlarmList：获取门限值失败" + sn + "-" + fsuId + "-" + deviceId + "-" + alarm.getAlarmId());
                }

                data.add(jsonObject);
            }

            result = true;
        } catch (Exception ex) {
            if (message.equals("")) {
                message = "获取活动告警失败";
            }
            logger.error("getAlarmList：" + message + " " + ex.getMessage());
            data = null;
        } finally {

            jsonResult.setSuccess(result);
            jsonResult.setInfo(message);
            jsonResult.setData(data);

            return jsonResult;
        }
    }

    private void sortRedisAlarmListByStartTime(List<RedisAlarm> redisAlarmList, boolean isDesc) {
        redisAlarmList.sort((arg0, arg1) -> {
            int result;

            if (arg0.getStartTime().equals(arg1.getStartTime())) {
                result = arg0.getSerialNo().compareTo(arg1.getSerialNo());
            } else {
                result = arg0.getStartTime().compareTo(arg1.getStartTime());
            }

            if (isDesc) {
                result *= -1;
            }

            return result;
        });
    }
}
