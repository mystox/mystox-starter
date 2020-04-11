package com.kongtrolink.framework.scloud;

import com.kongtrolink.framework.scloud.entity.model.DeviceModel;
import com.kongtrolink.framework.scloud.query.DeviceQuery;
import com.kongtrolink.framework.scloud.service.DeviceService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ImportResource;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;

/**
 * @Auther: liudd
 * @Date: 2020/4/11 10:24
 * @Description:
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = DeviceTest.class)
@ImportResource("application-dev.yml")
public class DeviceTest {

    @Autowired
    DeviceService deviceService;
    private String uniqueCode = "YYDS";

    @Test
    public void getAllTest(){
        DeviceQuery deviceQuery = new DeviceQuery();
        deviceQuery.setServerCode(uniqueCode);
        deviceQuery.setPageSize(Integer.MAX_VALUE);
        List<String> deviceCodeList = new ArrayList<>();
        deviceCodeList.add("11010113800005");
        deviceCodeList.add("11010111840001");
        deviceCodeList.add("11010111820001");
        deviceCodeList.add("11010111810001");
        deviceCodeList.add("11010111830001");

        deviceCodeList.add("11010110700001");
        deviceCodeList.add("11010121840001");
        deviceCodeList.add("11010111820002");
        deviceCodeList.add("11010111810002");
        deviceCodeList.add("11010111830002");
        deviceQuery.setDeviceCodes(deviceCodeList);
        try{
            List<DeviceModel> deviceModelList = deviceService.listDeviceList(uniqueCode, deviceQuery);
            for(DeviceModel deviceModel : deviceModelList){
                System.out.println("code" + deviceModel.getCode() + "; name:" + deviceModel.getName()
                        + "; siteCode:" + deviceModel.getSiteCode()  + ";sitename;" + deviceModel.getSiteName() + "; siteAddress:" + deviceModel.getSiteAddress());
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
