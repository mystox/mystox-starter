package demo.rabbitmq;

import tech.mystox.framework.stereotype.OperaCode;
import tech.mystox.framework.stereotype.Register;

/** Shared method contract. The consumer uses this interface without a local implementation. */
@Register
public interface CalculatorApi {
    @OperaCode(code = "demo.add")
    int add(int left, int right);
}
