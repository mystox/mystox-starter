package com.kongtrolink.framework.congtroller;

import com.kongtrolink.framework.config.LoginConfig;
import com.kongtrolink.framework.util.JsonResult;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * \* @Author: mystox
 * \* Date: 2018/10/11 18:20
 * \* Description:
 * \
 */
@RestController
@RequestMapping("auth")
public class LoginController
{

    @Autowired
    private LoginConfig loginConfig;

    @RequestMapping("/login")
    public JsonResult login(@RequestBody Map<String,String> requestBody)
    {
        String username = requestBody.get("username");
        String password = requestBody.get("password");
        if (StringUtils.isAnyBlank(username,password))
            return new JsonResult("用户名或者密码不能为空", false);
        Map<String,String> result = loginConfig.getUsers();
        if (result!=null) {
            if (StringUtils.equals(password, result.get(username)))
                return new JsonResult("登录成功", true);
            else return new JsonResult("用户名或者密码错误", false);
        }else
        {
            return new JsonResult("系统配置为空", false);
        }

    }
}