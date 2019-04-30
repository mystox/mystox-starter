package com.kongtrolink.gateway.nb.cmcc.entity;

import java.io.Serializable;

/**
 * xx
 * by Mag on 2018/12/5.
 */
public class DataPackAtt implements Serializable {

    private static final long serialVersionUID = -9041783718945785312L;

    private int DataPack;
    private int DataInd;

    public int getDataPack() {
        return DataPack;
    }

    public void setDataPack(int dataPack) {
        DataPack = dataPack;
    }

    public int getDataInd() {
        return DataInd;
    }

    public void setDataInd(int dataInd) {
        DataInd = dataInd;
    }
}
