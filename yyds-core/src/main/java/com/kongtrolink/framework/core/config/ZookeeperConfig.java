package com.kongtrolink.framework.core.config;

import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

/**
 * Created by mystoxlol on 2019/2/18, 10:52.
 * company: kongtrolink
 * description:
 * update record:
 */
//@Configuration
public class ZookeeperConfig implements Watcher
{
    Logger LOG = LoggerFactory.getLogger(this.getClass());
    @Value("${zookeeper.url}")
    private String zookeeperUrl;
    @Value("${zookeeper.sessionTimeout}")
    private int sessionTimeout;
    private CountDownLatch latch = new CountDownLatch(1);

    @Bean
    public ZooKeeper zooKeeper() throws IOException
    {
//        ZkClient zkClient = new ZkClient();
        return builder();
    }

   ZooKeeper builder() throws IOException
   {

        ZooKeeper zooKeeper = new ZooKeeper(zookeeperUrl, 4000, this);
        System.out.println(zooKeeper.hashCode());
//        ZkClient zkClient = new ZkClient();
        return zooKeeper;

    }

    //
//@Autowired
//ZooKeeper zooKeeper;
    @Override
    public void process(WatchedEvent watchedEvent)
    {
        if (watchedEvent.getState() == Watcher.Event.KeeperState.SyncConnected && latch.getCount() != 0)
        {
            latch.countDown();
        } else if (watchedEvent.getState() == Event.KeeperState.Expired)
        {
            ZooKeeper zooKeeper = null;
            try
            {
                synchronized (ZookeeperConfig.class)
                {
                    latch = new CountDownLatch(1);
                    zooKeeper = zooKeeper();
                    zooKeeper.close();
                    zooKeeper = builder();
                    System.out.println(zooKeeper);
                    System.out.println("endendendendendendendendedendendendend");

                }
            } catch (IOException e)
            {
                e.printStackTrace();
            } catch (InterruptedException e)
            {
                e.printStackTrace();
            }
            latch = new CountDownLatch(1);
        }
        LOG.info("已经触发了[{}]事件！内容: {} ", watchedEvent.getType(), watchedEvent.toString());
    }
}
