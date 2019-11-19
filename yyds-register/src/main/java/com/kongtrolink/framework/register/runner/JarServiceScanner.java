package com.kongtrolink.framework.register.runner;

import com.alibaba.fastjson.JSONObject;
import com.kongtrolink.framework.entity.AckEnum;
import com.kongtrolink.framework.entity.RegisterSub;
import com.kongtrolink.framework.entity.UnitHead;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by mystoxlol on 2019/8/28, 18:23.
 * company: kongtrolink
 * description:
 * update record:
 */
@Service
public class JarServiceScanner implements ServiceScanner {
    private Logger logger = LoggerFactory.getLogger(JarServiceScanner.class);

    //    @Value("${server.name}")
//    private String serverName;
//
//    @Value("${server.version}")
//    private String serverVersion;
    @Value("${jarResources.path:./jarResources}")
    private String jarResPath;


    @Value("${server.name}_${server.version}")
    private String serverCode;

    @Override
    public List<RegisterSub> getSubList() {
        DumperOptions dumperOptions = new DumperOptions();
        dumperOptions.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
        dumperOptions.setDefaultScalarStyle(DumperOptions.ScalarStyle.PLAIN);
        dumperOptions.setPrettyFlow(false);
        Yaml yaml = new Yaml(dumperOptions);
        File file = FileUtils.getFile(jarResPath+"/jarRes.yml");
        List<RegisterSub> subList = new ArrayList<>();
        if (file.exists()) {
            try {
                Map load = (Map) yaml.load(new FileInputStream(file));
                Map<String, String> operaMap = (Map<String, String>) load.get(serverCode);
                if (!CollectionUtils.isEmpty(operaMap)) {
                    Set<Map.Entry<String, String>> entries = operaMap.entrySet();
                    for (Map.Entry<String, String> e : entries) {
                        RegisterSub sub = new RegisterSub();
                        String value = e.getValue();
                        String key = e.getKey();
                        String[] split = value.split(":");
                        String executeUnit = UnitHead.JAR + split[0];
                        sub.setOperaCode(key);
                        sub.setAck(split.length > 1 && AckEnum.ACK.toString().equals(split[1]) ?
                                AckEnum.ACK : AckEnum.NA);
                        sub.setExecuteUnit(executeUnit);
                        subList.add(sub);
                    }
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
        logger.info("jar scanner result: [{}]", JSONObject.toJSONString(subList));
        return subList;
    }

    @Override
    public boolean addSub(RegisterSub registerSub) {
        DumperOptions dumperOptions = new DumperOptions();
        dumperOptions.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
        dumperOptions.setDefaultScalarStyle(DumperOptions.ScalarStyle.PLAIN);
        dumperOptions.setPrettyFlow(false);
        Yaml yaml = new Yaml(dumperOptions);
        File file = FileUtils.getFile(jarResPath+"/jarRes.yml");
        if (file.exists()) {
            FileOutputStream out = null;
            OutputStreamWriter output = null;
            try {
                Map load = (Map) yaml.load(new FileInputStream(file));
                Map<String, String> operaMap = (Map<String, String>) load.get(serverCode);
                String operaCode = registerSub.getOperaCode();
                AckEnum ack = registerSub.getAck();
                String executeUnit = registerSub.getExecuteUnit();
                operaMap.put(operaCode, executeUnit.replace(UnitHead.JAR,"") + ":" + ack);
                out = FileUtils.openOutputStream(file);
                output = new OutputStreamWriter(out);
                yaml.dump(load, output);
                return true;
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }finally {
                try {
                    out.close();
                output.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return false;
    }

    @Override
    public boolean deleteSub(String operaCode) {
        DumperOptions dumperOptions = new DumperOptions();
        dumperOptions.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
        dumperOptions.setDefaultScalarStyle(DumperOptions.ScalarStyle.PLAIN);
        dumperOptions.setPrettyFlow(false);
        Yaml yaml = new Yaml(dumperOptions);
        File file = FileUtils.getFile(jarResPath+"/jarRes.yml");
        if (file.exists()) {
            FileOutputStream out = null;
            OutputStreamWriter output = null;
            try {
                Map load = (Map) yaml.load(new FileInputStream(file));
                Map<String, String> operaMap = (Map<String, String>) load.get(serverCode);
                operaMap.remove(operaCode);
                out = FileUtils.openOutputStream(file);
                output = new OutputStreamWriter(out);
                yaml.dump(load, output);
                return true;
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }finally {
                try {
                    out.close();
                    output.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return false;
    }

}
