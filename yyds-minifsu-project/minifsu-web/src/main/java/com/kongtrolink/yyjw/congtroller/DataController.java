package com.kongtrolink.yyjw.congtroller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.kongtrolink.yyjw.core.ControllerInstance;
import com.kongtrolink.yyjw.model.signal.SignalCode;
import com.kongtrolink.yyjw.service.DataMntService;
import com.kongtrolink.yyjw.util.JsonResult;
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
     * @param fsuId
     * @return
     */
    @RequestMapping("/getSignalList")
    public JsonResult getSignalList(@RequestBody(required = false) Map<String, Object> requestBody, String fsuId)
    {


        JSONObject result = dataMntService.getSignalList(requestBody, fsuId);
        if (result != null)
        {
            if ( "0".equals(result.get("result"))) return new JsonResult("执行任务失败", false);
            JSONObject data = (JSONObject) result.get("data");
            if (data!=null)
            {
                transSignalCode(data);
            return new JsonResult(data);
            }
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

    @RequestMapping("/getSignalHistory")
    public JsonResult getSignalHistory(@RequestBody(required = false) Map<String, Object> requestBody, String fsuId)
    {
        JSONObject result = dataMntService.getSignalListHistory(requestBody, fsuId);
            if (result != null)
            {
                if ( "0".equals(result.get("result"))) return new JsonResult("执行任务失败", false);
                JSONObject data = (JSONObject) result.get("data");
                if (data!=null)
                {
                    transSignalCode(data);
                    return new JsonResult(data);
                }
            }
        return new JsonResult("请求错误或者超时", false);
    }

    @RequestMapping("/getAlarmList")
    public JsonResult getAlarmList(@RequestBody(required = false) Map<String, Object> requestBody, String fsuId)
    {
        JSONObject result = dataMntService.getAlarmList(requestBody, fsuId);
        return result == null ? new JsonResult("请求错误或者超时", false) :
                "0".equals(result.get("result")) ? new JsonResult("执行任务失败", false) :new JsonResult(result.get("data"));
    }


    @RequestMapping("/getThreshold")
    public JsonResult getThreshold(@RequestBody Map<String, Object> requestBody, String fsuId)
    {
        JSONObject result = dataMntService.getThreshold(requestBody, fsuId);
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
    public JsonResult setThreshold(@RequestBody(required = false) Map<String, Object> requestBody, String fsuId)
    {
        JSONObject result = dataMntService.setThreshold(requestBody, fsuId);
        return result == null ? new JsonResult("请求错误或者超时", false) :
                "0".equals(result.get("result")) ? new JsonResult("执行任务失败", false) :new JsonResult(result.get("data"));
    }
 @RequestMapping("/setSignal")
    public JsonResult setSignal(@RequestBody(required = false) Map<String, Object> requestBody, String fsuId)
    {
        JSONObject result = dataMntService.setSignal(requestBody, fsuId);
        return result == null ? new JsonResult("请求错误或者超时", false) :
                "0".equals(result.get("result")) ? new JsonResult("执行任务失败", false) :new JsonResult(result.get("data"));
    }

}