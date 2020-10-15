package tech.mystox.framework.context;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.ClassPathBeanDefinitionScanner;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ResourceLoader;

import java.rmi.registry.Registry;
import java.util.Set;

public class OperaClassPathBeanDefinitionScanner extends ClassPathBeanDefinitionScanner {
    private final BeanDefinitionRegistry registry;
    /*public OperaClassPathBeanDefinitionScanner(BeanDefinitionRegistry registry) {
        super(registry);
        this.registry = registry;
    }

    public OperaClassPathBeanDefinitionScanner(BeanDefinitionRegistry registry, boolean useDefaultFilters) {
        super(registry, useDefaultFilters);
        this.registry = registry;
    }

    public OperaClassPathBeanDefinitionScanner(BeanDefinitionRegistry registry, boolean useDefaultFilters, Environment environment) {
        super(registry, useDefaultFilters, environment);
        this.registry = registry;
    }*/

    public OperaClassPathBeanDefinitionScanner(BeanDefinitionRegistry registry, boolean useDefaultFilters, Environment environment, ResourceLoader resourceLoader) {
        super(registry, useDefaultFilters, environment, resourceLoader);
        this.registry = registry;
    }

    @Override
    public Set<BeanDefinitionHolder> doScan(String... basePackages) {
        return super.doScan(basePackages);
    }

    @Override
    public boolean checkCandidate(String beanName, BeanDefinition beanDefinition) throws IllegalStateException {
        if (!this.registry.containsBeanDefinition(beanName)) {
            return true;
        }
        /*BeanDefinition existingDef = this.registry.getBeanDefinition(beanName);
        BeanDefinition originatingDef = existingDef.getOriginatingBeanDefinition();
        if (originatingDef != null) {
            existingDef = originatingDef;
        }
        isCompatible(beanDefinition, existingDef);*/
        return false;
    }
}
