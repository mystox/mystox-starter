package com.kongtrolink.framework.scloud.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.kongtrolink.framework.entity.JsonResult;
import com.kongtrolink.framework.entity.MsgResult;
import com.kongtrolink.framework.scloud.constant.CommonConstant;
import com.kongtrolink.framework.scloud.constant.OperaCodeConstant;
import com.kongtrolink.framework.scloud.dao.MaintainerMongo;
import com.kongtrolink.framework.scloud.dao.UserMongo;
import com.kongtrolink.framework.scloud.entity.MaintainerEntity;
import com.kongtrolink.framework.scloud.entity.model.MaintainerModel;
import com.kongtrolink.framework.scloud.mqtt.entity.AuthPlatformResponseEntity;
import com.kongtrolink.framework.scloud.mqtt.entity.BasicUserEntity;
import com.kongtrolink.framework.scloud.query.MaintainerQuery;
import com.kongtrolink.framework.scloud.service.MaintainerService;
import com.kongtrolink.framework.scloud.util.ExcelUtil;
import com.kongtrolink.framework.scloud.util.StringUtil;
import com.kongtrolink.framework.service.MqttOpera;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 系统管理-用户管理-维护用户 接口实现类
 * Created by Eric on 2020/2/28.
 */
@Service
public class MaintainerServiceImpl implements MaintainerService{

    @Autowired
    MaintainerMongo maintainerMongo;
    @Autowired
    UserMongo userMongo;
    @Autowired
    MqttOpera mqttOpera;

    private static final Logger LOGGER = LoggerFactory.getLogger(MaintainerServiceImpl.class);

    /**
     * 获取维护用户列表
     */
    @Override
    public List<MaintainerModel> getMaintainerList(String uniqueCode, MaintainerQuery maintainerQuery) {
        List<MaintainerModel> list = new ArrayList<>();

        //获取企业下所有用户（当前云管暂未提供根据条件获取用户，当前方案为从云管获取企业下所有用户，然后在业务平台进行条件筛选）
        MsgResult msgResult = mqttOpera.opera(OperaCodeConstant.GET_USER_LIST_BY_ENTERPRISE_CODE, uniqueCode);
        if (msgResult.getStateCode() == CommonConstant.SUCCESSFUL){
            if (msgResult.getMsg() != null) {
                LOGGER.info("【维护用户管理】,从【云管】获取所有用户 成功");
                List<BasicUserEntity> basicUserEntityList = JSONArray.parseArray(msgResult.getMsg(), BasicUserEntity.class);
                if (basicUserEntityList.size() > 0) {

                    //筛选出维护人员
                    List<BasicUserEntity> basicMaintainerList = new ArrayList<>();
                    for (BasicUserEntity basicMaintainer : basicUserEntityList){
                        if (basicMaintainer.getCurrentRoleName().equals("维护人员")){
                            basicMaintainerList.add(basicMaintainer);
                        }
                    }

                    if (basicMaintainerList.size() > 0) {
                        //获取（业务平台数据库）所有维护人员(扩展信息)
                        List<MaintainerEntity> maintainers = maintainerMongo.findMaintainerList(uniqueCode, maintainerQuery);
                        Map<String, MaintainerEntity> maintainerEntityMap = new HashMap<>();    //key: 账号(唯一)，value:维护人员扩展信息
                        if (maintainers != null && maintainers.size() > 0) {
                            for (MaintainerEntity maintainer : maintainers) {
                                maintainerEntityMap.put(maintainer.getUsername(), maintainer);
                            }
                        }

                        for (BasicUserEntity basicMaintainer : basicMaintainerList) {
                            if (!StringUtil.isNUll(maintainerQuery.getName())) { //如果模糊搜索条件中姓名不为空，则筛选出满足条件的
                                if (basicMaintainer.getName().contains(maintainerQuery.getName())) {
                                    MaintainerModel maintainerModel = getMaintainerModel(basicMaintainer, maintainerEntityMap.get(basicMaintainer.getUsername()));
                                    list.add(maintainerModel);
                                }
                            } else {
                                MaintainerModel maintainerModel = getMaintainerModel(basicMaintainer, maintainerEntityMap.get(basicMaintainer.getUsername()));
                                list.add(maintainerModel);
                            }
                        }
                    }
                }
            }else {
                LOGGER.error("【维护用户管理】,从【云管】获取所有用户 失败");
            }
        }else {
            LOGGER.error("【维护用户管理】,从【云管】获取所有用户 MQTT通信失败");
        }

        return list;
    }

    /**
     * 导出维护用户列表
     */
    @Override
    public HSSFWorkbook exportMaintainerList(List<MaintainerModel> list) {
        String[][] maintainerSheet = getMaintainerListAsTable(list);

        HSSFWorkbook workBook = ExcelUtil.getInstance().createWorkBook(
                new String[] {"维护用户列表"}, new String[][][] { maintainerSheet });
        return workBook;
    }

    /**
     * 添加维护用户
     */
    @Override
    public String addMaintainer(String uniqueCode, MaintainerModel maintainerModel) {
        //保存在业务平台数据库的维护人员扩展信息
        MaintainerEntity maintainerEntity = new MaintainerEntity();
        maintainerEntity.setUsername(maintainerModel.getUsername());
        maintainerEntity.setCompanyName(maintainerModel.getCompanyName());
        maintainerEntity.setOrganizationId(maintainerModel.getOrganizationId());
        maintainerEntity.setStatus(maintainerModel.getStatus());
        maintainerEntity.setHireDate(maintainerModel.getHireDate());
        maintainerEntity.setExpireDate(maintainerModel.getExpireDate());
        maintainerEntity.setMajor(maintainerModel.getMajor());
        maintainerEntity.setSkill(maintainerModel.getSkill());
        maintainerEntity.setAddress(maintainerModel.getAddress());
        maintainerEntity.setIdCard(maintainerModel.getIdCard());
        maintainerEntity.setDuty(maintainerModel.getDuty());
        maintainerEntity.setEducation(maintainerModel.getEducation());
        maintainerEntity.setAuthentication(maintainerModel.getAuthentication());
        maintainerEntity.setAuthLevel(maintainerModel.getAuthLevel());
        maintainerEntity.setAuthDate(maintainerModel.getAuthDate());
        maintainerEntity.setAuthExpireDate(maintainerModel.getAuthExpireDate());

        //拼凑向【云管】下发的Message
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("name", maintainerModel.getName());
        jsonObject.put("username", maintainerModel.getUsername());
        jsonObject.put("phone", maintainerModel.getPhone());
        jsonObject.put("email", maintainerModel.getEmail());
        jsonObject.put("password", maintainerModel.getPassword());
        jsonObject.put("currentPostId", maintainerModel.getCurrentPostId());
        jsonObject.put("currentPositionName", maintainerModel.getCurrentPositionName());

        //向【云管】下发添加维护用户的MQTT消息
        MsgResult msgResult = mqttOpera.opera(OperaCodeConstant.ADD_USER, JSON.toJSONString(jsonObject));
        if (msgResult.getStateCode() == CommonConstant.SUCCESSFUL){
            if (msgResult.getMsg() != null) {
                AuthPlatformResponseEntity response = JSONObject.parseObject(msgResult.getMsg(), AuthPlatformResponseEntity.class);
                if (response.isSuccess()) {
                    LOGGER.info("【用户管理】,向【云管】发送添加维护人员,请求成功");
                    String userId = response.getData();  //userId由【云管】添加成功后返回
                    maintainerEntity.setUserId(userId);

                    //保存维护人员(扩展信息)
                    maintainerMongo.saveMaintainer(uniqueCode, maintainerEntity);

                    return userId;
                }else {
                    LOGGER.error("【用户管理】,向【云管】发送添加维护人员,请求失败");
                    return null;
                }
            }else {
                LOGGER.error("【用户管理】,向【云管】发送添加维护人员,msg为null,请求失败");
                return null;
            }
        }else {
            LOGGER.error("【用户管理】,向【云管】发送添加维护人员,通信失败");
            return null;
        }
    }

    /**
     * 修改维护用户
     */
    @Override
    public boolean modifyMaintainer(String uniqueCode, MaintainerModel maintainerModel) {
        String isModified = maintainerModel.getIsModified();    //修改维护用户时,是否修改了手机号或邮箱或密码
        boolean modifyResult = false;
        if (isModified.equals(CommonConstant.MODIFIED)){    //如果修改维护用户的基本属性(即手机号、邮箱、密码)
            //拼凑向【云管】下发的Message
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("userId", maintainerModel.getUserId());
            jsonObject.put("name", maintainerModel.getName());
            jsonObject.put("username", maintainerModel.getUsername());
            jsonObject.put("phone", maintainerModel.getPhone());
            jsonObject.put("email", maintainerModel.getEmail());
            jsonObject.put("password", maintainerModel.getPassword());
            jsonObject.put("currentPostId", maintainerModel.getCurrentPostId());
            jsonObject.put("currentPositionName", maintainerModel.getCurrentPositionName());

            //向【云管】下发修改维护用户的MQTT消息
            MsgResult msgResult = mqttOpera.opera(OperaCodeConstant.MODIFY_USER, JSON.toJSONString(jsonObject));
            if (msgResult.getStateCode() == CommonConstant.SUCCESSFUL){
                if (msgResult.getMsg() != null) {
                    AuthPlatformResponseEntity response = JSONObject.parseObject(msgResult.getMsg(), AuthPlatformResponseEntity.class);
                    if (response.isSuccess()) {
                        LOGGER.info("向【云管】发送修改维护人员MQTT,请求成功");
                        //对平台端数据库中保存的维护人员扩展信息进行修改
                        modifyResult = maintainerMongo.modifyMaintainer(uniqueCode, maintainerModel);
                    }else {
                        LOGGER.info("向【云管】发送修改维护人员,请求失败");
                    }
                }else {
                    LOGGER.info("向【云管】发送修改维护人员,msg为null，请求失败");
                }
            }else {
                LOGGER.error("向【云管】发送修改维护人员,通信失败");
            }
        }else { //如果没有修改维护用户的基本属性，则直接对平台端数据库中保存的维护人员扩展信息进行修改
            modifyResult = maintainerMongo.modifyMaintainer(uniqueCode, maintainerModel);
        }

        return modifyResult;
    }

    /**
     * 删除维护用户
     */
    @Override
    public void deleteMaintainer(String uniqueCode, MaintainerQuery maintainerQuery) {
        List<String> userIds = maintainerQuery.getUserIds();   //用户Id(集合)

        //由于【云管】目前不支持批量删除功能，只能进行循环单个删除
        for (String userId : userIds) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("userId", userId);

            //向【云管】下发删除维护用户的MQTT消息
            MsgResult msgResult = mqttOpera.opera(OperaCodeConstant.DELETE_USER, JSON.toJSONString(jsonObject));
            if (msgResult.getStateCode() == CommonConstant.SUCCESSFUL) {
                if (msgResult.getMsg() != null) {
                    AuthPlatformResponseEntity response = JSONObject.parseObject(msgResult.getMsg(), AuthPlatformResponseEntity.class);
                    if (response.isSuccess()) {
                        LOGGER.info("向【云管】发送删除维护人员,userId:{},请求成功", userId);
                        //删除平台端数据库中保存的维护人员(扩展信息)
                        maintainerMongo.deleteMaintainer(uniqueCode, userId);

                        //从用户管辖站点表中删除
                        userMongo.deleteUserSite(uniqueCode, userId);
                    }else {
                        LOGGER.error("向【云管】发送删除维护人员,userId:{},请求失败", userId);
                    }
                } else {
                    LOGGER.error("向【云管】发送删除维护人员,userId:{},msg为null,请求失败", userId);
                }
            } else {
                LOGGER.error("向【云管】发送删除维护人员,userId:{},通信失败", userId);
            }
        }
    }

    private String[][] getMaintainerListAsTable(List<MaintainerModel> list) {
        int colNum = 19;
        int rowNum = list.size() + 1;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        String[][] tableDatas = new String[rowNum][colNum];

        for (int i = 0; i < rowNum; i++) {
            String[] row = tableDatas[i];
            if (i == 0) {
                row[0] = "账号";
                row[1] = "姓名";
                row[2] = "联系电话";
                row[3] = "Email";
                row[4] = "代维公司";
                row[5] = "组织机构ID";
                row[6] = "在职状态";
                row[7] = "入职时间";
                row[8] = "合同到期";
                row[9] = "代维专业";
                row[10] = "具备技能";
                row[11] = "家庭住址";
                row[12] = "身份证号码";
                row[13] = "岗位职责";
                row[14] = "文化程度";
                row[15] = "认证名称";
                row[16] = "认证级别";
                row[17] = "认证获取时间";
                row[18] = "认证有效期";
            } else {
                MaintainerModel maintainerModel = list.get(i - 1);
                row[0] = maintainerModel.getUsername();
                row[1] = maintainerModel.getName();
                row[2] = maintainerModel.getPhone();
                row[3] = maintainerModel.getEmail();
                row[4] = maintainerModel.getCompanyName();
                row[5] = maintainerModel.getOrganizationId();
                row[6] = maintainerModel.getStatus();
                row[7] = maintainerModel.getHireDate() != null?sdf.format(new Date(maintainerModel.getHireDate())):"-";
                row[8] = maintainerModel.getExpireDate() != null?sdf.format(new Date(maintainerModel.getExpireDate())):"-";
                row[9] = maintainerModel.getMajor();
                row[10] = maintainerModel.getSkill();
                row[11] = maintainerModel.getAddress();
                row[12] = maintainerModel.getIdCard();
                row[13] = maintainerModel.getDuty();
                row[14] = maintainerModel.getEducation();
                row[15] = maintainerModel.getAuthentication();
                row[16] = maintainerModel.getAuthLevel();
                row[17] = maintainerModel.getAuthDate() != null?sdf.format(new Date(maintainerModel.getAuthDate())):"-";
                row[18] = maintainerModel.getAuthExpireDate() != null?sdf.format(new Date(maintainerModel.getAuthExpireDate())):"-";
            }
        }
        return tableDatas;
    }

    //将维护人员扩展属性赋值给返回前端的数据模型
    private MaintainerModel getMaintainerModel(BasicUserEntity basicMaintainerEntity, MaintainerEntity entity){
        MaintainerModel model = new MaintainerModel();
        if (entity != null) {
            model.setUserId(entity.getUserId());
            model.setUsername(entity.getUsername());
            model.setCompanyName(entity.getCompanyName());
            model.setOrganizationId(entity.getOrganizationId());
            model.setStatus(entity.getStatus());
            model.setHireDate(entity.getHireDate());
            model.setExpireDate(entity.getExpireDate());
            model.setMajor(entity.getMajor());
            model.setSkill(entity.getSkill());
            model.setAddress(entity.getAddress());
            model.setIdCard(entity.getIdCard());
            model.setDuty(entity.getDuty());
            model.setEducation(entity.getEducation());
            model.setAuthentication(entity.getAuthentication());
            model.setAuthLevel(entity.getAuthLevel());
            model.setAuthDate(entity.getAuthDate());
            model.setAuthExpireDate(entity.getAuthExpireDate());
        }

        model.setName(basicMaintainerEntity.getName());
        model.setPhone(basicMaintainerEntity.getPhone());
        model.setEmail(basicMaintainerEntity.getEmail());
        model.setCurrentPostId(basicMaintainerEntity.getCurrentRoleId());
        model.setCurrentPositionName(basicMaintainerEntity.getCurrentRoleName());

        return model;
    }
}
