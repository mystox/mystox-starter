package com.kongtrolink.framework.reports.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.kongtrolink.framework.entity.ServerName;
import com.kongtrolink.framework.entity.TopicPrefix;
import com.kongtrolink.framework.register.service.ServiceRegistry;
import com.kongtrolink.framework.reports.dao.ReportTaskDao;
import com.kongtrolink.framework.reports.dao.ReportTaskResultDao;
import com.kongtrolink.framework.reports.dao.ReportWebConfigDao;
import com.kongtrolink.framework.reports.entity.ReportTask;
import com.kongtrolink.framework.reports.entity.ReportTaskResult;
import com.kongtrolink.framework.reports.entity.ReportWebConfig;
import com.kongtrolink.framework.reports.service.ReportsControllerService;
import com.kongtrolink.framework.reports.stereotype.ReportOperaCode;
import org.apache.zookeeper.KeeperException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.core.env.EnvironmentCapable;
import org.springframework.core.env.StandardEnvironment;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.ClassMetadata;
import org.springframework.core.type.classreading.CachingMetadataReaderFactory;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by mystoxlol on 2019/10/31, 9:38.
 * company: kongtrolink
 * description:
 * update record:
 */
@Service
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

    @Autowired
    Environment environment;

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


    private List<String> getReportsCode() {
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
    }

    @Override
    public void saveConfigData(JSONObject data) {
        String serverCode = data.getString("serverCode");
        String enterpriseCode = data.getString("enterpriseCode");
        String configData = data.getString("configData");
        boolean exits = reportWebConfigDao.exits(serverCode, enterpriseCode);
        ReportWebConfig reportWebConfig = new ReportWebConfig(serverCode, enterpriseCode, configData);
        if (exits) {
            ReportWebConfig reportWebConfigOld = reportWebConfigDao.find(serverCode, enterpriseCode);
            reportWebConfig.setId(reportWebConfigOld.getId());
        }
        reportWebConfigDao.save(reportWebConfig);
    }

    @Override
    public ReportWebConfig getConfigData(String serverCode, String enterpriseCode) {
        return reportWebConfigDao.find(serverCode, enterpriseCode);
    }
}
