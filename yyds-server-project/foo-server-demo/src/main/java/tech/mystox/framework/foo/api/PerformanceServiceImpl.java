package tech.mystox.framework.foo.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import tech.mystox.framework.api.test.PerformanceService;

import java.util.concurrent.atomic.LongAdder;

/**
 * \* @Author: mystox
 * \* Date: 2019/11/22 10:28
 * \* Description:
 * \
 */
@Component
public class PerformanceServiceImpl implements PerformanceService {

    Logger logger = LoggerFactory.getLogger(PerformanceServiceImpl.class);
    private LongAdder longAdder = new LongAdder();

    @Override
    public void countStatistic(String msg) {
        longAdder.add(1);
        int i = longAdder.intValue();
        if (i%1000 == 0)
        logger.info("count >= {}", i);

    }


    public long getCount() {
        return longAdder.sum();
    }

    @Override
    public long clearCount(String param) {
        return longAdder.sumThenReset();
    }

    @Override
    public long countStatistics(String msg) {
        longAdder.add(1);
        long i = longAdder.longValue();
        if (i%1000 == 0)
            logger.info("count >= {}", i);
        return i;
    }

}