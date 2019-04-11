package com.kongtrolink.framework.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.kongtrolink.framework.core.ControllerInstance;
import com.kongtrolink.framework.core.entity.Fsu;
import com.kongtrolink.framework.service.FsuService;
import com.kongtrolink.framework.util.JsonResult;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * \* @Author: mystox
 * \* Date: 2018/12/1 10:41
 * \* Description:
 * \
 */
@RestController
@RequestMapping("/fsu")
public class FSUController
{
    Logger logger = LoggerFactory.getLogger(this.getClass().getSimpleName());

    @Autowired
    FsuService fsuService;
    @Autowired
    private ThreadPoolTaskExecutor taskExecutor;

    /**
     * 检查fsu是否已经配置,需要重新绑定
     *
     * @param requestBody
     * @return
     */
    @RequestMapping("/checkFsu")
    public JsonResult checkFsu(@RequestBody(required = false) Map<String, Object> requestBody)
    {
        List<Fsu> result = fsuService.searchFsu(requestBody);
        if (result != null && result.size() > 0)
            return new JsonResult("fsu已配置,是否解绑", false);
        else
            return new JsonResult("fsu未绑定配置,继续配置", true);
    }


    @RequestMapping("/list")
    public JsonResult list(@RequestBody(required = false) Map<String, Object> requestBody)
    {
        JSONArray result = fsuService.listFsu(requestBody);//查询结果
        return new JsonResult(result);
    }

    @RequestMapping("/getFsuListByCoordinate")
    public JsonResult getFsuListByCoordinate(@RequestBody(required = false) Map<String, Object> requestBody)
    {
        List<Fsu> result = fsuService.getFsuListByCoordinate(requestBody);
        return new JsonResult(result);

    }

    @RequestMapping("/getDeviceName")
    public JsonResult getDeviceName(String code)
    {
        Map<String, String> dStationMap = ControllerInstance.getInstance().getdStationMap();
        Map<String, String> roomStationMap = ControllerInstance.getInstance().getRoomStationMap();
        if (StringUtils.isNotBlank(code))
        {
            String sCode = StringUtils.substring(code, 6, 9);
            if ("418".equals(sCode))
            {
                String room_code = StringUtils.substring(code, 7, 10);
                return new JsonResult(roomStationMap.get(room_code));
            } else
            {
                String name = dStationMap.get(sCode);
                return new JsonResult(StringUtils.isBlank(name) ? "未知设备" : name);
            }
        } else return new JsonResult("code为空",false);

    }

  /*  @RequestMapping("/getTierName")
    public JsonResult getTierName(String fsuCode)
    {

        if (fsuCode != null)
        {
            String code = fsuCode;
            if (code.length()<6) return new JsonResult("code长度少于6",false);
            String tierCode = StringUtils.substring(code, 0, 6);
            if (StringUtils.isNotBlank(tierCode) && StringUtils.length(tierCode) >= 2)
            {
                StringBuilder sb = new StringBuilder();
                Map<String, String> tierMap = ControllerInstance.getInstance().getTierMap();
                String firstTier = StringUtils.substring(tierCode, 0, 2);
                sb.append(tierMap.get(firstTier));
                if (StringUtils.length(tierCode) >= 4)
                {
                    String secondTier = StringUtils.substring(tierCode, 0, 4);
                    String secondTierName = tierMap.get(secondTier);
                    if (StringUtils.isNotBlank(secondTierName))
                    {
                        sb.append("-");
                        sb.append(tierMap.get(secondTier));
                        if (StringUtils.length(tierCode) >= 6)
                        {

                            String tierName = tierMap.get(tierCode);
                            if (StringUtils.isNotBlank(tierName))
                            {
                                sb.append("-");
                                sb.append(tierName);
                            }
                        }
                    }
                }
                    return new JsonResult(sb.toString());
            }
        }
            return new JsonResult("fsuCode错误",false);

    }*/


    @RequestMapping("/getFsu")
    public JsonResult getFsu(@RequestBody(required = false) Map<String, Object> requestBody)
    {
        Fsu result = fsuService.getFsu(requestBody);

        return result == null ? new JsonResult("该fsu不存在", false) : new JsonResult(result);
    }

    @RequestMapping("/setFsu")
    public JsonResult setFsu(@RequestBody(required = false) Map<String, Object> requestBody)
    {
        JSONObject result = fsuService.setFsu(requestBody);
        return result == null ? new JsonResult("请求错误或者超时", false) : new JsonResult(result.get("data"));
    }

    @RequestMapping("/upgrade")
    public JsonResult upgrade(@RequestBody Map<String, Object> requestBody,String fsuId)
    {

        JSONObject result = fsuService.upgrade(requestBody,fsuId);
        return result == null ? new JsonResult("请求错误或者超时", false) : new JsonResult(result.get("data"));
    }

    @RequestMapping("/compiler")
    public JsonResult compiler(@RequestBody Map<String, Object> requestBody, String sn)
    {
        JSONObject result = fsuService.compiler(requestBody,sn);
        return result == null ? new JsonResult("请求错误或者超时", false) : new JsonResult(result.get("data"));
    }




    @RequestMapping("/getDeviceList")
    public JsonResult getDeviceList(@RequestBody Map<String, Object> requestBody, String sn)
    {

        JSONArray result = fsuService.getDeviceList(requestBody, sn);
        if (result != null)
        {
            /*Map<String, String> dStationMap = ControllerInstance.getInstance().getdStationMap();
            Map<String, String> roomStationMap = ControllerInstance.getInstance().getRoomStationMap();*/
//            JSONArray data = (JSONArray) result.get("data");
          /*  JSONArray devices = (JSONArray) data.get("devices");
            for (Object object : devices)
            {
                JSONObject device = (JSONObject) object;
                String code = (String) device.get("code");
                String sCode = StringUtils.substring(code, 6, 9);
                if ("418".equals(sCode))
                {
                    String room_code = StringUtils.substring(code, 7, 10);
                    device.put("name", roomStationMap.get(room_code));
                } else
                {
                    String name = dStationMap.get(sCode);
                    device.put("name", StringUtils.isBlank(name) ? "未知设备" : name);
                }
            }*/
            return new JsonResult(result);
        } else return new JsonResult("请求错误或者超时", false);
    }


    @RequestMapping("/getFsuStatus")
    public JsonResult getFsuStatus(@RequestBody(required = false) Map<String, Object> requestBody, String fsuId)
    {

        JSONObject result = fsuService.getFsuStatus(requestBody, fsuId);
        return result == null ? new JsonResult("请求错误或者超时", false) : new JsonResult(result.get("data"));
    }

    @RequestMapping("/logoutFsu")
    public JsonResult logoutFsu(@RequestBody(required = false) Map<String, Object> requestBody, String fsuId)
    {

        JSONObject result = fsuService.logoutFsu(requestBody, fsuId);
        return result == null ? new JsonResult("请求错误或者超时", false) : new JsonResult(result.get("data"));
    }

    @RequestMapping("/getOperationHistory")
    public JsonResult getOperationHistory(@RequestBody(required = false) Map<String, Object> requestBody,String fsuId)
    {
//        List<OperatHistory> operatHistories = fsuService.getOperationHistory(requestBody,fsuId);
        JSONObject result = fsuService.getOperationHistoryByMqtt(requestBody,fsuId);
//        System.out.println(operatHistories.size());
        return new JsonResult(result);
    }

}