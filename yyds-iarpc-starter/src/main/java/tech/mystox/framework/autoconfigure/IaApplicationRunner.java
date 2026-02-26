package tech.mystox.framework.autoconfigure;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.Ordered;
import tech.mystox.framework.core.IaContext;

public class IaApplicationRunner implements ApplicationRunner, Ordered {

    private final IaContext iaContext;

    public IaApplicationRunner(IaContext iaContext) {
        this.iaContext = iaContext;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        iaContext.start();
    }

    @Override
    public int getOrder() {
        return 0;
    }
}
