package demo.rabbitmq;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Service
@Profile("provider")
public class CalculatorService implements CalculatorApi {
    private static final Logger log = LoggerFactory.getLogger(CalculatorService.class);
    private final String instance;
    public CalculatorService(@Value("${demo.instance:provider-A}") String instance) { this.instance = instance; }
    @Override public int add(int left, int right) {
        log.info("FUNCTION executed remotely: instance={}, add({}, {})", instance, left, right);
        return Math.addExact(left, right);
    }
}
