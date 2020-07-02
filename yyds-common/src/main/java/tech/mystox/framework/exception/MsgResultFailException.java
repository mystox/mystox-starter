package tech.mystox.framework.exception;

/**
 * Created by mystoxlol on 2020/6/29, 20:40.
 * company: kongtrolink
 * description:
 * update record:
 */
public class MsgResultFailException extends RuntimeException {
    public MsgResultFailException() {
    }

    public MsgResultFailException(String message) {
        super(message);
    }
}
