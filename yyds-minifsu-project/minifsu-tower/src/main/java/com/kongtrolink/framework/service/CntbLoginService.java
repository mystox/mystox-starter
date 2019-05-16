package com.kongtrolink.framework.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.kongtrolink.framework.core.config.rpc.RpcClient;
import com.kongtrolink.framework.core.entity.Log;
import com.kongtrolink.framework.core.entity.ModuleMsg;
import com.kongtrolink.framework.core.entity.PktType;
import com.kongtrolink.framework.core.entity.StateCode;
import com.kongtrolink.framework.core.protobuf.RpcNotifyProto;
import com.kongtrolink.framework.core.rpc.RpcModuleBase;
import com.kongtrolink.framework.core.utils.RedisUtils;
import com.kongtrolink.framework.entity.CntbPktTypeTable;
import com.kongtrolink.framework.entity.RedisTable;
import com.kongtrolink.framework.entity.xml.base.Message;
import com.kongtrolink.framework.entity.xml.msg.LoginAck;
import com.kongtrolink.framework.entity.xml.msg.Login;
import com.kongtrolink.framework.entity.xml.msg.Device;
import com.kongtrolink.framework.entity.xml.msg.XmlList;
import com.kongtrolink.framework.entity.xml.util.MessageUtil;
import com.kongtrolink.framework.execute.module.RpcModule;
import com.kongtrolink.framework.execute.module.dao.CarrierDao;
import com.kongtrolink.framework.execute.module.dao.DeviceDao;
import com.kongtrolink.framework.execute.module.dao.LogDao;
import com.kongtrolink.framework.execute.module.model.RedisFsuBind;
import com.kongtrolink.framework.execute.module.model.RedisOnlineInfo;
import com.kongtrolink.framework.jsonType.JsonDevice;
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

    @Value("${server.bindIp}")
    private String host;
    @Value("${server.name}")
    private String name;

    @Autowired
    private RpcModule rpcModule;
    @Autowired
    private RedisUtils redisUtils;
    @Autowired
    private DeviceMatchService deviceMatchService;
    @Autowired
    private DeviceDao deviceDao;
    @Autowired
    private CarrierDao carrierDao;
    @Autowired
    LogDao logDao;
    @Value("${tower.gateway.hostname}")
    private String towerGatewayHostname;
    @Value("${tower.gateway.port}")
    private int towerGatewayPort;

    /**
     * 开始注册
     * @param sn sn
     */
    public void startLogin(String sn) {
        String key = RedisTable.getRegistryKey(sn);
        RedisOnlineInfo redisOnlineInfo = getRedisOnlineInfo(key);
        try {
            if (!(redisOnlineInfo != null && !redisOnlineInfo.isOnline() &&
                redisUtils.hasKey(RedisTable.VPN_HASH) &&
                redisUtils.hHasKey(RedisTable.VPN_HASH, redisOnlineInfo.getLocalName()))) {
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
            saveLog("", sn, CntbPktTypeTable.LOGIN, StateCode.FAILED);
            logger.error("注册过程中出现异常:" + JSONObject.toJSONString(e));
        } finally {
            redisOnlineInfo = redisUtils.get(key, RedisOnlineInfo.class);
            if (redisOnlineInfo != null) {
                redisOnlineInfo.setLogining(false);
                long time = redisUtils.getExpire(key);
                redisUtils.set(key, redisOnlineInfo, time);
            }
            logger.debug("-----------------------login finally-----------------------" + JSONObject.toJSONString(redisOnlineInfo));
        }
    }

    /**
     * 获取redis中在线信息
     * @param key redis的key
     * @return redis中在线信息
     */
    private RedisOnlineInfo getRedisOnlineInfo(String key) {
        if (!redisUtils.hasKey(key)) {
            return null;
        }
        return redisUtils.get(key, RedisOnlineInfo.class);
    }

    /**
     * 更新设备信息
     * @param redisOnlineInfo redis中在线信息
     * @return 更新结果
     */
    private boolean updateDevice(RedisOnlineInfo redisOnlineInfo) {
        InetSocketAddress addr = new InetSocketAddress(redisOnlineInfo.getInnerIp(), redisOnlineInfo.getInnerPort());

        JSONObject jsonObject = new JSONObject();
        ModuleMsg request = createRequestMsg(PktType.GET_DEVICES, redisOnlineInfo.getSn(), jsonObject);

        String response = postMsg(request, addr);
        JSONArray jsonArray = JSONArray.parseArray(response);

        if (jsonArray == null) {
            logger.error("注册时获取设备列表失败:" + response);
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

        InetSocketAddress addr = new InetSocketAddress(redisOnlineInfo.getInnerIp(), redisOnlineInfo.getInnerPort());

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("sn", redisOnlineInfo.getSn());
        ModuleMsg request = createRequestMsg(PktType.GET_FSU, redisOnlineInfo.getSn(), jsonObject);

        JSONObject jsonResponse = JSONObject.parseObject(postMsg(request, addr));

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
        result.setIp(redisUtils.hget(RedisTable.VPN_HASH, redisOnlineInfo.getLocalName()).toString());
        result.setMacId(array.getJSONObject(0).getString("imei"));
        result.setImsiId(array.getJSONObject(0).getString("imsi"));
        result.setNetworkType(array.getJSONObject(0).getString("nwType"));
        result.setLockedNetworkType("AUTO");
        result.setCarrier(getCarrier(array.getJSONObject(0).getString("carrier")));
        result.setNmVendor(array.getJSONObject(0).getString("wmVendor"));
        result.setNmType(array.getJSONObject(0).getString("wmType"));
        result.setRegMode(2);
        result.setFsuVendor("TDYY");
        result.setFsuType(array.getJSONObject(0).getString("model"));
        result.setFsuClass(redisOnlineInfo.getFsuClass());
        result.setVersion(array.getJSONObject(0).getString("adapterVer"));
        result.setDictVersion(redisOnlineInfo.getDictMode());

        RedisFsuBind redisFsuBind = redisUtils.get(RedisTable.getFsuBindKey(redisOnlineInfo.getFsuId()), RedisFsuBind.class);
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
     * 获取上报铁塔运营商类型
     * @param type 内部服务上报类型
     * @return 上报铁塔类型
     */
    private String getCarrier(String type) {
        if (type == null) {
            return "";
        }
        if (!redisUtils.hasKey(RedisTable.CARRIER_HASH) ||
                !redisUtils.hHasKey(RedisTable.CARRIER_HASH, type)) {
            Map<String, String> map = carrierDao.getAll();
            if (map.containsKey(type)) {
                Map<String, Object> tmp = new HashMap<>();
                //遍历map中的键
                for (String key : map.keySet()) {
                    tmp.put(key, map.get(key));
                }
                redisUtils.hmset(RedisTable.CARRIER_HASH, tmp, 0);
                return map.get(type);
            } else {
                return "";
            }
        }
        return redisUtils.hget(RedisTable.CARRIER_HASH, type).toString();
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

        InetSocketAddress addr = new InetSocketAddress(towerGatewayHostname, towerGatewayPort);

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("ip", redisOnlineInfo.getLoginIp());
        jsonObject.put("port", redisOnlineInfo.getLoginPort());
        jsonObject.put("msg", reqXmlMsg);

        ModuleMsg request = createRequestMsg(CntbPktTypeTable.SERVICE_GW, "", jsonObject);

        JSONObject jsonResponse = JSONObject.parseObject(postMsg(request, addr));

        if (jsonResponse != null && jsonResponse.containsKey("result") && (jsonResponse.getInteger("result") == 1)) {
            String resXmlMsg = jsonResponse.getString("msg");
            LoginAck loginAck = analyzeMsg(resXmlMsg);

            String key = RedisTable.getRegistryKey(redisOnlineInfo.getSn());
            RedisOnlineInfo onlineInfo = getRedisOnlineInfo(key);
            if (onlineInfo != null) {
                if (loginAck != null && Integer.parseInt(loginAck.getRightLevel()) == 2) {
                    onlineInfo.setOnline(true);
                    onlineInfo.setAlarmIp(loginAck.getScIp());
                    logger.debug("-----------------------login success-----------------------" + JSONObject.toJSONString(redisOnlineInfo));
                }
                onlineInfo.setLastTimeRecvTowerMsg(System.currentTimeMillis()/1000);
                onlineInfo.setLastTimeLogin(System.currentTimeMillis()/1000);
                long time = redisUtils.getExpire(key);
                redisUtils.set(key, onlineInfo, time);
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
            saveLog("", "", CntbPktTypeTable.LOGIN, StateCode.XML_ILLEGAL);
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
            saveLog("", "", CntbPktTypeTable.LOGIN_ACK, StateCode.XML_ILLEGAL);
            logger.error("AnalyzeXmlMsg: String转Document失败,xml:" + xml);
        } catch (JAXBException e) {
            saveLog("", "", CntbPktTypeTable.LOGIN_ACK, StateCode.XML_ILLEGAL);
            logger.error("GetXmlMsg: String转实体类失败,xml" + xml);
        }

        return result;
    }

    /**
     * 创建请求报文
     * @param pktType 报文头类型
     * @param sn sn
     * @param payload 发送信息
     * @return 字符串报文
     */
    private ModuleMsg createRequestMsg(String pktType, String sn, JSONObject payload) {
        ModuleMsg moduleMsg = new ModuleMsg(pktType, sn);
        moduleMsg.setPayload(payload);
        return moduleMsg;
    }

    /**
     * 发送请求
     * @param request 请求报文
     * @param addr 请求地址
     * @return 回复信息
     */
    private String postMsg(ModuleMsg request, InetSocketAddress addr) {
        String result = null;

        RpcNotifyProto.RpcMessage rpcMessage = null;
        try {
            rpcMessage = rpcModule.postMsg("", addr, JSONObject.toJSONString(request));
            result = rpcMessage.getPayload();
        } catch (IOException e) {
            saveLog(request.getMsgId(), request.getSN(), request.getPktType(), StateCode.CONNECT_ERROR);
            logger.error("sendRpcMsg: 发送Rpc请求失败,ip:" + addr.getHostName() + ",port:" + addr.getPort() + ",msg:" + request);
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
