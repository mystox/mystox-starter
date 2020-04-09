package com.kongtrolink.framework.scloud.controller;

import com.kongtrolink.framework.core.entity.User;
import com.kongtrolink.framework.entity.JsonResult;
import com.kongtrolink.framework.scloud.controller.base.ExportController;
import com.kongtrolink.framework.scloud.dao.HomePageMongo;
import com.kongtrolink.framework.scloud.entity.SiteEntity;
import com.kongtrolink.framework.scloud.entity.model.home.HomeFsuNumber;
import com.kongtrolink.framework.scloud.entity.model.home.HomeQuery;
import com.kongtrolink.framework.scloud.entity.model.home.HomeWorkModel;
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
     * 首页 - 站点数量(交维态/所有)
     * @param homeQuery 查询参数
     */
    @RequestMapping(value = "/siteNum", method = RequestMethod.POST)
    public @ResponseBody JsonResult getSiteNum(@RequestBody HomeQuery homeQuery) {
        try{
            String uniqueCode = getUniqueCode();
            String userId = getUserId();
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
            //统计交维态的站点总数
            List<SiteEntity> list = homePageService.getHomeSiteList(uniqueCode,userId,homeQuery);
            return new JsonResult(list);
        }catch (Exception e){
            e.printStackTrace();
        }
        return new JsonResult("查询失败",false);

    }

    /**
     * 告警工单统计
     *
     */
    @RequestMapping(value = "/work", method = RequestMethod.POST)
    public @ResponseBody JsonResult work(@RequestBody HomeQuery homeQuery) {
        try{
            String uniqueCode = getUniqueCode();
            String userId = getUserId();
            //统计交维态的站点总数
            List<HomeWorkModel> list = homePageService.getHomeWorkModel(uniqueCode,userId,homeQuery);
            int total = 0;
            if(list !=null){
                for(HomeWorkModel workModel:list){
                    total = total + workModel.getCount();
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


}
