package tech.mystox.framework.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import tech.mystox.framework.config.IaConf;

@Component
public class IaContext implements ApplicationRunner {
    private Logger logger = LoggerFactory.getLogger(IaContext.class);

    private final IaConf conf;
    private final IaENV iaEnv;
    private IaRegister iaRegister;

    public IaContext(IaConf conf, IaENV iaEnv) {
        this.conf = conf;
        this.iaEnv = iaEnv;
    }

    public IaENV getIaENV() {
        return iaEnv;
    }

    public IaRegister getIaRegister() {
        return iaRegister;
    }

    @Override
    public void run(ApplicationArguments args) {
        iaEnv.build(conf);
        iaRegister = new IaRegister(iaEnv);
        iaRegister.connect();
        // iaRegister.subTopic();
        iaRegister.register();

    }
}
