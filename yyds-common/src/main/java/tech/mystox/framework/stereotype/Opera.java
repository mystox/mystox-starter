package tech.mystox.framework.stereotype;

import tech.mystox.framework.entity.OperaType;

import java.lang.annotation.*;
import java.util.concurrent.TimeUnit;

/**
 * Created by mystoxlol on 2020/6/18, 9:45.
 * company: kongtrolink
 * description:
 * update record:
 */
@Target({ElementType.CONSTRUCTOR, ElementType.METHOD, ElementType.PARAMETER, ElementType.FIELD, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Opera {
    OperaType operaType() default OperaType.Sync;

    OperaTimeout operaTimeout() default @OperaTimeout(timeout = 30, timeUnit = TimeUnit.SECONDS);
}
