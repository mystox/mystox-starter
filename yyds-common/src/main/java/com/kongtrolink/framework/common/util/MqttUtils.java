package com.kongtrolink.framework.common.util;

import com.kongtrolink.framework.entity.TopicPrefix;

/**
 * Created by mystoxlol on 2019/9/3, 9:26.
 * company: kongtrolink
 * description:
 * update record:
 */
public class MqttUtils {
    public static String preconditionSubTopicId(String serverCode, String operaCode) {
        return TopicPrefix.SUB_PREFIX + "/" + serverCode + "/" + operaCode;
    }

    public static String preconditionServerCode(String serverName, String version) {
        return serverName + "_" + version;
    }
}
