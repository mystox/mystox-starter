package com.kongtrolink.framework.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.kongtrolink.framework.core.entity.Fsu;
import com.kongtrolink.framework.exception.ExcelParseException;
import com.kongtrolink.framework.model.session.BaseController;
import com.kongtrolink.framework.service.FsuService;
import com.kongtrolink.framework.util.ExcelUtil;
import com.kongtrolink.framework.util.JsonResult;
import org.apache.commons.fileupload.disk.DiskFileItem;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.util.*;

/**
 * \* @Author: mystox
 * \* Date: 2018/12/1 10:41
 * \* Description:
 * \
 */
@RestController
@RequestMapping("/fsu")
public class FSUController extends BaseController {

    Logger logger = LoggerFactory.getLogger(FSUController.class);
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
        String roleName = getCurrentRoleName();
        if (!isAdmin()&& !isManager() ) {
            if ((isRoot() || (StringUtils.isNotBlank(roleName) && roleName.contains("管理员")))) {
                List<String> managerUsers = getManagerUsers();
                requestBody.put("userIds", managerUsers);
            } else {
                List<String> userIds = new ArrayList<>();
                userIds.add(getUserId());
                requestBody.put("userIds", userIds);
            }
        }
        JSONObject result = fsuService.listFsu(requestBody);//查询结果
        return new JsonResult(result);
    }

    @RequestMapping("/getFsuListByCoordinate")
    public JsonResult getFsuListByCoordinate(@RequestBody(required = false) Map<String, Object> requestBody) {
        JSONArray result = fsuService.getFsuListByCoordinate(requestBody);
        return new JsonResult(result);

    }

    @RequestMapping("/registerToNbIot")
    public JsonResult registerToNb(@RequestBody(required = false) Map<String, Object> requestBody, String sn) {
        JSONObject result = fsuService.registerToNb(requestBody, sn);
        return result == null ? new JsonResult("请求错误或者超时", false) :
                0 == result.getInteger("result") ? new JsonResult(result.toJSONString(), false) : new JsonResult(result);
    }

    @RequestMapping("/getFsu")
    public JsonResult getFsu(@RequestBody(required = false) Map<String, Object> requestBody) {
        Fsu result = fsuService.getFsu(requestBody);

        return result == null ? new JsonResult("该fsu不存在", false) : new JsonResult(result);
    }

    @RequestMapping("/setFsu")
    public JsonResult setFsu(@RequestBody(required = false) Map<String, Object> requestBody) {
        String roleName = getCurrentRoleName();
        if (!isAdmin() && !isManager() && !isRoot() && !roleName.contains("管理员")) {
            String userId = getUserId();
            requestBody.put("userId", userId);
        }
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

    /**
     * 获取编译所需设备信息
     *
     * @return
     */
    @RequestMapping("/getCompilerDeviceInfo")
    public JsonResult getCompilerDeviceInfo(@RequestBody JSONObject compilerBody, String sn) {
        JSONObject result = fsuService.getCompilerDeviceInfo(compilerBody, sn);
        return result == null ? new JsonResult("请求错误或者超时", false) :
                new JsonResult(result);
    }

    /**
     * 获取编译配置
     *
     * @return
     */
    @RequestMapping("/getCompilerConfig")
    public JsonResult getCompilerConfig(@RequestBody JSONObject compilerBody, String sn) {
        JSONObject result = fsuService.getCompilerConfig(compilerBody, sn);
        return result == null ? new JsonResult("请求错误或者超时", false) :
                new JsonResult(result);
    }

    /**
     * 生成编译文件
     *
     * @return
     */
    @RequestMapping("/compilerFile")
    public JsonResult compilerFile(@RequestBody JSONObject compilerBody) {
        JSONObject result = fsuService.compilerFile(compilerBody);
        return result == null ? new JsonResult("请求错误或者超时", false) :
                new JsonResult(result);
    }

    @RequestMapping("/compiler")
    public JsonResult compiler(@RequestBody JSONObject requestBody, String sn) {
        JSONObject result = fsuService.compiler(requestBody, sn);
        return result == null ? new JsonResult("请求错误或者超时", false) :
                0 == result.getInteger("result") ? new JsonResult("执行任务失败", false) : new JsonResult(result);
    }


    @RequestMapping("/getDeviceList")
    public JsonResult getDeviceList(@RequestBody Map<String, Object> requestBody, String sn) {

        JSONArray result = fsuService.getDeviceList(requestBody, sn);
        if (result != null) {
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
        String userId = getUserId();
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
                snObj.put("userId", userId);
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
                    result.remove("result");
                    JsonResult jsonResult = new JsonResult("导入失败[" + result.toJSONString() + "]", false);
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
                    result.remove("result");
                    JsonResult jsonResult = new JsonResult("导入告警点表失败[" + result.toJSONString() + "]", false);
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
                    result.remove("result");
                    JsonResult jsonResult = new JsonResult("导入信号点失败[" + result.toJSONString() + "]", false);
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


    @RequestMapping(value = "/remoteCompilerFileDown")
    public void remoteCompilerFileDown(@RequestBody(required = false) JSONObject body, HttpServletResponse response, HttpServletRequest request) {
//        String url = body.getString("url");
        String urlStr = body.getString("url");
        if (StringUtils.isNotBlank(urlStr)) {
            urlStr = urlStr.replace("\\", "/"); //系统容错
        }
        InputStream is = null;

//        String url = "http://172.16.5.123:32804/Engine/Test/7/MMU100/CassEngine.bin";
        try {
            URL url = new URL(urlStr);
            URLConnection urlc = url.openConnection();
            urlc.setConnectTimeout(100000);
            urlc.setReadTimeout(100000);
            is = urlc.getInputStream();
            response.setContentType("application/force-download");// 设置强制下载不打开
//            response.addHeader("Content-Disposition", "attachment;fileName=" + );// 设置文件名
            byte[] buffer = new byte[1024];
            FileInputStream fis = null;
            BufferedInputStream bis = null;
            try {
//                fis = new FileInputStream(file);
                bis = new BufferedInputStream(is);
                OutputStream os = response.getOutputStream();
                int i = bis.read(buffer);
                while (i != -1) {
                    os.write(buffer, 0, i);
                    i = bis.read(buffer);
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (bis != null) {
                    try {
                        bis.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (is != null) {
                    try {
                        is.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

//            request.getRequestDispatcher(url).forward(request,response);
//            response.sendRedirect(url);
        } catch (Exception e) {
            e.printStackTrace();
        }
//        logger.info("sendRedirect:{}",url);
    }

    @RequestMapping(value = "/remoteCompilerFileDowna")
    public void remoteCompilerFileDowna(@RequestBody JSONObject body, HttpServletResponse response) {
        String url = body.getString("url");
        logger.info("forward:{}", url);

    }

}