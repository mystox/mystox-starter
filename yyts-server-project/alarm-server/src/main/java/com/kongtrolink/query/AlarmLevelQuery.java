package com.kongtrolink.query;

import com.kongtrolink.base.Paging;

import java.util.List;

/**
 * @Auther: liudd
 * @Date: 2019/9/11 15:06
 * @Description:
 */
public class AlarmLevelQuery extends Paging {

    private String id;                 //id
    private String uniqueCode;      //所属企业
    private List<String> sourceLevelList;    //原等级列表字符串（2,3,5...）
    private String targetLevel;    //目标等级

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
