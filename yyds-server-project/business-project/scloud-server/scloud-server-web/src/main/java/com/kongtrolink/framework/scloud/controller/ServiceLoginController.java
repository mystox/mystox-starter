package com.kongtrolink.framework.scloud.controller;

import com.kongtrolink.framework.core.entity.session.BaseController;
import com.kongtrolink.framework.entity.JsonResult;
import com.kongtrolink.framework.scloud.dao.CompanyMongo;
import com.kongtrolink.framework.scloud.entity.CompanyEntity;
import com.kongtrolink.framework.scloud.entity.model.UserModel;
import com.kongtrolink.framework.scloud.service.UserService;
import com.kongtrolink.framework.scloud.util.CookieUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * \* @Author: mystox
 * \* Date: 2020/4/14 10:19
 * \* Description: 系统登录
 * \
 */
@RestController
@RequestMapping("/service")
public class ServiceLoginController extends BaseController {
    @Autowired
    CompanyMongo companyMongo;
    @Autowired
    UserService userService;

    @RequestMapping("/login")
    public JsonResult serviceLogin() {
        String uniqueCode = getUniqueCode();
        checkInit(uniqueCode);  //检测并初始化企业信息
        String userId = getUserId();
        UserModel userModel = userService.getUserById(uniqueCode, userId);
        if (userModel != null) {
            Date validTime = userModel.getValidTime();
            if (validTime != null && validTime.getTime() != 0 && validTime.getTime() < System.currentTimeMillis()) {
                getSession().invalidate();
                CookieUtil.destroy(getHttpServletRequest(), getHttpServletResponse());
                return  new JsonResult("账号已过期", false);
            }
        }
        return new JsonResult("用户登录服务验证成功", true);
    }

    //检测并初始化企业信息
    private void checkInit(String uniqueCode){
        CompanyEntity entity = companyMongo.findCompanyInfo(uniqueCode);
        if (null == entity){    //如果业务平台中没有企业的初始化信息，则进行初始化
            entity = new CompanyEntity();
            entity.setUniqueCode(uniqueCode);
            List<String> alarmFields = new ArrayList<>();

            alarmFields.add("tierName");
            alarmFields.add("siteName");
            alarmFields.add("deviceName");
            alarmFields.add("name");
            alarmFields.add("signalId");
            alarmFields.add("value");
            alarmFields.add("targetLevelName");
            alarmFields.add("treport");

            entity.setAlarmFields(alarmFields);
            companyMongo.addCompany(entity);
        }
    }
}