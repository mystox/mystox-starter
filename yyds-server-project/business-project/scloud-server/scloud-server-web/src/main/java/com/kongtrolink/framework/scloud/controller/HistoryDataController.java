package com.kongtrolink.framework.scloud.controller;

import com.kongtrolink.framework.entity.JsonResult;
import com.kongtrolink.framework.entity.ListResult;
import com.kongtrolink.framework.scloud.controller.base.ExportController;
import com.kongtrolink.framework.scloud.dao.HistoryDataDao;
import com.kongtrolink.framework.scloud.entity.HistoryDataEntity;
import com.kongtrolink.framework.scloud.entity.model.HistoryDataModel;
import com.kongtrolink.framework.scloud.entity.realtime.SignalDiInfo;
import com.kongtrolink.framework.scloud.query.HistoryDataQuery;
import com.kongtrolink.framework.scloud.query.SignalDiInfoQuery;
import com.kongtrolink.framework.scloud.service.HistoryDataService;
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
            List<HistoryDataModel> list = historyDataService.getHisList(uniqueCode,historyDataQuery);
            int count  =  historyDataService.getHisCount(uniqueCode,historyDataQuery);
            ListResult value = new ListResult(list,count);
            return new JsonResult(value);
        }catch (Exception e){
            e.printStackTrace();
        }
        return new JsonResult("查询失败",false);
    }

    /**
     * 根据查询 某一个遥测信号值列表 导出
     */
    @RequestMapping(value = "/export", method = RequestMethod.POST)
    public void exportSignalDiInfo(@RequestBody HistoryDataQuery historyDataQuery, HttpServletResponse response) {
        String uniqueCode = getUniqueCode();
        historyDataQuery.setPageSize(Integer.MAX_VALUE);
        historyDataQuery.setCurrentPage(1);
        List<HistoryDataModel> list = historyDataService.getHisList(uniqueCode,historyDataQuery);
        String cntbId = historyDataQuery.getCntbId();
        String title = cntbId+"历史数据";
        String[] headsName = { "时间", "值"};
        String[] propertiesName = { "time", "value"};
        export(response,list,propertiesName, headsName, title);
    }
}
