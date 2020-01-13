package com.kongtrolink.framework.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.kongtrolink.framework.base.Contant;
import com.kongtrolink.framework.base.EnumLevelName;
import com.kongtrolink.framework.base.FacadeView;
import com.kongtrolink.framework.core.entity.User;
import com.kongtrolink.framework.core.entity.session.BaseController;
import com.kongtrolink.framework.entity.JsonResult;
import com.kongtrolink.framework.entity.ListResult;
import com.kongtrolink.framework.entity.MsgResult;
import com.kongtrolink.framework.enttiy.*;
import com.kongtrolink.framework.query.InformRuleQuery;
import com.kongtrolink.framework.service.InformRuleService;
import com.kongtrolink.framework.service.InformRuleUserService;
import com.kongtrolink.framework.mqtt.service.MqttSender;
import com.kongtrolink.framework.service.MqttOpera;
import com.kongtrolink.framework.service.MsgTemplateService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @Auther: liudd
 * @Date: 2019/10/11 09:41
 * @Description:告警通知规则
 */
@Controller
@RequestMapping("/deliverController")
public class InformRuleController extends BaseController {

    @Autowired
    InformRuleService ruleService;
    @Autowired
    InformRuleUserService ruleUserService;
    @Autowired
    MsgTemplateService msgTemplateService;
    @Autowired
    MqttSender mqttSender;
    @Autowired
    MqttOpera mqttOpera;

    private static final Logger logger = LoggerFactory.getLogger(InformRuleController.class);

    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public @ResponseBody
    JsonResult add(@RequestBody InformRule informRule, HttpServletRequest request ){
        //后端判定规则名称是否存在
        String name = informRule.getName();
        InformRule byName = ruleService.getByName(name);
        if(null != byName){
            return new JsonResult("规则名称"+name+"已存在！", false);
        }
        User user = getUser(request);
        if(null == user){
            user = new User();
        }
        user.setId("admin");
        user.setName("超级管理员");
        Date curDate = new Date();
        informRule.setOperator(new FacadeView(user.getId(), user.getName()));
        informRule.setUpdateTime(curDate);
        informRule.initDateInt();
        informRule.setRuleType(Contant.MANUAL);
        initTemplate(informRule);

        boolean result = ruleService.save(informRule);
        if(result){
            return new JsonResult(Contant.OPE_ADD + Contant.RESULT_SUC, true);
        }
        return new JsonResult(Contant.OPE_ADD + Contant.RESULT_FAIL, false);
    }

    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    public @ResponseBody JsonResult delete(@RequestBody InformRule informRule) {
        InformRule sourceRule = ruleService.get(informRule.get_id());
        if(null != sourceRule && Contant.SYSTEM.equals(sourceRule.getRuleType())){
            return new JsonResult("默认规则不允许删除", false);
        }
        boolean result = ruleService.delete(informRule.get_id());
        if (result) {
            //删除使用该规则的用户
            ruleUserService.deleteByRuleId(informRule.get_id());
            return new JsonResult(Contant.DELETED + Contant.RESULT_SUC, true);
        }else {
            return new JsonResult(Contant.DELETED + Contant.RESULT_FAIL, false);
        }
    }

    /**
     * @auther: liudd
     * @date: 2018/7/3 18:56
     * 功能描述:修改
     */
    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public @ResponseBody JsonResult update(@RequestBody InformRule informRule) {
        informRule.initDateInt();
        if(null != informRule && Contant.SYSTEM.equals(informRule.getRuleType())){
            return new JsonResult("默认规则不允许修改", false);
        }
        informRule.setRuleType(Contant.MANUAL);
        informRule.setUpdateTime(new Date());
        initTemplate(informRule);
        boolean result = ruleService.update(informRule);
        if (result) {
            return new JsonResult(Contant.OPE_UPDATE + Contant.RESULT_SUC, true);
        }else {
            return new JsonResult(Contant.OPE_UPDATE + Contant.RESULT_FAIL, false);
        }
    }

    /**
     * @auther: liudd
     * @date: 2018/7/3 18:59
     * 功能描述:获取列表
     */
    @RequestMapping(value = "/list")
    public @ResponseBody JsonResult list(@RequestBody InformRuleQuery ruleQuery) {
        List<InformRule> list = ruleService.list(ruleQuery);
        int count = ruleService.count(ruleQuery);
        ListResult<InformRule> listResult = new ListResult<>(list, count);
        return new JsonResult(listResult);
    }

    /**
     * @auther: liudd
     * @date: 2018/7/3 19:01
     * 功能描述:判定规则名称是否已在使用
     */
    @RequestMapping(value = "/checkName")
    public @ResponseBody JsonResult checkName(@RequestBody InformRuleQuery ruleQuery){
        InformRule informRule = ruleService.getByName(ruleQuery.getName());
        if(null != informRule){
            return new JsonResult("规则名称已存在", false);
        }
        return new JsonResult("规则名称可用", true);
    }

    /**
     * @auther: liudd
     * @date: 2018/7/3 19:07
     * 功能描述:启用或者禁用规则
     */
    @RequestMapping(value = "/updateStatus")
    public @ResponseBody JsonResult enabled(@RequestBody InformRuleQuery ruleQuery) {
        boolean result = ruleService.updateStatus(ruleQuery.get_id(), ruleQuery.getStatus());
        if(result){
            return new JsonResult(ruleQuery.getStatus() + Contant.RESULT_SUC, true);
        }else{
            return new JsonResult(ruleQuery + Contant.RESULT_FAIL, false);
        }
    }

    /**
     * @auther: liudd
     * @date: 2019/10/11 16:22
     * 功能描述:获取启用该通知规则的用户
     */
    @RequestMapping("/getUseList")
    public @ResponseBody JsonResult getUseList(@RequestBody InformRuleQuery ruleQuery){
        String id = ruleQuery.get_id();
        List<InformRuleUser> userList = ruleUserService.getByRuleId(id);
        return new JsonResult(userList);
    }

    /**
     * @auther: liudd
     * @date: 2019/10/11 16:19
     * 功能描述:授权
     */
    @RequestMapping("/authUser")
    public @ResponseBody JsonResult authUser(@RequestBody InformRuleQuery ruleQuery){
        List<String> userIds = ruleQuery.getUserIds();
        if(null != userIds && userIds.size()>0){
            //删除原来数据，保证一个用户只有一条启用的通知规则
            ruleUserService.deleteByRuleId(ruleQuery.get_id());
            ruleUserService.deleteByUserIds(userIds);
            List<String> usernames = ruleQuery.getUsernames();
            Date curTime = new Date();
            for(int i=0; i<userIds.size(); i++){
                InformRuleUser ruleUser = new InformRuleUser();
                ruleUser.setUpdateTime(curTime);
                ruleUser.setInformRule(new FacadeView(ruleQuery.get_id(), ruleQuery.getName()));
                ruleUser.setUser(new FacadeView(userIds.get(i), usernames.get(i)));
                ruleUserService.save(ruleUser);
            }
        }
        return new JsonResult(Contant.OPE_AUTH + Contant.RESULT_SUC, true);
    }

    /**
     * @auther: liudd
     * @date: 2019/10/17 16:36
     * 功能描述:获取企业和服务下所有用户
     */
    @RequestMapping("/getUserList")
    @ResponseBody
    public JsonResult getUserList(@RequestBody InformRuleQuery informRuleQuery){
        String enterpriseCode = informRuleQuery.getEnterpriseCode();
        String serverCode = informRuleQuery.getServerCode();
        List<User> userList = new ArrayList<>();
        User user1 = new User();
        user1.setId("392e4847-abf5-48a7-b6a4-f2bdd41bf1c2");
        user1.setUsername("dadd");
        user1.setName("大冬冬");
        user1.setPhone("15267071111");
        user1.setEmail("152222QQ.com");
        userList.add(user1);

        User user2 = new User();
        user2.setId("94132e81-1602-4036-8b15-6bb2f8dff089");
        user2.setUsername("dagg");
        user2.setName("大刚哥");
        user2.setPhone("15267072222");
        user2.setEmail("152222QQ.com");
        userList.add(user2);
        List<JSONObject> managerUsersMsg = getManagerUsersMsg();
        System.out.println(managerUsersMsg);
        return new JsonResult(managerUsersMsg);
    }

    public void initTemplate(InformRule informRule){
        FacadeView msgTemp = informRule.getMsgTemplate();
        if(null != msgTemp){
            MsgTemplate msgTemplate = msgTemplateService.get(msgTemp.getStrId());
            if(null != msgTemplate){
                informRule.initTemplate(msgTemplate);
            }
        }

        FacadeView emailTemp = informRule.getEmailTemplate();
        if(null != emailTemp){
            MsgTemplate msgTemplate = msgTemplateService.get(emailTemp.getStrId());
            if(null != msgTemplate){
                informRule.initTemplate(msgTemplate);
            }
        }
        FacadeView appTemp = informRule.getAppTemplate();
        if(null != appTemp){
            MsgTemplate msgTemplate = msgTemplateService.get(appTemp.getStrId());
            if(null != msgTemplate){
                informRule.initTemplate(msgTemplate);
            }
        }
    }

    /**
     * @auther: liudd
     * @date: 2019/10/29 14:00
     * 功能描述:测试其他模块接口
     */
    @RequestMapping("/testServer")
    @ResponseBody
    public JsonResult testServer(@RequestBody InformMsg informMsg){

        String serverVerson = informMsg.getServerVerson();
        String operateCode = informMsg.getOperateCode();
        String tempCode = informMsg.getTempCode();
        JSONObject jsonObject = new JSONObject();
        String msgStr = null;
        if("1".equals(tempCode)) {
            //获取区域下用户列表：AUTH_PLATFORM_1.0.0/getUserListByRegionCodes
            jsonObject.put("serverCode", "AUTH_PLATFORM");
            List<String> regionCodes = new ArrayList<>();
            regionCodes.add("220281");
            regionCodes.add("220282");
            jsonObject.put("regionCodes", regionCodes);
            System.out.println("获取区域下用户列表jsonObject:" + jsonObject);
            /*
            1;[{"classes":"企业用户","companyId":"c40e5fd6-2d94-47a2-8110-5084fd782ae6","currentOrgId":"c40e5fd6-2d94-47a2-8110-5084fd782ae6","currentOrgName":"zuzhi1","currentOrgType":"DEPARTMENT","currentPositionName":"mystoxlol","currentPostId":"b1978b5e-052a-4de7-882d-54a0cb3ccd62","currentRoleId":"b1978b5e-052a-4de7-882d-54a0cb3ccd62","currentRoleName":"mystoxlol","department":"zuzhi1","email":"mystox@163.com","errorCode":"","id":"5a12a0504817ea147350dbe1","job":"mystoxlol","message":"","name":"jxyd","password":"fcea920f7412b5da7be0cf42b8c93759","phone":"15067455667","receiveAlarmEmail":"0","receiveAlarmMsg":"1","receiveAlarmPush":"0","receiveWorkPush":"0","success":false,"type":"mystoxlol","uniqueCode":"zuzhi1","userGroup":"mystoxlol","userId":"5a12a0504817ea147350dbe1","username":"jxyd"}]
             */
        }else if("2".equals(tempCode)){
           /* {"serverVerson":"AUTH_PLATFORM_1.0.0", "operateCode":"getRegionCodeEntity", "tempCode":"2"}*/
            //根据地区编码列表获取地区名称 getRegionCodeEntity
            List<String> regionCodes = new ArrayList<>();
            regionCodes.add("220281");
            regionCodes.add("330301");
            regionCodes.add("000000");
            msgStr = regionCodes.toString();
            /*
            [{"code":"220281","id":"220281","latitude":43.716756,"longitude":127.351742,"name":"[\"吉林省\",\"吉林市\",\"蛟河市\"]"},{"code":"330301","id":"330301","latitude":28.002838,"longitude":120.690635,"name":"[\"浙江省\",\"温州市\",\"市辖区\"]"}]
             */
        }else if("5".equals(tempCode)){
            /* {"serverVerson":"AUTH_PLATFORM_1.0.0", "operateCode":"getRegionListByUsers", "tempCode":"5", "serverCode":"AUTH_PLATFORM_1.0.0"}*/
            //根据用户id，获取用户管理权限以及用户信息AUTH_PLATFORM_1.0.0/getRegionListByUsers;;TOWER_SERVER
            jsonObject.put("serverCode", informMsg.getServerCode());
            List<String> userIdList = new ArrayList<>();
            userIdList.add("392e4847-abf5-48a7-b6a4-f2bdd41bf1c2");
            userIdList.add("94132e81-1602-4036-8b15-6bb2f8dff089");
            jsonObject.put("userIds", userIdList);
            msgStr = jsonObject.toJSONString();
        }
        System.out.println("serverVerson:" + serverVerson +"; operateCode: "+operateCode+";msgStr:" + msgStr);
//        MsgResult msgResult = mqttSender.sendToMqttSync(serverVerson, operateCode, msgStr);
        MsgResult msgResult = mqttOpera.opera(operateCode,msgStr);
        if("5".equals(tempCode)){
            JSONObject resultObject = JSONObject.parseObject(msgResult.getMsg(), JSONObject.class);
            for(String userId : resultObject.keySet()){
                JSONObject userInfo = (JSONObject)resultObject.get(userId);
                System.out.println("userInfo:" + userInfo.toJSONString());
                String phone = userInfo.getString("phone");
                String email = userInfo.getString("email");
                List<String> regions = (List<String>)userInfo.get("region");
                System.out.println("regions:" + regions);

            }

        }
        return new JsonResult(msgResult);
    }

    @RequestMapping("/testAsset")
    @ResponseBody
    public JsonResult testAsset(@RequestBody InformRule informRule){
        String msgServerVerson = informRule.getMsgServerVerson();
        String msgOperaCode = informRule.getMsgOperaCode();
        String describe = informRule.getDescribe();
        //根据sns，从资产管理获取设备信息（包含address）ASSET_MANAGEMENT_SERVER_1.0.0/getCI, describe表示设备id
        List<String> sns = new ArrayList<>();
        sns.add(describe);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("sns", sns);
        System.out.println("jsonObject:" + jsonObject.toJSONString());
//        MsgResult msgResult = mqttSender.sendToMqttSync(msgServerVerson, msgOperaCode, jsonObject.toJSONString());
        MsgResult msgResult = mqttOpera.opera( msgOperaCode, jsonObject.toJSONString());
        String msg = msgResult.getMsg();
        System.out.println(msgResult.getStateCode() + ";" + msg);
        return new JsonResult(msgResult);
    }

    /**
     * @auther: liudd
     * @date: 2019/11/4 14:40
     * 功能描述:测试发送接口
     */
    @RequestMapping("/testSend")
    @ResponseBody
    public JsonResult testSend(@RequestBody InformMsg informMsg){

        /*
            短信：
        {"serverVerson":"ALARM_SERVER_SENDER_V1.0.0", "operateCode":"handleSender",
         "type":"短信", "url":"http://sendcloud.sohu.com/smsapi/send", "tempCode":"1144", "informAccount":"15267071976",
           "enterpriseName":"义益钛迪", "serverName":"铁塔服务", "addressName":"浙江省杭州市江干区九堡国家大学科技园", "alarmName":"整流模块01故障告警",
           "alarmStateType":"告警产生"
           }
         */
        //发送短信 tempCode = "1144", account=15267071976, type = Contant.TEMPLATE_MSG
        //ALARM_SERVER-SENDER_DEFAULT_V1.0.0/handleSender;emailOperaCode = account, emailReportCode = tempCode, emailResolveCode = type
        JSONObject jsonObject = (JSONObject) JSON.toJSON(informMsg);

        /*
        邮件：
        {"serverVerson":"ALARM_SERVER_SENDER_V1.0.0", "operateCode":"handleSender",
         "type":"邮件", "url":"http://api.sendcloud.net/apiv2/mail/sendtemplate", "tempCode":"power_alarm_templetId", "informAccount":"3243095682@qq.com",
           "enterpriseName":"义益钛迪", "serverName":"铁塔服务", "addressName":"浙江省杭州市江干区九堡国家大学科技园", "alarmName":"总电流过低告警",
           "alarmStateType":"告警产生"
           }
         */

        /*
        APP：
        {"serverVerson":"ALARM_SERVER_SENDER_V1.0.0", "operateCode":"handleSender",
         "type":"APP", "url":"http://api.sendcloud.net/apiv2/mail/sendtemplate", "tempCode":"101", "informAccount":"5ab45064fef5e1514ffd4581",
           "enterpriseName":"义益钛迪", "serverName":"铁塔服务", "addressName":"浙江省杭州市江干区九堡国家大学科技园", "alarmName":"总电流过低告警APP",
           "alarmStateType":"告警产生"
           }
         */
//        MsgResult msgResult = mqttSender.sendToMqttSync(informMsg.getServerVerson(), informMsg.getOperateCode(), jsonObject.toJSONString());
        MsgResult msgResult = mqttOpera.opera(informMsg.getOperateCode(), jsonObject.toJSONString());
        String msg = msgResult.getMsg();
        System.out.println(msgResult.getStateCode() + ";" + msg);

        return new JsonResult(msgResult);
    }

    @RequestMapping("/testDeliver")
    @ResponseBody
    public JsonResult testDeliver(@RequestBody Alarm alarm){
        alarm.setTreport(new Date());
        alarm.setTrecover(new Date());
        alarm.setTargetLevel(alarm.getLevel());
        alarm.setTargetLevelName(EnumLevelName.getNameByLevel(alarm.getLevel()));
        String serverVersion = "ALARM_SERVER_DELIVER_V1.0.0";
        String operateCode = "handleDeliver";
        List<Alarm> alarms = new ArrayList<>();
        alarms.add(alarm);
        String reportAlarmListJson = JSONObject.toJSONString(alarms);
        System.out.println(reportAlarmListJson);
        //{"enterpriseServer":"yytd","enterpriseCode":"yytd","serverCode":"TOWER_SERVER", "deviceId":"10010_1021006","deviceModel":"YY006","deviceType":"yy6","flag":1,"level":"1","name":"整流模块01故障告警","serial":"12","signalId":"024001"}
//        MsgResult msgResult = mqttSender.sendToMqttSync(serverVersion, operateCode, reportAlarmListJson);
        MsgResult msgResult = mqttOpera.opera(operateCode, reportAlarmListJson);
        String msg = msgResult.getMsg();
        System.out.println(msgResult.getStateCode() + ";" + msg);

        return new JsonResult(msgResult);
    }

    @RequestMapping("/testMyServer")
    @ResponseBody
    public JsonResult testMyServer(@RequestBody InformMsg informMsg){
        //访问controller : serverVerson= ALARM_SERVER_CONTROLLER_V1.0.0 ;operateCode =alarmHandle
        //访问level:serverVersion = ALARM_SERVER_LEVEL, operateCode=handleLevel
        String serverVerson = informMsg.getServerVerson();
        String operateCode = informMsg.getOperateCode();
        String alarmMqttJson = createAlarmMqttJson();
        logger.info("serverVerson:{}, operateCode:{}, msg:{}", serverVerson, operateCode, alarmMqttJson);
        mqttSender.sendToMqtt(serverVerson, operateCode, alarmMqttJson);
        return new JsonResult("成功");
    }

    private String createAlarmMqttJson(){
        String enterpriseCode = "yytd";
        String serverCode = "TOWER_SERVER";
        List<Alarm> alarms = new ArrayList<>();
        alarms.add(getRandomAlarm());
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("enterpriseCode", enterpriseCode);
        jsonObject.put("serverCode", serverCode);
        jsonObject.put("alarms", alarms);
        return jsonObject.toJSONString();
    }

    private Alarm getRandomAlarm(){
        Alarm alarm = new Alarm();
        alarm.setDeviceType("yy6");
        alarm.setDeviceModel("YY006");
        alarm.setDeviceId("10010_1021006");
        alarm.setSignalId("024001");
        alarm.setName("测试高温告警");
        int serial = (int) (1 + Math.random() * (100000 - 1 + 1));
        alarm.setSerial(""+serial);
        alarm.setFlag("1");
        alarm.setLevel(3);
        alarm.setValue(666);
        return alarm;
    }
}
