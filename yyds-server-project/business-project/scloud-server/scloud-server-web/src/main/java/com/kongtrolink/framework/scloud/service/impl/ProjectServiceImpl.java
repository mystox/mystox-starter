package com.kongtrolink.framework.scloud.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.kongtrolink.framework.core.entity.User;
import com.kongtrolink.framework.entity.ListResult;
import com.kongtrolink.framework.entity.MsgResult;
import com.kongtrolink.framework.scloud.constant.CommonConstant;
import com.kongtrolink.framework.scloud.constant.FsuOperationState;
import com.kongtrolink.framework.scloud.constant.ProjectOrderConstant;
import com.kongtrolink.framework.scloud.constant.ProjectOrderTestSignalEnum;
import com.kongtrolink.framework.scloud.dao.DeviceMongo;
import com.kongtrolink.framework.scloud.dao.DeviceSignalTypeMongo;
import com.kongtrolink.framework.scloud.dao.ProjectMongo;
import com.kongtrolink.framework.scloud.entity.*;
import com.kongtrolink.framework.scloud.entity.model.ProjectOrderModel;
import com.kongtrolink.framework.scloud.mqtt.entity.BasicDeviceEntity;
import com.kongtrolink.framework.scloud.mqtt.entity.CIResponseEntity;
import com.kongtrolink.framework.scloud.query.DeviceQuery;
import com.kongtrolink.framework.scloud.query.ProjectOrderQuery;
import com.kongtrolink.framework.scloud.service.AssetCIService;
import com.kongtrolink.framework.scloud.service.DeviceService;
import com.kongtrolink.framework.scloud.service.ProjectService;
import com.kongtrolink.framework.scloud.util.StringUtil;
import org.apache.commons.lang3.EnumUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * 工程管理 接口实现类
 * Created by Eric on 2020/4/13.
 */
@Service
public class ProjectServiceImpl implements ProjectService{

    @Autowired
    ProjectMongo projectMongo;
    @Autowired
    DeviceMongo deviceMongo;
    @Autowired
    DeviceSignalTypeMongo deviceSignalTypeMongo;
    @Autowired
    DeviceService deviceService;
    @Autowired
    AssetCIService assetCIService;

    private static final Logger LOGGER = LoggerFactory.getLogger(ProjectServiceImpl.class);

    /**
     * 获取测试单列表
     */
    @Override
    public ListResult<ProjectOrderModel> getProjectOrderList(String uniqueCode, ProjectOrderQuery projectOrderQuery) {
        List<ProjectOrderModel> list = new ArrayList<>();

        List<String> fsuCodes = null;
        //查询满足条件的FSU
        if (!StringUtil.isNUll(projectOrderQuery.getManufacturer()) || !StringUtil.isNUll(projectOrderQuery.getFsuState())){
            DeviceQuery deviceQuery = new DeviceQuery();
            deviceQuery.setCurrentPage(1);
            deviceQuery.setPageSize(Integer.MAX_VALUE);
            deviceQuery.setManufacturer(projectOrderQuery.getManufacturer());
            deviceQuery.setState(projectOrderQuery.getFsuState());
            deviceQuery.setDeviceType(CommonConstant.DEVICE_TYPE_FSU);
            List<DeviceEntity> fsuList = deviceMongo.findDeviceList(uniqueCode, deviceQuery);
            fsuCodes = new ArrayList<>();
            for (DeviceEntity fsu : fsuList){
                fsuCodes.add(fsu.getCode());
            }
        }

        //获取测试单
        if (fsuCodes != null) {
            projectOrderQuery.setDeviceCodes(fsuCodes);
        }
        int count = projectMongo.countProjectOrder(uniqueCode, projectOrderQuery);
        List<ProjectOrderEntity> projectOrderList = projectMongo.findProjectOrders(uniqueCode, projectOrderQuery);
        if (projectOrderList != null && projectOrderList.size() > 0) {
            if (fsuCodes == null){
                fsuCodes = new ArrayList<>();
            }
            for (ProjectOrderEntity projectOrder : projectOrderList){
                fsuCodes.add(projectOrder.getFsuCode());
            }

            DeviceQuery deviceQuery = new DeviceQuery();
            deviceQuery.setServerCode(projectOrderQuery.getServerCode());
            deviceQuery.setSiteCodes(projectOrderQuery.getSiteCodes());
            deviceQuery.setDeviceCodes(fsuCodes);
            List<RelatedDeviceInfo> fsuInfos = deviceService.findFsuList(uniqueCode, deviceQuery);
            Map<String, RelatedDeviceInfo> map = new HashMap<>(); //key:FSU设备Code, value:FSU设备相关信息
            for (RelatedDeviceInfo fsuInfo : fsuInfos){
                map.put(fsuInfo.getDeviceCode(), fsuInfo);
            }

            for (ProjectOrderEntity projectOrder : projectOrderList){
                ProjectOrderModel model = getProjectOrderModel(projectOrder);

                model.setFsuName(map.get(projectOrder.getFsuCode()).getDeviceName());
                model.setManufacturer(map.get(projectOrder.getFsuCode()).getManufacturer());
                model.setFsuState(map.get(projectOrder.getFsuCode()).getState());
                model.setOperationState(map.get(projectOrder.getFsuCode()).getOperationState());

                list.add(model);
            }
        }

        return new ListResult<>(list, count);
    }

    //返回给前端的测试单数据模型
    private ProjectOrderModel getProjectOrderModel(ProjectOrderEntity projectOrderEntity){
        ProjectOrderModel projectOrderModel = new ProjectOrderModel();
        projectOrderModel.setId(projectOrderEntity.getId());
        projectOrderModel.setSiteCode(projectOrderEntity.getSiteCode());
        projectOrderModel.setFsuCode(projectOrderEntity.getFsuCode());
        projectOrderModel.setDesc(projectOrderEntity.getDesc());
        projectOrderModel.setApplicantName(projectOrderEntity.getApplicantName());
        projectOrderModel.setApplicantPhone(projectOrderEntity.getApplicantName());
        projectOrderModel.setApplyTime(projectOrderEntity.getApplyTime());
        projectOrderModel.setState(projectOrderEntity.getState());

        return projectOrderModel;
    }

    /**
     * 创建测试单
     */
    @Override
    public void createProjectOrder(String uniqueCode, User user, ProjectOrderEntity projectOrderEntity) {
        Long currentTime = new Date().getTime();

        //保存测试单
        projectOrderEntity.setState(ProjectOrderConstant.STATE_SUBMIT);
        projectOrderEntity.setApplyTime(currentTime);
        projectMongo.saveProjectOrder(uniqueCode, projectOrderEntity);

        //更新FSU运行状态为"测试态"
        deviceMongo.updateFsuOperationState(uniqueCode, projectOrderEntity.getFsuCode(), FsuOperationState.TEST);

        //保存测试单操作记录
        projectMongo.saveProjectOrderLog(uniqueCode,
                new ProjectOrderLogEntity(projectOrderEntity.getId()
                        , new FacadeView(user.getId(),user.getName())
                        , ProjectOrderConstant.ACTION_CREATE
                        , null
                        , null
                        , currentTime));
    }

    /**
     * 获取测试单操作记录
     */
    @Override
    public List<ProjectOrderLogEntity> getOrderLogs(String uniqueCode, ProjectOrderQuery projectOrderQuery) {
        return projectMongo.findProjectOrderLogs(uniqueCode, projectOrderQuery);
    }

    /**
     * 生成测试单测试项
     */
    @Override
    public void createProjectOrderTest(String uniqueCode, ProjectOrderQuery projectOrderQuery) {

        List<ProjectOrderTestEntity> testList = new ArrayList<>();
        Map<String, String> deviceMap = projectOrderQuery.getDeviceMap();  //key:deviceCode, value:deviceName
        if (deviceMap.size() > 0){
            List<String> deviceCodes = new ArrayList<>(deviceMap.keySet()); //FSU关联设备的设备编码
            for (String deviceCode : deviceCodes){
                DeviceEntity device = deviceMongo.findDeviceByCode(uniqueCode, deviceCode);
                String deviceTypeCode = device.getTypeCode();
                //获取设备类型及信号类型
                DeviceType deviceType = deviceSignalTypeMongo.getByCode(uniqueCode, deviceTypeCode);
                if (deviceType != null && deviceType.getSignalTypeList() != null && deviceType.getSignalTypeList().size() > 0){
                    List<SignalType> signalTypeList = deviceType.getSignalTypeList();
                    for (SignalType signalType : signalTypeList){
                        // 21个测试信号点
                        // 开关电源06：交流输入XX停电告警016、防雷器故障告警022、整流模块XX故障告警024、交流输入XX相电压Ua101、交流输入XX相电压Ub102、交流输入XX相电压Uc103、直流电压111、直流负载总电流112、整流模块XX电流113
                        // 蓄电池组07：总电压102、前半组电压106、后半组电压107
                        // 普通空调15：回风温度102
                        // 智能电表（交流）16：A相电压Ua104、B相电压Ub105、C相电压Uc106
                        // 机房环境18：水浸告警001、烟雾告警002、红外告警003、环境温度101、环境湿度102
                        String cntbId = signalType.getCntbId();
                        if (isValidEnum(cntbId)){
                            ProjectOrderTestEntity orderTest = new ProjectOrderTestEntity(projectOrderQuery.getOrderId()
                                    , deviceMap.get(deviceCode)
                                    , deviceCode
                                    , signalType.getCntbId()
                                    , signalType.getType()
                                    , signalType.getTypeName()
                                    , signalType.getMeasurement());

                            testList.add(orderTest);
                        }
                    }
                }
            }
        }

        //删除原先的测试项列表
        projectMongo.removeOrderTest(uniqueCode, projectOrderQuery);

        //保存最新的测试项列表
        if (testList.size() >0) {
            projectMongo.saveOrderTest(uniqueCode, testList);
        }
    }

    private boolean isValidEnum(String cntbId){
        for (ProjectOrderTestSignalEnum signalEnum : ProjectOrderTestSignalEnum.values()){
            if (signalEnum.getCntbId().equals(cntbId)){
                return true;
            }
        }
        return false;
    }

    /**
     * 获取测试项列表
     */
    @Override
    public List<ProjectOrderTestEntity> getProjectOrderTest(String uniqueCode, ProjectOrderQuery projectOrderQuery) {
        return projectMongo.findOrderTest(uniqueCode, projectOrderQuery);
    }

    /**
     * 提交测试单
     */
    @Override
    public void submitProjectOrder(String uniqueCode, User user, List<ProjectOrderTestEntity> list) {
        for (ProjectOrderTestEntity orderTest : list){
            //更新测试项
            projectMongo.updateOrderTest(uniqueCode, orderTest);
        }

        //更新测试单状态
        projectMongo.updateProjectOrderState(uniqueCode, list.get(0).getOrderId(), ProjectOrderConstant.STATE_CHECK);

        //保存测试单操作记录
        projectMongo.saveProjectOrderLog(uniqueCode,
                new ProjectOrderLogEntity(list.get(0).getOrderId()
                        , new FacadeView(user.getId(),user.getName())
                        , ProjectOrderConstant.ACTION_SUBMIT
                        , null
                        , null
                        , new Date().getTime()));
    }

    /**
     * 审核测试单
     *
     * @param uniqueCode
     * @param user
     * @param projectOrderQuery
     */
    @Override
    public void projectOrderCheck(String uniqueCode, User user, ProjectOrderQuery projectOrderQuery) {
        String orderId = projectOrderQuery.getOrderId();
        String fsuCode = projectOrderQuery.getFsuCode();
        String state = projectOrderQuery.getState();
        String suggestion = projectOrderQuery.getSuggestion();
        String remark = projectOrderQuery.getRemark();

        //更新测试单状态
        projectMongo.updateProjectOrderState(uniqueCode, orderId, state);

        String actionStr = null;
        String fsuOperationState = null;
        if(ProjectOrderConstant.STATE_SUBMIT.equals(state)){
            actionStr = ProjectOrderConstant.ACTION_RETEST;
            fsuOperationState = FsuOperationState.TEST;
        }else if(ProjectOrderConstant.STATE_PASS.equals(state)){
            actionStr = ProjectOrderConstant.ACTION_PASS;
            fsuOperationState = FsuOperationState.MAINTENANCE;
        }else if(ProjectOrderConstant.STATE_CANCEL.equals(state)){
            actionStr = ProjectOrderConstant.ACTION_CANCEL;
            fsuOperationState = FsuOperationState.PROJECT;
        }
        //保存测试单操作记录
        projectMongo.saveProjectOrderLog(uniqueCode
                , new ProjectOrderLogEntity(orderId, new FacadeView(user.getId(), user.getName()), actionStr, suggestion, remark, new Date().getTime()));

        if(fsuOperationState != null){
            //更新FSU运行状态
            deviceMongo.updateFsuOperationState(uniqueCode, fsuCode, fsuOperationState);
        }
    }

    /**
     * 获取测试单下测试设备列表
     */
    @Override
    public List<RelatedDeviceInfo> getProjectOrderDevices(String uniqueCode, DeviceQuery deviceQuery) {
        List<RelatedDeviceInfo> list = new ArrayList<>();
        String deviceCode = deviceQuery.getDeviceCode();
        if (deviceCode != null && !deviceCode.equals("")){
            //从【中台-资管】获取FSU下的关联设备
            MsgResult msgResult = assetCIService.getRelatedDeviceList(uniqueCode, deviceQuery);
            if (msgResult.getStateCode() == CommonConstant.SUCCESSFUL){ //通信成功
                CIResponseEntity response = JSONObject.parseObject(msgResult.getMsg(), CIResponseEntity.class);
                if (response.getResult() == CommonConstant.SUCCESSFUL) { //请求成功
                    LOGGER.info("【工程管理】,从【资管】获取FSU下的关联设备 成功");
                    //根据中台返回的对应设备资产信息，过滤掉不满足测试单测试的设备，再拼成返回给前端的设备数据模型
                    list = getProjectOrderDeviceInfoList(uniqueCode, response, list);
                } else {
                    LOGGER.error("【工程管理】,从【资管】获取FSU下的关联设备 失败");
                }
            }else {
                LOGGER.error("【工程管理】,从【资管】获取FSU下的关联设备 MQTT通信失败");
            }
        }

        return list;
    }

    //根据中台返回的对应设备资产信息，过滤掉不满足测试单测试的设备，再拼成返回给前端的设备数据模型
    private List<RelatedDeviceInfo> getProjectOrderDeviceInfoList(String uniqueCode, CIResponseEntity response, List<RelatedDeviceInfo> list){
        for (JSONObject jsonObject : response.getInfos()) {
            BasicDeviceEntity basicDeviceEntity = JSONObject.parseObject(jsonObject.toJSONString(), BasicDeviceEntity.class);
            String assetType = basicDeviceEntity.getAssetType();
            if (assetType.equals("开关电源")
                    || assetType.equals("蓄电池组")
                    || assetType.equals("普通空调")
                    || assetType.equals("智能电表（交流）")
                    || assetType.equals("水浸传感器")
                    || assetType.equals("红外传感器")
                    || assetType.equals("温湿度传感器")
                    || assetType.equals("烟雾传感器")) {
                RelatedDeviceInfo relatedDeviceInfo = new RelatedDeviceInfo();
                relatedDeviceInfo.setDeviceName(basicDeviceEntity.getDeviceName());
                relatedDeviceInfo.setDeviceCode(basicDeviceEntity.getCode());
                relatedDeviceInfo.setType(basicDeviceEntity.getAssetType());
                relatedDeviceInfo.setModel(basicDeviceEntity.getModel());

                DeviceEntity device = deviceMongo.findDeviceByCode(uniqueCode, basicDeviceEntity.getCode());
                if (device != null) {
                    relatedDeviceInfo.setTypeCode(device.getTypeCode());
                    relatedDeviceInfo.setState(device.getState());
                    relatedDeviceInfo.setOperationState(device.getOperationState());
                    relatedDeviceInfo.setManufacturer(device.getManufacturer());
                }

                list.add(relatedDeviceInfo);
            }
        }

        return list;
    }

}
