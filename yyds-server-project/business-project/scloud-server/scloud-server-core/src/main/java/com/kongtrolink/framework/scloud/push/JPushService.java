/**
 * *****************************************************
 * Copyright (C) Kongtrolink techology Co.ltd - All Rights Reserved
 *
 * This file is part of Kongtrolink techology Co.Ltd property.
 *
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 ******************************************************
 */
package com.kongtrolink.framework.scloud.push;

import cn.jpush.api.JPushClient;
import cn.jpush.api.common.ClientConfig;
import cn.jpush.api.common.resp.APIConnectionException;
import cn.jpush.api.common.resp.APIRequestException;
import cn.jpush.api.push.PushResult;
import cn.jpush.api.push.model.Options;
import cn.jpush.api.push.model.Platform;
import cn.jpush.api.push.model.PushPayload;
import cn.jpush.api.push.model.audience.Audience;
import cn.jpush.api.push.model.notification.AndroidNotification;
import cn.jpush.api.push.model.notification.IosNotification;
import cn.jpush.api.push.model.notification.Notification;
import com.alibaba.fastjson.JSONObject;
import com.kongtrolink.framework.scloud.entity.PushEntity;
import com.kongtrolink.framework.scloud.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
/**
 *
 * @author Mosaico
 */
@Service
public class JPushService {
    private static final Logger LOGGER =  LoggerFactory.getLogger(JPushService.class);

    public JSONObject push(PushEntity pushEntity){
        JSONObject jsonObject = new JSONObject();
        if(!checnPara(jsonObject, pushEntity)){
            return jsonObject;
        }
        JPushClient jpushClient = new JPushClient(pushEntity.getPushSecret(), pushEntity.getPushKey(), null, ClientConfig.getInstance());
        PushPayload pushPayload = buildPayload(pushEntity);
        try {
            PushResult result  = jpushClient.sendPush(pushPayload);
            if (result == null || result.isResultOK() == false) {
                jsonObject.put("result", false);
            }else{
                jsonObject.put("result", true);
            }
            jsonObject.put("info", result);
        } catch (APIConnectionException ex) {
            jsonObject.put("result", false);
            jsonObject.put("info", ex.getMessage());
        } catch (APIRequestException ex) {
            jsonObject.put("result", false);
            jsonObject.put("info", ex.getMessage());
        }
        return jsonObject;
    }

    private boolean checnPara(JSONObject jsonObject, PushEntity pushEntity){
        boolean paraResult = true;
        StringBuilder info = new StringBuilder();
        if(null == pushEntity){
            info.append("pushEntity为空");
            paraResult = false;
        }
        if(StringUtil.isNUll(pushEntity.getTitle())){
            info.append(",title为空");
            paraResult = false;
        }
        if(StringUtil.isNUll(pushEntity.getContent())){
            info.append(",content为空");
            paraResult = false;
        }
        if(null == pushEntity.getAccountList() || pushEntity.getAccountList().size() == 0){
            info.append(",accountList为空或size为0");
            paraResult = false;
        }
        if(StringUtil.isNUll(pushEntity.getKeyName())){
            info.append(",keyName为空");
            paraResult = false;
        }
        if(StringUtil.isNUll(pushEntity.getKeyValue())){
            info.append(",keyValue为空");
            paraResult = false;
        }
        if(StringUtil.isNUll(pushEntity.getProName())){
            info.append(",proName为空");
            paraResult = false;
        }
        if(null == pushEntity.getProValue()){
            info.append(",proValue为空");
            paraResult = false;
        }
        if(StringUtil.isNUll(pushEntity.getPushType())){
            info.append(",pushType为空");
            paraResult = false;
        }
        if(StringUtil.isNUll(pushEntity.getPushKey())){
            info.append(",pushKey为空");
            paraResult = false;
        }
        if(StringUtil.isNUll(pushEntity.getPushSecret())){
            info.append(",pushKey为空");
            paraResult = false;
        }
        jsonObject.put("result", paraResult);
        return paraResult;
    }

    private PushPayload buildPayload(PushEntity pushEntity) {
        String title = pushEntity.getTitle();
        StringBuilder builder = new StringBuilder();
        builder.append(pushEntity.getTitle()).append(pushEntity.getContent());
        PushPayload payload = PushPayload.newBuilder()
                .setPlatform(Platform.all())
                .setAudience(Audience.alias(pushEntity.getAccountList()))
                .setOptions(Options.newBuilder().setApnsProduction(pushEntity.isProduct()).build())
                .setNotification(Notification.newBuilder()
                        .setAlert(builder.toString())
                        .addPlatformNotification(AndroidNotification.newBuilder()
                                .addExtra(pushEntity.getProName(), pushEntity.getProValue())
                                .addExtra(pushEntity.getKeyName(), pushEntity.getKeyValue())
                                .setTitle(title).build())
                        .addPlatformNotification(IosNotification.newBuilder()
                                .addExtra(pushEntity.getProName(), pushEntity.getProValue())
                                .addExtra(pushEntity.getKeyName(), pushEntity.getKeyValue())
                                .incrBadge(1).build())
                        .build())
                .build();
        return payload;
    }
}
