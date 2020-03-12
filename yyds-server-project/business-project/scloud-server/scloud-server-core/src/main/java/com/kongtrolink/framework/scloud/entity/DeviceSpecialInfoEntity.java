package com.kongtrolink.framework.scloud.entity;

/**
 * 不同设备特有属性 数据实体类
 * Created by Eric on 2020/2/12.
 */
public class DeviceSpecialInfoEntity {

    private int deviceId;   //设备ID
    private String deviceCode;  //设备编码

    //FSU动环主机("038")
    private String adaptation;      //适配层
    private String engine;          //引擎
    private String sMinitor;        //SMonitor

    //开关电源("006")
    private Integer rectifacationCount;     //模块总数
    private String rectifacationModel;      //整流模块型号
    private Double outVoltage;              //额定输出电压(V)(24V/48V)
    private String monitorState;            //监控显示状态(正常/异常)
    private String antiThunderState;        //防雷模块指示（正常/异常）
    private String moduleState;             //模块功能状态(正常/异常)

    //油机（柴油发电机组）("005")
    private Double ratedVoltage;    //额定工作电压(380V/220V)
    private Double ratedPower;  //额定功率
    private String startMode;   //启动方式:自动、手动
    private String coolingMode; //冷却方式：风冷、水冷
    private Double tankVolume;  //油箱容积

    //蓄电池组("007")
    private Double groupCapacity;        //单组额定容量
    private Double monVoltageLevel;     //单体电压等级（V）(2V/6V/12V)
    private Integer groupCellCount;         //单组电池个数

    //普通空调("015")
    private String refrigerationEffect; //制冷效果：正常、故障
    private String unitState;   //户外机组状态：完好、破损、变形、被盗
    private String drainpipeState;  //排水管状态:正常、漏水
    private Boolean selfStart;   //停电自启功能：true=有、false=无
    private Double refrigeratingCapacity;    //制冷量
    private Double inputRatedPower;   //输入额定功率
                            //额定电压（与油机共用一个字段）

    //UPS设备("008")
    private Double inputRatedVoltage;   //输入额定电压
    private Double outputRatedVoltage;  //输出额定电压
                            //额定功率（与油机共用一个字段）

    public int getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(int deviceId) {
        this.deviceId = deviceId;
    }

    public String getDeviceCode() {
        return deviceCode;
    }

    public void setDeviceCode(String deviceCode) {
        this.deviceCode = deviceCode;
    }

    public String getAdaptation() {
        return adaptation;
    }

    public void setAdaptation(String adaptation) {
        this.adaptation = adaptation;
    }

    public String getEngine() {
        return engine;
    }

    public void setEngine(String engine) {
        this.engine = engine;
    }

    public String getsMinitor() {
        return sMinitor;
    }

    public void setsMinitor(String sMinitor) {
        this.sMinitor = sMinitor;
    }

    public Integer getRectifacationCount() {
        return rectifacationCount;
    }

    public void setRectifacationCount(Integer rectifacationCount) {
        this.rectifacationCount = rectifacationCount;
    }

    public String getRectifacationModel() {
        return rectifacationModel;
    }

    public void setRectifacationModel(String rectifacationModel) {
        this.rectifacationModel = rectifacationModel;
    }

    public Double getOutVoltage() {
        return outVoltage;
    }

    public void setOutVoltage(Double outVoltage) {
        this.outVoltage = outVoltage;
    }

    public String getMonitorState() {
        return monitorState;
    }

    public void setMonitorState(String monitorState) {
        this.monitorState = monitorState;
    }

    public String getAntiThunderState() {
        return antiThunderState;
    }

    public void setAntiThunderState(String antiThunderState) {
        this.antiThunderState = antiThunderState;
    }

    public String getModuleState() {
        return moduleState;
    }

    public void setModuleState(String moduleState) {
        this.moduleState = moduleState;
    }

    public Double getRatedVoltage() {
        return ratedVoltage;
    }

    public void setRatedVoltage(Double ratedVoltage) {
        this.ratedVoltage = ratedVoltage;
    }

    public Double getRatedPower() {
        return ratedPower;
    }

    public void setRatedPower(Double ratedPower) {
        this.ratedPower = ratedPower;
    }

    public String getStartMode() {
        return startMode;
    }

    public void setStartMode(String startMode) {
        this.startMode = startMode;
    }

    public String getCoolingMode() {
        return coolingMode;
    }

    public void setCoolingMode(String coolingMode) {
        this.coolingMode = coolingMode;
    }

    public Double getTankVolume() {
        return tankVolume;
    }

    public void setTankVolume(Double tankVolume) {
        this.tankVolume = tankVolume;
    }

    public Double getGroupCapacity() {
        return groupCapacity;
    }

    public void setGroupCapacity(Double groupCapacity) {
        this.groupCapacity = groupCapacity;
    }

    public Double getMonVoltageLevel() {
        return monVoltageLevel;
    }

    public void setMonVoltageLevel(Double monVoltageLevel) {
        this.monVoltageLevel = monVoltageLevel;
    }

    public Integer getGroupCellCount() {
        return groupCellCount;
    }

    public void setGroupCellCount(Integer groupCellCount) {
        this.groupCellCount = groupCellCount;
    }

    public String getRefrigerationEffect() {
        return refrigerationEffect;
    }

    public void setRefrigerationEffect(String refrigerationEffect) {
        this.refrigerationEffect = refrigerationEffect;
    }

    public String getUnitState() {
        return unitState;
    }

    public void setUnitState(String unitState) {
        this.unitState = unitState;
    }

    public String getDrainpipeState() {
        return drainpipeState;
    }

    public void setDrainpipeState(String drainpipeState) {
        this.drainpipeState = drainpipeState;
    }

    public Boolean getSelfStart() {
        return selfStart;
    }

    public void setSelfStart(Boolean selfStart) {
        this.selfStart = selfStart;
    }

    public Double getRefrigeratingCapacity() {
        return refrigeratingCapacity;
    }

    public void setRefrigeratingCapacity(Double refrigeratingCapacity) {
        this.refrigeratingCapacity = refrigeratingCapacity;
    }

    public Double getInputRatedPower() {
        return inputRatedPower;
    }

    public void setInputRatedPower(Double inputRatedPower) {
        this.inputRatedPower = inputRatedPower;
    }

    public Double getInputRatedVoltage() {
        return inputRatedVoltage;
    }

    public void setInputRatedVoltage(Double inputRatedVoltage) {
        this.inputRatedVoltage = inputRatedVoltage;
    }

    public Double getOutputRatedVoltage() {
        return outputRatedVoltage;
    }

    public void setOutputRatedVoltage(Double outputRatedVoltage) {
        this.outputRatedVoltage = outputRatedVoltage;
    }
}
