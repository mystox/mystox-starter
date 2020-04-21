package com.kongtrolink.framework.scloud.entity.model;

import com.kongtrolink.framework.scloud.entity.MaintainerEntity;
import com.kongtrolink.framework.scloud.mqtt.entity.BasicUserEntity;

/**
 * 维护用户前端显示模型
 * Created by Eric on 2020/2/28.
 */
public class MaintainerModel extends MaintainerEntity {

    /**
     *
     */
    private static final long serialVersionUID = 5338061324345787329L;
    private String password;    //密码
    private String name;    //名称
    private String phone;   //联系电话
    private String email;   //邮箱
    private String currentPostId;   //角色Id
    private String currentPositionName; //角色名称

    private String isModified;  //修改维护用户时，是否修改了手机号||邮箱||密码,"0"-未修改，"1"-修改

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getCurrentPostId() {
        return currentPostId;
    }

    public void setCurrentPostId(String currentPostId) {
        this.currentPostId = currentPostId;
    }

    public String getCurrentPositionName() {
        return currentPositionName;
    }

    public void setCurrentPositionName(String currentPositionName) {
        this.currentPositionName = currentPositionName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getIsModified() {
        return isModified;
    }

    public void setIsModified(String isModified) {
        this.isModified = isModified;
    }

    /**
     * @auther: liudd
     * @date: 2020/4/21 10:39
     * 功能描述:根据业务服务数据库维护人员和运管中心用户信息创建维护人员信息。
     * 数据库维护人员信息不能为空
     */
    public static MaintainerModel createByEntityBasic( MaintainerEntity entity, BasicUserEntity basicMaintainerEntity){
        if(entity == null){
            return null;
        }
        MaintainerModel model = new MaintainerModel();
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
        if(null != basicMaintainerEntity) {
            model.setName(basicMaintainerEntity.getName());
            model.setPhone(basicMaintainerEntity.getPhone());
            model.setEmail(basicMaintainerEntity.getEmail());
            model.setCurrentPostId(basicMaintainerEntity.getCurrentRoleId());
            model.setCurrentPositionName(basicMaintainerEntity.getCurrentRoleName());
        }
        return model;
    }
}
