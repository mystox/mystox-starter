package com.kongtrolink.framework.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.kongtrolink.framework.core.entity.ModuleMsg;
import com.kongtrolink.framework.core.entity.PktType;
import com.kongtrolink.framework.core.entity.StateCode;
import com.kongtrolink.framework.core.protobuf.RpcNotifyProto;
import com.kongtrolink.framework.core.utils.RedisUtils;
import com.kongtrolink.framework.entity.CntbPktTypeTable;
import com.kongtrolink.framework.entity.RedisTable;
import com.kongtrolink.framework.entity.xml.base.Message;
import com.kongtrolink.framework.entity.xml.msg.LoginAck;
import com.kongtrolink.framework.entity.xml.msg.Login;
import com.kongtrolink.framework.entity.xml.msg.Device;
import com.kongtrolink.framework.entity.xml.msg.XmlList;
import com.kongtrolink.framework.entity.xml.util.MessageUtil;
import com.kongtrolink.framework.execute.module.dao.CarrierDao;
import com.kongtrolink.framework.execute.module.dao.DeviceDao;
import com.kongtrolink.framework.execute.module.model.RedisFsuBind;
import com.kongtrolink.framework.execute.module.model.RedisOnlineInfo;
import com.kongtrolink.framework.jsonType.JsonDevice;
import com.kongtrolink.framework.utils.CommonUtils;
import com.kongtrolink.framework.utils.LogUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import javax.xml.bind.JAXBException;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.*;

/**
 * @author fengw
 * 铁塔平台注册服务
 * 新建文件 2019-4-17 17:10:10
 */
@Service
public class CntbLoginService {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private RpcService rpcService;
    @Autowired
    private CommonUtils commonUtils;
    @Autowired
    private DeviceMatchService deviceMatchService;
    @Autowired
    private DeviceDao deviceDao;
    @Autowired
    LogUtils logUtils;


    /**
     * 开始注册
     * @param sn sn
     */
    public void startLogin(String sn) {

        RedisOnlineInfo redisOnlineInfo = commonUtils.getRedisOnlineInfo(sn);
        try {
            if (!(redisOnlineInfo != null && !redisOnlineInfo.isOnline() &&
                    (commonUtils.getVpnIp(redisOnlineInfo.getLocalName()) != null))) {
                return;
            }

            if (!updateDevice(redisOnlineInfo)) {
                return;
            }

            Login info = getLoginInfo(redisOnlineInfo);

            if (info != null) {
                login(redisOnlineInfo, info);
            }
        } catch (Exception e) {
            logUtils.saveLog("", sn, CntbPktTypeTable.LOGIN, StateCode.FAILED);
            logger.error("注册过程中出现异常:" + JSONObject.toJSONString(e));
        } finally {
            redisOnlineInfo = commonUtils.getRedisOnlineInfo(sn);
            if (redisOnlineInfo != null) {
                redisOnlineInfo.setLogining(false);
                commonUtils.setRedisOnlineInfo(redisOnlineInfo);
            }
            logger.debug("-----------------------login finally-----------------------" + JSONObject.toJSONString(redisOnlineInfo));
        }
    }

    /**
     * 更新设备信息
     * @param redisOnlineInfo redis中在线信息
     * @return 更新结果
     */
    private boolean updateDevice(RedisOnlineInfo redisOnlineInfo) {

        JSONArray jsonArray = rpcService.getDevices(redisOnlineInfo.getSn(),
                redisOnlineInfo.getInnerIp(), redisOnlineInfo.getInnerPort());

        if (jsonArray == null) {
            logger.error("注册时获取设备列表失败");
            return false;
        }
        List<JsonDevice> curList = new ArrayList<>();
        for (int i = 0; i < jsonArray.size(); ++i) {
            JsonDevice jsonDevice = new JsonDevice();
            jsonDevice.setType(jsonArray.getJSONObject(i).getInteger("type"));
            jsonDevice.setResNo(jsonArray.getJSONObject(i).getInteger("resNo"));
            jsonDevice.setPort(jsonArray.getJSONObject(i).getString("port"));
            curList.add(jsonDevice);
        }

        List<JsonDevice> cntbList = deviceDao.getListByFsuId(redisOnlineInfo.getFsuId());
        for (JsonDevice jsonDevice : cntbList) {
            jsonDevice.setPort(null);
            jsonDevice.setResNo(0);
            jsonDevice.setType(0);
        }
        deviceMatchService.matchingDevice(cntbList, curList);
        deviceDao.deleteListByFsuId(redisOnlineInfo.getFsuId());
        deviceDao.insertListByFsuId(cntbList);

        return true;
    }

    /**
     * 获取注册信息
     * @param redisOnlineInfo redis中在线信息
     * @return 注册信息
     */
    private Login getLoginInfo(RedisOnlineInfo redisOnlineInfo) {
        Login result = null;

        JSONObject jsonResponse = rpcService.getFsu(redisOnlineInfo.getSn(),
                redisOnlineInfo.getInnerIp(), redisOnlineInfo.getInnerPort());

        if (jsonResponse == null || (!jsonResponse.containsKey("list"))) {
            return result;
        }
        JSONArray array = jsonResponse.getJSONArray("list");
        if (array.size() != 1) {
            return result;
        }

        result = new Login();
        result.setFsuId(redisOnlineInfo.getFsuId());
        result.setFsuCode(redisOnlineInfo.getFsuId());
        result.setUserName("cntower");
        result.setPaSCWord("cntower");
        result.setIp(commonUtils.getVpnIp(redisOnlineInfo.getLocalName()));
        result.setMacId(array.getJSONObject(0).getString("imei"));
        result.setImsiId(array.getJSONObject(0).getString("imsi"));
        result.setNetworkType(array.getJSONObject(0).getString("nwType"));
        result.setLockedNetworkType("AUTO");
        result.setCarrier(commonUtils.getCarrier(array.getJSONObject(0).getString("carrier")));
        result.setNmVendor(array.getJSONObject(0).getString("wmVendor"));
        result.setNmType(array.getJSONObject(0).getString("wmType"));
        result.setRegMode(2);
        result.setFsuVendor("TDYY");
        result.setFsuType(array.getJSONObject(0).getString("model"));
        result.setFsuClass(redisOnlineInfo.getFsuClass());
        result.setVersion(array.getJSONObject(0).getString("adapterVer"));
        result.setDictVersion(redisOnlineInfo.getDictMode());

        RedisFsuBind redisFsuBind = commonUtils.getRedisFsuBind(redisOnlineInfo.getFsuId());
        XmlList list = new XmlList();
        for (int i = 0; i < redisFsuBind.getDeviceIdList().size(); ++i) {
            Device device = new Device();
            device.setId(redisFsuBind.getDeviceIdList().get(i));
            device.setCode(redisFsuBind.getDeviceIdList().get(i));
            list.getDeviceList().add(device);
        }
        result.setDeviceList(list);

        return result;
    }



    /**
     * 根据注册信息，上报注册报文，并解析注册结果
     * @param redisOnlineInfo redis中在线信息
     * @param info 注册信息
     */
    private void login(RedisOnlineInfo redisOnlineInfo, Login info) {
        String reqXmlMsg = getXmlMsg(info);
        if (null == reqXmlMsg) {
            return;
        }

        JSONObject jsonResponse = rpcService.requestCntb(reqXmlMsg, redisOnlineInfo.getLoginIp(), redisOnlineInfo.getLoginPort());

        if (jsonResponse != null && jsonResponse.containsKey("result") && (jsonResponse.getInteger("result") == 1)) {
            String resXmlMsg = jsonResponse.getString("msg");
            LoginAck loginAck = analyzeMsg(resXmlMsg);

            RedisOnlineInfo onlineInfo = commonUtils.getRedisOnlineInfo(redisOnlineInfo.getSn());
            if (onlineInfo != null) {
                if (loginAck != null && Integer.parseInt(loginAck.getRightLevel()) == 2) {
                    onlineInfo.setOnline(true);
                    onlineInfo.setAlarmIp(loginAck.getScIp());
                    logger.debug("-----------------------login success-----------------------" + JSONObject.toJSONString(redisOnlineInfo));
                }
                onlineInfo.setLastTimeRecvTowerMsg(System.currentTimeMillis()/1000);
                onlineInfo.setLastTimeLogin(System.currentTimeMillis()/1000);
                commonUtils.setRedisOnlineInfo(onlineInfo);
                logger.debug("-----------------------login end-----------------------" + JSONObject.toJSONString(redisOnlineInfo));
            }
        }
    }

    /**
     * 获取Xml报文
     * @param login 注册信息
     * @return Xml报文字符串
     */
    private String getXmlMsg(Login login) {
        String result = "";

        try {
            Message message = new Message(login);
            result = MessageUtil.messageToString(message);
        } catch (Exception ex) {
            logUtils.saveLog("", "", CntbPktTypeTable.LOGIN, StateCode.XML_ILLEGAL);
            logger.error("GetXmlMsg: 实体类转xml失败,msg:" + JSONObject.toJSONString(login));
        }

        return result;
    }

    /**
     * 解析Xml报文字符串
     * @param xml Xml报文字符串
     * @return 回复信息
     */
    private LoginAck analyzeMsg(String xml) {
        LoginAck result = null;

        try {
            Document doc = MessageUtil.stringToDocument(xml);
            String pkType = doc.getElementsByTagName("Name").item(0).getTextContent();
            int code = Integer.parseInt(doc.getElementsByTagName("Code").item(0).getTextContent());

            if ((pkType.equals(CntbPktTypeTable.LOGIN_ACK)) && (code == CntbPktTypeTable.LOGIN_ACK_CODE)) {
                Node reqInfoNode = doc.getElementsByTagName("Info").item(0);
                String reqInfoStr = MessageUtil.infoNodeToString(reqInfoNode);
                result = (LoginAck)MessageUtil.stringToMessage(reqInfoStr, LoginAck.class);
            }
        } catch (ParserConfigurationException | SAXException | IOException e) {
            logUtils.saveLog("", "", CntbPktTypeTable.LOGIN_ACK, StateCode.XML_ILLEGAL);
            logger.error("AnalyzeXmlMsg: String转Document失败,xml:" + xml);
        } catch (JAXBException e) {
            logUtils.saveLog("", "", CntbPktTypeTable.LOGIN_ACK, StateCode.XML_ILLEGAL);
            logger.error("GetXmlMsg: String转实体类失败,xml" + xml);
        }

        return result;
    }
}
