package com.kongtrolink.framework.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.kongtrolink.framework.core.entity.Fsu;
import com.kongtrolink.framework.exception.ExcelParseException;
import com.kongtrolink.framework.service.FsuService;
import com.kongtrolink.framework.util.ExcelUtil;
import com.kongtrolink.framework.util.JsonResult;
import org.apache.commons.fileupload.disk.DiskFileItem;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * \* @Author: mystox
 * \* Date: 2018/12/1 10:41
 * \* Description:
 * \
 */
@RestController
@RequestMapping("/fsu")
public class FSUController {

    private FsuService fsuService;

    @Autowired
    public void setFsuService(FsuService fsuService) {
        this.fsuService = fsuService;
    }

    /**
     * 检查fsu是否已经配置,需要重新绑定
     *
     * @param requestBody
     * @return
     */
    @RequestMapping("/checkFsu")
    public JsonResult checkFsu(@RequestBody(required = false) Map<String, Object> requestBody) {
        List<Fsu> result = fsuService.searchFsu(requestBody);
        if (result != null && result.size() > 0)
            return new JsonResult("fsu已配置,是否解绑", false);
        else
            return new JsonResult("fsu未绑定配置,继续配置", true);
    }

    /**
     * 检查fsu是否已经配置,需要重新绑定
     *
     * @param requestBody
     * @return
     */
    @RequestMapping("/unbind")
    public JsonResult unbind(@RequestBody(required = false) Map<String, Object> requestBody, String sn) {
        JSONObject result = fsuService.unbind(requestBody, sn);
        return result == null ? new JsonResult("请求错误或者超时", false) :
                0 == result.getInteger("result") ? new JsonResult("执行任务失败", false) : new JsonResult(result);
    }

    @RequestMapping("/list")
    public JsonResult list(@RequestBody(required = false) Map<String, Object> requestBody) {
        JSONObject result = fsuService.listFsu(requestBody);//查询结果
        return new JsonResult(result);
    }

    @RequestMapping("/getFsuListByCoordinate")
    public JsonResult getFsuListByCoordinate(@RequestBody(required = false) Map<String, Object> requestBody) {
        List<Fsu> result = fsuService.getFsuListByCoordinate(requestBody);
        return new JsonResult(result);

    }

    @RequestMapping("/getFsu")
    public JsonResult getFsu(@RequestBody(required = false) Map<String, Object> requestBody) {
        Fsu result = fsuService.getFsu(requestBody);

        return result == null ? new JsonResult("该fsu不存在", false) : new JsonResult(result);
    }

    @RequestMapping("/setFsu")
    public JsonResult setFsu(@RequestBody(required = false) Map<String, Object> requestBody) {
        JSONObject result = fsuService.setFsu(requestBody);
        return result == null ? new JsonResult("请求错误或者超时", false) :
                0 == result.getInteger("result") ? new JsonResult("执行任务失败", false) : new JsonResult(result);
    }

    @RequestMapping("/upgrade")
    public JsonResult upgrade(@RequestBody Map<String, Object> requestBody, String sn) {

        JSONObject result = fsuService.upgrade(requestBody, sn);
        return result == null ? new JsonResult("请求错误或者超时", false) :
                0 == result.getInteger("result") ? new JsonResult("执行任务失败", false) : new JsonResult(result);
    }

    @RequestMapping("/compiler")
    public JsonResult compiler(@RequestBody Map<String, Object> requestBody, String sn) {
        JSONObject result = fsuService.compiler(requestBody, sn);
        return result == null ? new JsonResult("请求错误或者超时", false) :
                0 == result.getInteger("result") ? new JsonResult("执行任务失败", false) : new JsonResult(result);
    }


    @RequestMapping("/getDeviceList")
    public JsonResult getDeviceList(@RequestBody Map<String, Object> requestBody, String sn) {

        JSONArray result = fsuService.getDeviceList(requestBody, sn);
        if (result != null) {
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
    public JsonResult getFsuStatus(@RequestBody(required = false) Map<String, Object> requestBody, String sn) {

        JSONObject result = fsuService.getFsuStatus(requestBody, sn);
        return result == null ? new JsonResult("请求错误或者超时", false) : new JsonResult(result);
    }

    @RequestMapping("/getRunState")
    public JsonResult getRunState(@RequestBody(required = false) Map<String, Object> requestBody, String sn) {

        JSONObject result = fsuService.getRunState(requestBody, sn);
        return result == null ? new JsonResult("请求错误或者超时", false) : new JsonResult(result);
    }

    @RequestMapping("/getTerminalLog")
    public JsonResult getTerminalPayload(@RequestBody(required = false) Map<String, Object> requestBody, String sn) {
        JSONObject result = fsuService.getTerminalPayload(requestBody, sn);
        return result == null ? new JsonResult("请求错误或者超时", false) : new JsonResult(result);
    }

    @RequestMapping("/logoutFsu")
    public JsonResult logoutFsu(@RequestBody(required = false) Map<String, Object> requestBody, String fsuId) {

        JSONObject result = fsuService.logoutFsu(requestBody, fsuId);
        return result == null ? new JsonResult("请求错误或者超时", false) : new JsonResult(result);
    }

    @RequestMapping("/getOperationHistory")
    public JsonResult getOperationHistory(@RequestBody(required = false) Map<String, Object> requestBody, String fsuId) {
//        List<OperatHistory> operatHistories = fsuService.getOperationHistory(requestBody,fsuId);
        JSONObject result = fsuService.getOperationHistoryByMqtt(requestBody, fsuId);
//        System.out.println(operatHistories.size());
        return new JsonResult(result);
    }

    @RequestMapping("/reboot")
    public JsonResult terminalReboot(@RequestBody(required = false) Map<String, Object> requestBody, String sn) {
//        List<OperatHistory> operatHistories = fsuService.getOperationHistory(requestBody,fsuId);
        JSONObject result = fsuService.terminalReboot(requestBody, sn);
//        System.out.println(operatHistories.size());
        return new JsonResult(result);
    }

    @RequestMapping("/setGPRS")
    public JsonResult setGPRS(@RequestBody(required = false) Map<String, Object> requestBody, String sn) {
//        List<OperatHistory> operatHistories = fsuService.getOperationHistory(requestBody,fsuId);
        JSONObject result = fsuService.setGprs(requestBody, sn);
//        System.out.println(operatHistories.size());
        return new JsonResult(result);
    }


    @RequestMapping(value = "/terminal/import", method = RequestMethod.POST)
    public JsonResult terminalImport(@RequestParam MultipartFile file, HttpServletRequest request) {
        // 解析 Excel 文件
        JSONArray snList = new JSONArray();
        CommonsMultipartFile cmf = (CommonsMultipartFile) file;
        DiskFileItem dfi = (DiskFileItem) cmf.getFileItem();
        File f = dfi.getStoreLocation();
        Set<String> snSet = new HashSet<>();
        try {
            String[][] cell = ExcelUtil.getInstance().getCellsFromFile(f);
            for (int r = 0; r < cell.length; r++) {

                JSONObject snObj = new JSONObject();
                String sn = cell[r][0];
                if (StringUtils.isBlank(sn)) {
                    return new JsonResult("存在空SN行", false);
                }
                if (snSet.contains(sn)) {
                    return new JsonResult("excel存在重复SN", false);
                }
                snSet.add(sn);
                snObj.put("SN", sn);
                snObj.put("vendor", cell[r][1]);
                String model = "";
                if (cell[r].length > 1) {
                    model = cell[r][1];
                }
                snObj.put("model", StringUtils.isNotBlank(model) ? model : "mmu100");
                snList.add(snObj);
            }
            JSONObject result = fsuService.saveTerminal(snList);
            if (result != null) {
                if ((int) result.get("result") == 0) {
                    result.remove("remove");
                    JsonResult jsonResult = new JsonResult("导入失败[" + result.toJSONString()+"]", false);
                    jsonResult.setData(result);
                    return jsonResult;
                }
                return new JsonResult(result);
            }
        } catch (ExcelParseException e) {
            return new JsonResult(e.getMessage(), false);
        }
        return new JsonResult("请求失败", false);
    }

    @RequestMapping(value = "/alarmModel/import", method = RequestMethod.POST)
    public JsonResult alarmModelImport(@RequestParam MultipartFile file, HttpServletRequest request) {
// 解析 Excel 文件
        JSONArray alarmSignalList = new JSONArray();
        CommonsMultipartFile cmf = (CommonsMultipartFile) file;
        DiskFileItem dfi = (DiskFileItem) cmf.getFileItem();
        File f = dfi.getStoreLocation();
        try {
            String[][] cell = ExcelUtil.getInstance().getCellsFromFile(f);
            for (int r = 0; r < cell.length; r++) {
                JSONObject alarmModel = new JSONObject();
                alarmModel.put("devType", cell[r][1]); //设备类型
                alarmModel.put("alarmId", cell[r][2]);
                alarmModel.put("coId", cell[r][10]); //信号点id
                alarmModel.put("coType", cell[r][11]); //信号点类型
                alarmModel.put("enable", "1".equals(cell[r][5])); //告警使能
                alarmModel.put("threshold", cell[r][6]);
                alarmModel.put("thresholdFlag", cell[r][7]);
                alarmModel.put("level", cell[r][8]);
                alarmModel.put("hystersis", cell[r][9]);
                alarmModel.put("delay", cell[r][12]);
                alarmModel.put("recoverDelay", cell[r][13]);
                alarmModel.put("repeatDelay", cell[r][14]);
                alarmModel.put("alarmDesc", cell[r][16]);
                alarmModel.put("normalDesc", cell[r][17]);
                alarmModel.put("highRateI", cell[r][18]);
                alarmModel.put("highRateT", cell[r][19]);
                alarmSignalList.add(alarmModel);
            }
            JSONObject result = fsuService.saveAlarmModelList(alarmSignalList);
            if (result != null) {
                if ((int) result.get("result") == 0) {
                    JsonResult jsonResult = new JsonResult("导入失败", false);
                    jsonResult.setData(result);
                    return jsonResult;
                }
                return new JsonResult(result);
            }
        } catch (ExcelParseException e) {
            return new JsonResult(e.getMessage(), false);
        }
        return new JsonResult("请求失败", false);
    }

    @RequestMapping(value = "/signalModel/import", method = RequestMethod.POST)
    public JsonResult signalModelImport(@RequestParam MultipartFile file, HttpServletRequest request) {
// 解析 Excel 文件
        JSONArray signalModelList = new JSONArray();
        CommonsMultipartFile cmf = (CommonsMultipartFile) file;
        DiskFileItem dfi = (DiskFileItem) cmf.getFileItem();
        File f = dfi.getStoreLocation();
        try {
            String[][] cell = ExcelUtil.getInstance().getCellsFromFile(f);
            for (int r = 0; r < cell.length; r++) {
                JSONObject signalModel = new JSONObject();
                signalModel.put("deviceType", cell[r][1]);
                signalModel.put("dataId", cell[r][2]);
                signalModel.put("type", cell[r][3]);
                signalModel.put("name", cell[r][4]);
                signalModel.put("unit", cell[r][10]);
                signalModel.put("valueBase", cell[r][11]);
                signalModelList.add(signalModel);
            }
            JSONObject result = fsuService.saveSignalModelList(signalModelList);
            if (result != null) {
                if ((int) result.get("result") == 0) {
                    JsonResult jsonResult = new JsonResult("导入失败", false);
                    jsonResult.setData(result);
                    return jsonResult;
                }
                return new JsonResult(result);
            }
        } catch (ExcelParseException e) {
            return new JsonResult(e.getMessage(), false);
        }
        return new JsonResult("请求失败", false);
    }

}