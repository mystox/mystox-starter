package tech.mystox.framework.stereotype;

import org.springframework.context.annotation.Import;
import tech.mystox.framework.context.OperaAnnotationBeanPostProcessor;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(OperaAnnotationBeanPostProcessor.class)
public @interface EnableOpera {
}
