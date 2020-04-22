package com.kongtrolink.framework.scloud.controller;

import com.kongtrolink.framework.core.entity.User;
import com.kongtrolink.framework.core.entity.session.BaseController;
import com.kongtrolink.framework.entity.JsonResult;
import com.kongtrolink.framework.entity.ListResult;
import com.kongtrolink.framework.scloud.entity.ProjectOrderEntity;
import com.kongtrolink.framework.scloud.entity.ProjectOrderLogEntity;
import com.kongtrolink.framework.scloud.entity.ProjectOrderTestEntity;
import com.kongtrolink.framework.scloud.entity.RelatedDeviceInfo;
import com.kongtrolink.framework.scloud.entity.model.ProjectOrderModel;
import com.kongtrolink.framework.scloud.query.DeviceQuery;
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

import javax.servlet.http.HttpServletRequest;
import java.util.*;

/**
 * 工程管理 控制器
 * Created by Eric on 2020/4/13.
 */
@Controller
@RequestMapping(value = "/project/", method = RequestMethod.POST)
public class ProjectController extends BaseController{

    @Autowired
    ProjectService projectService;

    private static final Logger LOGGER = LoggerFactory.getLogger(ProjectController.class);

    /**
     * 获取测试单列表
     */
    @RequestMapping(value = "getProjectOrderList", method = RequestMethod.POST)
    public @ResponseBody JsonResult getProjectOrderList(@RequestBody ProjectOrderQuery projectOrderQuery){
        try{
            String uniqueCode = getUniqueCode();
            ListResult<ProjectOrderModel> result = projectService.getProjectOrderList(uniqueCode, projectOrderQuery);
            return new JsonResult(result);
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
            String uniqueCode = getUniqueCode();
            User user = getUser();
            projectService.createProjectOrder(uniqueCode, user, projectOrderEntity);
            return new JsonResult("创建成功", true);
        }catch (Exception e){
            e.printStackTrace();
            return new JsonResult("创建测试单异常", false);
        }
    }

    /**
     * 获取测试单-测试项-测试设备列表
     */
    @RequestMapping(value = "getProjectOrderDeviceList", method = RequestMethod.POST)
    public @ResponseBody JsonResult getProjectOrderDeviceList(@RequestBody DeviceQuery deviceQuery){
        try{
            String uniqueCode = getUniqueCode();
            List<RelatedDeviceInfo> list = projectService.getProjectOrderDevices(uniqueCode, deviceQuery);
            return new JsonResult(list);
        }catch (Exception e){
            e.printStackTrace();
            return new JsonResult("获取测试单设备列表异常", false);
        }
    }

    /**
     * 生成测试项
     */
    @RequestMapping(value = "createProjectOrderTest", method = RequestMethod.POST)
    public @ResponseBody JsonResult createProjectOrderTest(@RequestBody ProjectOrderQuery projectOrderQuery){
        try{
            String uniqueCode = getUniqueCode();
            projectService.createProjectOrderTest(uniqueCode, projectOrderQuery);
            return new JsonResult("生成测试项成功", true);
        }catch (Exception e){
            e.printStackTrace();
            return new JsonResult("生成测试项异常", false);
        }
    }

    /**
     * 获取测试项列表
     */
    @RequestMapping(value = "getProjectOrderTestList", method = RequestMethod.POST)
    public @ResponseBody JsonResult getProjectOrderTestList(@RequestBody ProjectOrderQuery projectOrderQuery){
        try{
            String uniqueCode = getUniqueCode();
            List<ProjectOrderTestEntity> list = projectService.getProjectOrderTest(uniqueCode, projectOrderQuery);
            return new JsonResult(list);
        }catch (Exception e){
            e.printStackTrace();
            return new JsonResult("获取测试项列表失败", false);
        }
    }

    /**
     * 提交测试单
     */
    @RequestMapping(value = "submitProjectOrder", method = RequestMethod.POST)
    public @ResponseBody JsonResult submitProjectOrder(@RequestBody List<ProjectOrderTestEntity> list) {
        try{
            String uniqueCode = getUniqueCode();
            projectService.submitProjectOrder(uniqueCode, getUser(), list);
            return new JsonResult("提交成功", true);
        }catch (Exception e){
            e.printStackTrace();
            return new JsonResult("提交测试单异常", false);
        }
    }

    /**
     * 获取测试项“遥测”信号点实时值
     */
    @RequestMapping(value = "orderTestSignalsCurrent", method = RequestMethod.POST)
    public @ResponseBody JsonResult orderTestSignalsCurrent(@RequestBody ProjectOrderQuery projectOrderQuery){
        try{
            String uniqueCode = getUniqueCode();

            //模拟返回数据
            String orderId = "5e9aacd9204b964b5cd3d6dc";
            String deviceCode1 = "12010131830003";
            String deviceCode2 = "12010130600001";
            List<ProjectOrderTestEntity> list = new ArrayList<>();
            ProjectOrderTestEntity entity1 = new ProjectOrderTestEntity();
            entity1.setOrderId(orderId);
            entity1.setDeviceCode(deviceCode1);
            entity1.setCntbId("0418101001");
            entity1.setCurrent(2.0d);
            list.add(entity1);

            ProjectOrderTestEntity entity2 = new ProjectOrderTestEntity();
            entity2.setOrderId(orderId);
            entity2.setDeviceCode(deviceCode1);
            entity2.setCntbId("0418102001");
            entity2.setCurrent(3.5d);
            list.add(entity2);

            ProjectOrderTestEntity entity3 = new ProjectOrderTestEntity();
            entity3.setOrderId(orderId);
            entity3.setDeviceCode(deviceCode2);
            entity3.setCntbId("0406101001");
            entity3.setCurrent(1.0d);
            list.add(entity3);

            return new JsonResult(list);
//            return new JsonResult("获取信号列表超时", false);
        }catch (Exception e){
            e.printStackTrace();
            return new JsonResult("获取信号点实时值异常", false);
        }
    }

    /**
     * 获取测试项“遥信”信号点的历史告警
     */
    @RequestMapping(value = "orderTestAlarmHistory", method = RequestMethod.POST)
    public @ResponseBody JsonResult orderTestAlarmHistory(@RequestBody ProjectOrderQuery projectOrderQuery){
        try{
            String uniqueCode = getUniqueCode();

            //模拟返回数据
            String orderId = "5e9aacd9204b964b5cd3d6dc";
            String deviceCode1 = "12010131830003";
            String deviceCode2 = "12010130600001";
            List<ProjectOrderTestEntity> list = new ArrayList<>();
            ProjectOrderTestEntity entity1 = new ProjectOrderTestEntity();
            entity1.setOrderId(orderId);
            entity1.setDeviceCode(deviceCode1);
            entity1.setCntbId("0418004001");
            entity1.setCurrent(0d);
            entity1.setHistory("温度过高告警");
            list.add(entity1);

            ProjectOrderTestEntity entity2 = new ProjectOrderTestEntity();
            entity2.setOrderId(orderId);
            entity2.setDeviceCode(deviceCode1);
            entity2.setCntbId("0418005001");
            entity2.setCurrent(0d);
            entity2.setHistory("温度超高告警");
            list.add(entity2);

            ProjectOrderTestEntity entity3 = new ProjectOrderTestEntity();
            entity3.setOrderId(orderId);
            entity3.setDeviceCode(deviceCode1);
            entity3.setCntbId("0418006001");
            entity3.setCurrent(1d);
            entity3.setHistory("温度过低告警");
            list.add(entity3);

            ProjectOrderTestEntity entity4 = new ProjectOrderTestEntity();
            entity4.setOrderId(orderId);
            entity4.setDeviceCode(deviceCode1);
            entity4.setCntbId("0418007001");
            entity4.setCurrent(0d);
            entity4.setHistory("湿度过高告警");
            list.add(entity4);

            ProjectOrderTestEntity entity5 = new ProjectOrderTestEntity();
            entity5.setOrderId(orderId);
            entity5.setDeviceCode(deviceCode1);
            entity5.setCntbId("0418008001");
            entity5.setCurrent(1d);
            entity5.setHistory("湿度过低告警");
            list.add(entity5);

            ProjectOrderTestEntity entity6 = new ProjectOrderTestEntity();
            entity6.setOrderId(orderId);
            entity6.setDeviceCode(deviceCode2);
            entity6.setCntbId("0406005001");
            entity6.setCurrent(0d);
            entity6.setHistory("电池供电告警");
            list.add(entity6);

            return new JsonResult(list);
        }catch (Exception e){
            e.printStackTrace();
            return new JsonResult("获取信号历史告警异常", false);
        }
    }

    /**
     * 审核测试单
     */
    @RequestMapping(value = "projectOrderCheck", method = RequestMethod.POST)
    public @ResponseBody JsonResult projectOrderCheck(@RequestBody ProjectOrderQuery projectOrderQuery) {
        try {
            String uniqueCode = getUniqueCode();
            projectService.projectOrderCheck(uniqueCode, getUser(), projectOrderQuery);
            return new JsonResult("审核成功", true);
        }catch (Exception e){
            e.printStackTrace();
            return new JsonResult("审核异常", false);
        }
    }

    /**
     * 获取测试单操作记录
     */
    @RequestMapping(value = "getOrderLogs", method = RequestMethod.POST)
    public @ResponseBody JsonResult getOrderLogs(@RequestBody ProjectOrderQuery projectOrderQuery){
        try{
            String uniqueCode = getUniqueCode();
            List<ProjectOrderLogEntity> list = projectService.getOrderLogs(uniqueCode, projectOrderQuery);
            return new JsonResult(list);
        }catch (Exception e){
            e.printStackTrace();
            return new JsonResult("获取测试单操作记录异常", false);
        }
    }


}
