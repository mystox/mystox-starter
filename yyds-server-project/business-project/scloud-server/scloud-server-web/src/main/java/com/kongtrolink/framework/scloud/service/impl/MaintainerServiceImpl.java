package com.kongtrolink.framework.scloud.service.impl;

import com.kongtrolink.framework.scloud.dao.MaintainerMongo;
import com.kongtrolink.framework.scloud.service.MaintainerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 系统管理-用户管理-维护用户 接口实现类
 * Created by Eric on 2020/2/28.
 */
@Service
public class MaintainerServiceImpl implements MaintainerService{

    @Autowired
    MaintainerMongo maintainerMongo;

    private static final Logger LOGGER = LoggerFactory.getLogger(MaintainerServiceImpl.class);


}
