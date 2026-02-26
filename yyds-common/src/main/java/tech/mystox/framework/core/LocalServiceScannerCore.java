package tech.mystox.framework.core;

import com.alibaba.fastjson2.JSON;
import io.github.classgraph.ClassGraph;
import io.github.classgraph.ClassInfo;
import io.github.classgraph.ClassInfoList;
import io.github.classgraph.ScanResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tech.mystox.framework.entity.AckEnum;
import tech.mystox.framework.entity.RegisterSub;
import tech.mystox.framework.entity.UnitHead;
import tech.mystox.framework.exception.RegisterAnalyseException;
import tech.mystox.framework.stereotype.OperaCode;
import tech.mystox.framework.stereotype.Register;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by mystoxlol on 2019/8/28, 15:40.
 * company: mystox
 * description:
 * update record:
 */
//@Service
public class LocalServiceScannerCore implements ServiceScanner {

    private final Logger logger = LoggerFactory.getLogger(LocalServiceScannerCore.class);

    static final String DEFAULT_RESOURCE_PATTERN = "**/*.class";
    String resourcePattern = DEFAULT_RESOURCE_PATTERN;

    //@Value("${register.scanBasePackage:tech.mystox.framework}")
    private final List<String> basePackages;
    private final BeanProvider beanProvider;

    public LocalServiceScannerCore(List<String> basePackages,
                                   BeanProvider beanProvider) {
        this.basePackages = basePackages;
        this.beanProvider = beanProvider;
    }

    /**
     * 扫描“basePackagePath”包下的资源，将对应注解资源封装。
     *
     * @return
     */
    @Override
    public List<RegisterSub> getSubList() {

        Set<String> operaSet = new HashSet<>();
        List<RegisterSub> subList = new ArrayList<>();

        try (ScanResult scanResult = new ClassGraph()
                .enableClassInfo()
                .enableAnnotationInfo()
                .acceptPackages(basePackages.toArray(new String[0]))
                .scan()) {

            ClassInfoList classInfos =
                    scanResult.getClassesWithAnnotation(Register.class.getName());

            for (ClassInfo classInfo : classInfos) {
                Class<?> clazz = classInfo.loadClass();
                // 如果需要限制必须是 Spring Bean
                if (beanProvider != null) {
                    try {
                        Object bean = beanProvider.getBean(clazz);
                        if (bean == null) continue;
                    } catch (Exception e) {
                        continue;
                    }
                }
                processClass(clazz, operaSet, subList);
            }
        }
        logger.info("local scanner result: {}", subList);
        return subList;
    }

    private void processClass(Class<?> clazz,
                              Set<String> operaSet,
                              List<RegisterSub> subList) {

        for (Method method : clazz.getMethods()) {
            OperaCode annotation = method.getAnnotation(OperaCode.class);
            if (annotation == null) continue;

            RegisterSub sub = buildRegisterSub(clazz, method, annotation);

            if (!operaSet.add(sub.getOperaCode())) {
                throw new RegisterAnalyseException(
                        "opera duplicate: " + sub.getOperaCode());
            }

            subList.add(sub);
        }
    }

    private RegisterSub buildRegisterSub(Class<?> clazz,
                                         Method method,
                                         OperaCode annotation) {

        RegisterSub sub = new RegisterSub();

        String code = annotation.code();
        if (code == null || code.isBlank()) {
            code = annotation.withClass()
                    ? clazz.getName() + "." + method.getName()
                    : method.getName();
        }

        sub.setOperaCode(code);

        sub.setAck(method.getReturnType() == Void.TYPE
                ? AckEnum.NA
                : AckEnum.ACK);
        Class<?>[] parameterTypes = method.getParameterTypes();
        sub.setExecuteUnit(
                UnitHead.LOCAL
                        + clazz.getName()
                        + "/"
                        + method.getName()
                        + "/" + JSON.toJSON(parameterTypes)
        );

        return sub;
    }

    @Override
    public boolean addSub(RegisterSub registerSub) {
        return false;
    }

    @Override
    public boolean deleteSub(String operaCode) {
        return false;
    }

}
