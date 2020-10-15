package tech.mystox.framework.context;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.beans.factory.config.*;
import org.springframework.beans.factory.support.*;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.context.annotation.AnnotationBeanNameGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.ImportResource;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.*;

import static org.springframework.beans.factory.support.BeanDefinitionBuilder.rootBeanDefinition;
import static org.springframework.context.annotation.AnnotationConfigUtils.CONFIGURATION_BEAN_NAME_GENERATOR;
import static org.springframework.core.annotation.AnnotationUtils.findAnnotation;
import static org.springframework.util.ClassUtils.resolveClassName;

public class OperaResourceBeanPostProcessor implements BeanDefinitionRegistryPostProcessor, EnvironmentAware,
        ResourceLoaderAware, BeanClassLoaderAware {

    Logger logger = LoggerFactory.getLogger(OperaResourceBeanPostProcessor.class);
    private final Set<String> packagesToScan;
    private Environment environment;
    ResourceLoader resourceLoader;
    ClassLoader classLoader;
    public OperaResourceBeanPostProcessor(String... packagesToScan) {
        this(Arrays.asList(packagesToScan));
    }

    public OperaResourceBeanPostProcessor(Collection<String> packagesToScan) {
        this(new LinkedHashSet<String>(packagesToScan));
    }

    public OperaResourceBeanPostProcessor(Set<String> packagesToScan) {
        this.packagesToScan = packagesToScan;
    }


    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {
        Set<String> resolvedPackagesToScan = resolvePackagesToScan(packagesToScan);

        if (!CollectionUtils.isEmpty(resolvedPackagesToScan)) {
            registerServiceBeans(resolvedPackagesToScan, registry);
        } else {
//            if (logger.isWarnEnabled()) {
//                logger.warn("packagesToScan is empty , ServiceBean registry will be ignored!");
//            }
        }
    }

   /* protected Set<BeanDefinitionHolder> doScan(String... basePackages) {
        Assert.notEmpty(basePackages, "At least one base package must be specified");
        Set<BeanDefinitionHolder> beanDefinitions = new LinkedHashSet<>();
        for (String basePackage : basePackages) {
            Set<BeanDefinition> candidates = findCandidateComponents(basePackage);
            for (BeanDefinition candidate : candidates) {
                ScopeMetadata scopeMetadata = this.scopeMetadataResolver.resolveScopeMetadata(candidate);
                candidate.setScope(scopeMetadata.getScopeName());
                String beanName = this.beanNameGenerator.generateBeanName(candidate, this.registry);
                if (candidate instanceof AbstractBeanDefinition) {
                    postProcessBeanDefinition((AbstractBeanDefinition) candidate, beanName);
                }
                if (candidate instanceof AnnotatedBeanDefinition) {
                    AnnotationConfigUtils.processCommonDefinitionAnnotations((AnnotatedBeanDefinition) candidate);
                }
                if (checkCandidate(beanName, candidate)) {
                    BeanDefinitionHolder definitionHolder = new BeanDefinitionHolder(candidate, beanName);
                    definitionHolder =
                            AnnotationConfigUtils.applyScopedProxyMode(scopeMetadata, definitionHolder, this.registry);
                    beanDefinitions.add(definitionHolder);
                    registerBeanDefinition(definitionHolder, this.registry);
                }
            }
        }
        return beanDefinitions;
    }*/
    private void registerServiceBeans(Set<String> packagesToScan, BeanDefinitionRegistry registry) {
        OperaClassPathBeanDefinitionScanner scanner = new OperaClassPathBeanDefinitionScanner(registry,
                /*componentScan.getBoolean("useDefaultFilters")*/true, this.environment, this.resourceLoader);

        BeanNameGenerator beanNameGenerator = resolveBeanNameGenerator(registry);

        scanner.setBeanNameGenerator(beanNameGenerator);

        scanner.addIncludeFilter(new AnnotationTypeFilter(Component.class));
        scanner.addIncludeFilter(new AnnotationTypeFilter(Bean.class));
        scanner.addIncludeFilter(new AnnotationTypeFilter(Import.class));
        scanner.addIncludeFilter(new AnnotationTypeFilter(ImportResource.class));
        for (String packageToScan : packagesToScan) {

            // Registers @Service Bean first
            scanner.scan(packageToScan);

            // Finds all BeanDefinitionHolders of @Service whether @ComponentScan scans or not.
             Set<BeanDefinition> beanDefinitions = scanner.findCandidateComponents(packageToScan);
            Set<BeanDefinitionHolder> beanDefinitionHolders = new LinkedHashSet<BeanDefinitionHolder>(beanDefinitions.size());
            for (BeanDefinition beanDefinition : beanDefinitions) {

                String beanName = beanNameGenerator.generateBeanName(beanDefinition, registry);
                BeanDefinitionHolder beanDefinitionHolder = new BeanDefinitionHolder(beanDefinition, beanName);
                beanDefinitionHolders.add(beanDefinitionHolder);

            }
//            Set<BeanDefinitionHolder> beanDefinitionHolders = (Set<BeanDefinitionHolder>) candidateComponents;
//            Set<BeanDefinitionHolder> beanDefinitionHolders =
//                    findServiceBeanDefinitionHolders(scanner, packageToScan, registry, null);

            if (!CollectionUtils.isEmpty(beanDefinitionHolders)) {

                for (BeanDefinitionHolder beanDefinitionHolder : beanDefinitionHolders) {
                    registerServiceBean(beanDefinitionHolder, registry, scanner);
                }

                if (logger.isInfoEnabled()) {
                    logger.info(beanDefinitionHolders.size() + " annotated Dubbo's @Service Components { " +
                            beanDefinitionHolders +
                            " } were scanned under package[" + packageToScan + "]");
                }

            } else {

                if (logger.isWarnEnabled()) {
                    logger.warn("No Spring Bean annotating Dubbo's @Service was found under package["
                            + packageToScan + "]");
                }

            }

        }

    }
    /**
     *
     * @param beanDefinitionHolder
     * @param registry
     * @param scanner
     * @see BeanDefinition
     */
    private void registerServiceBean(BeanDefinitionHolder beanDefinitionHolder, BeanDefinitionRegistry registry,
                                     OperaClassPathBeanDefinitionScanner scanner) {

        Class<?> beanClass = resolveClass(beanDefinitionHolder);

        Component service = findAnnotation(beanClass, Component.class);

        Class<?> interfaceClass = resolveServiceInterfaceClass(beanClass, service);

        String annotatedServiceBeanName = beanDefinitionHolder.getBeanName();


//        AbstractBeanDefinition serviceBeanDefinition =
//                buildServiceBeanDefinition(service, interfaceClass, annotatedServiceBeanName);
        BeanDefinitionBuilder builder = rootBeanDefinition(interfaceClass);

        AbstractBeanDefinition beanDefinition = builder.getBeanDefinition();
        // ServiceBean Bean name
        String beanName = beanClass.getSimpleName();
        String replaceName = tech.mystox.framework.common.util.StringUtils.toLowerCaseFirstOne(beanName);


        if (scanner.checkCandidate(replaceName, beanDefinition)) { // check duplicated candidate bean
            registry.registerBeanDefinition(beanName, beanDefinition);

            if (logger.isInfoEnabled()) {
                logger.warn("The BeanDefinition[" + annotatedServiceBeanName +
                        "] of ServiceBean has been registered with name : " + beanName);
            }

        } else {

            if (logger.isWarnEnabled()) {
                logger.warn("The Duplicated BeanDefinition[" + annotatedServiceBeanName +
                        "] of ServiceBean[ bean name : " + beanName +
                        "] was be found , Did  scan to same package in many times?");
            }

        }

    }
    /*private AbstractBeanDefinition buildServiceBeanDefinition(Component service, Class<?> interfaceClass,
                                                              String annotatedServiceBeanName) {

        BeanDefinitionBuilder builder = rootBeanDefinition(ServiceBean.class);

        AbstractBeanDefinition beanDefinition = builder.getBeanDefinition();

        MutablePropertyValues propertyValues = beanDefinition.getPropertyValues();

        String[] ignoreAttributeNames = of("provider", "monitor", "application", "module", "registry", "protocol", "interface");

        propertyValues.addPropertyValues(new AnnotationPropertyValuesAdapter(service, environment, ignoreAttributeNames));

        // References "ref" property to annotated-@Service Bean
        addPropertyReference(builder, "ref", annotatedServiceBeanName);
        // Set interface
        builder.addPropertyValue("interface", interfaceClass.getName());

        *//**
         * Add {@link com.alibaba.dubbo.config.ProviderConfig} Bean reference
         *//*
        String providerConfigBeanName = service.provider();
        if (StringUtils.hasText(providerConfigBeanName)) {
            addPropertyReference(builder, "provider", providerConfigBeanName);
        }

        *//**
         * Add {@link com.alibaba.dubbo.config.MonitorConfig} Bean reference
         *//*
        String monitorConfigBeanName = service.monitor();
        if (StringUtils.hasText(monitorConfigBeanName)) {
            addPropertyReference(builder, "monitor", monitorConfigBeanName);
        }

        *//**
         * Add {@link com.alibaba.dubbo.config.ApplicationConfig} Bean reference
         *//*
        String applicationConfigBeanName = service.application();
        if (StringUtils.hasText(applicationConfigBeanName)) {
            addPropertyReference(builder, "application", applicationConfigBeanName);
        }

        *//**
         * Add {@link com.alibaba.dubbo.config.ModuleConfig} Bean reference
         *//*
        String moduleConfigBeanName = service.module();
        if (StringUtils.hasText(moduleConfigBeanName)) {
            addPropertyReference(builder, "module", moduleConfigBeanName);
        }


        *//**
         * Add {@link com.alibaba.dubbo.config.RegistryConfig} Bean reference
         *//*
        String[] registryConfigBeanNames = service.registry();

        List<RuntimeBeanReference> registryRuntimeBeanReferences = toRuntimeBeanReferences(registryConfigBeanNames);

        if (!registryRuntimeBeanReferences.isEmpty()) {
            builder.addPropertyValue("registries", registryRuntimeBeanReferences);
        }

        *//**
         * Add {@link com.alibaba.dubbo.config.ProtocolConfig} Bean reference
         *//*
        String[] protocolConfigBeanNames = service.protocol();

        List<RuntimeBeanReference> protocolRuntimeBeanReferences = toRuntimeBeanReferences(protocolConfigBeanNames);

        if (!protocolRuntimeBeanReferences.isEmpty()) {
            builder.addPropertyValue("protocols", protocolRuntimeBeanReferences);
        }

        return builder.getBeanDefinition();

    }*/
    private Class<?> resolveClass(BeanDefinitionHolder beanDefinitionHolder) {

        BeanDefinition beanDefinition = beanDefinitionHolder.getBeanDefinition();

        return resolveClass(beanDefinition);

    }
    private Class<?> resolveClass(BeanDefinition beanDefinition) {

        String beanClassName = beanDefinition.getBeanClassName();

        return resolveClassName(beanClassName, classLoader);

    }
    private Class<?> resolveServiceInterfaceClass(Class<?> annotatedServiceBeanClass, Component service) {

//        Class<?> interfaceClass = service.interfaceClass();
        Class<?> superclass = annotatedServiceBeanClass.getSuperclass();
        /*if (superclass != null) {
            return superclass;
        }*/

       /* if (void.class.equals(interfaceClass)) {

            interfaceClass = null;

            String interfaceClassName = service.interfaceName();

            if (StringUtils.hasText(interfaceClassName)) {
                if (ClassUtils.isPresent(interfaceClassName, classLoader)) {
                    interfaceClass = resolveClassName(interfaceClassName, classLoader);
                }
            }

        }

        if (interfaceClass == null) {

            Class<?>[] allInterfaces = annotatedServiceBeanClass.getInterfaces();

            if (allInterfaces.length > 0) {
                interfaceClass = allInterfaces[0];
            }

        }*/

        Assert.notNull(superclass,
                "@Service interfaceClass() or interfaceName() or interface class must be present!");

//        Assert.isTrue(superclass.isInterface(),
//                "The type that was annotated @Service is not an interface!");

        return superclass;
    }

    private BeanNameGenerator resolveBeanNameGenerator(BeanDefinitionRegistry registry) {

        BeanNameGenerator beanNameGenerator = null;

        if (registry instanceof SingletonBeanRegistry) {
            SingletonBeanRegistry singletonBeanRegistry = SingletonBeanRegistry.class.cast(registry);
            beanNameGenerator = (BeanNameGenerator) singletonBeanRegistry.getSingleton(CONFIGURATION_BEAN_NAME_GENERATOR);
        }

        if (beanNameGenerator == null) {

            if (logger.isInfoEnabled()) {

                logger.info("BeanNameGenerator bean can't be found in BeanFactory with name ["
                        + CONFIGURATION_BEAN_NAME_GENERATOR + "]");
                logger.info("BeanNameGenerator will be a instance of " +
                        AnnotationBeanNameGenerator.class.getName() +
                        " , it maybe a potential problem on bean name generation.");
            }

            beanNameGenerator = new AnnotationBeanNameGenerator();

        }

        return beanNameGenerator;

    }
    private Set<String> resolvePackagesToScan(Set<String> packagesToScan) {
        Set<String> resolvedPackagesToScan = new LinkedHashSet<String>(packagesToScan.size());
        for (String packageToScan : packagesToScan) {
            if (StringUtils.hasText(packageToScan)) {
                String resolvedPackageToScan = environment.resolvePlaceholders(packageToScan.trim());
                resolvedPackagesToScan.add(resolvedPackageToScan);
            }
        }
        return resolvedPackagesToScan;
    }
    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {

    }

    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

    @Override
    public void setResourceLoader(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }
    @Override
    public void setBeanClassLoader(ClassLoader classLoader) {

        this.classLoader = classLoader;

    }
}
