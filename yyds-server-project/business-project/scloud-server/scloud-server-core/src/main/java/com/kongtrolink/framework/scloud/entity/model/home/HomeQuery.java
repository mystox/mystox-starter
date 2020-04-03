package com.kongtrolink.framework.scloud.entity.model.home;

import java.io.Serializable;
import java.util.List;

/**
 * 首页 查询参数
 * Created by Mg on 2018/5/11.
 */
public class HomeQuery implements Serializable{

    private static final long serialVersionUID = 2364848418917007425L;

    private String tierCode; // 区域code

    public String getTierCode() {
        return tierCode;
    }

    public void setTierCode(String tierCode) {
        this.tierCode = tierCode;
    }

}
