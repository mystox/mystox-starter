package com.kongtrolink.framework.gateway.tower.core.util;

/**
 * 铁塔B接口 强协议关联工具类
 */
public class GatewayTowerUtil {
    /**
     * 推送资管的设备信息报文中 设备类型字段取值 从设备ID中截取：
     *    设备编码：由XX省（2位数字）+XX区县（4位数字）+局站类型与设备类型组合（3位数字）+XX设备（5位数字)
     *    针对 设备类型18类型开头的设备类型 延后多截取1位  总共3位。
     * 举例： 设备ID：33060141500001  设备类型是：15
     *       设备ID：33060141830001  设备类型是：183
     * @param deviceId  设备ID
     * @return 类型
     */
    public static String getDeviceTypeFromId(String deviceId){
        if(deviceId==null){
            return null;
        }
        String type = deviceId.substring(7,9);
        if("18".equals(type)){
            return deviceId.substring(7,10);
        }
        return type;
    }

    public static void main(String[] args) {
        System.out.println(getDeviceTypeFromId("33060141500001"));
    }
}
