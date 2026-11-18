package demo.rabbitmq;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import tech.mystox.framework.entity.StateCode.StateCodeEnum;
import tech.mystox.framework.service.IaOpera;

@Component
@Profile("consumer")
@Order(1)
public class HelloCaller implements ApplicationRunner {
    private final IaOpera opera;
    private final String name;
    private final int calls;
    public HelloCaller(IaOpera opera, @Value("${demo.name:张三}") String name, @Value("${demo.calls:1}") int calls) {
        this.opera = opera; this.name = name; this.calls = calls;
    }
    @Override public void run(ApplicationArguments args) {
        if (calls < 1) throw new IllegalArgumentException("demo.calls must be positive");
        for (int i = 0; i < calls; i++) {
            var result = opera.opera("demo.hello", name);
            if (result.getStateCode() != StateCodeEnum.SUCCESS.getCode()) throw new IllegalStateException("RPC failed: " + result);
            System.out.println("RPC result: " + result.getMsg());
        }
    }
}
