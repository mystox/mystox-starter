package demo.rabbitmq;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import tech.mystox.framework.stereotype.EnableOpera;

@SpringBootApplication
@EnableOpera
public class RabbitRpcDemoApplication {
    public static void main(String[] args) {
        ConfigurableApplicationContext context;
        try {
            context = SpringApplication.run(RabbitRpcDemoApplication.class, args);
        } catch (RuntimeException failure) {
            // Spring has logged the startup/call error; return a failing CLI exit status.
            System.exit(1);
            return;
        }
        if (context.getEnvironment().matchesProfiles("consumer", "function-consumer")) {
            System.exit(SpringApplication.exit(context));
        }
    }
}
