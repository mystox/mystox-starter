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
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by mystoxlol on 2019/9/5, 8:49.
 * company: mystox
 * description: 回调加锁函数，从ack topic 回复中获取消息结果
 * update record:
 */
public class CallSubpackageMsg<T extends MsgPackage> {

    Logger logger = LoggerFactory.getLogger(CallSubpackageMsg.class);

    private final CompletableFuture<T> future = new CompletableFuture<>();


    private final ConcurrentHashMap<Integer, T> parts = new ConcurrentHashMap<>();

    private final AtomicBoolean completed = new AtomicBoolean(false);
    //private volatile boolean completed = false;

    private MsgPackage result;

    //public CallSubpackageMsg(int expectedCount) {
    //    this.expectedCount = expectedCount;
    //}

    public void callback(T msg) {
        if (completed.compareAndSet(false, true)) {
            future.complete(msg);
        }

    }

    /**
     * 组包
     * 如果出现并发问题，则方法添加synchronized 关键字 性能妥协
     *
     * @param msg 分包消息
     */
    public void callbackSubPackage(T msg) {
        if (completed.get()) {
            return;
        }
        int packageNum = msg.getPackageNum();
        int packageCount = msg.getPackageCount();
        parts.put(packageNum, msg);
        if (parts.size() == packageCount && completed.compareAndSet(false, true)) {
            T merged = merge(msg);
            future.complete(merged);
        }
    }

    private T merge(T template) {
        Integer packageCount = template.getPackageCount();
        try {

            List<Byte> list = new ArrayList<>();

            for (int i = 0; i < packageCount; i++) {
                T part = parts.get(i);
                list.addAll(Arrays.asList(ArrayUtils.toObject(part.getBytePayload())));
            }

            Byte[] bytesObj = list.toArray(new Byte[0]);
            byte[] bytes = ArrayUtils.toPrimitive(bytesObj);

            String payload = new String(bytes, StandardCharsets.UTF_8);

            int crc = ByteUtil.getCRC(bytes);

            if (crc != template.getCrc()) {
                logger.error("[{}] {} StickPackage crc is wrong msgCrc: [{}] resultCrc: [{}]", template.getClass().getTypeName(), this.result.getMsgId(), template.getCrc(), crc);
                throw new RuntimeException("CRC mismatch");
            }

            T result = (T) template.getClass()
                    .getDeclaredConstructor()
                    .newInstance();

            BeanUtils.copyProperties(template, result);
            result.setPayload(payload);

            return result;

        } catch (Exception e) {
            future.completeExceptionally(e);
            throw new RuntimeException(e);
        }
    }
//@Override
//@SuppressWarnings("unchecked")
//public T call() throws Exception {
//    latch.await();
//    return (T) this.result;
//}


    public CompletableFuture<T> getFuture() {
        return future;
    }

    public boolean isDone() {
        return future.isDone();
    }

    public T get(long timeout, TimeUnit unit)
            throws Exception {
        return future.get(timeout, unit);
    }

    public boolean isComplete() {
        return completed.get();
    }

}
