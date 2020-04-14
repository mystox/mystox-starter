package com.kongtrolink.framework.scloud.service;

import com.kongtrolink.framework.scloud.entity.UserSiteEntity;
import com.alibaba.fastjson.JSONObject;
import com.kongtrolink.framework.scloud.entity.UserEntity;

import com.kongtrolink.framework.scloud.query.UserQuery;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
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

    /**
     * @Date 13:12 2020/4/14
     * @Param No such property: code for class: Script1
     * @return com.kongtrolink.framework.scloud.entity.model.UserModel
     * @Author mystox
     * @Description //根据用户id 获取用户
     **/
    UserModel getUserById(String uniqueCode, String userId);
    /**
     * 添加系统用户
     */
    void addUser(String uniqueCode, UserModel userModel);
    /**
     * 修改系统用户
     */
    boolean modifyUser(String uniqueCode,UserModel userModel);
    /**
     * 删除系统用户
     */
    void deleteUser(String uniqueCode,UserModel userModel);
    /**
     * 用户列表
     */
    List<JSONObject> listUser(String uniqueCode, UserQuery userQuery);
    /**
     * 导出用户列表
     */
    HSSFWorkbook exportUserList(List<UserModel> list);
}
