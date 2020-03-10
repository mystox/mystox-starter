package com.kongtrolink.framework.reports.utils;

import com.kongtrolink.framework.reports.entity.query.FsuEntity;
import com.kongtrolink.framework.reports.entity.query.FsuOperationState;

import java.util.List;

/**
 * \* @Author: mystox
 * \* Date: 2020/3/10 13:28
 * \* Description:
 * \
 */
public class CommonCheck {
    /**
     * @Date 13:48 2020/3/10
     * @Param No such property: code for class: Script1
     * @return java.lang.String
     * @Author mystox
     * @Description //判断交维态
     **/
    public static String fsuOperaStateCheck(List<FsuEntity> fsuEntityList) {

        for(FsuEntity fsuEntity: fsuEntityList)
        {
            if (FsuOperationState.PROJECT.equals(fsuEntity.getOperationState()))
                return FsuOperationState.PROJECT;
        }
        for(FsuEntity fsuEntity: fsuEntityList)
        {
            if (FsuOperationState.TEST.equals(fsuEntity.getOperationState()))
                return FsuOperationState.TEST;
        }
        return FsuOperationState.MAINTENANCE;
    }
}