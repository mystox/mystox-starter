package tech.mystox.framework.exception;

/**
 * Created by mystoxlol on 2019/9/9, 18:05.
 * company: mystox
 * description: 注册发生的异常
 * update record:
 */
public class RegisterException extends Exception {

    public RegisterException(String message, Exception e) {
        super(message, e);
    }

    public RegisterException(String message) {
        super(message);
    }
}
