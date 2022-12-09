package tech.mystox.framework.register.utils;


import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import tech.mystox.framework.common.util.CollectionUtils;
import tech.mystox.framework.common.util.StringUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

/**
 * Created by mystox on 2022/8/9, 15:51.
 * company:
 * description: 基于zookeeper的分布式锁（服务专用）
 * update record:
 */

public class DistributedLock implements Lock, Watcher, AsyncCallback.StatCallback, AsyncCallback.DataCallback {

    Logger logger = LoggerFactory.getLogger(DistributedLock.class);
    private ZooKeeper zk = null;
    // 根节点
    private String ROOT_LOCK = "/mqtt/lock";
    // 竞争的资源
    private String lockName;
    // 等待的前一个锁
    private String WAIT_LOCK;
    // 当前锁
    private String CURRENT_LOCK;
    // 计数器
    private CountDownLatch countDownLatch;
    private AtomicLong tryLockCount;
    private int sessionTimeout = 300000_000;
    private CountDownLatch latch = new CountDownLatch(1);

    public DistributedLock(String registerUrl, int regSessionTimeout, String serverName) throws IOException, InterruptedException, KeeperException {
        this.lockName = serverName;
        this.zk = new ZooKeeper(registerUrl, regSessionTimeout, this);
        latch.await();
        Stat stat = zk.exists(ROOT_LOCK, false);
        if (stat == null) {
            // 如果根节点不存在，则创建根节点
            zk.create(ROOT_LOCK, new byte[0], ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
        }
    }

    public void setZk(ZooKeeper zk) {
        this.zk = zk;
    }

    /**
     * 配置分布式锁
     * <p>
     * //     * @param config   连接的url
     *
     * @param lockName 竞争资源
     */
    @Deprecated //外部zk源监听会失效
    public DistributedLock(ZooKeeper zk, String lockName) {
        this.lockName = lockName;
        try {
            this.zk = zk;
            Stat stat = zk.exists(ROOT_LOCK, false);
            if (stat == null) {
                // 如果根节点不存在，则创建根节点
                zk.create(ROOT_LOCK, new byte[0], ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
            }
        } catch (InterruptedException | KeeperException e) {
            e.printStackTrace();
        }
    }

    // 节点监视器
    @Autowired
    public void process(WatchedEvent event) {
        Watcher.Event.KeeperState state = event.getState();
        if (state == Watcher.Event.KeeperState.SyncConnected && latch.getCount() != 0) {
            logger.info("Distributed lock zk connect successful...");
            latch.countDown();

        }
    }

    void processEventDeal(WatchedEvent event) {
        logger.debug("[{}]LOCK Watch node event[{}]", Thread.currentThread().getId(), event.toString());
        Watcher.Event.EventType type = event.getType();
        String path = event.getPath();
        if (type == Watcher.Event.EventType.NodeDeleted) {
            if (StringUtils.equals(CURRENT_LOCK, path)) {
                logger.debug("Watch current lock node delete[{}]", path);
            }
            if (StringUtils.equals(WAIT_LOCK, path)) {
                logger.debug("Watch wait lock node been deleted[{}]", path);
                //                System.out.println("等待锁被删除");
                countDownLatch.countDown();
            }
        }
    }

    public void lock() {
        tryLockCount = new AtomicLong(0);
        try {
            if (this.tryLock()) {
                logger.debug("Get lock successful [{}]", CURRENT_LOCK);
            } else {
                logger.warn("Distributed lock result is false");
                Thread.currentThread().interrupt();
            }
        } catch (LockException e) {
            logger.error("Distributed lock error!!", e);
            Thread.currentThread().interrupt();
        }

    }

    @Override
    public boolean tryLock() throws LockException {
        try {
            if (tryLockCount.incrementAndGet() > 10) {
                throw new LockException("Try lock frequency error!!! count > " + tryLockCount.get());
            }
            String splitStr = "_lock_";
            if (lockName.contains(splitStr)) {
                throw new LockException("lockName error" + lockName);
            }
            if (StringUtils.isBlank(CURRENT_LOCK)) {
                // 创建临时有序节点并监控
                CURRENT_LOCK = zk.create(ROOT_LOCK + "/" + lockName + splitStr, new byte[0],
                        ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL);
                logger.debug("Current lock path created [{}]", CURRENT_LOCK);
            } else {
                Stat exists = zk.exists(CURRENT_LOCK, false);
                //还原节点
                if (exists == null) {
                    CURRENT_LOCK = zk.create(ROOT_LOCK + "/" + lockName + splitStr, new byte[0],
                            ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL);
                    logger.debug("Current lock path recreated [{}]", CURRENT_LOCK);
                }
            }
            // 取所有子节点
            List<String> subNodes = zk.getChildren(ROOT_LOCK, false);
            if (CollectionUtils.isEmpty(subNodes))
                throw new LockException("ROOT_LOCK " + ROOT_LOCK + " child nodes is empty");
            // 取出所有当前服务相关的lockName的锁
            List<String> lockObjects = new ArrayList<>();
            for (String node : subNodes) {
                String _node = node.split(splitStr)[0];
                if (_node.equals(lockName)) {
                    lockObjects.add(node);
                }
            }
            Collections.sort(lockObjects);
            logger.debug("Lock objects [{}]", lockObjects);
            // 目前上锁的节点名
            String currentNode = CURRENT_LOCK.substring(CURRENT_LOCK.lastIndexOf("/") + 1);
            int i = Collections.binarySearch(lockObjects, currentNode);
            if (i == -1) { //容错
                logger.debug("Lock objects can't find currentNode [{}] in lock objects {}", currentNode, lockObjects);
                //锁未找到当前服务锁，可能是重新获取锁的过程中
                return tryLock();
            }
            // 若当前节点为最小节点，则获取锁成功
            String minNode = ROOT_LOCK + "/" + lockObjects.get(0);
            if (CURRENT_LOCK.equals(minNode)) {
                logger.debug("CURRENT_LOCK[{}], minNode[{}]", CURRENT_LOCK, minNode);
                return true;
            }
            // 若不是最小节点，则找到自己的前一个节点
            WAIT_LOCK = ROOT_LOCK + "/" + lockObjects.get(i - 1);
            return waitForLock();
        } catch (InterruptedException | KeeperException e) {
            logger.error("Try lock exception happens", e);
        }
        return false;
    }

    @Override
    public boolean tryLock(long timeout, TimeUnit unit) {
        try {
            if (this.tryLock()) {
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    // 等待锁
    private boolean waitForLock() throws KeeperException, InterruptedException {
        //监控等待锁状态
        zk.exists(WAIT_LOCK, this::processEventDeal);
        logger.debug("=============[{}] Wait lock [{}]", Thread.currentThread().getId(), WAIT_LOCK);
        this.countDownLatch = new CountDownLatch(1);
        // 计数等待，若等到前一个节点消失，则precess中进行countDown，停止等待，获取锁
        this.countDownLatch.await();
        logger.debug("[{}] Wait event await [{}]", Thread.currentThread().getId(), CURRENT_LOCK);
        return tryLock();
    }

    // 等待锁
    private boolean waitForLock(long timeout, TimeUnit unit) throws KeeperException, InterruptedException {
        Stat stat = zk.exists(WAIT_LOCK, this::processEventDeal);
        if (stat != null) {
            logger.debug("[{}] Wait lock [{}]", Thread.currentThread().getId(), WAIT_LOCK);
            this.countDownLatch = new CountDownLatch(1);
            // 计数等待，若等到前一个节点消失，则precess中进行countDown，停止等待，获取锁
            boolean await = this.countDownLatch.await(timeout, unit);
            logger.debug("[{}] Get lock [{}] result [{}]", Thread.currentThread().getName(), CURRENT_LOCK, await);
        }
        return true;
    }

    public void unlock() {
        try {
            logger.debug("[{}] UnLock lock [{}]", Thread.currentThread().getName(), CURRENT_LOCK);
            if (CURRENT_LOCK != null && zk.exists(CURRENT_LOCK, false) != null) {
                zk.delete(CURRENT_LOCK, -1);
            }
            if (CURRENT_LOCK != null) CURRENT_LOCK = null;
            if (countDownLatch != null && countDownLatch.getCount() != 0) {
                countDownLatch.countDown();
            }
            //            zk.close();
        } catch (InterruptedException | KeeperException e) {
            e.printStackTrace();
        }
        //        super.unlock();
    }

    public Condition newCondition() {
        return new Condition() {
            @Override
            public void await() throws InterruptedException {

            }

            @Override
            public void awaitUninterruptibly() {

            }

            @Override
            public long awaitNanos(long nanosTimeout) throws InterruptedException {
                return 0;
            }

            @Override
            public boolean await(long time, TimeUnit unit) throws InterruptedException {
                return false;
            }

            @Override
            public boolean awaitUntil(Date deadline) throws InterruptedException {
                return false;
            }

            @Override
            public void signal() {

            }

            @Override
            public void signalAll() {

            }
        };
    }

    public void lockInterruptibly() throws InterruptedException {
        this.lock();
    }

    @Override
    public void processResult(int i, String path, Object o, Stat stat) {
        logger.info(i + "---" + path + "---" + o + "---" + stat);
        logger.debug("[{}] LOCK Watch node event[{}]", Thread.currentThread().getId(), stat.toString());
        if ("wait".equals(o)) {
            if (StringUtils.equals(CURRENT_LOCK, path)) {
                logger.debug("Watch current lock node delete[{}]", path);
            }
            if (StringUtils.equals(WAIT_LOCK, path)) {
                logger.debug("Watch wait lock node been deleted[{}]", path);
                //                System.out.println("等待锁被删除");
                countDownLatch.countDown();
            }
        }
    }

    @Override
    public void processResult(int rc, String path, Object ctx, byte[] data, Stat stat) {
        logger.debug("[{}]LOCK Watch node event[{}]", Thread.currentThread().getName(), stat.toString());
        //        if (StringUtils.equals(CURRENT_LOCK, path)) {
        //            logger.debug("Watch current lock node delete[{}]", path);
        //        }
        //        if (StringUtils.equals(WAIT_LOCK, path)) {
        //            logger.debug("Watch wait lock node been deleted[{}]", path);
        //            //                System.out.println("等待锁被删除");
        //            countDownLatch.countDown();
        //        }
    }


    public boolean isLocked() {
        try {
            if (countDownLatch != null && countDownLatch.getCount() != 0) return true;
            //如果当前服务锁存在则返回
            if (CURRENT_LOCK != null && zk.exists(CURRENT_LOCK, false) != null) {
                return true;
            }
            if (!StringUtils.isBlank(CURRENT_LOCK)) return true;
        } catch (KeeperException | InterruptedException e) {
            logger.error("Get distributed lock error", e);
        }
        return false;
    }


    public class LockException extends RuntimeException {
        private static final long serialVersionUID = 1L;

        public LockException(String e) {
            super(e);
        }

        public LockException(Exception e) {
            super(e);
        }
    }
}
