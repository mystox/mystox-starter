package com.kongtrolink.framework.config;

import com.kongtrolink.framework.mqtt.OperateEntity;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Auther: liudd
 * @Date: 2019/10/31 19:11
 * @Description:告警消除操作
 */
@Configuration
@ConfigurationProperties("resloveOperate")
@RefreshScope
public class RecoverOperateConfig {

    private Map<String, List<OperateEntity>> enterServeOperaListMap = new HashMap<>();
    private List<OperateEntity> resloveOperate;

    public Map<String, List<OperateEntity>> getEnterServeOperaListMap() {
        return enterServeOperaListMap;
    }

    public List<OperateEntity> getResloveOperate() {
        return resloveOperate;
    }

    public void setResloveOperate(List<OperateEntity> resloveOperate) {
        this.resloveOperate = resloveOperate;
    }

    /**
     * @auther: liudd
     * @date: 2019/10/30 14:53
     * 功能描述:初始化
     */
    public void initConfigMap(){
        if(null != resloveOperate && resloveOperate.size() >0){
            for(OperateEntity operateEntity : resloveOperate){
                String enterServerCode = operateEntity.getEnterServerCode();
                List<OperateEntity> operateEntityList = enterServeOperaListMap.get(enterServerCode);
                if(null == operateEntity){
                    operateEntityList = new ArrayList<>();
                }
                operateEntityList.add(operateEntity);
                enterServeOperaListMap.put(enterServerCode, operateEntityList);
            }
        }
    }
}
