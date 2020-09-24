package tech.mystox.framework.foo.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import tech.mystox.framework.api.test.PerformanceService;

import java.util.HashMap;
import java.util.Map;
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
    private LongAdder longAdder2 = new LongAdder();

    @Override
    public void countStatistic(String msg) {
        longAdder.add(1);
        int i = longAdder.intValue();
        if (i%1000 == 0)
        logger.info("count >= {}", i);

    }


    public Map<String,Long> getCount() {
        long sum = longAdder.sum();
        long sum2 = longAdder2.sum();
        Map<String, Long> sumResult = new HashMap<>();
        sumResult.put("sum1", sum);
        sumResult.put("sum2", sum2);
        return sumResult;
    }

    @Override
    public long clearCount(String param) {
        long l2 = longAdder2.sumThenReset();
        System.out.println("sum2:"+l2);
        if (l2 != 0) return l2;
        long l1 = longAdder.sumThenReset();
        System.out.println("sum1:"+l1);
        return l1;
    }

    @Override
    public long countStatistics(String msg) {
        longAdder2.add(1);
        long i = longAdder2.longValue();
        if (i%1000 == 0)
            logger.info("count >= {}", i);
        return i;
    }

    @Override
    public long requestAck(long msg) {
//        longAdder2.add(1);
//        long i = longAdder2.longValue();
        if (msg%1000 == 0)
            logger.info("requestAck count >= {}", msg);
        return msg;
    }

}