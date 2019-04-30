package cmcciot.nbapi.samples;

import cmcciot.nbapi.entity.*;
import cmcciot.nbapi.online.*;
import com.alibaba.fastjson.JSON;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by zhuocongbin
 * date 2018/3/15
 */
public class ApiSample {
    private static final Logger LOGGER = LoggerFactory.getLogger(ApiSample.class);
    public static void main(String[] args) throws Exception {
        String apiKey = "***********************";
        String imei = "*******";
        Integer objId = 3200;
        Integer objInstId = 0;
        Integer readResId = 5500;
        Integer executeResId = 5501;
        Integer writeResId = 5750;
        Integer writeMode = 2;
        // Create device
        CreateDeviceOpe deviceOpe = new CreateDeviceOpe(apiKey);
        Device device = new Device("samples", "imeitest", "320023320");
        LOGGER.info(deviceOpe.operation(device,JSON.toJSONString(device)).toString());
        //Read
        ReadOpe readOperation = new ReadOpe(apiKey);
        Read read = new Read(imei, objId);
        read.setObjInstId(objInstId);
        read.setResId(readResId);
        LOGGER.info(readOperation.operation(read, "").toString());
        // Write
//        WriteOpe writeOpe = new WriteOpe(apiKey);
//        Write write = new Write(imei, objId, objInstId, writeMode);
//        JSONArray jsonArray = new JSONArray();
//        JSONObject jsonObject = new JSONObject();
//        jsonObject.put("res_id", writeResId);
//        jsonObject.put("val", "data1");
//        jsonArray.put(jsonObject);
//        JSONObject data = new JSONObject();
//        data.put("data", jsonArray);
//        LOGGER.info(writeOpe.operation(write, data).toString());
        // Execute
        ExecuteOpe executeOpe = new ExecuteOpe(apiKey);
        Execute execute = new Execute(imei, objId, objInstId, executeResId);
        //下发命令内容，JSON格式
        Map<String,Object> map = new HashMap<>();
        map.put("args", "ping");
        LOGGER.info(executeOpe.operation(execute, JSON.toJSONString(map)).toString());
        // Resource
        ResourcesOpe resourcesOpe = new ResourcesOpe(apiKey);
        Resources resources = new Resources(imei);
        LOGGER.info(resourcesOpe.operation(resources, "").toString());
        // Observe
        ObserveOpe observeOpe = new ObserveOpe(apiKey);
        Observe observe = new Observe(imei, objId, false);
        LOGGER.info(observeOpe.operation(observe, "").toString());

    }
}
