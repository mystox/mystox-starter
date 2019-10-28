package com.kongtrolink.framework.gateway.service.parse;

import com.alibaba.fastjson.JSONObject;
import com.kongtrolink.framework.core.entity.RedisHashTable;
import com.kongtrolink.framework.core.utils.RedisUtils;
import com.kongtrolink.framework.gateway.entity.ParseProtocol;
import com.kongtrolink.framework.gateway.entity.Transverter;
import com.kongtrolink.framework.gateway.entity.TransverterConfig;
import com.kongtrolink.framework.gateway.service.transverter.TransverterService;
import com.kongtrolink.framework.mqtt.util.SpringContextUtil;
import org.apache.commons.lang3.StringUtils;
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
import org.springframework.util.ClassUtils;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * Created by mystoxlol on 2019/10/16, 9:11.
 * company: kongtrolink
 * description:
 * update record:
 */
public abstract class ParseHandler implements ParseService, EnvironmentCapable {
    Logger logger = LoggerFactory.getLogger(ParseHandler.class);

    @Autowired
    RedisUtils redisUtils;
    @Value("${redis.communication.expired:1200}")
    private long communicationExpired;

    @Value("${gateway.transverter:com.kongtrolink.framework}")
    private String transverterPackage;

    @Value("${config.resources:./configResources}")
    private String resourcesPath;
    static final String DEFAULT_RESOURCE_PATTERN = "**/*.class";
    String resourcePattern = DEFAULT_RESOURCE_PATTERN;


    private Map<String, TransverterService> transverterMap = new HashMap<>();
    private Map<String, String> msgTypeMap = new HashMap<>();

    @Autowired
    TransverterConfig transverterConfig;

    @Autowired
    Environment environment;

    @Override
    public Environment getEnvironment() {
        if (this.environment == null) {
            this.environment = new StandardEnvironment();
        }
        return this.environment;
    }

    @Override
    public void execute(String payload) {
        ParseProtocol parseProtocol = parsePayload(payload);
        String sn = parseProtocol.getSn();
        String uuid = parseProtocol.getUuid();
        communicationRefresh(sn, uuid);
        TransverterService transverterService = transverterSelect(parseProtocol);
        //根据消息类型获取协议转换器执行
        if (transverterService == null)
            logger.error("payload msgType[{}] can not find transverter service...", parseProtocol.getMsgType());
        else
            transverterService.transfer(parseProtocol);
    }

    /**
     * 对报文进行解析
     *
     * @param payload
     * @return
     */
    protected abstract ParseProtocol parsePayload(String payload);


    public void init() {
        configTransverterInit();
        scannerTransverterInit();
    }


    public void configTransverterInit() {

        Map<String, String> map = new HashMap<>();
        Map<String, List<String>> operaMap = transverterConfig.getTransverter();
        if (!CollectionUtils.isEmpty(operaMap)) {
            Set<Map.Entry<String, List<String>>> entries = operaMap.entrySet();
            for (Map.Entry<String, List<String>> e : entries) {
                List<String> msgTypes = e.getValue();
                String key = e.getKey();
                msgTypes.forEach((v) -> map.put(v, key));
            }
        }
        logger.info("transverter config scanner result: [{}]", JSONObject.toJSONString(map));
        setMsgTypeMap(map);
    }

    /**
     * 根据配置文件选择报文处理器
     *
     * @param parseProtocol
     */
    protected TransverterService transverterSelect(ParseProtocol parseProtocol) {
        String packetName = parseProtocol.getMsgType();
        String transverterHandler = getMsgTypeMap().get(packetName);
        return getTransverterMap().get(transverterHandler);

    }

    /**
     * 扫描协议转换器
     */
    void scannerTransverterInit() {
        String basePackagePath = ClassUtils
                .convertClassNameToResourcePath(getEnvironment().resolveRequiredPlaceholders(transverterPackage));
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
                boolean b = annotationMetadata.hasAnnotation(Transverter.class.getName());
                if (b) {
                    ClassMetadata classMetadata = metadataReader.getClassMetadata();
                    Map<String, Object> annotationAttributes = annotationMetadata.getAnnotationAttributes(Transverter.class.getName());
                    String value = (String) annotationAttributes.get("value");
                    String className = classMetadata.getClassName();
                    Class<?> aClass = Class.forName(className);
                    TransverterService transverterService;
                    if (StringUtils.isNotBlank(value)) {
                        transverterService = SpringContextUtil.getBean(value, TransverterService.class);
                    } else {
                        String simpleName = aClass.getSimpleName();
                        String substring = simpleName.substring(0, 1);
                        value = simpleName.replaceFirst(substring, substring.toLowerCase());
                        transverterService = SpringContextUtil.getBean(
                                value, TransverterService.class);
                    }
                    if (transverterService != null)
                        transverterMap.put(value, transverterService);
                }
            }
            logger.info("transverterService scanner result: [{}]", JSONObject.toJSONString(transverterMap.keySet()));
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }


    }
   /* void configTransverterInit() {

        DumperOptions dumperOptions = new DumperOptions();
        dumperOptions.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
        dumperOptions.setDefaultScalarStyle(DumperOptions.ScalarStyle.PLAIN);
        dumperOptions.setPrettyFlow(false);
        Yaml yaml = new Yaml(dumperOptions);
        File file = FileUtils.getFile(resourcesPath + "/gateway-transverterRelation.yml");
        System.out.println(JSONObject.toJSONString(transverterConfig));
        Map<String, String> map = new HashMap<>();
        if (file.exists()) {
            try {
                Map load = (Map) yaml.load(new FileInputStream(file));
                Map<String, List<String>> operaMap = (Map<String, List<String>>) load.get("transverter");
                if (!CollectionUtils.isEmpty(operaMap)) {
                    Set<Map.Entry<String, List<String>>> entries = operaMap.entrySet();
                    for (Map.Entry<String, List<String>> e : entries) {
                        List<String> msgTypes = e.getValue();
                        String key = e.getKey();
                        msgTypes.forEach((v) -> map.put(v, key));
                    }
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
        logger.info("transverter config scanner result: [{}]", JSONObject.toJSONString(map));
    }*/


    /**
     * 通讯信息刷新
     *
     * @param sn
     * @param uuid
     */
    private void communicationRefresh(String sn, String uuid) {
        //根据SN从communication_hash获取记录
        //不存在该 SN 记录
        String key = RedisHashTable.COMMUNICATION_HASH + ":" + sn;
        JSONObject value = redisUtils.get(key, JSONObject.class);
        Long expiredTime = 0L;
        if (value == null) {//添加通讯信息
            value = new JSONObject();
            value.put("UUID", uuid);
            value.put("STATUS", 0);//设置未注册状态
            //触发重新注册
            redisUtils.set(key, value);
            //将路由信息存入redis 数据格式为:{SN:{uuid:uuid,GWip:gip,STATUS:0}}
        } else {
            String uuid2 = (String) value.get("UUID");//uuid发生变化 表示终端重连 则更新通讯路由信息
            if (StringUtils.isNotBlank(uuid) && !uuid.equals(uuid2)) {
                value.put("UUID", uuid);
                redisUtils.set(key, value);
            }
            Object expired = value.get("expired");
            if (expired != null) expiredTime = ((Integer) expired).longValue();
        }

        // 设置过期时间
        if (expiredTime == 0)
            expiredTime = communicationExpired;
        redisUtils.expired(key, expiredTime, TimeUnit.SECONDS);
    }


    public Map<String, TransverterService> getTransverterMap() {
        return transverterMap;
    }

    public void setTransverterMap(Map<String, TransverterService> transverterMap) {
        this.transverterMap = transverterMap;
    }

    public Map<String, String> getMsgTypeMap() {
        return msgTypeMap;
    }

    public void setMsgTypeMap(Map<String, String> msgTypeMap) {
        this.msgTypeMap = msgTypeMap;
    }
}
