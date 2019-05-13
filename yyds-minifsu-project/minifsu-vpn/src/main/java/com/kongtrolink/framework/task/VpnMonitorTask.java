package com.kongtrolink.framework.task;

import com.alibaba.fastjson.JSONObject;
import com.kongtrolink.framework.core.utils.RedisUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.*;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Component
@EnableScheduling
public class VpnMonitorTask {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Value("${vpn.osVersion}")
    private String osVersion;

    @Value("${vpn.agent}")
    private String agent;

    @Value("#{'${vpn.vpnList}'.split(';')}")
    private List<String> vpnList;

    @Autowired
    private RedisUtils redisUtils;

    @Scheduled(fixedRate=60000)
    private void task() {
        if (osVersion.equals("linux") && agent.equals("tower")) {
            linuxTower();
        }
    }

    private void linuxTower() {
//        logger.info("start");
        String folder = "/var/run/";
        for (String vpn : vpnList) {
            String[] vpnInfo = vpn.split(",");
//            logger.info("check " + vpn);
            try {
                String filePath = folder + "ppp-" + vpnInfo[0] + ".pid";
                File file = new File(filePath);
                if (!file.exists()) {
//                    logger.info(filePath + " not exist");
                    exec(new String[] {"/bin/sh", "-c", "echo 'c " + vpnInfo[0] + "' > /var/run/xl2tpd/l2tp-control"});
                }
                if (file.exists()) {
                    String content = readFile(filePath);
//                    logger.info(filePath + ":" + content);
                    String interfaceName = content.split("\n")[1];
//                    logger.info("interfaceName:" + interfaceName);
                    String[] ipList = exec(new String[] {"/bin/bash", "-c", "ifconfig " + interfaceName + " | egrep \"((25[0-5]|2[0-4][0-9]|((1[0-9]{2})|([1-9]?[0-9])))\\.){3}(25[0-5]|2[0-4][0-9]|((1[0-9]{2})|([1-9]?[0-9])))\" -o"}).split("\n");
//                    logger.info("ipList:" + JSONObject.toJSONString(ipList));
                    if (ipList.length == 3) {
                        if (redisUtils.hHasKey("vpn_hash", vpnInfo[0])) {
                            if (redisUtils.hget("vpn_hash", vpnInfo[0]).toString().equals(ipList[0])) {
                                return;
                            }
                        }
                        exec(new String[] { "/bin/sh", "-c", "route add -net " + vpnInfo[1] + " netmask " + vpnInfo[2] + " dev " + interfaceName });
                        redisUtils.hset("vpn_hash", vpnInfo[0], ipList[0]);
                        logger.info(vpnInfo[0] + " IP发生变化:" + ipList[0]);
                    }
                }
            } catch (Exception ex) {
                logger.error("检测VPN状态异常：" + vpn + "\n" + ex.getMessage());
            }
        }
    }

    /**
     * 执行命令
     * @param cmd cmd
     * @return 命令返回结果，执行失败返回null
     */
    private String exec(String[] cmd) {
        String result = "";
        try {
//            logger.info("cmd:" + cmd[2]);
            Process p = Runtime.getRuntime().exec(cmd);
            p.waitFor(10, TimeUnit.SECONDS);
            int processResult = p.exitValue();
            if (processResult != 0) {
                throw new Exception("p.exitValue():" + processResult);
            }
            try (InputStreamReader isr = new InputStreamReader(p.getInputStream());
                 BufferedReader br = new BufferedReader(isr)) {
                String line;
                while((line = br.readLine()) != null) {
                    result += line + "\n";
                }
            }
//            logger.info("result:\n" + result);
        } catch (Exception ex) {
            result = null;
            logger.error("执行命令失败：" + cmd + "\n" + ex.getMessage());
        }
        return result;
    }

    /**
     * 读取文件信息
     * @param filePath 文件路径
     * @return 文件内容，读取失败为null
     */
    private String readFile(String filePath) {
        String result = "";
        try (FileReader reader = new FileReader(filePath);
             BufferedReader br = new BufferedReader(reader)) {
            String line;
            //网友推荐更加简洁的写法
            while ((line = br.readLine()) != null) {
                result += line + "\n";
            }
        } catch (Exception ex) {
            result = null;
            logger.error("读取文件失败：" + filePath + "\n" + ex.getMessage());
        }
        return result;
    }
}
