package com.kongtrolink.framework.scloud.controller;

import com.alibaba.fastjson.JSONObject;
import com.kongtrolink.framework.core.entity.session.BaseController;
import com.kongtrolink.framework.entity.JsonResult;
import com.kongtrolink.framework.scloud.entity.model.UserModel;
import com.kongtrolink.framework.scloud.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

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
    UserService userService;

    @RequestMapping("/login")
    public JsonResult serviceLogin(@RequestBody JSONObject body) {
        String uniqueCode = getUniqueCode();
        String userId = getUserId();
        UserModel userModel = userService.getUserById(uniqueCode, userId);
        Date validTime = userModel.getValidTime();
        if (validTime != null)
            new JsonResult("账号已过期", false);
        return new JsonResult("用户登录服务验证成功", true);
    }



}