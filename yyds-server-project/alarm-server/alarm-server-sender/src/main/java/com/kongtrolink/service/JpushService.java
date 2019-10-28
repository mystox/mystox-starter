package com.kongtrolink.service;

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
import com.kongtrolink.framework.base.Contant;
import com.kongtrolink.framework.enttiy.InformMsg;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.util.HashSet;
import java.util.Set;

/**
 * @Auther: liudd
 * @Date: 2019/10/26 10:00
 * @Description:
 */
@Service
public class JpushService {

    @Value("${jpush.appKey}")
    String APP_KEY;
    @Value("${jpush.masterSecret}")
    String MASTER_SECRET;
    @Value("${jpush.production}")
    private String APNS_PRODUCTION;
    private static final Logger LOGGER = LoggerFactory.getLogger(JpushService.class);

    public boolean pushApp(InformMsg informMsg) {
        boolean pushSuccess = true;
        Set<String> userIds = new HashSet<>();
        userIds.add(informMsg.getInformAccount());
        // 推送至义益云监控
        if (userIds != null && userIds.size() > 0) {
            PushPayload payload = buildPayload(informMsg, userIds);
            PushResult result = sendPush(payload);
            if (result == null || result.isResultOK() == false) {
                pushSuccess = false;
            }
        }
        LOGGER.info("AlarmId: {}, yiyiUserIds: {}, pushSuccess: {}", informMsg.getAlarmName(), userIds, pushSuccess);
        return true;
    }

    private PushPayload buildPayload(InformMsg informMsg, Set<String> userIds) {
        String title = "[告警已消除]";
        Integer num = Integer.parseInt(informMsg.getTempCode());
        if (Contant.ALARM_STATE_REPORT.equals(informMsg.getAlarmStateType())) {
            title = "[新告警提醒]";
        }
        StringBuilder builder = new StringBuilder();
        String titleInfo = informMsg.getAddress() + "-" + informMsg.getAlarmName();
        builder.append(title).append(titleInfo);
        // 是否向产品环境推送
        boolean apnsProduction = Boolean.parseBoolean(APNS_PRODUCTION);
        PushPayload payload = PushPayload.newBuilder()
                .setPlatform(Platform.all())
                .setAudience(Audience.alias(userIds))
                .setOptions(Options.newBuilder().setApnsProduction(apnsProduction).build())
                .setNotification(Notification.newBuilder()
                        .setAlert(builder.toString())
                        .addPlatformNotification(AndroidNotification.newBuilder()
                                .addExtra(Contant.TYPE, num)
                                .setTitle(title).build())
                        .addPlatformNotification(IosNotification.newBuilder()
                                .addExtra(Contant.TYPE, num)
                                .incrBadge(1).build())
                        .build())
                .build();
        return payload;
    }

    public PushResult sendPush(PushPayload payload) {
        ClientConfig clientConfig = ClientConfig.getInstance();
        JPushClient jpushClient;
        jpushClient = new JPushClient(MASTER_SECRET, APP_KEY, null, clientConfig);
        PushResult result = null;
        try {
            result = jpushClient.sendPush(payload);
            LOGGER.info("Push Result:{}", result);
        } catch (APIConnectionException ex) {
            LOGGER.error("Connection Error. Message:{}", ex);
        } catch (APIRequestException ex) {
            LOGGER.error("Response Error from JPush Server. Http status:{}. Error Code:{}. Error Message:{}. Msg ID:{}.",
                    ex.getStatus(), ex.getErrorCode(), ex.getErrorMessage(), ex.getMsgId());
        }

        return result;
    }
}
