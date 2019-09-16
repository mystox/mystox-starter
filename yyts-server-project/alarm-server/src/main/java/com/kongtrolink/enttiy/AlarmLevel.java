package com.kongtrolink.enttiy;

import com.kongtrolink.base.FacadeView;

import java.util.Date;
import java.util.List;

/**
 * @Auther: liudd
 * @Date: 2019/9/11 14:59
 * @Description:
 */
public class AlarmLevel {

    private String id;                      //id
    private String uniqueCode;              //所属企业
    private List<String> sourceLevelList;   //原等级列表字符串（12345），不添加“，”号等分隔符
    private String targetLevel;             //目标等级，一条自定义只包含一个目标等级，否则不好判定目标等级重复定义
    private Date tCreate;                   //创建时间
    private FacadeView creator;             //创建者

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUniqueCode() {
        return uniqueCode;
    }

    public void setUniqueCode(String uniqueCode) {
        this.uniqueCode = uniqueCode;
    }

    public Date gettCreate() {
        return tCreate;
    }

    public void settCreate(Date tCreate) {
        this.tCreate = tCreate;
    }

    public FacadeView getCreator() {
        return creator;
    }

    public void setCreator(FacadeView creator) {
        this.creator = creator;
    }

    public List<String> getSourceLevelList() {
        return sourceLevelList;
    }

    public void setSourceLevelList(List<String> sourceLevelList) {
        this.sourceLevelList = sourceLevelList;
    }

    public String getTargetLevel() {
        return targetLevel;
    }

    public void setTargetLevel(String targetLevel) {
        this.targetLevel = targetLevel;
    }
}
