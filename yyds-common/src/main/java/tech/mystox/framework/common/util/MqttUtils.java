package tech.mystox.framework.common.util;

import tech.mystox.framework.entity.TopicPrefix;

/**
 * Created by mystoxlol on 2019/9/3, 9:26.
 * company: mystox
 * description:
 * update record:
 */
public class MqttUtils {
    public static String preconditionSubTopicId(String serverCode, String operaCode) {
        return TopicPrefix.SUB_PREFIX + "/" + serverCode + "/" + operaCode;
    }

    // public static String preconditionPubTopicId(String serverCode, String operaCode) {
    //     return TopicPrefix.PUB_PREFIX + "/" + serverCode + "/" + operaCode;
    // }

    public static String preconditionServerCode(String serverName, String version) {
        return serverName + "_" + version;
    }

    /**
     * @return java.lang.String
     * @Date 10:02 2020/1/3
     * @Param No such property: code for class: Script1
     * @Author mystox
     * @Description // GROUP_CODE/SERVER_NAME_VERSION
     **/
    public static String preconditionGroupServerCode(String groupCode, String serverCode) {
        return groupCode + "/" + serverCode;
    }

    /**
     * @return java.lang.String
     * @Date 9:50 2020/1/3
     * @Param No such property: code for class: Script1
     * @Author mystox
     * @Description //完整路径 /mqtt/operaRoute/groupCode/serverCode/operaCode
     **/
    public static String preconditionRoutePath(String groupCodeServerCode, String operaCode) {
        return preconditionOperaCodePath(preconditionGroupServerPath(TopicPrefix.OPERA_ROUTE, groupCodeServerCode), operaCode);
    }

    /**
     * @return java.lang.String
     * @Date 14:34 2020/1/4
     * @Param No such property: code for class: Script1
     * @Author mystox
     * @Description 根据不同的目录构建服务路径
     **/
    public static String preconditionGroupServerPath(String topicPrefix, String groupServerCode) {
        return topicPrefix + "/" + groupServerCode;
    }


    /**
     * @return java.lang.String
     * @Date 14:35 2020/1/4
     * @Param No such property: code for class: Script1
     * @Author mystox
     * @Description 构建操作码路径
     **/
    public static String preconditionOperaCodePath(String groupServerPath, String operaCode) {
        return groupServerPath + "/" + operaCode;
    }

    /**
     * @return java.lang.String
     * @Date 15:24 2020/1/4
     * @Param No such property: code for class: Script1
     * @Author mystox
     * @Description 构建服务的统一ack topic 格式样例 /mqtt/sub/GROUP_CODE/SERVER_CODE/+/ack
     **/
    public static String preconditionSubACKTopicId(String groupServerCode) {
        return preconditionGroupServerPath(TopicPrefix.SUB_PREFIX, groupServerCode) +"/+"+ "/ack";
    }
}
