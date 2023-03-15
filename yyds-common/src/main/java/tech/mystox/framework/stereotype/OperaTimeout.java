package tech.mystox.framework.stereotype;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.concurrent.TimeUnit;

/**
 * Created by mystox on 2021/8/4, 15:06.
 * company:
 * description:
 * update record:
 */
@Target(ElementType.ANNOTATION_TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface OperaTimeout {


    long timeout() default 30L;

    TimeUnit timeUnit() default TimeUnit.SECONDS;
}
