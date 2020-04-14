package com.kongtrolink.framework.scloud.controller;

import com.kongtrolink.framework.core.entity.User;
import com.kongtrolink.framework.core.entity.session.BaseController;
import com.kongtrolink.framework.entity.JsonResult;
import com.kongtrolink.framework.scloud.entity.ProjectOrderEntity;
import com.kongtrolink.framework.scloud.entity.ProjectOrderLogEntity;
import com.kongtrolink.framework.scloud.entity.model.ProjectOrderModel;
import com.kongtrolink.framework.scloud.query.ProjectOrderQuery;
import com.kongtrolink.framework.scloud.service.ProjectService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.List;

/**
 * 工程管理 控制器
 * Created by Eric on 2020/4/13.
 */
@Controller
@RequestMapping(value = "/project/", method = RequestMethod.POST)
public class ProjectController extends BaseController{

    @Autowired
    ProjectService projectService;

    private String uniqueCode = "YYDS"; //写死，为了自测用
    private static final Logger LOGGER = LoggerFactory.getLogger(ProjectController.class);

    /**
     * 获取测试单列表
     */
    @RequestMapping(value = "getProjectOrderList", method = RequestMethod.POST)
    public @ResponseBody JsonResult getProjectOrderList(@RequestBody ProjectOrderQuery projectOrderQuery){
        try{
//            String uniqueCode = getUniqueCode();
            List<ProjectOrderModel> list = projectService.getProjectOrderList(uniqueCode, projectOrderQuery);

            return new JsonResult(list);
        }catch (Exception e){
            e.printStackTrace();
            return new JsonResult("获取测试单列表异常", false);
        }
    }

    /**
     * 创建测试单
     */
    @RequestMapping(value = "createProjectOrder", method = RequestMethod.POST)
    public @ResponseBody JsonResult createProjectOrder(@RequestBody ProjectOrderEntity projectOrderEntity){
        try{
//            String uniqueCode = getUniqueCode();
            User user = getUser();
            projectService.createProjectOrder(uniqueCode, user, projectOrderEntity);
            return new JsonResult("创建成功", true);
        }catch (Exception e){
            e.printStackTrace();
            return new JsonResult("创建测试单异常", false);
        }
    }

    /**
     * 获取测试单操作记录
     */
    @RequestMapping(value = "getOrderLog", method = RequestMethod.POST)
    public @ResponseBody JsonResult getOrderLog(@RequestBody ProjectOrderQuery projectOrderQuery){
        try{
//            String uniqueCode = getUniqueCode();
            ProjectOrderLogEntity entity = new ProjectOrderLogEntity();
            return new JsonResult(entity);
        }catch (Exception e){
            e.printStackTrace();
            return new JsonResult("获取测试单操作记录异常", false);
        }
    }


}
