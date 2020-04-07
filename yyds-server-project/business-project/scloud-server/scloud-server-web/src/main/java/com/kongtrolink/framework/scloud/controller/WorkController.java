package com.kongtrolink.framework.scloud.controller;

import com.alibaba.fastjson.JSONObject;
import com.kongtrolink.framework.core.entity.User;
import com.kongtrolink.framework.core.entity.session.BaseController;
import com.kongtrolink.framework.entity.JsonResult;
import com.kongtrolink.framework.entity.ListResult;
import com.kongtrolink.framework.scloud.constant.WorkConstants;
import com.kongtrolink.framework.scloud.controller.base.ExportController;
import com.kongtrolink.framework.scloud.entity.*;
import com.kongtrolink.framework.scloud.query.AlarmQuery;
import com.kongtrolink.framework.scloud.query.WorkQuery;
import com.kongtrolink.framework.scloud.service.*;
import com.kongtrolink.framework.scloud.util.XlsExporter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @Auther: liudd
 * @Date: 2020/4/1 13:26
 * @Description:
 */
@Controller
@RequestMapping("/work")
public class WorkController extends ExportController {

    @Autowired
    WorkService workService;
    @Autowired
    SiteService siteService;
    @Autowired
    AlarmService alarmService;
    @Autowired
    WorkAlarmConfigService workAlarmConfigService;
    @Autowired
    WorkConfigService workConfigService;
    @Autowired
    WorkRecordService workRecordService;
    /**
     * WEB端工单列表，只能获取当前用户管辖的站点
     * @param workQuery
     * @param request
     * @return
     */
    @RequestMapping(value = "/list", method = RequestMethod.POST)
    public @ResponseBody
    JsonResult list(@RequestBody WorkQuery workQuery, HttpServletRequest request) {
        String uniqueCode = getUniqueCode();
        List<Work> list = workService.list(uniqueCode, workQuery);
        int count = workService.count(uniqueCode, workQuery);
        JsonResult jsonResult = new JsonResult(list, count);
        return jsonResult;
    }

    /**
     * 转派是获取工单所在站点的所有维护人员
     * @param workQuery
     * @param request
     * @return
     */
//    @RequestMapping(value = "/getSiteMaintainers", method = RequestMethod.POST)
//    public @ResponseBody JsonResult getSiteMaintainers(@RequestBody WorkQuery workQuery, HttpServletRequest request) {
//        String uniqueCode = getUniqueCode();
//        String siteId = workQuery.getSiteId();
//        Site site = siteService.querySite(uniqueCode, siteId);
//        if(null == site){
//            return new JsonResult("获取数据失败，站点不存在", false);
//        }
//        List<Maintainer> maintainerList = maintainerService.getBySiteCode(uniqueCode, site.getCode());
//        return new JsonResult(maintainerList);
//    }

    /**
     * 手动派单，匹配自动派单的告警也可以提前手动派单
     * @param request
     * @return
     */
    @RequestMapping(value = "/sendWork", method = RequestMethod.POST)
    public @ResponseBody JsonResult add(@RequestBody WorkQuery workQuery, HttpServletRequest request){
        boolean result ;
        String uniqueCode = getUniqueCode();
        User user = getUser(request);
        //判定该设备是否有未回单的工单
        Work noOverWork = workService.getNotOverByDeviceCode(uniqueCode, workQuery.getDeviceCode());
        if(null != noOverWork){
            noOverWork.increateAlarm(workQuery.getWorkAlarm());
            boolean update = workService.update(uniqueCode, noOverWork);
            if(update){
                //发送推送
                JSONObject jpush = workService.createJpush(noOverWork, WorkConstants.OPERATE_SEND, null);

                return new JsonResult("派单成功", true);
            }
            return new JsonResult("派单失败", false);
        }else{  //该设备没有待回单工单
            FacadeView operator = new FacadeView(user.getUsername(), user.getName(), user.getPhone());
            result = createNewWork(uniqueCode, workQuery, operator, null);
        }
        //先调用远程接口
        AlarmQuery alarmQuery = new AlarmQuery();
        alarmQuery.setEnterpriseCode(workQuery.getEnterpriseCode());
        alarmQuery.setServerCode(workQuery.getServerCode());
        alarmQuery.setId(workQuery.getAlarmId());
        alarmQuery.setTreport(alarmQuery.getTreport());
        JSONObject jsonObject = alarmService.updateWorkInfo(alarmQuery);
        if(!jsonObject.getBoolean("success")){
            return new JsonResult(jsonObject.getString("info"), false);
        }
        if(result){
            return new JsonResult("派单成功", true);
        }
        return new JsonResult("派单失败", false);
    }

    /**
     * @auther: liudd
     * @date: 2020/4/2 14:12
     * 功能描述:生成新工单
     */
    private boolean createNewWork(String uniqueCode, WorkQuery workQuery, FacadeView operator, FacadeView receiver){
        //获取该告警对应的告警工单配置对应信息
        WorkAlarmConfig alarmWorkConfig = workAlarmConfigService.findByAlarmKey(uniqueCode, workQuery.getWorkAlarm().getAlarmKey());
        WorkConfig workConfig ;
        if(null != alarmWorkConfig) {
            //根据对应信息找到工单配置
            workConfig = workConfigService.getById(uniqueCode, alarmWorkConfig.getWorkConfigId());
        }else {
            //匹配手动告警工单配置，如果没有，则使用默认告警工单配置
            workConfig = workConfigService.matchManualConfig(uniqueCode, workQuery.getSiteCode());
        }
        //生成工单
        Work work = workService.createWork(workQuery, workConfig, WorkConstants.WORK_CONFIG_TYPE_MANUAL);
        //保存
        workService.add(uniqueCode, work);

        //产生一条工单记录
        WorkRecord workRecord = workRecordService.createWorkRecord(work, operator, WorkConstants.OPERATE_SEND,
                null, work.getOperatorTime(), 0, WorkConstants.FTU_WEB, receiver);
        workRecordService.add(uniqueCode, workRecord);
        //删除数据库中告警工单配置对应信息
        workAlarmConfigService.deleteByAlarmKey(uniqueCode, workQuery.getWorkAlarm().getAlarmKey());
        //发送工单推送
        JSONObject jpush = workService.createJpush(work, WorkConstants.OPERATE_SEND, null);

        return true;
    }

    /**
     * @auther: liudd
     * @date: 2020/4/2 12:16
     * 功能描述:接单
     */
    @RequestMapping(value = "/receive", method = RequestMethod.POST)
    public @ResponseBody JsonResult receive(@RequestBody WorkQuery workQuery, HttpServletRequest request) {
        Date curTime = new Date();
        String uniqueCode = getUniqueCode();
        User user = getUser(request);
        String workId = workQuery.getId();
        return workService.receCommon(uniqueCode, workId, user, curTime, WorkConstants.FTU_WEB);
    }

    /**
     * @auther: liudd
     * @date: 2020/4/2 12:08
     * 功能描述:转派后，处理人为待接单人
     */
    @RequestMapping(value = "/redeploy", method = RequestMethod.POST)
    public @ResponseBody JsonResult redeploy(@RequestBody WorkRecord workRecord, HttpServletRequest request){
        //worker中需要包含处理人的id和name
        String uniqueCode = getUniqueCode();
        Date curDate = new Date();
        User user = getUser(request);
        return workService.redeployCommon(uniqueCode, workRecord, user, curDate, WorkConstants.FTU_WEB);
    }

    /**
     * web端催单
     * @param workRecord
     * @param request
     * @return
     */
    @RequestMapping(value = "/urgeWork", method = RequestMethod.POST)
    public @ResponseBody JsonResult urgeWork(@RequestBody WorkRecord workRecord, HttpServletRequest request){
        String uniqueCode = getUniqueCode();
        Date curDate = new Date();
        //因为是管理员催单，所以不能用service的方法生成工单记录
        User user = getUser(request);
        return workService.urgeCommon(uniqueCode, workRecord, user, curDate, WorkConstants.FTU_WEB);
    }

    /**
     * @auther: liudd
     * @date: 2020/4/2 13:31
     * 功能描述:回单
     */
    @RequestMapping(value = "/over", method = RequestMethod.POST)
    public @ResponseBody JsonResult over(@RequestBody WorkRecord workRecord, HttpServletRequest request){
        Date curDate = new Date();
        String uniqueCode = getUniqueCode();
        User user = getUser(request);
        return workService.overCommon(uniqueCode, workRecord, user, curDate, WorkConstants.FTU_WEB);
    }

    /**
     * 撤单
     * @param workRecord
     * @param request
     * @return
     */
    @RequestMapping(value = "/cancel", method = RequestMethod.POST)
    public @ResponseBody
    JsonResult cancel(@RequestBody WorkRecord workRecord, HttpServletRequest request){
        Date curDate = new Date();
        String uniqueCode = getUniqueCode();
        User user = getUser(request);
        return workService.cancelCommon(uniqueCode, workRecord, user, curDate, WorkConstants.FTU_WEB);
    }

    /**
     * @auther: liudd
     * @date: 2020/4/2 14:06
     * 功能描述:告警工单详情
     */
    @RequestMapping(value = "/details", method = RequestMethod.POST)
    public @ResponseBody JsonResult details(@RequestBody WorkRecord workRecord) {
        String workId = workRecord.getWorkId();
        String uniqueCode = getUniqueCode();
        return workService.detailCommon(uniqueCode, workId);
    }

    /**
     * @auther: liudd
     * @date: 2020/4/2 14:06
     * 功能描述:导出
     */
    @RequestMapping(value = "/export", method = RequestMethod.POST)
    public void export(@RequestBody WorkQuery workQuery, HttpServletResponse response) {
        String uniqueCode = getUniqueCode();
        workQuery.setCurrentPage(1);
        workQuery.setPageSize(Integer.MAX_VALUE);
        List<Work> list = workService.list(uniqueCode, workQuery);
        List<Work> realList = new ArrayList<>();
        for(Work work : list){
            List<WorkAlarm> workAlarmList = work.getWorkAlarmList();
            for(WorkAlarm workAlarm : workAlarmList){
                Work srcWork = new Work();
                srcWork.setId(work.getId());
                srcWork.setCode(work.getCode());
                srcWork.setSendType(work.getSendType());
                srcWork.setState(work.getState());
                srcWork.setSentTime(work.getSentTime());
                srcWork.setTaskTime(work.getTaskTime());
                srcWork.setSite(work.getSite());
                srcWork.setAlarmTime(workAlarm.gettReport());
                srcWork.setAlarmStatus(workAlarm.getState());
                srcWork.setAlarmLevel(workAlarm.getLevel());
                srcWork.setAlarmName(workAlarm.getAlarmName());
                srcWork.setWorker(work.getWorker());
                realList.add(srcWork);
            }
        }
        String title = "工单列表";
        String[] headsName = { "流水号","派单类型","工单状态","告警时间", "派单时间","任务时限", "站点名称",
                "告警状态","告警级别","告警名称", "接单人"};
        String[] properiesName = { "code", "sendType", "status" ,"alarmTime","sentTime", "taskTime", "site",
                "alarmStatus","alarmLevel", "alarmName", "worker"};
        export(response, realList, properiesName, headsName, title);
    }
}
