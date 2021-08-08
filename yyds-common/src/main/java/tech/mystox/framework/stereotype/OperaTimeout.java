package tech.mystox.framework.stereotype;

import java.util.concurrent.TimeUnit;

/**
 * Created by mystox on 2021/8/4, 15:06.
 * company:
 * description:
 * update record:
 */
public @interface OperaTimeout {


    long timeout() default 30L;

    TimeUnit timeUnit() default TimeUnit.SECONDS;
}
