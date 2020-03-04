package com.kongtrolink.framework.scloud.controller;

import com.kongtrolink.framework.core.entity.session.BaseController;
import com.kongtrolink.framework.scloud.service.MaintainerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * 系统管理-用户管理-维护用户 控制器
 * Created by Eric on 2020/2/28.
 */
@Controller
@RequestMapping(value = "/maintainer/", method = RequestMethod.POST)
public class MaintainerController extends BaseController {

    @Autowired
    MaintainerService maintainerService;

    private static final Logger LOGGER = LoggerFactory.getLogger(MaintainerController.class);
}
