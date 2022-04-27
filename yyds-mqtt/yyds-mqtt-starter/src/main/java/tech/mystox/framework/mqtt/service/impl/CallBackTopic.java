package tech.mystox.framework.mqtt.service.impl;

import tech.mystox.framework.common.util.ByteUtil;
import tech.mystox.framework.entity.MsgRsp;
import org.apache.commons.lang3.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;
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
public class CallBackTopic implements Callable<MsgRsp> {
    Logger logger = LoggerFactory.getLogger(CallBackTopic.class);

    private final CountDownLatch latch = new CountDownLatch(1);

    private ConcurrentHashMap<Integer, MsgRsp> map = new ConcurrentHashMap<>();

    private MsgRsp result;

    public void callback(MsgRsp result) {
        this.result = result;
        latch.countDown();
    }

    /**
     * 组包
     * 如果出现并发问题，则方法添加synchronized 关键字 性能妥协
     *
     * @param result
     */
    public void callbackSubPackage(MsgRsp result) {
        int packageNum = result.getPackageNum();
        int packageCount = result.getPackageCount();
        map.put(packageNum, result);
        if (map.size() == packageCount) {
            List<Byte> list = new ArrayList<>();
            for (int i = 0; i < packageCount; i++) {
                MsgRsp resp = map.get(i);
                list.addAll(Arrays.asList(ArrayUtils.toObject(resp.getBytePayload())));
            }
            Byte[] bytes1 = list.toArray(new Byte[0]);

            byte[] bytes3 = ArrayUtils.toPrimitive(bytes1);

            String payload = null;
            payload = new String(bytes3, StandardCharsets.UTF_8);
            int crc = ByteUtil.getCRC(bytes3);
            int msgCrc = result.getCrc();
            if (crc != msgCrc) {
                logger.error("[{}] stickPackage crc is wrong msgCrc: [{}] resultCrc: [{}]", this.result.getMsgId(), msgCrc, crc);
            }
            this.result = new MsgRsp(result.getMsgId(), payload);
            logger.info("[{}] stickPackage success..{}, {}, {}", this.result.getMsgId(), packageCount, packageNum, crc);
            latch.countDown();
        }
    }

    @Override
    public MsgRsp call() throws Exception {
        latch.await();
        return result;
    }

}
