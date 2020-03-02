package com.kongtrolink.framework.reports.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.kongtrolink.framework.core.entity.User;
import com.kongtrolink.framework.core.entity.session.BaseController;
import com.kongtrolink.framework.core.entity.session.WebPageInfo;
import com.kongtrolink.framework.core.utils.ControllerCommonUtils;
import com.kongtrolink.framework.entity.JsonResult;
import com.kongtrolink.framework.entity.MsgResult;
import com.kongtrolink.framework.reports.entity.ReportConfigRecord;
import com.kongtrolink.framework.reports.entity.ReportTask;
import com.kongtrolink.framework.reports.entity.ReportWebConfig;
import com.kongtrolink.framework.reports.service.ReportsControllerService;
import com.kongtrolink.framework.service.MqttOpera;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mystoxlol on 2019/10/31, 9:34.
 * company: kongtrolink
 * description:
 * update record:
 */
@RestController
@RequestMapping("/reports")
public class ReportsController extends BaseController {
    @Autowired
    ReportsControllerService reportsControllerService;

    //    @Autowired
//    MqttSender mqttSender;
    @Autowired
    MqttOpera mqttOpera;

    @Value("${server.name}")
    private String serverName;
    @Value("${server.version}")
    private String serverVersion;


    @RequestMapping(value = "/getReportTaskList", method = RequestMethod.POST)
    JsonResult getReportsTask(@RequestBody JSONObject body) {
        JSONObject reportTaskList = reportsControllerService.getReportsTask(body);
        return new JsonResult(reportTaskList);

    }


    @RequestMapping(value = "/getReportTaskResults", method = RequestMethod.POST)
    JsonResult getReportsTaskResults(@RequestBody JSONObject body) {
        JSONObject reportsTaskResult = reportsControllerService.getReportsTaskResult(body);
        return new JsonResult(reportsTaskResult);

    }

    @RequestMapping(value = "/getReportsOperaCodeList", method = RequestMethod.POST)
    JsonResult getReportsOperaCodeList() {
        List<JSONObject> reportsOperaCodeList = reportsControllerService.getReportsOperaCodeList();
        return new JsonResult(reportsOperaCodeList);
    }


    @RequestMapping("/taskExecutor")
    JsonResult reportsTest(@RequestBody(required = false) JSONObject body) {
        String operaCode = body.getString("operaCode");
        String reportServerCode = body.getString("reportServerCode");
//        MsgResult msgResult = mqttSender.sendToMqttSync(
//                reportServerCode, operaCode, body.toJSONString());
        MsgResult msgResult = mqttOpera.opera(operaCode, body.toJSONString());
        String msg = msgResult.getMsg();
        return new JsonResult(JSON.parseObject(msg));

    }

    @RequestMapping("/configDataSave")
    JsonResult configDataSave(@RequestBody(required = false) JSONObject body) {
        User user = getUser();
        //报表页面添加布局配置
        reportsControllerService.saveConfigData(body);
        //报表配置记录
        reportsControllerService.recordConfigData(body, user);
        return new JsonResult("保存成功", true);
    }

    @RequestMapping("/configDataGet")
    JsonResult configDataGet(@RequestBody(required = false) JSONObject body) {
        String serverCode = body.getString("serverCode");
        String enterpriseCode = body.getString("enterpriseCode");
        String funcPrivCode = body.getString("funcPrivCode");
        ReportWebConfig reportWebConfig = reportsControllerService.getConfigData(serverCode, enterpriseCode, funcPrivCode);
        if (StringUtils.isBlank(serverCode)) {
            reportsControllerService.getPrivData();
        }
        return new JsonResult(reportWebConfig);
    }

    @RequestMapping("/recordConfigDataGet")
    JsonResult recordConfigDataGet(@RequestBody(required = false) JSONObject body) {
        String serverCode = body.getString("serverCode");
        String enterpriseCode = body.getString("enterpriseCode");
//        String funcPrivCode = body.getString("funcPrivCode");
        List<ReportConfigRecord> reportWebConfig = reportsControllerService.getRecordConfigData(serverCode, enterpriseCode);
        return new JsonResult(reportWebConfig);
    }


    @RequestMapping("/recordConfigPrivGet")
    JsonResult getReportWebFuncPirv(@RequestBody(required = false) JSONObject body) {
        String serverCode = body.getString("serverCode");
        String enterpriseCode = body.getString("enterpriseCode");
        WebPageInfo currentMainMenu = getCurrentMainMenu();
        List<WebPageInfo> reportPrivs = new ArrayList<>();
        ControllerCommonUtils.getReportsFuncPriv(currentMainMenu, reportPrivs);
        List<JSONObject> result = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(reportPrivs)) {
//            List<String> privCodes = ReflectionUtils.convertElementPropertyToList(reportPrivs, "code");
            reportPrivs.forEach(reportPriv -> {
                String reportPrivCode = reportPriv.getCode();
//                String privName = reportPriv.getName();
                String showName = StringUtils.isBlank(reportPriv.getAlias()) ? reportPriv.getName() : reportPriv.getAlias();
                String hierarchyName = reportPriv.getHierarchyName();
                List<ReportConfigRecord> recordConfigDataList = reportsControllerService.getRecordConfigDataByPrivCode(serverCode, enterpriseCode, reportPrivCode);
                if (CollectionUtils.isNotEmpty(recordConfigDataList)) {
                    recordConfigDataList.forEach(reportConfigRecord -> {
                        String reportsTaskId = reportConfigRecord.getReportsTaskId();
                        JSONObject recordJson = JSON.parseObject(JSON.toJSONString(reportConfigRecord), JSONObject.class);
                        result.add(recordJson);
                        ReportTask reportTask = reportsControllerService.getReportsTaskByTaskId(reportsTaskId);
                        //填充包报表信息
                        recordJson.put("reportName", reportTask.getReportName());
                        recordJson.put("operaCode", reportTask.getOperaCode());
                        //填充页面权限名称信息
                        recordJson.put("privName", showName);
                        recordJson.put("privHierarchy", hierarchyName);
                    });
                } else {
                    JSONObject recordJson = new JSONObject();
                    recordJson.put("privName", showName);
                    recordJson.put("privHierarchy", hierarchyName);
                    recordJson.put("funcPrivCode", reportPrivCode);
                    result.add(recordJson);
                }


            });
        }
        return new JsonResult(result);
//        return new JsonResult(reportWebConfig);
    }





}

