package com.kongtrolink.framework.reports.stereotype;

import com.kongtrolink.framework.reports.entity.ExecutorType;

/**
 * Created by mystoxlol on 2019/10/31, 12:53.
 * company: kongtrolink
 * description:
 * update record:
 */
public @interface ReportExtend {

    enum FieldType {DATE, STRING, NUMBER,DISTRICT,URI,MQTT,DATE_PERIOD,JSON}

    enum DateType {HOUR, DAY, WEEK, MONTH, YEAR}

    String field() default "";

    String name() default "";

    FieldType type() default FieldType.STRING;

    ExecutorType belong() default ExecutorType.execute;

    String[] select() default {};

    String value() default "";

    boolean hide() default false;

    String uri() default "";

    String description() default "";

}
