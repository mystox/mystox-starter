package com.kongtrolink.framework.scloud.service.impl;

import com.kongtrolink.framework.core.entity.User;
import com.kongtrolink.framework.entity.JsonResult;
import com.kongtrolink.framework.scloud.constant.BaseConstant;
import com.kongtrolink.framework.scloud.constant.WorkConstants;
import com.kongtrolink.framework.scloud.dao.WorkDao;
import com.kongtrolink.framework.scloud.entity.*;
import com.kongtrolink.framework.scloud.push.JPushService;
import com.kongtrolink.framework.scloud.query.WorkQuery;
import com.kongtrolink.framework.scloud.service.WorkRecordService;
import com.kongtrolink.framework.scloud.service.WorkService;
import com.kongtrolink.framework.scloud.util.StringUtil;
import com.kongtrolink.framework.service.MqttOpera;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.util.*;

/**
 * @Auther: liudd
 * @Date: 2020/4/1 13:27
 * @Description:
 */
@Service
public class WorkServiceImpl implements WorkService{

    @Autowired
    WorkDao workDao;
    @Autowired
    WorkRecordService recordService;
    @Autowired
    MqttOpera mqttOpera;

    @Value("${push.app.pushKey:null}")
    private String appPushKey;
    @Value("${push.app.pushSecret:null}")
    private String appPushSecret;
    @Value("${push.app.pushProduct:false}")
    private boolean appProduct;
    @Value("${push.app.keyName:null}")
    private String appKeyName;      //type
    @Value("${push.app.proName:null}")
    private String appProName;      //workId

    @Override
    public void add(String uniqueCode, Work work) {
        workDao.add(uniqueCode, work);
    }

    @Override
    public boolean delete(String uniqueCode, String workId) {
        return workDao.delete(uniqueCode, workId);
    }

    @Override
    public boolean update(String uniqueCode, Work work) {
        if(delete(uniqueCode, work.getId())){
            add(uniqueCode, work);
            return true;
        }
        return false;
    }

    @Override
    public Work getById(String uniqueCode, String workId) {
        return workDao.getById(uniqueCode, workId);
    }

    @Override
    public List<Work> list(String uniqueCode, WorkQuery workQuery) {
        return workDao.list(uniqueCode, workQuery);
    }

    @Override
    public int count(String uniqueCode, WorkQuery workQuery) {
        return workDao.count(uniqueCode, workQuery);
    }

    @Override
    public Work getNotOverByDeviceCode(String uniqueCode, String deviceCode) {
        return workDao.getNotOverByDeviceCode(uniqueCode, deviceCode);
    }

    @Override
    public Work createWork(WorkQuery workQuery, WorkConfig workConfig, String sendType) {
        Work work = new Work();
        work.setCode(StringUtil.createCodeByDate(new Date()));
        Date curDate = new Date();
        work.setSentTime(curDate);
        work.setOperatorTime(curDate);
        work.setSendType(sendType);
        //site信息
        work.setSite(new FacadeView(workQuery.getSiteCode(), workQuery.getSiteName()));
        work.setSiteType(workQuery.getSiteType());
        //设备信息
        work.setDevice(new FacadeView(workQuery.getDeviceCode(), workQuery.getDeviceName()));
        work.setDeviceType(workQuery.getDeviceType());
        work.setState(WorkConstants.STATE_RECEIVE);
        work.initCSJDXX(curDate, workConfig);
        work.initCSHDXX(curDate, workConfig);
        work.increateAlarm(workQuery.getWorkAlarm());
        return work;
    }

    /**
     * @param uniqueCode
     * @param user
     * @auther: liudd
     * @date: 2020/4/2 12:23
     * 功能描述:接单公共部分
     */
    @Override
    public JsonResult receCommon(String uniqueCode, Work work, User user, Date curTime, String FTU) {
        FacadeView operator = new FacadeView(user.getUsername(), user.getName(), user.getPhone());
        //上次操作时间，用于计算处理时间
        Date beforOperatorTime = work.getOperatorTime();
        float handleTime = (curTime.getTime() - beforOperatorTime.getTime()) / (1000*60);
        work.setReceTime(curTime);
        work.setOperatorTime(curTime);
        work.setState(WorkConstants.STATE_HANDLER);
        work.setWorker(operator);
        work.setReceTime(curTime);
        //计算超时接单时间，超时回单时间
        work.countCSJDSJ(curTime);
        //修改工单的操作信息（操作时间， 状态）
        update(uniqueCode, work);
        WorkRecord workRecord = recordService.createWorkRecord(work, operator, WorkConstants.OPERATE_RECE,
                null, curTime, handleTime, FTU, null);
        recordService.add(uniqueCode, workRecord);
        return new JsonResult("接单成功", true);
    }

    /**
     * @param uniqueCode
     * @param curDate
     * @param FTU
     * @auther: liudd
     * @date: 2020/4/2 13:19
     * 功能描述:转派公共部分
     */
    @Override
    public JsonResult redeployCommon(String uniqueCode, Work work, WorkRecord workRecord,  User user, Date curDate, String FTU) {
        Date beforOperatorTime = work.getOperatorTime();
        //设置处理时间
        float handleTime = (curDate.getTime() - beforOperatorTime.getTime()) / (1000*60);
        work.setOperatorTime(curDate);
        work.setState(WorkConstants.STATE_RECEIVE);
        work.setWorker(workRecord.getReceiver());
        update(uniqueCode, work);

        //设置操作类型为转派
        workRecord.setWorker(new FacadeView(user.getUsername(), user.getName(), user.getPhone()));
        workRecord.setOperateType(WorkConstants.OPERATE_REDE);
        workRecord.setOperateTime(curDate);
        workRecord.setHandleTime(handleTime);
        workRecord.setOperateFTU(FTU);
        recordService.add(uniqueCode, workRecord);
        return new JsonResult("转派成功", true);
    }

    @Override
    public JsonResult urgeCommon(String uniqueCode, Work work, WorkRecord workRecord, User user, Date curDate, String FTU) {
        Date beforOperatorTime = work.getOperatorTime();
        //设置处理时间
        float handleTime = (curDate.getTime() - beforOperatorTime.getTime()) / (1000*60);
        workRecord.setWorker(new FacadeView(user.getUsername(), user.getName(), user.getPhone()));
        //设置操作类型为催单
        workRecord.setOperateType(WorkConstants.OPERATE_URGE);
        workRecord.setOperateTime(curDate);
        workRecord.setHandleTime(handleTime);
        workRecord.setOperateFTU(FTU);
        workRecord.setReceiver(work.getWorker());
        recordService.add(uniqueCode, workRecord);
        return new JsonResult("催单成功", true);
    }

    @Override
    public JsonResult overCommon(String uniqueCode, Work work, WorkRecord workRecord, User user, Date curDate, String FTU) {
        Date beforOperatorTime = work.getOperatorTime();
        float handleTime = (curDate.getTime() - beforOperatorTime.getTime()) / (1000*60);
        workRecord.setWorker(new FacadeView(user.getUsername(), user.getName(), user.getPhone()));
        workRecord.setOperateType(WorkConstants.OPERATE_OVER);
        workRecord.setOperateTime(curDate);
        workRecord.setHandleTime(handleTime);
        workRecord.setOperateFTU(FTU);

        if(WorkConstants.STATE_UNDOWN.equals(workRecord.getHandleResult())){//未完成
            //修改操作类型为反馈信息
            workRecord.setOperateType(WorkConstants.OPERATE_FEED);
            recordService.add(uniqueCode, workRecord);
            return new JsonResult("反馈成功", true);
        }else if(WorkConstants.STATE_OVER.equals(work.getState())){ //已回单的工单只能作为回单补充
            workRecord.setOperateType(WorkConstants.OPERATE_REPL);
        }else{ //已完成，需要验证该设备的告警是否全部消除
            if(WorkConstants.ALARM_STATE_PENDING.equals(work.getAlarmState())){
                return new JsonResult("回单失败，该工单有告警未消除", false);
            }
            work.setOperatorTime(curDate);
            work.setState(WorkConstants.STATE_OVER);
            work.setWorker(new FacadeView(user.getUsername(), user.getName(), user.getPhone()));
            work.setBackTime(curDate);
            work.countCSHDSJ(curDate);
            update(uniqueCode, work);
        }
        recordService.add(uniqueCode, workRecord);
        return new JsonResult("回单成功", true);
    }

    @Override
    public JsonResult cancelCommon(String uniqueCode, Work work, WorkRecord workRecord, User user, Date curDate, String FTU) {
        work.setState(WorkConstants.STATE_CANCEL);
        work.setOperatorTime(curDate);
        update(uniqueCode, work);

        Date beforOperatorTime = work.getOperatorTime();
        float handleTime = (curDate.getTime() - beforOperatorTime.getTime()) / (1000*60);
        workRecord.setWorker(new FacadeView(user.getUsername(), user.getName(), user.getPhone()));
        workRecord.setOperateType(WorkConstants.OPERATE_BACK);
        workRecord.setOperateTime(curDate);
        workRecord.setHandleTime(handleTime);
        workRecord.setOperateFTU(FTU);
        recordService.add(uniqueCode, workRecord);
        return new JsonResult("撤单成功", true);
    }

    @Override
    public JsonResult detailCommon(String uniqueCode, String workId) {
        Work work = getById(uniqueCode, workId);
        //获取该工单的处理记录
        List<WorkRecord> workRecordList = recordService.getListByWorkId(uniqueCode, workId);
        work.setWorkRecordList(workRecordList);
        return new JsonResult(work);
    }

    /**
     * @param uniqueCode
     * @auther: liudd
     * @date: 2020/4/2 17:18
     * 功能描述:告警消除，联动修改工单
     */
    @Override
    public void resolveAlarm(String uniqueCode, AlarmBusiness business) {
        Work work = workDao.getByKey(uniqueCode, business.getKey());
        if(null == work){
            return;
        }
        List<WorkAlarm> workAlarmList = work.getWorkAlarmList();
        for(WorkAlarm workAlarm : workAlarmList) {
            if (workAlarm.getAlarmKey().equals(business.getKey())) {
                workAlarm.setState(WorkConstants.ALARM_STATE_RESOLVED);
                workAlarm.setTreport(business.getTrecover());
                work.decreateAlarm();
            }
        }
        workDao.updateAlarmInfo(uniqueCode, work);
    }

    @Override
    public PushEntity createJpush(Work work, String operate, List<String> accountList) {
        String title = "[工单通告--"+ operate +"]";
        String content = "您有一条工单："+work.getDevice().getName() + "设备，请及时处理" ;
        PushEntity pushEntity = new PushEntity(title, content, accountList, BaseConstant.TEMPLATE_APP, appPushKey, appPushSecret, appProduct);
        pushEntity.setKeyName(appKeyName);
        pushEntity.setKeyValue(work.getId());
        pushEntity.setProName(appProName);
        pushEntity.setProValue(BaseConstant.JPUSH_TYPE_WORK);
        return pushEntity;
    }
}
