package com.kongtrolink.gateway.nb.ctcc.iot.service;

import com.iotplatform.client.NorthApiException;
import com.iotplatform.client.dto.*;
import com.iotplatform.client.invokeapi.DataCollection;
import com.iotplatform.client.invokeapi.SignalDelivery;
import com.kongtrolink.gateway.nb.ctcc.iot.NbIotServer;
import org.apache.commons.collections.map.HashedMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Map;

/**
 * NB命令的各种接口
 * by Mag on 2018/11/23.
 */
@Service
public class NbIotService {
    @Resource
    NbIotServer nbIotServer;

    private static final Logger LOGGER = LoggerFactory.getLogger(NbIotService.class);

    /**
     * 下发NB命令 post an NB-IoT device command
     */
    public void postNbCommand(String deviceId,String serverId,String method,String data){
        SignalDelivery signalDelivery = new SignalDelivery(nbIotServer.getNorthApiClient());
        if(deviceId==null || "".equals(deviceId)){
            LOGGER.error("下发NB命令失败 deviceId 为必填 项 缺失 !!!! ");
            return ;
        }
        LOGGER.info("====== 下发NB命令 post an NB-IoT device command  ======");
        PostDeviceCommandOutDTO2 pdcOutDTO = postCommand(signalDelivery, deviceId,serverId,method,data, null);
        if(pdcOutDTO==null){
            LOGGER.info("返回 失败了 null !!! ");
        }else{
            LOGGER.info("return :"+pdcOutDTO.toString());
        }
    }

    /**
     * 注册
     */
    public boolean register(String nodeId){
        return nbIotServer.register(nodeId);
    }
    /**
     * 查询单个 设备信息
     */
    public void queryDevice(String deviceId) throws Exception{
        /**---------------------query device info------------------------*/
        //this is a test device
        DataCollection dataCollection = new DataCollection(nbIotServer.getNorthApiClient());
        System.out.println("====== query device info ======");
        QuerySingleDeviceInfoOutDTO qsdiOutDTO = dataCollection.querySingleDeviceInfo(deviceId, null, null,null);
        if (qsdiOutDTO != null) {
            System.out.println(qsdiOutDTO.toString());
        }
    }

    /**
     * 批量查询 设备信息
     * @throws Exception
     */
    public void queryBatchDevice() throws Exception{
        /**---------------------query batch device info------------------------*/
        DataCollection dataCollection = new DataCollection(nbIotServer.getNorthApiClient());
        System.out.println("\n======query batch device info======");
        QueryBatchDevicesInfoInDTO qbdiInDTO = new QueryBatchDevicesInfoInDTO();
        qbdiInDTO.setPageNo(0);
        qbdiInDTO.setPageSize(10);
        QueryBatchDevicesInfoOutDTO qbdiOutDTO = dataCollection.queryBatchDevicesInfo(qbdiInDTO,null);
        if (qbdiOutDTO != null) {
            System.out.println(qbdiOutDTO.toString());
        }
    }


    private PostDeviceCommandOutDTO2 postCommand(SignalDelivery signalDelivery,String deviceId,String serverId,String method,String data, String accessToken) {
        PostDeviceCommandInDTO2 pdcInDTO = new PostDeviceCommandInDTO2();
        pdcInDTO.setDeviceId(deviceId);
        CommandDTOV4 cmd = new CommandDTOV4();
        cmd.setServiceId(serverId);
        cmd.setMethod(method); //"PUT" is the command name defined in the profile
        Map<String, Object> cmdParam = new HashedMap();
//        data = data+"#";
        cmdParam.put("value",data);
        cmd.setParas(cmdParam);
        pdcInDTO.setExpireTime(0);//下发命令的有效时间，单位为秒
        pdcInDTO.setCommand(cmd);
        try {
            return signalDelivery.postDeviceCommand(pdcInDTO, null, accessToken);
        } catch (NorthApiException e) {
            System.out.println(e.toString());
        }
        return null;
    }

}
