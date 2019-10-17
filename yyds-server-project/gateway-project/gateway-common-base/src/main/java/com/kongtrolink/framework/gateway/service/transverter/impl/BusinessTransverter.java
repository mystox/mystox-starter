package com.kongtrolink.framework.gateway.service.transverter.impl;

import com.kongtrolink.framework.gateway.entity.ParseProtocol;
import com.kongtrolink.framework.gateway.service.transverter.TransverterHandler;
import com.kongtrolink.framework.stereotype.Transverter;
import org.springframework.stereotype.Service;

/**
 * Created by mystoxlol on 2019/10/16, 15:29.
 * company: kongtrolink
 * description: 其他业务转换器
 * update record:
 */
@Service
@Transverter(name = "businessCode")
public class BusinessTransverter extends TransverterHandler {

    @Override
    protected void transferExecute(ParseProtocol parseProtocol) {

    }
}
