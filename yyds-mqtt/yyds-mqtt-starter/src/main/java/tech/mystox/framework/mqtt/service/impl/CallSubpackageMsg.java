package tech.mystox.framework.mqtt.service.impl;

import org.apache.commons.lang3.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import tech.mystox.framework.common.util.ByteUtil;
import tech.mystox.framework.entity.MsgPackage;

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
public class CallSubpackageMsg<T extends MsgPackage> implements Callable<T> {
    Logger logger = LoggerFactory.getLogger(CallSubpackageMsg.class);

    private final CountDownLatch latch = new CountDownLatch(1);

    private final ConcurrentHashMap<Integer, T> map = new ConcurrentHashMap<>();

    private MsgPackage result;

    public void callback(T msg) {
        this.result = msg;
        latch.countDown();
    }

    /**
     * 组包
     * 如果出现并发问题，则方法添加synchronized 关键字 性能妥协
     *
     * @param msg 分包消息
     */
    public void callbackSubPackage(T msg) throws InstantiationException, IllegalAccessException {
        int packageNum = msg.getPackageNum();
        int packageCount = msg.getPackageCount();
        map.put(packageNum, msg);
        if (map.size() == packageCount) {
            List<Byte> list = new ArrayList<>();
            for (int i = 0; i < packageCount; i++) {
                MsgPackage resp = map.get(i);
                list.addAll(Arrays.asList(ArrayUtils.toObject(resp.getBytePayload())));
            }
            Byte[] bytes1 = list.toArray(new Byte[0]);
            byte[] bytes3 = ArrayUtils.toPrimitive(bytes1);
            String payload = new String(bytes3, StandardCharsets.UTF_8);
            int crc = ByteUtil.getCRC(bytes3);
            int msgCrc = msg.getCrc();
            if (crc != msgCrc) {
                logger.error("[{}] {} StickPackage crc is wrong msgCrc: [{}] resultCrc: [{}]", msg.getClass().getTypeName(), this.result.getMsgId(), msgCrc, crc);
            }
            this.result = msg.getClass().newInstance();
            BeanUtils.copyProperties(msg, this.result);
            result.setPayload(payload);
            logger.info("[{}] [{}] StickPackage success..size{} count{}",
                    this.result.getMsgId(), msg.getClass().getSimpleName(), payload.length(), packageCount);
            latch.countDown();
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public T call() throws Exception {
        latch.await();
        return (T) this.result;
    }


}
