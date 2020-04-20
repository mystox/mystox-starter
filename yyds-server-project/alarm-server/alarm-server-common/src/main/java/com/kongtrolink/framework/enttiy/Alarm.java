package com.kongtrolink.framework.enttiy;

import com.kongtrolink.framework.base.Contant;
import com.kongtrolink.framework.base.DateUtil;
import com.kongtrolink.framework.base.FacadeView;
import com.kongtrolink.framework.base.MongTable;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * @Auther: liudd
 * @Date: 2019/9/11 14:19
 * @Description:告警基本类型
 */
public class Alarm {

    private String id;
    private String enterpriseCode;      //企业编码（uniqueCode）
    private String serverCode;
    private String serial;              //告警序列号
    private String name;                //告警名称
    private float value;                //告警值
    private Integer level;               //告警等级
    private String deviceType;          //设备型号，设备类型，与资管一致
    private String deviceModel;         //设备类型，设备型号，如果没有与deviceType一致
    private String deviceId;            //设备对应的编码，需要与资产管理对应，设备id，即SN(deviceCode)
    private String signalId;            //信号点id，信号点id必须有，如果消息报文不包含信号点，则需要根据业务定义相关信号点(cntbId)
    private String flag;                //告警标志（0-结束；1-上报）
    private Integer targetLevel;         //目标等级
    private String targetLevelName;     //目标等级名称
    private String color;               //告警颜色
    private Date treport;               //上报时间
    private Date trecover;              //消除时间
    private FacadeView recoverMan;      //消除人
    private String state;               //告警状态（待处理，已消除）
    private Map<String, String> auxilaryMap;    //附加属性列map
    private Map<String, String> deviceInfos;    //设备信息map
    private String type;                //告警类型（实时/历史）
    private Date hcTime;                //被周期处理时间
    private String entDevSig;           //enterprise_CodedeviceId_signalId， 用于告警关注，屏蔽等功能
    private String key ;                //唯一键，可作为索引
    private String checkState;          //告警确认状态
    private Date checkTime;             //确认时间
    private String checkContant;        //确认内容
    private FacadeView checker;         //确认人
    private String workCode;            //工单编码，用于前端展示

    public FacadeView getRecoverMan() {
        return recoverMan;
    }

    public void setRecoverMan(FacadeView recoverMan) {
        this.recoverMan = recoverMan;
    }

    public String getWorkCode() {
        return workCode;
    }

    public void setWorkCode(String workCode) {
        this.workCode = workCode;
    }

    public String getCheckState() {
        return checkState;
    }

    public void setCheckState(String checkState) {
        this.checkState = checkState;
    }

    public String getCheckContant() {
        return checkContant;
    }

    public void setCheckContant(String checkContant) {
        this.checkContant = checkContant;
    }

    public FacadeView getChecker() {
        return checker;
    }

    public void setChecker(FacadeView checker) {
        this.checker = checker;
    }

    public Date getCheckTime() {
        return checkTime;
    }

    public void setCheckTime(Date checkTime) {
        this.checkTime = checkTime;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public Date getHcTime() {
        return hcTime;
    }

    public void setHcTime(Date hcTime) {
        this.hcTime = hcTime;
    }

    public String getFlag() {
        return flag;
    }

    public void setFlag(String flag) {
        this.flag = flag;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Map<String, String> getDeviceInfos() {
        return deviceInfos;
    }

    public void setDeviceInfos(Map<String, String> deviceInfos) {
        this.deviceInfos = deviceInfos;
    }

    public String getEnterpriseCode() {
        return enterpriseCode;
    }

    public void setEnterpriseCode(String enterpriseCode) {
        this.enterpriseCode = enterpriseCode;
    }

    public String getServerCode() {
        return serverCode;
    }

    public void setServerCode(String serverCode) {
        this.serverCode = serverCode;
    }

    public String getSerial() {
        return serial;
    }

    public void setSerial(String serial) {
        this.serial = serial;
    }

    public String getSignalId() {
        return signalId;
    }

    public void setSignalId(String signalId) {
        this.signalId = signalId;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getTargetLevelName() {
        return targetLevelName;
    }

    public void setTargetLevelName(String targetLevelName) {
        this.targetLevelName = targetLevelName;
    }

    public String getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(String deviceType) {
        this.deviceType = deviceType;
    }

    public String getDeviceModel() {
        return deviceModel;
    }

    public void setDeviceModel(String deviceModel) {
        this.deviceModel = deviceModel;
    }

    public Map<String, String> getAuxilaryMap() {
        return auxilaryMap;
    }

    public void setAuxilaryMap(Map<String, String> auxilaryMap) {
        this.auxilaryMap = auxilaryMap;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public float getValue() {
        return value;
    }

    public void setValue(float value) {
        this.value = value;
    }

    public Date getTreport() {
        return treport;
    }

    public void setTreport(Date treport) {
        this.treport = treport;
    }

    public Date getTrecover() {
        return trecover;
    }

    public void setTrecover(Date trecover) {
        this.trecover = trecover;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getEnterpriseServer(){
        return this.enterpriseCode + Contant.UNDERLINE + this.serverCode;
    }

    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }

    public Integer getTargetLevel() {
        return targetLevel;
    }

    public void setTargetLevel(Integer targetLevel) {
        this.targetLevel = targetLevel;
    }

    public String getEntDevSig() {
        return entDevSig;
    }

    public void setEntDevSig(String entDevSig) {
        this.entDevSig = entDevSig;
    }

    public void initEntDevSig(){
        this.entDevSig = enterpriseCode + Contant.UNDERLINE + deviceId+ Contant.UNDERLINE +signalId;
    }

    public void initKey(){
        initEntDevSig();
        this.key = enterpriseCode + Contant.UNDERLINE + serverCode + Contant.COLON + deviceId + Contant.UNDERLINE + serial;
    }

    public String createHistoryTable(){
        String table = enterpriseCode + Contant.UNDERLINE + serverCode + Contant.UNDERLINE + MongTable.ALARM_HISTORY;
        table = table + Contant.UNDERLINE + DateUtil.getYear_week(treport);
        return table;
    }

    @Override
    public String toString() {
        return "Alarm{" +
                "enterpriseCode='" + enterpriseCode + '\'' +
                ", serverCode='" + serverCode + '\'' +
                ", name='" + name + '\'' +
                ", flag='" + flag + '\'' +
                ", state='" + state + '\'' +
                ", key='" + key + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Alarm alarm = (Alarm) o;

        if (enterpriseCode != null ? !enterpriseCode.equals(alarm.enterpriseCode) : alarm.enterpriseCode != null)
            return false;
        if (serverCode != null ? !serverCode.equals(alarm.serverCode) : alarm.serverCode != null) return false;
        if (serial != null ? !serial.equals(alarm.serial) : alarm.serial != null) return false;
        return deviceId != null ? deviceId.equals(alarm.deviceId) : alarm.deviceId == null;
    }

    @Override
    public int hashCode() {
        int result = enterpriseCode != null ? enterpriseCode.hashCode() : 0;
        result = 31 * result + (serverCode != null ? serverCode.hashCode() : 0);
        result = 31 * result + (serial != null ? serial.hashCode() : 0);
        result = 31 * result + (deviceId != null ? deviceId.hashCode() : 0);
        return result;
    }

    public static void main(String[] a)throws Exception{
        String dateStr = "2019-11-21 5:4:5";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-M-d H:m:s");
        System.out.println(simpleDateFormat.format(new Date()));
        Date parse = simpleDateFormat.parse(dateStr);
        SimpleDateFormat simple2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        System.out.println("dataStr:" + simple2.format(parse));
        for(int i=1; i<20; i++){
            int ran2 = (int) (Math.random()*10);
            System.out.println("i:" + i + "; ran2:" + ran2);
            int aa = ran2 % 2;
            System.out.println("i :" + i + "; aa:" + aa);

        }
    }
}
