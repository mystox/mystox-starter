package demo.rabbitmq;

import java.lang.reflect.Proxy;
import java.util.concurrent.TimeUnit;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import tech.mystox.framework.stereotype.Opera;
import tech.mystox.framework.stereotype.OperaTimeout;

@Component
@Profile("function-consumer")
@Order(1) // Framework initialization runs at order 0.
public class FunctionCaller implements ApplicationRunner {
    @Opera(operaTimeout = @OperaTimeout(timeout = 3, timeUnit = TimeUnit.SECONDS))
    private CalculatorApi calculator;

    @Value("${demo.left:12}") private int left;
    @Value("${demo.right:30}") private int right;

    @Override public void run(ApplicationArguments args) {
        if (!Proxy.isProxyClass(calculator.getClass())) throw new IllegalStateException("Expected an @Opera interface proxy");
        int result = calculator.add(left, right);
        if ((long) result != (long) left + right) throw new IllegalStateException("Unexpected RPC return value: " + result);
        System.out.printf("FUNCTION result: add(%d, %d) = %d%n", left, right, result);
    }
}
