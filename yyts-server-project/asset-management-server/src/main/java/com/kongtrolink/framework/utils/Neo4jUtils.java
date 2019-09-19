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
    public static String getBusinessCode(int enterpriseCode, int serverCode) {
        return enterpriseCode + "-" + serverCode;
    }

    /**
     * 通过业务编码获取企业编码和服务编码
     * @param businessCode 业务编码
     * @return 第一个是企业编码，第二个是服务编码
     */
    public static List<Integer> getEnterpriseCodeAndServerCode(String businessCode) {
        ArrayList result = new ArrayList();
        String[] codes = businessCode.split("-");
        result.add(Integer.valueOf(codes[0]));
        result.add(Integer.valueOf(codes[1]));
        return result;
    }
}
