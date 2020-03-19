package com.kongtrolink.framework.scloud.controller;

import com.alibaba.fastjson.JSONObject;
import com.kongtrolink.framework.core.constant.ScloudBusinessOperate;
import com.kongtrolink.framework.entity.JsonResult;
import com.kongtrolink.framework.entity.ListResult;
import com.kongtrolink.framework.entity.MsgResult;
import com.kongtrolink.framework.scloud.controller.base.ExportController;
import com.kongtrolink.framework.scloud.dao.HistoryDataDao;
import com.kongtrolink.framework.scloud.entity.HistoryDataEntity;
import com.kongtrolink.framework.scloud.entity.his.JobMessageAckEntity;
import com.kongtrolink.framework.scloud.entity.his.JobMessageEntity;
import com.kongtrolink.framework.scloud.entity.model.HistoryDataDayModel;
import com.kongtrolink.framework.scloud.entity.model.HistoryDataModel;
import com.kongtrolink.framework.scloud.entity.realtime.SignalDiInfo;
import com.kongtrolink.framework.scloud.query.HistoryDataQuery;
import com.kongtrolink.framework.scloud.query.SignalDiInfoQuery;
import com.kongtrolink.framework.scloud.service.HistoryDataService;
import com.kongtrolink.framework.service.MqttOpera;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

/**
 * 历史数据功能类
 * @author Mag
 **/
@Controller
@RequestMapping(value = "/historyData", method = RequestMethod.POST)
public class HistoryDataController  extends ExportController {

    @Autowired
    HistoryDataService historyDataService;
    @Autowired
    MqttOpera mqttOpera;
    /**
     * 根据查询条件获取 历史数据列表 - 分页
     *
     * @param historyDataQuery 查询条件
     * @return 列表
     */
    @RequestMapping(value = "/getList")
    public @ResponseBody JsonResult getHisList(@RequestBody HistoryDataQuery historyDataQuery){
        try{
            String uniqueCode = getUniqueCode();
            String cntbId = historyDataQuery.getCntbId();
            List list;
            if(cntbId==null || "".equals(cntbId)){
                list = historyDataService.getHisAllList(uniqueCode,historyDataQuery);
            }else{
                list = historyDataService.getHisList(uniqueCode,historyDataQuery);
            }
            int count  =  historyDataService.getHisCount(uniqueCode,historyDataQuery);
            ListResult value = new ListResult(list,count);
            return new JsonResult(value);
        }catch (Exception e){
            e.printStackTrace();
        }
        return new JsonResult("查询失败",false);
    }

    @RequestMapping(value = "/getDayReport")
    public @ResponseBody JsonResult getDayReport(@RequestBody HistoryDataQuery historyDataQuery){
        try{
            String uniqueCode = getUniqueCode();
            List list = historyDataService.getDayReport(uniqueCode,historyDataQuery);
            return new JsonResult(list);
        }catch (Exception e){
            e.printStackTrace();
        }
        return new JsonResult("查询失败",false);
    }

    @RequestMapping(value = "/exportDayReport")
    public void getDayReport(@RequestBody HistoryDataQuery historyDataQuery, HttpServletResponse response){
        try{
            String uniqueCode = getUniqueCode();
            List< HistoryDataDayModel > list = historyDataService.getDayReport(uniqueCode,historyDataQuery);
            String cntbId = historyDataQuery.getCntbId();
            String title = cntbId+"历史数据按天统计";
            String[] headsName = { "站点层级", "站点名称", "站点编码", "设备名称","设备编码","遥测信号","日期","最大值","最小值","平均值"};
            String[] propertiesName = { "tierName", "siteName", "siteCode", "deviceName", "deviceCode", "signalName", "time", "maxValue", "minValue", "avgValue"};
            export(response,list,propertiesName, headsName, title);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    /**
     * 根据查询 某一个遥测信号值列表 导出
     */
    @RequestMapping(value = "/export", method = RequestMethod.POST)
    public void exportSignalDiInfo(@RequestBody HistoryDataQuery historyDataQuery, HttpServletResponse response) {
        String uniqueCode = getUniqueCode();
        historyDataQuery.setPageSize(Integer.MAX_VALUE);
        historyDataQuery.setCurrentPage(1);
        String cntbId = historyDataQuery.getCntbId();
        List list;
        if(cntbId==null || "".equals(cntbId)){
            list = historyDataService.getHisAllList(uniqueCode,historyDataQuery);
            historyDataService.exportMap(response,list,uniqueCode,historyDataQuery.getDeviceType(),"历史数据");
        }else{
            list = historyDataService.getHisList(uniqueCode,historyDataQuery);
            String title = cntbId+"历史数据";
            String[] headsName = { "时间", "值"};
            String[] propertiesName = { "time", "value"};
            export(response,list,propertiesName, headsName, title);
        }

    }

    @RequestMapping(value = "/config", method = RequestMethod.POST)
    public  @ResponseBody JsonResult  exportSignalDiInfo(@RequestBody JobMessageEntity jobMessageEntity) {
        try{
            String uniqueCode = getUniqueCode();
            jobMessageEntity.setUniqueCode(uniqueCode);
            MsgResult result = mqttOpera.opera(ScloudBusinessOperate.UPDATE_FSU_POLLING, JSONObject.toJSONString(jobMessageEntity));
            String value  = result.getMsg();
            JobMessageAckEntity jobMessageAckEntity = JSONObject.parseObject(value,JobMessageAckEntity.class);
            return new JsonResult(jobMessageAckEntity);
        }catch (Exception e){
            e.printStackTrace();
        }
        return new JsonResult("设置失败",false);
    }
}
