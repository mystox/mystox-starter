package com.kongtrolink.framework.mqtt.controller;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

/**
 * Created by mystoxlol on 2019/8/20, 15:18.
 * company: kongtrolink
 * description:
 * update record:
 */
@RestController
@RequestMapping("/mqtt")
public class MqttController {


    /**
     * 注册订阅表
     * @return
     */
    @RequestMapping("/registerSub")
    public String registerSub(@RequestBody String body) {


        return "";
    }

    /**
     * 注册请求表
     * @return
     */
    @RequestMapping("/registerPub")
    public String registerPub() {

        return "";
    }

    /**
     * 更新订阅表
     * @return
     */
    @RequestMapping("/updateSub")
    public String updateSub() {

        return "";
    }

    /**
     * 更新请求表
     * @return
     */
    @RequestMapping("/updatePub")
    public String updatePub() {

        return "";
    }

    /**
     * 删除订阅表
     * @return
     */
    @RequestMapping("/deleteSub")
    public String deleteSub() {

        return "";
    }

    /**
     * 删除请求表
     * @return
     */
    @RequestMapping("/deletePub")
    public String deletePub() {

        return "";
    }

    public static void main(String[] args)
    {
        System.out.println(new Date(1566286026000l));
        System.out.println(new Date(1566286151000l));
        System.out.println(new Date(1566306009000l));
    }
}
