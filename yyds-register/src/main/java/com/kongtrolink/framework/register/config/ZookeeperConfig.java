package com.kongtrolink.framework.register.config;

import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
    private CountDownLatch latch = new CountDownLatch(1);

    private String zookeeperUrl;

    private ZooKeeper zooKeeper;

//    @Bean
    public ZooKeeper setZooKeeper(ZooKeeper zooKeeper) throws IOException
    {
//        ZkClient zkClient = new ZkClient();
        return zooKeeper;
    }


   ZooKeeper builder(String zookeeperUrl) throws IOException
   {
        ZooKeeper zooKeeper = new ZooKeeper(zookeeperUrl, 4000, this);
        return zooKeeper;

    }

    public String getZookeeperUrl() {
        return zookeeperUrl;
    }

    public void setZookeeperUrl(String zookeeperUrl) {
        this.zookeeperUrl = zookeeperUrl;
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
           /* try
            {
                synchronized (ZookeeperConfig.class)
                {
                    latch = new CountDownLatch(1);
                    zooKeeper = zooKeeper();
                    zooKeeper.close();
                    zooKeeper = builder("");

                }
            } catch (IOException e)
            {
                e.printStackTrace();
            } catch (InterruptedException e)
            {
                e.printStackTrace();
            }*/
            latch = new CountDownLatch(1);
        }
        LOG.info("已经触发了[{}]事件！内容: {} ", watchedEvent.getType(), watchedEvent.toString());
    }
}
