package tech.mystox.framework.api.test;

import tech.mystox.framework.stereotype.OperaCode;
import tech.mystox.framework.stereotype.Register;

import java.util.Map;

/**
 * \* @Author: mystox
 * \* Date: 2019/11/22 10:27
 * \* Description:
 * \
 */
@Register
public interface PerformanceService {

    @OperaCode
    public void countStatistic(String msg);
    @OperaCode
    public Map<String,Long> getCount();
    @OperaCode
    public long clearCount(String param);

    @OperaCode
    public long countStatistics(String msg);

    @OperaCode
    public long requestAck(long msg);

}