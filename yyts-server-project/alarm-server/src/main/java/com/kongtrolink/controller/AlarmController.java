package com.kongtrolink.controller;

import com.kongtrolink.service.AlarmService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @Auther: liudd
 * @Date: 2019/9/11 14:42
 * @Description:
 */
@Controller
@RequestMapping("/alarmController/")
public class AlarmController {

    @Autowired
    AlarmService alarmService;


}
