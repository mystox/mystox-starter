package tech.mystox.framework.exception;

import tech.mystox.framework.entity.StateCode;

/**
 * Created by mystoxlol on 2020/6/29, 20:40.
 * company:
 * description:
 * update record:
 */
public class MsgResultFailException extends RuntimeException {
    public MsgResultFailException() {
    }

    public MsgResultFailException(String message) {
        super(message);
    }
    public MsgResultFailException(StateCode.StateCodeEnum stateCodeEnum, String message) {
        super("["+stateCodeEnum+"("+stateCodeEnum.getCode()+")]"+message);
    }
}
