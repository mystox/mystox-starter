package tech.mystox.framework.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tech.mystox.framework.config.IaConf;
import tech.mystox.framework.exception.RegisterException;

public class IaContext {
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

    public IaConf getConf() {
        return conf;
    }

    public IaRegister getIaRegister() {
        return iaRegister;
    }

    public void start() throws RegisterException {
        logger.info("Ia rpc framework run beginning...");
        iaEnv.build(this);
        iaRegister = new IaRegister(iaEnv);
        iaRegister.connect();
        // iaRegister.subTopic();
        iaRegister.register();

    }

    public void stop() {
        if (iaRegister != null) {
            iaRegister.unregister();
        }
    }
}
