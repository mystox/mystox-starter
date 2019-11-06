package com.kongtrolink.framework.reports.controller;

import com.alibaba.fastjson.JSONObject;
import com.kongtrolink.framework.entity.JsonResult;
import com.kongtrolink.framework.entity.MsgResult;
import com.kongtrolink.framework.reports.entity.ReportWebConfig;
import com.kongtrolink.framework.reports.service.ReportsControllerService;
import com.kongtrolink.framework.service.MqttSender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Created by mystoxlol on 2019/10/31, 9:34.
 * company: kongtrolink
 * description:
 * update record:
 */
@RestController
@RequestMapping("/reports")
public class ReportsController {
    @Autowired
    ReportsControllerService reportsControllerService;

    @Autowired
    MqttSender mqttSender;

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
        MsgResult msgResult = mqttSender.sendToMqttSyn(
                reportServerCode, operaCode, body.toJSONString());
        return new JsonResult(msgResult);

    }

    @RequestMapping("/configDataSave")
    JsonResult configDataSave(@RequestBody(required = false) JSONObject body) {
        reportsControllerService.saveConfigData(body);
        return new JsonResult();
    }
    @RequestMapping("/configDataGet")
    JsonResult configDataGet(@RequestBody(required = false) JSONObject body) {
        String serverCode = body.getString("serverCode");
        String enterpriseCode = body.getString("enterpriseCode");
        ReportWebConfig reportWebConfig = reportsControllerService.getConfigData(serverCode,enterpriseCode);
        return new JsonResult(reportWebConfig);
    }
}

