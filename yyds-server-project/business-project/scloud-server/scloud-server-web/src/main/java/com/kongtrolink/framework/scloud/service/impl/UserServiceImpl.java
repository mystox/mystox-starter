package com.kongtrolink.framework.scloud.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.kongtrolink.framework.scloud.service.DeliverService;
import com.kongtrolink.framework.scloud.util.SessionCommonService;
import com.kongtrolink.framework.entity.JsonResult;
import com.kongtrolink.framework.entity.MsgResult;
import com.kongtrolink.framework.scloud.constant.CommonConstant;
import com.kongtrolink.framework.scloud.dao.UserMongo;
import com.kongtrolink.framework.scloud.entity.UserEntity;
import com.kongtrolink.framework.scloud.entity.UserSiteEntity;
import com.kongtrolink.framework.scloud.entity.model.UserModel;
import com.kongtrolink.framework.scloud.query.UserQuery;
import com.kongtrolink.framework.scloud.service.UserService;
import com.kongtrolink.framework.scloud.util.ExcelUtil;
import com.kongtrolink.framework.service.MqttOpera;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 系统管理-用户管理-系统用户 接口实现类
 * Created by Yu Pengtao on 2020/4/13.
 */
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    SessionCommonService sessionCommonService;
    @Autowired
    UserMongo userMongo;
    @Autowired
    DeliverService deliverService;
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
     * 添加系统用户
     * @param uniqueCode
     * @param userModel
     */
    @Override
    public JsonResult addUser(String uniqueCode, UserModel userModel) {
        UserEntity userEntity = new UserEntity();
        Map<String, Object> map = new HashMap<>();   //传给云管的参数
        map.put("name", userModel.getName());
        map.put("username", userModel.getUsername());
        map.put("phone", userModel.getPhone());
        map.put("email", userModel.getEmail());
        map.put("currentPostId", userModel.getCurrentPostId());
        map.put("currentPositionName", userModel.getCurrentRoleName());
        map.put("informRule", userModel.getInformRule());
        map.put("password", userModel.getPassword());
        String userModelMsg = JSONObject.toJSONString(map);
        MsgResult opera = mqttOpera.opera("addUser", userModelMsg);
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
            userEntity.setGender(userModel.getGender());
            userEntity.setUserTime(userModel.getUserTime());
            userMongo.addUser(uniqueCode, userEntity);
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
        Map<String, Object> map = new HashMap<>();   //传给云管的参数
        map.put("userId", userModel.getUserId());
        map.put("name", userModel.getName());
        map.put("username", userModel.getUsername());
        map.put("phone", userModel.getPhone());
        map.put("email", userModel.getEmail());
        map.put("currentPostId", userModel.getCurrentPostId());
        map.put("currentPositionName", userModel.getCurrentRoleName());
        map.put("informRule", userModel.getInformRule());
        map.put("password", userModel.getPassword());
        String userModelMsg = JSONObject.toJSONString(map);
        MsgResult opera = mqttOpera.opera("modifyUser", userModelMsg);
        if (opera.getStateCode() == CommonConstant.SUCCESSFUL) {
            boolean modifyUser = userMongo.modifyUser(uniqueCode, userModel);
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
        Map<String, Object> map = new HashMap<>();   //传给云管的参数
        map.put("userId", userModel.getUserId());
        String userModelMsg = JSONObject.toJSONString(map);
        MsgResult opera = mqttOpera.opera("deleteUser", userModelMsg);
        if (opera.getStateCode() == CommonConstant.SUCCESSFUL) {
            userMongo.deleteUser(uniqueCode, userModel);
        }
        boolean delResult = deliverService.delDeliverUser(uniqueCode, userModel.getUserId());
    }

    /**
     * 获取用户
     * @param uniqueCode
     * @param userQuery
     * @return
     */
    @Override
    public List<JSONObject> listUser(String uniqueCode, UserQuery userQuery, String serverCode) {
        Set<String> onlineUsernames = new HashSet<>();
        if (StringUtils.isNotBlank(serverCode)) {
            onlineUsernames = sessionCommonService.getUsernameListByCurrentServerCode(serverCode);
        }
        Map<String, Object> map = new HashMap<>();   //传给云管的参数
        map.put("enterpriseCode", uniqueCode);
        map.put("name", userQuery.getName());
        map.put("username", userQuery.getUsername());
        map.put("currentRoleName", userQuery.getCurrentRoleName());
        String userMsg = JSONObject.toJSONString(map);
        MsgResult opera = mqttOpera.opera("listUser", userMsg);
            List<JSONObject> userResult = new ArrayList<>();
        if (opera.getStateCode() == CommonConstant.SUCCESSFUL) {
            String msg = opera.getMsg();
            JSONObject resultRange = JSONObject.parseObject(msg, JSONObject.class);
            Boolean success = resultRange.getBoolean("success");
            if (success) {
        List<JSONObject> result = new ArrayList<>();
                result = resultRange.getJSONArray("list").toJavaList(JSONObject.class);
                for (JSONObject userEntity : result) {

                    String userId = userEntity.getString("userId");
                    UserEntity userEntity1 = userMongo.listUser(uniqueCode, userId, userQuery);
                    if (userEntity1 == null) userEntity1 = new UserEntity();
                    UserModel userModel = new UserModel();
                    BeanUtils.copyProperties(userEntity1,userModel);
                    JSONObject userJson = (JSONObject) JSONObject.toJSON(userModel);
                    userJson.putAll(userEntity);
                    String username = userEntity.getString("username");
                    if (onlineUsernames.contains(username)) {
                        userJson.put("onlineStatus", 1);
                    } else {
                        userJson.put("onlineStatus", 0);
                    }
                    userResult.add(userJson);
                }
            }
            return userResult;
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
    public HSSFWorkbook exportUserList(List<JSONObject> list) {
        String[][] userSheet = getUserListAsTable(list);
        HSSFWorkbook workbook = ExcelUtil.getInstance().createWorkBook(
                new String[] {"系统用户列表"}, new String[][][] { userSheet });
        return workbook;
    }

    /**
     * 导入用户列表
     * @param list
     */
    @Override
    public JsonResult importUserList(String uniqueCode, List<UserModel> list) {
        Map<String,Object> map = new HashMap<>();
        map.put("enterpriseCode",uniqueCode);
        String msg = JSONObject.toJSONString(map);
        MsgResult opera = mqttOpera.opera("getRoleListByUniqueCode",msg);//从云管获取角色名
        List<UserModel> userList = new ArrayList<>();
        List<String> name = new ArrayList<>();
        if (opera.getStateCode() == CommonConstant.SUCCESSFUL){
            String msg1 = opera.getMsg();
            JSONArray jsonArray = JSONArray.parseArray(msg1);
            for (int o =0;o<jsonArray.size();o++){
                UserModel user = new UserModel();
                JSONObject result = (JSONObject) jsonArray.get(o);
                String roleName = result.get("name").toString();
                String roleId = result.get("id").toString();
                name.add(roleName);
                user.setCurrentPostId(roleId);
                user.setCurrentRoleName(roleName);
                userList.add(user);
            }
        }else {
            return new JsonResult("导入失败",false);
        }
        for (int i = 0;i <list.size();i++) {
            for (int j = 0;j <userList.size();j++){
                String roleName = list.get(i).getCurrentRoleName();
                if (userList.get(j).getCurrentRoleName().equals(roleName)){
                    list.get(i).setCurrentPostId(userList.get(j).getCurrentPostId());
                }
            }
            if (!name.contains(list.get(i).getCurrentRoleName())){
                return new JsonResult("第"+(i+2)+"行角色名称不存在,请修改",false);
            }
        }
        String msg1 = JSON.toJSONString(list);
        String info = null;
        try {
            MsgResult opera1 = mqttOpera.opera("addUserBatch", msg1);//云管批量添加用户
            if (opera1.getStateCode() == CommonConstant.SUCCESSFUL) {
                JSONObject msg2 = JSONObject.parseObject(opera1.getMsg(), JSONObject.class);
                String data = msg2.getString("data");
                JSONArray jsonArray = JSONArray.parseArray(data);
                info = msg2.getString("info");
                for (int i = 0;i <jsonArray.size();i++){
                    JSONObject result = (JSONObject) jsonArray.get(i);
                    String username = result.get("username").toString();
                    String userId = result.get("userId").toString();
                    for (int j = 0;j <list.size();j++){
                        if (list.get(j).getUsername().equals(username)){
                            list.get(j).setUserId(userId);
                        }
                    }
                }
            }else {
                return new JsonResult("添加失败",false);
            }
        }catch (Exception e){
            e.printStackTrace();
            return new JsonResult(info,false);
        }
        List<UserEntity> userEntities = new ArrayList<>();
        for (UserModel user:list){
            UserEntity u = new UserEntity();
            u.setUserId(user.getUserId());
            u.setValidTime(user.getValidTime());
            u.setUserTime(user.getUserTime());
            userEntities.add(u);
        }
        userMongo.addUserBatch(uniqueCode,userEntities);
        return new JsonResult("导入成功",true);
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
        return userMongo.findUserById(uniqueCode, userId);
    }
    public String[][] getUserListAsTable(List<JSONObject> list){
        int colNum = 7;
        int rowNum = list.size() + 1;
        SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd");
        String[][] tableData = new String[rowNum][colNum];
        for (int i = 0; i < rowNum; i++){
            String[] row = tableData[i];
            if (i == 0){
                row[0] = "账号";
                row[1] = "隶属角色";
                row[2] = "姓名";
                row[3] = "联系电话";
                row[4] = "E-mail";
                row[5] = "用户时效";
                row[6] = "有效日期";
            }else {
                JSONObject result = list.get(i-1);
                row[0] = result.getString("username");
                row[1] = result.getString("currentRoleName");
                row[2] = result.getString("name");
                row[3] = result.getString("phone");
                row[4] = result.getString("email");
                row[5] = result.getString("validTime") != null?"临时":"长期";
                row[6] = result.getString("validTime") != null?sd.format(new Date(result.getString("validTime"))):"-";
            }
        }
        return tableData;
    }
}
