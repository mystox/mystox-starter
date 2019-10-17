package com.kongtrolink.framework.controller;

import com.kongtrolink.framework.core.entity.session.BaseController;
import com.kongtrolink.framework.service.AlarmService;
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
public class AlarmController extends BaseController {

    @Autowired
    AlarmService alarmService;


}
