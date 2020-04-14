package com.kongtrolink.framework.scloud.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.kongtrolink.framework.entity.JsonResult;
import com.kongtrolink.framework.entity.MsgResult;
import com.kongtrolink.framework.scloud.constant.CommonConstant;
import com.kongtrolink.framework.scloud.dao.UserMongo;
import com.kongtrolink.framework.scloud.entity.UserEntity;
import com.kongtrolink.framework.scloud.entity.UserSiteEntity;
import com.kongtrolink.framework.scloud.entity.model.UserModel;
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
        //保存新的用户管辖站点
        userMongo.saveUserSite(uniqueCode, userSites);
    }
    /**
     * 添加系统用户
     * @param uniqueCode
     * @param userModel
     */
    @Override
    public JsonResult addUser(String uniqueCode, UserModel userModel) {
        UserEntity userEntity = new UserEntity();
        Map<String,Object> map = new HashMap<>();   //传给云管的参数
        map.put("name",userModel.getName());
        map.put("username",userModel.getUsername());
        map.put("phone",userModel.getPhone());
        map.put("email",userModel.getEmail());
        map.put("currentPostId",userModel.getCurrentPostId());
        map.put("currentPositionName",userModel.getCurrentRoleName());
        map.put("informRule",userModel.getInformRule());
        String userModelMsg = JSONObject.toJSONString(map);
        MsgResult opera = mqttOpera.opera("addUser",userModelMsg);
        int stateCode = opera.getStateCode();
        if (stateCode == CommonConstant.SUCCESSFUL) {
            String msg = opera.getMsg();
            JsonResult jsonResult = JSONObject.parseObject(msg, JsonResult.class);
            Boolean success = jsonResult.getSuccess();
            if (!success)
                return jsonResult;
            Object data = jsonResult.getData();
            String userId = String.valueOf(data);//填充数据库实体
            userEntity.setUserId(userId);
//            userEntity.setLockStatus(userModel.getLockStatus());
            userEntity.setUserStatus(userModel.getUserStatus());
            userEntity.setValidTime(userModel.getValidTime());
            userEntity.setWorkId(userModel.getWorkId());
//            userEntity.setCreateTime(userModel.getCreateTime());
//            userEntity.setLastLogin(userModel.getLastLogin());
//            userEntity.setChangeTime(userModel.getChangeTime());
            userEntity.setRemark(userModel.getRemark());
//            userEntity.setPassword(userModel.getPassword());
            userEntity.setSex(userModel.getSex());
            userEntity.setUserTime(userModel.getUserTime());
            userMongo.addUser(uniqueCode,userEntity);
            return jsonResult;
        } else {
            String msg = opera.getMsg();
            JsonResult jsonResult = JSONObject.parseObject(msg, JsonResult.class);
            return jsonResult;
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
                    UserModel userModel = userMongo.listUser(uniqueCode,userId,userQuery);
                    if (userModel == null)
                        continue;
                    JSONObject userJson = (JSONObject) JSONObject.toJSON(userModel);
                    userList.putAll(userJson);
                }
            }
            return result;
        }else {
            return null;
        }
    }

    /**
     * 导出用户列表
     * @param list
     * @return
     */
    @Override
    public HSSFWorkbook exportUserList(List<UserModel> list) {
        String[][] userSheet = getUserListAsTable(list);
        HSSFWorkbook workbook = ExcelUtil.getInstance().createWorkBook(
                new String[] {"系统用户列表"},new String[][][] {userSheet});
        return workbook;
    }

    /**
     * 获取用户管辖站点
     */
    @Override
    public List<UserSiteEntity> getUserSite(String uniqueCode, String userId) {
        return userMongo.findUserSite(uniqueCode, userId);
    }

    @Override
    public UserModel getUserById(String uniqueCode, String userId) {
        return userMongo.findUserById(uniqueCode,userId);
    }
    public String[][] getUserListAsTable(List<UserModel> list){
        int colNum = 13;
        int rowNum = list.size() + 1;
        SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd");
        String[][] tableData = new String[colNum][rowNum];
        for (int i = 0; i < rowNum; i++){
            String[] row = tableData[i];
            if (i == 0){
                row[0] = "账号";
                row[1] = "隶属角色";
                row[2] = "姓名";
                row[3] = "员工工号";
                row[4] = "性别";
                row[5] = "联系电话";
                row[6] = "E-mail";
                row[7] = "创建时间";
                row[8] = "有效日期";
                row[9] = "最后登录日期";
                row[10] = "密码更改日期";
                row[11] = "锁定状态";
                row[12] = "用户状态";
            }else {
                UserModel userModel = list.get(i-1);
                row[0] = userModel.getUsername();
                row[1] = userModel.getCurrentRoleName();
                row[2] = userModel.getName();
                row[3] = userModel.getWorkId();
                row[4] = userModel.getSex();
                row[5] = userModel.getPhone();
                row[6] = userModel.getEmail();
                row[7] = userModel.getCreateTime()  != null?userModel.getCreateTime():"-";
                row[8] = userModel.getValidTime() != null?sd.format(new Date(userModel.getValidTime())):"-";
                row[9] = userModel.getLastLogin();
                row[10] = userModel.getChangeTime() != null?userModel.getChangeTime():"-";
                row[11] = userModel.getLockStatus();
                row[12] = userModel.getUserStatus();
            }
        }
        return tableData;
    }
}
