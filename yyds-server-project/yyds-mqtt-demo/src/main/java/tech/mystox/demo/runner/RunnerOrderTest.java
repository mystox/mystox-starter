package tech.mystox.demo.runner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;

/**
 * Created by mystox on 2022/7/17, 14:00.
 * company:
 * description:
 * update record:
 */
@Component
public class RunnerOrderTest implements ApplicationRunner, Ordered {

    Logger logger = LoggerFactory.getLogger(RunnerOrderTest.class);
    @Override
    public void run(ApplicationArguments args) throws Exception {
        logger.info("执行器开始执行1");
    }

    @Override
    public int getOrder() {
        return 1;
    }
}
