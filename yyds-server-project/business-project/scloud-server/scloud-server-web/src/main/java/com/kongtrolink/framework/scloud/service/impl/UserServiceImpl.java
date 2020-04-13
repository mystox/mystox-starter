package com.kongtrolink.framework.scloud.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.kongtrolink.framework.entity.MsgResult;
import com.kongtrolink.framework.scloud.constant.CommonConstant;
import com.kongtrolink.framework.scloud.constant.OperaCodeConstant;
import com.kongtrolink.framework.scloud.dao.UserMongo;
import com.kongtrolink.framework.scloud.entity.UserSiteEntity;
import com.kongtrolink.framework.scloud.entity.model.UserModel;
import com.kongtrolink.framework.scloud.mqtt.entity.BasicUserEntity;
import com.kongtrolink.framework.scloud.query.UserQuery;
import com.kongtrolink.framework.scloud.service.UserService;
import com.kongtrolink.framework.service.MqttOpera;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * 系统管理-用户管理-系统用户 接口实现类
 * Created by Eric on 2020/2/28.
 */
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    UserMongo userMongo;
    @Autowired
    MqttOpera mqttOpera;

    private static final Logger LOGGER = LoggerFactory.getLogger(UserServiceImpl.class);

    /**
     * 保存或修改 用户管辖站点
     *
     * @param uniqueCode 企业唯一码
     * @param userSites 用户管辖站点
     */
    @Override
    public void modifyUserSite(String uniqueCode, List<UserSiteEntity> userSites) {
        //保存前，先删除原有用户管辖站点
        userMongo.deleteUserSite(uniqueCode, userSites.get(0).getUserId());

        //保存新的用户管辖站点
        userMongo.saveUserSite(uniqueCode, userSites);
    }

    /**
     * 获取用户管辖站点
     */
    @Override
    public List<UserSiteEntity> getUserSite(String uniqueCode, String userId) {
        return userMongo.findUserSite(uniqueCode, userId);
    }
}
