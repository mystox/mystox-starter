package com.kongtrolink.framework.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.kongtrolink.framework.core.ControllerInstance;
import com.kongtrolink.framework.model.signal.SignalCode;
import com.kongtrolink.framework.service.DataMntService;
import com.kongtrolink.framework.util.JsonResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * \* @Author: mystox
 * \* Date: 2018/11/30 10:45
 * \* Description:
 * \
 */
@RestController
@RequestMapping("/dataMnt")
public class DataController
{


    @Autowired
    DataMntService dataMntService;


    /**
     * 信号点实时数据
     * @param requestBody
     * @param sn
     * @return
     */
    @RequestMapping("/getSignalList")
    public JsonResult getSignalList(@RequestBody(required = false) Map<String, Object> requestBody, String sn)
    {


        JSONObject result = dataMntService.getSignalList(requestBody, sn);
        if (result != null)
        {
            return new JsonResult(result);
        }
        return new JsonResult("请求错误或者超时", false);
    }

     void transSignalCode(JSONObject data)
    {
        Map<String, SignalCode> signalCodeMap = ControllerInstance.getInstance().getSignalMap();
        if (data != null)
        {

            JSONArray signals = (JSONArray) data.get("signals");
            if (signals == null) signals = (JSONArray) data.get("points");
            if (signals != null)
            {
                for (Object object : signals)
                {
                    JSONObject signal = (JSONObject) object;
                    String signalId = (String) signal.get("id");
                    SignalCode signalCode = signalCodeMap.get(signalId.substring(1));
                    if (signalCode == null)
                    {
                        System.out.println(signalId.substring(1));
                        signal.put("name", "未知设备");
                        signal.put("unit", "");
                    }
                    else
                    {
                        signal.put("name", signalCode.getName());
                        signal.put("unit", signalCode.getUnit());
                    }
                }
            }
        }
    }


    @RequestMapping("/getAlarmList")
    public JsonResult getAlarmList(@RequestBody(required = false) Map<String, Object> requestBody, String fsuId)
    {
        JSONObject result = dataMntService.getAlarmList(requestBody, fsuId);
        return result == null ? new JsonResult("请求错误或者超时", false) :
                "0".equals(result.get("result")) ? new JsonResult("执行任务失败", false) :new JsonResult(result.get("data"));
    }


    @RequestMapping("/getThreshold")
    public JsonResult getThreshold(@RequestBody Map<String, Object> requestBody, String sn)
    {
        JSONObject result = dataMntService.getThreshold(requestBody, sn);
        if (result != null)
        {
            JSONObject data = (JSONObject) result.get("data");
            if ( "0".equals(result.get("result"))) return new JsonResult("执行任务失败", false);
            if (data!=null)
            {
                transSignalCode(data);
                return new JsonResult(data);
            }
        }
        return new JsonResult("请求错误或者超时", false);
    }

    @RequestMapping("/setThreshold")
    public JsonResult setThreshold(@RequestBody(required = false) Map<String, Object> requestBody, String sn)
    {
        JSONObject result = dataMntService.setThreshold(requestBody, sn);
        return result == null ? new JsonResult("请求错误或者超时", false) :
                "0".equals(result.get("result")) ? new JsonResult("执行任务失败", false) :new JsonResult(result.get("data"));
    }
 @RequestMapping("/setSignal")
    public JsonResult setSignal(@RequestBody(required = false) Map<String, Object> requestBody, String sn)
    {
        JSONObject result = dataMntService.setSignal(requestBody, sn);
        return result == null ? new JsonResult("请求错误或者超时", false) :
                0==(Integer)result.get("result") ? new JsonResult("执行任务失败", false) :new JsonResult(result);
    }

}