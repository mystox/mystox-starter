package com.kongtrolink.framework.core.utils;

import com.kongtrolink.framework.core.entity.session.WebPageInfo;
import org.apache.commons.collections.CollectionUtils;

import java.util.List;

/**
 * \* @Author: mystox
 * \* Date: 2019/12/24 9:55
 * \* Description:
 * \
 */
public class ControllerCommonUtils {


    /**
     * @Date  2019/12/24
     * @Param No such property: code for class: Script1
     * @return void
     * @Author mystox
     * @Description //遍历报表查看的功能权限
     **/
    public static void getReportsFuncPriv(WebPageInfo currentMainMenu,List<WebPageInfo> reportsWebPageInfo) {
        List<WebPageInfo> nextMenuList = currentMainMenu.getNextMenuList();
        if (CollectionUtils.isNotEmpty(nextMenuList)) {
            nextMenuList.forEach(webPageInfo -> {
                String code = webPageInfo.getCode();
                String uri = webPageInfo.getUri();
                if ("/ZKReport/customReportShow".equals(uri)/* && code.endsWith("CUSTOM_REPORT_SHOW")*/) { //暂时uri为判断标准
                    reportsWebPageInfo.add(webPageInfo);
                }
                    getReportsFuncPriv(webPageInfo, reportsWebPageInfo);
            });
        }

    }

}