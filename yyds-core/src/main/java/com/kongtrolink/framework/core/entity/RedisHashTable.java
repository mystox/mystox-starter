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
    public final static String SN_DEV_ID_ALARM_BEGIN_DELAY_HASH = "sn_dev_id_alarm_begin_delay_hash";  //告警开始延迟记录表
    public final static String SN_DEV_ID_ALARM_END_DELAY_HASH = "sn_dev_id_alarm_end_delay_hash";  //告警结束延迟记录表
    public final static String HIGHRATE_SN_HASH  = "sn_highrage_hash_";  //告警高频过滤表


}
