package com.kongtrolink.framework.util;

import com.kongtrolink.framework.model.station.StationCode;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by mystoxlol on 2018/12/7, 15:01.
 * company: kongtrolink
 * description:
 * update record:
 */
public class StationCodeUtil
{
    public static Map<String,String> toCodeMap(List<StationCode> stationCodeList)
    {
        Map<String, String> result = new HashMap<>();
        if (stationCodeList!=null && stationCodeList.size()>0)
        {

            for (StationCode stationCode:stationCodeList)
                result.put(stationCode.getCode(), stationCode.getName());
        }
        return result;
    }
}
