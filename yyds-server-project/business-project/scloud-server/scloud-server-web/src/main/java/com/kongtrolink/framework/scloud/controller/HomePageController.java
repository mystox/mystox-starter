package com.kongtrolink.framework.scloud.controller;

import com.kongtrolink.framework.core.entity.User;
import com.kongtrolink.framework.entity.JsonResult;
import com.kongtrolink.framework.scloud.constant.WorkConstants;
import com.kongtrolink.framework.scloud.controller.base.ExportController;
import com.kongtrolink.framework.scloud.dao.HomePageMongo;
import com.kongtrolink.framework.scloud.entity.SiteEntity;
import com.kongtrolink.framework.scloud.entity.model.SiteModel;
import com.kongtrolink.framework.scloud.entity.model.home.*;
import com.kongtrolink.framework.scloud.query.SiteQuery;
import com.kongtrolink.framework.scloud.service.HomePageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @author Mag
 **/
@Controller
@RequestMapping(value = "/home", method = RequestMethod.POST)
public class HomePageController extends ExportController {
    @Autowired
    private HomePageService homePageService;
    /**
     * 首页 - 地图
     * @param homeQuery 查询参数
     */
    @RequestMapping(value = "/siteMap", method = RequestMethod.POST)
    public @ResponseBody JsonResult getHomeSiteAlarmMap(@RequestBody HomeQuery homeQuery) {
        try{
            String uniqueCode = getUniqueCode();
            String userId = getUserId();
            homeQuery.setCurrentRoot(isCurrentRoot());
            List<HomeSiteAlarmMap> list = homePageService.getHomeSiteAlarmMap(uniqueCode,userId,homeQuery);
            return new JsonResult(list);
        }catch (Exception e){
            e.printStackTrace();
        }
        return new JsonResult("查询失败",false);

    }
    /**
     * 首页 - 站点数量(交维态/所有)
     * @param homeQuery 查询参数
     */
    @RequestMapping(value = "/siteNum", method = RequestMethod.POST)
    public @ResponseBody JsonResult getSiteNum(@RequestBody HomeQuery homeQuery) {
        try{
            String uniqueCode = getUniqueCode();
            String userId = getUserId();
            homeQuery.setCurrentRoot(isCurrentRoot());
            //统计交维态的站点总数
            HomeFsuNumber homeFsuNumber = homePageService.getHomeFsuNumber(uniqueCode,userId,homeQuery);
            return new JsonResult(homeFsuNumber);
        }catch (Exception e){
            e.printStackTrace();
        }
        return new JsonResult("查询失败",false);

    }
    /**
     * 首页 - 站点列表
     * @param homeQuery 查询参数
     */
    @RequestMapping(value = "/siteList", method = RequestMethod.POST)
    public @ResponseBody JsonResult getSiteList(@RequestBody HomeQuery homeQuery) {
        try{
            String uniqueCode = getUniqueCode();
            String userId = getUserId();
            homeQuery.setCurrentRoot(isCurrentRoot());
            //统计交维态的站点总数
            List<HomeSiteAlarmMap> list = homePageService.getHomeSiteList(uniqueCode,userId,homeQuery);
            return new JsonResult(list);
        }catch (Exception e){
            e.printStackTrace();
        }
        return new JsonResult("查询失败",false);

    }

    /**
     * 首页 - 实时告警统计
     * 只统计交维态数量
     * @param homeQuery 查询参数
     */
    @RequestMapping(value = "/alarmNum", method = RequestMethod.POST)
    public @ResponseBody JsonResult getHomeAlarmLevelNum(@RequestBody HomeQuery homeQuery,HttpServletRequest request) {
        try{
            String uniqueCode = getUniqueCode();
            String userId = getUserId();
            homeQuery.setCurrentRoot(isCurrentRoot());
            HomeAlarmLevelNum value = homePageService.getHomeAlarmLevelNum(uniqueCode,userId,homeQuery);
            return new JsonResult(value);
        }catch (Exception e){
            e.printStackTrace();
        }
        return new JsonResult("查询失败",false);
    }

    /**
     * 告警工单统计
     * 未接： state 待接
     * 在途工单: state 待办 待审批
     * 超时工单:  isOverTime 是/否 超时工单
     * 历史工单：state 已完成 已撤销
     */
    @RequestMapping(value = "/work", method = RequestMethod.POST)
    public @ResponseBody JsonResult work(@RequestBody HomeQuery homeQuery) {
        try{
            String uniqueCode = getUniqueCode();
            String userId = getUserId();
            homeQuery.setCurrentRoot(isCurrentRoot());
            HomeWorkDto list = homePageService.getHomeWorkModel(uniqueCode,userId,homeQuery);
            return  new JsonResult(list);
        }catch (Exception e){
            e.printStackTrace();
        }
        return new JsonResult("查询失败",false);

    }
    /**
     * FSU在线状态统计 交维态FSU设备的实时在线情况百分比
     */
    @RequestMapping(value = "/fsuOnline", method = RequestMethod.POST)
    public @ResponseBody JsonResult getHomeFsuOnlineModel(@RequestBody HomeQuery homeQuery) {
        try{
            String uniqueCode = getUniqueCode();
            String userId = getUserId();
            homeQuery.setCurrentRoot(isCurrentRoot());
            //统计交维态的站点总数
            List<HomeFsuOnlineModel> list = homePageService.getHomeFsuOnlineModel(uniqueCode,userId,homeQuery);
            int total = 0;
            if(list !=null){
                for(HomeFsuOnlineModel fsuOnlineModel:list){
                    total = total + fsuOnlineModel.getCount();
                }
            }
            JsonResult value = new JsonResult(list);
            value.setCount(total);
            return value;
        }catch (Exception e){
            e.printStackTrace();
        }
        return new JsonResult("查询失败",false);

    }

    @RequestMapping(value = "/siteModel", method = RequestMethod.POST)
    public @ResponseBody JsonResult getSiteModel(@RequestBody SiteQuery siteQuery) {
        try{
            String uniqueCode = getUniqueCode();
            siteQuery.setCurrentRoot(isCurrentRoot());
            siteQuery.setUserId(getUserId());
            //统计交维态的站点总数
            SiteModel siteModel = homePageService.getSiteModel(uniqueCode,siteQuery);
            return new JsonResult(siteModel);
        }catch (Exception e){
            e.printStackTrace();
        }
        return new JsonResult("查询失败",false);
    }


}
