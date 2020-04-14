package com.kongtrolink.framework.scloud.controller;

import com.alibaba.fastjson.JSONObject;
import com.kongtrolink.framework.core.entity.session.BaseController;
import com.kongtrolink.framework.entity.JsonResult;
import com.kongtrolink.framework.scloud.entity.model.UserModel;
import com.kongtrolink.framework.scloud.service.UserService;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * \* @Author: mystox
 * \* Date: 2020/4/14 10:19
 * \* Description: 系统登录
 * \
 */
@RestController
@RequestMapping("/service")
public class ServiceLoginController extends BaseController {


    UserService userService;

    @RequestMapping("/login")
    public JsonResult serviceLogin(@RequestBody  JSONObject body) {

        String uniqueCode = getUniqueCode();
        String userId = getUserId();
        UserModel userModel = userService.getUserById(uniqueCode, userId);
        //todo 获取锁定状态
        if (true)
            return new JsonResult("帐号已锁定，请联系管理人员", false);
        return new JsonResult("登录成功",true);

    }

}