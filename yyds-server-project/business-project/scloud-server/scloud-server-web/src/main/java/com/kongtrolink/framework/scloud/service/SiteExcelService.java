package com.kongtrolink.framework.scloud.service;

import com.kongtrolink.framework.scloud.constant.Regex;
import org.springframework.stereotype.Service;

/**
 * 站点导入Excel 接口类
 * Created by Eric on 2020/4/16.
 */
@Service
public class SiteExcelService {

    private boolean[] nullable = {false, false, false, false, false, true, true};
    private String[] regex = {Regex.TIER_CODE, Regex.RESOURCE_NAME, Regex.ADDRESS, Regex.COORDINATE, Regex.SITE_TYPE, Regex.PERSON_NAME, Regex.CELLPHONE};


}
