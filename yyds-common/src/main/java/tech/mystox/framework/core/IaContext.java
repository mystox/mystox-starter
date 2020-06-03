package tech.mystox.framework.core;

import tech.mystox.framework.config.IaConf;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
@Component
public class IaContext implements ApplicationRunner {
    @Autowired
    private IaConf conf;
    @Autowired
    private IaENV iaEnv;



    private IaRegister iaRegister;
    private Logger logger = LoggerFactory.getLogger(IaContext.class);
    public IaENV getIaENV() {
        return iaEnv;
    }
    public IaRegister getIaRegister() { return iaRegister; }
    @Override
    public void run(ApplicationArguments args) {
     iaEnv.build(conf);
    iaRegister =new IaRegister(iaEnv);
    iaRegister.subTopic();
    iaRegister.connect();
    iaRegister.register();

    }
}
