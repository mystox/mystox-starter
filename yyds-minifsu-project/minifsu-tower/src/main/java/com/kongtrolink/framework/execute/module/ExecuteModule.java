package com.kongtrolink.framework.execute.module;

import com.alibaba.fastjson.JSONObject;
import com.kongtrolink.framework.core.entity.Log;
import com.kongtrolink.framework.core.entity.ModuleMsg;
import com.kongtrolink.framework.core.entity.PktType;
import com.kongtrolink.framework.core.entity.StateCode;
import com.kongtrolink.framework.core.protobuf.RpcNotifyProto;
import com.kongtrolink.framework.core.protobuf.protorpc.RpcNotifyImpl;
import com.kongtrolink.framework.core.service.ModuleInterface;
import com.kongtrolink.framework.entity.xml.util.MessageUtil;
import com.kongtrolink.framework.execute.module.dao.LogDao;
import com.kongtrolink.framework.entity.CntbPktTypeTable;
import com.kongtrolink.framework.service.TowerService;
import com.kongtrolink.framework.utils.CommonUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.util.Date;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Created by mystoxlol on 2019/2/25, 19:25.
 * company: kongtrolink
 * description:
 * update record:
 */
@Service
public class ExecuteModule extends RpcNotifyImpl implements ModuleInterface
{

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private ThreadPoolTaskExecutor taskExecutor;

    @Autowired
    private TowerService towerService;

    @Autowired
    CommonUtils commonUtils;

    private ConcurrentHashMap<String, ConcurrentLinkedQueue<ModuleMsg>> msgHashMap = new ConcurrentHashMap();

    @Autowired
    LogDao logDao;

    @Value("${spring.redis.lockTimeout}")
    private int lockTimeout;
    @Value("${server.bindIp}")
    private String host;
    @Value("${server.name}")
    private String name;

    @Override
    public boolean init()
    {
        logger.info("workerExecute-execute module init");
        //初始化线程池之类的基础任务服务
        return true;
    }

    /**
     * 默认业务程序执行体
     * @param
     * @return
     */
    @Override
    protected RpcNotifyProto.RpcMessage execute(String msgId, String payload)
    {
        JSONObject response = new JSONObject();
        boolean result = true;

        ModuleMsg moduleMsg = JSONObject.parseObject(payload, ModuleMsg.class);

        //若为铁塔平台发送请求，则scMessage方法的返回值为向铁塔返回的xml报文，若为null则返回失败
        if (moduleMsg.getPktType().equals(CntbPktTypeTable.GW_SERVICE)) {
            String responseMsg = scMessage(moduleMsg.getPayload());
            response.put("msg", responseMsg);
            result = (responseMsg != null);
        } else {
            ConcurrentLinkedQueue<ModuleMsg> msgQueue = new ConcurrentLinkedQueue();
            ConcurrentLinkedQueue<ModuleMsg> curQueue = msgHashMap.putIfAbsent(moduleMsg.getSN(), msgQueue);
            if (curQueue == null) {
                curQueue = msgQueue;
            }
            curQueue.add(moduleMsg);

            logger.info("execute: receive msg: " + payload + ", msgHashMap.size: " + msgHashMap.size() + ", queue.size:" + msgHashMap.get(moduleMsg.getSN()).size());
            taskExecutor.execute(() -> handleMsg(moduleMsg.getSN()));
        }

        response.put("result", result ? 1 : 0);

        return RpcNotifyProto.RpcMessage.newBuilder()
                .setType(RpcNotifyProto.MessageType.RESPONSE)
                .setPayloadType(RpcNotifyProto.PayloadType.JSON)
                .setPayload(response.toJSONString())
                .setMsgId(StringUtils.isBlank(msgId)?"":msgId)
                .build();
    }

    private void handleMsg(String sn) {
        boolean result = false;

        String lock = null;

        try {
            if (!msgHashMap.containsKey(sn)) {
                //该sn没有对应消息，直接返回
                return;
            }

            ConcurrentLinkedQueue<ModuleMsg> msgQueue = msgHashMap.get(sn);
            if (msgQueue.size() == 0) {
                return;
            }

            lock = commonUtils.getRedisLock(sn, lockTimeout);
            if (lock == null) {
                //未取得redis锁，直接返回
                logger.info("handleMsg: getRedisLock failed, sn: " + sn + ", msgHashMap.size: " + msgHashMap.size() + ", queue.size:" + msgHashMap.get(sn).size());
                return;
            }
            logger.info("handleMsg: getRedisLock success, sn: " + sn + ", lock: " + lock);

            ModuleMsg moduleMsg = msgQueue.poll();
            if (moduleMsg == null) {
                return;
            }

            logger.info("handleMsg: lock: " + lock + ", msg: " + JSONObject.toJSONString(moduleMsg));
            JSONObject infoPayload = moduleMsg.getPayload();

            try {
                //处理请求
                switch (moduleMsg.getPktType()) {
                    case PktType.FSU_BIND:
                        result = towerService.fsuBind(moduleMsg.getSN(), infoPayload);
                        break;
                    case PktType.TERMINAL_UNBIND:
                        result = towerService.fsuUnbind(moduleMsg.getSN());
                        break;
                    case PktType.DATA_CHANGE:
                    case PktType.DATA_REPORT:
                        towerService.refreshTowerOnlineInfo(moduleMsg.getSN(), infoPayload);
                        towerService.checkAlarm(moduleMsg.getSN());
                        result = towerService.rcvData(moduleMsg.getSN(), infoPayload);
                        towerService.saveHisData(moduleMsg.getSN());
                        break;
                    case PktType.DATA_STATUS:
                        towerService.refreshTowerOnlineInfo(moduleMsg.getSN(), infoPayload);
                        towerService.checkAlarm(moduleMsg.getSN());
                        result = towerService.rcvFsuInfo(moduleMsg.getSN(), infoPayload);
                        towerService.saveHisData(moduleMsg.getSN());
                        break;
                    case PktType.ALARM_REGISTER:
                        towerService.refreshTowerOnlineInfo(moduleMsg.getSN(), infoPayload);
                        result = towerService.rcvAlarm(moduleMsg.getSN(), infoPayload);
                        towerService.checkAlarm(moduleMsg.getSN());
                        towerService.saveHisData(moduleMsg.getSN());
                        break;
                    default:
                        towerService.refreshTowerOnlineInfo(moduleMsg.getSN(), infoPayload);
                        towerService.checkAlarm(moduleMsg.getSN());
                        towerService.saveHisData(moduleMsg.getSN());
                        result = true;
                        break;
                }
            } catch (Exception e) {
                saveLog(moduleMsg.getMsgId(), moduleMsg.getSN(), moduleMsg.getPktType(), StateCode.FAILED);
                logger.error("处理请求异常：PktType:" + moduleMsg.getPktType() + ",payload:" +
                        moduleMsg.getPayload() + ",Exception:" + JSONObject.toJSONString(e));
            }


            if (!result) {
                logger.error("处理请求失败：PktType:" + moduleMsg.getPktType() + ",payload:" +
                        moduleMsg.getPayload() + ",Exception:" + "handleMsg");
            }

        } catch (Exception e) {
            logger.error("处理请求异常 ,Exception:" + JSONObject.toJSONString(e));
        } finally {
            if (lock != null) {
                commonUtils.redisUnlock(sn, lock);
                logger.info("handleMsg: redisUnlock success, sn: " + sn + ",lock: " + lock);
                handleMsg(sn);
            }
        }
    }

    /**
     * 检查铁塔网关发送来的信息，并根据类型进行处理
     * @param infoPayload 请求信息
     * @return 回复报文，若为null，则不回复
     */
    private String scMessage(JSONObject infoPayload) {
        String result = null;
        try {
            Document doc = MessageUtil.stringToDocument(infoPayload.getString("msg"));
            String type = doc.getElementsByTagName("Name").item(0).getTextContent();
            int code = Integer.parseInt(doc.getElementsByTagName("Code").item(0).getTextContent());
            Node reqInfoNode = doc.getElementsByTagName("Info").item(0);
            String request = MessageUtil.infoNodeToString(reqInfoNode);

            if (type.equals(CntbPktTypeTable.GET_FSUINFO) && code == CntbPktTypeTable.GET_FSUINFO_CODE) {
                result = towerService.cntbGetFsuInfo(request);
            } else if (type.equals(CntbPktTypeTable.TIME_CHECK) && code == CntbPktTypeTable.TIME_CHECK_CODE) {
                result = towerService.cntbTimeCheck();
            } else if (type.equals(CntbPktTypeTable.GET_DATA) && code == CntbPktTypeTable.GET_DATA_CODE) {
                result = towerService.cntbGetData(request);
            } else if (type.equals(CntbPktTypeTable.SET_POINT) && code == CntbPktTypeTable.SET_POINT_CODE) {
                result = towerService.cntbSetPoint(request);
            } else if (type.equals(CntbPktTypeTable.GET_THRESHOLD) && code == CntbPktTypeTable.GET_THRESHOLD_CODE) {
                result = towerService.cntbGetThreshold(request);
            } else if (type.equals(CntbPktTypeTable.SET_THRESHOLD) && code == CntbPktTypeTable.SET_THRESHOLD_CODE) {
                result = towerService.cntbSetThreshold(request);
            }
        } catch (ParserConfigurationException | SAXException | IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 保存日志
     * @param msgId
     * @param sn
     * @param msgType
     * @param stateCode
     */
    void saveLog(String msgId, String sn, String msgType, int stateCode) {
        Log log = new Log(new Date(System.currentTimeMillis()),
                stateCode,
                sn, msgType, msgId, name, host);
        logDao.save(log);
    }
}
