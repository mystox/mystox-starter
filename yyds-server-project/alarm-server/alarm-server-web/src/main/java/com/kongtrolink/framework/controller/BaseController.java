package com.kongtrolink.framework.controller;

import com.kongtrolink.framework.entity.User;

import javax.servlet.http.HttpServletRequest;

/**
 * @Auther: liudd
 * @Date: 2019/10/11 14:10
 * @Description:
 */
public class BaseController {

    public static User getUser(HttpServletRequest request){
        User user = new User();
        user.setId("defultUser");
        user.setName("admin");
        return user;
    }
}
