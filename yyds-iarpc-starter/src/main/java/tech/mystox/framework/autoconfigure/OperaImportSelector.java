package tech.mystox.framework.autoconfigure;

import org.springframework.context.annotation.ImportSelector;
import org.springframework.core.type.AnnotationMetadata;

public class OperaImportSelector implements ImportSelector {
    @Override
    public String[] selectImports(AnnotationMetadata importingClassMetadata) {
        return new String[] {
            "tech.mystox.framework.autoconfigure.OperaCoreConfiguration"
        };
    }
}
