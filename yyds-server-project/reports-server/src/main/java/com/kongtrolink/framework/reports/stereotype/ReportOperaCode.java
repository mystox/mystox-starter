package com.kongtrolink.framework.reports.stereotype;

import java.lang.annotation.*;

/**
 * Created by mystoxlol on 2019/8/27, 14:57.
 * company: kongtrolink
 * description:
 * update record:
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ReportOperaCode {

    String value() default "";

    String code() default "";

    ReportExtend[] extend() default {};

    int rhythm() default 1;

    String[] dataType() default {};


}
