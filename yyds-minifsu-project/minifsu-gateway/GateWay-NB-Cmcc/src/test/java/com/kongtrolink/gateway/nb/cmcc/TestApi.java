package com.kongtrolink.gateway.nb.cmcc;

import cmcc.iot.onenet.javasdk.api.device.AddDevicesApi;
import cmcc.iot.onenet.javasdk.model.Location;
import cmcc.iot.onenet.javasdk.response.BasicResponse;
import cmcc.iot.onenet.javasdk.response.device.NewDeviceResponse;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mystoxlol on 2019/6/25, 18:53.
 * company: kongtrolink
 * description:
 * update record:
 */
public class TestApi {
    @Test
    public void testAdddevices() {
        String key = "dXEYY0fKmauVJ3vStt2ZpXw59YY=";
        String title = "devices_test";
        String desc = "devices_test";
        String protocol = "HTTP";
        Location location =new Location(106,29,370);//设备位置{"纬度", "经度", "高度"}（可选）
        List<String> tags = new ArrayList<String>();
        tags.add("china");
        tags.add("mobile");
        String auth_info = "201503041a5829151";
        /****
         * 设备新增
         * 参数顺序与构造函数顺序一致
         * @param title： 设备名，String
         * @param protocol： 接入协议（可选，默认为HTTP）,String
         * @param desc： 设备描述（可选）,String
         * @param tags： 设备标签（可选，可为一个或多个）,List<String>
         * @param location： 设备位置{"纬度", "精度", "高度"}（可选）,Location类型
         * @param isPrivate： 设备私密性,Boolean类型（可选，默认为ture）
         * @param authInfo： 设备唯一编号 ,Object
         * @param other： 其他信息,Map<String, Object>（可选，可自定义）
         * @param interval： MODBUS设备 下发命令周期,Integer类型，秒（可选）
         * @param key： masterkey,String
         */
        AddDevicesApi api = new AddDevicesApi(title, protocol, desc, tags, location, null, auth_info, null, null, key);
        BasicResponse<NewDeviceResponse> response = api.executeApi();
        System.out.println("errno:"+response.errno+" error:"+response.error);
        System.out.println(response.getJson());

    }
}
