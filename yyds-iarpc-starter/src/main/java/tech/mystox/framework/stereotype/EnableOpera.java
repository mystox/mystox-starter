package tech.mystox.framework.stereotype;

import org.springframework.context.annotation.Import;
import tech.mystox.framework.autoconfigure.OperaImportSelector;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(OperaImportSelector.class)
public @interface EnableOpera {
}
