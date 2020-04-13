package com.kongtrolink.framework.scloud.service;

import com.kongtrolink.framework.scloud.entity.UserSiteEntity;
import com.kongtrolink.framework.scloud.entity.model.UserModel;
import com.kongtrolink.framework.scloud.query.UserQuery;

import java.util.List;

/**
 * 系统管理-用户管理-系统用户 接口类
 * Created by Eric on 2020/2/28.
 */
public interface UserService {

    /**
     * 保存或修改 用户管辖站点
     */
    void modifyUserSite(String uniqueCode, List<UserSiteEntity> userSites);

    /**
     * 获取用户管辖站点
     */
    List<UserSiteEntity> getUserSite(String uniqueCode, String userId);
}
