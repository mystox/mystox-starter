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
import com.kongtrolink.framework.execute.module.model.RedisFsuBind;
import com.kongtrolink.framework.execute.module.model.RedisOnlineInfo;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import javax.xml.bind.JAXBException;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;

/**
 * @author fengw
 * 铁塔平台注册服务
 * 新建文件 2019-4-17 17:10:10
 */
public class CntbLoginService extends RpcModuleBase implements Runnable {

    private String key;
    private String innerIp;
    private int innerPort;
    private String hostname;
    private int port;
    private RpcModule rpcModule;
    private RedisUtils redisUtils;
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
     * @param carrierDao 运营商信息数据库操作
     */
    public CntbLoginService(String sn, String hostname, int port,
                            RpcModule rpcModule, RedisUtils redisUtils, RpcClient rpcClient,
                            CarrierDao carrierDao) {
        super(rpcClient);
        this.key = RedisTable.getRegistryKey(sn);
        this.hostname = hostname;
        this.port = port;
        this.rpcModule = rpcModule;
        this.redisUtils = redisUtils;
        this.redisOnlineInfo = redisUtils.get(key, RedisOnlineInfo.class);
        this.carrierDao = carrierDao;
    }

    @Override
    public void run() {
        if (!(redisOnlineInfo != null && !redisOnlineInfo.isOnline() &&
                redisUtils.hasKey(RedisTable.VPN_HASH) &&
                redisUtils.hHasKey(RedisTable.VPN_HASH, redisOnlineInfo.getLocalName()))) {
            return;
        }

        Login info = getLoginInfo(redisOnlineInfo);

        if (info != null) {
            login(info);
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
     * 获取注册信息
     * @param onlineInfo redis中在线信息
     * @return 注册信息
     */
    private Login getLoginInfo(RedisOnlineInfo onlineInfo) {
        Login result = null;

        InetSocketAddress addr = new InetSocketAddress(redisOnlineInfo.getInnerIp(), redisOnlineInfo.getInnerPort());

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("sn", onlineInfo.getSn());
        String request = createRequestMsg(PktType.GET_FSU, jsonObject);

        //todo 没有和内部服务进行通信
        JSONObject jsonResponse = postMsg(request, addr);

//        JSONObject jsonResponse = new JSONObject();
//        jsonResponse.put("success", true);
//        JSONArray tmp = new JSONArray();
//        JSONObject info = new JSONObject();
//        info.put("imei", "imei");
//        info.put("imsi", "imsi");
//        info.put("nwType", "nwType");
//        info.put("carrier", "CU");
//        info.put("wmVendor", "wmVendor");
//        info.put("wmType", "wmType");
//        info.put("model", "model");
//        info.put("adapterVer", "adapterVer");
//        tmp.add(info);
//        jsonResponse.put("data", tmp);

        if (!jsonResponse.containsKey("list")) {
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

        String request = createRequestMsg(CntbPktTypeTable.SERVICE_GW, jsonObject);

        JSONObject jsonResponse = postMsg(request, addr);

//        JSONObject jsonResponse = new JSONObject();
//        jsonResponse.put("result", true);
//        jsonResponse.put("msg", "<?xml version=\"1.0\" encoding=\"UTF-8\"?><Response><PK_Type><Name>LOGIN_ACK</Name><Code>102</Code></PK_Type><Info><SCIP>172.16.6.66</SCIP><RightLevel>2</RightLevel></Info></Response>");

        if (jsonResponse.containsKey("result") && (jsonResponse.getInteger("result") == 1)) {
            String resXmlMsg = jsonResponse.getString("msg");
            LoginAck loginAck = analyzeMsg(resXmlMsg);

            RedisOnlineInfo onlineInfo = getRedisOnlineInfo();
            if (onlineInfo != null) {
                if (loginAck != null && Integer.parseInt(loginAck.getRightLevel()) == 2) {
                    onlineInfo.setOnline(true);
                    onlineInfo.setAlarmIp(loginAck.getScIp());
                }
                onlineInfo.setLastTimeRecvTowerMsg(System.currentTimeMillis()/1000);
                onlineInfo.setLastTimeLogin(System.currentTimeMillis()/1000);
                long time = redisUtils.getExpire(key);
                redisUtils.set(key, onlineInfo, time);
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
            //todo 实体类转xml失败
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
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JAXBException e) {
            e.printStackTrace();
        }

        return result;
    }

    /**
     * 创建请求报文
     * @param pktType 报文头类型
     * @param payload 发送信息
     * @return 字符串报文
     */
    private String createRequestMsg(String pktType, JSONObject payload){
        ModuleMsg moduleMsg = new ModuleMsg(pktType);
        moduleMsg.setPayload(payload);
        return JSON.toJSONString(moduleMsg);
    }

    /**
     * 发送请求
     * @param request 请求报文
     * @param addr 请求地址
     * @return 回复信息
     */
    private JSONObject postMsg(String request, InetSocketAddress addr) {
        JSONObject result = null;

        RpcNotifyProto.RpcMessage rpcMessage = null;
        try {
            rpcMessage = rpcModule.postMsg("", addr, request);
            String response = rpcMessage.getPayload();
            result = JSONObject.parseObject(response);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return result;
    }
}
