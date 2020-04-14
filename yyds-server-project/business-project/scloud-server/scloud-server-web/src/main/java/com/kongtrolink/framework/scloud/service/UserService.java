package com.kongtrolink.framework.scloud.service;

import com.alibaba.fastjson.JSONObject;
import com.kongtrolink.framework.scloud.entity.UserEntity;
import com.kongtrolink.framework.scloud.entity.model.UserModel;
import com.kongtrolink.framework.scloud.query.UserQuery;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import java.util.List;

/**
 * 系统管理-用户管理-系统用户 接口类
 * Created by Eric on 2020/2/28.
 */
public interface UserService {
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
