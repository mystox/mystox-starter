package com.kongtrolink.framework.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.kongtrolink.framework.core.config.rpc.RpcClient;
import com.kongtrolink.framework.core.entity.ModuleMsg;
import com.kongtrolink.framework.core.entity.PktType;
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
import com.kongtrolink.framework.execute.module.model.RedisFsuBind;
import com.kongtrolink.framework.execute.module.model.RedisOnlineInfo;
import com.kongtrolink.framework.jsonType.JsonDevice;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import javax.xml.bind.JAXBException;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author fengw
 * 铁塔平台注册服务
 * 新建文件 2019-4-17 17:10:10
 */
public class CntbLoginService extends RpcModuleBase implements Runnable {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    private String key;
    private String hostname;
    private int port;
    private RpcModule rpcModule;
    private RedisUtils redisUtils;
    private DeviceMatchService deviceMatchService;
    private DeviceDao deviceDao;
    private CarrierDao carrierDao;

    private RedisOnlineInfo redisOnlineInfo;

    /**
     * 构造函数
     * @param sn sn
     * @param hostname 铁塔网关服务地址
     * @param port 铁塔网关服务端口
     * @param rpcModule rpcModule
     * @param redisUtils redisUtils
     * @param rpcClient rpcClient
     * @param deviceMatchService 设备匹配帮助类
     * @param deviceDao 设备信息数据库操作
     * @param carrierDao 运营商信息数据库操作
     */
    public CntbLoginService(String sn, String hostname, int port,
                            RpcModule rpcModule, RedisUtils redisUtils, RpcClient rpcClient,
                            DeviceMatchService deviceMatchService, DeviceDao deviceDao, CarrierDao carrierDao) {
        super(rpcClient);
        this.key = RedisTable.getRegistryKey(sn);
        this.hostname = hostname;
        this.port = port;
        this.rpcModule = rpcModule;
        this.redisUtils = redisUtils;
        this.redisOnlineInfo = redisUtils.get(key, RedisOnlineInfo.class);
        this.deviceMatchService = deviceMatchService;
        this.deviceDao = deviceDao;
        this.carrierDao = carrierDao;
    }

    @Override
    public void run() {
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
                login(info);
            }
        } catch (Exception e) {
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
     * @return redis中在线信息
     */
    private RedisOnlineInfo getRedisOnlineInfo() {
        if (!redisUtils.hasKey(key)) {
            return null;
        }
        return redisUtils.get(key, RedisOnlineInfo.class);
    }

    /**
     * 更新设备信息
     * @param onlineInfo redis中在线信息
     * @return 更新结果
     */
    private boolean updateDevice(RedisOnlineInfo onlineInfo) {
        InetSocketAddress addr = new InetSocketAddress(redisOnlineInfo.getInnerIp(), redisOnlineInfo.getInnerPort());

        JSONObject jsonObject = new JSONObject();
        String request = createRequestMsg(PktType.GET_DEVICES, onlineInfo.getSn(), jsonObject);

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

        List<JsonDevice> cntbList = deviceDao.getListByFsuId(onlineInfo.getFsuId());
        for (JsonDevice jsonDevice : cntbList) {
            jsonDevice.setPort(null);
            jsonDevice.setResNo(0);
            jsonDevice.setType(0);
        }
        deviceMatchService.matchingDevice(cntbList, curList);
        deviceDao.deleteListByFsuId(onlineInfo.getFsuId());
        deviceDao.insertListByFsuId(cntbList);

        return true;
    }

    /**
     * 获取注册信息
     * @param onlineInfo redis中在线信息
     * @return 注册信息
     */
    private Login getLoginInfo(RedisOnlineInfo onlineInfo) {
        Login result = null;

        InetSocketAddress addr = new InetSocketAddress(redisOnlineInfo.getInnerIp(), redisOnlineInfo.getInnerPort());

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("sn", onlineInfo.getSn());
        String request = createRequestMsg(PktType.GET_FSU, onlineInfo.getSn(), jsonObject);

        JSONObject jsonResponse = JSONObject.parseObject(postMsg(request, addr));

        if (jsonResponse == null || (!jsonResponse.containsKey("list"))) {
            return result;
        }
        JSONArray array = jsonResponse.getJSONArray("list");
        if (array.size() != 1) {
            return result;
        }

        result = new Login();
        result.setFsuId(onlineInfo.getFsuId());
        result.setFsuCode(onlineInfo.getFsuId());
        result.setUserName("cntower");
        result.setPaSCWord("cntower");
        result.setIp(redisUtils.hget(RedisTable.VPN_HASH, onlineInfo.getLocalName()).toString());
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
        result.setFsuClass(onlineInfo.getFsuClass());
        result.setVersion(array.getJSONObject(0).getString("adapterVer"));
        result.setDictVersion(onlineInfo.getDictMode());

        RedisFsuBind redisFsuBind = redisUtils.get(RedisTable.getFsuBindKey(onlineInfo.getFsuId()), RedisFsuBind.class);
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
     * @param info 注册信息
     */
    private void login(Login info) {
        String reqXmlMsg = getXmlMsg(info);
        if (null == reqXmlMsg) {
            return;
        }

        InetSocketAddress addr = new InetSocketAddress(hostname, port);

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("ip", redisOnlineInfo.getLoginIp());
        jsonObject.put("port", redisOnlineInfo.getLoginPort());
        jsonObject.put("msg", reqXmlMsg);

        String request = createRequestMsg(CntbPktTypeTable.SERVICE_GW, "", jsonObject);

        JSONObject jsonResponse = JSONObject.parseObject(postMsg(request, addr));

        if (jsonResponse != null && jsonResponse.containsKey("result") && (jsonResponse.getInteger("result") == 1)) {
            String resXmlMsg = jsonResponse.getString("msg");
            LoginAck loginAck = analyzeMsg(resXmlMsg);

            RedisOnlineInfo onlineInfo = getRedisOnlineInfo();
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
            logger.error("AnalyzeXmlMsg: String转Document失败,xml:" + xml);
        } catch (JAXBException e) {
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
    private String createRequestMsg(String pktType, String sn, JSONObject payload) {
        ModuleMsg moduleMsg = new ModuleMsg(pktType, sn);
        moduleMsg.setPayload(payload);
        return JSON.toJSONString(moduleMsg);
    }

    /**
     * 发送请求
     * @param request 请求报文
     * @param addr 请求地址
     * @return 回复信息
     */
    private String postMsg(String request, InetSocketAddress addr) {
        String result = null;

        RpcNotifyProto.RpcMessage rpcMessage = null;
        try {
            rpcMessage = rpcModule.postMsg("", addr, request);
            result = rpcMessage.getPayload();
        } catch (IOException e) {
            logger.error("sendRpcMsg: 发送Rpc请求失败,ip:" + addr.getHostName() + ",port:" + addr.getPort() + ",msg:" + request);
        }

        return result;
    }
}
