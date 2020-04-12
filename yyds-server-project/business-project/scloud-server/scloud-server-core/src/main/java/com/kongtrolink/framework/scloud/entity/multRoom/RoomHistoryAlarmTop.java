package com.kongtrolink.framework.scloud.entity.multRoom;


import com.kongtrolink.framework.scloud.entity.FacadeView;

import java.io.Serializable;
import java.util.List;

/**
 * 历史告警 统计
 * by Mag on 6/20/2018.
 */
public class RoomHistoryAlarmTop implements Serializable{


    private static final long serialVersionUID = 5382917584208330976L;

    private FacadeView signal01;
    private FacadeView signal02;
    private FacadeView signal03;
    private List<RoomAlarmCount> infoList;

    public FacadeView getSignal01() {
        return signal01;
    }

    public void setSignal01(FacadeView signal01) {
        this.signal01 = signal01;
    }

    public FacadeView getSignal02() {
        return signal02;
    }

    public void setSignal02(FacadeView signal02) {
        this.signal02 = signal02;
    }

    public FacadeView getSignal03() {
        return signal03;
    }

    public void setSignal03(FacadeView signal03) {
        this.signal03 = signal03;
    }

    public List<RoomAlarmCount> getInfoList() {
        return infoList;
    }

    public void setInfoList(List<RoomAlarmCount> infoList) {
        this.infoList = infoList;
    }
}
