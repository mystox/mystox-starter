package com.kongtrolink.framework.scloud.service;

import com.kongtrolink.framework.scloud.constant.Regex;
import org.springframework.stereotype.Service;

/**
 * 设备导入Excel 接口类
 * Created by Eric on 2020/4/16.
 */
@Service
public class DeviceExcelService {

    private boolean[] nullable = {false, false, false, false, false, true, true};
    private String[] regex = {Regex.SITE_CODE, Regex.RESOURCE_NAME, Regex.RESOURCE_NAME, Regex.DEFAULT,
            Regex.DEFAULT, Regex.DEFAULT, Regex.IP};


}
