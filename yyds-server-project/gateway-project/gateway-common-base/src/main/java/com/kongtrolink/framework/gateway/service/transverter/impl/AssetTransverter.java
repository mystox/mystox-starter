package com.kongtrolink.framework.gateway.service.transverter.impl;

import com.kongtrolink.framework.common.util.MqttUtils;
import com.kongtrolink.framework.entity.OperaCode;
import com.kongtrolink.framework.entity.ServerName;
import com.kongtrolink.framework.gateway.entity.ParseProtocol;
import com.kongtrolink.framework.gateway.entity.Transverter;
import com.kongtrolink.framework.gateway.service.transverter.TransverterHandler;
import org.springframework.beans.factory.annotation.Value;

/**
 * Created by mystoxlol on 2019/10/16, 15:28.
 * company: kongtrolink
 * description: 资产协议转换器
 * update record:
 */
@Transverter("asset")
public class AssetTransverter extends TransverterHandler {
    @Value("gateway.assetReport.version:1.0.0")
    private String assetServerVersion;


    @Override
    protected void transferExecute(ParseProtocol parseProtocol) {


        //TODO
        String jsonResult = "";
        reportMsg(MqttUtils.preconditionServerCode(ServerName.ASSET_MANAGEMENT_SERVER,assetServerVersion),
                OperaCode.DEVICE_REPORT,jsonResult);
    }
}
