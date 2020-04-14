package com.kongtrolink.framework.scloud.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.kongtrolink.framework.entity.JsonResult;
import com.kongtrolink.framework.entity.MsgResult;
import com.kongtrolink.framework.scloud.constant.CommonConstant;
import com.kongtrolink.framework.scloud.constant.OperaCodeConstant;
import com.kongtrolink.framework.scloud.dao.UserMongo;
import com.kongtrolink.framework.scloud.entity.UserEntity;
import com.kongtrolink.framework.scloud.entity.model.UserModel;
import com.kongtrolink.framework.scloud.mqtt.entity.BasicUserEntity;
import com.kongtrolink.framework.scloud.query.UserQuery;
import com.kongtrolink.framework.scloud.service.UserService;
import com.kongtrolink.framework.service.MqttOpera;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

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
     * 添加系统用户
     * @param uniqueCode
     * @param userModel
     */
    @Override
    public void addUser(String uniqueCode, UserModel userModel) {
        UserEntity userEntity = new UserEntity();
        Map<String,Object> map = new HashMap<>();   //传给云管的参数
        map.put("name",userModel.getName());
        map.put("username",userModel.getUsername());
        map.put("phone",userModel.getPhone());
        map.put("email",userModel.getEmail());
        map.put("currentPostId",userModel.getCurrentPostId());
        map.put("currentRoleName",userModel.getCurrentRoleName());
        map.put("informRule",userModel.getInformRule());
        String userModelMsg = JSONObject.toJSONString(map);
        MsgResult opera = mqttOpera.opera("addUser",userModelMsg);
        int stateCode = opera.getStateCode();
        if (stateCode == CommonConstant.SUCCESSFUL) {
            String msg = opera.getMsg();
            JsonResult jsonResult = JSONObject.parseObject(msg, JsonResult.class);
            Object data = jsonResult.getData();
            String userId = String.valueOf(data);//填充数据库实体
            userEntity.setUserId(userId);
            userEntity.setLockStatus(userModel.getLockStatus());
            userEntity.setUserStatus(userModel.getUserStatus());
            userEntity.setValidTime(userModel.getValidTime());
            userEntity.setWorkId(userModel.getWorkId());
            userEntity.setCreateTime(userModel.getCreateTime());
            userEntity.setLastLogin(userModel.getLastLogin());
            userEntity.setChangeTime(userModel.getChangeTime());
            userEntity.setRemark(userModel.getRemark());
            userEntity.setPassword(userModel.getPassword());
            userEntity.setSex(userModel.getSex());
            userEntity.setUserTime(userModel.getUserTime());
            userMongo.addUser(uniqueCode,userEntity);
        }
    }

    /**
     * 修改系统用户
     * @param uniqueCode
     * @param userModel
     * @return
     */
    @Override
    public boolean modifyUser(String uniqueCode, UserModel userModel) {
        Map<String,Object> map = new HashMap<>();   //传给云管的参数
        map.put("userId",userModel.getUserId());
        map.put("name",userModel.getName());
        map.put("username",userModel.getUsername());
        map.put("phone",userModel.getPhone());
        map.put("email",userModel.getEmail());
        map.put("currentPostId",userModel.getCurrentPostId());
        map.put("currentRoleName",userModel.getCurrentRoleName());
        map.put("informRule",userModel.getInformRule());
        String userModelMsg = JSONObject.toJSONString(userModel);
        MsgResult opera = mqttOpera.opera("modifyUser",userModelMsg);
        if (opera.getStateCode() == CommonConstant.SUCCESSFUL){
            boolean modifyUser = userMongo.modifyUser(uniqueCode,userModel);
            return modifyUser;
        }else {
            return false;
        }
    }

    /**
     * 删除系统用户
     * @param uniqueCode
     * @param userModel
     */
    @Override
    public void deleteUser(String uniqueCode, UserModel userModel) {
        Map<String,Object> map = new HashMap<>();   //传给云管的参数
        map.put("userId",userModel.getUserId());
        String userModelMsg = JSONObject.toJSONString(map);
        MsgResult opera = mqttOpera.opera("deleteUser",userModelMsg);
        if (opera.getStateCode() == CommonConstant.SUCCESSFUL){
            userMongo.deleteUser(uniqueCode,userModel);
        }
    }

    /**
     * 获取用户
     * @param uniqueCode
     * @param userQuery
     * @return
     */
    @Override
    public List<JSONObject> listUser(String uniqueCode, UserQuery userQuery) {
        Map<String,Object> map = new HashMap<>();   //传给云管的参数
        map.put("enterpriseCode",uniqueCode);
        map.put("name",userQuery.getName());
        map.put("username",userQuery.getUsername());
        map.put("currentRoleName",userQuery.getCurrentRoleName());
        String userMsg = JSONObject.toJSONString(map);
        List<JSONObject> result = new ArrayList<>();
        MsgResult opera = mqttOpera.opera("listUser",userMsg);
        if (opera.getStateCode() == CommonConstant.SUCCESSFUL){
            String msg = opera.getMsg();
            JSONObject resultRange = JSONObject.parseObject(msg, JSONObject.class);
            Boolean success = resultRange.getBoolean("success");
            if (success) {
                result = resultRange.getJSONArray("list").toJavaList(JSONObject.class);
                for (JSONObject userList:result){
                    String userId = userList.getString("userId");
                    UserModel list = userMongo.listUser(uniqueCode,userId,userQuery);
                    JSONObject userJson = (JSONObject) JSONObject.toJSON(list);
                    userList.putAll(userJson);
                }
            }
            return result;
        }else {
            return null;
        }
    }

    @Override
    public HSSFWorkbook exportUserList(List<UserModel> list) {
        return null;
    }

//    public static void main(String[] args){
//        UserModel userModel = new UserModel();
//        String s = "{\"msg\": \"success\", \"data\": [{\"xm\": \"张三\", \"birthdate\": \"1990-01-18 11:10:41\"},{\"xm\": \"李四\", \"birthdate\": \"1991-01-18 11:10:41\"}]}";        //json字符串转Map
//        //json字符串转Map
//        Map<String,Object> jsonToMap = JSONObject.parseObject(s);
//        System.out.println("jsonToMap："+jsonToMap);
//        //json字符串转List
//        List<Object> jsonToList = JSONArray.parseArray(jsonToMap.get("data").toString());
//        System.out.println("jsonToList："+jsonToList);
//    }
}
