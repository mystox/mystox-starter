package com.kongtrolink.framework.mqtt;

import java.util.List;

/**
 * @Auther: liudd
 * @Date: 2019/10/25 11:11
 * @Description:从云管获取设备地区信息接口
 */
public class AddressInfoRequest {

    private List<String> addressList;

    public List<String> getAddressList() {
        return addressList;
    }

    public void setAddressList(List<String> addressList) {
        this.addressList = addressList;
    }
}
