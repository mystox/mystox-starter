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
    public final static String SN_DATA_HASH = "sn_data_hash";       //实时数据{sn1:data}
    public final static String SN_DEV_ID_ALARMSIGNAL_HASH ="sn_dev_id_alarmsignal_hash";    //信号点下对应的所有告警点信息
    public final static String SN_ALARM_HASH = "sn__alarm_hash";  //实时告警{sn1:alarmObjectList}
}
