package demo.rabbitmq;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import tech.mystox.framework.stereotype.OperaCode;
import tech.mystox.framework.stereotype.Register;

@Service
@Register
@Profile("provider")
public class HelloService {
    private final String instance;
    public HelloService(@Value("${demo.instance:provider-A}") String instance) { this.instance = instance; }
    @OperaCode(code = "demo.hello")
    public String hello(String name) { return "[" + instance + "] 你好，" + name; }
}
