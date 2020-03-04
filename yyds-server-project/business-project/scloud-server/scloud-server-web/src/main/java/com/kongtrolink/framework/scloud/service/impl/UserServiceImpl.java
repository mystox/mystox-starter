package com.kongtrolink.framework.scloud.service.impl;

import com.kongtrolink.framework.scloud.dao.UserMongo;
import com.kongtrolink.framework.scloud.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 系统管理-用户管理-系统用户 接口实现类
 * Created by Eric on 2020/2/28.
 */
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    UserMongo userMongo;

    private static final Logger LOGGER = LoggerFactory.getLogger(UserServiceImpl.class);
}
