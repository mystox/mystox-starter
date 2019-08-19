package com.kongtrolink.framework.service;

import com.alibaba.fastjson.JSONObject;
import com.kongtrolink.framework.core.entity.PktType;
import com.kongtrolink.framework.core.entity.StateCode;
import com.kongtrolink.framework.entity.xml.base.Message;
import com.kongtrolink.framework.entity.xml.msg.SendAlarm;
import com.kongtrolink.framework.entity.xml.msg.TAlarm;
import com.kongtrolink.framework.entity.xml.msg.XmlList;
import com.kongtrolink.framework.entity.xml.util.MessageUtil;
import com.kongtrolink.framework.execute.module.model.RedisOnlineInfo;
import com.kongtrolink.framework.entity.CntbPktTypeTable;
import com.kongtrolink.framework.execute.module.dao.AlarmLogDao;
import com.kongtrolink.framework.execute.module.model.RedisAlarm;
import com.kongtrolink.framework.entity.xml.msg.SendAlarmAck;
import com.kongtrolink.framework.utils.CommonUtils;
import com.kongtrolink.framework.utils.LogUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import javax.xml.bind.JAXBException;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author fengw
 * 铁塔上报告警服务
 * 新建文件 2019-4-22 15:53:02
 */
@Service
public class CntbAlarmService {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    RpcService rpcService;
    @Autowired
    CommonUtils commonUtils;
    @Autowired
    AlarmLogDao alarmLogDao;
    @Autowired
    LogUtils logUtils;

    /**
     * 开始告警
     * @param sn sn
     */
    public void startAlarm(String sn) {
        RedisOnlineInfo redisOnlineInfo = commonUtils.getRedisOnlineInfo(sn);
        try {
            if (redisOnlineInfo == null || !redisOnlineInfo.isOnline()) {
                //未获取到redis中在线信息或铁塔平台离线，不上报告警
                return;
            }

            List<RedisAlarm> redisAlarmList = commonUtils.getRedisAlarmList(redisOnlineInfo.getFsuId());

            try {
                for (int i = redisAlarmList.size() - 1; i >= 0; --i) {
                    RedisAlarm redisAlarm = redisAlarmList.get(i);
                    if (!checkReport(redisOnlineInfo, redisAlarm)) {
                        redisAlarmList.remove(i);
                        continue;
                    }
                    logger.info("-----------------------alarm start-----------------------" + JSONObject.toJSONString(redisAlarm));
                }

                if (redisAlarmList.size() == 0) {
                    return;
                }

                sendAlarm(redisOnlineInfo, redisAlarmList);
            } finally {
                for (RedisAlarm redisAlarm : redisAlarmList) {
                    if (!redisAlarm.isEndReported()) {
                        commonUtils.setRedisAlarm(redisAlarm);
                        logger.info("-----------------------alarm finally-----------------------" + JSONObject.toJSONString(redisAlarm));
                    }
                }
            }

            for (RedisAlarm redisAlarm : redisAlarmList) {
                if (!alarmLogDao.upsert(redisAlarm)) {
                    logUtils.saveLog("", "", PktType.ALARM_REGISTER, StateCode.MONGO_ERROR);
                    logger.error("SendAlarm:告警保存失败,redisAlarm:" + JSONObject.toJSONString(redisAlarm));
                }
            }
        } catch (Exception e) {
            logUtils.saveLog("", sn, PktType.ALARM_REGISTER, StateCode.FAILED);
            logger.error("上报告警过程中出现异常:" + JSONObject.toJSONString(e));
        }
    }

    /**
     * 检查告警是否可上报
     * @param redisOnlineInfo redis中在线信息
     * @param redisAlarm 告警信息
     * @return 是否上报
     */
    private boolean checkReport(RedisOnlineInfo redisOnlineInfo, RedisAlarm redisAlarm) {

        if (redisAlarm.getAlarmFlag().equals(RedisAlarm.BEGIN) && redisAlarm.isStartReported()) {
            //当前告警状态为BEGIN且已成功上报，则不再上报
            return false;
        }
        if (redisAlarm.getAlarmFlag().equals(RedisAlarm.END) && redisAlarm.isEndReported()) {
            //当前告警状态为END且已成功上报，则不再上报并从redis中删除
            commonUtils.delRedisAlarm(redisAlarm.getFsuId(), redisAlarm.getSerialNo());
            return false;
        }
        if (redisAlarm.getAlarmFlag().equals(RedisAlarm.END) && !redisAlarm.isStartReported()) {
            //如果告警已结束且开始告警还未上报，则不再上报并从redis中删除
            commonUtils.delRedisAlarm(redisAlarm.getFsuId(), redisAlarm.getSerialNo());
            return false;
        }

        if (redisAlarm.getLastReportTime() + 24 * 60 * 60 > System.currentTimeMillis() / 1000) {
            //上次上报时间距离当前时间超过24小时，则上报次数清0，返回true
            redisAlarm.setReportCount(0);
        }

        //上次上报时间+上报间隔 大于 当前时间 不上报告警
        //上报次数大于上报上限 不上报告警
        return (redisAlarm.getLastReportTime() + redisOnlineInfo.getAlarmInterval() < System.currentTimeMillis() / 1000) &&
                redisAlarm.getReportCount() <= redisOnlineInfo.getAlarmReportLimit();
    }

    /**
     * 向铁塔平台发送告警
     * @param redisOnlineInfo redis中在线信息
     * @param redisAlarmList 告警信息
     */
    private void sendAlarm(RedisOnlineInfo redisOnlineInfo, List<RedisAlarm> redisAlarmList) {

        List<TAlarm> tAlarmList = new ArrayList<>();
        for (RedisAlarm redisAlarm : redisAlarmList) {
            TAlarm tAlarm = new TAlarm();
            tAlarm.setId(redisAlarm.getId());
            tAlarm.setSerialNo(redisAlarm.getSerialNo());
            tAlarm.setFsuId(redisAlarm.getFsuId());
            tAlarm.setFsuCode(redisAlarm.getFsuId());
            tAlarm.setDeviceId(redisAlarm.getDeviceId());
            tAlarm.setDeviceCode(redisAlarm.getDeviceId());
            tAlarm.setAlarmLevel(redisAlarm.getAlarmLevel());
            tAlarm.setAlarmDesc(redisAlarm.getCompleteDesc());
            if (!redisAlarm.isStartReported()) {
                tAlarm.setAlarmTime(redisAlarm.getStartTime());
                tAlarm.setAlarmFlag(RedisAlarm.BEGIN);
            } else if (!redisAlarm.isEndReported()) {
                tAlarm.setAlarmTime(redisAlarm.getEndTime());
                tAlarm.setAlarmFlag(RedisAlarm.END);
            } else {
                continue;
            }
            tAlarmList.add(tAlarm);
        }

        SendAlarm sendAlarm = new SendAlarm();
        sendAlarm.getValue().setAlarmListList(new ArrayList<>());
        sendAlarm.getValue().getAlarmListList().add(new XmlList());
        sendAlarm.getValue().getAlarmListList().get(0).settAlarmList(tAlarmList);

        String reqXmlMsg = getXmlMsg(sendAlarm);
        if (null == reqXmlMsg) {
            return;
        }

        JSONObject jsonResponse = rpcService.requestCntb(reqXmlMsg, redisOnlineInfo.getAlarmIp(), 8080);

        boolean result = false;
        if (jsonResponse != null && jsonResponse.containsKey("result")
                && (jsonResponse.getInteger("result") == 1)) {
            String resXmlMsg = jsonResponse.getString("msg");
            SendAlarmAck sendAlarmAck = analyzeMsg(resXmlMsg);

            if (sendAlarmAck.getResult() == 1) {
                result = true;
            }
        }

        for (RedisAlarm redisAlarm : redisAlarmList) {
            redisAlarm.setLastReportTime(System.currentTimeMillis() / 1000);
            if (result) {
                redisAlarm.setLastReportTime(0);
                redisAlarm.setReportCount(0);
                if (!redisAlarm.isStartReported()) {
                    redisAlarm.setStartReported(true);
                    logger.info("-----------------------alarm begin success-----------------------" + JSONObject.toJSONString(redisAlarm));
                } else if (!redisAlarm.isEndReported()) {
                    redisAlarm.setEndReported(true);
                    commonUtils.delRedisAlarm(redisAlarm.getFsuId(), redisAlarm.getSerialNo());
                    logger.info("-----------------------alarm end success-----------------------" + JSONObject.toJSONString(redisAlarm));
                    continue;
                } else {
                    continue;
                }
            } else {
                redisAlarm.setReportCount(redisAlarm.getReportCount() + 1);
                logger.info("-----------------------alarm reported fail-----------------------" + JSONObject.toJSONString(redisAlarm));
            }
            commonUtils.setRedisAlarm(redisAlarm);
        }
    }

    /**
     * 获取Xml报文
     * @param sendAlarm 告警信息
     * @return Xml报文字符串
     */
    private String getXmlMsg(SendAlarm sendAlarm) {
        String result = "";

        try {
            Message message = new Message(sendAlarm);
            result = MessageUtil.messageToString(message);
        } catch (Exception ex) {
            logUtils.saveLog("", "", CntbPktTypeTable.SEND_ALARM, StateCode.XML_ILLEGAL);
            logger.error("GetXmlMsg: 实体类转xml失败,msg:" + JSONObject.toJSONString(sendAlarm));
        }

        return result;
    }

    /**
     * 解析Xml报文字符串
     * @param xml Xml报文字符串
     * @return 回复信息
     */
    private SendAlarmAck analyzeMsg(String xml) {
        SendAlarmAck result = null;

        try {
            Document doc = MessageUtil.stringToDocument(xml);
            String pkType = doc.getElementsByTagName("Name").item(0).getTextContent();
            int code = Integer.parseInt(doc.getElementsByTagName("Code").item(0).getTextContent());

            if ((pkType.equals(CntbPktTypeTable.SEND_ALARM_ACK)) && (code == CntbPktTypeTable.SEND_ALARM_ACK_CODE)) {
                Node reqInfoNode = doc.getElementsByTagName("Info").item(0);
                String reqInfoStr = MessageUtil.infoNodeToString(reqInfoNode);
                result = (SendAlarmAck)MessageUtil.stringToMessage(reqInfoStr, SendAlarmAck.class);
            }
        } catch (ParserConfigurationException | SAXException | IOException e) {
            logUtils.saveLog("", "", CntbPktTypeTable.SEND_ALARM_ACK, StateCode.XML_ILLEGAL);
            logger.error("AnalyzeXmlMsg: String转Document失败,xml:" + xml);
        } catch (JAXBException e) {
            logUtils.saveLog("", "", CntbPktTypeTable.SEND_ALARM_ACK, StateCode.XML_ILLEGAL);
            logger.error("GetXmlMsg: String转实体类失败,xml" + xml);
        }

        return result;
    }
}
