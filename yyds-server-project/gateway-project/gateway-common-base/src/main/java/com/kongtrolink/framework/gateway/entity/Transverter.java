package com.kongtrolink.framework.gateway.entity;

import org.springframework.stereotype.Component;

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
@Component
public @interface Transverter {
    String value() default "";
}
