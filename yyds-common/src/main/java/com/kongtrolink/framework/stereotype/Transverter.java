package com.kongtrolink.framework.stereotype;

import java.lang.annotation.*;

/**
 * Created by mystoxlol on 2019/10/16, 10:33.
 * company: kongtrolink
 * description:
 * update record:
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Transverter {
    String name() default "";
}
