package com.kongtrolink.framework.scloud.controller;

import com.kongtrolink.framework.core.entity.User;
import com.kongtrolink.framework.core.entity.session.BaseController;
import com.kongtrolink.framework.entity.JsonResult;
import com.kongtrolink.framework.entity.ListResult;
import com.kongtrolink.framework.exception.ParameterException;
import com.kongtrolink.framework.scloud.constant.BaseConstant;
import com.kongtrolink.framework.scloud.entity.*;
import com.kongtrolink.framework.scloud.entity.model.DeviceModel;
import com.kongtrolink.framework.scloud.entity.model.SiteModel;
import com.kongtrolink.framework.scloud.query.*;
import com.kongtrolink.framework.scloud.service.*;
import com.kongtrolink.framework.scloud.service.impl.DeviceServiceImpl;
import com.kongtrolink.framework.scloud.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
@Controller
@RequestMapping("/alarmFocus")
public class AlarmFocusController extends BaseController {

    @Autowired
    AlarmFocusService alarmFocusService;
    @Autowired
    DeviceService deviceService;
    @Autowired
    SiteService siteService;
    @Autowired
    DeviceSignalTypeService deviceSignalTypeService;
    @Autowired
    AlarmService alarmService;
    @Autowired
    AlarmBusinessService businessService;

    private static final Logger LOGGER = LoggerFactory.getLogger(AlarmFocusController.class);
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
        if (null != user) {
            alarmFocus.setUserId(user.getId());
            alarmFocus.setUsername(user.getUsername());
        }else{
            alarmFocus.setUserId("root");
            alarmFocus.setUsername("超级管理员");
        }
        alarmFocus.initEntDevSig();
        String uniqueCode = alarmFocus.getEnterpriseCode();
        alarmFocus.setFocusTime(curTime);
        boolean result = alarmFocusService.add(uniqueCode, alarmFocus);
        if(result){
            return new JsonResult("关注成功", true);
        }
        return new JsonResult("关注失败", false);
    }

    @RequestMapping("/delete")
    @ResponseBody
    public JsonResult delete(@RequestBody AlarmFocus alarmFocus){
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
        if(null != user) {
            alarmFocusQuery.setUserId(user.getId());
        }
        List<AlarmFocus> alarmFocusList = alarmFocusService.list(uniqueCode, alarmFocusQuery);
        int count = alarmFocusService.count(uniqueCode, alarmFocusQuery);
        //填充站点等信息
//        List<Integer> siteIdList = new ArrayList<>();
//        Map<Integer, List<AlarmFocus>> siteIdFocusListMap = new HashMap<>();
//        List<String> deviceCodeList = new ArrayList<>();
//        Map<String, List<AlarmFocus>> deviceCodeFocusListMap = new HashMap<>();
//        for(AlarmFocus alarmFocus : alarmFocusList){
//            int siteId = alarmFocus.getSiteId();
//            if(!siteIdList.contains(siteId)){
//                siteIdList.add(siteId);
//            }
//            List<AlarmFocus> siteIdFocusList = siteIdFocusListMap.get(siteId);
//            if(null == siteIdFocusList){
//                siteIdFocusList = new ArrayList<>();
//            }
//            siteIdFocusList.add(alarmFocus);
//            siteIdFocusListMap.put(siteId, siteIdFocusList);
//
//            String deviceId = alarmFocus.getDeviceId();
//            if(!deviceCodeList.contains(deviceId)){
//                deviceCodeList.add(deviceId);
//            }
//            List<AlarmFocus> deviceCodeFocusList = deviceCodeFocusListMap.get(deviceId);
//            if(null == deviceCodeFocusList){
//                deviceCodeFocusList = new ArrayList<>();
//            }
//            deviceCodeFocusList.add(alarmFocus);
//            deviceCodeFocusListMap.put(deviceId, deviceCodeFocusList);
//        }
//        SiteQuery siteQuery = new SiteQuery();
//        siteQuery.setPageSize(Integer.MAX_VALUE);
//        siteQuery.setSiteIdList(siteIdList);
//        List<SiteModel> siteModelList = siteService.findSiteList(uniqueCode, siteQuery);
//        if(null != siteModelList){
//            for(SiteModel siteModel : siteModelList){
//                int siteId = siteModel.getSiteId();
//                List<AlarmFocus> siteIdFocusList = siteIdFocusListMap.get(siteId);
//                if(null != siteIdFocusList){
//                    for(AlarmFocus alarmFocus : siteIdFocusList){
//                        alarmFocus.initSiteInfo(siteModel);
//                    }
//                }
//            }
//        }
//        DeviceQuery deviceQuery = new DeviceQuery();
//        deviceQuery.setCurrentPage(1);
//        deviceQuery.setPageSize(Integer.MAX_VALUE);
//        deviceQuery.setDeviceCodes(deviceCodeList);
//        List<DeviceModel> deviceModelList = new ArrayList<>();
//        try{
//            deviceModelList = deviceService.findDeviceList(uniqueCode, deviceQuery);
//        }catch (Exception e){
//            e.printStackTrace();
//            LOGGER.error("获取设备列表异常---" );
//        }
//        if(null != deviceModelList){
//            for(DeviceModel deviceModel : deviceModelList){
//                List<AlarmFocus> deviceCodeFocusList = deviceCodeFocusListMap.get(deviceModel.getCode());
//                if(null != deviceCodeFocusList){
//                    for(AlarmFocus alarmFocus : deviceCodeFocusList){
//                        alarmFocus.initDeviceInfo(deviceModel);
//                    }
//                }
//            }
//        }
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
        String uniqueCode = getUniqueCode();
        //获取当前用户关注的信号点
        User user = getUser(request);
        if(null != user) {
            focusQuery.setUserId(user.getId());
        }
        List<AlarmFocus> alarmFocusList = alarmFocusService.list(uniqueCode, focusQuery);
        Map<String, AlarmFocus> entDevSigListFocusMap = new HashMap<>();
        List<String> entDevSigList = new ArrayList<>();
        for(AlarmFocus alarmFocus : alarmFocusList){
            String entDevSig = alarmFocus.initEntDevSig();
            if(!entDevSigList.contains(entDevSig)){
                entDevSigList.add(entDevSig);
            }
            entDevSigListFocusMap.put(entDevSig, alarmFocus);
        }
        AlarmBusinessQuery businessQuery = new AlarmBusinessQuery();
        businessQuery.setEnterpriseCode(focusQuery.getEnterpriseCode());
        businessQuery.setServerCode(focusQuery.getServerCode());
        businessQuery.setCurrentPage(focusQuery.getCurrentPage());
        businessQuery.setPageSize(focusQuery.getPageSize());
        businessQuery.setSkipSize(focusQuery.getPageSize());
        businessQuery.setEntDevSigList(entDevSigList);
        businessQuery.setState(BaseConstant.ALARM_STATE_PENDING);
        businessQuery.setSiteCodeList(focusQuery.getSiteCodeList());
        businessQuery.setOperatorId(focusQuery.getUserId());
        List<AlarmBusiness> list = businessService.list(uniqueCode, businessQuery);
        for(AlarmBusiness alarmBusiness : list){
            String entDevSig = alarmBusiness.getEntDevSig();
            AlarmFocus alarmFocus = entDevSigListFocusMap.get(entDevSig);
            if(null != alarmFocus){
                alarmBusiness.setFocusId(alarmFocus.getId());
            }
        }
        int count = businessService.count(uniqueCode, businessQuery);
        ListResult<AlarmBusiness> listResult = new ListResult<>(list, count);
        JsonResult jsonResult =new JsonResult(listResult);
        return jsonResult;
    }
}
