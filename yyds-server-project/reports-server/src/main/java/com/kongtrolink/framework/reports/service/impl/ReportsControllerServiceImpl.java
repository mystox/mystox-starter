package com.kongtrolink.framework.reports.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.kongtrolink.framework.core.entity.User;
import com.kongtrolink.framework.entity.OperaCodePrefix;
import com.kongtrolink.framework.entity.ServerName;
import com.kongtrolink.framework.entity.TopicPrefix;
import com.kongtrolink.framework.register.config.WebPrivFuncConfig;
import com.kongtrolink.framework.register.entity.PrivFuncEntity;
import com.kongtrolink.framework.register.runner.RegisterRunner;
import com.kongtrolink.framework.register.service.ServiceRegistry;
import com.kongtrolink.framework.reports.dao.ReportConfigRecordDao;
import com.kongtrolink.framework.reports.dao.ReportTaskDao;
import com.kongtrolink.framework.reports.dao.ReportTaskResultDao;
import com.kongtrolink.framework.reports.dao.ReportWebConfigDao;
import com.kongtrolink.framework.reports.entity.ReportConfigRecord;
import com.kongtrolink.framework.reports.entity.ReportTask;
import com.kongtrolink.framework.reports.entity.ReportTaskResult;
import com.kongtrolink.framework.reports.entity.ReportWebConfig;
import com.kongtrolink.framework.reports.service.ReportsControllerService;
import org.apache.commons.io.FileUtils;
import org.apache.zookeeper.KeeperException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.Environment;
import org.springframework.core.env.EnvironmentCapable;
import org.springframework.core.env.StandardEnvironment;
import org.springframework.stereotype.Service;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

import java.io.*;
import java.util.*;

/**
 * Created by mystoxlol on 2019/10/31, 9:38.
 * company: kongtrolink
 * description:
 * update record:
 */
@Lazy
@Service
@Order(5)
public class ReportsControllerServiceImpl implements ReportsControllerService, EnvironmentCapable {

    Logger logger = LoggerFactory.getLogger(ReportsControllerServiceImpl.class);

    @Value("${server.version}")
    private String serverVersion;

    static final String DEFAULT_RESOURCE_PATTERN = "**/*.class";
    String resourcePattern = DEFAULT_RESOURCE_PATTERN;

    @Value("${register.scanBasePackage:com.kongtrolink.framework}")
    private String basePackage;


    private ReportTaskResultDao reportTaskResultDao;
    private ReportTaskDao reportTaskDao;
    private ReportWebConfigDao reportWebConfigDao;
    private ServiceRegistry serviceRegistry;

    private WebPrivFuncConfig webPrivFuncConfig;

    private ReportConfigRecordDao reportConfigRecordDao;

    @Lazy
    @Autowired(required = false)
    RegisterRunner registerRunner;

    @Autowired
    Environment environment;

    @Autowired
    public void setWebPrivFuncConfig(WebPrivFuncConfig webPrivFuncConfig) {
        this.webPrivFuncConfig = webPrivFuncConfig;
    }

//    @Autowired
//    public void setRegisterRunner(RegisterRunner registerRunner) {
//        this.registerRunner = registerRunner;
//    }

    @Override
    public Environment getEnvironment() {
        if (this.environment == null) {
            this.environment = new StandardEnvironment();
        }
        return this.environment;
    }

    @Autowired
    public void setReportWebConfigDao(ReportWebConfigDao reportWebConfigDao) {
        this.reportWebConfigDao = reportWebConfigDao;
    }

    @Autowired
    public void setReportConfigRecordDao(ReportConfigRecordDao reportConfigRecordDao) {
        this.reportConfigRecordDao = reportConfigRecordDao;
    }

    @Autowired
    public void setReportTaskResultDao(ReportTaskResultDao reportTaskResultDao) {
        this.reportTaskResultDao = reportTaskResultDao;
    }

    @Autowired
    public void setServiceRegistry(ServiceRegistry serviceRegistry) {
        this.serviceRegistry = serviceRegistry;
    }

    @Autowired
    public void setReportTaskDao(ReportTaskDao reportTaskDao) {
        this.reportTaskDao = reportTaskDao;
    }

    @Override
    public JSONObject getReportsTask(JSONObject body) {
//        String reportServerVersion = body.getString("reportServerVersion");
//        if (StringUtils.isBlank(reportServerVersion))
//        body.put("reportServerVersion", serverVersion);
        List<ReportTask> reportTasks = reportTaskDao.findReportTask(body);
        JSONObject result = new JSONObject();
        long reportTaskCount = reportTaskDao.findReportTaskCount(body);
        result.put("total", reportTaskCount);
        result.put("list", reportTasks);
        return result;
    }

    @Override
    public JSONObject getReportsTaskResult(JSONObject body) {
        List<ReportTaskResult> reportsTaskResults = reportTaskResultDao.findReportsTaskResult(body);
        long reportTaskCount = reportTaskResultDao.findReportTaskResultCount(body);
        JSONObject result = new JSONObject();
        result.put("total", reportTaskCount);
        result.put("list", reportsTaskResults);
        return result;
    }

    @Override
    public List<JSONObject> getReportsOperaCodeList() {
        try {
            String reportsName = ServerName.REPORTS_SERVER;
            List<String> children = serviceRegistry.getChildren(TopicPrefix.SUB_PREFIX);
            List<JSONObject> operaList = new ArrayList<>();
            for (String c : children) {
//                String serverVersion = serverMsg.getServerVersion();
//                String serverName = serverMsg.getServerName();
//                String serverCode = MqttUtils.preconditionServerCode(serverName, serverVersion);
                if (c.contains(reportsName)) {
                    String serverPath = TopicPrefix.SUB_PREFIX + "/" + c;
//                String data = serviceRegistry.getData(TopicPrefix.SUB_PREFIX + "/" + c);
                    List<String> operaChildren = serviceRegistry.getChildren(serverPath);
                    for (String operaPath : operaChildren) {
                        if (!operaPath.startsWith(OperaCodePrefix.REPORTS)) continue; //约定前缀不为报表的忽略
                        JSONObject operaMsg = new JSONObject();
                        operaMsg.put("operaCode", operaPath);
                        operaMsg.put("serverCode", c);
                        operaList.add(operaMsg);
                    }
                }
            }
            return operaList;
        } catch (KeeperException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }


    /*private List<String> getReportsCode() {
        List<String> reportsCodeList = new ArrayList<>();
        String basePackagePath = ClassUtils.convertClassNameToResourcePath(getEnvironment().resolveRequiredPlaceholders(basePackage));
        String packageSearchPath = ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX +
                basePackagePath + '/' + resourcePattern;
        try {
            ResourcePatternResolver resourcePatternResolver = new PathMatchingResourcePatternResolver();
            Resource[] resources = resourcePatternResolver.getResources(packageSearchPath);
            for (Resource resource : resources) {
                boolean readable = resource.isReadable();
                if (!readable) continue;
                MetadataReaderFactory metadataReaderFactory = new CachingMetadataReaderFactory();
                MetadataReader metadataReader = metadataReaderFactory.getMetadataReader(resource);
                AnnotationMetadata annotationMetadata = metadataReader.getAnnotationMetadata();
                boolean b = annotationMetadata.hasAnnotation(Service.class.getName());
                if (b) {
                    ClassMetadata classMetadata = metadataReader.getClassMetadata();
                    String className = classMetadata.getClassName();
                    Class<?> aClass = Class.forName(className);
                    Method[] methods = aClass.getMethods();
                    for (Method method : methods) {
                        ReportOperaCode annotation = method.getAnnotation(ReportOperaCode.class);
                        if (annotation == null) continue;
                        String code = annotation.value();
                        if (StringUtils.isEmpty(code)) {
                            code = method.getName();
                        }
                        reportsCodeList.add(code);
                    }
                }
            }
            logger.info("reports scanner result: [{}]", JSONObject.toJSONString(reportsCodeList));
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return reportsCodeList;
    }*/

    @Override
    public void saveConfigData(JSONObject data) {
        String serverCode = data.getString("serverCode");
        String enterpriseCode = data.getString("enterpriseCode");
        String configData = data.getString("configData");
        String funPrivCode = data.getString("funPrivCode");
        boolean exits = reportWebConfigDao.exits(serverCode, enterpriseCode);
        ReportWebConfig reportWebConfig = new ReportWebConfig(serverCode, enterpriseCode, configData, funPrivCode);
        if (exits) {
            ReportWebConfig reportWebConfigOld = reportWebConfigDao.find(serverCode, enterpriseCode, funPrivCode);
            reportWebConfig.setId(reportWebConfigOld.getId());
        }
        reportWebConfigDao.save(reportWebConfig);


//        JSONObject privData = data.getJSONObject("privData");
//        if (privData!=null) {
//            //保存更新配置文件
//            savePrivFuncConfig(privData);
//        }


    }


    public void savePrivFuncConfig(JSONObject privData) {
        PrivFuncEntity privFunc = webPrivFuncConfig.getPrivFunc();

        PrivFuncEntity privFuncEntity = JSON.toJavaObject(privData, PrivFuncEntity.class);

        List<PrivFuncEntity> children = privFunc.getChildren();
        for (int i = 0; i < children.size(); i++) {
            PrivFuncEntity c = children.get(i);
            String code = c.getCode();
            if (code.equals("REPORTS_PAGE")) //如果是自定义的告警页面，则保存和刷新告警功能
                children.add(i, privFuncEntity);
        }


        updateConfigFile(privFunc);
        //重新注册web
        try {
            registerRunner.registerWebPriv();
        } catch (KeeperException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
      /*  Iterator<PrivFuncEntity> iterator = children.iterator();
        while (iterator.hasNext()) {
            PrivFuncEntity next = iterator.next();
            String code = next.getCode();
            if (code.equals("REPORTS_PAGE"))
                next = privFuncEntity;
        }*/
    }

    private void updateConfigFile(PrivFuncEntity privFunc) {
        DumperOptions dumperOptions = new DumperOptions();
        dumperOptions.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
        dumperOptions.setDefaultScalarStyle(DumperOptions.ScalarStyle.PLAIN);
        dumperOptions.setPrettyFlow(false);
        Yaml yaml = new Yaml(dumperOptions);
        File file = FileUtils.getFile("file:config/privFuncConfig.yml");
        if (!file.exists()) {
            try {
                FileUtils.forceMkdirParent(file);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (file.exists()) {
            FileOutputStream out = null;
            OutputStreamWriter output = null;
            try {
//                Map load = (Map) yaml.load(new FileInputStream(file));
                out = FileUtils.openOutputStream(file);
                output = new OutputStreamWriter(out);
                yaml.dump(privFunc, output);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    out.close();
                    output.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    @Override
    public ReportWebConfig getConfigData(String serverCode, String enterpriseCode, String funPrivCode) {
        return reportWebConfigDao.find(serverCode, enterpriseCode, funPrivCode);
    }

    @Override
    public PrivFuncEntity getPrivData() {
        PrivFuncEntity privFunc = webPrivFuncConfig.getPrivFunc();
        List<PrivFuncEntity> children = privFunc.getChildren();
        for (PrivFuncEntity funcEntity : children) {
            String code = funcEntity.getCode();
            if (code.equals("REPORTS_PAGE")) //如果是自定义的告警页面，则保存和刷新告警功能
            {
                return funcEntity;
            }

        }
        return privFunc;
    }

    @Override
    public void recordConfigData(JSONObject data, User user) {
        String serverCode = data.getString("serverCode");
        String enterpriseCode = data.getString("enterpriseCode");
        String funPrivCode = data.getString("funPrivCode");

        Set<String> saveReportCodes = new HashSet<>(); // 保存的报表配置code集合，去重使用
        String configData = data.getString("configData");
        JSONArray configDataArray = JSONArray.parseArray(configData);

        configDataArray.forEach(config -> {
            JSONObject jsonObject = (JSONObject) config;
            String tabName = jsonObject.getString("name");
            JSONArray saveArray = jsonObject.getJSONArray("save");
            saveArray.forEach(s -> {
                JSONObject save = (JSONObject) s;
                String operaCode = save.getString("operaCode");
                String reportServerCode = save.getString("reportServerCode");

                if (!saveReportCodes.contains(operaCode)) { // 未保存过的在此保存
                    saveReportCodes.add(operaCode);
                    ReportTask reportTask = reportTaskDao.findByByUniqueCondition(serverCode, enterpriseCode, operaCode, reportServerCode);
                    if (reportTask !=null) {
                        String reportTaskId = reportTask.getId();
                        ReportConfigRecord reportConfigRecord = reportConfigRecordDao.findByReportTaskIdAndFuncPrivCode(reportTaskId, funPrivCode);
                        if (reportConfigRecord == null) reportConfigRecord = new ReportConfigRecord();
                        reportConfigRecord.setReportsTaskId(reportTaskId);
                        reportConfigRecord.setRecordTime(new Date());
                        reportConfigRecord.setConfigUsername(user.getName());
                        reportConfigRecordDao.save(reportConfigRecord);
                    }

                }
            });
        });


    }

    @Override
    public List<ReportConfigRecord> getRecordConfigData(String serverCode, String enterpriseCode) {

        return reportConfigRecordDao.findByServerCodeAndEnterpriseCode(serverCode,enterpriseCode);
    }

    @Override
    public List<ReportConfigRecord> getRecordConfigDataByPrivCode(String serverCode, String enterpriseCode, String funcPrivCode) {

        return reportConfigRecordDao.findByReportTaskIdAndFuncPrivCodeAndFuncPrivCode(serverCode, enterpriseCode, funcPrivCode);
    }

    @Override
    public ReportTask getReportsTaskByTaskId(String reportsTaskId) {
        return reportTaskDao.findById(reportsTaskId);
    }
}
