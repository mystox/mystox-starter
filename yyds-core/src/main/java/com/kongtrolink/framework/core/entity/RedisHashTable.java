package com.kongtrolink.framework.core.entity;

/**
 * Created by mystoxlol on 2019/3/27, 9:35.
 * company: kongtrolink
 * description:
 * update record:
 */
public class RedisHashTable
{
    public final static String COMMUNICATION_HASH = "communication_hash"; //SN1:{GWip：ip1；UUID（连接编号）:id1；BIP ：bip（业务服务地址）,STATUS:0(未注册)1（已注册）}
    public final static String SN_DEVICE_LIST_HASH = "sn_device_list_hash";//  SN1:{ deviceId1, deviceId2...};
    public final static String SN_DATA_HASH = "data_hash_";       //实时数据{sn1:data}
    public final static String SN_DEV_ID_ALARM_SIGNAL_HASH ="sn_dev_id_alarm_signal_hash";    //信号点下对应的所有告警点信息
    public final static String SN_ALARM_HASH = "sn_alarm_hash_";  //实时告警{sn1:alarmObjectList}
    public final static String SN_BEGIN_DELAY_ALARM_HASH = "sn_begin_delay_hash";  //告警开始延迟记录表
    public final static String HIGHRATE_SN_HASH  = "sn_highrage_";  //告警高频过滤表
    public final static String REPEAT_SN_HASH  = "sn_repeat_";  //重复延迟过滤表
    public final static String SN_ALARM_NUM_HASH = "sn_alarm_num_hash";     //告警序列号（SN内唯一，不需要用自增）
    public final static String SN_MSGID = "sn_msgId";     //消息的唯一标识

}
