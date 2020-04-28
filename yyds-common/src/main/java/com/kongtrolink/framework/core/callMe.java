package com.kongtrolink.framework.core;

public interface callMe {
    public  final static int Unknown=-1;
    public  final static int Disconnected=0;
    public  final static int NoSyncConnected=1;
    public  final static int SyncConnected=3;
    public  final static int AuthFailed=4;
    public  final static int ConnectedReadOnly=5;
    public  final static int SaslAuthenticated=6;
    public  final static int RebuidStatus=10;
    public  final static int Expired=-112;
    public void call(int code) throws InterruptedException;
}
