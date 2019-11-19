package com.kongtrolink.framework.base;

import com.kongtrolink.framework.mqtt.OperateEntity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Auther: liudd
 * @Date: 2019/11/1 10:43
 * @Description:yml配置文件操作实体类
 */
public class OperateConfig {

    Map<String, List<OperateEntity>> enterServeOperaListMap = new HashMap<>();
    List<OperateEntity> operate;

    public Map<String, List<OperateEntity>> getEnterServeOperaListMap() {
        //判断当前是否初始化过
        List<OperateEntity> operate = getOperate();
        if(null == enterServeOperaListMap || enterServeOperaListMap.size()==0){
            if(null != operate && operate.size()>0){
                initConfigMap();
            }
        }
        return enterServeOperaListMap;
    }

    public List<OperateEntity> getOperate() {
        return operate;
    }

    public void setOperate(List<OperateEntity> operate) {
        this.operate = operate;
    }

    /**
     * @auther: liudd
     * @date: 2019/10/30 14:53
     * 功能描述:初始化
     */
    public void initConfigMap(){
        if(null != operate && operate.size() >0){
            for(OperateEntity operateEntity : operate){
                String enterServerCode = operateEntity.getEnterServerCode();
                if(StringUtil.isNUll(enterServerCode)){
                    continue;
                }
                List<OperateEntity> operateEntityList = enterServeOperaListMap.get(enterServerCode);
                if(null == operateEntityList){
                    operateEntityList = new ArrayList<>();
                }
                operateEntityList.add(operateEntity);
                enterServeOperaListMap.put(enterServerCode, operateEntityList);
            }
        }
    }
}
