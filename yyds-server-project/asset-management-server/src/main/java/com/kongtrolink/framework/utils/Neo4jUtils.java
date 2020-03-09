package com.kongtrolink.framework.utils;

import java.util.ArrayList;
import java.util.List;

public class Neo4jUtils {

    /**
     * 通过企业编码和服务编码获取业务编码
     * @param enterpriseCode 企业编码
     * @param serverCode 服务编码
     * @return 业务编码
     */
    public static String getBusinessCode(String enterpriseCode, String serverCode) {
        return enterpriseCode + "-" + serverCode;
    }

    /**
     * 通过业务编码获取企业编码和服务编码
     * @param businessCode 业务编码
     * @return 第一个是企业编码，第二个是服务编码
     */
    public static List<String> getEnterpriseCodeAndServerCode(String businessCode) {
        ArrayList<String> result = new ArrayList<>();
        String[] codes = businessCode.split("-");
        result.add(codes[0]);
        result.add(codes[1]);
        return result;
    }

    /**
     * 获取CI类型组合编码
     * @param code1 第一级类型编码
     * @param code2 第二级类型编码
     * @param code3 第三级类型编码
     * @return 组合编码
     */
    public static String getCITypeCode(String code1, String code2, String code3) {
        return code1 + "-" + code2 + "-" + code3;
    }

    /**
     * 获取CI类型编码
     * @param id CI ID
     * @return CI类型编码
     */
    public static String getTypeCode(String id) {
        return id.split("-")[2];
    }

    /**
     * 获取业务编码
     * @param id CI ID
     * @return 业务编码
     */
    public static String getBusinessCode(String id) {
        String[] contents = id.split("-");
        return contents[3] + "-" + contents[4];
    }

    /**
     * 获取CI ID
     * @param ciTypeCode CI的类型编码，从1级到3级，通过‘-’分隔
     * @param sn ci的SN
     * @param enterpriseCode 企业编码
     * @param serverCode 服务编码
     * @return CI的Id
     */
    public static String getCIId(String ciTypeCode, String sn, String enterpriseCode, String serverCode) {
        return ciTypeCode + "-" + enterpriseCode + "-" + serverCode + "-" + sn;
    }
}
