package tech.mystox.framework.mqtt.service.impl;

import tech.mystox.framework.common.util.ByteUtil;
import tech.mystox.framework.entity.MqttResp;
import org.apache.commons.lang3.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;

/**
 * Created by mystoxlol on 2019/9/5, 8:49.
 * company: mystox
 * description: 回调加锁函数，从ack topic 回复中获取消息结果
 * update record:
 */
public class CallBackTopic implements Callable<MqttResp> {
    Logger logger = LoggerFactory.getLogger(CallBackTopic.class);

    private CountDownLatch latch = new CountDownLatch(1);

    private ConcurrentHashMap<Integer, MqttResp> map = new ConcurrentHashMap<>();

    private MqttResp result;

    public void callback(MqttResp result) {
        this.result = result;
        latch.countDown();
    }

    /**
     * 组包
     * 如果出现并发问题，则方法添加synchronized 关键字 性能妥协
     *
     * @param result
     */
    public void callbackSubPackage(MqttResp result) {
        int packageNum = result.getPackageNum();
        int packageCount = result.getPackageCount();
        map.put(packageNum, result);
        if (map.size() == packageCount) {
            List<Byte> list = new ArrayList<>();
            for (int i = 0; i < packageCount; i++) {
                MqttResp resp = map.get(i);
                list.addAll(Arrays.asList(ArrayUtils.toObject(resp.getBytePayload())));
            }
            Byte[] bytes1 = list.toArray(new Byte[list.size()]);

            byte[] bytes3 = ArrayUtils.toPrimitive(bytes1);

            String payload = null;
            try {
                payload = new String(bytes3, "utf-8");
            } catch (UnsupportedEncodingException e) {
                logger.error("callback sub package error[{}]",e.toString());
                if (logger.isDebugEnabled())
                e.printStackTrace();
            }
            int crc = ByteUtil.getCRC(bytes3);
            int msgCrc = result.getCrc();
            if (crc != msgCrc) {
                logger.error("[{}] stickPackage crc is wrong msgCrc: [{}] resultCrc: [{}]", this.result.getMsgId(), msgCrc, crc);
            }
            this.result = new MqttResp(result.getMsgId(), payload);
            logger.info("[{}] stickPackage success..{}, {}, {}", this.result.getMsgId(), packageCount, packageNum, crc);
            latch.countDown();
        }
    }

    @Override
    public MqttResp call() throws Exception {
        latch.await();
        return result;
    }

}
