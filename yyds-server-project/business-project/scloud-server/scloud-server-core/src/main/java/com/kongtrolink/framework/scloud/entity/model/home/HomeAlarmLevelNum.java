package com.kongtrolink.framework.scloud.entity.model.home;

import java.io.Serializable;
import java.util.Map;

/**
 * 首页 实时告警统计 根据 告警级别汇总
 * Created by Mg on 2020年4月10日
 */
public class HomeAlarmLevelNum implements Serializable{

    private static final long serialVersionUID = 221568752788867023L;

    private Map<String,Integer> levelMap; //对应告警级别的数量

    private int confirm;//确认告警数量

    private int unConfirm;//未确认告警数量

    public Map<String, Integer> getLevelMap() {
        return levelMap;
    }

    public void setLevelMap(Map<String, Integer> levelMap) {
        this.levelMap = levelMap;
    }

    public int getConfirm() {
        return confirm;
    }

    public void setConfirm(int confirm) {
        this.confirm = confirm;
    }

    public int getUnConfirm() {
        return unConfirm;
    }

    public void setUnConfirm(int unConfirm) {
        this.unConfirm = unConfirm;
    }
}
