package com.kongtrolink.framework.scloud.controller;

import com.alibaba.fastjson.JSONObject;
import com.kongtrolink.framework.core.entity.User;
import com.kongtrolink.framework.core.entity.session.BaseController;
import com.kongtrolink.framework.entity.JsonResult;
import com.kongtrolink.framework.entity.ListResult;
import com.kongtrolink.framework.entity.MsgResult;
import com.kongtrolink.framework.exception.ParameterException;
import com.kongtrolink.framework.scloud.entity.*;
import com.kongtrolink.framework.scloud.entity.model.DeviceModel;
import com.kongtrolink.framework.scloud.entity.model.SiteModel;
import com.kongtrolink.framework.scloud.query.AlarmFocusQuery;
import com.kongtrolink.framework.scloud.query.AlarmQuery;
import com.kongtrolink.framework.scloud.service.*;
import com.kongtrolink.framework.scloud.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import javax.servlet.http.HttpServletRequest;
import java.util.*;

/**
 * @Auther: liudd
 * @Date: 2020/3/3 10:24
 * @Description:
 */
@Controller("alarmFocus")
public class AlarmFocusController extends BaseController {

    @Autowired
    AlarmFocusService alarmFocusService;
    @Autowired
    DeviceService deviceService;
    @Autowired
    SiteService siteService;
    @Autowired
    SignalService signalService;
    @Autowired
    AlarmService alarmService;

    /**
     * @auther: liudd
     * @date: 2020/2/26 15:21
     * 功能描述:关注/取消关注告警
     */
    @RequestMapping("/add")
    @ResponseBody
    public JsonResult add(@RequestBody AlarmFocus alarmFocus, HttpServletRequest request){
        Date curTime = new Date();
        User user = getUser(request);
        String uniqueCode = alarmFocus.getEnterpriseCode();
        try {
            //判定参数
            if (StringUtil.isNUll(uniqueCode)) {
                throw new ParameterException("企业编码为空");
            }
            if(StringUtil.isNUll(alarmFocus.getDeviceId())){
                throw new ParameterException("设备编码为空");
            }
            if(StringUtil.isNUll(alarmFocus.getSignalId())){
                throw new ParameterException("信号点id为空");
            }
            if (null != user) {
                alarmFocus.setUserId(user.getId());
                alarmFocus.setUsername(user.getUsername());
            }
            alarmFocus.setFocusTime(curTime);
            //填充信号点名称，设备名称，站点等信息
            DeviceModel deviceModel = deviceService.getByCode(uniqueCode, alarmFocus.getDeviceId());
            if(null == deviceModel){
                throw new ParameterException("设备编码" + alarmFocus.getDeviceId()+ "对应的设备不存在");
            }
            alarmFocus.initDeviceInfo(deviceModel);
            //获取设备类型
            DeviceType deviceType = signalService.getByCode(uniqueCode, deviceModel.getTypeCode());
            if(null == deviceType){
                throw new ParameterException("设备类型" + deviceModel.getTypeCode() + "不存在");
            }
            SignalType signalType = deviceType.getSignalTypeByCntbId(alarmFocus.getSignalId());
            if(null == signalType){
                throw new ParameterException("信号点" + alarmFocus.getSignalId() + "不存在");
            }
            alarmFocus.initSignalInfo(signalType);
            boolean result = alarmFocusService.add(uniqueCode, alarmFocus);
            if(result){
                return new JsonResult("关注成功", true);
            }
        }catch (ParameterException e){
            return new JsonResult(e.getMessage(), false);
        }
        return new JsonResult("关注成功", true);
    }

    @RequestMapping("/delete")
    @ResponseBody
    public JsonResult delete(@RequestBody AlarmFocus alarmFocus, HttpServletRequest request){
        String id = alarmFocus.getId();
        String uniqueCode = getUniqueCode();
        boolean delResult = alarmFocusService.delete(uniqueCode, id);
        if(delResult){
            return new JsonResult("取消关注成功", true);
        }
        return new JsonResult("取消关注失败", false);
    }

    @RequestMapping("/list")
    @ResponseBody
    public JsonResult list(@RequestBody AlarmFocusQuery alarmFocusQuery, HttpServletRequest request){
        String uniqueCode = getUniqueCode();
        User user = getUser(request);
        alarmFocusQuery.setUserId(user.getId());
        List<AlarmFocus> alarmFocusList = alarmFocusService.list(uniqueCode, alarmFocusQuery);
        int count = alarmFocusService.count(uniqueCode, alarmFocusQuery);
        //填充站点等信息
        List<Integer> siteIdList = new ArrayList<>();
        Map<Integer, List<AlarmFocus>> siteIdFocusListMap = new HashMap<>();
        List<String> deviceCodeList = new ArrayList<>();
        Map<String, List<AlarmFocus>> deviceCodeFocusListMap = new HashMap<>();
        for(AlarmFocus alarmFocus : alarmFocusList){
            int siteId = alarmFocus.getSiteId();
            if(!siteIdList.contains(siteId)){
                siteIdList.add(siteId);
            }
            List<AlarmFocus> siteIdFocusList = siteIdFocusListMap.get(siteId);
            if(null == siteIdFocusList){
                siteIdFocusList = new ArrayList<>();
            }
            siteIdFocusList.add(alarmFocus);
            siteIdFocusListMap.put(siteId, siteIdFocusList);

            String deviceId = alarmFocus.getDeviceId();
            if(!deviceCodeList.contains(deviceId)){
                deviceCodeList.add(deviceId);
            }
            List<AlarmFocus> deviceCodeFocusList = deviceCodeFocusListMap.get(deviceId);
            if(null == deviceCodeFocusList){
                deviceCodeFocusList = new ArrayList<>();
            }
            deviceCodeFocusList.add(alarmFocus);
            deviceCodeFocusListMap.put(deviceId, deviceCodeFocusList);
        }
        List<SiteModel> siteModelList = siteService.getByIdList(uniqueCode, siteIdList);
        if(null != siteModelList){
            for(SiteModel siteModel : siteModelList){
                int siteId = siteModel.getSiteId();
                List<AlarmFocus> siteIdFocusList = siteIdFocusListMap.get(siteId);
                if(null != siteIdFocusList){
                    for(AlarmFocus alarmFocus : siteIdFocusList){
                        alarmFocus.initSiteInfo(siteModel);
                    }
                }
            }
        }
        List<DeviceModel> deviceModelList = deviceService.getByCodeList(uniqueCode, deviceCodeList);
        if(null != deviceModelList){
            for(DeviceModel deviceModel : deviceModelList){
                List<AlarmFocus> deviceCodeFocusList = deviceCodeFocusListMap.get(deviceModel.getCode());
                if(null != deviceCodeFocusList){
                    for(AlarmFocus alarmFocus : deviceCodeFocusList){
                        alarmFocus.initDeviceInfo(deviceModel);
                    }
                }
            }
        }
        ListResult<AlarmFocus> listResult = new ListResult<>(alarmFocusList, count);
        return new JsonResult(listResult);
    }

    /**
     * @auther: liudd
     * @date: 2020/3/3 14:21
     * 功能描述:获取当前用户关注的信号点所在告警(实时告警)
     */
    @RequestMapping("/getFocusAlarmList")
    @ResponseBody
    public JsonResult getFocusAlarmList(@RequestBody AlarmFocusQuery focusQuery, HttpServletRequest request){
        User user = getUser(request);
        String uniqueCode = getUniqueCode();
        //获取当前用户关注的信号点
        focusQuery.setUserId(user.getId());
        List<AlarmFocus> alarmFocusList = alarmFocusService.list(uniqueCode, focusQuery);
        List<String> entDevSigList = new ArrayList<>();
        for(AlarmFocus alarmFocus : alarmFocusList){
            String entDevSig = alarmFocus.initEntDevSig();
            if(!entDevSigList.contains(entDevSig)){
                entDevSigList.add(entDevSig);
            }
        }
        AlarmQuery alarmQuery = new AlarmQuery();
        alarmQuery.setCurrentPage(focusQuery.getCurrentPage());
        alarmQuery.setPageSize(focusQuery.getPageSize());
        alarmQuery.setEnterpriseCode(focusQuery.getEnterpriseCode());
        alarmQuery.setServerCode(focusQuery.getServerCode());
        alarmQuery.setType(focusQuery.getType());
        alarmQuery.setEntDevSigList(entDevSigList);
        try {
            //具体查询历史还是实时数据，由中台告警模块根据参数判定
            JsonResult jsonResult = alarmService.list(alarmQuery);
            return jsonResult;
        }catch (ParameterException paraException){
            return new JsonResult(paraException.getMessage(), false);
        }catch (Exception e) {
            return new JsonResult(e.getMessage(), false);
        }
    }
}
